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

package com.niyaj.model

const val RESTAURANT_LOGO_NAME = "reslogo"
const val RESTAURANT_PRINT_LOGO_NAME = "printlogo"

data class Profile(
    val restaurantId: Int = 0,

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
            restaurantId = 0,
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
            updatedAt = null,
        )
    }
}
