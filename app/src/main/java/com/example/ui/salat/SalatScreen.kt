package com.example.ui.salat

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.data.prayer.AdhanAudioRepository
import com.example.data.prayer.AdhanSound
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import android.content.pm.PackageManager
import android.os.Build
import android.content.ContextWrapper
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.example.R
import com.example.data.model.CalculationAuthority
import com.example.data.model.PrayerTime
import com.example.data.model.PrayerZone
import com.example.ui.MainViewModel
import com.example.ui.components.BentoCard
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.BorderTealLight
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DangerRedDark
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.DangerRedBgDark
import com.example.ui.theme.DangerRedBgLight
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldTintBgDark
import com.example.ui.theme.GoldTintBgLight
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.PrimaryTealGradient
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.ReadingThemes
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalAppThemeMode
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.SurfaceElevated

/**
 * Dedicated color tokens for Salat screen, keeping Light and Dark modes strictly identical
 * to their design baseline, and isolating Warm mode palette separately.
 */
private data class SalatColorPalette(
    val pageBackground: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val primaryAccent: Color,
    val salatGreen: Color,
    val salatGrey: Color,
    val salatGreyCircleBg: Color,
    val linkBadgeBg: Color,
    val linkTextColor: Color,
    val goldColor: Color,
    val goldAccentColor: Color,
    val softGoldNoticeBg: Color,
    val noticeBg: Color,
    val noticeTextColor: Color,
    val titleText: Color,
    val subtext: Color,
    val dividerColor: Color,
    val sunriseBg: Color,
    val sunriseTextColor: Color,
    val iconBadgeBg: Color,
    val statusBadgeBg: Color,
    val activeRowBg: Color,
    val settingsCardBg: Color,
    val settingsInnerCardBg: Color,
    val settingsBadgeBg: Color,
    val settingsBadgeText: Color,
    val settingsButtonBg: Color,
    val settingsButtonText: Color,
    val settingsProgressTrack: Color,
    val settingsIconBadgeBg: Color,
    val settingsUnselectedBg: Color
)

@Composable
private fun rememberSalatColors(themeColors: ReadingThemeColors): SalatColorPalette {
    val isDark = themeColors.isDark
    val isWarm = LocalAppThemeMode.current == AppThemeMode.WARM

    return remember(isDark, isWarm, themeColors) {
        if (isWarm) {
            // WARM MODE: Warm parchment & amber/gold palette, soft teal notice & teal sunrise
            SalatColorPalette(
                pageBackground = Color(0xFFFCFBF9),
                cardBackground = Color.White,
                cardBorder = Color(0xFFE8DFD1),
                primaryAccent = Color(0xFFD9A44E),
                salatGreen = Color(0xFFD9A44E),
                salatGrey = Color(0xFF7A6650),
                salatGreyCircleBg = Color(0xFFFAF6EE),
                linkBadgeBg = Color(0xFFF2EAE1),
                linkTextColor = Color(0xFF7A5C3E),
                goldColor = Color(0xFFD9A44E),
                goldAccentColor = Color(0xFFD9A44E),
                softGoldNoticeBg = Color(0xFFE6F2F0),
                noticeBg = Color(0xFFE6F2F0), // Soft teal notice
                noticeTextColor = Color(0xFF0F433F), // Deep teal text/icons
                titleText = Color(0xFF1F1F1F),
                subtext = Color(0xFF7A6650),
                dividerColor = Color(0xFFEDE0C8),
                sunriseBg = Color(0xFFE6F2F0), // Teal sunrise pill
                sunriseTextColor = Color(0xFF0F433F), // Teal sunrise icon & text
                iconBadgeBg = Color(0xFFFBF3E4),
                statusBadgeBg = Color(0xFFFBF3E4),
                activeRowBg = Color(0xFFFBF3E4).copy(alpha = 0.5f),
                settingsCardBg = Color(0xFFFAF6EE),
                settingsInnerCardBg = Color.White,
                settingsBadgeBg = Color(0xFFF2EAE1),
                settingsBadgeText = Color(0xFF7A5C3E),
                settingsButtonBg = Color(0xFFD9A44E),
                settingsButtonText = Color.White,
                settingsProgressTrack = Color(0xFFF0E5D4),
                settingsIconBadgeBg = Color(0xFFFBF3E4),
                settingsUnselectedBg = Color(0xFFFAF6EE)
            )
        } else if (isDark) {
            // DARK MODE: Dark obsidian theme
            SalatColorPalette(
                pageBackground = themeColors.background,
                cardBackground = themeColors.surface,
                cardBorder = themeColors.border.copy(alpha = 0.5f),
                primaryAccent = Color(0xFF2FBF96),
                salatGreen = SuccessGreenDark,
                salatGrey = Color(0xFF8B8D91),
                salatGreyCircleBg = themeColors.border,
                linkBadgeBg = Color(0xFF1E282D),
                linkTextColor = Color(0xFF2FBF96),
                goldColor = SecondaryGoldDark,
                goldAccentColor = SecondaryGoldDark,
                softGoldNoticeBg = Color(0xFF064E3B).copy(alpha = 0.25f),
                noticeBg = Color(0xFF064E3B).copy(alpha = 0.25f),
                noticeTextColor = Color(0xFF34D399),
                titleText = themeColors.arabicText,
                subtext = themeColors.translationText,
                dividerColor = themeColors.border.copy(alpha = 0.25f),
                sunriseBg = themeColors.border,
                sunriseTextColor = SecondaryGoldDark,
                iconBadgeBg = themeColors.border,
                statusBadgeBg = SuccessGreenDark.copy(alpha = 0.15f),
                activeRowBg = SuccessGreenDark.copy(alpha = 0.12f),
                settingsCardBg = themeColors.border,
                settingsInnerCardBg = themeColors.surface,
                settingsBadgeBg = Color(0xFF1E282D),
                settingsBadgeText = Color(0xFF2FBF96),
                settingsButtonBg = Color(0xFF2FBF96),
                settingsButtonText = Color.White,
                settingsProgressTrack = themeColors.border,
                settingsIconBadgeBg = themeColors.border,
                settingsUnselectedBg = themeColors.surface
            )
        } else {
            // LIGHT MODE: Soft gold notice & standard palette
            SalatColorPalette(
                pageBackground = themeColors.background,
                cardBackground = Color.White,
                cardBorder = Color(0xFFE2E8F0),
                primaryAccent = Color(0xFF107C41),
                salatGreen = Color(0xFF107C41),
                salatGrey = Color(0xFF757575),
                salatGreyCircleBg = Color(0xFFF3F4F6),
                linkBadgeBg = Color(0xFFF1F5F9),
                linkTextColor = Color(0xFF334155),
                goldColor = Color(0xFFC28100),
                goldAccentColor = Color(0xFFB45309),
                softGoldNoticeBg = Color(0xFFF0FDF4),
                noticeBg = Color(0xFFF0FDF4), // Refreshing soft mint green notice
                noticeTextColor = Color(0xFF15803D), // Rich emerald text & icons
                titleText = themeColors.arabicText,
                subtext = themeColors.translationText,
                dividerColor = Color(0xFFECEFF1),
                sunriseBg = Color(0xFFFEF3C7),
                sunriseTextColor = Color(0xFFC28100),
                iconBadgeBg = Color(0xFF107C41).copy(alpha = 0.12f),
                statusBadgeBg = Color(0xFF107C41).copy(alpha = 0.12f),
                activeRowBg = Color(0xFF107C41).copy(alpha = 0.08f),
                settingsCardBg = Color(0xFFF6F8F7),
                settingsInnerCardBg = Color.White,
                settingsBadgeBg = Color(0xFFEDF2F7),
                settingsBadgeText = Color(0xFF2A4365),
                settingsButtonBg = Color(0xFF107C41),
                settingsButtonText = Color.White,
                settingsProgressTrack = Color(0xFFE2E8F0),
                settingsIconBadgeBg = Color(0xFF107C41).copy(alpha = 0.12f),
                settingsUnselectedBg = Color(0xFFF6F8F7)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalatScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var isSettingsModalOpen by remember { mutableStateOf(false) }
    var isCalendarModalOpen by remember { mutableStateOf(false) }

    val readingThemeName by viewModel.sharedReadingTheme.collectAsStateWithLifecycle()
    val themeColors = remember(readingThemeName) { ReadingThemes.getThemeByName(readingThemeName) }
    val colors = rememberSalatColors(themeColors)

    Scaffold(
        topBar = {
            NoorTopBar(
                title = stringResource(R.string.salat_screen_title),
                eyebrow = "NOOR",
                subtitle = stringResource(R.string.salat_screen_subtitle),
                isDark = themeColors.isDark,
                themeColors = themeColors,
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = stringResource(R.string.action_back),
                actions = {
                    NoorGlassIconButton(
                        onClick = { isCalendarModalOpen = true },
                        icon = Icons.Default.CalendarMonth,
                        contentDescription = "Monthly Prayer Timetable"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    NoorGlassIconButton(
                        onClick = { isSettingsModalOpen = true },
                        icon = Icons.Default.Tune,
                        contentDescription = stringResource(R.string.salat_settings_sheet_title)
                    )
                }
            )
        },
        containerColor = colors.pageBackground,
        modifier = modifier
    ) { paddingValues ->
        SalatTimesContent(
            viewModel = viewModel,
            themeColors = themeColors,
            colors = colors,
            onOpenSettings = { isSettingsModalOpen = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )

        // Standard Modal Bottom Sheet for Salat Settings (Opens bottom to top)
        if (isSettingsModalOpen) {
            SalatSettingsModalSheet(
                onDismiss = { isSettingsModalOpen = false },
                viewModel = viewModel,
                themeColors = themeColors,
                colors = colors
            )
        }

        if (isCalendarModalOpen) {
            SalatCalendarModalSheet(
                onDismiss = { isCalendarModalOpen = false },
                viewModel = viewModel,
                themeColors = themeColors,
                colors = colors
            )
        }
    }
}

// =========================================================================
// SALAT SETTINGS MODAL BOTTOM SHEET (BOTTOM-TO-TOP TRANSITION & DROPDOWN SELECTORS)
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalatSettingsModalSheet(
    onDismiss: () -> Unit,
    viewModel: MainViewModel,
    themeColors: ReadingThemeColors,
    colors: SalatColorPalette
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val autoSilent by viewModel.autoSilentDuringSalat.collectAsStateWithLifecycle()
    val silentDuration by viewModel.silentDurationMinutes.collectAsStateWithLifecycle()
    val isHanafi by viewModel.isHanafiAsr.collectAsStateWithLifecycle()
    val selectedZone by viewModel.selectedPrayerZone.collectAsStateWithLifecycle()
    val selectedAuthority by viewModel.selectedAuthority.collectAsStateWithLifecycle()
    val manualOffsets by viewModel.prayerManualMinuteOffsets.collectAsStateWithLifecycle()
    val athanSound by viewModel.athanSoundName.collectAsStateWithLifecycle()
    val globalAdhanSoundId by viewModel.globalAdhanSoundId.collectAsStateWithLifecycle()
    val perPrayerAdhanSounds by viewModel.perPrayerAdhanSounds.collectAsStateWithLifecycle()
    val adhanVolume by viewModel.adhanSoundVolume.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAthanAudioPreviewPlaying.collectAsStateWithLifecycle()
    val hijriOffset by viewModel.hijriAdjustmentDays.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val primaryAccent = colors.primaryAccent
    val cardBg = colors.settingsCardBg
    val innerCardBg = colors.settingsInnerCardBg
    val textTitle = colors.titleText
    val textSub = colors.subtext
    val dividerColor = colors.dividerColor
    val badgeBg = colors.settingsBadgeBg
    val badgeText = colors.settingsBadgeText
    val buttonBg = colors.settingsButtonBg
    val buttonText = colors.settingsButtonText
    val progressTrack = colors.settingsProgressTrack
    val iconBadgeBg = colors.settingsIconBadgeBg
    val unselectedBg = colors.settingsUnselectedBg
    val isDark = themeColors.isDark

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = innerCardBg,
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 14.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.5.dp)
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(dividerColor)
                )
            }
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 4.dp, bottom = 36.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(iconBadgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = primaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.salat_settings_sheet_title),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textTitle,
                                    fontSize = 16.sp
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = badgeBg,
                                border = null
                            ) {
                                Text(
                                    text = if (isArabic) "إعدادات" else "OPTIONS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = badgeText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.salat_settings_sheet_sub),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = textSub,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_done),
                        tint = textSub,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider(color = dividerColor)

            // 1. MOSQUE MODE (AUTO-SILENT DND)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = cardBg,
                border = null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.salat_mosque_mode_title),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textTitle
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.salat_mosque_mode_sub, silentDuration),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = textSub,
                                    fontSize = 11.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Switch(
                            checked = autoSilent,
                            onCheckedChange = { viewModel.toggleAutoSilent() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = buttonBg,
                                uncheckedTrackColor = dividerColor,
                                uncheckedThumbColor = Color.White
                            )
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isArabic) "مدة التفعيل: $silentDuration دقيقة" else "Preset Silent Duration: ${silentDuration}m",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (autoSilent) primaryAccent else textSub,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val presetMinutes = listOf(10, 15, 20, 30, 45)
                            presetMinutes.forEach { min ->
                                val isSelected = silentDuration == min
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) buttonBg else unselectedBg,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setSilentDuration(min) }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${min}m",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else textTitle,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Manual Undo / DND Notice
                    if (autoSilent) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) Color(0xFF3B2D05) else Color(0xFFFFFBEB),
                            border = null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isArabic)
                                        "ملاحظة: تفعيل وضع المسجد يقوم بتشغيل خاصية كتم الصوت/عدم الإزعاج عند دخول وقت الصلاة. سيتوجب عليك إلغاء الوضع الصامت يدوياً من هاتف بعد انتهاء الصلاة."
                                    else
                                        "Note: Mosque Mode toggles system Silent/DND when prayer time arrives. Toggling this activates silent mode; you will need to manually return your phone volume/DND after prayer.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDark) Color(0xFFFEF3C7) else Color(0xFF92400E),
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 2. OPTION INPUT DROPDOWN: PRAYER TIME ZONE
            SalatZoneDropdownSelector(
                zones = viewModel.prayerZones,
                selectedZone = selectedZone,
                isArabic = isArabic,
                themeColors = themeColors,
                colors = colors,
                onSelectZone = { viewModel.selectPrayerZone(it) }
            )

            // 3. OPTION INPUT DROPDOWN: CALCULATION AUTHORITY
            SalatAuthorityDropdownSelector(
                authorities = viewModel.calculationAuthorities,
                selectedAuthority = selectedAuthority,
                themeColors = themeColors,
                colors = colors,
                onSelectAuthority = { viewModel.selectCalculationAuthority(it) }
            )

            // 4. MANUAL PRAYER TIME MINUTE ADJUSTMENTS
            SalatManualOffsetsCard(
                manualOffsets = manualOffsets,
                isArabic = isArabic,
                themeColors = themeColors,
                colors = colors,
                onUpdateOffset = { prayerName, delta ->
                    viewModel.updatePrayerManualOffset(prayerName, delta)
                },
                onResetOffsets = {
                    viewModel.resetPrayerManualOffsets()
                }
            )

            // 5. ADHAN AUDIO RECITATION & PER-PRAYER SOUNDS
            SalatAdhanAudioSettingsCard(
                globalVoiceId = globalAdhanSoundId,
                perPrayerSounds = perPrayerAdhanSounds,
                volume = adhanVolume,
                isPlaying = isAudioPlaying,
                themeColors = themeColors,
                colors = colors,
                isArabic = isArabic,
                onSelectGlobalVoice = { viewModel.setGlobalAdhanSound(it) },
                onSelectPrayerVoice = { pName, sId -> viewModel.setPrayerAdhanSound(pName, sId) },
                onVolumeChange = { viewModel.setAdhanSoundVolume(it) },
                onImportCustomAudio = { uri, pName -> viewModel.importCustomAdhanAudio(uri, pName) },
                onTogglePreview = { sId -> viewModel.toggleAthanAudioPreview(sId) }
            )

            // 6. HANAFI ASR JURISTIC CALCULATION
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = cardBg,
                border = null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.salat_hanafi_asr_title),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = textTitle
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isHanafi) stringResource(R.string.salat_hanafi_asr_desc)
                            else stringResource(R.string.salat_standard_asr_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = textSub,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Switch(
                        checked = isHanafi,
                        onCheckedChange = { viewModel.toggleHanafiAsr(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = buttonBg,
                            uncheckedTrackColor = dividerColor,
                            uncheckedThumbColor = Color.White
                        )
                    )
                }
            }

            // 7. HIJRI CALENDAR ADJUSTMENT
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = cardBg,
                border = null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(iconBadgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.salat_hijri_calib_title),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textTitle
                                )
                            )
                            Text(
                                text = stringResource(R.string.salat_hijri_calib_sub, if (hijriOffset > 0) "+$hijriOffset" else "$hijriOffset"),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = textSub,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = innerCardBg,
                            border = null,
                            modifier = Modifier.clickable { viewModel.updateHijriAdjustment(-1) }
                        ) {
                            Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Minus Day",
                                    tint = textSub,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (hijriOffset != 0) badgeBg else Color.Transparent,
                            border = null
                        ) {
                            Text(
                                text = if (hijriOffset > 0) "+$hijriOffset" else "$hijriOffset",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (hijriOffset != 0) badgeText else textTitle
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = innerCardBg,
                            border = null,
                            modifier = Modifier.clickable { viewModel.updateHijriAdjustment(1) }
                        ) {
                            Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Plus Day",
                                    tint = primaryAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 8. NOTIFICATION TROUBLESHOOTING & RELIABILITY DIAGNOSTICS
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        onDismiss()
                        viewModel.navigateTo(com.example.ui.NoorDestination.NOTIFICATION_TROUBLESHOOTING)
                    },
                shape = RoundedCornerShape(16.dp),
                color = cardBg,
                border = null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(iconBadgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.settings_troubleshooting_title),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textTitle,
                                    fontSize = 13.5.sp
                                )
                            )
                            Text(
                                text = stringResource(R.string.settings_troubleshooting_sub),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = textSub,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = buttonBg.copy(alpha = 0.12f),
                        border = null
                    ) {
                        Text(
                            text = "Check",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = buttonBg,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Done Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonBg),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.action_done),
                    color = buttonText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// =========================================================================
// DROPDOWN SELECTORS FOR SETTINGS
// =========================================================================

@Composable
private fun SalatZoneDropdownSelector(
    zones: List<PrayerZone>,
    selectedZone: PrayerZone,
    isArabic: Boolean,
    themeColors: ReadingThemeColors,
    colors: SalatColorPalette,
    onSelectZone: (PrayerZone) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val primaryAccent = colors.primaryAccent
    val cardBg = colors.settingsCardBg
    val innerCardBg = colors.settingsInnerCardBg
    val textTitle = colors.titleText
    val textSub = colors.subtext
    val iconBadgeBg = colors.settingsIconBadgeBg

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.salat_zones_title),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = textTitle
            )
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = true },
                shape = RoundedCornerShape(14.dp),
                color = cardBg,
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(iconBadgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isArabic) selectedZone.arabicName else "${selectedZone.name}, ${selectedZone.country}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textTitle
                                )
                            )
                            Text(
                                text = selectedZone.zoneLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = textSub,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Zone",
                        tint = textSub,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 360.dp)
                    .background(innerCardBg)
            ) {
                zones.forEach { zone ->
                    val isSelected = zone.id == selectedZone.id
                    val displayName = if (isArabic) zone.arabicName else "${zone.name}, ${zone.country}"

                    DropdownMenuItem(
                        text = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = displayName,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) primaryAccent else textTitle
                                            )
                                        )
                                        Text(
                                            text = zone.zoneLabel,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = textSub,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = primaryAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            onSelectZone(zone)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SalatAuthorityDropdownSelector(
    authorities: List<CalculationAuthority>,
    selectedAuthority: CalculationAuthority,
    themeColors: ReadingThemeColors,
    colors: SalatColorPalette,
    onSelectAuthority: (CalculationAuthority) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val primaryAccent = colors.primaryAccent
    val cardBg = colors.settingsCardBg
    val innerCardBg = colors.settingsInnerCardBg
    val textTitle = colors.titleText
    val textSub = colors.subtext
    val iconBadgeBg = colors.settingsIconBadgeBg

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.salat_calc_authority_title),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = textTitle
            )
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = true },
                shape = RoundedCornerShape(14.dp),
                color = cardBg,
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(iconBadgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = selectedAuthority.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textTitle
                                )
                            )
                            val ishaLabel = if (selectedAuthority.ishaIntervalMinutes != null) "${selectedAuthority.ishaIntervalMinutes}m" else "${selectedAuthority.ishaAngle}°"
                            Text(
                                text = "Fajr: ${selectedAuthority.fajrAngle}° • Isha: $ishaLabel",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = primaryAccent,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Authority",
                        tint = textSub,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 360.dp)
                    .background(innerCardBg)
            ) {
                authorities.forEach { auth ->
                    val isSelected = auth.id == selectedAuthority.id
                    val authIshaLabel = if (auth.ishaIntervalMinutes != null) "${auth.ishaIntervalMinutes}m" else "${auth.ishaAngle}°"

                    DropdownMenuItem(
                        text = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = auth.name,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) primaryAccent else textTitle
                                            )
                                        )
                                        Text(
                                            text = "Fajr: ${auth.fajrAngle}° • Isha: $authIshaLabel",
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = textSub,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = primaryAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            onSelectAuthority(auth)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SalatManualOffsetsCard(
    manualOffsets: Map<String, Int>,
    isArabic: Boolean,
    themeColors: ReadingThemeColors,
    colors: SalatColorPalette,
    onUpdateOffset: (String, Int) -> Unit,
    onResetOffsets: () -> Unit
) {
    val prayers = listOf(
        "Fajr" to (if (isArabic) "الفجر" else "Fajr"),
        "Sunrise" to (if (isArabic) "الشروق" else "Sunrise"),
        "Dhuhr" to (if (isArabic) "الظهر" else "Dhuhr"),
        "Asr" to (if (isArabic) "العصر" else "Asr"),
        "Maghrib" to (if (isArabic) "المغرب" else "Maghrib"),
        "Isha" to (if (isArabic) "العشاء" else "Isha")
    )

    val primaryAccent = colors.primaryAccent
    val cardBg = colors.settingsCardBg
    val innerCardBg = colors.settingsInnerCardBg
    val textTitle = colors.titleText
    val textSub = colors.subtext
    val dividerColor = colors.dividerColor
    val badgeBg = colors.settingsBadgeBg
    val badgeText = colors.settingsBadgeText
    val iconBadgeBg = colors.settingsIconBadgeBg
    val unselectedBg = colors.settingsUnselectedBg

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.salat_manual_offsets_title),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textTitle
                        )
                    )
                    Text(
                        text = stringResource(R.string.salat_manual_offsets_sub),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = textSub,
                            fontSize = 11.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeBg,
                    border = null,
                    modifier = Modifier.clickable { onResetOffsets() }
                ) {
                    Text(
                        text = stringResource(R.string.salat_reset_offsets),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = badgeText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            HorizontalDivider(color = dividerColor, thickness = 0.8.dp)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                prayers.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { (pKey, pLabel) ->
                            val currentOffset = manualOffsets[pKey] ?: 0
                            val offsetText = if (currentOffset > 0) "+$currentOffset min" else if (currentOffset < 0) "$currentOffset min" else "0 min"

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = innerCardBg,
                                border = null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = pLabel,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = textTitle,
                                                fontSize = 12.sp
                                            )
                                        )
                                        Text(
                                            text = offsetText,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (currentOffset != 0) primaryAccent else textSub,
                                                fontWeight = if (currentOffset != 0) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 10.5.sp
                                            )
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = unselectedBg,
                                            border = null,
                                            modifier = Modifier.clickable { onUpdateOffset(pKey, -1) }
                                        ) {
                                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Remove,
                                                    contentDescription = "Minus 1 min",
                                                    tint = textSub,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = CircleShape,
                                            color = iconBadgeBg,
                                            border = null,
                                            modifier = Modifier.clickable { onUpdateOffset(pKey, 1) }
                                        ) {
                                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Plus 1 min",
                                                    tint = primaryAccent,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SalatAdhanAudioSettingsCard(
    globalVoiceId: String,
    perPrayerSounds: Map<String, String>,
    volume: Int,
    isPlaying: Boolean,
    themeColors: ReadingThemeColors,
    colors: SalatColorPalette,
    isArabic: Boolean,
    onSelectGlobalVoice: (String) -> Unit,
    onSelectPrayerVoice: (String, String) -> Unit,
    onVolumeChange: (Int) -> Unit,
    onImportCustomAudio: (android.net.Uri, String?) -> Unit,
    onTogglePreview: (String?) -> Unit
) {
    val context = LocalContext.current
    var isVoiceDropdownExpanded by remember { mutableStateOf(false) }
    var isPerPrayerExpanded by remember { mutableStateOf(false) }
    var prayerSelectingAudio by remember { mutableStateOf<String?>(null) }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onImportCustomAudio(it, prayerSelectingAudio)
            prayerSelectingAudio = null
        }
    }

    val allSounds = remember(globalVoiceId, perPrayerSounds) {
        AdhanAudioRepository.getAllSounds(context)
    }

    val currentGlobalSound = remember(globalVoiceId) {
        AdhanAudioRepository.getSoundById(context, globalVoiceId)
    }

    val primaryAccent = colors.primaryAccent
    val cardBg = colors.settingsCardBg
    val innerCardBg = colors.settingsInnerCardBg
    val textTitle = colors.titleText
    val textSub = colors.subtext

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isArabic) "أصوات وتخصيص الأذان" else stringResource(R.string.salat_adhan_voice_title),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = textTitle
            )
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = cardBg,
            border = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Default Voice Selection Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "الصوت الافتراضي للأذان" else "Default Adhan Voice",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = textTitle,
                                fontSize = 13.5.sp
                            )
                        )
                        Text(
                            text = if (isArabic) "ملفات صوتية مدمجة دون اتصال بالإنترنت" else "Bundled offline high-fidelity recitation",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = textSub,
                                fontSize = 11.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = { onTogglePreview(globalVoiceId) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(primaryAccent.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Stop Adhan" else "Preview Adhan",
                            tint = primaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Voice Selector Pill
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isVoiceDropdownExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        color = innerCardBg,
                        border = null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(primaryAccent.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Audiotrack,
                                        contentDescription = null,
                                        tint = primaryAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = currentGlobalSound.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = textTitle,
                                            fontSize = 13.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = currentGlobalSound.subtitle,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = textSub,
                                            fontSize = 10.5.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select voice",
                                tint = textSub,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isVoiceDropdownExpanded,
                        onDismissRequest = { isVoiceDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(innerCardBg)
                    ) {
                        allSounds.forEach { sound ->
                            val isSelected = sound.id == globalVoiceId
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = sound.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) primaryAccent else textTitle,
                                                    fontSize = 13.sp
                                                )
                                            )
                                            Text(
                                                text = sound.subtitle,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = textSub,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = primaryAccent,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    onSelectGlobalVoice(sound.id)
                                    isVoiceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // 2. Pick Custom Audio from Device Storage Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            prayerSelectingAudio = null
                            audioPickerLauncher.launch("audio/*")
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = primaryAccent.copy(alpha = 0.08f),
                    border = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(primaryAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "اختيار ملف صوتي مخصص من جهازك" else "Pick Custom Audio File from Device",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = primaryAccent,
                                    fontSize = 12.5.sp
                                )
                            )
                            Text(
                                text = if (isArabic) "اختر ملف MP3 أو M4A أو WAV ليُشغَّل كأذان" else "Import your own MP3 / M4A / WAV file as Adhan",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = textSub,
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }
                }

                // 3. Adhan Volume Slider
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isArabic) "مستوى صوت الأذان" else "Adhan Playback Volume",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textTitle,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                        Text(
                            text = "$volume%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = primaryAccent,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Slider(
                        value = volume.toFloat(),
                        onValueChange = { onVolumeChange(it.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = primaryAccent,
                            activeTrackColor = primaryAccent,
                            inactiveTrackColor = innerCardBg
                        )
                    )
                }

                // 4. Per-Prayer Adhan Sound Customization Accordion
                Column(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPerPrayerExpanded = !isPerPrayerExpanded },
                        shape = RoundedCornerShape(12.dp),
                        color = innerCardBg,
                        border = null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 11.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "تخصيص أذان لكل صلاة على حدة" else "Per-Prayer Adhan Sound Selection",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = textTitle,
                                        fontSize = 12.5.sp
                                    )
                                )
                                Text(
                                    text = if (isArabic) "تعيين أصوات الأذان لكل صلاة بشكل مستقل" else "Assign distinct adhan voices for each prayer",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = textSub,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                            Icon(
                                imageVector = if (isPerPrayerExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isPerPrayerExpanded) "Collapse" else "Expand",
                                tint = primaryAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isPerPrayerExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val prayerList = listOf(
                                "Fajr" to (if (isArabic) "الفجر" else "Fajr"),
                                "Dhuhr" to (if (isArabic) "الظهر" else "Dhuhr"),
                                "Asr" to (if (isArabic) "العصر" else "Asr"),
                                "Maghrib" to (if (isArabic) "المغرب" else "Maghrib"),
                                "Isha" to (if (isArabic) "العشاء" else "Isha")
                            )

                            prayerList.forEach { (pKey, pDisplay) ->
                                val selectedSoundId = perPrayerSounds[pKey]
                                    ?: AdhanAudioRepository.getDefaultSoundIdForPrayer(pKey)
                                val soundObj = AdhanAudioRepository.getSoundById(context, selectedSoundId, pKey)

                                var dropdownOpen by remember { mutableStateOf(false) }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = innerCardBg,
                                    border = null,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { dropdownOpen = true }
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = pDisplay,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = primaryAccent,
                                                        fontSize = 13.sp
                                                    )
                                                )
                                            }
                                            Text(
                                                text = soundObj.title,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = textTitle,
                                                    fontSize = 11.5.sp
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            IconButton(
                                                onClick = { onTogglePreview(selectedSoundId) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                    contentDescription = "Preview",
                                                    tint = primaryAccent,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { dropdownOpen = true },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowDropDown,
                                                    contentDescription = "Change sound",
                                                    tint = textSub,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = dropdownOpen,
                                            onDismissRequest = { dropdownOpen = false },
                                            modifier = Modifier
                                                .fillMaxWidth(0.85f)
                                                .background(innerCardBg)
                                        ) {
                                            allSounds.forEach { sound ->
                                                val isChosen = sound.id == selectedSoundId
                                                DropdownMenuItem(
                                                    text = {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = sound.title,
                                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                                        fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                                                        color = if (isChosen) primaryAccent else textTitle,
                                                                        fontSize = 12.5.sp
                                                                    )
                                                                )
                                                                Text(
                                                                    text = sound.subtitle,
                                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                                        color = textSub,
                                                                        fontSize = 10.5.sp
                                                                    )
                                                                )
                                                            }
                                                            if (isChosen) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Check,
                                                                    contentDescription = null,
                                                                    tint = primaryAccent,
                                                                    modifier = Modifier.size(16.dp)
                                                                )
                                                            }
                                                        }
                                                    },
                                                    onClick = {
                                                        onSelectPrayerVoice(pKey, sound.id)
                                                        dropdownOpen = false
                                                    }
                                                )
                                            }

                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.FileUpload,
                                                            contentDescription = null,
                                                            tint = primaryAccent,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Text(
                                                            text = if (isArabic) "استيراد ملف صوتي مخصص لهذا الأذان..." else "Import custom audio for $pDisplay...",
                                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                                color = primaryAccent,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 12.sp
                                                            )
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    prayerSelectingAudio = pKey
                                                    dropdownOpen = false
                                                    audioPickerLauncher.launch("audio/*")
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
        }
    }
}

// =========================================================================
// TIMES CONTENT (UNIFIED CARD STYLING, CLEAN DIVIDERS & ALIGNED COLUMNS)
// =========================================================================

@Composable
private fun SalatTimesContent(
    viewModel: MainViewModel,
    themeColors: ReadingThemeColors,
    colors: SalatColorPalette,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activityResultRegistryOwner = LocalActivityResultRegistryOwner.current ?: remember(context) {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is ActivityResultRegistryOwner) {
                return@remember ctx
            }
            ctx = ctx.baseContext
        }
        null
    }

    if (activityResultRegistryOwner != null) {
        CompositionLocalProvider(LocalActivityResultRegistryOwner provides activityResultRegistryOwner) {
            SalatTimesContentInternal(
                viewModel = viewModel,
                themeColors = themeColors,
                colors = colors,
                onOpenSettings = onOpenSettings,
                modifier = modifier
            )
        }
    } else {
        SalatTimesContentInternal(
            viewModel = viewModel,
            themeColors = themeColors,
            colors = colors,
            onOpenSettings = onOpenSettings,
            modifier = modifier
        )
    }
}

@Composable
private fun SalatTimesContentInternal(
    viewModel: MainViewModel,
    themeColors: ReadingThemeColors,
    colors: SalatColorPalette,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prayerTimes by viewModel.prayerTimes.collectAsStateWithLifecycle()
    val completedPrayers by viewModel.completedPrayers.collectAsStateWithLifecycle()
    val selectedZone by viewModel.selectedPrayerZone.collectAsStateWithLifecycle()
    val selectedAuthority by viewModel.selectedAuthority.collectAsStateWithLifecycle()
    val isHanafi by viewModel.isHanafiAsr.collectAsStateWithLifecycle()
    val timersMap by viewModel.prayerNotificationTimers.collectAsStateWithLifecycle()
    val enabledMap by viewModel.prayerNotificationEnabled.collectAsStateWithLifecycle()
    val alertTypesMap by viewModel.prayerAlertTypes.collectAsStateWithLifecycle()
    val perPrayerAdhanSounds by viewModel.perPrayerAdhanSounds.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAthanAudioPreviewPlaying.collectAsStateWithLifecycle()
    val supplementaryTimes by viewModel.supplementaryPrayerTimes.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            viewModel.showToast(if (isArabic) "يرجى تمكين الإشعارات من إعدادات الهاتف لتلقي تنبيهات الصلاة." else "Please enable notifications in system settings to receive prayer alerts.")
        }
    }

    val triggerPermissionCheck = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val prefs = remember { context.getSharedPreferences("noor_app_preferences", Context.MODE_PRIVATE) }
    var isNoticePermanentlyDismissed by remember {
        mutableStateOf(prefs.getBoolean("salat_notice_never_show_again", false))
    }
    var isNoticeExpanded by rememberSaveable { mutableStateOf(false) }
    var isForbiddenExpanded by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Active Zone Card (Compact Single Surface Header)
        val normalDateFormatted = remember {
            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEE, d MMM", java.util.Locale.getDefault()))
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = colors.cardBackground,
            border = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top Line: Location & Authority (Full Width)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colors.iconBadgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Zone",
                            tint = colors.salatGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) selectedZone.arabicName else "${selectedZone.name}, ${selectedZone.country}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.titleText,
                                fontSize = 14.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${selectedAuthority.name} • ${if (isHanafi) stringResource(R.string.salat_hanafi_asr) else stringResource(R.string.salat_standard_asr)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = colors.subtext,
                                fontSize = 11.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Bottom Line: Normal Date + Hijri Date alongside completed badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = stringResource(R.string.salat_date_cd),
                            tint = colors.salatGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "$normalDateFormatted  •  ${stringResource(R.string.salat_sample_hijri_date)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = colors.linkTextColor,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = colors.linkBadgeBg,
                        border = null
                    ) {
                        Text(
                            text = stringResource(R.string.salat_completed_count, completedPrayers.size),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colors.salatGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Notice: Manual Time Adjustment (Compact Collapsible Banner)
        if (!isNoticePermanentlyDismissed) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(12.dp),
                color = colors.noticeBg,
                border = null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = colors.noticeTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isArabic) "تعديل مواقيت الصلاة" else "Adjust prayer times?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.noticeTextColor,
                                    fontSize = 13.5.sp
                                )
                            )
                        }

                        // Hide / Show button top right
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    isNoticeExpanded = !isNoticeExpanded
                                }
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = if (isNoticeExpanded) (if (isArabic) "إخفاء" else "Hide") else (if (isArabic) "عرض" else "Show"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = colors.noticeTextColor,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                )
                            )
                            Icon(
                                imageVector = if (isNoticeExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isNoticeExpanded) "Hide notice content" else "Show notice content",
                                tint = colors.noticeTextColor,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isNoticeExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isArabic)
                                    "يمكنك ضبط وتقديم أو تأخير دقائق كل صلاة يدوياً من الإعدادات لتطابق مسجدك بدقة."
                                else
                                    "If you see any discrepancy with your local mosque, you can manually fine-tune each prayer's minutes in Settings.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.noticeTextColor.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    lineHeight = 18.5.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isArabic) "عدم الإظهار مجدداً" else "Don't show again",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = colors.noticeTextColor,
                                        fontSize = 12.sp
                                    ),
                                    modifier = Modifier
                                        .clickable {
                                            isNoticePermanentlyDismissed = true
                                            prefs.edit().putBoolean("salat_notice_never_show_again", true).apply()
                                        }
                                        .padding(vertical = 4.dp, horizontal = 2.dp)
                                )

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = colors.cardBackground,
                                    border = null,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .clickable { onOpenSettings() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Text(
                                            text = if (isArabic) "تعديل الأوقات" else "Adjust Times",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = colors.noticeTextColor,
                                                fontSize = 12.5.sp
                                            )
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = null,
                                            tint = colors.noticeTextColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        var openDropdownPrayer by remember { mutableStateOf<String?>(null) }

        // Single Unified Card containing title, subtitle, and all prayer times together
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.cardBackground, RoundedCornerShape(16.dp))
                .zIndex(if (openDropdownPrayer != null) 20f else 1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                // Daily Prayer Timetable Header (inside the container)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.salat_daily_schedule_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.titleText,
                            fontSize = 15.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.salat_daily_schedule_sub),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.subtext,
                            fontSize = 13.sp
                        )
                    )
                }

                prayerTimes.forEachIndexed { index, prayer ->
                    val isChecked = completedPrayers.contains(prayer.name)
                    val isActionable = prayer.isPast || prayer.isCurrent
                    val offsetMin = timersMap[prayer.name] ?: 0
                    val isNotificationEnabled = enabledMap[prayer.name] ?: true
                    val isSunrise = prayer.name == "Sunrise"
                    val isCurrentActive = prayer.isCurrent && !isSunrise
                    val isDropdownOpen = openDropdownPrayer == prayer.name
                    val openUpward = true
                    val rowZIndex = if (isDropdownOpen) 100f else (if (openDropdownPrayer != null) 0f else 1f)

                    val prayerDisplayName = when (prayer.name) {
                        "Fajr" -> stringResource(R.string.prayer_fajr)
                        "Sunrise" -> stringResource(R.string.prayer_sunrise)
                        "Dhuhr" -> stringResource(R.string.prayer_dhuhr)
                        "Asr" -> stringResource(R.string.prayer_asr)
                        "Maghrib" -> stringResource(R.string.prayer_maghrib)
                        "Isha" -> stringResource(R.string.prayer_isha)
                        else -> prayer.name
                    }

                    val renderedPrayerName = if (isArabic) prayer.arabicName else prayerDisplayName

                    val nextIsActive = index + 1 < prayerTimes.size && prayerTimes[index + 1].isCurrent && prayerTimes[index + 1].name != "Sunrise"

                    // Prayer Row Container: Both active and inactive rows have the exact same width and height.
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(rowZIndex)
                            .padding(horizontal = 2.dp, vertical = 1.dp)
                    ) {
                        PrayerRowItem(
                            prayer = prayer,
                            renderedPrayerName = renderedPrayerName,
                            isChecked = isChecked,
                            isActionable = isActionable,
                            isSunrise = isSunrise,
                            offsetMin = offsetMin,
                            alertType = alertTypesMap[prayer.name] ?: (if (isSunrise) "Mute" else "Adhan"),
                            isArabic = isArabic,
                            themeColors = themeColors,
                            colors = colors,
                            onOpenSettings = {
                                openDropdownPrayer = prayer.name
                            },
                            onTogglePrayer = {
                                if (isActionable && !isSunrise) {
                                    viewModel.togglePrayerCompleted(prayer)
                                }
                            }
                        )
                    }

                    // Divider between rows (suppressed when touching active row to preserve clean rounded border)
                    if (index < prayerTimes.size - 1 && !isCurrentActive && !nextIsActive) {
                        HorizontalDivider(
                            color = colors.dividerColor.copy(alpha = 0.5f),
                            thickness = 0.6.dp,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                }

                // Voluntary & Night Timings (Duha, Islamic Midnight, Qiyam al-Layl)
                if (supplementaryTimes != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(
                        color = colors.dividerColor.copy(alpha = 0.7f),
                        thickness = 0.8.dp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isArabic) "مواقيت السنن والنوافل وقيام الليل" else stringResource(R.string.salat_supplementary_title),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.titleText,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Duha
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.linkBadgeBg,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isArabic) "صلاة الضحى" else stringResource(R.string.prayer_duha),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = colors.subtext,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = supplementaryTimes?.duhaTimeString ?: "--:--",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.titleText,
                                        fontSize = 13.5.sp
                                    )
                                )
                            }
                        }

                        // 2. Midnight
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.linkBadgeBg,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isArabic) "نصف الليل" else stringResource(R.string.prayer_midnight),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = colors.subtext,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = supplementaryTimes?.midnightTimeString ?: "--:--",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.titleText,
                                        fontSize = 13.5.sp
                                    )
                                )
                            }
                        }

                        // 3. Qiyam al-Layl
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.linkBadgeBg,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isArabic) "قيام الليل" else stringResource(R.string.prayer_qiyam),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = colors.subtext,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = supplementaryTimes?.qiyamTimeString ?: "--:--",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.titleText,
                                        fontSize = 13.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Forbidden Prayer Times Notice Card (Soft Red palette, collapsible)
        val isWarm = LocalAppThemeMode.current == AppThemeMode.WARM
        val softRedBg = when {
            isWarm -> Color(0xFFFDF2F0)
            themeColors.isDark -> DangerRedBgDark
            else -> Color(0xFFFEF2F2)
        }
        val dangerRedColor = when {
            isWarm -> Color(0xFFB84033)
            themeColors.isDark -> DangerRedDark
            else -> Color(0xFFDC2626)
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = RoundedCornerShape(16.dp),
            color = softRedBg,
            border = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = if (isArabic) "أوقات الكراهة" else "Forbidden Times",
                            tint = dangerRedColor,
                            modifier = Modifier.size(19.dp)
                        )
                        Text(
                            text = if (isArabic) "أوقات الكراهة وتحريم صلاة التطوع" else "Forbidden Prayer Times",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = dangerRedColor,
                                fontSize = 15.5.sp
                            )
                        )
                    }

                    // Hide / Show button top right
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                isForbiddenExpanded = !isForbiddenExpanded
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = if (isForbiddenExpanded) (if (isArabic) "إخفاء" else "Hide") else (if (isArabic) "عرض" else "Show"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = dangerRedColor,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                        Icon(
                            imageVector = if (isForbiddenExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isForbiddenExpanded) "Hide forbidden times" else "Show forbidden times",
                            tint = dangerRedColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isForbiddenExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val forbiddenTextColor = when {
                            isWarm -> Color(0xFF7A3A35)
                            themeColors.isDark -> themeColors.translationText
                            else -> Color(0xFF7F1D1D).copy(alpha = 0.9f)
                        }
                        Text(
                            text = if (isArabic)
                                "الأوقات المنهي عنها شرعاً والتي يكره أو يحرم فيها ابتداء صلوات النوافل والتطوع المطلق:"
                            else
                                "Specific periods of the day when offering voluntary (nafl) prayers is strictly forbidden or disliked in Islamic tradition:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = forbiddenTextColor,
                                fontSize = 13.sp,
                                lineHeight = 18.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val forbiddenList = if (isArabic) listOf(
                            "• وقت الشروق: يمتد من بداية طلوع قرص الشمس حتى ترتفع في الأفق قيد رمح (نحو 15 إلى 20 دقيقة بعد تمام شروق الشمس).",
                            "• وقت الاستواء: في منتصف النهار حينما تتعامد الشمس في كبد السماء حتى تميل وتزول إيذاناً بحلول وقت صلاة الظهر المباركة.",
                            "• وقت الغروب: في أواخر وقت العصر عند اصفرار قرص الشمس وتغير ضوئها حتى تغرب وتختفي كلياً ويدخل وقت أذان المغرب."
                        ) else listOf(
                            "• Sunrise: From dawn and sunrise until the sun has completely risen above the horizon, spanning approximately 15 to 20 minutes after the sunrise phase concludes.",
                            "• Zenith: At exact midday when the sun reaches its celestial peak, lasting until the sun begins to decline toward the Dhuhr prayer time.",
                            "• Sunset: During the late afternoon when the sun turns pale yellow until it has completely vanished below the horizon at the start of Maghrib."
                        )
                        forbiddenList.forEach { item ->
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = forbiddenTextColor.copy(alpha = 0.85f),
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        if (openDropdownPrayer != null) {
            val selectedPrayer = prayerTimes.find { it.name == openDropdownPrayer }
            if (selectedPrayer != null) {
                val isSunrise = selectedPrayer.name == "Sunrise"
                if (!isSunrise) {
                    val prayerSoundId = perPrayerAdhanSounds[selectedPrayer.name]
                        ?: AdhanAudioRepository.getDefaultSoundIdForPrayer(selectedPrayer.name)
                    PrayerRowSettingsSheet(
                        prayerName = selectedPrayer.name,
                        arabicName = selectedPrayer.arabicName,
                        timeString = selectedPrayer.timeString,
                        alertType = alertTypesMap[selectedPrayer.name] ?: "Adhan",
                        offsetMin = timersMap[selectedPrayer.name] ?: 0,
                        currentSoundId = prayerSoundId,
                        onDismiss = { openDropdownPrayer = null },
                        onSave = { finalAlert, finalOffset, finalSoundId ->
                            viewModel.setPrayerAlertType(selectedPrayer.name, finalAlert)
                            viewModel.setPrayerNotificationTimer(selectedPrayer.name, finalOffset)
                            viewModel.setPrayerAdhanSound(selectedPrayer.name, finalSoundId)
                            if (finalAlert == "Notification" || finalAlert == "Adhan") {
                                triggerPermissionCheck()
                            }
                            openDropdownPrayer = null
                        },
                        onImportCustomAudio = { uri ->
                            viewModel.importCustomAdhanAudio(uri, selectedPrayer.name)
                        },
                        onTogglePreview = { soundId ->
                            viewModel.toggleAthanAudioPreview(soundId)
                        },
                        isPreviewPlaying = isAudioPlaying,
                        colors = colors,
                        themeColors = themeColors,
                        isArabic = isArabic
                    )
                }
            }
        }
    }
}

// =========================================================================
// PRAYER ROW ITEM (INLINE SEAMLESS EXPAND NOTIFICATION PILL & REFINED TYPOGRAPHY)
// =========================================================================

@Composable
private fun PrayerRowItem(
    prayer: PrayerTime,
    renderedPrayerName: String,
    isChecked: Boolean,
    isActionable: Boolean,
    isSunrise: Boolean,
    offsetMin: Int,
    alertType: String,
    isArabic: Boolean,
    themeColors: ReadingThemeColors,
    colors: SalatColorPalette,
    onOpenSettings: () -> Unit,
    onTogglePrayer: () -> Unit
) {
    val salatGreen = colors.salatGreen
    val salatGrey = colors.salatGrey
    val salatGreyCircleBg = colors.salatGreyCircleBg
    val linkBadgeBg = colors.linkBadgeBg
    val linkTextColor = colors.linkTextColor

    val isMute = alertType == "Mute"
    val pillBgColor = if (!isMute) linkBadgeBg else colors.salatGreyCircleBg
    val pillContentColor = if (!isMute) linkTextColor else salatGrey

    val shortOffsetLabel = if (isMute) {
        if (isArabic) "صامت" else "Mute"
    } else {
        when (offsetMin) {
            -30 -> if (isArabic) "30 د" else "30 Min"
            -20 -> if (isArabic) "20 د" else "20 Min"
            -15 -> if (isArabic) "15 د" else "15 Min"
            -10 -> if (isArabic) "10 د" else "10 Min"
            -5 -> if (isArabic) "5 د" else "5 Min"
            0 -> if (isArabic) "في الوقت" else "Exact"
            else -> if (offsetMin < 0) "${-offsetMin} Min" else "${offsetMin} Min"
        }
    }

    val iconContainerColor = when {
        isChecked && !isSunrise -> salatGreen
        isSunrise -> colors.sunriseBg
        prayer.isCurrent && !isSunrise -> colors.iconBadgeBg
        else -> salatGreyCircleBg
    }

    val normalTextColor = colors.titleText

    val prayerTitleColor = when {
        prayer.isCurrent && !isSunrise -> salatGreen
        isSunrise -> colors.sunriseTextColor
        else -> normalTextColor
    }

    val prayerTimeColor = when {
        prayer.isCurrent && !isSunrise -> salatGreen
        isSunrise -> colors.sunriseTextColor
        else -> normalTextColor
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Checkbox/Icon + Name + Status Badge
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor)
                    .clickable(
                        enabled = isActionable && !isSunrise,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onTogglePrayer
                    ),
                contentAlignment = Alignment.Center
            ) {
                val uncheckedCheckColor = if (themeColors.isDark) salatGrey.copy(alpha = 0.45f) else Color(0xFF94A3B8).copy(alpha = 0.7f)

                when {
                    isChecked -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.salat_status_completed),
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    isSunrise -> {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = stringResource(R.string.prayer_sunrise),
                            tint = colors.sunriseTextColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    prayer.isCurrent -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.salat_status_active),
                            tint = salatGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = uncheckedCheckColor,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // Prayer Name + Status Badge
            Row(
                modifier = Modifier
                    .clickable(
                        enabled = isActionable && !isSunrise,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onTogglePrayer
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = renderedPrayerName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (prayer.isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                        color = prayerTitleColor,
                        fontSize = 14.5.sp
                    )
                )

                // Status Badge
                if (isChecked) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = colors.iconBadgeBg,
                        border = null
                    ) {
                        Text(
                            text = if (isArabic) "تم" else "Done",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = salatGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (prayer.isCurrent && !isSunrise) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = colors.iconBadgeBg,
                        border = null
                    ) {
                        Text(
                            text = if (isArabic) "الآن" else "Current",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = salatGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Right: Unified Alert & Timing Menu Pill + Right-Aligned Time
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isSunrise) {
                Surface(
                    modifier = Modifier
                        .width(76.dp)
                        .height(26.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onOpenSettings
                        ),
                    shape = RoundedCornerShape(50),
                    color = pillBgColor,
                    shadowElevation = 0.dp,
                    border = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (!isMute) {
                                if (alertType == "Adhan") Icons.Default.NotificationsActive else Icons.Default.Notifications
                            } else {
                                Icons.Default.NotificationsOff
                            },
                            contentDescription = if (!isMute) stringResource(R.string.salat_notification_active) else stringResource(R.string.salat_notification_muted),
                            tint = pillContentColor,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.5.dp))
                        Text(
                            text = shortOffsetLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = pillContentColor,
                                fontWeight = FontWeight.Normal,
                                fontSize = 10.5.sp
                            ),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(1.5.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Timing settings",
                            tint = pillContentColor,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Fixed-Width Right-Aligned Time Container
            Text(
                text = prayer.timeString,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (prayer.isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                    color = prayerTimeColor,
                    fontSize = 14.5.sp
                ),
                modifier = Modifier.width(52.dp)
            )
        }
    }
}

// =========================================================================
// PRAYER ROW SETTINGS MODAL BOTTOM SHEET (PREMIUM CARD & SHORTCUTS STYLE)
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrayerRowSettingsSheet(
    prayerName: String,
    arabicName: String,
    timeString: String,
    alertType: String,
    offsetMin: Int,
    currentSoundId: String,
    onDismiss: () -> Unit,
    onSave: (String, Int, String) -> Unit,
    onImportCustomAudio: (android.net.Uri) -> Unit,
    onTogglePreview: (String) -> Unit,
    isPreviewPlaying: Boolean,
    colors: SalatColorPalette,
    themeColors: ReadingThemeColors,
    isArabic: Boolean
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val primaryAccent = colors.primaryAccent
    val cardBg = colors.settingsCardBg
    val innerCardBg = colors.settingsInnerCardBg
    val textTitle = colors.titleText
    val textSub = colors.subtext
    val dividerColor = colors.dividerColor

    var tempAlertType by remember { mutableStateOf(alertType) }
    var tempOffsetMin by remember { mutableStateOf(offsetMin) }
    var tempSoundId by remember { mutableStateOf(currentSoundId) }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onImportCustomAudio(it)
            tempSoundId = AdhanAudioRepository.ID_CUSTOM
        }
    }

    val availableSounds = remember(prayerName) {
        AdhanAudioRepository.getAllSounds(context)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = innerCardBg,
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 14.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.5.dp)
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(dividerColor)
                )
            }
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Row (Fixed at Top)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val displayName = when (prayerName) {
                        "Fajr" -> stringResource(R.string.prayer_fajr)
                        "Dhuhr" -> stringResource(R.string.prayer_dhuhr)
                        "Asr" -> stringResource(R.string.prayer_asr)
                        "Maghrib" -> stringResource(R.string.prayer_maghrib)
                        "Isha" -> stringResource(R.string.prayer_isha)
                        else -> prayerName
                    }
                    val titleText = if (isArabic) {
                        "إعدادات صلاة $arabicName"
                    } else {
                        "$displayName Settings"
                    }
                    val subtitleText = if (isArabic) {
                        "تخصيص نوع التنبيه ووقت الإشعار التذكيري • $timeString"
                    } else {
                        "Customize alert type and pre-alert timing • $timeString"
                    }

                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = textTitle,
                            fontSize = 19.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = textSub,
                            fontSize = 13.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .background(cardBg, CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textTitle,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = dividerColor,
                thickness = 0.75.dp
            )

            // Scrollable Options Body (Guarantees Save/Cancel are always visible and accessible)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section 1: Alert Type with small text explanation
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isArabic) "نوع التنبيه عند وقت الصلاة" else "Alert Type at Prayer Time",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = textTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp
                        )
                    )

                    val alertOptions = listOf(
                        Triple(
                            "Mute",
                            if (isArabic) "صامت" else "Mute",
                            if (isArabic) "إيقاف جميع الأصوات والإشعارات لهذه الصلاة." else "Disable all audio and banner notifications for this prayer."
                        ),
                        Triple(
                            "Notification",
                            if (isArabic) "إشعار مرئي صامت" else "Notification",
                            if (isArabic) "تلقي إشعار مرئي بدون صوت أذان (مناسب للعمل والدراسة)." else "Receive a visual notification banner on time without audio (great for work)."
                        ),
                        Triple(
                            "Adhan",
                            if (isArabic) "صوت الأذان" else "Adhan",
                            if (isArabic) "تشغيل صوت الأذان كاملاً عند دخول وقت الصلاة الفعلي." else "Play the full beautiful Adhan sound at the exact prayer time."
                        )
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        alertOptions.forEach { (optionKey, optionLabel, optionDesc) ->
                            val isSelected = tempAlertType == optionKey
                            val optionBg = if (isSelected) {
                                primaryAccent.copy(alpha = 0.12f)
                            } else {
                                cardBg
                            }

                            val icon = when (optionKey) {
                                "Mute" -> Icons.Default.NotificationsOff
                                "Notification" -> Icons.Default.Notifications
                                else -> Icons.Default.NotificationsActive
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(optionBg)
                                    .clickable {
                                        tempAlertType = optionKey
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) primaryAccent else textSub,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = optionLabel,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                           color = if (isSelected) primaryAccent else textTitle,
                                           fontWeight = FontWeight.Bold,
                                           fontSize = 14.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = optionDesc,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = textSub,
                                            fontSize = 11.5.sp,
                                            lineHeight = 15.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) primaryAccent else textSub.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 2: Adhan Voice Selection (Always displayed; greyed out when Notification or Mute is chosen)
                val isAdhanActive = tempAlertType == "Adhan"

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "صوت الأذان لهذه الصلاة" else "Adhan Sound for this Salat",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = if (isAdhanActive) textTitle else colors.salatGrey,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                )
                            )
                            Text(
                                text = if (isAdhanActive) {
                                    if (isArabic) "أصوات محلية بدون إنترنت أو استيراد ملف صوتي مخصص" else "Offline bundled reciters or import custom device audio"
                                } else {
                                    if (isArabic) "غير مفعّل • يتطلب اختيار (صوت الأذان) في الأعلى" else "Inactive • Requires selecting 'Adhan' alert above"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isAdhanActive) textSub else colors.salatGrey.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        if (!isAdhanActive) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = colors.salatGreyCircleBg,
                                border = null
                            ) {
                                Text(
                                    text = if (isArabic) "غير نشط" else "Disabled",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = colors.salatGrey,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 10.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = colors.linkBadgeBg,
                                border = null
                            ) {
                                Text(
                                    text = if (isArabic) "مفعّل" else "Active",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = colors.linkTextColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableSounds.forEach { sound ->
                            val isChosen = tempSoundId == sound.id
                            val soundBg = if (isAdhanActive) {
                                if (isChosen) primaryAccent.copy(alpha = 0.12f) else cardBg
                            } else {
                                colors.salatGreyCircleBg.copy(alpha = 0.6f)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(soundBg)
                                    .clickable {
                                        tempSoundId = sound.id
                                        if (!isAdhanActive) {
                                            tempAlertType = "Adhan"
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = sound.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isChosen && isAdhanActive) FontWeight.Bold else FontWeight.Normal,
                                            color = if (!isAdhanActive) {
                                                colors.salatGrey
                                            } else if (isChosen) {
                                                primaryAccent
                                            } else {
                                                textTitle
                                            },
                                            fontSize = 13.5.sp
                                        )
                                    )
                                    Text(
                                        text = sound.subtitle,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isAdhanActive) textSub else colors.salatGrey.copy(alpha = 0.7f),
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                // Preview Button and Selection Checkmark
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (!isAdhanActive) {
                                                    colors.salatGreyCircleBg
                                                } else if (isPreviewPlaying && isChosen) {
                                                    colors.linkTextColor
                                                } else {
                                                    colors.linkBadgeBg
                                                }
                                            )
                                            .clickable(enabled = isAdhanActive) { onTogglePreview(sound.id) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isPreviewPlaying && isChosen && isAdhanActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                                            contentDescription = "Preview",
                                            tint = if (!isAdhanActive) {
                                                colors.salatGrey.copy(alpha = 0.45f)
                                            } else if (isPreviewPlaying && isChosen) {
                                                Color.White
                                            } else {
                                                colors.linkTextColor
                                            },
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (!isAdhanActive) {
                                                    if (isChosen) colors.salatGrey.copy(alpha = 0.35f) else Color.Transparent
                                                } else if (isChosen) {
                                                    primaryAccent
                                                } else {
                                                    textSub.copy(alpha = 0.25f)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isChosen) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = if (isAdhanActive) Color.White else colors.salatGrey,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Pick Custom Audio File from Storage
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (!isAdhanActive) {
                                        tempAlertType = "Adhan"
                                    }
                                    audioPickerLauncher.launch("audio/*")
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isAdhanActive) cardBg else colors.salatGreyCircleBg.copy(alpha = 0.6f),
                            border = null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isAdhanActive) colors.linkBadgeBg else colors.salatGrey.copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FileUpload,
                                        contentDescription = null,
                                        tint = if (isAdhanActive) colors.linkTextColor else colors.salatGrey,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isArabic) "اختيار ملف صوتي مخصص من جهازك" else "Pick Custom Audio File from Device",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isAdhanActive) textTitle else colors.salatGrey,
                                            fontSize = 12.5.sp
                                        )
                                    )
                                    Text(
                                        text = if (isArabic) "استيراد ملف صوتي خاص بك لهذه الصلاة" else "Import your own audio file for this prayer",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isAdhanActive) textSub else colors.salatGrey.copy(alpha = 0.7f),
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 3: Pre-Prayer Preparation Alert
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "التنبيه التحضيري المسبق" else "Pre-Prayer Preparation Alert",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = textTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            )
                        )

                        if (tempOffsetMin < 0) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = colors.linkBadgeBg,
                                border = null
                            ) {
                                Text(
                                    text = if (isArabic) "إشعار مسبق قبل ${-tempOffsetMin} د" else "${-tempOffsetMin}m Early Alert",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = colors.linkTextColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = cardBg,
                                border = null
                            ) {
                                Text(
                                    text = if (isArabic) "بدون تنبيه مسبق" else "No Pre-Alert",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = textSub,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Notice Card Placed ABOVE Pills with Deep Explanation
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = colors.noticeBg,
                        border = null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = colors.noticeTextColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isArabic) "كيف تعمل تنبيهات الصلاة والأذان؟" else "How Prayer Alerts & Adhan Work",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = colors.noticeTextColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    )
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // 1. Main exact time alert
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.noticeTextColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = if (isArabic) {
                                            "وقت الصلاة الفعلي ($timeString): سيبدأ ${if (tempAlertType == "Adhan") "صوت الأذان المختار" else if (tempAlertType == "Notification") "إشعار الصلاة" else "الوضع الصامت"} بدقة عند حلول وقت الصلاة تماماً."
                                        } else {
                                            "Exact Prayer Time ($timeString): Your alert choice (${if (tempAlertType == "Adhan") "Adhan Audio" else if (tempAlertType == "Notification") "Notification" else "Muted"}) will ALWAYS ring at the exact prayer time."
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = textTitle,
                                            fontSize = 11.5.sp,
                                            lineHeight = 16.5.sp
                                        )
                                    )
                                }

                                // 2. Advance reminder
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.noticeTextColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = if (isArabic) {
                                            "التنبيه التحضيري المسبق (اختياري): إشعار تذكيري هادئ قبل الصلاة (٥ إلى ٣٠ دقيقة) لتتمكن من الوضوء والاستعداد أو الذهاب للمسجد مبكراً دون عجلة. اختر 'بدون' إذا كنت تفضل التنبيه عند وقت الصلاة فقط."
                                        } else {
                                            "Early Preparation Alert (Optional): A gentle advance notification before prayer (5–30 min) giving you time for wudu, preparation, or walking to the mosque. Choose 'Off (None)' to only get alerts at exact prayer time."
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = textTitle,
                                            fontSize = 11.5.sp,
                                            lineHeight = 16.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Timing Offset Pills: Option '0' is labeled Off/None with link color for unchosen pills
                    val timingOptions = listOf(
                        0 to (if (isArabic) "بدون (معطل)" else "Off (None)"),
                        -5 to (if (isArabic) "قبل ٥ د" else "-5 min"),
                        -10 to (if (isArabic) "قبل ١٠ د" else "-10 min"),
                        -15 to (if (isArabic) "قبل ١٥ د" else "-15 min"),
                        -20 to (if (isArabic) "قبل ٢٠ د" else "-20 min"),
                        -30 to (if (isArabic) "قبل ٣٠ د" else "-30 min")
                    )

                    val scrollState = rememberScrollState()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        timingOptions.forEach { (offset, optionLabel) ->
                            val isMute = tempAlertType == "Mute"
                            val isSelected = !isMute && offset == tempOffsetMin
                            val pillBg = if (isSelected) {
                                primaryAccent
                            } else {
                                colors.linkBadgeBg
                            }
                            val pillContentColor = if (isSelected) {
                                Color.White
                            } else {
                                colors.linkTextColor
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(pillBg)
                                    .clickable(enabled = !isMute) {
                                        tempOffsetMin = offset
                                    }
                                    .padding(horizontal = 16.dp, vertical = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = optionLabel,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isMute) pillContentColor.copy(alpha = 0.4f) else pillContentColor,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Fixed Bottom Action Buttons Section (Always visible on screen, never cut off)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(innerCardBg)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                HorizontalDivider(
                    color = dividerColor,
                    thickness = 0.75.dp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(0.35f)
                            .height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            containerColor = cardBg,
                            contentColor = textTitle
                        ),
                        border = null
                    ) {
                        Text(
                            text = if (isArabic) "إلغاء" else "Cancel",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }

                    androidx.compose.material3.Button(
                        onClick = {
                            onSave(tempAlertType, tempOffsetMin, tempSoundId)
                        },
                        modifier = Modifier
                            .weight(0.65f)
                            .height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = primaryAccent,
                            contentColor = Color.White
                        ),
                        border = null
                    ) {
                        Text(
                            text = if (isArabic) "حفظ التغييرات" else "Save Settings",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalatCalendarModalSheet(
    onDismiss: () -> Unit,
    viewModel: MainViewModel,
    themeColors: ReadingThemeColors,
    colors: SalatColorPalette
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    val selectedZone by viewModel.selectedPrayerZone.collectAsStateWithLifecycle()
    val selectedAuthority by viewModel.selectedAuthority.collectAsStateWithLifecycle()
    val isHanafiAsr by viewModel.isHanafiAsr.collectAsStateWithLifecycle()
    val minuteOffsets by viewModel.prayerManualMinuteOffsets.collectAsStateWithLifecycle()
    val hijriOffset by viewModel.hijriAdjustmentDays.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    var currentYearMonth by remember { mutableStateOf(java.time.YearMonth.now()) }
    val daysInMonth = remember(currentYearMonth) { currentYearMonth.lengthOfMonth() }
    
    val monthPrayerTimes = remember(currentYearMonth, selectedZone, selectedAuthority, isHanafiAsr, minuteOffsets) {
        (1..daysInMonth).map { day ->
            val date = java.time.LocalDate.of(currentYearMonth.year, currentYearMonth.monthValue, day)
            val times = viewModel.repository.calculatePrayerTimes(
                zone = selectedZone,
                authority = selectedAuthority,
                isHanafiAsr = isHanafiAsr,
                date = date,
                minuteOffsets = minuteOffsets
            )
            date to times
        }
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.pageBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = 12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isArabic) "مواقيت الصلاة الشهرية" else "MONTHLY TIMETABLE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.salatGreen,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = if (isArabic) selectedZone.arabicName else selectedZone.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.titleText
                        )
                    )
                }
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .background(colors.activeRowBg, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.titleText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            HorizontalDivider(color = colors.cardBorder, thickness = 1.dp)
            
            // Month Switcher Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val nowMonth = remember { java.time.YearMonth.now() }
                val maxMonth = remember { nowMonth.plusMonths(1) }
                val canGoPrev = currentYearMonth.isAfter(nowMonth)
                val canGoNext = currentYearMonth.isBefore(maxMonth)

                IconButton(
                    onClick = { if (canGoPrev) currentYearMonth = currentYearMonth.minusMonths(1) },
                    enabled = canGoPrev,
                    modifier = Modifier.background(
                        if (canGoPrev) colors.activeRowBg else colors.activeRowBg.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Previous Month",
                        tint = if (canGoPrev) colors.titleText else colors.titleText.copy(alpha = 0.4f),
                        modifier = Modifier.graphicsLayer(rotationZ = 90f)
                    )
                }
                
                Text(
                    text = currentYearMonth.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault()) + " " + currentYearMonth.year,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.titleText
                    )
                )
                
                IconButton(
                    onClick = { if (canGoNext) currentYearMonth = currentYearMonth.plusMonths(1) },
                    enabled = canGoNext,
                    modifier = Modifier.background(
                        if (canGoNext) colors.activeRowBg else colors.activeRowBg.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Next Month",
                        tint = if (canGoNext) colors.titleText else colors.titleText.copy(alpha = 0.4f),
                        modifier = Modifier.graphicsLayer(rotationZ = -90f)
                    )
                }
            }
            
            // Spreadsheet Table
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(colors.cardBackground, RoundedCornerShape(24.dp))
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(24.dp))
            ) {
                val sharedHorizontalScrollState = rememberScrollState()
                
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Row with Sticky Date Column on the left
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.activeRowBg, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Static sticky Date Header
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .fillMaxHeight()
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            TableCellHeader(text = if (isArabic) "التاريخ" else "Date", colors = colors)
                        }
                        
                        // Vertical divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(colors.cardBorder)
                        )
                        
                        // Horizontally scrollable prayer headers (completely spelled out with comfortable width)
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .horizontalScroll(sharedHorizontalScrollState)
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCellHeader(text = if (isArabic) "الفجر" else "Fajr", colors = colors, modifier = Modifier.width(85.dp))
                            TableCellHeader(text = if (isArabic) "الشروق" else "Sunrise", colors = colors, modifier = Modifier.width(85.dp))
                            TableCellHeader(text = if (isArabic) "الظهر" else "Dhuhr", colors = colors, modifier = Modifier.width(85.dp))
                            TableCellHeader(text = if (isArabic) "العصر" else "Asr", colors = colors, modifier = Modifier.width(85.dp))
                            TableCellHeader(text = if (isArabic) "المغرب" else "Maghrib", colors = colors, modifier = Modifier.width(85.dp))
                            TableCellHeader(text = if (isArabic) "العشاء" else "Isha", colors = colors, modifier = Modifier.width(85.dp))
                        }
                    }
                    
                    HorizontalDivider(color = colors.cardBorder)
                    
                    // Scrollable vertical data container
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        (1..daysInMonth).forEach { index ->
                            val dayNum = index
                            val (date, times) = monthPrayerTimes[index - 1]
                            val isToday = date == java.time.LocalDate.now()
                            
                            val dayOfWeek = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())
                            val hijriStr = getHijriDateStringForCalendar(date, hijriOffset, isArabic)
                            
                            Surface(
                                color = if (isToday) colors.salatGreen.copy(alpha = 0.12f) else Color.Transparent,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(IntrinsicSize.Min),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Left sticky part (Date cell)
                                        Box(
                                            modifier = Modifier
                                                .width(90.dp)
                                                .fillMaxHeight()
                                                .background(if (isToday) colors.salatGreen.copy(alpha = 0.12f) else colors.cardBackground)
                                                .padding(vertical = 8.dp, horizontal = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = "$dayNum",
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isToday) colors.salatGreen else colors.titleText
                                                        )
                                                    )
                                                    Text(
                                                        text = dayOfWeek,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = colors.subtext,
                                                            fontSize = 10.sp
                                                        )
                                                    )
                                                }
                                                Text(
                                                    text = hijriStr.substringBefore(" AH").substringBefore(" هـ").substringAfter(" "),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = colors.subtext,
                                                        fontSize = 8.5.sp
                                                    ),
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                        
                                        // Vertical separation line
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .fillMaxHeight()
                                                .background(colors.cardBorder)
                                        )
                                        
                                        // Right part (horizontally scrollable prayer times)
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .horizontalScroll(sharedHorizontalScrollState)
                                                .padding(vertical = 8.dp, horizontal = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TableCell(text = times.getOrNull(0)?.timeString ?: "", isNext = times.getOrNull(0)?.isNext == true, colors = colors, modifier = Modifier.width(85.dp))
                                            TableCell(text = times.getOrNull(1)?.timeString ?: "", isNext = times.getOrNull(1)?.isNext == true, colors = colors, modifier = Modifier.width(85.dp))
                                            TableCell(text = times.getOrNull(2)?.timeString ?: "", isNext = times.getOrNull(2)?.isNext == true, colors = colors, modifier = Modifier.width(85.dp))
                                            TableCell(text = times.getOrNull(3)?.timeString ?: "", isNext = times.getOrNull(3)?.isNext == true, colors = colors, modifier = Modifier.width(85.dp))
                                            TableCell(text = times.getOrNull(4)?.timeString ?: "", isNext = times.getOrNull(4)?.isNext == true, colors = colors, modifier = Modifier.width(85.dp))
                                            TableCell(text = times.getOrNull(5)?.timeString ?: "", isNext = times.getOrNull(5)?.isNext == true, colors = colors, modifier = Modifier.width(85.dp))
                                        }
                                    }
                                    
                                    HorizontalDivider(
                                        color = colors.cardBorder.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TableCellHeader(
    text: String,
    colors: SalatColorPalette,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = colors.subtext,
            fontSize = 11.sp
        ),
        modifier = modifier,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

@Composable
private fun TableCell(
    text: String,
    isNext: Boolean,
    colors: SalatColorPalette,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
            color = if (isNext) colors.salatGreen else colors.titleText,
            fontSize = 12.sp
        ),
        modifier = modifier,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

private fun getHijriDateStringForCalendar(localDate: java.time.LocalDate, offsetDays: Int, isArabic: Boolean): String {
    return try {
        val adjustedLocalDate = localDate.plusDays(offsetDays.toLong())
        val hijriDate = java.time.chrono.HijrahDate.from(adjustedLocalDate)
        val month = hijriDate.get(java.time.temporal.ChronoField.MONTH_OF_YEAR)
        val day = hijriDate.get(java.time.temporal.ChronoField.DAY_OF_MONTH)
        val year = hijriDate.get(java.time.temporal.ChronoField.YEAR)
        
        val monthNameEn = when (month) {
            1 -> "Muharram"
            2 -> "Safar"
            3 -> "Rabi' al-Awwal"
            4 -> "Rabi' ath-Thani"
            5 -> "Jumada al-Awwal"
            6 -> "Jumada ath-Thani"
            7 -> "Rajab"
            8 -> "Sha'ban"
            9 -> "Ramadan"
            10 -> "Shawwal"
            11 -> "Dhu al-Qi'dah"
            12 -> "Dhu al-Hijjah"
            else -> "Ramadan"
        }
        
        val monthNameAr = when (month) {
            1 -> "محرم"
            2 -> "صفر"
            3 -> "ربيع الأول"
            4 -> "ربيع الثاني"
            5 -> "جمادى الأولى"
            6 -> "جمادى الآخرة"
            7 -> "رجب"
            8 -> "شعبان"
            9 -> "رمضان"
            10 -> "شوال"
            11 -> "ذو القعدة"
            12 -> "ذو الحجة"
            else -> "رمضان"
        }
        
        if (isArabic) {
            "$day $monthNameAr $year هـ"
        } else {
            "$day $monthNameEn $year AH"
        }
    } catch (e: Exception) {
        if (isArabic) "١٤ رمضان ١٤٤٥ هـ" else "14 Ramadan 1445 AH"
    }
}
