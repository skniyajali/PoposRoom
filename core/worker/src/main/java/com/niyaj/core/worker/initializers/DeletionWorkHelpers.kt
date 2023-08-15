package com.niyaj.core.worker.initializers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import com.niyaj.core.worker.R

private const val DeletionNotificationId = 131313
private const val DeletionNotificationChannelID = "DeletionNotificationChannel"


/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.deletionForegroundInfo() = ForegroundInfo(
    DeletionNotificationId,
    deletionWorkNotification(),
)

/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
private fun Context.deletionWorkNotification(): Notification {
    val channel = NotificationChannel(
        DeletionNotificationChannelID,
        getString(R.string.deletion_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.deletion_notification_channel_description)
    }
    // Register the channel with the system
    val notificationManager: NotificationManager? =
        getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    notificationManager?.createNotificationChannel(channel)

    return NotificationCompat.Builder(this, DeletionNotificationChannelID)
        .setSmallIcon(R.drawable.auto_delete_icon)
        .setContentTitle(getString(R.string.deletion_notification_title))
        .setContentText(getString(R.string.deletion_notification_text))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}