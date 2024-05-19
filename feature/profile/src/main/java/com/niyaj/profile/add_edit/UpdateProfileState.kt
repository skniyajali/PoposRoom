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

package com.niyaj.profile.add_edit

data class UpdateProfileState(
    val name: String = "",
    val nameError: String? = null,

    val tagline: String = "",
    val taglineError: String? = null,

    val email: String = "",
    val emailError: String? = null,

    val resLogo: String = "",
    val printLogo: String = "",

    val primaryPhone: String = "",
    val primaryPhoneError: String? = null,

    val secondaryPhone: String = "",
    val secondaryPhoneError: String? = null,

    val address: String = "",
    val addressError: String? = null,

    val paymentQrCode: String = "",
    val paymentQrCodeError: String? = null,

    val description: String = "",
    val descriptionError: String? = null
)
