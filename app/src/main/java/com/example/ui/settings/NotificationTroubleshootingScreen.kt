package com.example.ui.settings

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.NoorNotificationHelper
import com.example.data.prayer.AdhanPlayer
import com.example.data.prayer.AlarmReliabilityHelper
import com.example.data.prayer.PrayerAlarmScheduler
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.theme.ReadingThemeColors

// Palette tokens aligned with the borderless Noor design system
private val StatusGreen = Color(0xFF107C41)
private val StatusGreenBg = Color(0xFFE8F5E9)
private val StatusAmber = Color(0xFFD97706)
private val StatusAmberBg = Color(0xFFFEF3C7)
private val StatusRed = Color(0xFFDC2626)
private val StatusRedBg = Color(0xFFFEE2E2)
private val BrandEmerald = Color(0xFF0D5C3A)
private val BrandEmeraldDark = Color(0xFF16A34A)

@Composable
fun NotificationTroubleshootingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    customThemeColors: ReadingThemeColors? = null
) {
    val appThemeMode by viewModel.appThemeMode.collectAsStateWithLifecycle()
    val themeColors = customThemeColors ?: when (appThemeMode) {
        com.example.ui.theme.AppThemeMode.LIGHT -> com.example.ui.theme.ReadingThemes.MadaniCrisp
        com.example.ui.theme.AppThemeMode.WARM -> com.example.ui.theme.ReadingThemes.SepiaParchment
        com.example.ui.theme.AppThemeMode.DARK -> com.example.ui.theme.ReadingThemes.ObsidianNight
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val prayerTimes by viewModel.prayerTimes.collectAsStateWithLifecycle()
    val prayerTimers by viewModel.prayerNotificationTimers.collectAsStateWithLifecycle()
    val prayerEnabled by viewModel.prayerNotificationEnabled.collectAsStateWithLifecycle()
    val prayerAlertTypes by viewModel.prayerAlertTypes.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    var hasDismissedCompletionPopup by remember { mutableStateOf(false) }

    // Trigger state to re-run diagnostics
    var refreshKey by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val isAdhanAudioPlaying by AdhanPlayer.isPlayingFlow.collectAsStateWithLifecycle()
    var testCountdown by remember { mutableStateOf(-1) }
    var showFeedbackPrompt by remember { mutableStateOf(false) }
    var testSucceeded by remember { mutableStateOf<Boolean?>(null) }
    var testWasTriggered by remember { mutableStateOf(false) }
    var prevPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isAdhanAudioPlaying) {
        if (prevPlaying && !isAdhanAudioPlaying && testWasTriggered) {
            showFeedbackPrompt = true
            testWasTriggered = false
        }
        prevPlaying = isAdhanAudioPlaying
    }

    // Re-check diagnostics whenever the user returns from system settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshKey++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            if (AdhanPlayer.isPlayingFlow.value) {
                AdhanPlayer.stop(isDismiss = false)
            }
        }
    }

    val report = remember(refreshKey, prayerTimes, prayerTimers, prayerEnabled, prayerAlertTypes) {
        AlarmReliabilityHelper.runDiagnostics(
            context = context,
            prayers = prayerTimes,
            timersMap = prayerTimers,
            enabledMap = prayerEnabled,
            alertTypes = prayerAlertTypes
        )
    }

    val isInitiallyHealthy = remember {
        val initialReport = AlarmReliabilityHelper.runDiagnostics(
            context = context,
            prayers = prayerTimes,
            timersMap = prayerTimers,
            enabledMap = prayerEnabled,
            alertTypes = prayerAlertTypes
        )
        initialReport.overallHealthy
    }

    var fixedAllIssuesDuringSession by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(report.overallHealthy) {
        if (!isInitiallyHealthy && report.overallHealthy) {
            fixedAllIssuesDuringSession = true
        }
    }

    val isDark = themeColors.isDark
    val cardBg = if (isDark) themeColors.surface else Color.White
    val containerBg = if (isDark) themeColors.background else Color(0xFFF7F9FA)
    val textPrimary = themeColors.arabicText
    val textSecondary = themeColors.translationText

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = containerBg,
        topBar = {
            Surface(
                color = if (isDark) themeColors.surface else Color.White,
                shadowElevation = if (isDark) 0.dp else 1.dp,
                border = null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("troubleshooting_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                            tint = textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_troubleshooting_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                fontSize = 17.sp
                            )
                        )
                        Text(
                            text = "Diagnostic & background reliability",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = textSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                    IconButton(
                        onClick = {
                            refreshKey++
                            viewModel.schedulePrayerAlarms()
                            viewModel.showToast("Diagnostics refreshed")
                        },
                        modifier = Modifier.testTag("troubleshooting_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = if (isDark) BrandEmeraldDark else BrandEmerald
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overall Health Status Banner
            item(key = "health_banner") {
                val bannerBg = if (report.overallHealthy) {
                    if (isDark) Color(0xFF143825) else StatusGreenBg
                } else {
                    if (isDark) Color(0xFF382914) else StatusAmberBg
                }
                val bannerIcon = if (report.overallHealthy) Icons.Default.CheckCircle else Icons.Default.Warning
                val bannerTint = if (report.overallHealthy) {
                    if (isDark) Color(0xFF4ADE80) else StatusGreen
                } else {
                    if (isDark) Color(0xFFFBBF24) else StatusAmber
                }
                val bannerTitle = if (report.overallHealthy) {
                    if (isArabic) "كافة اختبارات دقة الأذان ناجحة" else "All Reliability Checks Passed"
                } else {
                    if (isArabic) "إجراءات مطلوبة لضمان دقة الأذان" else "Action Needed for Reliable Adhan"
                }
                val bannerSub = if (report.overallHealthy) {
                    if (isArabic) "تم ضبط جهازك بنجاح لتشغيل الأذان في وقته بدقة دون أن توقفه ميزات توفير الطاقة." else "Your device is configured to deliver exact prayer alarms and Adhan audio without being silenced by battery saver."
                } else {
                    if (isArabic) "قد تؤخر بعض إعدادات توفير الطاقة انطلاق الأذان عند قفل الشاشة. راجع الفحوصات أدناه." else "Some settings on your device may delay or silence the Adhan when your screen is locked. Review the checks below."
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = bannerBg,
                    border = null
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(bannerTint.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = bannerIcon,
                                contentDescription = null,
                                tint = bannerTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = bannerTitle,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF1E293B),
                                    fontSize = 14.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = bannerSub,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            )
                        }
                    }
                }
            }

            // Notification Center Hub Redirect Banner
            item(key = "notif_center_redirect_card") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(NoorDestination.NOTIFICATION_CENTER) }
                        .testTag("notif_hub_redirect_banner"),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDark) Color(0xFF1A2E26) else Color(0xFFE6F6F1),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF1BA486).copy(alpha = 0.35f) else Color(0xFF1BA486).copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1BA486)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "مركز الإشعارات والتنبيهات" else "Notification Center Hub",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF1F1F1F),
                                    fontSize = 14.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isArabic) "تخصيص أصوات الأذان وتنبيهات الصلوات والأذكار ومستويات الصوت." else "Customize athan reciters, prayer alerts, morning & evening azkar, and volume levels.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF5F5E5A),
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.5.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isDark) Color(0xFF2A4365).copy(alpha = 0.6f) else Color(0xFFEDF2F7)
                        ) {
                            Text(
                                text = if (isArabic) "فتح المركز" else "Open Hub",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFF90CDF4) else Color(0xFF2A4365),
                                    fontSize = 11.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // SECTION 1: Diagnostic Checklist ("Why didn't my adhan play?")
            item(key = "diagnostic_header") {
                Column {
                    Text(
                        text = stringResource(R.string.diagnostic_title).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) BrandEmeraldDark else BrandEmerald,
                            letterSpacing = 1.sp,
                            fontSize = 11.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Instant hardware and background permission checks",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = textSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            items(report.items.size, key = { "item_${report.items[it].key}" }) { index ->
                val item = report.items[index]
                DiagnosticItemCard(
                    item = item,
                    themeColors = themeColors,
                    onFix = {
                        if (item.key == "next_alarm") {
                            // Unmute or reschedule
                            viewModel.schedulePrayerAlarms()
                            viewModel.showToast("All prayer alarms re-registered")
                            refreshKey++
                        } else {
                            item.onFix(context)
                        }
                    }
                )
            }

            // SECTION 2: Manufacturer-Specific OEM Autostart Card
            item(key = "oem_card") {
                val oem = report.detectedManufacturer
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = cardBg,
                    border = null,
                    shadowElevation = if (isDark) 0.dp else 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background((if (isDark) BrandEmeraldDark else BrandEmerald).copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = if (isDark) BrandEmeraldDark else BrandEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Device Autostart & Power Management",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = "Detected: ${oem.displayName} (${oem.brandTag})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) BrandEmeraldDark else BrandEmerald,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = oem.issueDescription,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = textSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Step-by-step instructions card (borderless)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDark) themeColors.background else Color(0xFFF1F5F9),
                            border = null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Recommended Steps for ${oem.brandTag}:",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                                oem.steps.forEachIndexed { stepIdx, stepText ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "${stepIdx + 1}.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) BrandEmeraldDark else BrandEmerald,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                        Text(
                                            text = stepText,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = textPrimary,
                                                fontSize = 11.5.sp,
                                                lineHeight = 16.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Button to deep-link into OEM settings
                        Button(
                            onClick = {
                                val success = AlarmReliabilityHelper.openAutostartSettings(context)
                                if (!success) {
                                    viewModel.showToast("Opening App Settings — check Battery & Permissions")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) BrandEmeraldDark else BrandEmerald
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("open_autostart_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Open Autostart / Battery Settings",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // SECTION 3: Test Adhan & Notification Player
            item(key = "test_alert_section") {
                val isRunning = testCountdown > 0 || isAdhanAudioPlaying
                val successBg = if (isDark) Color(0xFF143825) else Color(0xFFE8F5E9)
                val successText = if (isDark) Color(0xFF4ADE80) else StatusGreen
                val alertBg = if (isDark) Color(0xFF382914) else StatusAmberBg
                val alertText = if (isDark) Color(0xFFFBBF24) else StatusAmber

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = when {
                        isRunning -> alertBg
                        testSucceeded == true -> successBg
                        else -> cardBg
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            isRunning -> alertText.copy(alpha = 0.35f)
                            testSucceeded == true -> successText.copy(alpha = 0.35f)
                            else -> if (isDark) Color(0xFF2A373F) else Color(0xFFECEFF1)
                        }
                    ),
                    shadowElevation = if (isDark) 0.dp else 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isRunning -> alertText.copy(alpha = 0.15f)
                                            testSucceeded == true -> successText.copy(alpha = 0.15f)
                                            else -> if (isDark) Color(0xFF1A2E26) else Color(0xFFE6F6F1)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        isRunning -> Icons.Default.NotificationsActive
                                        testSucceeded == true -> Icons.Default.CheckCircle
                                        else -> Icons.Default.NotificationsActive
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        isRunning -> alertText
                                        testSucceeded == true -> successText
                                        else -> if (isDark) BrandEmeraldDark else Color(0xFF1BA486)
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "فحص واختبار صوت الأذان" else "Test Adhan Notification Now",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isRunning -> alertText
                                            testSucceeded == true -> successText
                                            else -> textPrimary
                                        },
                                        fontSize = 14.5.sp
                                    )
                                )
                                Text(
                                    text = if (isArabic) "التحقق من مكبر الصوت وظهور الإشعار على شاشة القفل" else "Verify speaker output and lock-screen banner",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = when {
                                            isRunning -> alertText.copy(alpha = 0.85f)
                                            testSucceeded == true -> successText.copy(alpha = 0.85f)
                                            else -> textSecondary
                                        },
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        // Status and Prompt States
                        when {
                            isRunning -> {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = if (isArabic)
                                            "الاختبار قيد التشغيل — أقفل هاتفك وانتظر انطلاق الأذان."
                                        else
                                            "The test is running — lock your phone and wait for the Adhan.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = alertText,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    )
                                    if (testCountdown > 0) {
                                        Text(
                                            text = if (isArabic)
                                                "سيتم إطلاق الأذان خلال $testCountdown ثوانٍ... ⏰"
                                            else
                                                "Triggering Adhan in $testCountdown seconds... ⏰",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = alertText.copy(alpha = 0.9f),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                    if (isAdhanAudioPlaying) {
                                        Button(
                                            onClick = {
                                                AdhanPlayer.stop(isDismiss = true)
                                                showFeedbackPrompt = true
                                                testWasTriggered = false
                                                testCountdown = -1
                                            },
                                            shape = RoundedCornerShape(50),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = StatusRed,
                                                contentColor = Color.White
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(42.dp)
                                                .testTag("btn_stop_test_adhan")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Stop,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isArabic) "إيقاف الأذان" else "Stop Adhan",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }

                            showFeedbackPrompt -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = if (isArabic) "هل عمل الاختبار وسمعت صوت الأذان؟" else "Did it work?",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                showFeedbackPrompt = false
                                                testSucceeded = true
                                                viewModel.triggerHaptic()
                                                viewModel.showToast(if (isArabic) "رائع! تم تأكيد عمل الأذان 🎉" else "Great! Adhan audio verified 🎉")
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .testTag("btn_adhan_test_yes"),
                                            shape = RoundedCornerShape(50),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF1BA486),
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isArabic) "نعم" else "Yes",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                showFeedbackPrompt = false
                                                testSucceeded = false
                                                viewModel.showToast(if (isArabic) "راجع إعدادات الصوت وتوفير الطاقة أعلاه" else "Please check media volume and battery settings above")
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .testTag("btn_adhan_test_no"),
                                            shape = RoundedCornerShape(50),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isDark) Color(0xFF2A373F) else Color(0xFFEDF2F7),
                                                contentColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF2A4365)
                                            ),
                                            border = BorderStroke(1.dp, if (isDark) Color(0xFF384650) else Color(0xFFECEFF1))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = if (isDark) Color(0xFFE2E8F0) else Color(0xFF2A4365)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isArabic) "لا" else "No",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }

                            else -> {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = if (isArabic)
                                            "يطلق هذا الفحص إشعار صلاة حي ويشغل مقطع أذان تجريبي للتأكد من عمل الرنين وقنوات الإشعارات ومستوى الصوت."
                                        else
                                            "Triggers a live prayer notification and plays a sample Adhan tone to confirm your ringer, notification channels, and volume are working.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = textSecondary,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp
                                        )
                                    )

                                    Button(
                                        onClick = {
                                            testCountdown = 10
                                            testWasTriggered = true
                                            showFeedbackPrompt = false
                                            testSucceeded = null
                                            coroutineScope.launch {
                                                while (testCountdown > 0) {
                                                    delay(1000)
                                                    testCountdown--
                                                }
                                                NoorNotificationHelper.showPrayerAlert(
                                                    context = context,
                                                    prayerName = "Salat Test",
                                                    timeFormatted = "Now",
                                                    isPreAlert = false,
                                                    offsetMinutes = 0
                                                )
                                                AdhanPlayer.play(
                                                    context = context,
                                                    prayerName = "Salat",
                                                    isSnooze = true
                                                )
                                            }
                                        },
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDark) BrandEmeraldDark else Color(0xFF1BA486),
                                            contentColor = Color.White
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .testTag("test_adhan_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (testSucceeded != null) {
                                                if (isArabic) "إعادة فحص الأذان (10 ثوانٍ)" else "Re-test Adhan (10s)"
                                            } else {
                                                if (isArabic) "إطلاق فحص الأذان (10 ثوانٍ)" else "Launch Adhan Test (10s)"
                                            },
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (fixedAllIssuesDuringSession && !hasDismissedCompletionPopup) {
        AlertDialog(
            onDismissRequest = { hasDismissedCompletionPopup = true },
            shape = RoundedCornerShape(20.dp),
            containerColor = if (isDark) themeColors.surface else Color.White,
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF143825) else Color(0xFFE6F6F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isDark) Color(0xFF4ADE80) else Color(0xFF1BA486),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = if (isArabic) "تم ضبط كافة الإعدادات بنجاح!" else "All Reliability Checks Passed!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = if (isArabic)
                        "أصبح جهازك جاهزاً لتشغيل الأذان بدقة كاملة. هل ترغب بالانتقال إلى مركز الإشعارات لتخصيص أصوات الأذان وتنبيهات الأذكار؟"
                    else
                        "Your device is now fully optimized for accurate Adhan delivery. Would you like to go to the Notification Center to customize your Adhan reciters and Azkar alerts?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        hasDismissedCompletionPopup = true
                        viewModel.navigateTo(NoorDestination.NOTIFICATION_CENTER)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1BA486)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("popup_go_to_notif_center_button")
                ) {
                    Text(
                        text = if (isArabic) "الانتقال لمركز الإشعارات" else "Go to Notification Center",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { hasDismissedCompletionPopup = true },
                    modifier = Modifier.testTag("popup_dismiss_button")
                ) {
                    Text(
                        text = if (isArabic) "سأفعل ذلك لاحقاً" else "I'll do it later",
                        color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF5F5E5A),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )
    }
}

@Composable
private fun DiagnosticItemCard(
    item: AlarmReliabilityHelper.DiagnosticItem,
    themeColors: ReadingThemeColors,
    onFix: () -> Unit
) {
    val isDark = themeColors.isDark
    val cardBg = if (isDark) themeColors.surface else Color.White
    val textPrimary = themeColors.arabicText
    val textSecondary = themeColors.translationText

    val isGreen = item.isPassed && !item.isWarning
    val pillBg = when {
        isGreen -> if (isDark) Color(0xFF143825) else StatusGreenBg
        item.isWarning -> if (isDark) Color(0xFF382914) else StatusAmberBg
        else -> if (isDark) Color(0xFF381818) else StatusRedBg
    }
    val pillTint = when {
        isGreen -> if (isDark) Color(0xFF4ADE80) else StatusGreen
        item.isWarning -> if (isDark) Color(0xFFFBBF24) else StatusAmber
        else -> if (isDark) Color(0xFFF87171) else StatusRed
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = cardBg,
        border = null,
        shadowElevation = if (isDark) 0.dp else 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status icon circle
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(pillTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isGreen -> Icons.Default.Check
                            item.isWarning -> Icons.Default.Warning
                            else -> Icons.Default.Close
                        },
                        contentDescription = null,
                        tint = pillTint,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            fontSize = 13.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    // Status Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = pillBg,
                        border = null
                    ) {
                        Text(
                            text = item.statusText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = pillTint,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                // Fix Action Button if needed
                if (item.actionText != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onFix,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) BrandEmeraldDark else BrandEmerald
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("fix_${item.key}_button")
                    ) {
                        Text(
                            text = item.actionText,
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.helpDetails,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = textSecondary,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}
