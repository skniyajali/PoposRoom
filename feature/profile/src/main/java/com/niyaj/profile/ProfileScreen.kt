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

@file:Suppress("DEPRECATION")

package com.niyaj.profile

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.common.tags.ProfileTestTags.PRINT_LOGO_NOTE
import com.niyaj.common.tags.ProfileTestTags.PROFILE_SCREEN
import com.niyaj.common.tags.ProfileTestTags.RES_LOGO_NOTE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Account
import com.niyaj.model.Profile
import com.niyaj.profile.components.AccountInfo
import com.niyaj.profile.components.RestaurantCard
import com.niyaj.profile.destinations.ChangePasswordScreenDestination
import com.niyaj.profile.destinations.UpdateProfileScreenDestination
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

/**
 * Profile Screen Composable
 * @author Sk Niyaj Ali
 *
 */
@RootNavGraph(start = true)
@Destination(route = Screens.PROFILE_SCREEN)
@Composable
fun ProfileScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<UpdateProfileScreenDestination, String>,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()

    val statusBarColor = MaterialTheme.colorScheme.primary

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false,
        )
    }

    val info = viewModel.info.collectAsStateWithLifecycle().value
    val accountInfo = viewModel.accountInfo.collectAsStateWithLifecycle().value

    val resLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { viewModel.changeRestaurantLogo(it) }
    }

    val printLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { viewModel.changePrintLogo(it) }
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

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    BackHandler {
        navigator.popBackStack()
    }

    TrackScreenViewEvent(screenName = Screens.PROFILE_SCREEN)

    ProfileScreenContent(
        modifier = modifier,
        profile = info,
        accountInfo = accountInfo,
        onBackClick = navigator::popBackStack,
        onClickEditProfile = {
            navigator.navigate(UpdateProfileScreenDestination(it))
        },
        onClickChangePassword = {
            navigator.navigate(ChangePasswordScreenDestination(it))
        },
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
internal fun ProfileScreenContent(
    profile: Profile,
    onBackClick: () -> Unit,
    onClickEditProfile: (Int) -> Unit,
    onClickChangePassword: (Int) -> Unit,
    onClickChangeResLogo: () -> Unit,
    onClickChangePrintLogo: () -> Unit,
    modifier: Modifier = Modifier,
    accountInfo: Account? = null,
    statusBarColor: Color = MaterialTheme.colorScheme.primary,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val lazyListState = rememberLazyListState()
    var showPrintLogo by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = PROFILE_SCREEN) },
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
                actions = {
                    IconButton(
                        onClick = {
                            onClickEditProfile(profile.restaurantId)
                        },
                    ) {
                        Icon(
                            imageVector = PoposIcons.Edit,
                            contentDescription = "Edit Profile",
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
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "ProfileScreen::State")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(LightColor6),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item("Restaurant Info") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    RestaurantCard(
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

            item("Account Info") {
                accountInfo?.let {
                    AccountInfo(
                        modifier = Modifier.padding(horizontal = SpaceSmall),
                        account = accountInfo,
                    )
                }
            }

            item("Change Password") {
                SettingsCard(
                    modifier = Modifier.padding(SpaceSmall),
                    title = "Change Password",
                    subtitle = "Click here to change password",
                    icon = PoposIcons.Password,
                    containerColor = Color.White,
                    onClick = {
                        onClickChangePassword(profile.restaurantId)
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ProfileScreenContentPreview(
    modifier: Modifier = Modifier,
    profile: Profile = Profile.defaultProfileInfo,
    account: Account = Account.defaultAccount,
) {
    PoposRoomTheme {
        ProfileScreenContent(
            modifier = modifier,
            profile = profile,
            accountInfo = account,
            onBackClick = {},
            onClickEditProfile = {},
            onClickChangePassword = {},
            onClickChangeResLogo = {},
            onClickChangePrintLogo = {},
        )
    }
}
