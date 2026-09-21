package com.example.data.quran

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.example.data.model.HifzAyahTiming
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * UI/Playback state for the isolated Hifz (memorization) word-level audio drill player.
 */
data class HifzAudioState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val isPaused: Boolean = false,
    val surahNumber: Int = 0,
    val ayahNumber: Int = 0,
    val fromWordIndex: Int = 0,
    val toWordIndex: Int = 0,
    val currentWordIndex: Int? = null,
    val currentPositionMs: Long = 0L,
    val targetStartMs: Long = 0L,
    val targetEndMs: Long = 0L,
    val totalAyahDurationMs: Long = 0L,
    val audioUrl: String = "",
    val reciterId: String = "alafasy",
    val currentLoop: Int = 1,
    val totalLoops: Int = 1,
    val errorMessage: String? = null
)

/**
 * Isolated audio controller for Hifz memorization drills.
 * 
 * Provides millisecond-accurate word range playback (e.g. words 1..5) using Tarteel CDN
 * audio streams and QUL word timing segment data.
 * 
 * Completely decoupled from the general Quran/Ayah MediaPlayer to guarantee zero side effects.
 */
class HifzAudioController(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {
    companion object {
        private const val TAG = "HifzAudioController"
        private const val TRACKER_INTERVAL_MS = 15L // 15ms resolution for crisp word boundary stopping

        // Acoustic tolerance buffers to prevent clipping phonetic onsets/offsets and account for MP3 frame seek inaccuracy
        private const val START_BUFFER_MS = 35L // Start ~35ms early to avoid clipping word onset
        private const val STOP_BUFFER_MS = 45L  // Stop ~45ms late so final vowels/consonants don't get cut off
    }

    private var mediaPlayer: MediaPlayer? = null
    private var currentTiming: HifzAyahTiming? = null
    private var progressJob: Job? = null
    private var onPlaybackCompleteCallback: (() -> Unit)? = null

    private var targetStartMs: Long = 0L
    private var targetEndMs: Long = 0L
    private var currentLoopCount: Int = 1
    private var totalLoopCount: Int = 1
    private var delayBetweenLoopsMs: Long = 0L
    private var isCompleting: Boolean = false
    private var isLastSegmentInAyah: Boolean = false

    private val _state = MutableStateFlow(HifzAudioState())
    val state: StateFlow<HifzAudioState> = _state.asStateFlow()

    /**
     * Plays a specific word range [fromWordIndex..toWordIndex] within an Ayah.
     *
     * Playback automatically stops at the exact millisecond boundary of [toWordIndex].
     *
     * @param surahNumber Surah number (1-114).
     * @param ayahNumber Ayah number within the Surah.
     * @param fromWordIndex 1-based start word index (inclusive).
     * @param toWordIndex 1-based end word index (inclusive).
     * @param reciterId Reciter identifier (e.g. "alafasy").
     * @param loopCount Number of times to loop this exact word range before stopping (default 1).
     * @param delayBetweenLoops Delay in milliseconds between consecutive loop repetitions.
     * @param onComplete Callback invoked when all requested loops finish.
     */
    fun playWordRange(
        surahNumber: Int,
        ayahNumber: Int,
        fromWordIndex: Int,
        toWordIndex: Int,
        reciterId: String = "alafasy",
        loopCount: Int = 1,
        delayBetweenLoops: Long = 0L,
        onComplete: () -> Unit = {}
    ) {
        scope.launch {
            stopInternal(clearState = false)

            _state.value = _state.value.copy(
                isBuffering = true,
                isPlaying = false,
                isPaused = false,
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                fromWordIndex = fromWordIndex,
                toWordIndex = toWordIndex,
                reciterId = reciterId,
                currentLoop = 1,
                totalLoops = loopCount.coerceAtLeast(1),
                errorMessage = null
            )

            currentLoopCount = 1
            totalLoopCount = loopCount.coerceAtLeast(1)
            delayBetweenLoopsMs = delayBetweenLoops
            onPlaybackCompleteCallback = onComplete

            val timing = HifzWordTimingRepository.getAyahTiming(context, reciterId, surahNumber, ayahNumber)
            if (timing == null) {
                val error = "Timing data unavailable for $surahNumber:$ayahNumber ($reciterId)"
                Log.w(TAG, error)
                _state.value = _state.value.copy(isBuffering = false, isPlaying = false, errorMessage = error)
                return@launch
            }

            currentTiming = timing
            isCompleting = false

            val totalWords = timing.totalWords
            isLastSegmentInAyah = (toWordIndex >= totalWords)

            val rangeTiming = timing.getRangeTiming(fromWordIndex, toWordIndex)
            if (rangeTiming == null) {
                val error = "Word range $fromWordIndex..$toWordIndex not found in $surahNumber:$ayahNumber"
                Log.w(TAG, error)
                _state.value = _state.value.copy(isBuffering = false, isPlaying = false, errorMessage = error)
                return@launch
            }

            targetStartMs = rangeTiming.first

            val endSeg = timing.getSegment(toWordIndex)
            val nextSeg = timing.getSegment(toWordIndex + 1)

            targetEndMs = if (isLastSegmentInAyah || endSeg == null) {
                timing.endTimeMs
            } else if (nextSeg != null) {
                // For intermediate split segments, stop cleanly right before next word starts
                (nextSeg.startMs - 20L).coerceAtLeast(endSeg.endMs)
            } else {
                endSeg.endMs
            }

            _state.value = _state.value.copy(
                targetStartMs = targetStartMs,
                targetEndMs = targetEndMs,
                totalAyahDurationMs = timing.totalDurationMs,
                audioUrl = timing.audioUrl
            )

            // Apply acoustic lead-in buffer to avoid clipping word onset
            val seekStartMs = (targetStartMs - START_BUFFER_MS).coerceAtLeast(0L)
            prepareAndPlay(timing.audioUrl, seekStartMs)
        }
    }

    /**
     * Plays a single word in isolation.
     */
    fun playSingleWord(
        surahNumber: Int,
        ayahNumber: Int,
        wordIndex: Int,
        reciterId: String = "alafasy",
        loopCount: Int = 1,
        onComplete: () -> Unit = {}
    ) {
        playWordRange(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            fromWordIndex = wordIndex,
            toWordIndex = wordIndex,
            reciterId = reciterId,
            loopCount = loopCount,
            onComplete = onComplete
        )
    }

    /**
     * Plays the entire verse with word-level tracking.
     */
    fun playVerse(
        surahNumber: Int,
        ayahNumber: Int,
        reciterId: String = "alafasy",
        loopCount: Int = 1,
        delayBetweenLoops: Long = 0L,
        onComplete: () -> Unit = {}
    ) {
        playWordRange(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            fromWordIndex = 1,
            toWordIndex = Int.MAX_VALUE,
            reciterId = reciterId,
            loopCount = loopCount,
            delayBetweenLoops = delayBetweenLoops,
            onComplete = onComplete
        )
    }

    /**
     * Pauses the active word playback.
     */
    fun pause() {
        try {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    mp.pause()
                    progressJob?.cancel()
                    _state.value = _state.value.copy(isPlaying = false, isPaused = true)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing Hifz player: ${e.message}", e)
        }
    }

    /**
     * Resumes playback if currently paused.
     */
    fun resume() {
        try {
            mediaPlayer?.let { mp ->
                if (_state.value.isPaused) {
                    mp.start()
                    _state.value = _state.value.copy(isPlaying = true, isPaused = false)
                    startPositionTracker()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming Hifz player: ${e.message}", e)
        }
    }

    /**
     * Stops playback and resets active progress.
     */
    fun stop() {
        stopInternal(clearState = true)
    }

    /**
     * Seeks playback to a specific word index within the currently loaded Ayah.
     */
    fun seekToWord(wordIndex: Int) {
        val timing = currentTiming ?: return
        val segment = timing.getSegment(wordIndex) ?: return
        try {
            mediaPlayer?.seekTo(segment.startMs.toInt())
            _state.value = _state.value.copy(
                currentPositionMs = segment.startMs,
                currentWordIndex = wordIndex
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking to word $wordIndex: ${e.message}", e)
        }
    }

    /**
     * Releases player resources when done.
     */
    fun release() {
        stopInternal(clearState = true)
        scope.cancel()
    }

    private fun stopInternal(clearState: Boolean) {
        progressJob?.cancel()
        progressJob = null
        try {
            mediaPlayer?.apply {
                try {
                    if (isPlaying) {
                        pause()
                    }
                } catch (e: Exception) {
                    // Ignore transient state errors
                }
                reset()
                release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing MediaPlayer: ${e.message}", e)
        }
        mediaPlayer = null

        if (clearState) {
            _state.value = HifzAudioState()
            onPlaybackCompleteCallback = null
        }
    }

    private fun prepareAndPlay(audioUrl: String, startPositionMs: Long) {
        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    _state.value = _state.value.copy(
                        isBuffering = false,
                        isPlaying = true,
                        isPaused = false
                    )

                    val mpDur = mp.duration.toLong()
                    if (isLastSegmentInAyah && mpDur > 0) {
                        targetEndMs = maxOf(targetEndMs, mpDur)
                        _state.value = _state.value.copy(
                            targetEndMs = targetEndMs,
                            totalAyahDurationMs = mpDur
                        )
                    }

                    // Seek to the exact starting millisecond of the word range
                    if (startPositionMs > 0) {
                        mp.seekTo(startPositionMs.toInt())
                    }
                    mp.start()
                    startPositionTracker()
                }
                setOnCompletionListener {
                    handleWordRangeCompleted()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what extra=$extra")
                    _state.value = _state.value.copy(
                        isBuffering = false,
                        isPlaying = false,
                        errorMessage = "Audio playback error ($what, $extra)"
                    )
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start Hifz audio streaming: ${e.message}", e)
            _state.value = _state.value.copy(
                isBuffering = false,
                isPlaying = false,
                errorMessage = "Playback failed: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Continuous 15ms resolution coroutine loop that checks the current playback position.
     * Stops instantly when [targetEndMs] is reached and highlights the active word.
     */
    private fun startPositionTracker() {
        progressJob?.cancel()
        progressJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                val mp = mediaPlayer
                if (mp != null) {
                    try {
                        if (mp.isPlaying) {
                            val currentPos = mp.currentPosition.toLong()
                            val timing = currentTiming
                            val activeWord = timing?.getActiveWordIndex(currentPos)

                            withContext(Dispatchers.Main) {
                                _state.value = _state.value.copy(
                                    currentPositionMs = currentPos,
                                    currentWordIndex = activeWord
                                )
                            }

                            if (!isLastSegmentInAyah) {
                                val effectiveStopMs = targetEndMs + STOP_BUFFER_MS
                                if (currentPos >= effectiveStopMs) {
                                    withContext(Dispatchers.Main) {
                                        handleWordRangeCompleted()
                                    }
                                    break
                                }
                            } else {
                                val mpDur = try { mp.duration.toLong() } catch (e: Exception) { 0L }
                                val effectiveDur = maxOf(targetEndMs, mpDur)
                                if (effectiveDur > 0 && currentPos >= effectiveDur - 30L) {
                                    withContext(Dispatchers.Main) {
                                        handleWordRangeCompleted()
                                    }
                                    break
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Ignore transient player state errors during rapid seeking/teardown
                        break
                    }
                }
                delay(TRACKER_INTERVAL_MS)
            }
        }
    }

    /**
     * Handles reaching the end of the word range: repeats if more loops remain,
     * otherwise pauses/stops cleanly at the boundary and fires [onPlaybackCompleteCallback].
     */
    private fun handleWordRangeCompleted() {
        if (isCompleting) return
        isCompleting = true

        progressJob?.cancel()
        progressJob = null

        if (currentLoopCount < totalLoopCount) {
            currentLoopCount++
            _state.value = _state.value.copy(currentLoop = currentLoopCount)

            scope.launch {
                try {
                    mediaPlayer?.let { mp ->
                        if (mp.isPlaying) {
                            mp.pause()
                        }
                    }
                    if (delayBetweenLoopsMs > 0L) {
                        delay(delayBetweenLoopsMs)
                    }
                    val seekStartMs = (targetStartMs - START_BUFFER_MS).coerceAtLeast(0L)
                    mediaPlayer?.let { mp ->
                        mp.seekTo(seekStartMs.toInt())
                        mp.start()
                    }
                    isCompleting = false
                    startPositionTracker()
                } catch (e: Exception) {
                    Log.e(TAG, "Loop repeat error: ${e.message}", e)
                    isCompleting = false
                }
            }
        } else {
            // Finished all loops
            try {
                mediaPlayer?.pause()
            } catch (e: Exception) {
                // ignore
            }
            _state.value = _state.value.copy(
                isPlaying = false,
                isPaused = false,
                currentPositionMs = targetEndMs,
                currentWordIndex = currentTiming?.getActiveWordIndex(targetEndMs)
            )
            val cb = onPlaybackCompleteCallback
            onPlaybackCompleteCallback = null
            cb?.invoke()
        }
    }
}
