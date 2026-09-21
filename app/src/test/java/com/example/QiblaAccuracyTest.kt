package com.example

import com.example.data.prayer.QiblaCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class QiblaAccuracyTest {

    @Test
    fun testQiblaBearingAndDeclination_London() {
        // London: Lat 51.5074 N, Lon -0.1278 W
        val qibla = QiblaCalculator.calculateQibla(51.5074, -0.1278)
        
        // Expected True North Qibla azimuth from London is approximately 118.98° (ESE)
        assertEquals(118.98f, qibla.azimuthDegrees, 0.5f)
        assertEquals("ESE", qibla.cardinalDirection)
        assertTrue("Distance should be around ~4700-4800km", qibla.distanceKm in 4700.0..4900.0)
    }

    @Test
    fun testQiblaBearingAndDeclination_NewYork() {
        // New York: Lat 40.7128 N, Lon -74.0060 W
        val qibla = QiblaCalculator.calculateQibla(40.7128, -74.0060)
        
        // Expected True North Qibla azimuth from NYC is approximately 58.48° (ENE)
        assertEquals(58.48f, qibla.azimuthDegrees, 0.5f)
        assertEquals("ENE", qibla.cardinalDirection)
        assertTrue("Distance should be around ~10,200km", qibla.distanceKm in 10000.0..10500.0)
    }

    @Test
    fun testQiblaBearingAndDeclination_Cairo() {
        // Cairo: Lat 30.0444 N, Lon 31.2357 E
        val qibla = QiblaCalculator.calculateQibla(30.0444, 31.2357)
        
        // Expected True North Qibla azimuth from Cairo is approximately 136.6° (SE)
        assertEquals(136.6f, qibla.azimuthDegrees, 1.0f)
        assertEquals("SE", qibla.cardinalDirection)
        assertTrue("Distance should be around ~1200-1300km", qibla.distanceKm in 1200.0..1400.0)
    }

    @Test
    fun testDeclinationHeadingCorrection() {
        // Raw magnetic heading pointing North (0°), Declination is -12.5° (West) -> True Heading is 347.5°
        val trueHeadingWest = QiblaCalculator.getTrueNorthHeading(0f, -12.5f)
        assertEquals(347.5f, trueHeadingWest, 0.01f)

        // Raw magnetic heading pointing North (0°), Declination is +4.5° (East) -> True Heading is 4.5°
        val trueHeadingEast = QiblaCalculator.getTrueNorthHeading(0f, 4.5f)
        assertEquals(4.5f, trueHeadingEast, 0.01f)

        // Angle wrap-around check: Raw 355° + Declination 10° -> 5°
        val wrapHeading = QiblaCalculator.getTrueNorthHeading(355f, 10f)
        assertEquals(5.0f, wrapHeading, 0.01f)
    }

    @Test
    fun testRelativeAngleToKaaba() {
        val qiblaBearing = 119f
        
        // Phone points at True North 119° -> relative angle = 0° (Aligned)
        val relativeAligned = QiblaCalculator.computeRelativeAngle(qiblaBearing, 119f)
        assertEquals(0f, relativeAligned, 0.01f)

        // Phone points at True North 100° -> needs to turn right by 19°
        val relativeRight = QiblaCalculator.computeRelativeAngle(qiblaBearing, 100f)
        assertEquals(19f, relativeRight, 0.01f)

        // Phone points at True North 140° -> needs to turn left by -21°
        val relativeLeft = QiblaCalculator.computeRelativeAngle(qiblaBearing, 140f)
        assertEquals(-21f, relativeLeft, 0.01f)
    }
}
