package com.popos.core.notifications.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import com.popos.core.notifications.R

const val DELETION_NOTIFICATION_ID = 131313
private const val DELETION_NOTIFICATION_CHANNEL_ID = "DeletionNotificationChannel"


/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.deletionWorkNotification(): Notification {
    ensureNotificationChannelExists()

    return NotificationCompat.Builder(this, DELETION_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.auto_delete_icon)
        .setContentTitle(getString(R.string.deletion_notification_title))
        .setContentText(getString(R.string.deletion_notification_text))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .setOnlyAlertOnce(true)
        .build()
}


/**
 * Ensures that a notification channel is present if applicable
 */
private fun Context.ensureNotificationChannelExists() {
    val channel = NotificationChannel(
        DELETION_NOTIFICATION_CHANNEL_ID,
        getString(R.string.deletion_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.deletion_notification_channel_description)
    }

    // Register the channel with the system
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}