package com.example.ui.onboarding

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerZone
import kotlinx.coroutines.launch

// Official App Color Tokens
private val TealPrimary = Color(0xFF1BA486)
private val TealTintBg = Color(0xFFE6F6F1)
private val SoftNavyText = Color(0xFF2A4365)
private val SoftNavyPillBg = Color(0xFFEDF2F7)
private val GoldBadgeText = Color(0xFFC68A00)
private val GoldBadgeBg = Color(0xFFFBF0DC)
private val SurfaceCardLight = Color(0xFFFFFFFF)
private val CanvasPageLight = Color(0xFFF6F8F7)
private val NeutralBorder = Color(0xFFECEFF1)
private val TextPrimaryLight = Color(0xFF1F1F1F)
private val TextSecondaryLight = Color(0xFF5F5E5A)

/**
 * Creative, highly polished 4-page onboarding walkthrough for Noor.
 * - Skip button exits onboarding flow directly on pages 1-3.
 * - Language picker floats in a non-disruptive Dropdown overlay above the screen.
 * - Notifications and Location permissions are completely separate, dedicated cards.
 * - Interactive live theme switcher with instant visual try-on.
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

    // 1. Language State
    var appLanguage by remember {
        mutableStateOf(sharedPrefs.getString("app_language", "English") ?: "English")
    }
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    // 2. Theme State (Live preview colors)
    var activeThemeName by remember {
        mutableStateOf(sharedPrefs.getString("shared_reading_theme", "Madani Crisp") ?: "Madani Crisp")
    }

    val dynamicPalette = remember(activeThemeName) {
        when (activeThemeName) {
            "Obsidian Night" -> OnboardingPalette(
                background = Color(0xFF12151A),
                card = Color(0xFF1A1E24),
                cardSecondary = Color(0xFF232830),
                textPrimary = Color(0xFFE8E6DF),
                textSecondary = Color(0xFF9E9E9E),
                border = Color(0xFF2C323B),
                accent = Color(0xFF2FBF96),
                accentTint = Color(0xFF1B3830),
                isDark = true
            )
            "Warm Parchment", "Sepia Parchment" -> OnboardingPalette(
                background = Color(0xFFFCFBF9),
                card = Color(0xFFFFFFFF),
                cardSecondary = Color(0xFFF7F3EB),
                textPrimary = Color(0xFF2C2C2A),
                textSecondary = Color(0xFF7A6650),
                border = Color(0xFFEDE0C8),
                accent = Color(0xFFD9A44E),
                accentTint = GoldBadgeBg,
                isDark = false
            )
            else -> OnboardingPalette(
                background = CanvasPageLight,
                card = SurfaceCardLight,
                cardSecondary = SoftNavyPillBg,
                textPrimary = TextPrimaryLight,
                textSecondary = TextSecondaryLight,
                border = NeutralBorder,
                accent = TealPrimary,
                accentTint = TealTintBg,
                isDark = false
            )
        }
    }

    // 3. User Choices
    var selectedFocus by remember { mutableStateOf(setOf<String>()) }
    var selectedZoneId by remember {
        mutableStateOf(sharedPrefs.getString("selected_prayer_zone_id", null))
    }

    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        val pagerState = rememberPagerState(pageCount = { 4 })

        fun advanceToNextOrFinish() {
            coroutineScope.launch {
                if (pagerState.currentPage < 3) {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                } else {
                    onComplete(
                        selectedFocus,
                        appLanguage,
                        activeThemeName,
                        requestNotifications,
                        requestLocation
                    )
                }
            }
        }

        Scaffold(
            containerColor = dynamicPalette.background,
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 1. Horizontal Pager with 4 screens
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = true,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 22.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (page) {
                            0 -> CreativeWelcomePage(
                                isArabic = isArabic,
                                palette = dynamicPalette,
                                onBegin = { advanceToNextOrFinish() }
                            )

                            1 -> CreativeFocusPage(
                                isArabic = isArabic,
                                palette = dynamicPalette,
                                selectedFocus = selectedFocus,
                                onToggleFocus = { id ->
                                    selectedFocus = if (selectedFocus.contains(id)) {
                                        selectedFocus - id
                                    } else {
                                        selectedFocus + id
                                    }
                                },
                                onContinue = { advanceToNextOrFinish() }
                            )

                            2 -> CreativeAmbiencePage(
                                isArabic = isArabic,
                                currentLanguage = appLanguage,
                                activeThemeName = activeThemeName,
                                palette = dynamicPalette,
                                onLanguageChange = { lang ->
                                    appLanguage = lang
                                    sharedPrefs.edit().putString("app_language", lang).apply()
                                },
                                onThemeChange = { theme ->
                                    activeThemeName = theme
                                    val modeStr = when (theme) {
                                        "Obsidian Night" -> "Obsidian Night"
                                        "Warm Parchment", "Sepia Parchment" -> "Warm Parchment"
                                        else -> "Madani Crisp"
                                    }
                                    sharedPrefs.edit().putString("shared_reading_theme", modeStr).apply()
                                },
                                onContinue = { advanceToNextOrFinish() }
                            )

                            3 -> CreativePermissionsPage(
                                isArabic = isArabic,
                                zones = zones,
                                palette = dynamicPalette,
                                selectedZoneId = selectedZoneId,
                                onSelectZone = { zone ->
                                    selectedZoneId = zone.id
                                    sharedPrefs.edit().putString("selected_prayer_zone_id", zone.id).apply()
                                },
                                onRequestNotifications = requestNotifications,
                                onRequestLocation = requestLocation,
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

                // 2. Top Bar with Screen Title & Single-Screen Skip Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    // Small step progress pill
                    Surface(
                        modifier = Modifier.align(if (isArabic) Alignment.CenterEnd else Alignment.CenterStart),
                        shape = RoundedCornerShape(50),
                        color = dynamicPalette.cardSecondary,
                        border = BorderStroke(1.dp, dynamicPalette.border)
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1} / 4",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = dynamicPalette.accent,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Skip button: Exits onboarding flow directly (visible on pages 1-3 only)
                    if (pagerState.currentPage < 3) {
                        Surface(
                            modifier = Modifier.align(if (isArabic) Alignment.CenterStart else Alignment.CenterEnd),
                            shape = RoundedCornerShape(50),
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable { onSkip() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isArabic) "تخطي" else "Skip",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = dynamicPalette.accent,
                                        fontSize = 13.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = dynamicPalette.accent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                // 3. Bottom Modern Linear Step Dots
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { idx ->
                            val isSelected = pagerState.currentPage == idx
                            val width = if (isSelected) 24.dp else 7.dp
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(width)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (isSelected) dynamicPalette.accent else dynamicPalette.textSecondary.copy(alpha = 0.25f)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

/** -------------------------------------------------------------
 * Screen 1: Welcome & Bismillah Spiritual Hero
 * ------------------------------------------------------------- */
@Composable
private fun CreativeWelcomePage(
    isArabic: Boolean,
    palette: OnboardingPalette,
    onBegin: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 28.dp)
    ) {
        // Decorative Bismillah Crest & Mosque Silhouette
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = palette.card,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = if (palette.isDark) 0.dp else 2.dp,
            modifier = Modifier
                .size(110.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                palette.accentTint,
                                palette.card
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        // Bismillah Calligraphic Accent
        Text(
            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Serif,
                color = palette.accent,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        )

        // Main Title
        Text(
            text = if (isArabic) "نور • رفيقك الإيماني" else "Noor • Spiritual Companion",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        )

        // Subtitle
        Text(
            text = if (isArabic)
                "مساحتك اليومية لتلاوة وحفظ كتاب الله، ضبط مواقيت الصلاة بدقة، وتعمير أوقاتك بالأذكار المباركة."
            else
                "Your daily sanctuary for verified Quran recitation, precise prayer timings, and peaceful devotions.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = palette.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp,
                fontSize = 13.5.sp
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 3 Key Feature Highlight Badges
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FeatureHighlightChip(
                icon = Icons.Default.AutoStories,
                title = if (isArabic) "مصحف المدينة المنورة بالرسم العثماني" else "Authentic Madani Quran Mushaf",
                palette = palette
            )
            FeatureHighlightChip(
                icon = Icons.Default.AccessTime,
                title = if (isArabic) "تنبيهات أذان ومواقيت صلاة دقيقة" else "Verified Prayer Alerts & Sun Calculations",
                palette = palette
            )
            FeatureHighlightChip(
                icon = Icons.Default.Spa,
                title = if (isArabic) "أذكار الصباح والمساء وحفظ القرآن" else "Daily Azkar Fortress & Hifz Coach",
                palette = palette
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Primary Begin Button
        Button(
            onClick = onBegin,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isArabic) "ابدأ رحلتك الإيمانية" else "Begin Your Journey",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun FeatureHighlightChip(
    icon: ImageVector,
    title: String,
    palette: OnboardingPalette
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = palette.card,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(palette.accentTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    fontSize = 13.sp
                )
            )
        }
    }
}

/** -------------------------------------------------------------
 * Screen 2: Focus & Daily Intentions (Bento Style Selection)
 * ------------------------------------------------------------- */
@Composable
private fun CreativeFocusPage(
    isArabic: Boolean,
    palette: OnboardingPalette,
    selectedFocus: Set<String>,
    onToggleFocus: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 28.dp)
    ) {
        // Section Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isArabic) "حدد مقاصدك الإيمانية" else "Choose Your Intentions",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    fontSize = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic)
                    "اختر ما ترغب بالتركيز عليه لتخصيص لوحتك الإيمانية اليومية."
                else
                    "Select devotions you'd like Noor to highlight on your dashboard.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = palette.textSecondary,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        // 4 Bento Cards
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            CreativeFocusCard(
                id = "memorize",
                title = if (isArabic) "حفظ القرآن الكريم ومراجعته" else "Memorize the Quran",
                subtitle = if (isArabic) "أداة التكرار الذكي وتثبيت الحفظ" else "Spaced repetition & Hifz coach",
                badge = if (isArabic) "القرآن" else "Quran",
                icon = Icons.Default.AutoStories,
                isSelected = selectedFocus.contains("memorize"),
                onClick = { onToggleFocus("memorize") },
                palette = palette
            )
            CreativeFocusCard(
                id = "miss_prayer",
                title = if (isArabic) "المحافظة على الصلاة في وقتها" else "Never Miss a Prayer",
                subtitle = if (isArabic) "أذان وتنبيهات دقيقة قبل وبعد الصلاة" else "Accurate adhan & timely reminders",
                badge = if (isArabic) "الصلاة" else "Salat",
                icon = Icons.Default.AccessTime,
                isSelected = selectedFocus.contains("miss_prayer"),
                onClick = { onToggleFocus("miss_prayer") },
                palette = palette
            )
            CreativeFocusCard(
                id = "dhikr",
                title = if (isArabic) "أذكار الصباح والمساء والأدعية" else "Daily Dhikr & Fortress of Duas",
                subtitle = if (isArabic) "حصن المسلم والمسبحة الإلكترونية" else "Morning/Evening azkar & tasbih",
                badge = if (isArabic) "الأذكار" else "Azkar",
                icon = Icons.Default.Spa,
                isSelected = selectedFocus.contains("dhikr"),
                onClick = { onToggleFocus("dhikr") },
                palette = palette
            )
            CreativeFocusCard(
                id = "track",
                title = if (isArabic) "متابعة الختمة والتقدم الإيماني" else "Track Khatma & Habits",
                subtitle = if (isArabic) "خطط ختم القرآن وإحصائيات الالتزام" else "Khatma milestone tracker & streaks",
                badge = if (isArabic) "الختمة" else "Khatma",
                icon = Icons.Default.TrendingUp,
                isSelected = selectedFocus.contains("track"),
                onClick = { onToggleFocus("track") },
                palette = palette
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onContinue,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = if (isArabic) "حفظ ومتابعة" else "Save & Continue",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
            )
        }
    }
}

@Composable
private fun CreativeFocusCard(
    id: String,
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    palette: OnboardingPalette
) {
    val bgColor = if (isSelected) palette.accentTint else palette.card
    val borderColor = if (isSelected) palette.accent else palette.border

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) palette.accent else palette.cardSecondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else palette.accent,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            fontSize = 14.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = palette.textSecondary,
                        fontSize = 12.sp
                    )
                )
            }

            // Status Check Pill
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) palette.accent else Color.Transparent)
                    .then(
                        if (!isSelected) Modifier.background(palette.cardSecondary) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

/** -------------------------------------------------------------
 * Screen 3: Reading Canvas Ambience & Language (Floating Dropdown)
 * ------------------------------------------------------------- */
@Composable
private fun CreativeAmbiencePage(
    isArabic: Boolean,
    currentLanguage: String,
    activeThemeName: String,
    palette: OnboardingPalette,
    onLanguageChange: (String) -> Unit,
    onThemeChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 28.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isArabic) "مظهر القراءة واللغة" else "Reading Canvas & Language",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    fontSize = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic)
                    "اختر مظهر المصحف ولغة الواجهة لتجربة قراءة مريحة لعينيك."
                else
                    "Choose your interface language and live-preview Quran canvas themes.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = palette.textSecondary,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        // Floating Language Dropdown Menu (Opens OVER without pushing content below!)
        FloatingLanguageSelector(
            currentLanguageCode = currentLanguage,
            isArabic = isArabic,
            palette = palette,
            onLanguageSelected = onLanguageChange
        )

        // 3 Live Theme Canvas Swatches
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isArabic) "مظهر المصحف الشريف" else "Quran Canvas Theme",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    fontSize = 14.5.sp
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CreativeThemeSwatch(
                    name = "Madani Crisp",
                    title = if (isArabic) "ناصع" else "Crisp",
                    badge = if (isArabic) "نهاري" else "Day",
                    bg = Color(0xFFF6F8F7),
                    card = Color(0xFFFFFFFF),
                    text = Color(0xFF1F1F1F),
                    accent = Color(0xFF1BA486),
                    isSelected = activeThemeName == "Madani Crisp",
                    onClick = { onThemeChange("Madani Crisp") },
                    modifier = Modifier.weight(1f)
                )
                CreativeThemeSwatch(
                    name = "Warm Parchment",
                    title = if (isArabic) "دافئ" else "Warm",
                    badge = if (isArabic) "مريح" else "Sepia",
                    bg = Color(0xFFFCFBF9),
                    card = Color(0xFFFFFFFF),
                    text = Color(0xFF2C2C2A),
                    accent = Color(0xFFD9A44E),
                    isSelected = activeThemeName == "Warm Parchment" || activeThemeName == "Sepia Parchment",
                    onClick = { onThemeChange("Warm Parchment") },
                    modifier = Modifier.weight(1f)
                )
                CreativeThemeSwatch(
                    name = "Obsidian Night",
                    title = if (isArabic) "داكن" else "Obsidian",
                    badge = if (isArabic) "ليلي" else "Night",
                    bg = Color(0xFF12151A),
                    card = Color(0xFF1A1E24),
                    text = Color(0xFFE8E6DF),
                    accent = Color(0xFF2FBF96),
                    isSelected = activeThemeName == "Obsidian Night",
                    onClick = { onThemeChange("Obsidian Night") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onContinue,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = if (isArabic) "متابعة إلى الإعدادات" else "Continue to Setup",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
            )
        }
    }
}

@Composable
private fun FloatingLanguageSelector(
    currentLanguageCode: String,
    isArabic: Boolean,
    palette: OnboardingPalette,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val languages = listOf(
        "English" to "English (Default)",
        "Arabic" to "العربية (Arabic)",
        "French" to "Français (French)",
        "Urdu" to "اردو (Urdu)",
        "Indonesian" to "Bahasa Indonesia",
        "Turkish" to "Türkçe (Turkish)"
    )

    val currentLabel = languages.find { it.first == currentLanguageCode }?.second ?: "English (Default)"

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { expanded = true },
            shape = RoundedCornerShape(16.dp),
            color = palette.card,
            border = BorderStroke(1.dp, if (expanded) palette.accent else palette.border)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(palette.accentTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = palette.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isArabic) "لغة التطبيق والترجمة" else "App Display Language",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = palette.textSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                        Text(
                            text = currentLabel,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                                fontSize = 14.5.sp
                            )
                        )
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Non-disruptive floating Material DropdownMenu (anchored above the layout)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(0.dp, 6.dp),
            modifier = Modifier
                .width(280.dp)
                .background(palette.card)
                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
        ) {
            languages.forEach { (code, label) ->
                val isSelected = currentLanguageCode == code
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) palette.accent else palette.textPrimary,
                                    fontSize = 13.5.sp
                                )
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = palette.accent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onLanguageSelected(code)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = palette.textPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun CreativeThemeSwatch(
    name: String,
    title: String,
    badge: String,
    bg: Color,
    card: Color,
    text: Color,
    accent: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = bg,
        border = BorderStroke(if (isSelected) 2.5.dp else 1.dp, if (isSelected) accent else text.copy(alpha = 0.15f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(10.dp)
        ) {
            // Miniature Mushaf page mock
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = card,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                border = BorderStroke(1.dp, text.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .height(5.dp)
                            .fillMaxWidth(0.75f)
                            .clip(CircleShape)
                            .background(accent)
                    )
                    Box(
                        modifier = Modifier
                            .height(3.5.dp)
                            .fillMaxWidth(0.9f)
                            .clip(CircleShape)
                            .background(text.copy(alpha = 0.35f))
                    )
                    Box(
                        modifier = Modifier
                            .height(3.5.dp)
                            .fillMaxWidth(0.6f)
                            .clip(CircleShape)
                            .background(text.copy(alpha = 0.2f))
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = text,
                        fontSize = 12.sp
                    ),
                    maxLines = 1
                )
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = accent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

/** -------------------------------------------------------------
 * Screen 4: Stay on Time (Separate Notifications & Location Cards!)
 * ------------------------------------------------------------- */
@Composable
private fun CreativePermissionsPage(
    isArabic: Boolean,
    zones: List<PrayerZone>,
    palette: OnboardingPalette,
    selectedZoneId: String?,
    onSelectZone: (PrayerZone) -> Unit,
    onRequestNotifications: () -> Unit,
    onRequestLocation: () -> Unit,
    onStartNoor: () -> Unit
) {
    var showManualCity by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var notifGrantedLocally by remember { mutableStateOf(false) }
    var locGrantedLocally by remember { mutableStateOf(false) }

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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isArabic) "مواقيت الصلاة والتنبيهات" else "Prayer Timings & Alerts",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    fontSize = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic)
                    "فعل التنبيهات وحدد موقعك لحساب مواقيت الصلاة بدقة فائقة."
                else
                    "Grant separate permissions for adhan alerts and automatic location calculation.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = palette.textSecondary,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        if (!showManualCity) {
            // CARD 1: Notifications (Completely Separate!)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.card,
                border = BorderStroke(1.dp, if (notifGrantedLocally) palette.accent else palette.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(palette.accentTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = palette.accent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "إشعارات الأذان والورد" else "Prayer Alerts & Azkar",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                                fontSize = 13.5.sp
                            )
                        )
                        Text(
                            text = if (isArabic) "تنبيهات وقت دخول الصلاة والأذكار" else "Live adhan calls & daily reminders",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = palette.textSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Button(
                        onClick = {
                            notifGrantedLocally = true
                            onRequestNotifications()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (notifGrantedLocally) palette.accentTint else palette.accent,
                            contentColor = if (notifGrantedLocally) palette.accent else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = if (notifGrantedLocally) {
                                if (isArabic) "تم التفعيل" else "Enabled"
                            } else {
                                if (isArabic) "تفعيل" else "Enable"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            // CARD 2: Location (Completely Separate!)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.card,
                border = BorderStroke(1.dp, if (locGrantedLocally) palette.accent else palette.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(GoldBadgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = GoldBadgeText,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "تحديد الموقع التلقائي (GPS)" else "Automatic GPS Location",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                                fontSize = 13.5.sp
                            )
                        )
                        Text(
                            text = if (isArabic) "حساب زوايا الشمس الفلكية بدقة" else "Calculates accurate solar angles",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = palette.textSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Button(
                        onClick = {
                            locGrantedLocally = true
                            onRequestLocation()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (locGrantedLocally) GoldBadgeBg else GoldBadgeText,
                            contentColor = if (locGrantedLocally) GoldBadgeText else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = if (locGrantedLocally) {
                                if (isArabic) "تم التحديد" else "Located"
                            } else {
                                if (isArabic) "تحديد" else "Locate"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            // Quiet Manual Option
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showManualCity = true }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EditLocation,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (isArabic) "أو اختر مدينتك يدوياً بدون GPS" else "Or choose your city manually without GPS",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = palette.accent,
                        fontSize = 12.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Start Button
            Button(
                onClick = onStartNoor,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.accent,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isArabic) "ابدأ باستخدام تطبيق نور" else "Start Using Noor",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        } else {
            // Manual City Selection View
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.card,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "اختيار المدينة يدوياً" else "Select City Manually",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                                fontSize = 14.5.sp
                            )
                        )
                        Text(
                            text = if (isArabic) "الرجوع لـ GPS" else "Back to GPS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = palette.accent
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { showManualCity = false }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = {
                            Text(
                                if (isArabic) "ابحث بالمدينة أو الدولة..." else "Search city or country...",
                                style = MaterialTheme.typography.bodySmall.copy(color = palette.textSecondary)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = palette.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = palette.textPrimary,
                            unfocusedTextColor = palette.textPrimary,
                            focusedBorderColor = palette.accent,
                            unfocusedBorderColor = palette.border
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(filteredZones) { zone ->
                            val isSelected = selectedZoneId == zone.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) palette.accentTint else Color.Transparent,
                                border = if (isSelected) BorderStroke(1.5.dp, palette.accent) else BorderStroke(1.dp, palette.border),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectZone(zone) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = if (isArabic) zone.arabicName else zone.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = palette.textPrimary,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = "${zone.country} • ${zone.zoneLabel}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = palette.textSecondary,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = palette.accent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onStartNoor,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.accent,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (isArabic) "ابدأ باستخدام تطبيق نور" else "Start Using Noor",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                )
            }
        }
    }
}

/** Palette carrier for dynamic live theme switching */
private data class OnboardingPalette(
    val background: Color,
    val card: Color,
    val cardSecondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val accent: Color,
    val accentTint: Color,
    val isDark: Boolean
)
