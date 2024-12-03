// app/src/main/java/com/bignerdranch/android/moodi/MoodNotificationWorker.kt
package com.bignerdranch.android.moodi

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class MoodNotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    private fun sendNotification() {
        val messages = listOf(
            "How's your day going? Take a moment to reflect.",
            "Time for a quick mood check-in!",
            "Your emotional well-being matters. Log your mood now.",
            "Remember to track your mood today!",
            "Quick check-in: How are you feeling?"
        )
        
        val intent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, NotificationUtils.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Mood Check-in")
            .setContentText(messages.random())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}
