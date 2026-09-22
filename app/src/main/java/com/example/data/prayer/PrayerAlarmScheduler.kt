package com.example.data.prayer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.model.PrayerTime
import java.util.Calendar

/**
 * Manages reliable AlarmManager alarms for Adhan and pre-salat reminders.
 * Schedules separate, independent alarms for pre-alert reminders and main adhan alerts.
 */
object PrayerAlarmScheduler {

    const val ACTION_PRAYER_ALARM = "com.example.action.PRAYER_ALARM"
    const val ACTION_STOP_ADHAN = "com.example.action.STOP_ADHAN"
    const val ACTION_SNOOZE_ADHAN = "com.example.action.SNOOZE_ADHAN"
    const val EXTRA_PRAYER_NAME = "extra_prayer_name"
    const val EXTRA_TIME_FORMATTED = "extra_time_formatted"
    const val EXTRA_OFFSET_MINUTES = "extra_offset_minutes"
    const val EXTRA_IS_PRE_ALERT = "extra_is_pre_alert"
    const val EXTRA_IS_SNOOZE = "extra_is_snooze"
    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    // Base request codes ensuring distinct PendingIntents for Main, Pre-Alert, and Snooze
    private val PRAYER_MAIN_CODES = mapOf(
        "Fajr" to 101,
        "Sunrise" to 102,
        "Dhuhr" to 103,
        "Asr" to 104,
        "Maghrib" to 105,
        "Isha" to 106
    )

    private val PRAYER_PRE_ALERT_CODES = mapOf(
        "Fajr" to 201,
        "Sunrise" to 202,
        "Dhuhr" to 203,
        "Asr" to 204,
        "Maghrib" to 205,
        "Isha" to 206
    )

    private val PRAYER_SNOOZE_CODES = mapOf(
        "Fajr" to 301,
        "Sunrise" to 302,
        "Dhuhr" to 303,
        "Asr" to 304,
        "Maghrib" to 305,
        "Isha" to 306
    )

    fun getMainRequestCode(prayerName: String): Int =
        PRAYER_MAIN_CODES[prayerName] ?: (1000 + (prayerName.hashCode() and 0x7FFFFFFF) % 100)

    fun getPreAlertRequestCode(prayerName: String): Int =
        PRAYER_PRE_ALERT_CODES[prayerName] ?: (2000 + (prayerName.hashCode() and 0x7FFFFFFF) % 100)

    fun getSnoozeRequestCode(prayerName: String): Int =
        PRAYER_SNOOZE_CODES[prayerName] ?: (3000 + (prayerName.hashCode() and 0x7FFFFFFF) % 100)

    data class ScheduledAlarmInfo(
        val prayerName: String,
        val timeFormatted: String,
        val triggerMillis: Long,
        val isPreAlert: Boolean,
        val offsetMinutes: Int
    )

    /**
     * Schedules separate pre-alert AND main adhan alarms independently per prayer.
     * If offsetMinutes < 0, a pre-alert is scheduled at (time + offsetMinutes) and
     * the main adhan alarm is scheduled at the exact prayer time (offset 0).
     */
    fun scheduleAll(
        context: Context,
        prayers: List<PrayerTime>,
        timersMap: Map<String, Int>,
        enabledMap: Map<String, Boolean>
    ) {
        val prefs = context.getSharedPreferences("noor_app_preferences", Context.MODE_PRIVATE)
        val isLocationConfigured = prefs.getBoolean("is_location_configured", false)
        if (!isLocationConfigured) {
            cancelAll(context)
            return
        }
        val savedZoneId = prefs.getString("selected_prayer_zone_id", null)
        val zone = com.example.data.repository.NoorRepository.prayerZones.find { it.id == savedZoneId }
            ?: return
        val tz = java.util.TimeZone.getTimeZone(zone.timeZoneId)

        val isHanafi = prefs.getBoolean("is_hanafi_asr", false)
        val savedAuthId = prefs.getString("selected_calc_authority_id", null)
        val auth = com.example.data.repository.NoorRepository.calculationAuthorities.find { it.id == savedAuthId }
            ?: com.example.data.repository.NoorRepository.calculationAuthorities.first()

        val offsets = mutableMapOf<String, Int>()
        listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha").forEach { pName ->
            offsets[pName] = prefs.getInt("manual_offset_$pName", 0)
        }
        val tomorrowPrayers = com.example.data.prayer.PrayerCalculator.calculatePrayerTimesList(
            zone = zone,
            authority = auth,
            isHanafiAsr = isHanafi,
            date = java.time.LocalDate.now().plusDays(1),
            minuteOffsets = offsets
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        for (prayer in prayers) {
            val isEnabled = enabledMap[prayer.name] ?: (prayer.name != "Sunrise")
            val offsetMin = timersMap[prayer.name] ?: 0

            val mainRequestCode = getMainRequestCode(prayer.name)
            val preAlertRequestCode = getPreAlertRequestCode(prayer.name)

            // Main Adhan Intent (fires at exact prayer time, or positive delay if offset > 0)
            val mainOffset = if (offsetMin > 0) offsetMin else 0
            val mainIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = ACTION_PRAYER_ALARM
                putExtra(EXTRA_PRAYER_NAME, prayer.name)
                putExtra(EXTRA_TIME_FORMATTED, prayer.timeString)
                putExtra(EXTRA_OFFSET_MINUTES, mainOffset)
                putExtra(EXTRA_IS_PRE_ALERT, false)
            }

            val mainPendingIntent = PendingIntent.getBroadcast(
                context,
                mainRequestCode,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Pre-Alert Intent (fires at negative offset e.g. -15m before prayer time)
            val preAlertIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = ACTION_PRAYER_ALARM
                putExtra(EXTRA_PRAYER_NAME, prayer.name)
                putExtra(EXTRA_TIME_FORMATTED, prayer.timeString)
                putExtra(EXTRA_OFFSET_MINUTES, offsetMin)
                putExtra(EXTRA_IS_PRE_ALERT, true)
            }

            val preAlertPendingIntent = PendingIntent.getBroadcast(
                context,
                preAlertRequestCode,
                preAlertIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (!isEnabled) {
                alarmManager.cancel(mainPendingIntent)
                alarmManager.cancel(preAlertPendingIntent)
                continue
            }

            val now = java.util.Calendar.getInstance(tz)

            // 1. Schedule Main Alarm (Exact prayer time or delayed if offset > 0)
            val mainCal = java.util.Calendar.getInstance(tz).apply {
                set(java.util.Calendar.HOUR_OF_DAY, prayer.hour)
                set(java.util.Calendar.MINUTE, prayer.minute)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
                if (mainOffset > 0) {
                    add(java.util.Calendar.MINUTE, mainOffset)
                }
            }
            if (mainCal.before(now)) {
                val tomPrayer = tomorrowPrayers.find { it.name == prayer.name } ?: prayer
                mainCal.set(java.util.Calendar.HOUR_OF_DAY, tomPrayer.hour)
                mainCal.set(java.util.Calendar.MINUTE, tomPrayer.minute)
                mainCal.add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
            setAlarm(alarmManager, mainCal.timeInMillis, mainPendingIntent)

            // 2. Schedule Pre-Alert Alarm independently if negative offset is configured
            if (offsetMin < 0) {
                val preCal = java.util.Calendar.getInstance(tz).apply {
                    set(java.util.Calendar.HOUR_OF_DAY, prayer.hour)
                    set(java.util.Calendar.MINUTE, prayer.minute)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                    add(java.util.Calendar.MINUTE, offsetMin)
                }
                if (preCal.before(now)) {
                    val tomPrayer = tomorrowPrayers.find { it.name == prayer.name } ?: prayer
                    preCal.set(java.util.Calendar.HOUR_OF_DAY, tomPrayer.hour)
                    preCal.set(java.util.Calendar.MINUTE, tomPrayer.minute)
                    preCal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    preCal.add(java.util.Calendar.MINUTE, offsetMin)
                }
                setAlarm(alarmManager, preCal.timeInMillis, preAlertPendingIntent)
            } else {
                // Pre-alert disabled for this prayer; cancel the slot
                alarmManager.cancel(preAlertPendingIntent)
            }
        }
    }

    private fun setAlarm(
        alarmManager: AlarmManager,
        triggerMillis: Long,
        pendingIntent: PendingIntent
    ) {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun scheduleSnooze(
        context: Context,
        prayerName: String,
        timeFormatted: String,
        snoozeMinutes: Int = 5
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val requestCode = getSnoozeRequestCode(prayerName)

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = ACTION_PRAYER_ALARM
            putExtra(EXTRA_PRAYER_NAME, prayerName)
            putExtra(EXTRA_TIME_FORMATTED, timeFormatted)
            putExtra(EXTRA_OFFSET_MINUTES, 0)
            putExtra(EXTRA_IS_PRE_ALERT, false)
            putExtra(EXTRA_IS_SNOOZE, true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerMillis = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)
        setAlarm(alarmManager, triggerMillis, pendingIntent)
    }

    fun cancelSnooze(context: Context, prayerName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val requestCode = getSnoozeRequestCode(prayerName)
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            action = ACTION_PRAYER_ALARM
            putExtra(EXTRA_PRAYER_NAME, prayerName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val allPrayers = listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha")
        for (prayer in allPrayers) {
            val mainCode = getMainRequestCode(prayer)
            val preCode = getPreAlertRequestCode(prayer)
            val snoozeCode = getSnoozeRequestCode(prayer)

            val pMain = PendingIntent.getBroadcast(
                context, mainCode,
                Intent(context, PrayerAlarmReceiver::class.java).apply { action = ACTION_PRAYER_ALARM },
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pMain?.let { alarmManager.cancel(it) }

            val pPre = PendingIntent.getBroadcast(
                context, preCode,
                Intent(context, PrayerAlarmReceiver::class.java).apply { action = ACTION_PRAYER_ALARM },
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pPre?.let { alarmManager.cancel(it) }

            val pSnooze = PendingIntent.getBroadcast(
                context, snoozeCode,
                Intent(context, PrayerAlarmReceiver::class.java).apply { action = ACTION_PRAYER_ALARM },
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pSnooze?.let { alarmManager.cancel(it) }
        }
    }

    /**
     * Resolves the next upcoming scheduled alarm for diagnostic display.
     */
    fun getNextScheduledAlarm(
        prayers: List<PrayerTime>,
        timersMap: Map<String, Int>,
        enabledMap: Map<String, Boolean>
    ): ScheduledAlarmInfo? {
        val now = Calendar.getInstance()
        var earliest: ScheduledAlarmInfo? = null
        var earliestMillis = Long.MAX_VALUE

        for (prayer in prayers) {
            val isEnabled = enabledMap[prayer.name] ?: (prayer.name != "Sunrise")
            if (!isEnabled) continue

            val offsetMin = timersMap[prayer.name] ?: 0

            // 1. Check Pre-Alert if offset is negative
            if (offsetMin < 0) {
                val preCal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, prayer.hour)
                    set(Calendar.MINUTE, prayer.minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    add(Calendar.MINUTE, offsetMin)
                }
                if (preCal.before(now)) {
                    preCal.add(Calendar.DAY_OF_YEAR, 1)
                }
                if (preCal.timeInMillis < earliestMillis) {
                    earliestMillis = preCal.timeInMillis
                    earliest = ScheduledAlarmInfo(
                        prayerName = prayer.name,
                        timeFormatted = prayer.timeString,
                        triggerMillis = preCal.timeInMillis,
                        isPreAlert = true,
                        offsetMinutes = offsetMin
                    )
                }
            }

            // 2. Check Main Alarm
            val mainOffset = if (offsetMin > 0) offsetMin else 0
            val mainCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, prayer.hour)
                set(Calendar.MINUTE, prayer.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (mainOffset > 0) {
                    add(Calendar.MINUTE, mainOffset)
                }
            }
            if (mainCal.before(now)) {
                mainCal.add(Calendar.DAY_OF_YEAR, 1)
            }
            if (mainCal.timeInMillis < earliestMillis) {
                earliestMillis = mainCal.timeInMillis
                earliest = ScheduledAlarmInfo(
                    prayerName = prayer.name,
                    timeFormatted = prayer.timeString,
                    triggerMillis = mainCal.timeInMillis,
                    isPreAlert = false,
                    offsetMinutes = mainOffset
                )
            }
        }
        return earliest
    }
}
