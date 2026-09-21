package com.example.data.quran

import android.content.Context
import android.util.Log
import com.example.data.local.NoorDao
import com.example.data.local.SurahEntity
import com.example.data.local.VerseEntity
import com.example.data.repository.NoorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

object QuranDataLoader {
    private const val TAG = "QuranPreload"
    private const val ASSET_FILE = "quran/quran_complete.json"

    /**
     * Preloads complete verified Quran data into Room database on first launch.
     * Uses org.json with robust error handling and step-by-step logging.
     */
    suspend fun preloadQuranIfNeeded(context: Context, dao: NoorDao) = withContext(Dispatchers.IO) {
        AyahSplitRepository.init(context)
        var surahCount = 0
        var verseCount = 0

        // Stage 1: Check Database Preload Status
        try {
            Log.d(TAG, "Stage 1: Checking existing database surah and verse counts...")
            surahCount = dao.getSurahCount()
            verseCount = dao.getTotalVerseCount()
            Log.d(TAG, "Stage 1 Result: Database currently has surahCount=$surahCount, verseCount=$verseCount (Expected: 114 surahs, 6236 verses).")
        } catch (e: Exception) {
            Log.e(TAG, "Stage 1 Error: Failed to query database counts: ${e.message}", e)
        }

        if (surahCount == 114 && verseCount == 6236) {
            Log.i(TAG, "Quran database already fully initialized (surahCount=114, verseCount=6236). Validating integrity...")
            try {
                validateQuranDataIntegrity(dao)
            } catch (e: Exception) {
                Log.e(TAG, "Integrity validation error: ${e.message}", e)
            }
            val finalSurahCount = dao.getSurahCount()
            val finalVerseCount = dao.getTotalVerseCount()
            Log.i(TAG, "=== QURAN PRELOAD STATUS SUMMARY ===")
            Log.i(TAG, "Total Surahs in Room: $finalSurahCount / 114")
            Log.i(TAG, "Total Verses in Room: $finalVerseCount / 6236")
            Log.i(TAG, "=====================================")
            return@withContext
        } else {
            Log.w(TAG, "Quran database incomplete or corrupted (surahCount=$surahCount, verseCount=$verseCount). Clearing existing surahs/verses and re-running preload...")
            try {
                dao.deleteAllVerses()
                dao.deleteAllSurahs()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear corrupted Quran tables: ${e.message}", e)
            }
        }

        Log.i(TAG, "Stage 2: Initializing complete Quran database from assets...")
        val surahEntities = mutableListOf<SurahEntity>()
        val verseEntities = mutableListOf<VerseEntity>()

        // Stage 2: Open Asset and Parse JSON via org.json
        try {
            Log.d(TAG, "Stage 2.1: Opening asset file path: $ASSET_FILE")
            val jsonString = context.assets.open(ASSET_FILE).bufferedReader(Charsets.UTF_8).use { it.readText() }
            Log.d(TAG, "Stage 2.2: Successfully read asset file. Length: ${jsonString.length} chars. Parsing JSON array...")

            val jsonArray = JSONArray(jsonString)
            var parsedSurahs = 0
            var parsedVerses = 0

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val number = obj.optInt("number", 0)
                val nameEnglish = obj.optString("nameEnglish", "")
                val nameArabic = obj.optString("nameArabic", "")
                val englishMeaning = obj.optString("englishMeaning", "")
                val totalVerses = obj.optInt("totalVerses", 0)
                val revelationType = obj.optString("revelationType", "")

                surahEntities.add(
                    SurahEntity(
                        number = number,
                        nameArabic = nameArabic,
                        nameEnglish = nameEnglish,
                        englishMeaning = englishMeaning,
                        totalVerses = totalVerses,
                        revelationType = revelationType
                    )
                )
                parsedSurahs++

                val versesArray = obj.optJSONArray("verses") ?: JSONArray()
                for (j in 0 until versesArray.length()) {
                    val vObj = versesArray.getJSONObject(j)
                    val sNum = vObj.optInt("surahNumber", number)
                    val vNum = vObj.optInt("verseNumber", 0)
                    val absNum = vObj.optInt("absoluteNumber", 0)
                    val juz = vObj.optInt("juz", 1)
                    val page = vObj.optInt("page", 1)
                    val arText = vObj.optString("arabicText", "")
                    val trText = vObj.optString("transliteration", "")
                    val enText = vObj.optString("translation", "")
                    val tafsir = vObj.optString("tafsirShort", "")

                    verseEntities.add(
                        VerseEntity(
                            surahNumber = sNum,
                            verseNumber = vNum,
                            absoluteNumber = absNum,
                            juz = juz,
                            page = page,
                            arabicText = NoorRepository.sanitizeArabicVerseText(sNum, vNum, arText),
                            transliteration = trText,
                            translation = enText,
                            tafsirShort = tafsir
                        )
                    )
                    parsedVerses++
                }
            }
            Log.i(TAG, "Stage 2 Success: Successfully parsed $parsedSurahs surahs and $parsedVerses verses from JSON asset.")
        } catch (e: Exception) {
            Log.e(TAG, "Stage 2 Error: Failed to open or parse JSON asset: ${e.message}", e)
            return@withContext
        }

        // Stage 3: Room Database Insertion
        try {
            Log.d(TAG, "Stage 3.1: Inserting ${surahEntities.size} surahs into Room...")
            dao.insertSurahs(surahEntities)
            Log.d(TAG, "Stage 3.2: Surahs insertion completed. Inserting ${verseEntities.size} verses in chunks...")

            verseEntities.chunked(500).forEachIndexed { index, chunk ->
                dao.insertVerses(chunk)
                Log.d(TAG, "Stage 3.3: Inserted verse chunk ${index + 1} (${chunk.size} verses)")
            }
            Log.i(TAG, "Stage 3 Success: Successfully populated ${surahEntities.size} surahs and ${verseEntities.size} verses into Room.")
        } catch (e: Exception) {
            Log.e(TAG, "Stage 3 Error: Failed during Room database insertion: ${e.message}", e)
            return@withContext
        }

        // Stage 4: Integrity Validation
        try {
            Log.d(TAG, "Stage 4: Running post-insertion data integrity validation...")
            validateQuranDataIntegrity(dao)
        } catch (e: Exception) {
            Log.e(TAG, "Stage 4 Error: Integrity validation failed: ${e.message}", e)
        }

        val finalSurahCount = dao.getSurahCount()
        val finalVerseCount = dao.getTotalVerseCount()
        Log.i(TAG, "=== QURAN PRELOAD STATUS SUMMARY ===")
        Log.i(TAG, "Total Surahs in Room: $finalSurahCount / 114")
        Log.i(TAG, "Total Verses in Room: $finalVerseCount / 6236")
        Log.i(TAG, "=====================================")
    }

    /**
     * Debug validation function confirming:
     * 1. Exactly 114 surahs
     * 2. Exactly 6,236 total ayahs
     * 3. Each surah's ayah count matches the canonical count
     */
    suspend fun validateQuranDataIntegrity(dao: NoorDao) = withContext(Dispatchers.IO) {
        val surahs = dao.getAllSurahs()
        val totalAyahs = dao.getTotalVerseCount()

        if (surahs.size != 114) {
            Log.w(TAG, "INTEGRITY WARNING: Expected exactly 114 surahs, but found ${surahs.size} in database!")
        }
        if (totalAyahs != 6236) {
            Log.w(TAG, "INTEGRITY WARNING: Expected exactly 6,236 verses, but found $totalAyahs in database!")
        }

        var mismatchCount = 0
        for (meta in QuranData.canonicalSurahs) {
            val countInDb = dao.getVerseCountForSurah(meta.number)
            if (countInDb != meta.totalVerses) {
                Log.w(
                    TAG,
                    "INTEGRITY WARNING: Surah ${meta.number} (${meta.nameEnglish}) verse count mismatch! Expected ${meta.totalVerses}, but DB has $countInDb verses."
                )
                mismatchCount++
            }
        }

        if (mismatchCount == 0 && surahs.size == 114 && totalAyahs == 6236) {
            Log.i(TAG, "INTEGRITY CHECK PASSED: Exactly 114 surahs and 6,236 canonical ayahs verified in Room.")
        }
    }
}
