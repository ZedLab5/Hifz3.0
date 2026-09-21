package com.example.ui.settings

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import com.example.ui.NoorDestination
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.theme.BorderDividerDark
import com.example.ui.theme.BorderDividerLight
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.HeaderGradientLight
import com.example.ui.theme.NoorTopBarGradient
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.GoldTintBgLight
import com.example.ui.theme.GoldTintBgDark
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.ReadingThemes
import com.example.ui.theme.SurfaceElevatedLight
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.components.NoorTopBar
import androidx.compose.material3.Scaffold
import com.example.widget.PrayerWidgetReceiver

// Palette tokens matching the centralized Salat settings design system
private val SalatEmeraldPrimary = Color(0xFF107C41)
private val SalatBadgeBg = Color(0xFFEDF2F7)
private val SalatBadgeText = Color(0xFF2A4365)
private val SalatDivider = Color(0xFFECEFF1)
private val SalatCardBg = Color(0xFFF6F8F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val appThemeMode by viewModel.appThemeMode.collectAsStateWithLifecycle()
    val themeColors = remember(appThemeMode) {
        when (appThemeMode) {
            AppThemeMode.LIGHT -> ReadingThemes.MadaniCrisp
            AppThemeMode.WARM -> ReadingThemes.SepiaParchment
            AppThemeMode.DARK -> ReadingThemes.ObsidianNight
        }
    }

    val showArabicSecondary by viewModel.showArabicSecondaryText.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)
    val morningAzkarNotif by viewModel.morningEveningAzkarNotification.collectAsStateWithLifecycle()
    val dailyAyahNotif by viewModel.dailyAyahNotification.collectAsStateWithLifecycle()
    val qazaNotif by viewModel.qazaReminderNotification.collectAsStateWithLifecycle()
    val isStreakTrackingEnabled by viewModel.isStreakTrackingEnabled.collectAsStateWithLifecycle()
    val vibrateAdhan by viewModel.vibrationOnAdhan.collectAsStateWithLifecycle()
    val adhanVolume by viewModel.adhanSoundVolume.collectAsStateWithLifecycle()
    val isDndReadingEnabled by viewModel.isDndReadingEnabled.collectAsStateWithLifecycle()

    // Independent expansion state for expandable settings rows
    var showDndPermissionDialog by remember { mutableStateOf(false) }
    var showLanguageSelector by remember { mutableStateOf(false) }
    var isCalcExpanded by remember { mutableStateOf(false) }
    var isWidgetExpanded by remember { mutableStateOf(false) }
    var isContactExpanded by remember { mutableStateOf(false) }
    var isShareExpanded by remember { mutableStateOf(false) }
    var isSocialExpanded by remember { mutableStateOf(false) }
    var isPrivacyExpanded by remember { mutableStateOf(false) }
    var isAboutExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NoorTopBar(
                title = stringResource(R.string.settings_title),
                subtitle = stringResource(R.string.settings_subtitle),
                onBackClick = onNavigateBack,
                backContentDescription = stringResource(R.string.action_back),
                isDark = isDarkMode,
                themeColors = themeColors
            )
        },
        containerColor = if (isDarkMode) themeColors.background else Color.White,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                // ==========================================
                // SECTION 1: APPEARANCE & LANGUAGE
                // ==========================================
                item(key = "section_appearance") {
                    SettingsSection(
                        title = "APPEARANCE & LANGUAGE",
                        themeColors = themeColors
                    ) {
                        // 1. Three Theme Tabs (Light, Warm, Dark)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = when (appThemeMode) {
                                            AppThemeMode.LIGHT -> Icons.Default.LightMode
                                            AppThemeMode.WARM -> Icons.Default.WbSunny
                                            AppThemeMode.DARK -> Icons.Default.DarkMode
                                        },
                                        contentDescription = null,
                                        tint = themeColors.accent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Theme",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = themeColors.arabicText,
                                            fontSize = 15.sp
                                        )
                                    )
                                }
                                Text(
                                    text = when (appThemeMode) {
                                        AppThemeMode.LIGHT -> "Light (Crisp)"
                                        AppThemeMode.WARM -> "Warm (Parchment)"
                                        AppThemeMode.DARK -> "Dark (Obsidian)"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeColors.accent,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            // 3 Tabs row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDarkMode) themeColors.background else SalatBadgeBg)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val themeOptions = listOf(
                                    Triple(AppThemeMode.LIGHT, "Light", Icons.Default.LightMode),
                                    Triple(AppThemeMode.WARM, "Warm", Icons.Default.WbSunny),
                                    Triple(AppThemeMode.DARK, "Dark", Icons.Default.DarkMode)
                                )
                                themeOptions.forEach { (mode, label, icon) ->
                                    val isSelected = appThemeMode == mode
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isSelected) (if (isDarkMode) themeColors.surface else Color.White) else Color.Transparent
                                            )
                                            .clickable {
                                                viewModel.setAppThemeMode(mode)
                                            }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (isSelected) (if (isDarkMode) themeColors.accent else SalatEmeraldPrimary) else themeColors.translationText,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) (if (isDarkMode) themeColors.arabicText else SalatEmeraldPrimary) else themeColors.translationText,
                                                    fontSize = 13.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Primary App Language Selector
                        SettingsExpandableRow(
                            icon = Icons.Default.Language,
                            title = stringResource(R.string.settings_primary_language),
                            subtitle = appLanguage,
                            isExpanded = showLanguageSelector,
                            onToggleExpand = { showLanguageSelector = !showLanguageSelector },
                            themeColors = themeColors
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val languages = listOf(
                                    "English" to "English (Default)",
                                    "Arabic" to "العربية (Arabic)",
                                    "French" to "Français (French)",
                                    "Urdu" to "اردو (Urdu)",
                                    "Indonesian" to "Bahasa Indonesia",
                                    "Turkish" to "Türkçe (Turkish)"
                                )

                                languages.forEach { (code, label) ->
                                    val isSelected = appLanguage == code
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.setAppLanguage(code)
                                                showLanguageSelector = false
                                            },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) {
                                            if (isDarkMode) themeColors.border else SalatEmeraldPrimary.copy(alpha = 0.12f)
                                        } else {
                                            if (isDarkMode) themeColors.background else SalatCardBg
                                        },
                                        border = null
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) (if (isDarkMode) themeColors.accent else SalatEmeraldPrimary) else themeColors.arabicText,
                                                    fontSize = 13.sp
                                                )
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = if (isDarkMode) themeColors.accent else SalatEmeraldPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 3. Show Arabic Script Secondary Layer
                        SettingsToggleRow(
                            icon = Icons.Default.AutoStories,
                            title = stringResource(R.string.settings_show_arabic_secondary),
                            subtitle = stringResource(R.string.settings_show_arabic_desc),
                            checked = showArabicSecondary,
                            onCheckedChange = { viewModel.toggleArabicSecondaryText(it) },
                            themeColors = themeColors
                        )

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 4. Do Not Disturb Reading & Worship Mode
                        SettingsToggleRow(
                            icon = Icons.Default.NotificationsOff,
                            title = if (isArabic) "وضع عدم الإزعاج أثناء القراءة" else "Do Not Disturb Reading Mode",
                            subtitle = if (isArabic) "كتم الإشعارات والمكالمات ورسائل التواصل الاجتماعي تلقائياً أثناء استخدام القرآن والأذكار والأدعية والحفظ والتسبيح." else "Automatically silence calls, messages, and social notifications while reading Quran, Azkar, Duas, Hifz, or Tasbih.",
                            checked = isDndReadingEnabled,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    if (viewModel.isNotificationPolicyAccessGranted(context)) {
                                        viewModel.setDndReadingEnabled(true)
                                    } else {
                                        showDndPermissionDialog = true
                                    }
                                } else {
                                    viewModel.setDndReadingEnabled(false)
                                    viewModel.disableDnd(context)
                                }
                            },
                            themeColors = themeColors
                        )
                    }
                }

                // ==========================================
                // SECTION 2: NOTIFICATIONS & AUDIO
                // ==========================================
                item(key = "section_notifications_audio") {
                    SettingsSection(
                        title = "NOTIFICATIONS & AUDIO",
                        themeColors = themeColors
                    ) {
                        // 1. Azkar Notifications
                        SettingsToggleRow(
                            icon = Icons.Default.WbSunny,
                            title = stringResource(R.string.settings_notif_azkar),
                            subtitle = stringResource(R.string.settings_notif_azkar_desc),
                            checked = morningAzkarNotif,
                            onCheckedChange = { viewModel.toggleMorningEveningAzkarNotification() },
                            themeColors = themeColors
                        )

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 2. Daily Ayah Reminder
                        SettingsToggleRow(
                            icon = Icons.Default.MenuBook,
                            title = stringResource(R.string.settings_notif_daily_ayah),
                            subtitle = stringResource(R.string.settings_notif_ayah_desc),
                            checked = dailyAyahNotif,
                            onCheckedChange = { viewModel.toggleDailyAyahNotification() },
                            themeColors = themeColors
                        )

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 3. Qaza Prayer Reminder
                        SettingsToggleRow(
                            icon = Icons.Default.Schedule,
                            title = stringResource(R.string.settings_notif_qaza),
                            subtitle = stringResource(R.string.settings_notif_qaza_desc),
                            checked = qazaNotif,
                            onCheckedChange = { viewModel.toggleQazaReminderNotification() },
                            themeColors = themeColors
                        )

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 4. Spiritual Streaks & Consistency Reminders
                        SettingsToggleRow(
                            icon = Icons.Default.Timeline,
                            title = if (isArabic) "سلسلة الالتزام والنشاط اليومي" else "Spiritual Streaks & Consistency",
                            subtitle = if (isArabic) "متابعة السلسلة وخريطة الالتزام اليومية. يمكنك إيقافها للحصول على تجربة عبادة هادئة بدون عدادات." else "Track daily streaks and activity heatmaps. Turn off anytime for a pressure-free worship experience.",
                            checked = isStreakTrackingEnabled,
                            onCheckedChange = { viewModel.toggleStreakTracking(it) },
                            themeColors = themeColors
                        )

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 5. Vibration on Adhan
                        SettingsToggleRow(
                            icon = Icons.Default.Vibration,
                            title = stringResource(R.string.settings_notif_vibrate_adhan),
                            subtitle = stringResource(R.string.settings_notif_vibrate_desc),
                            checked = vibrateAdhan,
                            onCheckedChange = { viewModel.toggleVibrationOnAdhan() },
                            themeColors = themeColors
                        )

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 5. Adhan Sound Volume
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
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
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isDarkMode) SecondaryGoldDark.copy(alpha = 0.18f) else SalatEmeraldPrimary.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = null,
                                            tint = if (isDarkMode) SecondaryGoldDark else SalatEmeraldPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = stringResource(R.string.settings_adhan_volume),
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = themeColors.arabicText
                                            )
                                        )
                                        Text(
                                            text = "Sound level for prayer calls",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = themeColors.translationText,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                    }
                                }
                                Text(
                                    text = "$adhanVolume%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkMode) themeColors.accent else SalatEmeraldPrimary,
                                        fontSize = 13.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Slider(
                                value = adhanVolume.toFloat(),
                                onValueChange = { viewModel.setAdhanSoundVolume(it.toInt()) },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = if (isDarkMode) themeColors.accent else SalatEmeraldPrimary,
                                    activeTrackColor = if (isDarkMode) themeColors.accent else SalatEmeraldPrimary,
                                    inactiveTrackColor = if (isDarkMode) themeColors.border else SalatDivider
                                )
                            )
                        }
                    }
                }

                // ==========================================
                // SECTION 3: PRAYER & WIDGETS
                // ==========================================
                item(key = "section_prayer_widgets") {
                    SettingsSection(
                        title = "PRAYER & WIDGETS",
                        themeColors = themeColors
                    ) {
                        // 1. Prayer Calculation Method
                        SettingsExpandableRow(
                            icon = Icons.Default.Calculate,
                            title = stringResource(R.string.settings_section_calc),
                            subtitle = stringResource(R.string.settings_calc_sub),
                            trailingBadge = "MWL",
                            isExpanded = isCalcExpanded,
                            onToggleExpand = { isCalcExpanded = !isCalcExpanded },
                            themeColors = themeColors
                        ) {
                            Text(
                                text = stringResource(R.string.settings_calc_details),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = themeColors.translationText,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            )
                        }

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 2. Home Screen Widget
                        SettingsExpandableRow(
                            icon = Icons.Default.Widgets,
                            title = stringResource(R.string.settings_section_widget),
                            subtitle = stringResource(R.string.settings_widget_sub),
                            isExpanded = isWidgetExpanded,
                            onToggleExpand = { isWidgetExpanded = !isWidgetExpanded },
                            themeColors = themeColors
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = stringResource(R.string.settings_widget_desc),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeColors.translationText,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )
                                )

                                val appWidgetManager = remember { AppWidgetManager.getInstance(context) }
                                val isPinSupported = remember {
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                                            appWidgetManager != null &&
                                            appWidgetManager.isRequestPinAppWidgetSupported
                                }

                                if (isPinSupported) {
                                    Button(
                                        onClick = {
                                            val provider = ComponentName(context, PrayerWidgetReceiver::class.java)
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                try {
                                                    val callbackIntent = Intent(context, PrayerWidgetReceiver::class.java).apply {
                                                        action = "com.example.ACTION_WIDGET_PINNED"
                                                    }
                                                    val successPendingIntent = PendingIntent.getBroadcast(
                                                        context,
                                                        0,
                                                        callbackIntent,
                                                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                                    )
                                                    val isRequested = appWidgetManager.requestPinAppWidget(provider, null, successPendingIntent)
                                                    if (isRequested) {
                                                        viewModel.showToast(
                                                            if (viewModel.isArabicLanguage()) "تم إرسال طلب إضافة الودجت للشاشة الرئيسية"
                                                            else "Add widget request sent to home screen!"
                                                        )
                                                    } else {
                                                        viewModel.showToast(
                                                            if (viewModel.isArabicLanguage()) "اضغط مطولاً على الشاشة الرئيسية > الودجت > نور لإضافته"
                                                            else "To add: Long press Home Screen -> Widgets -> Noor"
                                                        )
                                                    }
                                                } catch (e: Exception) {
                                                    viewModel.showToast(
                                                        if (viewModel.isArabicLanguage()) "اضغط مطولاً على الشاشة الرئيسية > الودجت > نور"
                                                        else "Long press Home Screen -> Widgets -> Noor"
                                                    )
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) themeColors.accent else SalatEmeraldPrimary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Widgets,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(R.string.settings_widget_pin_button),
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Manual Step-by-step guidance card without borders
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isDarkMode) themeColors.border else SalatCardBg,
                                    border = null
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = stringResource(R.string.settings_widget_manual_title),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = themeColors.arabicText,
                                                fontSize = 12.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = stringResource(R.string.settings_widget_step_1),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = themeColors.translationText,
                                                fontSize = 11.5.sp,
                                                lineHeight = 16.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = stringResource(R.string.settings_widget_step_2),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = themeColors.translationText,
                                                fontSize = 11.5.sp,
                                                lineHeight = 16.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = stringResource(R.string.settings_widget_step_3),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = themeColors.translationText,
                                                fontSize = 11.5.sp,
                                                lineHeight = 16.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 3. Notification Troubleshooting & Diagnostics
                        SettingsClickableRow(
                            icon = Icons.Default.NotificationsActive,
                            title = stringResource(R.string.settings_troubleshooting_title),
                            subtitle = stringResource(R.string.settings_troubleshooting_sub),
                            onClick = {
                                viewModel.navigateTo(NoorDestination.NOTIFICATION_TROUBLESHOOTING)
                            },
                            themeColors = themeColors,
                            testTag = "settings_troubleshooting_row"
                        )
                    }
                }

                // ==========================================
                // SECTION 4: SUPPORT & COMMUNITY
                // ==========================================
                item(key = "section_support_community") {
                    SettingsSection(
                        title = "SUPPORT & COMMUNITY",
                        themeColors = themeColors
                    ) {
                        // 1. Contact Us & Feedback
                        SettingsExpandableRow(
                            icon = Icons.Default.Email,
                            title = stringResource(R.string.settings_section_contact),
                            subtitle = stringResource(R.string.settings_contact_sub),
                            isExpanded = isContactExpanded,
                            onToggleExpand = { isContactExpanded = !isContactExpanded },
                            themeColors = themeColors
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = stringResource(R.string.settings_contact_desc),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeColors.translationText,
                                        fontSize = 12.sp,
                                        lineHeight = 16.5.sp
                                    )
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:support@alnoorapp.com")
                                                putExtra(Intent.EXTRA_SUBJECT, "Al-Noor App Feedback & Support")
                                            }
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Support Email", "support@alnoorapp.com"))
                                                viewModel.showToast("Copied support@alnoorapp.com to clipboard")
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isDarkMode) themeColors.accent else SalatEmeraldPrimary),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(stringResource(R.string.settings_email_support), color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Surface(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Support Email", "support@alnoorapp.com"))
                                            viewModel.showToast("Email address copied: support@alnoorapp.com")
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isDarkMode) themeColors.border else SalatBadgeBg,
                                        border = null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = null,
                                                tint = if (isDarkMode) themeColors.accent else SalatEmeraldPrimary,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(stringResource(R.string.settings_copy_email), color = if (isDarkMode) themeColors.accent else SalatEmeraldPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 2. Share Al-Noor
                        SettingsExpandableRow(
                            icon = Icons.Default.Share,
                            title = "Share Al-Noor",
                            subtitle = "Spread beneficial knowledge with loved ones",
                            isExpanded = isShareExpanded,
                            onToggleExpand = { isShareExpanded = !isShareExpanded },
                            themeColors = themeColors
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "\"Whoever guides someone to goodness will have a reward like one who did it.\" (Sahih Muslim)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeColors.arabicText,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        fontSize = 12.sp,
                                        lineHeight = 16.5.sp
                                    )
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (themeColors.isDark) SecondaryGoldDark else SalatEmeraldPrimary)
                                            .clickable {
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(
                                                        Intent.EXTRA_TEXT,
                                                        "Assalamu Alaikum! Check out Al-Noor – Your spiritual companion with verified Quran, prayer times, authentic Du'as, Khatma plans, and daily reflections.\n\nhttps://alnoorapp.com"
                                                    )
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Share Al-Noor via"))
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Share App", color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isDarkMode) themeColors.border else SalatBadgeBg,
                                        border = null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Al-Noor Link", "https://alnoorapp.com"))
                                                viewModel.showToast("App share link copied to clipboard!")
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = null,
                                                tint = if (isDarkMode) themeColors.accent else SalatEmeraldPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Copy Link",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isDarkMode) themeColors.accent else SalatEmeraldPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.5.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 3. Follow Us
                        SettingsExpandableRow(
                            icon = Icons.Default.Public,
                            title = "Follow Us",
                            subtitle = "Official social channels & updates",
                            isExpanded = isSocialExpanded,
                            onToggleExpand = { isSocialExpanded = !isSocialExpanded },
                            themeColors = themeColors
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SocialChannelChip(
                                        label = "𝕏 Twitter",
                                        handle = "@AlNoorIslamic",
                                        themeColors = themeColors,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        onClick = {
                                            viewModel.showToast("Opening @AlNoorIslamic on 𝕏")
                                        }
                                    )
                                    SocialChannelChip(
                                        label = "YouTube",
                                        handle = "@AlNoorApp",
                                        themeColors = themeColors,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        onClick = {
                                            viewModel.showToast("Opening @AlNoorApp on YouTube")
                                        }
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SocialChannelChip(
                                        label = "Telegram",
                                        handle = "t.me/AlNoorApp",
                                        themeColors = themeColors,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        onClick = {
                                            viewModel.showToast("Opening Al-Noor Telegram channel")
                                        }
                                    )
                                    SocialChannelChip(
                                        label = "Instagram",
                                        handle = "@AlNoor.App",
                                        themeColors = themeColors,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        onClick = {
                                            viewModel.showToast("Opening @AlNoor.App on Instagram")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 5: ABOUT & PRIVACY
                // ==========================================
                item(key = "section_about_privacy") {
                    SettingsSection(
                        title = "ABOUT & PRIVACY",
                        themeColors = themeColors
                    ) {
                        // 1. Privacy Policy & Security
                        SettingsExpandableRow(
                            icon = Icons.Default.Security,
                            title = stringResource(R.string.settings_section_privacy),
                            subtitle = stringResource(R.string.settings_privacy_sub),
                            isExpanded = isPrivacyExpanded,
                            onToggleExpand = { isPrivacyExpanded = !isPrivacyExpanded },
                            themeColors = themeColors
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = stringResource(R.string.settings_privacy_desc),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeColors.translationText,
                                        fontSize = 12.sp,
                                        lineHeight = 16.5.sp
                                    )
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isDarkMode) themeColors.border else Color.White)
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PrivacyItem(
                                        title = stringResource(R.string.settings_privacy_item1_title),
                                        detail = stringResource(R.string.settings_privacy_item1_desc),
                                        themeColors = themeColors
                                    )
                                    PrivacyItem(
                                        title = stringResource(R.string.settings_privacy_item2_title),
                                        detail = stringResource(R.string.settings_privacy_item2_desc),
                                        themeColors = themeColors
                                    )
                                    PrivacyItem(
                                        title = stringResource(R.string.settings_privacy_item3_title),
                                        detail = stringResource(R.string.settings_privacy_item3_desc),
                                        themeColors = themeColors
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            color = (if (isDarkMode) themeColors.border else SalatDivider).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 2. About Us & Mission
                        SettingsExpandableRow(
                            icon = Icons.Default.Info,
                            title = stringResource(R.string.settings_section_about),
                            subtitle = stringResource(R.string.settings_about_sub),
                            isExpanded = isAboutExpanded,
                            onToggleExpand = { isAboutExpanded = !isAboutExpanded },
                            themeColors = themeColors
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = stringResource(R.string.settings_about_desc),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeColors.translationText,
                                        fontSize = 12.sp,
                                        lineHeight = 16.5.sp
                                    )
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isDarkMode) themeColors.border else Color.White)
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.settings_about_sources_title),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDarkMode) themeColors.accent else SalatEmeraldPrimary,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                    Text(
                                        text = stringResource(R.string.settings_about_sources_list),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = themeColors.arabicText,
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 6: APP TUTORIAL (REPLAY)
                // ==========================================
                item(key = "section_tutorial") {
                    SettingsSection(
                        title = if (isArabic) "الجولة التعريفية" else "APP TUTORIAL",
                        themeColors = themeColors
                    ) {
                        SettingsClickableRow(
                            icon = Icons.Default.AutoStories,
                            title = stringResource(R.string.settings_replay_tutorial_title),
                            subtitle = stringResource(R.string.settings_replay_tutorial_desc),
                            themeColors = themeColors,
                            testTag = "settings_replay_tutorial_row",
                            onClick = {
                                viewModel.requestHomeTutorial()
                            }
                        )
                    }
                }

                item(key = "bottom_space") {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

    if (showDndPermissionDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDndPermissionDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.NotificationsOff,
                    contentDescription = null,
                    tint = if (isDarkMode) SecondaryGoldDark else SalatEmeraldPrimary,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = if (isArabic) "إذن عدم الإزعاج أثناء القراءة" else "Do Not Disturb Permission Required",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText
                    )
                )
            },
            text = {
                Text(
                    text = if (isArabic)
                        "لتفعيل وضع 'عدم الإزعاج' تلقائياً أثناء القراءة والعبادة (القرآن، الأذكار، الأدعية، الحفظ، التسبيح) وكتم الاتصالات ورسائل التواصل الاجتماعي، يرجى منح إذن 'الوصول إلى عدم الإزعاج' من إعدادات النظام."
                    else
                        "To automatically silence incoming calls, social media messages, and notifications while reading Quran, Azkar, Duas, Hifz, or Tasbih, please allow Notification Policy Access in Android System Settings.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = themeColors.translationText,
                        fontSize = 13.5.sp
                    )
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showDndPermissionDialog = false
                        viewModel.setDndReadingEnabled(true)
                        try {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                ) {
                    Text(
                        text = if (isArabic) "فتح إعدادات النظام" else "Open System Settings",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) themeColors.accent else SalatEmeraldPrimary
                        )
                    )
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showDndPermissionDialog = false }
                ) {
                    Text(
                        text = if (isArabic) "إلغاء" else "Cancel",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = themeColors.translationText
                        )
                    )
                }
            },
            containerColor = if (isDarkMode) themeColors.surface else Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

/**
 * Section container matching Profile layout: labeled header with seamless surface card and NO borders
 */
@Composable
private fun SettingsSection(
    title: String,
    themeColors: ReadingThemeColors,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = themeColors.isDark
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF5F5E5A),
                fontSize = 11.5.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDark) themeColors.surface else SalatCardBg,
            border = null,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                content = content
            )
        }
    }
}

/**
 * Standard toggle row with icon, title, description, and Switch (no borders)
 */
@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp
) {
    val isDark = themeColors.isDark
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 13.dp),
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
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) SecondaryGoldDark.copy(alpha = 0.18f) else SalatEmeraldPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDark) SecondaryGoldDark else SalatEmeraldPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText,
                        fontSize = 14.sp
                    )
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.translationText,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = if (isDark) SecondaryGoldDark else SalatEmeraldPrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = if (isDark) BorderDividerDark else SalatDivider
            )
        )
    }
}

/**
 * Clickable action row with icon, title, description, and forward arrow
 */
@Composable
private fun SettingsClickableRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    testTag: String = ""
) {
    val isDark = themeColors.isDark
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
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
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) SecondaryGoldDark.copy(alpha = 0.18f) else SalatEmeraldPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDark) SecondaryGoldDark else SalatEmeraldPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText,
                        fontSize = 14.sp
                    )
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.translationText,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = if (isDark) themeColors.accent else SalatEmeraldPrimary,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * Standard expandable row inside section card with chevron indicator
 */
@Composable
private fun SettingsExpandableRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    trailingBadge: String? = null,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    content: @Composable () -> Unit
) {
    val isDark = themeColors.isDark
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpand)
                .padding(horizontal = 16.dp, vertical = 13.dp),
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
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) SecondaryGoldDark.copy(alpha = 0.18f) else SalatEmeraldPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDark) SecondaryGoldDark else SalatEmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText,
                                fontSize = 14.sp
                            )
                        )
                        if (trailingBadge != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDark) themeColors.border else SalatBadgeBg,
                                border = null
                            ) {
                                Text(
                                    text = trailingBadge,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) themeColors.accent else SalatBadgeText
                                    )
                                )
                            }
                        }
                    }
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = themeColors.translationText,
                                fontSize = 11.5.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }
            }

            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = if (isExpanded) (if (isDark) themeColors.accent else SalatEmeraldPrimary) else themeColors.translationText,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 14.dp, top = 2.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun PrivacyItem(
    title: String,
    detail: String,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp
) {
    val isDark = themeColors.isDark
    Column {
        Text(
            text = "• $title",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isDark) themeColors.accent else SalatEmeraldPrimary,
                fontSize = 12.sp
            )
        )
        Text(
            text = detail,
            style = MaterialTheme.typography.bodySmall.copy(
                color = themeColors.arabicText.copy(alpha = 0.85f),
                fontSize = 11.5.sp,
                lineHeight = 15.sp
            )
        )
    }
}

@Composable
private fun SocialChannelChip(
    label: String,
    handle: String,
    modifier: Modifier = Modifier,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    onClick: () -> Unit
) {
    val isDark = themeColors.isDark
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) themeColors.border else Color.White,
        border = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = handle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = themeColors.translationText,
                        fontSize = 10.5.sp
                    )
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = if (isDark) themeColors.accent else SalatEmeraldPrimary,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}


