package com.example.data.prayer

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Encapsulates real-time compass telemetry including raw magnetic heading,
 * True North corrected heading (via GeomagneticField declination), sensor accuracy,
 * and calibration state.
 */
data class CompassReading(
    val rawHeading: Float,       // 0..360° relative to Magnetic North
    val trueHeading: Float,      // 0..360° relative to True North (Declination corrected)
    val accuracy: Int,           // SensorManager.SENSOR_STATUS_* (0=UNRELIABLE, 1=LOW, 2=MEDIUM, 3=HIGH)
    val declination: Float,      // Degrees declination applied (+ = East, - = West)
    val isLowAccuracy: Boolean   // True if accuracy is LOW (1) or UNRELIABLE (0)
)

/**
 * Provides real-time heading and True North azimuth using hardware rotation vector
 * and geomagnetic sensors, corrected with magnetic declination.
 */
class CompassSensorManager(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val rotationVectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    val hasCompassSensors: Boolean
        get() = rotationVectorSensor != null || (accelerometerSensor != null && magnetometerSensor != null)

    /**
     * Flow of comprehensive compass readings with True North declination correction
     * and accuracy state monitoring.
     */
    fun getCompassFlow(
        latitude: Double,
        longitude: Double,
        altitudeMeters: Double = 0.0
    ): Flow<CompassReading> = callbackFlow {
        if (sensorManager == null) {
            trySend(
                CompassReading(
                    rawHeading = 0f,
                    trueHeading = 0f,
                    accuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
                    declination = 0f,
                    isLowAccuracy = false
                )
            )
            close()
            return@callbackFlow
        }

        val declination = try {
            val geoField = GeomagneticField(
                latitude.toFloat(),
                longitude.toFloat(),
                altitudeMeters.toFloat(),
                System.currentTimeMillis()
            )
            geoField.declination
        } catch (e: Exception) {
            0f
        }

        var smoothedHeading = 0f
        var hasInitializedHeading = false
        var currentAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH

        val listener = object : SensorEventListener {
            private val rotationMatrix = FloatArray(9)
            private val orientationAngles = FloatArray(3)
            private val truncatedRotationVector = FloatArray(4)

            private var lastAccelerometer = FloatArray(3)
            private var lastMagnetometer = FloatArray(3)
            private var lastAccelerometerSet = false
            private var lastMagnetometerSet = false

            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                if (event.accuracy != SensorManager.SENSOR_STATUS_NO_CONTACT) {
                    currentAccuracy = event.accuracy
                }

                var rawHeading: Float? = null

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val vec = if (event.values.size > 4) {
                        System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4)
                        truncatedRotationVector
                    } else {
                        event.values
                    }

                    SensorManager.getRotationMatrixFromVector(rotationMatrix, vec)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val rad = orientationAngles[0]
                    rawHeading = (Math.toDegrees(rad.toDouble()).toFloat() + 360f) % 360f
                } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
                    lastAccelerometerSet = true
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
                    lastMagnetometerSet = true
                }

                if (rawHeading == null && lastAccelerometerSet && lastMagnetometerSet) {
                    val success = SensorManager.getRotationMatrix(
                        rotationMatrix,
                        null,
                        lastAccelerometer,
                        lastMagnetometer
                    )
                    if (success) {
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)
                        val rad = orientationAngles[0]
                        rawHeading = (Math.toDegrees(rad.toDouble()).toFloat() + 360f) % 360f
                    }
                }

                rawHeading?.let { heading ->
                    // Smooth filtering for natural compass rotation without jitter
                    if (!hasInitializedHeading) {
                        smoothedHeading = heading
                        hasInitializedHeading = true
                    } else {
                        // Angle diff accounting for 0/360 wrap-around
                        val diff = (heading - smoothedHeading + 540f) % 360f - 180f
                        smoothedHeading = (smoothedHeading + diff * 0.25f + 360f) % 360f
                    }

                    val trueHeading = (smoothedHeading + declination + 360f) % 360f
                    val isLow = currentAccuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW ||
                            currentAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE

                    trySend(
                        CompassReading(
                            rawHeading = smoothedHeading,
                            trueHeading = trueHeading,
                            accuracy = currentAccuracy,
                            declination = declination,
                            isLowAccuracy = isLow
                        )
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                currentAccuracy = accuracy
                val isLow = accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW ||
                        accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE
                val trueHeading = (smoothedHeading + declination + 360f) % 360f

                trySend(
                    CompassReading(
                        rawHeading = smoothedHeading,
                        trueHeading = trueHeading,
                        accuracy = accuracy,
                        declination = declination,
                        isLowAccuracy = isLow
                    )
                )
            }
        }

        // Register sensors
        if (rotationVectorSensor != null) {
            sensorManager.registerListener(
                listener,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        } else {
            if (accelerometerSensor != null) {
                sensorManager.registerListener(
                    listener,
                    accelerometerSensor,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
            if (magnetometerSensor != null) {
                sensorManager.registerListener(
                    listener,
                    magnetometerSensor,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    /**
     * Legacy simple heading flow.
     */
    fun getHeadingFlow(): Flow<Float> = callbackFlow {
        getCompassFlow(0.0, 0.0).collect {
            trySend(it.trueHeading)
        }
        awaitClose { }
    }
}

