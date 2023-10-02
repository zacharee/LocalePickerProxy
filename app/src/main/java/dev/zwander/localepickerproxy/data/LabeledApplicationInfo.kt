package dev.zwander.localepickerproxy.data

import android.content.Context
import android.content.pm.ApplicationInfo

class LabeledApplicationInfo(wrapped: ApplicationInfo, context: Context) : ApplicationInfo(wrapped) {
    val label: CharSequence = wrapped.loadLabel(context.packageManager)
}