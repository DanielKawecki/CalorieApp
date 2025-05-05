package pl.edu.uwr.pam.calorieapp

import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.random.Random

class DailyNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val hour = inputData.getInt("hour", -1)
        val message = when (hour) {
            9 -> "Enter your breakfast!"
            14 -> "Enter your lunch!"
            20 -> "Enter your dinner!"
            else -> "Reminder!"
        }

        val prefs = applicationContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("notifications_enabled", true)
        if (!enabled) return Result.success()

        showNotification("Calorie Reminder", message)
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "daily_notification_channel"

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }
}

fun showNotification(context: Context, title: String, message: String) {
    val channelId = "debug_channel"

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Debug Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(Random.nextInt(), notification)
}

@Composable
fun DebugNotificationButton() {
    val context = LocalContext.current

    Button(onClick = {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("notifications_enabled", true)
        if (enabled) {
            showNotification(context, "Test Notification", "This is a debug test!")
        }
    }) {
        Text("Trigger Notification")
    }
}
