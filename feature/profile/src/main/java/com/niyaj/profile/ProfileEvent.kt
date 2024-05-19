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

package com.niyaj.profile

import android.net.Uri

sealed class ProfileEvent {

    data class NameChanged(val name: String) : ProfileEvent()

    data class TaglineChanged(val tagline: String) : ProfileEvent()

    data class DescriptionChanged(val description: String) : ProfileEvent()

    data class EmailChanged(val email: String) : ProfileEvent()

    data class PrimaryPhoneChanged(val primaryPhone: String) : ProfileEvent()

    data class SecondaryPhoneChanged(val secondaryPhone: String) : ProfileEvent()

    data class AddressChanged(val address: String) : ProfileEvent()

    data class PaymentQrCodeChanged(val paymentQrCode: String) : ProfileEvent()

    data object StartScanning : ProfileEvent()

    data class LogoChanged(val uri: Uri) : ProfileEvent()

    data class PrintLogoChanged(val uri: Uri) : ProfileEvent()

    data object SetProfileInfo: ProfileEvent()

    data object UpdateProfile : ProfileEvent()

    data object LogoutProfile: ProfileEvent()
}
