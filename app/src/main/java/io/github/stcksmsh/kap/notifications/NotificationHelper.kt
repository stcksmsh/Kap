import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.stcksmsh.kap.data.loadReminderSettings

object NotificationHelper {

    private const val CH_DEFAULT = "kap.reminders.default"
    private const val CH_SILENT  = "kap.reminders.silent"

    fun ensureChannels(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java)

        if (nm.getNotificationChannel(CH_DEFAULT) == null) {
            nm.createNotificationChannel(
                NotificationChannel(
                    CH_DEFAULT, "Hydration reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    enableVibration(true)
                }
            )
        }
        if (nm.getNotificationChannel(CH_SILENT) == null) {
            nm.createNotificationChannel(
                NotificationChannel(
                    CH_SILENT, "Hydration (silent)",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    setSound(null, null)
                    enableVibration(false)
                }
            )
        }
    }

    fun showNotification(context: Context) {
        ensureChannels(context)
        val settings = loadReminderSettings(context)
        val channelId = if (settings.soundEnabled || settings.vibrationEnabled) CH_DEFAULT else CH_SILENT

        if (androidx.core.content.ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return

        val pi = android.app.PendingIntent.getActivity(
            context, 0,
            android.content.Intent(context, io.github.stcksmsh.kap.MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notif = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(io.github.stcksmsh.kap.R.drawable.ic_launcher_foreground) // pick a final icon
            .setContentTitle("Time to hydrate!")
            .setContentText("A quick sip keeps you on track.")
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()

        androidx.core.app.NotificationManagerCompat.from(context)
            .notify((System.currentTimeMillis() and 0x7FFFFFFF).toInt(), notif)
    }
}
