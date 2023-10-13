package com.niyaj.poposroom

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import androidx.profileinstaller.ProfileVerifier
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.niyaj.common.utils.Constants.NETWORK_PERMISSION_REQUEST_CODE
import com.niyaj.common.utils.Constants.NOTIFICATION_PERMISSION_REQUEST_CODE
import com.niyaj.common.utils.Constants.UPDATE_MANAGER_REQUEST_CODE
import com.niyaj.common.utils.hasNetworkPermission
import com.niyaj.common.utils.hasNotificationPermission
import com.niyaj.data.utils.NetworkMonitor
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.DarkThemeConfig
import com.niyaj.model.ThemeBrand
import com.niyaj.poposroom.ui.PoposApp
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

    /**
     * Lazily inject [JankStats], which is used to track jank throughout the app.
     */
    @Inject
    lateinit var lazyStats: dagger.Lazy<JankStats>

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateOptions = AppUpdateOptions
        .newBuilder(AppUpdateType.IMMEDIATE)
        .setAllowAssetPackDeletion(false)
        .build()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        appUpdateManager = AppUpdateManagerFactory.create(this)

        val hasNotificationPermission = this.hasNotificationPermission()
        val hasNetworkPermission = this.hasNetworkPermission()

        if (!hasNotificationPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        if (!hasNetworkPermission) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_NETWORK_STATE,
                ),
                NETWORK_PERMISSION_REQUEST_CODE
            )
        }

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach {
                        uiState = it
                    }.collect()
            }
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

//        enableSystemInsetsHandling()
        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, false)

        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            run {
                if (result.resultCode != RESULT_OK) {
                    Toast.makeText(
                        this,
                        "Something Went Wrong!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        setContent {
            val systemUiController = rememberSystemUiController()
            val darkTheme = shouldUseDarkTheme(uiState)

            // Update the dark content of the system bars to match the theme
            DisposableEffect(systemUiController, darkTheme) {
                systemUiController.systemBarsDarkContentEnabled = !darkTheme
                onDispose {}
            }

            val sizeClass = calculateWindowSizeClass(activity = this)

            PoposRoomTheme(
                darkTheme = darkTheme,
                androidTheme = shouldUseAndroidTheme(uiState),
                disableDynamicTheming = shouldDisableDynamicTheming(uiState),
            ) {
                PoposApp(
                    viewModel = viewModel,
                    windowSizeClass = sizeClass,
                    networkMonitor = networkMonitor,
                    onCheckForAppUpdate = {
                        checkForAppUpdates()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lazyStats.get().isTrackingEnabled = true
        lifecycleScope.launch {
            logCompilationStatus()
        }

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        this,
                        updateOptions,
                        UPDATE_MANAGER_REQUEST_CODE
                    )
                }
            }
    }

    override fun onPause() {
        super.onPause()
        lazyStats.get().isTrackingEnabled = false
    }

    /**
     * Logs the app's Baseline Profile Compilation Status using [ProfileVerifier].
     */
    private suspend fun logCompilationStatus() {
        /*
        When delivering through Google Play, the baseline profile is compiled during installation.
        In this case you will see the correct state logged without any further action necessary.
        To verify baseline profile installation locally, you need to manually trigger baseline
        profile installation.
        For immediate compilation, call:
         `adb shell cmd package compile -f -m speed-profile com.example.macrobenchmark.target`
        You can also trigger background optimizations:
         `adb shell pm bg-dexopt-job`
        Both jobs run asynchronously and might take some time complete.
        To see quick turnaround of the ProfileVerifier, we recommend using `speed-profile`.
        If you don't do either of these steps, you might only see the profile status reported as
        "enqueued for compilation" when running the sample locally.
        */
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

    private fun checkForAppUpdates() {
        if (!BuildConfig.DEBUG) {
            appUpdateManager
                .appUpdateInfo
                .addOnSuccessListener { info ->
                    val isUpdateAvailable =
                        info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

                    val isUpdateAllowed = when (updateOptions.appUpdateType()) {
                        AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                        else -> false
                    }

                    if (isUpdateAvailable && isUpdateAllowed) {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            this,
                            updateOptions,
                            UPDATE_MANAGER_REQUEST_CODE
                        )
                    }

                }.addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Unable to update app!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}

/**
 * Returns `true` if the Android theme should be used, as a function of the [uiState].
 */
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

/**
 * Returns `true` if the dynamic color is disabled, as a function of the [uiState].
 */
@Composable
private fun shouldDisableDynamicTheming(
    uiState: MainActivityUiState,
): Boolean = when (uiState) {
    MainActivityUiState.Loading -> false
    is MainActivityUiState.Success -> !uiState.userData.useDynamicColor
}

/**
 * Returns `true` if dark theme should be used, as a function of the [uiState] and the
 * current system context.
 */
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