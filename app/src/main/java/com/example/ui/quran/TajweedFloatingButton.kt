package com.example.ui.quran

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.QuranReadingThemeColors

/**
 * Floating Tajweed button that stays persistently visible during scrolling in both reading modes.
 * Opens the Tajweed Rules & Settings menu when tapped.
 */
@Composable
fun TajweedFloatingButton(
    isVisible: Boolean,
    position: TajweedButtonPosition,
    themeColors: QuranReadingThemeColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val alignment = when (position) {
        TajweedButtonPosition.TOP_LEFT -> Alignment.TopStart
        TajweedButtonPosition.TOP_RIGHT -> Alignment.TopEnd
        TajweedButtonPosition.BOTTOM_LEFT -> Alignment.BottomStart
        TajweedButtonPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
    }

    val paddingModifier = when (position) {
        TajweedButtonPosition.TOP_LEFT -> Modifier.padding(top = topInset + 68.dp, start = 14.dp)
        TajweedButtonPosition.TOP_RIGHT -> Modifier.padding(top = topInset + 68.dp, end = 14.dp)
        TajweedButtonPosition.BOTTOM_LEFT -> Modifier.padding(bottom = bottomInset + 80.dp, start = 14.dp)
        TajweedButtonPosition.BOTTOM_RIGHT -> Modifier.padding(bottom = bottomInset + 80.dp, end = 14.dp)
    }

    Box(
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = isVisible,
            modifier = Modifier
                .align(alignment)
                .then(paddingModifier),
            enter = fadeIn() + scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)),
            exit = fadeOut() + scaleOut()
        ) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = themeColors.accent),
                        onClick = onClick
                    )
                    .testTag("floating_tajweed_rules_button"),
                shape = RoundedCornerShape(20.dp),
                color = themeColors.surface.copy(alpha = if (themeColors.isDark) 0.94f else 0.97f),
                shadowElevation = 5.dp,
                border = null
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(themeColors.accent.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Tajweed Rules",
                            tint = themeColors.accent,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Tajweed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText,
                                fontSize = 11.5.sp,
                                lineHeight = 13.sp
                            )
                        )
                        Text(
                            text = "أَحْكَام",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = themeColors.accent,
                                fontSize = 10.sp,
                                lineHeight = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
