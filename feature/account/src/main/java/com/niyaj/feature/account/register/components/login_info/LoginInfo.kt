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

package com.niyaj.feature.account.register.components.login_info

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.account.R
import com.niyaj.ui.components.ImageCard
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.PhoneNoCountBox
import com.niyaj.ui.components.StandardOutlinedTextField

@Composable
fun LoginInfo(
    modifier: Modifier,
    lazyListState: LazyListState,
    infoState: LoginInfoState,
    nameError: String? = null,
    emailError: String? = null,
    phoneError: String? = null,
    passwordError: String? = null,
    secondaryPhoneError: String? = null,
    @DrawableRes
    defaultLogo: Int = com.niyaj.core.ui.R.drawable.popos,
    onChangeName: (LoginInfoEvent) -> Unit,
    onChangePhone: (LoginInfoEvent) -> Unit,
    onChangeEmail: (LoginInfoEvent) -> Unit,
    onChangeSecondaryPhone: (LoginInfoEvent) -> Unit,
    onChangePassword: (LoginInfoEvent) -> Unit,
    onChangeLogo: () -> Unit,
) = trace("LoginInfo") {
    var showPassword by remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceMedium, Alignment.CenterVertically),
        state = lazyListState,
    ) {
        item("Title") {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceMini),
            ) {
                Text(
                    text = stringResource(R.string.setup_profile),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = stringResource(R.string.setup_profile_text),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }

        item("Res_Logo") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                ImageCard(
                    defaultImage = defaultLogo,
                    imageName = infoState.logo,
                    onEditClick = onChangeLogo,
                )

                NoteCard(
                    text = stringResource(id = R.string.login_info_note),
                )
            }
        }

        item("Name_field") {
            StandardOutlinedTextField(
                label = "Restaurant Name",
                value = infoState.name,
                leadingIcon = PoposIcons.Restaurant,
                isError = nameError != null,
                errorText = nameError,
                onValueChange = {
                    onChangeName(LoginInfoEvent.NameChanged(it))
                },
            )
        }

        item("Phone Field") {
            StandardOutlinedTextField(
                label = "Phone No",
                value = infoState.phone,
                leadingIcon = PoposIcons.PhoneAndroid,
                isError = phoneError != null,
                errorText = phoneError,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    onChangePhone(LoginInfoEvent.PhoneChanged(it))
                },
                trailingIcon = {
                    PhoneNoCountBox(count = infoState.phone.length)
                },
            )
        }

        item("Secondary Phone") {
            StandardOutlinedTextField(
                label = "Secondary Phone",
                value = infoState.secondaryPhone,
                leadingIcon = PoposIcons.Phone,
                isError = secondaryPhoneError != null,
                errorText = secondaryPhoneError,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    onChangeSecondaryPhone(LoginInfoEvent.SecondaryPhoneChanged(it))
                },
                trailingIcon = {
                    PhoneNoCountBox(count = infoState.secondaryPhone.length)
                },
            )
        }

        item("Email_Field") {
            StandardOutlinedTextField(
                value = infoState.email,
                label = "Email Address",
                leadingIcon = PoposIcons.Email,
                isError = emailError != null,
                errorText = emailError,
                keyboardType = KeyboardType.Email,
                onValueChange = {
                    onChangeEmail(LoginInfoEvent.EmailChanged(it))
                },
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Cursive,
                ),
            )

        }

        item("Password_Field") {
            StandardOutlinedTextField(
                label = stringResource(R.string.password),
                value = infoState.password,
                leadingIcon = PoposIcons.Password,
                isError = passwordError != null,
                errorText = passwordError,
                isPasswordToggleDisplayed = true,
                isPasswordVisible = showPassword,
                onPasswordToggleClick = {
                    showPassword = !showPassword
                },
                onValueChange = {
                    onChangePassword(LoginInfoEvent.PasswordChanged(it))
                },
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Cursive,
                ),
            )
        }

    }
}