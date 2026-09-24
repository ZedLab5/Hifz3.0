package com.example.ui.quran

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Tune
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.ReadingThemes
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.luminance
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Surah
import com.example.data.quran.KhatmaEngine
import com.example.data.quran.QuranData
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.components.SpotlightOverlay
import com.example.ui.components.SpotlightStep
import com.example.ui.components.rememberSpotlightState
import com.example.ui.components.spotlightTarget
import com.example.ui.theme.HomePageColors
import com.example.ui.theme.rememberHomePageColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranSurahSelectionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val currentPlayingSurah by viewModel.currentPlayingSurah.collectAsStateWithLifecycle()
    val khatmaState by viewModel.khatmaDashboardState.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val readingProgress by viewModel.readingProgress.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf("All") } // "All", "Juz", "Favorites"
    var selectedJuzNumber by remember { mutableIntStateOf(1) } // 1..30
    var searchQuery by remember { mutableStateOf("") }

    // Parse x:xx (e.g. 2:255) verse reference
    val ayahRefPattern = remember { Regex("""^(\d{1,3})\s*[:\s]\s*(\d{1,3})$""") }
    val parsedAyahRef = remember(searchQuery) {
        val trimmed = searchQuery.trim()
        val match = ayahRefPattern.find(trimmed)
        if (match != null) {
            val surahNum = match.groupValues[1].toIntOrNull()
            val ayahNum = match.groupValues[2].toIntOrNull()
            if (surahNum != null && surahNum in 1..114 && ayahNum != null && ayahNum > 0) {
                Pair(surahNum, ayahNum)
            } else null
        } else null
    }

    val filteredSurahs = remember(selectedTab, selectedJuzNumber, favorites, searchQuery, parsedAyahRef, readingProgress) {
        val rawList = if (parsedAyahRef != null) {
            QuranData.surahs.filter { it.number == parsedAyahRef.first }
        } else {
            val initial = when (selectedTab) {
                "Favorites" -> {
                    QuranData.surahs.filter { surah ->
                        favorites.any { fav ->
                            fav.title.contains("Surah ${surah.nameEnglish}", ignoreCase = true) ||
                                    fav.title.contains(surah.nameArabic) ||
                                    fav.source.contains("Surah ${surah.number}", ignoreCase = true)
                        }
                    }
                }
                "Juz" -> {
                    val juzIndex = (selectedJuzNumber - 1).coerceIn(0, 29)
                    val startSurah = KhatmaEngine.juzStartPoints[juzIndex].first
                    val endSurah = if (juzIndex < 29) KhatmaEngine.juzStartPoints[juzIndex + 1].first else 114
                    QuranData.surahs.filter { it.number in startSurah..endSurah }
                }
                else -> QuranData.surahs
            }
            if (searchQuery.isBlank()) {
                initial
            } else {
                initial.filter {
                    it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                    it.nameArabic.contains(searchQuery) ||
                    it.number.toString() == searchQuery.trim()
                }
            }
        }

        // Hoist bookmarked surah to the very top (above Surah 1 Al-Fatiha) when browsing All
        val bookmarkedSurahNum = readingProgress?.surahNumber
        if (bookmarkedSurahNum != null && selectedTab == "All" && searchQuery.isBlank() && parsedAyahRef == null) {
            val bookmarkedSurah = rawList.firstOrNull { it.number == bookmarkedSurahNum }
            if (bookmarkedSurah != null) {
                listOf(bookmarkedSurah) + rawList.filter { it.number != bookmarkedSurahNum }
            } else rawList
        } else rawList
    }

    val spotlightState = rememberSpotlightState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val step1Title = stringResource(R.string.spotlight_quran_step1_title)
    val step1Desc = stringResource(R.string.spotlight_quran_step1_desc)
    val step2Title = stringResource(R.string.spotlight_quran_step2_title)
    val step2Desc = stringResource(R.string.spotlight_quran_step2_desc)
    val step3Title = stringResource(R.string.spotlight_quran_step3_title)
    val step3Desc = stringResource(R.string.spotlight_quran_step3_desc)
    val step4Title = stringResource(R.string.spotlight_quran_step4_title)
    val step4Desc = stringResource(R.string.spotlight_quran_step4_desc)

    val spotlightSteps = remember(step1Title, step1Desc, step2Title, step2Desc, step3Title, step3Desc, step4Title, step4Desc) {
        listOf(
            SpotlightStep(
                key = "quran_step_search_bar",
                title = step1Title,
                description = step1Desc,
                cornerRadius = 14.dp,
                padding = 4.dp
            ),
            SpotlightStep(
                key = "quran_step_reciters_icon",
                title = step3Title,
                description = step3Desc,
                cornerRadius = 22.dp,
                padding = 4.dp
            ),
            SpotlightStep(
                key = "quran_step_favorite_icon",
                title = step4Title,
                description = step4Desc,
                cornerRadius = 16.dp,
                padding = 6.dp
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(homeColors.pageBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NoorTopBar(
                title = "Holy Qur'an",
                eyebrow = "القرآن الكريم",
                subtitle = "114 Surahs • Divine Revelation",
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back",
                isDark = isDark,
                actions = {
                    NoorGlassIconButton(
                        onClick = { viewModel.navigateTo(NoorDestination.QURAN_RECITERS) },
                        icon = Icons.Default.Headphones,
                        contentDescription = "Quran Reciters",
                        modifier = Modifier.spotlightTarget(spotlightState, "quran_step_reciters_icon")
                    )
                    NoorGlassIconButton(
                        onClick = {
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                            spotlightState.start(spotlightSteps)
                        },
                        icon = Icons.Default.HelpOutline,
                        contentDescription = stringResource(R.string.spotlight_help_tooltip),
                        modifier = Modifier.testTag("quran_help_tutorial_button")
                    )
                }
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
            // Quick Features: Khatma Planner & Hifz Studio
            item(key = "quran_quick_features_row") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Hifz Studio Card
                    Surface(
                        onClick = { viewModel.openMemorizationStudio() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = homeColors.outerCardBackground,
                        shadowElevation = if (isDark) 0.dp else 0.4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(homeColors.iconBadgeBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = homeColors.iconColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "Hifz Studio",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = homeColors.titleText,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Hide words, test ayah recall & track memorization",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = homeColors.subtext,
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp
                                ),
                                minLines = 2,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = "Start memorizing",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = homeColors.iconColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = homeColors.iconColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Khatma Planner Card
                    Surface(
                        onClick = { viewModel.navigateTo(NoorDestination.QURAN_KHATMA) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = homeColors.outerCardBackground,
                        shadowElevation = if (isDark) 0.dp else 0.4.dp
                    ) {
                        val linkText = if (khatmaState != null) "${khatmaState!!.progressPercentage.toInt()}% done" else "Start a goal"

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(homeColors.badgeBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoStories,
                                        contentDescription = null,
                                        tint = homeColors.badgeText,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Text(
                                    text = "Khatma Plan",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = homeColors.titleText,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Set completion schedules & daily reading targets",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = homeColors.subtext,
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp
                                ),
                                minLines = 2,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = linkText,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = homeColors.badgeText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = homeColors.badgeText,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Fixed Search Bar (Below Hifz Studio & Khatma Cards)
            item(key = "quran_fixed_search_bar") {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = homeColors.outerCardBackground,
                    border = BorderStroke(1.dp, homeColors.dividerBorder.copy(alpha = 0.5f)),
                    shadowElevation = if (isDark) 0.dp else 0.4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .spotlightTarget(spotlightState, "quran_step_search_bar")
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search : e.g Yusuf, 12, or 2:255",
                                color = homeColors.subtext,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = homeColors.linkText,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (parsedAyahRef != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(end = 6.dp)
                                ) {
                                    Surface(
                                        onClick = {
                                            val targetSurah = QuranData.surahs.firstOrNull { it.number == parsedAyahRef.first }
                                            if (targetSurah != null) {
                                                viewModel.selectSurahForReading(targetSurah, parsedAyahRef.second)
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = homeColors.badgeBg,
                                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "Jump to Ayah",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = homeColors.badgeText,
                                                    fontSize = 11.5.sp
                                                )
                                            )
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                tint = homeColors.badgeText,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = homeColors.outerCardBackground,
                            unfocusedContainerColor = homeColors.outerCardBackground,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedTextColor = homeColors.titleText,
                            unfocusedTextColor = homeColors.titleText
                        ),
                        singleLine = true
                    )
                }
            }

            // Section results header & filter tabs
            item(key = "results_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Standard Madani Hafs",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = homeColors.subtext,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )

                    Surface(
                        shape = CircleShape,
                        color = homeColors.outerCardBackground,
                        border = BorderStroke(1.dp, homeColors.dividerBorder.copy(alpha = 0.4f)),
                        shadowElevation = if (isDark) 0.dp else 0.4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val tabs = listOf("All", "Juz", "Favorites")
                            tabs.forEach { tabName ->
                                val isSelected = selectedTab == tabName
                                val categoryBg = if (isSelected) homeColors.buttonFillBg else Color.Transparent
                                val categoryText = if (isSelected) homeColors.buttonFillText else homeColors.unselectedText

                                Surface(
                                    shape = CircleShape,
                                    color = categoryBg,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { selectedTab = tabName }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when (tabName) {
                                                "All" -> "All"
                                                "Juz" -> "Juz"
                                                "Favorites" -> "Favorites"
                                                else -> tabName
                                            },
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = categoryText,
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

            // If Juz tab is active: Horizontal Juz selector chips (Juz 1 to 30) below the tabs
            if (selectedTab == "Juz") {
                item(key = "juz_selector") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items((1..30).toList()) { juz ->
                            val isJuzSelected = selectedJuzNumber == juz
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isJuzSelected) homeColors.badgeBg else homeColors.innerContainer,
                                border = null,
                                modifier = Modifier.clickable { selectedJuzNumber = juz }
                            ) {
                                Text(
                                    text = "Juz $juz",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isJuzSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isJuzSelected) homeColors.badgeText else homeColors.titleText,
                                        fontSize = 12.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Favorites Empty State if tab is Favorites and nothing is saved
            if (selectedTab == "Favorites" && filteredSurahs.isEmpty()) {
                item(key = "favorites_empty") {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = homeColors.outerCardBackground,
                        border = null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = homeColors.subtext,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "No Favorites Saved Yet",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = homeColors.titleText
                                )
                            )
                            Text(
                                text = "Tap the heart icon on any Surah to view your favorites here.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = homeColors.subtext,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }

            // Surahs List Items
            itemsIndexed(
                items = filteredSurahs,
                key = { _, surah -> surah.number }
            ) { index, surah ->
                val isPlayingThis = isAudioPlaying && currentPlayingSurah.number == surah.number
                val isFav = favorites.any { fav ->
                    fav.title.contains("Surah ${surah.nameEnglish}", ignoreCase = true) ||
                            fav.arabicText == surah.nameArabic ||
                            fav.source.contains("Surah ${surah.number}", ignoreCase = true)
                }
                val isBookmarkedSurah = readingProgress != null && readingProgress?.surahNumber == surah.number
                val bookmarkedAyahNum = if (isBookmarkedSurah) (readingProgress?.ayahNumber ?: 0) else 0

                SurahListItemCard(
                    surah = surah,
                    isAudioPlaying = isPlayingThis,
                    isFavorite = isFav,
                    isBookmarked = isBookmarkedSurah,
                    bookmarkedAyah = bookmarkedAyahNum,
                    homeColors = homeColors,
                    onClick = {
                        if (isBookmarkedSurah && bookmarkedAyahNum > 0) {
                            viewModel.selectSurahForReading(surah, bookmarkedAyahNum)
                        } else {
                            viewModel.selectSurahForReading(surah)
                        }
                    },
                    onResumeReading = {
                        viewModel.selectSurahForReading(surah, bookmarkedAyahNum)
                    },
                    onPlayAudio = {
                        viewModel.playSurahAudio(surah)
                        viewModel.navigateTo(NoorDestination.QURAN_AUDIO_STREAM)
                    },
                    onToggleFavorite = {
                        viewModel.toggleSurahFavorite(surah)
                    },
                    favoriteModifier = if (index == 0) {
                        Modifier.spotlightTarget(spotlightState, "quran_step_favorite_icon")
                    } else {
                        Modifier
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }

    SpotlightOverlay(
        state = spotlightState,
        onDismiss = { spotlightState.dismiss() }
    )
    }
}

@Composable
fun SurahListItemCard(
    surah: Surah,
    isAudioPlaying: Boolean,
    isFavorite: Boolean,
    isBookmarked: Boolean = false,
    bookmarkedAyah: Int = 0,
    homeColors: HomePageColors,
    onClick: () -> Unit,
    onPlayAudio: () -> Unit,
    onToggleFavorite: () -> Unit,
    onResumeReading: () -> Unit = onClick,
    favoriteModifier: Modifier = Modifier,
    modifier: Modifier = Modifier
) {
    val simplifiedEnglishName = surah.nameEnglish.substringBefore(" (").trim()
    val amiriFamily = remember { FontFamily(Font(R.font.amiri_quran, FontWeight.Normal)) }
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = homeColors.outerCardBackground,
        shadowElevation = if (isDark) 0.dp else 0.4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val outlineColor = if (isAudioPlaying) {
                homeColors.iconColor
            } else {
                homeColors.badgeText
            }
            val badgeTextColor = if (isAudioPlaying) homeColors.iconColor else homeColors.badgeText

            FivePointStarSurahBadge(
                number = surah.number,
                isPlaying = isAudioPlaying,
                outlineColor = outlineColor,
                textColor = badgeTextColor,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable { onPlayAudio() }
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Surah English Name & Verses details
            Column(
                modifier = Modifier
                    .weight(1.5f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = simplifiedEnglishName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = homeColors.titleText,
                        fontSize = 15.sp,
                        letterSpacing = 0.15.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(com.example.ui.theme.NoorSpacing.TitleSubtextSpacingTight))

                Text(
                    text = "${surah.revelationType} • ${surah.totalVerses} Verses",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = homeColors.subtext,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Swap heart icon with Resume button if this surah is bookmarked
            if (isBookmarked) {
                Surface(
                    onClick = onResumeReading,
                    shape = RoundedCornerShape(10.dp),
                    color = homeColors.badgeBg,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clip(RoundedCornerShape(10.dp))
                        .then(favoriteModifier)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Resume Reading",
                            tint = homeColors.badgeText,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (bookmarkedAyah > 0) "Resume $bookmarkedAyah" else "Resume",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = homeColors.badgeText,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            } else {
                // Minimalist Softened Favorite Heart
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterVertically)
                        .then(favoriteModifier)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) homeColors.badgeText else homeColors.subtext.copy(alpha = 0.35f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Arabic Calligraphy
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = surah.nameArabic,
                    style = TextStyle(
                        fontFamily = amiriFamily,
                        fontWeight = FontWeight.Normal,
                        color = homeColors.titleText,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both
                        ),
                        platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false)
                    ),
                    modifier = Modifier.offset(y = (-1.5).dp),
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * 5-Corner Star Surah Badge that accommodates numbers up to 114 with perfect fit and balance.
 */
@Composable
fun FivePointStarSurahBadge(
    number: Int,
    isPlaying: Boolean,
    outlineColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 35.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f
            val outerRadius = minOf(w, h) / 2f - 1.2.dp.toPx()
            // Inner radius ratio 0.58f gives 5 distinct star corners with a spacious central core
            val innerRadius = outerRadius * 0.58f

            val path = Path().apply {
                for (i in 0 until 10) {
                    val angle = Math.toRadians((i * 36.0) - 90.0)
                    val r = if (i % 2 == 0) outerRadius else innerRadius
                    val x = cx + (r * Math.cos(angle)).toFloat()
                    val y = cy + (r * Math.sin(angle)).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }

            drawPath(
                path = path,
                color = if (isPlaying) outlineColor.copy(alpha = 0.15f) else outlineColor.copy(alpha = 0.05f)
            )
            drawPath(
                path = path,
                color = outlineColor,
                style = Stroke(
                    width = 1.2.dp.toPx(),
                    join = StrokeJoin.Round,
                    cap = StrokeCap.Round
                )
            )
        }

        if (isPlaying) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "Playing Audio",
                tint = outlineColor,
                modifier = Modifier.size(13.dp)
            )
        } else {
            val isThreeDigits = number >= 100
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = if (isThreeDigits) 9.sp else 11.5.sp,
                    letterSpacing = if (isThreeDigits) (-0.4).sp else 0.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
