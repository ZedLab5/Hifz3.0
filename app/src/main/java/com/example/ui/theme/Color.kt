package com.example.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor

// =========================================================================
// CENTRALIZED DESIGN SYSTEM COLOR TOKENS (LIGHT & DARK MODE)
// =========================================================================

// --- 1. Page Backgrounds ---
val CanvasMint = Color(0xFFFCFBF9)           // Light page background (#FCFBF9)
val CanvasDark = Color(0xFF10161A)           // Dark page background (#10161A)
val AppBackground = CanvasMint
val AppBackgroundLight = CanvasMint

// --- 2. Card Surfaces ---
val SurfaceWhite = Color(0xFFFFFFFF)         // Light card surface (#FFFFFF)
val SurfaceDark = Color(0xFF171F23)          // Dark card surface (#171F23)
val SurfaceElevatedLight = Color(0xFFF8F9FA) // Light card surface elevated (#F8F9FA)
val SurfaceElevatedDark = Color(0xFF1E2830)  // Dark card surface elevated (#1E2830)
val SurfaceElevated = SurfaceElevatedLight

// --- 3. Primary Teal & Gold Reset (Single Grey) ---
val SingleResetGrey = Color(0xFF757575)

val PrimaryTealLight = SingleResetGrey
val PrimaryTealDark = SingleResetGrey
val DeepVibrantTeal = SingleResetGrey
val LuminousCyan = SingleResetGrey
val DarkPine = SingleResetGrey
val NoorDarkPine = SingleResetGrey

// --- 4. Header Backgrounds (Top bar remains teal) ---
val HeaderTealStart = Color(0xFF0F433F)
val HeaderTealEnd = Color(0xFF2E7C73)
val HeaderGradientLight = SolidColor(HeaderTealStart)
val NoorTopBarGradient = HeaderGradientLight
val HeaderFlatDark = SurfaceDark             // Dark mode: flat #171F23

// --- 5. Progress Bar & Fill Gradients ---
val ProgressGradientLight = SolidColor(SingleResetGrey)
val ProgressGradientDark = SolidColor(SingleResetGrey)
val PrimaryTealGradient = ProgressGradientLight

// --- 6. Secondary Gold ---
val SecondaryGoldLight = SingleResetGrey
val SecondaryGoldDark = SingleResetGrey
val MetallicGold = SingleResetGrey
val GoldGradientEnd = SingleResetGrey
val NoorTopBarAntiqueGold = SingleResetGrey
val NoorTopBarEyebrowGold = SingleResetGrey

// --- 7. Gold Tint Background ---
val GoldTintBgLight = SingleResetGrey
val GoldTintBgDark = SingleResetGrey
val GoldHighlight = SingleResetGrey
val GoldBadgeBg = SingleResetGrey

// --- 8. Text Primary & Secondary ---
val TextPrimaryLight = Color(0xFF2C2C2A)     // Light text primary (#2C2C2A)
val TextPrimaryDark = Color(0xFFE7E9E8)      // Dark text primary (#E7E9E8)
val TextSecondaryLight = Color(0xFF5F5E5A)   // Light text secondary (#5F5E5A)
val TextSecondaryDark = Color(0xFF97A3A0)    // Dark text secondary (#97A3A0)
val SlateTealMuted = SingleResetGrey

// --- 9. Borders & Dividers ---
val BorderDividerLight = Color(0x14000000)   // Light border rgba(0,0,0,0.08)
val BorderDividerDark = Color(0xFF26333C)    // Dark border (#26333C)
val BorderTealGray = SingleResetGrey
val BorderTealLight = SingleResetGrey
val SoftTealTint = SingleResetGrey

// --- 10. Status & Semantic Colors ---
// Success Green: for checkmarks & habit-tracker "done" states only (not brand identity)
val SuccessGreenLight = Color(0xFF3B6D11)    // Light success (#3B6D11)
val SuccessGreenDark = Color(0xFF6FBE6B)     // Dark success (#6FBE6B)
val SuccessGreen = SuccessGreenLight

// Danger Red: for errors and destructive warnings only
val DangerRedLight = Color(0xFFE24B4A)       // Light danger (#E24B4A)
val DangerRedDark = Color(0xFFE5726E)        // Dark danger (#E5726E)
val DangerRedBgLight = Color(0xFFFDE8E8)      // Light danger container background
val DangerRedBgDark = Color(0xFF3B1219)       // Dark danger container background
val ErrorRed = DangerRedLight
val ErrorRedLight = DangerRedBgLight
val WarningAmber = SingleResetGrey

// Dark canvas alias
val CanvasObsidianNight = CanvasDark

// Decorative / legacy support
val GoldAccentGradient = SolidColor(SingleResetGrey)
val CardDarkGradient = SolidColor(SurfaceDark)
val FrostedGlassGradient = SolidColor(SurfaceWhite)

// --- 11. Custom Inner/Nested Gold Colors ---
val NestedGoldCardLight = SingleResetGrey
val NestedGoldCardDark = SingleResetGrey


