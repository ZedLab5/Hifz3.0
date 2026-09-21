package com.example.ui.quran

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ReadingThemeColors

@Composable
fun QuranBottomBar(
    themeColors: ReadingThemeColors,
    isAutoScrolling: Boolean,
    isAutoScrollPaused: Boolean,
    autoScrollSpeed: AutoScrollSpeed,
    onStartAutoScroll: () -> Unit,
    onToggleAutoScrollPlayPause: () -> Unit,
    onExitAutoScroll: () -> Unit,
    onSpeedDecrease: () -> Unit,
    onSpeedIncrease: () -> Unit,
    isAudioPlaying: Boolean,
    onToggleAudio: () -> Unit,
    isMushafMode: Boolean,
    onToggleMushafMode: () -> Unit,
    onOpenTextSettings: () -> Unit,
    onOpenNotes: () -> Unit,
    notesCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(themeColors.surface)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = isAutoScrolling,
            animationSpec = tween(150),
            label = "QuranBottomBarContent"
        ) { autoScrolling ->
            if (autoScrolling) {
                // Secondary Control Row: Expanded Auto-Scroll Controls
                AutoScrollControlRow(
                    themeColors = themeColors,
                    isPaused = isAutoScrollPaused,
                    speed = autoScrollSpeed,
                    onTogglePlayPause = onToggleAutoScrollPlayPause,
                    onExit = onExitAutoScroll,
                    onDecreaseSpeed = onSpeedDecrease,
                    onIncreaseSpeed = onSpeedIncrease
                )
            } else {
                // Default 5-Icon Bar: Auto-Scroll, Audio, Mushaf, Text, Notes
                DefaultQuranBottomNavRow(
                    themeColors = themeColors,
                    onStartAutoScroll = onStartAutoScroll,
                    isAudioPlaying = isAudioPlaying,
                    onToggleAudio = onToggleAudio,
                    isMushafMode = isMushafMode,
                    onToggleMushafMode = onToggleMushafMode,
                    onOpenTextSettings = onOpenTextSettings,
                    onOpenNotes = onOpenNotes,
                    notesCount = notesCount
                )
            }
        }
    }
}

@Composable
private fun DefaultQuranBottomNavRow(
    themeColors: ReadingThemeColors,
    onStartAutoScroll: () -> Unit,
    isAudioPlaying: Boolean,
    onToggleAudio: () -> Unit,
    isMushafMode: Boolean,
    onToggleMushafMode: () -> Unit,
    onOpenTextSettings: () -> Unit,
    onOpenNotes: () -> Unit,
    notesCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Mushaf Mode (Left)
        QuranNavItem(
            icon = Icons.AutoMirrored.Filled.MenuBook,
            label = "Mushaf",
            isActive = isMushafMode,
            themeColors = themeColors,
            testTag = "quran_bottom_nav_mushaf",
            onClick = onToggleMushafMode
        )

        // 2. Scroll (Left of Center)
        QuranNavItem(
            icon = Icons.Default.KeyboardDoubleArrowDown,
            label = "Scroll",
            isActive = false,
            themeColors = themeColors,
            testTag = "quran_bottom_nav_auto_scroll",
            onClick = onStartAutoScroll
        )

        // 3. Audio / Play - DEAD CENTER
        QuranNavItem(
            icon = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.Headphones,
            label = if (isAudioPlaying) "Pause" else "Audio",
            isActive = isAudioPlaying,
            themeColors = themeColors,
            testTag = "quran_bottom_nav_audio",
            onClick = onToggleAudio
        )

        // 4. Text (size / typography - Right of Center)
        QuranNavItem(
            icon = Icons.Default.FormatSize,
            label = "Text",
            isActive = false,
            themeColors = themeColors,
            testTag = "quran_bottom_nav_text",
            onClick = onOpenTextSettings
        )

        // 5. Notes (Right)
        QuranNavItem(
            icon = Icons.Default.EditNote,
            label = "Notes",
            isActive = false,
            badgeCount = notesCount,
            themeColors = themeColors,
            testTag = "quran_bottom_nav_notes",
            onClick = onOpenNotes
        )
    }
}

@Composable
private fun RowScope.QuranNavItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    themeColors: ReadingThemeColors,
    testTag: String,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val activeColor = themeColors.accent
    val inactiveColor = themeColors.translationText

    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) activeColor else inactiveColor,
                modifier = Modifier.size(22.dp)
            )

            // Subtle badge indicator if notes exist
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 1.dp, end = 2.dp)
                        .size(7.dp)
                        .background(activeColor, CircleShape)
                )
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) activeColor else inactiveColor,
                fontSize = 10.5.sp
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun AutoScrollControlRow(
    themeColors: ReadingThemeColors,
    isPaused: Boolean,
    speed: AutoScrollSpeed,
    onTogglePlayPause: () -> Unit,
    onExit: () -> Unit,
    onDecreaseSpeed: () -> Unit,
    onIncreaseSpeed: () -> Unit
) {
    val speeds = AutoScrollSpeed.values()
    val currentIndex = speeds.indexOf(speed)
    val canDecrease = currentIndex > 0
    val canIncrease = currentIndex < speeds.size - 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Far Left: Play / Pause Button
        Surface(
            shape = CircleShape,
            color = themeColors.accent,
            shadowElevation = 2.dp,
            onClick = onTogglePlayPause,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .testTag("auto_scroll_play_pause_btn")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isPaused) "Resume Auto-Scroll" else "Pause Auto-Scroll",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Center: Speed Controls (- [Label] +)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Minus Button
            Surface(
                shape = CircleShape,
                color = if (canDecrease) themeColors.background else themeColors.background.copy(alpha = 0.35f),
                border = null,
                onClick = onDecreaseSpeed,
                enabled = canDecrease,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .testTag("auto_scroll_speed_decrease")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease Auto-Scroll Speed",
                        tint = if (canDecrease) themeColors.arabicText else themeColors.translationText.copy(alpha = 0.35f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Current Speed Label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(min = 84.dp)
            ) {
                Text(
                    text = "SPEED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.translationText,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = speed.label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText
                    ),
                    maxLines = 1
                )
            }

            // Plus Button
            Surface(
                shape = CircleShape,
                color = if (canIncrease) themeColors.background else themeColors.background.copy(alpha = 0.35f),
                border = null,
                onClick = onIncreaseSpeed,
                enabled = canIncrease,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .testTag("auto_scroll_speed_increase")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase Auto-Scroll Speed",
                        tint = if (canIncrease) themeColors.arabicText else themeColors.translationText.copy(alpha = 0.35f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Far Right: Exit Button (stops auto-scroll, returns to 5-icon bar)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = themeColors.background,
            border = null,
            onClick = onExit,
            modifier = Modifier
                .height(38.dp)
                .clip(RoundedCornerShape(16.dp))
                .testTag("auto_scroll_exit_btn")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = themeColors.arabicText,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Exit",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText
                    )
                )
            }
        }
    }
}
