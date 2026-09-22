package com.example.ui.onboarding

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerZone
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A beautiful, highly-polished 4-page onboarding flow for Noor.
 * Supports English/Arabic localization on-the-fly, theme live previews, focus selections,
 * and smart permissions fallback with an inline searchable zone list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    zones: List<PrayerZone>,
    requestNotifications: () -> Unit,
    requestLocation: () -> Unit,
    onComplete: (
        focusSelections: Set<String>,
        language: String,
        theme: String,
        requestNotifications: () -> Unit,
        requestLocation: () -> Unit
    ) -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("noor_prefs", Context.MODE_PRIVATE) }
    val coroutineScope = rememberCoroutineScope()

    // 1. App Languages & State
    var appLanguage by remember {
        mutableStateOf(sharedPrefs.getString("app_language", "English") ?: "English")
    }
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) || 
                   appLanguage == "العربية" || 
                   appLanguage.startsWith("ar", ignoreCase = true)

    // 2. Theme Live Preview State
    var activeThemeName by remember {
        mutableStateOf(sharedPrefs.getString("shared_reading_theme", "Madani Crisp") ?: "Madani Crisp")
    }

    // Dynamic color selection matching Noor colors based on activeThemeName preview
    val (themeBg, themeCard, themeText, themeSecondaryText, themeBorder, themeAccent) = remember(activeThemeName) {
        when (activeThemeName) {
            "Obsidian Night" -> HexThemeColors(
                background = Color(0xFF12151A),
                card = Color(0xFF1A1E24),
                text = Color(0xFFE8E6DF),
                secondaryText = Color(0xFF8B8D91),
                border = Color(0xFF26333C),
                accent = Color(0xFF2FBF96)
            )
            "Warm Parchment", "Sepia Parchment" -> HexThemeColors(
                background = Color(0xFFFCFBF9),
                card = Color(0xFFFFFFFF),
                text = Color(0xFF2C2C2A),
                secondaryText = Color(0xFF7A6650),
                border = Color(0xFFEDE0C8),
                accent = Color(0xFFD9A44E)
            )
            else -> HexThemeColors(
                background = Color(0xFFF6F8F7),
                card = Color(0xFFFFFFFF),
                text = Color(0xFF1F1F1F),
                secondaryText = Color(0xFF5F5E5A),
                border = Color(0xFFECEFF1),
                accent = Color(0xFF1BA486)
            )
        }
    }

    // 3. Selection & State engines
    var selectedFocus by remember { mutableStateOf(setOf<String>()) }
    var selectedZoneId by remember { 
        mutableStateOf(sharedPrefs.getString("selected_prayer_zone_id", null)) 
    }

    // 4. Permission visual toggle states
    var locationDeclinedOrNotNow by remember { mutableStateOf(false) }
    var notificationsAllowed by remember { mutableStateOf<Boolean?>(null) }
    var locationAllowed by remember { mutableStateOf<Boolean?>(null) }

    // 5. Layout Direction Provider (RTL for Arabic, LTR for English)
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        val pagerState = rememberPagerState(pageCount = { 4 })

        Scaffold(
            containerColor = themeBg,
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Top corner Skip Button (Shown on page 1, 2, 3 only)
                if (pagerState.currentPage < 3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = if (isArabic) Alignment.TopStart else Alignment.TopEnd
                    ) {
                        Text(
                            text = if (isArabic) "تخطي" else "Skip",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeAccent,
                                fontSize = 15.sp
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSkip() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Main Pager content
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = true,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (page) {
                            0 -> WelcomePage(
                                isArabic = isArabic,
                                themeAccent = themeAccent,
                                themeText = themeText,
                                themeSecondaryText = themeSecondaryText,
                                themeCard = themeCard,
                                themeBorder = themeBorder,
                                onGetStarted = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(1)
                                    }
                                }
                            )

                            1 -> PickFocusPage(
                                isArabic = isArabic,
                                themeAccent = themeAccent,
                                themeText = themeText,
                                themeSecondaryText = themeSecondaryText,
                                themeCard = themeCard,
                                themeBorder = themeBorder,
                                selectedFocus = selectedFocus,
                                onToggleFocus = { id ->
                                    selectedFocus = if (selectedFocus.contains(id)) {
                                        selectedFocus - id
                                    } else {
                                        selectedFocus + id
                                    }
                                },
                                onContinue = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(2)
                                    }
                                }
                            )

                            2 -> ReadingStylePage(
                                isArabic = isArabic,
                                currentLanguage = appLanguage,
                                activeThemeName = activeThemeName,
                                themeAccent = themeAccent,
                                themeText = themeText,
                                themeSecondaryText = themeSecondaryText,
                                themeCard = themeCard,
                                themeBorder = themeBorder,
                                onLanguageChange = { lang ->
                                    appLanguage = lang
                                    sharedPrefs.edit().putString("app_language", lang).apply()
                                },
                                onThemeChange = { theme ->
                                    activeThemeName = theme
                                    sharedPrefs.edit().putString("shared_reading_theme", theme).apply()
                                    // Save corresponding AppThemeMode in SharedPreferences for deep consistency
                                    val modeStr = when (theme) {
                                        "Obsidian Night" -> "Obsidian Night"
                                        "Warm Parchment", "Sepia Parchment" -> "Warm Parchment"
                                        else -> "Madani Crisp"
                                    }
                                    sharedPrefs.edit().putString("shared_reading_theme", modeStr).apply()
                                },
                                onContinue = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(3)
                                    }
                                }
                            )

                            3 -> StayOnTimePage(
                                isArabic = isArabic,
                                zones = zones,
                                themeAccent = themeAccent,
                                themeText = themeText,
                                themeSecondaryText = themeSecondaryText,
                                themeCard = themeCard,
                                themeBorder = themeBorder,
                                selectedZoneId = selectedZoneId,
                                onSelectZone = { zone ->
                                    selectedZoneId = zone.id
                                    sharedPrefs.edit().putString("selected_prayer_zone_id", zone.id).apply()
                                },
                                notificationsAllowed = notificationsAllowed,
                                locationAllowed = locationAllowed,
                                locationDeclinedOrNotNow = locationDeclinedOrNotNow,
                                onRequestNotifications = {
                                    notificationsAllowed = true
                                    requestNotifications()
                                },
                                onDeclineNotifications = {
                                    notificationsAllowed = false
                                },
                                onRequestLocation = {
                                    locationAllowed = true
                                    requestLocation()
                                },
                                onDeclineLocation = {
                                    locationAllowed = false
                                    locationDeclinedOrNotNow = true
                                },
                                onStartNoor = {
                                    onComplete(
                                        selectedFocus,
                                        appLanguage,
                                        activeThemeName,
                                        requestNotifications,
                                        requestLocation
                                    )
                                }
                            )
                        }
                    }
                }

                // Progress Indicator Dots at the bottom
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { idx ->
                            val isSelected = pagerState.currentPage == idx
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 10.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) themeAccent else themeText.copy(alpha = 0.25f)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Welcome Screen Page */
@Composable
private fun WelcomePage(
    isArabic: Boolean,
    themeAccent: Color,
    themeText: Color,
    themeSecondaryText: Color,
    themeCard: Color,
    themeBorder: Color,
    onGetStarted: () -> Unit
) {
    var animStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        animStarted = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // App Logo Icon
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(themeAccent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = themeAccent,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Intent Title
        Text(
            text = if (isArabic) "مرحباً بك في نور" else "Welcome to Noor",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = themeText,
                textAlign = TextAlign.Center,
                fontSize = 28.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (isArabic) 
                "رفيقك الإيماني الشامل لقراءة وحفظ القرآن الكريم وتتبع صلواتك اليومية."
                else "Your custom spiritual companion for reading, memorizing Quran, and tracking daily prayers.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = themeSecondaryText,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(44.dp))

        // Three fading icon-labels
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            WelcomeBadge(
                visible = animStarted,
                icon = Icons.Default.AutoStories,
                label = if (isArabic) "قرآن كريم" else "Read Quran",
                themeAccent = themeAccent,
                themeCard = themeCard,
                themeBorder = themeBorder,
                themeText = themeText,
                modifier = Modifier.weight(1f)
            )

            WelcomeBadge(
                visible = animStarted,
                icon = Icons.Default.NotificationsActive,
                label = if (isArabic) "تنبيهات الصلاة" else "Salah Alerts",
                themeAccent = themeAccent,
                themeCard = themeCard,
                themeBorder = themeBorder,
                themeText = themeText,
                modifier = Modifier.weight(1f)
            )

            WelcomeBadge(
                visible = animStarted,
                icon = Icons.Default.Star,
                label = if (isArabic) "حفظ ومتابعة" else "Hifz Track",
                themeAccent = themeAccent,
                themeCard = themeCard,
                themeBorder = themeBorder,
                themeText = themeText,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onGetStarted,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = themeAccent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = if (isArabic) "ابدأ الآن" else "Get Started",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
private fun WelcomeBadge(
    visible: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    themeAccent: Color,
    themeCard: Color,
    themeBorder: Color,
    themeText: Color,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = themeCard,
            border = BorderStroke(1.dp, themeBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(themeAccent.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = themeAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeText,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

/** Page 2 - Pick your Focus Multi-Select Cards */
@Composable
private fun PickFocusPage(
    isArabic: Boolean,
    themeAccent: Color,
    themeText: Color,
    themeSecondaryText: Color,
    themeCard: Color,
    themeBorder: Color,
    selectedFocus: Set<String>,
    onToggleFocus: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isArabic) "حدد اهتماماتك الأساسية" else "Pick Your Focus",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeText,
                    fontSize = 24.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "اختر ما ترغب بالتركيز عليه لتخصيص تجربتك الإيمانية" else "Select what you want to focus on to personalize your experience.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeSecondaryText,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val cards = listOf(
            FocusCardItem("memorize", if (isArabic) "حفظ القرآن الكريم" else "Memorize the Quran", if (isArabic) "أدوات تيسير الحفظ والتكرار والمتابعة" else "Easily memorize, repeat, and track progress", Icons.Default.AutoStories),
            FocusCardItem("miss_prayer", if (isArabic) "المحافظة على الصلاة" else "Never miss a prayer", if (isArabic) "تنبيهات وأدوات للالتزام بالصلوات" else "Custom alerts & historical logs to stay consistent", Icons.Default.Notifications),
            FocusCardItem("dhikr", if (isArabic) "الأذكار والأدعية اليومية" else "Daily dhikr & duas", if (isArabic) "مسبحة الكترونية وتذكيرات بالصلاة على النبي" else "Count your daily tasbih, morning & evening azkar", Icons.Default.Check),
            FocusCardItem("track", if (isArabic) "متابعة تقدمي الإيماني" else "Track my progress", if (isArabic) "إحصائيات تفصيلية وسجلات تاريخية" else "Advanced logs, metrics, and consistent milestones", Icons.Default.Timeline)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            cards.forEach { item ->
                val isSelected = selectedFocus.contains(item.id)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) themeAccent.copy(alpha = 0.08f) else themeCard,
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) themeAccent else themeBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleFocus(item.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) themeAccent.copy(alpha = 0.2f) else themeBorder
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = if (isSelected) themeAccent else themeSecondaryText,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeText,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = themeSecondaryText,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = themeAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(themeBorder)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onContinue,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = themeAccent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = if (isArabic) "متابعة" else "Continue",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

private data class FocusCardItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

/** Page 3 - Reading Style Selection with Live Swatch Previews */
@Composable
private fun ReadingStylePage(
    isArabic: Boolean,
    currentLanguage: String,
    activeThemeName: String,
    themeAccent: Color,
    themeText: Color,
    themeSecondaryText: Color,
    themeCard: Color,
    themeBorder: Color,
    onLanguageChange: (String) -> Unit,
    onThemeChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isArabic) "تخصيص العرض والقراءة" else "Reading & Interface Style",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeText,
                    fontSize = 24.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "اختر لغتك المفضلة والمظهر الأنسب لعينيك أثناء القراءة والورد اليومي" else "Select your preferred language and reading theme for optimal comfort.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeSecondaryText,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 1. Language Toggle Segment
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = themeCard,
            border = BorderStroke(1.dp, themeBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "لغة التطبيق" else "Application Language",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeText
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isEng = currentLanguage.equals("English", ignoreCase = true)
                    
                    // English Button
                    Button(
                        onClick = { onLanguageChange("English") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEng) themeAccent else themeBorder,
                            contentColor = if (isEng) Color.White else themeText
                        )
                    ) {
                        Text(
                            text = "English",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isEng) Color.White else themeText
                            )
                        )
                    }

                    // Arabic Button
                    Button(
                        onClick = { onLanguageChange("Arabic") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isEng) themeAccent else themeBorder,
                            contentColor = if (!isEng) Color.White else themeText
                        )
                    ) {
                        Text(
                            text = "العربية",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (!isEng) Color.White else themeText
                            )
                        )
                    }
                }
            }
        }

        // 2. Three Theme Swatches Section
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = themeCard,
            border = BorderStroke(1.dp, themeBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "مظهر صفحات القرآن والقراءة" else "Reading Canvas Theme",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeText
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isArabic) "سيقوم تغيير المظهر بتحديث خلفية هذه الصفحة مباشرة للتجربة" else "Tapping any swatch immediately applies a live preview of the screen style.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = themeSecondaryText,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                val themes = listOf(
                    ThemeSwatchItem("Madani Crisp", if (isArabic) "الوضع النهاري" else "Crisp Light", Color(0xFFF6F8F7), Color(0xFFFFFFFF), Color(0xFF1BA486)),
                    ThemeSwatchItem("Warm Parchment", if (isArabic) "الوضع الدافئ" else "Warm Parchment", Color(0xFFFCFBF9), Color(0xFFFAF6EE), Color(0xFFD9A44E)),
                    ThemeSwatchItem("Obsidian Night", if (isArabic) "الوضع الداكن" else "Obsidian Dark", Color(0xFF12151A), Color(0xFF1A1E24), Color(0xFF2FBF96))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    themes.forEach { swatch ->
                        val isSelected = activeThemeName == swatch.id || 
                                         (activeThemeName == "Sepia Parchment" && swatch.id == "Warm Parchment")
                        
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(swatch.bg)
                                .clickable { onThemeChange(swatch.id) }
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(swatch.accent),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = swatch.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (swatch.id == "Obsidian Night") Color.White else Color(0xFF1F1F1F),
                                    fontSize = 11.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onContinue,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = themeAccent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = if (isArabic) "متابعة" else "Continue",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

private data class ThemeSwatchItem(
    val id: String,
    val label: String,
    val bg: Color,
    val card: Color,
    val accent: Color
)

/** Page 4 - Stay on Time Permissions Setup & Searchable Zones Backup */
@Composable
private fun StayOnTimePage(
    isArabic: Boolean,
    zones: List<PrayerZone>,
    themeAccent: Color,
    themeText: Color,
    themeSecondaryText: Color,
    themeCard: Color,
    themeBorder: Color,
    selectedZoneId: String?,
    onSelectZone: (PrayerZone) -> Unit,
    notificationsAllowed: Boolean?,
    locationAllowed: Boolean?,
    locationDeclinedOrNotNow: Boolean,
    onRequestNotifications: () -> Unit,
    onDeclineNotifications: () -> Unit,
    onRequestLocation: () -> Unit,
    onDeclineLocation: () -> Unit,
    onStartNoor: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredZones = remember(searchQuery, zones) {
        if (searchQuery.isBlank()) {
            zones
        } else {
            zones.filter { zone ->
                zone.name.contains(searchQuery, ignoreCase = true) ||
                zone.arabicName.contains(searchQuery, ignoreCase = true) ||
                zone.country.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isArabic) "تفعيل التنبيهات والموقع" else "Stay Connected & Accurate",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeText,
                    fontSize = 24.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) "قم بإعداد التنبيهات لتصلك الإشعارات في مواقيتها الدقيقة تماماً" else "Enable alerts and coordinate adjustments for precision prayer times.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeSecondaryText,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Stacked Permissions (If not explicitly customized)
        if (!locationDeclinedOrNotNow) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // 1. Notifications Permission Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = themeCard,
                    border = BorderStroke(1.dp, themeBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(themeAccent.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = themeAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isArabic) "تنبيهات الآذان والإشعارات" else "Adhan & Reminders",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeText,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = if (isArabic) "لتلقي التنبيهات الإيمانية وصلاة الجمعة والمناسبات." else "Receive alerts for adhan, azkar, and custom reminders.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeSecondaryText,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onRequestNotifications,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (notificationsAllowed == true) themeAccent.copy(alpha = 0.2f) else themeAccent,
                                    contentColor = if (notificationsAllowed == true) themeAccent else Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (notificationsAllowed == true) (if (isArabic) "تم التفعيل" else "Allowed") else (if (isArabic) "تفعيل" else "Allow"),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            OutlinedButton(
                                onClick = onDeclineNotifications,
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, themeBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = themeSecondaryText),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isArabic) "ليس الآن" else "Not Now",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                        }
                    }
                }

                // 2. Location Permission Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = themeCard,
                    border = BorderStroke(1.dp, themeBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(themeAccent.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = themeAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isArabic) "تحديد الموقع التلقائي" else "Auto-Location Settings",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeText,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = if (isArabic) "نستخدم موقعك لحساب مواقيت الصلاة الدقيقة لمنطقتك." else "Used to auto-detect and update your coordinates for prayer times.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = themeSecondaryText,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onRequestLocation,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (locationAllowed == true) themeAccent.copy(alpha = 0.2f) else themeAccent,
                                    contentColor = if (locationAllowed == true) themeAccent else Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (locationAllowed == true) (if (isArabic) "تم التفعيل" else "Allowed") else (if (isArabic) "تفعيل" else "Allow"),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            OutlinedButton(
                                onClick = onDeclineLocation,
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, themeBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = themeSecondaryText),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isArabic) "ليس الآن" else "Not Now",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Inline Searchable Prayer Zone Selector Backup (Revealed if "Not now" or denied)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = themeCard,
                border = BorderStroke(1.dp, themeBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isArabic) "اختر منطقتك الجغرافية يدوياً" else "Select Your Region Manually",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeText,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isArabic) "تلقينا طلبك بعدم استخدام الموقع تلقائياً. يرجى اختيار بلدتك للحفاظ على توقيت صحيح:" else "To ensure correct adhan times without GPS, pick your nearest prayer zone standard:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeSecondaryText,
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { 
                            Text(
                                if (isArabic) "ابحث عن المدينة أو الدولة..." else "Search city or country...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = themeSecondaryText)
                            ) 
                        },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Search, 
                                contentDescription = null, 
                                tint = themeSecondaryText,
                                modifier = Modifier.size(20.dp)
                            ) 
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = themeText,
                            unfocusedTextColor = themeText,
                            focusedBorderColor = themeAccent,
                            unfocusedBorderColor = themeBorder
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredZones) { zone ->
                            val isSelected = selectedZoneId == zone.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) themeAccent.copy(alpha = 0.08f) else Color.Transparent,
                                border = if (isSelected) BorderStroke(1.5.dp, themeAccent) else BorderStroke(1.dp, themeBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectZone(zone) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = if (isArabic) zone.arabicName else zone.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = themeText
                                            )
                                        )
                                        Text(
                                            text = "${zone.country} • ${zone.zoneLabel}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = themeSecondaryText,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = themeAccent,
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

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onStartNoor,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = themeAccent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = if (isArabic) "ابدأ باستخدام نور" else "Start Using Noor",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

/** Pure helper data holder to represent style values dynamically */
private data class HexThemeColors(
    val background: Color,
    val card: Color,
    val text: Color,
    val secondaryText: Color,
    val border: Color,
    val accent: Color
)
