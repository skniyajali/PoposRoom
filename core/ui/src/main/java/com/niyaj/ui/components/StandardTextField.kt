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

package com.niyaj.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.niyaj.common.utils.Constants.CLEAR_ICON
import com.niyaj.common.utils.Constants.PASSWORD_HIDDEN_ICON
import com.niyaj.common.utils.Constants.PASSWORD_SHOWN_ICON
import com.niyaj.common.utils.Constants.TEXT_FIELD_LEADING_ICON
import com.niyaj.common.utils.Constants.TEXT_FIELD_TRAILING_ICON
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.ui.utils.DevicePreviews

@Composable
@Suppress("LongParameterList")
fun StandardTextField(
    label: String,
    leadingIcon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    trailingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isPasswordToggleDisplayed: Boolean = keyboardType == KeyboardType.Password,
    isPasswordVisible: Boolean = false,
    errorTextTag: String = label.plus("Error"),
    onPasswordToggleClick: (Boolean) -> Unit = {},
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    onTrailingIconClick: () -> Unit = {},
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .testTag(label)
            .fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        leadingIcon = {
            Icon(imageVector = leadingIcon, contentDescription = TEXT_FIELD_LEADING_ICON)
        },
        trailingIcon = {
            if (isPasswordToggleDisplayed) {
                IconButton(
                    onClick = {
                        onPasswordToggleClick(!isPasswordVisible)
                    },
                    modifier = Modifier,
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            PoposIcons.VisibilityOff
                        } else {
                            PoposIcons.Visibility
                        },
                        contentDescription = if (isPasswordVisible) {
                            PASSWORD_HIDDEN_ICON
                        } else {
                            PASSWORD_SHOWN_ICON
                        },
                    )
                }
            } else {
                trailingIcon?.let {
                    IconButton(
                        onClick = onTrailingIconClick,
                    ) {
                        Icon(imageVector = it, contentDescription = TEXT_FIELD_TRAILING_ICON)
                    }
                }
            }
        },
        prefix = prefix,
        suffix = suffix,
        supportingText = errorText?.let {
            {
                Text(
                    modifier = Modifier.testTag(errorTextTag),
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        isError = isError,
        singleLine = singleLine,
        visualTransformation = if (!isPasswordVisible && isPasswordToggleDisplayed) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = keyboardType,
        ),
        maxLines = maxLines,
    )
}

@Composable
@Suppress("LongParameterList")
fun StandardOutlinedTextField(
    label: String,
    leadingIcon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    showClearIcon: Boolean = false,
    clearIcon: ImageVector = PoposIcons.Close,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    errorTextTag: String = label.plus("Error"),
    message: String? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    isPasswordToggleDisplayed: Boolean = keyboardType == KeyboardType.Password,
    isPasswordVisible: Boolean = false,
    onPasswordToggleClick: (Boolean) -> Unit = {},
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    onClickClearIcon: () -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .testTag(label)
            .fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        leadingIcon = {
            Icon(imageVector = leadingIcon, contentDescription = TEXT_FIELD_LEADING_ICON)
        },
        trailingIcon = @Composable {
            if (isPasswordToggleDisplayed) {
                PasswordToggleIcon(
                    isPasswordVisible = isPasswordVisible,
                    onPasswordToggleClick = onPasswordToggleClick,
                )
            } else if (showClearIcon && isFocused) {
                ClearIconButton(
                    showClearIcon = true,
                    clearIcon = clearIcon,
                    onClickClearIcon = onClickClearIcon,
                )
            } else {
                trailingIcon?.invoke()
            }
        },
        prefix = prefix,
        suffix = suffix,
        supportingText = {
            if (errorText != null) {
                Text(
                    modifier = Modifier.testTag(errorTextTag),
                    text = errorText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            } else if (message != null) {
                Text(
                    modifier = Modifier.testTag(errorTextTag),
                    text = message,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        isError = isError,
        singleLine = singleLine,
        visualTransformation = if (!isPasswordVisible && isPasswordToggleDisplayed) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = keyboardType,
        ),
        maxLines = maxLines,
        interactionSource = interactionSource,
    )
}

@Composable
private fun PasswordToggleIcon(
    isPasswordVisible: Boolean,
    onPasswordToggleClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = {
            onPasswordToggleClick(!isPasswordVisible)
        },
        modifier = modifier,
    ) {
        Icon(
            imageVector = if (isPasswordVisible) {
                PoposIcons.VisibilityOff
            } else {
                PoposIcons.Visibility
            },
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = if (isPasswordVisible) {
                "VisibilityOff"
            } else {
                "VisibilityOn"
            },
        )
    }
}

@Composable
private fun ClearIconButton(
    showClearIcon: Boolean,
    clearIcon: ImageVector,
    onClickClearIcon: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = showClearIcon,
        modifier = modifier,
    ) {
        IconButton(
            onClick = onClickClearIcon,
            modifier = Modifier.semantics {
                contentDescription = CLEAR_ICON
            },
        ) {
            Icon(
                imageVector = clearIcon,
                contentDescription = "trailingIcon",
            )
        }
    }
}

@DevicePreviews
@Composable
private fun StandardTextFieldPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        StandardTextField(
            label = "Standard TextField",
            leadingIcon = PoposIcons.Add,
            value = "Standard TextField",
            onValueChange = {},
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun StandardOutlinedTextFieldPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        StandardOutlinedTextField(
            label = "Standard Outlined TextField",
            leadingIcon = PoposIcons.Add,
            value = "Standard Outlined TextField",
            onValueChange = {},
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun PasswordToggleIconPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        PasswordToggleIcon(
            isPasswordVisible = false,
            onPasswordToggleClick = {},
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun ClearIconButtonPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ClearIconButton(
            showClearIcon = true,
            clearIcon = PoposIcons.Close,
            onClickClearIcon = {},
            modifier = modifier,
        )
    }
}
