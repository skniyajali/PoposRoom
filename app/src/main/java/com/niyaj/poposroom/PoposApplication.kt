package com.niyaj.poposroom

import android.app.Application
import android.content.Context
import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp
import org.acra.config.mailSender
import org.acra.config.toast
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import timber.log.Timber

@HiltAndroidApp
class PoposApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        initAcra {
            //core configuration:
            stopServicesOnCrash = false
            sendReportsInDevMode = false
            deleteUnapprovedReportsOnApplicationStart = true
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON

            toast {
                //required
                text = getString(R.string.toast_text)
                //defaults to Toast.LENGTH_LONG
                length = Toast.LENGTH_LONG
            }

            mailSender {
                //required
                mailTo = "niyaj639@gmail.com"
                //defaults to true
                reportAsFile = true
                //defaults to ACRA-report.stacktrace
                reportFileName = "Crash.txt"
                //defaults to "<applicationId> Crash Report"
                subject = getString(R.string.mail_subject)
                //defaults to empty
                body = getString(R.string.mail_body)
            }

//            notification {
//                //required
//                title = getString(R.string.notification_title)
//                //required
//                text = getString(R.string.notification_text)
//                //required
//                channelName = getString(R.string.notification_channel)
//                //optional channel description
//                channelDescription = getString(R.string.notification_channel_desc)
//                //defaults to NotificationManager.IMPORTANCE_HIGH
////                resChannelImportance = NotificationManager.IMPORTANCE_MAX
//                //optional, enables ticker text
////                tickerText = getString(R.string.notification_ticker)
//                //defaults to android.R.drawable.stat_sys_warning
//                resIcon = R.drawable.ic_clear
//                //defaults to android.R.string.ok
//                sendButtonText = getString(R.string.notification_send)
//                //defaults to android.R.drawable.ic_menu_send
//                //defaults to android.R.string.cancel
//                discardButtonText = getString(R.string.notification_discard)
//                //defaults to android.R.drawable.ic_menu_delete
//                //optional, enables inline comment button
////                sendWithCommentButtonText = getString(R.string.notification_send_with_comment)
//                //required if above is set
////                resSendWithCommentButtonIcon = R.drawable.notification_send_with_comment
//                //optional inline comment hint
////                commentPrompt = getString(R.string.notification_comment)
//                //defaults to false
//                sendOnClick = true
//            }
        }
    }
}