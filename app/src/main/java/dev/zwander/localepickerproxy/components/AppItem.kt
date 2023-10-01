package dev.zwander.localepickerproxy.components

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.Image
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
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dev.zwander.localepickerproxy.util.launchLocaleSettingsForApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppItem(
    app: ApplicationInfo,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val appLabel = app.loadLabel(context.packageManager).toString()

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
            Image(
                painter = rememberDrawablePainter(drawable = app.loadIcon(context.packageManager)),
                contentDescription = appLabel,
                modifier = Modifier.size(48.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = appLabel,
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = app.packageName,
                )
            }
        }
    }
}
