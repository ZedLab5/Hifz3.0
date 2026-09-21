package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.quran.QuranData
import com.example.ui.MainViewModel
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealLight
import kotlinx.coroutines.launch

data class FavoriteShortcutCardItem(
    val id: String,
    val title: String,
    val countText: String,
    val onClick: () -> Unit
)

data class AzkarPillItem(
    val id: String,
    val title: String,
    val categoryId: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortcutsSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val quranBookmarks by viewModel.quranBookmarks.collectAsStateWithLifecycle()
    val readingProgress by viewModel.readingProgress.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val tealAccent = if (isDark) PrimaryTealDark else PrimaryTealLight
    val itemBg = if (isDark) Color(0xFF1E262B) else Color(0xFFF3F6F5)
    val countBadgeBg = if (isDark) tealAccent.copy(alpha = 0.16f) else tealAccent.copy(alpha = 0.12f)

    // Animated dismiss helper so slide-down animation always plays smoothly
    val dismissWithAnimation: (action: () -> Unit) -> Unit = { action ->
        scope.launch {
            sheetState.hide()
            onDismiss()
            action()
        }
    }

    val closeWithAnimation: () -> Unit = {
        scope.launch {
            sheetState.hide()
            onDismiss()
        }
    }

    // Dynamic counts from existing data sources
    val duaCount = remember(favorites) {
        favorites.count {
            val t = it.type.uppercase()
            t == "DUA" || t == "SUPPLICATION" || t == "DUAS"
        }
    }

    val surahCount = remember(favorites) {
        favorites.count { it.type.equals("SURAH", ignoreCase = true) }
    }

    val ayahCount = remember(favorites, quranBookmarks) {
        val favAyahs = favorites.count {
            val t = it.type.uppercase()
            t == "AYAH" || t == "VERSE" || t == "QURAN"
        }
        favAyahs + quranBookmarks.size
    }

    val azkarCount = remember(favorites) {
        favorites.count {
            val t = it.type.uppercase()
            t == "AZKAR" || t == "DHIKR" || t == "TASBIH"
        }
    }

    // Section 1: 4 Favorites Cards
    val favoriteCards = remember(duaCount, surahCount, ayahCount, azkarCount, isArabic) {
        listOf(
            FavoriteShortcutCardItem(
                id = "fav_duas",
                title = if (isArabic) "الأدعية المفضلة" else "Favorite Duas",
                countText = if (isArabic) "$duaCount محفوظة" else "$duaCount saved",
                onClick = { viewModel.openFavoritesWithFilter("DUA") }
            ),
            FavoriteShortcutCardItem(
                id = "fav_surahs",
                title = if (isArabic) "السور المفضلة" else "Favorite Surahs",
                countText = if (isArabic) "$surahCount محفوظة" else "$surahCount saved",
                onClick = { viewModel.openFavoritesWithFilter("SURAH") }
            ),
            FavoriteShortcutCardItem(
                id = "fav_ayahs",
                title = if (isArabic) "الآيات المفضلة" else "Favorite Ayahs",
                countText = if (isArabic) "$ayahCount محفوظة" else "$ayahCount saved",
                onClick = { viewModel.openFavoritesWithFilter("AYAH") }
            ),
            FavoriteShortcutCardItem(
                id = "fav_azkar",
                title = if (isArabic) "الأذكار المفضلة" else "Favorite Azkar",
                countText = if (isArabic) "$azkarCount محفوظة" else "$azkarCount saved",
                onClick = { viewModel.openFavoritesWithFilter("AZKAR") }
            )
        )
    }

    // Section 2: Continue Reading Quran Details (Always shown)
    val currentProgress = readingProgress
    val quranCardTitle = if (isArabic) "متابعة قراءة القرآن" else "Continue Reading Quran"
    val quranCardSubtitle = if (currentProgress != null) {
        val surah = QuranData.surahs.find { it.number == currentProgress.surahNumber }
        val name = if (isArabic) (surah?.nameArabic ?: currentProgress.surahName) else (surah?.nameEnglish ?: currentProgress.surahName)
        if (isArabic) {
            "$name • الآية ${currentProgress.ayahNumber} من ${currentProgress.totalAyahs}"
        } else {
            "$name • Ayah ${currentProgress.ayahNumber} of ${currentProgress.totalAyahs}"
        }
    } else {
        if (isArabic) "لم تبدأ القراءة بعد" else "You haven't started reading yet"
    }

    val quranActionLabel = if (currentProgress != null) {
        if (isArabic) "متابعة" else "Resume"
    } else {
        if (isArabic) "البداية" else "Start"
    }

    // Section 3: Most Used Azkar Pills (Direct category access into DuaData)
    val mostUsedPills = remember(isArabic) {
        listOf(
            AzkarPillItem(
                id = "morning_azkar",
                title = if (isArabic) "أذكار الصباح" else "Morning Azkar",
                categoryId = "Morning Azkar"
            ),
            AzkarPillItem(
                id = "evening_azkar",
                title = if (isArabic) "أذكار المساء" else "Evening Azkar",
                categoryId = "Evening Azkar"
            ),
            AzkarPillItem(
                id = "after_salah",
                title = if (isArabic) "أذكار بعد الصلاة" else "After Salah",
                categoryId = "After Salah"
            ),
            AzkarPillItem(
                id = "sleep_awakening",
                title = if (isArabic) "أذكار النوم والاستيقاظ" else "Sleep and Awakening",
                categoryId = "Sleep & Awakening"
            )
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = surfaceColor,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 6.dp)
                    .size(width = 38.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(onSurfaceVariant.copy(alpha = 0.25f))
            )
        },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // Main Sheet Header with Title, Subtitle, and down-chevron close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.shortcuts_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = onSurface
                        )
                    )
                    Text(
                        text = stringResource(R.string.shortcuts_subtitle),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = onSurfaceVariant
                        )
                    )
                }

                IconButton(
                    onClick = closeWithAnimation,
                    modifier = Modifier.testTag("shortcuts_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.action_close),
                        tint = onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // SECTION ONE: FAVORITES (Grid of 4 Cards)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.shortcuts_section_favorites),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = onSurface
                    )
                )
                Text(
                    text = stringResource(R.string.shortcuts_section_favorites_sub),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2x2 Grid of Plain Rounded Cards without borders or icons
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Row 1: Favorite Duas & Favorite Surahs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FavoriteGridCard(
                            item = favoriteCards[0],
                            itemBg = itemBg,
                            tealAccent = tealAccent,
                            countBadgeBg = countBadgeBg,
                            onSurface = onSurface,
                            modifier = Modifier.weight(1f),
                            onClick = { dismissWithAnimation { favoriteCards[0].onClick() } }
                        )
                        FavoriteGridCard(
                            item = favoriteCards[1],
                            itemBg = itemBg,
                            tealAccent = tealAccent,
                            countBadgeBg = countBadgeBg,
                            onSurface = onSurface,
                            modifier = Modifier.weight(1f),
                            onClick = { dismissWithAnimation { favoriteCards[1].onClick() } }
                        )
                    }

                    // Row 2: Favorite Ayahs & Favorite Azkar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FavoriteGridCard(
                            item = favoriteCards[2],
                            itemBg = itemBg,
                            tealAccent = tealAccent,
                            countBadgeBg = countBadgeBg,
                            onSurface = onSurface,
                            modifier = Modifier.weight(1f),
                            onClick = { dismissWithAnimation { favoriteCards[2].onClick() } }
                        )
                        FavoriteGridCard(
                            item = favoriteCards[3],
                            itemBg = itemBg,
                            tealAccent = tealAccent,
                            countBadgeBg = countBadgeBg,
                            onSurface = onSurface,
                            modifier = Modifier.weight(1f),
                            onClick = { dismissWithAnimation { favoriteCards[3].onClick() } }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // SECTION TWO: CONTINUE READING QURAN
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            dismissWithAnimation {
                                if (currentProgress != null) {
                                    viewModel.resumeReading(currentProgress)
                                } else {
                                    viewModel.selectSurahForReading(QuranData.surahs.first(), 0)
                                }
                            }
                        }
                        .testTag("shortcut_continue_reading_card"),
                    shape = RoundedCornerShape(16.dp),
                    color = itemBg,
                    border = null,
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 12.dp)
                        ) {
                            Text(
                                text = quranCardTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = onSurface
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = quranCardSubtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.5.sp,
                                    color = onSurfaceVariant
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(countBadgeBg)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = quranActionLabel,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = tealAccent
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // SECTION THREE: MOST USED AZKAR PILLS
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.shortcuts_section_most_used),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = onSurface
                    )
                )
                Text(
                    text = stringResource(R.string.shortcuts_section_most_used_sub),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2x2 Grid of plain rounded pills without borders or icons
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Row 1: Morning Azkar & Evening Azkar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AzkarGridPill(
                            item = mostUsedPills[0],
                            itemBg = itemBg,
                            onSurface = onSurface,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                dismissWithAnimation {
                                    viewModel.openDuaCategory(mostUsedPills[0].categoryId)
                                }
                            }
                        )
                        AzkarGridPill(
                            item = mostUsedPills[1],
                            itemBg = itemBg,
                            onSurface = onSurface,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                dismissWithAnimation {
                                    viewModel.openDuaCategory(mostUsedPills[1].categoryId)
                                }
                            }
                        )
                    }

                    // Row 2: After Salah & Sleep and Awakening
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AzkarGridPill(
                            item = mostUsedPills[2],
                            itemBg = itemBg,
                            onSurface = onSurface,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                dismissWithAnimation {
                                    viewModel.openDuaCategory(mostUsedPills[2].categoryId)
                                }
                            }
                        )
                        AzkarGridPill(
                            item = mostUsedPills[3],
                            itemBg = itemBg,
                            onSurface = onSurface,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                dismissWithAnimation {
                                    viewModel.openDuaCategory(mostUsedPills[3].categoryId)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun FavoriteGridCard(
    item: FavoriteShortcutCardItem,
    itemBg: Color,
    tealAccent: Color,
    countBadgeBg: Color,
    onSurface: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("shortcut_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        color = itemBg,
        border = null,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(countBadgeBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = item.countText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = tealAccent
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun AzkarGridPill(
    item: AzkarPillItem,
    itemBg: Color,
    onSurface: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("shortcut_pill_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        color = itemBg,
        border = null,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    color = onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
