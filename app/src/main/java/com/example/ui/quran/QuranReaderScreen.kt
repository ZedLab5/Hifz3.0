package com.example.ui.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.graphics.lerp
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.testTag
import com.example.data.local.QuranNoteEntity
import com.example.data.quran.QuranData
import com.example.ui.components.KeepScreenOn
import com.example.ui.components.DndReadingEffect
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.QuranArabicFont
import com.example.data.model.Surah
import com.example.data.model.Verse
import com.example.data.quran.KhatmaEngine
import com.example.ui.MainViewModel
import com.example.data.localization.tr
import com.example.ui.NoorDestination
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealLight
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.GoldTintBgLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.DangerRedBgLight
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.ReadingThemes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AutoScrollSpeed(
    val label: String,
    val dpPerSecond: Float
) {
    SUPER_SLOW("Super Slow", 14f),
    SLOW("Slow", 26f),
    MEDIUM("Medium", 52f),
    FAST("Fast", 96f)
}

typealias QuranReadingThemeColors = ReadingThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentSurah by viewModel.selectedSurahForReading.collectAsStateWithLifecycle()
    val fontSizeSp by viewModel.arabicFontSizeSp.collectAsStateWithLifecycle()
    val showTransliteration by viewModel.showTransliteration.collectAsStateWithLifecycle()
    KeepScreenOn()
    DndReadingEffect(viewModel)

    val showTranslation by viewModel.showTranslation.collectAsStateWithLifecycle()
    val sharedThemeName by viewModel.sharedReadingTheme.collectAsStateWithLifecycle()
    val isSepiaMode by viewModel.isQuranSepiaMode.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val currentPlayingVerse by viewModel.currentPlayingVerse.collectAsStateWithLifecycle()
    val currentPlayingSurah by viewModel.currentPlayingSurah.collectAsStateWithLifecycle()
    val isAyahAudioMode by viewModel.isAyahAudioMode.collectAsStateWithLifecycle()
    val isMp3PlayerRunning = isAudioPlaying && !isAyahAudioMode
    val isCurrentSurahPlaying = isAudioPlaying && currentPlayingSurah.number == currentSurah.number
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val readingProgress by viewModel.readingProgress.collectAsStateWithLifecycle()
    val targetAyahToScrollTo by viewModel.targetAyahToScrollTo.collectAsStateWithLifecycle()
    val quranScrollRequest by viewModel.quranScrollRequest.collectAsStateWithLifecycle()
    val khatmaState by viewModel.khatmaDashboardState.collectAsStateWithLifecycle()
    val isMushafFlowMode by viewModel.isMushafFlowMode.collectAsStateWithLifecycle()
    val isTajweedEnabled by viewModel.isTajweedEnabled.collectAsStateWithLifecycle()
    val tajweedButtonPosition by viewModel.tajweedButtonPosition.collectAsStateWithLifecycle()
    val selectedArabicFont by viewModel.selectedArabicFont.collectAsStateWithLifecycle()
    val isFullscreenMode by viewModel.isQuranReaderFullscreen.collectAsStateWithLifecycle()
    val quranNotes by viewModel.quranNotes.collectAsStateWithLifecycle()
    var noteTargetVerse by remember { mutableStateOf<Verse?>(null) }
    var showNotesSheet by remember { mutableStateOf(false) }
    var cardPulseHighlightAyah by remember { mutableIntStateOf(0) }
    val cardHighlightGreenAnim = remember { Animatable(0f) }

    val context = LocalContext.current
    val density = LocalDensity.current
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showSearchSheet by remember { mutableStateOf(false) }
    var showTajweedQuickReminder by remember { mutableStateOf(false) }

    val timerActive by viewModel.quranTimerActive.collectAsStateWithLifecycle()
    val timerRemaining by viewModel.quranTimerRemaining.collectAsStateWithLifecycle()
    val timerTarget by viewModel.quranTimerTarget.collectAsStateWithLifecycle()
    val timerPaused by viewModel.quranTimerPaused.collectAsStateWithLifecycle()
    val showCelebrationMinutes by viewModel.showQuranTimerCelebration.collectAsStateWithLifecycle()

    var showTimerSetupSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()
    val mushafFlowLazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-Scroll States (Works in both normal cards and Mushaf Flow mode)
    var isAutoScrolling by remember { mutableStateOf(false) }
    var isAutoScrollPaused by remember { mutableStateOf(false) }
    var autoScrollSpeed by remember { mutableStateOf(AutoScrollSpeed.MEDIUM) }

    // Automatically clean up on leaving the Quran reader screen
    DisposableEffect(Unit) {
        onDispose {
            isAutoScrolling = false
            isAutoScrollPaused = false
            viewModel.setQuranReaderFullscreen(false)
            if (viewModel.isAyahAudioMode.value) {
                viewModel.stopAudio()
            }
        }
    }

    // Reset auto-scroll on surah change
    LaunchedEffect(currentSurah.number) {
        isAutoScrolling = false
        isAutoScrollPaused = false
    }

    // Pause auto-scroll cleanly when switching reading modes (simplest and safest per requirement)
    LaunchedEffect(isMushafFlowMode) {
        if (isAutoScrolling) {
            isAutoScrollPaused = true
        }
    }

    // Continuous downward auto-scroll engine (branches across LazyListState & ScrollState)
    val currentSpeedState = rememberUpdatedState(autoScrollSpeed)
    LaunchedEffect(isAutoScrolling, isAutoScrollPaused, isMushafFlowMode) {
        if (!isAutoScrolling || isAutoScrollPaused) return@LaunchedEffect

        val activeScrollable: ScrollableState = if (isMushafFlowMode) mushafFlowLazyListState else listState

        var lastTimeNanos = 0L
        while (isActive && isAutoScrolling && !isAutoScrollPaused) {
            withFrameNanos { frameTimeNanos ->
                if (lastTimeNanos > 0L) {
                    val dt = (frameTimeNanos - lastTimeNanos) / 1_000_000_000f
                    val speed = currentSpeedState.value
                    val pxPerSec = with(density) { speed.dpPerSecond.dp.toPx() }
                    val delta = pxPerSec * dt
                    if (delta > 0f) {
                        activeScrollable.dispatchRawDelta(delta)
                    }
                }
                lastTimeNanos = frameTimeNanos
            }
        }
    }

    val scrollThresholdPx = with(density) { 15.dp.toPx() }

    // Automatic scroll-direction chrome visibility for LazyColumn (Standard Card View)
    LaunchedEffect(isMushafFlowMode, scrollThresholdPx) {
        if (isMushafFlowMode) return@LaunchedEffect
        var previousIndex = listState.firstVisibleItemIndex
        var previousOffset = listState.firstVisibleItemScrollOffset
        var downAccumulator = 0f
        var upAccumulator = 0f

        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (currentIndex, currentOffset) ->
                if (currentIndex == 0 && currentOffset == 0) {
                    // At the very top of the Surah: always reveal chrome
                    downAccumulator = 0f
                    upAccumulator = 0f
                    if (isFullscreenMode) {
                        viewModel.setQuranReaderFullscreen(false)
                    }
                    previousIndex = currentIndex
                    previousOffset = currentOffset
                    return@collect
                }

                val delta = if (currentIndex != previousIndex) {
                    if (currentIndex > previousIndex) scrollThresholdPx + 1f else -(scrollThresholdPx + 1f)
                } else {
                    (currentOffset - previousOffset).toFloat()
                }
                previousIndex = currentIndex
                previousOffset = currentOffset

                if (delta > 0.5f) {
                    // Scrolling down
                    upAccumulator = 0f
                    downAccumulator += delta
                    if (downAccumulator >= scrollThresholdPx) {
                        if (!isFullscreenMode) {
                            viewModel.setQuranReaderFullscreen(true)
                        }
                        downAccumulator = 0f
                    }
                } else if (delta < -0.5f) {
                    // Scrolling up
                    downAccumulator = 0f
                    upAccumulator += -delta
                    if (upAccumulator >= scrollThresholdPx) {
                        if (isFullscreenMode) {
                            viewModel.setQuranReaderFullscreen(false)
                        }
                        upAccumulator = 0f
                    }
                }
            }
    }

    // Automatic scroll-direction chrome visibility for Mushaf Flow Mode (Continuous Scroll View)
    LaunchedEffect(isMushafFlowMode, scrollThresholdPx) {
        if (!isMushafFlowMode) return@LaunchedEffect
        var previousIndex = mushafFlowLazyListState.firstVisibleItemIndex
        var previousOffset = mushafFlowLazyListState.firstVisibleItemScrollOffset
        var downAccumulator = 0f
        var upAccumulator = 0f

        snapshotFlow { mushafFlowLazyListState.firstVisibleItemIndex to mushafFlowLazyListState.firstVisibleItemScrollOffset }
            .collect { (currentIndex, currentOffset) ->
                if (currentIndex == 0 && currentOffset == 0) {
                    // At the very top of the Mushaf: always reveal chrome
                    downAccumulator = 0f
                    upAccumulator = 0f
                    if (isFullscreenMode) {
                        viewModel.setQuranReaderFullscreen(false)
                    }
                    previousIndex = currentIndex
                    previousOffset = currentOffset
                    return@collect
                }

                val delta = if (currentIndex != previousIndex) {
                    if (currentIndex > previousIndex) scrollThresholdPx + 1f else -(scrollThresholdPx + 1f)
                } else {
                    (currentOffset - previousOffset).toFloat()
                }
                previousIndex = currentIndex
                previousOffset = currentOffset

                if (delta > 0.5f) {
                    // Scrolling down
                    upAccumulator = 0f
                    downAccumulator += delta
                    if (downAccumulator >= scrollThresholdPx) {
                        if (!isFullscreenMode) {
                            viewModel.setQuranReaderFullscreen(true)
                        }
                        downAccumulator = 0f
                    }
                } else if (delta < -0.5f) {
                    // Scrolling up
                    downAccumulator = 0f
                    upAccumulator += -delta
                    if (upAccumulator >= scrollThresholdPx) {
                        if (isFullscreenMode) {
                            viewModel.setQuranReaderFullscreen(false)
                        }
                        upAccumulator = 0f
                    }
                }
            }
    }

    // Auto-scroll to target bookmarked Ayah or note, or reset position to top
    LaunchedEffect(quranScrollRequest?.id, currentSurah.number) {
        val req = quranScrollRequest
        val bookmark = readingProgress
        val effectiveTarget = if (req != null && req.surahNumber == currentSurah.number) {
            req.targetAyah
        } else if (bookmark != null && bookmark.surahNumber == currentSurah.number) {
            bookmark.ayahNumber
        } else {
            0
        }

        try {
            if (effectiveTarget >= 1) {
                var offset = 1 // SurahHeaderBanner
                if (isMp3PlayerRunning) offset++
                if (currentSurah.number != 9) offset++
                val targetIndex = (effectiveTarget - 1 + offset).coerceIn(0, currentSurah.verses.size + offset)
                listState.scrollToItem(targetIndex)

                cardPulseHighlightAyah = effectiveTarget
                val pulseEasing = FastOutSlowInEasing
                repeat(3) {
                    cardHighlightGreenAnim.animateTo(1f, tween(300, easing = pulseEasing))
                    cardHighlightGreenAnim.animateTo(0f, tween(300, easing = pulseEasing))
                }
            } else {
                listState.scrollToItem(0)
            }
        } finally {
            cardPulseHighlightAyah = 0
            cardHighlightGreenAnim.snapTo(0f)
        }
    }

    // Smart Auto-Scrolling Audio Player: dynamically follow active Ayah only during Ayah-by-Ayah recitation mode
    LaunchedEffect(currentPlayingVerse, isAudioPlaying, isAyahAudioMode, currentPlayingSurah.number, currentSurah.number) {
        if (isAudioPlaying && isAyahAudioMode && currentPlayingSurah.number == currentSurah.number) {
            var offset = 1 // SurahHeaderBanner
            if (isMp3PlayerRunning) offset++
            if (currentSurah.number != 9) offset++

            if (currentPlayingVerse == 0 && currentSurah.number != 9) {
                val bismillahIndex = if (isMp3PlayerRunning) 2 else 1
                listState.animateScrollToItem(bismillahIndex)
            } else if (currentPlayingVerse >= 1) {
                val targetIndex = (currentPlayingVerse - 1 + offset).coerceIn(0, currentSurah.verses.size + offset)
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    val colorScheme = MaterialTheme.colorScheme
    val isSystemDark = sharedThemeName == "Obsidian Night"

    // Determine current theme colors for Quran Reader canvas: Sepia if toggled (overriding dark/light), Obsidian if dark mode, otherwise app-wide Light theme
    val themeColors = remember(isSepiaMode, colorScheme, sharedThemeName) {
        if (isSepiaMode) {
            ReadingThemes.SepiaParchment
        } else if (sharedThemeName == "Obsidian Night") {
            ReadingThemes.ObsidianNight
        } else {
            ReadingThemes.fromColorScheme(colorScheme, isDark = false)
        }
    }

    // Modal bottom sheet / settings theme colors: follows app-wide Dark/Light theme, NOT Sepia
    val sheetThemeColors = remember(colorScheme, sharedThemeName) {
        if (sharedThemeName == "Obsidian Night") {
            ReadingThemes.ObsidianNight
        } else {
            ReadingThemes.fromColorScheme(colorScheme, isDark = false)
        }
    }

    val chromeVisibility by animateFloatAsState(
        targetValue = if (isFullscreenMode) 0f else 1f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "chromeVisibility"
    )

    // Explicit exception for Auto-Scroll: keep bottom bar visible at all times while Auto-Scroll is active
    val bottomBarVisibility by animateFloatAsState(
        targetValue = if (isFullscreenMode && !isAutoScrolling) 0f else 1f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "bottomBarVisibility"
    )

    val favoriteVerseKeysSet = remember(favorites, currentSurah.number) {
        favorites.map { it.title }.toSet()
    }
    val quranNotesMap = remember(quranNotes, currentSurah.number) {
        quranNotes.filter { it.surahNumber == currentSurah.number }
            .associate { it.verseNumber to it.noteText }
    }

    val screenBackground = if (isMushafFlowMode && !themeColors.isDark && !isSepiaMode) Color.White else themeColors.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(screenBackground)
    ) {
        // 1. Reading Canvas (Mushaf Flow or Standard Card View)
        if (isMushafFlowMode) {
            // Distraction-Free Continuous Reading Mode (Pure Uthmani text)
            val currentNotesMap = remember(quranNotes, currentSurah.number) {
                quranNotes.filter { it.surahNumber == currentSurah.number }
                    .associate { it.verseNumber to it.noteText }
            }
            MushafFlowView(
                surah = currentSurah,
                fontSizeSp = fontSizeSp,
                arabicFont = selectedArabicFont,
                themeColors = themeColors,
                isPlaying = isCurrentSurahPlaying && isAyahAudioMode,
                currentPlayingVerse = currentPlayingVerse,
                isAudioDisabled = isMp3PlayerRunning,
                viewModel = viewModel,
                notesMap = currentNotesMap,
                onOpenNoteForVerse = { verse -> noteTargetVerse = verse },
                onToggleBookmarkForVerse = { verse -> viewModel.saveExactReadingBookmark(currentSurah, verse.verseNumber) },
                onPlayVerse = { verse -> viewModel.playAyah(currentSurah, verse.verseNumber) },
                modifier = Modifier.fillMaxSize(),
                lazyListState = mushafFlowLazyListState,
                isFullscreenMode = isFullscreenMode
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 104.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                    // Surah Header Banner with Previous / Next Navigation
                    item(key = "surah_header") {
                        SurahHeaderBanner(
                            surah = currentSurah,
                            themeColors = themeColors,
                            onPreviousSurah = { viewModel.openPreviousSurah() },
                            onNextSurah = { viewModel.openNextSurah() }
                        )
                    }

                    // Static audio notice directly beneath hero section when MP3 player is active
                    if (isMp3PlayerRunning) {
                        item(key = "mp3_audio_notice") {
                            Mp3PlaybackActiveNotice()
                        }
                    }

                    // Bismillah Header (for all except Surah 9 At-Tawbah)
                    if (currentSurah.number != 9) {
                        item(key = "bismillah_card") {
                            BismillahBannerCard(
                                themeColors = themeColors,
                                arabicFont = selectedArabicFont,
                                isActive = isCurrentSurahPlaying && isAyahAudioMode && currentPlayingVerse == 0
                            )
                        }
                    }

                    // Empty state fallback if verses are still loading or empty
                    if (currentSurah.verses.isEmpty()) {
                        item(key = "empty_verses_card") {
                            QuranEmptyVersesCard(
                                surah = currentSurah,
                                themeColors = themeColors,
                                viewModel = viewModel,
                                onRetry = { viewModel.reloadCurrentSurah() }
                            )
                        }
                    } else {
                        // Verses List
                        items(
                            items = currentSurah.verses,
                            key = { "${currentSurah.number}_${it.verseNumber}" }
                        ) { verse ->
                            val isVerseActive = isCurrentSurahPlaying && currentPlayingVerse == verse.verseNumber
                            val isFavorite = favoriteVerseKeysSet.contains("Surah ${currentSurah.nameEnglish} Ayah ${verse.verseNumber}")
                            val isExactBookmark = readingProgress?.surahNumber == currentSurah.number &&
                                    readingProgress?.ayahNumber == verse.verseNumber

                            val currentPlan = khatmaState?.plan
                            val isKhatmaActive = currentPlan != null && !currentPlan.isCompleted
                            val currentReadCount = currentPlan?.readAyahsCount ?: 0
                            val verseAbsIndex = remember(currentSurah.number, verse.verseNumber) {
                                KhatmaEngine.getAbsoluteAyahIndex(currentSurah.number, verse.verseNumber)
                            }
                            val isKhatmaRead = isKhatmaActive && verseAbsIndex <= currentReadCount
                            val isKhatmaCurrentPointer = isKhatmaActive && verseAbsIndex == currentReadCount

                            val verseNoteText = quranNotesMap[verse.verseNumber]

                            VerseCardItem(
                                verse = verse,
                                surah = currentSurah,
                                fontSizeSp = fontSizeSp,
                                arabicFont = selectedArabicFont,
                                showTransliteration = showTransliteration,
                                showTranslation = showTranslation,
                                isActive = isVerseActive && isAyahAudioMode,
                                isBookmarked = isFavorite,
                                isReadingBookmark = isExactBookmark,
                                highlightGreenFactor = if (cardPulseHighlightAyah == verse.verseNumber) cardHighlightGreenAnim.value else 0f,
                                isKhatmaActive = isKhatmaActive,
                                isKhatmaRead = isKhatmaRead,
                                isKhatmaCurrentPointer = isKhatmaCurrentPointer,
                                isAudioDisabled = isMp3PlayerRunning,
                                themeColors = themeColors,
                                isTajweedEnabled = isTajweedEnabled,
                                existingNoteText = verseNoteText,
                                onPlayVerse = {
                                    if (isMp3PlayerRunning) {
                                        viewModel.showToast("MP3 player is currently active. Pause it to recite individual verses.")
                                    } else if (isCurrentSurahPlaying && isAyahAudioMode && currentPlayingVerse == verse.verseNumber) {
                                        viewModel.toggleAudioPlayback(currentSurah)
                                    } else {
                                        viewModel.playAyah(currentSurah, verse.verseNumber)
                                    }
                                },
                                onToggleBookmark = {
                                    viewModel.saveExactReadingBookmark(currentSurah, verse.verseNumber)
                                },
                                onOpenNote = {
                                    noteTargetVerse = verse
                                },
                                onMarkKhatma = {
                                    viewModel.markKhatmaProgressToVerse(currentSurah, verse.verseNumber)
                                },
                                onCopyVerse = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText(
                                        "Ayah ${currentSurah.nameEnglish} ${verse.verseNumber}",
                                        "${verse.arabicText}\n\n${verse.transliteration}\n\n${verse.translation}\n[Qur'an ${currentSurah.number}:${verse.verseNumber}]"
                                    )
                                    clipboard.setPrimaryClip(clip)
                                    viewModel.showToast("Ayah copied to clipboard!")
                                }
                            )
                        }
                    }

                    // Next / Previous Surah Navigation Footer
                    item(key = "surah_nav_footer") {
                        SurahNavigationFooter(
                            currentSurah = currentSurah,
                            themeColors = themeColors,
                            onPrevious = { viewModel.openPreviousSurah() },
                            onNext = { viewModel.openNextSurah() },
                            onOpenList = { viewModel.navigateTo(NoorDestination.QURAN_SURAH_LIST) }
                        )
                    }
                }
            }

            // 2. Persistent Top Bar with GPU-accelerated translation & alpha
            NoorTopBar(
                title = "${currentSurah.number}. ${currentSurah.nameEnglish}",
                eyebrow = "${currentSurah.revelationType.uppercase()} • ${currentSurah.nameArabic}",
                subtitle = "${currentSurah.totalVerses} Ayahs • ${currentSurah.englishMeaning}",
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back",
                isDark = themeColors.isDark,
                themeColors = themeColors,
                actions = {
                    // Search Quran
                    NoorGlassIconButton(
                        onClick = { showSearchSheet = true },
                        icon = Icons.Default.Search,
                        contentDescription = "Search Quran",
                        modifier = Modifier.testTag("quran_top_bar_search_button")
                    )
                    // Reading Display Settings
                    NoorGlassIconButton(
                        onClick = { showSettingsSheet = true },
                        icon = Icons.Default.Settings,
                        contentDescription = "Reading Settings"
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        alpha = chromeVisibility
                        translationY = -(1f - chromeVisibility) * size.height
                    }
            )

            // 3. Persistent Quran Bottom Bar with GPU-accelerated translation & alpha
            QuranBottomBar(
                themeColors = themeColors,
                isAutoScrolling = isAutoScrolling,
                isAutoScrollPaused = isAutoScrollPaused,
                autoScrollSpeed = autoScrollSpeed,
                onStartAutoScroll = {
                    isAutoScrolling = true
                    isAutoScrollPaused = false
                },
                onToggleAutoScrollPlayPause = {
                    isAutoScrollPaused = !isAutoScrollPaused
                },
                onExitAutoScroll = {
                    isAutoScrolling = false
                    isAutoScrollPaused = false
                    viewModel.setQuranReaderFullscreen(false)
                },
                onSpeedDecrease = {
                    val speeds = AutoScrollSpeed.values()
                    val currentIndex = speeds.indexOf(autoScrollSpeed)
                    if (currentIndex > 0) {
                        autoScrollSpeed = speeds[currentIndex - 1]
                    }
                },
                onSpeedIncrease = {
                    val speeds = AutoScrollSpeed.values()
                    val currentIndex = speeds.indexOf(autoScrollSpeed)
                    if (currentIndex < speeds.size - 1) {
                        autoScrollSpeed = speeds[currentIndex + 1]
                    }
                },
                isAudioPlaying = isCurrentSurahPlaying && isAyahAudioMode,
                onToggleAudio = {
                    if (isMp3PlayerRunning) {
                        viewModel.showToast("MP3 player is active. Pause it using the floating bar to start recitation here.")
                    } else if (isCurrentSurahPlaying && isAyahAudioMode) {
                        viewModel.toggleAudioPlayback(currentSurah)
                    } else {
                        val startVerse = if (currentPlayingVerse > 0) currentPlayingVerse else 1
                        viewModel.playAyah(currentSurah, startVerse, openPlayer = false)
                    }
                },
                isMushafMode = isMushafFlowMode,
                onToggleMushafMode = { viewModel.toggleMushafFlowMode() },
                onOpenNotes = { showNotesSheet = true },
                notesCount = quranNotes.size,
                isTimerActive = timerActive,
                timerRemainingSeconds = timerRemaining,
                onOpenTimer = { showTimerSetupSheet = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer {
                        val vis = if (isAutoScrolling) 1f else bottomBarVisibility
                        alpha = vis
                        translationY = (1f - vis) * size.height
                    }
            )

            // 4. Persistent Floating Tajweed Rules Button (Corner-positioned, immune to scroll hides)
            TajweedFloatingButton(
                isVisible = isTajweedEnabled,
                position = tajweedButtonPosition,
                themeColors = themeColors,
                onClick = { showTajweedQuickReminder = true },
                modifier = Modifier.fillMaxSize()
            )

        // Reading Settings Modal Bottom Sheet
        if (showSettingsSheet) {
            val sheetColors = sheetThemeColors
            ModalBottomSheet(
                onDismissRequest = { showSettingsSheet = false },
                sheetState = sheetState,
                containerColor = sheetColors.surface
            ) {
                val themeColors = sheetColors
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reading Preferences",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText,
                                    fontSize = 19.sp
                                )
                            )

                            IconButton(onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    showSettingsSheet = false
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = themeColors.arabicText
                                )
                            }
                        }

                        // 1. Reading Display Mode (Part 1 - Two Option Picker)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Reading Display Mode",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Option A: Mixed Reading
                                val isMixedSelected = !isMushafFlowMode
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setReadingDisplayMode(false) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isMixedSelected) themeColors.accent.copy(alpha = 0.08f) else themeColors.background,
                                    border = null
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (themeColors.isDark) themeColors.surface else themeColors.background)
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = "الْحَمْدُ لِلَّهِ",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = selectedArabicFont.fontFamily,
                                                    color = themeColors.arabicText
                                                ),
                                                textAlign = TextAlign.End,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Text(
                                                text = "All praise is to Allah",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 8.5.sp,
                                                    color = themeColors.translationText
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                        Text(
                                            text = "Mixed Reading",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (isMixedSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isMixedSelected) themeColors.accent else themeColors.arabicText,
                                                fontSize = 13.sp
                                            )
                                        )
                                    }
                                }

                                // Option B: Arabic Only (Continuous Mushaf Flow)
                                val isArabicOnlySelected = isMushafFlowMode
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setReadingDisplayMode(true) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isArabicOnlySelected) themeColors.accent.copy(alpha = 0.08f) else themeColors.background,
                                    border = null
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (themeColors.isDark) themeColors.surface else themeColors.background)
                                                .padding(horizontal = 8.dp, vertical = 11.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ۝",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = selectedArabicFont.fontFamily,
                                                    color = themeColors.arabicText
                                                ),
                                                textAlign = TextAlign.Center,
                                                maxLines = 1
                                            )
                                        }
                                        Text(
                                            text = "Arabic Only",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (isArabicOnlySelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isArabicOnlySelected) themeColors.accent else themeColors.arabicText,
                                                fontSize = 13.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // 2. Arabic Calligraphy Style (Part 2 - 3 Horizontal Rows)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Arabic Calligraphy Style",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText
                                )
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                QuranArabicFont.values().forEach { fontOption ->
                                    val isSelected = selectedArabicFont == fontOption
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.setSelectedArabicFont(fontOption) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) themeColors.accent.copy(alpha = 0.08f) else themeColors.background,
                                        border = null
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = fontOption.displayName,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) themeColors.accent else themeColors.arabicText,
                                                    fontSize = 13.5.sp
                                                )
                                            )
                                            Text(
                                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontFamily = fontOption.fontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = if (isSelected) themeColors.accent else themeColors.arabicText
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Arabic Font Size Controls (Small, Medium, Large)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatSize,
                                        contentDescription = null,
                                        tint = themeColors.accent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Arabic Font Size",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.arabicText
                                        )
                                    )
                                }
                                Text(
                                    text = "${fontSizeSp}sp",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.accent
                                    )
                                )
                            }

                            // Predefined Selector Tabs (Small, Medium, Large)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(themeColors.background)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val fontOptions = listOf("Small" to 22, "Medium" to 28, "Large" to 34)
                                fontOptions.forEach { (label, spSize) ->
                                    val isSelected = when (label) {
                                        "Small" -> fontSizeSp <= 24
                                        "Medium" -> fontSizeSp in 25..30
                                        else -> fontSizeSp > 30
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) themeColors.accent else Color.Transparent,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.arabicFontSizeSp.value = spSize }
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else themeColors.arabicText,
                                                    fontSize = 13.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Reading Canvas Sepia Parchment Toggle (Exclusive to Quran Reader)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.toggleQuranSepiaMode() },
                            shape = RoundedCornerShape(14.dp),
                            color = themeColors.surface,
                            border = null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(GoldTintBgLight)
                                        )
                                        Text(
                                            text = "Sepia Parchment Theme",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = themeColors.arabicText,
                                                fontSize = 14.5.sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Warm vintage parchment canvas exclusively for Quran reading",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = themeColors.translationText,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        )
                                    )
                                }

                                Switch(
                                    checked = isSepiaMode,
                                    onCheckedChange = { viewModel.toggleQuranSepiaMode(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = if (themeColors.isDark) PrimaryTealDark else PrimaryTealLight,
                                        uncheckedThumbColor = themeColors.translationText,
                                        uncheckedTrackColor = themeColors.border
                                    )
                                )
                            }
                        }

                        // 4.5. Tajweed Color-Coding Switch & Legend Button
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.toggleTajweedMode() },
                            shape = RoundedCornerShape(14.dp),
                            color = themeColors.surface,
                            border = null
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(if (themeColors.isDark) Color(0xFF64FFDA) else Color(0xFF00897B))
                                            )
                                            Text(
                                                text = "Tajweed Color-Coding",
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = themeColors.arabicText,
                                                    fontSize = 14.5.sp
                                                )
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "Vibrant, high-contrast guides for rules of Madd, Ghunnah, Qalqalah, and more",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = themeColors.translationText,
                                                fontSize = 12.sp,
                                                lineHeight = 16.sp
                                            )
                                        )
                                    }

                                    Switch(
                                        checked = isTajweedEnabled,
                                        onCheckedChange = { viewModel.toggleTajweedMode(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = if (themeColors.isDark) PrimaryTealDark else PrimaryTealLight,
                                            uncheckedThumbColor = themeColors.translationText,
                                            uncheckedTrackColor = themeColors.border
                                        )
                                    )
                                }

                                if (isTajweedEnabled) {
                                    // Button to open Tajweed Guide & Audio Examples
                                    Button(
                                        onClick = {
                                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                                showSettingsSheet = false
                                                viewModel.navigateTo(NoorDestination.QURAN_TAJWEED_GUIDE)
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = themeColors.accent.copy(alpha = 0.12f),
                                            contentColor = themeColors.accent
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = null,
                                        border = null
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Palette,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "View Tajweed Rules & Audio Guide",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // 5. Translation & Transliteration Toggles (Only relevant in Mixed Reading mode)
                        if (!isMushafFlowMode) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "English Translation",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = themeColors.arabicText
                                            )
                                        )
                                        Text(
                                            text = "Clear Sahih International translation",
                                            style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                                        )
                                    }
                                    Switch(
                                        checked = showTranslation,
                                        onCheckedChange = { viewModel.setShowTranslation(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = themeColors.accent
                                        )
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Phonetic Transliteration",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = themeColors.arabicText
                                            )
                                        )
                                        Text(
                                            text = "Helps non-Arabic readers pronounce correctly",
                                            style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                                        )
                                    }
                                    Switch(
                                        checked = showTransliteration,
                                        onCheckedChange = { viewModel.setShowTransliteration(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = themeColors.accent
                                        )
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = themeColors.border.copy(alpha = 0.5f))

                        // Auto-Scroll Section with Preset Speed Controls
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Auto-Scroll",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = themeColors.arabicText
                                            )
                                        )
                                        Text(
                                            text = "Hands-free continuous downward reading at a steady pace",
                                            style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                                        )
                                    }
                                    Switch(
                                        checked = isAutoScrolling,
                                        onCheckedChange = { enabled ->
                                            isAutoScrolling = enabled
                                            isAutoScrollPaused = false
                                            if (enabled) {
                                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                                    showSettingsSheet = false
                                                }
                                            }
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = themeColors.accent
                                        )
                                    )
                                }

                                // Auto-Scroll Speed Preset Selector
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Scroll Speed",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = themeColors.arabicText
                                            )
                                        )
                                        Text(
                                            text = autoScrollSpeed.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = themeColors.accent
                                            )
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(themeColors.background)
                                            .padding(4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        AutoScrollSpeed.values().forEach { speed ->
                                            val isSelected = autoScrollSpeed == speed
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isSelected) themeColors.accent else Color.Transparent,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable { autoScrollSpeed = speed }
                                            ) {
                                                Box(
                                                    modifier = Modifier.padding(vertical = 8.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = speed.label,
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                            color = if (isSelected) Color.White else themeColors.arabicText,
                                                            fontSize = 11.sp
                                                        ),
                                                        maxLines = 1,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }



        // Quran All Notes Sheet (Dedicated screen listing notes across the whole Quran)
        if (showNotesSheet) {
            QuranAllNotesSheet(
                notes = quranNotes,
                themeColors = themeColors,
                onSelectVerse = { surahNumber, verseNumber ->
                    val targetSurah = QuranData.surahs.find { it.number == surahNumber }
                    if (targetSurah != null) {
                        viewModel.selectSurahForReading(targetSurah, verseNumber)
                    }
                    showNotesSheet = false
                },
                onDeleteNote = { surahNumber, verseNumber ->
                    viewModel.deleteQuranNote(surahNumber, verseNumber)
                },
                onDismiss = { showNotesSheet = false }
            )
        }

        // Quran Search Modal Bottom Sheet
        if (showSearchSheet) {
            QuranSearchSheet(
                viewModel = viewModel,
                themeColors = sheetThemeColors,
                onSelectVerse = { surahNumber, verseNumber ->
                    showSearchSheet = false
                    val targetSurah = QuranData.surahs.find { it.number == surahNumber } ?: currentSurah
                    viewModel.selectSurahForReading(targetSurah, verseNumber)
                },
                onDismiss = { showSearchSheet = false }
            )
        }

        // Tajweed Quick Reminder Menu (Small Full-Width Menu with Essential Rules & Link to Full Dedicated Guide Screen)
        if (showTajweedQuickReminder) {
            TajweedQuickReminderMenu(
                themeColors = sheetThemeColors,
                buttonPosition = tajweedButtonPosition,
                onPositionChange = { newPos -> viewModel.setTajweedButtonPosition(newPos) },
                onOpenDeepGuide = {
                    showTajweedQuickReminder = false
                    viewModel.navigateTo(NoorDestination.QURAN_TAJWEED_GUIDE)
                },
                onDismiss = { showTajweedQuickReminder = false }
            )
        }

        // Quran Note Entry Sheet
        if (noteTargetVerse != null) {
            val target = noteTargetVerse!!
            val existingNote = quranNotes.firstOrNull {
                it.surahNumber == currentSurah.number && it.verseNumber == target.verseNumber
            }?.noteText

            QuranNoteEntrySheet(
                surah = currentSurah,
                verse = target,
                existingNoteText = existingNote,
                themeColors = themeColors,
                onSaveNote = { text ->
                    viewModel.saveQuranNote(currentSurah.number, target.verseNumber, text)
                },
                onDeleteNote = {
                    viewModel.deleteQuranNote(currentSurah.number, target.verseNumber)
                },
                onDismiss = { noteTargetVerse = null }
            )
        }

        // Quran Reading Timer Setup & Active Bottom Sheet
        if (showTimerSetupSheet) {
            QuranTimerBottomSheet(
                onDismiss = { showTimerSetupSheet = false },
                isTimerActive = timerActive,
                isTimerPaused = timerPaused,
                timerRemainingSeconds = timerRemaining,
                timerTargetSeconds = timerTarget,
                onStart = { minutes ->
                    showTimerSetupSheet = false
                    viewModel.startQuranTimer(minutes)
                },
                onPause = { viewModel.pauseQuranTimer() },
                onResume = { viewModel.resumeQuranTimer() },
                onCancel = { viewModel.cancelQuranTimer() },
                themeColors = sheetThemeColors
            )
        }

        // Quran Reading Timer Celebration Dialog
        if (showCelebrationMinutes != null) {
            QuranTimerCelebrationDialog(
                minutes = showCelebrationMinutes!!,
                onKeepReading = { viewModel.dismissQuranTimerCelebration() },
                onDone = {
                    viewModel.dismissQuranTimerCelebration()
                    viewModel.navigateTo(NoorDestination.HOME)
                }
            )
        }
    }
}

@Composable
fun SurahHeaderBanner(
    surah: Surah,
    themeColors: QuranReadingThemeColors,
    onPreviousSurah: () -> Unit,
    onNextSurah: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceDark,
        border = null
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_pinterest_hero),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.40f,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(SurfaceDark.copy(alpha = 0.7f))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Navigation Bar: Left: Surah position | Right: Prev/Next buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Surah Position Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.12f),
                        border = null
                    ) {
                        Text(
                            text = "Surah ${surah.number} / 114",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }

                    // Navigation Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (surah.number > 1) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.10f),
                                border = null,
                                modifier = Modifier.clickable(onClick = onPreviousSurah)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Previous Surah",
                                        tint = MetallicGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Prev",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        if (surah.number < 114) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.10f),
                                border = null,
                                modifier = Modifier.clickable(onClick = onNextSurah)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Next",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Next Surah",
                                        tint = MetallicGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Left-to-Right Hero Details Row (Left: English Names & Metadata | Right: Arabic Calligraphy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Revelation Type & Verses Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MetallicGold.copy(alpha = 0.20f),
                                border = null
                            ) {
                                Text(
                                    text = surah.revelationType.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MetallicGold,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Text(
                                text = "${surah.totalVerses} Ayahs",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        // English Name
                        Text(
                            text = surah.nameEnglish,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 22.sp
                            )
                        )

                        // Meaning
                        Text(
                            text = "\"${surah.englishMeaning}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MetallicGold,
                                fontSize = 13.sp
                            )
                        )
                    }

                    // Arabic Calligraphy on Right
                    Text(
                        text = "سُورَةُ ${surah.nameArabic}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = QuranArabicFont.AMIRI.fontFamily,
                            color = Color.White,
                            fontSize = 25.sp
                        ),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
fun Mp3PlaybackActiveNotice(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = DangerRedBgLight,
        border = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DangerRedBgLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = DangerRedLight,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ayah Audio Playback Unavailable",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DangerRedLight,
                        fontSize = 13.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "The MP3 player is currently active. Pause it to enable Ayah recitation.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DangerRedLight,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
fun QuranEmptyVersesCard(
    surah: Surah,
    themeColors: QuranReadingThemeColors,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = themeColors.surface,
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(themeColors.accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = themeColors.accent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = String.format(tr("surah_format", viewModel), surah.nameEnglish, surah.nameArabic),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.arabicText
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = tr("verses_loading_message", viewModel),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = themeColors.translationText
                ),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = tr("load_verses_button", viewModel),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun BismillahBannerCard(
    themeColors: QuranReadingThemeColors,
    arabicFont: QuranArabicFont = QuranArabicFont.AMIRI,
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = themeColors.surface,
        shadowElevation = if (themeColors.isDark) 0.dp else 0.4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 24.sp,
                    fontFamily = arabicFont.fontFamily,
                    fontWeight = FontWeight.Normal,
                    color = if (isActive) themeColors.accent else themeColors.arabicText
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = "In the Name of Allah, the Most Gracious, the Most Merciful",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = themeColors.translationText.copy(alpha = 0.8f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun VerseCardItem(
    verse: Verse,
    surah: Surah,
    fontSizeSp: Int,
    arabicFont: QuranArabicFont = QuranArabicFont.AMIRI,
    showTransliteration: Boolean,
    showTranslation: Boolean,
    isActive: Boolean,
    isBookmarked: Boolean,
    isReadingBookmark: Boolean,
    highlightGreenFactor: Float = 0f,
    isKhatmaActive: Boolean = false,
    isKhatmaRead: Boolean = false,
    isKhatmaCurrentPointer: Boolean = false,
    isAudioDisabled: Boolean = false,
    themeColors: QuranReadingThemeColors,
    isTajweedEnabled: Boolean = true,
    existingNoteText: String? = null,
    onPlayVerse: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenNote: () -> Unit = {},
    onMarkKhatma: () -> Unit = {},
    onCopyVerse: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = themeColors.surface,
        shadowElevation = if (themeColors.isDark) 0.dp else 0.4.dp,
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Verse Header Bar (Left: Clean Verse Number Pill & Bookmark/Khatma labels | Right: Ghost Line Action Icons)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side: Verse Number + (Bookmark OR Khatma Bookmark swapped based on active Khatma)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isActive) themeColors.accent else themeColors.accent.copy(alpha = 0.10f),
                        border = null
                    ) {
                        Text(
                            text = "${surah.number}:${verse.verseNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Color.White else themeColors.accent,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Bookmark Pill (ALWAYS AVAILABLE regardless of Khatma state)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isReadingBookmark || isBookmarked) GoldBadgeBg else Color.Transparent,
                        border = null,
                        modifier = Modifier.clickable { onToggleBookmark() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = if (isReadingBookmark || isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isReadingBookmark || isBookmarked) MetallicGold else themeColors.translationText.copy(alpha = 0.7f),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isReadingBookmark || isBookmarked) "Bookmarked" else "Bookmark",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isReadingBookmark || isBookmarked) MetallicGold else themeColors.translationText.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }

                    // Khatma Pill (Appears alongside Bookmark pill when Khatma reading plan is active)
                    if (isKhatmaActive) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when {
                                isKhatmaCurrentPointer -> GoldTintBgLight
                                isKhatmaRead -> SoftTealTint
                                else -> Color.Transparent
                            },
                            border = null,
                            modifier = Modifier.clickable { onMarkKhatma() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = if (isKhatmaRead || isKhatmaCurrentPointer) Icons.Default.CheckCircle else Icons.Default.AutoStories,
                                    contentDescription = "Khatma Progress",
                                    tint = when {
                                        isKhatmaCurrentPointer -> SecondaryGoldLight
                                        isKhatmaRead -> DeepVibrantTeal
                                        else -> themeColors.translationText.copy(alpha = 0.7f)
                                    },
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = when {
                                        isKhatmaCurrentPointer -> "Current"
                                        isKhatmaRead -> "Khatma Done"
                                        else -> "Mark Khatma"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isKhatmaCurrentPointer -> SecondaryGoldLight
                                            isKhatmaRead -> DeepVibrantTeal
                                            else -> themeColors.translationText.copy(alpha = 0.8f)
                                        }
                                    )
                                )
                            }
                        }
                    }

                    // Note Pill (Added directly to the right of the Bookmark pill)
                    val hasNote = !existingNoteText.isNullOrBlank()
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (hasNote) themeColors.accent.copy(alpha = 0.15f) else Color.Transparent,
                        border = null,
                        modifier = Modifier.clickable { onOpenNote() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Note",
                                tint = if (hasNote) themeColors.accent else themeColors.translationText.copy(alpha = 0.7f),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Note",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasNote) themeColors.accent else themeColors.translationText.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }

                // Right Side: Action Icons (Audio, Copy) - Flag/Bookmark icon removed to avoid repetition
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Audio Recite Icon
                    IconButton(
                        onClick = onPlayVerse,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isActive) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Recite Ayah",
                            tint = if (isAudioDisabled) {
                                themeColors.translationText.copy(alpha = 0.28f)
                            } else if (isActive) {
                                MetallicGold
                            } else {
                                themeColors.translationText.copy(alpha = 0.65f)
                            },
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Copy Icon
                    IconButton(
                        onClick = onCopyVerse,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Ayah Text",
                            tint = themeColors.translationText.copy(alpha = 0.65f),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            // Pure Arabic Text Display (Centered/Right-aligned, beautifully rendered)
            val arabicTextColor = when {
                isActive -> themeColors.accent
                highlightGreenFactor > 0f -> lerp(themeColors.arabicText, Color(0xFF10B981), highlightGreenFactor)
                else -> themeColors.arabicText
            }
            val formattedTajweedText = remember(verse.arabicText, isTajweedEnabled, themeColors, arabicTextColor) {
                TajweedEngine.formatTajweedText(
                    arabicText = verse.arabicText,
                    isEnabled = isTajweedEnabled,
                    themeColors = themeColors,
                    baseColor = arabicTextColor
                )
            }
            val formattedTextWithMarker = remember(formattedTajweedText, verse.verseNumber, themeColors) {
                buildAnnotatedString {
                    append(formattedTajweedText)
                    append("  ")
                    val start = length
                    append(com.example.ui.quran.formatAyahMarker(verse.verseNumber))
                    addStyle(
                        SpanStyle(color = themeColors.accent, fontWeight = FontWeight.Bold),
                        start,
                        length
                    )
                }
            }
            Text(
                text = formattedTextWithMarker,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = fontSizeSp.sp,
                    lineHeight = (fontSizeSp * 1.75).sp,
                    fontFamily = arabicFont.fontFamily,
                    fontWeight = FontWeight.Normal,
                    textDirection = TextDirection.Rtl
                ),
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            // Transliteration (if enabled)
            if (showTransliteration && verse.transliteration.isNotBlank()) {
                Text(
                    text = verse.transliteration,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = themeColors.transliterationText,
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // English Translation (if enabled)
            if (showTranslation && verse.translation.isNotBlank()) {
                Text(
                    text = verse.translation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = themeColors.translationText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
            }

            // Short Tafsir / Spiritual Context (if available)
            if (verse.tafsirShort.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = themeColors.background,
                    border = null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Tafsir Note",
                            tint = themeColors.accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = verse.tafsirShort,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = themeColors.translationText.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            // Display Saved Note Preview Card
            if (!existingNoteText.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = themeColors.accent.copy(alpha = 0.08f),
                    border = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenNote() }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Saved Note",
                            tint = themeColors.accent,
                            modifier = Modifier
                                .size(16.dp)
                                .offset(y = 2.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Your Note",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.accent
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = existingNoteText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = themeColors.arabicText,
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SurahNavigationFooter(
    currentSurah: Surah,
    themeColors: QuranReadingThemeColors,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onOpenList: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = themeColors.surface,
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = currentSurah.number > 1,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = themeColors.accent
                    ),
                    border = BorderStroke(1.dp, themeColors.accent.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Surah",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Prev",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = onOpenList,
                    shape = RoundedCornerShape(50), // Fully rounded edge for the highlighted one
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColors.accent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "All Surahs",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "114 Surahs",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = onNext,
                    enabled = currentSurah.number < 114,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = themeColors.accent
                    ),
                    border = BorderStroke(1.dp, themeColors.accent.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Surah",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranNoteEntrySheet(
    surah: Surah,
    verse: Verse,
    existingNoteText: String?,
    themeColors: QuranReadingThemeColors,
    onSaveNote: (String) -> Unit,
    onDeleteNote: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var noteInput by remember(existingNoteText) { mutableStateOf(existingNoteText ?: "") }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = themeColors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = themeColors.accent.copy(alpha = 0.15f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Ayah Reflection & Notes",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText
                            )
                        )
                        Text(
                            text = "${surah.nameEnglish} • Verse ${verse.verseNumber}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = themeColors.translationText
                            )
                        )
                    }
                }

                IconButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = themeColors.translationText
                    )
                }
            }

            // Arabic Preview Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = themeColors.background,
                border = null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    val formattedTextWithMarker = remember(verse.arabicText, verse.verseNumber, themeColors) {
                        buildAnnotatedString {
                            val base = TajweedEngine.formatTajweedText(
                                arabicText = verse.arabicText,
                                isEnabled = false,
                                themeColors = themeColors,
                                baseColor = themeColors.arabicText
                            )
                            append(base)
                            append("  ")
                            val start = length
                            append(com.example.ui.quran.formatAyahMarker(verse.verseNumber))
                            addStyle(
                                SpanStyle(color = themeColors.accent, fontWeight = FontWeight.Bold),
                                start,
                                length
                            )
                        }
                    }
                    Text(
                        text = formattedTextWithMarker,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = QuranArabicFont.AMIRI.fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            lineHeight = 32.sp,
                            textDirection = TextDirection.Rtl
                        ),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (verse.translation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = verse.translation,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = themeColors.translationText,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            // Note Text Area
            OutlinedTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 220.dp),
                placeholder = {
                    Text(
                        text = "Write your reflections, study notes, or personal reminders for this verse...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = themeColors.translationText.copy(alpha = 0.5f)
                        )
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = themeColors.arabicText,
                    fontSize = 14.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedContainerColor = themeColors.background,
                    unfocusedContainerColor = themeColors.background
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Action Buttons (Delete, Save)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!existingNoteText.isNullOrBlank()) {
                    OutlinedButton(
                        onClick = {
                            onDeleteNote()
                            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = null
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Note",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Delete", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }

                Button(
                    onClick = {
                        if (noteInput.isNotBlank()) {
                            onSaveNote(noteInput)
                        } else if (!existingNoteText.isNullOrBlank()) {
                            onDeleteNote()
                        }
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColors.accent,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save Note",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (!existingNoteText.isNullOrBlank()) "Update Note" else "Save Note",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun QuranTimerBottomSheet(
    onDismiss: () -> Unit,
    isTimerActive: Boolean,
    isTimerPaused: Boolean,
    timerRemainingSeconds: Int,
    timerTargetSeconds: Int,
    onStart: (minutes: Int) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    themeColors: ReadingThemeColors
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = themeColors.surface,
        contentColor = themeColors.arabicText
    ) {
        if (isTimerActive) {
            ActiveTimerMenuContent(
                themeColors = themeColors,
                remainingSeconds = timerRemainingSeconds,
                targetSeconds = timerTargetSeconds,
                isPaused = isTimerPaused,
                onPause = onPause,
                onResume = onResume,
                onCancel = onCancel,
                onDismiss = onDismiss
            )
        } else {
            SetupTimerMenuContent(
                themeColors = themeColors,
                onStart = onStart
            )
        }
    }
}

@Composable
private fun ActiveTimerMenuContent(
    themeColors: ReadingThemeColors,
    remainingSeconds: Int,
    targetSeconds: Int,
    isPaused: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    val progress = if (targetSeconds > 0) remainingSeconds.toFloat() / targetSeconds else 0f
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Active Reading Session",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = themeColors.arabicText
            )
        )

        // Large Progress Ring with countdown
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            com.example.ui.components.RadialProgressRing(
                progress = progress,
                strokeWidth = 8.dp,
                trackColor = themeColors.translationText.copy(alpha = 0.15f),
                progressGradient = androidx.compose.ui.graphics.SolidColor(themeColors.accent),
                modifier = Modifier.size(130.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText,
                        fontSize = 32.sp
                    )
                )
                Text(
                    text = if (isPaused) "PAUSED" else "RUNNING",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isPaused) themeColors.translationText else themeColors.accent,
                        letterSpacing = 1.sp
                    )
                )
            }
        }

        // Explanation text
        Text(
            text = "Focus on reading the Holy Quran. Your progress is being tracked safely.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = themeColors.translationText,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Control Buttons (Pause/Resume and Cancel/Stop)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cancel / Stop Button
            androidx.compose.material3.OutlinedButton(
                onClick = {
                    onCancel()
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, themeColors.border.copy(alpha = 0.5f)),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = themeColors.arabicText
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Stop Session",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Pause / Resume Button
            androidx.compose.material3.Button(
                onClick = { if (isPaused) onPause() else onResume() },
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = themeColors.accent,
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isPaused) "Resume" else "Pause",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun SetupTimerMenuContent(
    themeColors: ReadingThemeColors,
    onStart: (minutes: Int) -> Unit
) {
    var selectedDuration by remember { mutableStateOf(10) } // 10 mins selected by default
    var isCustomSelected by remember { mutableStateOf(false) }
    var customMinutes by remember { mutableStateOf(25) }

    val durations = listOf(5, 10, 15, 20, 30)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reading Timer",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = themeColors.arabicText
            )
        )

        // Short explanation text explaining the timer with more padding
        Text(
            text = "Set a daily reading goal to build a consistent habit with the Holy Qur'an. A celebratory screen will mark your completion and record your daily streak activity.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = themeColors.translationText,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Duration Chips Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            durations.forEach { minutes ->
                val isSelected = !isCustomSelected && selectedDuration == minutes
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isSelected) themeColors.accent else themeColors.background,
                    border = BorderStroke(1.dp, if (isSelected) themeColors.accent else themeColors.border),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isCustomSelected = false
                            selectedDuration = minutes
                        }
                ) {
                    Text(
                        text = "$minutes min",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else themeColors.translationText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Custom Chip
            Surface(
                shape = RoundedCornerShape(50),
                color = if (isCustomSelected) themeColors.accent else themeColors.background,
                border = BorderStroke(1.dp, if (isCustomSelected) themeColors.accent else themeColors.border),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        isCustomSelected = true
                    }
            ) {
                Text(
                    text = "Custom",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isCustomSelected) Color.White else themeColors.translationText
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Custom minutes picker if "Custom" is selected
        if (isCustomSelected) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                // Minus Button
                Surface(
                    shape = CircleShape,
                    color = themeColors.background,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            customMinutes = (customMinutes - 1).coerceAtLeast(1)
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = themeColors.arabicText
                        )
                    }
                }

                Text(
                    text = "$customMinutes minutes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText
                    )
                )

                // Plus Button
                Surface(
                    shape = CircleShape,
                    color = themeColors.background,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            customMinutes = (customMinutes + 1).coerceAtMost(180)
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = themeColors.arabicText
                        )
                    }
                }
            }
        }

        // Start Button
        androidx.compose.material3.Button(
            onClick = {
                val duration = if (isCustomSelected) customMinutes else selectedDuration
                onStart(duration)
            },
            shape = RoundedCornerShape(12.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = themeColors.accent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Start Timer",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun QuranTimerCelebrationDialog(
    minutes: Int,
    onKeepReading: () -> Unit,
    onDone: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onKeepReading
    ) {
        var animatedScale by remember { mutableStateOf(0f) }
        val scale by animateFloatAsState(
            targetValue = animatedScale,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "scale"
        )
        LaunchedEffect(Unit) {
            animatedScale = 1f
        }

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            tonalElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Confetti Burst
                ConfettiBurst(modifier = Modifier.size(300.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Springs-in Success Icon
                    Box(
                        modifier = Modifier
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .size(72.dp)
                            .background(Color(0xFFE6F6F1), CircleShape), // Teal Tint
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Success",
                            tint = Color(0xFF1BA486), // Primary Teal
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        text = "Ma'sha'Allah!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F) // Soft black primary
                        )
                    )

                    Text(
                        text = "You spent $minutes minutes with the Qur'an today",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF5F5E5A), // Soft dark gray secondary
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // "Keep Reading"
                        androidx.compose.material3.OutlinedButton(
                            onClick = onKeepReading,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF2A4365) // Soft Navy
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Keep Reading",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        // "Done"
                        androidx.compose.material3.Button(
                            onClick = onDone,
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1BA486), // Primary Teal
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Done",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfettiBurst(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "confetti")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    val particles = remember {
        List(40) {
            val angle = Math.random() * 2 * Math.PI
            val distance = (30f + Math.random() * 150f).toFloat()
            val size = (4f + Math.random() * 8f).toFloat()
            val color = when ((0..2).random()) {
                0 -> Color(0xFF1BA486) // Primary Teal
                1 -> Color(0xFF2A4365) // Soft Navy
                else -> Color(0xFFC68A00) // Gold
            }
            Triple(angle, distance, size to color)
        }
    }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        particles.forEach { (angle, distance, style) ->
            val currentDistance = distance * progress
            val x = center.x + (currentDistance * Math.cos(angle)).toFloat()
            val y = center.y + (currentDistance * Math.sin(angle)).toFloat()
            val (pSize, color) = style
            val alpha = 1f - progress
            drawCircle(
                color = color,
                radius = pSize,
                center = Offset(x, y),
                alpha = alpha
            )
        }
    }
}
