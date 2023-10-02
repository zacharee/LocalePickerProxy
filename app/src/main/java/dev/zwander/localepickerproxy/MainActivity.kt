package dev.zwander.localepickerproxy

import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import dev.zwander.localepickerproxy.components.AppList
import dev.zwander.localepickerproxy.ui.theme.LocalePickerProxyTheme
import dev.zwander.localepickerproxy.util.getAllAppsSupportingLocales
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lsposed.hiddenapibypass.HiddenApiBypass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HiddenApiBypass.addHiddenApiExemptions("")

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val context = LocalContext.current

            var apps by remember {
                mutableStateOf(listOf<ApplicationInfo>())
            }

            var isRefreshing by remember {
                mutableStateOf(true)
            }

            val pullRefreshState = rememberPullRefreshState(
                refreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                }
            )

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
                    color = MaterialTheme.colorScheme.background
                ) {
                    Crossfade(
                        targetState = isRefreshing,
                        label = "MainFade",
                    ) {
                        if (it) {
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
                                    apps = apps,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .pullRefresh(state = pullRefreshState),
                                )

                                PullRefreshIndicator(
                                    refreshing = false,
                                    state = pullRefreshState,
                                    modifier = Modifier.align(Alignment.TopCenter),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
