package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "AYAH", "DUA", "QUOTE", "DHIKR"
    val title: String,
    val arabicText: String,
    val translation: String,
    val source: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "prayer_records", primaryKeys = ["date", "prayerName"])
data class PrayerRecordEntity(
    val date: String, // YYYY-MM-DD
    val prayerName: String, // Fajr, Dhuhr, Asr, Maghrib, Isha
    val isCompleted: Boolean,
    val completedAt: Long
)

@Entity(tableName = "daily_habits")
data class DailyHabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetCount: Int,
    val currentCount: Int,
    val isCompleted: Boolean,
    val category: String, // "Quran", "Dhikr", "Salah", "Charity", "Knowledge"
    val iconType: String,
    val completedDateIso: String? = null // ISO date string (e.g. "2026-09-20") when habit was completed
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey
    val id: Int = 1,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val totalAyahs: Int,
    val updatedAt: Long
)

@Entity(tableName = "tasbih_records")
data class TasbihRecordEntity(
    @PrimaryKey
    val dhikrName: String,
    val currentCount: Int,
    val targetCount: Int,
    val totalAllTime: Int
)

@Entity(tableName = "khatma_plans")
data class KhatmaPlanEntity(
    @PrimaryKey
    val id: Long = 1, // Single active Khatma ID 1
    val title: String = "Personal Khatma",
    val totalDays: Int = 30,
    val startEpochDay: Long, // LocalDate.toEpochDay()
    val targetEndEpochDay: Long,
    val dailySessionsCount: Int = 3, // 1=Single, 2=Morning/Eve, 3=Morning/Afternoon/Eve, 5=5 Prayers
    val reminderEnabled: Boolean = true,
    val reminderTime: String = "07:00 AM",
    val totalAyahs: Int = 6236,
    val readAyahsCount: Int = 0, // 0..6236
    val lastReadSurah: Int = 1,
    val lastReadAyah: Int = 1,
    val isCompleted: Boolean = false,
    val completedAtEpochDay: Long? = null,
    val daysTaken: Int? = null,
    val paceAdjustmentType: String = "SPREAD", // "GRADUAL", "SPREAD", "EXTEND"
    val completedSessionsTodayBitmask: Int = 0, // Bitmask for sessions completed today
    val lastSessionDateDay: Long = 0, // The epoch day of lastSession update
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "khatma_history")
data class KhatmaHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val totalDays: Int,
    val daysTaken: Int,
    val totalAyahsRead: Int = 6236,
    val startDateFormatted: String,
    val completionDateFormatted: String,
    val completedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "qada_records")
data class QadaRecordEntity(
    @PrimaryKey
    val prayerType: String, // "Fajr", "Dhuhr", "Asr", "Maghrib", "Isha", "Witr"
    val totalMissed: Int = 0,
    val completedMadeUp: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "fast_logs")
data class FastLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "RAMADAN_MAKEUP", "MONDAY_THURSDAY", "WHITE_DAYS", "ASHURA", "ARAFAH", "SHAWWAL", "VOLUNTARY"
    val title: String,
    val subtitle: String = "",
    val isCompleted: Boolean = false,
    val completedDateIso: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "streak_daily_logs")
data class StreakDailyLogEntity(
    @PrimaryKey
    val date: String, // "YYYY-MM-DD"
    val salatCompleted: Boolean = false,
    val quranCompleted: Boolean = false,
    val azkarCompleted: Boolean = false,
    val duaCompleted: Boolean = false,
    val tasbihCompleted: Boolean = false,
    val isFreezeUsed: Boolean = false,
    val isExcused: Boolean = false,
    val excuseReason: String = "", // e.g. "Travel", "Illness", "Menstruation", "Personal Exemption"
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "streak_summary")
data class StreakSummaryEntity(
    @PrimaryKey
    val id: Int = 1,
    val freezesRemaining: Int = 2,
    val lastMonthReset: String = "2026-08",
    val longestStreakEver: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val englishMeaning: String,
    val totalVerses: Int,
    val revelationType: String
)

@Entity(
    tableName = "verses",
    primaryKeys = ["surahNumber", "verseNumber"]
)
data class VerseEntity(
    val surahNumber: Int,
    val verseNumber: Int,
    val absoluteNumber: Int,
    val juz: Int = 1,
    val page: Int = 1,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val tafsirShort: String = ""
)

@Entity(
    tableName = "quran_notes",
    primaryKeys = ["surahNumber", "verseNumber"]
)
data class QuranNoteEntity(
    val surahNumber: Int,
    val verseNumber: Int,
    val noteText: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "quran_bookmarks",
    indices = [Index(value = ["surahNumber", "verseNumber"], unique = true)]
)
data class QuranBookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val surahNumber: Int,
    val verseNumber: Int,
    val surahName: String = "",
    val surahNameArabic: String = "",
    val arabicText: String = "",
    val translation: String = "",
    val note: String = "",
    val colorTag: String = "gold", // gold, emerald, sapphire, ruby, purple
    val juz: Int = 1,
    val page: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_reading_sessions")
data class QuranReadingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,          // "YYYY-MM-DD"
    val targetSeconds: Int,
    val elapsedSeconds: Int,
    val completed: Boolean,
    val startedAt: Long = System.currentTimeMillis()
)




