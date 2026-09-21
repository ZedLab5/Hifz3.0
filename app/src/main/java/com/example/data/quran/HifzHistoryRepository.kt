package com.example.data.quran

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Surah
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HifzSessionLog(
    val id: String = System.currentTimeMillis().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val surahNumber: Int,
    val surahName: String,
    val surahNameArabic: String = "",
    val startAyah: Int,
    val endAyah: Int,
    val mode: String, // "Self-Recall", "Practice Drill", "Speed Revision"
    val totalAyahs: Int,
    val ayahsMemorized: Int,
    val ayahsMissed: Int,
    val hintsUsed: Int = 0,
    val durationSeconds: Int = 180,
    val repetitionsCompleted: Int = 3,
    val scorePercentage: Int = if (totalAyahs > 0) ((ayahsMemorized.toFloat() / totalAyahs) * 100).toInt() else 100,
    val notes: String = ""
) {
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

    val relativeTime: String
        get() {
            val now = System.currentTimeMillis()
            val diffMs = now - timestamp
            val diffMins = diffMs / (1000 * 60)
            val diffHours = diffMins / 60
            val diffDays = diffHours / 24

            return when {
                diffMins < 1 -> "Just now"
                diffMins < 60 -> "${diffMins}m ago"
                diffHours < 24 -> "${diffHours}h ago"
                diffDays == 1L -> "Yesterday"
                diffDays < 7 -> "${diffDays}d ago"
                else -> {
                    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                    sdf.format(Date(timestamp))
                }
            }
        }
}

object HifzHistoryRepository {
    private const val PREFS_NAME = "hifz_history_prefs"
    private const val KEY_SESSIONS_JSON = "hifz_sessions_history_json"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSessionLogs(context: Context): List<HifzSessionLog> {
        val prefs = getPrefs(context)
        val jsonStr = prefs.getString(KEY_SESSIONS_JSON, null)
        if (jsonStr.isNullOrBlank()) {
            return emptyList()
        }

        return try {
            val list = mutableListOf<HifzSessionLog>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    HifzSessionLog(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        surahNumber = obj.optInt("surahNumber", 1),
                        surahName = obj.optString("surahName", "Al-Fatihah"),
                        surahNameArabic = obj.optString("surahNameArabic", ""),
                        startAyah = obj.optInt("startAyah", 1),
                        endAyah = obj.optInt("endAyah", 7),
                        mode = obj.optString("mode", "Self-Recall"),
                        totalAyahs = obj.optInt("totalAyahs", 7),
                        ayahsMemorized = obj.optInt("ayahsMemorized", 7),
                        ayahsMissed = obj.optInt("ayahsMissed", 0),
                        hintsUsed = obj.optInt("hintsUsed", 0),
                        durationSeconds = obj.optInt("durationSeconds", 180),
                        repetitionsCompleted = obj.optInt("repetitionsCompleted", 3),
                        scorePercentage = obj.optInt("scorePercentage", 100),
                        notes = obj.optString("notes", "")
                    )
                )
            }
            list.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun recordSession(context: Context, log: HifzSessionLog) {
        val current = getSessionLogs(context).toMutableList()
        current.add(0, log)
        // Keep last 100 sessions
        val trimmed = if (current.size > 100) current.take(100) else current
        saveSessionLogs(context, trimmed)
    }

    fun clearHistory(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit().remove(KEY_SESSIONS_JSON).apply()
    }

    private fun saveSessionLogs(context: Context, logs: List<HifzSessionLog>) {
        val array = JSONArray()
        logs.forEach { log ->
            val obj = JSONObject().apply {
                put("id", log.id)
                put("timestamp", log.timestamp)
                put("surahNumber", log.surahNumber)
                put("surahName", log.surahName)
                put("surahNameArabic", log.surahNameArabic)
                put("startAyah", log.startAyah)
                put("endAyah", log.endAyah)
                put("mode", log.mode)
                put("totalAyahs", log.totalAyahs)
                put("ayahsMemorized", log.ayahsMemorized)
                put("ayahsMissed", log.ayahsMissed)
                put("hintsUsed", log.hintsUsed)
                put("durationSeconds", log.durationSeconds)
                put("repetitionsCompleted", log.repetitionsCompleted)
                put("scorePercentage", log.scorePercentage)
                put("notes", log.notes)
            }
            array.put(obj)
        }
        getPrefs(context).edit().putString(KEY_SESSIONS_JSON, array.toString()).apply()
    }
}

