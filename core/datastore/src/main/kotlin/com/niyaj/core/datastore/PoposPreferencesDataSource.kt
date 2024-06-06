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

package com.niyaj.core.datastore

import androidx.datastore.core.DataStore
import com.niyaj.model.DarkThemeConfig
import com.niyaj.model.ThemeBrand
import com.niyaj.model.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PoposPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData = userPreferences.data
        .map {
            UserData(
                themeBrand = when (it.themeBrand) {
                    null,
                    ThemeBrandProto.THEME_BRAND_UNSPECIFIED,
                    ThemeBrandProto.UNRECOGNIZED,
                    ThemeBrandProto.THEME_BRAND_DEFAULT,
                    -> ThemeBrand.DEFAULT
                    ThemeBrandProto.THEME_BRAND_ANDROID -> ThemeBrand.ANDROID
                },
                darkThemeConfig = when (it.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                    ->
                        DarkThemeConfig.FOLLOW_SYSTEM
                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT ->
                        DarkThemeConfig.LIGHT
                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                },
                useDynamicColor = it.useDynamicColor,
                shouldHideOnboarding = it.shouldHideOnboarding,
                userLoggedIn = it.userLoggedIn,
                loggedInUserId = it.loggedInUserId,
                sendOrderSms = it.sendOrderSms,
                useDeliveryPartnerQrCode = it.useDeliveryPartnerQrCode,
                selectedOrderId = it.selectedOrderId,
            )
        }

    val loggedInUser = userPreferences.data.map { it.loggedInUserId }

    val isUserLoggedIn = userPreferences.data.map { it.userLoggedIn }

    suspend fun usePartnerQrCode() = userPreferences.data.first().useDeliveryPartnerQrCode

    suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        userPreferences.updateData {
            it.copy {
                this.themeBrand = when (themeBrand) {
                    ThemeBrand.DEFAULT -> ThemeBrandProto.THEME_BRAND_DEFAULT
                    ThemeBrand.ANDROID -> ThemeBrandProto.THEME_BRAND_ANDROID
                }
            }
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy { this.useDynamicColor = useDynamicColor }
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM ->
                        DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy { this.shouldHideOnboarding = shouldHideOnboarding }
        }
    }

    suspend fun markUserAsLoggedIn(userId: Int) {
        userPreferences.updateData {
            it.copy {
                this.userLoggedIn = true
                this.loggedInUserId = userId
                updateShouldHideOnboardingIfNecessary()
            }
        }
    }

    suspend fun markUserAsLoggedOut() {
        userPreferences.updateData {
            it.copy {
                this.userLoggedIn = false
                this.loggedInUserId = 0
                updateShouldHideOnboardingIfNecessary()
            }
        }
    }

    suspend fun setSendOrderSms(sendSms: Boolean) {
        userPreferences.updateData {
            it.copy {
                this.sendOrderSms = sendSms
            }
        }
    }

    suspend fun setUseDeliveryPartnerQrCode(useCode: Boolean) {
        userPreferences.updateData {
            it.copy {
                this.useDeliveryPartnerQrCode = useCode
            }
        }
    }

    suspend fun setSelectedOrderId(orderId: Int) {
        userPreferences.updateData {
            it.copy {
                this.selectedOrderId = orderId
            }
        }
    }
}

private fun UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary() {
    if (userLoggedIn && loggedInUserId != 0) {
        shouldHideOnboarding = false
    }
}
