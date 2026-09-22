package com.example.data.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

object SpiritualAlarmScheduler {

    private const val TAG = "SpiritualAlarmScheduler"
    const val ACTION_SPIRITUAL_REMINDER = "com.example.action.SPIRITUAL_REMINDER"
    const val EXTRA_REMINDER_ID = "extra_reminder_id"

    fun scheduleReminder(context: Context, reminderId: String, hour: Int, minute: Int) {
        val reminderItem = SpiritualReminderRepository.getItem(reminderId) ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val triggerMillis = calculateNextTriggerMillis(reminderItem, hour, minute)
        val pendingIntent = createPendingIntent(context, reminderId)

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
            Log.d(TAG, "Scheduled spiritual reminder: $reminderId for millis $triggerMillis")
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

    fun cancelReminder(context: Context, reminderId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = createPendingIntent(context, reminderId)
        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Cancelled spiritual reminder: $reminderId")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun rescheduleAll(context: Context) {
        val prefs = context.getSharedPreferences("noor_app_preferences", Context.MODE_PRIVATE)
        val allStates = SpiritualReminderRepository.loadAllStates(prefs)

        for ((id, state) in allStates) {
            if (state.isEnabled) {
                scheduleReminder(context, id, state.hour, state.minute)
            } else {
                cancelReminder(context, id)
            }
        }
        Log.d(TAG, "Rescheduled all spiritual reminders successfully.")
    }

    private fun calculateNextTriggerMillis(item: SpiritualReminderItem, targetHour: Int, targetMinute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (item.isWeeklyFridayOnly) {
            // Find next Friday
            val currentDayOfWeek = target.get(Calendar.DAY_OF_WEEK)
            if (currentDayOfWeek == Calendar.FRIDAY && target.after(now)) {
                return target.timeInMillis
            }
            // Move to next Friday
            var daysToAdd = (Calendar.FRIDAY - currentDayOfWeek + 7) % 7
            if (daysToAdd == 0) daysToAdd = 7
            target.add(Calendar.DAY_OF_YEAR, daysToAdd)
            return target.timeInMillis
        }

        if (item.isFastingSpecific) {
            // Sunday evening (before Monday fast) or Wednesday evening (before Thursday fast)
            val currentDay = target.get(Calendar.DAY_OF_WEEK)
            if ((currentDay == Calendar.SUNDAY || currentDay == Calendar.WEDNESDAY) && target.after(now)) {
                return target.timeInMillis
            }
            // Find nearest Sunday or Wednesday
            var daysToSunday = (Calendar.SUNDAY - currentDay + 7) % 7
            if (daysToSunday == 0 && !target.after(now)) daysToSunday = 7

            var daysToWednesday = (Calendar.WEDNESDAY - currentDay + 7) % 7
            if (daysToWednesday == 0 && !target.after(now)) daysToWednesday = 7

            val chosenDays = minOf(daysToSunday, daysToWednesday)
            target.add(Calendar.DAY_OF_YEAR, chosenDays)
            return target.timeInMillis
        }

        // Daily standard reminder
        if (target.before(now) || target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis
    }

    private fun createPendingIntent(context: Context, reminderId: String): PendingIntent {
        val intent = Intent(context, SpiritualReminderReceiver::class.java).apply {
            action = ACTION_SPIRITUAL_REMINDER
            putExtra(EXTRA_REMINDER_ID, reminderId)
        }
        val requestCode = (reminderId.hashCode() and 0x7FFFFFFF) % 10000 + 4000
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
