package com.example.ui.duas

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import com.example.data.quran.DuaData
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.AmiriQuranFontFamily
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.GoldTintBgDark
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.NoorTopBarGradient
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealLight
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.ReadingThemes
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.SoftTealTint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuasLibraryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val themeColors = remember(isDarkMode) { if (isDarkMode) ReadingThemes.ObsidianNight else ReadingThemes.MadaniCrisp }

    val searchQuery by viewModel.duasSearchQuery.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val showArabicInCards by viewModel.showArabicInAzkarCards.collectAsStateWithLifecycle()
    val azkarTextSize by viewModel.azkarTextSize.collectAsStateWithLifecycle()
    val isAutoScrollEnabled by viewModel.isAzkarAutoScrollEnabled.collectAsStateWithLifecycle()
    val isHapticEnabled by viewModel.isAzkarHapticEnabled.collectAsStateWithLifecycle()
    val showTransliteration by viewModel.showAzkarTransliteration.collectAsStateWithLifecycle()
    val showBenefits by viewModel.showAzkarBenefits.collectAsStateWithLifecycle()
    var showSettingsSheet by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("All") }

    val context = LocalContext.current

    val searchResults = remember(searchQuery) {
        if (searchQuery.isBlank()) emptyList() else {
            DuaData.categorizedDuas.filter { dua ->
                dua.title.contains(searchQuery, ignoreCase = true) ||
                        dua.translation.contains(searchQuery, ignoreCase = true) ||
                        dua.arabicText.contains(searchQuery) ||
                        dua.category.contains(searchQuery, ignoreCase = true) ||
                        dua.occasion.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val favoriteDuas = remember(favorites, searchQuery) {
        val allFavs = DuaData.categorizedDuas.filter { dua ->
            favorites.any { fav -> fav.type == "DUA" && fav.title == dua.title }
        }
        if (searchQuery.isBlank()) {
            allFavs
        } else {
            allFavs.filter { dua ->
                dua.title.contains(searchQuery, ignoreCase = true) ||
                        dua.translation.contains(searchQuery, ignoreCase = true) ||
                        dua.arabicText.contains(searchQuery) ||
                        dua.category.contains(searchQuery, ignoreCase = true) ||
                        dua.occasion.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            NoorTopBar(
                title = "Du'as & Azkar",
                eyebrow = "HISN AL-MUSLIM",
                subtitle = "Authentic Supplications & Remembrances",
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back",
                isDark = themeColors.isDark,
                themeColors = themeColors,
                actions = {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Bar with spacing from above
            item(key = "search_bar") {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.duasSearchQuery.value = it },
                        placeholder = {
                            Text(
                                text = "Search du'as by keyword, situation, Arabic...",
                                style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText.copy(alpha = 0.6f))
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = themeColors.accent
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.duasSearchQuery.value = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = themeColors.translationText.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = themeColors.surface,
                            unfocusedContainerColor = themeColors.surface,
                            focusedTextColor = themeColors.arabicText,
                            unfocusedTextColor = themeColors.arabicText,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Streamlined Filter Navigation Tabs: "All" and "Favorites" (Fully Rounded Capsule design)
            item(key = "tab_navigation") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    color = themeColors.surface,
                    shadowElevation = if (themeColors.isDark) 0.dp else 0.4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val tabs = listOf("All", "Favorites")
                        tabs.forEach { tab ->
                            val isSelected = selectedTab == tab
                            val backgroundBrush = if (isSelected) {
                                if (themeColors.isDark) PrimaryTealDark else PrimaryTealLight
                            } else {
                                Color.Transparent
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .background(backgroundBrush)
                                    .clickable { selectedTab = tab }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else themeColors.arabicText,
                                        fontSize = 13.5.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTab == "All") {
                if (searchQuery.isNotBlank()) {
                    // Search Results Mode
                    item(key = "search_header") {
                        Text(
                            text = "Search Results (${searchResults.size})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText
                            )
                        )
                    }

                    if (searchResults.isEmpty()) {
                        item(key = "no_search_results") {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = themeColors.surface,
                                shadowElevation = if (themeColors.isDark) 0.dp else 0.4.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = themeColors.translationText,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "No Du'as found for \"$searchQuery\"",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.arabicText
                                        )
                                    )
                                    Text(
                                        text = "Try searching for morning, protection, travel, forgiveness, or peace",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = themeColors.translationText,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        items(searchResults, key = { it.id }) { dua ->
                            val isBookmarked = favorites.any { it.title == dua.title }
                            SearchResultDuaCard(
                                dua = dua,
                                isBookmarked = isBookmarked,
                                showTransliteration = showTransliteration,
                                themeColors = themeColors,
                                onOpenCategory = {
                                    viewModel.openDuaCategory(dua.category)
                                },
                                onToggleBookmark = {
                                    viewModel.toggleFavorite(
                                        itemType = "DUA",
                                        title = dua.title,
                                        subtitle = dua.arabicText,
                                        details = "${dua.translation}\n\n[${dua.reference}]"
                                    )
                                },
                                onCopy = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText(
                                        dua.title,
                                        "${dua.arabicText}\n\n${dua.transliteration}\n\n${dua.translation}\n[${dua.reference}]"
                                    )
                                    clipboard.setPrimaryClip(clip)
                                    viewModel.showToast("Dua copied to clipboard!")
                                }
                            )
                        }
                    }
                } else {
                    // Category Browsing Mode - Collections Directory
                    item(key = "categories_header") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Authentic Azkar Collections",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = "${DuaData.categories.size} Categories",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = themeColors.accent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    // Category Cards List
                    items(DuaData.categories, key = { it.id }) { category ->
                        DuaCategorySelectionCard(
                            category = category,
                            themeColors = themeColors,
                            onClick = {
                                viewModel.openDuaCategory(category.id)
                            }
                        )
                    }
                }
            } else {
                // Favorites Tab
                item(key = "favorites_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Saved Remembrances",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "${favoriteDuas.size} Saved",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = themeColors.accent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                if (favoriteDuas.isEmpty()) {
                    item(key = "empty_favorites") {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = themeColors.surface,
                            shadowElevation = if (themeColors.isDark) 0.dp else 0.4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 36.dp, horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(themeColors.accent.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = themeColors.accent,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Text(
                                    text = "No Bookmarked Supplications",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.arabicText
                                    )
                                )
                                Text(
                                    text = "Tap the bookmark icon inside any Du'a or Zikr card to save it here for quick access.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeColors.translationText,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 17.sp
                                    )
                                )
                            }
                        }
                    }
                } else {
                    items(favoriteDuas, key = { "fav_${it.id}" }) { dua ->
                        SearchResultDuaCard(
                            dua = dua,
                            isBookmarked = true,
                            showTransliteration = showTransliteration,
                            themeColors = themeColors,
                            onOpenCategory = {
                                viewModel.openDuaCategory(dua.category)
                            },
                            onToggleBookmark = {
                                viewModel.toggleFavorite(
                                    itemType = "DUA",
                                    title = dua.title,
                                    subtitle = dua.arabicText,
                                    details = "${dua.translation}\n\n[${dua.reference}]"
                                )
                            },
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    dua.title,
                                    "${dua.arabicText}\n\n${dua.transliteration}\n\n${dua.translation}\n[${dua.reference}]"
                                )
                                clipboard.setPrimaryClip(clip)
                                viewModel.showToast("Dua copied to clipboard!")
                            }
                        )
                    }
                }
            }
        }
    }

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
            categoryName = "All Azkar",
            onDismiss = { showSettingsSheet = false },
            onToggleArabic = { viewModel.setShowArabicInAzkarCards(it) },
            onSelectTextSize = { viewModel.setAzkarTextSize(it) },
            onToggleAutoScroll = { viewModel.setAzkarAutoScroll(it) },
            onToggleHaptic = { viewModel.setAzkarHaptic(it) },
            onToggleTransliteration = { viewModel.setAzkarTransliteration(it) },
            onToggleBenefits = { viewModel.setAzkarBenefits(it) },
            onResetCategory = {
                viewModel.resetCategoryDuaCounts("Morning Azkar")
                viewModel.resetCategoryDuaCounts("Evening Azkar")
                showSettingsSheet = false
            }
        )
    }
}

@Composable
fun DuaCategorySelectionCard(
    category: DuaCategory,
    themeColors: ReadingThemeColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryIcon = when (category.iconType) {
        "morning" -> Icons.Default.WbSunny
        "evening" -> Icons.Default.Bedtime
        "salah" -> Icons.Default.Mosque
        "sleep" -> Icons.Default.Bedtime
        "shield" -> Icons.Default.Shield
        "health" -> Icons.Default.Healing
        "travel" -> Icons.Default.Flight
        "praise" -> Icons.Default.AutoAwesome
        "forgiveness" -> Icons.Default.Healing
        else -> Icons.Default.MenuBook
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = themeColors.surface,
        shadowElevation = if (themeColors.isDark) 0.dp else 0.4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Category Icon Box
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(themeColors.accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = category.titleEnglish,
                        tint = themeColors.accent,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = category.titleEnglish,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText,
                                fontSize = 16.sp
                            )
                        )

                        // Count Badge with gold color theme
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (themeColors.isDark) GoldTintBgDark else GoldBadgeBg,
                            border = null
                        ) {
                            Text(
                                text = "${category.itemCount} Du'as",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (themeColors.isDark) SecondaryGoldDark else SecondaryGoldLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.translationText,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Forward Navigation Arrow
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(themeColors.accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open ${category.titleEnglish}",
                    tint = themeColors.accent,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Composable
fun SearchResultDuaCard(
    dua: DuaItem,
    isBookmarked: Boolean,
    showTransliteration: Boolean = false,
    themeColors: ReadingThemeColors,
    onOpenCategory: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCopy: () -> Unit,
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = themeColors.accent.copy(alpha = 0.12f),
                    modifier = Modifier.clickable(onClick = onOpenCategory)
                ) {
                    Text(
                        text = dua.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accent,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = themeColors.translationText,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    IconButton(onClick = onToggleBookmark, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) MetallicGold else themeColors.translationText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = dua.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.arabicText,
                    fontSize = 15.sp
                )
            )

            // 1. Primary English Translation
            Text(
                text = dua.translation,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    color = themeColors.translationText,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )
            )

            // 2. Secondary Phonetic Transliteration (Respects showTransliteration preference)
            if (showTransliteration && dua.transliteration.isNotBlank()) {
                Text(
                    text = dua.transliteration,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = themeColors.translationText.copy(alpha = 0.85f),
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    )
                )
            }

            // 3. Tertiary Traditional Arabic Script (Regular weight) with soft divider above
            if (dua.arabicText.isNotBlank()) {
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
                        fontFamily = AmiriQuranFontFamily,
                        color = themeColors.arabicText,
                        fontSize = 20.sp,
                        lineHeight = 32.sp
                    ),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = "${dua.occasion} • ${dua.reference}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = themeColors.translationText.copy(alpha = 0.75f),
                    fontSize = 11.sp
                )
            )
        }
    }
}
