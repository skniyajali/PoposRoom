/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.profile.createOrUpdate

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.common.tags.ProfileTestTags.ADDRESS_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.ADDRESS_FIELD
import com.niyaj.common.tags.ProfileTestTags.ADD_EDIT_PROFILE_BTN
import com.niyaj.common.tags.ProfileTestTags.DESC_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.DESC_FIELD
import com.niyaj.common.tags.ProfileTestTags.EMAIL_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.EMAIL_FIELD
import com.niyaj.common.tags.ProfileTestTags.NAME_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.NAME_FIELD
import com.niyaj.common.tags.ProfileTestTags.PRINT_LOGO_NOTE
import com.niyaj.common.tags.ProfileTestTags.P_PHONE_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.P_PHONE_FIELD
import com.niyaj.common.tags.ProfileTestTags.QR_CODE_ERROR_TAG
import com.niyaj.common.tags.ProfileTestTags.QR_CODE_FIELD
import com.niyaj.common.tags.ProfileTestTags.RES_LOGO_NOTE
import com.niyaj.common.tags.ProfileTestTags.S_PHONE_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.S_PHONE_FIELD
import com.niyaj.common.tags.ProfileTestTags.TAG_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.TAG_FIELD
import com.niyaj.common.tags.ProfileTestTags.UPDATE_PROFILE
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Profile
import com.niyaj.profile.components.UpdatedRestaurantCard
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@Destination(route = Screens.UPDATE_PROFILE_SCREEN)
@Composable
fun UpdateProfileScreen(
    resId: Int,
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    viewModel: UpdateProfileViewModel = hiltViewModel(),
) {
    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colorScheme.primary

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false,
        )
    }

    val info = viewModel.info.collectAsStateWithLifecycle().value
    val scannedBitmap = viewModel.scannedBitmap.collectAsStateWithLifecycle().value

    val resLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            scope.launch {
                viewModel.onEvent(UpdateProfileEvent.LogoChanged(uri = it))
            }
        }
    }

    val printLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            scope.launch {
                viewModel.onEvent(UpdateProfileEvent.PrintLogoChanged(uri = it))
            }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }
            }
        }
    }

    TrackScreenViewEvent(screenName = Screens.UPDATE_PROFILE_SCREEN + "resId?=$resId")

    UpdateProfileScreenContent(
        modifier = modifier,
        profile = info,
        state = viewModel.updateState,
        scannedBitmap = scannedBitmap?.asImageBitmap(),
        onEvent = viewModel::onEvent,
        onBackClick = navigator::navigateUp,
        onClickChangeResLogo = {
            resLogoLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        onClickChangePrintLogo = {
            printLogoLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        statusBarColor = statusBarColor,
        snackbarHostState = snackbarHostState,
    )
}

@VisibleForTesting
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UpdateProfileScreenContent(
    profile: Profile,
    state: UpdateProfileState,
    onEvent: (UpdateProfileEvent) -> Unit,
    onBackClick: () -> Unit,
    onClickChangeResLogo: () -> Unit,
    onClickChangePrintLogo: () -> Unit,
    modifier: Modifier = Modifier,
    scannedBitmap: ImageBitmap? = null,
    statusBarColor: Color = MaterialTheme.colorScheme.primary,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val lazyListState = rememberLazyListState()

    var showPrintLogo by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = UPDATE_PROFILE) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                    ) {
                        Icon(
                            imageVector = PoposIcons.ArrowBack,
                            contentDescription = "Navigate Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = statusBarColor,
                    titleContentColor = contentColorFor(backgroundColor = statusBarColor),
                    actionIconContentColor = contentColorFor(backgroundColor = statusBarColor),
                    navigationIconContentColor = contentColorFor(backgroundColor = statusBarColor),
                ),
            )
        },
    ) { paddingValues ->
        TrackScrollJank(scrollableState = lazyListState, stateName = "UpdateProfile::Fields")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            state = lazyListState,
            contentPadding = PaddingValues(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item("UpdatedRestaurantCard") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    UpdatedRestaurantCard(
                        modifier = Modifier,
                        info = profile,
                        showPrintLogo = showPrintLogo,
                        onClickChangeResLogo = onClickChangeResLogo,
                        onClickChangePrintLogo = onClickChangePrintLogo,
                        onClickViewPrintLogo = {
                            showPrintLogo = !showPrintLogo
                        },
                    )
                    NoteCard(text = RES_LOGO_NOTE)
                    Spacer(modifier = Modifier.height(SpaceMini))
                    NoteCard(text = PRINT_LOGO_NOTE)
                }
            }

            item(NAME_FIELD) {
                StandardOutlinedTextField(
                    label = NAME_FIELD,
                    leadingIcon = PoposIcons.Restaurant,
                    value = state.name,
                    onValueChange = {
                        onEvent(UpdateProfileEvent.NameChanged(it))
                    },
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(NAME_FIELD),
                    isError = state.nameError != null,
                    errorText = state.nameError,
                    errorTextTag = NAME_ERROR_FIELD,
                )
            }

            item(EMAIL_FIELD) {
                StandardOutlinedTextField(
                    label = EMAIL_FIELD,
                    leadingIcon = PoposIcons.Email,
                    value = state.email,
                    onValueChange = {
                        onEvent(UpdateProfileEvent.NameChanged(it))
                    },
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(EMAIL_FIELD),
                    isError = state.emailError != null,
                    errorText = state.emailError,
                    keyboardType = KeyboardType.Email,
                    errorTextTag = EMAIL_ERROR_FIELD,
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Cursive,
                    ),
                )
            }

            item(P_PHONE_FIELD) {
                StandardOutlinedTextField(
                    label = P_PHONE_FIELD,
                    leadingIcon = PoposIcons.PhoneAndroid,
                    value = state.primaryPhone,
                    onValueChange = {
                        onEvent(UpdateProfileEvent.PrimaryPhoneChanged(it))
                    },
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(P_PHONE_FIELD),
                    isError = state.primaryPhoneError != null,
                    errorText = state.primaryPhoneError,
                    keyboardType = KeyboardType.Number,
                    errorTextTag = P_PHONE_ERROR_FIELD,
                )
            }

            item(S_PHONE_FIELD) {
                StandardOutlinedTextField(
                    label = S_PHONE_FIELD,
                    leadingIcon = PoposIcons.Phone,
                    value = state.secondaryPhone,
                    onValueChange = {
                        onEvent(UpdateProfileEvent.SecondaryPhoneChanged(it))
                    },
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(S_PHONE_FIELD),
                    isError = state.secondaryPhoneError != null,
                    errorText = state.secondaryPhoneError,
                    keyboardType = KeyboardType.Number,
                    errorTextTag = S_PHONE_ERROR_FIELD,
                )
            }

            item(TAG_FIELD) {
                StandardOutlinedTextField(
                    label = TAG_FIELD,
                    leadingIcon = PoposIcons.StarHalf,
                    value = state.tagline,
                    onValueChange = {
                        onEvent(UpdateProfileEvent.TaglineChanged(it))
                    },
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(TAG_FIELD),
                    isError = state.taglineError != null,
                    errorText = state.taglineError,
                    errorTextTag = TAG_ERROR_FIELD,
                )
            }

            item(ADDRESS_FIELD) {
                StandardOutlinedTextField(
                    label = ADDRESS_FIELD,
                    leadingIcon = PoposIcons.LocationOn,
                    value = state.address,
                    onValueChange = {
                        onEvent(UpdateProfileEvent.AddressChanged(it))
                    },
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(ADDRESS_FIELD),
                    isError = state.addressError != null,
                    errorText = state.addressError,
                    maxLines = 2,
                    errorTextTag = ADDRESS_ERROR_FIELD,
                )
            }

            item(DESC_FIELD) {
                StandardOutlinedTextField(
                    label = DESC_FIELD,
                    leadingIcon = PoposIcons.Note,
                    value = state.description,
                    onValueChange = {
                        onEvent(UpdateProfileEvent.DescriptionChanged(it))
                    },
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(DESC_FIELD),
                    isError = state.descriptionError != null,
                    errorText = state.descriptionError,
                    singleLine = false,
                    maxLines = 2,
                    errorTextTag = DESC_ERROR_FIELD,
                )
            }

            item(QR_CODE_FIELD) {
                StandardOutlinedTextField(
                    label = QR_CODE_FIELD,
                    leadingIcon = PoposIcons.QrCode,
                    value = state.paymentQrCode,
                    onValueChange = {
                        onEvent(UpdateProfileEvent.PaymentQrCodeChanged(it))
                    },
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(QR_CODE_FIELD),
                    isError = state.paymentQrCodeError != null,
                    errorText = state.paymentQrCodeError,
                    singleLine = false,
                    maxLines = 4,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                onEvent(UpdateProfileEvent.StartScanning)
                            },
                        ) {
                            Icon(
                                imageVector = PoposIcons.QrCodeScanner,
                                contentDescription = "Scan QR Code",
                            )
                        }
                    },
                    errorTextTag = QR_CODE_ERROR_TAG,
                )
            }

            item("Scanned Bitmap") {
                if (scannedBitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            bitmap = scannedBitmap,
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }

            item(ADD_EDIT_PROFILE_BTN) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                PoposButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpaceSmall)
                        .testTag(ADD_EDIT_PROFILE_BTN),
                    text = UPDATE_PROFILE,
                    icon = PoposIcons.Edit,
                    onClick = {
                        onEvent(UpdateProfileEvent.UpdateUpdateProfile)
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}

@DevicePreviews
@Composable
private fun UpdateProfileScreenContentPreview(
    modifier: Modifier = Modifier,
    profile: Profile = Profile.defaultProfileInfo,
) {
    PoposRoomTheme {
        UpdateProfileScreenContent(
            modifier = modifier,
            profile = profile,
            state = UpdateProfileState(),
            scannedBitmap = null,
            onEvent = {},
            onBackClick = {},
            onClickChangeResLogo = {},
            onClickChangePrintLogo = {},
        )
    }
}
