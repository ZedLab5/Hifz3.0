package com.example.ui.quran

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.QuranReadingThemeColors

data class TajweedQuickRule(
    val category: TajweedCategory,
    val titleEn: String,
    val titleAr: String,
    val quickReminder: String,
    val sampleWord: String
)

/**
 * Compact, full-width Tajweed Quick Reminder menu opened by the floating button.
 * Shows necessary explanations only, corner position preferences, and a direct link to the full in-depth guide.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TajweedQuickReminderMenu(
    themeColors: QuranReadingThemeColors,
    buttonPosition: TajweedButtonPosition,
    onPositionChange: (TajweedButtonPosition) -> Unit,
    onOpenDeepGuide: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val palette = TajweedThemePalette.getPalette(themeColors)

    val quickRules = listOf(
        TajweedQuickRule(
            category = TajweedCategory.MADD,
            titleEn = "Madd",
            titleAr = "مدّ",
            quickReminder = "2, 4, 5 or 6 counts",
            sampleWord = "جَآءَ"
        ),
        TajweedQuickRule(
            category = TajweedCategory.GHUNNAH,
            titleEn = "Ghunnah",
            titleAr = "غُنَّة",
            quickReminder = "2 counts nasal sound",
            sampleWord = "إِنَّ"
        ),
        TajweedQuickRule(
            category = TajweedCategory.QALQALAH,
            titleEn = "Qalqalah",
            titleAr = "قَلْقَلَة",
            quickReminder = "Echo bounce (ق ط ب ج د)",
            sampleWord = "أَحَدٌ"
        ),
        TajweedQuickRule(
            category = TajweedCategory.IKHFA,
            titleEn = "Ikhfa",
            titleAr = "إِخْفَاء",
            quickReminder = "Conceal with Ghunnah (15 letters)",
            sampleWord = "مِندُونِ"
        ),
        TajweedQuickRule(
            category = TajweedCategory.IDGHAM,
            titleEn = "Idgham",
            titleAr = "إِدْغَام",
            quickReminder = "Merge letters (يرملون)",
            sampleWord = "مَن يَقُولُ"
        ),
        TajweedQuickRule(
            category = TajweedCategory.IQLAB,
            titleEn = "Iqlab",
            titleAr = "إِقْلَاب",
            quickReminder = "Convert Noon/Tanween to Meem before Ba",
            sampleWord = "مِنۢ بَعْدِ"
        ),
        TajweedQuickRule(
            category = TajweedCategory.MEEM_SAKINAH,
            titleEn = "Meem Sakinah",
            titleAr = "الميم الساكنة",
            quickReminder = "Ikhfa, Idgham, or Izhar Shafawi",
            sampleWord = "لَهُم مَّا"
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = themeColors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Bar
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
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(themeColors.accent.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Tajweed Reminder",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText,
                                fontSize = 16.5.sp
                            )
                        )
                        Text(
                            text = "مُذَكِّرُ أَحْكَامِ التَّجْوِيدِ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = themeColors.accent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_quick_tajweed_menu")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = themeColors.arabicText
                    )
                }
            }

            // Corner Position Selector
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = if (themeColors.isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.04f),
                border = null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Button Corner Placement",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TajweedButtonPosition.values().forEach { pos ->
                            val isSelected = buttonPosition == pos
                            val bgColor = if (isSelected) {
                                themeColors.accent.copy(alpha = if (themeColors.isDark) 0.28f else 0.18f)
                            } else {
                                themeColors.surface
                            }
                            val textColor = if (isSelected) themeColors.accent else themeColors.arabicText

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onPositionChange(pos) }
                                    .testTag("tajweed_pos_quick_${pos.name.lowercase()}"),
                                shape = RoundedCornerShape(8.dp),
                                color = bgColor,
                                border = null
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${pos.iconSymbol} ${pos.title.replace("Top ", "T-").replace("Bottom ", "B-")}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = textColor,
                                            fontSize = 10.5.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Essential Rules List (Compact & Scannable)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickRules.forEach { rule ->
                    val ruleColor = palette[rule.category] ?: themeColors.accent

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = themeColors.background,
                        border = null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(ruleColor)
                                )

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = rule.titleEn,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = themeColors.arabicText,
                                                fontSize = 12.5.sp
                                            )
                                        )
                                        Text(
                                            text = "(${rule.titleAr})",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ruleColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                    }

                                    Text(
                                        text = rule.quickReminder,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = themeColors.translationText,
                                            fontSize = 11.sp,
                                            lineHeight = 14.sp
                                        )
                                    )
                                }
                            }

                            // Quranic Highlight Example Word
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ruleColor.copy(alpha = 0.14f),
                                border = null
                            ) {
                                Text(
                                    text = rule.sampleWord,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ruleColor,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Direct Link to the Full In-Depth Tajweed Guide
            Button(
                onClick = {
                    onDismiss()
                    onOpenDeepGuide()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_deep_tajweed_guide_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColors.accent.copy(alpha = if (themeColors.isDark) 0.18f else 0.12f),
                    contentColor = themeColors.accent
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = null,
                border = null
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Open Deep Tajweed Guide & Full Rules",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
