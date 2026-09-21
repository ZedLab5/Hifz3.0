package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.quran.QuranDataLoader
import com.example.data.repository.NoorRepository
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class QuranDataIntegrityTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var repository: NoorRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = NoorRepository(
            dao = db.noorDao(),
            quranBookmarkDao = db.quranBookmarkDao(),
            tasbihDao = db.tasbihDao(),
            prayerDao = db.prayerDao()
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testQuranJsonAssetsIntegrity() {
        val assetFiles = listOf(
            "quran/quran_complete.json"
        )

        for (fileName in assetFiles) {
            try {
                val content = context.assets.open(fileName).bufferedReader(Charsets.UTF_8).use { it.readText() }
                assertTrue("Asset $fileName should not be empty", content.isNotBlank())
                if (content.trim().startsWith("[")) {
                    val array = JSONArray(content)
                    assertTrue("Asset $fileName array should not be empty", array.length() > 0)
                    if (fileName == "quran/quran_complete.json") {
                        assertEquals(114, array.length())
                        var totalAyahs = 0
                        for (i in 0 until array.length()) {
                            val surah = array.getJSONObject(i)
                            val verses = surah.getJSONArray("verses")
                            totalAyahs += verses.length()
                        }
                        assertEquals(6236, totalAyahs)
                    }
                } else {
                    val obj = JSONObject(content)
                    assertTrue("Asset $fileName object should not be empty", obj.length() > 0)
                }
            } catch (e: Throwable) {
                fail("Failed on file $fileName: ${e.message}")
            }
        }
    }

    @Test
    fun testPreloadAndFetchVerses() = runBlocking {
        try {
            QuranDataLoader.preloadQuranIfNeeded(context, db.noorDao())
        } catch (e: Throwable) {
            fail("preloadQuranIfNeeded failed: ${e.message}")
        }

        val surahCount = db.noorDao().getSurahCount()
        val verseCount = db.noorDao().getTotalVerseCount()

        println("Surah count in DB: $surahCount, Verse count in DB: $verseCount")

        assertEquals(114, surahCount)
        assertEquals(6236, verseCount)

        // Verify Surah 1 (Al-Fatihah) has 7 verses with Arabic, translation, transliteration
        val surah1 = repository.getSurahWithVerses(1)
        assertNotNull(surah1)
        assertEquals(1, surah1.number)
        assertEquals("Al-Fatihah", surah1.nameEnglish)
        assertEquals(7, surah1.verses.size)
        assertTrue(surah1.verses[0].arabicText.isNotBlank())
        assertTrue(surah1.verses[0].translation.isNotBlank())

        // Verify Surah 114 (An-Nas) has 6 verses
        val surah114 = repository.getSurahWithVerses(114)
        assertNotNull(surah114)
        assertEquals(114, surah114.number)
        assertEquals(6, surah114.verses.size)
    }
}
