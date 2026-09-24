package com.example.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import java.time.LocalDate
import java.time.format.TextStyle
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Locale
import com.example.R
import com.example.data.model.HomeWidgetType
import com.example.data.model.PrayerTime
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.components.BentoCard
import com.example.ui.components.HomeSectionActionLabel
import com.example.ui.components.LocalHomeSpotlightState
import com.example.ui.components.spotlightTarget
import com.example.ui.components.universalCardShadow
import com.example.ui.theme.HomePageColors
import com.example.ui.theme.rememberHomePageColors

/**
 * Home Screen using the centralized PageColorRegistry:
 * Colors update dynamically on theme mode switch (Light, Warm, Dark).
 */
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onRequestLocation: (() -> Unit)? = null,
    onRequestNotifications: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val homeColors = rememberHomePageColors()

    val textPrimary = homeColors.titleText
    val textSecondary = homeColors.subtext
    val primaryTeal = homeColors.linkText
    val secondaryGold = homeColors.badgeText
    val goldTintBg = homeColors.badgeBg
    val pageBackground = homeColors.pageBackground
    val surfaceColor = homeColors.outerCardBackground
    val borderDivider = homeColors.dividerBorder

    val prayerTimes by viewModel.prayerTimes.collectAsStateWithLifecycle()
    val nextPrayerName by viewModel.nextPrayerName.collectAsStateWithLifecycle()
    val nextPrayerTimeStr by viewModel.nextPrayerTimeStr.collectAsStateWithLifecycle()
    val nextPrayerCountdown by viewModel.nextPrayerCountdown.collectAsStateWithLifecycle()
    val locationName by viewModel.locationName.collectAsStateWithLifecycle()
    val selectedZone by viewModel.selectedPrayerZone.collectAsStateWithLifecycle()
    val isLocationConfigured by viewModel.isLocationConfigured.collectAsStateWithLifecycle()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var diagnosticCheckKey by remember { mutableStateOf(0) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                diagnosticCheckKey++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val isBatteryExempt = remember(context, diagnosticCheckKey) {
        com.example.data.prayer.AlarmReliabilityHelper.isIgnoringBatteryOptimizations(context)
    }
    val canExactAlarm = remember(context, diagnosticCheckKey) {
        com.example.data.prayer.AlarmReliabilityHelper.canScheduleExactAlarms(context)
    }
    val needsTroubleshooting = isLocationConfigured && hasNotificationPermission && (!isBatteryExempt || !canExactAlarm)

    val gmtTag = remember(selectedZone.timeZoneId, isLocationConfigured) {
        if (!isLocationConfigured) {
            "--"
        } else {
            try {
                val zoneId = java.time.ZoneId.of(selectedZone.timeZoneId)
                val offset = zoneId.rules.getOffset(java.time.Instant.now())
                val hours = offset.totalSeconds / 3600
                val sign = if (hours >= 0) "+" else ""
                "GMT$sign$hours"
            } catch (_: Exception) {
                "GMT"
            }
        }
    }

    val formattedCountdown = remember(nextPrayerCountdown, isLocationConfigured) {
        if (!isLocationConfigured) {
            "--:--"
        } else {
            val parts = nextPrayerCountdown.trim().split(":")
            if (parts.size >= 2) {
                val h = parts[0].toIntOrNull() ?: 0
                val m = parts[1].toIntOrNull() ?: 0
                if (h > 0) "${h}h ${m}m" else "${m}m"
            } else {
                nextPrayerCountdown
            }
        }
    }

    val isCustomizeFeedOpen by viewModel.isCustomizeHomeSheetOpen.collectAsStateWithLifecycle()
    val isCustomizeQuickAccessOpen by viewModel.isCustomizeQuickAccessSheetOpen.collectAsStateWithLifecycle()
    val quickAccessTools by viewModel.quickAccessTools.collectAsStateWithLifecycle()
    val selectedMood by viewModel.selectedMood.collectAsStateWithLifecycle()

    val rawWidgetsOrder by viewModel.homeWidgetsOrder.collectAsStateWithLifecycle()
    val widgetsVisibility by viewModel.homeWidgetsVisibility.collectAsStateWithLifecycle()

    val defaultList = remember { HomeWidgetType.defaultOrderedList() }
    val widgetsOrder = remember(rawWidgetsOrder) {
        val list = if (rawWidgetsOrder.isEmpty()) {
            defaultList
        } else {
            (rawWidgetsOrder + defaultList.filter { !rawWidgetsOrder.contains(it) }).distinct()
        }
        list.filter { it != HomeWidgetType.SALAT_TIMELINE }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(pageBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header row
            HomeHeaderRow(
                viewModel = viewModel,
                location = locationName,
                isDark = isDark,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryTeal = primaryTeal,
                secondaryGold = secondaryGold,
                surfaceColor = surfaceColor,
                borderDivider = borderDivider
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Salat Timeline: Fixed on top of the Home Feed (no other sections can go above it)
            HeroNextPrayerCard(
                nextPrayerName = if (!isLocationConfigured) (if (isArabic) "مواقيت الصلاة" else "Prayer Times") else nextPrayerName.ifBlank { "Salat" },
                nextPrayerTime = if (!isLocationConfigured) "--:--" else nextPrayerTimeStr.ifBlank { "--:--" },
                gmtTag = gmtTag,
                countdownText = if (!isLocationConfigured) "--:--" else nextPrayerCountdown.ifBlank { "--:--" },
                prayers = prayerTimes,
                isLocationConfigured = isLocationConfigured,
                isDark = isDark,
                goldTintBg = goldTintBg,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryTeal = primaryTeal,
                secondaryGold = secondaryGold,
                borderDivider = borderDivider,
                modifier = Modifier.testTag("hero_next_prayer_card")
            )

            // Fixed Notices: Positioned below Hero and above Quick Access tools
            val showNotices = !isLocationConfigured || !hasNotificationPermission || needsTroubleshooting
            if (showNotices) {
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!isLocationConfigured) {
                        HomePermissionNoticeBanner(
                            icon = Icons.Default.Explore,
                            iconTint = Color(0xFFC68A00),
                            iconBg = Color(0xFFFBF0DC),
                            title = if (isArabic) "الموقع غير مفعّل" else "Location is Off",
                            description = if (isArabic) "حدد مدينتك أو فعّل الموقع لعرض مواقيت الصلاة واتجاه القبلة بدقة." else "Set your city or enable GPS to calculate accurate prayer times.",
                            actionText = if (isArabic) "تحديد الموقع" else "Set Location",
                            onAction = {
                                if (onRequestLocation != null) {
                                    onRequestLocation()
                                } else {
                                    viewModel.openSalatSettings()
                                }
                            },
                            surfaceColor = surfaceColor,
                            borderDivider = borderDivider,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }

                    if (!hasNotificationPermission) {
                        HomePermissionNoticeBanner(
                            icon = Icons.Default.AccessTime,
                            iconTint = Color(0xFF1BA486),
                            iconBg = Color(0xFFE6F6F1),
                            title = if (isArabic) "تنبيهات الصلاة متوقفة" else "Prayer Alerts Muted",
                            description = if (isArabic) "اسمح بالإشعارات لتلقي تنبيهات الأذان عند دخول وقت الصلاة." else "Enable notifications to receive timely adhan calls for each prayer.",
                            actionText = if (isArabic) "تفعيل التنبيهات" else "Enable Alerts",
                            onAction = {
                                if (onRequestNotifications != null) {
                                    onRequestNotifications()
                                } else {
                                    viewModel.updateNotificationPermissionStatus()
                                }
                            },
                            surfaceColor = surfaceColor,
                            borderDivider = borderDivider,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }

                    if (needsTroubleshooting) {
                        HomePermissionNoticeBanner(
                            icon = Icons.Default.Security,
                            iconTint = Color(0xFF2A4365),
                            iconBg = Color(0xFFEDF2F7),
                            title = if (isArabic) "تحسين دقة مواقيت الأذان" else "Optimize Adhan Reliability",
                            description = if (isArabic) "اضبط استثناء البطارية وصلاحية المنبهات لضمان انطلاق الأذان في موعده تماماً." else "Check background battery & alarm permissions to ensure exact athan playback.",
                            actionText = if (isArabic) "فحص الإعدادات" else "Troubleshoot",
                            onAction = {
                                viewModel.navigateTo(NoorDestination.NOTIFICATION_TROUBLESHOOTING)
                            },
                            surfaceColor = surfaceColor,
                            borderDivider = borderDivider,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                }
            }

            // Standardized spacing across all home sections
            val sectionSpacing = 18.dp

            // 2. Dynamic modular sections driven by user's customizable feed order and visibility
            val visibleWidgets = widgetsOrder.filter { it != HomeWidgetType.SALAT_TIMELINE && (widgetsVisibility[it] ?: it.defaultVisible) }

            visibleWidgets.forEachIndexed { index, widgetType ->
                Spacer(modifier = Modifier.height(sectionSpacing))

                when (widgetType) {
                    HomeWidgetType.SALAT_TIMELINE -> {
                        // Handled above as a fixed top section
                    }
                HomeWidgetType.SPIRITUAL_ESSENTIALS -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(sectionSpacing)
                    ) {
                        TopFeaturesSection(
                            viewModel = viewModel,
                            isDark = isDark,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            primaryTeal = primaryTeal,
                            secondaryGold = secondaryGold,
                            goldTintBg = goldTintBg,
                            surfaceColor = surfaceColor,
                            borderDivider = borderDivider,
                            modifier = Modifier.testTag("top_features_section")
                        )
                        DailyActivitySection(
                            viewModel = viewModel,
                            isDark = isDark,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            primaryTeal = primaryTeal,
                            secondaryGold = secondaryGold,
                            goldTintBg = goldTintBg,
                            surfaceColor = surfaceColor,
                            borderDivider = borderDivider,
                            modifier = Modifier.testTag("daily_activity_section")
                        )
                    }
                }
                HomeWidgetType.UNIFIED_STREAKS -> {
                    // Unified streak fire row is now merged directly into DailyActivitySection
                }
                HomeWidgetType.KHATMA_TRACKER -> {
                    QuranKhatmaHomeWidget(
                        viewModel = viewModel,
                        modifier = Modifier.testTag("khatma_tracker_section")
                    )
                }
                HomeWidgetType.DAILY_REVELATION -> {
                    DailyAyahAndDuaShowcase(
                        viewModel = viewModel,
                        modifier = Modifier.testTag("daily_revelation_section")
                    )
                }
                HomeWidgetType.FAVORITES_CAROUSEL -> {
                    // Favorites carousel removed from home screen and relocated to Shortcuts sheet
                }
                HomeWidgetType.MOOD_REFLECTION -> {
                    DailyMoodWisdomSection(
                        viewModel = viewModel,
                        selectedMood = selectedMood,
                        isIslamic = true,
                        modifier = Modifier.testTag("mood_reflection_section")
                    )
                }
                HomeWidgetType.AUDIO_RECITERS -> {
                    QuranRecitersShowcase(
                        viewModel = viewModel,
                        modifier = Modifier.testTag("audio_reciters_section")
                    )
                }
            }
        }

        // Clean bottom spacing matching section padding
        Spacer(modifier = Modifier.height(24.dp))
    }
    }

    // Bottom sheets for customization
    if (isCustomizeFeedOpen) {
        CustomizeHomeFeedSheet(
            viewModel = viewModel,
            widgetsOrder = widgetsOrder,
            widgetsVisibility = widgetsVisibility,
            initialTab = 0,
            onDismiss = { viewModel.closeCustomizeHomeSheet() }
        )
    }

    if (isCustomizeQuickAccessOpen) {
        CustomizeHomeFeedSheet(
            viewModel = viewModel,
            widgetsOrder = widgetsOrder,
            widgetsVisibility = widgetsVisibility,
            initialTab = 1,
            onDismiss = { viewModel.closeCustomizeQuickAccessSheet() }
        )
    }
}

// =========================================================================
// 1. HEADER ROW
// =========================================================================
@Composable
private fun HomeHeaderRow(
    viewModel: MainViewModel,
    location: String,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    primaryTeal: Color,
    secondaryGold: Color,
    surfaceColor: Color,
    borderDivider: Color,
    modifier: Modifier = Modifier
) {
    val menuIconTint = if (isDark) primaryTeal else secondaryGold

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Menu icon button inside circular tap target + "Guest Mode" / Location
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { viewModel.navigateTo(NoorDestination.PROFILE) }
                    .testTag("header_menu_button"),
                shape = CircleShape,
                color = surfaceColor,
                shadowElevation = 0.dp,
                border = null
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = menuIconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column {
                Text(
                    text = "Guest Mode",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = location.ifBlank { "Location is Off" },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = textSecondary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Right: Right-side icon buttons on surfaceColor
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val homeSpotlight = LocalHomeSpotlightState.current
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { viewModel.openCustomizeHomeSheet() }
                    .testTag("header_customize_button")
                    .then(
                        if (homeSpotlight != null) {
                            Modifier.spotlightTarget(homeSpotlight, "header_customize_button")
                        } else Modifier
                    ),
                shape = CircleShape,
                color = surfaceColor,
                shadowElevation = 0.dp,
                border = null
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.DashboardCustomize,
                        contentDescription = "Customize",
                        tint = textPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { viewModel.openSettingsModal() }
                    .testTag("header_settings_button"),
                shape = CircleShape,
                color = surfaceColor,
                shadowElevation = 0.dp,
                border = null
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = textPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

// =========================================================================
// PERMISSION & LOCATION NOTICE BANNER (Official Palette)
// =========================================================================
@Composable
private fun HomePermissionNoticeBanner(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit,
    surfaceColor: Color,
    borderDivider: Color,
    textPrimary: Color,
    textSecondary: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onAction() },
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor,
        border = BorderStroke(1.dp, borderDivider),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(com.example.ui.theme.NoorSpacing.TitleSubtextSpacing)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = textPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.5.sp,
                        color = textSecondary,
                        lineHeight = 15.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFEDF2F7),
                border = null
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A4365),
                        fontSize = 11.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// =========================================================================
// 2. HERO "NEXT PRAYER" CARD
// =========================================================================
@Composable
private fun HeroNextPrayerCard(
    nextPrayerName: String,
    nextPrayerTime: String,
    gmtTag: String,
    countdownText: String,
    prayers: List<PrayerTime>,
    isLocationConfigured: Boolean = true,
    isDark: Boolean,
    goldTintBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    primaryTeal: Color,
    secondaryGold: Color,
    borderDivider: Color,
    modifier: Modifier = Modifier
) {
    val displayPrayerName = when {
        !isLocationConfigured -> "Salat"
        nextPrayerName.equals("Dzuhur", ignoreCase = true) -> "Dhuhr"
        nextPrayerName.isNotBlank() -> nextPrayerName
        else -> "Fajr"
    }

    // Format next prayer time cleanly as HH:mm
    val displayNextPrayerTime = remember(nextPrayerTime, isLocationConfigured) {
        if (!isLocationConfigured) {
            "--:--"
        } else {
            val raw = nextPrayerTime.trim().substringBefore(" ")
            if (raw.contains(":")) raw else "--:--"
        }
    }

    val displayCountdown = remember(countdownText, isLocationConfigured) {
        if (!isLocationConfigured) {
            "--:--"
        } else if (countdownText.isBlank() || countdownText == "00:00:00") {
            "--:--"
        } else {
            countdownText
        }
    }

    val prayerKeys = remember { listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha") }
    val stripItems = remember(prayers, nextPrayerName, isLocationConfigured) {
        prayerKeys.map { key ->
            val match = prayers.firstOrNull { it.name.equals(key, ignoreCase = true) }
            val time = if (!isLocationConfigured) {
                "--:--"
            } else {
                match?.let {
                    if (it.timeString == "--:--") "--:--" else String.format(Locale.US, "%02d:%02d", it.hour, it.minute)
                } ?: "--:--"
            }
            val displayName = when {
                key.equals("Dzuhur", ignoreCase = true) -> "Dhuhr"
                else -> key
            }
            val isActive = isLocationConfigured && (key.equals(nextPrayerName, ignoreCase = true) ||
                    (key.equals("Dhuhr", ignoreCase = true) && nextPrayerName.equals("Dzuhur", ignoreCase = true)) ||
                    match?.isNext == true)

            PrayerStripEntry(
                key = key,
                displayName = displayName,
                time = time,
                isActive = isActive
            )
        }
    }

    val homeColors = rememberHomePageColors()
    val heroAccent = homeColors.badgeText

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .universalCardShadow(
                shape = RoundedCornerShape(24.dp),
                elevation = 4.dp,
                isDark = isDark
            )
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black)
    ) {
        // Hero background image (mosque photo) ALWAYS present in background
        Image(
            painter = painterResource(id = R.drawable.img_pinterest_hero),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            alpha = 0.85f,
            modifier = Modifier.matchParentSize()
        )

        // Gradient Overlay for readability
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.30f),
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Top Row: Asymmetric Hero Typography & Countdown Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left Column: Category Tag + Prayer Name
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = heroAccent.copy(alpha = 0.20f)
                    ) {
                        Text(
                            text = "NEXT SALAT",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                color = heroAccent
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = displayPrayerName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                // Right Column: Display Next Prayer Time + Glassy Countdown Badge
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = displayNextPrayerTime,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Pill countdown badge with dark glassy fill
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.Black.copy(alpha = 0.50f)
                    ) {
                        Text(
                            text = "In $displayCountdown",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = heroAccent
                            )
                        )
                    }
                }
            }

            // Prayer Timeline Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_prayers_glassy_strip"),
                shape = RoundedCornerShape(18.dp),
                color = Color.Black.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    stripItems.forEach { entry ->
                        if (entry.isActive) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(heroAccent.copy(alpha = 0.22f))
                                    .padding(vertical = 8.dp, horizontal = 2.dp)
                                    .testTag("hero_prayer_${entry.key.lowercase()}"),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = entry.displayName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = heroAccent
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = entry.time,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 8.dp, horizontal = 2.dp)
                                    .testTag("hero_prayer_${entry.key.lowercase()}"),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = entry.displayName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White.copy(alpha = 0.70f)
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = entry.time,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(alpha = 0.90f)
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


private data class PrayerStripEntry(
    val key: String,
    val displayName: String,
    val time: String,
    val isActive: Boolean
)

/**
 * Time of day icon:
 * - Crescent for Fajr & Isha
 * - Sun for Dhuhr & Asr
 * - Sunset for Maghrib
 * Uses the uniform gold color passed as tint.
 */
@Composable
private fun TimeOfDayIcon(
    prayerKey: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val keyLower = prayerKey.lowercase()
    when {
        keyLower.contains("fajr") || keyLower.contains("isha") -> {
            Canvas(modifier = modifier) {
                val r = size.minDimension / 2f
                val cx = size.width / 2f
                val cy = size.height / 2f
                val outer = Path().apply {
                    addOval(Rect(cx - r, cy - r, cx + r, cy + r))
                }
                val cut = Path().apply {
                    addOval(Rect(cx - r * 0.4f, cy - r * 1.15f, cx + r * 1.5f, cy + r * 0.95f))
                }
                val crescent = Path.combine(PathOperation.Difference, outer, cut)
                drawPath(crescent, color = tint)
            }
        }
        keyLower.contains("maghrib") -> {
            Icon(
                imageVector = Icons.Default.WbTwilight,
                contentDescription = null,
                tint = tint,
                modifier = modifier
            )
        }
        else -> {
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = null,
                tint = tint,
                modifier = modifier
            )
        }
    }
}

// =========================================================================
// 4. "TOP FEATURES" SECTION (AIRY FLOATING PILL ROW)
// =========================================================================
@Composable
private fun TopFeaturesSection(
    viewModel: MainViewModel,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    primaryTeal: Color,
    secondaryGold: Color,
    goldTintBg: Color,
    surfaceColor: Color,
    borderDivider: Color,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    val quickAccessTools by viewModel.quickAccessTools.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isLangArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val currentTools = remember(quickAccessTools) {
        val list = quickAccessTools.toMutableList()
        for (defaultTool in com.example.data.model.QuickAccessTool.defaultTools()) {
            if (list.size >= 5) break
            if (!list.contains(defaultTool)) {
                list.add(defaultTool)
            }
        }
        list.take(5)
    }

    // Entire Quick Access content is unified inside a single premium card container
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .universalCardShadow(
                shape = RoundedCornerShape(20.dp),
                elevation = 4.dp,
                isDark = isDark
            ),
        shape = RoundedCornerShape(20.dp),
        color = homeColors.outerCardBackground,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section header row matching Daily Activity design perfectly
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isLangArabic) "الوصول السريع" else "Quick Access",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = homeColors.titleText,
                            fontSize = 15.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(com.example.ui.theme.NoorSpacing.TitleSubtextSpacingComfortable))
                    Text(
                        text = if (isLangArabic) "وصول سريع لأهم الأدوات اليومية" else "Quick access to your spiritual utility essentials",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            color = homeColors.subtext
                        )
                    )
                }

                HomeSectionActionLabel(
                    text = if (isLangArabic) "كل الأدوات" else "All Tools",
                    onClick = { viewModel.navigateTo(NoorDestination.ALL_TOOLS) },
                    isDark = isDark,
                    primaryTeal = homeColors.linkText,
                    testTag = "see_more_features_button"
                )
            }

            // Quick Access Icons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentTools.forEach { tool ->
                    val icon = getQuickAccessToolIcon(tool)
                    val label = if (isLangArabic) tool.titleAr else tool.titleEn
                    val destination = getQuickAccessToolDestination(tool)

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.navigateTo(destination) }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                            .testTag("feature_shortcut_${tool.id}"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        val toolBg = homeColors.iconBadgeBg
                        val toolIcon = homeColors.iconColor

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(toolBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = toolIcon,
                                modifier = Modifier.size(25.dp)
                            )
                        }

                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = homeColors.titleText,
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 5. "DAILY ACTIVITY" SECTION (PREMIUM MODULAR DEVOTIONS GRID)
// =========================================================================
@Composable
private fun DailyActivitySection(
    viewModel: MainViewModel,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    primaryTeal: Color,
    secondaryGold: Color,
    goldTintBg: Color,
    surfaceColor: Color,
    borderDivider: Color,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isLangArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val streakData by viewModel.unifiedStreakData.collectAsStateWithLifecycle()
    val completedPrayers by viewModel.completedPrayers.collectAsStateWithLifecycle()
    val tasbihCount by viewModel.tasbihCount.collectAsStateWithLifecycle()
    val tasbihTarget by viewModel.tasbihTarget.collectAsStateWithLifecycle()
    val quranAyahsReadToday by viewModel.quranAyahsReadToday.collectAsStateWithLifecycle()
    val azkarCountToday by viewModel.azkarCountToday.collectAsStateWithLifecycle()

    val effectiveTasbihTarget = if (tasbihTarget > 0) tasbihTarget else 33

    // 4 Core Devotions: Salat, Quran, Tasbih, Azkar
    val salatDone = completedPrayers.size >= 5
    val quranDone = quranAyahsReadToday >= 10
    val tasbihDone = tasbihCount >= effectiveTasbihTarget
    val adhkarDone = azkarCountToday >= 5

    val completedCount = (if (salatDone) 1 else 0) +
            (if (quranDone) 1 else 0) +
            (if (tasbihDone) 1 else 0) +
            (if (adhkarDone) 1 else 0)

    val percentage = ((completedCount / 4f) * 100).toInt()

    val progressFraction by androidx.compose.animation.core.animateFloatAsState(
        targetValue = completedCount / 4f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 600),
        label = "dailyDevotionsProgress"
    )

    BentoCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Header Row: Title & Subtext + Progress Circle on the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isLangArabic) "الورد والنشاط اليومي" else "Daily Devotions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = homeColors.titleText,
                            fontSize = 16.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(com.example.ui.theme.NoorSpacing.TitleSubtextSpacingComfortable))
                    Text(
                        text = if (isLangArabic) "تمت تأدية $completedCount من 4 طاعات أساسية اليوم" else "$completedCount of 4 daily devotions completed",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            color = homeColors.subtext
                        )
                    )
                }

                // Modern Circular Progress Indicator
                Box(
                    modifier = Modifier.size(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(42.dp)) {
                        val strokeWidth = 3.5.dp.toPx()
                        drawCircle(
                            color = homeColors.progressTrack,
                            style = Stroke(width = strokeWidth)
                        )
                        if (progressFraction > 0f) {
                            drawArc(
                                color = homeColors.progressFill,
                                startAngle = -90f,
                                sweepAngle = 360f * progressFraction,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )
                        }
                    }

                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = homeColors.titleText,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            // 2. 2x2 Modular Grid of Core Activities
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Row 1: Salat + Quran
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. SALAT CARD
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = homeColors.innerContainer,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.navigateTo(NoorDestination.SALAT) }
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
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(homeColors.iconBadgeBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IslamicIconSalat(
                                        modifier = Modifier.size(17.dp),
                                        tint = homeColors.iconColor
                                    )
                                }

                                // Action Link: "Check" / "تفقد" (Standard link badge)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(homeColors.linkBadgeBg)
                                        .clickable { viewModel.navigateTo(NoorDestination.SALAT) }
                                        .padding(vertical = 3.dp, horizontal = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = if (isLangArabic) "تفقد" else "Check",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = homeColors.linkText
                                            )
                                        )
                                        Icon(
                                            imageVector = if (isLangArabic) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = homeColors.linkText,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(com.example.ui.theme.NoorSpacing.TitleSubtextSpacing)) {
                                Text(
                                    text = if (isLangArabic) "الصلوات الخمس" else "Salat",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = homeColors.titleText
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (isLangArabic) "${completedPrayers.size}/٥" else "${completedPrayers.size}/5",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = homeColors.subtext
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // 2. QURAN CARD
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = homeColors.innerContainer,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.navigateTo(NoorDestination.QURAN_SURAH_LIST) }
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
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(homeColors.iconBadgeBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IslamicIconMushaf(
                                        modifier = Modifier.size(17.dp),
                                        tint = homeColors.iconColor
                                    )
                                }

                                // Action Link: "Read" / "اقرأ" (Standard link badge)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(homeColors.linkBadgeBg)
                                        .clickable { viewModel.navigateTo(NoorDestination.QURAN_SURAH_LIST) }
                                        .padding(vertical = 3.dp, horizontal = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = if (isLangArabic) "اقرأ" else "Read",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = homeColors.linkText
                                            )
                                        )
                                        Icon(
                                            imageVector = if (isLangArabic) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = homeColors.linkText,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(com.example.ui.theme.NoorSpacing.TitleSubtextSpacing)) {
                                Text(
                                    text = if (isLangArabic) "القرآن" else "Quran",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = homeColors.titleText
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (isLangArabic) "${quranAyahsReadToday}/١٠ آيات" else "${quranAyahsReadToday}/10 Ayahs",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = homeColors.subtext
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Row 2: Dhikr + Adhkar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 3. TASBIH & DHIKR CARD
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = homeColors.innerContainer,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.navigateTo(NoorDestination.AZKAR_READER) }
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
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(homeColors.iconBadgeBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IslamicIconTasbeeh(
                                        modifier = Modifier.size(17.dp),
                                        tint = homeColors.iconColor
                                    )
                                }

                                // Action Link: "Count" / "سبّح" (Standard link badge)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(homeColors.linkBadgeBg)
                                        .clickable { viewModel.navigateTo(NoorDestination.AZKAR_READER) }
                                        .padding(vertical = 3.dp, horizontal = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = if (isLangArabic) "سبّح" else "Count",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = homeColors.linkText
                                            )
                                        )
                                        Icon(
                                            imageVector = if (isLangArabic) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = homeColors.linkText,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(com.example.ui.theme.NoorSpacing.TitleSubtextSpacing)) {
                                Text(
                                    text = if (isLangArabic) "التسبيح" else "Tasbih",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = homeColors.titleText
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (isLangArabic) "${tasbihCount}/${effectiveTasbihTarget}" else "${tasbihCount}/${effectiveTasbihTarget}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = homeColors.subtext
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // 4. DAILY ADHKAR & DUAS CARD
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = homeColors.innerContainer,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.navigateTo(NoorDestination.AZKAR_READER) }
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
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(homeColors.iconBadgeBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IslamicIconDua(
                                        modifier = Modifier.size(17.dp),
                                        tint = homeColors.iconColor
                                    )
                                }

                                // Action Link: "Recite" / "اتلُ" (Standard link badge)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(homeColors.linkBadgeBg)
                                        .clickable { viewModel.navigateTo(NoorDestination.AZKAR_READER) }
                                        .padding(vertical = 3.dp, horizontal = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = if (isLangArabic) "اتلُ" else "Recite",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = homeColors.linkText
                                            )
                                        )
                                        Icon(
                                            imageVector = if (isLangArabic) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = homeColors.linkText,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(com.example.ui.theme.NoorSpacing.TitleSubtextSpacing)) {
                                Text(
                                    text = if (isLangArabic) "الأذكار" else "Azkar",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = homeColors.titleText
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (isLangArabic) "${azkarCountToday}/٥" else "${azkarCountToday}/5",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = homeColors.subtext
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Divider line above fire row
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.8.dp,
                color = borderDivider
            )

            // 7-Day Devotion Streak Fire Row (Moved below divider & reduced in size)
            val today = remember { LocalDate.now() }
            val currentDayOfWeek = today.dayOfWeek.value // 1 (Mon) .. 7 (Sun)
            val daysOfWeek = remember(today, isLangArabic, streakData) {
                (1..7).map { dayNum ->
                    val date = today.minusDays((currentDayOfWeek - dayNum).toLong())
                    val isToday = dayNum == currentDayOfWeek
                    val isPast = dayNum < currentDayOfWeek
                    val dayLabel = date.dayOfWeek.getDisplayName(
                        TextStyle.NARROW,
                        if (isLangArabic) Locale("ar") else Locale.ENGLISH
                    )

                    val isCompleted = if (isToday) {
                        streakData.isTodayAnyCompleted
                    } else if (isPast) {
                        val daysAgo = currentDayOfWeek - dayNum
                        daysAgo < streakData.currentStreak
                    } else false

                    Triple(dayLabel, isToday, isCompleted)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                daysOfWeek.forEach { (label, isToday, isCompleted) ->
                    val isHighlighted = isCompleted || (isToday && streakData.isTodayAnyCompleted)
                    val circleBg = if (isHighlighted) homeColors.badgeBg else homeColors.subtext.copy(alpha = 0.12f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(26.dp),
                            shape = CircleShape,
                            color = circleBg,
                            border = null
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isHighlighted) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = "Spiritual Devotion",
                                        tint = if (isToday) homeColors.badgeText else Color(0xFF107C41),
                                        modifier = Modifier.size(13.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = homeColors.subtext.copy(alpha = 0.35f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isToday || isCompleted) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCompleted) homeColors.badgeText else if (isToday) homeColors.titleText else homeColors.subtext,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }

            // 3. Bottom Row: Left description text & Right "All tasks" navigation link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(NoorDestination.HABIT_TRACKER) }
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLangArabic) "سجل العادات والالتزام اليومي" else "Faith habits & daily progress",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        color = textSecondary
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isLangArabic) "عرض كل المهام" else "All tasks",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryTeal
                        )
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = primaryTeal,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
