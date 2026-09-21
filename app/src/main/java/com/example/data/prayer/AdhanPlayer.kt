package com.example.data.prayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.R
import com.example.data.local.NoorNotificationHelper
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * Bulletproof Adhan audio playback manager.
 * Features:
 * - Bundled offline audio files played directly from resources with zero network reliance.
 * - Per-prayer sound selection (with Fajr special phrase support).
 * - Custom audio file support imported from device storage.
 * - Dynamic volume support respecting the in-app volume slider.
 * - Session tracking and explicit stop flags to prevent race conditions.
 * - Dismissal guard flag so stopped adhans cannot restart until next prayer time.
 * - Physical volume-down silencing support.
 * - Decoupled from Mosque Mode (never overrides system ringer settings).
 */
object AdhanPlayer {

    private const val TAG = "AdhanPlayer"
    private const val ADHAN_MAX_DURATION_MS = 4 * 60 * 1000L // 4 minutes safety timeout
    private const val DISMISSAL_GUARD_WINDOW_MS = 45 * 60 * 1000L // 45 minutes
    private const val PREFS_NAME = "noor_app_preferences"

    private enum class PlayState { IDLE, PREPARING, PLAYING, STOPPED }

    @Volatile
    private var currentState: PlayState = PlayState.IDLE

    private val currentSessionId = AtomicLong(0)
    private val isExplicitlyStopped = AtomicBoolean(false)

    @Volatile
    private var currentActivePrayer: String? = null

    @Volatile
    private var lastDismissedPrayer: String? = null

    @Volatile
    private var lastDismissedTimeMillis: Long = 0L

    private var mediaPlayer: MediaPlayer? = null
    private var ringtone: Ringtone? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private var timeoutRunnable: Runnable? = null

    private var volumeReceiver: BroadcastReceiver? = null
    private var applicationContext: Context? = null

    fun isPlaying(): Boolean {
        return currentState == PlayState.PLAYING || currentState == PlayState.PREPARING ||
                (mediaPlayer?.isPlaying == true) || (ringtone?.isPlaying == true)
    }

    /**
     * Plays the Adhan for a specific prayer time.
     * Uses the per-prayer sound configuration (or soundIdOverride if provided for preview).
     */
    fun play(
        context: Context,
        prayerName: String = "Salat",
        isSnooze: Boolean = false,
        soundIdOverride: String? = null
    ) {
        applicationContext = context.applicationContext

        // Guard Check: Verify if this prayer was already stopped/dismissed
        if (!isSnooze && isGuardedAgainstRestart(context, prayerName)) {
            Log.w(TAG, "Adhan for $prayerName was already dismissed by user. Guard flag active - skipping playback.")
            return
        }

        // Stop any currently running instance cleanly
        stop(prayerName = null, isDismiss = false)

        val sessionId = currentSessionId.incrementAndGet()
        isExplicitlyStopped.set(false)
        currentState = PlayState.PREPARING
        currentActivePrayer = prayerName

        registerVolumeObserver(context)

        // Safety timeout to prevent hanging players
        timeoutRunnable = Runnable {
            if (sessionId == currentSessionId.get() && isPlaying()) {
                Log.d(TAG, "Adhan completed max safety duration (4m). Releasing player.")
                stop(prayerName = prayerName, isDismiss = false)
            }
        }
        mainHandler.postDelayed(timeoutRunnable!!, ADHAN_MAX_DURATION_MS)

        // Determine sound to play
        val sound = if (soundIdOverride != null) {
            AdhanAudioRepository.getSoundById(context, soundIdOverride, fallbackPrayer = prayerName)
        } else {
            AdhanAudioRepository.getSoundForPrayer(context, prayerName)
        }

        // Determine user volume
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val volumePercent = prefs.getInt("adhan_volume", 85)
        val volFactor = (volumePercent.coerceIn(0, 100)) / 100f

        try {
            val mp = MediaPlayer()
            mediaPlayer = mp
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )

            mp.setOnPreparedListener { player ->
                mainHandler.post {
                    if (sessionId != currentSessionId.get() || isExplicitlyStopped.get()) {
                        Log.d(TAG, "Player prepared after stop was called. Aborting.")
                        safeReleasePlayer(player)
                        return@post
                    }
                    try {
                        player.setVolume(volFactor, volFactor)
                        player.start()
                        currentState = PlayState.PLAYING
                        Log.d(TAG, "Adhan started playing successfully for $prayerName using sound: ${sound.title} at ${volumePercent}% volume.")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to start MediaPlayer: ${e.message}")
                        if (sessionId == currentSessionId.get() && !isExplicitlyStopped.get()) {
                            playBundledFallback(context, player, prayerName, sessionId, volFactor)
                        }
                    }
                }
            }

            mp.setOnCompletionListener { player ->
                mainHandler.post {
                    Log.d(TAG, "Adhan audio completed naturally.")
                    if (sessionId == currentSessionId.get()) {
                        stop(prayerName = prayerName, isDismiss = false)
                    }
                }
            }

            mp.setOnErrorListener { player, what, extra ->
                mainHandler.post {
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra (Session: $sessionId)")
                    if (sessionId != currentSessionId.get() || isExplicitlyStopped.get()) {
                        // User dismissed or session cancelled - do NOT play fallback!
                        Log.d(TAG, "Ignoring error callback because player was explicitly stopped.")
                        safeReleasePlayer(player)
                        return@post
                    }
                    safeReleasePlayer(player)
                    mediaPlayer = null
                    // Try offline bundled fallback first
                    playBundledFallback(context, null, prayerName, sessionId, volFactor)
                }
                true
            }

            // Configure audio data source based on sound type
            when {
                sound.rawResId != null -> {
                    // 1. Primary path: Bundled offline file (guaranteed zero network dependency)
                    val afd = context.resources.openRawResourceFd(sound.rawResId)
                    mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    afd.close()
                    mp.prepareAsync()
                }

                sound.customFilePath != null -> {
                    // 2. Custom imported device audio
                    val customFile = File(sound.customFilePath)
                    if (customFile.exists() && customFile.length() > 0) {
                        mp.setDataSource(customFile.absolutePath)
                        mp.prepareAsync()
                    } else {
                        Log.w(TAG, "Custom audio file not found. Falling back to bundled offline sound.")
                        playBundledFallback(context, mp, prayerName, sessionId, volFactor)
                    }
                }

                sound.remoteUrl != null -> {
                    // 3. Optional remote stream enhancement
                    mp.setDataSource(sound.remoteUrl)
                    mp.prepareAsync()
                }

                else -> {
                    playBundledFallback(context, mp, prayerName, sessionId, volFactor)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Exception preparing MediaPlayer: ${e.message}. Attempting bundled fallback.")
            if (sessionId == currentSessionId.get() && !isExplicitlyStopped.get()) {
                playBundledFallback(context, null, prayerName, sessionId, volFactor)
            }
        }
    }

    /**
     * Fallback to offline bundled raw sound first before resorting to system ringtone.
     */
    private fun playBundledFallback(
        context: Context,
        existingPlayer: MediaPlayer?,
        prayerName: String,
        sessionId: Long,
        volFactor: Float
    ) {
        if (sessionId != currentSessionId.get() || isExplicitlyStopped.get()) return

        try {
            val mp = existingPlayer ?: MediaPlayer().also { mediaPlayer = it }
            mp.reset()
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )

            val fallbackRawId = if (prayerName.equals("Fajr", ignoreCase = true)) {
                R.raw.adhan_fajr
            } else {
                R.raw.adhan_makkah
            }

            val afd = context.resources.openRawResourceFd(fallbackRawId)
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()

            mp.setOnPreparedListener { player ->
                mainHandler.post {
                    if (sessionId != currentSessionId.get() || isExplicitlyStopped.get()) {
                        safeReleasePlayer(player)
                        return@post
                    }
                    player.setVolume(volFactor, volFactor)
                    player.start()
                    currentState = PlayState.PLAYING
                    Log.d(TAG, "Started bundled fallback adhan successfully.")
                }
            }

            mp.setOnCompletionListener {
                if (sessionId == currentSessionId.get()) {
                    stop(prayerName = prayerName, isDismiss = false)
                }
            }

            mp.setOnErrorListener { player, _, _ ->
                safeReleasePlayer(player)
                mediaPlayer = null
                if (sessionId == currentSessionId.get() && !isExplicitlyStopped.get()) {
                    playSystemFallback(context, sessionId)
                }
                true
            }

            mp.prepareAsync()
        } catch (e: Exception) {
            Log.e(TAG, "Bundled fallback failed: ${e.message}. Falling back to system ringtone.")
            safeReleasePlayer(mediaPlayer)
            mediaPlayer = null
            playSystemFallback(context, sessionId)
        }
    }

    private fun playSystemFallback(context: Context, sessionId: Long) {
        if (sessionId != currentSessionId.get() || isExplicitlyStopped.get()) {
            return
        }
        try {
            val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtone = RingtoneManager.getRingtone(context, alertUri)?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                }
                play()
                currentState = PlayState.PLAYING
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play fallback system ringtone: ${e.message}")
        }
    }

    /**
     * Dynamically updates volume for the active MediaPlayer instance.
     */
    fun setVolume(volumePercent: Int) {
        val vol = (volumePercent.coerceIn(0, 100)) / 100f
        try {
            mediaPlayer?.setVolume(vol, vol)
        } catch (e: Exception) {
            Log.w(TAG, "Could not adjust player volume: ${e.message}")
        }
    }

    /**
     * Stops playback and records dismissal guard state if requested.
     */
    fun stop(prayerName: String? = null, isDismiss: Boolean = false) {
        val wasPlaying = isPlaying()
        val targetPrayer = prayerName ?: currentActivePrayer

        isExplicitlyStopped.set(true)
        currentState = PlayState.STOPPED
        currentSessionId.incrementAndGet()

        // Cancel timeout runnable
        timeoutRunnable?.let { mainHandler.removeCallbacks(it) }
        timeoutRunnable = null

        // Unregister volume observer
        unregisterVolumeObserver()

        // If this was an explicit dismissal or it was actively playing, record guard state
        if (isDismiss || wasPlaying) {
            targetPrayer?.let { name ->
                lastDismissedPrayer = name
                lastDismissedTimeMillis = System.currentTimeMillis()
                saveDismissalToPrefs(name, lastDismissedTimeMillis)
                Log.d(TAG, "Dismissal recorded for prayer: $name at $lastDismissedTimeMillis")
            }
        }

        // Clean up MediaPlayer safely
        val mp = mediaPlayer
        mediaPlayer = null
        safeReleasePlayer(mp)

        // Clean up Ringtone safely
        try {
            ringtone?.let {
                if (it.isPlaying) {
                    it.stop()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ringtone = null
        }

        currentActivePrayer = null
    }

    private fun isGuardedAgainstRestart(context: Context, prayerName: String): Boolean {
        val now = System.currentTimeMillis()

        // 1. In-memory check
        if (prayerName.equals(lastDismissedPrayer, ignoreCase = true) &&
            (now - lastDismissedTimeMillis) < DISMISSAL_GUARD_WINDOW_MS
        ) {
            return true
        }

        // 2. Persistent SharedPreferences check
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedPrayer = prefs.getString("guard_last_dismissed_prayer", null)
            val savedTime = prefs.getLong("guard_last_dismissed_time", 0L)
            if (prayerName.equals(savedPrayer, ignoreCase = true) && (now - savedTime) < DISMISSAL_GUARD_WINDOW_MS) {
                lastDismissedPrayer = savedPrayer
                lastDismissedTimeMillis = savedTime
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    private fun saveDismissalToPrefs(prayerName: String, timeMillis: Long) {
        applicationContext?.let { ctx ->
            try {
                ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString("guard_last_dismissed_prayer", prayerName)
                    .putLong("guard_last_dismissed_time", timeMillis)
                    .apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun safeReleasePlayer(mp: MediaPlayer?) {
        if (mp == null) return
        try {
            mp.setOnPreparedListener(null)
            mp.setOnCompletionListener(null)
            mp.setOnErrorListener(null)
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.reset()
            mp.release()
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing MediaPlayer: ${e.message}")
        }
    }

    private fun registerVolumeObserver(context: Context) {
        if (volumeReceiver != null) return
        try {
            val filter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context?, intent: Intent?) {
                    if (intent?.action == "android.media.VOLUME_CHANGED_ACTION") {
                        val prev = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", -1)
                        val curr = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1)
                        if (curr in 0 until prev && isPlaying()) {
                            Log.d(TAG, "Volume-down detected via system broadcast. Silencing adhan.")
                            c?.let {
                                NoorNotificationHelper.cancelPrayerAlerts(it)
                            }
                            stop(prayerName = currentActivePrayer, isDismiss = true)
                        }
                    }
                }
            }
            volumeReceiver = receiver
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.applicationContext.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                context.applicationContext.registerReceiver(receiver, filter)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not register volume broadcast receiver: ${e.message}")
        }
    }

    private fun unregisterVolumeObserver() {
        val receiver = volumeReceiver ?: return
        val ctx = applicationContext ?: return
        try {
            ctx.unregisterReceiver(receiver)
        } catch (e: Exception) {
            // Receiver might not be registered or already unregistered
        } finally {
            volumeReceiver = null
        }
    }
}
