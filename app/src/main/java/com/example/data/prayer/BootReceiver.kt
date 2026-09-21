package com.example.data.prayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.repository.NoorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Reschedules prayer time alarms automatically whenever device is rebooted or app updated.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val prefs = context.getSharedPreferences("noor_app_preferences", Context.MODE_PRIVATE)
                    val db = AppDatabase.getDatabase(context)
                    val repository = NoorRepository(
                        dao = db.noorDao(),
                        quranBookmarkDao = db.quranBookmarkDao(),
                        tasbihDao = db.tasbihDao(),
                        prayerDao = db.prayerDao()
                    )

                    val savedZoneId = prefs.getString("selected_prayer_zone_id", null)
                    val zone = repository.prayerZones.find { it.id == savedZoneId } ?: repository.prayerZones.first()
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
                    val defaultTimers = mapOf("Fajr" to -15, "Sunrise" to 0, "Dhuhr" to 0, "Asr" to 0, "Maghrib" to 0, "Isha" to 0)
                    val defaultEnabled = mapOf("Fajr" to true, "Sunrise" to false, "Dhuhr" to true, "Asr" to true, "Maghrib" to true, "Isha" to true)
                    PrayerAlarmScheduler.scheduleAll(
                        context = context,
                        prayers = prayers,
                        timersMap = defaultTimers,
                        enabledMap = defaultEnabled
                    )

                    // Also refresh Glance Home Screen Widget
                    com.example.widget.PrayerWidgetUpdater.update(context)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
