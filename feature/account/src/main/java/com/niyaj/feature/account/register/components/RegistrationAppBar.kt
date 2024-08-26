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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposOutlinedButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.feature.account.R
import com.niyaj.ui.utils.DevicePreviews

@Composable
private fun TopAppBarTitle(
    questionIndex: Int,
    totalQuestionsCount: Int,
    modifier: Modifier = Modifier,
) = trace("TopAppBarTitle") {
    Row(modifier = modifier) {
        Text(
            text = (questionIndex + 1).toString(),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = stringResource(R.string.question_count, totalQuestionsCount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTopAppBar(
    questionIndex: Int,
    totalQuestionsCount: Int,
    onClosePressed: () -> Unit,
    modifier: Modifier = Modifier,
) = trace("RegisterTopAppBar") {
    val animatedProgress by animateFloatAsState(
        targetValue = (questionIndex + 1) / totalQuestionsCount.toFloat(),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "",
    )

    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        TopAppBar(
            title = {
                TopAppBarTitle(
                    questionIndex = questionIndex,
                    totalQuestionsCount = totalQuestionsCount,
                )
            },
            actions = {
                IconButton(
                    onClick = onClosePressed,
                    modifier = Modifier.padding(4.dp),
                ) {
                    Icon(
                        imageVector = PoposIcons.Close,
                        contentDescription = "Close Icon",
                    )
                }
            },
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
fun RegisterBottomBar(
    shouldShowPreviousButton: Boolean,
    shouldShowDoneButton: Boolean,
    isNextButtonEnabled: Boolean,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit,
    modifier: Modifier = Modifier,
) = trace("RegisterBottomBar") {
    AnimatedVisibility(
        visible = isNextButtonEnabled || shouldShowPreviousButton,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { fullHeight ->
                fullHeight / 4
            },
        ),
        exit = fadeOut() + slideOutVertically(
            targetOffsetY = { fullHeight ->
                fullHeight / 4
            },
        ),
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
            ) {
                if (shouldShowPreviousButton) {
                    PoposOutlinedButton(
                        text = stringResource(id = R.string.previous),
                        onClick = onPreviousPressed,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        icon = PoposIcons.NavigateBefore,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                if (shouldShowDoneButton) {
                    PoposButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        text = stringResource(id = R.string.submit),
                        icon = PoposIcons.Done,
                        onClick = onDonePressed,
                        enabled = isNextButtonEnabled,
                    )
                } else {
                    PoposButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        text = stringResource(id = R.string.next),
                        icon = PoposIcons.NavigateNext,
                        onClick = onNextPressed,
                        enabled = isNextButtonEnabled,
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun TopAppBarTitlePreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        TopAppBarTitle(
            questionIndex = 0,
            totalQuestionsCount = 3,
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun RegisterTopAppBarPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        RegisterTopAppBar(
            questionIndex = 0,
            totalQuestionsCount = 3,
            onClosePressed = {},
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun RegisterBottomBarPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        RegisterBottomBar(
            shouldShowPreviousButton = true,
            shouldShowDoneButton = false,
            isNextButtonEnabled = true,
            onPreviousPressed = {},
            onNextPressed = {},
            onDonePressed = {},
            modifier = modifier,
        )
    }
}
