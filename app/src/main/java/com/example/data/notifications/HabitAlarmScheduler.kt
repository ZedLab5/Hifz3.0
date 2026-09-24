package com.example.data.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.DailyHabitEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

object HabitAlarmScheduler {

    private const val TAG = "HabitAlarmScheduler"
    const val ACTION_HABIT_ROUTINE_ALARM = "com.example.action.HABIT_ROUTINE_ALARM"
    const val EXTRA_HABIT_ID = "extra_habit_id"
    const val EXTRA_HABIT_TITLE = "extra_habit_title"
    const val EXTRA_HABIT_CATEGORY = "extra_habit_category"

    fun scheduleHabitAlarm(
        context: Context,
        habitId: Long,
        habitTitle: String,
        category: String,
        timeMinutes: Int,
        daysMask: Int = 127,
        targetDateIso: String? = null
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val triggerMillis = calculateNextTriggerMillis(timeMinutes, daysMask, targetDateIso) ?: return
        val pendingIntent = createPendingIntent(context, habitId, habitTitle, category)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled habit alarm for #$habitId '$habitTitle' at $triggerMillis")
        } catch (e: SecurityException) {
            Log.w(TAG, "Exact alarm permission missing, scheduling with fallback", e)
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            } catch (fallbackEx: Exception) {
                fallbackEx.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelHabitAlarm(context: Context, habitId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = createPendingIntent(context, habitId, "", "")
        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Cancelled habit alarm for #$habitId")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun rescheduleAll(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(context)
                val habits = db.noorDao().getAllHabitsOnce()
                for (habit in habits) {
                    if (habit.isAlarmEnabled && habit.scheduledTimeMinutes != null) {
                        scheduleHabitAlarm(
                            context = context,
                            habitId = habit.id,
                            habitTitle = habit.title,
                            category = habit.category,
                            timeMinutes = habit.scheduledTimeMinutes,
                            daysMask = habit.pinnedDaysMask,
                            targetDateIso = habit.targetDateIso
                        )
                    } else {
                        cancelHabitAlarm(context, habit.id)
                    }
                }
                Log.d(TAG, "Rescheduled all habit alarms successfully.")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun calculateNextTriggerMillis(
        timeMinutes: Int,
        daysMask: Int,
        targetDateIso: String?
    ): Long? {
        val now = Calendar.getInstance()
        val hour = timeMinutes / 60
        val minute = timeMinutes % 60

        if (!targetDateIso.isNullOrBlank()) {
            // Specific calendar date (YYYY-MM-DD)
            val parts = targetDateIso.split("-")
            if (parts.size == 3) {
                val year = parts[0].toIntOrNull() ?: return null
                val month = (parts[1].toIntOrNull() ?: 1) - 1
                val day = parts[2].toIntOrNull() ?: return null

                val target = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                return if (target.after(now)) target.timeInMillis else null
            }
        }

        // Recurring day-of-week mask (Bit 0: Mon, Bit 1: Tue, ... Bit 6: Sun)
        // Check today and the next 7 days
        for (dayOffset in 0..7) {
            val candidate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, dayOffset)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (candidate.after(now)) {
                val calDayOfWeek = candidate.get(Calendar.DAY_OF_WEEK)
                // Convert Java Calendar (Sunday=1, Monday=2... Saturday=7) to 0..6 (Mon=0..Sun=6)
                val bitIndex = when (calDayOfWeek) {
                    Calendar.MONDAY -> 0
                    Calendar.TUESDAY -> 1
                    Calendar.WEDNESDAY -> 2
                    Calendar.THURSDAY -> 3
                    Calendar.FRIDAY -> 4
                    Calendar.SATURDAY -> 5
                    Calendar.SUNDAY -> 6
                    else -> 0
                }
                if ((daysMask and (1 shl bitIndex)) != 0) {
                    return candidate.timeInMillis
                }
            }
        }

        return null
    }

    private fun createPendingIntent(
        context: Context,
        habitId: Long,
        habitTitle: String,
        category: String
    ): PendingIntent {
        val intent = Intent(context, HabitAlarmReceiver::class.java).apply {
            action = ACTION_HABIT_ROUTINE_ALARM
            putExtra(EXTRA_HABIT_ID, habitId)
            putExtra(EXTRA_HABIT_TITLE, habitTitle)
            putExtra(EXTRA_HABIT_CATEGORY, category)
        }
        val requestCode = (habitId.hashCode() and 0x7FFFFFFF) % 10000 + 8000
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
