package dev.zwander.localepickerproxy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import dev.zwander.localepickerproxy.components.AppList
import dev.zwander.localepickerproxy.components.SearchBar
import dev.zwander.localepickerproxy.data.LabeledApplicationInfo
import dev.zwander.localepickerproxy.ui.theme.LocalePickerProxyTheme
import dev.zwander.localepickerproxy.util.getAllAppsSupportingLocales
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.lsposed.hiddenapibypass.HiddenApiBypass

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HiddenApiBypass.addHiddenApiExemptions("")

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            var apps by remember {
                mutableStateOf(listOf<LabeledApplicationInfo>())
            }

            var searchTerm by remember {
                mutableStateOf("")
            }

            var isRefreshing by remember {
                mutableStateOf(true)
            }

            var searchBarHeight by remember {
                mutableIntStateOf(0)
            }

            val mutualNonTopWindowInsets = if (WindowInsets.isImeVisible) {
                WindowInsets.ime.add(
                    WindowInsets.systemBars.only(
                        WindowInsetsSides.End + WindowInsetsSides.Start,
                    )
                )
            } else {
                WindowInsets.systemBars.only(
                    WindowInsetsSides.Bottom + WindowInsetsSides.End + WindowInsetsSides.Start,
                )
            }

            val contentPadding = mutualNonTopWindowInsets
                .add(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                .add(WindowInsets(8.dp, 8.dp, 8.dp, 16.dp))
                .add(WindowInsets(bottom = with(LocalDensity.current) { searchBarHeight.toDp() }))
                .asPaddingValues()

            val pullRefreshState = rememberPullRefreshState(
                refreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                }
            )

            val listState = rememberLazyListState()

            val filteredApps by remember {
                derivedStateOf {
                    apps.filter {
                        it.label.contains(searchTerm, true) ||
                                it.packageName.contains(searchTerm, true)
                    }
                }
            }

            LaunchedEffect(key1 = isRefreshing) {
                if (isRefreshing) {
                    apps = withContext(Dispatchers.IO) {
                        context.getAllAppsSupportingLocales()
                    }
                    isRefreshing = false
                }
            }

            LocalePickerProxyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Crossfade(
                        targetState = isRefreshing,
                        label = "MainFade",
                        modifier = Modifier.fillMaxSize(),
                    ) { refreshing ->
                        if (refreshing) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                AppList(
                                    apps = filteredApps,
                                    contentPadding = contentPadding,
                                    listState = listState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .pullRefresh(state = pullRefreshState),
                                )

                                PullRefreshIndicator(
                                    refreshing = false,
                                    state = pullRefreshState,
                                    modifier = Modifier.align(Alignment.TopCenter),
                                )

                                SearchBar(
                                    text = searchTerm,
                                    onTextChange = { text -> searchTerm = text },
                                    onScrollToTop = {
                                        scope.launch {
                                            if (filteredApps.isNotEmpty()) {
                                                listState.animateScrollToItem(0)
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .padding(
                                            mutualNonTopWindowInsets
                                            .add(WindowInsets(left = 8.dp, right = 8.dp, bottom = 8.dp))
                                            .asPaddingValues()
                                        )
                                        .onSizeChanged { size ->
                                            searchBarHeight = size.height
                                        },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
