package com.example.ui.quran

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.QuranArabicFont
import com.example.data.model.Surah
import com.example.data.model.Verse
import com.example.data.repository.NoorRepository
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.theme.MetallicGold

val AmiriQuranFontFamily = FontFamily(
    Font(R.font.amiri_quran, FontWeight.Normal)
)

/**
 * Helper to return the standard numeric representation for the verse number.
 */
fun toArabicIndic(number: Int): String {
    return number.toString()
}

/**
 * Ayah End Marker Badge:
 * Draws a uniform, perfectly round double-circle ornament with the verse number
 * centered inside it. If a note exists for this verse, a subtle gold indicator dot is drawn.
 */
@Composable
fun AyahEndMarkerBadge(
    verseNumber: Int,
    circleDiameterDp: Dp,
    themeColors: QuranReadingThemeColors,
    hasNote: Boolean = false,
    modifier: Modifier = Modifier
) {
    val digitCount = verseNumber.toString().length
    val numeralFontSize = when {
        digitCount <= 1 -> (circleDiameterDp.value * 0.44f).sp
        digitCount == 2 -> (circleDiameterDp.value * 0.38f).sp
        else -> (circleDiameterDp.value * 0.30f).sp
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(circleDiameterDp)
                .offset(y = 1.3.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val outerRadius = (size.minDimension / 2f) - 1.2.dp.toPx()

                // 1. Subtle warm background glow
                drawCircle(
                    color = if (hasNote) MetallicGold.copy(alpha = 0.25f) else themeColors.accent.copy(alpha = 0.09f),
                    radius = outerRadius,
                    center = center
                )

                // 2. Outer ornate circle ring
                drawCircle(
                    color = if (hasNote) MetallicGold else themeColors.accent.copy(alpha = 0.85f),
                    radius = outerRadius,
                    center = center,
                    style = Stroke(width = if (hasNote) 1.6.dp.toPx() else 1.2.dp.toPx())
                )

                // 3. Ornate 8-point geometric cardinal & diagonal accent points
                val markerRadius = outerRadius * 0.96f
                for (i in 0 until 8) {
                    val angleRad = (i * 45.0 * Math.PI / 180.0).toFloat()
                    val px = center.x + markerRadius * kotlin.math.cos(angleRad)
                    val py = center.y + markerRadius * kotlin.math.sin(angleRad)
                    val dotSize = if (i % 2 == 0) 1.2.dp.toPx() else 0.8.dp.toPx()
                    drawCircle(
                        color = if (hasNote) MetallicGold else themeColors.accent,
                        radius = dotSize,
                        center = Offset(px, py)
                    )
                }

                // 4. Inner delicate framing ring
                val innerRadius = (outerRadius - 2.4.dp.toPx()).coerceAtLeast(1f)
                drawCircle(
                    color = themeColors.accent.copy(alpha = 0.40f),
                    radius = innerRadius,
                    center = center,
                    style = Stroke(width = 0.7.dp.toPx())
                )

                // 5. Persistent note indicator badge dot
                if (hasNote) {
                    drawCircle(
                        color = Color(0xFFD97706),
                        radius = 2.4.dp.toPx(),
                        center = Offset(center.x, center.y + outerRadius - 1.dp.toPx())
                    )
                }
            }

            Text(
                text = verseNumber.toString(),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    platformStyle = @Suppress("DEPRECATION") PlatformTextStyle(
                        includeFontPadding = false
                    ),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    ),
                    fontSize = numeralFontSize,
                    fontWeight = FontWeight.Bold,
                    color = if (hasNote) MetallicGold else themeColors.accent,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.wrapContentSize(Alignment.Center)
            )
        }
    }
}

@Composable
fun rememberShimmerSweepBrush(
    baseColor: Color,
    highlightColor: Color
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnim by transition.animateFloat(
        initialValue = -500f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    return Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 400f, 0f)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MaskedVerseSkeletonFlow(
    verse: Verse,
    fontSizeSp: Int,
    themeColors: QuranReadingThemeColors,
    circleDiameterDp: Dp,
    hasNote: Boolean,
    shimmerBrush: Brush,
    onVerseClick: () -> Unit,
    onVerseLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val words = remember(verse.arabicText) {
        verse.arabicText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    }
    val pillHeight = (fontSizeSp * 0.58f).dp
    val charUnitWidth = (fontSizeSp * 0.44f).dp

    FlowRow(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .pointerInput(verse.verseNumber) {
                detectTapGestures(
                    onTap = { onVerseClick() },
                    onLongPress = { onVerseLongPress() }
                )
            }
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
    ) {
        words.forEach { word ->
            val wordCharCount = word.length.coerceIn(2, 14)
            val pillWidth = (wordCharCount * charUnitWidth.value).coerceIn((fontSizeSp * 0.9f), (fontSizeSp * 4.5f)).dp

            Box(
                modifier = Modifier
                    .size(width = pillWidth, height = pillHeight)
                    .clip(RoundedCornerShape(6.dp))
                    .background(shimmerBrush)
            )
        }

        Box(
            modifier = Modifier
                .size(circleDiameterDp)
                .offset(y = 1.3.dp)
        ) {
            AyahEndMarkerBadge(
                verseNumber = verse.verseNumber,
                circleDiameterDp = circleDiameterDp,
                themeColors = themeColors,
                hasNote = hasNote
            )
        }
    }
}

@Composable
private fun MushafChunkItem(
    surah: Surah,
    chunk: List<Verse>,
    isTajweedEnabled: Boolean,
    themeColors: QuranReadingThemeColors,
    arabicFont: QuranArabicFont,
    fontSizeSp: Int,
    circleDiameterDp: Dp,
    markerSizeSp: androidx.compose.ui.unit.TextUnit,
    isPlaying: Boolean,
    currentPlayingVerse: Int,
    briefHighlightAyah: Int,
    highlightGreenAnimValue: Float,
    notesMap: Map<Int, String>,
    onVerseLongPress: (Verse) -> Unit,
    onVerseClick: (Verse) -> Unit,
    modifier: Modifier = Modifier
) {
    if (chunk.isEmpty()) return

    val startAyah = chunk.first().verseNumber
    val endAyah = chunk.last().verseNumber

    val chunkData = remember(
        surah.number,
        startAyah,
        endAyah,
        isTajweedEnabled,
        themeColors.isDark,
        themeColors.name == "Sepia Parchment"
    ) {
        MushafTextCache.getOrCreateRange(
            surah = surah,
            startAyah = startAyah,
            endAyah = endAyah,
            isTajweedEnabled = isTajweedEnabled,
            themeColors = themeColors
        )
    }

    val verseRanges = chunkData.verseRanges

    val annotatedText = remember(
        chunkData,
        isPlaying,
        currentPlayingVerse,
        notesMap,
        themeColors.accent,
        themeColors.arabicText,
        briefHighlightAyah,
        highlightGreenAnimValue
    ) {
        val hasHighlight = (briefHighlightAyah in startAyah..endAyah && highlightGreenAnimValue > 0f)
        val hasPlaying = (isPlaying && currentPlayingVerse in startAyah..endAyah)
        val hasNotes = chunk.any { notesMap[it.verseNumber]?.isNotBlank() == true }

        if (!hasHighlight && !hasPlaying && !hasNotes) {
            chunkData.baseAnnotatedString
        } else {
            val builder = AnnotatedString.Builder(chunkData.baseAnnotatedString)
            if (hasHighlight) {
                val range = verseRanges[briefHighlightAyah]
                if (range != null) {
                    val greenColor = lerp(themeColors.arabicText, Color(0xFF10B981), highlightGreenAnimValue)
                    builder.addStyle(
                        style = SpanStyle(color = greenColor),
                        start = range.first,
                        end = range.second
                    )
                }
            }
            if (hasPlaying) {
                val range = verseRanges[currentPlayingVerse]
                if (range != null) {
                    builder.addStyle(
                        style = SpanStyle(
                            color = themeColors.accent,
                            fontWeight = FontWeight.SemiBold
                        ),
                        start = range.first,
                        end = range.second
                    )
                }
            }
            if (hasNotes) {
                for (verse in chunk) {
                    val noteText = notesMap[verse.verseNumber]
                    if (!noteText.isNullOrBlank()) {
                        val range = verseRanges[verse.verseNumber]
                        if (range != null) {
                            builder.addStyle(
                                style = SpanStyle(
                                    textDecoration = TextDecoration.Underline,
                                    color = themeColors.accent
                                ),
                                start = range.first,
                                end = range.second
                            )
                        }
                    }
                }
            }
            builder.toAnnotatedString()
        }
    }

    val inlineContentMap = remember(chunk, fontSizeSp, themeColors, circleDiameterDp, markerSizeSp, notesMap) {
        chunk.associate { verse ->
            val markerId = "marker_${verse.verseNumber}"
            val hasNote = notesMap[verse.verseNumber]?.isNotBlank() == true
            markerId to InlineTextContent(
                Placeholder(
                    width = markerSizeSp,
                    height = markerSizeSp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                AyahEndMarkerBadge(
                    verseNumber = verse.verseNumber,
                    circleDiameterDp = circleDiameterDp,
                    themeColors = themeColors,
                    hasNote = hasNote
                )
            }
        }
    }

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
            .pointerInput(chunk) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        val layout = textLayoutResult
                        if (layout != null) {
                            val charIndex = layout.getOffsetForPosition(tapOffset)
                            val verseNum = verseRanges.entries.firstOrNull { (_, range) ->
                                charIndex >= range.first && charIndex <= range.second + 4
                            }?.key
                            if (verseNum != null) {
                                val targetVerse = chunk.firstOrNull { it.verseNumber == verseNum }
                                if (targetVerse != null) {
                                    onVerseClick(targetVerse)
                                }
                            }
                        }
                    },
                    onLongPress = { tapOffset ->
                        val layout = textLayoutResult
                        if (layout != null) {
                            val charIndex = layout.getOffsetForPosition(tapOffset)
                            val verseNum = verseRanges.entries.firstOrNull { (_, range) ->
                                charIndex >= range.first && charIndex <= range.second + 4
                            }?.key
                            if (verseNum != null) {
                                val targetVerse = chunk.firstOrNull { it.verseNumber == verseNum }
                                if (targetVerse != null) {
                                    onVerseLongPress(targetVerse)
                                }
                            }
                        }
                    }
                )
            }
    ) {
        Text(
            text = annotatedText,
            inlineContent = inlineContentMap,
            onTextLayout = { textLayoutResult = it },
            style = TextStyle(
                fontFamily = arabicFont.fontFamily,
                fontSize = fontSizeSp.sp,
                lineHeight = (fontSizeSp * 2.1).sp,
                fontWeight = FontWeight.Normal,
                color = themeColors.arabicText,
                textAlign = TextAlign.Center,
                textDirection = TextDirection.Rtl
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RevealedVerseFlow(
    verse: Verse,
    arabicFont: QuranArabicFont,
    fontSizeSp: Int,
    themeColors: QuranReadingThemeColors,
    circleDiameterDp: Dp,
    markerSizeSp: androidx.compose.ui.unit.TextUnit,
    hasNote: Boolean,
    isCurrentlyPlaying: Boolean,
    onVerseClick: () -> Unit,
    onVerseLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inlineMarkerId = "marker_${verse.verseNumber}"
    val annotatedVerseText = remember(verse.arabicText, isCurrentlyPlaying, hasNote, themeColors) {
        buildAnnotatedString {
            val startIdx = length
            append(verse.arabicText)
            append(" ")
            if (isCurrentlyPlaying) {
                addStyle(
                    style = SpanStyle(
                        color = themeColors.accent,
                        fontWeight = FontWeight.SemiBold
                    ),
                    start = startIdx,
                    end = length
                )
            }
            if (hasNote) {
                addStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        color = themeColors.accent
                    ),
                    start = startIdx,
                    end = length
                )
            }
            appendInlineContent(inlineMarkerId, " ")
        }
    }

    val inlineMap = remember(verse.verseNumber, circleDiameterDp, markerSizeSp, themeColors, hasNote) {
        mapOf(
            inlineMarkerId to InlineTextContent(
                Placeholder(
                    width = markerSizeSp,
                    height = markerSizeSp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                AyahEndMarkerBadge(
                    verseNumber = verse.verseNumber,
                    circleDiameterDp = circleDiameterDp,
                    themeColors = themeColors,
                    hasNote = hasNote
                )
            }
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isCurrentlyPlaying) themeColors.accent.copy(alpha = 0.12f) else Color.Transparent)
            .pointerInput(verse.verseNumber) {
                detectTapGestures(
                    onTap = { onVerseClick() },
                    onLongPress = { onVerseLongPress() }
                )
            }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = annotatedVerseText,
            inlineContent = inlineMap,
            style = TextStyle(
                fontFamily = arabicFont.fontFamily,
                fontSize = fontSizeSp.sp,
                lineHeight = (fontSizeSp * 2.1).sp,
                fontWeight = if (isCurrentlyPlaying) FontWeight.SemiBold else FontWeight.Normal,
                color = themeColors.arabicText,
                textAlign = TextAlign.Center,
                textDirection = TextDirection.Rtl
            )
        )
    }
}

/**
 * Continuous Reading Mode:
 * Renders all ayahs of the surah as one uninterrupted, justified paragraph of Arabic text
 * in the user's selected Arabic calligraphy style with manual inline Ayah End Marker badges.
 * Supports active audio ayah highlighting, persistent note markers, and progressive reveal memorization mode.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MushafFlowView(
    surah: Surah,
    fontSizeSp: Int,
    arabicFont: QuranArabicFont = QuranArabicFont.AMIRI,
    themeColors: QuranReadingThemeColors,
    isPlaying: Boolean = false,
    currentPlayingVerse: Int = 0,
    isAudioDisabled: Boolean = false,
    viewModel: MainViewModel,
    notesMap: Map<Int, String> = emptyMap(),
    onOpenNoteForVerse: (Verse) -> Unit = {},
    onToggleBookmarkForVerse: (Verse) -> Unit = {},
    onPlayVerse: (Verse) -> Unit = {},
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    isFullscreenMode: Boolean = false
) {
    val verses = surah.verses
    val density = LocalDensity.current

    val isHideUnreadVerses by viewModel.isHideUnreadVersesEnabled.collectAsStateWithLifecycle()
    val revealedVerses by viewModel.revealedVersesInSession.collectAsStateWithLifecycle()
    val memorizationRepeatCount by viewModel.memorizationRepeatCount.collectAsStateWithLifecycle()
    val memorizationCurrentRepetition by viewModel.memorizationCurrentRepetition.collectAsStateWithLifecycle()
    val isTajweedEnabled by viewModel.isTajweedEnabled.collectAsStateWithLifecycle()

    val shimmerBrush = rememberShimmerSweepBrush(
        baseColor = themeColors.accent.copy(alpha = 0.12f),
        highlightColor = themeColors.accent.copy(alpha = 0.30f)
    )

    var contextMenuVerse by remember { mutableStateOf<Verse?>(null) }
    var briefHighlightAyah by remember { mutableIntStateOf(0) }
    val highlightGreenAnim = remember { Animatable(0f) }

    val quranScrollRequest by viewModel.quranScrollRequest.collectAsStateWithLifecycle()
    val readingProgress by viewModel.readingProgress.collectAsStateWithLifecycle()
    val targetAyah by viewModel.targetAyahToScrollTo.collectAsStateWithLifecycle()

    // Consistent circle dimensions scaled smoothly with font size
    val circleDiameterDp = (fontSizeSp * 0.88f).dp
    val markerSizeSp = (fontSizeSp * 0.88f).sp

    // Retrieve cached base AnnotatedString and verse ranges instantly or load asynchronously with a loading state
    val isAlreadyCached = remember(surah.number, isTajweedEnabled, themeColors.isDark, themeColors.name == "Sepia Parchment") {
        MushafTextCache.hasKey(surah.number, isTajweedEnabled, themeColors)
    }

    var cachedDataState by remember(surah.number, isTajweedEnabled, themeColors.isDark, themeColors.name == "Sepia Parchment") {
        if (isAlreadyCached) {
            mutableStateOf<CachedMushafData?>(MushafTextCache.getOrCreate(surah, isTajweedEnabled, themeColors))
        } else {
            mutableStateOf<CachedMushafData?>(null)
        }
    }

    LaunchedEffect(surah.number, isTajweedEnabled, themeColors.isDark, themeColors.name == "Sepia Parchment") {
        if (cachedDataState == null) {
            val data = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                MushafTextCache.getOrCreate(surah, isTajweedEnabled, themeColors)
            }
            cachedDataState = data
        }
    }

    val cachedData = cachedDataState

    if (cachedData == null) {
        val loadColors = if (!themeColors.isDark && themeColors.name != "Sepia Parchment") {
            themeColors.copy(background = Color.White)
        } else {
            themeColors
        }

        val loadingShimmer = rememberShimmerSweepBrush(
            baseColor = themeColors.accent.copy(alpha = 0.55f),
            highlightColor = MetallicGold
        )
        val infiniteTransition = rememberInfiniteTransition(label = "islamic_star_loading")
        val starRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 10000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "star_rotation"
        )
        val starPulse by infiniteTransition.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "star_pulse"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(loadColors.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Canvas(
                    modifier = Modifier.size(64.dp)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val starSize = size.minDimension * 0.56f * starPulse
                    val half = starSize / 2f
                    val cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())

                    rotate(degrees = starRotation, pivot = center) {
                        // First rounded square of the 8-point star
                        drawRoundRect(
                            brush = loadingShimmer,
                            topLeft = Offset(center.x - half, center.y - half),
                            size = androidx.compose.ui.geometry.Size(starSize, starSize),
                            cornerRadius = cornerRadius,
                            style = Stroke(width = 2.2.dp.toPx())
                        )

                        // Second square rotated 45 degrees
                        rotate(degrees = 45f, pivot = center) {
                            drawRoundRect(
                                brush = loadingShimmer,
                                topLeft = Offset(center.x - half, center.y - half),
                                size = androidx.compose.ui.geometry.Size(starSize, starSize),
                                cornerRadius = cornerRadius,
                                style = Stroke(width = 2.2.dp.toPx())
                            )
                        }

                        // Inner geometric rosette circle
                        drawCircle(
                            brush = loadingShimmer,
                            radius = size.minDimension * 0.16f * starPulse,
                            center = center,
                            style = Stroke(width = 1.6.dp.toPx())
                        )

                        // Central golden focal dot
                        drawCircle(
                            color = MetallicGold,
                            radius = 3.2.dp.toPx(),
                            center = center
                        )
                    }
                }

                Text(
                    text = "Preparing your Tajweed text…",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = themeColors.translationText,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.3.sp
                    )
                )
            }
        }
        return
    }

    val chunks = remember(surah.verses) {
        if (surah.verses.isNotEmpty()) surah.verses.chunked(12) else emptyList()
    }

    // Calculate header items count preceding the verse chunks in the LazyColumn
    val headerCount = 1 + (if (isAudioDisabled) 1 else 0) + (if (surah.number != 9) 1 else 0)

    // Auto-scroll when active playing ayah changes in Reading Mode
    LaunchedEffect(currentPlayingVerse, isPlaying, surah.number) {
        if (isPlaying) {
            if (currentPlayingVerse == 0) {
                lazyListState.animateScrollToItem(0)
            } else if (currentPlayingVerse > 0) {
                val chunkIndex = chunks.indexOfFirst { chunk ->
                    chunk.any { it.verseNumber == currentPlayingVerse }
                }
                if (chunkIndex != -1) {
                    lazyListState.animateScrollToItem(headerCount + chunkIndex)
                }
            }
        }
    }

    // Scroll to target bookmarked ayah or reset scroll to top when opening a surah
    LaunchedEffect(quranScrollRequest?.id, surah.number) {
        val req = quranScrollRequest
        val bookmark = readingProgress
        val effectiveTarget = if (req != null && req.surahNumber == surah.number) {
            req.targetAyah
        } else if (bookmark != null && bookmark.surahNumber == surah.number) {
            bookmark.ayahNumber
        } else {
            0
        }

        try {
            if (effectiveTarget > 0) {
                val chunkIndex = chunks.indexOfFirst { chunk ->
                    chunk.any { it.verseNumber == effectiveTarget }
                }
                if (chunkIndex != -1) {
                    lazyListState.animateScrollToItem(headerCount + chunkIndex)
                }

                briefHighlightAyah = effectiveTarget
                val pulseEasing = FastOutSlowInEasing
                repeat(3) {
                    highlightGreenAnim.animateTo(1f, tween(300, easing = pulseEasing))
                    highlightGreenAnim.animateTo(0f, tween(300, easing = pulseEasing))
                }
            } else {
                lazyListState.scrollToItem(0)
            }
        } finally {
            briefHighlightAyah = 0
            highlightGreenAnim.snapTo(0f)
        }
    }

    val effectiveThemeColors = if (!themeColors.isDark && themeColors.name != "Sepia Parchment") {
        themeColors.copy(background = Color.White)
    } else {
        themeColors
    }

    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 104.dp
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 76.dp

    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .fillMaxSize()
            .background(effectiveThemeColors.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = topPadding, bottom = bottomPadding)
    ) {
        // 1. Surah Header Banner
        item(key = "surah_header") {
            SurahHeaderBanner(
                surah = surah,
                themeColors = effectiveThemeColors,
                onPreviousSurah = { viewModel.openPreviousSurah() },
                onNextSurah = { viewModel.openNextSurah() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isAudioDisabled) {
            item(key = "mp3_notice") {
                Mp3PlaybackActiveNotice()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // 2. Bismillah Header
        if (surah.number != 9) {
            item(key = "bismillah_banner") {
                BismillahBannerCard(
                    themeColors = themeColors,
                    arabicFont = arabicFont,
                    isActive = isPlaying && currentPlayingVerse == 0
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // 3. Verses Rendering with Long-Press Detection and Memorization Progressive Reveal
        if (verses.isEmpty()) {
            item(key = "empty_verses") {
                QuranEmptyVersesCard(
                    surah = surah,
                    themeColors = themeColors,
                    viewModel = viewModel,
                    onRetry = { viewModel.reloadCurrentSurah() }
                )
            }
        } else if (isHideUnreadVerses) {
            // Memorization Mode Banner
            item(key = "memorization_banner") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = themeColors.accent.copy(alpha = 0.08f),
                    border = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(themeColors.accent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = themeColors.accent,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Memorization Progressive Reveal",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.arabicText,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = if (memorizationRepeatCount > 1 && isPlaying && currentPlayingVerse > 0) {
                                        "Ayah $currentPlayingVerse • Repetition $memorizationCurrentRepetition of $memorizationRepeatCount"
                                    } else {
                                        "Verses reveal progressively as recitation plays"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeColors.accent,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = themeColors.surface,
                                border = null,
                                modifier = Modifier.clickable { viewModel.resetRevealedVersesInSession() }
                            ) {
                                Text(
                                    text = "Reset",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = themeColors.translationText,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = themeColors.accent,
                                modifier = Modifier.clickable { viewModel.revealAllVersesInSession() }
                            ) {
                                Text(
                                    text = "Reveal All",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Progressive Reveal Flow
            item(key = "memorization_flow") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
                        ) {
                            verses.forEach { verse ->
                                val isRevealed = revealedVerses.contains(verse.verseNumber)
                                val isCurrentlyPlaying = isPlaying && currentPlayingVerse == verse.verseNumber
                                val hasNote = notesMap[verse.verseNumber]?.isNotBlank() == true

                                AnimatedContent(
                                    targetState = isRevealed,
                                    transitionSpec = {
                                        (fadeIn(animationSpec = tween(450)) + scaleIn(initialScale = 0.96f, animationSpec = tween(450)))
                                            .togetherWith(fadeOut(animationSpec = tween(250)))
                                    },
                                    label = "verse_reveal_${verse.verseNumber}"
                                ) { revealed ->
                                    if (revealed) {
                                        RevealedVerseFlow(
                                            verse = verse,
                                            arabicFont = arabicFont,
                                            fontSizeSp = fontSizeSp,
                                            themeColors = themeColors,
                                            circleDiameterDp = circleDiameterDp,
                                            markerSizeSp = markerSizeSp,
                                            hasNote = hasNote,
                                            isCurrentlyPlaying = isCurrentlyPlaying,
                                            onVerseClick = { onPlayVerse(verse) },
                                            onVerseLongPress = {
                                                viewModel.triggerHaptic()
                                                contextMenuVerse = verse
                                            }
                                        )
                                    } else {
                                        MaskedVerseSkeletonFlow(
                                            verse = verse,
                                            fontSizeSp = fontSizeSp,
                                            themeColors = themeColors,
                                            circleDiameterDp = circleDiameterDp,
                                            hasNote = hasNote,
                                            shimmerBrush = shimmerBrush,
                                            onVerseClick = {
                                                viewModel.triggerHaptic()
                                                onPlayVerse(verse)
                                            },
                                            onVerseLongPress = {
                                                viewModel.triggerHaptic()
                                                contextMenuVerse = verse
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Chunks
            items(
                count = chunks.size,
                key = { index -> "chunk_${surah.number}_$index" }
            ) { chunkIndex ->
                val chunk = chunks[chunkIndex]
                MushafChunkItem(
                    surah = surah,
                    chunk = chunk,
                    isTajweedEnabled = isTajweedEnabled,
                    themeColors = themeColors,
                    arabicFont = arabicFont,
                    fontSizeSp = fontSizeSp,
                    circleDiameterDp = circleDiameterDp,
                    markerSizeSp = markerSizeSp,
                    isPlaying = isPlaying,
                    currentPlayingVerse = currentPlayingVerse,
                    briefHighlightAyah = briefHighlightAyah,
                    highlightGreenAnimValue = highlightGreenAnim.value,
                    notesMap = notesMap,
                    onVerseLongPress = { verse ->
                        viewModel.triggerHaptic()
                        contextMenuVerse = verse
                    },
                    onVerseClick = { verse ->
                        onPlayVerse(verse)
                    }
                )
            }
        }

        // 4. Surah Navigation Footer
        item(key = "surah_footer") {
            Spacer(modifier = Modifier.height(16.dp))
            SurahNavigationFooter(
                currentSurah = surah,
                themeColors = themeColors,
                onPrevious = { viewModel.openPreviousSurah() },
                onNext = { viewModel.openNextSurah() },
                onOpenList = { viewModel.navigateTo(NoorDestination.QURAN_SURAH_LIST) }
            )
        }
    }

    // Contextual Long-Press Popup Sheet
    if (contextMenuVerse != null) {
        val targetVerse = contextMenuVerse!!
        val hasExistingNote = notesMap[targetVerse.verseNumber]?.isNotBlank() == true

        ModalBottomSheet(
            onDismissRequest = { contextMenuVerse = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = themeColors.surface,
            contentColor = themeColors.arabicText
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Surah ${surah.nameEnglish} • Ayah ${targetVerse.verseNumber}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText
                            )
                        )
                        Text(
                            text = "Contextual Verse Options",
                            style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                        )
                    }

                    IconButton(onClick = { contextMenuVerse = null }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = themeColors.translationText
                        )
                    }
                }

                HorizontalDivider(color = themeColors.border.copy(alpha = 0.5f))

                // Action 1: Save Bookmark
                Surface(
                    onClick = {
                        onToggleBookmarkForVerse(targetVerse)
                        contextMenuVerse = null
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = themeColors.background,
                    border = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = MetallicGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bookmark Ayah",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText
                                )
                            )
                            Text(
                                text = "Save exact reading bookmark position here",
                                style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                            )
                        }
                    }
                }

                // Action 2: Play Recitation
                Surface(
                    onClick = {
                        onPlayVerse(targetVerse)
                        contextMenuVerse = null
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = themeColors.background,
                    border = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Play Recitation",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText
                                )
                            )
                            Text(
                                text = "Listen to recitation for this verse",
                                style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                            )
                        }
                    }
                }

                // Action 3: Add / Edit Note
                Surface(
                    onClick = {
                        val verseToNote = targetVerse
                        contextMenuVerse = null
                        onOpenNoteForVerse(verseToNote)
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (hasExistingNote) themeColors.accent.copy(alpha = 0.12f) else themeColors.background,
                    border = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier.size(22.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (hasExistingNote) "Edit Note" else "Add Note",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText
                                )
                            )
                            Text(
                                text = if (hasExistingNote) "View or edit saved note for this verse" else "Attach a study reflection or personal note",
                                style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
