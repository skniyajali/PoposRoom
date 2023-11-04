package com.niyaj.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.niyaj.common.utils.Constants.PASSWORD_HIDDEN_ICON
import com.niyaj.common.utils.Constants.PASSWORD_SHOWN_ICON
import com.niyaj.common.utils.Constants.TEXT_FIELD_LEADING_ICON
import com.niyaj.common.utils.Constants.TEXT_FIELD_TRAILING_ICON

@Composable
fun StandardTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    leadingIcon: ImageVector,
    trailingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isPasswordToggleDisplayed: Boolean = keyboardType == KeyboardType.Password,
    isPasswordVisible: Boolean = false,
    errorTextTag: String = label.plus("Error"),
    onPasswordToggleClick: (Boolean) -> Unit = {},
    prefix: @Composable() (() -> Unit)? = null,
    suffix: @Composable() (() -> Unit)? = null,
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
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            Icons.Filled.VisibilityOff
                        } else {
                            Icons.Filled.Visibility
                        },
                        contentDescription = if (isPasswordVisible) {
                            PASSWORD_HIDDEN_ICON
                        } else {
                            PASSWORD_SHOWN_ICON
                        }
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
        supportingText = {
            errorText?.let {
                Text(
                    modifier = Modifier.testTag(errorTextTag),
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
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
fun StandardOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    isError: Boolean = false,
    errorText: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    leadingIcon: ImageVector,
    trailingIcon: @Composable() (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    errorTextTag: String = label.plus("Error"),
    message: String? = null,
    isPasswordToggleDisplayed: Boolean = keyboardType == KeyboardType.Password,
    isPasswordVisible: Boolean = false,
    onPasswordToggleClick: (Boolean) -> Unit = {},
    prefix: @Composable() (() -> Unit)? = null,
    suffix: @Composable() (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
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
        trailingIcon = if (isPasswordToggleDisplayed) {
            val icon: @Composable () -> Unit = {
                IconButton(
                    onClick = {
                        onPasswordToggleClick(!isPasswordVisible)
                    },
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            Icons.Filled.VisibilityOff
                        } else {
                            Icons.Filled.Visibility
                        },
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = if (isPasswordVisible) {
                            PASSWORD_HIDDEN_ICON
                        } else {
                            PASSWORD_SHOWN_ICON
                        }
                    )
                }
            }
            icon
        } else trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = {
            if (errorText != null) {
                Text(
                    modifier = Modifier.testTag(errorTextTag),
                    text = errorText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (message != null) {
                Text(
                    modifier = Modifier.testTag(errorTextTag),
                    text = message,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
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