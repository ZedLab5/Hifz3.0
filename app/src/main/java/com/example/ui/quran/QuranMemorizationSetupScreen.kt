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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
        bottomBar = {
            // Pinned Bottom Action: Normal rounded button at bottom
            Surface(
                color = themeColors.surface,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
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
                }
            }
        },
        containerColor = themeColors.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
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
                    val surahProgress = if (surah.totalVerses > 0) memorizedCountInSurah.toFloat() / surah.totalVerses else 0f
                    val surahPercentage = (surahProgress * 100).toInt()

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
                            verticalArrangement = Arrangement.spacedBy(14.dp)
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

                                // Circular Percentage Indicator for Surah Mastery
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    CircularProgressIndicator(
                                        progress = { surahProgress },
                                        modifier = Modifier.fillMaxSize(),
                                        color = themeColors.accent,
                                        strokeWidth = 4.dp,
                                        trackColor = themeColors.accent.copy(alpha = 0.15f)
                                    )
                                    Text(
                                        text = "$surahPercentage%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = themeColors.accent,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            // Surah Mastery Bar
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Memorized in Surah",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = themeColors.translationText,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = "$memorizedCountInSurah/${surah.totalVerses} Ayahs",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.accent,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape)
                                        .background(themeColors.border.copy(alpha = 0.3f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(surahProgress)
                                            .height(6.dp)
                                            .clip(CircleShape)
                                            .background(themeColors.accent)
                                    )
                                }
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
                            viewModel.setMemorizationSurah(targetSurah)
                            viewModel.setMemorizationRange(log.startAyah, log.endAyah)
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
                        }
                    )
                }

                // 3. Drill Repetitions & Timing Card
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
                                text = "DRILL REPETITIONS & TIMING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = themeColors.accent,
                                    fontSize = 10.sp
                                )
                            )

                            // Repetition Count Selector
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Repeat Each Ayah:",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = themeColors.arabicText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    )
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
                                                modifier = Modifier.padding(vertical = 8.dp),
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

                            // Ayah Pattern Selector
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Repetition Pattern:",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = themeColors.arabicText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    )
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
                                                modifier = Modifier.padding(vertical = 8.dp),
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

                            // Delay between Ayahs
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Pause Between Ayahs:",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = themeColors.arabicText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    )
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
                                    listOf(0, 1, 2, 3, 5).forEach { sec ->
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
                                                modifier = Modifier.padding(vertical = 8.dp),
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

                            SetupToggleItem(
                                title = "Reveal on Audio Recitation",
                                subtitle = "Automatically unmasks each verse as Qari recites",
                                checked = audioSyncReveal,
                                themeColors = themeColors,
                                onCheckedChange = { viewModel.toggleMemorizationAudioSyncReveal() }
                            )

                            SetupToggleItem(
                                title = "Show English Translation",
                                subtitle = "Display English translation text alongside Arabic",
                                checked = showTranslation,
                                themeColors = themeColors,
                                onCheckedChange = { viewModel.toggleMemorizationShowTranslation() }
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
}

@Composable
private fun AyahRangeAndBatchCard(
    startAyah: Int,
    endAyah: Int,
    totalVerses: Int,
    batchSize: Int,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onRangeChanged: (Int, Int) -> Unit
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
                    listOf(3, 5, 7, 10, 15).forEach { size ->
                        val isCurrentBatch = batchSize == size && endAyah < totalVerses
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
                    // Full Surah pill
                    val isFull = startAyah == 1 && endAyah == totalVerses
                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = if (isFull) themeColors.accent else themeColors.background,
                        modifier = Modifier
                            .weight(1.4f)
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
                                    fontWeight = if (isFull) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isFull) Color.White else themeColors.arabicText,
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
    onIncrement: () -> Unit
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                Text(
                    text = "$value",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = themeColors.arabicText,
                        fontSize = 17.sp
                    )
                )
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
