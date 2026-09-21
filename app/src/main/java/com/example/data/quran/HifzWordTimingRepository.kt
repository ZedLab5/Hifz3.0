package com.example.data.quran

import android.content.Context
import android.util.JsonReader
import android.util.Log
import com.example.data.model.HifzAyahTiming
import com.example.data.model.HifzWordSegment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap

/**
 * Repository responsible for loading and querying word-level audio segment timing data
 * from assets for Hifz (memorization) drills.
 */
object HifzWordTimingRepository {
    private const val TAG = "HifzWordTimingRepo"
    private const val HIFZ_ASSETS_DIR = "quran/hifz"

    // In-memory cache: reciterId -> Map<"surah:ayah", HifzAyahTiming>
    private val reciterTimingsCache = ConcurrentHashMap<String, Map<String, HifzAyahTiming>>()

    /**
     * Checks if word timing data is available for the specified reciter ID.
     */
    fun isReciterSupported(context: Context, reciterId: String): Boolean {
        val normalized = normalizeReciterId(reciterId)
        if (reciterTimingsCache.containsKey(normalized)) return true
        return try {
            val assetPath = "$HIFZ_ASSETS_DIR/word_segments_$normalized.json"
            context.assets.open(assetPath).close()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Lists all reciter IDs currently supported with word segment timing assets.
     */
    fun getSupportedReciters(context: Context): List<String> {
        val list = mutableListOf<String>()
        try {
            val files = context.assets.list(HIFZ_ASSETS_DIR) ?: emptyArray()
            for (file in files) {
                if (file.startsWith("word_segments_") && file.endsWith(".json")) {
                    val id = file.removePrefix("word_segments_").removeSuffix(".json")
                    list.add(id)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error listing hifz assets: ${e.message}", e)
        }
        if (list.isEmpty()) {
            list.add("alafasy")
        }
        return list
    }

    /**
     * Retrieves word timing and audio URL for a single Ayah.
     */
    suspend fun getAyahTiming(
        context: Context,
        reciterId: String = "alafasy",
        surahNumber: Int,
        ayahNumber: Int
    ): HifzAyahTiming? = withContext(Dispatchers.IO) {
        val reciter = normalizeReciterId(reciterId)
        val timings = loadTimingsForReciter(context, reciter)
        val key = "$surahNumber:$ayahNumber"
        timings[key]
    }

    /**
     * Retrieves all Ayah timings for a specific Surah.
     * Returns a map of ayahNumber -> HifzAyahTiming.
     */
    suspend fun getSurahTimings(
        context: Context,
        reciterId: String = "alafasy",
        surahNumber: Int
    ): Map<Int, HifzAyahTiming> = withContext(Dispatchers.IO) {
        val reciter = normalizeReciterId(reciterId)
        val timings = loadTimingsForReciter(context, reciter)
        val result = mutableMapOf<Int, HifzAyahTiming>()
        timings.forEach { (_, timing) ->
            if (timing.surahNumber == surahNumber) {
                result[timing.ayahNumber] = timing
            }
        }
        result
    }

    /**
     * Preloads timing data into cache in background.
     */
    suspend fun preloadReciter(context: Context, reciterId: String = "alafasy") = withContext(Dispatchers.IO) {
        val reciter = normalizeReciterId(reciterId)
        loadTimingsForReciter(context, reciter)
    }

    /**
     * Loads timing map for a reciter either from cache or asset file.
     * Strips unused 'duration' field.
     */
    private suspend fun loadTimingsForReciter(
        context: Context,
        reciterId: String
    ): Map<String, HifzAyahTiming> = withContext(Dispatchers.IO) {
        reciterTimingsCache[reciterId]?.let { return@withContext it }

        val assetPath = "$HIFZ_ASSETS_DIR/word_segments_$reciterId.json"
        val parsedMap = try {
            loadUsingJsonReader(context, assetPath)
        } catch (e: Exception) {
            Log.w(TAG, "JsonReader failed for $assetPath, falling back to JSONObject: ${e.message}")
            try {
                loadUsingJsonObject(context, assetPath)
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to parse $assetPath: ${ex.message}", ex)
                emptyMap()
            }
        }

        if (parsedMap.isNotEmpty()) {
            reciterTimingsCache[reciterId] = parsedMap
            Log.i(TAG, "Loaded ${parsedMap.size} ayah timings for reciter '$reciterId'")
            return@withContext parsedMap
        }

        // Fallback to alafasy asset if requested reciter timing asset is missing
        if (reciterId != "alafasy") {
            Log.i(TAG, "Reciter '$reciterId' timing not found, falling back to 'alafasy' timing asset")
            val fallbackMap = loadTimingsForReciter(context, "alafasy")
            if (fallbackMap.isNotEmpty()) {
                reciterTimingsCache[reciterId] = fallbackMap
            }
            return@withContext fallbackMap
        }

        parsedMap
    }

    /**
     * High-speed streaming JSON parser via [JsonReader].
     * Avoids large memory allocation and ignores the unused 'duration' field.
     */
    private fun loadUsingJsonReader(context: Context, assetPath: String): Map<String, HifzAyahTiming> {
        val result = HashMap<String, HifzAyahTiming>(6236)
        context.assets.open(assetPath).use { inputStream ->
            JsonReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                reader.beginObject()
                while (reader.hasNext()) {
                    val key = reader.nextName() // e.g. "1:1"
                    val timing = readAyahTiming(reader)
                    if (timing != null) {
                        result[key] = timing
                    }
                }
                reader.endObject()
            }
        }
        return result
    }

    private fun readAyahTiming(reader: JsonReader): HifzAyahTiming? {
        reader.beginObject()
        var surahNumber = 0
        var ayahNumber = 0
        var audioUrl = ""
        var segments: List<HifzWordSegment> = emptyList()

        while (reader.hasNext()) {
            when (reader.nextName()) {
                "surah_number" -> surahNumber = reader.nextInt()
                "ayah_number" -> ayahNumber = reader.nextInt()
                "audio_url" -> audioUrl = reader.nextString()
                "segments" -> segments = readSegments(reader)
                "duration" -> reader.skipValue() // Strip unused duration field
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return if (surahNumber > 0 && ayahNumber > 0 && audioUrl.isNotBlank()) {
            HifzAyahTiming(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                audioUrl = audioUrl,
                segments = segments
            )
        } else {
            null
        }
    }

    private fun readSegments(reader: JsonReader): List<HifzWordSegment> {
        val list = mutableListOf<HifzWordSegment>()
        reader.beginArray()
        while (reader.hasNext()) {
            reader.beginArray()
            var wordIndex = 0
            var startMs = 0L
            var endMs = 0L
            var idx = 0
            while (reader.hasNext()) {
                when (idx) {
                    0 -> wordIndex = reader.nextInt()
                    1 -> startMs = reader.nextLong()
                    2 -> endMs = reader.nextLong()
                    else -> reader.skipValue()
                }
                idx++
            }
            reader.endArray()
            if (wordIndex > 0) {
                list.add(HifzWordSegment(wordIndex, startMs, endMs))
            }
        }
        reader.endArray()
        return list
    }

    /**
     * Fallback parser using standard [JSONObject].
     */
    private fun loadUsingJsonObject(context: Context, assetPath: String): Map<String, HifzAyahTiming> {
        val result = HashMap<String, HifzAyahTiming>()
        val jsonString = context.assets.open(assetPath).bufferedReader(Charsets.UTF_8).use { it.readText() }
        val root = JSONObject(jsonString)
        val keys = root.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val obj = root.optJSONObject(key) ?: continue
            val surahNumber = obj.optInt("surah_number", 0)
            val ayahNumber = obj.optInt("ayah_number", 0)
            val audioUrl = obj.optString("audio_url", "")
            val segArray = obj.optJSONArray("segments")

            val segments = mutableListOf<HifzWordSegment>()
            if (segArray != null) {
                for (i in 0 until segArray.length()) {
                    val item = segArray.optJSONArray(i) ?: continue
                    val wIdx = item.optInt(0, 0)
                    val sMs = item.optLong(1, 0L)
                    val eMs = item.optLong(2, 0L)
                    if (wIdx > 0) {
                        segments.add(HifzWordSegment(wIdx, sMs, eMs))
                    }
                }
            }

            if (surahNumber > 0 && ayahNumber > 0 && audioUrl.isNotBlank()) {
                result[key] = HifzAyahTiming(
                    surahNumber = surahNumber,
                    ayahNumber = ayahNumber,
                    audioUrl = audioUrl,
                    segments = segments
                )
            }
        }
        return result
    }

    private fun normalizeReciterId(reciterId: String): String {
        val lower = reciterId.lowercase().trim()
        return when {
            lower.contains("alafasy") || lower.contains("mishari") || lower.contains("afasy") -> "alafasy"
            lower.contains("husary") || lower.contains("hussary") -> "husary"
            else -> lower
        }
    }
}
