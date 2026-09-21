package com.example.ui.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.HadithItem
import com.example.data.quran.HadithCollectionInfo
import com.example.data.quran.HadithRepository
import com.example.ui.MainViewModel
import com.example.ui.components.NoConnectionBanner
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.AmiriQuranFontFamily
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.GoldTintBgDark
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.ReadingThemes
import com.example.ui.theme.SoftTealTint
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithLibraryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val themeColors = remember(isDarkMode) { if (isDarkMode) ReadingThemes.ObsidianNight else ReadingThemes.MadaniCrisp }
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCollection by remember { mutableStateOf(HadithRepository.collections.first()) }
    var selectedTopic by remember { mutableStateOf("All Topics") }
    var arabicTextSizeSp by remember { mutableStateOf(21.sp) }

    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var hadithList by remember { mutableStateOf<List<HadithItem>>(emptyList()) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun loadHadiths() {
        scope.launch {
            isLoading = true
            isError = false
            errorMessage = ""

            val result = HadithRepository.searchOrFilterHadiths(
                query = searchQuery,
                selectedCollectionId = selectedCollection.id,
                selectedTopic = selectedTopic
            )

            result.fold(
                onSuccess = { items ->
                    hadithList = items
                    isLoading = false
                    isError = false
                },
                onFailure = { err ->
                    isLoading = false
                    isError = true
                    errorMessage = err.localizedMessage ?: "Unable to connect to remote Hadith server."
                }
            )
        }
    }

    LaunchedEffect(selectedCollection, selectedTopic) {
        loadHadiths()
    }

    Scaffold(
        topBar = {
            NoorTopBar(
                title = "Hadith Library • المكتبة الحديثية",
                eyebrow = "SUNNAH & TRADITION",
                subtitle = "${selectedCollection.nameEn} • Authentic Collections",
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back",
                isDark = isDarkMode,
                themeColors = themeColors,
                actions = {
                    NoorGlassIconButton(
                        onClick = {
                            arabicTextSizeSp = if (arabicTextSizeSp >= 26.sp) 18.sp else (arabicTextSizeSp.value + 3f).sp
                        },
                        icon = Icons.Default.FormatSize,
                        contentDescription = "Adjust Text Size"
                    )
                }
            )
        },
        containerColor = themeColors.background,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        loadHadiths()
                    },
                    placeholder = {
                        Text(
                            text = "Search by topic, narrator, or keyword...",
                            color = themeColors.translationText.copy(alpha = 0.6f),
                            fontSize = 13.5.sp
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
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                loadHadiths()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = themeColors.translationText
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedContainerColor = themeColors.surface,
                        unfocusedContainerColor = themeColors.surface,
                        focusedTextColor = themeColors.arabicText,
                        unfocusedTextColor = themeColors.arabicText
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Collection Tabs Header
            ScrollableTabRow(
                selectedTabIndex = HadithRepository.collections.indexOf(selectedCollection).coerceAtLeast(0),
                containerColor = themeColors.surface,
                contentColor = themeColors.arabicText,
                edgePadding = 16.dp,
                divider = {},
                indicator = { tabPositions ->
                    val index = HadithRepository.collections.indexOf(selectedCollection).coerceAtLeast(0)
                    if (index < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                            color = themeColors.accent
                        )
                    }
                }
            ) {
                HadithRepository.collections.forEach { col ->
                    val isSelected = col.id == selectedCollection.id
                    Tab(
                        selected = isSelected,
                        onClick = { selectedCollection = col },
                        text = {
                            Text(
                                text = col.nameEn,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) themeColors.arabicText else themeColors.translationText,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    )
                }
            }

            // Topic Selector Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(HadithRepository.topicsList) { topic ->
                    val isSelected = topic == selectedTopic
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) themeColors.accent else themeColors.surface,
                        border = null,
                        modifier = Modifier.clickable { selectedTopic = topic }
                    ) {
                        Text(
                            text = topic,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else themeColors.translationText,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            // Main Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(color = themeColors.accent)
                                Text(
                                    text = "Fetching remote authentic collection...",
                                    style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                                )
                            }
                        }
                    }

                    isError -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 24.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            NoConnectionBanner(
                                onRetry = { loadHadiths() },
                                title = "Remote Connection Error",
                                message = errorMessage,
                                themeColors = themeColors
                            )
                        }
                    }

                    hadithList.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = themeColors.translationText.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "No hadiths match your search or topic filter.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = themeColors.translationText)
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(bottom = 32.dp, top = 4.dp)
                        ) {
                            items(hadithList, key = { it.id }) { hadith ->
                                val isFav = favorites.any { f -> f.type == "HADITH" && f.title == hadith.id }

                                HadithCardItem(
                                    hadith = hadith,
                                    arabicTextSizeSp = arabicTextSizeSp,
                                    isFavorite = isFav,
                                    themeColors = themeColors,
                                    onToggleFavorite = {
                                        viewModel.toggleFavorite(
                                            itemType = "HADITH",
                                            title = hadith.id,
                                            subtitle = hadith.arabicText,
                                            details = hadith.translation,
                                            source = "${hadith.book} (${hadith.hadithNumber})"
                                        )
                                    },
                                    onCopy = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val textToCopy = "${hadith.book} - ${hadith.hadithNumber}\n" +
                                                "Narrator: ${hadith.narrator}\n\n" +
                                                "${hadith.arabicText}\n\n" +
                                                "${hadith.translation}\n\n" +
                                                "Grade: ${hadith.grade}"
                                        val clip = ClipData.newPlainText("Hadith Text", textToCopy)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Hadith copied to clipboard", Toast.LENGTH_SHORT).show()
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

@Composable
private fun HadithCardItem(
    hadith: HadithItem,
    arabicTextSizeSp: androidx.compose.ui.unit.TextUnit,
    isFavorite: Boolean,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onToggleFavorite: () -> Unit,
    onCopy: () -> Unit
) {
    val isDark = themeColors.isDark

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = themeColors.surface,
        border = null,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Collection & Hadith Number + Grade Badge on Left/Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Collection and Number
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${hadith.book} • ${hadith.hadithNumber}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText,
                            fontSize = 13.5.sp
                        )
                    )
                    Text(
                        text = hadith.narrator,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.translationText,
                            fontSize = 11.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right: Authenticity Grade Badge (Sahih / Hasan / etc.) - NO BORDER
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) GoldTintBgDark else GoldBadgeBg,
                    border = null
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isDark) SecondaryGoldLight else MetallicGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = hadith.grade.ifBlank { "Sahih" },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) SecondaryGoldLight else MetallicGold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Arabic Text
            if (hadith.arabicText.isNotBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) Color(0xFF161E20) else SoftTealTint.copy(alpha = 0.4f),
                    border = null
                ) {
                    Text(
                        text = hadith.arabicText,
                        fontFamily = AmiriQuranFontFamily,
                        fontSize = arabicTextSizeSp,
                        lineHeight = (arabicTextSizeSp.value * 1.6f).sp,
                        color = themeColors.arabicText,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    )
                }
            }

            // Translation
            Text(
                text = hadith.translation,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = themeColors.translationText,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp
                )
            )

            // Explanation / Context
            if (hadith.explanation.isNotBlank()) {
                Text(
                    text = hadith.explanation,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = themeColors.translationText.copy(alpha = 0.8f),
                        fontSize = 11.5.sp
                    )
                )
            }

            // Bottom Actions Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    hadith.topics.take(3).forEach { topic ->
                        Surface(
                            shape = CircleShape,
                            color = themeColors.accent.copy(alpha = 0.12f),
                            border = null
                        ) {
                            Text(
                                text = "#$topic",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = themeColors.accent,
                                    fontSize = 10.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Hadith",
                            tint = themeColors.translationText,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isFavorite) themeColors.accent else themeColors.translationText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
