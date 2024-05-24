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

package com.niyaj.data.repository

import com.niyaj.model.DarkThemeConfig
import com.niyaj.model.ThemeBrand
import com.niyaj.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>

    /**
     * Stream of the currently logged in user ID.
     */
    val loggedInUserId: Flow<Int>

    /**
     * Stream of the user has been logged in
     */
    val isUserLoggedIn: Flow<Boolean>

    /**
     * Sets the desired theme brand.
     */
    suspend fun setThemeBrand(themeBrand: ThemeBrand)

    /**
     * Sets the desired dark theme config.
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    /**
     * Sets the preferred dynamic color config.
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)

    /**
     * Sets whether the user has completed the onboarding process.
     */
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)

    /**
     * Sets whether the user is logged in.
     */
    suspend fun setUserLoggedIn(userId: Int)

    /**
     * Sets whether the user is logged out.
     */
    suspend fun setUserLoggedOut()

    /**
     * Sets whether the user has opted to receive order SMS.
     */
    suspend fun setSendOrderSms(sendOrderSms: Boolean)

    /**
     * Sets whether the user has opted to use delivery partner QR code.
     */
    suspend fun setUseDeliveryPartnerQrCode(useDeliveryPartnerQrCode: Boolean)

    /**
     * Sets the selected order ID.
     */
    suspend fun setSelectedOrderId(selectedOrderId: Int)
}