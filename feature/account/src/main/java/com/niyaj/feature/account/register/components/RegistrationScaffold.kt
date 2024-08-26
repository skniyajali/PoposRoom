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

package com.niyaj.feature.account.register.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.feature.account.register.RegisterScreenState
import com.niyaj.feature.account.register.utils.RegisterScreenPage
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun RegistrationScaffold(
    screenData: RegisterScreenState,
    isNextEnabled: Boolean,
    onClosePressed: () -> Unit,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    modifier: Modifier = Modifier,
    onDonePressed: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable (PaddingValues) -> Unit,
) = trace("RegistrationScaffold") {
    Scaffold(
        topBar = {
            RegisterTopAppBar(
                questionIndex = screenData.pageIndex,
                totalQuestionsCount = screenData.pageCount,
                onClosePressed = onClosePressed,
            )
        },
        content = content,
        bottomBar = {
            RegisterBottomBar(
                shouldShowPreviousButton = screenData.shouldShowPreviousButton,
                shouldShowDoneButton = screenData.shouldShowDoneButton,
                isNextButtonEnabled = isNextEnabled,
                onPreviousPressed = onPreviousPressed,
                onNextPressed = onNextPressed,
                onDonePressed = onDonePressed,
            )
        },
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    )
}

@DevicePreviews
@Composable
private fun RegistrationScaffoldPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        RegistrationScaffold(
            screenData = RegisterScreenState(
                pageIndex = 0,
                pageCount = 3,
                shouldShowPreviousButton = false,
                shouldShowDoneButton = false,
                screenPage = RegisterScreenPage.LOGIN_INFO,
            ),
            isNextEnabled = true,
            onClosePressed = {},
            onPreviousPressed = {},
            onNextPressed = {},
            modifier = modifier,
            onDonePressed = {},
            content = {
                Text("Register Screen Scaffold")
            },
        )
    }
}
