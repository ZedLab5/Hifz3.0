package com.example.data.model

enum class StreakActivityType(
    val title: String,
    val description: String
) {
    SALAT("Daily Obligatory Salat", "Fulfill your daily obligatory prayers"),
    QURAN("Quran Reading", "Read any portion of the Holy Qur'an"),
    AZKAR("Azkar Recitation", "Morning, Evening or Daily Azkar"),
    DUA("Du'a & Supplication", "Read or reflect on personal supplications"),
    TASBIH("Tasbih & Dhikr", "Count remembrance and praise of Allah")
}

data class DayStreakStatus(
    val date: String, // "YYYY-MM-DD"
    val salatCompleted: Boolean = false,
    val quranCompleted: Boolean = false,
    val azkarCompleted: Boolean = false,
    val duaCompleted: Boolean = false,
    val tasbihCompleted: Boolean = false,
    val isFreezeUsed: Boolean = false,
    val isExcused: Boolean = false,
    val excuseReason: String = ""
) {
    val completedCount: Int
        get() = (if (salatCompleted) 1 else 0) +
                (if (quranCompleted) 1 else 0) +
                (if (azkarCompleted) 1 else 0) +
                (if (duaCompleted) 1 else 0) +
                (if (tasbihCompleted) 1 else 0)

    val isAnyCompleted: Boolean
        get() = completedCount > 0 || isFreezeUsed || isExcused
}

data class UnifiedStreakData(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val salatStreak: Int = 0,
    val quranStreak: Int = 0,
    val azkarStreak: Int = 0,
    val duaStreak: Int = 0,
    val tasbihStreak: Int = 0,
    val freezesRemaining: Int = 2,
    val todayCompletedCount: Int = 0,
    val isTodayAnyCompleted: Boolean = false,
    val todaySalatDone: Boolean = false,
    val todayQuranDone: Boolean = false,
    val todayAzkarDone: Boolean = false,
    val todayDuaDone: Boolean = false,
    val todayTasbihDone: Boolean = false,
    val todayExcused: Boolean = false,
    val todayExcuseReason: String = "",
    val yesterdayExcused: Boolean = false,
    val isYesterdayMissed: Boolean = false,
    val canUseFreeze: Boolean = false,
    val isTrackingEnabled: Boolean = true,
    val recentDays: List<DayStreakStatus> = emptyList()
)
