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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.AccountRepository
import com.niyaj.data.repository.validation.ProfileValidationRepository
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val analyticsHelper: AnalyticsHelper,
    validationRepository: ProfileValidationRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val resId = savedStateHandle.get<Int>("resId") ?: 0

    var state by mutableStateOf(ChangePasswordState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val passwordError = snapshotFlow { state.newPassword }.mapLatest {
        validationRepository.validatePassword(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val confirmPasswordError = snapshotFlow { state.confirmPassword }.mapLatest {
        if (state.newPassword != it) {
            "Current password does not match"
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun onEvent(event: ChangePasswordEvent) {
        when (event) {
            is ChangePasswordEvent.CurrentPasswordChanged -> {
                state = state.copy(
                    currentPassword = event.currentPassword,
                )
            }

            is ChangePasswordEvent.NewPasswordChanged -> {
                state = state.copy(
                    newPassword = event.newPassword,
                )
            }

            is ChangePasswordEvent.ConfirmPasswordChanged -> {
                state = state.copy(
                    confirmPassword = event.confirmPassword,
                )
            }

            is ChangePasswordEvent.ChangePassword -> {
                changePassword()
            }
        }
    }

    private fun changePassword() {
        viewModelScope.launch {
            val hasError = listOf(
                passwordError,
                confirmPasswordError,
            ).any { it.value != null }

            if (!hasError) {
                val result = accountRepository.changePassword(
                    resId = resId,
                    currentPassword = state.currentPassword,
                    newPassword = state.newPassword,
                )

                when (result) {
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Password changed successfully"))
                        analyticsHelper.logUserPasswordChanged(resId, "success")
                    }

                    is Resource.Error -> {
                        _error.value = result.message
                        analyticsHelper.logUserPasswordChanged(resId, "error")
                    }
                }
            }
        }
    }
}

internal fun AnalyticsHelper.logUserPasswordChanged(resId: Int, status: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "user_password_changed",
            extras = listOf(
                AnalyticsEvent.Param("user_password_changed", "$resId - $status"),
            ),
        ),
    )
}
