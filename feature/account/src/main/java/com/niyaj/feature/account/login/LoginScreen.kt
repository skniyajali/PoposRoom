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

package com.niyaj.feature.account.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.designsystem.components.PoposTextButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.Ivory
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.gradient6
import com.niyaj.feature.account.R
import com.niyaj.feature.account.destinations.RegisterScreenDestination
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate

/**
 * A Composable function representing the login screen.
 * @author Sk Niyaj Ali
 * @param navController The NavController used for navigation within the app.
 * @param viewModel The LoginViewModel used for managing the login screen's state and logic.
 */
@RootNavGraph(start = true)
@Destination(route = Screens.LOGIN_SCREEN)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val lazyListState = rememberLazyListState()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val checkLoggedIn = viewModel.isLoggedIn.collectAsStateWithLifecycle().value

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var showPassword by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = checkLoggedIn, key2 = Unit) {
        if (checkLoggedIn) {
            navController.navigate(Screens.HOME_SCREEN) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        }
    }

    LaunchedEffect(key1 = event) {
        event?.let { result ->
            when (result) {
                is UiEvent.OnSuccess -> {
                    navController.navigate(Screens.HOME_SCREEN) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }

                is UiEvent.OnError -> {
                    error = result.errorMessage
                }
            }
        }
    }

    TrackScreenViewEvent(screenName = Screens.LOGIN_SCREEN)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient6),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f, true),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.login),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
            )
        }

        Card(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            modifier = Modifier
                .weight(1.8f),
            colors = CardDefaults.cardColors(
                containerColor = Ivory,
            ),
        ) {
            LoginForm(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceMedium),
                lazyListState = lazyListState,
                emailOrPhone = viewModel.state.emailOrPhone,
                password = viewModel.state.password,
                emailError = viewModel.state.emailError,
                passwordError = viewModel.state.passwordError,
                errorMessage = error,
                showPassword = showPassword,
                onTogglePassword = {
                    showPassword = !showPassword
                },
                onEmailOrPhoneChanged = {
                    viewModel.onEvent(LoginEvent.EmailOrPhoneChanged(it))
                },
                onPasswordChanged = {
                    viewModel.onEvent(LoginEvent.PasswordChanged(it))
                },
                onClickLogin = {
                    viewModel.onEvent(LoginEvent.OnClickLogin)
                },
                onClickRegister = {
                    navController.navigate(RegisterScreenDestination())
                },
            )
        }
    }
}

/**
 * A composable function that renders a login form UI.
 *
 * @author Sk Niyaj Ali
 * @param modifier Modifier for customizing the layout of the login form.
 * @param lazyListState State object for managing the scroll position of the LazyColumn.
 * @param emailOrPhone Current value of the email/phone field.
 * @param password Current value of the password field.
 * @param showPassword Flag indicating whether the password should be shown or hidden.
 * @param onTogglePassword Callback function invoked when the password visibility toggle is clicked.
 * @param emailError Error message for the email/phone field, if any.
 * @param passwordError Error message for the password field, if any.
 * @param errorMessage Error message to be displayed, if any.
 * @param onEmailOrPhoneChanged Callback function invoked when the email/phone field value changes.
 * @param onPasswordChanged Callback function invoked when the password field value changes.
 * @param onClickLogin Callback function invoked when the login button is clicked.
 * @param onClickRegister Callback function invoked when the register button is clicked.
 */
@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    emailOrPhone: String,
    password: String,
    showPassword: Boolean,
    onTogglePassword: (Boolean) -> Unit,
    emailError: String? = null,
    passwordError: String? = null,
    errorMessage: String? = null,
    onEmailOrPhoneChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onClickLogin: () -> Unit,
    onClickRegister: () -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        state = lazyListState,
    ) {
        item("Welcome_Text") {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceMini),
            ) {
                Text(
                    text = stringResource(R.string.welcome) + "\uD83D\uDC4B",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = stringResource(R.string.login_text),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }

        item("Email/Phone_Field") {
            StandardOutlinedTextField(
                value = emailOrPhone,
                leadingIcon = PoposIcons.Email,
                isError = emailError != null,
                errorText = emailError,
                label = stringResource(R.string.email_phone),
                onValueChange = onEmailOrPhoneChanged,
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Cursive,
                ),
            )
        }

        item("Password_Field") {
            StandardOutlinedTextField(
                label = stringResource(R.string.password),
                leadingIcon = PoposIcons.Password,
                value = password,
                onValueChange = onPasswordChanged,
                isError = passwordError != null,
                errorText = passwordError,
                isPasswordToggleDisplayed = true,
                isPasswordVisible = showPassword,
                onPasswordToggleClick = onTogglePassword,
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Cursive,
                ),
            )
        }

        errorMessage?.let {
            item("Message_Field") {
                NoteCard(text = it)
            }
        }

        item("Login_Button") {
            Spacer(modifier = Modifier.height(SpaceSmall))

            StandardButton(
                text = stringResource(id = R.string.login),
                icon = PoposIcons.Login,
                onClick = onClickLogin,
                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.secondary,
                ),
            )
        }

        item("Signup_Button") {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.CenterVertically),
            ) {
                Text(
                    text = stringResource(R.string.no_account),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                )

                PoposTextButton(
                    onClick = onClickRegister,
                    text = stringResource(id = R.string.register),
                    icon = PoposIcons.ArrowForward,
                )
            }
        }
    }
}