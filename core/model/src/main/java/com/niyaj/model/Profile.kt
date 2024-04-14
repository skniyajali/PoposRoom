package com.niyaj.model

import androidx.compose.runtime.Stable

const val RESTAURANT_LOGO_NAME = "reslogo"
const val RESTAURANT_PRINT_LOGO_NAME = "printlogo"

@Stable
data class Profile(
    val restaurantId: Int,

    val name: String,

    val email: String,

    val primaryPhone: String,

    val secondaryPhone: String,

    val tagline: String,

    val description: String,

    val address: String,

    val logo: String,

    val printLogo: String,

    val paymentQrCode: String,

    val createdAt: String,

    val updatedAt: String? = null,
) {
    companion object {
        const val RESTAURANT_ID = 2222
        private const val RESTAURANT_NAME = "Popos Highlight"
        private const val RESTAURANT_TAGLINE = "- Pure And Tasty -"
        private const val RESTAURANT_DESCRIPTION = "Multi Cuisine Veg & Non-Veg Restaurant"
        private const val RESTAURANT_EMAIL = "poposhighlight@gmail.com"
        private const val RESTAURANT_SECONDARY_PHONE: String = "9597185001"
        private const val RESTAURANT_PRIMARY_PHONE: String = "9500825077"
        private const val RESTAURANT_ADDRESS =
            "Chinna Seeragapadi, Salem, TamilNadu, India 636308, Opp. of VIMS Hospital"
        private const val RESTAURANT_PAYMENT_QR_DATA =
            "upi://pay?pa=paytmqr281005050101zry6uqipmngr@paytm&pn=Paytm%20Merchant&paytmqr=281005050101ZRY6UQIPMNGR"

        val defaultProfileInfo = Profile(
            restaurantId = RESTAURANT_ID,
            name = RESTAURANT_NAME,
            email = RESTAURANT_EMAIL,
            primaryPhone = RESTAURANT_PRIMARY_PHONE,
            secondaryPhone = RESTAURANT_SECONDARY_PHONE,
            tagline = RESTAURANT_TAGLINE,
            description = RESTAURANT_DESCRIPTION,
            address = RESTAURANT_ADDRESS,
            logo = "",
            printLogo = "",
            paymentQrCode = RESTAURANT_PAYMENT_QR_DATA,
            createdAt = System.currentTimeMillis().toString(),
            updatedAt = null

        )
    }
}