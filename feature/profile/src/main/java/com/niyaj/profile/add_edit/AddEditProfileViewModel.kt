package com.niyaj.profile.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.Constants.RESTAURANT_ID
import com.niyaj.data.repository.ProfileRepository
import com.niyaj.data.repository.validation.ProfileValidationRepository
import com.niyaj.model.Profile
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val validation: ProfileValidationRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    var state by mutableStateOf(AddEditProfileState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("restaurantId")?.let {
            getProfileInfo(it)
        }
    }

    fun onEvent(event: AddEditProfileEvent) {
        when(event) {
            is AddEditProfileEvent.NameChanged -> {
                state = state.copy(
                    name = event.name,
                )
            }

            is AddEditProfileEvent.EmailChanged -> {
                state = state.copy(
                    email = event.email,
                )
            }

            is AddEditProfileEvent.PrimaryPhoneChanged -> {
                state = state.copy(
                    primaryPhone = event.primaryPhone,
                )
            }

            is AddEditProfileEvent.SecondaryPhoneChanged -> {
                state = state.copy(
                    secondaryPhone = event.secondaryPhone,
                )
            }

            is AddEditProfileEvent.TaglineChanged -> {
                state = state.copy(
                    tagline = event.tagline,
                )
            }

            is AddEditProfileEvent.DescriptionChanged -> {
                state = state.copy(
                    description = event.description,
                )
            }

            is AddEditProfileEvent.AddressChanged -> {
                state = state.copy(
                    address = event.address,
                )
            }

            is AddEditProfileEvent.LogoChanged -> {
                state = state.copy(
                    logo = event.logo,
                )
            }

            is AddEditProfileEvent.PrintLogoChanged -> {
                state = state.copy(
                    printLogo = event.printLogo,
                )
            }

            is AddEditProfileEvent.PaymentQrCodeChanged -> {
                state = state.copy(
                    paymentQrCode = event.paymentQrCode,
                )
            }

            is AddEditProfileEvent.CreateOrUpdateProfileInfo -> {
                createOrUpdateProfileInfo(event.restaurantId)
            }
        }
    }

    private fun createOrUpdateProfileInfo(restaurantId: Int) {
        viewModelScope.launch {
            val validateName = validation.validateName(state.name)
            val validateEmail = validation.validateEmail(state.email)
            val validateDescription = validation.validateDescription(state.description)
            val validateTagline = validation.validateTagline(state.tagline)
            val validatePrimaryPhone = validation.validatePrimaryPhone(state.primaryPhone)
            val validateSecondaryPhone = validation.validateSecondaryPhone(state.secondaryPhone)
            val validateAddress = validation.validateAddress(state.address)
            val validateLogo = validation.validateLogo(state.logo)

            val hasError = listOf(
                validateName,
                validateEmail,
                validateDescription,
                validateAddress,
                validateTagline,
                validatePrimaryPhone,
                validateSecondaryPhone,
//                validateLogo,
            ).any { !it.successful }

            if (hasError) {
                state = state.copy(
                    nameError = validateName.errorMessage,
                    emailError = validateEmail.errorMessage,
                    descriptionError = validateDescription.errorMessage,
                    taglineError = validateTagline.errorMessage,
                    primaryPhoneError = validatePrimaryPhone.errorMessage,
                    secondaryPhoneError = validateSecondaryPhone.errorMessage,
                    addressError = validateAddress.errorMessage,
                    logoError = validateLogo.errorMessage
                )

                return@launch
            }else {
                val newProfile = Profile(
                    restaurantId = RESTAURANT_ID,
                    name = state.name,
                    email = state.email,
                    primaryPhone = state.primaryPhone,
                    secondaryPhone = state.secondaryPhone,
                    tagline = state.tagline,
                    description = state.description,
                    address = state.description,
                    logo = state.description,
                    printLogo = state.description,
                    paymentQrCode = state.paymentQrCode,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = if (restaurantId != 0) System.currentTimeMillis().toString() else null,
                )

                when(profileRepository.insertOrUpdateProfile(newProfile)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable to create or update profile information"))
                    }
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Profile information created or updated successfully"))
                    }
                }
            }
        }
    }

    private fun getProfileInfo(restaurantId: Int) {
        viewModelScope.launch {
            profileRepository.getProfileInfo().collectLatest { it ->
                it?.let { profile ->
                    state = state.copy(
                        name = profile.name,
                        email = profile.email,
                        primaryPhone = profile.primaryPhone,
                        secondaryPhone = profile.secondaryPhone,
                        tagline = profile.tagline,
                        description = profile.description,
                        address = profile.address,
                        logo = profile.logo,
                        printLogo = profile.printLogo,
                        paymentQrCode = profile.paymentQrCode
                    )
                }

            }
        }
    }
}