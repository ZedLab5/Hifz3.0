package com.example.data.prayer

import android.app.AlarmManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.example.data.model.PrayerTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Handles device-level background reliability, OEM autostart deep-linking,
 * battery optimization exemptions, and prayer notification diagnostics.
 */
object AlarmReliabilityHelper {

    private const val PREFS_NAME = "noor_reliability_prefs"
    private const val KEY_BATTERY_PROMPTED = "battery_optimization_prompted"

    enum class DeviceManufacturer(
        val displayName: String,
        val brandTag: String,
        val issueDescription: String,
        val steps: List<String>
    ) {
        XIAOMI(
            displayName = "Xiaomi / Redmi / POCO",
            brandTag = "MIUI / HyperOS",
            issueDescription = "MIUI & HyperOS aggressively kill background alarms and silence punctual Adhans during screen lock unless Autostart and No Restrictions are enabled.",
            steps = listOf(
                "Turn ON 'Autostart' for Noor in App Info.",
                "In 'Battery saver', choose 'No restrictions' instead of MIUI Battery Saver.",
                "Lock Noor in the Recent Apps screen so the system never terminates it."
            )
        ),
        SAMSUNG(
            displayName = "Samsung Galaxy",
            brandTag = "One UI",
            issueDescription = "Samsung One UI places active background apps into 'Sleeping' or 'Deep sleeping' states, delaying exact prayer alerts.",
            steps = listOf(
                "Open Device Care > Battery > Background usage limits.",
                "Ensure Noor is NOT in 'Sleeping apps' or 'Deep sleeping apps'.",
                "Add Noor to 'Never sleeping apps'.",
                "In App Info > Battery, choose 'Unrestricted' instead of 'Optimized'."
            )
        ),
        OPPO(
            displayName = "Oppo / Realme / OnePlus",
            brandTag = "ColorOS / OxygenOS",
            issueDescription = "ColorOS and OxygenOS terminate background prayer services and delay exact alarms when screen is turned off.",
            steps = listOf(
                "Open App Info > Battery usage > Turn ON 'Allow background activity'.",
                "Turn ON 'Allow auto-launch' and 'Allow secondary launch'.",
                "In Settings > Battery > Optimize battery use, set Noor to 'Don't optimize'."
            )
        ),
        VIVO(
            displayName = "Vivo / iQOO",
            brandTag = "Funtouch OS / OriginOS",
            issueDescription = "Funtouch OS freezes background alarms unless 'High background power consumption' is specifically allowed.",
            steps = listOf(
                "Open Settings / iManager > Battery > Background power consumption management.",
                "Find Noor and choose 'High background power consumption' (Allow background power).",
                "Open Settings > Apps > Autostart and enable Noor."
            )
        ),
        HUAWEI(
            displayName = "Huawei / Honor",
            brandTag = "EMUI / MagicOS",
            issueDescription = "EMUI aggressively manages background apps and blocks exact alarm audio unless manual app launch is enabled.",
            steps = listOf(
                "Open Phone Manager / Settings > Battery > App launch.",
                "Find Noor, switch from 'Manage automatically' to 'Manage manually'.",
                "Turn ON all 3 options: 'Auto-launch', 'Secondary launch', and 'Run in background'."
            )
        ),
        OTHER(
            displayName = "Android Device",
            brandTag = "Stock Android",
            issueDescription = "Android Doze mode suspends background timers and can silence punctual prayer alarms unless battery optimization is disabled.",
            steps = listOf(
                "In App Info > App battery usage, select 'Unrestricted'.",
                "Ensure 'Alarms & reminders' exact alarm permission is enabled in App Info.",
                "Ensure notifications are turned on with high importance."
            )
        );

        companion object {
            fun detect(): DeviceManufacturer {
                val man = (Build.MANUFACTURER ?: "").lowercase()
                val brand = (Build.BRAND ?: "").lowercase()
                return when {
                    man.contains("xiaomi") || man.contains("redmi") || man.contains("poco") ||
                            brand.contains("xiaomi") || brand.contains("redmi") || brand.contains("poco") -> XIAOMI
                    man.contains("samsung") || brand.contains("samsung") -> SAMSUNG
                    man.contains("oppo") || man.contains("realme") || man.contains("oneplus") ||
                            brand.contains("oppo") || brand.contains("realme") || brand.contains("oneplus") -> OPPO
                    man.contains("vivo") || man.contains("iqoo") || brand.contains("vivo") || brand.contains("iqoo") -> VIVO
                    man.contains("huawei") || man.contains("honor") || brand.contains("huawei") || brand.contains("honor") -> HUAWEI
                    else -> OTHER
                }
            }
        }
    }

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            return powerManager?.isIgnoringBatteryOptimizations(context.packageName) == true
        }
        return true
    }

    fun requestIgnoreBatteryOptimizations(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback to general battery optimization screen
                try {
                    val fallbackIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(fallbackIntent)
                } catch (e2: Exception) {
                    openAppDetailsSettings(context)
                }
            }
        }
    }

    fun hasPromptedBatteryOptimization(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_BATTERY_PROMPTED, false)
    }

    fun setPromptedBatteryOptimization(context: Context, prompted: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_BATTERY_PROMPTED, prompted).apply()
    }

    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            return alarmManager?.canScheduleExactAlarms() == true
        }
        return true
    }

    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                openAppDetailsSettings(context)
            }
        }
    }

    fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun openNotificationSettings(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } else {
                openAppDetailsSettings(context)
            }
        } catch (e: Exception) {
            openAppDetailsSettings(context)
        }
    }

    fun openAppDetailsSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Attempts to launch manufacturer-specific autostart or power management activities.
     * Returns true if an OEM activity was launched, or false if fallen back to App Details.
     */
    fun openAutostartSettings(context: Context): Boolean {
        val oem = DeviceManufacturer.detect()
        val candidateIntents = mutableListOf<Intent>()

        when (oem) {
            DeviceManufacturer.XIAOMI -> {
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
                    )
                )
                candidateIntents.add(
                    Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT)
                )
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.miui.securitycenter", "com.miui.powercenter.PowerSettings")
                    )
                )
            }
            DeviceManufacturer.SAMSUNG -> {
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity")
                    )
                )
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity")
                    )
                )
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")
                    )
                )
            }
            DeviceManufacturer.OPPO -> {
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")
                    )
                )
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")
                    )
                )
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.oplus.battery", "com.oplus.battery.BatteryActivity")
                    )
                )
            }
            DeviceManufacturer.VIVO -> {
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")
                    )
                )
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")
                    )
                )
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")
                    )
                )
            }
            DeviceManufacturer.HUAWEI -> {
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")
                    )
                )
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")
                    )
                )
                candidateIntents.add(
                    Intent().setComponent(
                        ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")
                    )
                )
            }
            DeviceManufacturer.OTHER -> {
                // Stock android fallback handled below
            }
        }

        // Try candidate intents sequentially
        for (intent in candidateIntents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            } catch (e: Exception) {
                // Try next
            }
        }

        // Fallback: Open app details where user can tap Battery / Permissions
        openAppDetailsSettings(context)
        return false
    }

    data class DiagnosticItem(
        val key: String,
        val title: String,
        val statusText: String,
        val isPassed: Boolean,
        val isWarning: Boolean = false,
        val actionText: String? = null,
        val helpDetails: String,
        val onFix: (Context) -> Unit
    )

    data class DiagnosticReport(
        val items: List<DiagnosticItem>,
        val overallHealthy: Boolean,
        val detectedManufacturer: DeviceManufacturer,
        val isBatteryExempt: Boolean
    )

    fun runDiagnostics(
        context: Context,
        prayers: List<PrayerTime>,
        timersMap: Map<String, Int>,
        enabledMap: Map<String, Boolean>,
        alertTypes: Map<String, String>
    ): DiagnosticReport {
        val items = mutableListOf<DiagnosticItem>()

        // 1. Notification Permission
        val notifsEnabled = areNotificationsEnabled(context)
        items.add(
            DiagnosticItem(
                key = "notification_permission",
                title = "Notification Permission",
                statusText = if (notifsEnabled) "Granted" else "Disabled by System",
                isPassed = notifsEnabled,
                actionText = if (!notifsEnabled) "Enable" else null,
                helpDetails = if (notifsEnabled)
                    "Notifications are permitted. Android will deliver prayer banners to your lock screen."
                else
                    "Android is blocking notifications from Noor. Tap 'Enable' to grant permission in system settings.",
                onFix = { openNotificationSettings(it) }
            )
        )

        // 2. Exact Alarms Permission (Android 12+)
        val exactAlarmGranted = canScheduleExactAlarms(context)
        items.add(
            DiagnosticItem(
                key = "exact_alarms",
                title = "Exact Alarms & Timers",
                statusText = if (exactAlarmGranted) "Granted (Punctual)" else "Restricted",
                isPassed = exactAlarmGranted,
                actionText = if (!exactAlarmGranted) "Allow" else null,
                helpDetails = if (exactAlarmGranted)
                    "Exact alarms are allowed. The app can wake the device precisely at the calculated prayer second."
                else
                    "Exact alarm permission is revoked. Android will bundle or delay prayer alarms by up to 15+ minutes.",
                onFix = { openExactAlarmSettings(it) }
            )
        )

        // 3. Battery Optimization Status
        val isBatteryExempt = isIgnoringBatteryOptimizations(context)
        items.add(
            DiagnosticItem(
                key = "battery_optimization",
                title = "Battery Optimization (Doze Mode)",
                statusText = if (isBatteryExempt) "Unrestricted (Safe)" else "Optimized (Risk of Delay)",
                isPassed = isBatteryExempt,
                isWarning = !isBatteryExempt,
                actionText = if (!isBatteryExempt) "Exempt" else null,
                helpDetails = if (isBatteryExempt)
                    "Noor is exempted from battery restrictions. Alarms will fire even when the screen is idle for hours."
                else
                    "The system may silence or delay Adhan playback during deep sleep. Tap 'Exempt' to request unrestricted background execution.",
                onFix = { requestIgnoreBatteryOptimizations(it) }
            )
        )

        // 4. Next Scheduled Alarm
        val nextAlarm = PrayerAlarmScheduler.getNextScheduledAlarm(prayers, timersMap, enabledMap)
        if (nextAlarm != null) {
            val alertType = alertTypes[nextAlarm.prayerName] ?: "Adhan"
            val diffMs = nextAlarm.triggerMillis - System.currentTimeMillis()
            val diffMins = (diffMs / (60 * 1000L)).coerceAtLeast(0)
            val diffHours = diffMins / 60
            val remMins = diffMins % 60
            val timeRemainingStr = if (diffHours > 0) "${diffHours}h ${remMins}m" else "${remMins}m"

            val label = if (nextAlarm.isPreAlert) {
                "${nextAlarm.prayerName} Pre-alert (-${-nextAlarm.offsetMinutes}m)"
            } else {
                "${nextAlarm.prayerName} ($alertType)"
            }

            val isHealthy = alertType != "Mute"
            val timeFmt = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(nextAlarm.triggerMillis))

            items.add(
                DiagnosticItem(
                    key = "next_alarm",
                    title = "Next Scheduled Alarm",
                    statusText = if (isHealthy) "$label in $timeRemainingStr ($timeFmt)" else "$label is Muted",
                    isPassed = isHealthy,
                    isWarning = !isHealthy,
                    actionText = if (!isHealthy) "Unmute" else null,
                    helpDetails = if (isHealthy)
                        "An active AlarmManager wake-up timer is registered with Android for $label."
                    else
                        "Next upcoming prayer ($label) is set to Mute. Tap 'Unmute' to enable Adhan or silent notifications.",
                    onFix = {
                        // Handled via ViewModel callback or navigation
                    }
                )
            )
        } else {
            items.add(
                DiagnosticItem(
                    key = "next_alarm",
                    title = "Next Scheduled Alarm",
                    statusText = "No Active Alarms Scheduled",
                    isPassed = false,
                    actionText = "Reschedule",
                    helpDetails = "All prayer alarms are currently disabled or prayer times are empty. Tap to refresh.",
                    onFix = {
                        // Reschedule
                    }
                )
            )
        }

        // 5. Sound & Ringer Mode
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val ringerMode = audioManager?.ringerMode ?: AudioManager.RINGER_MODE_NORMAL
        val alarmVolume = audioManager?.getStreamVolume(AudioManager.STREAM_ALARM) ?: 5
        val maxAlarmVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_ALARM) ?: 7

        val isSoundOk = alarmVolume > 0
        val ringerText = when (ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> "Device in Silent Mode"
            AudioManager.RINGER_MODE_VIBRATE -> "Device in Vibrate Mode"
            else -> "Normal Sound Profile"
        }

        items.add(
            DiagnosticItem(
                key = "device_volume",
                title = "Alarm Volume & Ringer",
                statusText = "Alarm Volume: $alarmVolume/$maxAlarmVolume ($ringerText)",
                isPassed = isSoundOk,
                isWarning = (alarmVolume == 0 || ringerMode != AudioManager.RINGER_MODE_NORMAL),
                actionText = if (!isSoundOk) "Increase" else null,
                helpDetails = if (isSoundOk)
                    "Alarm audio stream is audible. Note that Adhan plays on the ALARM stream so it rings even when media is muted."
                else
                    "Your alarm audio volume is set to 0. You will not hear the Adhan audio when it triggers.",
                onFix = {
                    try {
                        val intent = Intent(Settings.ACTION_SOUND_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        it.startActivity(intent)
                    } catch (e: Exception) {
                        openAppDetailsSettings(it)
                    }
                }
            )
        )

        val overallHealthy = items.all { it.isPassed && !it.isWarning }
        val manufacturer = DeviceManufacturer.detect()

        return DiagnosticReport(
            items = items,
            overallHealthy = overallHealthy,
            detectedManufacturer = manufacturer,
            isBatteryExempt = isBatteryExempt
        )
    }
}
