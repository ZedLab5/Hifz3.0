package com.example.ui.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.ReadingThemes

enum class HifzLevelPreset(
    val title: String,
    val subtitle: String,
    val badge: String,
    val defaultBatchSize: Int,
    val repeatCount: Int,
    val pattern: String,
    val delaySeconds: Int,
    val description: String,
    val icon: ImageVector
) {
    BEGINNER(
        title = "Beginner",
        subtitle = "Gentle pace & high repetition",
        badge = "🌱 Starter",
        defaultBatchSize = 5,
        repeatCount = 5,
        pattern = "5, 5, 5",
        delaySeconds = 3,
        description = "5 ayahs • 5x repeats • 3s pause",
        icon = Icons.Default.School
    ),
    INTERMEDIATE(
        title = "Intermediate",
        subtitle = "Balanced drill & active recall",
        badge = "⚡ Balanced",
        defaultBatchSize = 7,
        repeatCount = 3,
        pattern = "3, 3, 3",
        delaySeconds = 2,
        description = "7 ayahs • 3x repeats • 2s pause",
        icon = Icons.Default.AutoAwesome
    ),
    ADVANCED(
        title = "Advanced",
        subtitle = "Fast chunks for revision & huffaz",
        badge = "🏆 Mastery",
        defaultBatchSize = 10,
        repeatCount = 2,
        pattern = "2, 2, 2",
        delaySeconds = 1,
        description = "10 ayahs • 2x repeats • 1s pause",
        icon = Icons.Default.Psychology
    )
}

@Composable
fun QuranMemorizationSetupScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val surah by viewModel.memorizationSurah.collectAsStateWithLifecycle()
    val startAyah by viewModel.memorizationStartAyah.collectAsStateWithLifecycle()
    val endAyah by viewModel.memorizationEndAyah.collectAsStateWithLifecycle()
    val repeatCount by viewModel.memorizationRepeatCount.collectAsStateWithLifecycle()
    val ayahPattern by viewModel.memorizationAyahPattern.collectAsStateWithLifecycle()
    val delaySeconds by viewModel.memorizationDelaySeconds.collectAsStateWithLifecycle()
    val loopRange by viewModel.memorizationLoopRange.collectAsStateWithLifecycle()
    val audioSyncReveal by viewModel.memorizationAudioSyncReveal.collectAsStateWithLifecycle()
    val showTranslation by viewModel.memorizationShowTranslation.collectAsStateWithLifecycle()
    val memorizedSet by viewModel.memorizedAyahsSet.collectAsStateWithLifecycle()
    val selectedReciter by viewModel.selectedReciter.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val activeTab by viewModel.memorizationStudioTab.collectAsStateWithLifecycle()
    val silhouetteOpacity by viewModel.hifzSilhouetteOpacity.collectAsStateWithLifecycle()
    val recallGroupSize by viewModel.recallGroupSize.collectAsStateWithLifecycle()
    val historyLogs by viewModel.hifzSessionLogs.collectAsStateWithLifecycle()

    val themeColors = remember(isDarkMode) {
        if (isDarkMode) ReadingThemes.ObsidianNight else ReadingThemes.MadaniCrisp
    }

    var showSurahPicker by remember { mutableStateOf(false) }
    var showReciterPicker by remember { mutableStateOf(false) }
    var showDirectAyahInputDialog by remember { mutableStateOf<Pair<String, Int>?>(null) } // "FROM" or "TO" -> value
    var showDrillExplanationSheet by remember { mutableStateOf(false) }
    var showRecommendationsSheetGroup by remember { mutableStateOf<Int?>(null) }
    var activePreset by remember { mutableStateOf<HifzLevelPreset?>(HifzLevelPreset.INTERMEDIATE) }

    val batchSize = (endAyah - startAyah + 1).coerceAtLeast(1)
    val memorizedCountInSurah = remember(surah, memorizedSet) {
        (1..surah.totalVerses).count { vNum ->
            memorizedSet.contains("${surah.number}_$vNum")
        }
    }

    LaunchedEffect(surah.number, surah.verses.size) {
        if (surah.verses.isEmpty()) {
            viewModel.ensureMemorizationVersesLoaded()
        }
    }

    Scaffold(
        topBar = {
            NoorTopBar(
                title = "Hifz Setup",
                eyebrow = "تحفيظ القرآن الكريم",
                subtitle = "Configure your memorization program",
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back",
                isDark = themeColors.isDark,
                themeColors = themeColors
            )
        },
        containerColor = themeColors.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. MODE SELECTOR (PRACTICE vs RECALL vs HISTORY)
            item {
                val isPractice = activeTab == 0
                val isRecall = activeTab == 2
                val isHistory = activeTab == 3
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp)),
                    shape = RoundedCornerShape(26.dp),
                    color = themeColors.surface,
                    border = null,
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Practice Tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(26.dp))
                                .clickable {
                                    viewModel.setMemorizationStudioTab(0)
                                }
                                .testTag("setup_tab_practice"),
                            shape = RoundedCornerShape(26.dp),
                            color = if (isPractice) themeColors.accent else Color.Transparent,
                            border = null,
                            shadowElevation = 0.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = if (isPractice) Color.White else themeColors.translationText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = "Practice",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (isPractice) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isPractice) Color.White else themeColors.arabicText,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }

                        // Recall Tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(26.dp))
                                .clickable {
                                    viewModel.setMemorizationStudioTab(2)
                                }
                                .testTag("setup_tab_recall"),
                            shape = RoundedCornerShape(26.dp),
                            color = if (isRecall) themeColors.accent else Color.Transparent,
                            border = null,
                            shadowElevation = 0.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = if (isRecall) Color.White else themeColors.translationText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = "Self-Recall",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (isRecall) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isRecall) Color.White else themeColors.arabicText,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }

                        // History Tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(26.dp))
                                .clickable {
                                    viewModel.setMemorizationStudioTab(3)
                                }
                                .testTag("setup_tab_history"),
                            shape = RoundedCornerShape(26.dp),
                            color = if (isHistory) themeColors.accent else Color.Transparent,
                            border = null,
                            shadowElevation = 0.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = if (isHistory) Color.White else themeColors.translationText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = "History",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (isHistory) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isHistory) Color.White else themeColors.arabicText,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 2. SURAH SELECTION CARD (Under the tabs, omitted on History tab)
            if (activeTab != 3) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showSurahPicker = true }
                            .testTag("hifz_setup_surah_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                        border = null,
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SURAH TO MEMORIZE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = themeColors.accent,
                                        fontSize = 10.sp
                                    )
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = themeColors.accent.copy(alpha = 0.12f),
                                    border = null
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Change",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = themeColors.accent,
                                                fontSize = 11.sp
                                            )
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = themeColors.accent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(themeColors.accent.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${surah.number}",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = themeColors.accent
                                            )
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = surah.nameEnglish,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = themeColors.arabicText,
                                                letterSpacing = (-0.5).sp
                                            )
                                        )
                                        Text(
                                            text = "${surah.totalVerses} Ayahs • ${surah.revelationType}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = themeColors.translationText,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = surah.nameArabic,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.accent,
                                        fontSize = 22.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // === TAB-SPECIFIC CONFIGURATIONS ===
            if (activeTab == 3) {
                // --- HISTORY VIEW ---
                item {
                    HifzHistoryContent(
                        sessionLogs = historyLogs,
                        memorizedCount = memorizedSet.size,
                        themeColors = themeColors,
                        onDrillSession = { log ->
                            val targetSurah = com.example.data.quran.QuranData.surahs.firstOrNull { it.number == log.surahNumber } ?: surah
                            viewModel.setMemorizationSurah(targetSurah, log.startAyah, log.endAyah)
                            viewModel.setMemorizationStudioTab(if (log.mode == "Self-Recall") 2 else 0)
                            viewModel.startMemorizationSession()
                        },
                        onClearHistory = {
                            viewModel.clearHifzHistory()
                        }
                    )
                }
            } else if (activeTab != 2) {
                // --- PRACTICE MODE CONFIGURATION ---
                // 0. Recommended Hifz Passages (2 Side-by-Side Cards)
                item {
                    RecommendedHifzSection(
                        themeColors = themeColors,
                        onOpenGroup = { groupIndex ->
                            showRecommendationsSheetGroup = groupIndex
                        }
                    )
                }

                // 1. Ready-made Presets (Beginner, Intermediate, Advanced)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "READY-MADE PROGRAM PRESETS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = themeColors.translationText,
                                fontSize = 11.sp
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HifzLevelPreset.entries.forEach { preset ->
                                val isSelected = activePreset == preset
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            activePreset = preset
                                            viewModel.applyHifzPreset(
                                                batchSize = preset.defaultBatchSize,
                                                repeatCount = preset.repeatCount,
                                                pattern = preset.pattern,
                                                delaySeconds = preset.delaySeconds
                                            )
                                        }
                                        .testTag("preset_${preset.name.lowercase()}"),
                                    shape = RoundedCornerShape(16.dp),
                                    color = themeColors.surface,
                                    border = if (isSelected) {
                                        BorderStroke(1.5.dp, themeColors.accent)
                                    } else null
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = preset.icon,
                                                contentDescription = null,
                                                tint = if (isSelected) themeColors.accent else themeColors.translationText,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = themeColors.accent,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = preset.title,
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) themeColors.accent else themeColors.arabicText,
                                                fontSize = 13.sp
                                            )
                                        )

                                        Text(
                                            text = preset.description,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = themeColors.translationText,
                                                fontSize = 10.5.sp,
                                                lineHeight = 14.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Ayah Range & Batch Size Card
                item {
                    AyahRangeAndBatchCard(
                        startAyah = startAyah,
                        endAyah = endAyah,
                        totalVerses = surah.totalVerses,
                        batchSize = batchSize,
                        themeColors = themeColors,
                        onRangeChanged = { newStart, newEnd ->
                            activePreset = null
                            viewModel.setMemorizationRange(newStart, newEnd)
                        },
                        onDirectAyahInputClick = { type, current ->
                            showDirectAyahInputDialog = Pair(type, current)
                        }
                    )
                }

                // 3. Drill Repetitions & Custom Timing Controls
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                        border = null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(22.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "DRILL REPETITIONS & TIMING",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = themeColors.accent,
                                        fontSize = 10.sp
                                    )
                                )

                                Surface(
                                    shape = CircleShape,
                                    color = themeColors.accent.copy(alpha = 0.12f),
                                    modifier = Modifier.clickable { showDrillExplanationSheet = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = themeColors.accent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "How Drills Work",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = themeColors.accent,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }

                            // Repetition Count Selector
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Repeat Each Ayah:",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = themeColors.arabicText,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp
                                            )
                                        )
                                        Text(
                                            text = "Number of times Qari recites each verse",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = themeColors.translationText,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = "${repeatCount}x times",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.accent,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(1, 2, 3, 5, 7, 10).forEach { count ->
                                        val isSelected = repeatCount == count
                                        Surface(
                                            shape = RoundedCornerShape(26.dp),
                                            color = if (isSelected) themeColors.accent else themeColors.background,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    activePreset = null
                                                    viewModel.setMemorizationRepeatCount(count)
                                                }
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${count}x",
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

                            HorizontalDivider(color = themeColors.border.copy(alpha = 0.25f))

                            // Ayah Pattern Selector
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Repetition Pattern:",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = themeColors.arabicText,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp
                                            )
                                        )
                                        Text(
                                            text = "Links adjacent ayahs into flow loops",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = themeColors.translationText,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = ayahPattern,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.accent,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("3, 3, 3", "5, 5, 5", "2, 2, 2", "1, 1, 1").forEach { patternCandidate ->
                                        val isSelected = ayahPattern == patternCandidate || ayahPattern == patternCandidate.replace(", ", " ")
                                        Surface(
                                            shape = RoundedCornerShape(26.dp),
                                            color = if (isSelected) themeColors.accent else themeColors.background,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    activePreset = null
                                                    viewModel.setAyahPattern(patternCandidate)
                                                }
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = patternCandidate.replace(", ", " "),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) Color.White else themeColors.arabicText,
                                                        fontSize = 11.5.sp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = themeColors.border.copy(alpha = 0.25f))

                            // Delay between Ayahs
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Pause Between Ayahs:",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = themeColors.arabicText,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp
                                            )
                                        )
                                        Text(
                                            text = "Silent recitation window after each verse",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = themeColors.translationText,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = if (delaySeconds == 0) "Instant" else "${delaySeconds}s pause",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.accent,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(0, 1, 3, 5, 10).forEach { sec ->
                                        val isSelected = delaySeconds == sec
                                        Surface(
                                            shape = RoundedCornerShape(26.dp),
                                            color = if (isSelected) themeColors.accent else themeColors.background,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    activePreset = null
                                                    viewModel.setMemorizationDelaySeconds(sec)
                                                }
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (sec == 0) "0s" else "${sec}s",
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
                }

                // 4. Recitation Voice & Audio Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                        border = null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "RECITATION VOICE & AUDIO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = themeColors.accent,
                                    fontSize = 10.sp
                                )
                            )

                            // Reciter Card
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showReciterPicker = true },
                                shape = RoundedCornerShape(12.dp),
                                color = themeColors.background
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
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
                                                .background(themeColors.accent.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.GraphicEq,
                                                contentDescription = null,
                                                tint = themeColors.accent,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = selectedReciter.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = themeColors.arabicText
                                                )
                                            )
                                            Text(
                                                text = "${selectedReciter.style} • ${selectedReciter.country}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = themeColors.translationText,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = CircleShape,
                                        color = themeColors.accent.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = "Change",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = themeColors.accent,
                                                fontSize = 11.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = themeColors.border.copy(alpha = 0.4f))

                            // Toggles
                            SetupToggleItem(
                                title = "Loop Range Continuously",
                                subtitle = "Auto-restarts session from Start Ayah after finishing",
                                checked = loopRange,
                                themeColors = themeColors,
                                onCheckedChange = { viewModel.toggleMemorizationLoopRange() }
                            )
                        }
                    }
                }
            } else {
                // --- RECALL MODE CONFIGURATION ---
                // 1. Recall Mode Info Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                        border = null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(themeColors.accent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = themeColors.accent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Self-Test & Recall Session",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.arabicText
                                    )
                                )
                                Text(
                                    text = "Ayahs are masked so you can test recitation from memory. Tap words or ayahs to reveal and confirm accuracy.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeColors.translationText,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // 2. Ayah Range to Test
                item {
                    AyahRangeAndBatchCard(
                        startAyah = startAyah,
                        endAyah = endAyah,
                        totalVerses = surah.totalVerses,
                        batchSize = batchSize,
                        themeColors = themeColors,
                        onRangeChanged = { newStart, newEnd ->
                            activePreset = null
                            viewModel.setMemorizationRange(newStart, newEnd)
                        },
                        onDirectAyahInputClick = { type, current ->
                            showDirectAyahInputDialog = Pair(type, current)
                        }
                    )
                }

                // 3. Masking & Silhouette Opacity
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                        border = null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "MASKING INTENSITY (SILHOUETTE)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = themeColors.accent,
                                    fontSize = 10.sp
                                )
                            )

                            Text(
                                text = "Adjust how subtle or hidden the verses appear during self-testing:",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = themeColors.translationText,
                                    fontSize = 11.5.sp
                                )
                            )

                            val opacityOptions = listOf(
                                Triple(0.04f, "Hidden", "4%"),
                                Triple(0.15f, "Subtle", "15%"),
                                Triple(0.25f, "Contour", "25%"),
                                Triple(0.40f, "Ghost", "40%")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                opacityOptions.forEach { (valFloat, label, pct) ->
                                    val isSelected = kotlin.math.abs(silhouetteOpacity - valFloat) < 0.05f
                                    Surface(
                                        shape = RoundedCornerShape(26.dp),
                                        color = if (isSelected) themeColors.accent else themeColors.background,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                viewModel.setHifzSilhouetteOpacity(valFloat)
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else themeColors.arabicText,
                                                    fontSize = 12.sp
                                                )
                                            )
                                            Text(
                                                text = pct,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else themeColors.translationText,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Testing Group Size Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                        border = null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "TESTING CHUNK / GROUP SIZE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = themeColors.accent,
                                    fontSize = 10.sp
                                )
                            )

                            Text(
                                text = "Number of verses to test before revealing and checking results:",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = themeColors.translationText,
                                    fontSize = 11.5.sp
                                )
                            )

                            val groupSizeOptions = listOf(
                                1 to "1 Ayah",
                                3 to "3 Ayahs",
                                5 to "5 Ayahs",
                                batchSize to "All ($batchSize)"
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                groupSizeOptions.forEach { (size, label) ->
                                    val isSelected = recallGroupSize == size
                                    Surface(
                                        shape = RoundedCornerShape(26.dp),
                                        color = if (isSelected) themeColors.accent else themeColors.background,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                viewModel.setRecallGroupSize(size)
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else themeColors.arabicText,
                                                    fontSize = 11.5.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Audio Verification & Reciter Card for Recall
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                        border = null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "AUDIO VERIFICATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = themeColors.accent,
                                    fontSize = 10.sp
                                )
                            )

                            // Reciter Card
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showReciterPicker = true },
                                shape = RoundedCornerShape(12.dp),
                                color = themeColors.background
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
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
                                                .background(themeColors.accent.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.GraphicEq,
                                                contentDescription = null,
                                                tint = themeColors.accent,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = selectedReciter.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = themeColors.arabicText
                                                )
                                            )
                                            Text(
                                                text = "${selectedReciter.style} • ${selectedReciter.country}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = themeColors.translationText,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = CircleShape,
                                        color = themeColors.accent.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = "Change",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = themeColors.accent,
                                                fontSize = 11.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = themeColors.border.copy(alpha = 0.4f))

                            SetupToggleItem(
                                title = "Audio Sync Reveal",
                                subtitle = "Automatically unmasks each verse as Qari recites",
                                checked = audioSyncReveal,
                                themeColors = themeColors,
                                onCheckedChange = { viewModel.toggleMemorizationAudioSyncReveal() }
                            )

                            SetupToggleItem(
                                title = "Show English Translation",
                                subtitle = "Display English translation text after revealing an ayah",
                                checked = showTranslation,
                                themeColors = themeColors,
                                onCheckedChange = { viewModel.toggleMemorizationShowTranslation() }
                            )
                        }
                    }
                }
            }

            // 6. Start Session Action Button (Normal button at bottom of page)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (activeTab == 3) {
                            viewModel.setMemorizationStudioTab(0)
                            viewModel.startMemorizationSession()
                        } else {
                            viewModel.startMemorizationSession()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_hifz_session_button"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColors.accent
                    )
                ) {
                    Text(
                        text = when (activeTab) {
                            2 -> "Start Recall Session"
                            3 -> "Start New Hifz Session"
                            else -> "Start Hifz Session"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // SURAH PICKER SHEET
    if (showSurahPicker) {
        SurahPickerSheet(
            currentSurah = surah,
            themeColors = themeColors,
            onSelectSurah = { selected ->
                viewModel.setMemorizationSurah(selected)
                activePreset = HifzLevelPreset.INTERMEDIATE
                showSurahPicker = false
            },
            onDismiss = { showSurahPicker = false }
        )
    }

    // RECITER PICKER SHEET
    if (showReciterPicker) {
        ReciterPickerSheet(
            selectedReciter = selectedReciter,
            themeColors = themeColors,
            onSelectReciter = { reciter ->
                viewModel.setReciter(reciter)
                showReciterPicker = false
            },
            onDismiss = { showReciterPicker = false }
        )
    }

    // DRILL EXPLANATION SHEET
    if (showDrillExplanationSheet) {
        DrillExplanationSheet(
            themeColors = themeColors,
            onDismiss = { showDrillExplanationSheet = false }
        )
    }

    // RECOMMENDATIONS PICKER SHEET
    if (showRecommendationsSheetGroup != null) {
        RecommendationsPickerSheet(
            initialGroup = showRecommendationsSheetGroup!!,
            themeColors = themeColors,
            onSelectPassage = { surahNum, sAyah, eAyah ->
                val targetSurah = com.example.data.quran.QuranData.surahs.firstOrNull { it.number == surahNum }
                if (targetSurah != null) {
                    viewModel.setMemorizationSurah(targetSurah, sAyah, eAyah)
                    activePreset = HifzLevelPreset.INTERMEDIATE
                }
                showRecommendationsSheetGroup = null
            },
            onDismiss = { showRecommendationsSheetGroup = null }
        )
    }

    // DIRECT AYAH INPUT DIALOG
    showDirectAyahInputDialog?.let { (type, currentVal) ->
        DirectAyahInputDialog(
            type = type,
            currentValue = currentVal,
            totalVerses = surah.totalVerses,
            themeColors = themeColors,
            onConfirm = { typedValue ->
                if (type == "FROM") {
                    val newStart = typedValue.coerceIn(1, endAyah)
                    viewModel.setMemorizationRange(newStart, endAyah)
                } else {
                    val newEnd = typedValue.coerceIn(startAyah, surah.totalVerses)
                    viewModel.setMemorizationRange(startAyah, newEnd)
                }
                showDirectAyahInputDialog = null
            },
            onDismiss = { showDirectAyahInputDialog = null }
        )
    }
}

@Composable
private fun AyahRangeAndBatchCard(
    startAyah: Int,
    endAyah: Int,
    totalVerses: Int,
    batchSize: Int,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onRangeChanged: (Int, Int) -> Unit,
    onDirectAyahInputClick: (String, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "AYAH RANGE & BATCH SIZE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = themeColors.accent,
                    fontSize = 10.sp
                )
            )

            // Quick Batch selector buttons
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Quick Batch Size:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = themeColors.translationText,
                        fontSize = 11.5.sp
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isAllSelected = startAyah == 1 && endAyah == totalVerses
                    listOf(3, 5, 7, 10, 15).forEach { size ->
                        val isCurrentBatch = !isAllSelected && batchSize == size
                        Surface(
                            shape = RoundedCornerShape(26.dp),
                            color = if (isCurrentBatch) themeColors.accent else themeColors.background,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val newEnd = (startAyah + size - 1).coerceAtMost(totalVerses)
                                    onRangeChanged(startAyah, newEnd)
                                }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$size",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isCurrentBatch) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCurrentBatch) Color.White else themeColors.arabicText,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                    // All pill
                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = if (isAllSelected) themeColors.accent else themeColors.background,
                        modifier = Modifier
                            .weight(1.2f)
                            .clickable {
                                onRangeChanged(1, totalVerses)
                            }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "All",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isAllSelected) Color.White else themeColors.arabicText,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = themeColors.border.copy(alpha = 0.4f))

            // Fine Stepper Controls: From Ayah and To Ayah
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start Ayah Stepper
                RangeStepperBox(
                    title = "From Ayah",
                    value = startAyah,
                    minValue = 1,
                    maxValue = endAyah,
                    themeColors = themeColors,
                    modifier = Modifier.weight(1f),
                    onDecrement = {
                        onRangeChanged((startAyah - 1).coerceAtLeast(1), endAyah)
                    },
                    onIncrement = {
                        onRangeChanged((startAyah + 1).coerceAtMost(endAyah), endAyah)
                    },
                    onNumberClick = {
                        onDirectAyahInputClick("FROM", startAyah)
                    }
                )

                // End Ayah Stepper
                RangeStepperBox(
                    title = "To Ayah",
                    value = endAyah,
                    minValue = startAyah,
                    maxValue = totalVerses,
                    themeColors = themeColors,
                    modifier = Modifier.weight(1f),
                    onDecrement = {
                        onRangeChanged(startAyah, (endAyah - 1).coerceAtLeast(startAyah))
                    },
                    onIncrement = {
                        onRangeChanged(startAyah, (endAyah + 1).coerceAtMost(totalVerses))
                    },
                    onNumberClick = {
                        onDirectAyahInputClick("TO", endAyah)
                    }
                )
            }

            // Summary Pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = themeColors.accent.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Selected Session Scope",
                        style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText, fontSize = 11.sp)
                    )
                    Text(
                        text = "$batchSize Verses (Ayahs $startAyah – $endAyah)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = themeColors.accent, fontSize = 12.sp)
                    )
                }
            }

            // Short Explanation Text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = themeColors.accent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Batch size breaks your memorization target into smaller manageable verse sets. Once a batch is completed, your session automatically advances to the next set.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = themeColors.translationText,
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun RangeStepperBox(
    title: String,
    value: Int,
    minValue: Int,
    maxValue: Int,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    modifier: Modifier = Modifier,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onNumberClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = themeColors.background,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = themeColors.translationText,
                    fontSize = 10.5.sp
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onDecrement,
                    enabled = value > minValue,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = if (value > minValue) themeColors.accent else themeColors.translationText.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = themeColors.surface,
                    border = BorderStroke(1.dp, themeColors.accent.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable { onNumberClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$value",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = themeColors.arabicText,
                                fontSize = 17.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Type Ayah Number",
                            tint = themeColors.accent,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onIncrement,
                    enabled = value < maxValue,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = if (value < maxValue) themeColors.accent else themeColors.translationText.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SetupToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = themeColors.arabicText
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeColors.translationText,
                    fontSize = 11.sp
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
                uncheckedTrackColor = themeColors.background
            )
        )
    }
}

private data class RecommendedPassage(
    val title: String,
    val arabicName: String,
    val subtitle: String,
    val surahNumber: Int,
    val startAyah: Int,
    val endAyah: Int,
    val icon: ImageVector
)

@Composable
private fun RecommendedHifzSection(
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onOpenGroup: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECOMMENDED HIFZ PASSAGES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = themeColors.translationText,
                    fontSize = 11.sp
                )
            )
            Text(
                text = "Tap to Browse",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = themeColors.accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: 15 Short Surahs
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = themeColors.surface,
                border = BorderStroke(1.dp, themeColors.accent.copy(alpha = 0.22f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenGroup(0) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(themeColors.accent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = themeColors.accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Browse 15 Short Surahs",
                            tint = themeColors.translationText,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "15 Short Surahs",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText,
                                fontSize = 14.5.sp
                            )
                        )
                        Text(
                            text = "Start with commonly memorized short surahs",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = themeColors.translationText,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }
            }

            // Card 2: 15 Important Ayahs
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = themeColors.surface,
                border = BorderStroke(1.dp, themeColors.accent.copy(alpha = 0.22f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenGroup(1) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(themeColors.accent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = themeColors.accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Browse 15 Important Ayahs",
                            tint = themeColors.translationText,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "15 Important Ayahs",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText,
                                fontSize = 14.5.sp
                            )
                        )
                        Text(
                            text = "Memorize important and commonly recited ayahs",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = themeColors.translationText,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecommendationsPickerSheet(
    initialGroup: Int,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onSelectPassage: (Int, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedGroup by remember { mutableStateOf(initialGroup) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val shortSurahs = remember {
        listOf(
            RecommendedPassage("Surah Al-Ikhlas", "سورة الإخلاص", "Surah 112 • 4 Ayahs", 112, 1, 4, Icons.Default.School),
            RecommendedPassage("Surah Al-Falaq", "سورة الفلق", "Surah 113 • 5 Ayahs", 113, 1, 5, Icons.Default.Psychology),
            RecommendedPassage("Surah An-Nas", "سورة الناس", "Surah 114 • 6 Ayahs", 114, 1, 6, Icons.Default.School),
            RecommendedPassage("Surah Al-Kawthar", "سورة الكوثر", "Surah 108 • 3 Ayahs", 108, 1, 3, Icons.Default.AutoAwesome),
            RecommendedPassage("Surah An-Nasr", "سورة النصر", "Surah 110 • 3 Ayahs", 110, 1, 3, Icons.Default.School),
            RecommendedPassage("Surah Al-Asr", "سورة العصر", "Surah 103 • 3 Ayahs", 103, 1, 3, Icons.Default.Psychology),
            RecommendedPassage("Surah Quraysh", "سورة قريش", "Surah 106 • 4 Ayahs", 106, 1, 4, Icons.Default.School),
            RecommendedPassage("Surah Al-Masad", "سورة المسد", "Surah 111 • 5 Ayahs", 111, 1, 5, Icons.Default.AutoAwesome),
            RecommendedPassage("Surah Al-Kafirun", "سورة الكافرون", "Surah 109 • 6 Ayahs", 109, 1, 6, Icons.Default.School),
            RecommendedPassage("Surah Al-Ma'un", "سورة الماعون", "Surah 107 • 7 Ayahs", 107, 1, 7, Icons.Default.Psychology),
            RecommendedPassage("Surah Al-Fil", "سورة الفيل", "Surah 105 • 5 Ayahs", 105, 1, 5, Icons.Default.School),
            RecommendedPassage("Surah Al-Humazah", "سورة الهمزة", "Surah 104 • 9 Ayahs", 104, 1, 9, Icons.Default.AutoAwesome),
            RecommendedPassage("Surah At-Takathur", "سورة التكاثر", "Surah 102 • 8 Ayahs", 102, 1, 8, Icons.Default.Psychology),
            RecommendedPassage("Surah Al-Qari'ah", "سورة القارعة", "Surah 101 • 11 Ayahs", 101, 1, 11, Icons.Default.School),
            RecommendedPassage("Surah Ad-Duha", "سورة الضحى", "Surah 93 • 11 Ayahs", 93, 1, 11, Icons.Default.AutoAwesome)
        )
    }

    val importantAyahs = remember {
        listOf(
            RecommendedPassage("Ayat al-Kursi", "آية الكرسي", "Al-Baqarah • v.255", 2, 255, 255, Icons.Default.AutoAwesome),
            RecommendedPassage("Last 2 Ayahs", "خواتيم البقرة", "Al-Baqarah • v.285–286", 2, 285, 286, Icons.Default.School),
            RecommendedPassage("Al-Kahf (First 10)", "أول الكهف", "Al-Kahf • v.1–10", 18, 1, 10, Icons.Default.Psychology),
            RecommendedPassage("Al-Kahf (Last 10)", "آخر الكهف", "Al-Kahf • v.101–110", 18, 101, 110, Icons.Default.AutoAwesome),
            RecommendedPassage("Surah Al-Mulk", "سورة الملك", "Al-Mulk • v.1–10", 67, 1, 10, Icons.Default.School),
            RecommendedPassage("Ayat Al-Hashr", "خواتيم الحشر", "Al-Hashr • v.21–24", 59, 21, 24, Icons.Default.Psychology),
            RecommendedPassage("Surah Ar-Rahman", "أول الرحمن", "Ar-Rahman • v.1–13", 55, 1, 13, Icons.Default.AutoAwesome),
            RecommendedPassage("Surah Yasin", "أول يس", "Yasin • v.1–12", 36, 1, 12, Icons.Default.School),
            RecommendedPassage("Surah As-Sajdah", "أول السجدة", "As-Sajdah • v.1–10", 32, 1, 10, Icons.Default.Psychology),
            RecommendedPassage("Surah Al-Mu'minun", "أول المؤمنون", "Al-Mu'minun • v.1–11", 23, 1, 11, Icons.Default.School),
            RecommendedPassage("Verse of Light", "آية النور", "An-Nur • v.35", 24, 35, 35, Icons.Default.AutoAwesome),
            RecommendedPassage("Du'a Dhul-Nun", "دعاء ذي النون", "Al-Anbiya • v.87–88", 21, 87, 88, Icons.Default.Psychology),
            RecommendedPassage("Ali 'Imran (190-194)", "خواتيم آل عمران", "Ali 'Imran • v.190–194", 3, 190, 194, Icons.Default.School),
            RecommendedPassage("Ibad Ar-Rahman", "عباد الرحمن", "Al-Furqan • v.63–70", 25, 63, 70, Icons.Default.AutoAwesome),
            RecommendedPassage("Surah As-Saff", "تجارة تنجيكم", "As-Saff • v.10–13", 61, 10, 13, Icons.Default.Psychology)
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = themeColors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = themeColors.accent,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Recommended Passages",
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

            // Group Switcher Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedGroup == 0) themeColors.accent else themeColors.background,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedGroup = 0 }
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "15 Short Surahs",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (selectedGroup == 0) Color.White else themeColors.arabicText,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedGroup == 1) themeColors.accent else themeColors.background,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedGroup = 1 }
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "15 Important Ayahs",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (selectedGroup == 1) Color.White else themeColors.arabicText,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }

            Text(
                text = if (selectedGroup == 0) "Start with commonly memorized short surahs" else "Memorize important and commonly recited ayahs",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeColors.translationText,
                    fontSize = 12.sp
                )
            )

            HorizontalDivider(color = themeColors.border.copy(alpha = 0.3f))

            // Scrollable List of 15 Items
            val activeList = if (selectedGroup == 0) shortSurahs else importantAyahs

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(activeList) { item ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = themeColors.background,
                        border = BorderStroke(1.dp, themeColors.accent.copy(alpha = 0.15f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectPassage(item.surahNumber, item.startAyah, item.endAyah)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(themeColors.accent.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = themeColors.accent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.arabicText,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Text(
                                        text = item.subtitle,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = themeColors.translationText,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.arabicName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = themeColors.accent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Select",
                                    tint = themeColors.translationText,
                                    modifier = Modifier.size(18.dp)
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
private fun DrillExplanationSheet(
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = themeColors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = themeColors.accent,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "How Hifz Drills Work",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText,
                            fontSize = 18.sp
                        )
                    )
                }
            }

            HorizontalDivider(color = themeColors.border.copy(alpha = 0.3f))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DrillStepItem(
                    stepNumber = "1",
                    title = "Each Ayah Repetition",
                    description = "Focuses on memorizing individual verses. The Qari recites an ayah, and you repeat it multiple times (e.g., 3x or 5x) until comfortable.",
                    themeColors = themeColors
                )
                DrillStepItem(
                    stepNumber = "2",
                    title = "Repetition Pattern (Linking Ayahs)",
                    description = "Links adjacent verses together. For example, '1-1-2-12' recites Ayah 1 twice, Ayah 2 once, then Ayahs 1 and 2 together to build seamless flow.",
                    themeColors = themeColors
                )
                DrillStepItem(
                    stepNumber = "3",
                    title = "Pause Between Ayahs",
                    description = "Gives you a timed silent window (1s, 3s, 5s, or 10s) after each recitation to recite from memory before the Qari continues.",
                    themeColors = themeColors
                )
                DrillStepItem(
                    stepNumber = "4",
                    title = "Auto-Advancing Batches",
                    description = "When 'Loop Range' is OFF, finishing the current batch automatically advances your session to the next set of ayahs in the surah.",
                    themeColors = themeColors
                )
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent)
            ) {
                Text("Got It!", style = MaterialTheme.typography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun DrillStepItem(
    stepNumber: String,
    title: String,
    description: String,
    themeColors: com.example.ui.theme.ReadingThemeColors
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(themeColors.accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.accent
                )
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.arabicText
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeColors.translationText,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}

@Composable
private fun DirectAyahInputDialog(
    type: String,
    currentValue: Int,
    totalVerses: Int,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var textValue by remember { mutableStateOf("$currentValue") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (type == "FROM") "Set Start Ayah" else "Set End Ayah",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.arabicText
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Type the Ayah number (1 to $totalVerses):",
                    style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                )
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it.filter { char -> char.isDigit() } },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeColors.accent,
                        unfocusedBorderColor = themeColors.border,
                        focusedTextColor = themeColors.arabicText,
                        unfocusedTextColor = themeColors.arabicText
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsed = textValue.toIntOrNull() ?: currentValue
                    val validValue = parsed.coerceIn(1, totalVerses)
                    onConfirm(validValue)
                }
            ) {
                Text("Apply", style = MaterialTheme.typography.labelLarge.copy(color = themeColors.accent, fontWeight = FontWeight.Bold))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge.copy(color = themeColors.translationText))
            }
        },
        containerColor = themeColors.surface
    )
}
