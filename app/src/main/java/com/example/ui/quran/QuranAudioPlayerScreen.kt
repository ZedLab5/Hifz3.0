package com.example.ui.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.quran.QuranData
import com.example.ui.components.KeepScreenOn
import com.example.ui.components.NoConnectionBanner
import com.example.ui.MainViewModel
import java.util.Locale

import com.example.ui.theme.BorderDividerDark
import com.example.ui.theme.CanvasObsidianNight
import com.example.ui.theme.DarkPine
import com.example.ui.theme.GoldTintBgDark
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

// ---------- Centralized Theme-Aware Obsidian Palette ----------
private val ObsidianBgTop = CanvasObsidianNight
private val ObsidianBgMid = SurfaceDark
private val ObsidianBgBottom = CanvasObsidianNight

private val CardSurface = SurfaceDark
private val CardBorder = BorderDividerDark
private val PillBg = SurfaceElevatedDark
private val PillBorder = BorderDividerDark
private val SheetSurface = SurfaceDark

private val SoftGold = SecondaryGoldDark
private val SoftGoldLight = SecondaryGoldLight
private val SoftGoldContainer = GoldTintBgDark
private val SoftGoldBorder = SecondaryGoldDark.copy(alpha = 0.3f)

private val AccentGreen = PrimaryTealDark
private val AccentGreenPrimary = PrimaryTealDark
private val DownloadedCardBg = PrimaryTealDark.copy(alpha = 0.12f)
private val DownloadedCardBorder = PrimaryTealDark.copy(alpha = 0.3f)

private val TextPrimary = TextPrimaryDark
private val TextSecondary = TextSecondaryDark
private val TextMuted = TextSecondaryDark.copy(alpha = 0.7f)
private val DarkButtonText = DarkPine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranAudioPlayerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    KeepScreenOn()

    val isPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val isBuffering by viewModel.isAudioBuffering.collectAsStateWithLifecycle()
    val playingSurah by viewModel.currentPlayingSurah.collectAsStateWithLifecycle()
    val progress by viewModel.audioProgress.collectAsStateWithLifecycle()
    val currentPositionMs by viewModel.audioCurrentPositionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.audioDurationMs.collectAsStateWithLifecycle()
    val selectedReciter by viewModel.selectedReciter.collectAsStateWithLifecycle()
    val reciters = viewModel.reciters
    val isRepeatOne by viewModel.isAudioRepeatOne.collectAsStateWithLifecycle()
    val sleepTimerMins by viewModel.sleepTimerMinutes.collectAsStateWithLifecycle()
    val downloadedSurahs by viewModel.downloadedSurahs.collectAsStateWithLifecycle()
    val isDownloadingMap by viewModel.isSurahDownloading.collectAsStateWithLifecycle()
    val isAudioNetworkError by viewModel.isAudioNetworkError.collectAsStateWithLifecycle()
    val arabicFont by viewModel.selectedArabicFont.collectAsStateWithLifecycle()

    var showReciterSheet by remember { mutableStateOf(false) }
    var showSurahListSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val downloadKey = "${selectedReciter.id}_${playingSurah.number}"
    val isDownloaded = downloadedSurahs.contains(downloadKey) || viewModel.isSurahDownloaded(playingSurah.number, selectedReciter.id)
    val downloadProgress = isDownloadingMap[downloadKey]

    // Pure, clean vertical gradient background without decorative shapes
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBgTop)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ------------------------------------------------------------
            // 1. TOP BAR: Minimize + Clean Title / Status + Settings
            // ------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Minimize Button
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize Player",
                        tint = TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Center Title & Status
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "NOW PLAYING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftGold,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isDownloaded) "Offline Audio" else "Studio Recitation",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isDownloaded) AccentGreen else TextSecondary,
                            fontSize = 11.5.sp
                        )
                    )
                }

                // Settings Button
                IconButton(
                    onClick = { showSettingsSheet = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Audio Settings",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (isAudioNetworkError) {
                Spacer(modifier = Modifier.height(8.dp))
                NoConnectionBanner(
                    onRetry = {
                        viewModel.isAudioNetworkError.value = false
                        viewModel.toggleAudioPlayback()
                    },
                    isCompact = true,
                    message = "Streaming audio requires network connection. Tap to retry or play offline surahs."
                )
            }

            // ------------------------------------------------------------
            // 2. SURAH NAME PRESENTATION: Clean, Confident Typography
            // ------------------------------------------------------------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Surah Number Tag
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SoftGoldContainer,
                    border = null
                ) {
                    Text(
                        text = "SURAH ${playingSurah.number}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SoftGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Clean, Prominent Arabic Calligraphy
                Text(
                    text = playingSurah.nameArabic,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = arabicFont.fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 46.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // English Name
                Text(
                    text = playingSurah.nameEnglish,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 24.sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Meaning & Metadata
                Text(
                    text = "${playingSurah.englishMeaning} • ${playingSurah.revelationType} • ${playingSurah.totalVerses} Ayahs",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontSize = 13.5.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            // ------------------------------------------------------------
            // 3. RECITER INFO ROW: Minimal Card Style
            // ------------------------------------------------------------
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showReciterSheet = true },
                shape = RoundedCornerShape(16.dp),
                color = CardSurface,
                border = null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(PillBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = "Reciter",
                                tint = SoftGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = selectedReciter.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${selectedReciter.style} • ${selectedReciter.country}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Change Button Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PillBg,
                        border = null
                    ) {
                        Text(
                            text = "Change",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SoftGold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }

            // ------------------------------------------------------------
            // 4. PROGRESS BAR & TIME LABELS
            // ------------------------------------------------------------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
            ) {
                Slider(
                    value = progress,
                    onValueChange = { viewModel.seekAudioTo(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = SoftGold,
                        activeTrackColor = SoftGold,
                        inactiveTrackColor = PillBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDurationMs(currentPositionMs),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )

                    if (isBuffering) {
                        Text(
                            text = "Buffering audio...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AccentGreen,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Text(
                        text = formatDurationMs(durationMs),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // ------------------------------------------------------------
            // 5. PLAYBACK CONTROLS: Restrained, Clean Touch Targets
            // ------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Surah
                IconButton(
                    onClick = { viewModel.playPreviousSurah() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Surah",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Skip-Back 10 Seconds
                IconButton(
                    onClick = { viewModel.skipBack10Seconds() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "Skip Back 10 Seconds",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // HERO PLAY / PAUSE BUTTON
                Surface(
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .clickable { viewModel.toggleAudioPlayback() },
                    shape = CircleShape,
                    color = SoftGold,
                    border = null
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isBuffering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = DarkButtonText,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = DarkButtonText,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // REPEAT BUTTON
                IconButton(
                    onClick = { viewModel.toggleRepeatMode() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isRepeatOne) SoftGoldContainer else CardSurface)
                ) {
                    Icon(
                        imageVector = if (isRepeatOne) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = "Repeat Surah",
                        tint = if (isRepeatOne) SoftGold else TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Next Surah
                IconButton(
                    onClick = { viewModel.playNextSurah() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Surah",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // ------------------------------------------------------------
            // 6. BOTTOM ACTIONS: "Choose Surah" & "Download for Offline"
            // ------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button 1: "Choose Surah"
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showSurahListSheet = true },
                    shape = RoundedCornerShape(14.dp),
                    color = CardSurface,
                    border = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 13.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ListAlt,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Choose Surah",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp
                            )
                        )
                    }
                }

                // Button 2: "Download for Offline"
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            if (isDownloaded) {
                                viewModel.deleteSurahOffline(playingSurah, selectedReciter)
                            } else {
                                viewModel.downloadSurahOffline(playingSurah, selectedReciter)
                            }
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDownloaded) DownloadedCardBg else CardSurface,
                    border = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 13.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (downloadProgress != null) {
                            CircularProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier.size(19.dp),
                                color = TextPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isDownloaded) Icons.Default.CheckCircle else Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = if (isDownloaded) AccentGreen else TextPrimary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isDownloaded) "Downloaded" else "Download",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isDownloaded) AccentGreen else TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------
    // MODAL BOTTOM SHEET: CHOOSE RECITER
    // ------------------------------------------------------------
    if (showReciterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReciterSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SheetSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .padding(bottom = 30.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Choose Reciter",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "High-definition studio recitation streams",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = SoftGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(reciters) { reciter ->
                        val isCurrent = reciter.id == selectedReciter.id
                        val itemBg = if (isCurrent) PillBg else CardSurface

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    viewModel.selectReciter(reciter)
                                    showReciterSheet = false
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = itemBg,
                            border = null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    val iconBoxBg = if (isCurrent) SoftGold else PillBg
                                    val iconTint = if (isCurrent) DarkButtonText else TextPrimary

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(iconBoxBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = reciter.name,
                                            tint = iconTint,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = reciter.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                                color = TextPrimary
                                            )
                                        )
                                        Text(
                                            text = "${reciter.style} • ${reciter.country}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isCurrent) SoftGold else TextSecondary,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }

                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(SoftGold),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = DarkButtonText,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------
    // MODAL BOTTOM SHEET: SELECT SURAH QUEUE
    // ------------------------------------------------------------
    if (showSurahListSheet) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredSurahs = remember(searchQuery) {
            if (searchQuery.isBlank()) {
                QuranData.completeSurahList
            } else {
                QuranData.completeSurahList.filter {
                    it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                            it.nameArabic.contains(searchQuery) ||
                            it.number.toString() == searchQuery.trim()
                }
            }
        }

        ModalBottomSheet(
            onDismissRequest = { showSurahListSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SheetSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Select Surah",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name or number...", color = TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedContainerColor = CardSurface,
                        unfocusedContainerColor = CardSurface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    items(filteredSurahs) { surah ->
                        val isPlayingThis = surah.number == playingSurah.number
                        val itemBg = if (isPlayingThis) PillBg else CardSurface

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.playSurahAudio(surah, openPlayer = false)
                                    showSurahListSheet = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = itemBg,
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
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val badgeBg = if (isPlayingThis) SoftGold else PillBg
                                    val badgeTextColor = if (isPlayingThis) DarkButtonText else TextSecondary

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = badgeBg,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${surah.number}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = badgeTextColor,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = surah.nameEnglish,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = TextPrimary,
                                                fontWeight = if (isPlayingThis) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                        Text(
                                            text = "${surah.revelationType} • ${surah.totalVerses} Ayahs",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = surah.nameArabic,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        color = if (isPlayingThis) SoftGold else TextPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------
    // MODAL BOTTOM SHEET: PLAYER SETTINGS (SLEEP TIMER & RECITER)
    // ------------------------------------------------------------
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SheetSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .padding(bottom = 30.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Player Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = SoftGold
                    )
                }

                // Sleep Timer Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sleep Timer",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = SoftGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        if (sleepTimerMins != null) {
                            Text(
                                text = "Active: $sleepTimerMins min",
                                style = MaterialTheme.typography.labelSmall.copy(color = AccentGreen)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            null to "Off",
                            15 to "15m",
                            30 to "30m",
                            45 to "45m",
                            60 to "60m"
                        ).forEach { (mins, label) ->
                            val isSelected = sleepTimerMins == mins
                            val pillBg = if (isSelected) SoftGold else CardSurface
                            val pillTextColor = if (isSelected) DarkButtonText else TextPrimary

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setSleepTimer(mins) },
                                shape = RoundedCornerShape(10.dp),
                                color = pillBg,
                                border = null
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 9.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = pillTextColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Switch Reciter quick row
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            showSettingsSheet = false
                            showReciterSheet = true
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = CardSurface,
                    border = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Switch Reciter",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "Current: ${selectedReciter.name}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = SoftGold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Formats duration in milliseconds to MM:SS or HH:MM:SS format
 */
private fun formatDurationMs(ms: Int): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return if (minutes >= 60) {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        String.format(Locale.US, "%d:%02d:%02d", hours, remainingMinutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}
