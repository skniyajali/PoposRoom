package com.niyaj.profile.add_edit

data class AddEditProfileState(
    val name: String = "",
    val nameError: String? = null,

    val email: String = "",
    val emailError: String? = null,

    val primaryPhone: String = "",
    val primaryPhoneError: String? = null,

    val secondaryPhone: String = "",
    val secondaryPhoneError: String? = null,

    val tagline: String = "",
    val taglineError: String? = null,

    val description: String = "",
    val descriptionError: String? = null,

    val address: String = "",
    val addressError: String? = null,

    val logo: String = "",
    val logoError: String? = null,

    val printLogo: String = "",

    val paymentQrCode: String = ""
)
