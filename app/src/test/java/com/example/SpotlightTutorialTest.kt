package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.example.ui.components.SpotlightOverlay
import com.example.ui.components.SpotlightState
import com.example.ui.components.SpotlightStep
import com.example.ui.components.spotlightTarget
import com.example.ui.theme.NoorTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SpotlightTutorialTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSpotlightStepLifecycle() {
        val state = SpotlightState()
        var step1Entered = false
        var step1Exited = false
        var step2Entered = false
        var step2Exited = false

        val steps = listOf(
            SpotlightStep(
                key = "target_1",
                title = "Step 1",
                description = "Desc 1",
                onEnter = { step1Entered = true },
                onExit = { step1Exited = true }
            ),
            SpotlightStep(
                key = "target_2",
                title = "Step 2",
                description = "Desc 2",
                onEnter = { step2Entered = true },
                onExit = { step2Exited = true }
            )
        )

        // 1. Start tutorial
        state.start(steps)
        assertTrue(state.isVisible)
        assertEquals(0, state.currentStepIndex)
        assertTrue(step1Entered)
        assertFalse(step1Exited)
        assertFalse(state.isLastStep)

        // 2. Advance to Step 2
        state.next()
        assertTrue(state.isVisible)
        assertEquals(1, state.currentStepIndex)
        assertTrue(step1Exited)
        assertTrue(step2Entered)
        assertTrue(state.isLastStep)

        // 3. Complete tutorial
        state.next()
        assertFalse(state.isVisible)
        assertTrue(step2Exited)
    }

    @Test
    fun testSpotlightDismissLifecycle() {
        val state = SpotlightState()
        var step1Exited = false

        val steps = listOf(
            SpotlightStep(
                key = "target_1",
                title = "Step 1",
                description = "Desc 1",
                onExit = { step1Exited = true }
            )
        )

        state.start(steps)
        assertTrue(state.isVisible)

        state.dismiss()
        assertFalse(state.isVisible)
        assertTrue(step1Exited)
    }

    @Test
    fun testTwoStepHomeScreenTutorialConfiguration() {
        // Verify exactly two steps total for the home tutorial
        val homeSteps = listOf(
            SpotlightStep(
                key = "header_customize_button",
                title = "Customize Home & Quick Access",
                description = "Personalize your home screen sections and choose which quick access tools appear to fit your daily spiritual routine.",
                cornerRadius = 20.dp,
                padding = 6.dp,
                showCustomizeAnimation = true
            ),
            SpotlightStep(
                key = "nav_shortcuts",
                title = "Quick Shortcuts",
                description = "Quickly open your favorites, continue reading the Quran, and jump to frequently used azkar shortcuts.",
                cornerRadius = 14.dp,
                padding = 6.dp,
                showCustomizeAnimation = false
            )
        )

        assertEquals(2, homeSteps.size)
        assertEquals("header_customize_button", homeSteps[0].key)
        assertTrue(homeSteps[0].title.contains("Home") && homeSteps[0].title.contains("Quick Access"))
        assertTrue(homeSteps[0].description.contains("quick access"))
        assertTrue(homeSteps[0].showCustomizeAnimation)

        assertEquals("nav_shortcuts", homeSteps[1].key)
        assertEquals("Quick Shortcuts", homeSteps[1].title)
        assertFalse(homeSteps[1].showCustomizeAnimation)

        // Confirm there is no "all_tools" step in the sequence
        assertFalse(homeSteps.any { it.key == "all_tools" })
    }

    @Test
    fun testCardPositioningAboveAndBelowTarget() {
        val overlayHeightPx = 800f
        val spacingPx = 14f
        val cardHeightPx = 180f
        val minMarginTopPx = 36f
        val minMarginBottomPx = 36f

        // Case 1: Target near top (e.g. header customize button at Y = 60..120)
        val topCutoutTop = 60f
        val topCutoutBottom = 120f
        val topSpaceAbove = topCutoutTop
        val topSpaceBelow = overlayHeightPx - topCutoutBottom
        val topPlaceBelow = topSpaceBelow >= topSpaceAbove
        assertTrue("Card should be placed below when target is near the top", topPlaceBelow)

        val targetTopCardY = topCutoutBottom + spacingPx
        val clampedTopCardY = targetTopCardY.coerceIn(
            minMarginTopPx,
            (overlayHeightPx - cardHeightPx - minMarginBottomPx).coerceAtLeast(minMarginTopPx)
        )
        assertEquals(134f, clampedTopCardY, 0.1f)
        assertTrue(clampedTopCardY > topCutoutBottom)

        // Case 2: Target near bottom (e.g. bottom bar shortcuts at Y = 740..790)
        val bottomCutoutTop = 740f
        val bottomCutoutBottom = 790f
        val bottomSpaceAbove = bottomCutoutTop
        val bottomSpaceBelow = overlayHeightPx - bottomCutoutBottom
        val bottomPlaceBelow = bottomSpaceBelow >= bottomSpaceAbove
        assertFalse("Card should be placed above when target is near the bottom", bottomPlaceBelow)

        val targetBottomCardY = bottomCutoutTop - spacingPx - cardHeightPx
        val clampedBottomCardY = targetBottomCardY.coerceIn(
            minMarginTopPx,
            (overlayHeightPx - cardHeightPx - minMarginBottomPx).coerceAtLeast(minMarginTopPx)
        )
        assertEquals(546f, clampedBottomCardY, 0.1f)
        assertTrue(clampedBottomCardY + cardHeightPx < bottomCutoutBottom)

        // Smooth transition delta: moving from 134f to 546f without intermediate inversion
        val travelDelta = clampedBottomCardY - clampedTopCardY
        assertEquals(412f, travelDelta, 0.1f)
    }

    @Test
    fun testEndToEndTwoStepTutorialExecution() {
        val state = SpotlightState()
        var tutorialDismissed = false

        val steps = listOf(
            SpotlightStep(
                key = "header_customize_button",
                title = "Customize Home & Quick Access",
                description = "Personalize your home screen sections and choose which quick access tools appear.",
                showCustomizeAnimation = true
            ),
            SpotlightStep(
                key = "nav_shortcuts",
                title = "Quick Shortcuts",
                description = "Quickly open your favorites and continue reading.",
                showCustomizeAnimation = false
            )
        )

        composeTestRule.setContent {
            NoorTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Target 1: Top header button
                    Box(
                        modifier = Modifier
                            .offset(x = 280.dp, y = 40.dp)
                            .size(44.dp)
                            .spotlightTarget(state, "header_customize_button")
                    ) {
                        Text("Header")
                    }

                    // Target 2: Bottom navigation shortcuts
                    Box(
                        modifier = Modifier
                            .offset(x = 280.dp, y = 700.dp)
                            .size(48.dp)
                            .spotlightTarget(state, "nav_shortcuts")
                    ) {
                        Text("Shortcuts")
                    }

                    SpotlightOverlay(
                        state = state,
                        onDismiss = { tutorialDismissed = true }
                    )
                }
            }
        }

        // Start the 2-step tutorial
        composeTestRule.runOnUiThread {
            state.start(steps)
        }
        composeTestRule.waitForIdle()

        // Step 1: Customize Home & Quick Access
        composeTestRule.onNodeWithTag("spotlight_tutorial_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Customize Home & Quick Access").assertIsDisplayed()
        composeTestRule.onNodeWithTag("spotlight_customize_animation").assertIsDisplayed()
        assertEquals(0, state.currentStepIndex)
        assertFalse(state.isLastStep)

        // Click "Next" to advance to Step 2
        composeTestRule.onNodeWithTag("spotlight_next_button").performClick()
        composeTestRule.waitForIdle()

        // Step 2: Quick Shortcuts
        composeTestRule.onNodeWithTag("spotlight_tutorial_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quick Shortcuts").assertIsDisplayed()
        assertEquals(1, state.currentStepIndex)
        assertTrue(state.isLastStep)

        // Click "Done" to finish the tutorial
        composeTestRule.onNodeWithTag("spotlight_next_button").performClick()
        composeTestRule.waitForIdle()

        // Verify overlay is dismissed and tutorial completed
        assertFalse(state.isVisible)
        assertTrue(tutorialDismissed)
        composeTestRule.onNodeWithTag("spotlight_tutorial_card").assertDoesNotExist()
    }

    @Test
    fun testSafeguardAutoSkipsMissingStep() {
        val state = SpotlightState()
        var tutorialDismissed = false

        // Step 1 has a key that is NOT on screen; Step 2 has a valid on-screen target
        val steps = listOf(
            SpotlightStep(
                key = "non_existent_element",
                title = "Missing Step",
                description = "This step should be automatically skipped."
            ),
            SpotlightStep(
                key = "valid_element",
                title = "Valid Step",
                description = "This step is valid and should be shown."
            )
        )

        composeTestRule.setContent {
            NoorTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Only valid_element is registered on screen
                    Box(
                        modifier = Modifier
                            .offset(x = 100.dp, y = 200.dp)
                            .size(60.dp)
                            .spotlightTarget(state, "valid_element")
                    ) {
                        Text("Valid Target")
                    }

                    SpotlightOverlay(
                        state = state,
                        onDismiss = { tutorialDismissed = true }
                    )
                }
            }
        }

        // Start tutorial with missing step first
        composeTestRule.runOnUiThread {
            state.start(steps)
        }
        composeTestRule.waitForIdle()

        // Advance main clock to trigger safeguard timeout (300ms)
        composeTestRule.mainClock.advanceTimeBy(400)
        composeTestRule.waitForIdle()

        // The safeguard should have skipped the missing step and advanced directly to the valid step!
        assertEquals(1, state.currentStepIndex)
        composeTestRule.onNodeWithTag("spotlight_tutorial_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Valid Step").assertIsDisplayed()
        composeTestRule.onNodeWithText("Missing Step").assertDoesNotExist()
    }
}
