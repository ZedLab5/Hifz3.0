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
        HifzEventEntity::class
    ],
    version = 12,
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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noor_database.db"
                )
                .addMigrations(MIGRATION_10_11, MIGRATION_11_12)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
