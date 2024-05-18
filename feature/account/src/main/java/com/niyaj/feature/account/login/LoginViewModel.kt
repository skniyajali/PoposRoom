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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.AccountRepository
import com.niyaj.model.Profile
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    var state by mutableStateOf(LoginState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val isLoggedIn = accountRepository
        .checkIsLoggedIn(resId = Profile.RESTAURANT_ID)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailOrPhoneChanged -> {
                state = state.copy(
                    emailOrPhone = event.emailOrPhone,
                )
            }

            is LoginEvent.PasswordChanged -> {
                state = state.copy(
                    password = event.password,
                )
            }

            is LoginEvent.OnClickLogin -> {
                viewModelScope.launch {
                    if (state.emailOrPhone.isEmpty()) {
                        state = state.copy(
                            emailError = "Email or Phone No Is Required",
                        )

                        return@launch
                    }

                    if (state.password.isEmpty()) {
                        state = state.copy(
                            passwordError = "Password Is Required",
                        )

                        return@launch
                    }

                    val result = accountRepository.login(
                        state.emailOrPhone,
                        state.password,
                    )

                    when (result) {
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Login Successfully"))
                            analyticsHelper.logUserLoggedIn(state.emailOrPhone, "success")
                        }

                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message.toString()))
                            analyticsHelper.logUserLoggedIn(state.emailOrPhone, "error")
                        }
                    }
                }
            }
        }
    }
}

internal fun AnalyticsHelper.logUserLoggedIn(emailOrPhone: String, status: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "user_logged_in",
            extras = listOf(
                AnalyticsEvent.Param("user_logged_in", "$emailOrPhone - $status"),
            ),
        ),
    )
}
