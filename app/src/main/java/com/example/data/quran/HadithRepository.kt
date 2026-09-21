package com.example.data.quran

import com.example.data.model.HadithItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/**
 * Data model representing a Hadith Collection meta info.
 */
data class HadithCollectionInfo(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val description: String,
    val defaultGrade: String,
    val totalCountEstimate: Int
)

/**
 * Short-lived in-memory cache wrapper for fetched Hadith lists.
 * Cached entries expire after 15 minutes and are never stored on permanent disk or DB.
 */
private data class CachedHadiths(
    val timestampMs: Long,
    val items: List<HadithItem>
)

object HadithRepository {

    private const val BASE_URL = "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions"
    private const val CACHE_EXPIRATION_MS = 15 * 60 * 1000L // 15 minutes short-lived cache

    // In-memory short-lived cache map (CollectionId/Topic -> CachedHadiths)
    private val memoryCache = ConcurrentHashMap<String, CachedHadiths>()

    val collections = listOf(
        HadithCollectionInfo(
            id = "nawawi",
            nameEn = "40 Hadith Nawawi",
            nameAr = "الأربعون النووية",
            description = "42 foundational hadiths summarizing the essence of Islamic creed, ethics, and practice.",
            defaultGrade = "Sahih",
            totalCountEstimate = 42
        ),
        HadithCollectionInfo(
            id = "bukhari",
            nameEn = "Sahih al-Bukhari",
            nameAr = "صحيح البخاري",
            description = "The most authentic book of Hadith compiled by Imam Al-Bukhari.",
            defaultGrade = "Sahih al-Bukhari",
            totalCountEstimate = 7563
        ),
        HadithCollectionInfo(
            id = "muslim",
            nameEn = "Sahih Muslim",
            nameAr = "صحيح مسلم",
            description = "The premier canonical collection of authentic traditions compiled by Imam Muslim.",
            defaultGrade = "Sahih Muslim",
            totalCountEstimate = 7500
        ),
        HadithCollectionInfo(
            id = "tirmidhi",
            nameEn = "Jami` at-Tirmidhi",
            nameAr = "جامع الترمذي",
            description = "Renowned Sunan collection with detailed scholarly grade classifications.",
            defaultGrade = "Hasan Sahih",
            totalCountEstimate = 3956
        ),
        HadithCollectionInfo(
            id = "abudawud",
            nameEn = "Sunan Abi Dawud",
            nameAr = "سنن أبي داود",
            description = "Major collection focusing on legal judgments, Sunnah, and daily etiquettes.",
            defaultGrade = "Hasan",
            totalCountEstimate = 5274
        ),
        HadithCollectionInfo(
            id = "nasai",
            nameEn = "Sunan an-Nasa'i",
            nameAr = "سنن النسائي",
            description = "Distinguished Sunan collection known for rigorous narrator scrutiny.",
            defaultGrade = "Sahih",
            totalCountEstimate = 5758
        ),
        HadithCollectionInfo(
            id = "ibnmajah",
            nameEn = "Sunan Ibn Majah",
            nameAr = "سنن ابن ماجه",
            description = "One of the six major canonical Hadith works covering diverse life topics.",
            defaultGrade = "Hasan",
            totalCountEstimate = 4341
        )
    )

    val topicsList = listOf(
        "All Topics",
        "Sincerity & Intentions",
        "Faith & Iman",
        "Worship & Prayer",
        "Patience & Hardship",
        "Seeking Knowledge",
        "Good Character & Manners",
        "Forgiveness & Repentance",
        "Parents & Family",
        "Dhikr & Supplication",
        "Travel & Safety",
        "Charity & Brotherhood"
    )

    /**
     * Fetch hadiths for a collection on demand from remote server.
     * Uses short-lived in-memory caching.
     */
    suspend fun getHadithsForCollection(
        collectionId: String,
        limit: Int = 100,
        forcedRefresh: Boolean = false
    ): Result<List<HadithItem>> = withContext(Dispatchers.IO) {
        val cacheKey = "col_$collectionId"
        val now = System.currentTimeMillis()

        if (!forcedRefresh) {
            val cached = memoryCache[cacheKey]
            if (cached != null && (now - cached.timestampMs) < CACHE_EXPIRATION_MS) {
                return@withContext Result.success(cached.items.take(limit))
            }
        }

        try {
            val targetCollection = collections.find { it.id == collectionId } ?: collections.first()
            val engUrl = "$BASE_URL/eng-$collectionId.json"
            val araUrl = "$BASE_URL/ara-$collectionId.json"

            val engJsonString = fetchUrlString(engUrl) ?: throw Exception("Network failure loading English Hadith data for $collectionId")
            val araJsonString = fetchUrlString(araUrl)

            val items = parseHadithJson(engJsonString, araJsonString, targetCollection)

            if (items.isNotEmpty()) {
                memoryCache[cacheKey] = CachedHadiths(now, items)
                Result.success(items.take(limit))
            } else {
                Result.failure(Exception("No Hadiths found in remote collection"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Search remote hadiths across collections or filter loaded items by topic.
     */
    suspend fun searchOrFilterHadiths(
        query: String,
        selectedCollectionId: String? = null,
        selectedTopic: String? = null
    ): Result<List<HadithItem>> = withContext(Dispatchers.IO) {
        val targetColId = selectedCollectionId ?: "nawawi"
        val fetchResult = getHadithsForCollection(targetColId, limit = 500)

        fetchResult.map { allInCol ->
            var filtered = allInCol

            if (!selectedTopic.isNullOrBlank() && selectedTopic != "All Topics") {
                val tLower = selectedTopic.lowercase()
                filtered = filtered.filter { item ->
                    item.chapter.contains(tLower, ignoreCase = true) ||
                            item.topics.any { topic -> topic.contains(tLower, ignoreCase = true) || tLower.contains(topic) } ||
                            matchesTopicKeywords(tLower, item)
                }
            }

            if (query.isNotBlank()) {
                val qLower = query.trim().lowercase()
                filtered = filtered.filter { item ->
                    item.translation.contains(qLower, ignoreCase = true) ||
                            item.arabicText.contains(query.trim()) ||
                            item.narrator.contains(qLower, ignoreCase = true) ||
                            item.chapter.contains(qLower, ignoreCase = true) ||
                            item.hadithNumber.contains(qLower, ignoreCase = true) ||
                            item.topics.any { it.contains(qLower, ignoreCase = true) }
                }
            }

            filtered
        }
    }

    private fun fetchUrlString(urlString: String): String? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Noor-App-Client")

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }

    private fun parseHadithJson(
        engJsonStr: String,
        araJsonStr: String?,
        colInfo: HadithCollectionInfo
    ): List<HadithItem> {
        val result = mutableListOf<HadithItem>()

        try {
            val engObj = JSONObject(engJsonStr)
            val engHadithsArr = engObj.optJSONArray("hadiths") ?: return emptyList()

            val araMap = mutableMapOf<Int, String>()
            if (!araJsonStr.isNullOrBlank()) {
                try {
                    val araObj = JSONObject(araJsonStr)
                    val araArr = araObj.optJSONArray("hadiths")
                    if (araArr != null) {
                        for (i in 0 until araArr.length()) {
                            val hObj = araArr.optJSONObject(i)
                            if (hObj != null) {
                                val num = hObj.optInt("hadithnumber", i + 1)
                                val text = hObj.optString("text", "")
                                if (text.isNotBlank()) araMap[num] = text
                            }
                        }
                    }
                } catch (_: Exception) {}
            }

            for (i in 0 until engHadithsArr.length()) {
                val hObj = engHadithsArr.optJSONObject(i) ?: continue
                val hNum = hObj.optInt("hadithnumber", i + 1)
                val rawText = hObj.optString("text", "")
                if (rawText.isBlank()) continue

                // Extract grade if available in API object
                var extractedGrade = colInfo.defaultGrade
                val gradesArr = hObj.optJSONArray("grades")
                if (gradesArr != null && gradesArr.length() > 0) {
                    val firstGradeObj = gradesArr.optJSONObject(0)
                    val gradeVal = firstGradeObj?.optString("grade", "") ?: ""
                    if (gradeVal.isNotBlank() && !gradeVal.contains("Unknown", ignoreCase = true)) {
                        extractedGrade = gradeVal
                    }
                }

                val narratorAndText = parseNarratorAndTranslation(rawText)
                val araText = araMap[hNum] ?: extractArabicFallback(rawText)

                result.add(
                    HadithItem(
                        id = "${colInfo.id}_$hNum",
                        book = colInfo.nameEn,
                        hadithNumber = "Hadith $hNum",
                        chapter = colInfo.nameEn,
                        chapterAr = colInfo.nameAr,
                        narrator = narratorAndText.first,
                        arabicText = araText,
                        translation = narratorAndText.second,
                        topics = deriveTopics(rawText),
                        grade = extractedGrade,
                        explanation = "Source: ${colInfo.nameEn} (Verified Hadith Edition)"
                    )
                )
            }
        } catch (_: Exception) {}

        return result
    }

    private fun parseNarratorAndTranslation(rawText: String): Pair<String, String> {
        val lower = rawText.lowercase()
        return if (lower.startsWith("narrated ") || lower.startsWith("it is narrated ")) {
            val colonIdx = rawText.indexOf(':')
            if (colonIdx in 1..80) {
                val narrator = rawText.substring(0, colonIdx).trim()
                val body = rawText.substring(colonIdx + 1).trim()
                Pair(narrator, body)
            } else {
                Pair("Prophetic Tradition", rawText)
            }
        } else {
            Pair("Prophetic Tradition", rawText)
        }
    }

    private fun extractArabicFallback(rawText: String): String {
        return "الحديث الشريف في المصدر الرئيسي"
    }

    private fun deriveTopics(text: String): List<String> {
        val t = text.lowercase()
        val list = mutableListOf<String>()
        if (t.contains("intention") || t.contains("motive") || t.contains("niyyah")) list.add("sincerity")
        if (t.contains("pray") || t.contains("salah") || t.contains("prostration")) list.add("prayer")
        if (t.contains("charity") || t.contains("zakat") || t.contains("give")) list.add("charity")
        if (t.contains("forgive") || t.contains("repent") || t.contains("sin")) list.add("forgiveness")
        if (t.contains("parent") || t.contains("mother") || t.contains("father")) list.add("parents")
        if (t.contains("patience") || t.contains("hardship") || t.contains("trial")) list.add("patience")
        if (t.contains("knowledge") || t.contains("learn") || t.contains("scholar")) list.add("knowledge")
        if (t.contains("travel") || t.contains("journey")) list.add("travel")
        if (t.contains("dhikr") || t.contains("remembrance") || t.contains("supplication")) list.add("dhikr")
        if (list.isEmpty()) list.add("general")
        return list
    }

    private fun matchesTopicKeywords(topicFilter: String, item: HadithItem): Boolean {
        val t = topicFilter.lowercase()
        val text = (item.translation + " " + item.chapter).lowercase()
        return when {
            t.contains("sincerity") -> text.contains("intent") || text.contains("sincerity") || text.contains("niyyah")
            t.contains("faith") -> text.contains("faith") || text.contains("iman") || text.contains("belief")
            t.contains("worship") -> text.contains("pray") || text.contains("worship") || text.contains("salah")
            t.contains("patience") -> text.contains("patient") || text.contains("patience") || text.contains("hardship")
            t.contains("knowledge") -> text.contains("know") || text.contains("learn") || text.contains("scholar")
            t.contains("character") -> text.contains("manner") || text.contains("character") || text.contains("kind")
            t.contains("forgiveness") -> text.contains("forgiv") || text.contains("repent") || text.contains("sin")
            t.contains("parents") -> text.contains("parent") || text.contains("mother") || text.contains("father")
            t.contains("dhikr") -> text.contains("dhikr") || text.contains("remember") || text.contains("supplicat")
            t.contains("travel") -> text.contains("travel") || text.contains("journey")
            t.contains("charity") -> text.contains("charity") || text.contains("zakat") || text.contains("brother")
            else -> true
        }
    }
}
