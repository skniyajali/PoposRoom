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

package com.niyaj.feature.account.register

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.common.utils.saveImageToInternalStorage
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.AccountRepository
import com.niyaj.data.repository.ProfileRepository
import com.niyaj.data.repository.QRCodeScanner
import com.niyaj.data.repository.UserDataRepository
import com.niyaj.data.repository.validation.ProfileValidationRepository
import com.niyaj.feature.account.register.components.basicInfo.BasicInfoEvent
import com.niyaj.feature.account.register.components.basicInfo.BasicInfoState
import com.niyaj.feature.account.register.components.loginInfo.LoginInfoEvent
import com.niyaj.feature.account.register.components.loginInfo.LoginInfoState
import com.niyaj.feature.account.register.utils.RegisterScreenPage
import com.niyaj.model.Account
import com.niyaj.model.Profile
import com.niyaj.model.RESTAURANT_LOGO_NAME
import com.niyaj.model.RESTAURANT_PRINT_LOGO_NAME
import com.niyaj.ui.utils.QRCodeEncoder
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val restaurantInfoRepository: ProfileRepository,
    private val validation: ProfileValidationRepository,
    private val userDataRepository: UserDataRepository,
    private val scanner: QRCodeScanner,
    private val application: Application,
    @Dispatcher(PoposDispatchers.IO)
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val _scannedBitmap = MutableStateFlow<Bitmap?>(null)
    val scannedBitmap = _scannedBitmap.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val pageOrder: List<RegisterScreenPage> = listOf(
        RegisterScreenPage.LOGIN_INFO,
        RegisterScreenPage.BASIC_INFO,
    )

    private var pageIndex = 0

    // Screen states

    private val _loginInfoState = mutableStateOf(LoginInfoState())
    val loginInfoState: LoginInfoState
        get() = _loginInfoState.value

    private val _basicInfoState = mutableStateOf(BasicInfoState())
    val basicInfoState: BasicInfoState
        get() = _basicInfoState.value

    private val _registerScreenState = mutableStateOf(createRegisterScreenData())
    val registerScreenState: RegisterScreenState
        get() = _registerScreenState.value

    private val _isNextEnabled = mutableStateOf(false)
    val isNextEnabled: Boolean
        get() = getIsNextEnabled()

    val emailError = snapshotFlow { _loginInfoState.value.email }.mapLatest {
        validation.validateEmail(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val phoneError = snapshotFlow { _loginInfoState.value.phone }.mapLatest {
        validation.validatePrimaryPhone(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val passwordError = snapshotFlow { _loginInfoState.value.password }.mapLatest {
        validation.validatePassword(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val nameError = snapshotFlow { _loginInfoState.value.name }.mapLatest {
        validation.validateName(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val secondaryPhoneError = snapshotFlow { _loginInfoState.value.secondaryPhone }.mapLatest {
        validation.validateSecondaryPhone(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val taglineError = snapshotFlow { _basicInfoState.value.tagline }.mapLatest {
        validation.validateTagline(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val descError = snapshotFlow { _basicInfoState.value.description }.mapLatest {
        validation.validateDescription(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val addressError = snapshotFlow { _basicInfoState.value.address }.mapLatest {
        validation.validateAddress(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val paymentQrCodeError = snapshotFlow { _basicInfoState.value.paymentQrCode }.mapLatest {
        validation.validatePaymentQRCode(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    fun onBackPressed(): Boolean {
        if (pageIndex == 0) {
            return false
        }
        changePage(pageIndex - 1)
        return true
    }

    fun onPreviousPressed() {
        if (pageIndex == 0) {
            throw IllegalStateException("onPreviousPressed when on page index is 0")
        }
        changePage(pageIndex - 1)
    }

    fun onNextPressed() {
        changePage(pageIndex + 1)
    }

    fun onDonePressed() {
        viewModelScope.launch {
            val resInfo = Profile(
                name = _loginInfoState.value.name,
                email = _loginInfoState.value.email.lowercase(),
                primaryPhone = _loginInfoState.value.phone,
                secondaryPhone = _loginInfoState.value.secondaryPhone,
                tagline = _basicInfoState.value.tagline,
                address = _basicInfoState.value.address,
                description = _basicInfoState.value.description,
                logo = _loginInfoState.value.logo,
                printLogo = _basicInfoState.value.printLogo,
                paymentQrCode = _basicInfoState.value.paymentQrCode,
                createdAt = System.currentTimeMillis().toString(),
            )
            when (val profileRes = restaurantInfoRepository.insertOrUpdateProfile(resInfo)) {
                is Resource.Success -> {
                    val result = accountRepository.register(
                        Account(
                            restaurantId = profileRes.data ?: 0,
                            email = _loginInfoState.value.email,
                            phone = _loginInfoState.value.phone,
                            password = _loginInfoState.value.password,
                            isLoggedIn = true,
                            createdAt = System.currentTimeMillis(),
                        ),
                    )

                    when (result) {
                        is Resource.Success -> {
                            userDataRepository.setUserLoggedIn(profileRes.data ?: 0)
                            _eventFlow.emit(UiEvent.OnSuccess("Your restaurant profile has been created"))
                            analyticsHelper.logUserProfileCreated(resInfo)
                        }

                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable to create restaurant profile"))
                        }
                    }
                }

                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to create restaurant profile"))
                }
            }
        }
    }

    fun onLoginInfoEvent(event: LoginInfoEvent) {
        when (event) {
            is LoginInfoEvent.NameChanged -> {
                _loginInfoState.value = _loginInfoState.value.copy(
                    name = event.name.capitalizeWords,
                )
            }

            is LoginInfoEvent.SecondaryPhoneChanged -> {
                _loginInfoState.value = _loginInfoState.value.copy(
                    secondaryPhone = event.secondaryPhone,
                )
            }

            is LoginInfoEvent.EmailChanged -> {
                _loginInfoState.value = _loginInfoState.value.copy(
                    email = event.email,
                )
            }

            is LoginInfoEvent.PasswordChanged -> {
                _loginInfoState.value = _loginInfoState.value.copy(
                    password = event.password,
                )
            }

            is LoginInfoEvent.PhoneChanged -> {
                _loginInfoState.value = _loginInfoState.value.copy(
                    phone = event.phone,
                )
            }

            is LoginInfoEvent.LogoChanged -> {
                viewModelScope.launch {
                    val fileName = "$RESTAURANT_LOGO_NAME-${System.currentTimeMillis()}.png"

                    val result = withContext(dispatcher) {
                        application.saveImageToInternalStorage(
                            event.logo,
                            fileName,
                            _loginInfoState.value.logo,
                            dispatcher,
                        )
                    }

                    when (result) {
                        is Resource.Success -> {
                            _loginInfoState.value = _loginInfoState.value.copy(
                                logo = fileName,
                            )
                        }

                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable to save image"))
                        }
                    }
                }
            }
        }
    }

    fun onBasicInfoEvent(event: BasicInfoEvent) {
        when (event) {
            is BasicInfoEvent.TaglineChanged -> {
                _basicInfoState.value = _basicInfoState.value.copy(
                    tagline = event.tagline.capitalizeWords,
                )
            }

            is BasicInfoEvent.DescriptionChanged -> {
                _basicInfoState.value = _basicInfoState.value.copy(
                    description = event.description.capitalizeWords,
                )
            }

            is BasicInfoEvent.AddressChanged -> {
                _basicInfoState.value = _basicInfoState.value.copy(
                    address = event.address.capitalizeWords,
                )
            }

            is BasicInfoEvent.PaymentQRChanged -> {
                _basicInfoState.value = _basicInfoState.value.copy(
                    paymentQrCode = event.paymentQrCode,
                )
            }

            is BasicInfoEvent.StartScanning -> {
                startScanning()
            }

            is BasicInfoEvent.PrintLogoChanged -> {
                viewModelScope.launch {
                    val fileName = "$RESTAURANT_PRINT_LOGO_NAME-${System.currentTimeMillis()}.png"

                    val result = withContext(dispatcher) {
                        application.saveImageToInternalStorage(
                            event.printLogo,
                            fileName,
                            _basicInfoState.value.printLogo,
                            dispatcher,
                        )
                    }

                    when (result) {
                        is Resource.Success -> {
                            _basicInfoState.value = _basicInfoState.value.copy(
                                printLogo = fileName,
                            )
                        }

                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable to save image"))
                        }
                    }
                }
            }
        }
    }

    private fun changePage(pageIndex: Int) {
        this.pageIndex = pageIndex
        _isNextEnabled.value = getIsNextEnabled()
        _registerScreenState.value = createRegisterScreenData()
    }

    private fun getIsNextEnabled(): Boolean {
        return when (pageOrder[pageIndex]) {
            RegisterScreenPage.LOGIN_INFO -> listOf(
                nameError.value,
                secondaryPhoneError.value,
                emailError.value,
                passwordError.value,
                phoneError.value,
            ).all { it == null }

            RegisterScreenPage.BASIC_INFO -> listOf(
                taglineError.value,
                addressError.value,
                paymentQrCodeError.value,
            ).all { it == null }
        }
    }

    private fun createRegisterScreenData(): RegisterScreenState {
        return RegisterScreenState(
            pageIndex = pageIndex,
            pageCount = pageOrder.size,
            shouldShowPreviousButton = pageIndex > 0,
            shouldShowDoneButton = pageIndex == pageOrder.size - 1,
            screenPage = pageOrder[pageIndex],
        )
    }

    private fun startScanning() {
        viewModelScope.launch {
            scanner.startScanning().collectLatest {
                if (!it.isNullOrEmpty()) {
                    _scannedBitmap.value = QRCodeEncoder().encodeBitmap(it)

                    _basicInfoState.value = _basicInfoState.value.copy(
                        paymentQrCode = it,
                    )
                }
            }
        }
    }
}

internal fun AnalyticsHelper.logUserProfileCreated(profile: Profile) {
    logEvent(
        event = AnalyticsEvent(
            type = "user_profile_created",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("user_profile_created", profile.toString()),
            ),
        ),
    )
}
