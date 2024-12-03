package io.github.stcksmsh.kap.model


const val reminderTitle = "Remember to stay hydrated!"


/**
 * Represents a specific time of day.
 *
 * @property hour The hour of the day (0-23).
 * @property minute The minute of the hour (0-59).
 */
data class TimeOfDay(
    val hour: Int, val minute: Int
) {
    companion object {
        fun now(): TimeOfDay {
            val now = System.currentTimeMillis()
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = now
            return TimeOfDay(
                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                calendar.get(java.util.Calendar.MINUTE)
            )
        }
    }

    operator fun minus(other: TimeOfDay): Int {
        return (hour - other.hour) * 60 + (minute - other.minute)
    }

    operator fun minus(minute: Int): TimeOfDay {
        val newMinute = (this.minute - minute + 60) % 60
        val newHour = (hour - (60 + minute - this.minute ) / 60 + 24) % 24
        return TimeOfDay(newHour, newMinute)
    }

    operator fun plus(minutes: Int): TimeOfDay {
        val newMinute = (minute + minutes) % 60
        val newHour = (hour + (minute + minutes) / 60) % 24
        return TimeOfDay(newHour, newMinute)
    }
}

/**
 * Settings for reminders (notifications)
 *
 * @property intervalMinutes interval in minutes between reminders
 * @property startTime time of day from which to start sending notifications
 * @property endTime time of day when to stop sending notifications
 * @property soundEnabled whether the notifications make a sound
 * @property vibrationEnabled whether the notifications make vibrations
 */
data class ReminderSettings(
    val remindersEnabled: Boolean,
    val intervalMinutes: Int,
    val startTime: TimeOfDay,
    val endTime: TimeOfDay,
    val soundEnabled: Boolean,
    val vibrationEnabled: Boolean
)