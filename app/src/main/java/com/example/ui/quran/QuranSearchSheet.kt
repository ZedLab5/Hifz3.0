package com.example.ui.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DuaItem
import com.example.data.model.HadithItem
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.model.Verse
import com.example.data.quran.DuaData
import com.example.data.quran.HadithData
import com.example.data.quran.QuranData
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.theme.DarkPine
import com.example.ui.theme.ReadingThemeColors
import kotlinx.coroutines.delay

enum class SearchFilterCategory(val label: String) {
    ALL("All"),
    QURAN("Quran"),
    DUAS("Duas"),
    HADITH("Hadith")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuranSearchSheet(
    viewModel: MainViewModel,
    themeColors: ReadingThemeColors,
    onSelectVerse: (surahNumber: Int, verseNumber: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf(SearchFilterCategory.ALL) }

    // Search Result States
    var matchingSurahs by remember { mutableStateOf<List<Surah>>(emptyList()) }
    var matchingVerses by remember { mutableStateOf<List<Verse>>(emptyList()) }
    var matchingDuas by remember { mutableStateOf<List<DuaItem>>(emptyList()) }
    var matchingHadiths by remember { mutableStateOf<List<HadithItem>>(emptyList()) }
    var exactReferenceVerse by remember { mutableStateOf<Pair<Surah, Verse>?>(null) }

    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // Quick suggestion presets across Quran, Duas, Hadith, Topics
    val quickSuggestions = remember {
        listOf(
            QuickSearchChip("2:255", "Ayat al-Kursi (2:255)", Icons.Default.AutoAwesome),
            QuickSearchChip("travel", "✈️ Travel & Journey", Icons.Default.Search),
            QuickSearchChip("anxiety", "🤲 Anxiety & Peace", Icons.Default.Favorite),
            QuickSearchChip("morning", "🌅 Morning Azkar", Icons.Default.Search),
            QuickSearchChip("forgiveness", "🕊️ Forgiveness (Istighfar)", Icons.Default.Search),
            QuickSearchChip("health", "🌿 Health & Healing", Icons.Default.Search),
            QuickSearchChip("sleep", "🌙 Sleep & Night", Icons.Default.Search),
            QuickSearchChip("intentions", "📜 Hadith on Intentions", Icons.Default.MenuBook),
            QuickSearchChip("parents", "💖 Duas for Parents", Icons.Default.Favorite),
            QuickSearchChip("18:1", "Surah Al-Kahf (18:1)", Icons.Default.AutoAwesome),
            QuickSearchChip("knowledge", "📚 Seeking Knowledge", Icons.Default.MenuBook)
        )
    }

    // Debounced unified search across Quran (Surahs, Ayahs, Refs), Duas, and Hadith
    LaunchedEffect(searchQuery) {
        val rawQuery = searchQuery.trim()
        if (rawQuery.isBlank()) {
            matchingSurahs = emptyList()
            matchingVerses = emptyList()
            matchingDuas = emptyList()
            matchingHadiths = emptyList()
            exactReferenceVerse = null
            isSearching = false
            return@LaunchedEffect
        }

        isSearching = true
        delay(180) // 180ms debounce

        try {
            val qLower = rawQuery.lowercase()

            // 1. Check for Ayah reference (e.g. "2:255", "18:10", "36:82", "surah 2 ayah 255")
            val refPattern = Regex("""^(?:surah\s*)?(\d{1,3})[:\s,\-\.vVaA]+(?:ayah\s*|verse\s*)?(\d{1,3})$""", RegexOption.IGNORE_CASE)
            val match = refPattern.find(rawQuery)
            var directMatch: Pair<Surah, Verse>? = null

            if (match != null) {
                val surahNum = match.groupValues[1].toIntOrNull() ?: 0
                val verseNum = match.groupValues[2].toIntOrNull() ?: 0
                if (surahNum in 1..114 && verseNum >= 1) {
                    val targetSurah = QuranData.surahs.firstOrNull { it.number == surahNum }
                    if (targetSurah != null && verseNum <= targetSurah.totalVerses) {
                        val verseObj = viewModel.getExactVerse(surahNum, verseNum)
                        if (verseObj != null) {
                            directMatch = Pair(targetSurah, verseObj)
                        } else {
                            val fallbackVerse = Verse(
                                surahNumber = surahNum,
                                verseNumber = verseNum,
                                arabicText = "آية $verseNum من سورة ${targetSurah.nameArabic}",
                                transliteration = "${targetSurah.nameEnglish} Ayah $verseNum",
                                translation = "Jump to Surah ${targetSurah.nameEnglish} Ayah $verseNum"
                            )
                            directMatch = Pair(targetSurah, fallbackVerse)
                        }
                    }
                }
            }
            exactReferenceVerse = directMatch

            // 2. Surah name / number matches
            matchingSurahs = QuranData.surahs.filter { surah ->
                surah.nameEnglish.contains(rawQuery, ignoreCase = true) ||
                        surah.nameArabic.contains(rawQuery) ||
                        surah.englishMeaning.contains(rawQuery, ignoreCase = true) ||
                        surah.number.toString() == rawQuery
            }.take(8)

            // 3. Quran Verse text matching
            matchingVerses = viewModel.searchQuranVerses(rawQuery)

            // 4. Dua and Azkar Library search (Supports keyword / topic matching like 'travel', 'anxiety', 'morning')
            matchingDuas = viewModel.searchDuas(rawQuery)

            // 5. Hadith Library search (Supports topic matching like 'intentions', 'brotherhood', 'manners', etc.)
            matchingHadiths = viewModel.searchHadiths(rawQuery)

        } catch (_: Exception) {
            // Graceful search error recovery
        } finally {
            isSearching = false
        }
    }

    val totalQuranCount = (if (exactReferenceVerse != null) 1 else 0) + matchingSurahs.size + matchingVerses.size
    val totalDuasCount = matchingDuas.size
    val totalHadithCount = matchingHadiths.size
    val totalResults = totalQuranCount + totalDuasCount + totalHadithCount

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = themeColors.surface,
        modifier = Modifier.fillMaxHeight(0.94f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeColors.surface)
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 6.dp)
        ) {
            // Top Header Bar
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
                            .size(38.dp)
                            .background(themeColors.accent.copy(alpha = 0.14f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Unified Search",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = themeColors.arabicText
                            )
                        )
                        Text(
                            text = "Quran, Du'as & Hadith",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                color = themeColors.translationText.copy(alpha = 0.8f)
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close search",
                        tint = themeColors.translationText.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Input Field (Borderless Material 3)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search Quran, Duas, Hadith, topic (e.g. travel, anxiety)...",
                        color = themeColors.translationText.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = themeColors.accent,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = themeColors.accent
                        )
                    } else if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = themeColors.translationText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedContainerColor = themeColors.background,
                    unfocusedContainerColor = themeColors.background,
                    focusedTextColor = themeColors.arabicText,
                    unfocusedTextColor = themeColors.arabicText
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_search_input_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Tabs (All, Quran, Duas, Hadith, Reciters)
            if (searchQuery.isNotBlank()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(SearchFilterCategory.values()) { category ->
                        val isSelected = selectedCategoryFilter == category
                        val count = when (category) {
                            SearchFilterCategory.ALL -> totalResults
                            SearchFilterCategory.QURAN -> totalQuranCount
                            SearchFilterCategory.DUAS -> totalDuasCount
                            SearchFilterCategory.HADITH -> totalHadithCount
                        }

                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { selectedCategoryFilter = category },
                            shape = CircleShape,
                            color = if (isSelected) themeColors.accent else themeColors.background,
                            border = null
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = category.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) {
                                            if (themeColors.isDark) DarkPine else Color.White
                                        } else {
                                            themeColors.arabicText
                                        },
                                        fontSize = 12.sp
                                    )
                                )
                                if (count > 0) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (isSelected) {
                                                    if (themeColors.isDark) DarkPine.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.25f)
                                                } else {
                                                    themeColors.accent.copy(alpha = 0.15f)
                                                },
                                                shape = CircleShape
                                            )
                                            .padding(horizontal = 6.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "$count",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) {
                                                    if (themeColors.isDark) DarkPine else Color.White
                                                } else {
                                                    themeColors.accent
                                                }
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Results / Empty State / Suggestions Area
            if (searchQuery.isBlank()) {
                // Topic Discovery & Quick Suggestions
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item(key = "quick_topics_header") {
                        Column(modifier = Modifier.padding(top = 4.dp)) {
                            Text(
                                text = "Search by Topic, Dua & Reference",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.translationText.copy(alpha = 0.85f),
                                    fontSize = 12.5.sp
                                )
                            )
                            Text(
                                text = "Tap any topic below to discover relevant Ayahs, Duas & Hadiths",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = themeColors.translationText.copy(alpha = 0.65f)
                                )
                            )
                        }
                    }

                    item(key = "quick_chips_grid") {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickSuggestions.forEach { chip ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { searchQuery = chip.query },
                                    shape = RoundedCornerShape(12.dp),
                                    color = themeColors.background,
                                    border = null
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = chip.icon,
                                            contentDescription = null,
                                            tint = themeColors.accent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = chip.label,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = themeColors.arabicText
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Helpful Tips Card
                    item(key = "search_tips_card") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(themeColors.background, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = themeColors.accent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Smart Search Capabilities",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.accent
                                    )
                                )
                            }
                            Text(
                                text = "• Ayah References: Type \"2:255\" or \"18:10\" to jump directly.\n• Du'a Topics: Search keywords like \"travel\", \"anxiety\", \"sleep\", \"health\", \"forgiveness\", or \"parents\".\n• Hadith Library: Search \"intentions\", \"mercy\", \"anger\", or \"patience\".\n• Audio & Qaris: Search \"reciter\", \"recitation\", or reciter names (e.g. \"Alafasy\", \"Abdul Basit\").",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = themeColors.translationText.copy(alpha = 0.85f),
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            } else {
                // Active Search Results List
                if (!isSearching && totalResults == 0) {
                    // Empty Results State
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = themeColors.translationText.copy(alpha = 0.5f),
                            modifier = Modifier.size(42.dp)
                        )
                        Text(
                            text = "No results for \"$searchQuery\"",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText
                            )
                        )
                        Text(
                            text = "Try searching by topic (e.g. anxiety, travel, morning), Ayah ref (e.g. 2:255), Surah name, or \"reciter\".",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = themeColors.translationText.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ----------------------------------------------------
                        // SECTION 1: DIRECT AYAH REFERENCE MATCH
                        // ----------------------------------------------------
                        if (selectedCategoryFilter == SearchFilterCategory.ALL || selectedCategoryFilter == SearchFilterCategory.QURAN) {
                            exactReferenceVerse?.let { (surah, verse) ->
                                item(key = "exact_ref_header") {
                                    SearchSectionHeader(
                                        title = "QURAN - DIRECT AYAH MATCH",
                                        badgeCount = 1,
                                        themeColors = themeColors
                                    )
                                }
                                item(key = "exact_ref_${surah.number}_${verse.verseNumber}") {
                                    SearchResultVerseCard(
                                        surah = surah,
                                        verse = verse,
                                        themeColors = themeColors,
                                        isDirectMatch = true,
                                        onClick = {
                                            onSelectVerse(surah.number, verse.verseNumber)
                                        }
                                    )
                                }
                            }

                            // SECTION 2: MATCHING SURAHS
                            if (matchingSurahs.isNotEmpty()) {
                                item(key = "surahs_header") {
                                    SearchSectionHeader(
                                        title = "QURAN - SURAHS",
                                        badgeCount = matchingSurahs.size,
                                        themeColors = themeColors
                                    )
                                }
                                items(matchingSurahs, key = { "surah_${it.number}" }) { surah ->
                                    SearchResultSurahCard(
                                        surah = surah,
                                        themeColors = themeColors,
                                        onClick = {
                                            onSelectVerse(surah.number, 1)
                                        }
                                    )
                                }
                            }

                            // SECTION 3: MATCHING VERSES
                            if (matchingVerses.isNotEmpty()) {
                                item(key = "verses_header") {
                                    SearchSectionHeader(
                                        title = "QURAN - VERSES",
                                        badgeCount = matchingVerses.size,
                                        themeColors = themeColors
                                    )
                                }
                                items(matchingVerses, key = { "verse_${it.surahNumber}_${it.verseNumber}" }) { verse ->
                                    val surah = QuranData.surahs.firstOrNull { it.number == verse.surahNumber }
                                    if (surah != null) {
                                        SearchResultVerseCard(
                                            surah = surah,
                                            verse = verse,
                                            themeColors = themeColors,
                                            isDirectMatch = false,
                                            onClick = {
                                                onSelectVerse(surah.number, verse.verseNumber)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // ----------------------------------------------------
                        // SECTION 4: DUAS & SUPPLICATIONS
                        // ----------------------------------------------------
                        if ((selectedCategoryFilter == SearchFilterCategory.ALL || selectedCategoryFilter == SearchFilterCategory.DUAS) &&
                            matchingDuas.isNotEmpty()
                        ) {
                            item(key = "duas_section_header") {
                                SearchSectionHeader(
                                    title = "DU'AS & SUPPLICATIONS",
                                    badgeCount = matchingDuas.size,
                                    themeColors = themeColors
                                )
                            }
                            items(matchingDuas, key = { "dua_${it.id}" }) { dua ->
                                val isBookmarked = favorites.any { it.title == dua.title }
                                SearchResultDuaCard(
                                    dua = dua,
                                    isBookmarked = isBookmarked,
                                    themeColors = themeColors,
                                    onOpenReader = {
                                        onDismiss()
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
                                            "${dua.title}\n\n${dua.arabicText}\n\n${dua.translation}\n\n[${dua.reference}]"
                                        )
                                        clipboard.setPrimaryClip(clip)
                                        viewModel.showToast("Du'a copied to clipboard")
                                    }
                                )
                            }
                        }

                        // ----------------------------------------------------
                        // SECTION 5: HADITH LIBRARY
                        // ----------------------------------------------------
                        if ((selectedCategoryFilter == SearchFilterCategory.ALL || selectedCategoryFilter == SearchFilterCategory.HADITH) &&
                            matchingHadiths.isNotEmpty()
                        ) {
                            item(key = "hadith_section_header") {
                                SearchSectionHeader(
                                    title = "HADITH LIBRARY",
                                    badgeCount = matchingHadiths.size,
                                    themeColors = themeColors
                                )
                            }
                            items(matchingHadiths, key = { "hadith_${it.id}" }) { hadith ->
                                SearchResultHadithCard(
                                    hadith = hadith,
                                    themeColors = themeColors,
                                    onCopy = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText(
                                            hadith.book,
                                            "${hadith.book} (${hadith.hadithNumber})\nNarrator: ${hadith.narrator}\n\n${hadith.arabicText}\n\n${hadith.translation}\n\nGrade: ${hadith.grade}"
                                        )
                                        clipboard.setPrimaryClip(clip)
                                        viewModel.showToast("Hadith copied to clipboard")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// REUSABLE COMPONENTS FOR SECTION HEADERS & RESULT CARDS (BORDERLESS)
// -------------------------------------------------------------------------

data class QuickSearchChip(
    val query: String,
    val label: String,
    val icon: ImageVector
)

@Composable
private fun SearchSectionHeader(
    title: String,
    badgeCount: Int,
    themeColors: ReadingThemeColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
                color = themeColors.accent,
                fontSize = 11.sp
            )
        )
        if (badgeCount > 0) {
            Text(
                text = "$badgeCount found",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = themeColors.translationText.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
private fun SearchResultSurahCard(
    surah: Surah,
    themeColors: ReadingThemeColors,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = themeColors.background,
        border = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(themeColors.accent.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${surah.number}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accent
                        )
                    )
                }
                Column {
                    Text(
                        text = surah.nameEnglish,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = themeColors.arabicText
                        )
                    )
                    Text(
                        text = "${surah.englishMeaning} • ${surah.totalVerses} Ayahs • ${surah.revelationType}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = themeColors.translationText.copy(alpha = 0.8f)
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = surah.nameArabic,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.accent,
                        fontSize = 16.sp
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open Surah",
                    tint = themeColors.translationText.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchResultVerseCard(
    surah: Surah,
    verse: Verse,
    themeColors: ReadingThemeColors,
    isDirectMatch: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (isDirectMatch) themeColors.accent.copy(alpha = 0.08f) else themeColors.background,
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
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
                    Box(
                        modifier = Modifier
                            .background(themeColors.accent.copy(alpha = 0.16f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${surah.number}:${verse.verseNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = themeColors.accent
                            )
                        )
                    }
                    Text(
                        text = "Surah ${surah.nameEnglish}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText,
                            fontSize = 13.5.sp
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Jump to Ayah",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = themeColors.accent,
                            fontSize = 11.5.sp
                        )
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = themeColors.accent,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (verse.arabicText.isNotBlank()) {
                val formattedTextWithMarker = remember(verse.arabicText, verse.verseNumber, themeColors) {
                    buildAnnotatedString {
                        append(verse.arabicText)
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
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 26.sp,
                        color = themeColors.arabicText,
                        textAlign = TextAlign.Right,
                        textDirection = TextDirection.Rtl
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (verse.translation.isNotBlank()) {
                Text(
                    text = verse.translation,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp,
                        color = themeColors.translationText
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SearchResultDuaCard(
    dua: DuaItem,
    isBookmarked: Boolean,
    themeColors: ReadingThemeColors,
    onOpenReader: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCopy: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onOpenReader),
        shape = RoundedCornerShape(14.dp),
        color = themeColors.background,
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Category Badge + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .background(themeColors.accent.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = dua.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                color = themeColors.accent
                            )
                        )
                    }
                    Text(
                        text = dua.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText,
                            fontSize = 13.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) themeColors.accent else themeColors.translationText.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = themeColors.translationText.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Arabic Text
            if (dua.arabicText.isNotBlank()) {
                Text(
                    text = dua.arabicText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.5.sp,
                        lineHeight = 25.sp,
                        color = themeColors.arabicText,
                        textAlign = TextAlign.Right,
                        textDirection = TextDirection.Rtl
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Translation
            if (dua.translation.isNotBlank()) {
                Text(
                    text = dua.translation,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = themeColors.translationText
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Reference & Occasion Note
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (dua.occasion.isNotBlank()) "${dua.reference} • ${dua.occasion}" else dua.reference,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.5.sp,
                        color = themeColors.translationText.copy(alpha = 0.7f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Read in Azkar",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accent,
                            fontSize = 11.sp
                        )
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = themeColors.accent,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultHadithCard(
    hadith: HadithItem,
    themeColors: ReadingThemeColors,
    onCopy: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(14.dp),
        color = themeColors.background,
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Book & Number Header + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(themeColors.accent.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${hadith.book} • ${hadith.hadithNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                color = themeColors.accent
                            )
                        )
                    }
                    Text(
                        text = hadith.chapter,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Hadith",
                        tint = themeColors.translationText.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Narrator
            Text(
                text = "Narrated by ${hadith.narrator}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = themeColors.accent
                )
            )

            // Arabic Text
            if (hadith.arabicText.isNotBlank()) {
                Text(
                    text = hadith.arabicText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        color = themeColors.arabicText,
                        textAlign = TextAlign.Right,
                        textDirection = TextDirection.Rtl
                    ),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Translation
            if (hadith.translation.isNotBlank()) {
                Text(
                    text = hadith.translation,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = themeColors.translationText
                    ),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Expanded Explanation / Topic Tags
            if (isExpanded && hadith.explanation.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(themeColors.accent.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "💡 ${hadith.explanation}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            color = themeColors.arabicText,
                            lineHeight = 16.sp
                        )
                    )
                }
            }

            // Footer info: Grade + Expand Prompt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Grade: ${hadith.grade}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = themeColors.translationText.copy(alpha = 0.7f)
                    )
                )

                Text(
                    text = if (isExpanded) "Show Less" else "Tap for Full Text & Insights",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.accent
                    )
                )
            }
        }
    }
}


