package io.github.stcksmsh.kap.model


const val reminderTitle = "Remember to stay hydrated!"


/**
 * Represents a specific time of day.
 *
 * @property hour The hour of the day (0-23).
 * @property minute The minute of the hour (0-59).
 */
data class TimeOfDay(val hour: Int, val minute: Int) {
    init {
        require(hour in 0..23 && minute in 0..59) { "Invalid time: $hour:$minute" }
    }

    companion object {
        fun now(): TimeOfDay {
            val cal = java.util.Calendar.getInstance()
            return TimeOfDay(cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
        }
    }

    /** Minutes since 00:00 */
    fun toMinutes() = hour * 60 + minute

    /** From minutes since 00:00 (mod 24h) */
    private fun fromMinutes(total: Int): TimeOfDay {
        val m = ((total % (24 * 60)) + (24 * 60)) % (24 * 60)
        return TimeOfDay(m / 60, m % 60)
    }

    operator fun plus(minutes: Int): TimeOfDay = fromMinutes(toMinutes() + minutes)
    operator fun minus(minutes: Int): TimeOfDay = fromMinutes(toMinutes() - minutes)

    /** Signed minutes difference this - other in [-1439, 1439] */
    operator fun minus(other: TimeOfDay): Int = this.toMinutes() - other.toMinutes()

    fun toTodayCalendar(): java.util.Calendar =
        java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
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