package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface NoorDao {
    // Favorites
    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(item: FavoriteItemEntity): Long

    @Delete
    suspend fun deleteFavorite(item: FavoriteItemEntity)

    @Query("DELETE FROM favorites WHERE title = :title AND arabicText = :arabicText")
    suspend fun deleteFavoriteByContent(title: String, arabicText: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE title = :title OR arabicText = :arabicText)")
    fun isFavorite(title: String, arabicText: String): Flow<Boolean>

    // Daily Habits
    @Query("SELECT * FROM daily_habits")
    fun getAllHabits(): Flow<List<DailyHabitEntity>>

    @Query("SELECT * FROM daily_habits")
    suspend fun getAllHabitsOnce(): List<DailyHabitEntity>

    @Query("SELECT * FROM daily_habits WHERE id = :id LIMIT 1")
    suspend fun getHabitById(id: Long): DailyHabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: DailyHabitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<DailyHabitEntity>)

    @Update
    suspend fun updateHabit(habit: DailyHabitEntity)

    @Delete
    suspend fun deleteHabit(habit: DailyHabitEntity)

    // Reading Progress
    @Query("SELECT * FROM reading_progress WHERE id = 1 LIMIT 1")
    fun getReadingProgress(): Flow<ReadingProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReadingProgress(progress: ReadingProgressEntity)

    @Query("DELETE FROM reading_progress WHERE id = 1")
    suspend fun clearReadingProgress()

    // Khatma Active Plan
    @Query("SELECT * FROM khatma_plans WHERE id = 1 LIMIT 1")
    fun getActiveKhatmaPlan(): Flow<KhatmaPlanEntity?>

    @Query("SELECT * FROM khatma_plans WHERE id = 1 LIMIT 1")
    suspend fun getActiveKhatmaPlanOnce(): KhatmaPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveKhatmaPlan(plan: KhatmaPlanEntity)

    @Query("DELETE FROM khatma_plans WHERE id = 1")
    suspend fun deleteActiveKhatmaPlan()

    // Khatma History
    @Query("SELECT * FROM khatma_history ORDER BY completedAtTimestamp DESC")
    fun getAllKhatmaHistory(): Flow<List<KhatmaHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKhatmaHistory(history: KhatmaHistoryEntity): Long

    // Fast Logs
    @Query("SELECT * FROM fast_logs ORDER BY createdAt ASC")
    fun getAllFastLogs(): Flow<List<FastLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFastLog(fastLog: FastLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFastLogs(fastLogs: List<FastLogEntity>)

    @Update
    suspend fun updateFastLog(fastLog: FastLogEntity)

    @Delete
    suspend fun deleteFastLog(fastLog: FastLogEntity)

    // Streak Daily Logs
    @Query("SELECT * FROM streak_daily_logs ORDER BY date DESC")
    fun getAllStreakDailyLogs(): Flow<List<StreakDailyLogEntity>>

    @Query("SELECT * FROM streak_daily_logs WHERE date = :date LIMIT 1")
    suspend fun getStreakDailyLog(date: String): StreakDailyLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStreakDailyLog(log: StreakDailyLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStreakDailyLogs(logs: List<StreakDailyLogEntity>)

    // Streak Summary
    @Query("SELECT * FROM streak_summary WHERE id = 1 LIMIT 1")
    fun getStreakSummary(): Flow<StreakSummaryEntity?>

    @Query("SELECT * FROM streak_summary WHERE id = 1 LIMIT 1")
    suspend fun getStreakSummaryOnce(): StreakSummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStreakSummary(summary: StreakSummaryEntity)

    // Surahs (Metadata)
    @Query("SELECT * FROM surahs ORDER BY number ASC")
    fun getAllSurahsFlow(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs ORDER BY number ASC")
    suspend fun getAllSurahs(): List<SurahEntity>

    @Query("SELECT * FROM surahs WHERE number = :number LIMIT 1")
    suspend fun getSurahByNumber(number: Int): SurahEntity?

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)

    @Query("DELETE FROM surahs")
    suspend fun deleteAllSurahs()

    // Verses
    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber ORDER BY verseNumber ASC")
    fun getVersesForSurahFlow(surahNumber: Int): Flow<List<VerseEntity>>

    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber ORDER BY verseNumber ASC")
    suspend fun getVersesForSurah(surahNumber: Int): List<VerseEntity>

    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber AND verseNumber = :verseNumber LIMIT 1")
    suspend fun getVerse(surahNumber: Int, verseNumber: Int): VerseEntity?

    @Query("""
        SELECT * FROM verses 
        WHERE translation LIKE '%' || :query || '%' 
           OR arabicText LIKE '%' || :query || '%'
           OR transliteration LIKE '%' || :query || '%'
        ORDER BY surahNumber ASC, verseNumber ASC
        LIMIT 60
    """)
    suspend fun searchVerses(query: String): List<VerseEntity>

    @Query("SELECT COUNT(*) FROM verses")
    suspend fun getTotalVerseCount(): Int

    @Query("SELECT COUNT(*) FROM verses WHERE surahNumber = :surahNumber")
    suspend fun getVerseCountForSurah(surahNumber: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<VerseEntity>)

    @Query("DELETE FROM verses")
    suspend fun deleteAllVerses()

    // Quran Notes
    @Query("SELECT * FROM quran_notes ORDER BY updatedAt DESC")
    fun getAllQuranNotes(): Flow<List<QuranNoteEntity>>

    @Query("SELECT * FROM quran_notes WHERE surahNumber = :surahNumber AND verseNumber = :verseNumber LIMIT 1")
    suspend fun getNoteForVerse(surahNumber: Int, verseNumber: Int): QuranNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(note: QuranNoteEntity)

    @Query("DELETE FROM quran_notes WHERE surahNumber = :surahNumber AND verseNumber = :verseNumber")
    suspend fun deleteNote(surahNumber: Int, verseNumber: Int)

    // Hifz Events
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHifzEvent(event: HifzEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHifzEvents(events: List<HifzEventEntity>)

    @Query("SELECT * FROM hifz_events ORDER BY timestampUtcMs DESC")
    fun getAllHifzEvents(): Flow<List<HifzEventEntity>>

    @Query("SELECT * FROM hifz_events ORDER BY timestampUtcMs DESC")
    suspend fun getAllHifzEventsOnce(): List<HifzEventEntity>

    @Query("SELECT * FROM hifz_events WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC, timestampUtcMs DESC")
    fun getHifzEventsForSurah(surahNumber: Int): Flow<List<HifzEventEntity>>

    @Query("SELECT * FROM hifz_events WHERE type = 'MISSED' ORDER BY timestampUtcMs DESC")
    fun getMissedHifzEvents(): Flow<List<HifzEventEntity>>

    @Query("SELECT * FROM hifz_events WHERE timestampUtcMs BETWEEN :startMs AND :endMs ORDER BY timestampUtcMs ASC")
    fun getHifzEventsInRange(startMs: Long, endMs: Long): Flow<List<HifzEventEntity>>

    // Bulk user data clearing (Account / Data Reset)
    @Query("DELETE FROM favorites")
    suspend fun clearAllFavorites()

    @Query("DELETE FROM daily_habits")
    suspend fun clearAllHabits()

    @Query("DELETE FROM khatma_plans")
    suspend fun clearAllKhatmaPlans()

    @Query("DELETE FROM khatma_history")
    suspend fun clearAllKhatmaHistory()

    @Query("DELETE FROM fast_logs")
    suspend fun clearAllFastLogs()

    @Query("DELETE FROM streak_daily_logs")
    suspend fun clearAllStreakDailyLogs()

    @Query("DELETE FROM streak_summary")
    suspend fun clearStreakSummary()

    @Query("DELETE FROM quran_notes")
    suspend fun clearAllQuranNotes()

    @Query("DELETE FROM hifz_events")
    suspend fun clearAllHifzEvents()

    // Quran Reading Sessions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranReadingSession(session: QuranReadingSessionEntity): Long

    @Query("SELECT SUM(elapsedSeconds) FROM quran_reading_sessions WHERE date = :date")
    fun getQuranReadingTimeForDay(date: String): Flow<Int?>

    @Query("SELECT SUM(elapsedSeconds) FROM quran_reading_sessions WHERE date BETWEEN :startDate AND :endDate")
    fun getQuranReadingTimeInRange(startDate: String, endDate: String): Flow<Int?>

    @Query("SELECT SUM(elapsedSeconds) FROM quran_reading_sessions")
    fun getTotalQuranReadingTime(): Flow<Int?>
}
