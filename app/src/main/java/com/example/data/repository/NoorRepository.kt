package com.example.data.repository

import com.example.data.local.DailyHabitEntity
import com.example.data.local.FastLogEntity
import com.example.data.local.FavoriteItemEntity
import com.example.data.local.KhatmaHistoryEntity
import com.example.data.local.KhatmaPlanEntity
import com.example.data.local.NoorDao
import com.example.data.local.PrayerDao
import com.example.data.local.PrayerRecordEntity
import com.example.data.local.QadaRecordEntity
import com.example.data.local.QuranBookmarkDao
import com.example.data.local.QuranBookmarkEntity
import com.example.data.local.QuranNoteEntity
import com.example.data.local.QuranReadingSessionEntity
import com.example.data.local.ReadingProgressEntity
import com.example.data.local.StreakDailyLogEntity
import com.example.data.local.StreakSummaryEntity
import com.example.data.local.SurahEntity
import com.example.data.local.TasbihDao
import com.example.data.local.TasbihRecordEntity
import com.example.data.local.VerseEntity
import com.example.data.model.CalculationAuthority
import com.example.data.model.DuaItem
import com.example.data.model.PrayerTime
import com.example.data.model.PrayerZone
import com.example.data.model.Reciter
import com.example.data.model.StreakActivityType
import com.example.data.model.Surah
import com.example.data.model.SurahMeta
import com.example.data.model.UnifiedStreakData
import com.example.data.model.Verse
import com.example.data.quran.DuaData
import com.example.data.quran.QuranData
import com.example.data.quran.QuranDataLoader
import com.example.data.quran.StreakEngine
import com.example.data.prayer.PrayerCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NoorRepository(
    private val dao: NoorDao,
    private val quranBookmarkDao: QuranBookmarkDao,
    private val tasbihDao: TasbihDao,
    private val prayerDao: PrayerDao
) {
    val favorites: Flow<List<FavoriteItemEntity>> = dao.getAllFavorites()
    val habits: Flow<List<DailyHabitEntity>> = dao.getAllHabits()
    val readingProgress: Flow<ReadingProgressEntity?> = dao.getReadingProgress()
    val tasbihRecords: Flow<List<TasbihRecordEntity>> = tasbihDao.getAllTasbihRecords()
    val allPrayerRecords: Flow<List<PrayerRecordEntity>> = prayerDao.getAllPrayerRecords()
    val activeKhatmaPlan: Flow<KhatmaPlanEntity?> = dao.getActiveKhatmaPlan()
    val khatmaHistory: Flow<List<KhatmaHistoryEntity>> = dao.getAllKhatmaHistory()
    val qadaRecords: Flow<List<QadaRecordEntity>> = prayerDao.getAllQadaRecords()
    val fastLogs: Flow<List<FastLogEntity>> = dao.getAllFastLogs()
    val streakDailyLogs: Flow<List<StreakDailyLogEntity>> = dao.getAllStreakDailyLogs()
    val streakSummary: Flow<StreakSummaryEntity?> = dao.getStreakSummary()
    val allQuranNotes: Flow<List<QuranNoteEntity>> = dao.getAllQuranNotes()
    val quranBookmarks: Flow<List<QuranBookmarkEntity>> = quranBookmarkDao.getAllBookmarks()

    // Quran Bookmarks Management
    suspend fun addQuranBookmark(
        surahNumber: Int,
        verseNumber: Int,
        surahName: String = "",
        surahNameArabic: String = "",
        arabicText: String = "",
        translation: String = "",
        note: String = "",
        colorTag: String = "gold",
        juz: Int = 1,
        page: Int = 1
    ): Long {
        val entity = QuranBookmarkEntity(
            surahNumber = surahNumber,
            verseNumber = verseNumber,
            surahName = surahName,
            surahNameArabic = surahNameArabic,
            arabicText = arabicText,
            translation = translation,
            note = note,
            colorTag = colorTag,
            juz = juz,
            page = page
        )
        return quranBookmarkDao.insertBookmark(entity)
    }

    suspend fun toggleQuranBookmark(
        surahNumber: Int,
        verseNumber: Int,
        surahName: String = "",
        surahNameArabic: String = "",
        arabicText: String = "",
        translation: String = "",
        juz: Int = 1,
        page: Int = 1
    ) {
        val isBookmarked = isQuranBookmarkedOnce(surahNumber, verseNumber)
        if (isBookmarked) {
            removeQuranBookmarkByAyah(surahNumber, verseNumber)
        } else {
            addQuranBookmark(
                surahNumber = surahNumber,
                verseNumber = verseNumber,
                surahName = surahName,
                surahNameArabic = surahNameArabic,
                arabicText = arabicText,
                translation = translation,
                juz = juz,
                page = page
            )
        }
    }

    suspend fun removeQuranBookmarkByAyah(surahNumber: Int, verseNumber: Int) {
        quranBookmarkDao.deleteBookmarkByAyah(surahNumber, verseNumber)
    }

    suspend fun removeQuranBookmarkById(id: Long) {
        quranBookmarkDao.deleteBookmarkById(id)
    }

    fun isQuranBookmarked(surahNumber: Int, verseNumber: Int): Flow<Boolean> {
        return quranBookmarkDao.isBookmarked(surahNumber, verseNumber)
    }

    suspend fun isQuranBookmarkedOnce(surahNumber: Int, verseNumber: Int): Boolean {
        return quranBookmarkDao.isBookmarkedOnce(surahNumber, verseNumber)
    }

    suspend fun updateBookmarkNote(id: Long, note: String) {
        quranBookmarkDao.updateBookmarkNote(id, note)
    }

    suspend fun updateBookmarkColorTag(id: Long, colorTag: String) {
        quranBookmarkDao.updateBookmarkColorTag(id, colorTag)
    }

    suspend fun saveQuranNote(surahNumber: Int, verseNumber: Int, text: String) {
        if (text.isBlank()) {
            dao.deleteNote(surahNumber, verseNumber)
        } else {
            dao.saveNote(QuranNoteEntity(surahNumber = surahNumber, verseNumber = verseNumber, noteText = text.trim()))
        }
    }

    suspend fun deleteQuranNote(surahNumber: Int, verseNumber: Int) {
        dao.deleteNote(surahNumber, verseNumber)
    }

    // Quran Data Access
    val allSurahsFlow: Flow<List<SurahMeta>> = dao.getAllSurahsFlow().map { entities ->
        if (entities.isNotEmpty()) {
            entities.map {
                SurahMeta(
                    number = it.number,
                    nameArabic = it.nameArabic,
                    nameEnglish = it.nameEnglish,
                    englishMeaning = it.englishMeaning,
                    totalVerses = it.totalVerses,
                    revelationType = it.revelationType
                )
            }
        } else {
            QuranData.canonicalSurahs
        }
    }

    suspend fun preloadQuranIfNeeded(context: android.content.Context) {
        QuranDataLoader.preloadQuranIfNeeded(context, dao)
    }

    suspend fun getSurahs(): List<SurahMeta> {
        val inDb = dao.getAllSurahs()
        return if (inDb.isNotEmpty()) {
            inDb.map {
                SurahMeta(
                    number = it.number,
                    nameArabic = it.nameArabic,
                    nameEnglish = it.nameEnglish,
                    englishMeaning = it.englishMeaning,
                    totalVerses = it.totalVerses,
                    revelationType = it.revelationType
                )
            }
        } else {
            QuranData.canonicalSurahs
        }
    }

    suspend fun getSurahMeta(number: Int): SurahMeta? {
        val entity = dao.getSurahByNumber(number)
        return if (entity != null) {
            SurahMeta(
                number = entity.number,
                nameArabic = entity.nameArabic,
                nameEnglish = entity.nameEnglish,
                englishMeaning = entity.englishMeaning,
                totalVerses = entity.totalVerses,
                revelationType = entity.revelationType
            )
        } else {
            QuranData.getSurahMeta(number)
        }
    }

    companion object {
        private val bismillahPrefixRegex = Regex("^[بِّسْمِ|بِسْمِ].*?ٱلرَّحِيمِ\\s*")

        fun sanitizeArabicVerseText(surahNumber: Int, verseNumber: Int, rawArabic: String): String {
            if (surahNumber != 1 && verseNumber == 1) {
                val cleaned = rawArabic.replace(bismillahPrefixRegex, "").trim()
                return if (cleaned.isNotEmpty()) cleaned else rawArabic
            }
            return rawArabic
        }

        val prayerZones: List<PrayerZone> = listOf(
            PrayerZone("ma_tangier", "Tangier & Tetouan", "طنجة وتطوان", "Morocco", "Zone 1 (North)", 35.7595, -5.8340, "Africa/Casablanca"),
            PrayerZone("ma_casablanca", "Casablanca & Rabat", "الدار البيضاء والرباط", "Morocco", "Zone 2 (Central Coast)", 33.5731, -7.5898, "Africa/Casablanca"),
            PrayerZone("ma_fes", "Fes & Meknes", "فاس ومكناس", "Morocco", "Zone 3 (Saïss / Central)", 34.0181, -5.0078, "Africa/Casablanca"),
            PrayerZone("ma_marrakech", "Marrakech & Safi", "مراكش وآسفي", "Morocco", "Zone 4 (Haouz / South)", 31.6295, -7.9811, "Africa/Casablanca"),
            PrayerZone("ma_oujda", "Oujda & Nador", "وجدة والناظور", "Morocco", "Zone 5 (Oriental / East)", 34.6814, -1.9086, "Africa/Casablanca"),
            PrayerZone("ma_agadir", "Agadir & Souss", "أكادير وسوس", "Morocco", "Zone 6 (Souss-Massa)", 30.4278, -9.5981, "Africa/Casablanca"),
            PrayerZone("ma_laayoune", "Laayoune & Dakhla", "العيون والداخلة", "Morocco", "Zone 7 (Sahara)", 27.1536, -13.2033, "Africa/Casablanca"),
            PrayerZone("sa_makkah", "Makkah Al-Mukarramah", "مكة المكرمة", "Saudi Arabia", "Hijaz Sanctuary Zone", 21.3891, 39.8579, "Asia/Riyadh"),
            PrayerZone("sa_madinah", "Madinah Al-Munawwarah", "المدينة المنورة", "Saudi Arabia", "Prophetic City Zone", 24.5247, 39.5692, "Asia/Riyadh"),
            PrayerZone("eg_cairo", "Cairo Al-Qahira", "القاهرة", "Egypt", "Nile Valley Zone", 30.0444, 31.2357, "Africa/Cairo"),
            PrayerZone("tr_istanbul", "Istanbul", "إسطنبول", "Turkey", "Bosphorus / Marmara Zone", 41.0082, 28.9784, "Europe/Istanbul"),
            PrayerZone("ae_dubai", "Dubai & Abu Dhabi", "دبي وأبوظبي", "United Arab Emirates", "Gulf Arabian Zone", 25.2048, 55.2708, "Asia/Dubai"),
            PrayerZone("gb_london", "London", "لندن", "United Kingdom", "Western Europe / UK Zone", 51.5074, -0.1278, "Europe/London"),
            PrayerZone("fr_paris", "Paris", "باريس", "France", "Central Europe Zone", 48.8566, 2.3522, "Europe/Paris"),
            PrayerZone("us_newyork", "New York", "نيويورك", "United States", "North America Eastern", 40.7128, -74.0060, "America/New_York")
        )

        val calculationAuthorities: List<CalculationAuthority> = listOf(
            CalculationAuthority("habous", "Moroccan Ministry of Habous & Islamic Affairs", "Official Kingdom of Morocco timing standard", 19.0, 17.0),
            CalculationAuthority("mwl", "Muslim World League (MWL)", "Standard international calculation method", 18.0, 17.0),
            CalculationAuthority("umm_al_qura", "Umm Al-Qura University, Makkah", "Official Saudi Arabia timing standard", 18.5, 0.0, ishaIntervalMinutes = 90),
            CalculationAuthority("egypt", "Egyptian General Authority of Survey", "Egypt, Africa and parts of Arab world", 19.5, 17.5),
            CalculationAuthority("jakim", "JAKIM (Malaysia)", "Jabatan Kemajuan Islam Malaysia standard (Fajr: 20°, Isha: 18°)", 20.0, 18.0),
            CalculationAuthority("muis", "MUIS (Singapore)", "Majlis Ugama Islam Singapura standard (Fajr: 20°, Isha: 18°)", 20.0, 18.0),
            CalculationAuthority("isna", "Islamic Society of North America (ISNA)", "United States & Canada standard", 15.0, 15.0),
            CalculationAuthority("diyanet", "Diyanet İşleri Başkanlığı (Turkey)", "Republic of Turkey standard", 18.0, 17.0),
            CalculationAuthority("karachi", "University of Islamic Sciences, Karachi", "Pakistan, Bangladesh, India & Afghanistan", 18.0, 18.0)
        )
    }

    fun getVersesForSurahFlow(surahNumber: Int): Flow<List<Verse>> {
        return dao.getVersesForSurahFlow(surahNumber).map { list ->
            list.map {
                Verse(
                    surahNumber = it.surahNumber,
                    verseNumber = it.verseNumber,
                    arabicText = sanitizeArabicVerseText(it.surahNumber, it.verseNumber, it.arabicText),
                    transliteration = it.transliteration,
                    translation = it.translation,
                    tafsirShort = it.tafsirShort,
                    juz = it.juz,
                    page = it.page,
                    absoluteAyahIndex = it.absoluteNumber
                )
            }
        }
    }

    suspend fun getVersesForSurah(surahNumber: Int): List<Verse> {
        return dao.getVersesForSurah(surahNumber).map {
            Verse(
                surahNumber = it.surahNumber,
                verseNumber = it.verseNumber,
                arabicText = sanitizeArabicVerseText(it.surahNumber, it.verseNumber, it.arabicText),
                transliteration = it.transliteration,
                translation = it.translation,
                tafsirShort = it.tafsirShort,
                juz = it.juz,
                page = it.page,
                absoluteAyahIndex = it.absoluteNumber
            )
        }
    }

    suspend fun getSurahWithVerses(surahNumber: Int): Surah {
        val meta = getSurahMeta(surahNumber) ?: QuranData.getSurahMeta(surahNumber) ?: SurahMeta(
            number = surahNumber,
            nameArabic = "",
            nameEnglish = "Surah $surahNumber",
            englishMeaning = "",
            totalVerses = 0,
            revelationType = "Meccan"
        )
        val verses = getVersesForSurah(surahNumber)
        return Surah(
            number = meta.number,
            nameArabic = meta.nameArabic,
            nameEnglish = meta.nameEnglish,
            englishMeaning = meta.englishMeaning,
            totalVerses = if (verses.isNotEmpty()) verses.size else meta.totalVerses,
            revelationType = meta.revelationType,
            verses = verses
        )
    }

    suspend fun searchVerses(query: String): List<Verse> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return emptyList()
        return dao.searchVerses(trimmed).map {
            Verse(
                surahNumber = it.surahNumber,
                verseNumber = it.verseNumber,
                arabicText = sanitizeArabicVerseText(it.surahNumber, it.verseNumber, it.arabicText),
                transliteration = it.transliteration,
                translation = it.translation,
                tafsirShort = it.tafsirShort,
                juz = it.juz,
                page = it.page,
                absoluteAyahIndex = it.absoluteNumber
            )
        }
    }

    suspend fun getVerse(surahNumber: Int, verseNumber: Int): Verse? {
        val entity = dao.getVerse(surahNumber, verseNumber) ?: return null
        return Verse(
            surahNumber = entity.surahNumber,
            verseNumber = entity.verseNumber,
            arabicText = sanitizeArabicVerseText(entity.surahNumber, entity.verseNumber, entity.arabicText),
            transliteration = entity.transliteration,
            translation = entity.translation,
            tafsirShort = entity.tafsirShort,
            juz = entity.juz,
            page = entity.page,
            absoluteAyahIndex = entity.absoluteNumber
        )
    }

    suspend fun getActiveKhatmaPlanOnce(): KhatmaPlanEntity? = dao.getActiveKhatmaPlanOnce()

    suspend fun saveKhatmaPlan(plan: KhatmaPlanEntity) {
        dao.saveKhatmaPlan(plan)
    }

    suspend fun deleteActiveKhatmaPlan() {
        dao.deleteActiveKhatmaPlan()
    }

    suspend fun recordKhatmaHistory(history: KhatmaHistoryEntity) {
        dao.insertKhatmaHistory(history)
    }

    fun isFavorite(title: String, arabicText: String): Flow<Boolean> = dao.isFavorite(title, arabicText)

    suspend fun toggleFavorite(
        type: String,
        title: String,
        arabicText: String,
        translation: String,
        source: String
    ) {
        val isFav = dao.isFavorite(title, arabicText).firstOrNull() ?: false
        if (isFav) {
            dao.deleteFavoriteByContent(title, arabicText)
        } else {
            dao.insertFavorite(
                FavoriteItemEntity(
                    type = type,
                    title = title,
                    arabicText = arabicText,
                    translation = translation,
                    source = source
                )
            )
        }
    }

    suspend fun removeFavorite(item: FavoriteItemEntity) {
        dao.deleteFavorite(item)
    }

    suspend fun initDefaultHabitsIfEmpty() {
        val existing = dao.getAllHabits().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val defaults = listOf(
                DailyHabitEntity(
                    title = "Fajr Prayer & Morning Adhkar",
                    targetCount = 1,
                    currentCount = 0,
                    isCompleted = false,
                    category = "Prayer",
                    iconType = "sun",
                    pinnedToPlanner = true,
                    pinnedDaysMask = 127,
                    scheduledTimeMinutes = 315, // 05:15 AM
                    isAlarmEnabled = true
                ),
                DailyHabitEntity(
                    title = "Morning Quran Surah Al-Kahf / Ya-Sin",
                    targetCount = 1,
                    currentCount = 0,
                    isCompleted = false,
                    category = "Quran",
                    iconType = "book",
                    pinnedToPlanner = true,
                    pinnedDaysMask = 127,
                    scheduledTimeMinutes = 450, // 07:30 AM
                    isAlarmEnabled = true
                ),
                DailyHabitEntity(
                    title = "Duha Prayer (Salat al-Duha)",
                    targetCount = 1,
                    currentCount = 0,
                    isCompleted = false,
                    category = "Sunnah",
                    iconType = "sun",
                    pinnedToPlanner = true,
                    pinnedDaysMask = 127,
                    scheduledTimeMinutes = 570, // 09:30 AM
                    isAlarmEnabled = true
                ),
                DailyHabitEntity(
                    title = "Dhuhr Prayer & Midday Reflection",
                    targetCount = 1,
                    currentCount = 0,
                    isCompleted = false,
                    category = "Prayer",
                    iconType = "sun",
                    pinnedToPlanner = true,
                    pinnedDaysMask = 127,
                    scheduledTimeMinutes = 750, // 12:30 PM
                    isAlarmEnabled = true
                ),
                DailyHabitEntity(
                    title = "Afternoon Hifz Quran Revision",
                    targetCount = 1,
                    currentCount = 0,
                    isCompleted = false,
                    category = "Quran",
                    iconType = "star",
                    pinnedToPlanner = true,
                    pinnedDaysMask = 127,
                    scheduledTimeMinutes = 945, // 03:45 PM
                    isAlarmEnabled = true
                ),
                DailyHabitEntity(
                    title = "Asr Prayer & Evening Istighfar",
                    targetCount = 1,
                    currentCount = 0,
                    isCompleted = false,
                    category = "Prayer",
                    iconType = "tasbih",
                    pinnedToPlanner = true,
                    pinnedDaysMask = 127,
                    scheduledTimeMinutes = 990, // 04:30 PM
                    isAlarmEnabled = true
                ),
                DailyHabitEntity(
                    title = "Evening Adhkar & Maghrib Dua",
                    targetCount = 1,
                    currentCount = 0,
                    isCompleted = false,
                    category = "Dhikr",
                    iconType = "moon",
                    pinnedToPlanner = true,
                    pinnedDaysMask = 127,
                    scheduledTimeMinutes = 1095, // 06:15 PM
                    isAlarmEnabled = true
                ),
                DailyHabitEntity(
                    title = "Isha Prayer & 100x Salawat",
                    targetCount = 100,
                    currentCount = 0,
                    isCompleted = false,
                    category = "Dhikr",
                    iconType = "tasbih",
                    pinnedToPlanner = true,
                    pinnedDaysMask = 127,
                    scheduledTimeMinutes = 1215, // 08:15 PM
                    isAlarmEnabled = true
                ),
                DailyHabitEntity(
                    title = "Surah Al-Mulk Before Sleep",
                    targetCount = 1,
                    currentCount = 0,
                    isCompleted = false,
                    category = "Quran",
                    iconType = "moon",
                    pinnedToPlanner = true,
                    pinnedDaysMask = 127,
                    scheduledTimeMinutes = 1290, // 09:30 PM
                    isAlarmEnabled = true
                )
            )
            dao.insertHabits(defaults)
        } else {
            // Backwards-compatibility upgrade: if existing habits are untimed or unpinned, populate mock schedule times and alarms
            val updated = existing.mapIndexed { index, habit ->
                var h = habit
                if (!h.pinnedToPlanner) {
                    h = h.copy(pinnedToPlanner = true, pinnedDaysMask = 127)
                }
                if (h.scheduledTimeMinutes == null) {
                    val time = when (index % 6) {
                        0 -> 315  // 05:15 AM
                        1 -> 570  // 09:30 AM
                        2 -> 750  // 12:30 PM
                        3 -> 990  // 04:30 PM
                        4 -> 1095 // 06:15 PM
                        else -> 1215 // 08:15 PM
                    }
                    h = h.copy(scheduledTimeMinutes = time, isAlarmEnabled = true)
                }
                h
            }
            dao.insertHabits(updated)
        }

        val existingQada = prayerDao.getAllQadaRecords().firstOrNull()
        if (existingQada.isNullOrEmpty()) {
            val defaultQada = listOf(
                QadaRecordEntity(prayerType = "Fajr", totalMissed = 0, completedMadeUp = 0),
                QadaRecordEntity(prayerType = "Dhuhr", totalMissed = 0, completedMadeUp = 0),
                QadaRecordEntity(prayerType = "Asr", totalMissed = 0, completedMadeUp = 0),
                QadaRecordEntity(prayerType = "Maghrib", totalMissed = 0, completedMadeUp = 0),
                QadaRecordEntity(prayerType = "Isha", totalMissed = 0, completedMadeUp = 0),
                QadaRecordEntity(prayerType = "Witr", totalMissed = 0, completedMadeUp = 0)
            )
            prayerDao.saveQadaRecords(defaultQada)
        }

        val existingFasts = dao.getAllFastLogs().firstOrNull()
        if (existingFasts.isNullOrEmpty()) {
            val defaultFasts = listOf(
                FastLogEntity(
                    type = "RAMADAN_MAKEUP",
                    title = "Ramadan Make-Up Fast #1",
                    subtitle = "Obligatory Qada for missed Ramadan day",
                    isCompleted = false
                ),
                FastLogEntity(
                    type = "MONDAY_THURSDAY",
                    title = "Monday Sunnah Fast",
                    subtitle = "Deeds are presented on Mondays (Sunnah)",
                    isCompleted = false
                ),
                FastLogEntity(
                    type = "MONDAY_THURSDAY",
                    title = "Thursday Sunnah Fast",
                    subtitle = "Deeds are presented on Thursdays (Sunnah)",
                    isCompleted = false
                ),
                FastLogEntity(
                    type = "WHITE_DAYS",
                    title = "Ayyam al-Beed (13th, 14th, 15th)",
                    subtitle = "Fasting three days of every lunar month",
                    isCompleted = false
                ),
                FastLogEntity(
                    type = "SHAWWAL",
                    title = "6 Days of Shawwal Fast #1",
                    subtitle = "Reward of fasting the entire year",
                    isCompleted = false
                ),
                FastLogEntity(
                    type = "ARAFAH",
                    title = "Day of Arafah (9th Dhul-Hijjah)",
                    subtitle = "Expiates sins of previous and coming year",
                    isCompleted = false
                ),
                FastLogEntity(
                    type = "ASHURA",
                    title = "Day of Ashura (10th Muharram)",
                    subtitle = "Expiates sins of the previous year",
                    isCompleted = false
                )
            )
            dao.insertFastLogs(defaultFasts)
        }
    }

    suspend fun updateQadaMissedCount(prayerType: String, delta: Int) {
        val existing = prayerDao.getAllQadaRecords().firstOrNull()?.find { it.prayerType == prayerType }
        val currentMissed = existing?.totalMissed ?: 0
        val currentMadeUp = existing?.completedMadeUp ?: 0
        val newMissed = (currentMissed + delta).coerceAtLeast(0)
        prayerDao.saveQadaRecord(
            QadaRecordEntity(
                prayerType = prayerType,
                totalMissed = newMissed,
                completedMadeUp = currentMadeUp,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun completeQadaMadeUp(prayerType: String) {
        val existing = prayerDao.getAllQadaRecords().firstOrNull()?.find { it.prayerType == prayerType }
        val currentMissed = existing?.totalMissed ?: 0
        val currentMadeUp = existing?.completedMadeUp ?: 0
        val newMissed = (currentMissed - 1).coerceAtLeast(0)
        val newMadeUp = currentMadeUp + 1
        prayerDao.saveQadaRecord(
            QadaRecordEntity(
                prayerType = prayerType,
                totalMissed = newMissed,
                completedMadeUp = newMadeUp,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun decrementQadaMadeUp(prayerType: String) {
        val existing = prayerDao.getAllQadaRecords().firstOrNull()?.find { it.prayerType == prayerType }
        val currentMissed = existing?.totalMissed ?: 0
        val currentMadeUp = existing?.completedMadeUp ?: 0
        val newMadeUp = (currentMadeUp - 1).coerceAtLeast(0)
        prayerDao.saveQadaRecord(
            QadaRecordEntity(
                prayerType = prayerType,
                totalMissed = currentMissed,
                completedMadeUp = newMadeUp,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleFastLogCompleted(fastLog: FastLogEntity) {
        val todayStr = getTodayDateString()
        val newCompleted = !fastLog.isCompleted
        val updated = fastLog.copy(
            isCompleted = newCompleted,
            completedDateIso = if (newCompleted) todayStr else ""
        )
        dao.updateFastLog(updated)
    }

    suspend fun addFastLog(title: String, type: String, subtitle: String = "", note: String = "") {
        dao.insertFastLog(
            FastLogEntity(
                type = type,
                title = title,
                subtitle = subtitle,
                isCompleted = false,
                note = note
            )
        )
    }

    suspend fun deleteFastLog(fastLog: FastLogEntity) {
        dao.deleteFastLog(fastLog)
    }

    suspend fun updateHabitProgress(habit: DailyHabitEntity, newCount: Int) {
        val count = newCount.coerceIn(0, habit.targetCount)
        val isDone = count >= habit.targetCount
        val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val updatedDateIso = if (isDone) {
            todayStr
        } else if (habit.completedDateIso == todayStr) {
            null
        } else {
            habit.completedDateIso
        }
        val updated = habit.copy(
            currentCount = count,
            isCompleted = isDone,
            completedDateIso = updatedDateIso
        )
        dao.updateHabit(updated)
    }

    suspend fun updateHabit(habit: DailyHabitEntity) {
        dao.updateHabit(habit)
    }

    suspend fun addCustomHabit(
        title: String,
        targetCount: Int,
        category: String,
        pinnedToPlanner: Boolean = false,
        pinnedDaysMask: Int = 127,
        scheduledTimeMinutes: Int? = null,
        isAlarmEnabled: Boolean = false,
        targetDateIso: String? = null,
        notes: String? = null
    ): Long {
        return dao.insertHabit(
            DailyHabitEntity(
                title = title,
                targetCount = targetCount,
                currentCount = 0,
                isCompleted = false,
                category = category,
                iconType = "star",
                pinnedToPlanner = pinnedToPlanner,
                pinnedDaysMask = pinnedDaysMask,
                scheduledTimeMinutes = scheduledTimeMinutes,
                isAlarmEnabled = isAlarmEnabled,
                targetDateIso = targetDateIso,
                notes = notes
            )
        )
    }

    suspend fun deleteHabit(habit: DailyHabitEntity) {
        dao.deleteHabit(habit)
    }

    suspend fun saveReadingProgress(surahNumber: Int, surahName: String, ayahNumber: Int, totalAyahs: Int) {
        dao.saveReadingProgress(
            ReadingProgressEntity(
                id = 1,
                surahNumber = surahNumber,
                surahName = surahName,
                ayahNumber = ayahNumber,
                totalAyahs = totalAyahs,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearReadingProgress() {
        dao.clearReadingProgress()
    }

    suspend fun updateTasbih(dhikrName: String, count: Int, target: Int) {
        val existing = tasbihDao.getTasbihRecord(dhikrName)
        val total = (existing?.totalAllTime ?: 0) + 1
        val record = TasbihRecordEntity(
            dhikrName = dhikrName,
            currentCount = count,
            targetCount = target,
            totalAllTime = total
        )
        tasbihDao.saveTasbihRecord(record)
    }

    suspend fun resetTasbih(dhikrName: String, target: Int) {
        val existing = tasbihDao.getTasbihRecord(dhikrName)
        val record = TasbihRecordEntity(
            dhikrName = dhikrName,
            currentCount = 0,
            targetCount = target,
            totalAllTime = existing?.totalAllTime ?: 0
        )
        tasbihDao.saveTasbihRecord(record)
    }

    fun getFajrDayLocalDate(
        zone: PrayerZone = prayerZones.first(),
        authority: CalculationAuthority = calculationAuthorities.first(),
        isHanafiAsr: Boolean = false,
        minuteOffsets: Map<String, Int> = emptyMap()
    ): LocalDate {
        val tz = java.util.TimeZone.getTimeZone(zone.timeZoneId)
        val now = Calendar.getInstance(tz)
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val today = LocalDate.now()
        val times = PrayerCalculator.calculatePrayerTimesList(
            zone = zone,
            authority = authority,
            isHanafiAsr = isHanafiAsr,
            date = today,
            minuteOffsets = minuteOffsets
        )
        val fajrTime = times.find { it.name == "Fajr" }
        val fajrMinutes = if (fajrTime != null) fajrTime.hour * 60 + fajrTime.minute else (5 * 60 + 15)
        return if (currentMinutes < fajrMinutes) {
            today.minusDays(1)
        } else {
            today
        }
    }

    fun getFajrDayDateString(zone: PrayerZone = prayerZones.first()): String {
        return getFajrDayLocalDate(zone).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    fun getTodayDateString(): String {
        return getFajrDayDateString()
    }

    fun getPrayerRecordsForToday(zone: PrayerZone = prayerZones.first()): Flow<List<PrayerRecordEntity>> {
        return prayerDao.getPrayerRecordsForDate(getFajrDayDateString(zone))
    }

    suspend fun getCompletedPrayersForFajrDay(zone: PrayerZone = prayerZones.first()): Set<String> {
        val date = getFajrDayDateString(zone)
        val records = prayerDao.getPrayerRecordsForDateOnce(date)
        return records.filter { it.isCompleted && !it.prayerName.equals("Sunrise", ignoreCase = true) }.map { it.prayerName }.toSet()
    }

    suspend fun setPrayerCompleted(prayerName: String, isCompleted: Boolean, zone: PrayerZone = prayerZones.first()) {
        if (prayerName.equals("Sunrise", ignoreCase = true) || prayerName.equals("الشروق", ignoreCase = true)) {
            return
        }
        val date = getFajrDayDateString(zone)
        val record = PrayerRecordEntity(
            date = date,
            prayerName = prayerName,
            isCompleted = isCompleted,
            completedAt = if (isCompleted) System.currentTimeMillis() else 0L
        )
        prayerDao.insertOrUpdatePrayer(record)
        // Synchronize with unified streak system for the same Fajr-to-Fajr day
        val todayRecords = prayerDao.getPrayerRecordsForDateOnce(date)
        val hasAnyCompleted = todayRecords.filter { !it.prayerName.equals("Sunrise", ignoreCase = true) }.any { it.isCompleted }
        recordSalatActivity(hasAnyCompleted, date)
    }

    val prayerZones: List<PrayerZone>
        get() = Companion.prayerZones

    val calculationAuthorities: List<CalculationAuthority>
        get() = Companion.calculationAuthorities

    fun calculatePrayerTimes(
        zone: PrayerZone = prayerZones.first(),
        authority: CalculationAuthority = calculationAuthorities.first(),
        isHanafiAsr: Boolean = false,
        date: LocalDate = LocalDate.now(),
        minuteOffsets: Map<String, Int> = emptyMap()
    ): List<PrayerTime> {
        return PrayerCalculator.calculatePrayerTimesList(
            zone = zone,
            authority = authority,
            isHanafiAsr = isHanafiAsr,
            date = date,
            minuteOffsets = minuteOffsets
        )
    }

    fun calculateSupplementaryTimes(
        zone: PrayerZone = prayerZones.first(),
        authority: CalculationAuthority = calculationAuthorities.first(),
        isHanafiAsr: Boolean = false,
        date: LocalDate = LocalDate.now(),
        minuteOffsets: Map<String, Int> = emptyMap()
    ): PrayerCalculator.SupplementaryTimes {
        return PrayerCalculator.calculateSupplementaryTimes(
            zone = zone,
            authority = authority,
            isHanafiAsr = isHanafiAsr,
            date = date,
            minuteOffsets = minuteOffsets
        )
    }

    // Static data helpers
    fun getReciters(): List<Reciter> = QuranData.reciters
    fun getDailyAyah(): DuaItem = DuaData.dailyAyah
    fun getDailyDua(): DuaItem = DuaData.dailyDua
    fun getCategorizedDuas(): List<DuaItem> = DuaData.categorizedDuas

    // ============================================================
    // UNIFIED STREAK SYSTEM METHODS
    // ============================================================

    suspend fun initDefaultStreaksIfEmpty() {
        val existingLogs = dao.getAllStreakDailyLogs().firstOrNull()
        if (existingLogs.isNullOrEmpty()) {
            val initialLogs = StreakEngine.generateInitialSeedLogs()
            dao.saveStreakDailyLogs(initialLogs)
            val initialSummary = StreakSummaryEntity(
                id = 1,
                freezesRemaining = 2,
                lastMonthReset = StreakEngine.getCurrentMonthString(),
                longestStreakEver = 14
            )
            dao.saveStreakSummary(initialSummary)
        } else {
            // Check if month changed to reset freezes
            val currentMonth = StreakEngine.getCurrentMonthString()
            val summary = dao.getStreakSummaryOnce()
            if (summary == null) {
                dao.saveStreakSummary(
                    StreakSummaryEntity(
                        id = 1,
                        freezesRemaining = 2,
                        lastMonthReset = currentMonth,
                        longestStreakEver = 0
                    )
                )
            } else if (summary.lastMonthReset != currentMonth) {
                dao.saveStreakSummary(
                    summary.copy(
                        freezesRemaining = 2,
                        lastMonthReset = currentMonth
                    )
                )
            }
        }
    }

    suspend fun recordActivity(activityType: StreakActivityType, dateStr: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) {
        val existingLog = dao.getStreakDailyLog(dateStr)
        val updatedLog = if (existingLog != null) {
            when (activityType) {
                StreakActivityType.SALAT -> existingLog.copy(salatCompleted = true, updatedAt = System.currentTimeMillis())
                StreakActivityType.QURAN -> existingLog.copy(quranCompleted = true, updatedAt = System.currentTimeMillis())
                StreakActivityType.AZKAR -> existingLog.copy(azkarCompleted = true, updatedAt = System.currentTimeMillis())
                StreakActivityType.DUA -> existingLog.copy(duaCompleted = true, updatedAt = System.currentTimeMillis())
                StreakActivityType.TASBIH -> existingLog.copy(tasbihCompleted = true, updatedAt = System.currentTimeMillis())
            }
        } else {
            StreakDailyLogEntity(
                date = dateStr,
                salatCompleted = activityType == StreakActivityType.SALAT,
                quranCompleted = activityType == StreakActivityType.QURAN,
                azkarCompleted = activityType == StreakActivityType.AZKAR,
                duaCompleted = activityType == StreakActivityType.DUA,
                tasbihCompleted = activityType == StreakActivityType.TASBIH,
                isFreezeUsed = false,
                isExcused = false,
                excuseReason = "",
                updatedAt = System.currentTimeMillis()
            )
        }
        dao.saveStreakDailyLog(updatedLog)
    }

    suspend fun setActivityForDay(dateStr: String, activityType: StreakActivityType, isCompleted: Boolean) {
        val existingLog = dao.getStreakDailyLog(dateStr)
        val updatedLog = if (existingLog != null) {
            when (activityType) {
                StreakActivityType.SALAT -> existingLog.copy(salatCompleted = isCompleted, updatedAt = System.currentTimeMillis())
                StreakActivityType.QURAN -> existingLog.copy(quranCompleted = isCompleted, updatedAt = System.currentTimeMillis())
                StreakActivityType.AZKAR -> existingLog.copy(azkarCompleted = isCompleted, updatedAt = System.currentTimeMillis())
                StreakActivityType.DUA -> existingLog.copy(duaCompleted = isCompleted, updatedAt = System.currentTimeMillis())
                StreakActivityType.TASBIH -> existingLog.copy(tasbihCompleted = isCompleted, updatedAt = System.currentTimeMillis())
            }
        } else {
            StreakDailyLogEntity(
                date = dateStr,
                salatCompleted = (activityType == StreakActivityType.SALAT && isCompleted),
                quranCompleted = (activityType == StreakActivityType.QURAN && isCompleted),
                azkarCompleted = (activityType == StreakActivityType.AZKAR && isCompleted),
                duaCompleted = (activityType == StreakActivityType.DUA && isCompleted),
                tasbihCompleted = (activityType == StreakActivityType.TASBIH && isCompleted),
                isFreezeUsed = false,
                isExcused = false,
                excuseReason = "",
                updatedAt = System.currentTimeMillis()
            )
        }
        dao.saveStreakDailyLog(updatedLog)
    }

    suspend fun excuseDay(dateStr: String, reason: String = "Travel") {
        val existingLog = dao.getStreakDailyLog(dateStr)
        val updatedLog = existingLog?.copy(
            isExcused = true,
            excuseReason = reason,
            updatedAt = System.currentTimeMillis()
        ) ?: StreakDailyLogEntity(
            date = dateStr,
            isExcused = true,
            excuseReason = reason,
            updatedAt = System.currentTimeMillis()
        )
        dao.saveStreakDailyLog(updatedLog)
    }

    suspend fun removeDayExcuse(dateStr: String) {
        val existingLog = dao.getStreakDailyLog(dateStr) ?: return
        val updatedLog = existingLog.copy(
            isExcused = false,
            excuseReason = "",
            updatedAt = System.currentTimeMillis()
        )
        dao.saveStreakDailyLog(updatedLog)
    }

    suspend fun recordSalatActivity(isCompleted: Boolean, dateStr: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) {
        val existingLog = dao.getStreakDailyLog(dateStr)
        val updatedLog = if (existingLog != null) {
            existingLog.copy(salatCompleted = isCompleted, updatedAt = System.currentTimeMillis())
        } else {
            StreakDailyLogEntity(
                date = dateStr,
                salatCompleted = isCompleted,
                updatedAt = System.currentTimeMillis()
            )
        }
        dao.saveStreakDailyLog(updatedLog)
    }

    suspend fun recordQuranActivity() = recordActivity(StreakActivityType.QURAN)
    suspend fun recordAzkarActivity() = recordActivity(StreakActivityType.AZKAR)
    suspend fun recordDuaActivity() = recordActivity(StreakActivityType.DUA)
    suspend fun recordTasbihActivity() = recordActivity(StreakActivityType.TASBIH)

    suspend fun useStreakFreeze(): Boolean {
        val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val yesterdayStr = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val summary = dao.getStreakSummaryOnce() ?: StreakSummaryEntity()
        val currentMonth = StreakEngine.getCurrentMonthString()
        val freezesLeft = if (summary.lastMonthReset != currentMonth) 2 else summary.freezesRemaining

        if (freezesLeft <= 0) return false

        // Determine if yesterday is the day to freeze or today
        val yesterdayLog = dao.getStreakDailyLog(yesterdayStr)
        val todayLog = dao.getStreakDailyLog(todayStr)

        val targetDateStr = if (yesterdayLog == null || (!yesterdayLog.salatCompleted && !yesterdayLog.quranCompleted && !yesterdayLog.azkarCompleted && !yesterdayLog.duaCompleted && !yesterdayLog.tasbihCompleted && !yesterdayLog.isFreezeUsed && !yesterdayLog.isExcused)) {
            yesterdayStr
        } else {
            todayStr
        }

        val targetLog = dao.getStreakDailyLog(targetDateStr)
        val newTargetLog = targetLog?.copy(isFreezeUsed = true, updatedAt = System.currentTimeMillis())
            ?: StreakDailyLogEntity(date = targetDateStr, isFreezeUsed = true, updatedAt = System.currentTimeMillis())

        dao.saveStreakDailyLog(newTargetLog)
        dao.saveStreakSummary(
            summary.copy(
                freezesRemaining = (freezesLeft - 1).coerceAtLeast(0),
                lastMonthReset = currentMonth,
                updatedAt = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun clearAllUserData() {
        dao.clearAllFavorites()
        dao.clearAllHabits()
        dao.clearReadingProgress()
        dao.clearAllKhatmaPlans()
        dao.clearAllKhatmaHistory()
        dao.clearAllFastLogs()
        dao.clearAllStreakDailyLogs()
        dao.clearStreakSummary()
        dao.clearAllQuranNotes()
        dao.clearAllHifzEvents()
        prayerDao.clearAllPrayerRecords()
        prayerDao.clearAllQadaRecords()
        tasbihDao.clearAllTasbihRecords()
        quranBookmarkDao.clearAllBookmarks()
    }

    // Quran Reading Sessions
    suspend fun saveQuranReadingSession(session: QuranReadingSessionEntity): Long {
        return dao.insertQuranReadingSession(session)
    }

    fun getQuranReadingTimeForDay(date: String): Flow<Int?> {
        return dao.getQuranReadingTimeForDay(date)
    }

    fun getQuranReadingTimeInRange(startDate: String, endDate: String): Flow<Int?> {
        return dao.getQuranReadingTimeInRange(startDate, endDate)
    }

    fun getTotalQuranReadingTime(): Flow<Int?> {
        return dao.getTotalQuranReadingTime()
    }
}
