package com.example.data.prayer

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.R
import java.io.File
import java.io.FileOutputStream

/**
 * Metadata representation of an Adhan sound.
 */
data class AdhanSound(
    val id: String,
    val title: String,
    val subtitle: String,
    val rawResId: Int? = null,
    val isFajrSpecific: Boolean = false,
    val customFilePath: String? = null,
    val remoteUrl: String? = null
) {
    val isBundled: Boolean get() = rawResId != null
    val isCustom: Boolean get() = customFilePath != null
    val isRemote: Boolean get() = remoteUrl != null
}

/**
 * Manages bundled offline adhan files, per-prayer sound choices,
 * custom user-imported device audio, and optional remote streams.
 */
object AdhanAudioRepository {

    private const val TAG = "AdhanAudioRepository"
    private const val PREFS_NAME = "noor_app_preferences"

    const val ID_FAJR = "bundled_fajr"
    const val ID_MAKKAH = "bundled_makkah"
    const val ID_MADINAH = "bundled_madinah"
    const val ID_CUSTOM = "custom_device_audio"

    private const val PREF_GLOBAL_SOUND = "adhan_sound_global"
    private const val PREF_PRAYER_SOUND_PREFIX = "adhan_sound_"
    private const val PREF_CUSTOM_FILE_NAME = "custom_adhan_filename"
    private const val CUSTOM_FILE_NAME = "custom_adhan_audio.mp3"

    /**
     * Default bundled offline audio sources guaranteed to be available with zero network connection.
     */
    fun getBundledSounds(): List<AdhanSound> {
        return listOf(
            AdhanSound(
                id = ID_FAJR,
                title = "Fajr Adhan (Al-Haram Al-Maki)",
                subtitle = "Al-Haram Al-Maki • Offline Bundled",
                rawResId = R.raw.adhan_fajr,
                isFajrSpecific = true
            ),
            AdhanSound(
                id = ID_MAKKAH,
                title = "Makkah Al-Mukarramah",
                subtitle = "Sheikh Ali Ahmad Mulla • Offline Bundled",
                rawResId = R.raw.adhan_makkah,
                isFajrSpecific = false
            ),
            AdhanSound(
                id = ID_MADINAH,
                title = "Madinah Al-Munawwarah",
                subtitle = "Al-Haram Al-Madani • Offline Bundled",
                rawResId = R.raw.adhan_madinah,
                isFajrSpecific = false
            )
        )
    }

    /**
     * Returns all available adhan sound options: bundled offline files, custom picked file (if present),
     * and optional remote stream enhancements.
     */
    fun getAllSounds(context: Context): List<AdhanSound> {
        val result = getBundledSounds().toMutableList()

        // Check for imported custom audio file in internal storage
        val customFile = File(context.filesDir, CUSTOM_FILE_NAME)
        if (customFile.exists() && customFile.length() > 0) {
            val prefs = getPrefs(context)
            val displayName = prefs.getString(PREF_CUSTOM_FILE_NAME, "My Device Adhan") ?: "My Device Adhan"
            result.add(
                AdhanSound(
                    id = ID_CUSTOM,
                    title = displayName,
                    subtitle = "Custom file from device storage • Offline",
                    customFilePath = customFile.absolutePath,
                    isFajrSpecific = false
                )
            )
        }

        return result
    }

    fun getDefaultSoundIdForPrayer(prayerName: String): String {
        return if (prayerName.equals("Fajr", ignoreCase = true)) {
            ID_FAJR
        } else {
            ID_MAKKAH
        }
    }

    /**
     * Retrieves the selected AdhanSound configured for a specific prayer.
     * Fajr defaults to ID_FAJR with 'As-Salatu Khayrun Min An-Nawm', others default to ID_MAKKAH.
     */
    fun getSoundForPrayer(context: Context, prayerName: String): AdhanSound {
        val prefs = getPrefs(context)
        val defaultId = getDefaultSoundIdForPrayer(prayerName)
        val prayerPrefKey = "$PREF_PRAYER_SOUND_PREFIX$prayerName"
        val configuredId = prefs.getString(prayerPrefKey, null)
            ?: prefs.getString(PREF_GLOBAL_SOUND, defaultId)
            ?: defaultId

        return getSoundById(context, configuredId, fallbackPrayer = prayerName)
    }

    /**
     * Resolves a sound by ID, with robust fallback to bundled offline recordings.
     */
    fun getSoundById(context: Context, soundId: String?, fallbackPrayer: String = "Salat"): AdhanSound {
        val allSounds = getAllSounds(context)
        if (soundId != null) {
            val exact = allSounds.firstOrNull { it.id == soundId }
            if (exact != null) return exact

            // Backward compatibility matching against legacy display titles
            val byTitle = allSounds.firstOrNull {
                it.title.contains(soundId, ignoreCase = true) || soundId.contains(it.title, ignoreCase = true)
            }
            if (byTitle != null) return byTitle
        }

        // Fallback to default bundled sound
        val defaultId = getDefaultSoundIdForPrayer(fallbackPrayer)
        return allSounds.firstOrNull { it.id == defaultId }
            ?: getBundledSounds().first()
    }

    /**
     * Sets the adhan sound for a specific prayer (e.g. Fajr, Dhuhr, Asr, Maghrib, Isha).
     */
    fun setSoundForPrayer(context: Context, prayerName: String, soundId: String) {
        getPrefs(context).edit()
            .putString("$PREF_PRAYER_SOUND_PREFIX$prayerName", soundId)
            .apply()
        Log.d(TAG, "Configured prayer $prayerName with adhan sound: $soundId")
    }

    /**
     * Sets the default global adhan sound.
     */
    fun setGlobalSound(context: Context, soundId: String) {
        getPrefs(context).edit()
            .putString(PREF_GLOBAL_SOUND, soundId)
            .apply()
        Log.d(TAG, "Configured global adhan sound: $soundId")
    }

    /**
     * Imports a user-selected audio file from device storage (via Storage Access Framework).
     * Copies the content to app internal storage so playback never requires dangerous storage
     * permissions and persists across reboots.
     */
    fun importCustomAudio(context: Context, uri: Uri): AdhanSound? {
        return try {
            var displayName = "Custom Device Adhan"
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIdx >= 0) {
                        val name = cursor.getString(nameIdx)
                        if (!name.isNullOrBlank()) {
                            displayName = name
                        }
                    }
                }
            }

            val targetFile = File(context.filesDir, CUSTOM_FILE_NAME)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            if (!targetFile.exists() || targetFile.length() == 0L) {
                Log.e(TAG, "Failed to copy custom audio: target file empty")
                return null
            }

            getPrefs(context).edit()
                .putString(PREF_CUSTOM_FILE_NAME, displayName)
                .apply()

            Log.d(TAG, "Imported custom audio: $displayName (${targetFile.length()} bytes)")
            AdhanSound(
                id = ID_CUSTOM,
                title = displayName,
                subtitle = "Custom file from device storage • Offline",
                customFilePath = targetFile.absolutePath,
                isFajrSpecific = false
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception importing custom audio: ${e.message}", e)
            null
        }
    }

    /**
     * Removes the imported custom audio file and reverts any prayer sound configured to it.
     */
    fun deleteCustomAudio(context: Context) {
        try {
            val targetFile = File(context.filesDir, CUSTOM_FILE_NAME)
            if (targetFile.exists()) {
                targetFile.delete()
            }
            getPrefs(context).edit()
                .remove(PREF_CUSTOM_FILE_NAME)
                .apply()
        } catch (e: Exception) {
            Log.w(TAG, "Error removing custom audio: ${e.message}")
        }
    }

    fun hasCustomAudio(context: Context): Boolean {
        val file = File(context.filesDir, CUSTOM_FILE_NAME)
        return file.exists() && file.length() > 0
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
