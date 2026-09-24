package com.example.ui.routines

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.DailyHabitEntity
import com.example.ui.MainViewModel
import com.example.ui.components.BentoCard
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.components.RadialProgressRing
import com.example.ui.theme.TitleSubtextSpacer
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// Official Light / Dark Palette Tokens
private val PrimaryTeal = Color(0xFF1BA486)
private val TealTintBg = Color(0xFFE6F6F1)
private val SoftNavyText = Color(0xFF2A4365)
private val SoftNavyPillBg = Color(0xFFEDF2F7)
private val GoldBadgeText = Color(0xFFC68A00)
private val GoldBadgeBg = Color(0xFFFBF0DC)
private val SurfaceCanvasLight = Color(0xFFF6F8F7)
private val NeutralBorderLight = Color(0xFFECEFF1)

// Google Calendar Style View Modes
private enum class GCalViewMode {
    DAY_TIMELINE,
    SCHEDULE_AGENDA,
    MONTH_GRID
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTrackerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    initialTab: Int = 0
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isLangArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val themeColors = if (isDark) com.example.ui.theme.ReadingThemes.ObsidianNight else com.example.ui.theme.ReadingThemes.MadaniCrisp

    // Date / Planner State
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val today = remember { LocalDate.now() }
    val isViewingToday = selectedDate == today

    // Calendar View State
    var gcalViewMode by remember { mutableStateOf(GCalViewMode.DAY_TIMELINE) }
    var calendarSelectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    // Dialog States
    var showAddDialog by remember { mutableStateOf(false) }
    var preselectedAddDateIso by remember { mutableStateOf<String?>(null) }
    var preselectedAddHour by remember { mutableStateOf<Int?>(null) }
    var editingHabitForSchedule by remember { mutableStateOf<DailyHabitEntity?>(null) }

    // Filter routines for the selected calendar day
    val selectedDayOfWeek = selectedDate.dayOfWeek
    val selectedBitIndex = when (selectedDayOfWeek) {
        DayOfWeek.MONDAY -> 0
        DayOfWeek.TUESDAY -> 1
        DayOfWeek.WEDNESDAY -> 2
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 4
        DayOfWeek.SATURDAY -> 5
        DayOfWeek.SUNDAY -> 6
    }
    val selectedDateStr = remember(selectedDate) { selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }

    val pinnedHabitsForDate = remember(habits, selectedDateStr, selectedBitIndex) {
        habits.filter { habit ->
            if (!habit.pinnedToPlanner) return@filter false
            if (!habit.targetDateIso.isNullOrBlank()) {
                habit.targetDateIso == selectedDateStr
            } else {
                (habit.pinnedDaysMask and (1 shl selectedBitIndex)) != 0
            }
        }
    }

    // Unpinned habits: Ensure each pinned routine is displayed ONLY ONCE in the pin zone
    val unpinnedHabits = remember(habits, pinnedHabitsForDate) {
        habits.filterNot { habit ->
            habit.pinnedToPlanner || pinnedHabitsForDate.any { it.id == habit.id }
        }
    }

    val totalCount = habits.size.coerceAtLeast(1)
    val completedCount = habits.count { it.isCompleted }

    val pinnedTotal = pinnedHabitsForDate.size
    val pinnedCompleted = pinnedHabitsForDate.count { it.isCompleted }
    val pinnedProgress = if (pinnedTotal > 0) pinnedCompleted.toFloat() / pinnedTotal.toFloat() else 0f

    Scaffold(
        topBar = {
            NoorTopBar(
                title = if (selectedTab == 0) {
                    if (isLangArabic) "الجدول اليومي والعادات" else "Daily Planner & Routines"
                } else {
                    if (isLangArabic) "تقويم العادات المجدولة" else "Google Style Calendar"
                },
                eyebrow = "ISTIQAMAH PLANNER",
                subtitle = if (selectedTab == 0) {
                    "$completedCount/$totalCount Completed • Steadfast Consistency"
                } else {
                    "Google Calendar Timeline & Schedule"
                },
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back",
                isDark = isDark,
                themeColors = themeColors,
                actions = {
                    NoorGlassIconButton(
                        onClick = {
                            preselectedAddDateIso = if (selectedTab == 1) {
                                calendarSelectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            } else null
                            preselectedAddHour = null
                            showAddDialog = true
                        },
                        icon = Icons.Default.Add,
                        contentDescription = "Add Routine"
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    preselectedAddDateIso = if (selectedTab == 1) {
                        calendarSelectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    } else null
                    preselectedAddHour = null
                    showAddDialog = true
                },
                containerColor = PrimaryTeal,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_habit_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Routine")
            }
        },
        containerColor = if (isDark) themeColors.background else SurfaceCanvasLight,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main Tab Switcher: Planner & Tasks (0) | Google Calendar View (1)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .height(46.dp),
                shape = CircleShape,
                color = if (isDark) Color(0xFF1E293B) else SoftNavyPillBg
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Tab 0: Daily Planner & Tasks
                    val isTasksSelected = selectedTab == 0
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(if (isTasksSelected) PrimaryTeal else Color.Transparent)
                            .clickable { selectedTab = 0 },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = null,
                                tint = if (isTasksSelected) Color.White else (if (isDark) Color(0xFF94A3B8) else SoftNavyText),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isLangArabic) "الجدول والمهام" else "Planner & Tasks",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (isTasksSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isTasksSelected) Color.White else (if (isDark) Color(0xFF94A3B8) else SoftNavyText),
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }

                    // Tab 1: Google Calendar View
                    val isCalendarSelected = selectedTab == 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(if (isCalendarSelected) PrimaryTeal else Color.Transparent)
                            .clickable { selectedTab = 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = if (isCalendarSelected) Color.White else (if (isDark) Color(0xFF94A3B8) else SoftNavyText),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isLangArabic) "تقويم المهام (Calendar)" else "Calendar Style",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (isCalendarSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCalendarSelected) Color.White else (if (isDark) Color(0xFF94A3B8) else SoftNavyText),
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (selectedTab == 0) {
                // Tab 0: Daily Planner & Tasks View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 86.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Horizontal Date Strip (Lightweight Calendar Horizon)
                    item(key = "calendar_horizon_strip") {
                        CalendarHorizonStrip(
                            selectedDate = selectedDate,
                            today = today,
                            habits = habits,
                            isDark = isDark,
                            isArabic = isLangArabic,
                            onSelectDate = { selectedDate = it }
                        )
                    }

                    // 2. Selected Day Planner Agenda Banner
                    item(key = "planner_agenda_banner") {
                        PlannerAgendaBanner(
                            selectedDate = selectedDate,
                            isToday = isViewingToday,
                            pinnedCount = pinnedTotal,
                            completedPinnedCount = pinnedCompleted,
                            pinnedProgress = pinnedProgress,
                            isDark = isDark,
                            isArabic = isLangArabic,
                            onJumpToToday = { selectedDate = today }
                        )
                    }

                    // 3. Pinned Routines Section for Selected Date (Shown ONCE only)
                    if (pinnedHabitsForDate.isNotEmpty()) {
                        item(key = "pinned_header") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = null,
                                        tint = GoldBadgeText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (isLangArabic) "المهام المثبتة لليوم ($pinnedCompleted/$pinnedTotal)" else "Pinned to Day Agenda ($pinnedCompleted/$pinnedTotal)",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFFF1F5F9) else SoftNavyText,
                                            fontSize = 13.sp
                                        )
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isDark) Color(0xFF334155) else GoldBadgeBg
                                ) {
                                    Text(
                                        text = if (pinnedProgress >= 1f) (if (isLangArabic) "مكتمل ✓" else "Completed ✓") else (if (isLangArabic) "قيد الإنجاز" else "In Progress"),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GoldBadgeText,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        items(pinnedHabitsForDate, key = { "pinned_${it.id}" }) { habit ->
                            HabitItemCard(
                                habit = habit,
                                isDark = isDark,
                                isArabic = isLangArabic,
                                isPinnedView = true,
                                onIncrement = { viewModel.incrementHabit(habit) },
                                onMarkDone = { viewModel.markHabitDone(habit) },
                                onTogglePin = { viewModel.toggleHabitPin(habit) },
                                onEditSchedule = { editingHabitForSchedule = habit },
                                onDelete = { viewModel.deleteHabit(habit) }
                            )
                        }
                    }

                    // 4. Other / Unpinned Spiritual Habits Header
                    item(key = "other_habits_header") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (pinnedHabitsForDate.isNotEmpty()) {
                                    if (isLangArabic) "بقية العادات والأوراد" else "Other Daily Habits & Routines"
                                } else {
                                    if (isLangArabic) "جميع العادات والأوراد اليومية" else "All Daily Spiritual Habits"
                                },
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFF1F5F9) else SoftNavyText,
                                    fontSize = 14.sp
                                )
                            )

                            Text(
                                text = if (isLangArabic) "اسحب للإكمال أو الحذف" else "Swipe to done/delete",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5F5E5A),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // 5. Unpinned Habits List (Never duplicates pinned items)
                    if (unpinnedHabits.isEmpty() && pinnedHabitsForDate.isNotEmpty()) {
                        item(key = "all_pinned_notice") {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isDark) Color(0xFF1E293B) else Color.White,
                                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else NeutralBorderLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PushPin, contentDescription = null, tint = GoldBadgeText)
                                    Text(
                                        text = if (isLangArabic) "جميع مهامك وأورادك مثبتة في الأجندة اليومية أعلاه 🌟" else "All your active habits are pinned to your agenda above 🌟",
                                        style = MaterialTheme.typography.bodySmall.copy(color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5F5E5A))
                                    )
                                }
                            }
                        }
                    } else {
                        items(unpinnedHabits, key = { "unpinned_${it.id}" }) { habit ->
                            SwipeableHabitRow(
                                habit = habit,
                                onMarkAsDone = { viewModel.markHabitDone(habit) },
                                onDelete = { viewModel.deleteHabit(habit) }
                            ) {
                                HabitItemCard(
                                    habit = habit,
                                    isDark = isDark,
                                    isArabic = isLangArabic,
                                    isPinnedView = false,
                                    onIncrement = { viewModel.incrementHabit(habit) },
                                    onMarkDone = { viewModel.markHabitDone(habit) },
                                    onTogglePin = { viewModel.toggleHabitPin(habit) },
                                    onEditSchedule = { editingHabitForSchedule = habit },
                                    onDelete = { viewModel.deleteHabit(habit) }
                                )
                            }
                        }
                    }
                }
            } else {
                // Tab 1: Full Google Calendar Style Experience
                GoogleCalendarExperience(
                    viewMode = gcalViewMode,
                    currentYearMonth = currentYearMonth,
                    selectedDate = calendarSelectedDate,
                    habits = habits,
                    isDark = isDark,
                    isArabic = isLangArabic,
                    onViewModeChange = { gcalViewMode = it },
                    onSelectDate = { calendarSelectedDate = it },
                    onPreviousMonth = { currentYearMonth = currentYearMonth.minusMonths(1) },
                    onNextMonth = { currentYearMonth = currentYearMonth.plusMonths(1) },
                    onJumpToToday = {
                        val now = LocalDate.now()
                        calendarSelectedDate = now
                        currentYearMonth = YearMonth.of(now.year, now.month)
                    },
                    onIncrementHabit = { viewModel.incrementHabit(it) },
                    onMarkDoneHabit = { viewModel.markHabitDone(it) },
                    onTogglePinHabit = { viewModel.toggleHabitPin(it) },
                    onEditHabitSchedule = { editingHabitForSchedule = it },
                    onDeleteHabit = { viewModel.deleteHabit(it) },
                    onAddNewRoutineForSlot = { date, hour ->
                        preselectedAddDateIso = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        preselectedAddHour = hour
                        showAddDialog = true
                    }
                )
            }
        }
    }

    // Add Routine Modal with Integrated Planner & Alarms
    if (showAddDialog) {
        AddHabitPlannerDialog(
            isDark = isDark,
            isArabic = isLangArabic,
            initialTargetDateIso = preselectedAddDateIso,
            initialHour = preselectedAddHour,
            onDismiss = {
                showAddDialog = false
                preselectedAddDateIso = null
                preselectedAddHour = null
            },
            onConfirm = { title, target, category, isPinned, daysMask, timeMinutes, isAlarm, targetDateIso, notes ->
                viewModel.addCustomHabitWithPlanner(
                    title = title,
                    target = target,
                    category = category,
                    isPinned = isPinned,
                    daysMask = daysMask,
                    timeMinutes = timeMinutes,
                    isAlarmEnabled = isAlarm,
                    targetDateIso = targetDateIso,
                    notes = notes
                )
                showAddDialog = false
                preselectedAddDateIso = null
                preselectedAddHour = null
            }
        )
    }

    // Edit Routine Schedule & Alarm Modal
    editingHabitForSchedule?.let { habit ->
        HabitScheduleAlarmDialog(
            habit = habit,
            isDark = isDark,
            isArabic = isLangArabic,
            onDismiss = { editingHabitForSchedule = null },
            onSave = { isPinned, daysMask, timeMinutes, isAlarm, targetDateIso, notes ->
                viewModel.updateHabitPlannerSchedule(
                    habit = habit,
                    isPinned = isPinned,
                    daysMask = daysMask,
                    timeMinutes = timeMinutes,
                    isAlarmEnabled = isAlarm,
                    targetDateIso = targetDateIso,
                    notes = notes
                )
                editingHabitForSchedule = null
            }
        )
    }
}

// -----------------------------------------------------------------------------------------
// Google Calendar Complete View Experience
// -----------------------------------------------------------------------------------------

@Composable
private fun GoogleCalendarExperience(
    viewMode: GCalViewMode,
    currentYearMonth: YearMonth,
    selectedDate: LocalDate,
    habits: List<DailyHabitEntity>,
    isDark: Boolean,
    isArabic: Boolean,
    onViewModeChange: (GCalViewMode) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onJumpToToday: () -> Unit,
    onIncrementHabit: (DailyHabitEntity) -> Unit,
    onMarkDoneHabit: (DailyHabitEntity) -> Unit,
    onTogglePinHabit: (DailyHabitEntity) -> Unit,
    onEditHabitSchedule: (DailyHabitEntity) -> Unit,
    onDeleteHabit: (DailyHabitEntity) -> Unit,
    onAddNewRoutineForSlot: (LocalDate, Int?) -> Unit
) {
    val today = remember { LocalDate.now() }
    val monthTitleFormatter = remember(isArabic) {
        DateTimeFormatter.ofPattern("MMMM yyyy", if (isArabic) Locale("ar") else Locale.ENGLISH)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 1. Google Calendar Style App Header Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(18.dp),
            color = if (isDark) Color(0xFF1E293B) else Color.White,
            border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else NeutralBorderLight)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Month title with navigation
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = currentYearMonth.format(monthTitleFormatter),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isDark) Color.White else SoftNavyText
                            )
                        )

                        IconButton(onClick = onPreviousMonth, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = if (isArabic) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Prev Month",
                                tint = PrimaryTeal,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(onClick = onNextMonth, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = if (isArabic) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Month",
                                tint = PrimaryTeal,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Google Calendar Iconic "Today" Avatar Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color(0xFF334155) else TealTintBg,
                        border = BorderStroke(1.dp, PrimaryTeal.copy(alpha = 0.5f)),
                        modifier = Modifier.clickable { onJumpToToday() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Jump to Today",
                                tint = PrimaryTeal,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${today.dayOfMonth} ${if (isArabic) "اليوم" else "Today"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = PrimaryTeal
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 2. Google Calendar View Mode Switcher (Day Timeline | Schedule Agenda | Month Grid)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF0F172A) else SoftNavyPillBg)
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    GCalModeChip(
                        label = if (isArabic) "اليوم والجدول" else "Day Timeline",
                        icon = Icons.Default.ViewDay,
                        isSelected = viewMode == GCalViewMode.DAY_TIMELINE,
                        onClick = { onViewModeChange(GCalViewMode.DAY_TIMELINE) },
                        modifier = Modifier.weight(1f),
                        isDark = isDark
                    )

                    GCalModeChip(
                        label = if (isArabic) "الأجندة" else "Schedule",
                        icon = Icons.Default.ViewAgenda,
                        isSelected = viewMode == GCalViewMode.SCHEDULE_AGENDA,
                        onClick = { onViewModeChange(GCalViewMode.SCHEDULE_AGENDA) },
                        modifier = Modifier.weight(1f),
                        isDark = isDark
                    )

                    GCalModeChip(
                        label = if (isArabic) "الشهر" else "Month",
                        icon = Icons.Default.CalendarMonth,
                        isSelected = viewMode == GCalViewMode.MONTH_GRID,
                        onClick = { onViewModeChange(GCalViewMode.MONTH_GRID) },
                        modifier = Modifier.weight(1f),
                        isDark = isDark
                    )
                }
            }
        }

        // 3. Google Calendar 7-Day Week Carousel Header
        GoogleCalendarWeekHeader(
            selectedDate = selectedDate,
            today = today,
            habits = habits,
            isDark = isDark,
            isArabic = isArabic,
            onSelectDate = onSelectDate
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 4. Main Google Calendar Content by View Mode
        when (viewMode) {
            GCalViewMode.DAY_TIMELINE -> {
                GoogleCalendarDayTimeline(
                    selectedDate = selectedDate,
                    today = today,
                    habits = habits,
                    isDark = isDark,
                    isArabic = isArabic,
                    onIncrementHabit = onIncrementHabit,
                    onMarkDoneHabit = onMarkDoneHabit,
                    onTogglePinHabit = onTogglePinHabit,
                    onEditHabitSchedule = onEditHabitSchedule,
                    onDeleteHabit = onDeleteHabit,
                    onAddNewRoutineForSlot = onAddNewRoutineForSlot
                )
            }
            GCalViewMode.SCHEDULE_AGENDA -> {
                GoogleCalendarScheduleAgenda(
                    selectedDate = selectedDate,
                    today = today,
                    habits = habits,
                    isDark = isDark,
                    isArabic = isArabic,
                    onSelectDate = onSelectDate,
                    onIncrementHabit = onIncrementHabit,
                    onMarkDoneHabit = onMarkDoneHabit,
                    onTogglePinHabit = onTogglePinHabit,
                    onEditHabitSchedule = onEditHabitSchedule,
                    onDeleteHabit = onDeleteHabit,
                    onAddNewRoutineForSlot = onAddNewRoutineForSlot
                )
            }
            GCalViewMode.MONTH_GRID -> {
                GoogleCalendarMonthGrid(
                    currentYearMonth = currentYearMonth,
                    selectedDate = selectedDate,
                    today = today,
                    habits = habits,
                    isDark = isDark,
                    isArabic = isArabic,
                    onSelectDate = {
                        onSelectDate(it)
                        onViewModeChange(GCalViewMode.DAY_TIMELINE)
                    }
                )
            }
        }
    }
}

@Composable
private fun GCalModeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) PrimaryTeal else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else (if (isDark) Color(0xFF94A3B8) else SoftNavyText),
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp,
                    color = if (isSelected) Color.White else (if (isDark) Color(0xFF94A3B8) else SoftNavyText)
                )
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// Google Calendar 7-Day Carousel Header Strip
// -----------------------------------------------------------------------------------------

@Composable
private fun GoogleCalendarWeekHeader(
    selectedDate: LocalDate,
    today: LocalDate,
    habits: List<DailyHabitEntity>,
    isDark: Boolean,
    isArabic: Boolean,
    onSelectDate: (LocalDate) -> Unit
) {
    // Current week window around selected date (Monday to Sunday)
    val startOfWeek = selectedDate.minusDays((selectedDate.dayOfWeek.value - 1).toLong())
    val weekDays = remember(startOfWeek) {
        (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(18.dp),
        color = if (isDark) Color(0xFF1E293B) else Color.White,
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else NeutralBorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            weekDays.forEach { date ->
                val isSelected = date == selectedDate
                val isCellToday = date == today

                val dayOfWeek = date.dayOfWeek
                val bitIndex = when (dayOfWeek) {
                    DayOfWeek.MONDAY -> 0
                    DayOfWeek.TUESDAY -> 1
                    DayOfWeek.WEDNESDAY -> 2
                    DayOfWeek.THURSDAY -> 3
                    DayOfWeek.FRIDAY -> 4
                    DayOfWeek.SATURDAY -> 5
                    DayOfWeek.SUNDAY -> 6
                }
                val dateIso = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val dayHabits = habits.filter { habit ->
                    if (!habit.targetDateIso.isNullOrBlank()) {
                        habit.targetDateIso == dateIso
                    } else {
                        (habit.pinnedDaysMask and (1 shl bitIndex)) != 0
                    }
                }
                val hasPinned = dayHabits.any { it.pinnedToPlanner }
                val hasHabits = dayHabits.isNotEmpty()

                val dayLabel = if (isArabic) {
                    when (dayOfWeek) {
                        DayOfWeek.MONDAY -> "إثن"
                        DayOfWeek.TUESDAY -> "ثلا"
                        DayOfWeek.WEDNESDAY -> "أرب"
                        DayOfWeek.THURSDAY -> "خمي"
                        DayOfWeek.FRIDAY -> "جمع"
                        DayOfWeek.SATURDAY -> "سبت"
                        DayOfWeek.SUNDAY -> "أحد"
                    }
                } else {
                    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).take(1).uppercase()
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            when {
                                isSelected -> PrimaryTeal
                                isCellToday -> if (isDark) Color(0xFF334155) else TealTintBg
                                else -> if (isDark) Color(0xFF0F172A) else SoftNavyPillBg.copy(alpha = 0.6f)
                            }
                        )
                        .border(
                            width = if (isCellToday && !isSelected) 1.5.dp else 0.dp,
                            color = if (isCellToday && !isSelected) PrimaryTeal else Color.Transparent,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectDate(date) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = dayLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else (if (isCellToday) PrimaryTeal else (if (isDark) Color(0xFF94A3B8) else Color(0xFF5F5E5A)))
                            )
                        )
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isSelected) Color.White else (if (isCellToday) PrimaryTeal else (if (isDark) Color(0xFFF1F5F9) else SoftNavyText))
                            )
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// 1. Google Calendar Day Timeline View (Hour-by-hour 24h Grid)
// -----------------------------------------------------------------------------------------

@Composable
private fun GoogleCalendarDayTimeline(
    selectedDate: LocalDate,
    today: LocalDate,
    habits: List<DailyHabitEntity>,
    isDark: Boolean,
    isArabic: Boolean,
    onIncrementHabit: (DailyHabitEntity) -> Unit,
    onMarkDoneHabit: (DailyHabitEntity) -> Unit,
    onTogglePinHabit: (DailyHabitEntity) -> Unit,
    onEditHabitSchedule: (DailyHabitEntity) -> Unit,
    onDeleteHabit: (DailyHabitEntity) -> Unit,
    onAddNewRoutineForSlot: (LocalDate, Int?) -> Unit
) {
    val selectedDayOfWeek = selectedDate.dayOfWeek
    val selectedBitIndex = when (selectedDayOfWeek) {
        DayOfWeek.MONDAY -> 0
        DayOfWeek.TUESDAY -> 1
        DayOfWeek.WEDNESDAY -> 2
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 4
        DayOfWeek.SATURDAY -> 5
        DayOfWeek.SUNDAY -> 6
    }
    val selectedDateIso = remember(selectedDate) { selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }

    val dayHabits = remember(habits, selectedDateIso, selectedBitIndex) {
        habits.filter { habit ->
            if (!habit.targetDateIso.isNullOrBlank()) {
                habit.targetDateIso == selectedDateIso
            } else {
                (habit.pinnedDaysMask and (1 shl selectedBitIndex)) != 0
            }
        }
    }

    var nowTime by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            nowTime = LocalTime.now()
            kotlinx.coroutines.delay(10000)
        }
    }

    val isToday = selectedDate == today

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 86.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 24-Hour Timeline Grid (04:00 AM to 11:00 PM) - Tasks appear directly INSIDE the calendar slots
        val startHour = 4
        val endHour = 23

        items(count = (endHour - startHour + 1)) { index ->
            val hour = startHour + index
            val hourRoutines = dayHabits.filter { habit ->
                val routineHour = (habit.scheduledTimeMinutes ?: 480) / 60
                routineHour == hour
            }

            val isCurrentHourSlot = isToday && (nowTime.hour == hour)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Time Label Column
                val amPm = if (hour >= 12) "PM" else "AM"
                val displayHour = if (hour % 12 == 0) 12 else hour % 12
                val timeLabel = String.format(Locale.ENGLISH, "%02d:00 %s", displayHour, amPm)

                Column(
                    modifier = Modifier
                        .width(62.dp)
                        .padding(top = 4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = String.format(Locale.ENGLISH, "%02d:00", displayHour),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isCurrentHourSlot) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.5.sp,
                            color = if (isCurrentHourSlot) PrimaryTeal else (if (isDark) Color(0xFF94A3B8) else Color(0xFF5F5E5A))
                        )
                    )
                    Text(
                        text = amPm,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            color = if (isDark) Color(0xFF64748B) else Color(0xFF9E9E9E)
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Timeline Line & Content Column
                Column(modifier = Modifier.weight(1f)) {
                    // Subtle Google Calendar Grid Line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(if (isDark) Color(0xFF334155) else NeutralBorderLight)
                    )

                    // Current Time Red Indicator Line (Google Calendar signature)
                    if (isCurrentHourSlot) {
                        val fraction = (nowTime.minute.toFloat() / 60f).coerceIn(0f, 1f)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = (fraction * 16).dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryTeal)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(PrimaryTeal)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (hourRoutines.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            hourRoutines.forEach { habit ->
                                GoogleCalendarEventCard(
                                    habit = habit,
                                    isDark = isDark,
                                    isArabic = isArabic,
                                    onIncrement = { onIncrementHabit(habit) },
                                    onMarkDone = { onMarkDoneHabit(habit) },
                                    onTogglePin = { onTogglePinHabit(habit) },
                                    onEditSchedule = { onEditHabitSchedule(habit) },
                                    onDelete = { onDeleteHabit(habit) }
                                )
                            }
                        }
                    } else {
                        // Empty slot clickable area to easily schedule an item at this hour!
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onAddNewRoutineForSlot(selectedDate, hour) }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = if (isArabic) "+ إضافة ورد عند $displayHour $amPm" else "+ Add routine at $displayHour $amPm",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// 2. Google Calendar Schedule (Agenda) Stream View
// -----------------------------------------------------------------------------------------

@Composable
private fun GoogleCalendarScheduleAgenda(
    selectedDate: LocalDate,
    today: LocalDate,
    habits: List<DailyHabitEntity>,
    isDark: Boolean,
    isArabic: Boolean,
    onSelectDate: (LocalDate) -> Unit,
    onIncrementHabit: (DailyHabitEntity) -> Unit,
    onMarkDoneHabit: (DailyHabitEntity) -> Unit,
    onTogglePinHabit: (DailyHabitEntity) -> Unit,
    onEditHabitSchedule: (DailyHabitEntity) -> Unit,
    onDeleteHabit: (DailyHabitEntity) -> Unit,
    onAddNewRoutineForSlot: (LocalDate, Int?) -> Unit
) {
    // Generate consecutive 14 days starting from today
    val daysStream = remember(today) {
        (0..13).map { today.plusDays(it.toLong()) }
    }

    val dayHeaderFormatter = remember(isArabic) {
        DateTimeFormatter.ofPattern("EEEE, d MMMM", if (isArabic) Locale("ar") else Locale.ENGLISH)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 86.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        daysStream.forEach { date ->
            val isCurrentToday = date == today
            val dateDayOfWeek = date.dayOfWeek
            val bitIndex = when (dateDayOfWeek) {
                DayOfWeek.MONDAY -> 0
                DayOfWeek.TUESDAY -> 1
                DayOfWeek.WEDNESDAY -> 2
                DayOfWeek.THURSDAY -> 3
                DayOfWeek.FRIDAY -> 4
                DayOfWeek.SATURDAY -> 5
                DayOfWeek.SUNDAY -> 6
            }
            val dateIso = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val streamHabits = habits.filter { habit ->
                if (!habit.targetDateIso.isNullOrBlank()) {
                    habit.targetDateIso == dateIso
                } else {
                    (habit.pinnedDaysMask and (1 shl bitIndex)) != 0
                }
            }.sortedBy { it.scheduledTimeMinutes ?: 9999 }

            item(key = "agenda_day_header_$dateIso") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Date Badge
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isCurrentToday) PrimaryTeal else (if (isDark) Color(0xFF334155) else SoftNavyPillBg)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isCurrentToday) Color.White else (if (isDark) Color.White else SoftNavyText)
                                )
                            )
                        }

                        Column {
                            Text(
                                text = if (isCurrentToday) (if (isArabic) "اليوم الحاضر" else "TODAY") else date.format(dayHeaderFormatter).uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isCurrentToday) PrimaryTeal else (if (isDark) Color(0xFF94A3B8) else SoftNavyText)
                                )
                            )
                            Text(
                                text = date.format(dayHeaderFormatter),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF1F1F1F)
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = { onAddNewRoutineForSlot(date, null) },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (streamHabits.isEmpty()) {
                item(key = "agenda_empty_$dateIso") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color(0xFF1E293B) else Color.White,
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else NeutralBorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isArabic) "لا توجد أوراد مجدولة • انقر على + لإضافة ورد" else "No routines scheduled • Tap + to add",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isDark) Color(0xFF64748B) else Color(0xFF9E9E9E),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            } else {
                items(streamHabits, key = { "agenda_habit_${dateIso}_${it.id}" }) { habit ->
                    GoogleCalendarEventCard(
                        habit = habit,
                        isDark = isDark,
                        isArabic = isArabic,
                        onIncrement = { onIncrementHabit(habit) },
                        onMarkDone = { onMarkDoneHabit(habit) },
                        onTogglePin = { onTogglePinHabit(habit) },
                        onEditSchedule = { onEditHabitSchedule(habit) },
                        onDelete = { onDeleteHabit(habit) }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// 3. Google Calendar Month Grid View (Matrix Layout)
// -----------------------------------------------------------------------------------------

@Composable
private fun GoogleCalendarMonthGrid(
    currentYearMonth: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    habits: List<DailyHabitEntity>,
    isDark: Boolean,
    isArabic: Boolean,
    onSelectDate: (LocalDate) -> Unit
) {
    val firstDayOfMonth = currentYearMonth.atDay(1)
    val daysInMonth = currentYearMonth.lengthOfMonth()
    val leadingEmptyDays = (firstDayOfMonth.dayOfWeek.value - 1) // 0 for Mon..6 for Sun

    val totalGridCells = leadingEmptyDays + daysInMonth
    val rows = (totalGridCells + 6) / 7

    val daysHeaderLabels = if (isArabic) {
        listOf("إثن", "ثلا", "أرب", "خمي", "جمع", "سبت", "أحد")
    } else {
        listOf("M", "T", "W", "T", "F", "S", "S")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 86.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "month_matrix_card") {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = if (isDark) Color(0xFF1E293B) else Color.White,
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else NeutralBorderLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Day of Week Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        daysHeaderLabels.forEach { label ->
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5F5E5A)
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Month Matrix
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (rowIndex in 0 until rows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (colIndex in 0 until 7) {
                                    val cellIndex = rowIndex * 7 + colIndex
                                    val dayNumber = cellIndex - leadingEmptyDays + 1

                                    if (dayNumber in 1..daysInMonth) {
                                        val cellDate = currentYearMonth.atDay(dayNumber)
                                        val isSelected = cellDate == selectedDate
                                        val isCellToday = cellDate == today

                                        val cellDayOfWeek = cellDate.dayOfWeek
                                        val cellBitIndex = when (cellDayOfWeek) {
                                            DayOfWeek.MONDAY -> 0
                                            DayOfWeek.TUESDAY -> 1
                                            DayOfWeek.WEDNESDAY -> 2
                                            DayOfWeek.THURSDAY -> 3
                                            DayOfWeek.FRIDAY -> 4
                                            DayOfWeek.SATURDAY -> 5
                                            DayOfWeek.SUNDAY -> 6
                                        }
                                        val cellDateIso = cellDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        val cellRoutines = habits.filter { habit ->
                                            if (!habit.targetDateIso.isNullOrBlank()) {
                                                habit.targetDateIso == cellDateIso
                                            } else {
                                                (habit.pinnedDaysMask and (1 shl cellBitIndex)) != 0
                                            }
                                        }
                                        val hasPinned = cellRoutines.any { it.pinnedToPlanner }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(0.9f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    when {
                                                        isSelected -> PrimaryTeal
                                                        isCellToday -> if (isDark) Color(0xFF334155) else TealTintBg
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .border(
                                                    width = if (isCellToday && !isSelected) 1.5.dp else 0.dp,
                                                    color = if (isCellToday && !isSelected) PrimaryTeal else Color.Transparent,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { onSelectDate(cellDate) }
                                                .padding(2.dp),
                                            contentAlignment = Alignment.TopCenter
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Text(
                                                    text = dayNumber.toString(),
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = if (isSelected || isCellToday) FontWeight.Bold else FontWeight.Medium,
                                                        fontSize = 12.5.sp,
                                                        color = if (isSelected) Color.White else (if (isDark) Color(0xFFF1F5F9) else Color(0xFF1F1F1F))
                                                    )
                                                )

                                                // Google Calendar Mini Pill Previews
                                                if (cellRoutines.isNotEmpty()) {
                                                    val previewText = cellRoutines.first().title
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = if (isSelected) Color.White.copy(alpha = 0.3f) else (if (hasPinned) GoldBadgeBg else TealTintBg),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            text = previewText,
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isSelected) Color.White else (if (hasPinned) GoldBadgeText else PrimaryTeal)
                                                            ),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
                                                        )
                                                    }

                                                    if (cellRoutines.size > 1) {
                                                        Text(
                                                            text = "+${cellRoutines.size - 1}",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 7.5.sp,
                                                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0xFF9E9E9E)
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Box(modifier = Modifier.weight(1f).aspectRatio(0.9f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Google Calendar Style Event Cards & Chips
// -----------------------------------------------------------------------------------------

@Composable
private fun GoogleCalendarEventCard(
    habit: DailyHabitEntity,
    isDark: Boolean,
    isArabic: Boolean,
    onIncrement: () -> Unit,
    onMarkDone: () -> Unit,
    onTogglePin: () -> Unit,
    onEditSchedule: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryAccent = when (habit.category.lowercase()) {
        "quran" -> PrimaryTeal
        "salah", "sunnah", "prayer" -> PrimaryTeal
        "dhikr" -> GoldBadgeText
        else -> SoftNavyText
    }

    val formattedTime = remember(habit.scheduledTimeMinutes) {
        if (habit.scheduledTimeMinutes != null) {
            val hour = habit.scheduledTimeMinutes / 60
            val minute = habit.scheduledTimeMinutes % 60
            val amPm = if (hour >= 12) "PM" else "AM"
            val displayHour = if (hour % 12 == 0) 12 else hour % 12
            String.format(Locale.ENGLISH, "%02d:%02d %s", displayHour, minute, amPm)
        } else ""
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { if (habit.isCompleted) onIncrement() else onMarkDone() },
        shape = RoundedCornerShape(10.dp),
        color = if (habit.isCompleted) (if (isDark) Color(0xFF1E293B) else TealTintBg)
                else (if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Completion Checkmark Toggle
            IconButton(
                onClick = {
                    if (habit.isCompleted) onIncrement() else onMarkDone()
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (habit.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = "Done",
                    tint = if (habit.isCompleted) PrimaryTeal else (if (isDark) Color(0xFF94A3B8) else Color(0xFF9E9E9E)),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isDark) Color.White else Color(0xFF1F1F1F)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    // Category Subtitle / Label
                    Text(
                        text = habit.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryAccent
                        )
                    )

                    // Time & Alarm Badge
                    if (formattedTime.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.clickable { onEditSchedule() }
                        ) {
                            if (habit.isAlarmEnabled) {
                                Icon(
                                    imageVector = Icons.Default.AlarmOn,
                                    contentDescription = "Alarm",
                                    tint = PrimaryTeal,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (habit.isAlarmEnabled) PrimaryTeal else (if (isDark) Color(0xFF94A3B8) else Color(0xFF5F5E5A))
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
private fun GoogleCalendarEventChip(
    habit: DailyHabitEntity,
    isDark: Boolean,
    isArabic: Boolean,
    onIncrement: () -> Unit,
    onMarkDone: () -> Unit,
    onTogglePin: () -> Unit,
    onEditSchedule: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (habit.pinnedToPlanner) GoldBadgeBg else (if (isDark) Color(0xFF0F3E34) else TealTintBg),
        border = BorderStroke(1.dp, if (habit.pinnedToPlanner) GoldBadgeText.copy(alpha = 0.4f) else PrimaryTeal.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onIncrement() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (habit.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = if (habit.isCompleted) PrimaryTeal else Color(0xFF9E9E9E),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { if (habit.isCompleted) onIncrement() else onMarkDone() }
                )
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isDark) Color.White else Color(0xFF1F1F1F)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "${habit.currentCount}/${habit.targetCount}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal,
                    fontSize = 11.sp
                )
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// Sub-components: Horizon Strip, Agenda Banner, Habit Item Card, Swipeable Row
// -----------------------------------------------------------------------------------------

@Composable
private fun CalendarHorizonStrip(
    selectedDate: LocalDate,
    today: LocalDate,
    habits: List<DailyHabitEntity>,
    isDark: Boolean,
    isArabic: Boolean,
    onSelectDate: (LocalDate) -> Unit
) {
    // Generate dates: 3 days past + today + 10 days ahead (14 days total)
    val dates = remember(today) {
        (-3..10).map { today.plusDays(it.toLong()) }
    }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        listState.scrollToItem(2)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = if (isDark) Color(0xFF1E293B) else Color.White,
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else NeutralBorderLight),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            // Header: Month & Year
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val monthFormatter = remember(isArabic) {
                    DateTimeFormatter.ofPattern("MMMM yyyy", if (isArabic) Locale("ar") else Locale.ENGLISH)
                }
                Text(
                    text = selectedDate.format(monthFormatter),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFF1F5F9) else SoftNavyText,
                        fontSize = 13.sp
                    )
                )

                if (selectedDate != today) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDark) Color(0xFF334155) else SoftNavyPillBg,
                        modifier = Modifier.clickable { onSelectDate(today) }
                    ) {
                        Text(
                            text = if (isArabic) "اليوم ↩" else "Today ↩",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTeal,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Day Horizon Scroller
            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(dates) { date ->
                    val isSelected = date == selectedDate
                    val isDateToday = date == today

                    val dayOfWeek = date.dayOfWeek
                    val bitIndex = when (dayOfWeek) {
                        DayOfWeek.MONDAY -> 0
                        DayOfWeek.TUESDAY -> 1
                        DayOfWeek.WEDNESDAY -> 2
                        DayOfWeek.THURSDAY -> 3
                        DayOfWeek.FRIDAY -> 4
                        DayOfWeek.SATURDAY -> 5
                        DayOfWeek.SUNDAY -> 6
                    }
                    val dateIso = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val hasPinnedForDay = habits.any { habit ->
                        habit.pinnedToPlanner && (
                                (!habit.targetDateIso.isNullOrBlank() && habit.targetDateIso == dateIso) ||
                                        (habit.targetDateIso.isNullOrBlank() && (habit.pinnedDaysMask and (1 shl bitIndex)) != 0)
                                )
                    }

                    val dayInitial = if (isArabic) {
                        when (dayOfWeek) {
                            DayOfWeek.SATURDAY -> "سبت"
                            DayOfWeek.SUNDAY -> "أحد"
                            DayOfWeek.MONDAY -> "إثن"
                            DayOfWeek.TUESDAY -> "ثلا"
                            DayOfWeek.WEDNESDAY -> "أرب"
                            DayOfWeek.THURSDAY -> "خمي"
                            DayOfWeek.FRIDAY -> "جمع"
                        }
                    } else {
                        dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase()
                    }

                    Box(
                        modifier = Modifier
                            .width(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    isSelected -> PrimaryTeal
                                    isDateToday -> if (isDark) Color(0xFF334155) else TealTintBg
                                    else -> if (isDark) Color(0xFF0F172A) else SoftNavyPillBg.copy(alpha = 0.6f)
                                }
                            )
                            .border(
                                width = if (isDateToday && !isSelected) 1.5.dp else 0.dp,
                                color = if (isDateToday && !isSelected) PrimaryTeal else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectDate(date) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = dayInitial,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else (if (isDateToday) PrimaryTeal else (if (isDark) Color(0xFF94A3B8) else Color(0xFF5F5E5A)))
                                )
                            )
                            Text(
                                text = date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color.White else (if (isDateToday) PrimaryTeal else (if (isDark) Color(0xFFF1F5F9) else SoftNavyText))
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
private fun PlannerAgendaBanner(
    selectedDate: LocalDate,
    isToday: Boolean,
    pinnedCount: Int,
    completedPinnedCount: Int,
    pinnedProgress: Float,
    isDark: Boolean,
    isArabic: Boolean,
    onJumpToToday: () -> Unit
) {
    val dateFormatter = remember(isArabic) {
        DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", if (isArabic) Locale("ar") else Locale.ENGLISH)
    }

    BentoCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isToday) TealTintBg else (if (isDark) Color(0xFF334155) else SoftNavyPillBg)
                        ) {
                            Text(
                                text = if (isToday) (if (isArabic) "اليوم الحاضر" else "Today's Agenda") else (if (isArabic) "خطة اليوم المحدد" else "Planner View"),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) PrimaryTeal else SoftNavyText,
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }

                    TitleSubtextSpacer()

                    Text(
                        text = selectedDate.format(dateFormatter),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1F1F1F),
                            fontSize = 14.5.sp
                        )
                    )
                }

                if (pinnedCount > 0) {
                    RadialProgressRing(
                        progress = pinnedProgress,
                        modifier = Modifier.size(54.dp),
                        strokeWidth = 6.dp
                    ) {
                        Text(
                            text = "$completedPinnedCount/$pinnedCount",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFF1F5F9) else SoftNavyText,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Text(
                text = if (pinnedCount > 0) {
                    if (pinnedProgress >= 1f) {
                        if (isArabic) "ما شاء الله! أنجزت جميع مهام الورد اليومية المخططة! 🌟" else "Masha'Allah! All scheduled routine tasks accomplished! 🌟"
                    } else {
                        if (isArabic) "لديك $pinnedCount مهام مثبتة لهذا اليوم ($completedPinnedCount منجزة)." else "You have $pinnedCount pinned tasks on your agenda ($completedPinnedCount completed)."
                    }
                } else {
                    if (isArabic) "لا توجد مهام مثبتة لهذا اليوم. انقر على أيقونة الدبوس 📌 لتثبيت مهامك." else "No tasks pinned for this day yet. Tap the 📌 icon on any routine to pin it."
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5F5E5A),
                    fontSize = 12.sp
                )
            )

            if (pinnedCount > 0) {
                LinearProgressIndicator(
                    progress = { pinnedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = PrimaryTeal,
                    trackColor = if (isDark) Color(0xFF334155) else TealTintBg
                )
            }
        }
    }
}

@Composable
private fun HabitItemCard(
    habit: DailyHabitEntity,
    isDark: Boolean,
    isArabic: Boolean,
    isPinnedView: Boolean,
    onIncrement: () -> Unit,
    onMarkDone: () -> Unit,
    onTogglePin: () -> Unit,
    onEditSchedule: () -> Unit,
    onDelete: () -> Unit
) {
    val habitFraction = if (habit.targetCount > 0) (habit.currentCount.toFloat() / habit.targetCount.toFloat()).coerceIn(0f, 1f) else 0f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onIncrement() },
        color = if (isDark) Color(0xFF1E293B) else Color.White,
        border = BorderStroke(
            1.dp,
            if (habit.pinnedToPlanner && isPinnedView) GoldBadgeText.copy(alpha = 0.5f)
            else if (isDark) Color(0xFF334155) else NeutralBorderLight
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (habit.isCompleted) PrimaryTeal
                                else if (isDark) Color(0xFF334155)
                                else TealTintBg
                            )
                            .clickable {
                                if (habit.isCompleted) onIncrement() else onMarkDone()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (habit.isCompleted) Icons.Default.Check else Icons.Default.Star,
                            contentDescription = "Status",
                            tint = if (habit.isCompleted) Color.White else PrimaryTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = habit.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1F1F1F),
                                fontSize = 13.5.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isDark) Color(0xFF334155) else SoftNavyPillBg
                            ) {
                                Text(
                                    text = habit.category,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDark) Color(0xFF94A3B8) else SoftNavyText
                                    )
                                )
                            }

                            if (habit.isAlarmEnabled && habit.scheduledTimeMinutes != null) {
                                val hour = habit.scheduledTimeMinutes / 60
                                val minute = habit.scheduledTimeMinutes % 60
                                val amPm = if (hour >= 12) "PM" else "AM"
                                val displayHour = if (hour % 12 == 0) 12 else hour % 12
                                val timeStr = String.format(Locale.ENGLISH, "%02d:%02d %s", displayHour, minute, amPm)

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isDark) PrimaryTeal.copy(alpha = 0.2f) else TealTintBg,
                                    modifier = Modifier.clickable { onEditSchedule() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AlarmOn,
                                            contentDescription = "Alarm Set",
                                            tint = PrimaryTeal,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Text(
                                            text = timeStr,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryTeal
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${habit.currentCount}/${habit.targetCount}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTeal,
                            fontSize = 13.sp
                        )
                    )

                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (habit.pinnedToPlanner) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (habit.pinnedToPlanner) "Unpin" else "Pin",
                            tint = if (habit.pinnedToPlanner) GoldBadgeText else (if (isDark) Color(0xFF64748B) else Color(0xFF9E9E9E)),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onEditSchedule,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (habit.isAlarmEnabled) Icons.Default.Alarm else Icons.Outlined.Alarm,
                            contentDescription = "Adjust Alarm & Planner",
                            tint = if (habit.isAlarmEnabled) PrimaryTeal else (if (isDark) Color(0xFF64748B) else Color(0xFF9E9E9E)),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { habitFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryTeal,
                trackColor = if (isDark) Color(0xFF334155) else SoftNavyPillBg
            )
        }
    }
}

@Composable
fun SwipeableHabitRow(
    habit: DailyHabitEntity,
    onMarkAsDone: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val swipeOffsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Transparent)
    ) {
        val offset = swipeOffsetX.value
        if (offset != 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = if (offset > 0) PrimaryTeal else Color(0xFFE53935),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = if (offset > 0) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (offset > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Mark as Done",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Delete",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(offset.toInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val targetOffset = swipeOffsetX.value
                                if (targetOffset > 150f) {
                                    onMarkAsDone()
                                    swipeOffsetX.animateTo(0f)
                                } else if (targetOffset < -150f) {
                                    onDelete()
                                    swipeOffsetX.animateTo(0f)
                                } else {
                                    swipeOffsetX.animateTo(0f)
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                val newOffset = (swipeOffsetX.value + dragAmount).coerceIn(-300f, 300f)
                                swipeOffsetX.snapTo(newOffset)
                            }
                        }
                    )
                }
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

// -----------------------------------------------------------------------------------------
// Dialog: Add Habit with Planner & Alarm Integration
// -----------------------------------------------------------------------------------------

@Composable
private fun AddHabitPlannerDialog(
    isDark: Boolean,
    isArabic: Boolean,
    initialTargetDateIso: String? = null,
    initialHour: Int? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        target: Int,
        category: String,
        isPinned: Boolean,
        daysMask: Int,
        timeMinutes: Int?,
        isAlarm: Boolean,
        targetDateIso: String?,
        notes: String?
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetStr by remember { mutableStateOf("1") }
    var category by remember { mutableStateOf("Quran") }
    var isPinned by remember { mutableStateOf(true) }
    var isAlarm by remember { mutableStateOf(initialHour != null) }

    var selectedHour by remember { mutableIntStateOf(initialHour ?: 7) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var daysMask by remember { mutableIntStateOf(127) }
    var targetDateIso by remember { mutableStateOf(initialTargetDateIso) }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Quran", "Dhikr", "Salah", "Sunnah", "Charity", "Knowledge")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryTeal)
                Text(
                    text = if (isArabic) "إضافة ورد أو عادة للتقويم" else "Schedule New Routine",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) Color.White else Color(0xFF1F1F1F)
                )
            }
        },
        containerColor = if (isDark) Color(0xFF1E293B) else Color.White,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isArabic) "اسم العبادة أو الورد" else "Routine Title") },
                    placeholder = { Text(if (isArabic) "مثال: قراءة سورة الملك قبل النوم" else "e.g. Read Surah Al-Mulk before sleep") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = if (isDark) Color(0xFF334155) else NeutralBorderLight,
                        focusedContainerColor = if (isDark) Color(0xFF0F172A) else Color.White,
                        unfocusedContainerColor = if (isDark) Color(0xFF0F172A) else Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (isArabic) "التصنيف" else "Category",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF94A3B8) else SoftNavyText)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val isSelected = category == cat
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) PrimaryTeal else (if (isDark) Color(0xFF334155) else SoftNavyPillBg),
                                modifier = Modifier.clickable { category = cat }
                            ) {
                                Text(
                                    text = cat,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else (if (isDark) Color(0xFFCBD5E1) else SoftNavyText),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    label = { Text(if (isArabic) "الهدف اليومي (العدد أو التكرار)" else "Target Count / Reps") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = if (isDark) Color(0xFF334155) else NeutralBorderLight,
                        focusedContainerColor = if (isDark) Color(0xFF0F172A) else Color.White,
                        unfocusedContainerColor = if (isDark) Color(0xFF0F172A) else Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDark) Color(0xFF0F172A) else SoftNavyPillBg.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PushPin, contentDescription = null, tint = GoldBadgeText, modifier = Modifier.size(18.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "تثبيت في الأجندة اليومية" else "Pin to Daily Planner",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.5.sp),
                                    color = if (isDark) Color.White else SoftNavyText
                                )
                                Text(
                                    text = if (isArabic) "يظهر في واجهة التقويم لليوم" else "Highlighted on the calendar strip",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp, color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5F5E5A))
                                )
                            }
                        }
                        Switch(
                            checked = isPinned,
                            onCheckedChange = { isPinned = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryTeal
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDark) Color(0xFF0F172A) else TealTintBg.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
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
                                Icon(imageVector = Icons.Default.Alarm, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "منبه وتوقيت محدد" else "Routine Time & Alarm",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.5.sp),
                                        color = if (isDark) Color.White else SoftNavyText
                                    )
                                    Text(
                                        text = if (isArabic) "يظهر في شبكة الساعات للتقويم" else "Positions on Google Calendar grid",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp, color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5F5E5A))
                                    )
                                }
                            }
                            Switch(
                                checked = isAlarm,
                                onCheckedChange = { isAlarm = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryTeal
                                )
                            )
                        }

                        if (isAlarm) {
                            Text(
                                text = if (isArabic) "أوقات مقترحة سريعة:" else "Spiritual Time Presets:",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, color = PrimaryTeal, fontWeight = FontWeight.Bold)
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                item {
                                    TimePresetChip("🌅 05:30 (Fajr)", 5, 30, selectedHour, selectedMinute) { h, m -> selectedHour = h; selectedMinute = m }
                                }
                                item {
                                    TimePresetChip("☀️ 07:00 (Adhkar)", 7, 0, selectedHour, selectedMinute) { h, m -> selectedHour = h; selectedMinute = m }
                                }
                                item {
                                    TimePresetChip("✨ 10:00 (Duha)", 10, 0, selectedHour, selectedMinute) { h, m -> selectedHour = h; selectedMinute = m }
                                }
                                item {
                                    TimePresetChip("📖 16:30 (Asr)", 16, 30, selectedHour, selectedMinute) { h, m -> selectedHour = h; selectedMinute = m }
                                }
                                item {
                                    TimePresetChip("🌙 22:00 (Witr)", 22, 0, selectedHour, selectedMinute) { h, m -> selectedHour = h; selectedMinute = m }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val amPm = if (selectedHour >= 12) "PM" else "AM"
                                val displayHour = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                                val formatted = String.format(Locale.ENGLISH, "%02d:%02d %s", displayHour, selectedMinute, amPm)

                                Text(
                                    text = if (isArabic) "الوقت المحدد: $formatted" else "Time: $formatted",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PrimaryTeal)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isDark) Color(0xFF334155) else Color.White,
                                        modifier = Modifier.clickable {
                                            selectedHour = (selectedHour + 1) % 24
                                        }
                                    ) {
                                        Text("+1 Hr", modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isDark) Color(0xFF334155) else Color.White,
                                        modifier = Modifier.clickable {
                                            selectedMinute = (selectedMinute + 15) % 60
                                        }
                                    ) {
                                        Text("+15m", modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetStr.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    val timeMinutes = if (isAlarm) (selectedHour * 60 + selectedMinute) else null
                    if (title.isNotBlank()) {
                        onConfirm(
                            title.trim(),
                            target,
                            category,
                            isPinned,
                            daysMask,
                            timeMinutes,
                            isAlarm,
                            targetDateIso,
                            notes.ifBlank { null }
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                Text(if (isArabic) "إضافة للتقويم" else "Schedule Routine", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isArabic) "إلغاء" else "Cancel", color = if (isDark) Color(0xFF94A3B8) else SoftNavyText)
            }
        }
    )
}

// -----------------------------------------------------------------------------------------
// Dialog: Adjust Routine Planner Recurrence & Alarm
// -----------------------------------------------------------------------------------------

@Composable
private fun HabitScheduleAlarmDialog(
    habit: DailyHabitEntity,
    isDark: Boolean,
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        isPinned: Boolean,
        daysMask: Int,
        timeMinutes: Int?,
        isAlarm: Boolean,
        targetDateIso: String?,
        notes: String?
    ) -> Unit
) {
    var isPinned by remember { mutableStateOf(habit.pinnedToPlanner) }
    var isAlarm by remember { mutableStateOf(habit.isAlarmEnabled) }
    val initialMinutes = habit.scheduledTimeMinutes ?: (7 * 60)
    var selectedHour by remember { mutableIntStateOf(initialMinutes / 60) }
    var selectedMinute by remember { mutableIntStateOf(initialMinutes % 60) }
    var daysMask by remember { mutableIntStateOf(habit.pinnedDaysMask) }
    var targetDateIso by remember { mutableStateOf(habit.targetDateIso) }
    var notes by remember { mutableStateOf(habit.notes ?: "") }

    val daysOfWeekLabels = if (isArabic) {
        listOf("إثن", "ثلا", "أرب", "خمي", "جمع", "سبت", "أحد")
    } else {
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Alarm, contentDescription = null, tint = PrimaryTeal)
                Text(
                    text = if (isArabic) "ضبط الجدول والمنبه" else "Adjust Routine Schedule & Alarm",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) Color.White else Color(0xFF1F1F1F)
                )
            }
        },
        containerColor = if (isDark) Color(0xFF1E293B) else Color.White,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PrimaryTeal)
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDark) Color(0xFF0F172A) else SoftNavyPillBg.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PushPin, contentDescription = null, tint = GoldBadgeText, modifier = Modifier.size(18.dp))
                            Text(
                                text = if (isArabic) "تثبيت في الأجندة اليومية" else "Pin to Daily Planner",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.5.sp),
                                color = if (isDark) Color.White else SoftNavyText
                            )
                        }
                        Switch(
                            checked = isPinned,
                            onCheckedChange = { isPinned = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryTeal)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isArabic) "أيام التكرار الأسبوعي" else "Weekly Recurrence Days",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF94A3B8) else SoftNavyText)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysOfWeekLabels.forEachIndexed { index, label ->
                            val isDayActive = (daysMask and (1 shl index)) != 0
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isDayActive) PrimaryTeal
                                        else if (isDark) Color(0xFF334155)
                                        else SoftNavyPillBg
                                    )
                                    .clickable {
                                        daysMask = if (isDayActive) {
                                            daysMask and (1 shl index).inv()
                                        } else {
                                            daysMask or (1 shl index)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (isDayActive) Color.White else (if (isDark) Color(0xFFCBD5E1) else SoftNavyText)
                                    )
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDark) Color(0xFF334155) else SoftNavyPillBg,
                            modifier = Modifier.clickable { daysMask = 127 }
                        ) {
                            Text(if (isArabic) "يومياً" else "Daily", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDark) Color(0xFF334155) else SoftNavyPillBg,
                            modifier = Modifier.clickable { daysMask = (1 shl 0) or (1 shl 3) }
                        ) {
                            Text(if (isArabic) "إثنين وخميس" else "Mon & Thu", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDark) Color(0xFF334155) else SoftNavyPillBg,
                            modifier = Modifier.clickable { daysMask = (1 shl 4) }
                        ) {
                            Text(if (isArabic) "الجمعة فقط" else "Friday only", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDark) Color(0xFF0F172A) else TealTintBg.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
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
                                Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
                                Text(
                                    text = if (isArabic) "تفعيل المنبه اليومي" else "Enable Routine Alarm",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.5.sp),
                                    color = if (isDark) Color.White else SoftNavyText
                                )
                            }
                            Switch(
                                checked = isAlarm,
                                onCheckedChange = { isAlarm = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryTeal)
                            )
                        }

                        if (isAlarm) {
                            val amPm = if (selectedHour >= 12) "PM" else "AM"
                            val displayHour = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                            val formatted = String.format(Locale.ENGLISH, "%02d:%02d %s", displayHour, selectedMinute, amPm)

                            Text(
                                text = if (isArabic) "وقت التنبيه: $formatted" else "Alarm Time: $formatted",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PrimaryTeal)
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                item {
                                    TimePresetChip("🌅 05:30 (Fajr)", 5, 30, selectedHour, selectedMinute) { h, m -> selectedHour = h; selectedMinute = m }
                                }
                                item {
                                    TimePresetChip("☀️ 07:00 (Adhkar)", 7, 0, selectedHour, selectedMinute) { h, m -> selectedHour = h; selectedMinute = m }
                                }
                                item {
                                    TimePresetChip("✨ 10:00 (Duha)", 10, 0, selectedHour, selectedMinute) { h, m -> selectedHour = h; selectedMinute = m }
                                }
                                item {
                                    TimePresetChip("📖 16:30 (Asr)", 16, 30, selectedHour, selectedMinute) { h, m -> selectedHour = h; selectedMinute = m }
                                }
                                item {
                                    TimePresetChip("🌙 22:00 (Witr)", 22, 0, selectedHour, selectedMinute) { h, m -> selectedHour = h; selectedMinute = m }
                                }
                            }

                            Column {
                                Text(
                                    text = "Hour: $displayHour $amPm (${selectedHour}:00)",
                                    style = MaterialTheme.typography.labelSmall.copy(color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5F5E5A))
                                )
                                Slider(
                                    value = selectedHour.toFloat(),
                                    onValueChange = { selectedHour = it.toInt() },
                                    valueRange = 0f..23f,
                                    steps = 22,
                                    colors = SliderDefaults.colors(thumbColor = PrimaryTeal, activeTrackColor = PrimaryTeal)
                                )
                            }

                            Column {
                                Text(
                                    text = "Minute: ${String.format(Locale.ENGLISH, "%02d", selectedMinute)}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = if (isDark) Color(0xFF94A3B8) else Color(0xFF5F5E5A))
                                )
                                Slider(
                                    value = selectedMinute.toFloat(),
                                    onValueChange = { selectedMinute = it.toInt() },
                                    valueRange = 0f..55f,
                                    steps = 10,
                                    colors = SliderDefaults.colors(thumbColor = PrimaryTeal, activeTrackColor = PrimaryTeal)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val timeMinutes = if (isAlarm) (selectedHour * 60 + selectedMinute) else null
                    onSave(
                        isPinned,
                        daysMask,
                        timeMinutes,
                        isAlarm,
                        targetDateIso,
                        notes.ifBlank { null }
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                Text(if (isArabic) "حفظ التعديلات" else "Save Schedule", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isArabic) "إلغاء" else "Cancel", color = if (isDark) Color(0xFF94A3B8) else SoftNavyText)
            }
        }
    )
}

@Composable
private fun TimePresetChip(
    label: String,
    hour: Int,
    minute: Int,
    currentHour: Int,
    currentMinute: Int,
    onSelect: (Int, Int) -> Unit
) {
    val isSelected = currentHour == hour && currentMinute == minute
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) PrimaryTeal else Color.White,
        border = BorderStroke(1.dp, if (isSelected) PrimaryTeal else NeutralBorderLight),
        modifier = Modifier.clickable { onSelect(hour, minute) }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else SoftNavyText,
                fontSize = 10.sp
            )
        )
    }
}
