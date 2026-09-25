package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderDividerDark
import com.example.ui.theme.BorderDividerLight
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealLight
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.rememberHomePageColors

@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    elevation: Dp = 4.dp,
    hasGlow: Boolean = false,
    glowBrush: Brush? = null,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val homeColors = rememberHomePageColors()
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.surface.luminance() < 0.5f

    val resolvedBgColor = if (backgroundColor != Color.Unspecified) {
        backgroundColor
    } else {
        homeColors.outerCardBackground
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.985f else 1.0f,
        label = "bentoScale"
    )

    val currentElevation by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed && onClick != null) 1.5.dp else elevation.coerceAtLeast(3.5.dp),
        label = "bentoElevation"
    )

    // Subtle physical surface micro-gradient that gives lighting depth without changing colors
    val cardBackgroundModifier = if (glowBrush != null) {
        Modifier.background(glowBrush)
    } else if (backgroundColor == Color.Unspecified || backgroundColor == homeColors.outerCardBackground) {
        if (isDark) {
            Modifier.background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E242B),
                        Color(0xFF171A20)
                    )
                )
            )
        } else {
            Modifier.background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFFAFCFA)
                    )
                )
            )
        }
    } else {
        Modifier.background(resolvedBgColor)
    }

    // Precise rim stroke for physical separation and tactile depth
    val borderModifier = if (borderWidth > 0.dp && borderColor != Color.Transparent) {
        Modifier.border(borderWidth, borderColor, shape)
    } else {
        if (isDark) {
            Modifier.border(1.dp, Color(0xFF28303A), shape)
        } else {
            Modifier.border(1.dp, Color(0xFFECEFF1), shape)
        }
    }

    val shadowModifier = Modifier.universalCardShadow(
        shape = shape,
        elevation = currentElevation,
        isDark = isDark
    )

    Box(
        modifier = modifier
            .scale(scale)
            .then(shadowModifier)
            .clip(shape)
            .then(cardBackgroundModifier)
            .then(borderModifier)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = androidx.compose.material3.ripple(),
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun GoldBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    val homeColors = rememberHomePageColors()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = homeColors.badgeBg,
        border = null
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                color = homeColors.badgeText,
                fontSize = 11.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
        )
    }
}

/**
 * Universal shadow for all cards across the Home screen.
 * Consistent spot & ambient shadow parameters with clear, rich material elevation.
 */
fun Modifier.universalCardShadow(
    shape: Shape = RoundedCornerShape(20.dp),
    elevation: Dp = 6.dp,
    isDark: Boolean = false
): Modifier {
    return if (isDark) {
        this.shadow(
            elevation = elevation.coerceAtLeast(4.dp),
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.55f),
            spotColor = Color.Black.copy(alpha = 0.75f)
        )
    } else {
        this.shadow(
            elevation = elevation.coerceAtLeast(6.dp),
            shape = shape,
            ambientColor = Color(0xFF14241E).copy(alpha = 0.12f),
            spotColor = Color(0xFF0D1B15).copy(alpha = 0.20f)
        )
    }
}

/**
 * Polished, compact action label with a soft teal background and teal text.
 * Used for section header links across the home screen ("All Tools", "Streaks", "+ Tracker", "Custom", etc.)
 * Always renders a consistent text + arrow format for a unified homescreen design system.
 */
@Composable
fun HomeSectionActionLabel(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    primaryTeal: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    icon: ImageVector? = Icons.AutoMirrored.Filled.ArrowForward,
    testTag: String? = null
) {
    val homeColors = rememberHomePageColors()
    val resolvedColor = if (primaryTeal != Color.Unspecified) primaryTeal else homeColors.linkText
    val bg = if (backgroundColor != Color.Unspecified) backgroundColor else homeColors.linkBadgeBg

    Surface(
        shape = RoundedCornerShape(50),
        color = bg,
        modifier = modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = resolvedColor
                )
            )
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = resolvedColor,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}

/**
 * Subtle, circular icon-only action button (e.g. Copy, Share) matching the homescreen soft-teal style.
 */
@Composable
fun HomeSectionIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    primaryTeal: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    testTag: String? = null
) {
    val homeColors = rememberHomePageColors()
    val resolvedColor = if (primaryTeal != Color.Unspecified) primaryTeal else homeColors.linkText
    val bg = if (backgroundColor != Color.Unspecified) backgroundColor else homeColors.linkBadgeBg

    Surface(
        shape = CircleShape,
        color = bg,
        modifier = modifier
            .size(30.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = resolvedColor,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}


