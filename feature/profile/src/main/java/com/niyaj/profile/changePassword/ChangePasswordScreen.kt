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

package com.niyaj.profile.changePassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.flow.collectLatest

@Destination(route = Screens.CHANGE_PASSWORD_SCREEN)
@Composable
fun ChangePasswordScreen(
    resId: Int,
    navController: DestinationsNavigator,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val lazyListState = rememberLazyListState()
    val passwordError = viewModel.passwordError.collectAsStateWithLifecycle().value
    val confirmError = viewModel.confirmPasswordError.collectAsStateWithLifecycle().value
    val error = viewModel.error.collectAsStateWithLifecycle().value

    val hasError = listOf(
        passwordError,
        confirmError,
    ).all { it == null }

    var showCurrent by remember {
        mutableStateOf(false)
    }

    var showNew by remember {
        mutableStateOf(false)
    }

    var showConfirm by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(it.successMessage)
                }

                else -> {}
            }
        }
    }

    TrackScreenViewEvent(screenName = Screens.CHANGE_PASSWORD_SCREEN + "resId?=$resId")

    PoposSecondaryScaffold(
        title = "Change Password",
        onBackClick = navController::navigateUp,
        showBottomBar = true,
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                PoposButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpaceMedium),
                    text = "Change Password",
                    enabled = hasError,
                    onClick = {
                        viewModel.onEvent(ChangePasswordEvent.ChangePassword)
                    },
                )
            }
        },
    ) { paddingValues ->
        TrackScrollJank(scrollableState = lazyListState, stateName = "ChangePassword::Fields")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            state = lazyListState,
            contentPadding = PaddingValues(SpaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item {
                StandardOutlinedTextField(
                    label = "Current Password",
                    value = viewModel.state.currentPassword,
                    leadingIcon = PoposIcons.Password,
                    keyboardType = KeyboardType.Password,
                    isPasswordVisible = showCurrent,
                    onPasswordToggleClick = {
                        showCurrent = !showCurrent
                    },
                    onValueChange = {
                        viewModel.onEvent(ChangePasswordEvent.CurrentPasswordChanged(it))
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Cursive,
                    ),
                )
            }

            item {
                StandardOutlinedTextField(
                    label = "New Password",
                    leadingIcon = PoposIcons.Password,
                    value = viewModel.state.newPassword,
                    isError = passwordError != null,
                    errorText = passwordError,
                    keyboardType = KeyboardType.Password,
                    isPasswordVisible = showNew,
                    onPasswordToggleClick = {
                        showNew = !showNew
                    },
                    onValueChange = {
                        viewModel.onEvent(ChangePasswordEvent.NewPasswordChanged(it))
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Cursive,
                    ),
                )
            }

            item {
                StandardOutlinedTextField(
                    label = "Confirm Password",
                    leadingIcon = PoposIcons.Password,
                    value = viewModel.state.confirmPassword,
                    isError = confirmError != null,
                    errorText = confirmError,
                    keyboardType = KeyboardType.Password,
                    isPasswordVisible = showConfirm,
                    onPasswordToggleClick = {
                        showConfirm = !showConfirm
                    },
                    onValueChange = {
                        viewModel.onEvent(ChangePasswordEvent.ConfirmPasswordChanged(it))
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Cursive,
                    ),
                )
            }

            error?.let {
                item {
                    Spacer(modifier = Modifier.height(SpaceSmall))
                    NoteCard(text = it)
                }
            }
        }
    }
}
