package com.example.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.text.style.TextDirection
import com.example.data.model.QuranArabicFont
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.localization.tr
import com.example.data.model.PrayerTime
import com.example.data.model.QuickAccessTool
import com.example.data.quran.DuaData
import com.example.data.quran.KhatmaEngine
import com.example.data.quran.KhatmaPaceStatus
import com.example.data.quran.QuranData
import coil.compose.AsyncImage
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.SalatTab
import com.example.ui.components.BentoCard
import com.example.ui.components.HomeSectionActionLabel
import com.example.ui.components.HomeSectionIconButton
import com.example.ui.components.universalCardShadow
import com.example.ui.quran.AmiriQuranFontFamily
import com.example.ui.theme.BorderDividerDark
import com.example.ui.theme.BorderDividerLight
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.BorderTealLight
import com.example.ui.theme.CanvasMint
import com.example.data.local.ReadingProgressEntity
import com.example.data.quran.KhatmaFullDashboardState
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.GoldTintBgDark
import com.example.ui.theme.GoldTintBgLight
import com.example.ui.theme.CardDarkGradient
import com.example.ui.theme.HeaderGradientLight
import com.example.ui.theme.HeaderTealEnd
import com.example.ui.theme.HeaderTealStart
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.NoorTopBarGradient
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealGradient
import com.example.ui.theme.PrimaryTealLight
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.ReadingThemes
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.NestedGoldCardLight
import com.example.ui.theme.NestedGoldCardDark
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceElevatedLight
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.HomePageColors
import com.example.ui.theme.rememberHomePageColors

// Accent Colors: Centralized 2-Stop Gradient & Gold Highlights
private val NoorTealStart = PrimaryTealLight
private val NoorTealEnd = HeaderTealEnd
val NoorAccentGradient = SolidColor(PrimaryTealLight)

// Prayer Tracker 2-Stop Gradient using centralized tokens
val PrayerTrackerDiagonalGradient = SolidColor(PrimaryTealLight)

// Centralized Gold Accents
val NoorGoldAccent = SecondaryGoldLight
val NoorGoldLight = SecondaryGoldDark
val NoorGoldSoft = GoldTintBgLight
val NoorGoldBorder = SecondaryGoldLight.copy(alpha = 0.3f)
val NoorGoldGradient = SolidColor(SecondaryGoldLight)
val NoorBorderGradient = SolidColor(PrimaryTealLight)

private val NoorDarkPine = DarkPine
private val NoorSageSlate = SlateTealMuted
private val NoorCardBorder = BorderDividerLight
private val NoorSurfaceSoft = SurfaceElevatedLight

// Soft green palette for refined, consistent spiritual cards
val NoorSoftGreenBorder = SoftTealTint
val NoorSoftGreenBg = SoftTealTint

// ============================================================
// CUSTOM MINIMAL ISLAMIC VECTOR ICONS (Clean & Refined)
// ============================================================

@Composable
fun IslamicIconAzkar(
    modifier: Modifier = Modifier,
    tint: Color = NoorGoldAccent
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h * 0.42f
        val r = w * 0.30f
        val strokeW = 1.6.dp.toPx()

        // Rosary Bead Loop
        val beadCount = 8
        for (i in 0 until beadCount) {
            val angle = Math.toRadians((i * 360.0 / beadCount) - 90.0)
            val bx = (cx + r * Math.cos(angle)).toFloat()
            val by = (cy + r * Math.sin(angle)).toFloat()
            drawCircle(color = tint, radius = 2.0.dp.toPx(), center = Offset(bx, by))
        }

        // Hanging Minaret Tassel
        drawLine(tint, Offset(cx, cy + r), Offset(cx, cy + r + h * 0.22f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawCircle(color = NoorGoldAccent, radius = 1.8.dp.toPx(), center = Offset(cx, cy + r + h * 0.26f))
    }
}

@Composable
fun IslamicIconQuranAudio(
    modifier: Modifier = Modifier,
    tint: Color = NoorGoldAccent
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.6.dp.toPx()

        // Headphone Arc
        val arcTop = h * 0.18f
        val arcBottom = h * 0.62f
        val leftX = w * 0.22f
        val rightX = w * 0.78f

        val path = Path().apply {
            moveTo(leftX, arcBottom)
            cubicTo(leftX, arcTop, rightX, arcTop, rightX, arcBottom)
        }
        drawPath(path, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // Left & Right Minimal Ear Cushions
        drawRoundRect(
            color = tint,
            topLeft = Offset(leftX - 2.5.dp.toPx(), arcBottom - 2.dp.toPx()),
            size = Size(5.dp.toPx(), 9.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(rightX - 2.5.dp.toPx(), arcBottom - 2.dp.toPx()),
            size = Size(5.dp.toPx(), 9.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
        )

        // Center Minimal Wave Bars
        val cx = w / 2f
        drawLine(tint, Offset(cx - 3.dp.toPx(), h * 0.52f), Offset(cx - 3.dp.toPx(), h * 0.76f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(NoorGoldAccent, Offset(cx, h * 0.44f), Offset(cx, h * 0.84f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(tint, Offset(cx + 3.dp.toPx(), h * 0.52f), Offset(cx + 3.dp.toPx(), h * 0.76f), strokeWidth = strokeW, cap = StrokeCap.Round)
    }
}

@Composable
fun IslamicIconTasbeeh(
    modifier: Modifier = Modifier,
    tint: Color = NoorGoldAccent
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val strokeW = 1.6.dp.toPx()

        // Outer Ring
        drawCircle(
            color = tint,
            radius = w * 0.34f,
            center = Offset(cx, cy),
            style = Stroke(width = strokeW)
        )

        // Inner Counter Dial & Notch
        drawCircle(
            color = tint.copy(alpha = 0.15f),
            radius = w * 0.18f,
            center = Offset(cx, cy),
            style = Fill
        )
        drawCircle(
            color = NoorGoldAccent,
            radius = 2.dp.toPx(),
            center = Offset(cx, cy)
        )

        // Top Clicker Button
        drawLine(
            color = NoorGoldAccent,
            start = Offset(cx, h * 0.08f),
            end = Offset(cx, h * 0.16f),
            strokeWidth = strokeW * 1.2f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun IslamicIconDua(
    modifier: Modifier = Modifier,
    tint: Color = NoorGoldAccent
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.6.dp.toPx()

        // Left Hand
        val leftHand = Path().apply {
            moveTo(w * 0.46f, h * 0.80f)
            lineTo(w * 0.22f, h * 0.65f)
            cubicTo(w * 0.16f, h * 0.46f, w * 0.26f, h * 0.26f, w * 0.44f, h * 0.24f)
            lineTo(w * 0.46f, h * 0.80f)
        }
        drawPath(leftHand, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // Right Hand
        val rightHand = Path().apply {
            moveTo(w * 0.54f, h * 0.80f)
            lineTo(w * 0.78f, h * 0.65f)
            cubicTo(w * 0.84f, h * 0.46f, w * 0.74f, h * 0.26f, w * 0.56f, h * 0.24f)
            lineTo(w * 0.54f, h * 0.80f)
        }
        drawPath(rightHand, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))
    }
}

@Composable
fun IslamicIconTask(
    modifier: Modifier = Modifier,
    tint: Color = NoorGoldAccent
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = w * 0.36f
        val path = Path()

        // 8-Point Islamic Star
        val points = 16
        for (i in 0 until points) {
            val r = if (i % 2 == 0) radius else radius * 0.65f
            val angle = Math.toRadians((i * 360.0 / points) - 90.0)
            val x = (cx + r * Math.cos(angle)).toFloat()
            val y = (cy + r * Math.sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color = tint, style = Stroke(width = 1.6.dp.toPx()))

        // Inner Check Mark
        val checkPath = Path().apply {
            moveTo(w * 0.36f, cy)
            lineTo(w * 0.47f, cy + 3.dp.toPx())
            lineTo(w * 0.64f, cy - 3.5.dp.toPx())
        }
        drawPath(checkPath, color = tint, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun IslamicIconSalat(
    modifier: Modifier = Modifier,
    tint: Color = NoorGoldAccent
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.6.dp.toPx()

        // Mihrab Arch
        val path = Path().apply {
            moveTo(w * 0.24f, h * 0.84f)
            lineTo(w * 0.24f, h * 0.46f)
            cubicTo(w * 0.24f, h * 0.24f, w * 0.5f, h * 0.16f, w * 0.5f, h * 0.14f)
            cubicTo(w * 0.5f, h * 0.16f, w * 0.76f, h * 0.24f, w * 0.76f, h * 0.46f)
            lineTo(w * 0.76f, h * 0.84f)
        }
        drawPath(path, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // Floor Base & Mihrab Lamp
        drawLine(tint, Offset(w * 0.16f, h * 0.84f), Offset(w * 0.84f, h * 0.84f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawCircle(color = NoorGoldAccent, radius = 2.dp.toPx(), center = Offset(w * 0.5f, h * 0.44f))
        drawLine(tint, Offset(w * 0.5f, h * 0.24f), Offset(w * 0.5f, h * 0.42f), strokeWidth = 1.2.dp.toPx())
    }
}

@Composable
fun IslamicIconQibla(
    modifier: Modifier = Modifier,
    tint: Color = NoorGoldAccent
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val strokeW = 1.6.dp.toPx()

        // Outer Compass Ring
        drawCircle(
            color = tint,
            radius = w * 0.34f,
            center = Offset(cx, cy),
            style = Stroke(width = strokeW)
        )

        // North Pointer with Gold Tip
        val needlePath = Path().apply {
            moveTo(cx, h * 0.20f)
            lineTo(cx - 3.5.dp.toPx(), cy + 2.dp.toPx())
            lineTo(cx, cy)
            lineTo(cx + 3.5.dp.toPx(), cy + 2.dp.toPx())
            close()
        }
        drawPath(needlePath, color = NoorGoldAccent)

        // Kaaba Cube Base
        drawRect(
            color = tint,
            topLeft = Offset(cx - 3.5.dp.toPx(), cy + 3.dp.toPx()),
            size = Size(7.dp.toPx(), 6.5.dp.toPx())
        )
    }
}

@Composable
fun IslamicIconMushaf(
    modifier: Modifier = Modifier,
    tint: Color = NoorGoldAccent
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val strokeW = 1.6.dp.toPx()

        // Open Holy Book (Mushaf)
        val leftPage = Path().apply {
            moveTo(cx, h * 0.50f)
            cubicTo(w * 0.36f, h * 0.46f, w * 0.22f, h * 0.36f, w * 0.18f, h * 0.32f)
            lineTo(w * 0.18f, h * 0.66f)
            cubicTo(w * 0.22f, h * 0.70f, w * 0.36f, h * 0.80f, cx, h * 0.84f)
        }
        drawPath(leftPage, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        val rightPage = Path().apply {
            moveTo(cx, h * 0.50f)
            cubicTo(w * 0.64f, h * 0.46f, w * 0.78f, h * 0.36f, w * 0.82f, h * 0.32f)
            lineTo(w * 0.82f, h * 0.66f)
            cubicTo(w * 0.78f, h * 0.70f, w * 0.64f, h * 0.80f, cx, h * 0.84f)
        }
        drawPath(rightPage, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // Center Spine & Gold Ribbon
        drawLine(tint, Offset(cx, h * 0.50f), Offset(cx, h * 0.84f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(NoorGoldAccent, Offset(cx, h * 0.50f), Offset(cx + 2.dp.toPx(), h * 0.92f), strokeWidth = 1.4.dp.toPx(), cap = StrokeCap.Round)
    }
}

// ============================================================
// 1. STANDALONE ATMOSPHERIC MOSQUE HERO CARD (Luxury Islamic Aesthetic)
// ============================================================

// ============================================================
// 1. STANDALONE USER PROFILE ROW & HERO PRAYER CARD
// ============================================================

@Composable
fun UserProfileRow(
    viewModel: MainViewModel,
    userName: String,
    location: String,
    modifier: Modifier = Modifier
) {
    val readingThemeName by viewModel.sharedReadingTheme.collectAsStateWithLifecycle()
    val themeColors = remember(readingThemeName) { ReadingThemes.getThemeByName(readingThemeName) }
    val isDark = themeColors.isDark

    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { viewModel.navigateTo(NoorDestination.PROFILE) }
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isDark) themeColors.surface else Color.White)
                    .padding(2.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_user_avatar),
                    contentDescription = tr("home_user_avatar", viewModel),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Column {
                Text(
                    text = tr("home_header_greeting", viewModel),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = NoorGoldAccent
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (userName.isBlank() || userName == "Guest Mode") tr("home_header_guest", viewModel) else userName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isDark) themeColors.arabicText else NoorDarkPine
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = NoorGoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = NoorGoldAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = location.ifBlank { tr("home_header_default_location", viewModel) },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) themeColors.translationText else NoorSageSlate
                        )
                    )
                }
            }
        }

        // Two small circular icon buttons on the right (Customize, Settings)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { viewModel.openCustomizeHomeSheet() },
                shape = CircleShape,
                color = if (isDark) themeColors.surface else Color.White,
                border = null
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.DashboardCustomize,
                        contentDescription = if (isArabic) "تخصيص" else "Customize",
                        tint = NoorGoldAccent,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { viewModel.openSettingsModal() },
                shape = CircleShape,
                color = if (isDark) themeColors.surface else Color.White,
                border = null
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = if (isArabic) "الإعدادات" else "Settings",
                        tint = NoorGoldAccent,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeroPrayerCard(
    viewModel: MainViewModel,
    nextPrayerName: String,
    nextPrayerTime: String,
    prayers: List<PrayerTime> = emptyList(),
    modifier: Modifier = Modifier
) {
    val countdown by viewModel.nextPrayerCountdown.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val heroShape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 6.dp,
                shape = heroShape,
                spotColor = Color.Black.copy(alpha = 0.15f),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(heroShape)
            .background(DarkPine, shape = heroShape)
            .clickable { viewModel.navigateTo(NoorDestination.SALAT) }
    ) {
        // Background Mosque Image centered
        Image(
            painter = painterResource(id = R.drawable.img_pinterest_hero),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = BiasAlignment(0f, -0.15f),
            alpha = 1.0f,
            modifier = Modifier.matchParentSize()
        )

        // Soft black overlay on top of the image
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        // Deep rich vertical black gradient overlay for crisp text readability
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0x99000000))
        )

        // Content: Spiritual Sanctuary Banner + Next Salat + Minimal Prayer Times Strip
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Creative Spiritual Sanctuary Top Reminder
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
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NoorGoldAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (isArabic) "الواحة الروحية • السكينة واليقين" else "Spiritual Sanctuary • Serenity & Peace",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(NoorGoldAccent)
                )
            }

            // Next Salat Primary Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NoorGoldAccent)
                        )
                        Text(
                            text = tr("home_header_upcoming_salat", viewModel),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = NoorGoldAccent
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = nextPrayerName.ifBlank { tr("prayer_isha", viewModel) },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = Color.White
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = nextPrayerTime.ifBlank { "05:36 pm" },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            letterSpacing = (-0.4).sp,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val displayCountdown = if (countdown.contains("left", ignoreCase = true) || countdown.contains("متبقية", ignoreCase = true)) {
                        countdown
                    } else {
                        String.format(tr("home_header_time_left", viewModel), countdown)
                    }
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = PrimaryTealLight.copy(alpha = 0.25f),
                        border = null
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(NoorGoldAccent)
                            )
                            Text(
                                text = displayCountdown,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NoorGoldAccent
                                )
                            )
                        }
                    }
                }
            }

            // Minimalist / Premium Prayer Times Strip below Next Salat
            val displayPrayers = if (prayers.isNotEmpty()) {
                prayers
            } else {
                listOf(
                    PrayerTime("Fajr", "الفجر", "05:12 AM", 5, 12),
                    PrayerTime("Sunrise", "الشروق", "06:34 AM", 6, 34),
                    PrayerTime("Dhuhr", "الظهر", "01:15 PM", 13, 15),
                    PrayerTime("Asr", "العصر", "04:45 PM", 16, 45),
                    PrayerTime("Maghrib", "المغرب", "07:30 PM", 19, 30),
                    PrayerTime("Isha", "العشاء", "08:50 PM", 20, 50)
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.16f),
                border = null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    displayPrayers.forEach { prayer ->
                        val isNext = prayer.isNext || prayer.name.equals(nextPrayerName, ignoreCase = true)
                        val nameStr = if (isArabic) prayer.arabicName else prayer.name
                        val cleanTime = prayer.timeString.replace(" AM", "").replace(" PM", "").replace(" ص", "").replace(" م", "").trim()

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(11.dp))
                                .background(if (isNext) SecondaryGoldLight.copy(alpha = 0.35f) else Color.Transparent)
                                .padding(vertical = 6.dp, horizontal = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = nameStr,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isNext) NoorGoldAccent else Color.White.copy(alpha = 0.9f)
                                    ),
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = cleanTime,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isNext) Color.White else Color.White.copy(alpha = 0.95f)
                                    ),
                                    maxLines = 1,
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

// ============================================================
// 2. MERGED SALAT TIMES & TRACKER CARD (Overlapping Hero Tray Layer)
// ============================================================

@Composable
fun ChronologicalPrayerTracker(
    viewModel: MainViewModel,
    prayers: List<PrayerTime>,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    val completedPrayers by viewModel.completedPrayers.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val infiniteTransition = rememberInfiniteTransition(label = "salat_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Outer Container
    BentoCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = homeColors.outerCardBackground,
        contentPadding = PaddingValues(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(homeColors.iconBadgeBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        IslamicIconSalat(modifier = Modifier.size(20.dp), tint = homeColors.iconColor)
                    }

                    Column {
                        Text(
                            text = if (isArabic) "مواقيت وتتبع الصلاة" else "Prayer Times & Tracker",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = homeColors.titleText
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) "سجّل صلواتك اليومية" else "Tap to record prayers",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = homeColors.subtext
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = homeColors.badgeBg,
                    border = null,
                    modifier = Modifier.clickable { viewModel.navigateToSalat(SalatTab.TIMES) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isArabic) "تذكيرات" else "Reminders",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = homeColors.badgeText
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = homeColors.badgeText,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inner Container
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = homeColors.innerContainer,
                border = null
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 12.dp)
                ) {
                    // Interactive Prayer Timeline: Connected line with circular checkmark nodes
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Connecting timeline rail spanning across the node centers, perfectly centered
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(34.dp)
                                .padding(top = 8.dp)
                        ) {
                            val itemWidth = size.width / 5f
                            val startX = itemWidth / 2f
                            val endX = size.width - (itemWidth / 2f)
                            val centerY = 17.dp.toPx()
                            drawLine(
                                color = homeColors.dividerBorder,
                                start = Offset(startX, centerY),
                                end = Offset(endX, centerY),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // 5 Daily Prayer Tracker Nodes Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            val prayerItems = listOf(
                                Pair(tr("prayer_fajr", viewModel), "Fajr"),
                                Pair(tr("prayer_dhuhr", viewModel), "Dhuhr"),
                                Pair(tr("prayer_asr", viewModel), "Asr"),
                                Pair(tr("prayer_maghrib", viewModel), "Maghrib"),
                                Pair(tr("prayer_isha", viewModel), "Isha")
                            )

                            prayerItems.forEachIndexed { index, (localizedName, keyName) ->
                                val matchingPrayer = prayers.find { it.name.equals(keyName, ignoreCase = true) }
                                val isChecked = matchingPrayer != null && completedPrayers.contains(matchingPrayer.name)
                                val isCurrent = matchingPrayer?.isCurrent == true
                                val isActionable = matchingPrayer != null && (matchingPrayer.isPast || matchingPrayer.isCurrent)

                                val timeText = matchingPrayer?.timeString ?: when (index) {
                                    0 -> "05:43"
                                    1 -> "12:45"
                                    2 -> "16:39"
                                    3 -> "19:15"
                                    4 -> "21:07"
                                    else -> "--:--"
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            matchingPrayer?.let { viewModel.togglePrayerCompleted(it) }
                                        }
                                        .padding(vertical = 8.dp, horizontal = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .scale(if (isCurrent && !isChecked) pulseScale else 1f)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isChecked -> homeColors.progressFill
                                                    isCurrent -> homeColors.badgeBg
                                                    else -> homeColors.outerCardBackground
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when {
                                            isChecked -> {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = tr("home_completed", viewModel),
                                                    tint = Color.White,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                            }
                                            isCurrent -> {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Active Salat",
                                                    tint = homeColors.badgeText,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                            }
                                            isActionable -> {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Not Yet Completed",
                                                    tint = homeColors.subtext.copy(alpha = 0.5f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            else -> {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Upcoming",
                                                    tint = homeColors.subtext.copy(alpha = 0.25f),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = localizedName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.5.sp,
                                            fontWeight = if (isCurrent || isChecked) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isCurrent || isChecked) homeColors.titleText else homeColors.subtext
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = timeText,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 13.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            color = homeColors.subtext
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

// ============================================================
// 2. QUICK ACCESS ESSENTIALS (Borderless 2x2 Grid Matching All Tools Style + Customization Link)
// ============================================================

fun navigateToQuickAccessTool(viewModel: MainViewModel, tool: QuickAccessTool) {
    when (tool) {
        QuickAccessTool.QURAN -> viewModel.navigateTo(NoorDestination.QURAN_SURAH_LIST)
        QuickAccessTool.SALAT -> viewModel.navigateToSalat(SalatTab.TIMES)
        QuickAccessTool.QIBLA -> viewModel.navigateTo(NoorDestination.QIBLA)
        QuickAccessTool.HIFZ -> viewModel.openMemorizationSetup()
        QuickAccessTool.TASBIH -> viewModel.navigateTo(NoorDestination.TASBIH)
        QuickAccessTool.DUAS -> viewModel.navigateTo(NoorDestination.DUAS_LIBRARY)
        QuickAccessTool.KHATMA -> viewModel.navigateTo(NoorDestination.QURAN_KHATMA)
        QuickAccessTool.STREAKS -> viewModel.navigateTo(NoorDestination.STREAKS)
        QuickAccessTool.HABITS -> viewModel.navigateTo(NoorDestination.HABIT_TRACKER)
        QuickAccessTool.AUDIO -> viewModel.navigateTo(NoorDestination.QURAN_AUDIO_STREAM)
    }
}

@Composable
fun QuickAccessMiniCard(
    tool: QuickAccessTool,
    isArabic: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    slotIndex: Int = 0
) {
    val homeColors = rememberHomePageColors()
    val iconBg = homeColors.iconBadgeBackground
    val iconTint = homeColors.iconColor
    val title = if (isArabic) tool.titleAr else tool.titleEn
    val subtitle = if (isArabic) tool.subtitleAr else tool.subtitleEn

    val cardBg = homeColors.outerCardBackground
    val titleColor = homeColors.titleText
    val subtitleColor = homeColors.subtext

    Surface(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Soft layered icon box matching theme colors
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    QuickAccessToolVisualIcon(
                        tool = tool,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            fontSize = 13.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = subtitleColor,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun MoreToolsTriggerCard(
    isExpanded: Boolean,
    isArabic: Boolean,
    isDark: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    val iconBg = homeColors.iconBadgeBackground
    val iconTint = homeColors.iconColor
    val title = if (isArabic) "أدوات إضافية" else "More Tools"
    val subtitle = if (isArabic) "استكشف المزيد" else "Explore extra"

    val cardBg = homeColors.outerCardBackground
    val titleColor = homeColors.titleText
    val subtitleColor = homeColors.subtext

    Surface(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Soft layered icon box matching theme colors
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DashboardCustomize,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            fontSize = 13.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = subtitleColor,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun SpiritualEssentialsGrid(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val readingThemeName by viewModel.sharedReadingTheme.collectAsStateWithLifecycle()
    val themeColors = remember(readingThemeName) { ReadingThemes.getThemeByName(readingThemeName) }
    val isDark = themeColors.isDark

    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)
    val quickAccessTools by viewModel.quickAccessTools.collectAsStateWithLifecycle()

    // Dynamically build/fill tools so we always have at least 8 tools (4 for main 2x2 + 4 for dropdown)
    val mainTools = remember(quickAccessTools) {
        val list = quickAccessTools.toMutableList()
        val all = QuickAccessTool.entries
        for (t in all) {
            if (list.size >= 8) break
            if (!list.contains(t)) {
                list.add(t)
            }
        }
        list
    }

    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Clean Section Header: Leading Badge + Title & Subtitle + Customize Action in Gold
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) GoldTintBgDark else GoldTintBgLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DashboardCustomize,
                        contentDescription = null,
                        tint = NoorGoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = if (isArabic) "الوصول السريع" else "Quick Access",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) themeColors.arabicText else NoorDarkPine,
                            fontSize = 15.5.sp
                        )
                    )
                    Text(
                        text = if (isArabic) "أدواتك المفضلة والمختارة" else "Your essential spiritual tools",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isDark) themeColors.translationText else NoorSageSlate,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { viewModel.openCustomizeQuickAccessSheet() }
                    .testTag("quick_access_customize_all_tools"),
                shape = RoundedCornerShape(10.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = if (isArabic) "تخصيص" else "Customize",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NoorGoldAccent
                        )
                    )
                    Icon(
                        imageVector = if (isArabic) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = NoorGoldAccent,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        // Row 1: Cards 0 and 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickAccessMiniCard(
                tool = mainTools[0],
                isArabic = isArabic,
                slotIndex = 0,
                onClick = { navigateToQuickAccessTool(viewModel, mainTools[0]) },
                modifier = Modifier.weight(1f)
            )
            QuickAccessMiniCard(
                tool = mainTools[1],
                isArabic = isArabic,
                slotIndex = 1,
                onClick = { navigateToQuickAccessTool(viewModel, mainTools[1]) },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: Cards 2 and 3 (All 4 slots are normal tools)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickAccessMiniCard(
                tool = mainTools[2],
                isArabic = isArabic,
                slotIndex = 2,
                onClick = { navigateToQuickAccessTool(viewModel, mainTools[2]) },
                modifier = Modifier.weight(1f)
            )
            QuickAccessMiniCard(
                tool = mainTools[3],
                isArabic = isArabic,
                slotIndex = 3,
                onClick = { navigateToQuickAccessTool(viewModel, mainTools[3]) },
                modifier = Modifier.weight(1f)
            )
        }

        // Under the section: "More Tools" trigger with no container, hidden when expanded
        if (!isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { isExpanded = true }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isArabic) "أدوات إضافية" else "More Tools",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) themeColors.accent else DeepVibrantTeal,
                            fontSize = 13.5.sp
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isArabic) "المزيد من الأدوات" else "More Tools",
                        tint = if (isDark) themeColors.accent else DeepVibrantTeal,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Dropdown containing the other 4 tools perfectly stacked + View All Tools link
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dropdown Row 1: Cards 4 and 5
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAccessMiniCard(
                        tool = mainTools[4],
                        isArabic = isArabic,
                        slotIndex = 4,
                        onClick = { navigateToQuickAccessTool(viewModel, mainTools[4]) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessMiniCard(
                        tool = mainTools[5],
                        isArabic = isArabic,
                        slotIndex = 5,
                        onClick = { navigateToQuickAccessTool(viewModel, mainTools[5]) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Dropdown Row 2: Cards 6 and 7
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAccessMiniCard(
                        tool = mainTools[6],
                        isArabic = isArabic,
                        slotIndex = 6,
                        onClick = { navigateToQuickAccessTool(viewModel, mainTools[6]) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessMiniCard(
                        tool = mainTools[7],
                        isArabic = isArabic,
                        slotIndex = 7,
                        onClick = { navigateToQuickAccessTool(viewModel, mainTools[7]) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // View All Tools link (Centered under dropdown, leaving only this at bottom)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { viewModel.navigateTo(NoorDestination.ALL_TOOLS) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isArabic) "عرض جميع الأدوات" else "View All Tools",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) themeColors.accent else DeepVibrantTeal
                            )
                        )
                        Icon(
                            imageVector = if (isArabic) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = if (isDark) themeColors.accent else DeepVibrantTeal,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// 2.5 FAVORITES & BOOKMARKS SCROLLABLE SECTION
// ============================================================

// ============================================================
// 2.5 FAVORITES & BOOKMARKS SECTION (MULTI-PANE BENTO SANCTUARY)
// ============================================================

@Composable
private fun FavoriteCategoryTile(
    title: String,
    count: Int,
    actionLabel: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    isDark: Boolean,
    homeColors: HomePageColors,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) homeColors.innerContainer else homeColors.outerCardBackground,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
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
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(homeColors.iconBadgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                // Link Badge replacing top right count number
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(homeColors.linkBadgeBg)
                        .padding(horizontal = 8.dp, vertical = 3.5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = homeColors.linkText
                            )
                        )
                        Icon(
                            imageVector = if (isArabic) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = homeColors.linkText,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = homeColors.titleText
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (isArabic) "$count محفوظة" else "$count bookmarked",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        color = homeColors.subtext
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun HomeFavoritesCarousel(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val downloadedSurahs by viewModel.downloadedSurahs.collectAsStateWithLifecycle()

    val textPrimary = homeColors.titleText
    val textSecondary = homeColors.subtext
    val primaryTeal = homeColors.linkText
    val iconTint = homeColors.iconColor

    // Dynamic category count calculations
    val duaFavorites = favorites.filter {
        val t = it.type.uppercase()
        t == "DUA" || t == "SUPPLICATION" || t == "DUAS"
    }
    val duasCount = duaFavorites.size

    val quranFavorites = favorites.filter {
        val t = it.type.uppercase()
        t == "SURAH" || t == "AYAH" || t == "QURAN" || t == "VERSE"
    }
    val quranCount = quranFavorites.size

    val azkarFavorites = favorites.filter {
        val t = it.type.uppercase()
        t == "AZKAR" || t == "DHIKR" || t == "TASBIH"
    }
    val azkarCount = azkarFavorites.size

    val audioFavorites = favorites.filter {
        val t = it.type.uppercase()
        t == "AUDIO" || t == "MP3" || t == "RECITER" || t == "TRACK"
    }
    val audioCount = audioFavorites.size + downloadedSurahs.size

    val totalCount = favorites.size

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header Row
        HomeSectionHeader(
            title = if (isArabic) "المفضلة والمحفوظات" else "Favorites & Library",
            subtext = if (isArabic) "مجموعتك المحفوظة من الآيات والأدعية والتلاوات" else "Your saved collection of verses, duas & audio",
            icon = Icons.Default.Favorite,
            isDark = isDark,
            primaryTeal = primaryTeal,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            trailingContent = {
                HomeSectionActionLabel(
                    text = if (isArabic) "عرض الكل ($totalCount)" else "All ($totalCount)",
                    onClick = { viewModel.navigateTo(NoorDestination.FAVORITES) },
                    isDark = isDark,
                    primaryTeal = primaryTeal,
                    testTag = "favorites_see_more"
                )
            }
        )

        // Scrollable Favorites Row matching the homescreen cards style
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. User's saved favorite cards (if any)
            items(favorites.reversed().take(8)) { favItem ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDark) homeColors.innerContainer else homeColors.outerCardBackground,
                    modifier = Modifier
                        .width(210.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(NoorDestination.FAVORITES) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Card Top Row: Type badge + Heart icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(homeColors.linkBadgeBg)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = favItem.type.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = homeColors.linkText
                                    )
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = homeColors.iconColor,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Title
                        Text(
                            text = favItem.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = homeColors.titleText
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Preview Text
                        if (favItem.arabicText.isNotBlank()) {
                            Text(
                                text = favItem.arabicText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.5.sp,
                                    lineHeight = 18.sp,
                                    color = homeColors.titleText
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else if (favItem.translation.isNotBlank()) {
                            Text(
                                text = favItem.translation,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 13.sp,
                                    color = homeColors.subtext
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Action Link
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(homeColors.linkBadgeBg)
                                .padding(horizontal = 8.dp, vertical = 3.5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = if (isArabic) "فتح" else "Open",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = homeColors.linkText
                                    )
                                )
                                Icon(
                                    imageVector = if (isArabic) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = homeColors.linkText,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Category Shortcut Tiles in the scrollable row
            // Quran Category Tile
            item {
                FavoriteCategoryTile(
                    title = if (isArabic) "السور والآيات" else "Quran Verses",
                    count = quranCount,
                    actionLabel = if (isArabic) "اقرأ" else "Read",
                    icon = {
                        IslamicIconMushaf(
                            modifier = Modifier.size(17.dp),
                            tint = iconTint
                        )
                    },
                    onClick = { viewModel.navigateTo(NoorDestination.QURAN_SURAH_LIST) },
                    isDark = isDark,
                    homeColors = homeColors,
                    isArabic = isArabic,
                    modifier = Modifier.width(160.dp)
                )
            }

            // Duas Category Tile
            item {
                FavoriteCategoryTile(
                    title = if (isArabic) "الأدعية المأثورة" else "Daily Duas",
                    count = duasCount,
                    actionLabel = if (isArabic) "اتلُ" else "Recite",
                    icon = {
                        IslamicIconDua(
                            modifier = Modifier.size(17.dp),
                            tint = iconTint
                        )
                    },
                    onClick = { viewModel.navigateTo(NoorDestination.FAVORITES) },
                    isDark = isDark,
                    homeColors = homeColors,
                    isArabic = isArabic,
                    modifier = Modifier.width(160.dp)
                )
            }

            // Azkar Category Tile
            item {
                FavoriteCategoryTile(
                    title = if (isArabic) "الأذكار والتسبيح" else "Saved Azkar",
                    count = azkarCount,
                    actionLabel = if (isArabic) "سبّح" else "Count",
                    icon = {
                        IslamicIconTasbeeh(
                            modifier = Modifier.size(17.dp),
                            tint = iconTint
                        )
                    },
                    onClick = { viewModel.navigateTo(NoorDestination.FAVORITES) },
                    isDark = isDark,
                    homeColors = homeColors,
                    isArabic = isArabic,
                    modifier = Modifier.width(160.dp)
                )
            }

            // Audio Category Tile
            item {
                FavoriteCategoryTile(
                    title = if (isArabic) "التلاوات الصوتية" else "Audio Library",
                    count = audioCount,
                    actionLabel = if (isArabic) "استمع" else "Listen",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(17.dp)
                        )
                    },
                    onClick = { viewModel.navigateTo(NoorDestination.QURAN_AUDIO_STREAM) },
                    isDark = isDark,
                    homeColors = homeColors,
                    isArabic = isArabic,
                    modifier = Modifier.width(160.dp)
                )
            }
        }
    }
}

// ============================================================
// 4B. DEVOTION STREAK WIDGET
// ============================================================

@Composable
fun UnifiedStreakHomeCard(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    customThemeColors: ReadingThemeColors? = null
) {
    val streakData by viewModel.unifiedStreakData.collectAsStateWithLifecycle()
    val isArabic by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isLangArabic = isArabic.equals("Arabic", ignoreCase = true) || isArabic == "العربية"

    val homeColors = rememberHomePageColors()
    val textPrimary = homeColors.titleText
    val textSecondary = homeColors.subtext
    val primaryTeal = homeColors.linkText
    val secondaryGold = homeColors.iconColor
    val borderDivider = homeColors.dividerBorder
    val surfaceColor = homeColors.outerCardBackground
    val innerSurfaceColor = homeColors.innerContainer
    val progressTrack = homeColors.progressTrack
    val progressFill = homeColors.progressFill
    val badgeBg = homeColors.badgeBg
    val badgeText = homeColors.badgeText
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val completedDeeds = streakData.todayCompletedCount.coerceIn(0, 4)
    val percentage = ((completedDeeds / 4f) * 100).toInt()
    val progressFraction by animateFloatAsState(
        targetValue = (completedDeeds / 4f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "streakProgressFraction"
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // BentoCard for Devotion Streak with 16.dp horizontal padding
        BentoCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("unified_streak_home_card"),
            backgroundColor = surfaceColor,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. Top Row: Title, Subtext & Progress Gauge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isLangArabic) "سلسلة المواظبة" else "Devotion Streak",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = textPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = if (streakData.currentStreak > 0) {
                                if (isLangArabic) "${streakData.currentStreak} أيام متواصلة • $completedDeeds/4 طاعات اليوم" else "${streakData.currentStreak}-Day Streak • $completedDeeds/4 deeds completed today"
                            } else {
                                if (isLangArabic) "ابدأ سلسلتك اليوم • $completedDeeds/4 طاعات اليوم" else "Keep daily consistency active • $completedDeeds/4 deeds completed"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (percentage == 100) badgeText else textSecondary
                            )
                        )
                    }

                    // Progress Ring
                    Box(
                        modifier = Modifier.size(42.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(42.dp)) {
                            val strokeWidth = 3.5.dp.toPx()
                            drawCircle(
                                color = progressTrack,
                                style = Stroke(width = strokeWidth)
                            )
                            if (progressFraction > 0f) {
                                drawArc(
                                    color = progressFill,
                                    startAngle = -90f,
                                    sweepAngle = 360f * progressFraction,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                        }

                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                fontSize = 12.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. 7-Day Devotion Week Track (Filled fire circle with matching flame icon)
                val today = LocalDate.now()
                val currentDayOfWeek = today.dayOfWeek.value // 1 (Mon) .. 7 (Sun)
                val daysOfWeek = (1..7).map { dayNum ->
                    val date = today.minusDays((currentDayOfWeek - dayNum).toLong())
                    val isToday = dayNum == currentDayOfWeek
                    val isPast = dayNum < currentDayOfWeek
                    val isFuture = dayNum > currentDayOfWeek
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

                    DayJewelInfo(
                        label = dayLabel,
                        isToday = isToday,
                        isCompleted = isCompleted,
                        isFuture = isFuture
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
            daysOfWeek.forEach { jewel ->
                val isHighlighted = jewel.isCompleted || (jewel.isToday && streakData.isTodayAnyCompleted)
                val circleBg = if (isHighlighted) homeColors.badgeBg else if (jewel.isToday) homeColors.badgeBg.copy(alpha = 0.5f) else homeColors.innerContainer

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(34.dp),
                        shape = CircleShape,
                        color = circleBg,
                        border = null
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isHighlighted || jewel.isToday) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = "Spiritual Devotion",
                                    tint = if (jewel.isToday) homeColors.badgeText else Color(0xFF107C41),
                                    modifier = Modifier.size(17.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = homeColors.subtext.copy(alpha = 0.35f),
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }

                            Text(
                                text = jewel.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (jewel.isToday || jewel.isCompleted) FontWeight.Bold else FontWeight.Medium,
                                    color = if (jewel.isCompleted) badgeText else if (jewel.isToday) textPrimary else textSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Divider line above footer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(borderDivider.copy(alpha = 0.5f))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Footer Row: Left description text & Right "View Streaks" navigation link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(NoorDestination.STREAKS) }
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isLangArabic) "سجل المواظبة والإحصائيات" else "View devotion history & badges",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isLangArabic) "عرض السلاسل" else "View Streaks",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = homeColors.linkText
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = homeColors.linkText,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class DayJewelInfo(
    val label: String,
    val isToday: Boolean,
    val isCompleted: Boolean,
    val isFuture: Boolean
)

private data class DeedItem(
    val label: String,
    val icon: String,
    val isDone: Boolean,
    val onClick: () -> Unit
)

// ============================================================
// 4C. QURAN KHATMA & READING PROGRESS WIDGET
// ============================================================

// 4C. QURAN KHATMA & READING PROGRESS WIDGET
// ============================================================

@Composable
private fun CompactSectionHeader(
    title: String,
    subtext: String,
    icon: ImageVector,
    isDark: Boolean = false,
    primaryTeal: Color = Color.Unspecified,
    textPrimary: Color = Color.Unspecified,
    textSecondary: Color = Color.Unspecified,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val homeColors = rememberHomePageColors()
    val resolvedPrimary = if (textPrimary != Color.Unspecified) textPrimary else homeColors.titleText
    val resolvedSecondary = if (textSecondary != Color.Unspecified) textSecondary else homeColors.subtext

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = resolvedPrimary
                    )
                )
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        color = resolvedSecondary,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }

        if (trailingContent != null) {
            trailingContent()
        }
    }
}

@Composable
fun QuranKhatmaHomeWidget(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    val textPrimary = homeColors.titleText
    val textSecondary = homeColors.subtext
    val primaryTeal = homeColors.linkText
    val secondaryGold = homeColors.iconColor
    val borderDivider = homeColors.dividerBorder
    val surfaceColor = homeColors.outerCardBackground
    val innerSurfaceColor = homeColors.innerContainer
    val progressTrack = homeColors.progressTrack
    val progressFill = homeColors.progressFill
    val badgeBg = homeColors.badgeBg
    val badgeText = homeColors.badgeText
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val khatmaState by viewModel.khatmaDashboardState.collectAsStateWithLifecycle()
    val readingProgress by viewModel.readingProgress.collectAsStateWithLifecycle()
    val isArabic by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isLangArabic = isArabic.equals("Arabic", ignoreCase = true) || isArabic == "العربية"

    val state = khatmaState
    val isPlanActive = state != null && !state.plan.isCompleted

    val hasBookmark = readingProgress != null
    val surahName = readingProgress?.surahName ?: stringResource(R.string.home_fatihah_name)
    val ayahNum = readingProgress?.ayahNumber ?: 1
    val totalAyahs = readingProgress?.totalAyahs ?: 7

    val onResumeReading = {
        if (readingProgress != null) {
            viewModel.resumeReading(readingProgress!!)
        } else {
            viewModel.selectSurahForReading(QuranData.surahs.first(), 0)
        }
    }

    val readFraction by animateFloatAsState(
        targetValue = if (hasBookmark && totalAyahs > 0) {
            (ayahNum.toFloat() / totalAyahs.toFloat()).coerceIn(0.04f, 1f)
        } else {
            0.04f
        },
        label = "reading_progress_fraction"
    )

    val surah = if (hasBookmark) {
        QuranData.surahs.find { it.number == readingProgress?.surahNumber } ?: QuranData.surahs.first()
    } else {
        QuranData.surahs.first()
    }

    // Unified Outer Container
    BentoCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("quran_khatma_outer_container"),
        backgroundColor = surfaceColor,
        borderWidth = 0.dp,
        borderColor = Color.Transparent,
        elevation = 2.dp,
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ============================================================
            // 1. KHATMA PLANNER TITLE & CONTENT
            // ============================================================
            CompactSectionHeader(
                title = if (isLangArabic) "مخطط ختم القرآن" else "Quran Khatma Planner",
                subtext = if (isPlanActive) {
                    if (isLangArabic) "الخطة النشطة ومعدل القراءة" else "Active plan and reading pace"
                } else {
                    if (isLangArabic) "خطط لتلاوتك اليومية والختم الميسر" else "Plan your daily readings and clean completion"
                },
                icon = Icons.Default.AutoStories,
                isDark = isDark,
                primaryTeal = primaryTeal,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                trailingContent = null
            )

            if (isPlanActive && state != null) {
                val uiInfo = remember(state, isLangArabic) {
                    computeKhatmaPaceUIInfo(state, isLangArabic)
                }
                KhatmaActiveProgressCard(
                    state = state,
                    uiInfo = uiInfo,
                    isLangArabic = isLangArabic,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    primaryTeal = primaryTeal,
                    secondaryGold = secondaryGold,
                    borderDivider = borderDivider,
                    innerSurfaceColor = innerSurfaceColor,
                    surfaceColor = surfaceColor,
                    isDark = isDark,
                    onResumeKhatma = {
                        val targetSurah = QuranData.surahs.find { it.number == state.nextReadingPosition.surahNumber }
                            ?: QuranData.surahs.first()
                        viewModel.selectSurahForReading(
                            targetSurah,
                            (state.nextReadingPosition.ayahNumber - 1).coerceAtLeast(0)
                        )
                    },
                    onListenAudio = {
                        val targetSurah = QuranData.surahs.find { it.number == state.nextReadingPosition.surahNumber }
                            ?: QuranData.surahs.first()
                        viewModel.playSurahAudio(targetSurah)
                    },
                    onPlanDetailsClick = {
                        viewModel.navigateTo(NoorDestination.QURAN_KHATMA)
                    }
                )
            } else {
                KhatmaPlannerIntroCard(
                    isLangArabic = isLangArabic,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    primaryTeal = primaryTeal,
                    borderDivider = borderDivider,
                    innerSurfaceColor = innerSurfaceColor,
                    isDark = isDark,
                    onRedirectToKhatma = {
                        viewModel.navigateTo(NoorDestination.QURAN_KHATMA)
                    }
                )
            }

            if (!isPlanActive) {
                // Elegant Thin Divider
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 0.8.dp,
                    color = borderDivider
                )

                // ============================================================
                // 2. START QURAN READING TITLE & CONTENT
                // ============================================================
                CompactSectionHeader(
                    title = if (hasBookmark) {
                        if (isLangArabic) "متابعة تلاوة القرآن" else "Continue Quran Reading"
                    } else {
                        if (isLangArabic) "بدء تلاوة القرآن" else "Start Quran Reading"
                    },
                    subtext = if (hasBookmark) {
                        if (isLangArabic) "أكمل من حيث توقفت" else "Continue from where you left off"
                    } else {
                        if (isLangArabic) "تلاوة حرة من البداية" else "Free reading from the beginning"
                    },
                    icon = Icons.Default.BookmarkBorder,
                    isDark = isDark,
                    primaryTeal = primaryTeal,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    trailingContent = null
                )

                // Inner container for Al-Fatihah / Reading details (soft background, no border, clean spacing)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onResumeReading)
                        .testTag("start_reading_card"),
                    shape = RoundedCornerShape(16.dp),
                    color = homeColors.innerContainer,
                    border = null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = surah.nameEnglish,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            )

                            val metaText = if (isLangArabic) {
                                "${surah.totalVerses} آيات • ${if (surah.revelationType == "Meccan") "مكية" else "مدنية"}"
                            } else {
                                "${surah.totalVerses} Ayahs • ${surah.revelationType}"
                            }
                            Text(
                                text = metaText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = textSecondary
                                )
                            )
                        }

                        // Clean progress bar without thumb or dot
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(progressTrack)
                                .testTag("start_reading_progress_bar")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(readFraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(progressFill)
                            )
                        }
                    }
                }

                // Open Mushaf Action Button (Tonal style with theme main color, fully rounded, borderless)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = progressFill.copy(alpha = 0.14f),
                    border = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onResumeReading)
                        .testTag("open_mushaf_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = progressFill,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLangArabic) "فتح المصحف" else "Open Mushaf",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = progressFill,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

data class KhatmaPaceUIInfo(
    val paceText: String,
    val isAheadOrOnTrack: Boolean,
    val dayNumberText: String,
    val targetText: String,
    val milestoneText: String,
    val progressPercentText: String,
    val todayProgressText: String,
    val todayRemainingText: String,
    val todayProgressFraction: Float
)

fun computeKhatmaPaceUIInfo(
    state: KhatmaFullDashboardState,
    isLangArabic: Boolean
): KhatmaPaceUIInfo {
    val paceText = when (state.paceStatus) {
        KhatmaPaceStatus.AHEAD -> if (isLangArabic) "متقدم" else "Ahead"
        KhatmaPaceStatus.ON_TRACK -> if (isLangArabic) "في الموعد" else "On Track"
        KhatmaPaceStatus.BEHIND -> if (isLangArabic) "يحتاج متابعة" else "Catch Up"
        KhatmaPaceStatus.COMPLETED -> if (isLangArabic) "مكتملة" else "Completed"
    }
    val isAheadOrOnTrack = state.paceStatus == KhatmaPaceStatus.ON_TRACK || state.paceStatus == KhatmaPaceStatus.AHEAD

    val dayNumberText = if (isLangArabic) {
        "اليوم ${state.currentDayNumber}/${state.totalDays}"
    } else {
        "Day ${state.currentDayNumber}/${state.totalDays}"
    }

    val targetText = if (isLangArabic) {
        "متبقي ${state.daysRemaining} يوم • الورد: ${state.todayTargetAyahs} آية/يوم"
    } else {
        "${state.daysRemaining} days left • Target: ${state.todayTargetAyahs} Ayahs/day"
    }

    val milestoneText = if (isLangArabic) {
        "آية ${state.nextReadingPosition.ayahNumber} • جزء ${state.nextReadingPosition.juzNumber}"
    } else {
        "Ayah ${state.nextReadingPosition.ayahNumber} • Juz ${state.nextReadingPosition.juzNumber}"
    }

    val progressPercentText = "${state.progressPercentage}%"

    val todayProgressText = if (isLangArabic) {
        "ورد اليوم: قرأت ${state.todayReadAyahs} من ${state.todayTargetAyahs} آية"
    } else {
        "Today: ${state.todayReadAyahs} of ${state.todayTargetAyahs} Ayahs"
    }

    val todayRemainingText = if (state.todayRemainingAyahs > 0) {
        if (isLangArabic) "متبقي ${state.todayRemainingAyahs}" else "${state.todayRemainingAyahs} left"
    } else {
        if (isLangArabic) "اكتمل الورد ✓" else "Target met ✓"
    }

    val todayProgressFraction = if (state.todayTargetAyahs > 0) {
        (state.todayReadAyahs.toFloat() / state.todayTargetAyahs.toFloat()).coerceIn(0.04f, 1f)
    } else {
        state.progressFraction.coerceIn(0.04f, 1f)
    }

    return KhatmaPaceUIInfo(
        paceText = paceText,
        isAheadOrOnTrack = isAheadOrOnTrack,
        dayNumberText = dayNumberText,
        targetText = targetText,
        milestoneText = milestoneText,
        progressPercentText = progressPercentText,
        todayProgressText = todayProgressText,
        todayRemainingText = todayRemainingText,
        todayProgressFraction = todayProgressFraction
    )
}

@Composable
private fun KhatmaResumeCta(
    isLangArabic: Boolean,
    nextAyahNumber: Int,
    primaryTeal: Color,
    secondaryGold: Color,
    innerSurfaceColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    Surface(
        shape = RoundedCornerShape(50),
        color = homeColors.linkBadgeBg,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                tint = homeColors.linkText,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = if (isLangArabic) "متابعة تلاوة الختمة (آية $nextAyahNumber)" else "Resume Khatma at Ayah $nextAyahNumber",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = homeColors.linkText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = homeColors.linkText,
                modifier = Modifier.size(13.3.dp)
            )
        }
    }
}

@Composable
private fun KhatmaPresetPillsRow(
    isLangArabic: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    primaryTeal: Color,
    borderDivider: Color,
    innerSurfaceColor: Color,
    isDark: Boolean,
    onPresetSelected: (days: Int, sessionsCount: Int, title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KhatmaQuickPresetCard(
            title = if (isLangArabic) "٣٠ يوماً" else "30 Days",
            subtitle = if (isLangArabic) "جزء/يوم" else "1 Juz/day",
            isHighlighted = false,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            primaryTeal = primaryTeal,
            borderDivider = borderDivider,
            surfaceColor = innerSurfaceColor,
            isDark = isDark,
            modifier = Modifier.weight(1f),
            onClick = {
                onPresetSelected(
                    30,
                    1,
                    if (isLangArabic) "ختمة الشهر (٣٠ يوم)" else "30-Day Ramadan Pace"
                )
            }
        )

        KhatmaQuickPresetCard(
            title = if (isLangArabic) "٦٠ يوماً" else "60 Days",
            subtitle = if (isLangArabic) "١٠ ص/يوم" else "10 pgs/day",
            isHighlighted = false,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            primaryTeal = primaryTeal,
            borderDivider = borderDivider,
            surfaceColor = innerSurfaceColor,
            isDark = isDark,
            modifier = Modifier.weight(1f),
            onClick = {
                onPresetSelected(
                    60,
                    1,
                    if (isLangArabic) "ختمة الستين يوماً" else "60-Day Gentle Pace"
                )
            }
        )

        KhatmaQuickPresetCard(
            title = if (isLangArabic) "٩٠ يوماً" else "90 Days",
            subtitle = if (isLangArabic) "⅓ جزء/يوم" else "⅓ Juz/day",
            isHighlighted = false,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            primaryTeal = primaryTeal,
            borderDivider = borderDivider,
            surfaceColor = innerSurfaceColor,
            isDark = isDark,
            modifier = Modifier.weight(1f),
            onClick = {
                onPresetSelected(
                    90,
                    1,
                    if (isLangArabic) "ختمة التسعين يوماً" else "90-Day Steady Pace"
                )
            }
        )
    }
}

@Composable
private fun KhatmaPlannerIntroCard(
    isLangArabic: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    primaryTeal: Color,
    borderDivider: Color,
    innerSurfaceColor: Color,
    isDark: Boolean,
    onRedirectToKhatma: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Preset pace options
        KhatmaPresetPillsRow(
            isLangArabic = isLangArabic,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            primaryTeal = primaryTeal,
            borderDivider = borderDivider,
            innerSurfaceColor = innerSurfaceColor,
            isDark = isDark,
            onPresetSelected = { _, _, _ ->
                onRedirectToKhatma()
            }
        )

        // Open Khatma Planner button (filled with theme's main color - green in light mode, fully rounded)
        val homeColors = rememberHomePageColors()
        Surface(
            shape = RoundedCornerShape(50),
            color = homeColors.progressFill,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .clickable { onRedirectToKhatma() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 13.dp, horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isLangArabic) "فتح خطة الختمة" else "Open Khatma Planner",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun KhatmaActiveProgressCard(
    state: KhatmaFullDashboardState,
    uiInfo: KhatmaPaceUIInfo,
    isLangArabic: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    primaryTeal: Color,
    secondaryGold: Color,
    borderDivider: Color,
    innerSurfaceColor: Color,
    surfaceColor: Color,
    isDark: Boolean,
    onResumeKhatma: () -> Unit,
    onListenAudio: () -> Unit,
    onPlanDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPlanDetailsClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isLangArabic) "ختمة القرآن الكريم" else "Quran Khatma",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = homeColors.titleText
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = homeColors.linkBadgeBg
                        ) {
                            Text(
                                text = uiInfo.dayNumberText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = homeColors.linkText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = uiInfo.targetText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            color = homeColors.subtext
                        )
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = homeColors.linkBadgeBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(homeColors.linkText)
                    )
                    Text(
                        text = uiInfo.paceText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = homeColors.linkText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = homeColors.innerContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isLangArabic) "محطة التلاوة القادمة" else "NEXT RECITATION MILESTONE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = homeColors.subtext,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = homeColors.outerCardBackground
                    ) {
                        Text(
                            text = uiInfo.milestoneText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = homeColors.titleText,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.nextReadingPosition.surahNameArabic,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = AmiriQuranFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 23.sp,
                                color = homeColors.titleText,
                                lineHeight = 30.sp
                            )
                        )
                        Text(
                            text = "${state.nextReadingPosition.surahNumber}. ${state.nextReadingPosition.surahNameEnglish}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp,
                                color = homeColors.linkText
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = homeColors.outerCardBackground
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiInfo.progressPercentText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = homeColors.linkText,
                                    fontSize = 16.5.sp
                                )
                            )
                            Text(
                                text = if (isLangArabic) "منجز" else "DONE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = homeColors.subtext,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = uiInfo.todayProgressText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = homeColors.titleText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        text = uiInfo.todayRemainingText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (state.todayRemainingAyahs > 0) homeColors.subtext else homeColors.linkText,
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(homeColors.progressTrack)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(uiInfo.todayProgressFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(homeColors.progressFill)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                KhatmaResumeCta(
                    isLangArabic = isLangArabic,
                    nextAyahNumber = state.nextReadingPosition.ayahNumber,
                    primaryTeal = primaryTeal,
                    secondaryGold = secondaryGold,
                    innerSurfaceColor = innerSurfaceColor,
                    onClick = onResumeKhatma
                )
            }
        }

        if (state.isTodayTargetAchieved) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = homeColors.linkBadgeBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = homeColors.linkText,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isLangArabic) "ما شاء الله! حققت ورد اليوم بنجاح (+${state.todayReadAyahs} آية)" else "Masha'Allah! Today's reading goal achieved (+${state.todayReadAyahs} Ayahs)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = homeColors.linkText,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(9.dp),
                color = homeColors.linkBadgeBg,
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .clickable { onListenAudio() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = homeColors.linkText,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isLangArabic) "استماع للسورة" else "Listen Audio",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = homeColors.linkText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onPlanDetailsClick() }
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLangArabic) "عرض جدول الختمة والتفاصيل" else "Khatma Plan & Schedule",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = homeColors.linkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = homeColors.linkText,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun QuranKhatmaMainCard(
    viewModel: MainViewModel,
    state: KhatmaFullDashboardState?,
    isPlanActive: Boolean,
    isLangArabic: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    primaryTeal: Color,
    secondaryGold: Color,
    borderDivider: Color,
    surfaceColor: Color,
    innerSurfaceColor: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    BentoCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = surfaceColor,
        contentPadding = PaddingValues(18.dp)
    ) {
        if (isPlanActive && state != null) {
            val uiInfo = remember(state, isLangArabic) {
                computeKhatmaPaceUIInfo(state, isLangArabic)
            }
            KhatmaActiveProgressCard(
                state = state,
                uiInfo = uiInfo,
                isLangArabic = isLangArabic,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryTeal = primaryTeal,
                secondaryGold = secondaryGold,
                borderDivider = borderDivider,
                innerSurfaceColor = innerSurfaceColor,
                surfaceColor = surfaceColor,
                isDark = isDark,
                onResumeKhatma = {
                    val surah = QuranData.surahs.find { it.number == state.nextReadingPosition.surahNumber }
                        ?: QuranData.surahs.first()
                    viewModel.selectSurahForReading(
                        surah,
                        (state.nextReadingPosition.ayahNumber - 1).coerceAtLeast(0)
                    )
                },
                onListenAudio = {
                    val surah = QuranData.surahs.find { it.number == state.nextReadingPosition.surahNumber }
                        ?: QuranData.surahs.first()
                    viewModel.playSurahAudio(surah)
                },
                onPlanDetailsClick = {
                    viewModel.navigateTo(NoorDestination.QURAN_KHATMA)
                }
            )
        } else {
            KhatmaPlannerIntroCard(
                isLangArabic = isLangArabic,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                primaryTeal = primaryTeal,
                borderDivider = borderDivider,
                innerSurfaceColor = innerSurfaceColor,
                isDark = isDark,
                onRedirectToKhatma = {
                    viewModel.navigateTo(NoorDestination.QURAN_KHATMA)
                }
            )
        }
    }
}

/**
 * Interactive Khatma preset duration pill (e.g. 30, 60, 90 days)
 */
@Composable
private fun KhatmaQuickPresetCard(
    title: String,
    subtitle: String,
    isHighlighted: Boolean,
    textPrimary: Color = Color.Unspecified,
    textSecondary: Color = Color.Unspecified,
    primaryTeal: Color = Color.Unspecified,
    borderDivider: Color = Color.Unspecified,
    surfaceColor: Color = Color.Unspecified,
    isDark: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val homeColors = rememberHomePageColors()
    val cardBg = if (isHighlighted) homeColors.linkBadgeBg else homeColors.innerContainer
    val titleCol = if (isHighlighted) homeColors.linkText else homeColors.titleText
    val subCol = if (isHighlighted) homeColors.linkText.copy(alpha = 0.85f) else homeColors.subtext

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = cardBg,
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = titleCol,
                    fontSize = 12.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = subCol,
                    fontSize = 10.sp
                )
            )
        }
    }
}

// ============================================================
// 5. DAILY REVELATION (AYAH OF THE DAY & AUTHENTIC SUPPLICATION)
// ============================================================

@Composable
fun DailyAyahAndDuaShowcase(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val dailyAyah = DuaData.dailyAyah
    val dailyDua = DuaData.dailyDua
    val showArabicSecondary by viewModel.showArabicSecondaryText.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val isArabicPrimary = appLanguage.equals("Arabic", ignoreCase = true) || appLanguage == "العربية" || appLanguage.startsWith("ar", ignoreCase = true)

    val textPrimary = homeColors.titleText
    val textSecondary = homeColors.subtext
    val primaryTeal = homeColors.linkText
    val secondaryGold = homeColors.iconColor
    val surfaceColor = homeColors.outerCardBackground
    val borderDivider = homeColors.dividerBorder

    val ayahGradient = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                surfaceColor,
                surfaceColor
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                surfaceColor,
                homeColors.innerContainer
            )
        )
    }
    val ayahBadgeBg = homeColors.badgeBg
    val ayahBadgeText = homeColors.badgeText
    val iconDuaTint = homeColors.iconColor

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header Row: Action buttons on right (Copy and Share - icon only)
        HomeSectionHeader(
            title = if (isArabicPrimary) "الوحي اليومي" else "Daily Revelation",
            subtext = if (isArabicPrimary) "إلهام يومي من الآيات والأدعية" else "Daily inspiration from Ayat & Duas",
            icon = Icons.Default.AutoAwesome,
            isDark = isDark,
            primaryTeal = primaryTeal,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            trailingContent = {
                val textToCopy = if (isArabicPrimary) {
                    "${dailyAyah.arabicText}\n(${dailyAyah.referenceAr.ifBlank { dailyAyah.reference }})"
                } else {
                    "${dailyAyah.translation} (${dailyAyah.reference})${if (showArabicSecondary) "\n" + dailyAyah.arabicText else ""}"
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HomeSectionIconButton(
                        icon = Icons.Default.ContentCopy,
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Ayah of the Day", textToCopy)
                            clipboard.setPrimaryClip(clip)
                            viewModel.showToast(context.getString(R.string.home_ayah_copied))
                        },
                        contentDescription = "Copy",
                        isDark = isDark,
                        testTag = "daily_revelation_copy"
                    )
                    HomeSectionIconButton(
                        icon = Icons.Default.Share,
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, textToCopy)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, null))
                        },
                        contentDescription = "Share",
                        isDark = isDark,
                        testTag = "daily_revelation_share"
                    )
                }
            }
        )

        // Daily Ayah - Card with a subtle, elegant gradient gold starting with white-ish color
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .universalCardShadow(
                    shape = RoundedCornerShape(20.dp),
                    elevation = 3.dp,
                    isDark = isDark
                )
                .clip(RoundedCornerShape(20.dp))
                .background(ayahGradient)
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = ayahBadgeBg
                    ) {
                        Text(
                            text = stringResource(R.string.home_ayah_of_the_day),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.5.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp,
                                color = ayahBadgeText
                            )
                        )
                    }

                    Text(
                        text = if (isArabicPrimary) dailyAyah.referenceAr.ifBlank { dailyAyah.reference } else dailyAyah.reference,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textSecondary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isArabicPrimary) {
                    // In Arabic mode: ONLY show pristine Arabic text, no English translation or transliteration
                    Text(
                        text = dailyAyah.arabicText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = QuranArabicFont.AMIRI.fontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Normal,
                            color = textPrimary,
                            textAlign = TextAlign.Start,
                            textDirection = TextDirection.Rtl,
                            lineHeight = 38.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // 1. PRIMARY LAYER (ENGLISH DOMINANT): Regular font weight, no quotes
                    Text(
                        text = dailyAyah.translation,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.5.sp,
                            color = textPrimary,
                            lineHeight = 24.sp
                        )
                    )

                    // 2. SECONDARY LAYER: Phonetic Transliteration
                    if (dailyAyah.transliteration.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dailyAyah.transliteration,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontSize = 13.5.sp,
                                color = textSecondary,
                                lineHeight = 20.sp
                            )
                        )
                    }

                    // 3. TERTIARY LAYER: Traditional Arabic Script (Subject to global toggle, Regular weight)
                    if (showArabicSecondary && dailyAyah.arabicText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = borderDivider)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = dailyAyah.arabicText,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = QuranArabicFont.AMIRI.fontFamily,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Normal,
                                color = textPrimary.copy(alpha = 0.9f),
                                textAlign = TextAlign.Start,
                                textDirection = TextDirection.Rtl,
                                lineHeight = 36.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Daily Dua - Main card color
        BentoCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            backgroundColor = surfaceColor,
            borderColor = borderDivider,
            onClick = { viewModel.navigateTo(NoorDestination.DUAS_LIBRARY) },
            contentPadding = PaddingValues(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabicPrimary) dailyDua.categoryAr.ifBlank { "دعاء اليوم" } else dailyDua.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = secondaryGold
                            )
                        )
                    }

                    Text(
                        text = if (isArabicPrimary) dailyDua.referenceAr.ifBlank { dailyDua.reference } else dailyDua.reference,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = textSecondary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isArabicPrimary) {
                    // In Arabic mode: ONLY show pristine Arabic text
                    Text(
                        text = dailyDua.arabicText,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = QuranArabicFont.AMIRI.fontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Normal,
                            color = textPrimary,
                            textAlign = TextAlign.Start,
                            textDirection = TextDirection.Rtl,
                            lineHeight = 38.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // 1. Primary Layer: English Translation
                    Text(
                        text = dailyDua.translation,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.5.sp,
                            color = textPrimary,
                            lineHeight = 24.sp
                        )
                    )

                    // 2. Secondary Layer: Phonetic Transliteration
                    if (dailyDua.transliteration.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = dailyDua.transliteration,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontSize = 13.5.sp,
                                color = textSecondary,
                                lineHeight = 20.sp
                            )
                        )
                    }

                    // 3. Tertiary Layer: Arabic Script (Regular weight)
                    if (showArabicSecondary && dailyDua.arabicText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = borderDivider)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = dailyDua.arabicText,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = QuranArabicFont.AMIRI.fontFamily,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Normal,
                                color = textPrimary.copy(alpha = 0.9f),
                                textAlign = TextAlign.Start,
                                textDirection = TextDirection.Rtl,
                                lineHeight = 36.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// ============================================================
// 6. HEART & SOUL: CURATED MOOD WISDOM REFLECTION
// ============================================================

@Composable
fun DailyMoodWisdomSection(
    viewModel: MainViewModel,
    selectedMood: String,
    isIslamic: Boolean,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val showArabicSecondary by viewModel.showArabicSecondaryText.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isArabicPrimary = appLanguage.equals("Arabic", ignoreCase = true) || appLanguage == "العربية" || appLanguage.startsWith("ar", ignoreCase = true)

    val textPrimary = homeColors.titleText
    val textSecondary = homeColors.subtext
    val primaryTeal = homeColors.linkText
    val secondaryGold = homeColors.iconColor
    val surfaceColor = homeColors.outerCardBackground
    val borderDivider = homeColors.dividerBorder

    val moods = listOf(
        "Anxious" to stringResource(R.string.mood_anxious),
        "Grateful" to stringResource(R.string.mood_grateful),
        "Tired" to stringResource(R.string.mood_tired),
        "Hopeful" to stringResource(R.string.mood_hopeful),
        "Lost" to stringResource(R.string.mood_lost),
        "Peaceful" to stringResource(R.string.mood_peaceful)
    )

    val cardGradient = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                surfaceColor,
                surfaceColor
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                surfaceColor,
                homeColors.innerContainer
            )
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header Row: Action buttons on right (Copy and Share - icon only)
        HomeSectionHeader(
            title = stringResource(R.string.home_spiritual_mood),
            subtext = if (isArabicPrimary) "تأملات روحية ملائمة لحالتك الوجدانية" else "Daily reflection tailored to your emotional state",
            icon = Icons.Default.AutoAwesome,
            isDark = isDark,
            primaryTeal = primaryTeal,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            trailingContent = {
                val wisdom = viewModel.getCurrentMoodWisdom()
                val textToCopy = if (isArabicPrimary) {
                    "${wisdom.arabicText}\n${wisdom.explanationAr.ifBlank { wisdom.explanation }}\n(${wisdom.sourceAr.ifBlank { wisdom.source }})"
                } else {
                    "${wisdom.translation}\n${wisdom.explanation}\n(${wisdom.source})${if (showArabicSecondary && wisdom.arabicText.isNotBlank()) "\n" + wisdom.arabicText else ""}"
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HomeSectionIconButton(
                        icon = Icons.Default.ContentCopy,
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Spiritual Mood Reflection", textToCopy)
                            clipboard.setPrimaryClip(clip)
                            viewModel.showToast(context.getString(R.string.home_ayah_copied))
                        },
                        contentDescription = "Copy",
                        isDark = isDark,
                        testTag = "spiritual_mood_copy"
                    )
                    HomeSectionIconButton(
                        icon = Icons.Default.Share,
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, textToCopy)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, null))
                        },
                        contentDescription = "Share",
                        isDark = isDark,
                        testTag = "spiritual_mood_share"
                    )
                }
            }
        )

        // Horizontal Mood Pills: Selected in main theme color (iconBadgeBg & iconColor), non-selected in unselectedBg
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 2.dp)
        ) {
            items(moods) { (moodKey, moodLabel) ->
                val isSelected = selectedMood.equals(moodKey, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isSelected) homeColors.iconBadgeBg else homeColors.unselectedBg,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            viewModel.selectMood(moodKey)
                        }
                ) {
                    Text(
                        text = moodLabel,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            color = if (isSelected) homeColors.iconColor else homeColors.unselectedText
                        )
                    )
                }
            }
        }

        // Wisdom Container wrapped in gradient card matching Ayah of the Day
        val wisdom = viewModel.getCurrentMoodWisdom()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .universalCardShadow(
                    shape = RoundedCornerShape(20.dp),
                    elevation = 3.dp,
                    isDark = isDark
                )
                .clip(RoundedCornerShape(20.dp))
                .background(cardGradient)
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.animateContentSize()
            ) {
                // Reference Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabicPrimary) wisdom.sourceAr.ifBlank { wisdom.source } else wisdom.source,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = homeColors.linkText
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isArabicPrimary) {
                    // In Arabic mode
                    if (wisdom.arabicText.isNotBlank()) {
                        Text(
                            text = wisdom.arabicText,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = QuranArabicFont.AMIRI.fontFamily,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Normal,
                                color = textPrimary,
                                textAlign = TextAlign.Start,
                                textDirection = TextDirection.Rtl,
                                lineHeight = 38.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Text(
                        text = wisdom.explanationAr.ifBlank { wisdom.explanation },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.5.sp,
                            color = textPrimary,
                            lineHeight = 24.sp
                        )
                    )
                } else {
                    // 1. Primary Translation
                    Text(
                        text = wisdom.translation,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.5.sp,
                            color = textPrimary,
                            lineHeight = 24.sp
                        )
                    )

                    // 2. Detailed Spiritual Explanation (clean text without star icon)
                    if (wisdom.explanation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = wisdom.explanation,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.5.sp,
                                color = textSecondary,
                                lineHeight = 21.sp
                            )
                        )
                    }

                    // 3. Arabic Script Text (Subject to global toggle / secondary display)
                    if (showArabicSecondary && wisdom.arabicText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = borderDivider)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = wisdom.arabicText,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = QuranArabicFont.AMIRI.fontFamily,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Normal,
                                color = textPrimary,
                                textAlign = TextAlign.Start,
                                textDirection = TextDirection.Rtl,
                                lineHeight = 36.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// 7. PREMIUM UPGRADE CARD (Under Spiritual Mood Section)
// ============================================================

@Composable
fun PremiumUpgradeCard(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val homeColors = rememberHomePageColors()

    BentoCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = homeColors.outerCardBackground,
        borderColor = homeColors.dividerBorder,
        onClick = {
            viewModel.showToast(context.getString(R.string.home_premium_toast))
        },
        contentPadding = PaddingValues(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Icon container
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(homeColors.iconBadgeBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Premium Icon",
                            tint = homeColors.iconColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.home_unlock_pro),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = homeColors.titleText
                                )
                            )
                            // Clean PRO Badge
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = homeColors.badgeBg
                            ) {
                                Text(
                                    text = stringResource(R.string.home_pro_badge),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = homeColors.badgeText
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = stringResource(R.string.home_pro_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.5.sp,
                                color = homeColors.subtext
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Feature Highlights Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = homeColors.innerContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(text = "✨", fontSize = 12.sp)
                        Text(
                            text = stringResource(R.string.home_unlimited_ai),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = homeColors.subtext
                            )
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = homeColors.innerContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(text = "🎧", fontSize = 12.sp)
                        Text(
                            text = stringResource(R.string.home_offline_qaris),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = homeColors.subtext
                            )
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = homeColors.innerContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(text = "🕌", fontSize = 12.sp)
                        Text(
                            text = stringResource(R.string.home_ad_free),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = homeColors.subtext
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CTA Button
            Surface(
                onClick = {
                    viewModel.showToast(context.getString(R.string.home_premium_toast))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = homeColors.badgeBg
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_unlock_upgrade_cta),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = homeColors.badgeText
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = homeColors.badgeText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}



// ============================================================
// 8. QURAN AUDIO RECITERS SHOWCASE
// ============================================================

@Composable
fun QuranRecitersShowcase(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val textPrimary = homeColors.titleText
    val textSecondary = homeColors.subtext
    val primaryTeal = homeColors.linkText
    val secondaryGold = homeColors.iconColor
    val borderDivider = homeColors.dividerBorder

    val selectedReciter by viewModel.selectedReciter.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()

    val reciters = QuranData.reciters
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) || appLanguage == "العربية" || appLanguage.startsWith("ar", ignoreCase = true)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Direct Header Row on page background
        HomeSectionHeader(
            title = if (isArabic) "استمع إلى القرآن" else "Listen To The Quran",
            subtext = if (isArabic) "استمع إلى تلاوات عطرة من كبار القراء" else "Listen to beautiful recitations from top reciters",
            icon = Icons.Default.Headphones,
            isDark = isDark,
            primaryTeal = primaryTeal,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            trailingContent = {
                HomeSectionActionLabel(
                    text = if (isArabic) "عرض المزيد" else "View More",
                    onClick = { viewModel.navigateTo(NoorDestination.QURAN_RECITERS) },
                    isDark = isDark,
                    primaryTeal = primaryTeal,
                    testTag = "reciters_see_more"
                )
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Edge-to-edge horizontal scrolling row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(reciters) { reciter ->
                val reciterName = if (isArabic) reciter.nameAr.ifBlank { reciter.name } else reciter.name
                val reciterStyle = if (isArabic) reciter.styleAr.ifBlank { reciter.style } else reciter.style
                val isSelected = reciter.id == selectedReciter.id

                val displayName = remember(reciterName) {
                    val parts = reciterName.split(" ")
                    if (parts.size > 2) "${parts.first()} ${parts.last()}" else reciterName
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(78.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            viewModel.selectReciter(reciter)
                            if (!isAudioPlaying) {
                                viewModel.playSurahAudio(QuranData.surahs.first(), openPlayer = false)
                            }
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(76.dp)
                    ) {
                        // Circular Avatar with ring (link color if selected, divider color if unselected)
                        Box(
                            modifier = Modifier
                                .size(66.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) homeColors.linkText else homeColors.dividerBorder)
                                .padding(2.5.dp)
                                .clip(CircleShape)
                        ) {
                            if (reciter.avatarUrl.isNotBlank()) {
                                AsyncImage(
                                    model = reciter.avatarUrl,
                                    contentDescription = reciterName,
                                    contentScale = ContentScale.Crop,
                                    error = reciter.drawableRes?.let { painterResource(it) } ?: painterResource(R.drawable.ic_noor_logo),
                                    placeholder = reciter.drawableRes?.let { painterResource(it) } ?: painterResource(R.drawable.ic_noor_logo),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(homeColors.innerContainer)
                                )
                            } else if (reciter.drawableRes != null) {
                                Image(
                                    painter = painterResource(reciter.drawableRes),
                                    contentDescription = reciterName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            } else {
                                // Placeholder avatar
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(homeColors.innerContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = homeColors.iconColor,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }

                        // Active "Audio" Pill Badge overlapping bottom rim of circle if selected
                        if (isSelected) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = homeColors.badgeBg,
                                border = null,
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = 2.dp)
                            ) {
                                Text(
                                    text = if (isArabic) "صوتي" else "Audio",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = homeColors.badgeText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = if (isSelected) homeColors.linkText else textPrimary,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = reciterStyle.split("•").firstOrNull()?.trim() ?: reciterStyle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            color = textSecondary,
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

@Composable
fun HomeSectionHeader(
    title: String,
    subtext: String,
    icon: ImageVector,
    isDark: Boolean = false,
    primaryTeal: Color = Color.Unspecified,
    textPrimary: Color = Color.Unspecified,
    textSecondary: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val homeColors = rememberHomePageColors()
    val resolvedPrimary = if (textPrimary != Color.Unspecified) textPrimary else homeColors.titleText
    val resolvedSecondary = if (textSecondary != Color.Unspecified) textSecondary else homeColors.subtext

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = resolvedPrimary
                    )
                )
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        color = resolvedSecondary
                    )
                )
            }
        }

        if (trailingContent != null) {
            trailingContent()
        }
    }
}


