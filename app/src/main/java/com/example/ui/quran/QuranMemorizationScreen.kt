package com.example.ui.quran

import com.example.ui.components.KeepScreenOn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.example.data.model.QuranArabicFont
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.model.Verse
import com.example.data.quran.QuranData
import com.example.data.repository.NoorRepository
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.components.universalCardShadow
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealLight
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.ReadingThemes

// =====================================================================
// Which of the three views is active. Single source of truth for
// navigation — replaces the old toggle-pill + hidden-range-chip-tap
// pattern that gave the screen two inconsistent ways to switch views.
// =====================================================================
private enum class HifzView { PRACTICE, RECALL, RANGE }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuranMemorizationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    KeepScreenOn()
    com.example.ui.components.DndReadingEffect(viewModel)

    val surah by viewModel.memorizationSurah.collectAsStateWithLifecycle()
    val startAyah by viewModel.memorizationStartAyah.collectAsStateWithLifecycle()
    val endAyah by viewModel.memorizationEndAyah.collectAsStateWithLifecycle()
    val silhouetteOpacity by viewModel.hifzSilhouetteOpacity.collectAsStateWithLifecycle()
    val repeatCount by viewModel.memorizationRepeatCount.collectAsStateWithLifecycle()
    val delaySeconds by viewModel.memorizationDelaySeconds.collectAsStateWithLifecycle()
    val loopRange by viewModel.memorizationLoopRange.collectAsStateWithLifecycle()
    val audioSyncReveal by viewModel.memorizationAudioSyncReveal.collectAsStateWithLifecycle()
    val showTranslation by viewModel.memorizationShowTranslation.collectAsStateWithLifecycle()
    val practiceMemorizedSet by viewModel.memorizedPracticeSet.collectAsStateWithLifecycle()
    val recallMemorizedSet by viewModel.memorizedRecallSet.collectAsStateWithLifecycle()
    val activeTab by viewModel.memorizationStudioTab.collectAsStateWithLifecycle()
    val delayActive by viewModel.memorizationDelayActive.collectAsStateWithLifecycle()
    val delayCountdown by viewModel.memorizationDelayCountdown.collectAsStateWithLifecycle()
    val revealedVerses by viewModel.revealedVersesInSession.collectAsStateWithLifecycle()
    val streakData by viewModel.unifiedStreakData.collectAsStateWithLifecycle()

    val drillMode by viewModel.memorizationDrillMode.collectAsStateWithLifecycle()
    val wordPattern by viewModel.memorizationWordPattern.collectAsStateWithLifecycle()
    val ayahPattern by viewModel.memorizationAyahPattern.collectAsStateWithLifecycle()
    val isCustomPatternEnabled by viewModel.isCustomPatternEnabled.collectAsStateWithLifecycle()
    val wordRepeatCount by viewModel.memorizationWordRepeatCount.collectAsStateWithLifecycle()
    val hifzAudioState by viewModel.hifzAudioState.collectAsStateWithLifecycle()
    val activeConfidencePrompt by viewModel.activeConfidencePrompt.collectAsStateWithLifecycle()
    val warmUpAvailableRange by viewModel.warmUpAvailableRange.collectAsStateWithLifecycle()
    val chunkReviewSize by viewModel.memorizationChunkReviewSize.collectAsStateWithLifecycle()
    val recallGroupSize by viewModel.recallGroupSize.collectAsStateWithLifecycle()

    val isPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val isBuffering by viewModel.isAudioBuffering.collectAsStateWithLifecycle()
    val currentPlayingVerse by viewModel.currentPlayingVerse.collectAsStateWithLifecycle()
    val currentPlayingSurah by viewModel.currentPlayingSurah.collectAsStateWithLifecycle()
    val selectedReciter by viewModel.selectedReciter.collectAsStateWithLifecycle()
    val arabicFontSize by viewModel.arabicFontSizeSp.collectAsStateWithLifecycle()
    val arabicFont by viewModel.selectedArabicFont.collectAsStateWithLifecycle()
    val isTajweedEnabled by viewModel.isTajweedEnabled.collectAsStateWithLifecycle()
    val tajweedButtonPosition by viewModel.tajweedButtonPosition.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

    val themeColors = remember(isDarkMode) {
        if (isDarkMode) ReadingThemes.ObsidianNight else ReadingThemes.MadaniCrisp
    }

    // Map the legacy Int tab index onto the new single-source-of-truth enum,
    // so ViewModel wiring (setMemorizationStudioTab) doesn't need to change.
    val activeView = when (activeTab) {
        1 -> HifzView.RANGE
        2 -> HifzView.RECALL
        else -> HifzView.PRACTICE
    }
    fun setView(view: HifzView) {
        if (view == HifzView.RECALL) {
            viewModel.pauseMemorizationDrill()
            viewModel.resetRevealedVersesInSession()
        }
        viewModel.setMemorizationStudioTab(
            when (view) {
                HifzView.PRACTICE -> 0
                HifzView.RANGE -> 1
                HifzView.RECALL -> 2
            }
        )
    }

    var showSurahPicker by remember { mutableStateOf(false) }
    var showHifzSettingsSheet by remember { mutableStateOf(false) }
    var showGroupsSettingsSheet by remember { mutableStateOf(false) }
    var showPatternSettingsSheet by remember { mutableStateOf(false) }
    var showJumpSettingsSheet by remember { mutableStateOf(false) }
    var showRangeSettingsSheet by remember { mutableStateOf(false) }
    var showTajweedQuickReminder by remember { mutableStateOf(false) }
    var hintTriggerToken by remember { mutableStateOf(0) }
    val headerChipsScrollState = rememberLazyListState()

    val targetVerses = remember(surah, startAyah, endAyah) {
        if (surah.verses.isNotEmpty()) {
            surah.verses.filter { it.verseNumber in startAyah..endAyah }
        } else {
            emptyList()
        }
    }
    val isSurahDataLoading = surah.verses.isEmpty()

    val activeMemorizedSet = if (activeView == HifzView.RECALL) recallMemorizedSet else practiceMemorizedSet
    val memorizedCountInSurah = remember(surah, activeMemorizedSet) {
        (1..surah.totalVerses).count { vNum ->
            activeMemorizedSet.contains("${surah.number}_$vNum")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopMemorizationDrill()
            viewModel.stopAndResetAudio()
        }
    }

    // Scroll state for continuous Mushaf flow
    val flowScrollState = rememberScrollState()

    LaunchedEffect(surah.number, surah.verses.size) {
        if (surah.verses.isEmpty()) {
            viewModel.ensureMemorizationVersesLoaded()
        }
    }

    LaunchedEffect(activeView) {
        if (activeView == HifzView.RECALL) {
            viewModel.resetRevealedVersesInSession()
        }
    }

    // ---- Single-banner priority: Confidence Prompt > Warm-Up > none ----
    val showConfidenceBanner = activeConfidencePrompt != null
    val showWarmUpBanner = !showConfidenceBanner &&
        warmUpAvailableRange != null && !isPlaying && !hifzAudioState.isPlaying

    Scaffold(
        topBar = {
            NoorTopBar(
                title = "Hifz Studio",
                eyebrow = "Surah ${surah.nameEnglish}",
                subtitle = "Ayahs $startAyah–$endAyah • ${surah.nameArabic}",
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back to Setup",
                isDark = themeColors.isDark,
                themeColors = themeColors,
                actions = {
                    NoorGlassIconButton(
                        onClick = { viewModel.navigateTo(NoorDestination.QURAN_MEMORIZATION_SETUP) },
                        icon = Icons.Default.Tune,
                        contentDescription = "Hifz Program Setup"
                    )
                    NoorGlassIconButton(
                        onClick = { showHifzSettingsSheet = true },
                        icon = Icons.Default.FormatSize,
                        contentDescription = "Font & Display Settings"
                    )
                }
            )
        },
        bottomBar = {
            HifzBottomBar(
                activeView = activeView,
                startAyah = startAyah,
                endAyah = endAyah,
                isPlaying = isPlaying || hifzAudioState.isPlaying,
                isBuffering = isBuffering || hifzAudioState.isBuffering,
                themeColors = themeColors,
                patternSubLabel = "$ayahPattern (${repeatCount}x)",
                onOpenJump = { showJumpSettingsSheet = true },
                onNextBatchClick = {
                    val batchSize = (endAyah - startAyah + 1).coerceAtLeast(1)
                    val nextStart = endAyah + 1
                    if (nextStart <= surah.totalVerses) {
                        val nextEnd = (nextStart + batchSize - 1).coerceAtMost(surah.totalVerses)
                        viewModel.setMemorizationRange(nextStart, nextEnd)
                    } else {
                        val nextEnd = batchSize.coerceAtMost(surah.totalVerses)
                        viewModel.setMemorizationRange(1, nextEnd)
                    }
                },
                onOpenPattern = { showPatternSettingsSheet = true },
                onHintClick = { hintTriggerToken++ },
                onOpenRange = { showRangeSettingsSheet = true },
                onOpenGroups = { showGroupsSettingsSheet = true },
                onPlayClick = {
                    val currentlyActive = isPlaying || isBuffering || hifzAudioState.isPlaying || hifzAudioState.isBuffering
                    if (currentlyActive) {
                        viewModel.pauseMemorizationDrill()
                    } else {
                        if (hifzAudioState.isPaused || viewModel.isHifzPaused.value) {
                            viewModel.resumeMemorizationDrill()
                        } else {
                            viewModel.playMemorizationDrill()
                        }
                    }
                }
            )
        },
        containerColor = themeColors.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                val activeMemorizedSet = if (activeView == HifzView.RECALL) recallMemorizedSet else practiceMemorizedSet
                // Mark Memorized Ayahs Bar
                MarkMemorizedAyahsBar(
                    startAyah = startAyah,
                    endAyah = endAyah,
                    surahNumber = surah.number,
                    memorizedSet = activeMemorizedSet,
                    themeColors = themeColors,
                    onToggleMemorized = { ayahNum ->
                        viewModel.toggleVerseMemorizedStatus(surah.number, ayahNum, isRecallMode = (activeView == HifzView.RECALL))
                    },
                    lazyListState = headerChipsScrollState
                )

            if (isSurahDataLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = themeColors.accent)
                }
            } else {
                if (activeView == HifzView.RECALL) {
                    RecallSelfTestContent(
                        targetVerses = targetVerses,
                        surah = surah,
                        startAyah = startAyah,
                        endAyah = endAyah,
                        memorizedSet = recallMemorizedSet,
                        silhouetteOpacity = silhouetteOpacity,
                        isPlaying = isPlaying || hifzAudioState.isPlaying,
                        currentPlayingVerse = currentPlayingVerse,
                        showTranslation = showTranslation,
                        isTajweedEnabled = isTajweedEnabled,
                        arabicFontSize = arabicFontSize,
                        arabicFont = arabicFont,
                        themeColors = themeColors,
                        flowScrollState = flowScrollState,
                        recallGroupSize = recallGroupSize,
                        onToggleBars = { },
                        onPlayVerse = { verse ->
                            viewModel.playVerseInMemorizationMode(surah, verse.verseNumber, isRecallMode = true)
                        },
                        onToggleMemorized = { verse ->
                            viewModel.toggleVerseMemorizedStatus(surah.number, verse.verseNumber, isRecallMode = true)
                        },
                        onMarkAllMemorized = { viewModel.markCurrentRangeMemorized(true, isRecallMode = true) },
                        onRecordRecallResult = { gotIt, struggled, missed, hints ->
                            val total = (endAyah - startAyah + 1).coerceAtLeast(1)
                            viewModel.recordHifzSession(
                                surahNumber = surah.number,
                                surahName = surah.nameEnglish,
                                surahNameArabic = surah.nameArabic,
                                startAyah = startAyah,
                                endAyah = endAyah,
                                mode = "Self-Recall",
                                totalAyahs = total,
                                ayahsMemorized = gotIt,
                                ayahsMissed = missed,
                                ayahsStruggled = struggled,
                                hintsUsed = hints,
                                notes = "Recall test completed: $gotIt Got it, $struggled Struggled, $missed Missed"
                            )
                        }
                    )
                } else {
                    UnifiedPracticeAndRecallContent(
                        isRecallMode = false,
                        targetVerses = targetVerses,
                        surah = surah,
                        startAyah = startAyah,
                        endAyah = endAyah,
                        revealedVerses = revealedVerses,
                        memorizedSet = practiceMemorizedSet,
                        silhouetteOpacity = silhouetteOpacity,
                        isPlaying = isPlaying || hifzAudioState.isPlaying,
                        currentPlayingVerse = currentPlayingVerse,
                        showTranslation = showTranslation,
                        isTajweedEnabled = isTajweedEnabled,
                        arabicFontSize = arabicFontSize,
                        arabicFont = arabicFont,
                        themeColors = themeColors,
                        flowScrollState = flowScrollState,
                        hintTriggerToken = hintTriggerToken,
                        onToggleBars = { },
                        onPlayVerse = { verse ->
                            viewModel.playVerseInMemorizationMode(surah, verse.verseNumber, isRecallMode = false)
                        },
                        onToggleMemorized = { verse ->
                            viewModel.toggleVerseMemorizedStatus(surah.number, verse.verseNumber, isRecallMode = false)
                        },
                        onVerseTap = { verse ->
                            viewModel.playVerseInMemorizationMode(surah, verse.verseNumber, isRecallMode = false)
                        },
                        onVerseReveal = { verseNumber ->
                            viewModel.revealVerse(verseNumber)
                        },
                        onResetRevealed = {
                            viewModel.resetRevealedVersesInSession()
                        },
                        onMarkAllMemorized = { viewModel.markCurrentRangeMemorized(true, isRecallMode = false) }
                    )
                }
            }
        }

        // Floating contextual banner overlay anchored above the fixed bottom bar
        AnimatedVisibility(
            visible = showConfidenceBanner || showWarmUpBanner,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
        ) {
            if (showConfidenceBanner) {
                activeConfidencePrompt?.let { prompt ->
                    ConfidenceBanner(
                        prompt = prompt,
                        themeColors = themeColors,
                        onDismiss = { viewModel.dismissConfidencePrompt() },
                        onRate = { rating -> viewModel.recordConfidenceRating(rating) }
                    )
                }
            } else if (showWarmUpBanner) {
                warmUpAvailableRange?.let { range ->
                    WarmUpBanner(
                        range = range,
                        themeColors = themeColors,
                        onSkip = { viewModel.dismissWarmUp() },
                        onWarmUp = { viewModel.playWarmUpSession() }
                    )
                }
            }
        }

        // Floating Tajweed Rules Button (shows when Tajweed is enabled, identical to Quran reader)
        TajweedFloatingButton(
            isVisible = isTajweedEnabled,
            position = tajweedButtonPosition,
            themeColors = themeColors,
            onClick = { showTajweedQuickReminder = true },
            modifier = Modifier.fillMaxSize()
        )
    }
}

    if (showTajweedQuickReminder) {
        TajweedQuickReminderMenu(
            themeColors = themeColors,
            buttonPosition = tajweedButtonPosition,
            onPositionChange = { newPos -> viewModel.setTajweedButtonPosition(newPos) },
            onOpenDeepGuide = {
                showTajweedQuickReminder = false
                viewModel.navigateTo(NoorDestination.QURAN_TAJWEED_GUIDE)
            },
            onDismiss = { showTajweedQuickReminder = false }
        )
    }

    if (showHifzSettingsSheet) {
        HifzSettingsSheet(
            silhouetteOpacity = silhouetteOpacity,
            repeatCount = repeatCount,
            delaySeconds = delaySeconds,
            loopRange = loopRange,
            audioSyncReveal = audioSyncReveal,
            showTranslation = showTranslation,
            isTajweedEnabled = isTajweedEnabled,
            arabicFontSize = arabicFontSize,
            selectedArabicFont = arabicFont,
            themeColors = themeColors,
            ayahPattern = ayahPattern,
            isCustomPatternEnabled = isCustomPatternEnabled,
            wordRepeatCount = wordRepeatCount,
            chunkReviewSize = chunkReviewSize,
            recallGroupSize = recallGroupSize,
            onToggleCustomPattern = { viewModel.setCustomPatternEnabled(it) },
            onSetAyahPattern = { viewModel.setAyahPattern(it) },
            onSelectWordRepeatCount = { viewModel.setWordRepeatCount(it) },
            onSelectChunkReviewSize = { viewModel.setChunkReviewSize(it) },
            onSelectRecallGroupSize = { viewModel.setRecallGroupSize(it) },
            onSetSilhouetteOpacity = { viewModel.setHifzSilhouetteOpacity(it) },
            onSelectRepeatCount = { viewModel.setMemorizationRepeatCount(it) },
            onSelectDelaySeconds = { viewModel.setMemorizationDelaySeconds(it) },
            onToggleLoopRange = { viewModel.toggleMemorizationLoopRange() },
            onToggleAudioSyncReveal = { viewModel.toggleMemorizationAudioSyncReveal() },
            onToggleShowTranslation = { viewModel.toggleMemorizationShowTranslation() },
            onToggleTajweed = { viewModel.toggleTajweedMode(it) },
            onOpenTajweedGuide = {
                showHifzSettingsSheet = false
                viewModel.navigateTo(NoorDestination.QURAN_TAJWEED_GUIDE)
            },
            onSetFontSize = { viewModel.setArabicFontSize(it) },
            onSetArabicFont = { viewModel.setSelectedArabicFont(it) },
            onDismiss = { showHifzSettingsSheet = false }
        )
    }

    if (showGroupsSettingsSheet) {
        GroupsSettingsSheet(
            recallGroupSize = recallGroupSize,
            themeColors = themeColors,
            onSelectRecallGroupSize = { viewModel.setRecallGroupSize(it) },
            onDismiss = { showGroupsSettingsSheet = false }
        )
    }

    if (showPatternSettingsSheet) {
        PatternSettingsSheet(
            ayahPattern = ayahPattern,
            repeatCount = repeatCount,
            isCustomPatternEnabled = isCustomPatternEnabled,
            themeColors = themeColors,
            onToggleCustomPattern = { viewModel.setCustomPatternEnabled(it) },
            onSetAyahPattern = { viewModel.setAyahPattern(it) },
            onSelectRepeatCount = { viewModel.setMemorizationRepeatCount(it) },
            onDismiss = { showPatternSettingsSheet = false }
        )
    }

    if (showSurahPicker) {
        SurahPickerSheet(
            currentSurah = surah,
            themeColors = themeColors,
            onSelectSurah = { selected ->
                viewModel.setMemorizationSurah(selected)
                showSurahPicker = false
            },
            onDismiss = { showSurahPicker = false }
        )
    }

    if (showJumpSettingsSheet) {
        JumpSettingsSheet(
            surah = surah,
            currentAyah = currentPlayingVerse,
            themeColors = themeColors,
            onSelectAyah = { selectedAyah ->
                val currentBatchSize = (endAyah - startAyah + 1).coerceIn(1, surah.totalVerses)
                val inRange = selectedAyah in startAyah..endAyah
                if (!inRange) {
                    val newStart = selectedAyah
                    val newEnd = (selectedAyah + currentBatchSize - 1).coerceAtMost(surah.totalVerses)
                    viewModel.setMemorizationRange(newStart, newEnd)
                }
                viewModel.stopMemorizationDrill()
                viewModel.currentPlayingVerse.value = selectedAyah
                showJumpSettingsSheet = false
            },
            onDismiss = { showJumpSettingsSheet = false }
        )
    }

    if (showRangeSettingsSheet) {
        RangeSettingsSheet(
            surah = surah,
            startAyah = startAyah,
            endAyah = endAyah,
            themeColors = themeColors,
            onSetRange = { s, e -> viewModel.setMemorizationRange(s, e) },
            onDismiss = { showRangeSettingsSheet = false }
        )
    }
}

// =====================================================================
// MARK MEMORIZED AYAHS BAR
// Short header text + edge-to-edge scrollable ayah pill chips
// =====================================================================

@Composable
private fun MarkMemorizedAyahsBar(
    startAyah: Int,
    endAyah: Int,
    surahNumber: Int,
    memorizedSet: Set<String>,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onToggleMemorized: (Int) -> Unit,
    lazyListState: LazyListState
) {
    val isDark = themeColors.isDark
    val neutralBg = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    val memorizedBg = if (isDark) Color(0xFF0F3E33) else Color(0xFFE6F6F1)
    val memorizedText = if (isDark) Color(0xFF34D399) else Color(0xFF0F766E)
    val textColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155)
    val mutedText = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Mark memorized ayahs",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = mutedText,
                fontSize = 11.5.sp,
                letterSpacing = 0.2.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Edge-to-edge scrollable ayah pills
        LazyRow(
            state = lazyListState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items((startAyah..endAyah).toList()) { ayahNum ->
                val isMemorized = memorizedSet.contains("${surahNumber}_$ayahNum")
                Surface(
                    shape = CircleShape,
                    color = if (isMemorized) memorizedBg else neutralBg,
                    modifier = Modifier.clickable { onToggleMemorized(ayahNum) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = if (isMemorized) Icons.Default.CheckCircle else Icons.Default.Check,
                            contentDescription = "Ayah $ayahNum memorized status",
                            tint = if (isMemorized) memorizedText else mutedText,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Ayah $ayahNum",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isMemorized) FontWeight.Bold else FontWeight.Normal,
                                color = if (isMemorized) memorizedText else textColor,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// =====================================================================
// CONTEXTUAL BANNERS (extracted, each renders standalone — never stacked
// with the other, enforced by the caller's showX booleans above)
// =====================================================================

@Composable
private fun WarmUpBanner(
    range: Pair<Int, Int>,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onSkip: () -> Unit,
    onWarmUp: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = themeColors.surface,
        border = null,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(shape = CircleShape, color = themeColors.accent.copy(alpha = 0.15f)) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = themeColors.accent,
                        modifier = Modifier.padding(6.dp).size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Next-Session Warm-Up",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText
                        )
                    )
                    Text(
                        text = "Replay Ayahs ${range.first}–${range.second} before new drill?",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.translationText,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onSkip) {
                    Text("Skip", style = MaterialTheme.typography.labelSmall.copy(color = themeColors.translationText))
                }
                Button(
                    onClick = onWarmUp,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Warm-Up", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
private fun ConfidenceBanner(
    prompt: com.example.ui.ConfidencePromptData,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onDismiss: () -> Unit,
    onRate: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = themeColors.surface,
        border = null,
        shadowElevation = 10.dp
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = CircleShape, color = themeColors.accent.copy(alpha = 0.15f)) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier.padding(4.dp).size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Chunk Review Complete",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText)
                        )
                        Text(
                            text = "Ayahs ${prompt.startAyah}–${prompt.endAyah} • How confident do you feel?",
                            style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText, fontSize = 11.5.sp)
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, "Dismiss", tint = themeColors.translationText, modifier = Modifier.size(16.dp))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { onRate("STILL_SHAKY") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = null,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE57373))
                ) {
                    Icon(Icons.Default.Replay, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Still shaky", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
                Button(
                    onClick = { onRate("GOT_IT") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Got it!", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }
    }
}

@Composable
private fun HifzBottomBar(
    activeView: HifzView,
    startAyah: Int,
    endAyah: Int,
    isPlaying: Boolean,
    isBuffering: Boolean,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    patternSubLabel: String = "Drill sequence",
    onOpenJump: () -> Unit,
    onNextBatchClick: () -> Unit,
    onOpenPattern: () -> Unit,
    onHintClick: () -> Unit,
    onOpenRange: () -> Unit,
    onOpenGroups: () -> Unit,
    onPlayClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(themeColors.surface)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Jump to Ayah (Far Left)
            HifzBottomNavButton(
                icon = Icons.Default.NearMe,
                label = "Jump",
                themeColors = themeColors,
                onClick = onOpenJump
            )

            // 2. Pattern (e.g. Pattern 3x3x3) or Hint
            if (activeView == HifzView.RECALL) {
                HifzBottomNavButton(
                    icon = Icons.Default.Lightbulb,
                    label = "Hint",
                    themeColors = themeColors,
                    onClick = onHintClick
                )
            } else {
                HifzBottomNavButton(
                    icon = Icons.Default.Tune,
                    label = "Pattern",
                    themeColors = themeColors,
                    onClick = onOpenPattern
                )
            }

            // 3. Play / Pause Button - Dead Center
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .clickable(onClick = onPlayClick)
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier.size(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = themeColors.accent,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = themeColors.accent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Text(
                    text = if (isPlaying) "Pause" else "Play",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.accent,
                        fontSize = 10.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 4. Range (Portion) or Groups
            if (activeView == HifzView.RECALL) {
                HifzBottomNavButton(
                    icon = Icons.Default.Layers,
                    label = "Groups",
                    themeColors = themeColors,
                    onClick = onOpenGroups
                )
            } else {
                HifzBottomNavButton(
                    icon = Icons.Default.FormatSize,
                    label = "Range",
                    themeColors = themeColors,
                    onClick = onOpenRange
                )
            }

            // 5. Next Batch (Far Right)
            HifzBottomNavButton(
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                label = "Next",
                themeColors = themeColors,
                onClick = onNextBatchClick
            )
        }
    }
}

@Composable
private fun RowScope.HifzBottomNavButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .weight(1f)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = themeColors.arabicText,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = themeColors.arabicText,
                fontSize = 10.5.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// =====================================================================
// UNIFIED PRACTICE & SELF-RECALL CANVAS
// (unchanged from the previously-verified working implementation —
// the Mushaf-flow text rendering, inline ayah markers, and long-press
// context menu all stay exactly as they were)
// =====================================================================

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun UnifiedPracticeAndRecallContent(
    isRecallMode: Boolean,
    targetVerses: List<Verse>,
    surah: Surah,
    startAyah: Int,
    endAyah: Int,
    revealedVerses: Set<Int>,
    memorizedSet: Set<String>,
    silhouetteOpacity: Float,
    isPlaying: Boolean,
    currentPlayingVerse: Int,
    showTranslation: Boolean,
    isTajweedEnabled: Boolean,
    arabicFontSize: Int,
    arabicFont: QuranArabicFont,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    flowScrollState: ScrollState,
    hintTriggerToken: Int,
    onToggleBars: () -> Unit,
    onPlayVerse: (Verse) -> Unit,
    onToggleMemorized: (Verse) -> Unit,
    onVerseTap: (Verse) -> Unit,
    onVerseReveal: (Int) -> Unit = {},
    onResetRevealed: () -> Unit = {},
    onMarkAllMemorized: () -> Unit
) {
    val hintWordCounts = remember(isRecallMode, surah.number, startAyah, endAyah) { mutableStateMapOf<Int, Int>() }
    var contextMenuVerse by remember { mutableStateOf<Verse?>(null) }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var textTopInParent by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    val rangeCache = remember(surah.number, startAyah, endAyah, isTajweedEnabled, themeColors) {
        MushafTextCache.getOrCreateRange(surah, startAyah, endAyah, isTajweedEnabled = isTajweedEnabled, themeColors = themeColors)
    }
    val verseRanges = rangeCache.verseRanges

    LaunchedEffect(isRecallMode) {
        if (isRecallMode) {
            hintWordCounts.clear()
        }
    }

    LaunchedEffect(hintTriggerToken) {
        if (hintTriggerToken > 0 && targetVerses.isNotEmpty()) {
            fun getCleanWords(v: Verse): List<String> {
                val cleanArabic = NoorRepository.sanitizeArabicVerseText(surah.number, v.verseNumber, v.arabicText.trim())
                return cleanArabic.split("\\s+".toRegex()).filter { it.isNotBlank() }
            }

            // Check if all ayahs in the selected range have been fully revealed or tested
            val allAyahsRevealedOrTested = targetVerses.all { v ->
                val words = getCleanWords(v)
                revealedVerses.contains(v.verseNumber) || (words.isNotEmpty() && (hintWordCounts[v.verseNumber] ?: 0) >= words.size)
            }

            if (allAyahsRevealedOrTested) {
                // Continuous Looping:
                // Once all ayahs in the selected range have been fully revealed or tested,
                // clicking "Hint" again resets the masked state and restarts smoothly from the first ayah.
                hintWordCounts.clear()
                onResetRevealed()
                val firstVerse = targetVerses.first()
                hintWordCounts[firstVerse.verseNumber] = 1
                flowScrollState.animateScrollTo(0)
            } else {
                // Multi-Ayah Progression:
                // When clicking "Hint" in Recall mode, hints reveal words sequentially.
                // Upon reaching the end of an ayah, the hint does not stop;
                // the subsequent hint tap advances immediately to reveal the first word of the next ayah in the range.

                // Any previous ayahs that have reached the end of their words are marked revealed in session
                targetVerses.forEach { v ->
                    val words = getCleanWords(v)
                    if (words.isNotEmpty() && (hintWordCounts[v.verseNumber] ?: 0) >= words.size && !revealedVerses.contains(v.verseNumber)) {
                        onVerseReveal(v.verseNumber)
                    }
                }

                // Find the first ayah in range that still has unrevealed words
                val targetAyah = targetVerses.firstOrNull { v ->
                    val words = getCleanWords(v)
                    !revealedVerses.contains(v.verseNumber) && (hintWordCounts[v.verseNumber] ?: 0) < words.size
                }

                if (targetAyah != null) {
                    val words = getCleanWords(targetAyah)
                    val currentHint = hintWordCounts[targetAyah.verseNumber] ?: 0
                    val nextHint = currentHint + 1
                    hintWordCounts[targetAyah.verseNumber] = nextHint

                    // Auto-scroll to ensure the currently hinted ayah is visible
                    val range = verseRanges[targetAyah.verseNumber]
                    val layout = textLayoutResult
                    if (range != null && layout != null) {
                        val line = layout.getLineForOffset(range.first)
                        val lineTop = layout.getLineTop(line)
                        val targetPx = textTopInParent + lineTop - with(density) { 90.dp.toPx() }
                        flowScrollState.animateScrollTo(targetPx.coerceAtLeast(0f).toInt())
                    }
                }
            }
        }
    }

    val handleAyahSelected: (Verse) -> Unit = { targetVerse ->
        if (isRecallMode) {
            onVerseReveal(targetVerse.verseNumber)
            val cleanArabic = NoorRepository.sanitizeArabicVerseText(surah.number, targetVerse.verseNumber, targetVerse.arabicText.trim())
            val words = cleanArabic.split("\\s+".toRegex()).filter { it.isNotBlank() }
            hintWordCounts[targetVerse.verseNumber] = words.size
        }
        onPlayVerse(targetVerse)
    }

    val revealedCount = targetVerses.count { revealedVerses.contains(it.verseNumber) }
    val totalCount = targetVerses.size
    val allTested = totalCount > 0 && revealedCount == totalCount

    val annotatedText = remember(
        rangeCache, isRecallMode, isPlaying, currentPlayingVerse, startAyah, endAyah,
        revealedVerses, hintWordCounts.toMap(), silhouetteOpacity, themeColors
    ) {
        if (!isRecallMode) {
            if (!isPlaying || currentPlayingVerse !in startAyah..endAyah) {
                rangeCache.baseAnnotatedString
            } else {
                val builder = AnnotatedString.Builder(rangeCache.baseAnnotatedString)
                verseRanges[currentPlayingVerse]?.let { range ->
                    builder.addStyle(
                        style = SpanStyle(
                            color = themeColors.accent,
                            fontWeight = FontWeight.Bold
                        ),
                        start = range.first, end = range.second
                    )
                }
                builder.toAnnotatedString()
            }
        } else {
            val builder = AnnotatedString.Builder(rangeCache.baseAnnotatedString)
            targetVerses.forEach { verse ->
                val range = verseRanges[verse.verseNumber] ?: return@forEach
                val isRevealed = revealedVerses.contains(verse.verseNumber)
                val hintCount = hintWordCounts[verse.verseNumber] ?: 0
                val isPlayingThis = isPlaying && currentPlayingVerse == verse.verseNumber

                if (isPlayingThis) {
                    builder.addStyle(
                        SpanStyle(color = themeColors.accent, fontWeight = FontWeight.Bold),
                        range.first,
                        range.second
                    )
                } else if (isRevealed) {
                    builder.addStyle(SpanStyle(color = themeColors.arabicText, fontWeight = FontWeight.Normal), range.first, range.second)
                } else if (hintCount <= 0) {
                    builder.addStyle(SpanStyle(color = themeColors.arabicText.copy(alpha = silhouetteOpacity)), range.first, range.second)
                } else {
                    val cleanArabic = NoorRepository.sanitizeArabicVerseText(surah.number, verse.verseNumber, verse.arabicText.trim())
                    val words = cleanArabic.split("\\s+".toRegex()).filter { it.isNotBlank() }
                    val hintedText = words.take(hintCount).joinToString(" ")
                    val splitOffset = (range.first + hintedText.length).coerceAtMost(range.second)

                    builder.addStyle(
                        SpanStyle(color = themeColors.accent, fontWeight = FontWeight.Bold),
                        range.first, splitOffset
                    )
                    if (splitOffset < range.second) {
                        builder.addStyle(SpanStyle(color = themeColors.arabicText.copy(alpha = silhouetteOpacity)), splitOffset, range.second)
                    }
                }
            }
            builder.toAnnotatedString()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clipToBounds()
    ) {
        val minCanvasHeight = maxHeight
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minCanvasHeight)
                .verticalScroll(flowScrollState)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            if (startAyah == 1 && surah.number != 9 && surah.number != 1) {
                val isBasmalaPlaying = isPlaying && currentPlayingVerse == 0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            onPlayVerse(
                                Verse(
                                    surahNumber = surah.number,
                                    verseNumber = 0,
                                    arabicText = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                                    transliteration = "Bismillahir-Rahmanir-Rahim",
                                    translation = "In the name of Allah, the Entirely Merciful, the Especially Merciful"
                                )
                            )
                        }
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                        style = TextStyle(
                            fontFamily = arabicFont.fontFamily,
                            fontSize = (arabicFontSize + 2).sp,
                            lineHeight = (arabicFontSize * 1.8).sp,
                            color = if (isBasmalaPlaying) themeColors.accent else themeColors.arabicText,
                            fontWeight = if (isBasmalaPlaying) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .onGloballyPositioned { coordinates -> textTopInParent = coordinates.positionInParent().y }
                    .pointerInput(isRecallMode, verseRanges) {
                        detectTapGestures(
                            onTap = { tapOffset ->
                                val layout = textLayoutResult ?: return@detectTapGestures
                                val charIndex = layout.getOffsetForPosition(tapOffset)
                                val verseNum = findVerseNumberForCharIndex(charIndex, verseRanges)
                                val targetVerse = targetVerses.firstOrNull { it.verseNumber == verseNum } ?: return@detectTapGestures
                                handleAyahSelected(targetVerse)
                            },
                            onLongPress = { tapOffset ->
                                val layout = textLayoutResult ?: return@detectTapGestures
                                val charIndex = layout.getOffsetForPosition(tapOffset)
                                val verseNum = findVerseNumberForCharIndex(charIndex, verseRanges)
                                contextMenuVerse = targetVerses.firstOrNull { it.verseNumber == verseNum }
                            }
                        )
                    }
            ) {
                Text(
                    text = annotatedText,
                    onTextLayout = { textLayoutResult = it },
                    style = TextStyle(
                        fontFamily = arabicFont.fontFamily,
                        fontSize = arabicFontSize.sp,
                        lineHeight = (arabicFontSize * 2.1).sp,
                        fontWeight = FontWeight.Normal,
                        color = themeColors.arabicText,
                        textAlign = TextAlign.Center,
                        textDirection = TextDirection.Rtl
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (isRecallMode && allTested) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = (if (themeColors.isDark) PrimaryTealDark else PrimaryTealLight).copy(alpha = 0.12f),
                    border = null
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🎉 All Verses Revealed!", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32)))
                        Text(
                            text = "Masha'Allah, you have recited and recalled Ayahs $startAyah–$endAyah of ${surah.nameEnglish}.",
                            style = MaterialTheme.typography.bodySmall.copy(color = themeColors.arabicText, textAlign = TextAlign.Center)
                        )
                        Button(onClick = onMarkAllMemorized, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                            Text("Mark All Ayahs as Mastered ✓", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            }
        }
    }

    if (contextMenuVerse != null) {
        val selectedVerse = contextMenuVerse!!
        val isMemorized = memorizedSet.contains("${surah.number}_${selectedVerse.verseNumber}")
        val isReciting = isPlaying && currentPlayingVerse == selectedVerse.verseNumber

        ModalBottomSheet(
            onDismissRequest = { contextMenuVerse = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = themeColors.surface,
            contentColor = themeColors.arabicText
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier.size(34.dp).clip(CircleShape)
                                .background(if (isReciting) themeColors.accent else themeColors.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${selectedVerse.verseNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = if (isReciting) Color.White else themeColors.arabicText)
                            )
                        }
                        Column {
                            Text("Ayah ${selectedVerse.verseNumber}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
                            Text("Surah ${surah.nameEnglish} • ${surah.nameArabic}", style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText))
                        }
                    }
                    if (isMemorized) {
                        Surface(shape = RoundedCornerShape(8.dp), color = (if (themeColors.isDark) PrimaryTealDark else PrimaryTealLight).copy(alpha = 0.15f), border = null) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(13.dp))
                                Text("Memorized", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 11.sp))
                            }
                        }
                    }
                }

                Surface(shape = RoundedCornerShape(12.dp), color = themeColors.background, border = null, modifier = Modifier.fillMaxWidth()) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = selectedVerse.arabicText,
                            style = TextStyle(fontFamily = arabicFont.fontFamily, fontSize = arabicFontSize.sp, lineHeight = (arabicFontSize * 1.6).sp, color = themeColors.arabicText, textAlign = TextAlign.Right),
                            maxLines = 3, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth().padding(12.dp)
                        )
                    }
                }

                if (selectedVerse.translation.isNotBlank()) {
                    Text(
                        text = selectedVerse.translation,
                        style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText, fontSize = 13.sp, lineHeight = 18.sp),
                        maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                }

                HorizontalDivider(color = themeColors.border.copy(alpha = 0.5f))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isReciting) themeColors.accent.copy(alpha = 0.15f) else themeColors.background,
                    border = null,
                    modifier = Modifier.fillMaxWidth().clickable { onPlayVerse(selectedVerse); contextMenuVerse = null }
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(themeColors.accent.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Icon(if (isReciting) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = themeColors.accent, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(if (isReciting) "Pause Recitation" else "Play Recitation", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
                            Text("Listen to Qari repeat this Ayah in current drill", style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText, fontSize = 11.5.sp))
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isMemorized) (if (themeColors.isDark) PrimaryTealDark else PrimaryTealLight).copy(alpha = 0.12f) else themeColors.background,
                    border = null,
                    modifier = Modifier.fillMaxWidth().clickable { onToggleMemorized(selectedVerse); contextMenuVerse = null }
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(if (isMemorized) (if (themeColors.isDark) SuccessGreenDark else SuccessGreenLight).copy(alpha = 0.2f) else themeColors.surface), contentAlignment = Alignment.Center) {
                            Icon(if (isMemorized) Icons.Default.CheckCircle else Icons.Default.Check, null, tint = if (isMemorized) (if (themeColors.isDark) SuccessGreenDark else SuccessGreenLight) else themeColors.translationText, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                if (isMemorized) "Mark as Needs Review" else "Mark Ayah Memorized",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = if (isMemorized) (if (themeColors.isDark) SuccessGreenDark else SuccessGreenLight) else themeColors.arabicText)
                            )
                            Text(
                                if (isMemorized) "Remove mastered badge from this Ayah" else "Count towards Surah mastery and daily streak",
                                style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText, fontSize = 11.5.sp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun RecallSelfTestContent(
    targetVerses: List<Verse>,
    surah: Surah,
    startAyah: Int,
    endAyah: Int,
    memorizedSet: Set<String>,
    silhouetteOpacity: Float,
    isPlaying: Boolean,
    currentPlayingVerse: Int,
    showTranslation: Boolean,
    isTajweedEnabled: Boolean,
    arabicFontSize: Int,
    arabicFont: QuranArabicFont,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    flowScrollState: ScrollState,
    recallGroupSize: Int,
    onToggleBars: () -> Unit,
    onPlayVerse: (Verse) -> Unit,
    onToggleMemorized: (Verse) -> Unit,
    onMarkAllMemorized: () -> Unit,
    onRecordRecallResult: (gotIt: Int, struggled: Int, missed: Int, hints: Int) -> Unit = { _, _, _, _ -> }
) {
    val isDark = themeColors.isDark
    val goldColor = if (isDark) Color(0xFFC9A227) else Color(0xFFC28100)
    val goldAccent = if (isDark) Color(0xFFD9A44E) else Color(0xFFB45309)
    val goldBg = if (isDark) Color(0xFF2E2415) else Color(0xFFFEF8EC)
    val goldTrack = if (isDark) Color(0xFF374151) else Color(0xFFFDE68A).copy(alpha = 0.45f)
    val linkTextColor = if (isDark) Color(0xFF2FBF96) else Color(0xFF0F433F)
    val primaryAccent = if (isDark) Color(0xFF2FBF96) else Color(0xFF107C41)

    // Active pool of verses being tested
    var activePool by remember(targetVerses) { mutableStateOf(targetVerses) }
    
    val groups = remember(activePool, recallGroupSize) {
        activePool.chunked(recallGroupSize)
    }
    
    var currentGroupIndex by remember(groups) { mutableStateOf(0) }
    
    val verdicts = remember(activePool) { mutableStateMapOf<Int, String>() }
    val hintCounts = remember(activePool) { mutableStateMapOf<Int, Int>() }
    var isCurrentRevealed by remember(groups, currentGroupIndex) { mutableStateOf(false) }

    val totalGroupsCount = groups.size
    val allTested = totalGroupsCount > 0 && currentGroupIndex >= totalGroupsCount

    var sessionRecorded by remember(activePool, currentGroupIndex) { mutableStateOf(false) }

    LaunchedEffect(allTested) {
        if (allTested && !sessionRecorded) {
            sessionRecorded = true
            val gotItCount = activePool.count { verdicts[it.verseNumber] == "Got it" }
            val struggledCount = activePool.count { verdicts[it.verseNumber] == "Struggled" }
            val missedCount = activePool.count { verdicts[it.verseNumber] == "Missed it" }
            val totalHints = hintCounts.values.sum()
            onRecordRecallResult(gotItCount, struggledCount, missedCount, totalHints)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val minCanvasHeight = maxHeight

        if (allTested) {
            // SESSION SUMMARY RESULTS VIEW
            val gotItCount = activePool.count { verdicts[it.verseNumber] == "Got it" }
            val struggledCount = activePool.count { verdicts[it.verseNumber] == "Struggled" }
            val missedCount = activePool.count { verdicts[it.verseNumber] == "Missed it" }

            val failedVerses = activePool.filter {
                val verd = verdicts[it.verseNumber]
                verd == "Struggled" || verd == "Missed it"
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(flowScrollState)
                    .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .universalCardShadow(
                            shape = RoundedCornerShape(20.dp),
                            elevation = 4.dp,
                            isDark = isDark
                        ),
                    shape = RoundedCornerShape(20.dp),
                    color = themeColors.surface,
                    shadowElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Recall Test Completed! 🎉",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText
                            )
                        )

                        Text(
                            text = "Masha'Allah, you have self-tested on Ayahs $startAyah–$endAyah. Here are your performance results:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = themeColors.translationText,
                                textAlign = TextAlign.Center
                            )
                        )

                        // Three columns side-by-side for results
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Got It
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDark) Color(0xFF1B5E20).copy(alpha = 0.15f) else Color(0xFFE8F5E9),
                                border = null,
                                shadowElevation = 0.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Got It", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$gotItCount", style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF2E7D32), fontWeight = FontWeight.ExtraBold))
                                }
                            }

                            // Struggled
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDark) Color(0xFFE65100).copy(alpha = 0.15f) else Color(0xFFFFF3E0),
                                border = null,
                                shadowElevation = 0.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Struggled", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFEF6C00), fontWeight = FontWeight.Bold))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$struggledCount", style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFEF6C00), fontWeight = FontWeight.ExtraBold))
                                }
                            }

                            // Missed It
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDark) Color(0xFFB71C1C).copy(alpha = 0.15f) else Color(0xFFFFEBEE),
                                border = null,
                                shadowElevation = 0.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Missed", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFC62828), fontWeight = FontWeight.Bold))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$missedCount", style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFC62828), fontWeight = FontWeight.ExtraBold))
                                }
                            }
                        }

                        if (failedVerses.isNotEmpty()) {
                            HorizontalDivider(color = themeColors.border.copy(alpha = 0.2f))
                            Text(
                                text = "Needs Review: Ayah(s) ${failedVerses.joinToString(", ") { "${it.verseNumber}" }}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = themeColors.translationText,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            )

                            Button(
                                onClick = {
                                    // Reset to only failed verses
                                    activePool = failedVerses
                                    currentGroupIndex = 0
                                    verdicts.clear()
                                    hintCounts.clear()
                                    isCurrentRevealed = false
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = primaryAccent)
                            ) {
                                Icon(Icons.Default.Replay, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Redrill These ${failedVerses.size} Ayahs", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Perfect recall! Complete mastery congratulations
                            Button(
                                onClick = onMarkAllMemorized,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mark All as Mastered ✓", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Always offer Restart Full Test option
                        OutlinedButton(
                            onClick = {
                                activePool = targetVerses
                                currentGroupIndex = 0
                                verdicts.clear()
                                hintCounts.clear()
                                isCurrentRevealed = false
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = themeColors.arabicText),
                            border = null
                        ) {
                            Text("Restart Full Test", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // ACTIVE GATED TEST STEP (FIXED BUTTONS AT BOTTOM, SCROLLABLE CARD AT TOP)
            val group = groups[currentGroupIndex]
            
            LaunchedEffect(currentPlayingVerse, isPlaying) {
                if (isPlaying && group.any { it.verseNumber == currentPlayingVerse }) {
                    isCurrentRevealed = true
                }
            }

            val cleanArabicList = remember(group) {
                group.map { verse ->
                    NoorRepository.sanitizeArabicVerseText(surah.number, verse.verseNumber, verse.arabicText.trim())
                }
            }
            val combinedCleanArabic = remember(cleanArabicList) {
                cleanArabicList.joinToString(" ")
            }
            val words = remember(combinedCleanArabic) {
                combinedCleanArabic.split("\\s+".toRegex()).filter { it.isNotBlank() }
            }

            val hintCountKey = remember(group) { group.first().verseNumber }
            val hintCount = hintCounts[hintCountKey] ?: 0

            val annotatedArabic = remember(group, cleanArabicList, combinedCleanArabic, hintCount, silhouetteOpacity, themeColors, isCurrentRevealed, isTajweedEnabled) {
                val builder = AnnotatedString.Builder()
                if (isCurrentRevealed) {
                    group.forEachIndexed { idx, verse ->
                        val cleanText = NoorRepository.sanitizeArabicVerseText(surah.number, verse.verseNumber, verse.arabicText.trim())
                        if (isTajweedEnabled) {
                            val tajweedPart = TajweedEngine.formatTajweedText(cleanText, isEnabled = true, themeColors = themeColors)
                            builder.append(tajweedPart)
                        } else {
                            val start = builder.length
                            builder.append(cleanText)
                            builder.addStyle(SpanStyle(color = themeColors.arabicText, fontWeight = FontWeight.Normal), start, builder.length)
                        }
                        
                        builder.append(" ")
                        val markerStart = builder.length
                        builder.append(formatAyahMarker(verse.verseNumber))
                        builder.addStyle(SpanStyle(color = themeColors.accent, fontWeight = FontWeight.Medium), markerStart, builder.length)
                        if (idx < group.size - 1) {
                            builder.append(" ")
                        }
                    }
                } else if (hintCount <= 0) {
                    group.forEachIndexed { idx, verse ->
                        val cleanText = NoorRepository.sanitizeArabicVerseText(surah.number, verse.verseNumber, verse.arabicText.trim())
                        val start = builder.length
                        builder.append(cleanText)
                        builder.addStyle(SpanStyle(color = themeColors.arabicText.copy(alpha = silhouetteOpacity)), start, builder.length)
                        
                        builder.append(" ")
                        val markerStart = builder.length
                        builder.append(formatAyahMarker(verse.verseNumber))
                        builder.addStyle(SpanStyle(color = themeColors.accent, fontWeight = FontWeight.Medium), markerStart, builder.length)
                        if (idx < group.size - 1) {
                            builder.append(" ")
                        }
                    }
                } else {
                    var wordsRevealedSoFar = 0
                    group.forEachIndexed { idx, verse ->
                        val cleanText = NoorRepository.sanitizeArabicVerseText(surah.number, verse.verseNumber, verse.arabicText.trim())
                        val verseWords = cleanText.split("\\s+".toRegex()).filter { it.isNotBlank() }
                        
                        val wordsToRevealForThisVerse = (hintCount - wordsRevealedSoFar).coerceIn(0, verseWords.size)
                        wordsRevealedSoFar += verseWords.size
                        
                        val revealedPart = verseWords.take(wordsToRevealForThisVerse).joinToString(" ")
                        val maskedPart = verseWords.drop(wordsToRevealForThisVerse).joinToString(" ")
                        
                        if (revealedPart.isNotEmpty()) {
                            val revStart = builder.length
                            builder.append(revealedPart)
                            builder.addStyle(SpanStyle(color = themeColors.accent, fontWeight = FontWeight.Bold), revStart, builder.length)
                        }
                        if (maskedPart.isNotEmpty()) {
                            if (revealedPart.isNotEmpty()) builder.append(" ")
                            val maskStart = builder.length
                            builder.append(maskedPart)
                            builder.addStyle(SpanStyle(color = themeColors.arabicText.copy(alpha = silhouetteOpacity)), maskStart, builder.length)
                        }
                        
                        builder.append(" ")
                        val markerStart = builder.length
                        builder.append(formatAyahMarker(verse.verseNumber))
                        builder.addStyle(SpanStyle(color = goldAccent, fontWeight = FontWeight.Medium), markerStart, builder.length)
                        if (idx < group.size - 1) {
                            builder.append(" ")
                        }
                    }
                }
                builder.toAnnotatedString()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Top Progress Section: Pinned right at top of recall area with modern design
                val startAyahNum = group.firstOrNull()?.verseNumber ?: 1
                val endAyahNum = group.lastOrNull()?.verseNumber ?: 1
                val groupAyahRangeText = if (startAyahNum == endAyahNum) "Ayah $startAyahNum" else "Ayahs $startAyahNum–$endAyahNum"
                val progressFraction = if (totalGroupsCount > 0) {
                    ((currentGroupIndex + 1).toFloat() / totalGroupsCount.toFloat()).coerceIn(0f, 1f)
                } else 0f

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
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
                                Surface(
                                    shape = CircleShape,
                                    color = goldAccent.copy(alpha = 0.12f),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = goldAccent,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Self-Recall Progress",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF1E293B),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = goldAccent.copy(alpha = 0.10f),
                                border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.25f))
                            ) {
                                Text(
                                    text = "Step ${currentGroupIndex + 1} of $totalGroupsCount • $groupAyahRangeText",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = goldAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = goldColor,
                            trackColor = goldTrack
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clipToBounds()
                ) {
                    // Center portion: scrollable focal card
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(flowScrollState)
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Big beautiful focal card containing the masked verses
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            shadowElevation = 0.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Custom badge or header
                                val badgeText = remember(group) {
                                    if (group.size == 1) "AYAH ${group.first().verseNumber}"
                                    else "AYAHS ${group.first().verseNumber}–${group.last().verseNumber}"
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = goldBg,
                                    border = null
                                ) {
                                    Text(
                                        text = badgeText,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = goldAccent,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }

                                // Arabic text
                                Text(
                                    text = annotatedArabic,
                                    style = TextStyle(
                                        fontFamily = arabicFont.fontFamily,
                                        fontSize = arabicFontSize.sp,
                                        lineHeight = (arabicFontSize * 2.1).sp,
                                        fontWeight = FontWeight.Normal,
                                        color = themeColors.arabicText,
                                        textAlign = TextAlign.Center,
                                        textDirection = TextDirection.Rtl
                                    ),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                                )

                                // Translations visible only after unmasking and if translation is toggled on
                                if (isCurrentRevealed && showTranslation) {
                                    HorizontalDivider(color = Color(0xFFE5E7EB))
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        group.forEach { verse ->
                                            if (verse.translation.isNotBlank()) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        text = "${verse.verseNumber}.",
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = linkTextColor,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                    Text(
                                                        text = verse.translation,
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            color = Color(0xFF4B5563),
                                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                            lineHeight = 20.sp
                                                        ),
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Pinned Bottom Action Controls Box
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                    if (!isCurrentRevealed) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 30% width for Get a Hint button styled to the left (Warm Gold hint pill)
                            Button(
                                onClick = {
                                    val nextHint = hintCount + 1
                                    if (nextHint >= words.size) {
                                        isCurrentRevealed = true
                                    } else {
                                        hintCounts[hintCountKey] = nextHint
                                    }
                                },
                                modifier = Modifier.weight(0.3f).fillMaxHeight(),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = goldBg,
                                    contentColor = goldAccent
                                ),
                                border = null,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Default.Lightbulb, null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Hint", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                            }

                            // 70% width for Reveal Whole button
                            Button(
                                onClick = { isCurrentRevealed = true },
                                modifier = Modifier.weight(0.7f).fillMaxHeight(),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = primaryAccent)
                            ) {
                                Icon(Icons.Default.Visibility, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reveal Whole", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            }
                        }
                    } else {
                        // Revealed Verdict options (Fixed at bottom)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Rate your recall accuracy:",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6B7280), fontWeight = FontWeight.Bold)
                            )

                            val isHinted = hintCount > 0

                            if (isHinted) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFF9800).copy(alpha = 0.15f),
                                    modifier = Modifier.padding(bottom = 2.dp)
                                ) {
                                    Text(
                                        text = "💡 Hint was used: \"Got It\" capped at \"Struggled\"",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFEF6C00), fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Got It (Disabled if hint was used)
                                Button(
                                    onClick = {
                                        if (!isHinted) {
                                            group.forEach { v ->
                                                verdicts[v.verseNumber] = "Got it"
                                                val key = "${surah.number}_${v.verseNumber}"
                                                if (!memorizedSet.contains(key)) {
                                                    onToggleMemorized(v)
                                                }
                                            }
                                            currentGroupIndex++
                                        }
                                    },
                                    enabled = !isHinted,
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2E7D32),
                                        disabledContainerColor = Color(0xFF2E7D32).copy(alpha = 0.25f),
                                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Text("Got it ✓", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                // Struggled
                                Button(
                                    onClick = {
                                        group.forEach { v ->
                                            verdicts[v.verseNumber] = "Struggled"
                                            val key = "${surah.number}_${v.verseNumber}"
                                            if (memorizedSet.contains(key)) {
                                                onToggleMemorized(v)
                                            }
                                        }
                                        currentGroupIndex++
                                    },
                                    modifier = Modifier.weight(1.1f).height(44.dp),
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00))
                                ) {
                                    Text("Struggled ⚠", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                // Missed It
                                Button(
                                    onClick = {
                                        group.forEach { v ->
                                            verdicts[v.verseNumber] = "Missed it"
                                            val key = "${surah.number}_${v.verseNumber}"
                                            if (memorizedSet.contains(key)) {
                                                onToggleMemorized(v)
                                            }
                                        }
                                        currentGroupIndex++
                                    },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                                ) {
                                    Text("Missed ✗", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

// =====================================================================
// RANGE SELECTOR & DROPDOWN COMPONENT
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AyahDropdownMenu(
    label: String,
    selectedAyah: Int,
    totalVerses: Int,
    modifier: Modifier = Modifier,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onAyahSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "Ayah $selectedAyah",
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = themeColors.translationText, fontSize = 11.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                focusedTextColor = themeColors.arabicText,
                unfocusedTextColor = themeColors.arabicText,
                focusedContainerColor = themeColors.background,
                unfocusedContainerColor = themeColors.background
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(themeColors.surface).heightIn(max = 240.dp)
        ) {
            (1..totalVerses).forEach { verseNum ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Ayah $verseNum",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (verseNum == selectedAyah) FontWeight.Bold else FontWeight.Normal,
                                color = if (verseNum == selectedAyah) themeColors.accent else themeColors.arabicText
                            )
                        )
                    },
                    onClick = {
                        onAyahSelected(verseNum)
                        expanded = false
                    }
                )
            }
        }
    }
}

// =====================================================================
// SETTINGS TAB + SHEET (the ONE settings destination)
// =====================================================================

@Composable
private fun HifzSettingsTab(
    silhouetteOpacity: Float,
    repeatCount: Int,
    delaySeconds: Int,
    loopRange: Boolean,
    audioSyncReveal: Boolean,
    showTranslation: Boolean,
    isTajweedEnabled: Boolean,
    arabicFontSize: Int,
    selectedArabicFont: QuranArabicFont,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    ayahPattern: String,
    isCustomPatternEnabled: Boolean,
    wordRepeatCount: Int,
    chunkReviewSize: Int,
    recallGroupSize: Int,
    onToggleCustomPattern: (Boolean) -> Unit,
    onSetAyahPattern: (String) -> Unit,
    onSelectWordRepeatCount: (Int) -> Unit,
    onSelectChunkReviewSize: (Int) -> Unit,
    onSelectRecallGroupSize: (Int) -> Unit,
    onSetSilhouetteOpacity: (Float) -> Unit,
    onSelectRepeatCount: (Int) -> Unit,
    onSelectDelaySeconds: (Int) -> Unit,
    onToggleLoopRange: () -> Unit,
    onToggleAudioSyncReveal: () -> Unit,
    onToggleShowTranslation: () -> Unit,
    onToggleTajweed: (Boolean) -> Unit,
    onOpenTajweedGuide: () -> Unit,
    onSetFontSize: (Int) -> Unit,
    onSetArabicFont: (QuranArabicFont) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                border = null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Tajweed Rules (أَحْكَام التَّجْوِيد)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText
                                )
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "Color-codes Ghunna, Ikhfa, Qalqalah, Madd, and Idgham rules with floating helper",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = themeColors.translationText,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }

                        Switch(
                            checked = isTajweedEnabled,
                            onCheckedChange = { onToggleTajweed(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF1BA486),
                                uncheckedThumbColor = themeColors.translationText,
                                uncheckedTrackColor = themeColors.border
                            )
                        )
                    }

                    if (isTajweedEnabled) {
                        Button(
                            onClick = onOpenTajweedGuide,
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
                                "Open Tajweed Rules Guide",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            CustomPatternSelectorCard(
                title = "Ayah Repetition Pattern",
                subtitle = "Define verse grouping sequence (e.g. '3 3 3' plays 3 Ayahs 3 times each)",
                currentPattern = ayahPattern,
                repeatCount = repeatCount,
                isCustomPatternEnabled = isCustomPatternEnabled,
                themeColors = themeColors,
                onToggleCustomPattern = onToggleCustomPattern,
                onPatternChanged = onSetAyahPattern,
                onRepeatCountChanged = onSelectRepeatCount
            )
        }

        item {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = themeColors.surface), border = null, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Silhouette Opacity", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
                            Text("Controls visibility of muted verses in Recall mode", style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText, fontSize = 12.sp))
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = themeColors.accent.copy(alpha = 0.15f), border = null) {
                            Text("${(silhouetteOpacity * 100).toInt()}%", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.accent), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                    Slider(
                        value = silhouetteOpacity, onValueChange = onSetSilhouetteOpacity, valueRange = 0.02f..0.50f,
                        colors = SliderDefaults.colors(thumbColor = themeColors.accent, activeTrackColor = themeColors.accent, inactiveTrackColor = themeColors.border.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = themeColors.surface), border = null, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Recitation Drill Rules", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Pause Between Ayahs: ${if (delaySeconds == 0) "Instant (0s)" else "${delaySeconds}s"}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
                        Row(modifier = Modifier.fillMaxWidth().clip(CircleShape).background(themeColors.background).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(0, 1, 2, 3, 5, 8).forEach { sec ->
                                val isSelected = delaySeconds == sec
                                Surface(shape = CircleShape, color = if (isSelected) themeColors.accent else Color.Transparent, modifier = Modifier.weight(1f).clickable { onSelectDelaySeconds(sec) }) {
                                    Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                        Text(if (sec == 0) "0s" else "${sec}s", style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) Color.White else themeColors.arabicText))
                                    }
                                }
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Chunk Review Interval: Every $chunkReviewSize Ayahs", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
                        Row(modifier = Modifier.fillMaxWidth().clip(CircleShape).background(themeColors.background).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(3, 5, 7, 10).forEach { size ->
                                val isSelected = chunkReviewSize == size
                                Surface(shape = CircleShape, color = if (isSelected) themeColors.accent else Color.Transparent, modifier = Modifier.weight(1f).clickable { onSelectChunkReviewSize(size) }) {
                                    Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                        Text("$size", style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) Color.White else themeColors.arabicText))
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = themeColors.border.copy(alpha = 0.5f))

                    SettingsToggleRow("Loop Range Continuously", "Auto-restarts drill from start Ayah upon finishing", loopRange, themeColors) { onToggleLoopRange() }
                    SettingsToggleRow("Reveal on Audio Recitation", "Unmasks each verse automatically as the Qari reaches it", audioSyncReveal, themeColors) { onToggleAudioSyncReveal() }
                    SettingsToggleRow("Display English Translation", "Hiding translations helps deep focus on Arabic recall", showTranslation, themeColors) { onToggleShowTranslation() }
                }
            }
        }

        item {
            Card(shape = CircleShape, colors = CardDefaults.cardColors(containerColor = themeColors.surface), border = null, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Arabic Typography", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))

                    Row(modifier = Modifier.fillMaxWidth().clip(CircleShape).background(themeColors.background).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        QuranArabicFont.entries.forEach { font ->
                            val isSelected = selectedArabicFont == font
                            Surface(shape = CircleShape, color = if (isSelected) themeColors.accent else Color.Transparent, modifier = Modifier.weight(1f).clickable { onSetArabicFont(font) }) {
                                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(font.displayName, style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) Color.White else themeColors.arabicText, fontSize = 11.5.sp))
                                }
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Arabic Font Size", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
                            Text("${arabicFontSize}sp", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.accent))
                        }
                        Slider(value = arabicFontSize.toFloat(), onValueChange = { onSetFontSize(it.toInt()) }, valueRange = 18f..38f, steps = 19,
                            colors = SliderDefaults.colors(thumbColor = themeColors.accent, activeTrackColor = themeColors.accent, inactiveTrackColor = themeColors.border))
                    }
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = themeColors.surface), border = null, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Self-Recall Settings", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Recall Group Size: ${if (recallGroupSize == 1) "1 Ayah at a time" else "$recallGroupSize Ayahs at a time"}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
                        Row(modifier = Modifier.fillMaxWidth().clip(CircleShape).background(themeColors.background).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(1, 3, 5, 10).forEach { size ->
                                val isSelected = recallGroupSize == size
                                Surface(shape = CircleShape, color = if (isSelected) themeColors.accent else Color.Transparent, modifier = Modifier.weight(1f).clickable { onSelectRecallGroupSize(size) }) {
                                    Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                        Text(if (size == 1) "Single (1)" else "$size Ayahs", style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) Color.White else themeColors.arabicText))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onToggle: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
            Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText, fontSize = 11.5.sp))
        }
        Switch(checked = checked, onCheckedChange = { onToggle() }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = themeColors.accent))
    }
}

@Composable
private fun CustomPatternSelectorCard(
    title: String,
    subtitle: String,
    currentPattern: String,
    repeatCount: Int,
    isCustomPatternEnabled: Boolean,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onToggleCustomPattern: (Boolean) -> Unit,
    onPatternChanged: (String) -> Unit,
    onRepeatCountChanged: (Int) -> Unit
) {
    var textInput by remember(currentPattern) { mutableStateOf(currentPattern) }

    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = themeColors.surface), border = null, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText, fontSize = 11.5.sp))
            }

            // Custom Pattern Toggle Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(themeColors.background)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Custom Pattern",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText)
                    )
                    Text(
                        if (isCustomPatternEnabled) "Define custom sequence (e.g. 1 3 2 5 4)" else "Use predefined pattern levels",
                        style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText, fontSize = 11.sp)
                    )
                }
                Switch(
                    checked = isCustomPatternEnabled,
                    onCheckedChange = { onToggleCustomPattern(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = themeColors.accent)
                )
            }

            if (isCustomPatternEnabled) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Custom Pattern Sequence:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = themeColors.translationText, fontSize = 11.sp)
                    )
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = {
                            textInput = it
                            onPatternChanged(it)
                        },
                        placeholder = { Text("e.g. 1 3 2 5 4 or 3, 3, 3", color = themeColors.translationText.copy(alpha = 0.6f), fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedTextColor = themeColors.arabicText,
                            unfocusedTextColor = themeColors.arabicText,
                            focusedContainerColor = themeColors.background,
                            unfocusedContainerColor = themeColors.background
                        )
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Predefined Pattern Levels:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = themeColors.translationText, fontSize = 11.sp)
                    )

                    // Beginner Level
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Beginner",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = themeColors.accent, fontSize = 11.sp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("4 4 4", "5 5 5", "6 6 6").forEach { level ->
                                val cleanLevel = level.replace(" ", ", ")
                                val cleanCurrent = currentPattern.replace(" ", ", ")
                                val isSelected = cleanCurrent == cleanLevel || currentPattern == level
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) themeColors.accent else themeColors.background,
                                    border = null,
                                    modifier = Modifier.weight(1f).clickable {
                                        textInput = level
                                        onPatternChanged(level)
                                    }
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            level,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else themeColors.arabicText,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Medium Level
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Medium",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = themeColors.accent, fontSize = 11.sp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("3 3 3", "2 2 2", "1 1 1").forEach { level ->
                                val cleanLevel = level.replace(" ", ", ")
                                val cleanCurrent = currentPattern.replace(" ", ", ")
                                val isSelected = cleanCurrent == cleanLevel || currentPattern == level
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) themeColors.accent else themeColors.background,
                                    border = null,
                                    modifier = Modifier.weight(1f).clickable {
                                        textInput = level
                                        onPatternChanged(level)
                                    }
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            level,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else themeColors.arabicText,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Single unified Repeat Each Step
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Repeat Each Step: ${if (repeatCount == 0) "None" else "${repeatCount}x"}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(themeColors.background).padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val repeatMap = listOf("None" to 0, "1x" to 1, "2x" to 2, "3x" to 3, "5x" to 5)
                    repeatMap.forEach { (label, count) ->
                        val isSelected = repeatCount == count
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) themeColors.accent else Color.Transparent,
                            modifier = Modifier.weight(1f).clickable { onRepeatCountChanged(count) }
                        ) {
                            Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else themeColors.arabicText
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupsSettingsSheet(
    recallGroupSize: Int,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onSelectRecallGroupSize: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = themeColors.surface) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Groups Settings", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close", tint = themeColors.translationText) }
            }

            Text("Recall Group Size: ${if (recallGroupSize == 1) "1 Ayah at a time" else "$recallGroupSize Ayahs at a time"}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
            Row(modifier = Modifier.fillMaxWidth().clip(CircleShape).background(themeColors.background).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(1, 3, 5, 10).forEach { size ->
                    val isSelected = recallGroupSize == size
                    Surface(shape = CircleShape, color = if (isSelected) themeColors.accent else Color.Transparent, modifier = Modifier.weight(1f).clickable { onSelectRecallGroupSize(size) }) {
                        Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                            Text(if (size == 1) "Single (1)" else "$size Ayahs", style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) Color.White else themeColors.arabicText))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HifzSettingsSheet(
    silhouetteOpacity: Float,
    repeatCount: Int,
    delaySeconds: Int,
    loopRange: Boolean,
    audioSyncReveal: Boolean,
    showTranslation: Boolean,
    isTajweedEnabled: Boolean,
    arabicFontSize: Int,
    selectedArabicFont: QuranArabicFont,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    ayahPattern: String,
    isCustomPatternEnabled: Boolean,
    wordRepeatCount: Int,
    chunkReviewSize: Int,
    recallGroupSize: Int,
    onToggleCustomPattern: (Boolean) -> Unit,
    onSetAyahPattern: (String) -> Unit,
    onSelectWordRepeatCount: (Int) -> Unit,
    onSelectChunkReviewSize: (Int) -> Unit,
    onSelectRecallGroupSize: (Int) -> Unit,
    onSetSilhouetteOpacity: (Float) -> Unit,
    onSelectRepeatCount: (Int) -> Unit,
    onSelectDelaySeconds: (Int) -> Unit,
    onToggleLoopRange: () -> Unit,
    onToggleAudioSyncReveal: () -> Unit,
    onToggleShowTranslation: () -> Unit,
    onToggleTajweed: (Boolean) -> Unit,
    onOpenTajweedGuide: () -> Unit,
    onSetFontSize: (Int) -> Unit,
    onSetArabicFont: (QuranArabicFont) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
 
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = themeColors.surface) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Hifz Studio Settings", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close", tint = themeColors.translationText) }
            }
 
            Box(modifier = Modifier.fillMaxWidth().height(520.dp)) {
                HifzSettingsTab(
                    silhouetteOpacity = silhouetteOpacity, repeatCount = repeatCount, delaySeconds = delaySeconds,
                    loopRange = loopRange, audioSyncReveal = audioSyncReveal, showTranslation = showTranslation,
                    isTajweedEnabled = isTajweedEnabled,
                    arabicFontSize = arabicFontSize, selectedArabicFont = selectedArabicFont, themeColors = themeColors,
                    ayahPattern = ayahPattern, isCustomPatternEnabled = isCustomPatternEnabled, wordRepeatCount = wordRepeatCount,
                    chunkReviewSize = chunkReviewSize, recallGroupSize = recallGroupSize,
                    onToggleCustomPattern = onToggleCustomPattern, onSetAyahPattern = onSetAyahPattern, onSelectWordRepeatCount = onSelectWordRepeatCount,
                    onSelectChunkReviewSize = onSelectChunkReviewSize, onSelectRecallGroupSize = onSelectRecallGroupSize,
                    onSetSilhouetteOpacity = onSetSilhouetteOpacity,
                    onSelectRepeatCount = onSelectRepeatCount, onSelectDelaySeconds = onSelectDelaySeconds,
                    onToggleLoopRange = onToggleLoopRange, onToggleAudioSyncReveal = onToggleAudioSyncReveal,
                    onToggleShowTranslation = onToggleShowTranslation,
                    onToggleTajweed = onToggleTajweed,
                    onOpenTajweedGuide = onOpenTajweedGuide,
                    onSetFontSize = onSetFontSize,
                    onSetArabicFont = onSetArabicFont
                )
            }
        }
    }
}

// =====================================================================
// SURAH & RECITER PICKERS (unchanged, working correctly)
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SurahPickerSheet(
    currentSurah: Surah,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onSelectSurah: (Surah) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) QuranData.surahs
        else QuranData.surahs.filter {
            it.nameEnglish.contains(searchQuery, ignoreCase = true) || it.nameArabic.contains(searchQuery) || it.number.toString().contains(searchQuery)
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = themeColors.surface) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Select Surah for Memorization", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))

            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it },
                placeholder = { Text("Search Surah name or number...", color = themeColors.translationText) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedTextColor = themeColors.arabicText,
                    unfocusedTextColor = themeColors.arabicText,
                    focusedContainerColor = themeColors.background,
                    unfocusedContainerColor = themeColors.background
                ),
                singleLine = true
            )

            LazyColumn(modifier = Modifier.fillMaxWidth().height(380.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(filteredSurahs, key = { it.number }) { s ->
                    val isSelected = s.number == currentSurah.number
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) themeColors.accent.copy(alpha = 0.12f) else themeColors.background,
                        border = null,
                        modifier = Modifier.fillMaxWidth().clickable { onSelectSurah(s) }
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(if (isSelected) themeColors.accent else themeColors.border.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                                    Text("${s.number}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else themeColors.arabicText, fontSize = 11.sp))
                                }
                                Column {
                                    Text(s.nameEnglish, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))
                                    Text("${s.totalVerses} Verses • ${s.revelationType}", style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText, fontSize = 11.sp))
                                }
                            }
                            Text(s.nameArabic, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = if (isSelected) themeColors.accent else themeColors.arabicText))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReciterPickerSheet(
    selectedReciter: Reciter,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onSelectReciter: (Reciter) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = themeColors.surface) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Select Reciter for Drill Recitation", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText))

            LazyColumn(modifier = Modifier.fillMaxWidth().height(340.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(QuranData.reciters, key = { it.id }) { reciter ->
                    val isSelected = reciter.id == selectedReciter.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) themeColors.accent.copy(alpha = 0.12f) else themeColors.background,
                        border = null,
                        modifier = Modifier.fillMaxWidth().clickable { onSelectReciter(reciter) }
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(reciter.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = if (isSelected) themeColors.accent else themeColors.arabicText))
                                Text("${reciter.style} • ${reciter.country}", style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText, fontSize = 11.5.sp))
                            }
                            if (isSelected) Icon(Icons.Default.CheckCircle, null, tint = themeColors.accent, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun findVerseNumberForCharIndex(
    charIndex: Int,
    verseRanges: Map<Int, Pair<Int, Int>>
): Int? {
    if (verseRanges.isEmpty()) return null
    val sorted = verseRanges.entries.sortedBy { it.key }
    for (i in sorted.indices) {
        val (verseNum, range) = sorted[i]
        val nextStart = if (i + 1 < sorted.size) sorted[i + 1].value.first else (range.second + 10)
        if (charIndex >= range.first && charIndex < nextStart) {
            return verseNum
        }
    }
    return sorted.minByOrNull { (_, range) ->
        val mid = (range.first + range.second) / 2
        kotlin.math.abs(charIndex - mid)
    }?.key
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PatternSettingsSheet(
    ayahPattern: String,
    repeatCount: Int,
    isCustomPatternEnabled: Boolean,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onToggleCustomPattern: (Boolean) -> Unit,
    onSetAyahPattern: (String) -> Unit,
    onSelectRepeatCount: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = themeColors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ayah Repetition Pattern",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = themeColors.translationText)
                }
            }

            CustomPatternSelectorCard(
                title = "Ayah Repetition Pattern",
                subtitle = "Define verse grouping sequence (e.g. '3 3 3' plays 3 Ayahs 3 times each)",
                currentPattern = ayahPattern,
                repeatCount = repeatCount,
                isCustomPatternEnabled = isCustomPatternEnabled,
                themeColors = themeColors,
                onToggleCustomPattern = onToggleCustomPattern,
                onPatternChanged = onSetAyahPattern,
                onRepeatCountChanged = onSelectRepeatCount
            )

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JumpSettingsSheet(
    surah: Surah,
    currentAyah: Int,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onSelectAyah: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = themeColors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Jump to Ayah",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText)
                    )
                    Text(
                        text = "Select any verse to start practicing from there",
                        style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = themeColors.translationText)
                }
            }

            val totalVerses = surah.totalVerses.coerceAtLeast(1)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 60.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f, fill = false)
                    .heightIn(max = 320.dp)
            ) {
                items(totalVerses) { index ->
                    val verseNum = index + 1
                    val isSelected = verseNum == currentAyah
                    Surface(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onSelectAyah(verseNum)
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) themeColors.accent else themeColors.surface,
                        border = null
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = verseNum.toString(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else themeColors.arabicText
                                )
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeSettingsSheet(
    surah: Surah,
    startAyah: Int,
    endAyah: Int,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onSetRange: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = themeColors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Customize Practice Range",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.arabicText)
                    )
                    Text(
                        text = "Select targeted Ayah portion to drill",
                        style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = themeColors.translationText)
                }
            }

            val totalVerses = surah.totalVerses.coerceAtLeast(1)
            var currentStart by remember(startAyah) { mutableStateOf(startAyah) }
            var currentEnd by remember(endAyah) { mutableStateOf(endAyah) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AyahDropdownMenu(
                    label = "Start Ayah",
                    selectedAyah = currentStart,
                    totalVerses = totalVerses,
                    modifier = Modifier.weight(1f),
                    themeColors = themeColors,
                    onAyahSelected = { newStart ->
                        currentStart = newStart
                        if (currentEnd < newStart) currentEnd = newStart
                        onSetRange(currentStart, currentEnd)
                    }
                )

                AyahDropdownMenu(
                    label = "End Ayah",
                    selectedAyah = currentEnd,
                    totalVerses = totalVerses,
                    modifier = Modifier.weight(1f),
                    themeColors = themeColors,
                    onAyahSelected = { newEnd ->
                        currentEnd = newEnd
                        if (currentStart > newEnd) currentStart = newEnd
                        onSetRange(currentStart, currentEnd)
                    }
                )
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start Practicing Range (Ayahs $currentStart–$currentEnd)", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}