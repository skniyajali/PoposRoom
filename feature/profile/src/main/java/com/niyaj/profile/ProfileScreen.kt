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

package com.niyaj.profile

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.niyaj.common.tags.ProfileTestTags.PROFILE_SCREEN
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.profile.components.AccountInfo
import com.niyaj.profile.components.RestaurantCard
import com.niyaj.profile.destinations.ChangePasswordScreenDestination
import com.niyaj.profile.destinations.UpdateProfileScreenDestination
import com.niyaj.ui.components.SettingsCard
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
@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination(route = Screens.PROFILE_SCREEN)
@Composable
fun ProfileScreen(
    navigator: DestinationsNavigator,
    viewModel: ProfileViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<UpdateProfileScreenDestination, String>,
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
    val accountInfo = viewModel.accountInfo.collectAsStateWithLifecycle().value

    var showPrintLogo by rememberSaveable {
        mutableStateOf(false)
    }

    val resLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            viewModel.onEvent(ProfileEvent.LogoChanged(uri = it))
        }
    }

    val printLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            viewModel.onEvent(ProfileEvent.PrintLogoChanged(uri = it))
        }
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = PROFILE_SCREEN) },
                navigationIcon = {
                    IconButton(
                        onClick = navigator::popBackStack,
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
                            navigator.navigate(UpdateProfileScreenDestination())
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
                RestaurantCard(
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

            item("Account Info") {
                accountInfo?.let {
                    AccountInfo(
                        modifier = Modifier.padding(SpaceSmall),
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
                        navigator.navigate(ChangePasswordScreenDestination(info.restaurantId))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}