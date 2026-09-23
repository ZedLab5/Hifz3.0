package com.example.ui.notifications

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.notifications.SpiritualReminderCategory
import com.example.data.notifications.SpiritualReminderItem
import com.example.data.notifications.SpiritualReminderRepository
import com.example.data.notifications.SpiritualReminderState
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.theme.ReadingThemeColors
import java.util.Locale

// Official HomeScreen Palette Rules:
// - Primary Teal: #1BA486 (Icons, active accents, primary fills)
// - Teal Tint Background: #E6F6F1 (Icon backgrounds, soft highlight chips)
// - Soft Navy Text / Link: #2A4365 (Action links, pill text, headings)
// - Soft Navy Pill Background: #EDF2F7 (Action pills, secondary buttons, resting pill badges)
// - Gold Badge Text: #C68A00 (Highlight badges, secondary status pills)
// - Gold Badge Background: #FBF0DC (Warm amber pill container)
// - Background Surface: #FFFFFF (Cards) and #F6F8F7 (Page canvas)
// - Neutral Borders: #ECEFF1
// - Text Primary: #1F1F1F
// - Text Secondary / Inactive: #5F5E5A

private val PrimaryTeal = Color(0xFF1BA486)
private val TealTintBg = Color(0xFFE6F6F1)
private val SoftNavyText = Color(0xFF2A4365)
private val SoftNavyPillBg = Color(0xFFEDF2F7)
private val GoldBadgeText = Color(0xFFC68A00)
private val GoldBadgeBg = Color(0xFFFBF0DC)
private val SurfaceCard = Color(0xFFFFFFFF)
private val CanvasBg = Color(0xFFF6F8F7)
private val NeutralBorder = Color(0xFFECEFF1)
private val TextPrimary = Color(0xFF1F1F1F)
private val TextSecondary = Color(0xFF5F5E5A)

// Dark Mode equivalents adhering to PageColorRegistry standards
private val DarkCanvasBg = Color(0xFF12151A)
private val DarkSurfaceCard = Color(0xFF1A1E24)
private val DarkNeutralBorder = Color(0xFF1E282D)
private val DarkTextPrimary = Color(0xFFE8E6DF)
private val DarkTextSecondary = Color(0xFF8B8D91)
private val DarkPrimaryTeal = Color(0xFF2FBF96)
private val DarkTealTintBg = Color(0xFF1E3A32)
private val DarkSoftNavyText = Color(0xFF2FBF96)
private val DarkSoftNavyPillBg = Color(0xFF1E282D)
private val DarkGoldBadgeText = Color(0xFFD9A44E)
private val DarkGoldBadgeBg = Color(0xFF2A2214)

@Composable
fun NotificationCenterScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appThemeMode by viewModel.appThemeMode.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isArabic = viewModel.isArabicLanguage()

    val remindersMap by viewModel.spiritualRemindersState.collectAsStateWithLifecycle()
    val activeCount by viewModel.activeSpiritualRemindersCount.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    var lifecycleResumeKey by remember { mutableStateOf(0) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                lifecycleResumeKey++
                viewModel.updateNotificationPermissionStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val hasNotificationPermissionSys = remember(context, lifecycleResumeKey) {
        NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
    val hasNotificationPermissionState by viewModel.hasNotificationPermission.collectAsStateWithLifecycle()
    val hasNotificationPermission = hasNotificationPermissionSys || hasNotificationPermissionState
    val isLocationConfigured by viewModel.isLocationConfigured.collectAsStateWithLifecycle()
    val hasFullControlAccess = hasNotificationPermission && isLocationConfigured

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateNotificationPermissionStatus(isGranted)
        lifecycleResumeKey++
    }

    var selectedCategoryFilter by remember { mutableStateOf<SpiritualReminderCategory?>(null) }

    val coroutineScope = rememberCoroutineScope()
    var testCountdown by remember { mutableStateOf(-1) }
    var showFeedbackPrompt by remember { mutableStateOf(false) }
    var testSucceeded by remember { mutableStateOf<Boolean?>(null) }

    val allReminders = remember { SpiritualReminderRepository.reminders }
    val filteredReminders = remember(selectedCategoryFilter, allReminders) {
        if (selectedCategoryFilter == null) allReminders
        else allReminders.filter { it.category == selectedCategoryFilter }
    }

    val canvasBg = if (isDarkMode) DarkCanvasBg else CanvasBg
    val cardBg = if (isDarkMode) DarkSurfaceCard else SurfaceCard
    val cardBorder = if (isDarkMode) DarkNeutralBorder else NeutralBorder
    val textPrimary = if (isDarkMode) DarkTextPrimary else TextPrimary
    val textSecondary = if (isDarkMode) DarkTextSecondary else TextSecondary
    val primaryTeal = if (isDarkMode) DarkPrimaryTeal else PrimaryTeal
    val tealTintBg = if (isDarkMode) DarkTealTintBg else TealTintBg
    val softNavyText = if (isDarkMode) DarkSoftNavyText else SoftNavyText
    val softNavyPillBg = if (isDarkMode) DarkSoftNavyPillBg else SoftNavyPillBg
    val goldBadgeText = if (isDarkMode) DarkGoldBadgeText else GoldBadgeText
    val goldBadgeBg = if (isDarkMode) DarkGoldBadgeBg else GoldBadgeBg

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(canvasBg),
        containerColor = canvasBg,
        topBar = {
            Surface(
                color = canvasBg,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("btn_back_notification_center")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = if (isArabic) "رجوع" else "Back",
                                tint = textPrimary
                            )
                        }
                        Column {
                            Text(
                                text = if (isArabic) "مركز الإشعارات والتذكيرات" else "Notification Center",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = textPrimary
                                )
                            )
                            Text(
                                text = if (isArabic) "$activeCount تذكيرات مفعلة" else "$activeCount Active Reminders",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = textSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    // Diagnostic tools button
                    IconButton(
                        onClick = { viewModel.navigateTo(NoorDestination.NOTIFICATION_TROUBLESHOOTING) },
                        modifier = Modifier.testTag("btn_notification_troubleshooting")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = if (isArabic) "تشخيص الإشعارات" else "Troubleshooting",
                            tint = textSecondary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Master Banner Card (Clean white surface card, calm and unified)
            item(key = "master_banner") {
                if (!hasNotificationPermission) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = cardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(tealTintBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsOff,
                                        contentDescription = null,
                                        tint = primaryTeal,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isArabic) "الإشعارات متوقفة حالياً" else "Notifications are Disabled",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = if (isArabic)
                                            "فعّل الإشعارات لتصلك تنبيهات الأذكار وتذكيرات الصلاة وقراءة القرآن في موعدها."
                                        else
                                            "Enable notifications on your device to receive timely spiritual alerts and prayer reminders.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = textSecondary,
                                            fontSize = 12.5.sp,
                                            lineHeight = 17.sp
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    try {
                                        val intent = Intent().apply {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                            } else {
                                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                                data = Uri.fromParts("package", context.packageName, null)
                                            }
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_enable_notifications_master"),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryTeal,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "تفعيل الإشعارات" else "Enable Notifications",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = cardBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(tealTintBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = primaryTeal,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isArabic) "تحكم كامل في التذكيرات" else "Full Notification Control",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = if (isArabic)
                                            "خصص وقت كل عبادة، وفعّل ما يناسب جدولك اليومي بدون أي استهلاك زائد للبطارية."
                                        else
                                            "Tailor every spiritual alert to your schedule. Runs with 0% idle background CPU.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = textSecondary,
                                            fontSize = 12.5.sp
                                        )
                                    )
                                }
                            }

                            // Master Action Buttons (Rounded Pills)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.muteAllSpiritualReminders() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_mute_all_reminders"),
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = softNavyPillBg,
                                        contentColor = softNavyText
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = softNavyText
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isArabic) "كتم الكل" else "Mute All",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = softNavyText
                                        )
                                    )
                                }

                                Button(
                                    onClick = { viewModel.restoreRecommendedSpiritualReminders() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_restore_recommended_reminders"),
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primaryTeal,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RestartAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isArabic) "الموصى به" else "Recommended",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Universal Test Notification & Verification Section
            item(key = "universal_test_section") {
                val successBg = if (isDarkMode) Color(0xFF1B3B2B) else Color(0xFFE6F4EA)
                val successText = if (isDarkMode) Color(0xFF81C784) else Color(0xFF137333)
                val alertBg = if (isDarkMode) Color(0xFF3C3014) else Color(0xFFFEF7E0)
                val alertText = if (isDarkMode) Color(0xFFFFD54F) else Color(0xFFB06000)

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        testCountdown > 0 -> alertBg
                        testSucceeded == true -> successBg
                        else -> cardBg
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        when {
                            testCountdown > 0 -> alertText.copy(alpha = 0.3f)
                            testSucceeded == true -> successText.copy(alpha = 0.3f)
                            else -> cardBorder
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (hasNotificationPermission) 1f else 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            testCountdown > 0 -> alertText.copy(alpha = 0.15f)
                                            testSucceeded == true -> successText.copy(alpha = 0.15f)
                                            else -> tealTintBg
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        testCountdown > 0 -> Icons.Default.Timer
                                        testSucceeded == true -> Icons.Default.CheckCircle
                                        else -> Icons.Default.Settings
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        testCountdown > 0 -> alertText
                                        testSucceeded == true -> successText
                                        else -> primaryTeal
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "فحص واختبار النظام الشامل" else "Universal System Verification",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            testCountdown > 0 -> alertText
                                            testSucceeded == true -> successText
                                            else -> textPrimary
                                        },
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = if (isArabic) {
                                        "جميع الإشعارات تعمل بنفس الطريقة. اختبر النظام الآن للتأكد من ظهورها على شاشة القفل."
                                    } else {
                                        "All notification alerts use the same system core. Test now to confirm lockscreen visibility."
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = when {
                                            testCountdown > 0 -> alertText.copy(alpha = 0.8f)
                                            testSucceeded == true -> successText.copy(alpha = 0.8f)
                                            else -> textSecondary
                                        },
                                        fontSize = 12.5.sp
                                    )
                                )
                            }
                        }

                        // State Renderings
                        when {
                            testCountdown > 0 -> {
                                Text(
                                    text = if (isArabic) {
                                        "سيتم إطلاق إشعار الفحص خلال $testCountdown ثوانٍ. أغلق شاشتك فوراً الآن! ⏰"
                                    } else {
                                        "Triggering universal test in $testCountdown seconds... LOCK YOUR PHONE NOW! ⏰"
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = alertText,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            showFeedbackPrompt -> {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isArabic) "هل ظهر إشعار الفحص بنجاح على شاشة القفل؟" else "Did the test notification show up on your lockscreen?",
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
                                                viewModel.showToast(if (isArabic) "رائع! تم التحقق بنجاح 🎉" else "Awesome! System is verified 🎉")
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(50),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = primaryTeal,
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
                                                text = if (isArabic) "نعم، نجح!" else "Yes, it worked!",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                showFeedbackPrompt = false
                                                testSucceeded = false
                                                // Navigate immediately to diagnostic & troubleshooting screen
                                                viewModel.navigateTo(NoorDestination.NOTIFICATION_TROUBLESHOOTING)
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(50),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = softNavyPillBg,
                                                contentColor = softNavyText
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = softNavyText
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isArabic) "لا، ساعدني بالإصلاح" else "No, help me fix it",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = softNavyText
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            testSucceeded == true -> {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isArabic) {
                                            "تم فحص نظام التنبيهات بنجاح وهو الآن جاهز بنسبة 100% للعمل على شاشة القفل."
                                        } else {
                                            "System status verified. Notifications are 100% active and working on your lockscreen."
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = successText,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                    )

                                    OutlinedButton(
                                        onClick = {
                                            testSucceeded = null
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = successText
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, successText.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = if (isArabic) "إعادة الاختبار" else "Test Again",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                    }
                                }
                            }

                            else -> {
                                Button(
                                    onClick = {
                                        if (hasNotificationPermission) {
                                            testSucceeded = null
                                            testCountdown = 10
                                            coroutineScope.launch {
                                                while (testCountdown > 0) {
                                                    delay(1000)
                                                    testCountdown -= 1
                                                }
                                                // Once countdown completes, trigger the notification and show prompt
                                                com.example.data.local.NoorNotificationHelper.showUniversalTestNotification(context)
                                                viewModel.triggerHaptic()
                                                showFeedbackPrompt = true
                                            }
                                        }
                                    },
                                    enabled = hasNotificationPermission,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primaryTeal,
                                        contentColor = Color.White,
                                        disabledContainerColor = softNavyPillBg,
                                        disabledContentColor = textSecondary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = if (hasNotificationPermission) Color.White else textSecondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isArabic) "إطلاق فحص شامل (10 ثوانٍ)" else "Launch Universal Test (10s)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Category Filter Chips (Consistent styling across all chips)
            item(key = "filter_chips") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (hasNotificationPermission) 1f else 0.5f)
                ) {
                    Text(
                        text = if (isArabic) "التصنيفات" else "Categories",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = textSecondary,
                            fontSize = 13.sp
                        )
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategoryFilter == null,
                                onClick = { if (hasNotificationPermission) selectedCategoryFilter = null },
                                enabled = hasNotificationPermission,
                                shape = RoundedCornerShape(50),
                                label = {
                                    Text(
                                        text = if (isArabic) "الكل (${allReminders.size})" else "All (${allReminders.size})",
                                        fontWeight = if (selectedCategoryFilter == null) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryTeal,
                                    selectedLabelColor = Color.White,
                                    containerColor = softNavyPillBg,
                                    labelColor = softNavyText
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = hasNotificationPermission,
                                    selected = selectedCategoryFilter == null,
                                    borderColor = cardBorder,
                                    selectedBorderColor = Color.Transparent
                                )
                            )
                        }

                        items(SpiritualReminderCategory.entries.toTypedArray()) { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            val count = allReminders.count { it.category == cat }

                            FilterChip(
                                selected = isSelected,
                                onClick = { if (hasNotificationPermission) selectedCategoryFilter = if (isSelected) null else cat },
                                enabled = hasNotificationPermission,
                                shape = RoundedCornerShape(50),
                                label = {
                                    Text(
                                        text = "${if (isArabic) cat.titleAr else cat.titleEn} ($count)",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryTeal,
                                    selectedLabelColor = Color.White,
                                    containerColor = softNavyPillBg,
                                    labelColor = softNavyText
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = hasNotificationPermission,
                                    selected = isSelected,
                                    borderColor = cardBorder,
                                    selectedBorderColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }

            // Reminders List
            items(filteredReminders, key = { it.id }) { item ->
                val state = remindersMap[item.id] ?: SpiritualReminderState(
                    isEnabled = item.defaultEnabled,
                    hour = item.defaultHour,
                    minute = item.defaultMinute
                )

                SpiritualReminderCard(
                    item = item,
                    state = state,
                    isEnabled = hasNotificationPermission,
                    isArabic = isArabic,
                    isDarkMode = isDarkMode,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    primaryTeal = primaryTeal,
                    tealTintBg = tealTintBg,
                    softNavyText = softNavyText,
                    softNavyPillBg = softNavyPillBg,
                    goldBadgeText = goldBadgeText,
                    goldBadgeBg = goldBadgeBg,
                    onToggle = { enabled ->
                        viewModel.toggleSpiritualReminder(item.id, enabled)
                    },
                    onTimeChange = { newHour, newMinute ->
                        viewModel.updateSpiritualReminderTime(item.id, newHour, newMinute)
                    }
                )

                if (item.id == "tahajjud_qiyam") {
                    Spacer(modifier = Modifier.height(6.dp))
                    QiyamReminderOffsetSection(
                        viewModel = viewModel,
                        isArabic = isArabic,
                        isDarkMode = isDarkMode,
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        primaryTeal = primaryTeal,
                        tealTintBg = tealTintBg,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        softNavyPillBg = softNavyPillBg
                    )
                }
            }

            // Bottom Helpful Note Card
            item(key = "salat_and_diagnostics_footer") {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = cardBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 24.dp)
                        .alpha(if (hasNotificationPermission) 1f else 0.55f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = primaryTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isArabic) "تنبيهات مواقيت الصلاة والأذان" else "Salat Times & Adhan Audio",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            )
                        }
                        Text(
                            text = if (isArabic)
                                "لضبط أصوات الأذان والتنبيهات المسبقة لكل صلاة مفردة (الفجر، الظهر، العصر، المغرب، العشاء)، يمكنك الدخول إلى شاشة مواقيت الصلاة."
                            else
                                "To manage custom Adhan audio reciters and pre-salat reminders for individual daily prayers, visit the Salat Times screen.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = textSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpiritualReminderCard(
    item: SpiritualReminderItem,
    state: SpiritualReminderState,
    isEnabled: Boolean = true,
    isArabic: Boolean,
    isDarkMode: Boolean,
    cardBg: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    primaryTeal: Color,
    tealTintBg: Color,
    softNavyText: Color,
    softNavyPillBg: Color,
    goldBadgeText: Color,
    goldBadgeBg: Color,
    onToggle: (Boolean) -> Unit,
    onTimeChange: (Int, Int) -> Unit
) {
    var showTimePickerDialog by remember { mutableStateOf(false) }

    if (showTimePickerDialog && isEnabled) {
        ReminderTimePickerDialog(
            title = if (isArabic) item.titleAr else item.titleEn,
            initialHour = state.hour,
            initialMinute = state.minute,
            isArabic = isArabic,
            cardBg = cardBg,
            cardBorder = cardBorder,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            primaryTeal = primaryTeal,
            tealTintBg = tealTintBg,
            softNavyPillBg = softNavyPillBg,
            canvasBg = if (isDarkMode) DarkCanvasBg else CanvasBg,
            onDismiss = { showTimePickerDialog = false },
            onConfirm = { newHour, newMinute ->
                onTimeChange(newHour, newMinute)
                showTimePickerDialog = false
            }
        )
    }

    val icon = remember(item.category, item.id) {
        when {
            item.id == SpiritualReminderRepository.ID_HIFZ_REVISION -> Icons.AutoMirrored.Filled.MenuBook
            item.id == SpiritualReminderRepository.ID_KHATMA_TARGET -> Icons.Default.AutoStories
            item.id == SpiritualReminderRepository.ID_FRIDAY_KAHF -> Icons.Default.Star
            item.id == SpiritualReminderRepository.ID_MORNING_AZKAR -> Icons.Default.WbSunny
            item.id == SpiritualReminderRepository.ID_EVENING_AZKAR -> Icons.Default.WbTwilight
            item.id == SpiritualReminderRepository.ID_BEDTIME_AZKAR -> Icons.Default.AccessTime
            item.id == SpiritualReminderRepository.ID_DAILY_AYAH -> Icons.Default.AutoStories
            item.id == SpiritualReminderRepository.ID_TAHAJJUD_QIYAM -> Icons.Default.AccessTime
            item.id == SpiritualReminderRepository.ID_SALAT_DUHA -> Icons.Default.WbSunny
            item.id == SpiritualReminderRepository.ID_SUNNAH_FASTING -> Icons.Default.Star
            item.id == SpiritualReminderRepository.ID_QAZA_REMINDER -> Icons.Default.Timeline
            item.id == SpiritualReminderRepository.ID_STREAK_SAFEGUARD -> Icons.Default.Timeline
            else -> Icons.Default.Notifications
        }
    }

    val iconBgColor = if (state.isEnabled && isEnabled) tealTintBg else softNavyPillBg
    val iconTint = if (state.isEnabled && isEnabled) primaryTeal else textSecondary

    val formattedTime = remember(state.hour, state.minute) {
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, state.hour)
            set(java.util.Calendar.MINUTE, state.minute)
        }
        val format = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
        format.format(cal.time)
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = cardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEnabled) 1f else 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main Info Header with Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(iconBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.padding(end = 8.dp)) {
                        Text(
                            text = if (isArabic) item.titleAr else item.titleEn,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (state.isEnabled && isEnabled) textPrimary else textSecondary,
                                fontSize = 15.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isArabic) item.category.titleAr else item.category.titleEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = textSecondary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Switch(
                    checked = state.isEnabled,
                    onCheckedChange = onToggle,
                    enabled = isEnabled,
                    modifier = Modifier.testTag("switch_${item.id}"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = primaryTeal,
                        uncheckedThumbColor = textSecondary,
                        uncheckedTrackColor = softNavyPillBg
                    )
                )
            }

            // Description
            Text(
                text = if (isArabic) item.descriptionAr else item.descriptionEn,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = textSecondary,
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp
                )
            )

            // Permanent Time Pill
            val actionContentColor = if (state.isEnabled && isEnabled) softNavyText else textSecondary

            Surface(
                shape = RoundedCornerShape(50),
                color = softNavyPillBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(enabled = isEnabled) { showTimePickerDialog = true }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = actionContentColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = actionContentColor,
                            fontSize = 13.sp
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = if (isArabic) "تعديل" else "Edit",
                        tint = actionContentColor,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    isArabic: Boolean,
    cardBg: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    primaryTeal: Color,
    tealTintBg: Color,
    softNavyPillBg: Color,
    canvasBg: Color,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(24.dp),
            color = cardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        fontSize = 17.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = if (isArabic) "اختر وقت التذكير المناسب لك" else "Choose your preferred reminder time",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = textSecondary,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = canvasBg,
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = textPrimary,
                        selectorColor = primaryTeal,
                        periodSelectorBorderColor = primaryTeal,
                        periodSelectorSelectedContainerColor = tealTintBg,
                        periodSelectorUnselectedContainerColor = Color.Transparent,
                        periodSelectorSelectedContentColor = primaryTeal,
                        periodSelectorUnselectedContentColor = textSecondary,
                        timeSelectorSelectedContainerColor = tealTintBg,
                        timeSelectorUnselectedContainerColor = softNavyPillBg,
                        timeSelectorSelectedContentColor = primaryTeal,
                        timeSelectorUnselectedContentColor = textSecondary
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = if (isArabic) "إلغاء" else "Cancel",
                            color = textSecondary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(timePickerState.hour, timePickerState.minute)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryTeal,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = if (isArabic) "تأكيد" else "Confirm",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QiyamReminderOffsetSection(
    viewModel: com.example.ui.MainViewModel,
    isArabic: Boolean,
    isDarkMode: Boolean,
    cardBg: Color,
    cardBorder: Color,
    primaryTeal: Color,
    tealTintBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    softNavyPillBg: Color
) {
    val qiyamOffset by viewModel.qiyamReminderOffsetMinutes.collectAsStateWithLifecycle()
    val offsetAbs = Math.abs(qiyamOffset)
    val timeLabel = if (qiyamOffset <= 0) "$offsetAbs min before Fajr" else "$offsetAbs min after Fajr"
    val timeLabelAr = if (qiyamOffset <= 0) "قبل الفجر بـ $offsetAbs دقيقة" else "بعد الفجر بـ $offsetAbs دقيقة"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
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
                        text = if (isArabic) "تعديل تنبيه قيام الليل" else "Tahajjud Reminder Offset",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    )
                    Text(
                        text = if (isArabic) "ضبط موعد تنبيه قيام الليل بالنسبة لصلاة الفجر ($timeLabelAr)" else "Adjust Tahajjud reminder time relative to Fajr ($timeLabel)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = textSecondary,
                            fontSize = 11.5.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = primaryTeal.copy(alpha = 0.12f),
                    border = null,
                    modifier = Modifier.clickable { viewModel.resetQiyamOffset() }
                ) {
                    Text(
                        text = if (isArabic) "إعادة ضبط" else "Reset",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = primaryTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
            }

            // Adjustment pill buttons (-15m, -5m, +5m, +15m)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(-15, -5, 5, 15).forEach { delta ->
                    val label = if (delta > 0) "+${delta}m" else "${delta}m"
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.updateQiyamOffset(delta) },
                        shape = RoundedCornerShape(12.dp),
                        color = softNavyPillBg,
                        border = null
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary,
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
