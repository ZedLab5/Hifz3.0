package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteItemEntity::class,
        PrayerRecordEntity::class,
        DailyHabitEntity::class,
        ReadingProgressEntity::class,
        TasbihRecordEntity::class,
        KhatmaPlanEntity::class,
        KhatmaHistoryEntity::class,
        QadaRecordEntity::class,
        FastLogEntity::class,
        StreakDailyLogEntity::class,
        StreakSummaryEntity::class,
        SurahEntity::class,
        VerseEntity::class,
        QuranNoteEntity::class,
        QuranBookmarkEntity::class,
        HifzEventEntity::class,
        QuranReadingSessionEntity::class
    ],
    version = 13,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noorDao(): NoorDao
    abstract fun quranBookmarkDao(): QuranBookmarkDao
    abstract fun tasbihDao(): TasbihDao
    abstract fun prayerDao(): PrayerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_10_11 = object : androidx.room.migration.Migration(10, 11) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `hifz_events` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `surahNumber` INTEGER NOT NULL,
                        `ayahNumber` INTEGER NOT NULL,
                        `type` TEXT NOT NULL,
                        `mode` TEXT NOT NULL,
                        `timestampUtcMs` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_hifz_events_surahNumber_ayahNumber` ON `hifz_events` (`surahNumber`, `ayahNumber`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_hifz_events_timestampUtcMs` ON `hifz_events` (`timestampUtcMs`)")
            }
        }

        val MIGRATION_11_12 = object : androidx.room.migration.Migration(11, 12) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `daily_habits` ADD COLUMN `completedDateIso` TEXT DEFAULT NULL")
            }
        }

        val MIGRATION_12_13 = object : androidx.room.migration.Migration(12, 13) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `quran_reading_sessions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `date` TEXT NOT NULL,
                        `targetSeconds` INTEGER NOT NULL,
                        `elapsedSeconds` INTEGER NOT NULL,
                        `completed` INTEGER NOT NULL,
                        `startedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noor_database.db"
                )
                .addMigrations(MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
