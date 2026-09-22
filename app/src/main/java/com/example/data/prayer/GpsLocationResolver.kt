package com.example.data.prayer

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.example.data.model.PrayerZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

object GpsLocationResolver {

    suspend fun resolveCurrentZone(context: Context): PrayerZone? {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                ?: return null

            val hasCoarse = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            val hasFine = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!hasCoarse && !hasFine) {
                return null
            }

            // Fast path: Try getLastKnownLocation on enabled providers
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                try {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null) {
                        if (bestLocation == null || loc.time > bestLocation.time) {
                            bestLocation = loc
                        }
                    }
                } catch (e: SecurityException) {
                    // Ignore and try next
                } catch (e: Exception) {
                    // Ignore
                }
            }

            val now = System.currentTimeMillis()
            // If we have a very recent location (within 5 minutes), use it!
            val location = if (bestLocation != null && (now - bestLocation.time) < 300_000) {
                bestLocation
            } else {
                // Slow path: request a fresh location update with 10s timeout
                requestLocationUpdate(locationManager, providers, hasFine)
            } ?: bestLocation // fallback to last known location if we couldn't get a fresh update

            if (location == null) {
                return null
            }

            // Try to reverse geocode name/arabicName with 3s timeout
            val names = reverseGeocode(context, location)
            val name = names?.first ?: "Current Location"
            val arabicName = names?.second ?: "الموقع الحالي"

            PrayerZone(
                id = "gps_current",
                name = name,
                arabicName = arabicName,
                country = "",
                zoneLabel = "",
                latitude = location.latitude,
                longitude = location.longitude,
                timeZoneId = java.time.ZoneId.systemDefault().id
            )
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun requestLocationUpdate(
        locationManager: LocationManager,
        providers: List<String>,
        hasFine: Boolean
    ): Location? {
        return withTimeoutOrNull(10000L) {
            suspendCancellableCoroutine { continuation ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                // Pick best provider. Under ACCESS_COARSE_LOCATION, NETWORK_PROVIDER is highly preferred.
                val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                val provider = when {
                    isNetworkEnabled -> LocationManager.NETWORK_PROVIDER
                    isGpsEnabled && hasFine -> LocationManager.GPS_PROVIDER
                    isGpsEnabled -> LocationManager.GPS_PROVIDER
                    else -> providers.firstOrNull()
                }

                if (provider == null) {
                    continuation.resume(null)
                    return@suspendCancellableCoroutine
                }

                try {
                    locationManager.requestLocationUpdates(
                        provider,
                        0L,
                        0f,
                        listener,
                        android.os.Looper.getMainLooper()
                    )
                } catch (e: SecurityException) {
                    // Try coarse/fallback if GPS threw SecurityException
                    if (provider == LocationManager.GPS_PROVIDER && isNetworkEnabled) {
                        try {
                            locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                0L,
                                0f,
                                listener,
                                android.os.Looper.getMainLooper()
                            )
                        } catch (ex: Exception) {
                            if (continuation.isActive) continuation.resume(null)
                        }
                    } else {
                        if (continuation.isActive) continuation.resume(null)
                    }
                } catch (e: Exception) {
                    if (continuation.isActive) continuation.resume(null)
                }

                continuation.invokeOnCancellation {
                    try {
                        locationManager.removeUpdates(listener)
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
            }
        }
    }

    private suspend fun reverseGeocode(context: Context, location: Location): Pair<String, String>? {
        return withTimeoutOrNull(3000L) {
            try {
                val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())
                withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val locality = address.locality ?: address.subAdminArea ?: address.adminArea ?: ""
                        val country = address.countryName ?: ""
                        
                        val englishName = if (locality.isNotEmpty()) {
                            if (country.isNotEmpty()) "$locality, $country" else locality
                        } else {
                            "Current Location"
                        }

                        var arabicName = "الموقع الحالي"
                        try {
                            val arGeocoder = android.location.Geocoder(context, java.util.Locale("ar"))
                            @Suppress("DEPRECATION")
                            val arAddresses = arGeocoder.getFromLocation(location.latitude, location.longitude, 1)
                            if (!arAddresses.isNullOrEmpty()) {
                                val arAddress = arAddresses[0]
                                val arLocality = arAddress.locality ?: arAddress.subAdminArea ?: arAddress.adminArea ?: ""
                                val arCountry = arAddress.countryName ?: ""
                                if (arLocality.isNotEmpty()) {
                                    arabicName = if (arCountry.isNotEmpty()) "$arLocality، $arCountry" else arLocality
                                }
                            }
                        } catch (e: Exception) {
                            // ignore and stick with defaults
                        }

                        Pair(englishName, arabicName)
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
