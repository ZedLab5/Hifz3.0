package com.example.data.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.local.NoorNotificationHelper

class SpiritualReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(SpiritualAlarmScheduler.EXTRA_REMINDER_ID) ?: return
        Log.d("SpiritualReminderReceiver", "Triggered spiritual reminder alarm: $reminderId")

        try {
            // 1. Post notification to shade
            NoorNotificationHelper.showSpiritualReminder(context, reminderId)

            // 2. Reschedule the next cycle for this reminder if still enabled
            val prefs = context.getSharedPreferences("noor_app_preferences", Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("spiritual_reminder_enabled_$reminderId", true)
            if (isEnabled) {
                val item = SpiritualReminderRepository.getItem(reminderId)
                val hour = prefs.getInt("spiritual_reminder_hour_$reminderId", item?.defaultHour ?: 12)
                val minute = prefs.getInt("spiritual_reminder_minute_$reminderId", item?.defaultMinute ?: 0)
                SpiritualAlarmScheduler.scheduleReminder(context, reminderId, hour, minute)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
