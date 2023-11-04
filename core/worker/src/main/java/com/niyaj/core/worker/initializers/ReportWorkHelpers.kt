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
    return NotificationCompat.Builder(this, ReportNotificationChannelID)
        .setSmallIcon(R.drawable.generate_report_icon)
        .setContentTitle(getString(R.string.reminder_notification_title))
        .setContentText(getString(R.string.reminder_notification_text))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}