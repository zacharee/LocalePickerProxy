package dev.zwander.localepickerproxy.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.zwander.localepickerproxy.data.LabeledApplicationInfo
import dev.zwander.localepickerproxy.util.AppIconFetcher
import dev.zwander.localepickerproxy.util.launchLocaleSettingsForApp
import sv.lib.squircleshape.SquircleShape

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
            Box(
                modifier = Modifier.clip(SquircleShape())
            ) {
                AsyncImage(
                    model = remember {
                        ImageRequest.Builder(context)
                            .data(app)
                            .fetcherFactory(AppIconFetcher.Factory(context))
                            .memoryCacheKey(app.packageName)
                            .build()
                    },
                    contentDescription = app.label.toString(),
                    modifier = Modifier.size(48.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = app.label.toString(),
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
