package com.example.ui.theme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Universal Spacing Tokens for consistent layout rhythm across the entire Noor application.
 */
object NoorSpacing {
    /** Universal spacing between a header/title and its accompanying subtext across all screens */
    val TitleSubtextSpacing: Dp = 5.dp

    /** Tight title to subtext spacing for dense components, list items, and toggles */
    val TitleSubtextSpacingTight: Dp = 3.5.dp

    /** Comfortable title to subtext spacing for major cards, hero banners, and section headers */
    val TitleSubtextSpacingComfortable: Dp = 6.5.dp

    /** Spacing between distinct sections on a screen */
    val SectionSpacing: Dp = 16.dp

    /** Standard margin below NoorTopBar */
    val BelowTopBar: Dp = 18.dp

    /** Universal inner padding for cards */
    val CardInternalPadding: Dp = 16.dp
}

/**
 * Universal Composable Spacer between titles and subtext across all cards and bars.
 */
@Composable
fun TitleSubtextSpacer(
    modifier: Modifier = Modifier,
    spacing: Dp = NoorSpacing.TitleSubtextSpacing
) {
    Spacer(modifier = modifier.height(spacing))
}
