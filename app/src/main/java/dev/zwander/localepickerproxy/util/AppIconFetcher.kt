@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
package dev.zwander.localepickerproxy.util

import android.content.Context
import android.content.pm.ApplicationInfo
import coil.ImageLoader
import coil.fetch.DrawableFetcher
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options

class AppIconFetcher(
    private val context: Context,
    private val app: ApplicationInfo,
    private val options: Options,
) : Fetcher {
    override suspend fun fetch(): FetchResult {
        val drawable = app.loadIcon(context.packageManager)

        return DrawableFetcher(
            data = drawable,
            options = options,
        ).fetch()
    }

    class Factory(
        private val context: Context,
    ) : Fetcher.Factory<ApplicationInfo> {
        override fun create(
            data: ApplicationInfo,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher {
            return AppIconFetcher(
                context = context,
                app = data,
                options = options,
            )
        }
    }
}
