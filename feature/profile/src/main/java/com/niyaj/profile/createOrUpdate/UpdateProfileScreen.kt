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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
import com.niyaj.common.tags.ProfileTestTags.P_PHONE_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.P_PHONE_FIELD
import com.niyaj.common.tags.ProfileTestTags.QR_CODE_ERROR_TAG
import com.niyaj.common.tags.ProfileTestTags.QR_CODE_FIELD
import com.niyaj.common.tags.ProfileTestTags.S_PHONE_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.S_PHONE_FIELD
import com.niyaj.common.tags.ProfileTestTags.TAG_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.TAG_FIELD
import com.niyaj.common.tags.ProfileTestTags.UPDATE_PROFILE
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.profile.ProfileEvent
import com.niyaj.profile.ProfileViewModel
import com.niyaj.profile.components.UpdatedRestaurantCard
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Destination(route = Screens.UPDATE_PROFILE_SCREEN)
@Composable
fun UpdateProfileScreen(
    navController: NavController = rememberNavController(),
    viewModel: ProfileViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colorScheme.primary

    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false,
        )
    }

    val info = viewModel.info.collectAsStateWithLifecycle().value

    val scannedBitmap = viewModel.scannedBitmap.collectAsStateWithLifecycle().value

    var showPrintLogo by rememberSaveable {
        mutableStateOf(false)
    }

    val resLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            scope.launch {
                viewModel.onEvent(ProfileEvent.LogoChanged(uri = it))
            }
        }
    }

    val printLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            scope.launch {
                viewModel.onEvent(ProfileEvent.PrintLogoChanged(uri = it))
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

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(ProfileEvent.SetProfileInfo)
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = event.successMessage,
                    )
                }

                is UiEvent.OnError -> {
                    snackbarHostState.showSnackbar(
                        message = event.errorMessage,
                    )
                }
            }
        }
    }

    TrackScreenViewEvent(screenName = Screens.UPDATE_PROFILE_SCREEN)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = UPDATE_PROFILE) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
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
            val sidePadding = (-8).dp

            item("UpdatedRestaurantCard") {
                UpdatedRestaurantCard(
                    modifier = Modifier
                        .layout { measurable, constraints ->
                            // Measure the composable adding the side padding*2 (left+right)
                            val placeable =
                                measurable.measure(
                                    constraints.offset(
                                        horizontal = -sidePadding.roundToPx() * 2,
                                        vertical = -sidePadding.roundToPx(),
                                    ),
                                )

                            // increase the width adding the side padding*2
                            layout(
                                placeable.width + sidePadding.roundToPx() * 2,
                                placeable.height,
                            ) {
                                // Where the composable gets placed
                                placeable.place(+sidePadding.roundToPx(), +sidePadding.roundToPx())
                            }
                        },
                    info = info,
                    showPrintLogo = showPrintLogo,
                    onClickEdit = {
                        resLogoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                    onClickChangePrintLogo = {
                        printLogoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                    onClickViewPrintLogo = {
                        showPrintLogo = !showPrintLogo
                    },
                )
            }

            item(NAME_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(NAME_FIELD),
                    value = viewModel.updateState.name,
                    label = NAME_FIELD,
                    leadingIcon = PoposIcons.Restaurant,
                    isError = viewModel.updateState.nameError != null,
                    errorText = viewModel.updateState.nameError,
                    errorTextTag = NAME_ERROR_FIELD,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.NameChanged(it))
                    },
                )
            }

            item(EMAIL_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(EMAIL_FIELD),
                    value = viewModel.updateState.email,
                    label = EMAIL_FIELD,
                    leadingIcon = PoposIcons.Email,
                    errorTextTag = EMAIL_ERROR_FIELD,
                    isError = viewModel.updateState.emailError != null,
                    errorText = viewModel.updateState.emailError,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.NameChanged(it))
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Cursive,
                    ),
                    keyboardType = KeyboardType.Email,
                )
            }

            item(P_PHONE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(P_PHONE_FIELD),
                    value = viewModel.updateState.primaryPhone,
                    label = P_PHONE_FIELD,
                    leadingIcon = PoposIcons.PhoneAndroid,
                    errorTextTag = P_PHONE_ERROR_FIELD,
                    isError = viewModel.updateState.primaryPhoneError != null,
                    errorText = viewModel.updateState.primaryPhoneError,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.PrimaryPhoneChanged(it))
                    },
                    keyboardType = KeyboardType.Number,
                )
            }

            item(S_PHONE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(S_PHONE_FIELD),
                    value = viewModel.updateState.secondaryPhone,
                    label = S_PHONE_FIELD,
                    leadingIcon = PoposIcons.Phone,
                    errorTextTag = S_PHONE_ERROR_FIELD,
                    isError = viewModel.updateState.secondaryPhoneError != null,
                    errorText = viewModel.updateState.secondaryPhoneError,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.SecondaryPhoneChanged(it))
                    },
                    keyboardType = KeyboardType.Number,
                )
            }

            item(TAG_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(TAG_FIELD),
                    value = viewModel.updateState.tagline,
                    label = TAG_FIELD,
                    errorTextTag = TAG_ERROR_FIELD,
                    leadingIcon = PoposIcons.StarHalf,
                    isError = viewModel.updateState.taglineError != null,
                    errorText = viewModel.updateState.taglineError,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.TaglineChanged(it))
                    },
                )
            }

            item(ADDRESS_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(ADDRESS_FIELD),
                    value = viewModel.updateState.address,
                    label = ADDRESS_FIELD,
                    maxLines = 2,
                    leadingIcon = PoposIcons.LocationOn,
                    errorTextTag = ADDRESS_ERROR_FIELD,
                    isError = viewModel.updateState.addressError != null,
                    errorText = viewModel.updateState.addressError,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.AddressChanged(it))
                    },
                )
            }

            item(DESC_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(DESC_FIELD),
                    value = viewModel.updateState.description,
                    label = DESC_FIELD,
                    singleLine = false,
                    maxLines = 2,
                    leadingIcon = PoposIcons.Note,
                    isError = viewModel.updateState.descriptionError != null,
                    errorText = viewModel.updateState.descriptionError,
                    errorTextTag = DESC_ERROR_FIELD,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.DescriptionChanged(it))
                    },
                )
            }

            item(QR_CODE_FIELD) {
                StandardOutlinedTextField(
                    modifier = Modifier
                        .padding(horizontal = SpaceSmall)
                        .testTag(QR_CODE_FIELD),
                    value = viewModel.updateState.paymentQrCode,
                    label = QR_CODE_FIELD,
                    leadingIcon = PoposIcons.QrCode,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(ProfileEvent.StartScanning)
                            },
                        ) {
                            Icon(
                                imageVector = PoposIcons.QrCodeScanner,
                                contentDescription = "Scan QR Code",
                            )
                        }
                    },
                    isError = viewModel.updateState.paymentQrCodeError != null,
                    errorText = viewModel.updateState.paymentQrCodeError,
                    errorTextTag = QR_CODE_ERROR_TAG,
                    singleLine = false,
                    maxLines = 4,
                    onValueChange = {
                        viewModel.onEvent(ProfileEvent.PaymentQrCodeChanged(it))
                    },
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
                            bitmap = scannedBitmap.asImageBitmap(),
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
                        .testTag(ADD_EDIT_PROFILE_BTN),
                    text = UPDATE_PROFILE,
                    icon = PoposIcons.Edit,
                    onClick = {
                        viewModel.onEvent(ProfileEvent.UpdateProfile)
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}
