package com.example.data.quran

import com.example.data.model.HadithItem

/**
 * HadithData service bridge forwarding queries to [HadithRepository] which fetches
 * authentic traditions remotely on demand with short-lived in-memory caching only.
 * Local bundling and hardcoding of Hadith content has been completely removed.
 */
object HadithData {

    /**
     * Search remote hadiths asynchronously via [HadithRepository].
     */
    suspend fun searchRemote(query: String): List<HadithItem> {
        if (query.isBlank()) return emptyList()
        return HadithRepository.searchOrFilterHadiths(query = query).getOrDefault(emptyList())
    }
}
