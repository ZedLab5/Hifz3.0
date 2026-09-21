package com.example.data.quran

import android.content.Context
import android.util.Log
import org.json.JSONArray
import java.util.concurrent.ConcurrentHashMap

/**
 * Precomputed Ayah splitting lookup table repository.
 * 
 * Ayahs with >15 words are split into multiple reading groups.
 * Ayahs with <=15 words are NOT in this table and read as a single continuous group [1..wordCount].
 */
object AyahSplitRepository {
    private const val TAG = "AyahSplitRepo"
    private const val PRIMARY_ASSET = "quran/ayah_splits.json"

    // Map of "surah:ayah" -> List<Pair<Int, Int>> (1-indexed start and end word indices)
    private val splitsMap = ConcurrentHashMap<String, List<Pair<Int, Int>>>()
    @Volatile private var isLoaded = false

    fun init(context: Context) {
        if (isLoaded) return
        synchronized(this) {
            if (isLoaded) return
            try {
                val inputStream = context.assets.open(PRIMARY_ASSET)
                val jsonString = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val array = JSONArray(jsonString)

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val surah = obj.getInt("surah")
                    val ayah = obj.getInt("ayah")
                    val groupsArr = obj.getJSONArray("groups")

                    val groups = mutableListOf<Pair<Int, Int>>()
                    for (g in 0 until groupsArr.length()) {
                        val pairArr = groupsArr.getJSONArray(g)
                        val startW = pairArr.getInt(0)
                        val endW = pairArr.getInt(1)
                        groups.add(Pair(startW, endW))
                    }

                    splitsMap["$surah:$ayah"] = groups
                }
                isLoaded = true
                Log.i(TAG, "Loaded ${splitsMap.size} precomputed ayah splits from assets")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load ayah splits asset: ${e.message}", e)
            }
        }
    }

    /**
     * Retrieves reading group segments for (surah, ayah).
     * 
     * - If found in the precomputed table (>15 words), returns its exact "groups" ranges (e.g. [[1, 12], [13, 19]]).
     * - If not found (<=15 words), returns a single continuous group [[1, totalWordsInAyah]].
     */
    fun getGroups(surah: Int, ayah: Int, totalWordsInAyah: Int = 15): List<Pair<Int, Int>> {
        val key = "$surah:$ayah"
        val existing = splitsMap[key]
        if (existing != null && existing.isNotEmpty()) {
            return existing
        }
        val safeWords = totalWordsInAyah.coerceAtLeast(1)
        return listOf(Pair(1, safeWords))
    }

    /**
     * Returns true if the specified ayah is in the precomputed split table (>15 words).
     */
    fun isSplit(surah: Int, ayah: Int): Boolean {
        return splitsMap.containsKey("$surah:$ayah")
    }

    /**
     * Returns total number of precomputed split entries loaded.
     */
    fun getLoadedCount(): Int = splitsMap.size
}
