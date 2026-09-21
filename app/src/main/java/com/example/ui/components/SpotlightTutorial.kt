package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.R
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealLight
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.roundToInt

/**
 * Data model for a single step in a spotlight tutorial.
 *
 * @property key Unique identifier corresponding to the target element registered with [Modifier.spotlightTarget].
 * @property title Primary title shown in the guidance card.
 * @property description Detailed explanation for the highlighted feature.
 * @property cornerRadius Corner radius of the cutout hole.
 * @property padding Extra padding around the measured target bounds to ensure breathing room.
 * @property onEnter Optional callback executed when this step becomes active (e.g. open a bottom sheet).
 * @property onExit Optional callback executed when this step is navigated away from or dismissed (e.g. close the bottom sheet).
 */
data class SpotlightStep(
    val key: String,
    val title: String,
    val description: String,
    val cornerRadius: Dp = 14.dp,
    val padding: Dp = 6.dp,
    val showCustomizeAnimation: Boolean = false,
    val onEnter: (() -> Unit)? = null,
    val onExit: (() -> Unit)? = null
)

/**
 * CompositionLocal providing access to the home screen's [SpotlightState].
 */
val LocalHomeSpotlightState = staticCompositionLocalOf<SpotlightState?> { null }

/**
 * Observable state holder managing spotlight tutorial steps, target layout coordinates, and visibility.
 */
@Stable
class SpotlightState {
    var isVisible by mutableStateOf(false)
        private set

    var currentStepIndex by mutableIntStateOf(0)
        private set

    var steps by mutableStateOf<List<SpotlightStep>>(emptyList())
        private set

    private val _targetBounds = mutableStateMapOf<String, Rect>()
    val targetBounds: Map<String, Rect> get() = _targetBounds

    val currentStep: SpotlightStep?
        get() = steps.getOrNull(currentStepIndex)

    val currentTargetBounds: Rect?
        get() = currentStep?.let { _targetBounds[it.key] }

    val isLastStep: Boolean
        get() = steps.isNotEmpty() && currentStepIndex >= steps.size - 1

    /**
     * Initiates the spotlight tutorial from step one.
     */
    fun start(newSteps: List<SpotlightStep>) {
        if (newSteps.isEmpty()) return
        currentStep?.onExit?.invoke()
        steps = newSteps
        currentStepIndex = 0
        isVisible = true
        currentStep?.onEnter?.invoke()
    }

    /**
     * Advances to the next step, or closes the overlay if on the last step.
     */
    fun next() {
        val exitingStep = currentStep
        if (isLastStep) {
            dismiss()
        } else {
            exitingStep?.onExit?.invoke()
            currentStepIndex++
            currentStep?.onEnter?.invoke()
        }
    }

    /**
     * Closes and hides the spotlight overlay.
     */
    fun dismiss() {
        val exitingStep = currentStep
        isVisible = false
        exitingStep?.onExit?.invoke()
    }

    /**
     * Registers or updates the measured bounds in root coordinates for a given target key.
     */
    fun registerTarget(key: String, bounds: Rect) {
        _targetBounds[key] = bounds
    }

    /**
     * Removes target registration for a given key.
     */
    fun unregisterTarget(key: String) {
        _targetBounds.remove(key)
    }
}

/**
 * Remembers a [SpotlightState] across recompositions.
 */
@Composable
fun rememberSpotlightState(): SpotlightState {
    return remember { SpotlightState() }
}

/**
 * Modifier extension that registers a UI component's layout position with [SpotlightState].
 * Any screen can tag arbitrary composables with unique keys to be targeted by a [SpotlightStep].
 */
fun Modifier.spotlightTarget(
    state: SpotlightState,
    key: String
): Modifier = this.onGloballyPositioned { coordinates ->
    if (coordinates.isAttached) {
        state.registerTarget(key, coordinates.boundsInRoot())
    }
}

/**
 * Standalone, reusable spotlight tutorial overlay.
 *
 * Dims the entire screen with a semi-transparent scrim except for a cutout matching the current
 * target element's actual position and size. Renders an informational guidance card positioned
 * above or below the cutout depending on available vertical room.
 *
 * @param state The [SpotlightState] governing current step and measured bounds.
 * @param modifier Optional modifier for the overlay container.
 * @param onDismiss Invoked when the tutorial is closed (via Skip or final Done).
 * @param scrimColor Dark dimming color for the backdrop.
 * @param highlightBorderColor Color of the border surrounding the spotlight cutout hole.
 */
@Composable
fun SpotlightOverlay(
    state: SpotlightState,
    modifier: Modifier = Modifier,
    usePopup: Boolean = false,
    onDismiss: () -> Unit = { state.dismiss() },
    scrimColor: Color = Color.Black.copy(alpha = 0.76f),
    highlightBorderColor: Color = Color.White.copy(alpha = 0.90f)
) {
    if (!state.isVisible || state.currentStep == null) return

    if (usePopup) {
        key(state.currentStepIndex) {
            Popup(
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                    clippingEnabled = false
                ),
                onDismissRequest = {
                    state.dismiss()
                    onDismiss()
                }
            ) {
                SpotlightOverlayContent(
                    state = state,
                    modifier = modifier,
                    onDismiss = onDismiss,
                    scrimColor = scrimColor,
                    highlightBorderColor = highlightBorderColor
                )
            }
        }
    } else {
        SpotlightOverlayContent(
            state = state,
            modifier = modifier,
            onDismiss = onDismiss,
            scrimColor = scrimColor,
            highlightBorderColor = highlightBorderColor
        )
    }
}

@Composable
private fun SpotlightOverlayContent(
    state: SpotlightState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    scrimColor: Color,
    highlightBorderColor: Color
) {
    BackHandler(enabled = state.isVisible) {
        state.dismiss()
        onDismiss()
    }

    AnimatedVisibility(
        visible = state.isVisible && state.currentStep != null,
        enter = fadeIn(animationSpec = tween(280)),
        exit = fadeOut(animationSpec = tween(240)),
        modifier = modifier
    ) {
        val density = LocalDensity.current
        var overlayOffsetInRoot by remember { mutableStateOf(Offset.Zero) }

        val currentStep = state.currentStep
        val rawBounds = state.currentTargetBounds

        // Safeguard: Check if target element can be found and measured on screen.
        // If not found or not measured within a grace period, automatically skip this step.
        LaunchedEffect(state.currentStepIndex, state.steps) {
            val step = state.currentStep ?: return@LaunchedEffect
            val isMeasured = withTimeoutOrNull(300) {
                snapshotFlow {
                    val bounds = state.targetBounds[step.key]
                    bounds != null && bounds.width > 0f && bounds.height > 0f
                }.firstOrNull { it }
            }
            if (isMeasured != true) {
                if (state.isLastStep) {
                    state.dismiss()
                    onDismiss()
                } else {
                    state.next()
                }
            }
        }

        // Calculate target rect relative to the overlay's coordinate space
        val targetRect = remember(rawBounds, overlayOffsetInRoot, currentStep, density) {
            if (rawBounds != null && currentStep != null && rawBounds.width > 0f && rawBounds.height > 0f) {
                val padPx = with(density) { currentStep.padding.toPx() }
                Rect(
                    left = rawBounds.left - overlayOffsetInRoot.x - padPx,
                    top = rawBounds.top - overlayOffsetInRoot.y - padPx,
                    right = rawBounds.right - overlayOffsetInRoot.x + padPx,
                    bottom = rawBounds.bottom - overlayOffsetInRoot.y + padPx
                )
            } else {
                null
            }
        }

        val hasValidTarget = targetRect != null && targetRect.width > 0f && targetRect.height > 0f
        val cornerRadius = currentStep?.cornerRadius ?: 14.dp

        // Smooth animations between consecutive targets
        val animLeft by animateFloatAsState(
            targetValue = targetRect?.left ?: 0f,
            animationSpec = tween(320, easing = FastOutSlowInEasing),
            label = "spotlight_left"
        )
        val animTop by animateFloatAsState(
            targetValue = targetRect?.top ?: 0f,
            animationSpec = tween(320, easing = FastOutSlowInEasing),
            label = "spotlight_top"
        )
        val animRight by animateFloatAsState(
            targetValue = targetRect?.right ?: 0f,
            animationSpec = tween(320, easing = FastOutSlowInEasing),
            label = "spotlight_right"
        )
        val animBottom by animateFloatAsState(
            targetValue = targetRect?.bottom ?: 0f,
            animationSpec = tween(320, easing = FastOutSlowInEasing),
            label = "spotlight_bottom"
        )
        val animCorner by animateDpAsState(
            targetValue = cornerRadius,
            animationSpec = tween(320, easing = FastOutSlowInEasing),
            label = "spotlight_corner"
        )

        val activeRect = if (hasValidTarget) {
            Rect(animLeft, animTop, animRight, animBottom)
        } else {
            null
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .testTag("spotlight_overlay")
                .onGloballyPositioned { coordinates ->
                    overlayOffsetInRoot = coordinates.positionInRoot()
                }
        ) {
            val overlayWidthPx = constraints.maxWidth.toFloat()
            val overlayHeightPx = constraints.maxHeight.toFloat()
            var cardHeightPx by remember { mutableFloatStateOf(0f) }

            // 1. Scrim canvas with transparent cutout hole
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        // Prevent underlying elements from receiving clicks during tutorial
                        detectTapGestures { }
                    }
            ) {
                if (hasValidTarget && activeRect != null && activeRect.width > 0 && activeRect.height > 0) {
                    val cornerPx = animCorner.toPx()
                    val cutoutPath = Path().apply {
                        fillType = PathFillType.EvenOdd
                        // Outer fullscreen rectangle
                        addRect(Rect(0f, 0f, size.width, size.height))
                        // Inner cutout rounded rectangle
                        addRoundRect(
                            RoundRect(
                                rect = activeRect,
                                cornerRadius = CornerRadius(cornerPx, cornerPx)
                            )
                        )
                    }
                    drawPath(cutoutPath, color = scrimColor)

                    // Accent border around highlighted cutout
                    drawRoundRect(
                        color = highlightBorderColor,
                        topLeft = Offset(activeRect.left, activeRect.top),
                        size = Size(activeRect.width, activeRect.height),
                        cornerRadius = CornerRadius(cornerPx, cornerPx),
                        style = Stroke(width = 2.dp.toPx())
                    )
                } else {
                    drawRect(color = scrimColor)
                }
            }

            // 2. Position card above or below depending on available space.
            // Using targetRect (the destination position) ensures the card's vertical offset
            // animates smoothly between steps instead of snapping or flipping at the midpoint.
            val spacingPx = with(density) { 14.dp.toPx() }
            val minMarginTopPx = with(density) { 36.dp.toPx() }
            val minMarginBottomPx = with(density) { 36.dp.toPx() }

            val targetCutoutTop = targetRect?.top ?: (overlayHeightPx / 2f)
            val targetCutoutBottom = targetRect?.bottom ?: (overlayHeightPx / 2f)

            val spaceAbove = targetCutoutTop
            val spaceBelow = overlayHeightPx - targetCutoutBottom
            val placeBelow = spaceBelow >= spaceAbove

            val effectiveCardHeight = if (cardHeightPx > 0f) cardHeightPx else with(density) { 180.dp.toPx() }

            val targetCardY = if (placeBelow) {
                targetCutoutBottom + spacingPx
            } else {
                targetCutoutTop - spacingPx - effectiveCardHeight
            }

            val maxAllowedY = (overlayHeightPx - effectiveCardHeight - minMarginBottomPx).coerceAtLeast(minMarginTopPx)
            val destinationCardY = targetCardY.coerceIn(minMarginTopPx, maxAllowedY)

            val animCardY by animateFloatAsState(
                targetValue = destinationCardY,
                animationSpec = tween(320, easing = FastOutSlowInEasing),
                label = "spotlight_card_y"
            )

            // Guidance Card - only displayed when a valid target is measured on screen
            if (hasValidTarget) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .align(Alignment.TopCenter)
                        .offset { IntOffset(x = 0, y = animCardY.roundToInt()) }
                        .onSizeChanged { size ->
                            if (size.height > 0) {
                                cardHeightPx = size.height.toFloat()
                            }
                        }
                ) {
                    SpotlightCard(
                        step = currentStep,
                        stepNumber = state.currentStepIndex + 1,
                        totalSteps = state.steps.size,
                        isLastStep = state.isLastStep,
                        onSkip = {
                            state.dismiss()
                            onDismiss()
                        },
                        onNext = {
                            if (state.isLastStep) {
                                state.dismiss()
                                onDismiss()
                            } else {
                                state.next()
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Information card displaying step counter, title, description, and navigation buttons.
 */
@Composable
fun SpotlightCard(
    step: SpotlightStep?,
    stepNumber: Int,
    totalSteps: Int,
    isLastStep: Boolean,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (step == null) return

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val cardBg = if (isDark) Color(0xFF1E262B) else Color.White
    val cardBorder = if (isDark) Color(0xFF334155).copy(alpha = 0.65f) else Color(0xFFE2E8F0)
    val onSurface = if (isDark) Color.White else Color(0xFF0F172A)
    val onSurfaceVariant = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val tealAccent = if (isDark) PrimaryTealDark else PrimaryTealLight

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp)
            .testTag("spotlight_tutorial_card"),
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        border = null,
        shadowElevation = if (isDark) 4.dp else 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // Header Row: Step counter pill and Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(tealAccent.copy(alpha = 0.14f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.spotlight_step_counter, stepNumber, totalSteps),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = tealAccent,
                            fontSize = 12.5.sp
                        ),
                        modifier = Modifier.testTag("spotlight_step_counter")
                    )
                }

                TextButton(
                    onClick = onSkip,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("spotlight_skip_button")
                ) {
                    Text(
                        text = stringResource(R.string.spotlight_skip),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Step Title
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = onSurface,
                    fontSize = 17.sp
                ),
                modifier = Modifier.testTag("spotlight_step_title")
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Step Description & Optional Rearrange Animation
            val shouldShowAnimation = step.showCustomizeAnimation ||
                    step.key == "header_customize_button"

            if (shouldShowAnimation) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = onSurfaceVariant,
                            fontSize = 13.5.sp,
                            lineHeight = 19.5.sp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("spotlight_step_description")
                    )

                    CustomizeRearrangeAnimation(
                        accentColor = tealAccent,
                        isDark = isDark,
                        modifier = Modifier.testTag("spotlight_customize_animation")
                    )
                }
            } else {
                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = onSurfaceVariant,
                        fontSize = 13.5.sp,
                        lineHeight = 19.5.sp
                    ),
                    modifier = Modifier.testTag("spotlight_step_description")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Actions: Next / Done button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onNext,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tealAccent,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp),
                    modifier = Modifier.testTag("spotlight_next_button")
                ) {
                    Text(
                        text = if (isLastStep) {
                            stringResource(R.string.spotlight_done)
                        } else {
                            stringResource(R.string.spotlight_next)
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Small, self-contained looping animation rendered inside the customize tutorial cards.
 * Visually hints at customizing and rearranging items by displaying miniature tile rows
 * that smoothly swap positions back and forth with subtle drag handle indicators.
 */
@Composable
fun CustomizeRearrangeAnimation(
    accentColor: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rearrange_transition")
    val cycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cycle"
    )

    // Two items swap positions smoothly
    val swapDistance = 15f
    val (offsetA, offsetB) = when {
        cycle < 0.18f -> Pair(0f, 0f)
        cycle < 0.48f -> {
            val t = (cycle - 0.18f) / 0.30f
            val smoothT = FastOutSlowInEasing.transform(t)
            Pair(smoothT * swapDistance, -smoothT * swapDistance)
        }
        cycle < 0.68f -> Pair(swapDistance, -swapDistance)
        cycle < 0.98f -> {
            val t = (cycle - 0.68f) / 0.30f
            val smoothT = FastOutSlowInEasing.transform(t)
            Pair((1f - smoothT) * swapDistance, -(1f - smoothT) * swapDistance)
        }
        else -> Pair(0f, 0f)
    }

    val containerBg = if (isDark) accentColor.copy(alpha = 0.12f) else accentColor.copy(alpha = 0.08f)
    val containerBorder = accentColor.copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .size(width = 54.dp, height = 54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(containerBg)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Item 1 (moves down by offsetA)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .offset(y = offsetA.dp)
                    .fillMaxWidth()
                    .height(11.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(accentColor.copy(alpha = if (offsetA > 0f) 0.95f else 0.75f))
                    .padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color.White.copy(alpha = 0.85f))
                )
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(7.dp)
                )
            }

            // Item 2 (moves up by offsetB)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .offset(y = offsetB.dp)
                    .fillMaxWidth()
                    .height(11.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1))
                    .padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .height(2.5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF475569))
                )
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = null,
                    tint = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF64748B),
                    modifier = Modifier.size(7.dp)
                )
            }

            // Item 3 (stationary base item)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(11.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isDark) Color(0xFF243038) else Color(0xFFE2E8F0))
                    .padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.5f))
                )
                Box(
                    modifier = Modifier
                        .weight(0.85f)
                        .height(2.5.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.4f) else Color(0xFF94A3B8))
                )
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = null,
                    tint = if (isDark) Color.White.copy(alpha = 0.4f) else Color(0xFF94A3B8),
                    modifier = Modifier.size(7.dp)
                )
            }
        }
    }
}
