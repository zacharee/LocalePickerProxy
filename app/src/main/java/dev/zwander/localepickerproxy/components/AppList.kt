package dev.zwander.localepickerproxy.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.zwander.localepickerproxy.data.LabeledApplicationInfo

@Composable
fun AppList(
    apps: List<LabeledApplicationInfo>,
    contentPadding: PaddingValues,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
        state = listState,
    ) {
        items(items = apps, key = { it.packageName }) {
            AppItem(
                app = it,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
