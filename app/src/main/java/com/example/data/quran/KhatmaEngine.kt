package com.example.data.quran

import com.example.data.local.KhatmaPlanEntity
import com.example.data.model.SurahMeta
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class AyahCoordinate(
    val surahNumber: Int,
    val surahNameEnglish: String,
    val surahNameArabic: String,
    val ayahNumber: Int,
    val juzNumber: Int,
    val absoluteAyahIndex: Int // 1..6236
) {
    val displayShort: String
        get() = "$surahNameEnglish $ayahNumber"

    val displayDetailed: String
        get() = "$surahNameEnglish ($surahNameArabic), Ayah $ayahNumber • Juz $juzNumber"
}

enum class KhatmaPaceStatus {
    AHEAD,
    ON_TRACK,
    BEHIND,
    COMPLETED
}

data class KhatmaSessionInfo(
    val index: Int, // 0-indexed
    val title: String, // e.g. "Morning", "After Fajr"
    val subtitle: String, // e.g. "8 Ayahs"
    val startAyahCoord: AyahCoordinate,
    val endAyahCoord: AyahCoordinate,
    val targetAyahsCount: Int,
    val isCompleted: Boolean
)

data class KhatmaDayItem(
    val dayNumber: Int, // 1..totalDays
    val dateFormatted: String,
    val startCoord: AyahCoordinate,
    val endCoord: AyahCoordinate,
    val targetAyahsCount: Int,
    val isCompleted: Boolean,
    val isToday: Boolean,
    val isPast: Boolean,
    val isUpcoming: Boolean
)

data class KhatmaFullDashboardState(
    val plan: KhatmaPlanEntity,
    val totalAyahs: Int = 6236,
    val readAyahsCount: Int,
    val progressFraction: Float, // 0.0 .. 1.0
    val progressPercentage: Int, // 0 .. 100
    val currentPosition: AyahCoordinate,
    val nextReadingPosition: AyahCoordinate,
    val currentDayNumber: Int,
    val totalDays: Int,
    val daysRemaining: Int,
    val todayTargetAyahs: Int,
    val todayReadAyahs: Int,
    val todayRemainingAyahs: Int,
    val isTodayTargetAchieved: Boolean,
    val paceStatus: KhatmaPaceStatus,
    val paceDiffAyahs: Int, // positive if ahead, negative if behind
    val estimatedCompletionDate: String,
    val dailySessions: List<KhatmaSessionInfo>,
    val dayPlanTimeline: List<KhatmaDayItem>
)

object KhatmaEngine {

    const val TOTAL_QURAN_AYAHS = 6236

    // Single source of truth from QuranData (Total = 6,236)
    val ALL_SURAHS: List<SurahMeta>
        get() = QuranData.canonicalSurahs

    private val surahMap by lazy { QuranData.canonicalSurahs.associateBy { it.number } }

    // Cumulative sum array: cumulativeVerses[i] = total verses from Surah 1 to Surah i
    private val cumulativeVerses: IntArray by lazy {
        val arr = IntArray(115)
        var sum = 0
        for (s in QuranData.canonicalSurahs) {
            sum += s.totalVerses
            arr[s.number] = sum
        }
        arr
    }

    // Canonical starting verse of each of the 30 Juz's (Surah, Ayah)
    val juzStartPoints = listOf(
        Pair(1, 1),   // Juz 1
        Pair(2, 142), // Juz 2
        Pair(2, 253), // Juz 3
        Pair(3, 93),  // Juz 4
        Pair(4, 24),  // Juz 5
        Pair(4, 148), // Juz 6
        Pair(5, 82),  // Juz 7
        Pair(6, 111), // Juz 8
        Pair(7, 88),  // Juz 9
        Pair(8, 41),  // Juz 10
        Pair(9, 93),  // Juz 11
        Pair(11, 6),  // Juz 12
        Pair(12, 53), // Juz 13
        Pair(15, 1),  // Juz 14
        Pair(17, 1),  // Juz 15
        Pair(18, 75), // Juz 16
        Pair(21, 1),  // Juz 17
        Pair(23, 1),  // Juz 18
        Pair(25, 21), // Juz 19
        Pair(27, 56), // Juz 20
        Pair(29, 46), // Juz 21
        Pair(33, 31), // Juz 22
        Pair(36, 28), // Juz 23
        Pair(39, 32), // Juz 24
        Pair(41, 47), // Juz 25
        Pair(46, 1),  // Juz 26
        Pair(51, 31), // Juz 27
        Pair(58, 1),  // Juz 28
        Pair(67, 1),  // Juz 29
        Pair(78, 1)   // Juz 30
    )

    private val juzStartAbsIndices: List<Int> by lazy {
        juzStartPoints.map { (surah, ayah) -> getAbsoluteAyahIndex(surah, ayah) }
    }

    fun getSurah(surahNumber: Int): SurahMeta {
        return surahMap[surahNumber] ?: ALL_SURAHS.first()
    }

    /**
     * Converts a (surahNumber, ayahNumber) to an absolute index in 1..6236
     */
    fun getAbsoluteAyahIndex(surahNumber: Int, ayahNumber: Int): Int {
        val s = surahNumber.coerceIn(1, 114)
        val surahMeta = getSurah(s)
        val a = ayahNumber.coerceIn(1, surahMeta.totalVerses)
        val prevCount = cumulativeVerses[s - 1]
        return prevCount + a
    }

    /**
     * Converts an absolute index in 1..6236 to an AyahCoordinate
     */
    fun getAyahCoordinate(absoluteIndex: Int): AyahCoordinate {
        val target = absoluteIndex.coerceIn(1, TOTAL_QURAN_AYAHS)
        var low = 1
        var high = 114
        var surahNum = 1

        while (low <= high) {
            val mid = (low + high) / 2
            val startOfMid = cumulativeVerses[mid - 1] + 1
            val endOfMid = cumulativeVerses[mid]
            if (target in startOfMid..endOfMid) {
                surahNum = mid
                break
            } else if (target < startOfMid) {
                high = mid - 1
            } else {
                low = mid + 1
            }
        }

        val meta = getSurah(surahNum)
        val ayahNum = target - cumulativeVerses[surahNum - 1]
        val juz = getJuzNumber(target)

        return AyahCoordinate(
            surahNumber = surahNum,
            surahNameEnglish = meta.nameEnglish,
            surahNameArabic = meta.nameArabic,
            ayahNumber = ayahNum,
            juzNumber = juz,
            absoluteAyahIndex = target
        )
    }

    fun getJuzNumber(absoluteIndex: Int): Int {
        for (i in juzStartAbsIndices.indices.reversed()) {
            if (absoluteIndex >= juzStartAbsIndices[i]) {
                return i + 1
            }
        }
        return 1
    }

    /**
     * Generates a complete day-by-day plan of the Khatma
     */
    fun generateDayTimeline(
        totalDays: Int,
        startDateEpochDay: Long,
        readAyahsCount: Int
    ): List<KhatmaDayItem> {
        val todayEpochDay = LocalDate.now().toEpochDay()
        val days = totalDays.coerceIn(1, 365)
        val items = mutableListOf<KhatmaDayItem>()
        val dateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())

        for (day in 1..days) {
            val dayEpoch = startDateEpochDay + (day - 1)
            val date = LocalDate.ofEpochDay(dayEpoch)
            val dateStr = date.format(dateFormatter)

            val startAbs = if (day == 1) 1 else (((day - 1) * TOTAL_QURAN_AYAHS.toDouble()) / days).roundToInt() + 1
            val endAbs = if (day == days) TOTAL_QURAN_AYAHS else ((day * TOTAL_QURAN_AYAHS.toDouble()) / days).roundToInt()

            val startCoord = getAyahCoordinate(startAbs)
            val endCoord = getAyahCoordinate(endAbs)
            val targetCount = endAbs - startAbs + 1

            val isCompleted = readAyahsCount >= endAbs
            val isToday = dayEpoch == todayEpochDay
            val isPast = dayEpoch < todayEpochDay
            val isUpcoming = dayEpoch > todayEpochDay

            items.add(
                KhatmaDayItem(
                    dayNumber = day,
                    dateFormatted = dateStr,
                    startCoord = startCoord,
                    endCoord = endCoord,
                    targetAyahsCount = targetCount,
                    isCompleted = isCompleted,
                    isToday = isToday,
                    isPast = isPast,
                    isUpcoming = isUpcoming
                )
            )
        }
        return items
    }

    /**
     * Splits today's target into user-defined sessions (1, 2, 3, or 5)
     */
    fun generateDailySessions(
        todayStartAbs: Int,
        todayEndAbs: Int,
        sessionsCount: Int,
        completedMask: Int,
        todayReadAyahs: Int
    ): List<KhatmaSessionInfo> {
        val totalToday = max(1, todayEndAbs - todayStartAbs + 1)
        val count = when (sessionsCount) {
            1 -> 1
            2 -> 2
            5 -> 5
            else -> 3
        }

        val titles = when (count) {
            1 -> listOf("Daily Reading Session")
            2 -> listOf("Morning Reflection", "Evening Recitation")
            5 -> listOf("After Fajr", "After Dhuhr", "After Asr", "After Maghrib", "After Isha")
            else -> listOf("Morning (Fajr / Duha)", "Afternoon (Asr)", "Evening (Maghrib / Isha)")
        }

        val sessionList = mutableListOf<KhatmaSessionInfo>()
        var currentStart = todayStartAbs

        for (i in 0 until count) {
            val sessionEnd = if (i == count - 1) {
                todayEndAbs
            } else {
                val fractionEnd = todayStartAbs + (((i + 1) * totalToday.toDouble()) / count).roundToInt() - 1
                min(todayEndAbs, fractionEnd)
            }

            val sessionTarget = sessionEnd - currentStart + 1
            val isMarkedCompleted = (completedMask and (1 shl i)) != 0
            // Also consider completed if enough read ayahs logged today
            val cumulativeSessionEndTarget = (sessionEnd - todayStartAbs + 1)
            val isReadCompleted = todayReadAyahs >= cumulativeSessionEndTarget
            val isCompleted = isMarkedCompleted || isReadCompleted

            sessionList.add(
                KhatmaSessionInfo(
                    index = i,
                    title = titles.getOrElse(i) { "Session ${i + 1}" },
                    subtitle = "$sessionTarget Ayahs",
                    startAyahCoord = getAyahCoordinate(currentStart),
                    endAyahCoord = getAyahCoordinate(sessionEnd),
                    targetAyahsCount = sessionTarget,
                    isCompleted = isCompleted
                )
            )
            currentStart = sessionEnd + 1
        }

        return sessionList
    }

    /**
     * Builds full high-fidelity Dashboard State from a KhatmaPlanEntity
     */
    fun buildDashboardState(plan: KhatmaPlanEntity): KhatmaFullDashboardState {
        val todayEpochDay = LocalDate.now().toEpochDay()
        val readAyahs = plan.readAyahsCount.coerceIn(0, TOTAL_QURAN_AYAHS)
        val progressFraction = (readAyahs.toFloat() / TOTAL_QURAN_AYAHS.toFloat()).coerceIn(0f, 1f)
        val progressPercentage = (progressFraction * 100).toInt().coerceIn(0, 100)

        val currentCoord = if (readAyahs == 0) getAyahCoordinate(1) else getAyahCoordinate(readAyahs)
        val nextReadingCoord = if (readAyahs >= TOTAL_QURAN_AYAHS) getAyahCoordinate(TOTAL_QURAN_AYAHS) else getAyahCoordinate(readAyahs + 1)

        val startDay = plan.startEpochDay
        val totalDays = max(1, plan.totalDays)
        val rawCurrentDay = (todayEpochDay - startDay + 1).toInt()
        val currentDay = rawCurrentDay.coerceIn(1, totalDays)
        val daysRemaining = max(0, totalDays - currentDay)

        // Today's scheduled range in original plan
        val todayStartAbs = if (currentDay == 1) 1 else (((currentDay - 1) * TOTAL_QURAN_AYAHS.toDouble()) / totalDays).roundToInt() + 1
        val todayEndAbs = if (currentDay == totalDays) TOTAL_QURAN_AYAHS else ((currentDay * TOTAL_QURAN_AYAHS.toDouble()) / totalDays).roundToInt()
        val todayTargetAyahs = max(1, todayEndAbs - todayStartAbs + 1)

        // Calculate expected cumulative target by today
        val expectedAyahsByToday = min(TOTAL_QURAN_AYAHS, ((currentDay * TOTAL_QURAN_AYAHS.toDouble()) / totalDays).roundToInt())
        val diff = readAyahs - expectedAyahsByToday

        val paceStatus = when {
            readAyahs >= TOTAL_QURAN_AYAHS -> KhatmaPaceStatus.COMPLETED
            diff > 3 -> KhatmaPaceStatus.AHEAD
            diff < -3 -> KhatmaPaceStatus.BEHIND
            else -> KhatmaPaceStatus.ON_TRACK
        }

        // Today's actual read progress
        val todayReadAyahs = if (readAyahs < todayStartAbs) {
            0
        } else {
            min(todayTargetAyahs, readAyahs - todayStartAbs + 1)
        }
        val todayRemainingAyahs = max(0, todayTargetAyahs - todayReadAyahs)
        val isTodayAchieved = todayReadAyahs >= todayTargetAyahs

        // Estimated completion date: based on actual pace or targetEnd
        val estimatedEndDate = LocalDate.ofEpochDay(plan.targetEndEpochDay)
        val estimatedCompletionDateFormatted = estimatedEndDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault()))

        // Sessions
        val effectiveBitmask = if (plan.lastSessionDateDay == todayEpochDay) plan.completedSessionsTodayBitmask else 0
        val sessions = generateDailySessions(
            todayStartAbs = todayStartAbs,
            todayEndAbs = todayEndAbs,
            sessionsCount = plan.dailySessionsCount,
            completedMask = effectiveBitmask,
            todayReadAyahs = todayReadAyahs
        )

        // Timeline
        val timeline = generateDayTimeline(
            totalDays = totalDays,
            startDateEpochDay = startDay,
            readAyahsCount = readAyahs
        )

        return KhatmaFullDashboardState(
            plan = plan,
            totalAyahs = TOTAL_QURAN_AYAHS,
            readAyahsCount = readAyahs,
            progressFraction = progressFraction,
            progressPercentage = progressPercentage,
            currentPosition = currentCoord,
            nextReadingPosition = nextReadingCoord,
            currentDayNumber = currentDay,
            totalDays = totalDays,
            daysRemaining = daysRemaining,
            todayTargetAyahs = todayTargetAyahs,
            todayReadAyahs = todayReadAyahs,
            todayRemainingAyahs = todayRemainingAyahs,
            isTodayTargetAchieved = isTodayAchieved,
            paceStatus = paceStatus,
            paceDiffAyahs = diff,
            estimatedCompletionDate = estimatedCompletionDateFormatted,
            dailySessions = sessions,
            dayPlanTimeline = timeline
        )
    }

    /**
     * Dua Khatm Al-Quran (Supplication upon completion of the Holy Quran)
     */
    val DUA_KHATM_ARABIC = """
        اللَّهُمَّ ارْحَمْنِي بِالْقُرْآنِ وَاجْعَلْهُ لِي إِمَامًا وَنُورًا وَهُدًى وَرَحْمَةً،
        اللَّهُمَّ ذَكِّرْنِي مِنْهُ مَا نَسِيتُ وَعَلِّمْنِي مِنْهُ مَا جَهِلْتُ،
        وَارْزُقْنِي تِلَاوَتَهُ آنَاءَ اللَّيْلِ وَأَطْرَافَ النَّهَارِ،
        وَاجْعَلْهُ لِي حُجَّةً يَا رَبَّ الْعَالَمِينَ.
        
        اللَّهُمَّ أَصْلِحْ لِي دِينِي الَّذِي هُوَ عِصْمَةُ أَمْرِي،
        وَأَصْلِحْ لِي دُنْيَايَ الَّتِي فِيهَا مَعَاشِي،
        وَأَصْلِحْ لِي آخِرَتِي الَّتِي فِيهَا مَعَادِي،
        وَاجْعَلِ الْحَيَاةَ زِيَادَةً لِي فِي كُلِّ خَيْرٍ،
        وَاجْعَلِ الْمَوْتَ رَاحَةً لِي مِنْ كُلِّ شَرٍّ.
    """.trimIndent()

    val DUA_KHATM_TRANSLATION = """
        "O Allah, have mercy on me through the Qur’an, and make it for me a guide, a light, guidance, and mercy.
        O Allah, remind me of what I have forgotten from it, teach me what I am ignorant of from it, grant me its recitation during the hours of the night and the edges of the day, and make it a supportive proof for me, O Lord of the Worlds.
        
        O Allah, rectify for me my religion which is the safeguard of my affairs, rectify for me my worldly life in which is my livelihood, rectify for me my Hereafter to which is my return, make life an abundance for me in every good, and make death a relief for me from every evil."
    """.trimIndent()
}
