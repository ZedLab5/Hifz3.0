package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Three-way theme mode supporting LIGHT, DARK, and WARM theme variants.
 */
enum class AppThemeMode {
    LIGHT,
    DARK,
    WARM
}

/**
 * CompositionLocal providing the currently active theme mode (LIGHT, DARK, or WARM)
 * to any Composable in the UI tree.
 */
val LocalAppThemeMode = staticCompositionLocalOf { AppThemeMode.LIGHT }

/**
 * Data holder storing exactly three color values for an element:
 * - light: Color value in Light mode
 * - dark: Color value in Dark mode
 * - warm: Color value in Warm mode
 */
data class ThemeColorValue(
    val light: Color,
    val dark: Color,
    val warm: Color
) {
    fun getValue(mode: AppThemeMode): Color = when (mode) {
        AppThemeMode.LIGHT -> light
        AppThemeMode.DARK -> dark
        AppThemeMode.WARM -> warm
    }
}

/**
 * Central lookup table for page-specific theme colors.
 * Organized by page name -> element name -> ThemeColorValue(light, dark, warm).
 *
 * NOTE: This registry is purely a storage data lookup table without shared design logic.
 * Every page entry is independent and isolated.
 */
object PageColorRegistry {

    // Fallback color for missing registry lookups (Bright Magenta so unpopulated or misspelled entries stand out immediately)
    private val MissingEntryMagenta = Color(0xFFFF00FF)
    private val FallbackColorValue = ThemeColorValue(
        light = MissingEntryMagenta,
        dark = MissingEntryMagenta,
        warm = MissingEntryMagenta
    )

    // Central registry storage: PageName -> Map<ElementName, ThemeColorValue>
    private val registry: MutableMap<String, MutableMap<String, ThemeColorValue>> = mutableMapOf()

    init {
        initHomeRegistry()
        initTopBarRegistry()
    }

    private fun initHomeRegistry() {
        val page = "Home"

        // 1. Page background (Bright, clean off-white for crisp card contrast)
        set(page, "page_background",
            light = Color(0xFFF6F8F7),
            dark = Color(0xFF12151A),
            warm = Color(0xFFFCFBF9)
        )

        // 2. Outer card background
        set(page, "outer_card_background",
            light = Color(0xFFFFFFFF),
            dark = Color(0xFF1A1E24),
            warm = Color(0xFFFFFFFF)
        )

        // 3. Inner container (nested, one step down from outer card)
        set(page, "inner_container",
            light = Color(0xFFF6FAF8),
            dark = Color(0xFF22272A),
            warm = Color(0xFFFAF6EE)
        )

        // 4. Title text
        set(page, "title_text",
            light = Color(0xFF1F1F1F),
            dark = Color(0xFFE8E6DF),
            warm = Color(0xFF1F1F1F)
        )

        // 5. Subtext / secondary text
        set(page, "subtext",
            light = Color(0xFF5F5E5A),
            dark = Color(0xFF8B8D91),
            warm = Color(0xFF7A6650)
        )

        // 6. Link / "see more" action text (Deep Sapphire Navy for Light Theme)
        set(page, "link_text",
            light = Color(0xFF2A4365),
            dark = Color(0xFF2FBF96),
            warm = Color(0xFF7A5C3E)
        )

        // Link pill background (Soft sapphire tint)
        set(page, "link_badge_bg",
            light = Color(0xFFEDF2F7),
            dark = Color(0xFF1E282D),
            warm = Color(0xFFF2EAE1)
        )

        // 7. Icon color (Switched green to #D9A44E)
        set(page, "icon_color",
            light = Color(0xFF1BA486),
            dark = Color(0xFF2FBF96),
            warm = Color(0xFFD9A44E)
        )

        // 8. Icon's circular badge background (Switched to #FBF3E4)
        set(page, "icon_badge_bg",
            light = Color(0xFFE6F6F1),
            dark = Color(0xFF1E3A32),
            warm = Color(0xFFFBF3E4)
        )

        // 9. Divider / border
        set(page, "divider_border",
            light = Color(0xFFECEFF1),
            dark = Color(0xFF1E282D),
            warm = Color(0xFFEDE0C8)
        )

        // 10. Badge or pill background (Soft light tint for #0F433F dark teal text)
        set(page, "badge_bg",
            light = Color(0xFFFBF0DC),
            dark = Color(0xFF2E2415),
            warm = Color(0xFFE6F2F0)
        )

        // 11. Badge or pill text (Vibrant golden yellow in light mode)
        set(page, "badge_text",
            light = Color(0xFFC68A00),
            dark = Color(0xFFC9A227),
            warm = Color(0xFF0F433F)
        )

        // 12. Progress bar track (unfilled)
        set(page, "progress_track",
            light = Color(0xFFEBEBEB),
            dark = Color(0xFF2B3134),
            warm = Color(0xFFF0E5D4)
        )

        // 13. Progress bar fill / active indicator
        set(page, "progress_fill",
            light = Color(0xFF1BA486),
            dark = Color(0xFF2FBF96),
            warm = Color(0xFFD9A44E)
        )

        // Specific element: Premium banner
        set(page, "premium_banner_bg",
            light = Color(0xFF1BA486),
            dark = Color(0xFF2FBF96),
            warm = Color(0xFFD9A44E)
        )
        set(page, "premium_banner_title",
            light = Color(0xFFFFFFFF),
            dark = Color(0xFFFFFFFF),
            warm = Color(0xFFFFFFFF)
        )
        set(page, "premium_badge_bg",
            light = Color(0xFFFBF0DC),
            dark = Color(0xFF2E2415),
            warm = Color(0xFFE6F2F0)
        )
        set(page, "premium_badge_text",
            light = Color(0xFFC68A00),
            dark = Color(0xFFC9A227),
            warm = Color(0xFF0F433F)
        )
        set(page, "premium_chip_bg",
            light = Color(0x33FFFFFF),
            dark = Color(0x33FFFFFF),
            warm = Color(0x33FFFFFF)
        )
        set(page, "premium_chip_text",
            light = Color(0xFFFFFFFF),
            dark = Color(0xFFFFFFFF),
            warm = Color(0xFFFFFFFF)
        )
        set(page, "premium_button_bg",
            light = Color(0xFFFFFFFF),
            dark = Color(0xFFFFFFFF),
            warm = Color(0xFFFFFFFF)
        )
        set(page, "premium_button_text",
            light = Color(0xFF1BA486),
            dark = Color(0xFF2FBF96),
            warm = Color(0xFFD9A44E)
        )

        // Specific element: Next Prayer Hero photo card accents (Unchanged)
        set(page, "hero_accent",
            light = Color(0xFF1BA486),
            dark = Color(0xFF2FBF96),
            warm = Color(0xFFFFB030)
        )
        set(page, "hero_badge_bg",
            light = Color(0xFFFBF0DC),
            dark = Color(0xFF2E2415),
            warm = Color(0xFFE8D2AE)
        )
        set(page, "hero_badge_text",
            light = Color(0xFFC68A00),
            dark = Color(0xFFC9A227),
            warm = Color(0xFF874D14)
        )

        // Resting / unselected pill states
        set(page, "unselected_bg",
            light = Color(0xFFEDF2F7),
            dark = Color(0xFF1E282D),
            warm = Color(0xFFF2EAE1)
        )
        set(page, "unselected_text",
            light = Color(0xFF2A4365),
            dark = Color(0xFF8B8D91),
            warm = Color(0xFF7A5C3E)
        )

        // Button fills
        set(page, "button_fill_bg",
            light = Color(0xFF1BA486),
            dark = Color(0xFF2FBF96),
            warm = Color(0xFFD9A44E)
        )
        set(page, "button_fill_text",
            light = Color(0xFFFFFFFF),
            dark = Color(0xFFFFFFFF),
            warm = Color(0xFFFFFFFF)
        )
    }

    private fun initTopBarRegistry() {
        val page = "TopBar"
        set(page, "background",
            light = Color(0xFF146B3E),
            dark = Color(0xFF146B3E),
            warm = Color(0xFF0F433F)
        )
        set(page, "title",
            light = Color(0xFFFFFFFF),
            dark = Color(0xFFFFFFFF),
            warm = Color(0xFFFFFFFF)
        )
        set(page, "subtitle",
            light = Color(0xFFFFFFFF),
            dark = Color(0xFFFFFFFF),
            warm = Color(0xFFFFFFFF)
        )
        set(page, "icon_tint",
            light = Color(0xFFFFFFFF),
            dark = Color(0xFFFFFFFF),
            warm = Color(0xFFFFFFFF)
        )
        set(page, "icon_badge",
            light = Color(0xFFD9A43E),
            dark = Color(0xFFC9A227),
            warm = Color(0xFFD9A44E)
        )
    }

    /**
     * Register or update a color entry for a specific page and element.
     */
    fun set(page: String, element: String, light: Color, dark: Color, warm: Color) {
        val pageEntries = registry.getOrPut(page) { mutableMapOf() }
        pageEntries[element] = ThemeColorValue(light, dark, warm)
    }

    /**
     * Set a ThemeColorValue entry directly.
     */
    fun set(page: String, element: String, value: ThemeColorValue) {
        val pageEntries = registry.getOrPut(page) { mutableMapOf() }
        pageEntries[element] = value
    }

    /**
     * Retrieve the Color for a given page, element, and active AppThemeMode.
     */
    fun getColor(page: String, element: String, mode: AppThemeMode): Color {
        val pageMap = registry[page]
        val colorValue = pageMap?.get(element) ?: FallbackColorValue
        return colorValue.getValue(mode)
    }

    /**
     * Retrieve the ThemeColorValue struct for a given page and element.
     */
    fun getColorValue(page: String, element: String): ThemeColorValue {
        return registry[page]?.get(element) ?: FallbackColorValue
    }

    /**
     * Clear all registered values (for reset testing).
     */
    fun clear() {
        registry.clear()
    }
}

/**
 * Composable helper function allowing any page or component to look up its own
 * theme color directly from the central registry using the currently active AppThemeMode.
 *
 * Example usage:
 *   val buttonColor = pageColor(page = "Salat", element = "main_button")
 */
@Composable
fun pageColor(page: String, element: String): Color {
    val activeMode = LocalAppThemeMode.current
    return PageColorRegistry.getColor(page, element, activeMode)
}

/**
 * Resolved home page color bundle for convenient and type-safe consumption in HomeScreen
 * and HomeSections, populated directly from the PageColorRegistry lookup table.
 */
data class HomePageColors(
    val pageBackground: Color,
    val outerCardBackground: Color,
    val innerContainer: Color,
    val titleText: Color,
    val subtext: Color,
    val linkText: Color,
    val linkBadgeBg: Color,
    val iconColor: Color,
    val iconBadgeBg: Color,
    val dividerBorder: Color,
    val badgeBg: Color,
    val badgeText: Color,
    val progressTrack: Color,
    val progressFill: Color,
    val premiumBannerBg: Color,
    val premiumBannerTitle: Color,
    val premiumBadgeBg: Color,
    val premiumBadgeText: Color,
    val premiumChipBg: Color,
    val premiumChipText: Color,
    val premiumButtonBg: Color,
    val premiumButtonText: Color,
    val heroAccent: Color,
    val heroBadgeBg: Color,
    val heroBadgeText: Color,
    val unselectedBg: Color,
    val unselectedText: Color,
    val buttonFillBg: Color,
    val buttonFillText: Color
) {
    val iconBadgeBackground: Color get() = iconBadgeBg
}

@Composable
fun rememberHomePageColors(): HomePageColors {
    val page = "Home"
    return HomePageColors(
        pageBackground = pageColor(page, "page_background"),
        outerCardBackground = pageColor(page, "outer_card_background"),
        innerContainer = pageColor(page, "inner_container"),
        titleText = pageColor(page, "title_text"),
        subtext = pageColor(page, "subtext"),
        linkText = pageColor(page, "link_text"),
        linkBadgeBg = pageColor(page, "link_badge_bg"),
        iconColor = pageColor(page, "icon_color"),
        iconBadgeBg = pageColor(page, "icon_badge_bg"),
        dividerBorder = pageColor(page, "divider_border"),
        badgeBg = pageColor(page, "badge_bg"),
        badgeText = pageColor(page, "badge_text"),
        progressTrack = pageColor(page, "progress_track"),
        progressFill = pageColor(page, "progress_fill"),
        premiumBannerBg = pageColor(page, "premium_banner_bg"),
        premiumBannerTitle = pageColor(page, "premium_banner_title"),
        premiumBadgeBg = pageColor(page, "premium_badge_bg"),
        premiumBadgeText = pageColor(page, "premium_badge_text"),
        premiumChipBg = pageColor(page, "premium_chip_bg"),
        premiumChipText = pageColor(page, "premium_chip_text"),
        premiumButtonBg = pageColor(page, "premium_button_bg"),
        premiumButtonText = pageColor(page, "premium_button_text"),
        heroAccent = pageColor(page, "hero_accent"),
        heroBadgeBg = pageColor(page, "hero_badge_bg"),
        heroBadgeText = pageColor(page, "hero_badge_text"),
        unselectedBg = pageColor(page, "unselected_bg"),
        unselectedText = pageColor(page, "unselected_text"),
        buttonFillBg = pageColor(page, "button_fill_bg"),
        buttonFillText = pageColor(page, "button_fill_text")
    )
}
