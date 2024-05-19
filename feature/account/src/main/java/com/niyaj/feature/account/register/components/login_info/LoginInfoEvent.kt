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

package com.niyaj.feature.account.register.components.login_info

import android.net.Uri

sealed interface LoginInfoEvent {

    data class NameChanged(val name: String) : LoginInfoEvent

    data class EmailChanged(val email: String) : LoginInfoEvent

    data class SecondaryPhoneChanged(val secondaryPhone: String) : LoginInfoEvent

    data class PhoneChanged(val phone: String) : LoginInfoEvent

    data class PasswordChanged(val password: String) : LoginInfoEvent

    data class LogoChanged(val logo: Uri) : LoginInfoEvent
}
