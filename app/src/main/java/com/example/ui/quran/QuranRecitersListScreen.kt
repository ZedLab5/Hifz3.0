package com.example.ui.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Reciter
import com.example.data.quran.QuranData
import com.example.ui.MainViewModel
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.ReadingThemes
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranRecitersListScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val reciters = viewModel.reciters
    val currentSelectedReciter by viewModel.selectedReciter.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val readingThemeName by viewModel.sharedReadingTheme.collectAsStateWithLifecycle()
    val themeColors = remember(readingThemeName) { ReadingThemes.getThemeByName(readingThemeName) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filterOptions = listOf(
        "All" to if (isArabic) "الكل" else "All",
        "Murattal" to if (isArabic) "مرتل" else "Murattal",
        "Mujawwad" to if (isArabic) "مجود" else "Mujawwad",
        "Saudi Arabia" to if (isArabic) "السعودية" else "Saudi",
        "Egypt" to if (isArabic) "مصر" else "Egypt"
    )

    val filteredReciters = reciters.filter { reciter ->
        val matchesQuery = searchQuery.isBlank() ||
                reciter.name.contains(searchQuery, ignoreCase = true) ||
                reciter.nameAr.contains(searchQuery, ignoreCase = true) ||
                reciter.style.contains(searchQuery, ignoreCase = true) ||
                reciter.country.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "All" -> true
            "Murattal" -> reciter.style.contains("Murattal", ignoreCase = true) || reciter.styleAr.contains("مرتل", ignoreCase = true)
            "Mujawwad" -> reciter.style.contains("Mujawwad", ignoreCase = true) || reciter.styleAr.contains("مجود", ignoreCase = true)
            "Saudi Arabia" -> reciter.country.contains("Saudi", ignoreCase = true)
            "Egypt" -> reciter.country.contains("Egypt", ignoreCase = true)
            else -> true
        }

        matchesQuery && matchesFilter
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            NoorTopBar(
                title = if (isArabic) "قراء القرآن الكريم" else "Quran Reciters",
                eyebrow = if (isArabic) "تلاوات عطرة" else "AUTHENTIC RECITATIONS",
                subtitle = if (isArabic) "${reciters.size} قارئاً معتمداً" else "${reciters.size} World Renowned Qaris",
                isDark = themeColors.isDark,
                themeColors = themeColors,
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = stringResource(R.string.action_back)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search Input
            item(key = "search_bar") {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = if (isArabic) "ابحث عن قارئ..." else "Search reciter or country...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (searchQuery.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true
                )
            }

            // Quick Filter Chips
            item(key = "filters_row") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(filterOptions) { (key, label) ->
                        val isSelected = selectedFilter == key
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) {
                                themeColors.accent
                            } else {
                                themeColors.surface
                            },
                            border = null,
                            modifier = Modifier.clickable { selectedFilter = key }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) {
                                        if (themeColors.isDark) DarkPine else Color.White
                                    } else {
                                        if (themeColors.isDark) themeColors.translationText else DarkPine
                                    },
                                    fontSize = 12.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Reciters Count Indicator
            item(key = "results_count") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "${filteredReciters.size} قارئ متاح" else "${filteredReciters.size} Reciters available",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = themeColors.translationText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            // Reciters List
            if (filteredReciters.isEmpty()) {
                item(key = "empty_reciters") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = themeColors.translationText.copy(alpha = 0.5f),
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = if (isArabic) "لم يتم العثور على أي قارئ" else "No reciters found",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText
                            )
                        )
                        Text(
                            text = if (isArabic) "جرب البحث باسم آخر أو تغيير التصفية" else "Try searching with a different name or clearing filters",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = themeColors.translationText
                            )
                        )
                    }
                }
            }

            items(filteredReciters, key = { it.id }) { reciter ->
                val isSelected = reciter.id == currentSelectedReciter.id
                val reciterName = if (isArabic) reciter.nameAr.ifBlank { reciter.name } else reciter.name
                val reciterStyle = if (isArabic) reciter.styleAr.ifBlank { reciter.style } else reciter.style

                val cardBg = if (isSelected) {
                    if (themeColors.isDark) themeColors.surface else SoftTealTint.copy(alpha = 0.5f)
                } else {
                    themeColors.surface
                }

                val cardBorder = if (isSelected) {
                    themeColors.accent
                } else {
                    if (themeColors.isDark) themeColors.border else BorderTealGray
                }

                val avatarBg = if (isSelected) {
                    if (themeColors.isDark) themeColors.border else SoftTealTint
                } else {
                    if (themeColors.isDark) themeColors.background else Color(0xFFF1F5F3)
                }

                val avatarBorder = if (isSelected) {
                    themeColors.accent
                } else {
                    if (themeColors.isDark) themeColors.border else BorderTealGray
                }

                val buttonBg = if (isSelected) {
                    themeColors.accent
                } else {
                    if (themeColors.isDark) themeColors.border else SoftTealTint
                }

                val buttonTextCol = if (isSelected) {
                    if (themeColors.isDark) DarkPine else Color.White
                } else {
                    if (themeColors.isDark) themeColors.accent else DeepVibrantTeal
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            role = Role.Button,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true)
                        ) {
                            viewModel.selectReciter(reciter)
                            viewModel.playSurahAudio(QuranData.surahs.first(), openPlayer = true)
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = cardBg,
                    border = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Leading Avatar Icon / Image
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(avatarBg),
                            contentAlignment = Alignment.Center
                        ) {
                            if (reciter.avatarUrl.isNotBlank()) {
                                AsyncImage(
                                    model = reciter.avatarUrl,
                                    contentDescription = reciterName,
                                    contentScale = ContentScale.Crop,
                                    error = reciter.drawableRes?.let { painterResource(it) } ?: painterResource(R.drawable.ic_noor_logo),
                                    placeholder = reciter.drawableRes?.let { painterResource(it) } ?: painterResource(R.drawable.ic_noor_logo),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            } else if (reciter.drawableRes != null) {
                                Image(
                                    painter = painterResource(reciter.drawableRes),
                                    contentDescription = reciterName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.Headphones else Icons.Default.Mic,
                                    contentDescription = reciterName,
                                    tint = if (isSelected) themeColors.accent else themeColors.translationText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Center Content Column (flexible width, will never squeeze other elements)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            // Row 1: Reciter Name + Active badge if selected
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = reciterName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.arabicText,
                                        fontSize = 15.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )

                                if (isSelected) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = themeColors.accent
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = if (themeColors.isDark) DarkPine else Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = if (isArabic) "المحدد" else "Active",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (themeColors.isDark) DarkPine else Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Row 2: Recitation Style description
                            Text(
                                text = reciterStyle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = themeColors.accent,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Row 3: Country / Origin
                            Text(
                                text = reciter.country,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = themeColors.translationText.copy(alpha = 0.85f),
                                    fontSize = 11.5.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Trailing Action Button: Play / Active
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = buttonBg,
                            modifier = Modifier.clickable(
                                role = Role.Button,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true)
                            ) {
                                viewModel.selectReciter(reciter)
                                viewModel.playSurahAudio(QuranData.surahs.first(), openPlayer = true)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = buttonTextCol,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = if (isArabic) "استمع" else "Play",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = buttonTextCol,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item(key = "bottom_space") {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
