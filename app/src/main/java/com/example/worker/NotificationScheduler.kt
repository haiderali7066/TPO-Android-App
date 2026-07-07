package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.CyclePrediction
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val TAG_NOTIFICATION_WORK = "tpo_notif_work"

    fun scheduleNotifications(context: Context, predictions: List<CyclePrediction>, enabled: Boolean) {
        val workManager = WorkManager.getInstance(context)
        
        // Always cancel existing scheduled jobs first to prevent double scheduling
        workManager.cancelAllWorkByTag(TAG_NOTIFICATION_WORK)

        if (!enabled || predictions.isEmpty()) {
            Log.d("NotificationScheduler", "Notifications disabled or no predictions.")
            return
        }

        val now = LocalDateTime.now()

        // We will schedule notifications for the next 2 cycles
        predictions.take(2).forEachIndexed { index, cycle ->
            // 1. Period starts today
            scheduleOneTimeNotification(
                context = context,
                workManager = workManager,
                targetDate = cycle.periodStartDate,
                title = "Period Starts Today",
                content = "Your period is predicted to start today.",
                id = index * 10 + 1,
                now = now
            )

            // 2. Period starts tomorrow
            scheduleOneTimeNotification(
                context = context,
                workManager = workManager,
                targetDate = cycle.periodStartDate.minusDays(1),
                title = "Period Starts Tomorrow",
                content = "Your period is predicted to start tomorrow.",
                id = index * 10 + 2,
                now = now
            )

            // 3. Ovulation day
            scheduleOneTimeNotification(
                context = context,
                workManager = workManager,
                targetDate = cycle.ovulationDate,
                title = "Predicted Ovulation Day",
                content = "Today is your predicted ovulation day.",
                id = index * 10 + 3,
                now = now
            )

            // 4. Ovulation tomorrow
            scheduleOneTimeNotification(
                context = context,
                workManager = workManager,
                targetDate = cycle.ovulationDate.minusDays(1),
                title = "Ovulation Tomorrow",
                content = "Your ovulation is predicted to be tomorrow.",
                id = index * 10 + 4,
                now = now
            )
        }
    }

    private fun scheduleOneTimeNotification(
        context: Context,
        workManager: WorkManager,
        targetDate: LocalDate,
        title: String,
        content: String,
        id: Int,
        now: LocalDateTime
    ) {
        // Set reminder time to 9:00 AM
        val targetDateTime = LocalDateTime.of(targetDate, LocalTime.of(9, 0))
        val delayMillis = Duration.between(now, targetDateTime).toMillis()

        if (delayMillis > 0) {
            val inputData = Data.Builder()
                .putString(NotificationWorker.EXTRA_TITLE, title)
                .putString(NotificationWorker.EXTRA_CONTENT, content)
                .putInt(NotificationWorker.NOTIFICATION_ID, id)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(inputData)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .addTag(TAG_NOTIFICATION_WORK)
                .build()

            workManager.enqueue(workRequest)
            Log.d("NotificationScheduler", "Scheduled '$title' in ${delayMillis / 1000 / 60} minutes (ID $id)")
        } else {
            Log.d("NotificationScheduler", "Skipped '$title' because target time is in the past ($targetDateTime)")
        }
    }
}
