package com.niyaj.core.worker.initializers


import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import com.niyaj.core.worker.R

private const val ReportNotificationId = 121212
private const val ReportNotificationChannelID = "ReportNotificationChannel"


/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.reportForegroundInfo() = ForegroundInfo(
    ReportNotificationId,
    reportWorkNotification(),
)

/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
private fun Context.reportWorkNotification(): Notification {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val channel = NotificationChannel(
//            ReportNotificationChannelID,
//            getString(R.string.reminder_notification_channel_name),
//            NotificationManager.IMPORTANCE_DEFAULT,
//        ).apply {
//            description = getString(R.string.reminder_notification_channel_description)
//        }
//        // Register the channel with the system
//        val notificationManager: NotificationManager? =
//            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
//
//        notificationManager?.createNotificationChannel(channel)
//    }

    return NotificationCompat.Builder(this, ReportNotificationChannelID)
        .setSmallIcon(R.drawable.generate_report_icon)
        .setContentTitle(getString(R.string.reminder_notification_title))
        .setContentText(getString(R.string.reminder_notification_text))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}