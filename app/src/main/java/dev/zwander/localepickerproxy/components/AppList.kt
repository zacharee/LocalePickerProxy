package dev.zwander.localepickerproxy.components

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.zwander.localepickerproxy.util.getAllAppsSupportingLocales
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppList(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var apps by remember {
        mutableStateOf(listOf<ApplicationInfo>())
    }

    LaunchedEffect(key1 = null) {
        apps = withContext(Dispatchers.IO) {
            context.getAllAppsSupportingLocales()
        }
    }

    val windowInsets = WindowInsets.systemBars
        .add(WindowInsets.ime)
        .add(WindowInsets(8.dp, 8.dp, 8.dp, 8.dp))
        .asPaddingValues()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = windowInsets,
    ) {
        items(items = apps, key = { it.packageName }) {
            AppItem(
                app = it,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
