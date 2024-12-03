package io.github.stcksmsh.kap.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.stcksmsh.kap.MainActivity
import io.github.stcksmsh.kap.R
import io.github.stcksmsh.kap.data.loadReminderSettings

object NotificationHelper {

    private const val CHANNEL_ID = "kap_reminder_channel"

    private val VIBRATION_PATTERN = longArrayOf(100, 300, 200, 400)

    private val AUDIO_ATTRIBUTES =
        AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build()

    fun createNotificationChannel(context: Context) {
        val reminderSettings = loadReminderSettings(context)

        val notificationSoundUri = if (reminderSettings.soundEnabled) {
            Uri.parse("android.resource://${context.packageName}/raw/notification_sound")
        } else {
            null
        }

            val channel = NotificationChannel(
                CHANNEL_ID, "Hydration Reminders", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to drink water"
                vibrationPattern = VIBRATION_PATTERN
                enableVibration(reminderSettings.vibrationEnabled)
                setSound(notificationSoundUri, AUDIO_ATTRIBUTES)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        fun showNotification(context: Context) {
            val reminderSettings = loadReminderSettings(context)
            val notificationSoundUri = if (reminderSettings.soundEnabled) {
                Uri.parse("android.resource://${context.packageName}/raw/notification_sound")
            } else {
                Uri.EMPTY
            }
            // check if channel has not been created
            if (NotificationManagerCompat.from(context)
                    .getNotificationChannel(CHANNEL_ID) == null
            ) {
                createNotificationChannel(context)
            } else {
                Log.d("NotificationHelper", "Channel already exists, updating settings")
                // Get the channel and update it
                NotificationManagerCompat.from(context).getNotificationChannel(CHANNEL_ID)?.apply {
                    setSound(
                        notificationSoundUri, AUDIO_ATTRIBUTES
                    )
                    enableVibration(reminderSettings.vibrationEnabled)
                }
            }

            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentTitle("Time to Hydrate!").setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent)
                .setVibrate(if (reminderSettings.vibrationEnabled) VIBRATION_PATTERN else null)
                .setSound(notificationSoundUri)
                .build()

            NotificationManagerCompat.from(context)
                .notify(System.currentTimeMillis().toInt(), notification)
        }
    }
