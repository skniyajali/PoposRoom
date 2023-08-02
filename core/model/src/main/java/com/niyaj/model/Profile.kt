package com.niyaj.model

data class Profile(
    val restaurantId: Int,

    val name: String,

    val email: String,

    val primaryPhone: String,

    val secondaryPhone: String = "",

    val tagline: String,

    val description: String = "",

    val address: String,

    val logo: String,

    val printLogo: String = "",

    val paymentQrCode: String = "",

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)
