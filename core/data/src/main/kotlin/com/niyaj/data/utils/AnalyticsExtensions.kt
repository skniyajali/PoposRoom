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

package com.niyaj.data.utils

import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper

internal fun AnalyticsHelper.logThemeChanged(themeName: String) =
    logEvent(
        AnalyticsEvent(
            type = "theme_changed",
            extras = listOf(
                AnalyticsEvent.Param(key = "theme_name", value = themeName),
            ),
        ),
    )

internal fun AnalyticsHelper.logDarkThemeConfigChanged(darkThemeConfigName: String) =
    logEvent(
        AnalyticsEvent(
            type = "dark_theme_config_changed",
            extras = listOf(
                AnalyticsEvent.Param(key = "dark_theme_config", value = darkThemeConfigName),
            ),
        ),
    )

internal fun AnalyticsHelper.logDynamicColorPreferenceChanged(useDynamicColor: Boolean) =
    logEvent(
        AnalyticsEvent(
            type = "dynamic_color_preference_changed",
            extras = listOf(
                AnalyticsEvent.Param(
                    key = "dynamic_color_preference",
                    value = useDynamicColor.toString(),
                ),
            ),
        ),
    )

internal fun AnalyticsHelper.logOnboardingStateChanged(shouldHideOnboarding: Boolean) {
    val eventType = if (shouldHideOnboarding) "onboarding_complete" else "onboarding_reset"
    logEvent(
        AnalyticsEvent(type = eventType),
    )
}

internal fun AnalyticsHelper.logUserLoggedIn(userId: Int) =
    logEvent(
        AnalyticsEvent(
            type = "user_logged_in",
            extras = listOf(
                AnalyticsEvent.Param(key = "user_id", value = userId.toString()),
            ),
        ),
    )

internal fun AnalyticsHelper.logUserLoggedOut() = logEvent(AnalyticsEvent(type = "user_logged_out"))

internal fun AnalyticsHelper.logOrderSmsPreferenceChanged(sendOrderSms: Boolean) = logEvent(
    AnalyticsEvent(
        type = "order_sms_preference_changed",
        extras = listOf(
            AnalyticsEvent.Param(key = "send_order_sms", value = sendOrderSms.toString()),
        ),
    ),
)

internal fun AnalyticsHelper.logDeliveryPartnerQrCodePreferenceChanged(useDeliveryPartnerQrCode: Boolean) =
    logEvent(
        AnalyticsEvent(
            type = "delivery_partner_qr_code_preference_changed",
            extras = listOf(
                AnalyticsEvent.Param(
                    key = "use_delivery_partner_qr_code",
                    value = useDeliveryPartnerQrCode.toString(),
                ),
            ),
        ),
    )

internal fun AnalyticsHelper.logSelectedOrderIdChanged(selectedOrderId: Int) = logEvent(
    AnalyticsEvent(
        type = "selected_order_id_changed",
        extras = listOf(
            AnalyticsEvent.Param(key = "selected_order_id", value = selectedOrderId.toString()),
        ),
    ),
)

internal fun AnalyticsHelper.logBackupPerformed() = logEvent(
    AnalyticsEvent(
        type = "backup_initiated",
        extras = listOf(
            AnalyticsEvent.Param(
                key = "backup_initiated",
                value = System.currentTimeMillis().toFormattedDateAndTime,
            ),
        ),
    ),
)

internal fun AnalyticsHelper.logBackupRestorePerformed() = logEvent(
    AnalyticsEvent(
        type = "restore_initiated",
        extras = listOf(
            AnalyticsEvent.Param(
                key = "restore_initiated",
                value = System.currentTimeMillis().toFormattedDateAndTime,
            ),
        ),
    ),
)
