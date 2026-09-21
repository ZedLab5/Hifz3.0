package com.example.data.prayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.data.local.NoorNotificationHelper
import com.example.widget.PrayerWidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles triggered prayer notification alarms, dismiss actions ("Stop"),
 * and snooze actions ("Snooze 5 min").
 * Also applies Mosque Mode (Auto-Silent during salat) if configured,
 * and refreshes the home screen Glance widget.
 */
class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val prayerName = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_NAME) ?: "Salat"
        val timeFormatted = intent.getStringExtra(PrayerAlarmScheduler.EXTRA_TIME_FORMATTED) ?: ""
        val notifId = intent.getIntExtra(PrayerAlarmScheduler.EXTRA_NOTIFICATION_ID, -1)

        when (action) {
            PrayerAlarmScheduler.ACTION_STOP_ADHAN -> {
                Log.d("PrayerAlarmReceiver", "Received ACTION_STOP_ADHAN for $prayerName")
                // 1. Stop Adhan and set dismissal guard flag
                AdhanPlayer.stop(prayerName = prayerName, isDismiss = true)

                // 2. Cancel any pending snooze for this prayer
                PrayerAlarmScheduler.cancelSnooze(context, prayerName)

                // 3. Dismiss notification
                if (notifId != -1) {
                    NoorNotificationHelper.cancelPrayerAlert(context, notifId)
                } else {
                    NoorNotificationHelper.cancelPrayerAlerts(context)
                }
                return
            }

            PrayerAlarmScheduler.ACTION_SNOOZE_ADHAN -> {
                Log.d("PrayerAlarmReceiver", "Received ACTION_SNOOZE_ADHAN for $prayerName (5 min)")
                // 1. Immediately stop current playing adhan
                AdhanPlayer.stop(prayerName = prayerName, isDismiss = true)

                // 2. Dismiss current notification
                if (notifId != -1) {
                    NoorNotificationHelper.cancelPrayerAlert(context, notifId)
                }

                // 3. Schedule reliable snooze alarm for 5 minutes
                PrayerAlarmScheduler.scheduleSnooze(
                    context = context,
                    prayerName = prayerName,
                    timeFormatted = timeFormatted,
                    snoozeMinutes = 5
                )
                return
            }

            else -> {
                // Default / ACTION_PRAYER_ALARM handling
                val isPreAlertIntent = intent.getBooleanExtra(PrayerAlarmScheduler.EXTRA_IS_PRE_ALERT, false)
                val offsetMinutes = intent.getIntExtra(PrayerAlarmScheduler.EXTRA_OFFSET_MINUTES, 0)
                val isSnooze = intent.getBooleanExtra(PrayerAlarmScheduler.EXTRA_IS_SNOOZE, false)
                val isPreAlert = (isPreAlertIntent || (offsetMinutes < 0)) && !isSnooze

                val prefs = context.getSharedPreferences("noor_app_preferences", Context.MODE_PRIVATE)
                val alertType = prefs.getString(
                    "alert_type_$prayerName",
                    if (prayerName == "Sunrise") "Mute" else "Adhan"
                ) ?: "Adhan"

                // 1. Apply Mosque Mode (Auto-Silent DND during salat) if enabled (never for pre-alert)
                if (!isPreAlert) {
                    applyMosqueMode(context, prefs)
                }

                // 2. Present prayer notification and audio alert
                if (alertType != "Mute") {
                    NoorNotificationHelper.showPrayerAlert(
                        context = context,
                        prayerName = prayerName,
                        timeFormatted = timeFormatted,
                        isPreAlert = isPreAlert,
                        offsetMinutes = offsetMinutes
                    )
                    if (alertType == "Adhan" && !isPreAlert) {
                        AdhanPlayer.play(context, prayerName = prayerName, isSnooze = isSnooze)
                    }
                }

                // 3. Asynchronously update the home screen Glance widget and re-arm alarms
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        PrayerWidgetUpdater.update(context)
                        // Re-arm alarms for tomorrow to ensure continuous daily cycle
                        AlarmRescheduler.rescheduleFromSavedSettings(context)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }

    private fun applyMosqueMode(context: Context, prefs: SharedPreferences) {
        val isAutoSilent = prefs.getBoolean("auto_silent_salat", true)
        if (!isAutoSilent) return

        val durationMinutes = prefs.getInt("silent_duration_minutes", 20)
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            val originalRingerMode = audioManager.ringerMode
            if (originalRingerMode == AudioManager.RINGER_MODE_NORMAL) {
                try {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                } catch (e: Exception) {
                    // DND policy access may not be granted on certain system images
                }

                // Schedule ringer restore after salat duration
                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        val currentMode = audioManager.ringerMode
                        if (currentMode == AudioManager.RINGER_MODE_VIBRATE ||
                            currentMode == AudioManager.RINGER_MODE_SILENT
                        ) {
                            audioManager.ringerMode = originalRingerMode
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, durationMinutes * 60 * 1000L)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
