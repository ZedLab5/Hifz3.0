package com.example.data.prayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Reschedules prayer time alarms automatically whenever device is rebooted or app updated.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED ||
            action == Intent.ACTION_DATE_CHANGED
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    AlarmRescheduler.rescheduleFromSavedSettings(context)
                    com.example.data.notifications.SpiritualAlarmScheduler.rescheduleAll(context)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        // Also refresh Glance Home Screen Widget
                        com.example.widget.PrayerWidgetUpdater.update(context)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    pendingResult.finish()
                }
            }
        }
    }
}
