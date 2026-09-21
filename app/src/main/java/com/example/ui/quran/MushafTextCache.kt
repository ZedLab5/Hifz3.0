package com.example.ui.quran

import android.util.LruCache
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.example.data.model.Surah
import com.example.data.repository.NoorRepository
import com.example.ui.theme.QuranReadingThemeColors

fun formatAyahMarker(verseNumber: Int): String {
    val arabicIndicDigits = verseNumber.toString().map { char ->
        when (char) {
            '0' -> '٠'
            '1' -> '١'
            '2' -> '٢'
            '3' -> '٣'
            '4' -> '٤'
            '5' -> '٥'
            '6' -> '٦'
            '7' -> '٧'
            '8' -> '٨'
            '9' -> '٩'
            else -> char
        }
    }.joinToString("")
    return "\u2067﴿$arabicIndicDigits﴾\u2069"
}

data class CachedMushafData(
    val baseAnnotatedString: AnnotatedString,
    val verseRanges: Map<Int, Pair<Int, Int>>
)

/**
 * High-performance in-memory cache for continuous Mushaf Flow text.
 * Caches the parsed AnnotatedString with Tajweed color coding and verse character ranges
 * so that toggling between normal card reading and Mushaf flow mode is instantaneous.
 */
object MushafTextCache {
    private val cache = LruCache<String, CachedMushafData>(40)

    private fun buildKey(surahNumber: Int, isTajweedEnabled: Boolean, themeColors: QuranReadingThemeColors?): String {
        val themeKey = when {
            themeColors == null -> "default"
            themeColors.isDark -> "dark"
            themeColors.name == "Sepia Parchment" -> "sepia"
            else -> "light"
        }
        return "${surahNumber}_tajweed_${isTajweedEnabled}_theme_$themeKey"
    }

    fun hasKey(surahNumber: Int, isTajweedEnabled: Boolean, themeColors: QuranReadingThemeColors?): Boolean {
        val key = buildKey(surahNumber, isTajweedEnabled, themeColors)
        return cache.get(key) != null
    }

    fun getOrCreate(
        surah: Surah,
        isTajweedEnabled: Boolean = true,
        themeColors: QuranReadingThemeColors? = null
    ): CachedMushafData {
        val key = buildKey(surah.number, isTajweedEnabled, themeColors)
        val existing = cache.get(key)
        if (existing != null) {
            return existing
        }

        val verses = surah.verses
        val ranges = HashMap<Int, Pair<Int, Int>>(verses.size)

        val builder = buildAnnotatedString {
            verses.forEach { verse ->
                val startIndex = length
                val cleanArabic = NoorRepository.sanitizeArabicVerseText(
                    surah.number,
                    verse.verseNumber,
                    verse.arabicText.trim()
                )
                if (isTajweedEnabled && themeColors != null) {
                    val tajweedFormatted = TajweedEngine.formatTajweedText(
                        arabicText = cleanArabic,
                        isEnabled = true,
                        themeColors = themeColors
                    )
                    append(tajweedFormatted)
                } else {
                    append(cleanArabic)
                }
                val endIndex = length
                ranges[verse.verseNumber] = Pair(startIndex, endIndex)

                append(" ")
                appendInlineContent(
                    id = "marker_${verse.verseNumber}",
                    alternateText = " ${formatAyahMarker(verse.verseNumber)} "
                )
                append(" ")
            }
        }

        val data = CachedMushafData(
            baseAnnotatedString = builder,
            verseRanges = ranges
        )
        cache.put(key, data)
        return data
    }

    fun getOrCreateRange(
        surah: Surah,
        startAyah: Int,
        endAyah: Int,
        isTajweedEnabled: Boolean = true,
        themeColors: QuranReadingThemeColors? = null
    ): CachedMushafData {
        val verses = if (surah.verses.isNotEmpty()) {
            surah.verses.filter { it.verseNumber in startAyah..endAyah }
        } else {
            emptyList()
        }
        val ranges = HashMap<Int, Pair<Int, Int>>(verses.size)

        val builder = buildAnnotatedString {
            verses.forEach { verse ->
                val startIndex = length
                val cleanArabic = NoorRepository.sanitizeArabicVerseText(
                    surah.number,
                    verse.verseNumber,
                    verse.arabicText.trim()
                )
                if (isTajweedEnabled && themeColors != null) {
                    val tajweedFormatted = TajweedEngine.formatTajweedText(
                        arabicText = cleanArabic,
                        isEnabled = true,
                        themeColors = themeColors
                    )
                    append(tajweedFormatted)
                } else {
                    append(cleanArabic)
                }
                val endIndex = length
                ranges[verse.verseNumber] = Pair(startIndex, endIndex)

                append(" ")
                val markerStart = length
                append(formatAyahMarker(verse.verseNumber))
                if (themeColors != null) {
                    addStyle(
                        androidx.compose.ui.text.SpanStyle(color = themeColors.accent, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                        markerStart,
                        length
                    )
                }
                append("  ")
            }
        }

        return CachedMushafData(
            baseAnnotatedString = builder,
            verseRanges = ranges
        )
    }

    fun clear() {
        cache.evictAll()
    }
}
