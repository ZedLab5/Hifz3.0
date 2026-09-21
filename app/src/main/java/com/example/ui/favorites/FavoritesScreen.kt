package com.example.ui.favorites

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.FavoriteItemEntity
import com.example.ui.MainViewModel
import com.example.ui.components.BentoCard
import com.example.ui.components.GoldBadge
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealLight
import com.example.ui.theme.SlateTealMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFavoritesFilter.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val tealAccent = if (isDark) PrimaryTealDark else PrimaryTealLight
    val pillActiveBg = tealAccent
    val pillActiveText = Color.White
    val pillInactiveBg = if (isDark) Color(0xFF1E262B) else Color(0xFFE8EEEC)
    val pillInactiveText = if (isDark) Color(0xFFB0BEC5) else Color(0xFF546E7A)

    val tabs = remember(favorites, isArabic) {
        val duasCount = favorites.count {
            val t = it.type.uppercase()
            t == "DUA" || t == "SUPPLICATION" || t == "DUAS"
        }
        val surahsCount = favorites.count { it.type.equals("SURAH", ignoreCase = true) }
        val ayahsCount = favorites.count {
            val t = it.type.uppercase()
            t == "AYAH" || t == "VERSE" || t == "QURAN"
        }
        val azkarCount = favorites.count {
            val t = it.type.uppercase()
            t == "AZKAR" || t == "DHIKR" || t == "TASBIH"
        }
        listOf(
            Triple("ALL", if (isArabic) "الكل" else "All", favorites.size),
            Triple("DUA", if (isArabic) "الأدعية" else "Duas", duasCount),
            Triple("SURAH", if (isArabic) "السور" else "Surahs", surahsCount),
            Triple("AYAH", if (isArabic) "الآيات" else "Ayahs", ayahsCount),
            Triple("AZKAR", if (isArabic) "الأذكار" else "Azkar", azkarCount)
        )
    }

    val filteredList = remember(favorites, selectedFilter) {
        when (selectedFilter.uppercase()) {
            "DUA" -> favorites.filter {
                val t = it.type.uppercase()
                t == "DUA" || t == "SUPPLICATION" || t == "DUAS"
            }
            "SURAH" -> favorites.filter { it.type.equals("SURAH", ignoreCase = true) }
            "AYAH" -> favorites.filter {
                val t = it.type.uppercase()
                t == "AYAH" || t == "VERSE" || t == "QURAN"
            }
            "AZKAR" -> favorites.filter {
                val t = it.type.uppercase()
                t == "AZKAR" || t == "DHIKR" || t == "TASBIH"
            }
            else -> favorites
        }
    }

    Scaffold(
        topBar = {
            NoorTopBar(
                title = if (isArabic) "المفضلة والمحفوظات" else "Saved Favorites",
                eyebrow = if (isArabic) "المحفوظات" else "SAVED ITEMS",
                subtitle = if (isArabic) "${favorites.size} عناصر محفوظة" else "${favorites.size} items • Personal repository of guidance",
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back"
            )
        },
        containerColor = if (isDark) MaterialTheme.colorScheme.background else CanvasMint,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category Filter Pills (No borders)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs) { (key, label, count) ->
                    val isSelected = selectedFilter.equals(key, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) pillActiveBg else pillInactiveBg,
                        border = null,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.selectedFavoritesFilter.value = key }
                            .testTag("filter_tab_$key")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) pillActiveText else pillInactiveText
                                )
                            )
                            if (count > 0) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) Color.White.copy(alpha = 0.25f)
                                            else tealAccent.copy(alpha = 0.12f)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = count.toString(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isSelected) pillActiveText else tealAccent
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Empty",
                            tint = SlateTealMuted.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (isArabic) "لا توجد عناصر محفوظة هنا" else "No saved bookmarks yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SlateTealMuted
                            )
                        )
                        Text(
                            text = if (isArabic)
                                "اضغط على أيقونة الإشارة المرجعية أو القلب في أي آية أو سورة أو دعاء لحفظها هنا."
                            else
                                "Tap the bookmark icon on any Ayah, Surah, or Dua to save here.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateTealMuted,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        BentoCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    GoldBadge(text = item.type)

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Favorite", "${item.title}\n${item.arabicText}\n${item.translation}\n${item.source}")
                                                clipboard.setPrimaryClip(clip)
                                                viewModel.showToast("Copied to clipboard!")
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy",
                                                tint = DeepVibrantTeal,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.removeFavorite(item) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Remove",
                                                tint = SlateTealMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                if (item.arabicText.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = item.arabicText,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = DarkPine,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End,
                                            lineHeight = 26.sp
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "\"${item.translation}\"",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkPine)
                                )

                                if (item.source.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "— ${item.source}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MetallicGold,
                                            fontWeight = FontWeight.Bold
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
}
