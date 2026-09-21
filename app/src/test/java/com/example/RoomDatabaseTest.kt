package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.PrayerDao
import com.example.data.local.PrayerRecordEntity
import com.example.data.local.QadaRecordEntity
import com.example.data.local.QuranBookmarkDao
import com.example.data.local.QuranBookmarkEntity
import com.example.data.local.TasbihDao
import com.example.data.local.TasbihRecordEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var bookmarkDao: QuranBookmarkDao
    private lateinit var tasbihDao: TasbihDao
    private lateinit var prayerDao: PrayerDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        bookmarkDao = db.quranBookmarkDao()
        tasbihDao = db.tasbihDao()
        prayerDao = db.prayerDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testQuranBookmarkInsertAndQuery() = runBlocking {
        val bookmark = QuranBookmarkEntity(
            surahNumber = 1,
            verseNumber = 2,
            surahName = "Al-Fatiha",
            surahNameArabic = "الفاتحة",
            arabicText = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
            translation = "All praise is due to Allah, Lord of the worlds.",
            colorTag = "gold",
            juz = 1,
            page = 1
        )

        val id = bookmarkDao.insertBookmark(bookmark)
        assertTrue(id > 0)

        val isBookmarked = bookmarkDao.isBookmarkedOnce(1, 2)
        assertTrue(isBookmarked)

        val retrieved = bookmarkDao.getBookmarkOnce(1, 2)
        assertNotNull(retrieved)
        assertEquals("Al-Fatiha", retrieved?.surahName)
        assertEquals("gold", retrieved?.colorTag)

        val allBookmarks = bookmarkDao.getAllBookmarks().first()
        assertEquals(1, allBookmarks.size)

        // Delete bookmark
        bookmarkDao.deleteBookmarkByAyah(1, 2)
        val isBookmarkedAfterDelete = bookmarkDao.isBookmarkedOnce(1, 2)
        assertFalse(isBookmarkedAfterDelete)
    }

    @Test
    fun testTasbihCountInsertIncrementAndReset() = runBlocking {
        val record = TasbihRecordEntity(
            dhikrName = "SubhanAllah",
            currentCount = 33,
            targetCount = 33,
            totalAllTime = 33
        )

        tasbihDao.saveTasbihRecord(record)

        val fetched = tasbihDao.getTasbihRecord("SubhanAllah")
        assertNotNull(fetched)
        assertEquals(33, fetched?.currentCount)
        assertEquals(33, fetched?.totalAllTime)

        // Reset Tasbih count
        tasbihDao.resetTasbihCount("SubhanAllah")
        val resetRecord = tasbihDao.getTasbihRecord("SubhanAllah")
        assertEquals(0, resetRecord?.currentCount)
        assertEquals(33, resetRecord?.totalAllTime)
    }

    @Test
    fun testPrayerRecordAndQadaTracking() = runBlocking {
        val date = "2026-09-11"
        val prayerRecord = PrayerRecordEntity(
            date = date,
            prayerName = "Fajr",
            isCompleted = true,
            completedAt = System.currentTimeMillis()
        )

        prayerDao.insertOrUpdatePrayer(prayerRecord)

        val records = prayerDao.getPrayerRecordsForDateOnce(date)
        assertEquals(1, records.size)
        assertEquals("Fajr", records[0].prayerName)
        assertTrue(records[0].isCompleted)

        val qada = QadaRecordEntity(
            prayerType = "Fajr",
            totalMissed = 5,
            completedMadeUp = 2
        )

        prayerDao.saveQadaRecord(qada)

        val qadaFetched = prayerDao.getQadaRecord("Fajr")
        assertNotNull(qadaFetched)
        assertEquals(5, qadaFetched?.totalMissed)
        assertEquals(2, qadaFetched?.completedMadeUp)
    }
}
