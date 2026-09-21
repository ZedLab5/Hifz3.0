package com.example.data.prayer

import com.example.data.model.CalculationAuthority
import com.example.data.model.PrayerTime
import com.example.data.model.PrayerZone
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Robust, high-precision astronomical prayer time calculator based on the
 * standard celestial algorithms (Jean Meeus / Adhan standard).
 *
 * Supports arbitrary geographic coordinates, dates, calculation authorities,
 * juristic Asr methods (Standard 1x shadow vs Hanafi 2x shadow), high-latitude
 * night-portion safety rules, and per-prayer minute offsets.
 */
object PrayerCalculator {

    enum class HighLatitudeRule {
        ANGLE_BASED, // Portion of night based on angle / 60
        ONE_SEVENTH,  // 1/7th of night
        NIGHT_MIDDLE  // 1/2 of night
    }

    data class CalculatedTimes(
        val fajrMinutes: Int,
        val sunriseMinutes: Int,
        val dhuhrMinutes: Int,
        val asrMinutes: Int,
        val maghribMinutes: Int,
        val ishaMinutes: Int,
        val duhaMinutes: Int = (sunriseMinutes + 15) % 1440,
        val midnightMinutes: Int = (maghribMinutes + ((1440 + fajrMinutes - maghribMinutes) % 1440) / 2) % 1440,
        val qiyamMinutes: Int = (maghribMinutes + (((1440 + fajrMinutes - maghribMinutes) % 1440) * 2) / 3) % 1440
    )

    data class SupplementaryTimes(
        val duhaTimeString: String,
        val midnightTimeString: String,
        val qiyamTimeString: String
    )

    /**
     * Compute prayer times in minutes from midnight (0..1439) for a given date, zone, and authority.
     */
    fun calculateTimes(
        year: Int,
        month: Int, // 1-12
        day: Int,   // 1-31
        latitude: Double,
        longitude: Double,
        timeZoneOffsetHours: Double,
        fajrAngle: Double,
        ishaAngle: Double,
        ishaIntervalMinutes: Int? = null,
        isHanafiAsr: Boolean = false,
        highLatRule: HighLatitudeRule = HighLatitudeRule.ANGLE_BASED,
        minuteOffsets: Map<String, Int> = emptyMap()
    ): CalculatedTimes {
        val jd = julianDate(year, month, day)
        val sun = sunPosition(jd)

        // Solar noon (Dhuhr) in hours (0..24)
        val dhuhrHour = midDay(timeZoneOffsetHours, longitude, sun.equationOfTime)

        // Sunrise & Sunset (Maghrib) with standard atmospheric refraction (-0.8333°)
        val sunriseHourAngle = hourAngle(-0.8333, latitude, sun.declination)
        val sunriseHour = if (sunriseHourAngle.isNaN()) {
            dhuhrHour - 6.0 // Fallback
        } else {
            dhuhrHour - sunriseHourAngle / 15.0
        }

        val maghribHour = if (sunriseHourAngle.isNaN()) {
            dhuhrHour + 6.0
        } else {
            dhuhrHour + sunriseHourAngle / 15.0
        }

        // Night length in hours (from Maghrib to Sunrise of next day)
        val nightLength = (24.0 + sunriseHour - maghribHour) % 24.0

        // Fajr calculation
        val fajrHourAngle = hourAngle(-fajrAngle, latitude, sun.declination)
        val fajrHour = if (fajrHourAngle.isNaN()) {
            // Apply high-latitude rule when sun never dips below fajrAngle
            val portion = when (highLatRule) {
                HighLatitudeRule.ANGLE_BASED -> fajrAngle / 60.0
                HighLatitudeRule.ONE_SEVENTH -> 1.0 / 7.0
                HighLatitudeRule.NIGHT_MIDDLE -> 0.5
            }
            sunriseHour - (nightLength * portion)
        } else {
            dhuhrHour - fajrHourAngle / 15.0
        }

        // Asr calculation (shadow ratio = 1 for Standard/Shafi'i, 2 for Hanafi)
        val shadowFactor = if (isHanafiAsr) 2.0 else 1.0
        val asrAltitudeRad = atan(1.0 / (shadowFactor + tan(Math.toRadians(abs(latitude - sun.declination)))))
        val asrAltitudeDeg = Math.toDegrees(asrAltitudeRad)
        val asrHourAngle = hourAngle(asrAltitudeDeg, latitude, sun.declination)
        val asrHour = if (asrHourAngle.isNaN()) {
            dhuhrHour + 3.0
        } else {
            dhuhrHour + asrHourAngle / 15.0
        }

        // Isha calculation
        val ishaHour = if (ishaIntervalMinutes != null && ishaIntervalMinutes > 0) {
            maghribHour + (ishaIntervalMinutes / 60.0)
        } else {
            val ishaHourAngle = hourAngle(-ishaAngle, latitude, sun.declination)
            if (ishaHourAngle.isNaN()) {
                val portion = when (highLatRule) {
                    HighLatitudeRule.ANGLE_BASED -> ishaAngle / 60.0
                    HighLatitudeRule.ONE_SEVENTH -> 1.0 / 7.0
                    HighLatitudeRule.NIGHT_MIDDLE -> 0.5
                }
                maghribHour + (nightLength * portion)
            } else {
                dhuhrHour + ishaHourAngle / 15.0
            }
        }

        // Convert hours to minutes (0..1439) with per-prayer manual offsets
        fun toMinutes(hour: Double, prayerName: String): Int {
            val totalMins = Math.round(fixHour(hour) * 60.0).toInt()
            val offset = minuteOffsets[prayerName] ?: 0
            return (totalMins + offset + 1440) % 1440
        }

        // Add standard 1-minute safety buffer for Dhuhr
        val dhuhrSafeHour = dhuhrHour + (1.0 / 60.0)

        return CalculatedTimes(
            fajrMinutes = toMinutes(fajrHour, "Fajr"),
            sunriseMinutes = toMinutes(sunriseHour, "Sunrise"),
            dhuhrMinutes = toMinutes(dhuhrSafeHour, "Dhuhr"),
            asrMinutes = toMinutes(asrHour, "Asr"),
            maghribMinutes = toMinutes(maghribHour, "Maghrib"),
            ishaMinutes = toMinutes(ishaHour, "Isha")
        )
    }

    /**
     * Helper to calculate complete PrayerTime models for a given date, zone, and authority.
     */
    fun calculatePrayerTimesList(
        zone: PrayerZone,
        authority: CalculationAuthority,
        isHanafiAsr: Boolean,
        date: LocalDate = LocalDate.now(),
        minuteOffsets: Map<String, Int> = emptyMap(),
        currentHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        currentMinute: Int = Calendar.getInstance().get(Calendar.MINUTE)
    ): List<PrayerTime> {
        val timeZone = try {
            val tz = TimeZone.getTimeZone(zone.timeZoneId)
            val zId = ZoneId.of(zone.timeZoneId)
            val zdt = date.atStartOfDay(zId)
            zId.rules.getOffset(zdt.toInstant()).totalSeconds / 3600.0
        } catch (e: Exception) {
            // Fallback to local default device offset
            val tz = TimeZone.getDefault()
            val cal = Calendar.getInstance(tz).apply {
                set(date.year, date.monthValue - 1, date.dayOfMonth)
            }
            (tz.getOffset(cal.timeInMillis) / 3600000.0)
        }

        val times = calculateTimes(
            year = date.year,
            month = date.monthValue,
            day = date.dayOfMonth,
            latitude = zone.latitude,
            longitude = zone.longitude,
            timeZoneOffsetHours = timeZone,
            fajrAngle = authority.fajrAngle,
            ishaAngle = authority.ishaAngle,
            ishaIntervalMinutes = authority.ishaIntervalMinutes,
            isHanafiAsr = isHanafiAsr,
            minuteOffsets = minuteOffsets
        )

        val currentTotalMinutes = currentHour * 60 + currentMinute

        val rawSchedule = listOf(
            Triple("Fajr", "الفجر", times.fajrMinutes),
            Triple("Sunrise", "الشروق", times.sunriseMinutes),
            Triple("Dhuhr", "الظهر", times.dhuhrMinutes),
            Triple("Asr", "العصر", times.asrMinutes),
            Triple("Maghrib", "المغرب", times.maghribMinutes),
            Triple("Isha", "العشاء", times.ishaMinutes)
        )

        val prayerScheduleForNext = rawSchedule.filter { it.first != "Sunrise" }
        val isPreFajr = currentTotalMinutes < times.fajrMinutes

        val nextPrayerName = if (isPreFajr) {
            "Fajr"
        } else {
            val nextIdx = prayerScheduleForNext.indexOfFirst { it.third > currentTotalMinutes }
            if (nextIdx != -1) prayerScheduleForNext[nextIdx].first else "Fajr"
        }

        val prayerOnlyNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")

        return rawSchedule.map { (name, arabic, timeMin) ->
            val positiveMin = (timeMin + 1440) % 1440
            val h = positiveMin / 60
            val m = positiveMin % 60
            val timeStr = String.format(Locale.getDefault(), "%02d:%02d", h, m)

            val isSunrise = name == "Sunrise"
            val isPast: Boolean
            val isCurrent: Boolean
            val isNext: Boolean

            if (isSunrise) {
                isPast = currentTotalMinutes > timeMin
                isNext = false
                isCurrent = false
            } else if (isPreFajr) {
                isPast = (name != "Isha")
                isCurrent = (name == "Isha")
                isNext = (name == "Fajr")
            } else {
                isPast = currentTotalMinutes > timeMin
                isNext = (name == nextPrayerName)
                isCurrent = if (nextPrayerName == "Fajr") {
                    name == "Isha"
                } else {
                    val nextIdx = prayerOnlyNames.indexOf(nextPrayerName)
                    if (nextIdx > 0) {
                        prayerOnlyNames[nextIdx - 1] == name
                    } else {
                        name == "Isha"
                    }
                }
            }

            PrayerTime(
                name = name,
                arabicName = arabic,
                timeString = timeStr,
                hour = h,
                minute = m,
                isNext = isNext,
                isPast = isPast,
                isCurrent = isCurrent
            )
        }
    }

    fun formatMinutesToTimeString(totalMinutes: Int): String {
        val h = (totalMinutes / 60) % 24
        val m = totalMinutes % 60
        return String.format(Locale.US, "%02d:%02d", h, m)
    }

    fun calculateSupplementaryTimes(
        zone: PrayerZone,
        authority: CalculationAuthority,
        isHanafiAsr: Boolean,
        date: LocalDate = LocalDate.now(),
        minuteOffsets: Map<String, Int> = emptyMap()
    ): SupplementaryTimes {
        val timeZone = try {
            val zId = ZoneId.of(zone.timeZoneId)
            val zdt = date.atStartOfDay(zId)
            zId.rules.getOffset(zdt.toInstant()).totalSeconds / 3600.0
        } catch (e: Exception) {
            val tz = TimeZone.getDefault()
            val cal = Calendar.getInstance(tz).apply {
                set(date.year, date.monthValue - 1, date.dayOfMonth)
            }
            tz.getOffset(cal.timeInMillis) / 3600000.0
        }

        val times = calculateTimes(
            year = date.year,
            month = date.monthValue,
            day = date.dayOfMonth,
            latitude = zone.latitude,
            longitude = zone.longitude,
            timeZoneOffsetHours = timeZone,
            fajrAngle = authority.fajrAngle,
            ishaAngle = authority.ishaAngle,
            ishaIntervalMinutes = authority.ishaIntervalMinutes,
            isHanafiAsr = isHanafiAsr,
            minuteOffsets = minuteOffsets
        )

        return SupplementaryTimes(
            duhaTimeString = formatMinutesToTimeString(times.duhaMinutes),
            midnightTimeString = formatMinutesToTimeString(times.midnightMinutes),
            qiyamTimeString = formatMinutesToTimeString(times.qiyamMinutes)
        )
    }

    // --- Astronomical Mathematical Primitives ---

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = Math.floor(y / 100.0)
        val b = 2.0 - a + Math.floor(a / 4.0)
        return Math.floor(365.25 * (y + 4716)) + Math.floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private data class SunCoord(val declination: Double, val equationOfTime: Double)

    private fun sunPosition(jd: Double): SunCoord {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sinDeg(g) + 0.020 * sinDeg(2 * g))
        val e = 23.439 - 0.00000036 * d
        val dRad = asin(sinDeg(e) * sinDeg(l))
        val raRad = atan2(cosDeg(e) * sinDeg(l), cosDeg(l))
        var ra = Math.toDegrees(raRad) / 15.0
        ra = fixHour(ra)
        val eqTime = (q / 15.0 - ra) * 60.0 // in minutes
        return SunCoord(declination = Math.toDegrees(dRad), equationOfTime = eqTime)
    }

    private fun midDay(timeZone: Double, longitude: Double, eqTimeMinutes: Double): Double {
        return fixHour(12.0 + timeZone - longitude / 15.0 - eqTimeMinutes / 60.0)
    }

    private fun hourAngle(altitudeDeg: Double, latitudeDeg: Double, declinationDeg: Double): Double {
        val latRad = Math.toRadians(latitudeDeg)
        val decRad = Math.toRadians(declinationDeg)
        val altRad = Math.toRadians(altitudeDeg)
        val cosH = (sin(altRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
        return if (cosH < -1.0 || cosH > 1.0) {
            Double.NaN
        } else {
            Math.toDegrees(acos(cosH))
        }
    }

    private fun fixAngle(a: Double): Double {
        var ang = a - 360.0 * Math.floor(a / 360.0)
        if (ang < 0) ang += 360.0
        return ang
    }

    private fun fixHour(h: Double): Double {
        var hr = h - 24.0 * Math.floor(h / 24.0)
        if (hr < 0) hr += 24.0
        return hr
    }

    private fun sinDeg(d: Double): Double = sin(Math.toRadians(d))
    private fun cosDeg(d: Double): Double = cos(Math.toRadians(d))
}
