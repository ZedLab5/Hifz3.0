package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.Surah
import com.example.ui.quran.SurahListItemCard
import com.example.ui.theme.NoorTheme
import com.example.ui.theme.rememberHomePageColors
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleSurah = Surah(
        number = 1,
        nameArabic = "الفاتحة",
        nameEnglish = "Al-Fatihah (The Opening)",
        englishMeaning = "The Opening",
        revelationType = "Meccan",
        totalVerses = 7
    )
    composeTestRule.setContent {
      NoorTheme {
        SurahListItemCard(
            surah = sampleSurah,
            isAudioPlaying = false,
            isFavorite = false,
            homeColors = rememberHomePageColors(),
            onClick = {},
            onPlayAudio = {},
            onToggleFavorite = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
