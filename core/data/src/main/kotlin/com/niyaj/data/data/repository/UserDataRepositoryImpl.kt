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

package com.niyaj.data.data.repository

import com.niyaj.core.datastore.PoposPreferencesDataSource
import com.niyaj.data.repository.UserDataRepository
import com.niyaj.data.utils.logDarkThemeConfigChanged
import com.niyaj.data.utils.logDeliveryPartnerQrCodePreferenceChanged
import com.niyaj.data.utils.logDynamicColorPreferenceChanged
import com.niyaj.data.utils.logOnboardingStateChanged
import com.niyaj.data.utils.logOrderSmsPreferenceChanged
import com.niyaj.data.utils.logSelectedOrderIdChanged
import com.niyaj.data.utils.logThemeChanged
import com.niyaj.data.utils.logUserLoggedIn
import com.niyaj.data.utils.logUserLoggedOut
import com.niyaj.model.DarkThemeConfig
import com.niyaj.model.ThemeBrand
import com.niyaj.model.UserData
import com.samples.apps.core.analytics.AnalyticsHelper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val dataStore: PoposPreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper,
) : UserDataRepository {

    override val userData: Flow<UserData>
        get() = dataStore.userData

    override val loggedInUserId: Flow<Int>
        get() = dataStore.loggedInUser

    override val isUserLoggedIn: Flow<Boolean>
        get() = dataStore.isUserLoggedIn

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        dataStore.setThemeBrand(themeBrand)
        analyticsHelper.logThemeChanged(themeBrand.name)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        dataStore.setDarkThemeConfig(darkThemeConfig)
        analyticsHelper.logDarkThemeConfigChanged(darkThemeConfig.name)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        dataStore.setDynamicColorPreference(useDynamicColor)
        analyticsHelper.logDynamicColorPreferenceChanged(useDynamicColor)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        dataStore.setShouldHideOnboarding(shouldHideOnboarding)
        analyticsHelper.logOnboardingStateChanged(shouldHideOnboarding)
    }

    override suspend fun setUserLoggedIn(userId: Int) {
        dataStore.markUserAsLoggedIn(userId)
        analyticsHelper.logUserLoggedIn(userId)
    }

    override suspend fun setUserLoggedOut() {
        dataStore.markUserAsLoggedOut()
        analyticsHelper.logUserLoggedOut()
    }

    override suspend fun setSendOrderSms(sendOrderSms: Boolean) {
        dataStore.setSendOrderSms(sendOrderSms)
        analyticsHelper.logOrderSmsPreferenceChanged(sendOrderSms)
    }

    override suspend fun setUseDeliveryPartnerQrCode(useDeliveryPartnerQrCode: Boolean) {
        dataStore.setUseDeliveryPartnerQrCode(useDeliveryPartnerQrCode)
        analyticsHelper.logDeliveryPartnerQrCodePreferenceChanged(useDeliveryPartnerQrCode)
    }

    override suspend fun setSelectedOrderId(selectedOrderId: Int) {
        dataStore.setSelectedOrderId(selectedOrderId)
        analyticsHelper.logSelectedOrderIdChanged(selectedOrderId)
    }
}
