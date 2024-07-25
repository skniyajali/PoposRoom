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

package com.popos.core.notifications.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.popos.core.notifications.R

const val ORDER_PRINT_NOTIFICATION_ID = 141414
private const val ORDER_PRINT_NOTIFICATION_CHANNEL_ID = "ReportNotificationChannel"

/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.printOrderNotification(orderId: Int): Notification {
    ensureNotificationChannelExists()

    return NotificationCompat.Builder(this, ORDER_PRINT_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.baseline_print_disabled)
        .setContentTitle(getString(R.string.order_print_title))
        .setContentText(getString(R.string.order_print_text).plus(" for order id - $orderId"))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setOnlyAlertOnce(false)
        .build()
}

/**
 * Ensures that a notification channel is present if applicable
 */
private fun Context.ensureNotificationChannelExists() {
    val channel = NotificationChannel(
        ORDER_PRINT_NOTIFICATION_CHANNEL_ID,
        getString(R.string.print_order_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.print_order_channel_desc)
    }
    // Register the channel with the system
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}
