package com.niyaj.profile.add_edit

sealed interface AddEditProfileEvent {

    data class NameChanged(val name: String): AddEditProfileEvent

    data class EmailChanged(val email: String): AddEditProfileEvent

    data class PrimaryPhoneChanged(val primaryPhone: String): AddEditProfileEvent

    data class SecondaryPhoneChanged(val secondaryPhone: String): AddEditProfileEvent

    data class TaglineChanged(val tagline: String): AddEditProfileEvent

    data class DescriptionChanged(val description: String): AddEditProfileEvent

    data class AddressChanged(val address: String): AddEditProfileEvent

    data class LogoChanged(val logo: String): AddEditProfileEvent

    data class PrintLogoChanged(val printLogo: String): AddEditProfileEvent

    data class PaymentQrCodeChanged(val paymentQrCode: String): AddEditProfileEvent

    data class CreateOrUpdateProfileInfo(val restaurantId: Int = 0) : AddEditProfileEvent

}