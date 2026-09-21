package com.example.ui.components

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Keeps the device screen awake (overriding system sleep timeout)
 * while the Composable is active in the composition.
 * Uses the platform-managed View.keepScreenOn property, which
 * automatically handles attachment/detachment lifecycles cleanly.
 */
@Composable
fun KeepScreenOn() {
    AndroidView(
        factory = { context ->
            View(context).apply {
                keepScreenOn = true
            }
        },
        update = { view ->
            view.keepScreenOn = true
        }
    )
}
