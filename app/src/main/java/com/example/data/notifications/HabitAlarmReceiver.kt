package com.example.data.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.NoorNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra(HabitAlarmScheduler.EXTRA_HABIT_ID, -1L)
        val habitTitle = intent.getStringExtra(HabitAlarmScheduler.EXTRA_HABIT_TITLE) ?: "Spiritual Routine"
        val habitCategory = intent.getStringExtra(HabitAlarmScheduler.EXTRA_HABIT_CATEGORY) ?: "Routine"

        Log.d("HabitAlarmReceiver", "Received habit routine alarm for #$habitId '$habitTitle'")

        if (habitId <= 0) return

        try {
            // 1. Post routine reminder notification
            NoorNotificationHelper.showRoutineAlarmNotification(
                context = context,
                habitId = habitId,
                habitTitle = habitTitle,
                category = habitCategory
            )

            // 2. Schedule next cycle if still active
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val habit = db.noorDao().getHabitById(habitId)
                    if (habit != null && habit.isAlarmEnabled && habit.scheduledTimeMinutes != null) {
                        HabitAlarmScheduler.scheduleHabitAlarm(
                            context = context,
                            habitId = habit.id,
                            habitTitle = habit.title,
                            category = habit.category,
                            timeMinutes = habit.scheduledTimeMinutes,
                            daysMask = habit.pinnedDaysMask,
                            targetDateIso = habit.targetDateIso
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
