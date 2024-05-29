/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.poposroom

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import androidx.profileinstaller.ProfileVerifier
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.utils.findActivity
import com.niyaj.common.utils.openAppSettings
import com.niyaj.common.utils.showToast
import com.niyaj.data.repository.UserDataRepository
import com.niyaj.data.utils.NetworkMonitor
import com.niyaj.data.utils.WorkMonitor
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.feature.account.AccountNavGraph
import com.niyaj.home.HomeNavGraph
import com.niyaj.model.DarkThemeConfig
import com.niyaj.model.ThemeBrand
import com.niyaj.poposroom.ui.PoposApp
import com.niyaj.poposroom.ui.rememberPoposAppState
import com.niyaj.ui.components.CustomPermissionDialog
import com.samples.apps.core.analytics.AnalyticsHelper
import com.samples.apps.core.analytics.LocalAnalyticsHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var lazyStats: dagger.Lazy<JankStats>

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var workMonitor: WorkMonitor

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var userDataRepository: UserDataRepository

    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainActivityUiState.Loading -> true
                is MainActivityUiState.Success -> false
            }
        }

        enableEdgeToEdge()

        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            run {
                if (result.resultCode != RESULT_OK) {
                    this.showToast("Something Went Wrong!")
                }
            }
        }

        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)

            // Update the dark content of the system bars to match the theme
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { darkTheme },
                )

                onDispose {}
            }

            val appState = rememberPoposAppState(
                networkMonitor = networkMonitor,
                workMonitor = workMonitor,
                userDataRepository = userDataRepository,
            )
            val isLoggedIn by appState.isLoggedIn.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalAnalyticsHelper provides analyticsHelper) {
                PoposRoomTheme(
                    darkTheme = darkTheme,
                    androidTheme = shouldUseAndroidTheme(uiState),
                    disableDynamicTheming = shouldDisableDynamicTheming(uiState),
                ) {
                    RequestAllPermissions()

                    PoposApp(
                        appState = appState,
                        startRoute = if (isLoggedIn) HomeNavGraph else AccountNavGraph,
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lazyStats.get().isTrackingEnabled = true
        lifecycleScope.launch {
            logCompilationStatus()
        }
    }

    override fun onPause() {
        super.onPause()
        lazyStats.get().isTrackingEnabled = false
    }

    private suspend fun logCompilationStatus() {
        withContext(Dispatchers.IO) {
            val status = ProfileVerifier.getCompilationStatusAsync().await()
            Timber.tag(TAG).d("ProfileInstaller status code: ${status.profileInstallResultCode}")
            Timber.d(
                TAG,
                when {
                    status.isCompiledWithProfile -> "ProfileInstaller: is compiled with profile"
                    status.hasProfileEnqueuedForCompilation() ->
                        "ProfileInstaller: Enqueued for compilation"

                    else -> "Profile not compiled or enqueued"
                },
            )
        }
    }
}

@Composable
private fun shouldUseAndroidTheme(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    MainActivityUiState.Loading -> false
    is MainActivityUiState.Success -> when (uiState.userData.themeBrand) {
        ThemeBrand.DEFAULT -> false
        ThemeBrand.ANDROID -> true
    }
}

@Composable
private fun shouldDisableDynamicTheming(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    MainActivityUiState.Loading -> false
    is MainActivityUiState.Success -> !uiState.userData.useDynamicColor
}

@Composable
private fun shouldUseDarkTheme(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    MainActivityUiState.Loading -> isSystemInDarkTheme()
    is MainActivityUiState.Success -> when (uiState.userData.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
}

private val allPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.POST_NOTIFICATIONS,
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
        )
    }
} else {
    listOf(
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
    )
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun RequestAllPermissions() {
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val allPermissionState = rememberMultiplePermissionsState(
        permissions = allPermissions,
        onPermissionsResult = { permissions ->
            showDialog.value = !permissions.all { it.value }
        },
    )

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    allPermissionState.launchMultiplePermissionRequest()
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        },
    )

    if (showDialog.value) {
        CustomPermissionDialog(
            title = "Permissions are required",
            text = "Please grant all permissions for the app to function properly.",
            icon = PoposIcons.Info,
            shouldShowRationale = allPermissionState.shouldShowRationale,
            onClickRequestPermission = {
                allPermissionState.launchMultiplePermissionRequest()
            },
            onClickOpenSettings = {
                context.findActivity().openAppSettings()
            },
            onDismissRequest = {
                showDialog.value = false
            },
        )
    }
}


private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)