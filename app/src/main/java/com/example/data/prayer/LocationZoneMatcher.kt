package com.example.data.prayer

import com.example.data.model.PrayerZone
import kotlin.math.*

/**
 * Utility matcher to calculate the nearest prayer zone to a set of GPS coordinates.
 * Uses the Haversine formula to measure great-circle distance on a spherical Earth.
 *
 * Haversine Formula:
 *   a = sin²(Δlat / 2) + cos(lat1) * cos(lat2) * sin²(Δlng / 2)
 *   c = 2 * atan2(√a, √(1 - a))
 *   d = R * c
 *
 * Earth Radius (R): 6371.0 km (mean Earth radius)
 */
object LocationZoneMatcher {

    const val EARTH_RADIUS_KM = 6371.0

    /**
     * Sensible default radius limit (250 km) to prevent assigning users in completely
     * different regions/countries to an unrelated prayer zone (e.g., someone in Jakarta being assigned to Dubai).
     */
    const val DEFAULT_MAX_RADIUS_KM = 250.0

    /**
     * Calculates the great-circle distance between two coordinates using the Haversine formula.
     */
    fun calculateDistanceKm(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Finds the nearest [PrayerZone] from a list of zones based on coordinates.
     * Guaranteed to return a zone if the list is not empty.
     */
    fun nearestZone(
        lat: Double,
        lng: Double,
        zones: List<PrayerZone>
    ): PrayerZone {
        require(zones.isNotEmpty()) { "Zones list cannot be empty" }
        return zones.minBy { zone ->
            calculateDistanceKm(lat, lng, zone.latitude, zone.longitude)
        }
    }

    /**
     * Finds the nearest [PrayerZone] from a list of zones within a maximum radius [maxKm].
     * Returns null if the closest zone is farther away than [maxKm].
     */
    fun nearestZoneWithinRadius(
        lat: Double,
        lng: Double,
        zones: List<PrayerZone>,
        maxKm: Double = DEFAULT_MAX_RADIUS_KM
    ): PrayerZone? {
        if (zones.isEmpty()) return null
        val nearest = nearestZone(lat, lng, zones)
        val distance = calculateDistanceKm(lat, lng, nearest.latitude, nearest.longitude)
        return if (distance <= maxKm) nearest else null
    }
}
