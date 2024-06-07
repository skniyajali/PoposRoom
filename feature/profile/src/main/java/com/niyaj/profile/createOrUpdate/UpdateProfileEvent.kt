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

import android.net.Uri

sealed class UpdateProfileEvent {

    data class NameChanged(val name: String) : UpdateProfileEvent()

    data class TaglineChanged(val tagline: String) : UpdateProfileEvent()

    data class DescriptionChanged(val description: String) : UpdateProfileEvent()

    data class EmailChanged(val email: String) : UpdateProfileEvent()

    data class PrimaryPhoneChanged(val primaryPhone: String) : UpdateProfileEvent()

    data class SecondaryPhoneChanged(val secondaryPhone: String) : UpdateProfileEvent()

    data class AddressChanged(val address: String) : UpdateProfileEvent()

    data class PaymentQrCodeChanged(val paymentQrCode: String) : UpdateProfileEvent()

    data object StartScanning : UpdateProfileEvent()

    data class LogoChanged(val uri: Uri) : UpdateProfileEvent()

    data class PrintLogoChanged(val uri: Uri) : UpdateProfileEvent()

    data object UpdateUpdateProfile : UpdateProfileEvent()
}
