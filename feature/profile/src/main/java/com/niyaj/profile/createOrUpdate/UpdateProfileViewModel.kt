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

package com.niyaj.profile.createOrUpdate

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.saveImageToInternalStorage
import com.niyaj.data.repository.AccountRepository
import com.niyaj.data.repository.ProfileRepository
import com.niyaj.data.repository.QRCodeScanner
import com.niyaj.data.repository.validation.ProfileValidationRepository
import com.niyaj.model.Profile
import com.niyaj.model.RESTAURANT_LOGO_NAME
import com.niyaj.model.RESTAURANT_PRINT_LOGO_NAME
import com.niyaj.ui.utils.QRCodeEncoder
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val validation: ProfileValidationRepository,
    private val accountRepository: AccountRepository,
    private val scanner: QRCodeScanner,
    private val application: Application,
    @Dispatcher(PoposDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val resId = savedStateHandle.get<Int>("resId") ?: 0

    var updateState by mutableStateOf(UpdateProfileState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _scannedBitmap = MutableStateFlow<Bitmap?>(null)
    val scannedBitmap = _scannedBitmap.asStateFlow()

    val info = snapshotFlow { resId }.flatMapLatest {
        repository.getProfileInfo(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Profile.defaultProfileInfo,
    )

    init {
        setProfileInfo()
    }

    @Suppress("CyclomaticComplexMethod")
    fun onEvent(event: UpdateProfileEvent) {
        when (event) {
            is UpdateProfileEvent.NameChanged -> {
                updateState = updateState.copy(
                    name = event.name,
                )
            }

            is UpdateProfileEvent.TaglineChanged -> {
                updateState = updateState.copy(
                    tagline = event.tagline,
                )
            }

            is UpdateProfileEvent.PrimaryPhoneChanged -> {
                updateState = updateState.copy(
                    primaryPhone = event.primaryPhone,
                )
            }

            is UpdateProfileEvent.SecondaryPhoneChanged -> {
                updateState = updateState.copy(
                    secondaryPhone = event.secondaryPhone,
                )
            }

            is UpdateProfileEvent.EmailChanged -> {
                updateState = updateState.copy(
                    email = event.email,
                )
            }

            is UpdateProfileEvent.DescriptionChanged -> {
                updateState = updateState.copy(
                    description = event.description,
                )
            }

            is UpdateProfileEvent.AddressChanged -> {
                updateState = updateState.copy(
                    address = event.address,
                )
            }

            is UpdateProfileEvent.PaymentQrCodeChanged -> {
                updateState = updateState.copy(
                    paymentQrCode = event.paymentQrCode,
                )
            }

            is UpdateProfileEvent.UpdateUpdateProfile -> {
                updateProfile()
            }

            is UpdateProfileEvent.LogoChanged -> {
                viewModelScope.launch {
                    val fileName = "$RESTAURANT_LOGO_NAME-${System.currentTimeMillis()}.png"

                    val result = withContext(dispatcher) {
                        application.saveImageToInternalStorage(
                            event.uri,
                            fileName,
                            updateState.resLogo,
                            dispatcher,
                        )
                    }

                    when (result) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable to save logo into device"))
                        }

                        is Resource.Success -> {
                            when (repository.updateRestaurantLogo(resId = resId, fileName)) {
                                is Resource.Success -> {
                                    _eventFlow.emit(UiEvent.OnSuccess("Profile photo has been updated"))
                                    updateState = updateState.copy(
                                        resLogo = fileName,
                                    )
                                }

                                is Resource.Error -> {
                                    _eventFlow.emit(UiEvent.OnError("Unable to update profile photo"))
                                }
                            }
                        }
                    }
                }
            }

            is UpdateProfileEvent.PrintLogoChanged -> {
                viewModelScope.launch {
                    val fileName = "$RESTAURANT_PRINT_LOGO_NAME-${System.currentTimeMillis()}.png"

                    val result = withContext(dispatcher) {
                        application.saveImageToInternalStorage(
                            event.uri,
                            fileName,
                            updateState.printLogo,
                            dispatcher,
                        )
                    }

                    when (result) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable to save print image into device"))
                        }

                        is Resource.Success -> {
                            when (repository.updatePrintLogo(resId, fileName)) {
                                is Resource.Success -> {
                                    _eventFlow.emit(UiEvent.OnSuccess("Print photo has been updated"))
                                    updateState = updateState.copy(
                                        printLogo = fileName,
                                    )
                                }

                                is Resource.Error -> {
                                    _eventFlow.emit(UiEvent.OnError("Unable to update print photo"))
                                }
                            }
                        }
                    }
                }
            }

            is UpdateProfileEvent.StartScanning -> {
                startScanning()
            }
        }
    }

    private fun updateProfile() {
        val validatedName = validation.validateName(updateState.name)
        val validatedTagLine = validation.validateTagline(updateState.tagline)
        val validatedEmail = validation.validateEmail(updateState.email)
        val validatedPPhone = validation.validatePrimaryPhone(updateState.primaryPhone)
        val validatedSPhone = validation.validateSecondaryPhone(updateState.secondaryPhone)
        val validatedAddress = validation.validateAddress(updateState.address)
        val validatedQrCode = validation.validatePaymentQRCode(updateState.paymentQrCode)
        val validatedDesc = validation.validateDescription(updateState.description)

        val hasError = listOf(
            validatedName,
            validatedTagLine,
            validatedEmail,
            validatedPPhone,
            validatedSPhone,
            validatedAddress,
            validatedDesc,
            validatedQrCode,
        ).any { !it.successful }

        if (hasError) {
            updateState = updateState.copy(
                nameError = validatedName.errorMessage,
                taglineError = validatedTagLine.errorMessage,
                emailError = validatedEmail.errorMessage,
                primaryPhoneError = validatedPPhone.errorMessage,
                secondaryPhoneError = validatedSPhone.errorMessage,
                addressError = validatedAddress.errorMessage,
                paymentQrCodeError = validatedQrCode.errorMessage,
                descriptionError = validatedDesc.errorMessage,
            )

            return
        } else {
            viewModelScope.launch {
                val updatedProfile = Profile(
                    restaurantId = resId,
                    name = updateState.name,
                    tagline = updateState.tagline,
                    email = updateState.email,
                    primaryPhone = updateState.primaryPhone,
                    secondaryPhone = updateState.secondaryPhone,
                    description = updateState.description,
                    address = updateState.address,
                    paymentQrCode = updateState.paymentQrCode,
                    logo = updateState.resLogo,
                    printLogo = updateState.printLogo,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = System.currentTimeMillis().toString(),
                )

                val accResult = accountRepository.updateAccountInfo(
                    resId,
                    updateState.email,
                    updateState.primaryPhone,
                )

                when (accResult) {
                    is Resource.Success -> {
                        when (val result = repository.insertOrUpdateProfile(updatedProfile)) {
                            is Resource.Success -> {
                                _eventFlow.emit(UiEvent.OnSuccess("Restaurant Info Updated."))
                            }

                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError(result.message.toString()))
                            }
                        }
                    }

                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(accResult.message.toString()))
                    }
                }
            }
        }
    }

    private fun setProfileInfo() {
        viewModelScope.launch {
            repository.getProfileInfo(resId = resId).collectLatest { info ->
                if (info.paymentQrCode.isNotEmpty()) {
                    _scannedBitmap.value = QRCodeEncoder().encodeBitmap(info.paymentQrCode)
                }

                updateState = updateState.copy(
                    name = info.name,
                    tagline = info.tagline,
                    email = info.email,
                    primaryPhone = info.primaryPhone,
                    secondaryPhone = info.secondaryPhone,
                    description = info.description,
                    address = info.address,
                    paymentQrCode = info.paymentQrCode,
                    resLogo = info.logo,
                    printLogo = info.printLogo,
                )
            }
        }
    }

    private fun startScanning() {
        viewModelScope.launch {
            scanner.startScanning().collectLatest {
                if (!it.isNullOrEmpty()) {
                    _scannedBitmap.value = QRCodeEncoder().encodeBitmap(it)

                    updateState = updateState.copy(
                        paymentQrCode = it,
                    )
                }
            }
        }
    }
}
