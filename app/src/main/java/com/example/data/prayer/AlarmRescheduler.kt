package com.example.data.prayer

import android.content.Context
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.repository.NoorRepository

object AlarmRescheduler {
    private const val TAG = "AlarmRescheduler"

    fun rescheduleFromSavedSettings(context: Context) {
        try {
            val prefs = context.getSharedPreferences("noor_app_preferences", Context.MODE_PRIVATE)
            val isLocationConfigured = prefs.getBoolean("is_location_configured", false)
            if (!isLocationConfigured) {
                PrayerAlarmScheduler.cancelAll(context)
                return
            }
            val db = AppDatabase.getDatabase(context)
            val repository = NoorRepository(
                dao = db.noorDao(),
                quranBookmarkDao = db.quranBookmarkDao(),
                tasbihDao = db.tasbihDao(),
                prayerDao = db.prayerDao()
            )

            val savedZoneId = prefs.getString("selected_prayer_zone_id", null)
            val zone = repository.prayerZones.find { it.id == savedZoneId } ?: return
            
            val savedAuthId = prefs.getString("selected_calc_authority_id", null)
            val auth = repository.calculationAuthorities.find { it.id == savedAuthId } ?: repository.calculationAuthorities.first()
            
            val isHanafi = prefs.getBoolean("is_hanafi_asr", false)
            
            val offsets = mutableMapOf<String, Int>()
            listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha").forEach { pName ->
                offsets[pName] = prefs.getInt("manual_offset_$pName", 0)
            }

            val prayers = repository.calculatePrayerTimes(
                zone = zone,
                authority = auth,
                isHanafiAsr = isHanafi,
                minuteOffsets = offsets
            )

            val loadedTimers = mutableMapOf<String, Int>()
            val loadedEnabled = mutableMapOf<String, Boolean>()

            listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha").forEach { pName ->
                val defaultAlert = if (pName == "Sunrise") "Mute" else "Adhan"
                val alertType = prefs.getString("alert_type_$pName", defaultAlert) ?: defaultAlert
                loadedEnabled[pName] = (alertType != "Mute")

                val defaultTimer = if (pName == "Fajr") -15 else 0
                val timer = prefs.getInt("notification_timer_$pName", defaultTimer)
                loadedTimers[pName] = timer
            }

            PrayerAlarmScheduler.scheduleAll(
                context = context,
                prayers = prayers,
                timersMap = loadedTimers,
                enabledMap = loadedEnabled
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reschedule alarms from saved settings", e)
            throw e
        }
    }
}
