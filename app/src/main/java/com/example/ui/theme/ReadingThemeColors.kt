package com.example.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Shared theme color tokens for reading-focused screens (Quran, Duas, Azkar, Tasbih).
 */
data class ReadingThemeColors(
    val background: Color,
    val surface: Color,
    val border: Color,
    val arabicText: Color,
    val translationText: Color,
    val transliterationText: Color,
    val accent: Color,
    val link: Color,
    val name: String,
    val isDark: Boolean = false
)

typealias QuranReadingThemeColors = ReadingThemeColors

object ReadingThemes {
    val MadaniCrisp = ReadingThemeColors(
        background = CanvasMint,
        surface = SurfaceWhite,
        border = Color.Transparent,
        arabicText = TextPrimaryLight,
        translationText = TextSecondaryLight,
        transliterationText = Color(0xFFD9A44E), // Secondary: Antique Gold
        accent = Color(0xFF1BA486),              // Main: Vibrant Teal
        link = Color(0xFF2A4365),                // Links: Sapphire Blue
        name = "Madani Crisp",
        isDark = false
    )

    val SepiaParchment = ReadingThemeColors(
        background = GoldTintBgLight,
        surface = SurfaceWhite,
        border = Color.Transparent,
        arabicText = TextPrimaryLight,
        translationText = TextSecondaryLight,
        transliterationText = Color(0xFFD9A44E), // Secondary: Antique Gold
        accent = Color(0xFFD9A44E),              // Main: Antique Gold
        link = Color(0xFF7A5C3E),                // Links: Warm Brown
        name = "Sepia Parchment",
        isDark = false
    )

    val ObsidianNight = ReadingThemeColors(
        background = CanvasDark,
        surface = SurfaceDark,
        border = Color.Transparent,
        arabicText = TextPrimaryDark,
        translationText = TextSecondaryDark,
        transliterationText = Color(0xFFC9A227), // Secondary: Antique Gold (Dark variant)
        accent = Color(0xFF2FBF96),              // Main: Vibrant Mint
        link = Color(0xFF2FBF96),                // Links: Vibrant Mint
        name = "Obsidian Night",
        isDark = true
    )

    val allThemes = listOf(MadaniCrisp, SepiaParchment, ObsidianNight)

    fun fromColorScheme(
        colorScheme: androidx.compose.material3.ColorScheme,
        isDark: Boolean
    ): ReadingThemeColors {
        return ReadingThemeColors(
            background = if (isDark) CanvasDark else CanvasMint,
            surface = if (isDark) SurfaceDark else SurfaceWhite,
            border = Color.Transparent,
            arabicText = if (isDark) TextPrimaryDark else TextPrimaryLight,
            translationText = if (isDark) TextSecondaryDark else TextSecondaryLight,
            transliterationText = if (isDark) Color(0xFFC9A227) else Color(0xFFD9A44E),
            accent = if (isDark) Color(0xFF2FBF96) else Color(0xFF1BA486),
            link = if (isDark) Color(0xFF2FBF96) else Color(0xFF2A4365),
            name = if (isDark) "Obsidian Night" else "Madani Crisp",
            isDark = isDark
        )
    }

    fun getThemeByName(name: String): ReadingThemeColors {
        return when (name) {
            "Sepia Parchment" -> SepiaParchment
            "Obsidian Night" -> ObsidianNight
            else -> MadaniCrisp
        }
    }
}

/**
 * Shared horizontal row for selecting reading canvas themes.
 */
@Composable
fun ReadingThemePickerRow(
    selectedThemeName: String,
    onThemeSelect: (String) -> Unit,
    activeTheme: ReadingThemeColors,
    modifier: Modifier = Modifier
) {
    val themes = listOf(
        "Madani Crisp" to CanvasMint,
        "Obsidian Night" to CanvasDark
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        themes.forEach { (name, color) ->
            val isSelected = selectedThemeName == name
            val isObsidian = name == "Obsidian Night"

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onThemeSelect(name) },
                shape = RoundedCornerShape(10.dp),
                color = color,
                border = null,
                shadowElevation = if (isSelected && !activeTheme.isDark) 2.dp else 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.split(" ").first(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isObsidian) TextPrimaryDark else if (isSelected) activeTheme.accent else TextPrimaryLight
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Full Section Component with Title, Subtitle, and Picker Row.
 */
@Composable
fun ReadingThemeSection(
    selectedThemeName: String,
    onThemeSelect: (String) -> Unit,
    activeTheme: ReadingThemeColors,
    modifier: Modifier = Modifier,
    title: String = "Reading Canvas Theme",
    subtitle: String = "Choose a palette tailored for comfort, night-time reading, or daytime clarity"
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = activeTheme.arabicText
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = activeTheme.translationText,
                    fontSize = 12.sp
                )
            )
        }

        ReadingThemePickerRow(
            selectedThemeName = selectedThemeName,
            onThemeSelect = onThemeSelect,
            activeTheme = activeTheme
        )
    }
}
