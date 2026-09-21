package com.example.data.backup

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.room.withTransaction
import com.example.data.local.AppDatabase
import com.example.data.local.DailyHabitEntity
import com.example.data.local.FastLogEntity
import com.example.data.local.FavoriteItemEntity
import com.example.data.local.HifzEventEntity
import com.example.data.local.KhatmaHistoryEntity
import com.example.data.local.KhatmaPlanEntity
import com.example.data.local.NoorDao
import com.example.data.local.PrayerRecordEntity
import com.example.data.local.QadaRecordEntity
import com.example.data.local.QuranBookmarkEntity
import com.example.data.local.QuranNoteEntity
import com.example.data.local.ReadingProgressEntity
import com.example.data.local.StreakDailyLogEntity
import com.example.data.local.StreakSummaryEntity
import com.example.data.local.TasbihRecordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupMetadata(
    val exportDate: String,
    val timestamp: Long,
    val khatmaCount: Int,
    val tasbihCount: Int,
    val favoritesCount: Int,
    val fastLogsCount: Int,
    val prayersCount: Int,
    val streakLogsCount: Int
)

data class ImportResult(
    val success: Boolean,
    val message: String,
    val itemsRestoredCount: Int = 0
)

object BackupManager {

    private val SETTINGS_WHITELIST = setOf(
        "app_home_theme",
        "shared_reading_theme",
        "quran_arabic_font",
        "quran_arabic_font_size",
        "is_mushaf_flow_mode",
        "is_quran_sepia_mode",
        "is_tajweed_enabled",
        "tajweed_btn_position",
        "show_arabic_in_azkar_cards",
        "azkar_text_size",
        "azkar_auto_scroll",
        "azkar_haptic",
        "azkar_transliteration",
        "azkar_benefits",
        "tasbih_haptic_enabled",
        "tasbih_sound_enabled",
        "tasbih_auto_reset",
        "tasbih_beads_visible",
        "hifz_masking_style",
        "hifz_silhouette_opacity",
        "hifz_recall_group_size",
        "hifz_delay_seconds",
        "hifz_loop_range",
        "hifz_audio_sync_reveal",
        "hifz_show_translation",
        "hifz_chunk_review_size",
        "hifz_word_pattern",
        "hifz_ayah_pattern",
        "hifz_drill_mode",
        "hifz_word_repeat_count",
        "memorization_repeat_count",
        "is_hide_unread_verses",
        "home_widgets_order",
        "home_widgets_visibility",
        "quick_access_tools",
        "selected_reciter_id",
        "salat_calculation_authority",
        "salat_asr_jurisdiction",
        "salat_selected_muezzin",
        "salat_manual_latitude",
        "salat_manual_longitude",
        "salat_manual_city_name",
        "morning_evening_azkar_notification",
        "daily_ayah_notification",
        "qaza_reminder_notification",
        "vibration_on_adhan"
    )

    private val USER_DATA_EXPLICIT_KEYS = setOf(
        "tasbih_total_all_time",
        "tasbih_laps_completed",
        "tasbih_target",
        "daily_quran_goal",
        "daily_dhikr_goal",
        "quran_ayahs_read_today",
        "quran_ayahs_read_today_date",
        "azkar_count_today",
        "azkar_count_today_date"
    )

    private fun isUserDataKey(key: String): Boolean {
        if (USER_DATA_EXPLICIT_KEYS.contains(key)) return true
        if (key == "hifz_memorized_ayahs_set") return true
        if (key.startsWith("hifz_confidence_")) return true
        if (key.startsWith("hifz_last_drilled_")) return true
        if (key.startsWith("tasbih_dhikr_")) return true
        return false
    }

    suspend fun generateBackupJson(
        context: Context,
        db: AppDatabase,
        userName: String,
        userEmail: String,
        userBio: String,
        appLanguage: String
    ): String = withContext(Dispatchers.IO) {
        val dao = db.noorDao()
        val root = JSONObject()
        root.put("app", "Al-Noor")
        root.put("version", 3)
        root.put("exportTimestamp", System.currentTimeMillis())
        root.put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()))

        // User Profile
        val userObj = JSONObject().apply {
            put("name", userName)
            put("email", userEmail)
            put("bio", userBio)
            put("language", appLanguage)
        }
        root.put("user_profile", userObj)

        // SharedPreferences export (Settings & User Data)
        val prefs = context.getSharedPreferences("noor_app_preferences", Context.MODE_PRIVATE)
        val allPrefs = prefs.all

        val settingsObj = JSONObject()
        val userDataObj = JSONObject()

        for ((key, value) in allPrefs) {
            if (SETTINGS_WHITELIST.contains(key)) {
                when (value) {
                    is Boolean -> settingsObj.put(key, value)
                    is Int -> settingsObj.put(key, value)
                    is Long -> settingsObj.put(key, value)
                    is Float -> settingsObj.put(key, value.toDouble())
                    is String -> settingsObj.put(key, value)
                }
            } else if (isUserDataKey(key)) {
                when (value) {
                    is Boolean -> userDataObj.put(key, value)
                    is Int -> userDataObj.put(key, value)
                    is Long -> userDataObj.put(key, value)
                    is Float -> userDataObj.put(key, value.toDouble())
                    is String -> userDataObj.put(key, value)
                    is Set<*> -> {
                        val arr = JSONArray()
                        value.filterIsInstance<String>().forEach { arr.put(it) }
                        userDataObj.put(key, arr)
                    }
                }
            }
        }
        root.put("settings", settingsObj)
        root.put("user_data", userDataObj)

        // Reading Progress
        val readingProg = dao.getReadingProgress().firstOrNull()
        if (readingProg != null) {
            val rpObj = JSONObject().apply {
                put("surahNumber", readingProg.surahNumber)
                put("surahName", readingProg.surahName)
                put("ayahNumber", readingProg.ayahNumber)
                put("totalAyahs", readingProg.totalAyahs)
                put("updatedAt", readingProg.updatedAt)
            }
            root.put("reading_progress", rpObj)
        }

        // Active Khatma Plan
        val activeKhatma = dao.getActiveKhatmaPlanOnce()
        if (activeKhatma != null) {
            val kpObj = JSONObject().apply {
                put("title", activeKhatma.title)
                put("totalDays", activeKhatma.totalDays)
                put("startEpochDay", activeKhatma.startEpochDay)
                put("targetEndEpochDay", activeKhatma.targetEndEpochDay)
                put("dailySessionsCount", activeKhatma.dailySessionsCount)
                put("reminderEnabled", activeKhatma.reminderEnabled)
                put("reminderTime", activeKhatma.reminderTime)
                put("totalAyahs", activeKhatma.totalAyahs)
                put("readAyahsCount", activeKhatma.readAyahsCount)
                put("lastReadSurah", activeKhatma.lastReadSurah)
                put("lastReadAyah", activeKhatma.lastReadAyah)
                put("isCompleted", activeKhatma.isCompleted)
                put("completedAtEpochDay", activeKhatma.completedAtEpochDay ?: -1L)
                put("daysTaken", activeKhatma.daysTaken ?: -1)
                put("paceAdjustmentType", activeKhatma.paceAdjustmentType)
                put("completedSessionsTodayBitmask", activeKhatma.completedSessionsTodayBitmask)
                put("lastSessionDateDay", activeKhatma.lastSessionDateDay)
                put("createdAt", activeKhatma.createdAt)
                put("updatedAt", activeKhatma.updatedAt)
            }
            root.put("khatma_plan", kpObj)
        }

        // Khatma History
        val khatmaHistory = dao.getAllKhatmaHistory().firstOrNull() ?: emptyList()
        val khArray = JSONArray()
        for (kh in khatmaHistory) {
            val khObj = JSONObject().apply {
                put("title", kh.title)
                put("totalDays", kh.totalDays)
                put("daysTaken", kh.daysTaken)
                put("totalAyahsRead", kh.totalAyahsRead)
                put("startDateFormatted", kh.startDateFormatted)
                put("completionDateFormatted", kh.completionDateFormatted)
                put("completedAtTimestamp", kh.completedAtTimestamp)
            }
            khArray.put(khObj)
        }
        root.put("khatma_history", khArray)

        // Tasbih Records
        val tasbihList = db.tasbihDao().getAllTasbihRecords().firstOrNull() ?: emptyList()
        val tasbihArray = JSONArray()
        for (t in tasbihList) {
            val tObj = JSONObject().apply {
                put("dhikrName", t.dhikrName)
                put("currentCount", t.currentCount)
                put("targetCount", t.targetCount)
                put("totalAllTime", t.totalAllTime)
            }
            tasbihArray.put(tObj)
        }
        root.put("tasbih_records", tasbihArray)

        // Favorites
        val favList = dao.getAllFavorites().firstOrNull() ?: emptyList()
        val favArray = JSONArray()
        for (f in favList) {
            val fObj = JSONObject().apply {
                put("type", f.type)
                put("title", f.title)
                put("arabicText", f.arabicText)
                put("translation", f.translation)
                put("source", f.source)
                put("createdAt", f.createdAt)
            }
            favArray.put(fObj)
        }
        root.put("favorites", favArray)

        // Fasting Logs
        val fastList = dao.getAllFastLogs().firstOrNull() ?: emptyList()
        val fastArray = JSONArray()
        for (fast in fastList) {
            val fastObj = JSONObject().apply {
                put("type", fast.type)
                put("title", fast.title)
                put("subtitle", fast.subtitle)
                put("isCompleted", fast.isCompleted)
                put("completedDateIso", fast.completedDateIso)
                put("note", fast.note)
                put("createdAt", fast.createdAt)
            }
            fastArray.put(fastObj)
        }
        root.put("fast_logs", fastArray)

        // Qada Records
        val qadaList = db.prayerDao().getAllQadaRecords().firstOrNull() ?: emptyList()
        val qadaArray = JSONArray()
        for (q in qadaList) {
            val qObj = JSONObject().apply {
                put("prayerType", q.prayerType)
                put("totalMissed", q.totalMissed)
                put("completedMadeUp", q.completedMadeUp)
                put("updatedAt", q.updatedAt)
            }
            qadaArray.put(qObj)
        }
        root.put("qada_records", qadaArray)

        // Prayer Records
        val prayerRecords = db.prayerDao().getAllPrayerRecords().firstOrNull() ?: emptyList()
        val prayerArray = JSONArray()
        for (pr in prayerRecords) {
            val prObj = JSONObject().apply {
                put("date", pr.date)
                put("prayerName", pr.prayerName)
                put("isCompleted", pr.isCompleted)
                put("completedAt", pr.completedAt)
            }
            prayerArray.put(prObj)
        }
        root.put("prayer_records", prayerArray)

        // Quran Bookmarks
        val bookmarks = db.quranBookmarkDao().getAllBookmarks().firstOrNull() ?: emptyList()
        val bmArray = JSONArray()
        for (bm in bookmarks) {
            val bmObj = JSONObject().apply {
                put("surahNumber", bm.surahNumber)
                put("verseNumber", bm.verseNumber)
                put("surahName", bm.surahName)
                put("surahNameArabic", bm.surahNameArabic)
                put("arabicText", bm.arabicText)
                put("translation", bm.translation)
                put("note", bm.note)
                put("colorTag", bm.colorTag)
                put("juz", bm.juz)
                put("page", bm.page)
                put("createdAt", bm.createdAt)
            }
            bmArray.put(bmObj)
        }
        root.put("quran_bookmarks", bmArray)

        // Quran Notes
        val notes = dao.getAllQuranNotes().firstOrNull() ?: emptyList()
        val notesArray = JSONArray()
        for (n in notes) {
            val nObj = JSONObject().apply {
                put("surahNumber", n.surahNumber)
                put("verseNumber", n.verseNumber)
                put("noteText", n.noteText)
                put("updatedAt", n.updatedAt)
            }
            notesArray.put(nObj)
        }
        root.put("quran_notes", notesArray)

        // Daily Habits
        val habits = dao.getAllHabits().firstOrNull() ?: emptyList()
        val habitArray = JSONArray()
        for (h in habits) {
            val hObj = JSONObject().apply {
                put("title", h.title)
                put("targetCount", h.targetCount)
                put("currentCount", h.currentCount)
                put("isCompleted", h.isCompleted)
                put("category", h.category)
                put("iconType", h.iconType)
                if (h.completedDateIso != null) {
                    put("completedDateIso", h.completedDateIso)
                }
            }
            habitArray.put(hObj)
        }
        root.put("daily_habits", habitArray)

        // Hifz Events
        val hifzEvents = dao.getAllHifzEventsOnce()
        val hifzArray = JSONArray()
        for (he in hifzEvents) {
            val heObj = JSONObject().apply {
                put("surahNumber", he.surahNumber)
                put("ayahNumber", he.ayahNumber)
                put("type", he.type)
                put("mode", he.mode)
                put("timestampUtcMs", he.timestampUtcMs)
            }
            hifzArray.put(heObj)
        }
        root.put("hifz_events", hifzArray)

        // Streak Daily Logs
        val streakLogs = dao.getAllStreakDailyLogs().firstOrNull() ?: emptyList()
        val streakArray = JSONArray()
        for (s in streakLogs) {
            val sObj = JSONObject().apply {
                put("date", s.date)
                put("salatCompleted", s.salatCompleted)
                put("quranCompleted", s.quranCompleted)
                put("azkarCompleted", s.azkarCompleted)
                put("duaCompleted", s.duaCompleted)
                put("tasbihCompleted", s.tasbihCompleted)
                put("isFreezeUsed", s.isFreezeUsed)
                put("isExcused", s.isExcused)
                put("excuseReason", s.excuseReason)
                put("updatedAt", s.updatedAt)
            }
            streakArray.put(sObj)
        }
        root.put("streak_daily_logs", streakArray)

        // Streak Summary
        val streakSummary = dao.getStreakSummaryOnce()
        if (streakSummary != null) {
            val ssObj = JSONObject().apply {
                put("freezesRemaining", streakSummary.freezesRemaining)
                put("lastMonthReset", streakSummary.lastMonthReset)
                put("longestStreakEver", streakSummary.longestStreakEver)
                put("updatedAt", streakSummary.updatedAt)
            }
            root.put("streak_summary", ssObj)
        }

        root.toString(2)
    }

    suspend fun restoreFromJson(
        context: Context,
        jsonString: String,
        db: AppDatabase,
        onProfileRestored: (name: String, email: String, bio: String, language: String) -> Unit
    ): ImportResult = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString.trim())
            val dao = db.noorDao()
            val prefs = context.getSharedPreferences("noor_app_preferences", Context.MODE_PRIVATE)
            var count = 0

            // All DB operations run inside ONE Room transaction
            db.withTransaction {
                // Restore User Profile
                if (root.has("user_profile")) {
                    val u = root.getJSONObject("user_profile")
                    val name = u.optString("name", "")
                    val email = u.optString("email", "")
                    val bio = u.optString("bio", "")
                    val lang = u.optString("language", "")
                    if (name.isNotBlank() || email.isNotBlank()) {
                        onProfileRestored(name, email, bio, lang)
                        count++
                    }
                }

                // Restore Settings (SharedPreferences)
                if (root.has("settings")) {
                    val settings = root.getJSONObject("settings")
                    val editor = prefs.edit()
                    val keys = settings.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        if (SETTINGS_WHITELIST.contains(k)) {
                            val valObj = settings.get(k)
                            when (valObj) {
                                is Boolean -> editor.putBoolean(k, valObj)
                                is Int -> editor.putInt(k, valObj)
                                is Long -> editor.putLong(k, valObj)
                                is Double -> editor.putFloat(k, valObj.toFloat())
                                is String -> editor.putString(k, valObj)
                            }
                        }
                    }
                    editor.apply()
                    count++
                }

                // Restore User Data (SharedPreferences)
                if (root.has("user_data")) {
                    val userData = root.getJSONObject("user_data")
                    val editor = prefs.edit()
                    val keys = userData.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        if (isUserDataKey(k)) {
                            val valObj = userData.get(k)
                            when (valObj) {
                                is Boolean -> editor.putBoolean(k, valObj)
                                is Int -> editor.putInt(k, valObj)
                                is Long -> editor.putLong(k, valObj)
                                is Double -> editor.putFloat(k, valObj.toFloat())
                                is String -> editor.putString(k, valObj)
                                is JSONArray -> {
                                    val stringSet = mutableSetOf<String>()
                                    for (idx in 0 until valObj.length()) {
                                        stringSet.add(valObj.getString(idx))
                                    }
                                    editor.putStringSet(k, stringSet)
                                }
                            }
                        }
                    }
                    editor.apply()
                    count++
                }

                // Restore Reading Progress
                if (root.has("reading_progress")) {
                    val rp = root.getJSONObject("reading_progress")
                    dao.saveReadingProgress(
                        ReadingProgressEntity(
                            id = 1,
                            surahNumber = rp.getInt("surahNumber"),
                            surahName = rp.getString("surahName"),
                            ayahNumber = rp.getInt("ayahNumber"),
                            totalAyahs = rp.getInt("totalAyahs"),
                            updatedAt = rp.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                    count++
                }

                // Restore Active Khatma Plan
                if (root.has("khatma_plan")) {
                    val kp = root.getJSONObject("khatma_plan")
                    val completedEpochDay = kp.optLong("completedAtEpochDay", -1L)
                    val daysTaken = kp.optInt("daysTaken", -1)
                    val existing = dao.getActiveKhatmaPlanOnce()
                    val newUpdatedAt = kp.optLong("updatedAt", System.currentTimeMillis())
                    
                    // Merge: update only if backup is newer or no existing plan
                    if (existing == null || newUpdatedAt >= existing.updatedAt) {
                        dao.saveKhatmaPlan(
                            KhatmaPlanEntity(
                                id = 1,
                                title = kp.optString("title", "Personal Khatma"),
                                totalDays = kp.optInt("totalDays", 30),
                                startEpochDay = kp.optLong("startEpochDay", 0L),
                                targetEndEpochDay = kp.optLong("targetEndEpochDay", 30L),
                                dailySessionsCount = kp.optInt("dailySessionsCount", 3),
                                reminderEnabled = kp.optBoolean("reminderEnabled", true),
                                reminderTime = kp.optString("reminderTime", "07:00 AM"),
                                totalAyahs = kp.optInt("totalAyahs", 6236),
                                readAyahsCount = kp.optInt("readAyahsCount", 0),
                                lastReadSurah = kp.optInt("lastReadSurah", 1),
                                lastReadAyah = kp.optInt("lastReadAyah", 1),
                                isCompleted = kp.optBoolean("isCompleted", false),
                                completedAtEpochDay = if (completedEpochDay > 0) completedEpochDay else null,
                                daysTaken = if (daysTaken > 0) daysTaken else null,
                                paceAdjustmentType = kp.optString("paceAdjustmentType", "SPREAD"),
                                completedSessionsTodayBitmask = kp.optInt("completedSessionsTodayBitmask", 0),
                                lastSessionDateDay = kp.optLong("lastSessionDateDay", 0L),
                                createdAt = kp.optLong("createdAt", System.currentTimeMillis()),
                                updatedAt = newUpdatedAt
                            )
                        )
                        count++
                    }
                }

                // Restore Khatma History (Dedupe by natural key: title + startDateFormatted + completionDateFormatted)
                if (root.has("khatma_history")) {
                    val khArr = root.getJSONArray("khatma_history")
                    val existingHistory = dao.getAllKhatmaHistory().firstOrNull() ?: emptyList()
                    for (i in 0 until khArr.length()) {
                        val kh = khArr.getJSONObject(i)
                        val title = kh.optString("title", "Khatma")
                        val startFmt = kh.optString("startDateFormatted", "")
                        val compFmt = kh.optString("completionDateFormatted", "")

                        val isDuplicate = existingHistory.any {
                            it.title == title && it.startDateFormatted == startFmt && it.completionDateFormatted == compFmt
                        }
                        if (!isDuplicate) {
                            dao.insertKhatmaHistory(
                                KhatmaHistoryEntity(
                                    id = 0,
                                    title = title,
                                    totalDays = kh.optInt("totalDays", 30),
                                    daysTaken = kh.optInt("daysTaken", 30),
                                    totalAyahsRead = kh.optInt("totalAyahsRead", 6236),
                                    startDateFormatted = startFmt,
                                    completionDateFormatted = compFmt,
                                    completedAtTimestamp = kh.optLong("completedAtTimestamp", System.currentTimeMillis())
                                )
                            )
                            count++
                        }
                    }
                }

                // Restore Tasbih Records
                if (root.has("tasbih_records")) {
                    val tArr = root.getJSONArray("tasbih_records")
                    for (i in 0 until tArr.length()) {
                        val t = tArr.getJSONObject(i)
                        val name = t.getString("dhikrName")
                        val existing = db.tasbihDao().getTasbihRecord(name)
                        
                        val newCount = t.optInt("currentCount", 0)
                        val newTarget = t.optInt("targetCount", 33)
                        val newTotalAllTime = t.optInt("totalAllTime", 0)

                        db.tasbihDao().saveTasbihRecord(
                            TasbihRecordEntity(
                                dhikrName = name,
                                currentCount = if (existing != null) maxOf(existing.currentCount, newCount) else newCount,
                                targetCount = newTarget,
                                totalAllTime = if (existing != null) maxOf(existing.totalAllTime, newTotalAllTime) else newTotalAllTime
                            )
                        )
                        count++
                    }
                }

                // Restore Favorites (Dedupe by natural key: type + title + createdAt)
                if (root.has("favorites")) {
                    val fArr = root.getJSONArray("favorites")
                    val existingFavs = dao.getAllFavorites().firstOrNull() ?: emptyList()
                    for (i in 0 until fArr.length()) {
                        val f = fArr.getJSONObject(i)
                        val type = f.optString("type", "AYAH")
                        val title = f.optString("title", "")
                        val createdAt = f.optLong("createdAt", System.currentTimeMillis())

                        val isDuplicate = existingFavs.any {
                            it.type == type && it.title == title && it.createdAt == createdAt
                        }
                        if (!isDuplicate) {
                            dao.insertFavorite(
                                FavoriteItemEntity(
                                    id = 0,
                                    type = type,
                                    title = title,
                                    arabicText = f.optString("arabicText", ""),
                                    translation = f.optString("translation", ""),
                                    source = f.optString("source", ""),
                                    createdAt = createdAt
                                )
                            )
                            count++
                        }
                    }
                }

                // Restore Fasting Logs (Dedupe by natural key: type + title + createdAt)
                if (root.has("fast_logs")) {
                    val fastArr = root.getJSONArray("fast_logs")
                    val existingFasts = dao.getAllFastLogs().firstOrNull() ?: emptyList()
                    for (i in 0 until fastArr.length()) {
                        val fast = fastArr.getJSONObject(i)
                        val type = fast.optString("type", "VOLUNTARY")
                        val title = fast.optString("title", "Fast")
                        val createdAt = fast.optLong("createdAt", System.currentTimeMillis())

                        val isDuplicate = existingFasts.any {
                            it.type == type && it.title == title && it.createdAt == createdAt
                        }
                        if (!isDuplicate) {
                            dao.insertFastLog(
                                FastLogEntity(
                                    id = 0,
                                    type = type,
                                    title = title,
                                    subtitle = fast.optString("subtitle", ""),
                                    isCompleted = fast.optBoolean("isCompleted", false),
                                    completedDateIso = fast.optString("completedDateIso", ""),
                                    note = fast.optString("note", ""),
                                    createdAt = createdAt
                                )
                            )
                            count++
                        }
                    }
                }

                // Restore Qada Records
                if (root.has("qada_records")) {
                    val qArr = root.getJSONArray("qada_records")
                    for (i in 0 until qArr.length()) {
                        val q = qArr.getJSONObject(i)
                        val pType = q.getString("prayerType")
                        val existing = db.prayerDao().getQadaRecord(pType)
                        val newMissed = q.optInt("totalMissed", 0)
                        val newMadeUp = q.optInt("completedMadeUp", 0)
                        val newUpdatedAt = q.optLong("updatedAt", System.currentTimeMillis())

                        if (existing == null || newUpdatedAt >= existing.updatedAt) {
                            db.prayerDao().saveQadaRecord(
                                QadaRecordEntity(
                                    prayerType = pType,
                                    totalMissed = newMissed,
                                    completedMadeUp = newMadeUp,
                                    updatedAt = newUpdatedAt
                                )
                            )
                            count++
                        }
                    }
                }

                // Restore Prayer Records
                if (root.has("prayer_records")) {
                    val prArr = root.getJSONArray("prayer_records")
                    for (i in 0 until prArr.length()) {
                        val pr = prArr.getJSONObject(i)
                        val date = pr.getString("date")
                        val prayerName = pr.getString("prayerName")
                        val isComp = pr.optBoolean("isCompleted", false)
                        val compAt = pr.optLong("completedAt", System.currentTimeMillis())

                        val existing = db.prayerDao().getPrayerRecord(date, prayerName)
                        if (existing == null || compAt >= existing.completedAt) {
                            db.prayerDao().insertOrUpdatePrayer(
                                PrayerRecordEntity(
                                    date = date,
                                    prayerName = prayerName,
                                    isCompleted = isComp,
                                    completedAt = compAt
                                )
                            )
                            count++
                        }
                    }
                }

                // Restore Quran Bookmarks (Dedupe by natural key: surahNumber + verseNumber)
                if (root.has("quran_bookmarks")) {
                    val bmArr = root.getJSONArray("quran_bookmarks")
                    for (i in 0 until bmArr.length()) {
                        val bm = bmArr.getJSONObject(i)
                        val surah = bm.getInt("surahNumber")
                        val verse = bm.getInt("verseNumber")

                        val existing = db.quranBookmarkDao().getBookmarkOnce(surah, verse)
                        if (existing == null) {
                            db.quranBookmarkDao().insertBookmark(
                                QuranBookmarkEntity(
                                    id = 0,
                                    surahNumber = surah,
                                    verseNumber = verse,
                                    surahName = bm.optString("surahName", ""),
                                    surahNameArabic = bm.optString("surahNameArabic", ""),
                                    arabicText = bm.optString("arabicText", ""),
                                    translation = bm.optString("translation", ""),
                                    note = bm.optString("note", ""),
                                    colorTag = bm.optString("colorTag", "gold"),
                                    juz = bm.optInt("juz", 1),
                                    page = bm.optInt("page", 1),
                                    createdAt = bm.optLong("createdAt", System.currentTimeMillis())
                                )
                            )
                            count++
                        }
                    }
                }

                // Restore Quran Notes
                if (root.has("quran_notes")) {
                    val notesArr = root.getJSONArray("quran_notes")
                    for (i in 0 until notesArr.length()) {
                        val n = notesArr.getJSONObject(i)
                        val surah = n.getInt("surahNumber")
                        val verse = n.getInt("verseNumber")
                        val text = n.optString("noteText", "")
                        val updAt = n.optLong("updatedAt", System.currentTimeMillis())

                        val existing = dao.getNoteForVerse(surah, verse)
                        if (existing == null || updAt >= existing.updatedAt) {
                            dao.saveNote(
                                QuranNoteEntity(
                                    surahNumber = surah,
                                    verseNumber = verse,
                                    noteText = text,
                                    updatedAt = updAt
                                )
                            )
                            count++
                        }
                    }
                }

                // Restore Daily Habits (Dedupe by natural key: title + category)
                if (root.has("daily_habits")) {
                    val habitArr = root.getJSONArray("daily_habits")
                    val existingHabits = dao.getAllHabits().firstOrNull() ?: emptyList()
                    for (i in 0 until habitArr.length()) {
                        val h = habitArr.getJSONObject(i)
                        val title = h.optString("title", "")
                        val category = h.optString("category", "General")

                        val isDuplicate = existingHabits.any {
                            it.title == title && it.category == category
                        }
                        if (!isDuplicate) {
                            val completedDateIso = if (h.has("completedDateIso") && !h.isNull("completedDateIso")) {
                                h.optString("completedDateIso").ifBlank { null }
                            } else null
                            dao.insertHabit(
                                DailyHabitEntity(
                                    id = 0,
                                    title = title,
                                    targetCount = h.optInt("targetCount", 1),
                                    currentCount = h.optInt("currentCount", 0),
                                    isCompleted = h.optBoolean("isCompleted", false),
                                    category = category,
                                    iconType = h.optString("iconType", "star"),
                                    completedDateIso = completedDateIso
                                )
                            )
                            count++
                        }
                    }
                }

                // Restore Hifz Events (Dedupe by natural key: surahNumber + ayahNumber + timestampUtcMs + type)
                if (root.has("hifz_events")) {
                    val hifzArr = root.getJSONArray("hifz_events")
                    val existingEvents = dao.getAllHifzEventsOnce()
                    for (i in 0 until hifzArr.length()) {
                        val he = hifzArr.getJSONObject(i)
                        val surah = he.getInt("surahNumber")
                        val ayah = he.getInt("ayahNumber")
                        val ts = he.optLong("timestampUtcMs", System.currentTimeMillis())
                        val type = he.optString("type", "MARKED")
                        val mode = he.optString("mode", "MEMORIZED")

                        val isDuplicate = existingEvents.any {
                            it.surahNumber == surah && it.ayahNumber == ayah && it.timestampUtcMs == ts && it.type == type
                        }
                        if (!isDuplicate) {
                            dao.insertHifzEvent(
                                HifzEventEntity(
                                    id = 0,
                                    surahNumber = surah,
                                    ayahNumber = ayah,
                                    type = type,
                                    mode = mode,
                                    timestampUtcMs = ts
                                )
                            )
                            count++
                        }
                    }
                }

                // Restore Streak Logs
                if (root.has("streak_daily_logs")) {
                    val sArr = root.getJSONArray("streak_daily_logs")
                    for (i in 0 until sArr.length()) {
                        val s = sArr.getJSONObject(i)
                        val date = s.getString("date")
                        val updAt = s.optLong("updatedAt", System.currentTimeMillis())
                        val existing = dao.getStreakDailyLog(date)

                        if (existing == null || updAt >= existing.updatedAt) {
                            dao.saveStreakDailyLog(
                                StreakDailyLogEntity(
                                    date = date,
                                    salatCompleted = s.optBoolean("salatCompleted", false),
                                    quranCompleted = s.optBoolean("quranCompleted", false),
                                    azkarCompleted = s.optBoolean("azkarCompleted", false),
                                    duaCompleted = s.optBoolean("duaCompleted", false),
                                    tasbihCompleted = s.optBoolean("tasbihCompleted", false),
                                    isFreezeUsed = s.optBoolean("isFreezeUsed", false),
                                    isExcused = s.optBoolean("isExcused", false),
                                    excuseReason = s.optString("excuseReason", ""),
                                    updatedAt = updAt
                                )
                            )
                            count++
                        }
                    }
                }

                // Restore Streak Summary
                if (root.has("streak_summary")) {
                    val ss = root.getJSONObject("streak_summary")
                    val updAt = ss.optLong("updatedAt", System.currentTimeMillis())
                    val existing = dao.getStreakSummaryOnce()

                    if (existing == null || updAt >= existing.updatedAt) {
                        dao.saveStreakSummary(
                            StreakSummaryEntity(
                                id = 1,
                                freezesRemaining = ss.optInt("freezesRemaining", 2),
                                lastMonthReset = ss.optString("lastMonthReset", "2026-08"),
                                longestStreakEver = ss.optInt("longestStreakEver", 0),
                                updatedAt = updAt
                            )
                        )
                        count++
                    }
                }
            }

            ImportResult(
                success = true,
                message = "Successfully restored $count spiritual data records.",
                itemsRestoredCount = count
            )
        } catch (e: Exception) {
            ImportResult(
                success = false,
                message = "Invalid backup format: ${e.localizedMessage ?: "Unknown error"}",
                itemsRestoredCount = 0
            )
        }
    }

    fun shareBackup(context: Context, backupJson: String) {
        val dateStr = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
        val fileName = "alnoor_backup_$dateStr.json"
        
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Al-Noor Spiritual Backup - $dateStr")
            putExtra(Intent.EXTRA_TEXT, backupJson)
        }
        val chooser = Intent.createChooser(sendIntent, "Export & Backup Al-Noor Data via (Drive, Email, Files)")
        context.startActivity(chooser)
    }

    fun copyToClipboard(context: Context, backupJson: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Al-Noor Spiritual Backup", backupJson))
    }
}
