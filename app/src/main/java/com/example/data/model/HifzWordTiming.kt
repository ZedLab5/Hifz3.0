package com.example.data.model

/**
 * Represents a single word timing segment within a verse's recitation.
 *
 * @property wordIndex 1-based index of the word within the ayah.
 * @property startMs Start time in milliseconds within the verse audio file.
 * @property endMs End time in milliseconds within the verse audio file.
 */
data class HifzWordSegment(
    val wordIndex: Int,
    val startMs: Long,
    val endMs: Long
) {
    val durationMs: Long get() = (endMs - startMs).coerceAtLeast(0)
}

/**
 * Timing data and audio stream metadata for a single Ayah in Hifz (memorization) mode.
 *
 * @property surahNumber Surah number (1-114).
 * @property ayahNumber Ayah number within the Surah.
 * @property audioUrl Dedicated Tarteel CDN hosted audio URL aligned with the word timestamps.
 * @property segments List of [HifzWordSegment] entries ordered by word index.
 */
data class HifzAyahTiming(
    val surahNumber: Int,
    val ayahNumber: Int,
    val audioUrl: String,
    val segments: List<HifzWordSegment>
) {
    val totalWords: Int get() = segments.size
    val startTimeMs: Long get() = segments.firstOrNull()?.startMs ?: 0L
    val endTimeMs: Long get() = segments.lastOrNull()?.endMs ?: 0L
    val totalDurationMs: Long get() = (endTimeMs - startTimeMs).coerceAtLeast(0)

    /**
     * Finds the segment for a given word index (1-based).
     */
    fun getSegment(wordIndex: Int): HifzWordSegment? {
        return segments.find { it.wordIndex == wordIndex }
    }

    /**
     * Resolves the start and end timestamps (in milliseconds) for a range of words [fromWordIndex..toWordIndex].
     * Clamps to valid segment indices.
     */
    fun getRangeTiming(fromWordIndex: Int, toWordIndex: Int): Pair<Long, Long>? {
        if (segments.isEmpty()) return null
        val sorted = segments.sortedBy { it.wordIndex }
        val startSeg = sorted.find { it.wordIndex >= fromWordIndex } ?: sorted.first()
        val endSeg = sorted.findLast { it.wordIndex <= toWordIndex } ?: sorted.last()
        return Pair(startSeg.startMs, endSeg.endMs)
    }

    /**
     * Determines which word index is actively playing at the given playback position in milliseconds.
     */
    fun getActiveWordIndex(positionMs: Long): Int? {
        return segments.find { positionMs in it.startMs..it.endMs }?.wordIndex
    }
}
