package com.popos.core.notifications.utils


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.popos.core.notifications.R

const val REPORT_NOTIFICATION_ID = 121212
private const val REPORT_NOTIFICATION_CHANNEL_ID = "ReportNotificationChannel"


/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.reportWorkNotification(): Notification {
    ensureNotificationChannelExists()

    return NotificationCompat.Builder(this, REPORT_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.generate_report_icon)
        .setContentTitle(getString(R.string.reminder_notification_title))
        .setContentText(getString(R.string.reminder_notification_text))
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
        REPORT_NOTIFICATION_CHANNEL_ID,
        getString(R.string.reminder_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.reminder_notification_channel_description)
    }
    // Register the channel with the system
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}