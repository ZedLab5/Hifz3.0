package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.quran.HifzWordTimingRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HifzWordTimingTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun testAlafasyTimingAssetLoadsCorrectly() = runBlocking {
        assertTrue(HifzWordTimingRepository.isReciterSupported(context, "alafasy"))

        // Test Al-Fatiha 1:1
        val timing11 = HifzWordTimingRepository.getAyahTiming(context, "alafasy", 1, 1)
        assertNotNull("Ayah timing for 1:1 should not be null", timing11)
        assertEquals(1, timing11!!.surahNumber)
        assertEquals(1, timing11.ayahNumber)
        assertEquals("https://audio-cdn.tarteel.ai/quran/alafasy/001001.mp3", timing11.audioUrl)
        assertEquals(4, timing11.segments.size)

        // Verify segment 1: [1, 0, 560]
        assertEquals(1, timing11.segments[0].wordIndex)
        assertEquals(0L, timing11.segments[0].startMs)
        assertEquals(560L, timing11.segments[0].endMs)

        // Test Range Timing: Words 1 through 2
        val range12 = timing11.getRangeTiming(1, 2)
        assertNotNull(range12)
        assertEquals(0L, range12!!.first)
        assertEquals(1200L, range12.second)

        // Test Range Timing: Words 3 through 4
        val range34 = timing11.getRangeTiming(3, 4)
        assertNotNull(range34)
        assertEquals(2000L, range34!!.first)
        assertEquals(6120L, range34.second)

        // Test Active Word detection
        assertEquals(1, timing11.getActiveWordIndex(250L))
        assertEquals(2, timing11.getActiveWordIndex(800L))
        assertEquals(3, timing11.getActiveWordIndex(2100L))
        assertEquals(4, timing11.getActiveWordIndex(3000L))
    }

    @Test
    fun testAyatAlKursiTiming() = runBlocking {
        // Test Ayatul Kursi (2:255)
        val timingKursi = HifzWordTimingRepository.getAyahTiming(context, "alafasy", 2, 255)
        assertNotNull("Ayah timing for 2:255 should not be null", timingKursi)
        assertEquals(2, timingKursi!!.surahNumber)
        assertEquals(255, timingKursi.ayahNumber)
        assertEquals("https://audio-cdn.tarteel.ai/quran/alafasy/002255.mp3", timingKursi.audioUrl)
        assertTrue(timingKursi.segments.size >= 50)
    }

    @Test
    fun testWordBoundaryAccuracyAcrossMultipleAyahs() = runBlocking {
        // Test Surah Al-Ikhlas (112:1-4)
        for (ayah in 1..4) {
            val timing = HifzWordTimingRepository.getAyahTiming(context, "alafasy", 112, ayah)
            assertNotNull("Ayah 112:$ayah timing should exist", timing)
            assertTrue("Ayah 112:$ayah must have segments", timing!!.segments.isNotEmpty())

            // Validate that every word's startMs <= endMs and strictly non-negative
            for (seg in timing.segments) {
                assertTrue("Word ${seg.wordIndex} in 112:$ayah startMs >= 0", seg.startMs >= 0)
                assertTrue("Word ${seg.wordIndex} in 112:$ayah endMs >= startMs", seg.endMs >= seg.startMs)
            }

            // Validate single word range uses that exact word's start and end ms
            for (seg in timing.segments) {
                val range = timing.getRangeTiming(seg.wordIndex, seg.wordIndex)
                assertNotNull(range)
                assertEquals(seg.startMs, range!!.first)
                assertEquals(seg.endMs, range.second)
            }
        }

        // Test Surah Al-Kawthar (108:1) - Short Surah / short words
        val timingKawthar = HifzWordTimingRepository.getAyahTiming(context, "alafasy", 108, 1)
        assertNotNull(timingKawthar)
        val w1Range = timingKawthar!!.getRangeTiming(1, 1)
        val w2Range = timingKawthar.getRangeTiming(2, 2)
        val w3Range = timingKawthar.getRangeTiming(3, 3)
        assertNotNull(w1Range)
        assertNotNull(w2Range)
        assertNotNull(w3Range)
        assertEquals(timingKawthar.segments[0].startMs, w1Range!!.first)
        assertEquals(timingKawthar.segments[0].endMs, w1Range.second)
    }
}
