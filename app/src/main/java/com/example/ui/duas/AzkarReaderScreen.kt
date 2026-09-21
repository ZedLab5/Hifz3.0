package com.example.ui.duas

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DuaItem
import com.example.data.model.QuranArabicFont
import com.example.data.quran.DuaData
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.components.DndReadingEffect
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.GoldTintBgDark
import com.example.ui.theme.GoldTintBgLight
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.PrimaryTealGradient
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.ReadingThemeSection
import com.example.ui.theme.ReadingThemes
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceElevatedLight
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarReaderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedDuaCategory.collectAsStateWithLifecycle()
    DndReadingEffect(viewModel)
    val azkarCountsMap by viewModel.azkarRemainingCounts.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val themeColors = remember(isDarkMode) { if (isDarkMode) ReadingThemes.ObsidianNight else ReadingThemes.MadaniCrisp }

    // Settings preferences from ViewModel
    val showArabicInCards by viewModel.showArabicInAzkarCards.collectAsStateWithLifecycle()
    val azkarTextSize by viewModel.azkarTextSize.collectAsStateWithLifecycle()
    val isAutoScrollEnabled by viewModel.isAzkarAutoScrollEnabled.collectAsStateWithLifecycle()
    val isHapticEnabled by viewModel.isAzkarHapticEnabled.collectAsStateWithLifecycle()
    val showTransliteration by viewModel.showAzkarTransliteration.collectAsStateWithLifecycle()
    val showBenefits by viewModel.showAzkarBenefits.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabicPrimary = appLanguage.equals("Arabic", ignoreCase = true) || appLanguage == "العربية"

    var showSettingsSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Authentic Arabic recitation audio player
    var playingDuaId by remember { mutableStateOf<String?>(null) }
    var isBuffering by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.reset()
                mediaPlayer.release()
            } catch (_: Exception) {}
        }
    }

    val onTogglePlayDua: (DuaItem) -> Unit = { dua ->
        val url = dua.audioUrl
        if (url.isBlank()) {
            Toast.makeText(context, "Authentic audio not available for this item", Toast.LENGTH_SHORT).show()
        } else if (playingDuaId == dua.id) {
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.reset()
            } catch (_: Exception) {}
            playingDuaId = null
            isBuffering = false
        } else {
            try {
                mediaPlayer.reset()
                isBuffering = true
                playingDuaId = dua.id
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                mediaPlayer.setDataSource(url)
                mediaPlayer.setOnPreparedListener { mp ->
                    isBuffering = false
                    mp.start()
                }
                mediaPlayer.setOnCompletionListener {
                    playingDuaId = null
                    isBuffering = false
                }
                mediaPlayer.setOnErrorListener { _, _, _ ->
                    playingDuaId = null
                    isBuffering = false
                    Toast.makeText(context, "Unable to play audio. Check internet connection.", Toast.LENGTH_SHORT).show()
                    true
                }
                mediaPlayer.prepareAsync()
            } catch (e: Exception) {
                playingDuaId = null
                isBuffering = false
                Toast.makeText(context, "Audio error: ${e.localizedMessage ?: "Unknown"}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val currentCategoryModel = remember(selectedCategory) {
        DuaData.categories.firstOrNull { it.id.equals(selectedCategory, ignoreCase = true) }
            ?: DuaData.categories.first()
    }

    val duasList = remember(selectedCategory) {
        DuaData.categorizedDuas.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    val completedCount = remember(duasList, azkarCountsMap) {
        duasList.count { dua -> (azkarCountsMap[dua.id] ?: dua.repeatCount) == 0 }
    }

    val totalCount = duasList.size
    val categoryProgress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    Scaffold(
        topBar = {
            NoorTopBar(
                title = currentCategoryModel.titleEnglish,
                eyebrow = if (currentCategoryModel.titleArabic.isNotBlank()) currentCategoryModel.titleArabic else "HISN AL-MUSLIM",
                subtitle = "$completedCount of $totalCount Completed • Daily Protection",
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back",
                isDark = themeColors.isDark,
                themeColors = themeColors,
                actions = {
                    // Reset counts button
                    NoorGlassIconButton(
                        onClick = { viewModel.resetCategoryDuaCounts(selectedCategory) },
                        icon = Icons.Default.Refresh,
                        contentDescription = "Reset Counts"
                    )

                    // Settings Button
                    NoorGlassIconButton(
                        onClick = { showSettingsSheet = true },
                        icon = Icons.Default.Tune,
                        contentDescription = "Azkar Settings"
                    )
                }
            )
        },
        containerColor = themeColors.background,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Daily Completion Progress Card with Gold Circular Progress Indicator on Right Side
            item(key = "category_progress_banner") {
                AzkarDailyCompletionCard(
                    categoryName = currentCategoryModel.titleEnglish,
                    description = currentCategoryModel.description,
                    completed = completedCount,
                    total = totalCount,
                    progress = categoryProgress,
                    themeColors = themeColors
                )
            }

            // Duas & Azkar Cards
            itemsIndexed(
                items = duasList,
                key = { _, dua -> dua.id }
            ) { index, dua ->
                val remaining = azkarCountsMap[dua.id] ?: dua.repeatCount
                val isCompleted = remaining == 0
                val isBookmarked = favorites.any { it.title == dua.title }

                // Lock logic: card is locked if index > 0 and the previous card is not completed yet
                val isPreviousCompleted = if (index == 0) true else {
                    val prevDua = duasList[index - 1]
                    val prevRemaining = azkarCountsMap[prevDua.id] ?: prevDua.repeatCount
                    prevRemaining == 0
                }
                val isLocked = index > 0 && !isPreviousCompleted

                InteractiveAzkarCard(
                    dua = dua,
                    remainingCount = remaining,
                    isCompleted = isCompleted,
                    isBookmarked = isBookmarked,
                    showArabic = showArabicInCards,
                    textSize = azkarTextSize,
                    showTransliteration = showTransliteration,
                    showBenefits = showBenefits,
                    isArabicPrimary = isArabicPrimary,
                    themeColors = themeColors,
                    isLocked = isLocked,
                    isPlaying = (playingDuaId == dua.id),
                    isBuffering = (isBuffering && playingDuaId == dua.id),
                    onTogglePlay = { onTogglePlayDua(dua) },
                    onTapCount = {
                        if (isLocked) {
                            viewModel.showToast("Please finish the previous Zikr first to unlock this card!")
                        } else {
                            viewModel.decrementDuaCount(dua) {
                                // On completed callback -> Auto-scroll to next Zikr card if enabled
                                if (isAutoScrollEnabled) {
                                    coroutineScope.launch {
                                        delay(280) // Graceful moment to see checkmark state
                                        val nextIndex = index + 1
                                        if (nextIndex < duasList.size) {
                                            // +1 offset for the header progress card at index 0
                                            listState.animateScrollToItem(nextIndex + 1)
                                        }
                                    }
                                }
                            }
                        }
                    },
                    onResetCount = { viewModel.resetDuaCount(dua) },
                    onToggleBookmark = {
                        viewModel.toggleFavorite(
                            itemType = "DUA",
                            title = dua.title,
                            subtitle = if (showArabicInCards) dua.arabicText else dua.translation,
                            details = "${dua.translation}\n\n[${dua.reference}]"
                        )
                    },
                    onCopyDua = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val textToCopy = if (showArabicInCards) {
                            "${dua.title}\n\n${dua.arabicText}\n\n${dua.transliteration}\n\n${dua.translation}\n[${dua.reference}]"
                        } else {
                            "${dua.title}\n\n${dua.transliteration}\n\n${dua.translation}\n[${dua.reference}]"
                        }
                        val clip = ClipData.newPlainText(dua.title, textToCopy)
                        clipboard.setPrimaryClip(clip)
                        viewModel.showToast("Zikr copied to clipboard!")
                    }
                )
            }

            // Footer Navigation (Return to Categories Directory)
            item(key = "footer_nav") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(NoorDestination.DUAS_LIBRARY) },
                    shape = RoundedCornerShape(14.dp),
                    color = themeColors.surface,
                    shadowElevation = if (themeColors.isDark) 0.dp else 0.4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(themeColors.accent.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = themeColors.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "All Du'a Categories",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.arabicText
                                    )
                                )
                                Text(
                                    text = "Browse Hisn al-Muslim library",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeColors.translationText,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    // Azkar Settings Modal Bottom Sheet
    if (showSettingsSheet) {
        AzkarSettingsBottomSheet(
            selectedThemeName = if (isDarkMode) "Obsidian Night" else "Madani Crisp",
            onThemeSelect = { viewModel.setSharedReadingTheme(it) },
            themeColors = themeColors,
            showArabic = showArabicInCards,
            textSize = azkarTextSize,
            isAutoScroll = isAutoScrollEnabled,
            isHaptic = isHapticEnabled,
            showTransliteration = showTransliteration,
            showBenefits = showBenefits,
            categoryName = currentCategoryModel.titleEnglish,
            onDismiss = { showSettingsSheet = false },
            onToggleArabic = { viewModel.setShowArabicInAzkarCards(it) },
            onSelectTextSize = { viewModel.setAzkarTextSize(it) },
            onToggleAutoScroll = { viewModel.setAzkarAutoScroll(it) },
            onToggleHaptic = { viewModel.setAzkarHaptic(it) },
            onToggleTransliteration = { viewModel.setAzkarTransliteration(it) },
            onToggleBenefits = { viewModel.setAzkarBenefits(it) },
            onResetCategory = {
                viewModel.resetCategoryDuaCounts(selectedCategory)
                showSettingsSheet = false
            }
        )
    }
}

/**
 * Daily Completion section with Gold Circular Progress Indicator on the right side.
 * Strictly English content — all Arabic text stripped.
 */
@Composable
fun AzkarDailyCompletionCard(
    categoryName: String,
    description: String,
    completed: Int,
    total: Int,
    progress: Float,
    themeColors: ReadingThemeColors,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "goldCircularProgress"
    )

    // Vibrant green theme palette for selective highlights
    val greenPrimary = themeColors.accent
    val greenAccent = themeColors.accent
    val badgeBg = if (themeColors.isDark) SurfaceElevatedDark else SurfaceElevatedLight

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = themeColors.surface,
        shadowElevation = if (themeColors.isDark) 0.dp else 0.4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column (Clean Description & Metadata)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeBg,
                    border = null
                ) {
                    Text(
                        text = "DAILY COMPLETION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = greenPrimary,
                            fontSize = 10.sp,
                            letterSpacing = 0.6.sp
                        ),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText,
                        fontSize = 17.sp
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(greenAccent)
                    )
                    Text(
                        text = if (completed == total && total > 0) "All Finished ✓" else "$completed of $total Completed",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = themeColors.arabicText,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            // Right Side: Green Circular Progress Indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(72.dp)
            ) {
                // Dark grey track circle to appear more
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(72.dp),
                    color = if (themeColors.isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round
                )

                // Active Arc
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(72.dp),
                    color = greenAccent,
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = themeColors.accent,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "$completed/$total",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.translationText,
                            fontSize = 9.5.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Individual Zikr Card with customizable font size, optional Arabic display,
 * and a prominent, full-width Action Button positioned at the bottom of the card.
 */
@Composable
fun InteractiveAzkarCard(
    dua: DuaItem,
    remainingCount: Int,
    isCompleted: Boolean,
    isBookmarked: Boolean,
    showArabic: Boolean,
    textSize: String,
    showTransliteration: Boolean,
    showBenefits: Boolean,
    isArabicPrimary: Boolean = false,
    themeColors: ReadingThemeColors,
    isPlaying: Boolean = false,
    isBuffering: Boolean = false,
    onTogglePlay: () -> Unit = {},
    onTapCount: () -> Unit,
    onResetCount: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCopyDua: () -> Unit,
    isLocked: Boolean = false,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "actionBtnScale"
    )

    // Dynamic Font Sizes based on user preference
    val translationSize = when (textSize) {
        "Small" -> 14.sp
        "Large" -> 17.5.sp
        "Extra Large" -> 20.sp
        else -> 15.5.sp // Medium
    }
    val translationLineHeight = when (textSize) {
        "Small" -> 21.sp
        "Large" -> 25.sp
        "Extra Large" -> 29.sp
        else -> 23.sp
    }

    val transliterationSize = when (textSize) {
        "Small" -> 12.5.sp
        "Large" -> 15.5.sp
        "Extra Large" -> 17.5.sp
        else -> 14.sp
    }

    val arabicSize = when (textSize) {
        "Small" -> 19.sp
        "Large" -> 25.sp
        "Extra Large" -> 29.sp
        else -> 22.sp
    }
    val arabicLineHeight = when (textSize) {
        "Small" -> 32.sp
        "Large" -> 40.sp
        "Extra Large" -> 46.sp
        else -> 36.sp
    }

    // Clean source text: keep only the authentic reference/occasion on the left
    val cleanSource = remember(dua.reference, dua.occasion) {
        val src = if (dua.reference.isNotBlank()) dua.reference else dua.occasion
        src.replace(Regex("\\(\\s*\\d+\\s*(times|x)\\s*\\)", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\b\\d+\\s*(times|x)\\b", RegexOption.IGNORE_CASE), "")
            .trim()
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = themeColors.surface,
        shadowElevation = if (themeColors.isDark) 0.dp else 0.4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Bar: Source + Authenticity Badge on Left, Play Button + Secondary Actions on Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Source Reference + Authenticity Grade Badge (NO BORDER)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = cleanSource.ifBlank { "Authentic Tradition" },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = themeColors.translationText,
                            fontSize = 12.5.sp,
                            letterSpacing = 0.2.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Authenticity Grade Badge (Sahih / Hasan / Authentic)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = themeColors.accent.copy(alpha = 0.15f),
                        border = null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = themeColors.accent,
                                modifier = Modifier.size(11.5.dp)
                            )
                            Text(
                                text = if (isArabicPrimary) "صحيح" else "Sahih",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.accent,
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }
                }

                // Other side: Authentic Play Button + Copy & Bookmark
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Normal-colored Authentic Recitation Audio Play Button
                    IconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier.size(32.dp)
                    ) {
                        if (isBuffering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(15.dp),
                                strokeWidth = 2.dp,
                                color = themeColors.translationText.copy(alpha = 0.75f)
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Stop Arabic recitation" else "Play authentic Arabic audio",
                                tint = themeColors.translationText.copy(alpha = if (isPlaying) 0.95f else 0.7f),
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onCopyDua,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Zikr",
                            tint = themeColors.translationText.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) MetallicGold else themeColors.translationText.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (!isArabicPrimary) {
                // ============================================================
                // PRIMARY LANGUAGE = ENGLISH (Default / Selected)
                // 1. Primary Container: English Translation in Poppins font
                // ============================================================
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dua.translation,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Normal,
                            color = themeColors.arabicText,
                            fontSize = translationSize,
                            lineHeight = translationLineHeight
                        ),
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
                    )
                }

                // 2. Secondary Layer: Phonetic Transliteration
                if (showTransliteration && dua.transliteration.isNotBlank()) {
                    Text(
                        text = dua.transliteration,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontWeight = FontWeight.Normal,
                            color = themeColors.translationText.copy(alpha = 0.85f),
                            fontSize = transliterationSize,
                            lineHeight = (transliterationSize.value * 1.45f).sp
                        ),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }

                // 3. Tertiary Layer: Arabic Script (Uthmani by default) with soft divider above
                if (showArabic && dua.arabicText.isNotBlank()) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        thickness = 0.75.dp,
                        color = themeColors.border.copy(alpha = 0.5f)
                    )
                    Text(
                        text = dua.arabicText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Normal,
                            fontFamily = QuranArabicFont.UTHMANI.fontFamily,
                            color = themeColors.arabicText,
                            fontSize = arabicSize,
                            lineHeight = arabicLineHeight
                        ),
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                    )
                }
            } else {
                // ============================================================
                // PRIMARY LANGUAGE = ARABIC
                // 1. Primary Container: Arabic Script (Uthmani by default)
                // ============================================================
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dua.arabicText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Normal,
                            fontFamily = QuranArabicFont.UTHMANI.fontFamily,
                            color = themeColors.arabicText,
                            fontSize = arabicSize,
                            lineHeight = arabicLineHeight
                        ),
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
                    )
                }

                // 2. Secondary Layer: Phonetic Transliteration
                if (showTransliteration && dua.transliteration.isNotBlank()) {
                    Text(
                        text = dua.transliteration,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontWeight = FontWeight.Normal,
                            color = themeColors.translationText.copy(alpha = 0.85f),
                            fontSize = transliterationSize,
                            lineHeight = (transliterationSize.value * 1.45f).sp
                        ),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }

                // 3. Tertiary Layer: English Translation
                if (dua.translation.isNotBlank()) {
                    Text(
                        text = dua.translation,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                            color = themeColors.translationText,
                            fontSize = translationSize,
                            lineHeight = translationLineHeight
                        ),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }

            // Spiritual Benefit / Hadith Virtue Note (Simplified, compact & clean)
            if (showBenefits && dua.benefit.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (themeColors.isDark) Color.Transparent else SurfaceElevatedLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Virtue",
                            tint = MetallicGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = dua.benefit,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Normal,
                                color = themeColors.translationText,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // PROMINENT COMPLETION ACTION BUTTON (More rounded pill shape & locks if previous is not done)
            if (isCompleted) {
                val completionBg = themeColors.surface
                val completionBorder = themeColors.accent
                val completionText = themeColors.accent
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = completionBg,
                    border = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(buttonScale)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onResetCount
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = completionText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Completed (${dua.repeatCount}x) • Tap to Reset",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = completionText,
                                fontSize = 13.5.sp
                            )
                        )
                    }
                }
            } else {
                val actionLabel = if (isLocked) {
                    "Complete Previous Zikr First"
                } else if (dua.repeatCount > 1) {
                    "Tap to Count • $remainingCount Remaining of ${dua.repeatCount}x"
                } else {
                    "Mark as Completed (1x)"
                }

                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = if (isLocked) {
                        if (themeColors.isDark) themeColors.surface else CanvasMint
                    } else if (themeColors.isDark) {
                        themeColors.accent
                    } else {
                        DeepVibrantTeal
                    },
                    border = null,
                    shadowElevation = if (isLocked) 0.dp else 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(if (isLocked) 1f else buttonScale)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onTapCount
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isLocked) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = "Tap to Count",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium,
                                color = if (isLocked) {
                                    themeColors.translationText
                                } else {
                                    Color.White
                                },
                                fontSize = 14.sp,
                                letterSpacing = 0.3.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Settings Modal Bottom Sheet for Duas & Azkar.
 * Includes Text Size options, Arabic visibility toggle, Auto-scroll toggle,
 * Haptic feedback toggle, and Transliteration/Virtues controls.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarSettingsBottomSheet(
    selectedThemeName: String,
    onThemeSelect: (String) -> Unit,
    themeColors: ReadingThemeColors,
    showArabic: Boolean,
    textSize: String,
    isAutoScroll: Boolean,
    isHaptic: Boolean,
    showTransliteration: Boolean,
    showBenefits: Boolean,
    categoryName: String,
    onDismiss: () -> Unit,
    onToggleArabic: (Boolean) -> Unit,
    onSelectTextSize: (String) -> Unit,
    onToggleAutoScroll: (Boolean) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    onToggleTransliteration: (Boolean) -> Unit,
    onToggleBenefits: (Boolean) -> Unit,
    onResetCategory: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = themeColors.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(themeColors.border)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (themeColors.isDark) themeColors.border else SoftTealTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Azkar & Du'a Preferences",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText,
                            fontSize = 18.sp
                        )
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = themeColors.translationText
                    )
                }
            }

            HorizontalDivider(color = themeColors.border.copy(alpha = 0.6f))

            // ============================================================
            // 1. TEXT SIZE SELECTOR
            // ============================================================
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = themeColors.accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Text & Typography Size",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText
                        )
                    )
                }

                val sizes = listOf("Small", "Medium", "Large", "Extra Large")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sizes.forEach { sizeOption ->
                        val isSelected = textSize == sizeOption
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) themeColors.accent else (if (themeColors.isDark) themeColors.border else SoftTealTint),
                            border = null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectTextSize(sizeOption) }
                        ) {
                            Text(
                                text = if (sizeOption == "Extra Large") "XL" else sizeOption,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else themeColors.arabicText,
                                    fontSize = 11.5.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = themeColors.border.copy(alpha = 0.6f))

            // ============================================================
            // 2. CONTENT & LANGUAGE PREFERENCES
            // ============================================================
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Content Display",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.translationText,
                        fontSize = 12.sp
                    )
                )

                // Show Arabic in Cards Toggle
                SettingsSwitchRow(
                    title = "Show Arabic Text in Cards",
                    subtitle = "Display original Arabic script inside Zikr cards",
                    checked = showArabic,
                    themeColors = themeColors,
                    onCheckedChange = onToggleArabic
                )

                // Show Transliteration Toggle
                SettingsSwitchRow(
                    title = "Phonetic Transliteration",
                    subtitle = "Assist with accurate English pronunciation",
                    checked = showTransliteration,
                    themeColors = themeColors,
                    onCheckedChange = onToggleTransliteration
                )

                // Show Spiritual Virtues Toggle
                SettingsSwitchRow(
                    title = "Spiritual Virtues & Hadith",
                    subtitle = "Display authentic references and rewards",
                    checked = showBenefits,
                    themeColors = themeColors,
                    onCheckedChange = onToggleBenefits
                )
            }

            HorizontalDivider(color = themeColors.border.copy(alpha = 0.6f))

            // ============================================================
            // 3. BEHAVIOR & INTERACTION PREFERENCES
            // ============================================================
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Behavior & Feedback",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.translationText,
                        fontSize = 12.sp
                    )
                )

                // Auto-Scroll Toggle
                SettingsSwitchRow(
                    title = "Auto-Scroll on Completion",
                    subtitle = "Smoothly advance to the next card when count is complete",
                    checked = isAutoScroll,
                    themeColors = themeColors,
                    onCheckedChange = onToggleAutoScroll
                )

                // Haptic Feedback Toggle
                SettingsSwitchRow(
                    title = "Vibration & Haptic Feedback",
                    subtitle = "Gentle vibration on each tap and completion",
                    checked = isHaptic,
                    themeColors = themeColors,
                    onCheckedChange = onToggleHaptic
                )
            }

            HorizontalDivider(color = themeColors.border.copy(alpha = 0.6f))

            // ============================================================
            // 4. RESET ACTIONS
            // ============================================================
            OutlinedButton(
                onClick = onResetCategory,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (themeColors.isDark) themeColors.surface else CanvasMint,
                    contentColor = themeColors.arabicText
                ),
                border = null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = themeColors.arabicText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reset All Counts in $categoryName",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    themeColors: ReadingThemeColors,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = themeColors.arabicText,
                    fontSize = 14.5.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeColors.translationText,
                    fontSize = 12.sp
                )
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = themeColors.accent,
                uncheckedThumbColor = themeColors.translationText,
                uncheckedTrackColor = if (themeColors.isDark) themeColors.border else SoftTealTint
            )
        )
    }
}
