package com.example.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import com.example.ui.onboarding.OnboardingScreen
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.core.view.WindowCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.ui.theme.PrimaryTealLight
import com.example.ui.duas.AzkarReaderScreen
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ui.components.KeepScreenOn
import com.example.ui.components.LocalHomeSpotlightState
import com.example.ui.components.ShortcutsSheet
import com.example.ui.components.KhatmaMilestoneCelebrationDialog
import com.example.ui.components.SpotlightOverlay
import com.example.ui.components.SpotlightState
import com.example.ui.components.SpotlightStep
import com.example.ui.components.spotlightTarget
import com.example.ui.duas.DuasLibraryScreen
import com.example.ui.quran.HadithLibraryScreen
import com.example.ui.favorites.FavoritesScreen
import com.example.ui.home.HomeScreen
import com.example.ui.khatma.QuranKhatmaScreen
import com.example.ui.profile.UserProfileScreen
import com.example.ui.qibla.QiblaScreen
import com.example.ui.quran.QuranAudioPlayerScreen
import com.example.ui.quran.QuranMemorizationScreen
import com.example.ui.quran.QuranMemorizationSetupScreen
import com.example.ui.quran.QuranReaderScreen
import com.example.ui.quran.QuranRecitersListScreen
import com.example.ui.quran.QuranSurahSelectionScreen
import com.example.ui.routines.HabitTrackerScreen
import com.example.ui.salat.SalatScreen
import com.example.ui.settings.AppSettingsScreen
import com.example.ui.streaks.StreaksScreen
import com.example.ui.tasbih.TasbihScreen
import com.example.ui.tools.AllToolsScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.BorderDividerDark
import com.example.ui.theme.BorderDividerLight
import com.example.ui.theme.BorderTealGray
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldAccentGradient
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealGradient
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.ReadingThemes
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextSecondaryLight

import androidx.compose.ui.res.stringResource
import com.example.R

data class BottomNavItem(
    val destination: NoorDestination?,
    val labelRes: Int,
    val icon: ImageVector,
    val onClickOverride: (() -> Unit)? = null
)

@Composable
fun NoorApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentDest by viewModel.currentDestination.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val currentPlayingSurah by viewModel.currentPlayingSurah.collectAsStateWithLifecycle()
    val currentPlayingVerse by viewModel.currentPlayingVerse.collectAsStateWithLifecycle()
    val isAyahAudioMode by viewModel.isAyahAudioMode.collectAsStateWithLifecycle()
    val selectedReciter by viewModel.selectedReciter.collectAsStateWithLifecycle()
    val toastMsg by viewModel.toastMessage.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val appThemeMode by viewModel.appThemeMode.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsModalOpen.collectAsStateWithLifecycle()
    val showTimerExitConfirmation by viewModel.showQuranTimerExitConfirmation.collectAsStateWithLifecycle()

    val navSurface = when (appThemeMode) {
        AppThemeMode.LIGHT -> SurfaceWhite
        AppThemeMode.WARM -> SurfaceWhite
        AppThemeMode.DARK -> SurfaceDark
    }
    val navBorder = when (appThemeMode) {
        AppThemeMode.LIGHT -> BorderDividerLight
        AppThemeMode.WARM -> Color(0xFFEDE0C8)
        AppThemeMode.DARK -> BorderDividerDark
    }
    val navSelectedTint = when (appThemeMode) {
        AppThemeMode.LIGHT -> Color(0xFF1BA486) // Green for Light
        AppThemeMode.WARM -> Color(0xFFD9A44E)  // Gold for Warm (Primary color)
        AppThemeMode.DARK -> Color(0xFF2FBF96)  // Green for Dark (Like the icons)
    }
    val navUnselectedTint = when (appThemeMode) {
        AppThemeMode.LIGHT -> TextSecondaryLight
        AppThemeMode.WARM -> Color(0xFF7A6650)
        AppThemeMode.DARK -> TextSecondaryDark
    }
    val navSelectedText = navSelectedTint
    val navUnselectedText = navUnselectedTint

    val view = LocalView.current
    if (!view.isInEditMode) {
        val isLightStatusBar = !isDarkMode
        val navBarColor = navSurface
        val isLightNavBar = !isDarkMode

        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    window.isStatusBarContrastEnforced = false
                    window.isNavigationBarContrastEnforced = false
                }
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = navBarColor.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = isLightStatusBar
                controller.isAppearanceLightNavigationBars = isLightNavBar
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMsg) {
        toastMsg?.let { snackbarHostState.showSnackbar(it) }
    }

    BackHandler(enabled = currentDest != NoorDestination.HOME) {
        viewModel.navigateBack()
    }

    val isShortcutsSheetOpen by viewModel.isShortcutsSheetOpen.collectAsStateWithLifecycle()
    val khatmaMilestoneModal by viewModel.khatmaMilestoneModal.collectAsStateWithLifecycle()
    val isHomeTutorialRequested by viewModel.isHomeTutorialRequested.collectAsStateWithLifecycle()
    val hasSeenHomeTutorial by viewModel.hasSeenHomeTutorial.collectAsStateWithLifecycle()
    val homeSpotlightState = remember { SpotlightState() }
    val context = LocalContext.current
    val hasSeenOnboarding by viewModel.hasSeenOnboarding.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateNotificationPermissionStatus(isGranted)
        if (!isGranted) {
            viewModel.showToast(if (isArabic) "يرجى تمكين الإشعارات من إعدادات الهاتف لتلقي تنبيهات الصلاة." else "Please enable notifications in system settings to receive prayer alerts.")
        } else {
            viewModel.showToast(if (isArabic) "تم تفعيل تنبيهات الصلاة بنجاح" else "Prayer notifications enabled successfully")
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            coroutineScope.launch {
                val resolvedZone = com.example.data.prayer.GpsLocationResolver.resolveCurrentZone(context)
                if (resolvedZone != null) {
                    viewModel.selectPrayerZone(resolvedZone)
                } else {
                    viewModel.markLocationConfigured()
                }
            }
        } else {
            viewModel.showToast(if (isArabic) "يرجى تمكين تحديد الموقع لتلقي مواقيت صلاة دقيقة تلقائياً." else "Please enable location to receive accurate prayer times automatically.")
        }
    }

    LaunchedEffect(isHomeTutorialRequested, hasSeenHomeTutorial, currentDest) {
        // Spotlight tutorial temporarily hidden until explicitly reactivated
    }

    val bottomNavItems = listOf(
        BottomNavItem(NoorDestination.HOME, R.string.nav_home, Icons.Default.Home),
        BottomNavItem(NoorDestination.QURAN_SURAH_LIST, R.string.nav_quran, Icons.AutoMirrored.Filled.MenuBook),
        BottomNavItem(NoorDestination.SALAT, R.string.nav_salat, Icons.Default.AccessTime),
        BottomNavItem(NoorDestination.TASBIH, R.string.nav_tasbih, Icons.Default.TouchApp),
        BottomNavItem(
            destination = null,
            labelRes = R.string.nav_shortcuts,
            icon = Icons.Default.Widgets,
            onClickOverride = { viewModel.openShortcutsSheet() }
        )
    )

    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        com.example.ui.theme.LocalAppThemeMode provides appThemeMode,
        LocalHomeSpotlightState provides homeSpotlightState
    ) {
        if (!hasSeenOnboarding) {
            OnboardingScreen(
                zones = viewModel.prayerZones,
                requestNotifications = {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                },
                requestLocation = {
                    locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                },
                onComplete = { focusSelections, language, theme, reqNotif, reqLoc ->
                    viewModel.completeOnboarding(focusSelections)
                    viewModel.navigateTo(NoorDestination.HOME)
                },
                onSkip = {
                    viewModel.skipOnboarding()
                    viewModel.navigateTo(NoorDestination.HOME)
                }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            val isBottomBarVisible = currentDest != NoorDestination.QURAN_AUDIO_STREAM &&
                    currentDest != NoorDestination.QURAN_READER &&
                    currentDest != NoorDestination.QURAN_MEMORIZATION &&
                    currentDest != NoorDestination.QURAN_MEMORIZATION_SETUP &&
                    currentDest != NoorDestination.QURAN_TAJWEED_GUIDE

            if (isBottomBarVisible) {
                // Flush Full-Width Tactile Navigation Bar with Settings Quick Access
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(navSurface)
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        bottomNavItems.forEach { item ->
                            val isReadingListSelected = currentDest == NoorDestination.QURAN_SURAH_LIST || currentDest == NoorDestination.QURAN_READER || currentDest == NoorDestination.QURAN_KHATMA
                            val isSelected = when (item.destination) {
                                NoorDestination.QURAN_SURAH_LIST -> isReadingListSelected
                                else -> currentDest == item.destination
                            }
                            val itemLabel = stringResource(item.labelRes)

                            val isShortcuts = item.destination == null
                            val navTag = when (item.destination) {
                                NoorDestination.HOME -> "nav_item_home"
                                NoorDestination.QURAN_SURAH_LIST -> "nav_item_quran"
                                NoorDestination.SALAT -> "nav_item_salat"
                                NoorDestination.TASBIH -> "nav_item_tasbih"
                                else -> "nav_item_shortcuts"
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .testTag(navTag)
                                    .then(
                                        if (isShortcuts) {
                                            Modifier.spotlightTarget(homeSpotlightState, "nav_shortcuts")
                                        } else Modifier
                                    )
                                    .clickable {
                                        if (item.onClickOverride != null) {
                                            item.onClickOverride.invoke()
                                        } else if (item.destination != null) {
                                            viewModel.navigateTo(item.destination)
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = itemLabel,
                                    tint = if (isSelected) navSelectedTint else navUnselectedTint,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = itemLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) navSelectedText else navUnselectedText,
                                        fontSize = 10.5.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        val quranReadingDestinations = remember {
            setOf(
                NoorDestination.QURAN_READER,
                NoorDestination.QURAN_MEMORIZATION,
                NoorDestination.QURAN_AUDIO_STREAM,
                NoorDestination.QURAN_KHATMA
            )
        }

        if (currentDest in quranReadingDestinations) {
            KeepScreenOn()
        }

        val saveableStateHolder = rememberSaveableStateHolder()

        val isSelfContainedBottomBar = currentDest == NoorDestination.QURAN_READER ||
                currentDest == NoorDestination.QURAN_MEMORIZATION ||
                currentDest == NoorDestination.QURAN_MEMORIZATION_SETUP ||
                currentDest == NoorDestination.QURAN_AUDIO_STREAM

        val effectiveBottomPadding = if (isSelfContainedBottomBar) 0.dp else paddingValues.calculateBottomPadding()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = effectiveBottomPadding)
        ) {
            AnimatedContent(
                targetState = currentDest,
                transitionSpec = {
                    val isInstantTransition = initialState == NoorDestination.QURAN_READER ||
                            initialState == NoorDestination.QURAN_MEMORIZATION ||
                            targetState == NoorDestination.QURAN_READER ||
                            targetState == NoorDestination.QURAN_MEMORIZATION
                    if (isInstantTransition) {
                        EnterTransition.None togetherWith ExitTransition.None
                    } else {
                        fadeIn() togetherWith fadeOut()
                    }
                },
                label = "navAnimation"
            ) { dest ->
                saveableStateHolder.SaveableStateProvider(key = dest) {
                    when (dest) {
                        NoorDestination.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onRequestLocation = {
                                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            },
                            onRequestNotifications = {
                                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }
                        )
                        NoorDestination.STREAKS -> StreaksScreen(viewModel = viewModel)
                        NoorDestination.QURAN_SURAH_LIST -> QuranSurahSelectionScreen(viewModel = viewModel)
                        NoorDestination.QURAN_READER -> QuranReaderScreen(viewModel = viewModel)
                        NoorDestination.QURAN_MEMORIZATION_SETUP -> QuranMemorizationSetupScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                        NoorDestination.QURAN_MEMORIZATION -> QuranMemorizationScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                        NoorDestination.QURAN_AUDIO_STREAM -> QuranAudioPlayerScreen(viewModel = viewModel)
                        NoorDestination.QURAN_RECITERS -> QuranRecitersListScreen(viewModel = viewModel)
                        NoorDestination.QURAN_KHATMA -> QuranKhatmaScreen(
                            viewModel = viewModel,
                            onNavigateBack = { viewModel.navigateBack() }
                        )
                        NoorDestination.TASBIH -> TasbihScreen(viewModel = viewModel)
                        NoorDestination.QIBLA -> QiblaScreen(viewModel = viewModel)
                        NoorDestination.SALAT -> SalatScreen(viewModel = viewModel)
                        NoorDestination.HABIT_TRACKER -> HabitTrackerScreen(viewModel = viewModel)
                        NoorDestination.DUAS_LIBRARY -> DuasLibraryScreen(viewModel = viewModel)
                        NoorDestination.AZKAR_READER -> AzkarReaderScreen(viewModel = viewModel)
                        NoorDestination.HADITH_LIBRARY -> HadithLibraryScreen(viewModel = viewModel)
                        NoorDestination.FAVORITES -> FavoritesScreen(viewModel = viewModel)
                        NoorDestination.PROFILE -> UserProfileScreen(
                            viewModel = viewModel,
                            onNavigateBack = { viewModel.navigateBack() }
                        )
                        NoorDestination.ALL_TOOLS -> AllToolsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { viewModel.navigateBack() }
                        )
                        NoorDestination.SETTINGS -> AppSettingsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { viewModel.navigateBack() }
                        )
                        NoorDestination.NOTIFICATION_TROUBLESHOOTING -> com.example.ui.settings.NotificationTroubleshootingScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                        NoorDestination.NOTIFICATION_CENTER -> com.example.ui.notifications.NotificationCenterScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                        NoorDestination.QURAN_TAJWEED_GUIDE -> com.example.ui.quran.TajweedGuideScreen(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Floating Audio Mini-Player Widget (Only shown when MP3 player is active, not during Quran reader ayah recitation)
            AnimatedVisibility(
                visible = isAudioPlaying && !isAyahAudioMode && currentDest != NoorDestination.QURAN_AUDIO_STREAM,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { viewModel.navigateTo(NoorDestination.QURAN_AUDIO_STREAM) }
                        .shadow(14.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFF091410)),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF0C1D17),
                    border = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF16382C)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Headphones,
                                    contentDescription = "Now Playing",
                                    tint = PrimaryTealLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                val currentSurahDisplayName = if (isArabic) currentPlayingSurah.nameArabic else currentPlayingSurah.nameEnglish
                                Text(
                                    text = stringResource(R.string.player_surah_title, currentSurahDisplayName),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = stringResource(R.string.player_audio_sub, selectedReciter.name),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = PrimaryTealLight,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Quick Play / Pause Toggle
                            Surface(
                                shape = CircleShape,
                                color = PrimaryTealLight.copy(alpha = 0.15f),
                                border = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        viewModel.toggleAudioPlayback()
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isAudioPlaying) "Pause" else "Play",
                                        tint = PrimaryTealLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Close / Stop Audio Button
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        viewModel.pauseAudio()
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = PrimaryTealLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isShortcutsSheetOpen) {
        ShortcutsSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.closeShortcutsSheet() }
        )
    }

    khatmaMilestoneModal?.let { milestone ->
        KhatmaMilestoneCelebrationDialog(
            milestone = milestone,
            onDismiss = { viewModel.dismissKhatmaMilestoneModal() },
            onReturnHome = {
                viewModel.dismissKhatmaMilestoneModal()
                viewModel.navigateTo(NoorDestination.HOME)
            },
            onContinueReading = {
                viewModel.dismissKhatmaMilestoneModal()
            }
        )
    }

    SpotlightOverlay(
        state = homeSpotlightState,
        onDismiss = {
            homeSpotlightState.dismiss()
            viewModel.isTutorialActive.value = false
        }
    )

    // First-run reliability check for battery optimization (Doze Mode)
    val appContext = androidx.compose.ui.platform.LocalContext.current
    var showBatteryPrompt by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(
            !com.example.data.prayer.AlarmReliabilityHelper.isIgnoringBatteryOptimizations(appContext) &&
                    !com.example.data.prayer.AlarmReliabilityHelper.hasPromptedBatteryOptimization(appContext)
        )
    }
    if (showBatteryPrompt) {
        com.example.ui.components.BatteryOptimizationPromptDialog(
            onDismiss = { showBatteryPrompt = false }
        )
    }

    if (showTimerExitConfirmation) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { viewModel.dismissTimerExitConfirmation() }
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFFBF0DC), CircleShape), // Warm Amber/Gold tint
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Warning",
                            tint = Color(0xFFC68A00), // Gold
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        text = "Session Incomplete",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F)
                        )
                    )

                    Text(
                        text = "You haven't finished your Quran reading session yet. Would you like to keep reading to complete your goal, or finish later?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF5F5E5A),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Finish Later
                        androidx.compose.material3.OutlinedButton(
                            onClick = { viewModel.confirmTimerExit() },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF2A4365) // Soft Navy
                            ),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(
                                text = "Finish Later",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                        }

                        // Continue Reading
                        androidx.compose.material3.Button(
                            onClick = { viewModel.dismissTimerExitConfirmation() },
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1BA486), // Primary Teal
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Continue",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
            }
        }
    }
}
