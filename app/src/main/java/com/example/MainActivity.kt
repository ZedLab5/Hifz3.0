package com.example

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.NoorNotificationHelper
import com.example.data.prayer.AdhanPlayer
import com.example.ui.MainViewModel
import com.example.ui.NoorApp
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.NoorTheme
import com.example.ui.theme.ReadingThemes
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (AdhanPlayer.isPlaying()) {
                AdhanPlayer.stop(isDismiss = true)
                NoorNotificationHelper.cancelPrayerAlerts(this)
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onResume() {
        super.onResume()
        if (AdhanPlayer.isPlaying()) {
            AdhanPlayer.stop(isDismiss = true)
            NoorNotificationHelper.cancelPrayerAlerts(this)
        }
        com.example.widget.PrayerWidgetUpdater.updateAsync(applicationContext)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationNavigation(intent)
    }

    private fun handleNotificationNavigation(intent: Intent?) {
        val destName = intent?.getStringExtra("target_destination") ?: return
        try {
            val dest = com.example.ui.NoorDestination.valueOf(destName)
            viewModel.navigateTo(dest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleNotificationNavigation(intent)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
            val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
                    appLanguage == "العربية" ||
                    appLanguage.startsWith("ar", ignoreCase = true)

            val currentLocale = remember(isArabic) {
                if (isArabic) Locale("ar") else Locale("en")
            }
            val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

            val configuration = LocalConfiguration.current
            val context = LocalContext.current

            val localizedConfiguration = remember(configuration, currentLocale) {
                Configuration(configuration).apply {
                    setLocale(currentLocale)
                    setLayoutDirection(currentLocale)
                }
            }

            val localizedContext = remember(context, currentLocale) {
                val config = Configuration(context.resources.configuration).apply {
                    setLocale(currentLocale)
                    setLayoutDirection(currentLocale)
                }
                context.createConfigurationContext(config)
            }

            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalConfiguration provides localizedConfiguration,
                LocalLayoutDirection provides layoutDirection,
                LocalContext provides localizedContext,
                LocalActivityResultRegistryOwner provides this@MainActivity
            ) {
                NoorTheme(darkTheme = isDarkMode) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.background
                    ) {
                        NoorApp(viewModel = viewModel)
                    }
                }
            }
        }
    }
}


