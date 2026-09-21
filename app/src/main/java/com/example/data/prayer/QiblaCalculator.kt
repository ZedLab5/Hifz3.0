package com.example.data.prayer

import android.hardware.GeomagneticField
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Calculates high-accuracy great-circle Qibla azimuth (bearing to Kaaba in Makkah),
 * geodesic distance in kilometers, and True North magnetic declination correction
 * using Android's GeomagneticField API.
 */
object QiblaCalculator {

    const val KAABA_LATITUDE = 21.422487
    const val KAABA_LONGITUDE = 39.826206

    data class QiblaInfo(
        val azimuthDegrees: Float,          // 0..360° from True North
        val distanceKm: Double,             // Geodesic distance to Kaaba in kilometers
        val cardinalDirection: String,      // E.g. "ESE", "NE", "WSW"
        val arabicDirection: String,        // E.g. "شرق - جنوب شرقي"
        val magneticDeclination: Float = 0f // Degrees declination from True North (+ = East, - = West)
    )

    /**
     * Compute Qibla direction from current latitude and longitude,
     * including magnetic declination offset calculated via GeomagneticField.
     */
    fun calculateQibla(
        latitude: Double,
        longitude: Double,
        altitudeMeters: Double = 0.0,
        timeMillis: Long = System.currentTimeMillis()
    ): QiblaInfo {
        val lat1Rad = Math.toRadians(latitude)
        val lon1Rad = Math.toRadians(longitude)
        val lat2Rad = Math.toRadians(KAABA_LATITUDE)
        val lon2Rad = Math.toRadians(KAABA_LONGITUDE)

        val deltaLon = lon2Rad - lon1Rad

        val y = sin(deltaLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLon)

        val initialBearingRad = atan2(y, x)
        val azimuth = (Math.toDegrees(initialBearingRad) + 360.0) % 360.0

        // Distance (Haversine formula)
        val deltaLat = lat2Rad - lat1Rad
        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2) * sin(deltaLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distanceKm = 6371.0 * c

        val (cardinal, arabic) = getCompassHeadingLabels(azimuth)

        // Calculate magnetic declination for this location
        val declination = try {
            val geoField = GeomagneticField(
                latitude.toFloat(),
                longitude.toFloat(),
                altitudeMeters.toFloat(),
                timeMillis
            )
            geoField.declination
        } catch (e: Exception) {
            0f
        }

        return QiblaInfo(
            azimuthDegrees = azimuth.toFloat(),
            distanceKm = distanceKm,
            cardinalDirection = cardinal,
            arabicDirection = arabic,
            magneticDeclination = declination
        )
    }

    /**
     * Converts a raw magnetic sensor heading to a true north heading
     * using the location's magnetic declination offset.
     */
    fun getTrueNorthHeading(rawMagneticHeading: Float, declination: Float): Float {
        return (rawMagneticHeading + declination + 360f) % 360f
    }

    /**
     * Computes the relative angle between the phone's top heading (True North)
     * and the Qibla bearing. Range is -180..+180 degrees.
     * 0° = perfectly pointing towards Kaaba.
     */
    fun computeRelativeAngle(qiblaTrueBearing: Float, phoneTrueHeading: Float): Float {
        return ((qiblaTrueBearing - phoneTrueHeading + 540f) % 360f) - 180f
    }

    private fun getCompassHeadingLabels(degrees: Double): Pair<String, String> {
        val directions = listOf(
            "N" to "شمال",
            "NNE" to "شمال شمال شرق",
            "NE" to "شمال شرق",
            "ENE" to "شرق شمال شرق",
            "E" to "شرق",
            "ESE" to "شرق جنوب شرق",
            "SE" to "جنوب شرق",
            "SSE" to "جنوب جنوب شرق",
            "S" to "جنوب",
            "SSW" to "جنوب جنوب غرب",
            "SW" to "جنوب غرب",
            "WSW" to "غرب جنوب غرب",
            "W" to "غرب",
            "WNW" to "غرب شمال غرب",
            "NW" to "شمال غرب",
            "NNW" to "شمال شمال غرب"
        )
        val index = (((degrees + 11.25) % 360.0) / 22.5).toInt()
        return directions[index % directions.size]
    }
}

