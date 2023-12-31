package dev.zwander.localepickerproxy.util

import android.annotation.SuppressLint
import android.app.LocaleConfig
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.net.Uri
import android.os.LocaleList
import android.provider.Settings
import dev.zwander.localepickerproxy.data.LabeledApplicationInfo

fun Context.getAllAppsSupportingLocales(): List<LabeledApplicationInfo> {
    return getAllAppsWithLauncherActivities().filter {
        appSupportsLocales(it) && !it.isAppSignedWithPlatformKey()
    }
}

fun Context.launchLocaleSettingsForApp(app: ApplicationInfo) {
    val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.data = Uri.parse("package:${app.packageName}")

    startActivity(intent)
}

private fun Context.getAllAppsWithLauncherActivities(): List<LabeledApplicationInfo> {
    return packageManager.queryIntentActivities(
        Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER),
        PackageManager.GET_META_DATA,
    ).map { it.activityInfo.applicationInfo }
        .distinctBy { it.packageName }
        .map { LabeledApplicationInfo(it, this) }
        .sortedBy { it.label.toString().lowercase() }
}

private fun Context.appSupportsLocales(app: ApplicationInfo): Boolean {
    getPackageLocales(app)?.let {
        return !it.isEmpty
    }

    if (isFeatureEnabled("settings_app_locale_opt_in_enabled")) {
        return false
    }

    return getAssetLocales(app).isNotEmpty()
}

private fun Context.getAssetLocales(app: ApplicationInfo): Array<String> {
    val resources = packageManager.getResourcesForApplication(app)

    return resources.assets.getNonSystemLocalesReflection()
}

private fun Context.getPackageLocales(app: ApplicationInfo): LocaleList? {
    try {
        val config = LocaleConfig(createPackageContext(app.packageName, 0))

        if (config.status == LocaleConfig.STATUS_SUCCESS) {
            return config.supportedLocales
        }
    } catch (_: PackageManager.NameNotFoundException) {}

    return null
}

private fun ApplicationInfo.isAppSignedWithPlatformKey(): Boolean {
    return ApplicationInfo::class.java
        .getMethod("isSignedWithPlatformKey")
        .invoke(this) as Boolean
}

private fun AssetManager.getNonSystemLocalesReflection(): Array<String> {
    @Suppress("UNCHECKED_CAST")
    return AssetManager::class.java
        .getMethod("getNonSystemLocales")
        .invoke(this) as Array<String>
}

@SuppressLint("PrivateApi")
private fun Context.isFeatureEnabled(feature: String): Boolean {
    return Class.forName("android.util.FeatureFlagUtils")
        .getMethod("isEnabled", Context::class.java, String::class.java)
        .invoke(null, this, feature) as Boolean
}
