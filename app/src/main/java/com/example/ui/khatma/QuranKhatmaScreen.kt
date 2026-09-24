package com.example.ui.khatma

import com.example.ui.components.KeepScreenOn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.KhatmaHistoryEntity
import com.example.data.local.KhatmaPlanEntity
import com.example.data.quran.KhatmaDayItem
import com.example.data.quran.KhatmaEngine
import com.example.data.quran.KhatmaFullDashboardState
import com.example.data.quran.KhatmaPaceStatus
import com.example.data.quran.KhatmaSessionInfo
import com.example.ui.MainViewModel
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.ReadingThemes
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BorderDividerDark
import com.example.ui.theme.BorderDividerLight
import com.example.ui.theme.DangerRedDark
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.GoldTintBgDark
import com.example.ui.theme.GoldTintBgLight
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.NoorTopBarGradient
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealLight
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.NestedGoldCardLight
import com.example.ui.theme.NestedGoldCardDark
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
import kotlin.math.roundToInt

data class KhatmaThemeColors(
    val bg: Color,
    val card: Color,
    val elevated: Color,
    val border: Color,
    val borderLight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val accent: Color,
    val accentSoft: Color,
    val teal: Color,
    val tealSoft: Color,
    val gold: Color,
    val goldBg: Color,
    val isDark: Boolean
)

fun getKhatmaColors(themeColors: ReadingThemeColors): KhatmaThemeColors {
    val isDark = themeColors.isDark
    return if (isDark) {
        KhatmaThemeColors(
            bg = themeColors.background,
            card = SurfaceDark,
            elevated = SurfaceElevatedDark,
            border = Color.Transparent,
            borderLight = Color.Transparent,
            textPrimary = TextPrimaryDark,
            textSecondary = TextSecondaryDark,
            textMuted = TextSecondaryDark.copy(alpha = 0.7f),
            accent = SecondaryGoldDark,
            accentSoft = GoldTintBgDark,
            teal = PrimaryTealDark,
            tealSoft = PrimaryTealDark.copy(alpha = 0.15f),
            gold = SecondaryGoldDark,
            goldBg = GoldTintBgDark,
            isDark = true
        )
    } else {
        KhatmaThemeColors(
            bg = themeColors.background,
            card = SurfaceWhite,
            elevated = SurfaceElevatedLight,
            border = Color.Transparent,
            borderLight = Color.Transparent,
            textPrimary = TextPrimaryLight,
            textSecondary = TextSecondaryLight,
            textMuted = TextSecondaryLight.copy(alpha = 0.7f),
            accent = SecondaryGoldLight,
            accentSoft = GoldTintBgLight,
            teal = PrimaryTealLight,
            tealSoft = SoftTealTint,
            gold = SecondaryGoldLight,
            goldBg = GoldBadgeBg,
            isDark = false
        )
    }
}

val KhatmaGreenCardGradient = SolidColor(SecondaryGoldLight)

val KhatmaGreenButtonGradient = SolidColor(SecondaryGoldLight)

val KhatmaLightColors = getKhatmaColors(ReadingThemes.MadaniCrisp)
val KhatmaDarkColors = getKhatmaColors(ReadingThemes.ObsidianNight)

val LocalKhatmaColors = staticCompositionLocalOf {
    KhatmaLightColors
}

@Composable
fun KhatmaGradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    gradient: Brush = KhatmaGreenButtonGradient,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 13.dp),
    content: @Composable RowScope.() -> Unit
) {
    val isDark = LocalKhatmaColors.current.isDark
    Surface(
        modifier = modifier
            .clip(shape)
            .clickable(enabled = enabled, onClick = onClick),
        shape = shape,
        color = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(KhatmaDarkTeal)
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = content
            )
        }
    }
}

// Dynamic properties that automatically resolve according to active Light/Dark theme:
private val KhatmaDarkBg: Color @Composable get() = LocalKhatmaColors.current.bg
private val KhatmaDarkCard: Color @Composable get() = LocalKhatmaColors.current.card
private val KhatmaDarkElevated: Color @Composable get() = LocalKhatmaColors.current.elevated
private val KhatmaDarkBorder: Color @Composable get() = LocalKhatmaColors.current.border
private val KhatmaDarkBorderLight: Color @Composable get() = LocalKhatmaColors.current.borderLight
private val KhatmaDarkTextPrimary: Color @Composable get() = LocalKhatmaColors.current.textPrimary
private val KhatmaDarkTextSecondary: Color @Composable get() = LocalKhatmaColors.current.textSecondary
private val KhatmaDarkTextMuted: Color @Composable get() = LocalKhatmaColors.current.textMuted
private val KhatmaDarkAccent: Color @Composable get() = LocalKhatmaColors.current.accent
private val KhatmaDarkAccentSoft: Color @Composable get() = LocalKhatmaColors.current.accentSoft
private val KhatmaDarkTeal: Color @Composable get() = LocalKhatmaColors.current.teal
private val KhatmaDarkTealSoft: Color @Composable get() = LocalKhatmaColors.current.tealSoft
private val KhatmaDarkGold: Color @Composable get() = LocalKhatmaColors.current.gold
private val KhatmaDarkGoldBg: Color @Composable get() = LocalKhatmaColors.current.goldBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranKhatmaScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    KeepScreenOn()
    com.example.ui.components.DndReadingEffect(viewModel)

    val dashboardState by viewModel.khatmaDashboardState.collectAsStateWithLifecycle()
    val isSetupOpen by viewModel.isKhatmaSetupSheetOpen.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isKhatmaSettingsSheetOpen.collectAsStateWithLifecycle()
    val isHistoryOpen by viewModel.isKhatmaHistorySheetOpen.collectAsStateWithLifecycle()
    val isCompletionOpen by viewModel.isKhatmaCompletionCelebrationOpen.collectAsStateWithLifecycle()
    val isPaceAdjustOpen by viewModel.isKhatmaPaceAdjustSheetOpen.collectAsStateWithLifecycle()
    val historyList by viewModel.khatmaHistory.collectAsStateWithLifecycle()
    val isSystemDark by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val readingThemeName by viewModel.sharedReadingTheme.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = remember(appLanguage) {
        appLanguage.equals("Arabic", ignoreCase = true) ||
        appLanguage.equals("العربية", ignoreCase = true) ||
        appLanguage.startsWith("ar", ignoreCase = true)
    }

    val readingThemeColors = remember(readingThemeName, isSystemDark) {
        if (isSystemDark) ReadingThemes.ObsidianNight else ReadingThemes.getThemeByName(readingThemeName)
    }
    val khatmaColors = remember(readingThemeColors) { getKhatmaColors(readingThemeColors) }

    var showQuickAddDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalKhatmaColors provides khatmaColors) {
        Scaffold(
            topBar = {
                NoorTopBar(
                    title = if (isArabic) "ختمة القرآن" else "Quran Khatma",
                    eyebrow = if (isArabic) "ختمة القرآن" else null,
                    subtitle = if (isArabic) "مخطط الختم ومتابعة الإنجاز" else "Completion Planner & Progress",
                    onBackClick = onNavigateBack,
                    backContentDescription = "Back",
                    isDark = isSystemDark,
                    themeColors = readingThemeColors,
                    actions = {
                        NoorGlassIconButton(
                            onClick = { viewModel.isKhatmaHistorySheetOpen.value = true },
                            icon = Icons.Default.History,
                            contentDescription = "Khatma History"
                        )
                        if (dashboardState != null) {
                            NoorGlassIconButton(
                                onClick = { viewModel.isKhatmaSettingsSheetOpen.value = true },
                                icon = Icons.Default.Settings,
                                contentDescription = "Khatma Settings"
                            )
                        }
                    }
                )
            },
            containerColor = KhatmaDarkBg
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val state = dashboardState
                if (state == null || state.plan.isCompleted) {
                    // Empty or completed state -> Onboarding Setup
                    KhatmaSetupView(
                        viewModel = viewModel,
                        isExistingKhatmaCompleted = state?.plan?.isCompleted == true,
                        onOpenHistory = { viewModel.isKhatmaHistorySheetOpen.value = true }
                    )
                } else {
                    // Active Khatma Dashboard
                    KhatmaDashboardContent(
                        state = state,
                        viewModel = viewModel,
                        onOpenSettings = { viewModel.isKhatmaSettingsSheetOpen.value = true },
                        onOpenPaceAdjust = { viewModel.isKhatmaPaceAdjustSheetOpen.value = true },
                        onQuickAdd = { showQuickAddDialog = true }
                    )
                }
            }
        }

        // Modal Sheets & Dialogs
        if (isSetupOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.isKhatmaSetupSheetOpen.value = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = KhatmaDarkCard
            ) {
                KhatmaSetupSheetContent(
                    viewModel = viewModel,
                    onDismiss = { viewModel.isKhatmaSetupSheetOpen.value = false }
                )
            }
        }

        if (isSettingsOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.isKhatmaSettingsSheetOpen.value = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = KhatmaDarkCard
            ) {
                KhatmaSettingsSheetContent(
                    viewModel = viewModel,
                    state = dashboardState,
                    onDismiss = { viewModel.isKhatmaSettingsSheetOpen.value = false },
                    onOpenPaceAdjust = {
                        viewModel.isKhatmaSettingsSheetOpen.value = false
                        viewModel.isKhatmaPaceAdjustSheetOpen.value = true
                    }
                )
            }
        }

        if (isPaceAdjustOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.isKhatmaPaceAdjustSheetOpen.value = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = KhatmaDarkCard
            ) {
                KhatmaPaceAdjustmentSheetContent(
                    viewModel = viewModel,
                    state = dashboardState,
                    onDismiss = { viewModel.isKhatmaPaceAdjustSheetOpen.value = false }
                )
            }
        }

        if (isHistoryOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.isKhatmaHistorySheetOpen.value = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = KhatmaDarkCard
            ) {
                KhatmaHistorySheetContent(
                    historyList = historyList,
                    onDismiss = { viewModel.isKhatmaHistorySheetOpen.value = false }
                )
            }
        }

        if (isCompletionOpen) {
            KhatmaCompletionCelebrationDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.isKhatmaCompletionCelebrationOpen.value = false }
            )
        }

        if (showQuickAddDialog) {
            QuickLogAyahsDialog(
                onDismiss = { showQuickAddDialog = false },
                onAdd = { count ->
                    viewModel.advanceKhatmaByAyahs(count)
                    showQuickAddDialog = false
                }
            )
        }
    }
}

/**
 * Clean Khatma Setup / Onboarding View
 */
private fun formatKhatmaReminderDisplay(rawTime: String): String {
    val parts = rawTime.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 20
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 30
    val ampm = if (hour >= 12) "PM" else "AM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format(Locale.getDefault(), "%d:%02d %s (%02d:%02d)", displayHour, minute, ampm, hour, minute)
}

/**
 * Pure Compose Material 3 Time Picker Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhatmaTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    val isDark = LocalKhatmaColors.current.isDark
    val goldColor = if (isDark) SecondaryGoldDark else SecondaryGoldLight
    val goldSoft = if (isDark) GoldTintBgDark else GoldTintBgLight

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(24.dp),
            color = if (isDark) SurfaceDark else SurfaceWhite,
            border = null,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Daily Reminder Time",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                        fontSize = 17.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = if (isDark) SurfaceElevatedDark else SurfaceElevatedLight,
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = if (isDark) TextPrimaryDark else TextPrimaryLight,
                        selectorColor = goldColor,
                        periodSelectorBorderColor = goldColor,
                        periodSelectorSelectedContainerColor = goldSoft,
                        periodSelectorUnselectedContainerColor = Color.Transparent,
                        periodSelectorSelectedContentColor = goldColor,
                        periodSelectorUnselectedContentColor = if (isDark) TextSecondaryDark else TextSecondaryLight,
                        timeSelectorSelectedContainerColor = goldSoft,
                        timeSelectorUnselectedContainerColor = if (isDark) SurfaceElevatedDark else SurfaceElevatedLight,
                        timeSelectorSelectedContentColor = goldColor,
                        timeSelectorUnselectedContentColor = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            "Cancel",
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(timePickerState.hour, timePickerState.minute)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = goldColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Confirm",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KhatmaSetupView(
    viewModel: MainViewModel,
    isExistingKhatmaCompleted: Boolean = false,
    onOpenHistory: () -> Unit = {}
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = remember(appLanguage) {
        appLanguage.equals("Arabic", ignoreCase = true) ||
        appLanguage.equals("العربية", ignoreCase = true) ||
        appLanguage.startsWith("ar", ignoreCase = true)
    }

    var selectedDays by remember { mutableIntStateOf(30) }
    var isCustomSelected by remember { mutableStateOf(false) }
    var customDays by remember { mutableFloatStateOf(30f) }
    var selectedSessions by remember { mutableIntStateOf(1) }
    var reminderEnabled by remember { mutableStateOf(true) }
    var reminderTime by remember { mutableStateOf("20:30") }
    var planTitle by remember { mutableStateOf(if (isArabic) "ختمة القرآن" else "Quran Khatma") }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    if (showTimePickerDialog) {
        val initialHour = remember(reminderTime) { reminderTime.split(":").getOrNull(0)?.toIntOrNull() ?: 20 }
        val initialMinute = remember(reminderTime) { reminderTime.split(":").getOrNull(1)?.toIntOrNull() ?: 30 }
        KhatmaTimePickerDialog(
            initialHour = initialHour,
            initialMinute = initialMinute,
            onDismiss = { showTimePickerDialog = false },
            onConfirm = { hour, minute ->
                reminderTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                showTimePickerDialog = false
            }
        )
    }

    val effectiveDays = if (isCustomSelected) customDays.roundToInt() else selectedDays
    val dailyTargetAyahs = (KhatmaEngine.TOTAL_QURAN_AYAHS.toDouble() / effectiveDays.toDouble()).roundToInt()
    val estCompletionDate = remember(effectiveDays) {
        LocalDate.now().plusDays(effectiveDays.toLong()).format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            // Daily Completion / Khatma Progress Card with Circular Progress Indicator (matching AzkarDailyCompletionCard style)
            val isDark = LocalKhatmaColors.current.isDark
            val setupProgress = (effectiveDays.toFloat() / 30f).coerceIn(0f, 1f)
            val progressPercent = (setupProgress * 100).roundToInt()

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = KhatmaDarkCard,
                shadowElevation = if (isDark) 0.dp else 0.4.dp,
                border = null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column (Badge, Title, Subtitle, Details)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = KhatmaDarkGoldBg,
                            border = null
                        ) {
                            Text(
                                text = if (isArabic) "خطة الختمة" else "KHATMA PLANNER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = KhatmaDarkGold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.6.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                            )
                        }

                        Text(
                            text = if (isArabic) {
                                if (isExistingKhatmaCompleted) "ابدأ ختمة جديدة" else "ابدأ ختمة القرآن"
                            } else {
                                if (isExistingKhatmaCompleted) "Start a Fresh Khatma" else "Begin Your Quran Khatma"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = KhatmaDarkTextPrimary,
                                fontSize = 17.sp
                            )
                        )

                        Text(
                            text = if (isArabic) {
                                "ضع خطة قراءة هادئة وتابع تقدمك آية بآية."
                            } else {
                                "Set a peaceful reading plan. Track progress verse by verse."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = KhatmaDarkTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(KhatmaDarkGold)
                            )
                            Text(
                                text = if (isArabic) {
                                    "$effectiveDays يوم • ~$dailyTargetAyahs آية/يوم"
                                } else {
                                    "$effectiveDays Days Goal • ~$dailyTargetAyahs ayahs/day"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = KhatmaDarkTextPrimary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    // Right Side: Circular Progress Indicator
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(72.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(72.dp),
                            color = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                            strokeWidth = 6.dp,
                            strokeCap = StrokeCap.Round
                        )

                        CircularProgressIndicator(
                            progress = { setupProgress },
                            modifier = Modifier.size(72.dp),
                            color = KhatmaDarkGold,
                            strokeWidth = 6.dp,
                            strokeCap = StrokeCap.Round
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$progressPercent%",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = KhatmaDarkGold,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = if (isArabic) "الهدف" else "Pace",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = KhatmaDarkTextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            // Duration Presets Section wrapped in dedicated Card
            val isDark = LocalKhatmaColors.current.isDark
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = KhatmaDarkCard,
                shadowElevation = if (isDark) 0.dp else 0.4.dp,
                border = null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isArabic) "اختر مدة الختمة" else "Choose Completion Goal",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = KhatmaDarkTextPrimary,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val presets = listOf(7, 15, 30, 45, 60, 90)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(presets) { days ->
                            val isSelected = !isCustomSelected && selectedDays == days

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) KhatmaDarkGoldBg else (if (isDark) NestedGoldCardDark else NestedGoldCardLight),
                                shadowElevation = 0.dp,
                                border = null,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        isCustomSelected = false
                                        selectedDays = days
                                    }
                                    .testTag("khatma_preset_${days}_days")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$days Days",
                                         style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                            color = if (isSelected) KhatmaDarkGold else KhatmaDarkTextPrimary,
                                            fontSize = 13.5.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(com.example.ui.theme.NoorSpacing.TitleSubtextSpacingTight))
                                    Text(
                                        text = when (days) {
                                            7 -> "Intensive"
                                            15 -> "1/2 Month"
                                            30 -> "1 Juz / Day"
                                            45 -> "Steady Pace"
                                            60 -> "1 Hizb / Day"
                                            else -> "Gentle Journey"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) KhatmaDarkGold.copy(alpha = 0.85f) else KhatmaDarkTextMuted
                                        )
                                    )
                                }
                            }
                        }

                        item {
                            val isSelected = isCustomSelected

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) KhatmaDarkGoldBg else (if (isDark) NestedGoldCardDark else NestedGoldCardLight),
                                shadowElevation = 0.dp,
                                border = null,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { isCustomSelected = true }
                                    .testTag("khatma_preset_custom_days")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Custom",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                            color = if (isSelected) KhatmaDarkGold else KhatmaDarkTextPrimary,
                                            fontSize = 13.5.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(com.example.ui.theme.NoorSpacing.TitleSubtextSpacingTight))
                                    Text(
                                        text = "${customDays.roundToInt()} Days",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) KhatmaDarkGold.copy(alpha = 0.85f) else KhatmaDarkTextMuted
                                        )
                                    )
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = isCustomSelected,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Custom Duration",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = KhatmaDarkTextPrimary
                                    )
                                )
                                Text(
                                    text = "${customDays.roundToInt()} Days",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = KhatmaDarkGold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                            Slider(
                                value = customDays,
                                onValueChange = { customDays = it },
                                valueRange = 5f..180f,
                                steps = 34,
                                colors = SliderDefaults.colors(
                                    thumbColor = KhatmaDarkGold,
                                    activeTrackColor = KhatmaDarkGold,
                                    inactiveTrackColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.testTag("khatma_custom_slider")
                            )
                        }
                    }
                }
            }
        }

        item {
            // Daily Split Sessions Selector wrapped in dedicated Card
            val isDark = LocalKhatmaColors.current.isDark
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = KhatmaDarkCard,
                shadowElevation = if (isDark) 0.dp else 0.4.dp,
                border = null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isArabic) "جلسات القراءة اليومية" else "Daily Reading Sessions",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = KhatmaDarkTextPrimary,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isArabic) "قسّم وردك اليومي على عدة جلسات مريحة" else "Split your daily target into bite-sized reflection sessions",
                        style = MaterialTheme.typography.bodySmall.copy(color = KhatmaDarkTextSecondary)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val sessionOptions = listOf(
                        Pair(1, if (isArabic) "جلسة واحدة (يومياً)" else "1 Session (Daily)"),
                        Pair(2, if (isArabic) "جلستان (صباحاً ومساءً)" else "2 Sessions (Morning / Evening)"),
                        Pair(3, if (isArabic) "٣ جلسات (فجراً وعصراً ومساءً)" else "3 Sessions (Morning / Afternoon / Night)"),
                        Pair(5, if (isArabic) "٥ جلسات (دبر كل صلاة)" else "5 Sessions (After Each Prayer)")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        sessionOptions.forEach { (count, label) ->
                            val isSelected = selectedSessions == count
                            Surface(
                                onClick = { selectedSessions = count },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) KhatmaDarkGoldBg else (if (isDark) SurfaceElevatedDark else Color(0xFFF8FAFC)),
                                shadowElevation = 0.dp,
                                border = null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (isSelected) KhatmaDarkGold else KhatmaDarkTextPrimary,
                                            fontSize = 13.5.sp
                                        )
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = KhatmaDarkGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            // Daily Reminder Switch & Time Setting in White Card
            val isDark = LocalKhatmaColors.current.isDark

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = KhatmaDarkCard,
                shadowElevation = if (isDark) 0.dp else 0.4.dp,
                border = null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(KhatmaDarkTealSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (reminderEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = "Reminder",
                                    tint = KhatmaDarkTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isArabic) "التذكير اليومي" else "Daily Reminder",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = KhatmaDarkTextPrimary,
                                        fontSize = 14.5.sp
                                    )
                                )
                                Text(
                                    text = if (reminderEnabled) {
                                        if (isArabic) "التنبيه عند $reminderTime" else "Notify at $reminderTime"
                                    } else {
                                        if (isArabic) "معطل" else "Disabled"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = KhatmaDarkTextSecondary
                                    )
                                )
                            }
                        }
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = KhatmaDarkTeal,
                                uncheckedThumbColor = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                uncheckedTrackColor = if (isDark) BorderDividerDark else BorderDividerLight
                            )
                        )
                    }

                    if (reminderEnabled) {
                        // Interactive Time Picker Button
                        Surface(
                            onClick = { showTimePickerDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isDark) SurfaceElevatedDark else Color(0xFFF8FAFC),
                            border = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("khatma_setup_pick_time_btn")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Pick Time",
                                        tint = KhatmaDarkTeal,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (isArabic) "وقت التنبيه" else "Notification Time",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = KhatmaDarkTeal,
                                                fontSize = 11.sp
                                            )
                                        )
                                        Text(
                                            text = formatKhatmaReminderDisplay(reminderTime),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = KhatmaDarkTextPrimary,
                                                fontSize = 14.sp
                                            )
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(KhatmaDarkTeal)
                                        .clickable { showTimePickerDialog = true }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = if (isArabic) "ضبط المنبّه" else "Set Timer",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp
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

        item {
            // Plan Summary & Calculation Preview
            val isDark = LocalKhatmaColors.current.isDark
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = KhatmaDarkCard,
                shadowElevation = if (isDark) 0.dp else 0.4.dp,
                border = null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Plan Summary",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = KhatmaDarkGold,
                            fontSize = 14.5.sp
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Daily Target:", style = MaterialTheme.typography.bodySmall.copy(color = KhatmaDarkTextSecondary))
                        Text(
                            text = "~$dailyTargetAyahs Ayahs / day",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Estimated Completion:", style = MaterialTheme.typography.bodySmall.copy(color = KhatmaDarkTextSecondary))
                        Text(
                            text = estCompletionDate,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Holy Quran:", style = MaterialTheme.typography.bodySmall.copy(color = KhatmaDarkTextSecondary))
                        Text(
                            text = "6,236 Ayahs (114 Surahs)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary)
                        )
                    }
                }
            }
        }

        item {
            // Start Khatma CTA Button (Gradient Green & Pill Rounded Edges)
            KhatmaGradientButton(
                onClick = {
                    viewModel.createOrResetKhatma(
                        days = effectiveDays,
                        startDate = LocalDate.now(),
                        sessionsCount = selectedSessions,
                        reminderEnabled = reminderEnabled,
                        reminderTime = reminderTime,
                        title = planTitle
                    )
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("create_khatma_button")
            ) {
                Icon(imageVector = Icons.Default.AutoStories, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) "ابدأ الختمة (بِسْمِ اللَّهِ)" else "Begin Khatma",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                )
            }
        }

        if (isExistingKhatmaCompleted) {
            item {
                val isDark = LocalKhatmaColors.current.isDark
                Button(
                    onClick = onOpenHistory,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) SurfaceElevatedDark else Color(0xFFF1F5F9),
                        contentColor = KhatmaDarkGold
                    ),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.History, contentDescription = null, tint = KhatmaDarkGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Completed Khatma History", color = KhatmaDarkGold, fontWeight = FontWeight.Medium, fontSize = 13.5.sp)
                }
            }
        }
    }
}

/**
 * Main Active Dashboard Content
 */
@Composable
fun KhatmaDashboardContent(
    state: KhatmaFullDashboardState,
    viewModel: MainViewModel,
    onOpenSettings: () -> Unit,
    onOpenPaceAdjust: () -> Unit,
    onQuickAdd: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Dashboard, 1: Timeline Plan

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = KhatmaDarkBg,
            contentColor = KhatmaDarkAccent,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = KhatmaDarkAccent
                    )
                }
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Dashboard", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = if (selectedTab == 0) KhatmaDarkAccent else KhatmaDarkTextSecondary) },
                icon = { Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (selectedTab == 0) KhatmaDarkAccent else KhatmaDarkTextSecondary) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Reading Plan (${state.totalDays} Days)", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = if (selectedTab == 1) KhatmaDarkAccent else KhatmaDarkTextSecondary) },
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (selectedTab == 1) KhatmaDarkAccent else KhatmaDarkTextSecondary) }
            )
        }

        when (selectedTab) {
            0 -> KhatmaDashboardOverview(
                state = state,
                viewModel = viewModel,
                onOpenPaceAdjust = onOpenPaceAdjust,
                onQuickAdd = onQuickAdd
            )
            1 -> KhatmaTimelineView(
                timeline = state.dayPlanTimeline,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun KhatmaDashboardOverview(
    state: KhatmaFullDashboardState,
    viewModel: MainViewModel,
    onOpenPaceAdjust: () -> Unit,
    onQuickAdd: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            // First section: Progress Card matching Azkar page style
            KhatmaHeroProgressCard(
                state = state,
                onContinueReading = { viewModel.continueKhatmaReading() },
                onQuickAdd = onQuickAdd
            )
        }

        item {
            // Section 2 Container: Today's Target & Sessions
            KhatmaTodaySessionsCard(
                state = state,
                onCompleteSession = { sessionIndex, targetAyahs ->
                    viewModel.completeKhatmaSession(sessionIndex, targetAyahs)
                },
                onReadSessionPortion = { surahNum, ayahNum ->
                    viewModel.openKhatmaReadingAtAyah(surahNum, ayahNum)
                }
            )
        }

        item {
            // Section 3 Container: Pace Status & Timeline
            KhatmaPaceBannerCard(
                state = state,
                onOpenPaceAdjust = onOpenPaceAdjust
            )
        }

        item {
            // Section 4 Container: Journey Analytics
            KhatmaJourneyAnalyticsCard(state = state)
        }

        item {
            // Section 5 Container: Quick Management Actions
            KhatmaQuickActionsCard(
                onOpenPaceAdjust = onOpenPaceAdjust,
                onFinishKhatma = { viewModel.markKhatmaCompleted() }
            )
        }
    }
}

/**
 * Hero Progress Card matching Azkar progress card style (same color, same shape, circular progress gauge)
 */
@Composable
fun KhatmaHeroProgressCard(
    state: KhatmaFullDashboardState,
    onContinueReading: () -> Unit,
    onQuickAdd: () -> Unit
) {
    val khatmaColors = LocalKhatmaColors.current
    val isDark = khatmaColors.isDark
    val isCompleted = state.progressPercentage >= 100
    val greenPrimary = if (isDark) SuccessGreenDark else SuccessGreenLight
    val accent = khatmaColors.accent
    val accentSoft = khatmaColors.accentSoft
    val surfaceColor = if (isDark) SurfaceDark else SurfaceWhite
    val borderCol = if (isDark) BorderDividerDark else BorderDividerLight

    val animatedProgress by animateFloatAsState(
        targetValue = state.progressFraction,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "heroProgress"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor,
        shadowElevation = if (isDark) 0.dp else 0.4.dp,
        border = null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row with Title, Badge, and Circular Progress Gauge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isCompleted) greenPrimary.copy(alpha = 0.15f) else accentSoft,
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Text(
                            text = if (isCompleted) "KHATMA COMPLETED" else "KHATMA PROGRESS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp,
                                color = if (isCompleted) greenPrimary else accent
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = state.plan.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                            fontSize = 17.sp
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isCompleted) greenPrimary else accent)
                        )
                        Text(
                            text = "Day ${state.currentDayNumber} of ${state.totalDays} • ${state.daysRemaining} Days left",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Circular indicator ring matching AzkarDailyCompletionCard
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(56.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = if (isDark) SurfaceElevatedDark else accentSoft,
                        strokeWidth = 5.dp
                    )
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = if (isCompleted) greenPrimary else accent,
                        strokeWidth = 5.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "${state.progressPercentage}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                    )
                }
            }

            // Linear Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isCompleted) greenPrimary else accent,
                trackColor = if (isDark) SurfaceElevatedDark else accentSoft,
                strokeCap = StrokeCap.Round
            )

            // Ayahs recited and pace status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${state.readAyahsCount.formatNumber()} / ${state.totalAyahs.formatNumber()} Ayahs",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                        fontSize = 13.sp
                    )
                )
                PaceBadge(status = state.paceStatus, diff = state.paceDiffAyahs, isHeroGradient = false)
            }

            // Current Reading Position Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) SurfaceElevatedDark else SurfaceElevatedLight,
                border = null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "Current Position",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                    fontSize = 10.5.sp
                                )
                            )
                            Text(
                                text = "${state.currentPosition.surahNameEnglish} (Ayah ${state.currentPosition.ayahNumber})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accentSoft
                    ) {
                        Text(
                            text = "Juz ${state.currentPosition.juzNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = accent,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Prominent "Continue Reading" Action Button
            Button(
                onClick = onContinueReading,
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KhatmaDarkTeal,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("continue_reading_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Continue Reading (${state.nextReadingPosition.displayShort})",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 13.5.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Today's Target & Split Sessions Card Container
 */
@Composable
fun KhatmaTodaySessionsCard(
    state: KhatmaFullDashboardState,
    onCompleteSession: (Int, Int) -> Unit,
    onReadSessionPortion: (Int, Int) -> Unit
) {
    val khatmaColors = LocalKhatmaColors.current
    val isDark = khatmaColors.isDark
    val borderCol = if (isDark) BorderDividerDark else BorderDividerLight
    val accent = khatmaColors.accent
    val accentSoft = khatmaColors.accentSoft
    val greenPrimary = if (isDark) SuccessGreenDark else SuccessGreenLight

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) SurfaceDark else SurfaceWhite,
        shadowElevation = if (isDark) 0.dp else 0.4.dp,
        border = null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Target",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "${state.todayReadAyahs} of ${state.todayTargetAyahs} Ayahs",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (state.isTodayTargetAchieved) greenPrimary else (if (isDark) TextSecondaryDark else TextSecondaryLight),
                            fontSize = 13.sp
                        )
                    )
                }

                if (state.isTodayTargetAchieved) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = greenPrimary.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = greenPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Today's Goal Done!",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = greenPrimary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                } else {
                    Text(
                        text = "${state.todayRemainingAyahs} Ayahs remaining",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Today's Linear Progress
            val todayProgress = (state.todayReadAyahs.toFloat() / state.todayTargetAyahs.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { todayProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (state.isTodayTargetAchieved) greenPrimary else accent,
                trackColor = if (isDark) SurfaceElevatedDark else accentSoft,
                strokeCap = StrokeCap.Round
            )

            // Split Sessions List Title
            Text(
                text = "Daily Reflection Sessions (${state.dailySessions.size})",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                    fontSize = 12.5.sp
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.dailySessions.forEach { session ->
                    SessionRowItem(
                        session = session,
                        onComplete = { onCompleteSession(session.index, session.targetAyahsCount) },
                        onRead = { onReadSessionPortion(session.startAyahCoord.surahNumber, session.startAyahCoord.ayahNumber) }
                    )
                }
            }
        }
    }
}

@Composable
fun SessionRowItem(
    session: KhatmaSessionInfo,
    onComplete: () -> Unit,
    onRead: () -> Unit
) {
    val khatmaColors = LocalKhatmaColors.current
    val isDark = khatmaColors.isDark
    val isDone = session.isCompleted
    val greenPrimary = if (isDark) SuccessGreenDark else SuccessGreenLight
    val accent = khatmaColors.accent
    val containerBg by animateColorAsState(
        if (isDone) (if (isDark) GoldTintBgDark else GoldTintBgLight) else (if (isDark) SurfaceElevatedDark else SurfaceElevatedLight),
        label = "sessionBg"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerBg,
        border = null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onComplete,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("session_complete_btn_${session.index}")
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                        contentDescription = if (isDone) "Completed" else "Mark Complete",
                        tint = if (isDone) greenPrimary else (if (isDark) TextSecondaryDark else TextSecondaryLight)
                    )
                }

                Column {
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDone) greenPrimary else (if (isDark) TextPrimaryDark else TextPrimaryLight),
                            fontSize = 13.5.sp
                        )
                    )
                    Text(
                        text = "${session.startAyahCoord.displayShort} → ${session.endAyahCoord.displayShort} (${session.targetAyahsCount} Ayahs)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            OutlinedButton(
                onClick = onRead,
                shape = RoundedCornerShape(16.dp),
                border = null,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = if (isDone) "Review" else "Read",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = accent
                )
            }
        }
    }
}

/**
 * Pace Status & Smart Pace Adjuster Banner Container
 */
@Composable
fun KhatmaPaceBannerCard(
    state: KhatmaFullDashboardState,
    onOpenPaceAdjust: () -> Unit
) {
    val khatmaColors = LocalKhatmaColors.current
    val isDark = khatmaColors.isDark
    val borderCol = if (isDark) BorderDividerDark else BorderDividerLight
    val greenPrimary = if (isDark) SuccessGreenDark else SuccessGreenLight
    val accent = khatmaColors.accent

    val (iconColor, icon, title, description) = when (state.paceStatus) {
        KhatmaPaceStatus.AHEAD -> Quadruple(
            greenPrimary,
            Icons.Default.TrendingUp,
            "Ahead of Schedule (+${state.paceDiffAyahs} Ayahs)",
            "Masha'Allah! You are reading ahead of your planned timeline. Keep this blessed momentum."
        )
        KhatmaPaceStatus.BEHIND -> Quadruple(
            if (isDark) DangerRedDark else DangerRedLight,
            Icons.Default.Speed,
            "Behind Schedule (${state.paceDiffAyahs} Ayahs)",
            "Life happens. Choose a gentle catch-up pace or extend your timeline with peace and barakah."
        )
        KhatmaPaceStatus.ON_TRACK -> Quadruple(
            greenPrimary,
            Icons.Default.CheckCircle,
            "Right on Track",
            "You are adhering faithfully to your daily Khatma goals. May Allah accept every letter."
        )
        KhatmaPaceStatus.COMPLETED -> Quadruple(
            if (isDark) SecondaryGoldDark else SecondaryGoldLight,
            Icons.Default.Star,
            "Khatma Completed! Alhamdulillah",
            "You have recited all 6,236 Ayahs of the Holy Quran."
        )
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) SurfaceDark else SurfaceWhite,
        shadowElevation = if (isDark) 0.dp else 0.4.dp,
        border = null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Pace Status & Timeline",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                    fontSize = 16.sp
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                        fontSize = 14.5.sp
                    )
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                    fontSize = 12.5.sp
                )
            )

            if (state.paceStatus == KhatmaPaceStatus.BEHIND) {
                Spacer(modifier = Modifier.height(2.dp))
                Button(
                    onClick = onOpenPaceAdjust,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KhatmaDarkTeal,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Compassionate Pace Adjuster", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
    }
}

/**
 * Journey Analytics Container Card
 */
@Composable
fun KhatmaJourneyAnalyticsCard(state: KhatmaFullDashboardState) {
    val isDark = LocalKhatmaColors.current.isDark
    val borderCol = if (isDark) BorderDividerDark else BorderDividerLight

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) SurfaceDark else SurfaceWhite,
        shadowElevation = if (isDark) 0.dp else 0.4.dp,
        border = null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Journey Analytics",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                    fontSize = 16.sp
                )
            )
            KhatmaStatsMatrix(state = state)
        }
    }
}

/**
 * Quick Actions Container Card
 */
@Composable
fun KhatmaQuickActionsCard(
    onOpenPaceAdjust: () -> Unit,
    onFinishKhatma: () -> Unit
) {
    val khatmaColors = LocalKhatmaColors.current
    val isDark = khatmaColors.isDark
    val borderCol = if (isDark) BorderDividerDark else BorderDividerLight
    val accent = khatmaColors.accent

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) SurfaceDark else SurfaceWhite,
        shadowElevation = if (isDark) 0.dp else 0.4.dp,
        border = null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                    fontSize = 16.sp
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenPaceAdjust,
                    shape = RoundedCornerShape(20.dp),
                    border = null,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = accent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Adjust Pace",
                        fontSize = 13.sp,
                        color = accent,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onFinishKhatma,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KhatmaDarkTeal,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Finish Khatma",
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * 2x2 Stats Matrix (Estimated Date, Days Left, Total Surahs, Daily Target)
 */
@Composable
fun KhatmaStatsMatrix(state: KhatmaFullDashboardState) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatGridItem(
                title = "Est. Completion",
                value = state.estimatedCompletionDate,
                subtitle = "In ${state.daysRemaining} days",
                icon = Icons.Default.DateRange,
                modifier = Modifier.weight(1f)
            )
            StatGridItem(
                title = "Daily Average",
                value = "${state.todayTargetAyahs} Ayahs",
                subtitle = "${state.plan.dailySessionsCount} sessions/day",
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatGridItem(
                title = "Current Surah",
                value = state.currentPosition.surahNameEnglish,
                subtitle = state.currentPosition.surahNameArabic,
                icon = Icons.Default.AutoStories,
                modifier = Modifier.weight(1f)
            )
            StatGridItem(
                title = "Current Juz",
                value = "Juz ${state.currentPosition.juzNumber}",
                subtitle = "of 30 Juz's",
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatGridItem(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    val khatmaColors = LocalKhatmaColors.current
    val isDark = khatmaColors.isDark
    val borderCol = if (isDark) BorderDividerDark else BorderDividerLight
    val accent = khatmaColors.accent

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isDark) SurfaceElevatedDark else SurfaceElevatedLight,
        shadowElevation = 0.dp,
        border = null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                        fontSize = 11.5.sp
                    )
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                    fontSize = 14.5.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Full Day Timeline / Calendar Tab
 */
@Composable
fun KhatmaTimelineView(
    timeline: List<KhatmaDayItem>,
    viewModel: MainViewModel
) {
    var filterMode by remember { mutableIntStateOf(0) } // 0: All, 1: Upcoming, 2: Completed

    val filteredList = remember(filterMode, timeline) {
        when (filterMode) {
            1 -> timeline.filter { it.isUpcoming || it.isToday }
            2 -> timeline.filter { it.isCompleted }
            else -> timeline
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 18.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Filter Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = filterMode == 0,
                    onClick = { filterMode = 0 },
                    label = { Text("All Days (${timeline.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KhatmaDarkAccent,
                        selectedLabelColor = Color.White,
                        containerColor = KhatmaDarkCard,
                        labelColor = KhatmaDarkTextSecondary
                    ),
                    border = null
                )
                FilterChip(
                    selected = filterMode == 1,
                    onClick = { filterMode = 1 },
                    label = { Text("Upcoming") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KhatmaDarkAccent,
                        selectedLabelColor = Color.White,
                        containerColor = KhatmaDarkCard,
                        labelColor = KhatmaDarkTextSecondary
                    ),
                    border = null
                )
                FilterChip(
                    selected = filterMode == 2,
                    onClick = { filterMode = 2 },
                    label = { Text("Completed") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KhatmaDarkAccent,
                        selectedLabelColor = Color.White,
                        containerColor = KhatmaDarkCard,
                        labelColor = KhatmaDarkTextSecondary
                    ),
                    border = null
                )
            }
        }

        items(filteredList) { item ->
            TimelineDayCard(
                dayItem = item,
                onReadDayPortion = {
                    viewModel.openKhatmaReadingAtAyah(item.startCoord.surahNumber, item.startCoord.ayahNumber)
                }
            )
        }
    }
}

@Composable
fun TimelineDayCard(
    dayItem: KhatmaDayItem,
    onReadDayPortion: () -> Unit
) {
    val isToday = dayItem.isToday
    val isCompleted = dayItem.isCompleted

    val containerBg = when {
        isToday -> KhatmaDarkAccentSoft
        else -> KhatmaDarkCard
    }

    val borderColor = when {
        isToday -> KhatmaDarkAccent
        else -> KhatmaDarkBorder
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerBg,
        shadowElevation = if (isToday || LocalKhatmaColors.current.isDark) 0.dp else 0.4.dp,
        border = null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Status Indicator Icon
                Surface(
                    shape = CircleShape,
                    color = when {
                        isCompleted -> KhatmaDarkAccent
                        isToday -> KhatmaDarkAccent.copy(alpha = 0.2f)
                        else -> KhatmaDarkElevated
                    },
                    border = null,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = "${dayItem.dayNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isToday) KhatmaDarkAccent else KhatmaDarkTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Day ${dayItem.dayNumber}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary, fontSize = 13.5.sp)
                        )
                        Text(
                            text = "• ${dayItem.dateFormatted}",
                            style = MaterialTheme.typography.bodySmall.copy(color = KhatmaDarkTextSecondary, fontSize = 12.sp)
                        )
                        if (isToday) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = KhatmaDarkAccent
                            ) {
                                Text(
                                    text = "TODAY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "${dayItem.startCoord.displayShort} → ${dayItem.endCoord.displayShort}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = KhatmaDarkTextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )

                    Text(
                        text = "${dayItem.targetAyahsCount} Ayahs • Juz ${dayItem.startCoord.juzNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = KhatmaDarkTextSecondary,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            OutlinedButton(
                onClick = onReadDayPortion,
                shape = RoundedCornerShape(16.dp),
                border = null,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = if (isCompleted) "Review" else "Read",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = KhatmaDarkAccent
                )
            }
        }
    }
}

/**
 * Pace Badge Indicator
 */
@Composable
fun PaceBadge(status: KhatmaPaceStatus, diff: Int, isHeroGradient: Boolean = false) {
    if (isHeroGradient) {
        val label = when (status) {
            KhatmaPaceStatus.AHEAD -> "+$diff Ahead"
            KhatmaPaceStatus.BEHIND -> "$diff Behind"
            KhatmaPaceStatus.ON_TRACK -> "On Track"
            KhatmaPaceStatus.COMPLETED -> "Completed"
        }
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White.copy(alpha = 0.2f),
            border = null
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
            )
        }
        return
    }

    val (bg, text, border, label) = when (status) {
        KhatmaPaceStatus.AHEAD -> Quadruple(
            KhatmaDarkGoldBg,
            KhatmaDarkGold,
            KhatmaDarkGold.copy(alpha = 0.4f),
            "+$diff Ahead"
        )
        KhatmaPaceStatus.BEHIND -> Quadruple(
            if (LocalKhatmaColors.current.isDark) DangerRedDark.copy(alpha = 0.2f) else DangerRedLight.copy(alpha = 0.15f),
            if (LocalKhatmaColors.current.isDark) DangerRedDark else DangerRedLight,
            if (LocalKhatmaColors.current.isDark) DangerRedDark.copy(alpha = 0.5f) else DangerRedLight.copy(alpha = 0.4f),
            "$diff Behind"
        )
        KhatmaPaceStatus.ON_TRACK -> Quadruple(
            KhatmaDarkAccentSoft,
            KhatmaDarkAccent,
            KhatmaDarkAccent.copy(alpha = 0.4f),
            "On Track"
        )
        KhatmaPaceStatus.COMPLETED -> Quadruple(
            KhatmaDarkAccent,
            Color.White,
            KhatmaDarkAccent,
            "Completed"
        )
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bg,
        border = null
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = text,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        )
    }
}

/**
 * Bottom Sheet for Settings & Changing Duration / Reset
 */
@Composable
fun KhatmaSettingsSheetContent(
    viewModel: MainViewModel,
    state: KhatmaFullDashboardState?,
    onDismiss: () -> Unit,
    onOpenPaceAdjust: () -> Unit
) {
    if (state == null) return

    var selectedDays by remember { mutableIntStateOf(state.totalDays) }
    var reminderEnabled by remember { mutableStateOf(state.plan.reminderEnabled) }
    var reminderTime by remember { mutableStateOf(state.plan.reminderTime) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Khatma Plan Settings",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary, fontSize = 17.sp)
        )

        // Duration Adjustment
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Change Total Duration",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary, fontSize = 14.sp)
            )
            val presets = listOf(7, 15, 30, 45, 60, 90)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presets) { days ->
                    FilterChip(
                        selected = selectedDays == days,
                        onClick = {
                            selectedDays = days
                            viewModel.changeKhatmaTotalDays(days)
                        },
                        label = { Text("$days Days", fontSize = 13.sp) },
                        shape = RoundedCornerShape(16.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KhatmaDarkAccent,
                            selectedLabelColor = Color.White,
                            containerColor = KhatmaDarkElevated,
                            labelColor = KhatmaDarkTextSecondary
                        ),
                        border = null
                    )
                }
            }
        }

        // Reminder Toggle & Timer
        var showTimePickerDialog by remember { mutableStateOf(false) }

        if (showTimePickerDialog) {
            val initialHour = remember(reminderTime) { reminderTime.split(":").getOrNull(0)?.toIntOrNull() ?: 20 }
            val initialMinute = remember(reminderTime) { reminderTime.split(":").getOrNull(1)?.toIntOrNull() ?: 30 }
            KhatmaTimePickerDialog(
                initialHour = initialHour,
                initialMinute = initialMinute,
                onDismiss = { showTimePickerDialog = false },
                onConfirm = { h, m ->
                    val newTime = String.format(Locale.getDefault(), "%02d:%02d", h, m)
                    reminderTime = newTime
                    viewModel.updateKhatmaReminder(reminderEnabled, newTime)
                    showTimePickerDialog = false
                }
            )
        }

        val isDark = LocalKhatmaColors.current.isDark
        val notifBgColor = KhatmaDarkTealSoft
        val notifBorderColor = KhatmaDarkTeal.copy(alpha = 0.3f)
        val notifAccentColor = KhatmaDarkTeal

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = notifBgColor,
            shadowElevation = if (isDark) 0.dp else 0.4.dp,
            border = null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Reminder",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = if (reminderEnabled) "Notify at $reminderTime" else "Disabled",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = {
                            reminderEnabled = it
                            viewModel.updateKhatmaReminder(it, reminderTime)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = notifAccentColor,
                            uncheckedThumbColor = if (isDark) TextSecondaryDark else TextSecondaryLight,
                            uncheckedTrackColor = if (isDark) BorderDividerDark else BorderDividerLight
                        )
                    )
                }

                if (reminderEnabled) {
                    Surface(
                        onClick = { showTimePickerDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) SurfaceElevatedDark else SurfaceWhite,
                        border = null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = notifAccentColor, modifier = Modifier.size(18.dp))
                                Text(
                                    text = formatKhatmaReminderDisplay(reminderTime),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                                        fontSize = 13.5.sp
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(notifAccentColor)
                                    .clickable { showTimePickerDialog = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Set Time",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Compassionate Pace Adjustment Button
        KhatmaGradientButton(
            onClick = onOpenPaceAdjust,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Speed, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Open Pace Adjuster",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        val dangerColor = if (isDark) DangerRedDark else DangerRedLight

        // Reset / Delete Plan Button
        OutlinedButton(
            onClick = { showDeleteConfirm = true },
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = dangerColor),
            border = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = dangerColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset Khatma Plan", color = dangerColor, fontWeight = FontWeight.Medium, fontSize = 13.5.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showDeleteConfirm) {
        val dangerColor = if (LocalKhatmaColors.current.isDark) DangerRedDark else DangerRedLight
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Reset Khatma?", fontWeight = FontWeight.SemiBold, color = if (LocalKhatmaColors.current.isDark) TextPrimaryDark else TextPrimaryLight, fontSize = 16.sp) },
            text = { Text("Are you sure you want to reset your current Khatma plan? You can start a new one anytime.", color = if (LocalKhatmaColors.current.isDark) TextSecondaryDark else TextSecondaryLight, fontSize = 13.5.sp) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteActiveKhatma()
                        showDeleteConfirm = false
                        onDismiss()
                    }
                ) {
                    Text("Reset Plan", color = dangerColor, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = if (LocalKhatmaColors.current.isDark) TextSecondaryDark else TextSecondaryLight)
                }
            },
            containerColor = if (LocalKhatmaColors.current.isDark) SurfaceDark else SurfaceWhite
        )
    }
}

/**
 * Compassionate Pace Adjustment Sheet
 */
@Composable
fun KhatmaPaceAdjustmentSheetContent(
    viewModel: MainViewModel,
    state: KhatmaFullDashboardState?,
    onDismiss: () -> Unit
) {
    if (state == null) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Smart Pace Adjuster",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary, fontSize = 17.sp)
        )
        Text(
            text = "Reciting the Quran is a spiritual relationship built on devotion, not stress. Choose how you would like to comfortably adapt your reading goals:",
            style = MaterialTheme.typography.bodySmall.copy(color = KhatmaDarkTextSecondary, fontSize = 12.5.sp)
        )

        // Option 1: Spread evenly
        Surface(
            onClick = {
                viewModel.adjustKhatmaPace("SPREAD")
                onDismiss()
            },
            shape = RoundedCornerShape(16.dp),
            color = KhatmaDarkCard,
            shadowElevation = if (LocalKhatmaColors.current.isDark) 0.dp else 0.4.dp,
            border = null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "1. Spread Evenly Across Remaining Days",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary, fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Recalculates the remaining ${KhatmaEngine.TOTAL_QURAN_AYAHS - state.readAyahsCount} Ayahs equally over the remaining ${state.daysRemaining} days.",
                    style = MaterialTheme.typography.bodySmall.copy(color = KhatmaDarkTextSecondary, fontSize = 12.sp)
                )
            }
        }

        // Option 2: Catch up gradually
        Surface(
            onClick = {
                viewModel.adjustKhatmaPace("GRADUAL")
                onDismiss()
            },
            shape = RoundedCornerShape(16.dp),
            color = KhatmaDarkCard,
            shadowElevation = if (LocalKhatmaColors.current.isDark) 0.dp else 0.4.dp,
            border = null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "2. Catch Up Gradually (+15 Ayahs / Day)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary, fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Adds a small, manageable booster to your daily sessions until you are back on track.",
                    style = MaterialTheme.typography.bodySmall.copy(color = KhatmaDarkTextSecondary, fontSize = 12.sp)
                )
            }
        }

        // Option 3: Extend completion deadline
        Surface(
            onClick = {
                viewModel.adjustKhatmaPace("EXTEND")
                onDismiss()
            },
            shape = RoundedCornerShape(16.dp),
            color = KhatmaDarkCard,
            shadowElevation = if (LocalKhatmaColors.current.isDark) 0.dp else 0.4.dp,
            border = null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "3. Extend Completion Deadline",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary, fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Maintains a calm, comfortable daily pace and smoothly pushes the target completion date outward.",
                    style = MaterialTheme.typography.bodySmall.copy(color = KhatmaDarkTextSecondary, fontSize = 12.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Khatma History Sheet
 */
@Composable
fun KhatmaHistorySheetContent(
    historyList: List<KhatmaHistoryEntity>,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Completed Khatmas History",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary, fontSize = 17.sp)
        )

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = KhatmaDarkTextMuted.copy(alpha = 0.5f),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No completed Khatmas yet",
                        style = MaterialTheme.typography.bodyMedium.copy(color = KhatmaDarkTextPrimary, fontWeight = FontWeight.Medium)
                    )
                    Text(
                        text = "Your completed Quran milestones will be preserved here.",
                        style = MaterialTheme.typography.bodySmall.copy(color = KhatmaDarkTextSecondary, fontSize = 12.sp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(historyList) { item ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = KhatmaDarkCard,
                        shadowElevation = if (LocalKhatmaColors.current.isDark) 0.dp else 0.4.dp,
                        border = null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary, fontSize = 14.5.sp)
                                )
                                Text(
                                    text = "Completed in ${item.daysTaken} days • ${item.completionDateFormatted}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = KhatmaDarkTextSecondary, fontSize = 12.sp)
                                )
                                Text(
                                    text = "6,236 Ayahs • Full Quran",
                                    style = MaterialTheme.typography.labelSmall.copy(color = KhatmaDarkAccent, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = KhatmaDarkAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Khatma Setup Sheet Content (for editing / creating)
 */
@Composable
fun KhatmaSetupSheetContent(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    KhatmaSetupView(
        viewModel = viewModel,
        isExistingKhatmaCompleted = false,
        onOpenHistory = {
            onDismiss()
            viewModel.isKhatmaHistorySheetOpen.value = true
        }
    )
}

/**
 * Serene Celebration Dialog with Dua Khatm Al-Quran
 */
@Composable
fun KhatmaCompletionCelebrationDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        confirmButton = {
            KhatmaGradientButton(
                onClick = {
                    onDismiss()
                    viewModel.isKhatmaSetupSheetOpen.value = true
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("Start a New Khatma", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = KhatmaDarkTextSecondary, fontSize = 13.5.sp)
            }
        },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "الحمد لله رب العالمين 🤍",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = KhatmaDarkAccent
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Khatma Completed!",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary, fontSize = 16.sp),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "May Allah accept your recitation, make the Quran a guiding light for your heart, and elevate your rank in Jannah.",
                        style = MaterialTheme.typography.bodySmall.copy(color = KhatmaDarkTextSecondary, fontSize = 12.sp),
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = KhatmaDarkElevated,
                        border = null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "دعاء ختم القرآن الكريم",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = KhatmaDarkAccent
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = KhatmaEngine.DUA_KHATM_ARABIC,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Right,
                                    color = KhatmaDarkTextPrimary,
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = KhatmaEngine.DUA_KHATM_TRANSLATION,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = KhatmaDarkTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        },
        containerColor = KhatmaDarkCard
    )
}

/**
 * Quick Log Ayahs Dialog
 */
@Composable
fun QuickLogAyahsDialog(
    onDismiss: () -> Unit,
    onAdd: (Int) -> Unit
) {
    val options = listOf(5, 10, 20, 50)
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Log Ayahs Read", fontWeight = FontWeight.SemiBold, color = KhatmaDarkTextPrimary, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select the number of Ayahs read to advance your Khatma progress:", color = KhatmaDarkTextSecondary, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    options.forEach { count ->
                        OutlinedButton(
                            onClick = { onAdd(count) },
                            shape = RoundedCornerShape(16.dp),
                            border = null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+$count", color = KhatmaDarkAccent, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KhatmaDarkTextSecondary)
            }
        },
        containerColor = KhatmaDarkCard
    )
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

private fun Int.formatNumber(): String {
    return String.format(Locale.getDefault(), "%,d", this)
}
