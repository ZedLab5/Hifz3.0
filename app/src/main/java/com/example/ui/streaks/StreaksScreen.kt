package com.example.ui.streaks

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DayStreakStatus
import com.example.data.model.StreakActivityType
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.routines.HabitTrackerScreen
import com.example.ui.theme.BorderDividerDark
import com.example.ui.theme.BorderDividerLight
import com.example.ui.theme.HeaderTealStart
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceElevatedLight
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextSecondaryLight
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val ExcusedTeal = Color(0xFF00897B)
val ExcusedTealLight = Color(0xFFE0F2F1)
val ExcusedTealDark = Color(0xFF004D40)

enum class DayCompletionState {
    NOT_DONE,
    COMPLETE,   // All 4 core daily devotions completed (Salat, Quran, Dua, Azkar)
    EXCEEDED,   // All 4 core daily devotions + extra Sunnah habit / Tasbih
    EXCUSED,    // Excused day (streak protected)
    FREEZE      // Freeze pass used
}

@Composable
private fun rememberStreakThemeColors(isDark: Boolean) = remember(isDark) {
    val salatEmeraldPrimary = Color(0xFF107C41)
    val salatCardBg = Color(0xFFF6F8F7)
    val salatBadgeBg = Color(0xFFEDF2F7)
    val salatDivider = Color(0xFFECEFF1)
    StreakThemeColors(
        surface = if (isDark) SurfaceDark else SurfaceWhite,
        surfaceElevated = if (isDark) SurfaceElevatedDark else salatCardBg,
        textPrimary = if (isDark) TextPrimaryDark else Color(0xFF1E293B),
        textSecondary = if (isDark) TextSecondaryDark else Color(0xFF5F5E5A),
        border = if (isDark) BorderDividerDark else salatDivider,
        success = if (isDark) Color(0xFF4ADE80) else salatEmeraldPrimary,
        goldExceeded = if (isDark) Color(0xFF4ADE80) else salatEmeraldPrimary,
        excused = ExcusedTeal,
        excusedBg = if (isDark) Color(0xFF143825) else salatBadgeBg,
        freeze = Color(0xFF0288D1),
        heroBg = if (isDark) SurfaceDark else SurfaceWhite,
        heroCardBg = if (isDark) SurfaceElevatedDark else salatCardBg
    )
}

private data class StreakThemeColors(
    val surface: Color,
    val surfaceElevated: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val success: Color,
    val goldExceeded: Color,
    val excused: Color,
    val excusedBg: Color,
    val freeze: Color,
    val heroBg: Color,
    val heroCardBg: Color
)

private fun getDayCompletionState(
    day: DayStreakStatus,
    habits: List<com.example.data.local.DailyHabitEntity> = emptyList(),
    isToday: Boolean = false,
    hasExtraHabitToday: Boolean = false
): DayCompletionState {
    if (day.isExcused) return DayCompletionState.EXCUSED
    if (day.isFreezeUsed) return DayCompletionState.FREEZE

    val isCoreDone = day.salatCompleted && day.quranCompleted && day.azkarCompleted && day.duaCompleted

    // Post-migration path: Check if any non-core habit was completed on day.date (matched via completedDateIso)
    val hasCompletedHabitOnDate = habits.any { habit ->
        !isCoreHabitTitle(habit.title) && habit.completedDateIso == day.date
    }

    // Pre-migration fallback: For older records prior to migration where completedDateIso was null,
    // fallback to day.tasbihCompleted so historical records are preserved.
    val isExtraDone = hasCompletedHabitOnDate || (isToday && hasExtraHabitToday) || day.tasbihCompleted

    return when {
        isCoreDone && isExtraDone -> DayCompletionState.EXCEEDED
        isCoreDone -> DayCompletionState.COMPLETE
        else -> DayCompletionState.NOT_DONE
    }
}

private fun isCoreHabitTitle(title: String): Boolean {
    val lower = title.lowercase()
    return lower.contains("salat") || lower.contains("salah") || lower.contains("prayer") ||
           lower.contains("quran") || lower.contains("qur'an") ||
           lower.contains("azkar") || lower.contains("dhikr") ||
           lower.contains("dua") || lower.contains("du'a")
}

@Composable
fun StreaksScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    HabitTrackerScreen(
        viewModel = viewModel,
        modifier = modifier,
        initialTab = 1
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreaksStatsContent(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val streakData by viewModel.unifiedStreakData.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    var selectedDayForDetail by remember { mutableStateOf<DayStreakStatus?>(null) }
    var showExcusePickerForDate by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val themeColors = rememberStreakThemeColors(isDark)

    val hasExtraHabitToday = remember(habits) {
        habits.any { habit -> habit.isCompleted && !isCoreHabitTitle(habit.title) }
    }

    // If streak tracking is disabled, show pressure-free mindfulness view
    if (!streakData.isTrackingEnabled) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("streaks_stats_content"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = themeColors.surface,
                    border = BorderStroke(1.dp, themeColors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(themeColors.surfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Spa,
                                contentDescription = null,
                                tint = themeColors.textPrimary,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Pressure-Free Worship",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.textPrimary
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Streak counters and consistency reminders are currently paused. Worship at your own gentle pace with pure intention and sincere presence.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = themeColors.textSecondary,
                                lineHeight = 20.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.toggleStreakTracking(true) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeColors.heroBg,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Turn On Streak Tracking",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            item {
                SpiritualConsistencyQuoteCard(themeColors)
            }
        }
        return
    }

    var isMockDataActive by remember { mutableStateOf(false) }

    val mockRecentDays = remember {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        (0 until 60).map { i ->
            val date = today.minusDays(i.toLong()).format(formatter)
            when {
                i in 0..17 -> {
                    val isExtra = i == 0 || i == 3 || i == 7 || i == 12 || i == 15
                    DayStreakStatus(
                        date = date,
                        salatCompleted = true,
                        quranCompleted = true,
                        azkarCompleted = true,
                        duaCompleted = true,
                        tasbihCompleted = isExtra
                    )
                }
                i == 18 -> DayStreakStatus(
                    date = date,
                    isExcused = true,
                    excuseReason = "Travel (Safar)"
                )
                i in 19..28 -> DayStreakStatus(
                    date = date,
                    salatCompleted = true,
                    quranCompleted = true,
                    azkarCompleted = true,
                    duaCompleted = true,
                    tasbihCompleted = (i % 3 == 0)
                )
                i == 29 -> DayStreakStatus(
                    date = date,
                    isFreezeUsed = true
                )
                i in 30..48 -> DayStreakStatus(
                    date = date,
                    salatCompleted = true,
                    quranCompleted = true,
                    azkarCompleted = true,
                    duaCompleted = true,
                    tasbihCompleted = (i % 2 == 0)
                )
                else -> DayStreakStatus(
                    date = date,
                    salatCompleted = (i % 2 == 0),
                    quranCompleted = (i % 3 != 0),
                    azkarCompleted = (i % 2 == 0),
                    duaCompleted = true,
                    tasbihCompleted = false
                )
            }
        }.reversed()
    }

    val displayCurrentStreak = if (isMockDataActive) 18 else streakData.currentStreak
    val displayLongestStreak = if (isMockDataActive) 32 else streakData.longestStreak
    val displayFreezesRemaining = if (isMockDataActive) 2 else streakData.freezesRemaining
    val displayRecentDays = if (isMockDataActive) mockRecentDays else streakData.recentDays

    val todayStr = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
    val todayStatus = remember(streakData, hasExtraHabitToday, isMockDataActive) {
        if (isMockDataActive) {
            DayCompletionState.EXCEEDED
        } else {
            val todayLog = streakData.recentDays.find { it.date == todayStr } ?: DayStreakStatus(
                date = todayStr,
                salatCompleted = streakData.todaySalatDone,
                quranCompleted = streakData.todayQuranDone,
                azkarCompleted = streakData.todayAzkarDone,
                duaCompleted = streakData.todayDuaDone,
                tasbihCompleted = streakData.todayTasbihDone,
                isExcused = streakData.todayExcused,
                excuseReason = streakData.todayExcuseReason
            )
            getDayCompletionState(todayLog, habits = habits, isToday = true, hasExtraHabitToday = hasExtraHabitToday)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("streaks_stats_content"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 0. Mock Data Banner / Switch
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            .background(if (isMockDataActive) Color(0xFFD97706) else themeColors.textSecondary)
                    )
                    Text(
                        text = if (isMockDataActive) "Sample Streak Preview (Mock Data)" else "Live Consistency Data",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = themeColors.textSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Surface(
                    onClick = { isMockDataActive = !isMockDataActive },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isMockDataActive) (if (isDark) Color(0xFF332005) else Color(0xFFFEF3C7)) else themeColors.surfaceElevated,
                    border = BorderStroke(1.dp, if (isMockDataActive) Color(0xFFF59E0B) else themeColors.border)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isMockDataActive) Icons.Filled.Star else Icons.Filled.Check,
                            contentDescription = null,
                            tint = if (isMockDataActive) Color(0xFFD97706) else themeColors.textSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isMockDataActive) "Mock Data ON" else "Live Data",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isMockDataActive) (if (isDark) Color(0xFFFDE68A) else Color(0xFF78350F)) else themeColors.textPrimary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // 1. Hero Summary Card: Current Streak + Stats
        item {
            StreakHeroSummaryCard(
                currentStreak = displayCurrentStreak,
                longestStreak = displayLongestStreak,
                freezesRemaining = displayFreezesRemaining,
                todayState = todayStatus,
                todayCoreCount = if (isMockDataActive) 4 else listOf(
                    streakData.todaySalatDone,
                    streakData.todayQuranDone,
                    streakData.todayAzkarDone,
                    streakData.todayDuaDone
                ).count { it },
                todayExcuseReason = streakData.todayExcuseReason,
                themeColors = themeColors,
                isDark = isDark
            )
        }

        // 2. 60-Day Contribution Heatmap Grid
        item {
            StreakCalendarHeatmapCard(
                recentDays = displayRecentDays,
                habits = habits,
                hasExtraHabitToday = if (isMockDataActive) true else hasExtraHabitToday,
                onDayClick = { selectedDayForDetail = it },
                themeColors = themeColors
            )
        }

        // 3. Excuse a Day Card (Distinct from Freeze Pass)
        item {
            StreakExcuseCard(
                todayExcused = streakData.todayExcused,
                todayExcuseReason = streakData.todayExcuseReason,
                yesterdayExcused = streakData.yesterdayExcused,
                onExcuseTodayClick = {
                    showExcusePickerForDate = todayStr
                },
                onExcuseYesterdayClick = {
                    val yesterdayStr = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    showExcusePickerForDate = yesterdayStr
                },
                onRemoveTodayExcuse = {
                    viewModel.removeDayExcuse(todayStr)
                },
                themeColors = themeColors
            )
        }

        // 4. Section: Compact Individual Activity Breakdown
        item {
            StreakCompactActivityDashboardCard(
                todaySalatDone = streakData.todaySalatDone,
                salatStreak = streakData.salatStreak,
                salatHistory = streakData.recentDays.takeLast(7).map { it.salatCompleted || it.isExcused },
                todayQuranDone = streakData.todayQuranDone,
                quranStreak = streakData.quranStreak,
                quranHistory = streakData.recentDays.takeLast(7).map { it.quranCompleted || it.isExcused },
                todayAzkarDone = streakData.todayAzkarDone,
                azkarStreak = streakData.azkarStreak,
                azkarHistory = streakData.recentDays.takeLast(7).map { it.azkarCompleted || it.isExcused },
                todayDuaDone = streakData.todayDuaDone,
                duaStreak = streakData.duaStreak,
                duaHistory = streakData.recentDays.takeLast(7).map { it.duaCompleted || it.isExcused },
                todayTasbihDone = streakData.todayTasbihDone,
                tasbihStreak = streakData.tasbihStreak,
                tasbihHistory = streakData.recentDays.takeLast(7).map { it.tasbihCompleted || it.isExcused },
                onNavigate = { viewModel.navigateTo(it) },
                themeColors = themeColors
            )
        }

        // 6. Streak Freeze Protection Card
        item {
            StreakFreezeProtectionCard(
                freezesRemaining = streakData.freezesRemaining,
                isYesterdayMissed = streakData.isYesterdayMissed,
                onUseFreeze = { viewModel.useStreakFreeze() },
                themeColors = themeColors
            )
        }

        // 7. Spiritual Quote Reflection (Non-judgmental & Warm)
        item {
            SpiritualConsistencyQuoteCard(themeColors)
        }
    }

    // Modal BottomSheet for Detailed Day Inspection & Interaction
    if (selectedDayForDetail != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedDayForDetail = null },
            sheetState = sheetState,
            containerColor = themeColors.surface
        ) {
            DayDetailSheetContent(
                day = selectedDayForDetail!!,
                habits = habits,
                hasExtraHabitToday = if (selectedDayForDetail!!.date == todayStr) hasExtraHabitToday else false,
                onClose = { selectedDayForDetail = null },
                onExcuseDay = { reason ->
                    viewModel.excuseDay(selectedDayForDetail!!.date, reason)
                    selectedDayForDetail = selectedDayForDetail!!.copy(isExcused = true, excuseReason = reason)
                },
                onRemoveExcuse = {
                    viewModel.removeDayExcuse(selectedDayForDetail!!.date)
                    selectedDayForDetail = selectedDayForDetail!!.copy(isExcused = false, excuseReason = "")
                },
                onToggleActivity = { activityType, isCompleted ->
                    viewModel.toggleDayActivity(selectedDayForDetail!!.date, activityType, isCompleted)
                    selectedDayForDetail = when (activityType) {
                        StreakActivityType.SALAT -> selectedDayForDetail!!.copy(salatCompleted = isCompleted)
                        StreakActivityType.QURAN -> selectedDayForDetail!!.copy(quranCompleted = isCompleted)
                        StreakActivityType.AZKAR -> selectedDayForDetail!!.copy(azkarCompleted = isCompleted)
                        StreakActivityType.DUA -> selectedDayForDetail!!.copy(duaCompleted = isCompleted)
                        StreakActivityType.TASBIH -> selectedDayForDetail!!.copy(tasbihCompleted = isCompleted)
                    }
                },
                themeColors = themeColors
            )
        }
    }

    // Dialog for picking excuse reason
    if (showExcusePickerForDate != null) {
        ExcuseReasonPickerDialog(
            dateStr = showExcusePickerForDate!!,
            onDismiss = { showExcusePickerForDate = null },
            onSelectReason = { reason ->
                viewModel.excuseDay(showExcusePickerForDate!!, reason)
                showExcusePickerForDate = null
            },
            themeColors = themeColors
        )
    }
}

@Composable
private fun StreakHeroSummaryCard(
    currentStreak: Int,
    longestStreak: Int,
    freezesRemaining: Int,
    todayState: DayCompletionState,
    todayCoreCount: Int,
    todayExcuseReason: String,
    themeColors: StreakThemeColors,
    isDark: Boolean
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = themeColors.surface,
        border = BorderStroke(1.dp, themeColors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top Row: Current Consistency & Flame Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(themeColors.success)
                        )
                        Text(
                            text = "CURRENT CONSISTENCY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = themeColors.textSecondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$currentStreak",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = themeColors.textPrimary,
                                fontSize = 42.sp
                            )
                        )
                        Text(
                            text = if (currentStreak == 1) "Day Active" else "Days Active",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = themeColors.textSecondary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                // Flame Badge Container - Soft Consistent Green / Sage Tone
                val badgeBg = when (todayState) {
                    DayCompletionState.EXCEEDED -> if (isDark) Color(0xFF143825) else themeColors.success.copy(alpha = 0.12f)
                    DayCompletionState.COMPLETE -> if (isDark) Color(0xFF143825) else themeColors.success.copy(alpha = 0.12f)
                    DayCompletionState.EXCUSED -> themeColors.excusedBg
                    else -> themeColors.surfaceElevated
                }
                val badgeBorder = when (todayState) {
                    DayCompletionState.EXCEEDED -> themeColors.success
                    DayCompletionState.COMPLETE -> themeColors.success
                    DayCompletionState.EXCUSED -> themeColors.excused
                    else -> themeColors.border
                }
                val badgeIconTint = when (todayState) {
                    DayCompletionState.EXCUSED -> themeColors.excused
                    DayCompletionState.EXCEEDED -> themeColors.success
                    DayCompletionState.COMPLETE -> themeColors.success
                    else -> themeColors.textSecondary
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(badgeBg)
                        .border(1.dp, badgeBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (todayState) {
                            DayCompletionState.EXCUSED -> Icons.Filled.Spa
                            DayCompletionState.EXCEEDED -> Icons.Filled.AutoAwesome
                            DayCompletionState.COMPLETE -> Icons.Filled.AutoAwesome
                            else -> Icons.Filled.AutoAwesome
                        },
                        contentDescription = "Spiritual Devotion Badge",
                        tint = badgeIconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Today's Status Banner Sub-Card - Soft Color Scheme
            val bannerContainer = when (todayState) {
                DayCompletionState.EXCEEDED -> if (isDark) Color(0xFF143825) else themeColors.success.copy(alpha = 0.12f)
                DayCompletionState.COMPLETE -> if (isDark) Color(0xFF143825) else themeColors.success.copy(alpha = 0.12f)
                DayCompletionState.EXCUSED -> themeColors.excusedBg
                else -> themeColors.surfaceElevated
            }
            val bannerBorder = when (todayState) {
                DayCompletionState.EXCEEDED -> themeColors.success.copy(alpha = 0.5f)
                DayCompletionState.COMPLETE -> themeColors.success.copy(alpha = 0.5f)
                DayCompletionState.EXCUSED -> themeColors.excused.copy(alpha = 0.5f)
                else -> themeColors.border
            }
            val bannerText = when (todayState) {
                DayCompletionState.EXCEEDED -> if (isDark) Color(0xFF4ADE80) else Color(0xFF0D5C3A)
                DayCompletionState.COMPLETE -> if (isDark) Color(0xFF4ADE80) else Color(0xFF0D5C3A)
                DayCompletionState.EXCUSED -> themeColors.textPrimary
                else -> themeColors.textPrimary
            }
            val bannerIconTint = when (todayState) {
                DayCompletionState.EXCEEDED -> themeColors.success
                DayCompletionState.COMPLETE -> themeColors.success
                DayCompletionState.EXCUSED -> themeColors.excused
                else -> themeColors.textSecondary
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = bannerContainer,
                border = BorderStroke(1.dp, bannerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = when (todayState) {
                            DayCompletionState.EXCUSED -> Icons.Filled.Spa
                            DayCompletionState.EXCEEDED -> Icons.Filled.AutoAwesome
                            DayCompletionState.COMPLETE -> Icons.Filled.AutoAwesome
                            else -> Icons.Filled.Info
                        },
                        contentDescription = null,
                        tint = bannerIconTint,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = when (todayState) {
                            DayCompletionState.EXCUSED -> "Today is Excused: $todayExcuseReason (Streak Protected)"
                            DayCompletionState.EXCEEDED -> "Today Exceeded! 4/4 Core Devotions + Sunnah Habit"
                            DayCompletionState.COMPLETE -> "Today Complete! All 4 Core Devotions Finished"
                            else -> "Today in Progress: $todayCoreCount of 4 Core Devotions Completed"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = bannerText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-metrics Grid (Best Streak & Monthly Freezes)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = themeColors.surfaceElevated,
                    border = BorderStroke(1.dp, themeColors.border),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF143825) else themeColors.success.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = themeColors.success,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Best Streak",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = themeColors.textSecondary,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$longestStreak Days",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.textPrimary,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = themeColors.surfaceElevated,
                    border = BorderStroke(1.dp, themeColors.border),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF0C2A3A) else Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AcUnit,
                                contentDescription = null,
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Freeze Passes",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = themeColors.textSecondary,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$freezesRemaining / 2 Left",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.textPrimary,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakExcuseCard(
    todayExcused: Boolean,
    todayExcuseReason: String,
    yesterdayExcused: Boolean,
    onExcuseTodayClick: () -> Unit,
    onExcuseYesterdayClick: () -> Unit,
    onRemoveTodayExcuse: () -> Unit,
    themeColors: StreakThemeColors
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = themeColors.excusedBg,
        border = BorderStroke(1.dp, themeColors.excused.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(themeColors.excused),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Spa,
                        contentDescription = "Excuse Day",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "Excuse a Day (Ease & Rukhsah)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.textPrimary
                        )
                    )
                    Text(
                        text = "Travel, illness, menstruation, or personal rest",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.textSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Islam provides ease (رُخْصَة) during life circumstances. Excusing a day keeps your streak unbroken without consuming freeze passes and with zero explanation needed.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeColors.textPrimary,
                    lineHeight = 18.sp,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!todayExcused) {
                    Button(
                        onClick = onExcuseTodayClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeColors.excused,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Spa,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Excuse Today",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onRemoveTodayExcuse,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, themeColors.border),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = themeColors.surfaceElevated,
                            contentColor = themeColors.textPrimary
                        ),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text(
                            text = "Remove Excuse",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                if (!yesterdayExcused) {
                    OutlinedButton(
                        onClick = onExcuseYesterdayClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, themeColors.excused.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = themeColors.surface,
                            contentColor = themeColors.excused
                        ),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text(
                            text = "Excuse Yesterday",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakCalendarHeatmapCard(
    recentDays: List<DayStreakStatus>,
    habits: List<com.example.data.local.DailyHabitEntity> = emptyList(),
    hasExtraHabitToday: Boolean,
    onDayClick: (DayStreakStatus) -> Unit,
    themeColors: StreakThemeColors
) {
    val todayStr = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
    
    // Get the last 7 days from recentDays (which are in chronological order, ending with today)
    val weeklyDays = remember(recentDays) { recentDays.takeLast(7) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = themeColors.surface,
        border = BorderStroke(1.dp, themeColors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Consistency Map",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.textPrimary
                        )
                    )
                    Text(
                        text = "Your daily spiritual check-in",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.textSecondary,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7 Columns Row (recent 7 days) - Premium Vertical Column Sticks Bar Chart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                weeklyDays.forEach { day ->
                    val isToday = day.date == todayStr
                    val state = getDayCompletionState(day, habits = habits, isToday = isToday, hasExtraHabitToday = hasExtraHabitToday)
                    
                    val dayOfWeekLabel = remember(day.date) {
                        try {
                            val parsed = LocalDate.parse(day.date)
                            parsed.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())
                        } catch (e: Exception) {
                            ""
                        }
                    }

                    val dayNumStr = remember(day.date) {
                        try {
                            val parsed = LocalDate.parse(day.date)
                            parsed.dayOfMonth.toString()
                        } catch (e: Exception) {
                            ""
                        }
                    }

                    // Count completed tasks out of 5 for this day
                    val completedCount = remember(day, habits) {
                        if (day.isExcused || day.isFreezeUsed) 5
                        else {
                            var count = 0
                            if (day.salatCompleted) count++
                            if (day.quranCompleted) count++
                            if (day.azkarCompleted) count++
                            if (day.duaCompleted) count++
                            val hasExtra = habits.any { !isCoreHabitTitle(it.title) && it.completedDateIso == day.date }
                            if (hasExtra || day.tasbihCompleted) count++
                            count
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isToday) themeColors.success.copy(alpha = 0.05f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (isToday) 1.5.dp else 0.dp,
                                color = if (isToday) themeColors.success else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onDayClick(day) }
                            .padding(vertical = 12.dp, horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Completion indicator text above the stick
                        Text(
                            text = if (day.isExcused) "Exc" else if (day.isFreezeUsed) "Frz" else "$completedCount/5",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    day.isExcused -> themeColors.excused
                                    day.isFreezeUsed -> Color(0xFF0288D1)
                                    completedCount >= 4 -> themeColors.success
                                    completedCount > 0 -> themeColors.success.copy(alpha = 0.7f)
                                    else -> themeColors.textSecondary.copy(alpha = 0.6f)
                                },
                                fontSize = 10.sp
                            )
                        )

                        // 2. The "Long Stick" column chart bar container
                        Box(
                            modifier = Modifier
                                .height(110.dp)
                                .width(22.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(themeColors.border.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            // Filled stick representing height proportional to completedCount
                            val heightFraction = remember(completedCount, day) {
                                if (day.isExcused || day.isFreezeUsed) 1.0f
                                else (completedCount / 5f).coerceAtLeast(0.12f)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(heightFraction)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(
                                        when {
                                            day.isExcused -> themeColors.excused
                                            day.isFreezeUsed -> Color(0xFF0288D1)
                                            completedCount >= 4 -> themeColors.success
                                            completedCount > 0 -> themeColors.success.copy(alpha = 0.60f)
                                            else -> themeColors.border.copy(alpha = 0.4f)
                                        }
                                    ),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                if (completedCount >= 4 || day.isExcused || day.isFreezeUsed) {
                                    Icon(
                                        imageVector = when {
                                            day.isExcused -> Icons.Filled.Spa
                                            day.isFreezeUsed -> Icons.Filled.AcUnit
                                            else -> Icons.Filled.AutoAwesome
                                        },
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(12.dp)
                                    )
                                }
                            }
                        }

                        // 3. Day Label
                        Text(
                            text = dayOfWeekLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isToday) themeColors.success else themeColors.textSecondary,
                                fontSize = 11.sp
                            )
                        )

                        // 4. Day Number
                        Text(
                            text = dayNumStr,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = themeColors.textPrimary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Premium Compact Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(
                    color = themeColors.success,
                    borderColor = themeColors.success,
                    label = "Completed",
                    textColor = themeColors.textSecondary
                )
                LegendItem(
                    color = themeColors.excusedBg,
                    borderColor = themeColors.excused,
                    label = "Excused",
                    textColor = themeColors.textSecondary
                )
                LegendItem(
                    color = Color(0xFF0288D1).copy(alpha = 0.15f),
                    borderColor = Color(0xFF0288D1),
                    label = "Freeze Pass",
                    textColor = themeColors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    borderColor: Color,
    label: String,
    textColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
                .border(1.dp, borderColor, RoundedCornerShape(3.dp))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun StreakCompactActivityDashboardCard(
    todaySalatDone: Boolean,
    salatStreak: Int,
    salatHistory: List<Boolean>,
    todayQuranDone: Boolean,
    quranStreak: Int,
    quranHistory: List<Boolean>,
    todayAzkarDone: Boolean,
    azkarStreak: Int,
    azkarHistory: List<Boolean>,
    todayDuaDone: Boolean,
    duaStreak: Int,
    duaHistory: List<Boolean>,
    todayTasbihDone: Boolean,
    tasbihStreak: Int,
    tasbihHistory: List<Boolean>,
    onNavigate: (NoorDestination) -> Unit,
    themeColors: StreakThemeColors
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = themeColors.surface,
        border = BorderStroke(1.dp, themeColors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Activity Consistency",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.textPrimary
                )
            )
            Text(
                text = "Track and maintain individual daily devotions",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeColors.textSecondary,
                    fontSize = 11.5.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            val activities = remember(
                todaySalatDone, salatStreak, salatHistory,
                todayQuranDone, quranStreak, quranHistory,
                todayAzkarDone, azkarStreak, azkarHistory,
                todayDuaDone, duaStreak, duaHistory,
                todayTasbihDone, tasbihStreak, tasbihHistory
            ) {
                listOf(
                    CompactActivityData(
                        title = "Salat",
                        desc = "5 Daily Prayers",
                        isDone = todaySalatDone,
                        streak = salatStreak,
                        history = salatHistory,
                        icon = Icons.Filled.AccessTime,
                        dest = NoorDestination.SALAT
                    ),
                    CompactActivityData(
                        title = "Qur'an",
                        desc = "Daily Reading",
                        isDone = todayQuranDone,
                        streak = quranStreak,
                        history = quranHistory,
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        dest = NoorDestination.QURAN_READER
                    ),
                    CompactActivityData(
                        title = "Azkar",
                        desc = "Morning & Evening",
                        isDone = todayAzkarDone,
                        streak = azkarStreak,
                        history = azkarHistory,
                        icon = Icons.Filled.WbSunny,
                        dest = NoorDestination.AZKAR_READER
                    ),
                    CompactActivityData(
                        title = "Du'as",
                        desc = "Daily Supplications",
                        isDone = todayDuaDone,
                        streak = duaStreak,
                        history = duaHistory,
                        icon = Icons.Filled.Favorite,
                        dest = NoorDestination.DUAS_LIBRARY
                    ),
                    CompactActivityData(
                        title = "Tasbih",
                        desc = "Smart Counter",
                        isDone = todayTasbihDone,
                        streak = tasbihStreak,
                        history = tasbihHistory,
                        icon = Icons.Filled.Spa,
                        dest = NoorDestination.TASBIH
                    )
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                activities.forEach { act ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(themeColors.surfaceElevated.copy(alpha = 0.5f))
                            .clickable { onNavigate(act.dest) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (act.isDone) themeColors.success.copy(alpha = 0.12f)
                                        else themeColors.border.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = act.icon,
                                    contentDescription = act.title,
                                    tint = if (act.isDone) themeColors.success else themeColors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = act.title,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.textPrimary
                                    )
                                )
                                Text(
                                    text = act.desc,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeColors.textSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Compact History & Streak Columns
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Mini 7-day dot-history
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                act.history.forEach { done ->
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (done) themeColors.success
                                                else themeColors.border.copy(alpha = 0.6f)
                                            )
                                    )
                                }
                            }

                            // Sub-streak badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (act.streak > 0) themeColors.success.copy(alpha = 0.12f) else themeColors.surfaceElevated,
                                border = BorderStroke(1.dp, if (act.streak > 0) themeColors.success.copy(alpha = 0.3f) else themeColors.border)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = "Spiritual Devotion",
                                        tint = if (act.streak > 0) themeColors.success else themeColors.textSecondary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "${act.streak}d",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (act.streak > 0) themeColors.success else themeColors.textSecondary,
                                            fontSize = 10.sp
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

private data class CompactActivityData(
    val title: String,
    val desc: String,
    val isDone: Boolean,
    val streak: Int,
    val history: List<Boolean>,
    val icon: ImageVector,
    val dest: NoorDestination
)

@Composable
private fun StreakFreezeProtectionCard(
    freezesRemaining: Int,
    isYesterdayMissed: Boolean,
    onUseFreeze: () -> Unit,
    themeColors: StreakThemeColors
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = themeColors.surface,
        border = BorderStroke(1.dp, themeColors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(themeColors.freeze),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AcUnit,
                        contentDescription = "Streak Freeze",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "Monthly Freeze Passes",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.textPrimary
                        )
                    )
                    Text(
                        text = "$freezesRemaining monthly passes available",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.textSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Each month you receive 2 streak passes as a backup. Unlike excused absences, freeze passes auto-protect unintended missed days without specifying reasons.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeColors.textPrimary,
                    lineHeight = 18.sp,
                    fontSize = 12.sp
                )
            )

            if (isYesterdayMissed && freezesRemaining > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onUseFreeze,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColors.freeze,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Protect Missed Day with Freeze Pass",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SpiritualConsistencyQuoteCard(themeColors: StreakThemeColors) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = themeColors.surface,
        border = BorderStroke(1.dp, themeColors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FormatQuote,
                    contentDescription = "Quote",
                    tint = themeColors.textSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Spiritual Wisdom",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.textPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "«أَحَبُّ الأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ»",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.textPrimary,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "\"The deeds most loved by Allah are those that are done consistently, even if they are small.\"",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontStyle = FontStyle.Italic,
                    color = themeColors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "— Sahih al-Bukhari 6464",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = themeColors.textSecondary,
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DayDetailSheetContent(
    day: DayStreakStatus,
    habits: List<com.example.data.local.DailyHabitEntity> = emptyList(),
    hasExtraHabitToday: Boolean,
    onClose: () -> Unit,
    onExcuseDay: (String) -> Unit,
    onRemoveExcuse: () -> Unit,
    onToggleActivity: (StreakActivityType, Boolean) -> Unit,
    themeColors: StreakThemeColors
) {
    var showReasonSelector by remember { mutableStateOf(false) }

    val dayState = getDayCompletionState(day, habits = habits, isToday = false, hasExtraHabitToday = hasExtraHabitToday)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Day Activity: ${day.date}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.textPrimary
                    )
                )
                Text(
                    text = when (dayState) {
                        DayCompletionState.EXCUSED -> "Excused Day (${day.excuseReason}) • Streak Protected"
                        DayCompletionState.FREEZE -> "Protected by Freeze Pass"
                        DayCompletionState.EXCEEDED -> "Exceeded • 4/4 Core Devotions + Sunnah Completed"
                        DayCompletionState.COMPLETE -> "Complete • All 4 Core Devotions Completed"
                        DayCompletionState.NOT_DONE -> "${day.completedCount} of 4 Core Devotions Completed"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = when (dayState) {
                            DayCompletionState.EXCUSED -> themeColors.excused
                            DayCompletionState.EXCEEDED -> themeColors.goldExceeded
                            DayCompletionState.COMPLETE -> themeColors.success
                            else -> themeColors.textSecondary
                        },
                        fontWeight = if (dayState != DayCompletionState.NOT_DONE) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
            }

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = themeColors.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Excused Banner or Actions
        if (day.isExcused) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = themeColors.excusedBg,
                border = BorderStroke(1.dp, themeColors.excused.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Spa,
                            contentDescription = null,
                            tint = themeColors.excused,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Excused: ${day.excuseReason}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.textPrimary
                            )
                        )
                    }

                    TextButton(onClick = onRemoveExcuse) {
                        Text(
                            text = "Remove Excuse",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }
        } else {
            if (!showReasonSelector) {
                OutlinedButton(
                    onClick = { showReasonSelector = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, themeColors.excused.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = themeColors.excusedBg,
                        contentColor = themeColors.excused
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Spa,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Excuse This Day (Travel, Illness, Exemption)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = themeColors.surfaceElevated,
                    border = BorderStroke(1.dp, themeColors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Select Excuse Category:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.textPrimary
                            )
                        )
                        val reasons = listOf(
                            "Travel" to Icons.Filled.Flight,
                            "Illness" to Icons.Filled.LocalHospital,
                            "Menstruation" to Icons.Filled.Spa,
                            "Personal Exemption" to Icons.Filled.SelfImprovement
                        )
                        reasons.forEach { (reason, ic) ->
                            Button(
                                onClick = {
                                    onExcuseDay(reason)
                                    showReasonSelector = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = themeColors.surface,
                                    contentColor = themeColors.textPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = ic, contentDescription = null, tint = themeColors.excused, modifier = Modifier.size(16.dp))
                                    Text(text = reason, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Tap any activity to mark complete for ${day.date}:",
            style = MaterialTheme.typography.labelSmall.copy(
                color = themeColors.textSecondary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        DayActivityInteractiveRow(
            title = "Daily Obligatory Salat",
            isDone = day.salatCompleted,
            icon = Icons.Filled.AccessTime,
            onToggle = { onToggleActivity(StreakActivityType.SALAT, !day.salatCompleted) },
            themeColors = themeColors
        )
        DayActivityInteractiveRow(
            title = "Holy Qur'an Reading",
            isDone = day.quranCompleted,
            icon = Icons.AutoMirrored.Filled.MenuBook,
            onToggle = { onToggleActivity(StreakActivityType.QURAN, !day.quranCompleted) },
            themeColors = themeColors
        )
        DayActivityInteractiveRow(
            title = "Morning & Evening Azkar",
            isDone = day.azkarCompleted,
            icon = Icons.Filled.WbSunny,
            onToggle = { onToggleActivity(StreakActivityType.AZKAR, !day.azkarCompleted) },
            themeColors = themeColors
        )
        DayActivityInteractiveRow(
            title = "Daily Du'as & Supplications",
            isDone = day.duaCompleted,
            icon = Icons.Filled.Favorite,
            onToggle = { onToggleActivity(StreakActivityType.DUA, !day.duaCompleted) },
            themeColors = themeColors
        )
        DayActivityInteractiveRow(
            title = "Tasbih & Remembrance (Sunnah)",
            isDone = day.tasbihCompleted,
            icon = Icons.Filled.Spa,
            onToggle = { onToggleActivity(StreakActivityType.TASBIH, !day.tasbihCompleted) },
            themeColors = themeColors
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DayActivityInteractiveRow(
    title: String,
    isDone: Boolean,
    icon: ImageVector,
    onToggle: () -> Unit,
    themeColors: StreakThemeColors
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = themeColors.surfaceElevated,
        border = BorderStroke(1.dp, if (isDone) themeColors.success.copy(alpha = 0.3f) else themeColors.border),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDone) themeColors.success else themeColors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isDone) themeColors.textPrimary else themeColors.textSecondary
                    )
                )
            }

            Icon(
                imageVector = if (isDone) Icons.Filled.CheckCircle else Icons.Filled.Close,
                contentDescription = if (isDone) "Completed" else "Not Completed",
                tint = if (isDone) themeColors.success else themeColors.textSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ExcuseReasonPickerDialog(
    dateStr: String,
    onDismiss: () -> Unit,
    onSelectReason: (String) -> Unit,
    themeColors: StreakThemeColors
) {
    val reasons = listOf(
        "Travel" to "سفر • Traveling & Journey" to Icons.Filled.Flight,
        "Illness" to "مرض • Sickness & Recovery" to Icons.Filled.LocalHospital,
        "Menstruation" to "عذر شرعي • Menstruation / Haid" to Icons.Filled.Spa,
        "Personal Exemption" to "عذر شخصي • Personal Exemption" to Icons.Filled.SelfImprovement
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Excuse Day ($dateStr)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.textPrimary
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select an excuse category. This protects your streak consistency without using freeze passes or requiring explanation.",
                    style = MaterialTheme.typography.bodySmall.copy(color = themeColors.textSecondary)
                )
                Spacer(modifier = Modifier.height(4.dp))
                reasons.forEach { (pair, icon) ->
                    val (key, label) = pair
                    Button(
                        onClick = { onSelectReason(key) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeColors.surfaceElevated,
                            contentColor = themeColors.textPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = themeColors.excused, modifier = Modifier.size(18.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = themeColors.textSecondary
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = themeColors.surface
    )
}
