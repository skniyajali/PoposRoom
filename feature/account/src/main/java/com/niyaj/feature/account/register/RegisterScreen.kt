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

package com.niyaj.feature.account.register

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.feature.account.destinations.RegistrationResultScreenDestination
import com.niyaj.feature.account.register.components.RegistrationScaffold
import com.niyaj.feature.account.register.components.basic_info.BasicInfo
import com.niyaj.feature.account.register.components.basic_info.BasicInfoEvent
import com.niyaj.feature.account.register.components.login_info.LoginInfo
import com.niyaj.feature.account.register.components.login_info.LoginInfoEvent
import com.niyaj.feature.account.register.registration_result.RegistrationResult
import com.niyaj.feature.account.register.utils.RegisterScreenPage
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate

private const val CONTENT_ANIMATION_DURATION = 300


@OptIn(ExperimentalPermissionsApi::class)
@Destination(route = Screens.REGISTER_SCREEN)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    val screenData = viewModel.registerScreenState

    val emailError = viewModel.emailError.collectAsStateWithLifecycle().value
    val passwordError = viewModel.passwordError.collectAsStateWithLifecycle().value
    val phoneError = viewModel.phoneError.collectAsStateWithLifecycle().value

    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val secondaryPhoneError = viewModel.secondaryPhoneError.collectAsStateWithLifecycle().value
    val taglineError = viewModel.taglineError.collectAsStateWithLifecycle().value

    val addressError = viewModel.addressError.collectAsStateWithLifecycle().value
    val descError = viewModel.descError.collectAsStateWithLifecycle().value
    val paymentQrCodeError = viewModel.paymentQrCodeError.collectAsStateWithLifecycle().value

    val scannedBitmap = viewModel.scannedBitmap.collectAsStateWithLifecycle().value

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(null).value

    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES,
            ),
        )
    } else {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ),
        )
    }

    fun checkForMediaPermission() {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(key1 = Unit) {
        checkForMediaPermission()
    }

    val resLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            viewModel.onLoginInfoEvent(LoginInfoEvent.LogoChanged(uri))
        }
    }

    val printLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            viewModel.onBasicInfoEvent(BasicInfoEvent.PrintLogoChanged(it))
        }
    }

    LaunchedEffect(key1 = event) {
        event?.let {
            when (it) {
                is UiEvent.OnSuccess -> {
                    navController.navigate(
                        RegistrationResultScreenDestination(
                            result = RegistrationResult.Success,
                            message = it.successMessage,
                        ),
                    )
                }

                is UiEvent.OnError -> {
                    navController.navigate(
                        RegistrationResultScreenDestination(
                            result = RegistrationResult.Failure,
                            message = it.errorMessage,
                        ),
                    )
                }
            }
        }
    }

    BackHandler {
        if (!viewModel.onBackPressed()) {
            navController.navigateUp()
        }
    }

    RegistrationScaffold(
        snackbarHostState = snackbarHostState,
        screenData = screenData,
        isNextEnabled = viewModel.isNextEnabled,
        onClosePressed = {
            navController.navigateUp()
        },
        onPreviousPressed = viewModel::onPreviousPressed,
        onNextPressed = viewModel::onNextPressed,
        onDonePressed = viewModel::onDonePressed,
    ) { paddingValues ->
        val modifier = Modifier.padding(paddingValues)

        AnimatedContent(
            targetState = screenData,
            transitionSpec = {
                val animationSpec: TweenSpec<IntOffset> =
                    tween(CONTENT_ANIMATION_DURATION)
                val direction = getTransitionDirection(
                    initialIndex = initialState.pageIndex,
                    targetIndex = targetState.pageIndex,
                )
                slideIntoContainer(
                    towards = direction,
                    animationSpec = animationSpec,
                ) togetherWith slideOutOfContainer(
                    towards = direction,
                    animationSpec = animationSpec,
                )
            },
            label = "",
        ) { targetState ->
            when (targetState.screenPage) {
                RegisterScreenPage.LOGIN_INFO -> {
                    LoginInfo(
                        modifier = modifier,
                        lazyListState = lazyListState,
                        infoState = viewModel.loginInfoState,
                        nameError = nameError,
                        secondaryPhoneError = secondaryPhoneError,
                        emailError = emailError,
                        phoneError = phoneError,
                        passwordError = passwordError,
                        onChangeName = viewModel::onLoginInfoEvent,
                        onChangePhone = viewModel::onLoginInfoEvent,
                        onChangeEmail = viewModel::onLoginInfoEvent,
                        onChangeSecondaryPhone = viewModel::onLoginInfoEvent,
                        onChangePassword = viewModel::onLoginInfoEvent,
                        onChangeLogo = {
                            checkForMediaPermission()

                            resLogoLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly,
                                ),
                            )
                        },
                    )
                }

                RegisterScreenPage.BASIC_INFO -> {
                    BasicInfo(
                        modifier = modifier,
                        infoState = viewModel.basicInfoState,
                        lazyListState = lazyListState,
                        taglineError = taglineError,
                        addressError = addressError,
                        descriptionError = descError,
                        paymentQRCodeError = paymentQrCodeError,
                        scannedBitmap = scannedBitmap,
                        onChangeAddress = viewModel::onBasicInfoEvent,
                        onChangeDescription = viewModel::onBasicInfoEvent,
                        onChangePaymentQRCode = viewModel::onBasicInfoEvent,
                        onClickScanCode = viewModel::onBasicInfoEvent,
                        onChangeTagline = viewModel::onBasicInfoEvent,
                        onChangeLogo = {
                            checkForMediaPermission()

                            printLogoLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly,
                                ),
                            )
                        },
                    )
                }
            }
        }
    }
}


private fun getTransitionDirection(
    initialIndex: Int,
    targetIndex: Int,
): AnimatedContentTransitionScope.SlideDirection {
    return if (targetIndex > initialIndex) {
        // Going forwards in the survey: Set the initial offset to start
        // at the size of the content so it slides in from right to left, and
        // slides out from the left of the screen to -fullWidth
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        // Going back to the previous question in the set, we do the same
        // transition as above, but with different offsets - the inverse of
        // above, negative fullWidth to enter, and fullWidth to exit.
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}
