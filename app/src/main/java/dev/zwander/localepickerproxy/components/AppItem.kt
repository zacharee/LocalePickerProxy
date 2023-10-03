@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
package dev.zwander.localepickerproxy.components

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.fetch.DrawableFetcher
import coil.fetch.Fetcher
import coil.request.ImageRequest
import dev.zwander.localepickerproxy.data.LabeledApplicationInfo
import dev.zwander.localepickerproxy.util.launchLocaleSettingsForApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppItem(
    app: LabeledApplicationInfo,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Card(
        onClick = {
            context.launchLocaleSettingsForApp(app)
        },
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .fetcherFactory(Fetcher.Factory<ApplicationInfo> { data, options, _ ->
                        Fetcher {
                            val drawable = data.loadIcon(context.packageManager)

                            DrawableFetcher(
                                data = drawable,
                                options = options,
                            ).fetch()
                        }
                    })
                    .data(app)
                    .build(),
                contentDescription = app.label.toString(),
                modifier = Modifier.size(48.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = app.label.toString(),
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = app.packageName,
                )
            }
        }
    }
}
