package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.localization.tr
import com.example.data.model.HomeWidgetType
import com.example.data.model.QuickAccessTool
import com.example.ui.MainViewModel
import com.example.ui.theme.GoldTintBgLight
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeHomeFeedSheet(
    viewModel: MainViewModel,
    widgetsOrder: List<HomeWidgetType>,
    widgetsVisibility: Map<HomeWidgetType, Boolean>,
    initialTab: Int = 0,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val quickAccessTools by viewModel.quickAccessTools.collectAsStateWithLifecycle()
    val allQuickAccessTools = QuickAccessTool.entries
    val requiredQuickAccessCount = 5

    // Tab 0: Home Sections, Tab 1: Quick Access Tools
    var selectedTab by remember { mutableIntStateOf(initialTab.coerceIn(0, 1)) }

    val primaryTeal = if (isDark) SecondaryGoldDark else SecondaryGoldLight
    val toolBg = if (isDark) SecondaryGoldDark.copy(alpha = 0.16f) else GoldTintBgLight
    val toolIcon = if (isDark) SecondaryGoldDark else SecondaryGoldLight

    val rawWidgets = remember(widgetsOrder) { widgetsOrder.filter { it != HomeWidgetType.SALAT_TIMELINE } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f))
            )
        },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.82f)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            // Minimal Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isArabic) "تخصيص الواجهة" else "Customize",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = if (selectedTab == 0) {
                            if (isArabic) "ترتيب وإظهار أقسام الرئيسية" else "Reorder & toggle sections"
                        } else {
                            if (isArabic) "اختيار وترتيب ٥ أدوات سريعة" else "Choose & arrange 5 quick tools"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Minimal Segmented Pill Switcher (Tab 0: Sections, Tab 1: Quick Access)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Tab 0: Home Sections
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedTab == 0) primaryTeal else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedTab = 0 }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewStream,
                            contentDescription = null,
                            tint = if (selectedTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "الأقسام" else "Sections",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                // Tab 1: Quick Access Tools
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedTab == 1) primaryTeal else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedTab = 1 }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            tint = if (selectedTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "الوصول السريع" else "Quick Access",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // TAB CONTENT
            if (selectedTab == 0) {
                // ==========================================
                // TAB 0: HOMESCREEN SECTIONS LIST (MINIMAL)
                // ==========================================
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(rawWidgets, key = { _, item -> item.id }) { index, widget ->
                        val isVisible = widgetsVisibility[widget] ?: true

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isDark) {
                                if (isVisible) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            } else {
                                if (isVisible) Color(0xFFF2F7F4) else Color(0xFFF8FAF9)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Reorder Arrows (Minimal & Sleek)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.moveHomeWidgetUp(widget) },
                                        enabled = index > 0,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowUp,
                                            contentDescription = tr("home_customize_move_up", viewModel),
                                            tint = if (index > 0) primaryTeal else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.moveHomeWidgetDown(widget) },
                                        enabled = index < rawWidgets.lastIndex,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = tr("home_customize_move_down", viewModel),
                                            tint = if (index < rawWidgets.lastIndex) primaryTeal else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }
                                }

                                // Widget Name & Description
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = getLocalizedWidgetTitle(widget, viewModel),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isVisible) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.5.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = getLocalizedWidgetDescription(widget, viewModel),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isVisible) 0.85f else 0.5f),
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Toggle Switch (Borderless)
                                Switch(
                                    checked = isVisible,
                                    onCheckedChange = { viewModel.toggleHomeWidgetVisibility(widget) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = primaryTeal,
                                        uncheckedThumbColor = if (isDark) MaterialTheme.colorScheme.outline else Color.White,
                                        uncheckedTrackColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFCBD5E1),
                                        uncheckedBorderColor = Color.Transparent,
                                        checkedBorderColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                // ==========================================
                // TAB 1: QUICK ACCESS TOOLS LIST (MINIMAL)
                // ==========================================
                val isDoneEnabled = quickAccessTools.size == requiredQuickAccessCount

                // Minimal Status Counter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isDoneEnabled) primaryTeal else MaterialTheme.colorScheme.secondary)
                        )
                        Text(
                            text = if (isArabic) {
                                if (isDoneEnabled) "تم اختيار ٥ من ٥ أدوات" else "تم اختيار ${quickAccessTools.size} من ٥ أدوات"
                            } else {
                                if (isDoneEnabled) "5 of 5 tools selected" else "${quickAccessTools.size} of 5 tools selected"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = if (isDoneEnabled) primaryTeal else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        )
                    }

                    // 5 micro dots
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in 0 until requiredQuickAccessCount) {
                            val isFilled = i < quickAccessTools.size
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isFilled) toolIcon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    items(allQuickAccessTools, key = { it.id }) { tool ->
                        val isSelected = quickAccessTools.contains(tool)
                        val selectedIndex = quickAccessTools.indexOf(tool)

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isDark) {
                                if (isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            } else {
                                if (isSelected) Color(0xFFEAF5F0) else Color(0xFFF7FAF8)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.toggleQuickAccessTool(tool)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Reorder Arrows (Minimal & Sleek on the left)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.moveQuickAccessToolUp(tool) },
                                        enabled = isSelected && selectedIndex > 0,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowUp,
                                            contentDescription = "Move Up",
                                            tint = if (isSelected && selectedIndex > 0) primaryTeal else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.moveQuickAccessToolDown(tool) },
                                        enabled = isSelected && selectedIndex < quickAccessTools.lastIndex,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Move Down",
                                            tint = if (isSelected && selectedIndex < quickAccessTools.lastIndex) primaryTeal else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                ) {
                                    // Rank Number Badge for selected items, subtle dot for unselected
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(primaryTeal),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${selectedIndex + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier.size(22.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                                            )
                                        }
                                    }

                                    // Tool Icon Box (Borderless)
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(toolBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = getQuickAccessToolIcon(tool),
                                            contentDescription = null,
                                            tint = toolIcon,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Tool Name & Subtitle
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isArabic) tool.titleAr else tool.titleEn,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                fontSize = 13.5.sp
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (isArabic) tool.subtitleAr else tool.subtitleEn,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isSelected) 0.85f else 0.5f),
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // Toggle Switch (Borderless, matching Tab 0)
                                Switch(
                                    checked = isSelected,
                                    onCheckedChange = { viewModel.toggleQuickAccessTool(tool) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = primaryTeal,
                                        uncheckedThumbColor = if (isDark) MaterialTheme.colorScheme.outline else Color.White,
                                        uncheckedTrackColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFCBD5E1),
                                        uncheckedBorderColor = Color.Transparent,
                                        checkedBorderColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Minimal Footer Actions (Zero borders)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (selectedTab == 0) {
                            viewModel.resetHomeWidgetsOrder()
                        } else {
                            viewModel.resetQuickAccessTools()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        contentColor = primaryTeal
                    ),
                    elevation = null
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "إعادة تعيين" else "Reset",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryTeal,
                        contentColor = Color.White
                    ),
                    elevation = null
                ) {
                    Text(
                        text = if (isArabic) "تم" else "Done",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun getLocalizedWidgetTitle(widget: HomeWidgetType, viewModel: MainViewModel): String {
    return when (widget) {
        HomeWidgetType.SALAT_TIMELINE -> tr("home_prayer_timeline", viewModel)
        HomeWidgetType.SPIRITUAL_ESSENTIALS -> tr("home_spiritual_essentials", viewModel)
        HomeWidgetType.FAVORITES_CAROUSEL -> tr("home_favorites_carousel", viewModel)
        HomeWidgetType.UNIFIED_STREAKS -> tr("home_unified_streaks", viewModel)
        HomeWidgetType.DAILY_REVELATION -> tr("home_daily_revelation", viewModel)
        HomeWidgetType.KHATMA_TRACKER -> tr("home_khatma_tracker", viewModel)
        HomeWidgetType.MOOD_REFLECTION -> tr("home_mood_reflection", viewModel)
        HomeWidgetType.AUDIO_RECITERS -> tr("home_audio_reciters", viewModel)
    }
}

@Composable
private fun getLocalizedWidgetDescription(widget: HomeWidgetType, viewModel: MainViewModel): String {
    return when (widget) {
        HomeWidgetType.SALAT_TIMELINE -> tr("home_prayer_timeline_sub", viewModel)
        HomeWidgetType.SPIRITUAL_ESSENTIALS -> tr("home_spiritual_essentials_sub", viewModel)
        HomeWidgetType.FAVORITES_CAROUSEL -> tr("home_favorites_carousel_sub", viewModel)
        HomeWidgetType.UNIFIED_STREAKS -> tr("home_unified_streaks_sub", viewModel)
        HomeWidgetType.DAILY_REVELATION -> tr("home_daily_revelation_sub", viewModel)
        HomeWidgetType.KHATMA_TRACKER -> tr("home_khatma_tracker_sub", viewModel)
        HomeWidgetType.MOOD_REFLECTION -> tr("home_mood_reflection_sub", viewModel)
        HomeWidgetType.AUDIO_RECITERS -> tr("home_audio_reciters_sub", viewModel)
    }
}

