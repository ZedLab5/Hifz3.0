package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel

/**
 * Automatically enables Do Not Disturb (DND) mode when entering reading/worship screens
 * (Quran, Azkar, Dua, Hifz, Tasbih, Khatma) if enabled in settings.
 * Restores normal notification policy upon leaving the screen.
 */
@Composable
fun DndReadingEffect(viewModel: MainViewModel) {
    val context = LocalContext.current
    val isDndEnabled by viewModel.isDndReadingEnabled.collectAsStateWithLifecycle()

    DisposableEffect(isDndEnabled) {
        if (isDndEnabled) {
            viewModel.enableDndIfAllowed(context)
        }
        onDispose {
            viewModel.disableDnd(context)
        }
    }
}
