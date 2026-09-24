package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderDividerDark
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealLight
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.pageColor

fun sanitizeTextForUi(text: String?, isArabicUi: Boolean): String? {
    if (text == null) return null
    if (isArabicUi) return text
    // If the UI is English, strip purely Arabic segments or text containing Arabic characters
    val hasArabic = text.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' || it in '\u08A0'..'\u08FF' || it in '\uFB50'..'\uFDFF' || it in '\uFE70'..'\uFEFF' }
    if (!hasArabic) return text

    // If text contains a separator like " • " with English on one side and Arabic on the other, keep the English part
    if (text.contains("•")) {
        val nonArabicParts = text.split("•")
            .map { it.trim() }
            .filter { part ->
                part.isNotEmpty() && !part.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' || it in '\u08A0'..'\u08FF' || it in '\uFB50'..'\uFDFF' || it in '\uFE70'..'\uFEFF' }
            }
        if (nonArabicParts.isNotEmpty()) {
            return nonArabicParts.joinToString(" • ")
        }
    }

    // Pure Arabic string in English UI: return empty or null so it's not shown
    return null
}

val NoorGlassBg = Color.White.copy(alpha = 0.12f)
val NoorTopBarHairlineBorder = Color.White.copy(alpha = 0.12f)
val NoorGlassBorder = Color.White.copy(alpha = 0.15f)
val NoorGlassIconTint = Color.White.copy(alpha = 0.92f)

/**
 * Standard glass circular container for top bar action/navigation icons.
 */
@Composable
fun NoorGlassIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    activeTint: Color? = null,
    defaultTint: Color? = null,
    badgeCount: Int? = null
) {
    val resolvedDefaultTint = defaultTint ?: pageColor("TopBar", "icon_tint")
    val resolvedActiveTint = activeTint ?: pageColor("TopBar", "icon_badge")
    val glassBg = resolvedDefaultTint.copy(alpha = 0.15f)

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(glassBg)
            .clickable(
                onClick = onClick,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) resolvedActiveTint else resolvedDefaultTint,
            modifier = Modifier.size(20.dp)
        )
        if (badgeCount != null && badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 3.dp, end = 3.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(resolvedActiveTint)
            )
        }
    }
}

/**
 * NoorTopBar - Reusable premium top bar for all primary application destinations.
 *
 * Background:
 * - Light Mode: 135° gradient from #0F433F -> #2E7C73
 * - Dark Mode: flat #171F23
 */
@Composable
fun NoorTopBar(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    backIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    backContentDescription: String = "Back",
    isLargeTitle: Boolean = false,
    isDark: Boolean = false,
    themeColors: ReadingThemeColors? = null,
    titleContent: (@Composable () -> Unit)? = null,
    backgroundColorOverride: Color? = null,
    titleColorOverride: Color? = null,
    subtitleColorOverride: Color? = null,
    iconColorOverride: Color? = null,
    borderColorOverride: Color? = null,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val isSystemDark = isDark || colorScheme.surface.luminance() < 0.5f
    val isArabicUi = LocalLayoutDirection.current == LayoutDirection.Rtl

    val displayEyebrow = sanitizeTextForUi(eyebrow, isArabicUi)
    val displayTitle = sanitizeTextForUi(title, isArabicUi) ?: title
    val displaySubtitle = sanitizeTextForUi(subtitle, isArabicUi)

    val bgColor = backgroundColorOverride ?: pageColor("TopBar", "background")
    val borderCol = borderColorOverride ?: pageColor("Home", "divider_border")
    val titleCol = titleColorOverride ?: pageColor("TopBar", "title")
    val subCol = subtitleColorOverride ?: pageColor("TopBar", "subtitle")

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    drawLine(
                        color = borderCol,
                        start = Offset(0f, size.height - strokeWidth / 2),
                        end = Offset(size.width, size.height - strokeWidth / 2),
                        strokeWidth = strokeWidth
                    )
                }
                .statusBarsPadding()
                .height(if (!displaySubtitle.isNullOrBlank()) 78.dp else 70.dp)
                .padding(bottom = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Area: Back Button (if present) + Title/Eyebrow/Subtitle
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (onBackClick != null) {
                        NoorGlassIconButton(
                            onClick = onBackClick,
                            icon = backIcon,
                            contentDescription = backContentDescription,
                            defaultTint = iconColorOverride
                        )
                    }

                    if (titleContent != null) {
                        titleContent()
                    } else {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = displayTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = if (isLargeTitle) 21.sp else 19.sp,
                                    color = titleCol
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (!displaySubtitle.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(com.example.ui.theme.NoorSpacing.TitleSubtextSpacing))
                                Text(
                                    text = displaySubtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = subCol
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Right Area: 0-2 (or more) trailing action glass icons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actions()
                }
            }
        }
        BelowTopBarSpacer()
    }
}

/**
 * Reusable vertical spacing to be placed immediately below NoorTopBar
 * inside Scaffold content padding to enforce consistent layout rhythm.
 */
@Composable
fun BelowTopBarSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(com.example.ui.theme.NoorSpacing.BelowTopBar))
}
