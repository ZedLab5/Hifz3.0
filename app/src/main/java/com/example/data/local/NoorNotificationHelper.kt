package com.example.data.local

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.data.prayer.PrayerAlarmReceiver
import com.example.data.prayer.PrayerAlarmScheduler

object NoorNotificationHelper {

    const val CHANNEL_PRAYER_ALERTS = "noor_prayer_alerts_v2"
    const val CHANNEL_ATHKAR_REMINDERS = "noor_athkar_reminders_v2"
    const val CHANNEL_KHATMA_REMINDERS = "noor_khatma_reminders_v2"
    const val CHANNEL_HIFZ_REMINDERS = "noor_hifz_reminders_v2"
    const val CHANNEL_SUNNAH_REMINDERS = "noor_sunnah_reminders_v2"
    const val CHANNEL_STREAK_REMINDERS = "noor_streak_reminders_v2"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val prayerChannel = NotificationChannel(
                CHANNEL_PRAYER_ALERTS,
                "Prayer Times & Adhan Pre-alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Gentle alerts before and during prayer times."
                enableVibration(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }

            val athkarChannel = NotificationChannel(
                CHANNEL_ATHKAR_REMINDERS,
                "Daily Athkar & Sunnah Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Morning and Evening Athkar routines."
                enableVibration(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }

            val khatmaChannel = NotificationChannel(
                CHANNEL_KHATMA_REMINDERS,
                "Khatma Reading Goals",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Gentle daily Quran reading milestones and target reminders."
                enableVibration(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }

            val hifzChannel = NotificationChannel(
                CHANNEL_HIFZ_REMINDERS,
                "Quran Hifz & Memorization",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily memorization revision and review sessions."
                enableVibration(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }

            val sunnahChannel = NotificationChannel(
                CHANNEL_SUNNAH_REMINDERS,
                "Voluntary Prayers & Fasting",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Tahajjud, Duha, Sunnah Fasting, and Qaza prayer reminders."
                enableVibration(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }

            val streakChannel = NotificationChannel(
                CHANNEL_STREAK_REMINDERS,
                "Streak Safeguards & Consistency",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Spiritual habit tracking and daily consistency safeguards."
                enableVibration(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }

            notificationManager.createNotificationChannels(
                listOf(prayerChannel, athkarChannel, khatmaChannel, hifzChannel, sunnahChannel, streakChannel)
            )
        }
    }

    fun showPrayerAlert(
        context: Context,
        prayerName: String,
        timeFormatted: String,
        isPreAlert: Boolean = false,
        offsetMinutes: Int = 0
    ) {
        val title = if (isPreAlert) "Approaching Salat: $prayerName" else "Time for Salat: $prayerName"
        val content = if (isPreAlert) {
            val mins = if (offsetMinutes < 0) -offsetMinutes else 15
            "$prayerName will start in $mins minutes ($timeFormatted). Prepare your heart and wudu 🌿"
        } else {
            "Hayya 'ala as-Salah. $prayerName is now at $timeFormatted 🕌"
        }

        val baseId = 1001 + (prayerName.hashCode() and 0x7FFFFFFF) % 100
        val notificationId = if (isPreAlert) baseId + 1000 else baseId

        try {
            val contentIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val contentPendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_PRAYER_ALERTS)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)

            if (!isPreAlert) {
                // 1. "Stop" Action Broadcast
                val stopIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                    action = PrayerAlarmScheduler.ACTION_STOP_ADHAN
                    putExtra(PrayerAlarmScheduler.EXTRA_PRAYER_NAME, prayerName)
                    putExtra(PrayerAlarmScheduler.EXTRA_NOTIFICATION_ID, notificationId)
                }
                val stopPendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId * 10 + 1,
                    stopIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // 2. "Snooze 5 min" Action Broadcast
                val snoozeIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                    action = PrayerAlarmScheduler.ACTION_SNOOZE_ADHAN
                    putExtra(PrayerAlarmScheduler.EXTRA_PRAYER_NAME, prayerName)
                    putExtra(PrayerAlarmScheduler.EXTRA_TIME_FORMATTED, timeFormatted)
                    putExtra(PrayerAlarmScheduler.EXTRA_NOTIFICATION_ID, notificationId)
                }
                val snoozePendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId * 10 + 2,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // 3. Delete Intent (swiping away the notification stops playback)
                val deletePendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId * 10 + 3,
                    stopIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                builder.setDeleteIntent(deletePendingIntent)
                    .addAction(
                        android.R.drawable.ic_media_pause,
                        "Stop",
                        stopPendingIntent
                    )
                    .addAction(
                        android.R.drawable.ic_popup_reminder,
                        "Snooze 5 min",
                        snoozePendingIntent
                    )
            }

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Notification permission might not be granted yet on Android 13+
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelPrayerAlert(context: Context, notificationId: Int) {
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(notificationId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelPrayerAlerts(context: Context) {
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            for (id in 1000..1110) {
                notificationManager.cancel(id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showAthkarReminder(context: Context, type: String = "Morning") {
        val title = if (type.equals("Morning", ignoreCase = true)) "Morning Athkar • أذكار الصباح" else "Evening Athkar • أذكار المساء"
        val content = "Begin with the remembrance of Allah: 'Verily in the remembrance of Allah do hearts find rest' (13:28) 🕊️"

        showNotification(
            context = context,
            channelId = CHANNEL_ATHKAR_REMINDERS,
            notificationId = 2001,
            title = title,
            content = content
        )
    }

    fun showKhatmaReminder(context: Context, dailyAyahTarget: Int, surahName: String) {
        val title = "Daily Quran Goal • Khatma"
        val content = "Today's goal: $dailyAyahTarget Ayahs starting in $surahName. Keep your spiritual light shining ✨"

        showNotification(
            context = context,
            channelId = CHANNEL_KHATMA_REMINDERS,
            notificationId = 3001,
            title = title,
            content = content
        )
    }

    fun showSpiritualReminder(context: Context, reminderId: String) {
        val item = com.example.data.notifications.SpiritualReminderRepository.getItem(reminderId) ?: return
        val prefs = context.getSharedPreferences("noor_prefs", Context.MODE_PRIVATE)
        val appLang = prefs.getString("app_language", "English") ?: "English"
        val isArabic = appLang.equals("Arabic", ignoreCase = true) || appLang == "العربية" || appLang.startsWith("ar", ignoreCase = true)

        val title = if (isArabic) item.titleAr else item.titleEn
        val content = if (isArabic) item.descriptionAr else item.descriptionEn

        val channelId = when (item.category) {
            com.example.data.notifications.SpiritualReminderCategory.QURAN_HIFZ -> {
                if (item.id == com.example.data.notifications.SpiritualReminderRepository.ID_KHATMA_TARGET) {
                    CHANNEL_KHATMA_REMINDERS
                } else {
                    CHANNEL_HIFZ_REMINDERS
                }
            }
            com.example.data.notifications.SpiritualReminderCategory.DAILY_AZKAR -> CHANNEL_ATHKAR_REMINDERS
            com.example.data.notifications.SpiritualReminderCategory.VOLUNTARY_WORSHIP -> CHANNEL_SUNNAH_REMINDERS
            com.example.data.notifications.SpiritualReminderCategory.CONSISTENCY -> CHANNEL_STREAK_REMINDERS
        }

        val notificationId = 5000 + (item.id.hashCode() and 0x7FFFFFFF) % 1000

        showNotification(
            context = context,
            channelId = channelId,
            notificationId = notificationId,
            title = title,
            content = content,
            targetDestination = item.targetDestination.name
        )
    }

    fun showUniversalTestNotification(context: Context) {
        val prefs = context.getSharedPreferences("noor_prefs", Context.MODE_PRIVATE)
        val appLang = prefs.getString("app_language", "English") ?: "English"
        val isArabic = appLang.equals("Arabic", ignoreCase = true) || appLang == "العربية" || appLang.startsWith("ar", ignoreCase = true)

        val title = if (isArabic) "🔔 فحص إشعارات النظام الشامل" else "🔔 Universal System Notification Test"
        val content = if (isArabic) 
            "ممتاز! الإشعارات تعمل بشكل مثالي وتظهر على شاشة القفل الآن." 
            else "Perfect! System notifications are working flawlessly on your lockscreen."

        showNotification(
            context = context,
            channelId = CHANNEL_PRAYER_ALERTS,
            notificationId = 9999,
            title = title,
            content = content
        )
    }

    private fun showNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        content: String,
        targetDestination: String? = null
    ) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                if (targetDestination != null) {
                    putExtra("target_destination", targetDestination)
                }
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Notification permission might not be granted yet on Android 13+
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
