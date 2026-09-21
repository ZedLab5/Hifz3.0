package com.example.data.quran

import com.example.data.local.StreakDailyLogEntity
import com.example.data.local.StreakSummaryEntity
import com.example.data.model.DayStreakStatus
import com.example.data.model.UnifiedStreakData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object StreakEngine {

    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM")

    fun getTodayDateString(): String = LocalDate.now().format(DATE_FORMATTER)
    fun getCurrentMonthString(): String = LocalDate.now().format(MONTH_FORMATTER)

    fun calculateStreakData(
        logsList: List<StreakDailyLogEntity>,
        summary: StreakSummaryEntity?,
        todayDate: LocalDate = LocalDate.now(),
        isTrackingEnabled: Boolean = true
    ): UnifiedStreakData {
        val todayStr = todayDate.format(DATE_FORMATTER)
        val yesterdayStr = todayDate.minusDays(1).format(DATE_FORMATTER)
        val logsMap = logsList.associateBy { it.date }

        val todayLog = logsMap[todayStr]
        val yesterdayLog = logsMap[yesterdayStr]

        val todaySalat = todayLog?.salatCompleted == true
        val todayQuran = todayLog?.quranCompleted == true
        val todayAzkar = todayLog?.azkarCompleted == true
        val todayDua = todayLog?.duaCompleted == true
        val todayTasbih = todayLog?.tasbihCompleted == true
        val todayFrozen = todayLog?.isFreezeUsed == true
        val todayExcused = todayLog?.isExcused == true
        val todayExcuseReason = todayLog?.excuseReason.orEmpty()
        val todayAnyDone = todaySalat || todayQuran || todayAzkar || todayDua || todayTasbih || todayFrozen || todayExcused

        val yesterdaySalat = yesterdayLog?.salatCompleted == true
        val yesterdayQuran = yesterdayLog?.quranCompleted == true
        val yesterdayAzkar = yesterdayLog?.azkarCompleted == true
        val yesterdayDua = yesterdayLog?.duaCompleted == true
        val yesterdayTasbih = yesterdayLog?.tasbihCompleted == true
        val yesterdayFrozen = yesterdayLog?.isFreezeUsed == true
        val yesterdayExcused = yesterdayLog?.isExcused == true
        val yesterdayAnyDone = yesterdaySalat || yesterdayQuran || yesterdayAzkar || yesterdayDua || yesterdayTasbih || yesterdayFrozen || yesterdayExcused

        // Freezes management
        val currentMonth = todayDate.format(MONTH_FORMATTER)
        val freezesRemaining = if (summary == null || summary.lastMonthReset != currentMonth) {
            2
        } else {
            summary.freezesRemaining.coerceIn(0, 2)
        }

        // Calculate Overall Streak (excused days maintain streak continuity without consuming a freeze pass)
        val currentStreak = calculateConsecutiveStreak(
            todayDate = todayDate,
            logsMap = logsMap,
            predicate = { log -> log.salatCompleted || log.quranCompleted || log.azkarCompleted || log.duaCompleted || log.tasbihCompleted || log.isFreezeUsed || log.isExcused }
        )

        // Calculate Individual Sub-Streaks
        val salatStreak = calculateConsecutiveStreak(
            todayDate = todayDate,
            logsMap = logsMap,
            predicate = { it.salatCompleted || it.isExcused }
        )

        val quranStreak = calculateConsecutiveStreak(
            todayDate = todayDate,
            logsMap = logsMap,
            predicate = { it.quranCompleted || it.isExcused }
        )

        val azkarStreak = calculateConsecutiveStreak(
            todayDate = todayDate,
            logsMap = logsMap,
            predicate = { it.azkarCompleted || it.isExcused }
        )

        val duaStreak = calculateConsecutiveStreak(
            todayDate = todayDate,
            logsMap = logsMap,
            predicate = { it.duaCompleted || it.isExcused }
        )

        val tasbihStreak = calculateConsecutiveStreak(
            todayDate = todayDate,
            logsMap = logsMap,
            predicate = { it.tasbihCompleted || it.isExcused }
        )

        // Calculate Longest Streak in history
        val computedLongest = calculateAllTimeLongestStreak(logsList)
        val recordedLongest = summary?.longestStreakEver ?: 0
        val longestStreak = maxOf(currentStreak, computedLongest, recordedLongest)

        // Recent 60 Days for Calendar / Contribution Heatmap
        val recentDays = mutableListOf<DayStreakStatus>()
        for (i in 59 downTo 0) {
            val date = todayDate.minusDays(i.toLong())
            val dateStr = date.format(DATE_FORMATTER)
            val log = logsMap[dateStr]
            recentDays.add(
                DayStreakStatus(
                    date = dateStr,
                    salatCompleted = log?.salatCompleted == true,
                    quranCompleted = log?.quranCompleted == true,
                    azkarCompleted = log?.azkarCompleted == true,
                    duaCompleted = log?.duaCompleted == true,
                    tasbihCompleted = log?.tasbihCompleted == true,
                    isFreezeUsed = log?.isFreezeUsed == true,
                    isExcused = log?.isExcused == true,
                    excuseReason = log?.excuseReason.orEmpty()
                )
            )
        }

        val todayCompletedCount = (if (todaySalat) 1 else 0) +
                (if (todayQuran) 1 else 0) +
                (if (todayAzkar) 1 else 0) +
                (if (todayDua) 1 else 0) +
                (if (todayTasbih) 1 else 0)

        val isYesterdayMissed = !yesterdayAnyDone
        val canUseFreeze = freezesRemaining > 0 && (isYesterdayMissed || !todayAnyDone)

        return UnifiedStreakData(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            salatStreak = salatStreak,
            quranStreak = quranStreak,
            azkarStreak = azkarStreak,
            duaStreak = duaStreak,
            tasbihStreak = tasbihStreak,
            freezesRemaining = freezesRemaining,
            todayCompletedCount = todayCompletedCount,
            isTodayAnyCompleted = todayAnyDone,
            todaySalatDone = todaySalat,
            todayQuranDone = todayQuran,
            todayAzkarDone = todayAzkar,
            todayDuaDone = todayDua,
            todayTasbihDone = todayTasbih,
            todayExcused = todayExcused,
            todayExcuseReason = todayExcuseReason,
            yesterdayExcused = yesterdayExcused,
            isYesterdayMissed = isYesterdayMissed,
            canUseFreeze = canUseFreeze,
            isTrackingEnabled = isTrackingEnabled,
            recentDays = recentDays
        )
    }

    private fun calculateConsecutiveStreak(
        todayDate: LocalDate,
        logsMap: Map<String, StreakDailyLogEntity>,
        predicate: (StreakDailyLogEntity) -> Boolean
    ): Int {
        val todayStr = todayDate.format(DATE_FORMATTER)
        val todayLog = logsMap[todayStr]
        val isTodayDone = todayLog != null && predicate(todayLog)

        var streak = 0
        var checkDate = if (isTodayDone) todayDate else todayDate.minusDays(1)

        while (true) {
            val dateStr = checkDate.format(DATE_FORMATTER)
            val log = logsMap[dateStr]
            if (log != null && predicate(log)) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }

        return streak
    }

    private fun calculateAllTimeLongestStreak(logsList: List<StreakDailyLogEntity>): Int {
        if (logsList.isEmpty()) return 0
        val sortedLogs = logsList.sortedBy { it.date }
        var maxStreak = 0
        var currentStreak = 0
        var lastDate: LocalDate? = null

        for (log in sortedLogs) {
            val isDone = log.salatCompleted || log.quranCompleted || log.azkarCompleted || log.duaCompleted || log.tasbihCompleted || log.isFreezeUsed || log.isExcused
            if (!isDone) {
                currentStreak = 0
                lastDate = null
                continue
            }

            try {
                val date = LocalDate.parse(log.date, DATE_FORMATTER)
                if (lastDate == null || date == lastDate.plusDays(1)) {
                    currentStreak++
                } else if (date == lastDate) {
                    // duplicate date, skip
                } else {
                    currentStreak = 1
                }
                lastDate = date
                if (currentStreak > maxStreak) {
                    maxStreak = currentStreak
                }
            } catch (_: Exception) {
                // Ignore parse errors
            }
        }
        return maxStreak
    }

    fun generateInitialSeedLogs(todayDate: LocalDate = LocalDate.now()): List<StreakDailyLogEntity> {
        val list = mutableListOf<StreakDailyLogEntity>()
        val formatter = DATE_FORMATTER

        // Day 0 (Today) - Clean start for user with all devotions false
        list.add(
            StreakDailyLogEntity(
                date = todayDate.format(formatter),
                salatCompleted = false,
                quranCompleted = false,
                azkarCompleted = false,
                duaCompleted = false,
                tasbihCompleted = false,
                isFreezeUsed = false
            )
        )

        return list
    }
}
