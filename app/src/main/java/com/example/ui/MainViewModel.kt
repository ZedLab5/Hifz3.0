package com.example.ui

import android.app.Application
import android.content.Context
import android.util.Log
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DailyHabitEntity
import com.example.data.local.FastLogEntity
import com.example.data.local.FavoriteItemEntity
import com.example.data.local.KhatmaHistoryEntity
import com.example.data.local.KhatmaPlanEntity
import com.example.data.local.NoorNotificationHelper
import com.example.data.local.QadaRecordEntity
import com.example.data.local.QuranNoteEntity
import com.example.data.local.ReadingProgressEntity
import com.example.data.local.StreakDailyLogEntity
import com.example.data.local.StreakSummaryEntity
import com.example.data.quran.AyahSplitRepository
import com.example.data.model.CalculationAuthority

import com.example.data.model.DailyMoodWisdom
import com.example.data.model.DuaItem
import com.example.data.model.HomeWidgetType
import com.example.data.model.KhatmaMilestoneData
import com.example.data.model.PrayerTime
import com.example.data.model.PrayerZone
import com.example.data.model.QuranArabicFont
import com.example.data.model.QuickAccessTool
import com.example.data.model.Reciter
import com.example.data.model.StreakActivityType
import com.example.data.model.Surah
import com.example.ui.quran.TajweedButtonPosition

import android.net.Uri
import com.example.data.model.UnifiedStreakData
import com.example.data.model.Verse
import com.example.data.prayer.AdhanAudioRepository
import com.example.data.prayer.AdhanPlayer
import com.example.data.prayer.AdhanSound
import com.example.data.prayer.PrayerAlarmScheduler
import com.example.data.prayer.PrayerCalculator
import com.example.data.quran.DuaData
import com.example.data.notifications.SpiritualReminderRepository
import com.example.data.notifications.SpiritualReminderState
import com.example.data.notifications.SpiritualAlarmScheduler
import com.example.data.quran.HifzAudioController
import com.example.data.quran.HifzAudioState
import com.example.data.quran.HifzWordTimingRepository
import com.example.data.quran.KhatmaEngine
import com.example.data.quran.KhatmaFullDashboardState
import com.example.data.quran.KhatmaPaceStatus
import com.example.data.quran.QuranAudioCacheManager
import com.example.data.quran.QuranData
import com.example.data.quran.StreakEngine
import com.example.data.repository.NoorRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

enum class SalatTab {
    TIMES,
    STREAKS,
    QADA
}

data class QuranScrollRequest(val surahNumber: Int, val targetAyah: Int, val id: Long = System.nanoTime())

enum class NoorDestination {
    HOME,
    SALAT,
    STREAKS,
    QURAN_SURAH_LIST,
    QURAN_READER,
    QURAN_AUDIO_STREAM,
    QURAN_RECITERS,
    QURAN_KHATMA,
    QURAN_MEMORIZATION_SETUP,
    QURAN_MEMORIZATION,
    TASBIH,
    QIBLA,
    HABIT_TRACKER,
    DUAS_LIBRARY,
    AZKAR_READER,
    HADITH_LIBRARY,
    FAVORITES,
    PROFILE,
    ALL_TOOLS,
    SETTINGS,
    NOTIFICATION_TROUBLESHOOTING,
    NOTIFICATION_CENTER,
    QURAN_TAJWEED_GUIDE
}

enum class HifzMaskingStyle(val title: String, val subtitle: String) {
    BLUR_MUTED("Muted Silhouette", "Softly muted verses showing subtle contour without full readability")
}

data class HifzDrillProgressInfo(
    val phase: String = "", // "GROUP", "LINKING", "CHUNK_REVIEW", "RECAP", "WARM_UP", "WORD_CHUNK"
    val label: String = "",
    val currentRep: Int = 1,
    val totalReps: Int = 1,
    val isLinking: Boolean = false,
    val isChunkReview: Boolean = false,
    val isRecap: Boolean = false,
    val isWarmUp: Boolean = false
)

data class ConfidencePromptData(
    val surahNumber: Int,
    val surahName: String,
    val startAyah: Int,
    val endAyah: Int,
    val mode: String,
    val onChoice: (String) -> Unit = {}
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    internal val repository = NoorRepository(
        dao = db.noorDao(),
        quranBookmarkDao = db.quranBookmarkDao(),
        tasbihDao = db.tasbihDao(),
        prayerDao = db.prayerDao()
    )
    private val sharedPrefs = application.getSharedPreferences("noor_app_preferences", Context.MODE_PRIVATE)

    // Navigation Destination
    private val _currentDestination = MutableStateFlow(NoorDestination.HOME)
    val currentDestination: StateFlow<NoorDestination> = _currentDestination.asStateFlow()

    // Navigation Stack for smooth back navigation
    private val navigationStack = mutableListOf<NoorDestination>()

    fun navigateTo(dest: NoorDestination) {
        if (_currentDestination.value == NoorDestination.QURAN_READER && dest != NoorDestination.QURAN_READER) {
            if (isAyahAudioMode.value) {
                stopAudio()
            }
        }
        if (_currentDestination.value != dest) {
            navigationStack.add(_currentDestination.value)
            _currentDestination.value = dest
        }
    }

    fun navigateBack(): Boolean {
        if (_currentDestination.value == NoorDestination.QURAN_READER) {
            if (isAyahAudioMode.value) {
                stopAudio()
            }
        }
        return if (navigationStack.isNotEmpty()) {
            _currentDestination.value = navigationStack.removeAt(navigationStack.lastIndex)
            true
        } else {
            if (_currentDestination.value != NoorDestination.HOME) {
                _currentDestination.value = NoorDestination.HOME
                true
            } else false
        }
    }

    // User Profile & Location
    val isUserLoggedIn = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getBoolean("is_logged_in", false)
        } catch (e: Exception) {
            false
        }
    )

    val userName = MutableStateFlow(
        try {
            val prefs = application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            val loggedIn = prefs.getBoolean("is_logged_in", false)
            if (loggedIn) {
                prefs.getString("user_name", "Zaid Ibrahim") ?: "Zaid Ibrahim"
            } else {
                "Guest Mode"
            }
        } catch (e: Exception) {
            "Guest Mode"
        }
    )

    val userEmail = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getString("user_email", "") ?: ""
        } catch (e: Exception) {
            ""
        }
    )

    val userBio = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getString("user_bio", "Seeking spiritual peace through the Noble Qur'an and remembrance")
                ?: "Seeking spiritual peace through the Noble Qur'an and remembrance"
        } catch (e: Exception) {
            "Seeking spiritual peace through the Noble Qur'an and remembrance"
        }
    )

    val userMemberSince = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getString("user_member_since", "Ramadan 1445 AH") ?: "Ramadan 1445 AH"
        } catch (e: Exception) {
            "Ramadan 1445 AH"
        }
    )

    val locationName = MutableStateFlow(
        if (!sharedPrefs.getBoolean("is_location_configured", false)) {
            "Location is Off"
        } else {
            try {
                application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                    .getString("location_name", "Location is Off") ?: "Location is Off"
            } catch (e: Exception) {
                "Location is Off"
            }
        }
    )

    val isLocationConfigured = MutableStateFlow(
        sharedPrefs.getBoolean("is_location_configured", false)
    )

    val hasNotificationPermission = MutableStateFlow(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                application,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            androidx.core.app.NotificationManagerCompat.from(application).areNotificationsEnabled()
        }
    )

    fun updateNotificationPermissionStatus(overrideGranted: Boolean? = null) {
        val app = getApplication<Application>()
        val granted = overrideGranted ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                app,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            androidx.core.app.NotificationManagerCompat.from(app).areNotificationsEnabled()
        }
        hasNotificationPermission.value = granted
    }

    fun markLocationConfigured() {
        isLocationConfigured.value = true
        sharedPrefs.edit().putBoolean("is_location_configured", true).apply()
        val zone = selectedPrayerZone.value
        locationName.value = "${zone.name}, ${zone.country}"
        refreshPrayerTimes()
    }

    fun openSalatSettings() {
        isSalatSettingsOpen.value = true
    }

    val isCloudSyncEnabled = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getBoolean("cloud_sync_enabled", true)
        } catch (e: Exception) {
            true
        }
    )

    val isStreakTrackingEnabled = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getBoolean("streak_tracking_enabled", true)
        } catch (e: Exception) {
            true
        }
    )

    val dailyQuranGoal = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getInt("daily_quran_goal", 4)
        } catch (e: Exception) {
            4
        }
    )

    val dailyDhikrGoal = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .getInt("daily_dhikr_goal", 100)
        } catch (e: Exception) {
            100
        }
    )

    fun isArabicLanguage(): Boolean {
        val lang = appLanguage.value
        return lang.equals("Arabic", ignoreCase = true) || lang == "العربية" || lang.startsWith("ar", ignoreCase = true)
    }

    fun connectUser(name: String, email: String, bio: String = "") {
        val finalName = if (name.isBlank()) "Zaid Ibrahim" else name.trim()
        val finalEmail = if (email.isBlank()) "zaid.ibrahim@example.com" else email.trim()
        val finalBio = if (bio.isBlank()) "Seeking spiritual peace through the Noble Qur'an and remembrance" else bio.trim()

        userName.value = finalName
        userEmail.value = finalEmail
        userBio.value = finalBio
        isUserLoggedIn.value = true

        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean("is_logged_in", true)
                .putString("user_name", finalName)
                .putString("user_email", finalEmail)
                .putString("user_bio", finalBio)
                .apply()
        } catch (e: Exception) {}

        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم تسجيل الدخول بنجاح! أهلاً بك يا $finalName" else "Welcome, $finalName! Account connected.")
    }

    fun disconnectUser() {
        isUserLoggedIn.value = false
        userName.value = "Guest Mode"
        userEmail.value = ""

        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean("is_logged_in", false)
                .putString("user_name", "Guest Mode")
                .putString("user_email", "")
                .apply()
        } catch (e: Exception) {}

        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم تسجيل الخروج. أنت الآن في وضع الضيف." else "Disconnected. You are now in Guest Mode.")
    }

    fun updateUserProfile(name: String, email: String, bio: String, location: String) {
        if (name.isNotBlank()) userName.value = name.trim()
        if (email.isNotBlank()) userEmail.value = email.trim()
        if (bio.isNotBlank()) userBio.value = bio.trim()
        if (location.isNotBlank()) locationName.value = location.trim()

        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("user_name", userName.value)
                .putString("user_email", userEmail.value)
                .putString("user_bio", userBio.value)
                .putString("location_name", locationName.value)
                .apply()
        } catch (e: Exception) {}

        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم تحديث الملف الشخصي بنجاح" else "Profile updated successfully")
    }

    fun updatePassword(newPass: String) {
        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم تحديث كلمة المرور بنجاح" else "Password updated successfully")
    }

    fun deleteAccount() {
        viewModelScope.launch {
            userName.value = ""
            userEmail.value = ""
            userBio.value = ""
            isUserLoggedIn.value = false
            sharedPrefs.edit()
                .putBoolean("user_logged_in", false)
                .remove("user_name")
                .remove("user_email")
                .remove("user_bio")
                .apply()
            repository.clearAllUserData()
            repository.initDefaultHabitsIfEmpty()
            triggerHaptic()
            showToast(if (isArabicLanguage()) "تم إزالة الحساب وإعادة ضبط جميع البيانات بنجاح" else "Account & local data erased successfully")
        }
    }

    fun toggleCloudSync() {
        val newVal = !isCloudSyncEnabled.value
        isCloudSyncEnabled.value = newVal
        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("cloud_sync_enabled", newVal).apply()
        } catch (e: Exception) {}
        triggerHaptic()
        showToast(if (newVal) "Local Mirror Syncing active" else "Local Mirror Syncing paused")
    }

    fun setDailyQuranGoal(pages: Int) {
        dailyQuranGoal.value = pages
        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit().putInt("daily_quran_goal", pages).apply()
        } catch (e: Exception) {}
        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم تحديد الهدف: $pages صفحات يومياً" else "Quran goal set to $pages pages/day")
    }

    fun setDailyDhikrGoal(count: Int) {
        dailyDhikrGoal.value = count
        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit().putInt("daily_dhikr_goal", count).apply()
        } catch (e: Exception) {}
        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم تحديد الهدف: $count تسبيحة يومياً" else "Dhikr goal set to $count daily")
    }

    fun updateUserName(name: String) {
        val newName = if (name.isBlank()) "Guest Mode" else name
        userName.value = newName
        try {
            val prefs = getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("user_name", newName).apply()
        } catch (e: Exception) {}
    }

    // App Settings & Global Localization State
    val isSettingsModalOpen = MutableStateFlow(false)
    val showArabicSecondaryText = MutableStateFlow(
        try { sharedPrefs.getBoolean("show_arabic_secondary_text", true) } catch (e: Exception) { true }
    )
    val appLanguage = MutableStateFlow(
        try {
            application.getSharedPreferences("noor_prefs", Context.MODE_PRIVATE)
                .getString("app_language", "English") ?: "English"
        } catch (e: Exception) {
            "English"
        }
    )

    fun t(key: String): String = com.example.data.localization.AppStrings.get(key, appLanguage.value)
    val adhanSoundVolume = MutableStateFlow(sharedPrefs.getInt("adhan_volume", 85))

    val globalAdhanSoundId = MutableStateFlow(
        sharedPrefs.getString("adhan_sound_global", AdhanAudioRepository.ID_MAKKAH) ?: AdhanAudioRepository.ID_MAKKAH
    )
    val perPrayerAdhanSounds = MutableStateFlow<Map<String, String>>(emptyMap())

    fun openSettingsModal() {
        navigateTo(NoorDestination.SETTINGS)
        triggerHaptic()
    }

    fun closeSettingsModal() {
        navigateBack()
    }

    fun toggleArabicSecondaryText(enabled: Boolean? = null) {
        val newState = enabled ?: !showArabicSecondaryText.value
        showArabicSecondaryText.value = newState
        sharedPrefs.edit().putBoolean("show_arabic_secondary_text", newState).apply()
        triggerHaptic()
        showToast(if (newState) "Arabic secondary text enabled" else "Arabic secondary text hidden (English only)")
    }

    fun setAppLanguage(language: String) {
        appLanguage.value = language
        val isArabic = language.equals("Arabic", ignoreCase = true) ||
                language == "العربية" ||
                language.startsWith("ar", ignoreCase = true)

        val locale = if (isArabic) Locale("ar") else Locale("en")
        try {
            Locale.setDefault(locale)
            val prefs = getApplication<Application>().getSharedPreferences("noor_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("app_language", language).apply()
        } catch (e: Exception) {
            // ignore
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                val localeManager = getApplication<Application>().getSystemService(android.app.LocaleManager::class.java)
                localeManager?.applicationLocales = android.os.LocaleList.forLanguageTags(if (isArabic) "ar" else "en")
            } catch (e: Exception) {
                // ignore
            }
        }

        triggerHaptic()
        showToast(if (isArabic) "تم تغيير لغة التطبيق إلى العربية" else "App language set to $language")
    }

    fun setAdhanSoundVolume(volume: Int) {
        val clamped = volume.coerceIn(0, 100)
        adhanSoundVolume.value = clamped
        sharedPrefs.edit().putInt("adhan_volume", clamped).apply()
        AdhanPlayer.setVolume(clamped)
    }

    // ==========================================
    // SPIRITUAL NOTIFICATION CENTER
    // ==========================================
    val spiritualRemindersState = MutableStateFlow<Map<String, SpiritualReminderState>>(
        SpiritualReminderRepository.loadAllStates(sharedPrefs)
    )

    val activeSpiritualRemindersCount = spiritualRemindersState.map { map ->
        map.values.count { it.isEnabled }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun toggleSpiritualReminder(id: String, enabled: Boolean) {
        val currentMap = spiritualRemindersState.value.toMutableMap()
        val currentState = currentMap[id] ?: return
        val updatedState = currentState.copy(isEnabled = enabled)
        currentMap[id] = updatedState
        spiritualRemindersState.value = currentMap

        SpiritualReminderRepository.saveState(sharedPrefs, id, updatedState)
        if (enabled) {
            SpiritualAlarmScheduler.scheduleReminder(getApplication(), id, updatedState.hour, updatedState.minute)
        } else {
            SpiritualAlarmScheduler.cancelReminder(getApplication(), id)
        }
        triggerHaptic()
    }

    fun updateSpiritualReminderTime(id: String, hour: Int, minute: Int) {
        val currentMap = spiritualRemindersState.value.toMutableMap()
        val currentState = currentMap[id] ?: return
        val updatedState = currentState.copy(hour = hour, minute = minute)
        currentMap[id] = updatedState
        spiritualRemindersState.value = currentMap

        SpiritualReminderRepository.saveState(sharedPrefs, id, updatedState)
        if (updatedState.isEnabled) {
            SpiritualAlarmScheduler.scheduleReminder(getApplication(), id, hour, minute)
        }
        triggerHaptic()
    }

    fun muteAllSpiritualReminders() {
        val currentMap = spiritualRemindersState.value.toMutableMap()
        for ((id, state) in currentMap) {
            val muted = state.copy(isEnabled = false)
            currentMap[id] = muted
            SpiritualReminderRepository.saveState(sharedPrefs, id, muted)
            SpiritualAlarmScheduler.cancelReminder(getApplication(), id)
        }
        spiritualRemindersState.value = currentMap
        triggerHaptic()
        showToast(if (isArabicLanguage()) "تم كتم جميع التذكيرات" else "All spiritual reminders muted")
    }

    fun restoreRecommendedSpiritualReminders() {
        val currentMap = mutableMapOf<String, SpiritualReminderState>()
        for (item in SpiritualReminderRepository.reminders) {
            val state = SpiritualReminderState(
                isEnabled = item.defaultEnabled,
                hour = item.defaultHour,
                minute = item.defaultMinute
            )
            currentMap[item.id] = state
            SpiritualReminderRepository.saveState(sharedPrefs, item.id, state)
            if (state.isEnabled) {
                SpiritualAlarmScheduler.scheduleReminder(getApplication(), item.id, state.hour, state.minute)
            } else {
                SpiritualAlarmScheduler.cancelReminder(getApplication(), item.id)
            }
        }
        spiritualRemindersState.value = currentMap
        triggerHaptic()
        showToast(if (isArabicLanguage()) "تمت استعادة التذكيرات الموصى بها" else "Recommended reminders restored")
    }

    fun sendTestSpiritualNotification(id: String) {
        viewModelScope.launch {
            val message = if (isArabicLanguage()) {
                "سيصل الإشعار التجريبي بعد 10 ثوانٍ ⏰ أغلق شاشة الهاتف الآن لتجربته على شاشة القفل!"
            } else {
                "Test notification will trigger in 10s. Lock your phone now to test on lockscreen! ⏰"
            }
            showToast(message)
            delay(10000)
            NoorNotificationHelper.showSpiritualReminder(getApplication(), id)
            triggerHaptic()
        }
    }

    // Room Database Streams
    val favorites: StateFlow<List<FavoriteItemEntity>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedFavoritesFilter = MutableStateFlow("ALL") // "ALL", "DUA", "SURAH", "AYAH"

    fun openFavoritesWithFilter(filter: String) {
        selectedFavoritesFilter.value = filter
        navigateTo(NoorDestination.FAVORITES)
    }

    val quranBookmarks: StateFlow<List<com.example.data.local.QuranBookmarkEntity>> = repository.quranBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<DailyHabitEntity>> = repository.habits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readingProgress: StateFlow<ReadingProgressEntity?> = repository.readingProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Khatma (Quran Completion) State Streams
    val activeKhatmaPlan: StateFlow<KhatmaPlanEntity?> = repository.activeKhatmaPlan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val khatmaHistory: StateFlow<List<KhatmaHistoryEntity>> = repository.khatmaHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val khatmaDashboardState: StateFlow<KhatmaFullDashboardState?> = activeKhatmaPlan
        .map { plan ->
            plan?.let { KhatmaEngine.buildDashboardState(it) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Khatma UI Sheet / Modal states
    val isKhatmaSetupSheetOpen = MutableStateFlow(false)
    val isKhatmaSettingsSheetOpen = MutableStateFlow(false)
    val isKhatmaHistorySheetOpen = MutableStateFlow(false)
    val isKhatmaCompletionCelebrationOpen = MutableStateFlow(false)
    val isKhatmaPaceAdjustSheetOpen = MutableStateFlow(false)

    // Live Prayer Timings & Dynamic Multi-Zone State
    val selectedSalatTab = MutableStateFlow(SalatTab.TIMES)
    val prayerZones: List<PrayerZone> get() = repository.prayerZones
    val calculationAuthorities: List<CalculationAuthority> get() = repository.calculationAuthorities

    val selectedPrayerZone = MutableStateFlow<PrayerZone>(repository.prayerZones.first())
    val selectedAuthority = MutableStateFlow<CalculationAuthority>(repository.calculationAuthorities.first())
    val isHanafiAsr = MutableStateFlow(false)
    val prayerManualMinuteOffsets = MutableStateFlow<Map<String, Int>>(
        mapOf("Fajr" to 0, "Sunrise" to 0, "Dhuhr" to 0, "Asr" to 0, "Maghrib" to 0, "Isha" to 0)
    )
    val isSalatSettingsOpen = MutableStateFlow(false)
    val autoSilentDuringSalat = MutableStateFlow(
        sharedPrefs.getBoolean("auto_silent_salat", false)
    )
    val silentDurationMinutes = MutableStateFlow(
        sharedPrefs.getInt("silent_duration_minutes", 20)
    )
    val hijriAdjustmentDays = MutableStateFlow(
        try { sharedPrefs.getInt("hijri_adjustment_days", 0) } catch (e: Exception) { 0 }
    )
    val isAthanAudioPreviewPlaying = MutableStateFlow(false)

    // Per-Prayer Notification Timers (Offset minutes: -15 = 15m before, 0 = exact time, +10 = 10m after)
    val prayerNotificationTimers = MutableStateFlow<Map<String, Int>>(
        mapOf("Fajr" to -15, "Sunrise" to 0, "Dhuhr" to 0, "Asr" to 0, "Maghrib" to 0, "Isha" to 0)
    )
    val prayerNotificationEnabled = MutableStateFlow<Map<String, Boolean>>(
        mapOf("Fajr" to true, "Sunrise" to false, "Dhuhr" to true, "Asr" to true, "Maghrib" to true, "Isha" to true)
    )
    val prayerAlertTypes = MutableStateFlow<Map<String, String>>(
        mapOf(
            "Fajr" to "Adhan",
            "Sunrise" to "Mute",
            "Dhuhr" to "Adhan",
            "Asr" to "Adhan",
            "Maghrib" to "Adhan",
            "Isha" to "Adhan"
        )
    )

    val supplementaryPrayerTimes = MutableStateFlow<PrayerCalculator.SupplementaryTimes?>(null)

    val qadaRecords: StateFlow<List<QadaRecordEntity>> = repository.qadaRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fastLogs: StateFlow<List<FastLogEntity>> = repository.fastLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSalatTab(tab: SalatTab) {
        selectedSalatTab.value = tab
    }

    fun navigateToSalat(tab: SalatTab = SalatTab.TIMES) {
        selectedSalatTab.value = tab
        navigateTo(NoorDestination.SALAT)
    }

    fun selectPrayerZone(zone: PrayerZone) {
        selectedPrayerZone.value = zone
        isLocationConfigured.value = true
        locationName.value = "${zone.name}, ${zone.country}"
        sharedPrefs.edit()
            .putString("selected_prayer_zone_id", zone.id)
            .putBoolean("is_location_configured", true)
            .apply()
        viewModelScope.launch {
            val savedCompleted = repository.getCompletedPrayersForFajrDay(zone)
            _completedPrayers.value = savedCompleted
            refreshPrayerTimes()
            schedulePrayerAlarms()
        }
        triggerHaptic()
        showToast("Active Zone: ${zone.name} (${zone.zoneLabel})")
    }

    fun selectCalculationAuthority(auth: CalculationAuthority) {
        selectedAuthority.value = auth
        sharedPrefs.edit().putString("selected_calc_authority_id", auth.id).apply()
        refreshPrayerTimes()
        schedulePrayerAlarms()
        triggerHaptic()
        showToast("Standard: ${auth.name}")
    }

    fun toggleHanafiAsr(enabled: Boolean) {
        isHanafiAsr.value = enabled
        sharedPrefs.edit().putBoolean("is_hanafi_asr", enabled).apply()
        refreshPrayerTimes()
        schedulePrayerAlarms()
        triggerHaptic()
        showToast(if (enabled) "Hanafi Asr (2x shadow) applied" else "Standard Asr applied")
    }

    val qiyamReminderOffsetMinutes = MutableStateFlow(-45)
    val isAudioNetworkError = MutableStateFlow(false)

    fun updateQiyamOffset(deltaMinutes: Int) {
        val oldVal = qiyamReminderOffsetMinutes.value
        val newVal = (oldVal + deltaMinutes).coerceIn(-180, 180)
        qiyamReminderOffsetMinutes.value = newVal
        sharedPrefs.edit().putInt("qiyam_reminder_offset_minutes", newVal).apply()
        triggerHaptic()
        val label = if (newVal > 0) "+$newVal min" else "$newVal min"
        showToast("Qiyam/Tahajjud reminder offset: $label")
    }

    fun resetQiyamOffset() {
        qiyamReminderOffsetMinutes.value = -45
        sharedPrefs.edit().putInt("qiyam_reminder_offset_minutes", -45).apply()
        triggerHaptic()
        showToast("Qiyam reminder reset to 45m before Fajr")
    }

    fun updatePrayerManualOffset(prayerName: String, deltaMinutes: Int) {
        val current = prayerManualMinuteOffsets.value.toMutableMap()
        val oldVal = current[prayerName] ?: 0
        val newVal = (oldVal + deltaMinutes).coerceIn(-60, 60)
        current[prayerName] = newVal
        prayerManualMinuteOffsets.value = current
        sharedPrefs.edit().putInt("manual_offset_$prayerName", newVal).apply()
        refreshPrayerTimes()
        schedulePrayerAlarms()
        triggerHaptic()
        val label = if (newVal > 0) "+$newVal min" else if (newVal < 0) "$newVal min" else "0 min (Default)"
        showToast("$prayerName offset adjusted: $label")
    }

    fun resetPrayerManualOffsets() {
        val resetMap = mapOf("Fajr" to 0, "Sunrise" to 0, "Dhuhr" to 0, "Asr" to 0, "Maghrib" to 0, "Isha" to 0)
        prayerManualMinuteOffsets.value = resetMap
        val editor = sharedPrefs.edit()
        resetMap.keys.forEach { pName ->
            editor.putInt("manual_offset_$pName", 0)
        }
        editor.apply()
        refreshPrayerTimes()
        schedulePrayerAlarms()
        triggerHaptic()
        showToast("All prayer manual offsets reset to 0")
    }

    fun schedulePrayerAlarms() {
        PrayerAlarmScheduler.scheduleAll(
            context = getApplication(),
            prayers = _prayerTimes.value,
            timersMap = prayerNotificationTimers.value,
            enabledMap = prayerNotificationEnabled.value
        )
    }

    fun setPrayerAlertType(prayerName: String, alertType: String) {
        val current = prayerAlertTypes.value.toMutableMap()
        current[prayerName] = alertType
        prayerAlertTypes.value = current
        sharedPrefs.edit().putString("alert_type_$prayerName", alertType).apply()

        val enabledMap = prayerNotificationEnabled.value.toMutableMap()
        enabledMap[prayerName] = (alertType != "Mute")
        prayerNotificationEnabled.value = enabledMap

        schedulePrayerAlarms()
        triggerHaptic()
        val label = when (alertType) {
            "Mute" -> "Muted"
            "Notification" -> "Silent Notification"
            "Adhan" -> "Adhan Audio Alert"
            else -> alertType
        }
        showToast("$prayerName alert set to $label")
    }

    fun setPrayerNotificationTimer(prayerName: String, offsetMinutes: Int) {
        val timers = prayerNotificationTimers.value.toMutableMap()
        timers[prayerName] = offsetMinutes
        prayerNotificationTimers.value = timers
        sharedPrefs.edit().putInt("notification_timer_$prayerName", offsetMinutes).apply()

        val currentAlerts = prayerAlertTypes.value.toMutableMap()
        val currentAlert = currentAlerts[prayerName] ?: "Adhan"
        if (currentAlert == "Mute") {
            currentAlerts[prayerName] = "Notification"
            prayerAlertTypes.value = currentAlerts
            sharedPrefs.edit().putString("alert_type_$prayerName", "Notification").apply()
            
            val enabledMap = prayerNotificationEnabled.value.toMutableMap()
            enabledMap[prayerName] = true
            prayerNotificationEnabled.value = enabledMap
        }

        schedulePrayerAlarms()
        triggerHaptic()

        val label = when {
            offsetMinutes == 0 -> "Exact Time"
            offsetMinutes < 0 -> "${-offsetMinutes}m Before"
            else -> "+${offsetMinutes}m After"
        }
        showToast("$prayerName timing set to $label")
    }

    fun toggleAthanAudioPreview(soundId: String? = null) {
        val currentlyPlaying = isAthanAudioPreviewPlaying.value
        if (currentlyPlaying) {
            AdhanPlayer.stop(isDismiss = false)
            isAthanAudioPreviewPlaying.value = false
            showToast("Adhan preview stopped")
        } else {
            val targetSoundId = soundId ?: globalAdhanSoundId.value
            AdhanPlayer.play(
                context = getApplication(),
                prayerName = "Salat",
                isSnooze = true,
                soundIdOverride = targetSoundId
            )
            isAthanAudioPreviewPlaying.value = true
            val sound = AdhanAudioRepository.getSoundById(getApplication(), targetSoundId)
            showToast("Playing preview: ${sound.title}")
        }
        triggerHaptic()
    }

    fun setPrayerAdhanSound(prayerName: String, soundId: String) {
        AdhanAudioRepository.setSoundForPrayer(getApplication(), prayerName, soundId)
        val updated = perPrayerAdhanSounds.value.toMutableMap()
        updated[prayerName] = soundId
        perPrayerAdhanSounds.value = updated
        triggerHaptic()
        val sound = AdhanAudioRepository.getSoundById(getApplication(), soundId, prayerName)
        showToast("$prayerName adhan set to ${sound.title}")
    }

    fun setGlobalAdhanSound(soundId: String) {
        AdhanAudioRepository.setGlobalSound(getApplication(), soundId)
        globalAdhanSoundId.value = soundId
        val sound = AdhanAudioRepository.getSoundById(getApplication(), soundId)
        athanSoundName.value = sound.title
        triggerHaptic()
        showToast("Default adhan set to ${sound.title}")
    }

    fun importCustomAdhanAudio(uri: Uri, targetPrayerName: String? = null) {
        val sound = AdhanAudioRepository.importCustomAudio(getApplication(), uri)
        if (sound != null) {
            if (targetPrayerName != null) {
                setPrayerAdhanSound(targetPrayerName, AdhanAudioRepository.ID_CUSTOM)
            } else {
                setGlobalAdhanSound(AdhanAudioRepository.ID_CUSTOM)
            }
            triggerHaptic()
            showToast("Custom Adhan imported: ${sound.title}")
        } else {
            showToast("Could not import audio file from device")
        }
    }

    fun updateHijriAdjustment(delta: Int) {
        val current = hijriAdjustmentDays.value
        val updated = (current + delta).coerceIn(-3, 3)
        hijriAdjustmentDays.value = updated
        sharedPrefs.edit().putInt("hijri_adjustment_days", updated).apply()
        triggerHaptic()
        showToast("Hijri calendar adjusted by $updated days")
    }

    fun toggleAutoSilent() {
        val newState = !autoSilentDuringSalat.value
        autoSilentDuringSalat.value = newState
        sharedPrefs.edit().putBoolean("auto_silent_salat", newState).apply()
        triggerHaptic()
        showToast(if (newState) "Mosque Mode (Auto-Silent) enabled" else "Mosque Mode disabled")
    }

    fun setSilentDuration(minutes: Int) {
        val clamped = minutes.coerceIn(10, 60)
        silentDurationMinutes.value = clamped
        sharedPrefs.edit().putInt("silent_duration_minutes", clamped).apply()
    }

    fun incrementQadaMissed(prayerType: String, delta: Int = 1) {
        viewModelScope.launch {
            repository.updateQadaMissedCount(prayerType, delta)
            triggerHaptic()
        }
    }

    fun completeQadaMadeUp(prayerType: String) {
        viewModelScope.launch {
            repository.completeQadaMadeUp(prayerType)
            triggerHaptic()
            showToast("Alhamdulillah! 1 $prayerType Qada prayer completed.")
        }
    }

    fun decrementQadaMadeUp(prayerType: String) {
        viewModelScope.launch {
            repository.decrementQadaMadeUp(prayerType)
            triggerHaptic()
        }
    }

    fun toggleFastLogCompleted(fastLog: FastLogEntity) {
        viewModelScope.launch {
            repository.toggleFastLogCompleted(fastLog)
            triggerHaptic()
            if (!fastLog.isCompleted) {
                showToast("Taqabbal Allah! Fast marked as completed.")
            }
        }
    }

    fun addRamadanMakeupFast() {
        viewModelScope.launch {
            val currentCount = (fastLogs.value.count { it.type == "RAMADAN_MAKEUP" }) + 1
            repository.addFastLog(
                title = "Ramadan Make-Up Fast #$currentCount",
                type = "RAMADAN_MAKEUP",
                subtitle = "Obligatory Qada for missed Ramadan day"
            )
            triggerHaptic()
            showToast("Added Ramadan Make-Up Fast #$currentCount")
        }
    }

    fun addCustomFastLog(title: String, type: String = "VOLUNTARY", subtitle: String = "") {
        viewModelScope.launch {
            repository.addFastLog(
                title = title,
                type = type,
                subtitle = subtitle
            )
            triggerHaptic()
            showToast("Added Fast: $title")
        }
    }

    fun deleteFastLog(fastLog: FastLogEntity) {
        viewModelScope.launch {
            repository.deleteFastLog(fastLog)
            triggerHaptic()
            showToast("Fast record removed")
        }
    }

    private val _prayerTimes = MutableStateFlow<List<PrayerTime>>(emptyList())
    val prayerTimes: StateFlow<List<PrayerTime>> = _prayerTimes.asStateFlow()

    private val _nextPrayerCountdown = MutableStateFlow("00:00:00")
    val nextPrayerCountdown: StateFlow<String> = _nextPrayerCountdown.asStateFlow()

    private val _nextPrayerName = MutableStateFlow("Dhuhr")
    val nextPrayerName: StateFlow<String> = _nextPrayerName.asStateFlow()

    private val _nextPrayerTimeStr = MutableStateFlow("13:34")
    val nextPrayerTimeStr: StateFlow<String> = _nextPrayerTimeStr.asStateFlow()

    private val _completedPrayers = MutableStateFlow<Set<String>>(emptySet())
    val completedPrayers: StateFlow<Set<String>> = _completedPrayers.asStateFlow()

    // Notification Offset in minutes (+/-)
    val prayerOffsetMinutes = MutableStateFlow(0)
    val athanSoundName = MutableStateFlow("Makkah Al-Mukarramah Adhan")

    // Daily Mood & Wisdom
    val selectedMood = MutableStateFlow(
        try { sharedPrefs.getString("selected_daily_mood", "Anxious") ?: "Anxious" } catch (e: Exception) { "Anxious" }
    )
    val isIslamicWisdomMode = MutableStateFlow(true)


    // Audio Player State
    val isAudioPlaying = MutableStateFlow(false)
    val isAudioBuffering = MutableStateFlow(false)
    val currentPlayingSurah = MutableStateFlow(QuranData.surahs.first())
    val currentPlayingVerse = MutableStateFlow(1)
    val selectedReciter = MutableStateFlow(
        run {
            val savedReciterId = try { sharedPrefs.getString("selected_reciter_id", null) } catch (e: Exception) { null }
            if (savedReciterId != null) {
                QuranData.reciters.find { it.id == savedReciterId } ?: QuranData.reciters.first()
            } else {
                QuranData.reciters.first()
            }
        }
    )
    val reciters: List<Reciter> = QuranData.reciters
    val audioProgress = MutableStateFlow(0f)
    val audioDurationMs = MutableStateFlow(0)
    val audioCurrentPositionMs = MutableStateFlow(0)
    val audioPlaybackSpeed = MutableStateFlow(
        try { sharedPrefs.getFloat("audio_playback_speed", 1.0f) } catch (e: Exception) { 1.0f }
    )
    val isAutoAdvanceAyah = MutableStateFlow(
        try { sharedPrefs.getBoolean("is_auto_advance_ayah", true) } catch (e: Exception) { true }
    )
    val isAudioRepeatOne = MutableStateFlow(
        try { sharedPrefs.getBoolean("is_audio_repeat_one", false) } catch (e: Exception) { false }
    )
    val isAyahAudioMode = MutableStateFlow(false)
    val sleepTimerMinutes = MutableStateFlow<Int?>(null)
    private var sleepTimerJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioProgressTrackerJob: Job? = null


    // Do Not Disturb (DND) Reading Mode State
    val isDndReadingEnabled = MutableStateFlow(
        try { sharedPrefs.getBoolean("is_dnd_reading_enabled", false) } catch (e: Exception) { false }
    )

    fun setDndReadingEnabled(enabled: Boolean) {
        isDndReadingEnabled.value = enabled
        sharedPrefs.edit().putBoolean("is_dnd_reading_enabled", enabled).apply()
    }

    fun isNotificationPolicyAccessGranted(context: android.content.Context): Boolean {
        val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
        return notificationManager?.isNotificationPolicyAccessGranted == true
    }

    private var originalInterruptionFilter: Int? = null
    private var isDndActivatedByApp = false

    fun enableDndIfAllowed(context: android.content.Context) {
        if (!isDndReadingEnabled.value) return
        try {
            val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
            if (notificationManager != null && notificationManager.isNotificationPolicyAccessGranted) {
                if (!isDndActivatedByApp) {
                    originalInterruptionFilter = notificationManager.currentInterruptionFilter
                }
                notificationManager.setInterruptionFilter(android.app.NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                isDndActivatedByApp = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disableDnd(context: android.content.Context) {
        try {
            val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
            if (notificationManager != null && notificationManager.isNotificationPolicyAccessGranted) {
                if (isDndActivatedByApp) {
                    val filterToRestore = originalInterruptionFilter ?: android.app.NotificationManager.INTERRUPTION_FILTER_ALL
                    notificationManager.setInterruptionFilter(filterToRestore)
                    isDndActivatedByApp = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Daily Devotions Tracking (Ayahs Read Today & Azkar Count Today)
    private val todayDevotionDateStr: String
        get() = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    val quranAyahsReadToday = MutableStateFlow(
        try {
            val savedDate = sharedPrefs.getString("quran_ayahs_read_today_date", "") ?: ""
            if (savedDate == java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))) {
                sharedPrefs.getInt("quran_ayahs_read_today", 0)
            } else 0
        } catch (e: Exception) { 0 }
    )

    val azkarCountToday = MutableStateFlow(
        try {
            val savedDate = sharedPrefs.getString("azkar_count_today_date", "") ?: ""
            if (savedDate == java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))) {
                sharedPrefs.getInt("azkar_count_today", 0)
            } else 0
        } catch (e: Exception) { 0 }
    )

    fun addQuranAyahsReadToday(count: Int) {
        if (count <= 0) return
        val todayStr = todayDevotionDateStr
        val savedDate = sharedPrefs.getString("quran_ayahs_read_today_date", "") ?: ""
        val baseCount = if (savedDate == todayStr) quranAyahsReadToday.value else 0
        val newCount = baseCount + count
        quranAyahsReadToday.value = newCount
        sharedPrefs.edit()
            .putString("quran_ayahs_read_today_date", todayStr)
            .putInt("quran_ayahs_read_today", newCount)
            .apply()
        if (newCount >= 10) {
            viewModelScope.launch {
                repository.recordQuranActivity()
            }
        }
    }

    fun incrementAzkarCountToday() {
        val todayStr = todayDevotionDateStr
        val savedDate = sharedPrefs.getString("azkar_count_today_date", "") ?: ""
        val baseCount = if (savedDate == todayStr) azkarCountToday.value else 0
        val newCount = baseCount + 1
        azkarCountToday.value = newCount
        sharedPrefs.edit()
            .putString("azkar_count_today_date", todayStr)
            .putInt("azkar_count_today", newCount)
            .apply()
        if (newCount >= 5) {
            viewModelScope.launch {
                repository.recordAzkarActivity()
            }
        }
    }

    // Digital Tasbih State
    val tasbihCount = MutableStateFlow(0)
    val tasbihTarget = MutableStateFlow(
        try { sharedPrefs.getInt("tasbih_target", 33) } catch (e: Exception) { 33 }
    )
    val selectedDhikr = MutableStateFlow(
        try { sharedPrefs.getString("tasbih_dhikr_title", "SubhanAllah (سُبْحَانَ اللَّهِ)") ?: "SubhanAllah (سُبْحَانَ اللَّهِ)" } catch (e: Exception) { "SubhanAllah (سُبْحَانَ اللَّهِ)" }
    )
    val selectedDhikrArabic = MutableStateFlow(
        try { sharedPrefs.getString("tasbih_dhikr_arabic", "سُبْحَانَ اللَّهِ") ?: "سُبْحَانَ اللَّهِ" } catch (e: Exception) { "سُبْحَانَ اللَّهِ" }
    )
    val selectedDhikrMeaning = MutableStateFlow(
        try { sharedPrefs.getString("tasbih_dhikr_meaning", "Glory be to Allah in His infinite perfection") ?: "Glory be to Allah in His infinite perfection" } catch (e: Exception) { "Glory be to Allah in His infinite perfection" }
    )
    val selectedDhikrVirtue = MutableStateFlow(
        try { sharedPrefs.getString("tasbih_dhikr_virtue", "Fills the scales with immense spiritual reward") ?: "Fills the scales with immense spiritual reward" } catch (e: Exception) { "Fills the scales with immense spiritual reward" }
    )
    val tasbihTotalAllTime = MutableStateFlow(
        try { sharedPrefs.getInt("tasbih_total_all_time", 0) } catch (e: Exception) { 0 }
    )
    val tasbihLapsCompleted = MutableStateFlow(
        try { sharedPrefs.getInt("tasbih_laps_completed", 0) } catch (e: Exception) { 0 }
    )
    val tasbihVisualTheme = MutableStateFlow(
        try { sharedPrefs.getString("tasbih_visual_theme", "Marble") ?: "Marble" } catch (e: Exception) { "Marble" }
    ) // "Marble", "Minimal Circle"
    val tasbihMarbleStyle = MutableStateFlow(
        try { sharedPrefs.getString("tasbih_marble_style", "GOLD") ?: "GOLD" } catch (e: Exception) { "GOLD" }
    )
    val isTasbihHapticEnabled = MutableStateFlow(
        try { sharedPrefs.getBoolean("tasbih_haptic_enabled", true) } catch (e: Exception) { true }
    )
    val isTasbihSoundEnabled = MutableStateFlow(
        try { sharedPrefs.getBoolean("tasbih_sound_enabled", true) } catch (e: Exception) { true }
    )
    val isTasbihAutoReset = MutableStateFlow(
        try { sharedPrefs.getBoolean("tasbih_auto_reset", true) } catch (e: Exception) { true }
    )
    val isTasbihBeadsVisible = MutableStateFlow(
        try { sharedPrefs.getBoolean("tasbih_beads_visible", true) } catch (e: Exception) { true }
    )
    val isTasbihTransliterationVisible = MutableStateFlow(
        try { sharedPrefs.getBoolean("tasbih_transliteration_visible", true) } catch (e: Exception) { true }
    )
    val isTasbihTranslationVisible = MutableStateFlow(
        try { sharedPrefs.getBoolean("tasbih_translation_visible", true) } catch (e: Exception) { true }
    )
    val isSelectedDhikrCustom = MutableStateFlow(
        try { sharedPrefs.getBoolean("tasbih_dhikr_is_custom", false) } catch (e: Exception) { false }
    )
    val selectedDhikrAudioUrl = MutableStateFlow(
        try { sharedPrefs.getString("tasbih_dhikr_audio_url", "https://www.hisnmuslim.com/audio/ar/106.mp3") ?: "https://www.hisnmuslim.com/audio/ar/106.mp3" } catch (e: Exception) { "https://www.hisnmuslim.com/audio/ar/106.mp3" }
    )

    // Home Screen Customization & Widget Order
    val homeWidgetsOrder = MutableStateFlow<List<HomeWidgetType>>(HomeWidgetType.defaultOrderedList())
    val homeWidgetsVisibility = MutableStateFlow<Map<HomeWidgetType, Boolean>>(
        HomeWidgetType.values().associateWith { it.defaultVisible }
    )
    val isCustomizeHomeSheetOpen = MutableStateFlow(false)

    // Quick Access Customization (2x2 Grid)
    val quickAccessTools = MutableStateFlow<List<QuickAccessTool>>(QuickAccessTool.defaultTools())
    val isCustomizeQuickAccessSheetOpen = MutableStateFlow(false)

    // Shortcuts Bottom Sheet & Home Tutorial Replay State
    val isShortcutsSheetOpen = MutableStateFlow(false)
    val isHomeTutorialRequested = MutableStateFlow(false)
    val hasSeenHomeTutorial = MutableStateFlow(sharedPrefs.getBoolean("has_seen_home_tutorial", false))
    val hasSeenOnboarding = MutableStateFlow(sharedPrefs.getBoolean("has_seen_onboarding_v4_complete", false))
    val isTutorialActive = MutableStateFlow(false)

    // Quran Reader State
    val selectedSurahForReading = MutableStateFlow(QuranData.surahs.first())
    val targetAyahToScrollTo = MutableStateFlow(0)
    val quranScrollRequest = MutableStateFlow<QuranScrollRequest?>(null)
    val arabicFontSizeSp = MutableStateFlow(
        try { sharedPrefs.getInt("quran_arabic_font_size", 24) } catch (e: Exception) { 24 }
    )
    val selectedArabicFont = MutableStateFlow(
        run {
            val savedFontId = try { sharedPrefs.getString("quran_arabic_font", QuranArabicFont.AMIRI.id) } catch (e: Exception) { QuranArabicFont.AMIRI.id }
            QuranArabicFont.fromId(savedFontId)
        }
    )
    val showTransliteration = MutableStateFlow(
        try { sharedPrefs.getBoolean("quran_show_transliteration", true) } catch (e: Exception) { true }
    )
    val showTranslation = MutableStateFlow(
        try { sharedPrefs.getBoolean("quran_show_translation", true) } catch (e: Exception) { true }
    )
    val isMushafFlowMode = MutableStateFlow(sharedPrefs.getBoolean("is_mushaf_flow_mode", false)) // Distraction-Free Pure Reading Flow
    val isQuranReaderFullscreen = MutableStateFlow(false) // Immersive Fullscreen Mode (resets per session)
    val isQuranSepiaMode = MutableStateFlow(sharedPrefs.getBoolean("is_quran_sepia_mode", false)) // Independent Sepia Parchment Canvas exclusive to Quran Reader
    val isTajweedEnabled = MutableStateFlow(sharedPrefs.getBoolean("is_tajweed_enabled", false)) // Real Uthmani Tajweed Color-Coding
    val tajweedButtonPosition = MutableStateFlow(
        try {
            TajweedButtonPosition.valueOf(sharedPrefs.getString("tajweed_btn_position", TajweedButtonPosition.BOTTOM_RIGHT.name) ?: TajweedButtonPosition.BOTTOM_RIGHT.name)
        } catch (e: Exception) {
            TajweedButtonPosition.BOTTOM_RIGHT
        }
    )

    // Memorization & Progressive Reveal System
    val isHideUnreadVersesEnabled = MutableStateFlow(sharedPrefs.getBoolean("is_hide_unread_verses", false))
    val memorizationRepeatCount = MutableStateFlow(sharedPrefs.getInt("memorization_repeat_count", 1))
    val memorizationCurrentRepetition = MutableStateFlow(1)
    val revealedVersesInSession = MutableStateFlow<Set<Int>>(emptySet())

    // Dedicated Quran Memorization Studio / Hifz Hub States
    val memorizationSurah = MutableStateFlow<Surah>(QuranData.surahs.firstOrNull() ?: Surah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan", emptyList()))
    val memorizationStartAyah = MutableStateFlow(1)
    val memorizationEndAyah = MutableStateFlow(7)
    val memorizationMaskingStyle = MutableStateFlow(
        try {
            HifzMaskingStyle.valueOf(
                sharedPrefs.getString("hifz_masking_style", HifzMaskingStyle.BLUR_MUTED.name)
                    ?: HifzMaskingStyle.BLUR_MUTED.name
            )
        } catch (e: Exception) {
            HifzMaskingStyle.BLUR_MUTED
        }
    )
    val hifzSilhouetteOpacity = MutableStateFlow(sharedPrefs.getFloat("hifz_silhouette_opacity", 0.18f))
    val memorizationDelaySeconds = MutableStateFlow(sharedPrefs.getInt("hifz_delay_seconds", 0))
    val memorizationLoopRange = MutableStateFlow(sharedPrefs.getBoolean("hifz_loop_range", false))
    val memorizationAudioSyncReveal = MutableStateFlow(sharedPrefs.getBoolean("hifz_audio_sync_reveal", true))
    val memorizationShowTranslation = MutableStateFlow(sharedPrefs.getBoolean("hifz_show_translation", false))
    val memorizationTestRecallMode = MutableStateFlow(false)
    val memorizedPracticeSet = MutableStateFlow<Set<String>>(
        sharedPrefs.getStringSet("hifz_memorized_practice_set", null)
            ?: sharedPrefs.getStringSet("hifz_memorized_ayahs_set", emptySet())
            ?: emptySet()
    )
    val memorizedRecallSet = MutableStateFlow<Set<String>>(
        sharedPrefs.getStringSet("hifz_memorized_recall_set", emptySet()) ?: emptySet()
    )
    val memorizedAyahsSet = memorizedPracticeSet
    val memorizationDelayActive = MutableStateFlow(false)
    val memorizationDelayCountdown = MutableStateFlow(0)
    val memorizationStudioTab = MutableStateFlow(0) // 0: Practice Drill, 1: Range Selector, 2: Self-Test Recall, 3: Stats

    val hifzSessionLogs = MutableStateFlow<List<com.example.data.quran.HifzSessionLog>>(
        try {
            com.example.data.quran.HifzHistoryRepository.getSessionLogs(application)
        } catch (e: Exception) {
            emptyList()
        }
    )

    fun recordHifzSession(
        surahNumber: Int,
        surahName: String,
        surahNameArabic: String = "",
        startAyah: Int,
        endAyah: Int,
        mode: String,
        totalAyahs: Int,
        ayahsMemorized: Int,
        ayahsMissed: Int,
        ayahsStruggled: Int = 0,
        hintsUsed: Int = 0,
        durationSeconds: Int = 180,
        repetitionsCompleted: Int = 1,
        notes: String = ""
    ) {
        val log = com.example.data.quran.HifzSessionLog(
            surahNumber = surahNumber,
            surahName = surahName,
            surahNameArabic = surahNameArabic,
            startAyah = startAyah,
            endAyah = endAyah,
            mode = mode,
            totalAyahs = totalAyahs,
            ayahsMemorized = ayahsMemorized,
            ayahsMissed = ayahsMissed,
            ayahsStruggled = ayahsStruggled,
            hintsUsed = hintsUsed,
            durationSeconds = durationSeconds,
            repetitionsCompleted = repetitionsCompleted,
            notes = notes
        )
        try {
            com.example.data.quran.HifzHistoryRepository.recordSession(getApplication(), log)
            hifzSessionLogs.value = com.example.data.quran.HifzHistoryRepository.getSessionLogs(getApplication())
        } catch (e: Exception) {}
    }

    fun clearHifzHistory() {
        try {
            com.example.data.quran.HifzHistoryRepository.clearHistory(getApplication())
            hifzSessionLogs.value = com.example.data.quran.HifzHistoryRepository.getSessionLogs(getApplication())
        } catch (e: Exception) {}
        triggerHaptic()
        showToast("Hifz session history cleared")
    }

    // Word-Level & Ayah-Level Custom Repetition Patterns
    val memorizationDrillMode = MutableStateFlow(sharedPrefs.getString("hifz_drill_mode", "AYAH") ?: "AYAH") // "AYAH" or "WORD"
    val isCustomPatternEnabled = MutableStateFlow(sharedPrefs.getBoolean("hifz_custom_pattern_enabled", false))
    val memorizationWordPattern = MutableStateFlow(sharedPrefs.getString("hifz_word_pattern", "4, 5, 2") ?: "4, 5, 2")
    val memorizationAyahPattern = MutableStateFlow(sharedPrefs.getString("hifz_ayah_pattern", "3, 3, 3") ?: "3, 3, 3")
    val memorizationCurrentPatternIndex = MutableStateFlow(0)
    val memorizationWordChunkSize = MutableStateFlow(sharedPrefs.getInt("hifz_word_chunk_size", 3)) // Backward compatibility
    val memorizationWordRepeatCount = MutableStateFlow(sharedPrefs.getInt("hifz_word_repeat_count", 3)) // 1x, 2x, 3x, 5x, 7x, 10x
    val memorizationActiveWordAyah = MutableStateFlow(1)
    val memorizationWordChunkStart = MutableStateFlow(1)
    val memorizationWordChunkEnd = MutableStateFlow(3)
    val memorizationWordDelaySeconds = MutableStateFlow(sharedPrefs.getInt("hifz_word_delay_seconds", 1))

    fun setCustomPatternEnabled(enabled: Boolean) {
        isCustomPatternEnabled.value = enabled
        sharedPrefs.edit().putBoolean("hifz_custom_pattern_enabled", enabled).apply()
        triggerHaptic()
    }

    // Progressive Chain & Confidence State
    val memorizationChunkReviewSize = MutableStateFlow(sharedPrefs.getInt("hifz_chunk_review_size", 5))
    val recallGroupSize = MutableStateFlow(sharedPrefs.getInt("hifz_recall_group_size", 1))
    val warmUpAvailableRange = MutableStateFlow<Pair<Int, Int>?>(null)
    val activeConfidencePrompt = MutableStateFlow<ConfidencePromptData?>(null)
    val drillProgressState = MutableStateFlow(HifzDrillProgressInfo())

    val hifzAudioController by lazy {
        HifzAudioController(getApplication(), viewModelScope)
    }
    val hifzAudioState: StateFlow<HifzAudioState> by lazy {
        hifzAudioController.state
    }
    val isHifzPaused = MutableStateFlow(false)
    val sharedReadingTheme = MutableStateFlow(
        when (sharedPrefs.getString("shared_reading_theme", "Madani Crisp")) {
            "Obsidian Night", "Dark" -> "Obsidian Night"
            "Warm Parchment", "Warm", "Sepia" -> "Warm Parchment"
            else -> "Madani Crisp"
        }
    ) // App-wide theme: "Madani Crisp" (Light), "Obsidian Night" (Dark), or "Warm Parchment" (Warm)
    val quranReadingTheme = sharedReadingTheme // Maintained for backward compatibility
    val appThemeMode: StateFlow<com.example.ui.theme.AppThemeMode> = sharedReadingTheme
        .map { themeName ->
            when (themeName) {
                "Obsidian Night", "Dark" -> com.example.ui.theme.AppThemeMode.DARK
                "Warm Parchment", "Warm", "Sepia" -> com.example.ui.theme.AppThemeMode.WARM
                else -> com.example.ui.theme.AppThemeMode.LIGHT
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            when (sharedPrefs.getString("shared_reading_theme", "Madani Crisp")) {
                "Obsidian Night", "Dark" -> com.example.ui.theme.AppThemeMode.DARK
                "Warm Parchment", "Warm", "Sepia" -> com.example.ui.theme.AppThemeMode.WARM
                else -> com.example.ui.theme.AppThemeMode.LIGHT
            }
        )
    val isDarkMode: StateFlow<Boolean> = appThemeMode
        .map { it == com.example.ui.theme.AppThemeMode.DARK }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            sharedPrefs.getString("shared_reading_theme", "Madani Crisp") == "Obsidian Night"
        )
    val homeTheme = MutableStateFlow(
        sharedPrefs.getString("app_home_theme", "Peach") ?: "Peach"
    )
    val quranNotes: StateFlow<List<QuranNoteEntity>> = repository.allQuranNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val quranSearchQuery = MutableStateFlow("")
    val quranFilterCategory = MutableStateFlow("All") // "All", "Meccan", "Medinan", "Popular", "Juz 'Amma"

    // Khatma Automatic Milestone Celebration Popup
    val khatmaMilestoneModal = MutableStateFlow<KhatmaMilestoneData?>(null)

    // Offline Audio Download Manager State
    val downloadedSurahs = MutableStateFlow<Set<String>>(emptySet())
    val isSurahDownloading = MutableStateFlow<Map<String, Float>>(emptyMap())

    // Du'as & Azkar State & Preferences
    val selectedDuaCategory = MutableStateFlow("Morning Azkar")
    val duasSearchQuery = MutableStateFlow("")
    val azkarRemainingCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val showArabicInAzkarCards = MutableStateFlow(true)
    val azkarTextSize = MutableStateFlow("Small") // "Small", "Medium", "Large", "Extra Large"
    val isAzkarAutoScrollEnabled = MutableStateFlow(true)
    val isAzkarHapticEnabled = MutableStateFlow(true)
    val showAzkarTransliteration = MutableStateFlow(false)
    val showAzkarBenefits = MutableStateFlow(true)
    val isAzkarSettingsOpen = MutableStateFlow(false)

    // Toast/Feedback message
    val toastMessage = MutableStateFlow<String?>(null)

    // Unified Streak System State
    val unifiedStreakData: StateFlow<UnifiedStreakData> = combine(
        repository.streakDailyLogs,
        repository.streakSummary,
        isStreakTrackingEnabled
    ) { logs, summary, trackingEnabled ->
        StreakEngine.calculateStreakData(logs, summary, isTrackingEnabled = trackingEnabled)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UnifiedStreakData()
    )

    fun toggleStreakTracking(enabled: Boolean) {
        isStreakTrackingEnabled.value = enabled
        try {
            getApplication<Application>().getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
                .edit().putBoolean("streak_tracking_enabled", enabled).apply()
        } catch (_: Exception) {}
        if (!enabled) {
            showToast("Streak tracking paused for a pressure-free experience.")
        } else {
            showToast("Streak tracking enabled.")
        }
    }

    fun excuseDay(dateStr: String, reason: String = "Travel") {
        viewModelScope.launch {
            repository.excuseDay(dateStr, reason)
            triggerHaptic()
            showToast("Day excused ($reason). Streak protected without using a freeze pass.")
        }
    }

    fun removeDayExcuse(dateStr: String) {
        viewModelScope.launch {
            repository.removeDayExcuse(dateStr)
            triggerHaptic()
            showToast("Excuse removed for $dateStr.")
        }
    }

    fun toggleDayActivity(dateStr: String, activityType: StreakActivityType, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.setActivityForDay(dateStr, activityType, isCompleted)
            triggerHaptic()
        }
    }

    fun excuseToday(reason: String = "Travel") {
        val todayStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        excuseDay(todayStr, reason)
    }

    fun excuseYesterday(reason: String = "Travel") {
        val yesterdayStr = java.time.LocalDate.now().minusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        excuseDay(yesterdayStr, reason)
    }

    fun recordQuranActivity() {
        viewModelScope.launch {
            if (quranAyahsReadToday.value < 10) {
                addQuranAyahsReadToday(1)
            } else {
                repository.recordQuranActivity()
            }
        }
    }

    fun recordAzkarActivity() {
        viewModelScope.launch {
            if (azkarCountToday.value < 5) {
                incrementAzkarCountToday()
            } else {
                repository.recordAzkarActivity()
            }
        }
    }

    fun recordDuaActivity() {
        viewModelScope.launch {
            repository.recordDuaActivity()
        }
    }

    fun recordTasbihActivity() {
        viewModelScope.launch {
            repository.recordTasbihActivity()
        }
    }

    fun useStreakFreeze() {
        viewModelScope.launch {
            val success = repository.useStreakFreeze()
            if (success) {
                triggerHaptic()
                showToast("Streak protected! Monthly freeze pass applied.")
            } else {
                showToast("No streak passes available this month.")
            }
        }
    }

    private var countdownJob: Job? = null
    private var audioSimulationJob: Job? = null

    init {
        try {
            if (!sharedPrefs.getBoolean("devotion_fresh_v3_reset", false)) {
                sharedPrefs.edit()
                    .putInt("quran_ayahs_read_today", 0)
                    .putInt("azkar_count_today", 0)
                    .putBoolean("devotion_fresh_v3_reset", true)
                    .apply()
                quranAyahsReadToday.value = 0
                azkarCountToday.value = 0
            }
        } catch (_: Exception) {}
        AyahSplitRepository.init(application)
        NoorNotificationHelper.createNotificationChannels(application)
        SpiritualAlarmScheduler.rescheduleAll(application)
        hasSeenHomeTutorial.value = sharedPrefs.getBoolean("has_seen_home_tutorial", false)
        hasSeenOnboarding.value = sharedPrefs.getBoolean("has_seen_onboarding_v4_complete", false)
        showArabicInAzkarCards.value = sharedPrefs.getBoolean("show_arabic_in_azkar_cards", true)
        azkarTextSize.value = sharedPrefs.getString("azkar_text_size", "Small") ?: "Small"
        isAzkarAutoScrollEnabled.value = sharedPrefs.getBoolean("azkar_auto_scroll", true)
        isAzkarHapticEnabled.value = sharedPrefs.getBoolean("azkar_haptic", true)
        showAzkarTransliteration.value = sharedPrefs.getBoolean("azkar_transliteration", false)
        showAzkarBenefits.value = sharedPrefs.getBoolean("azkar_benefits", true)
        val savedFontId = sharedPrefs.getString("quran_arabic_font", QuranArabicFont.AMIRI.id)
        selectedArabicFont.value = QuranArabicFont.fromId(savedFontId)

        val savedZoneId = sharedPrefs.getString("selected_prayer_zone_id", null)
        var isLocConfigured = sharedPrefs.getBoolean("is_location_configured", false)
        if (!isLocConfigured && savedZoneId != null) {
            isLocConfigured = true
            sharedPrefs.edit().putBoolean("is_location_configured", true).apply()
        }
        isLocationConfigured.value = isLocConfigured
        if (isLocConfigured && savedZoneId != null) {
            val foundZone = repository.prayerZones.find { it.id == savedZoneId }
            if (foundZone != null) {
                selectedPrayerZone.value = foundZone
                locationName.value = "${foundZone.name}, ${foundZone.country}"
            }
        } else if (!isLocConfigured) {
            val isAr = appLanguage.value.equals("Arabic", ignoreCase = true) ||
                    appLanguage.value == "العربية" ||
                    appLanguage.value.startsWith("ar", ignoreCase = true)
            locationName.value = if (isAr) "الموقع غير مفعّل" else "Location is Off"
        }
        val savedAuthId = sharedPrefs.getString("selected_calc_authority_id", null)
        if (savedAuthId != null) {
            val foundAuth = repository.calculationAuthorities.find { it.id == savedAuthId }
            if (foundAuth != null) {
                selectedAuthority.value = foundAuth
            }
        }
        isHanafiAsr.value = sharedPrefs.getBoolean("is_hanafi_asr", false)
        qiyamReminderOffsetMinutes.value = sharedPrefs.getInt("qiyam_reminder_offset_minutes", -45)
        val loadedOffsets = mutableMapOf<String, Int>()
        listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha").forEach { pName ->
            loadedOffsets[pName] = sharedPrefs.getInt("manual_offset_$pName", 0)
        }
        prayerManualMinuteOffsets.value = loadedOffsets

        val loadedAlertTypes = mutableMapOf<String, String>()
        val loadedEnabled = mutableMapOf<String, Boolean>()
        val loadedTimers = mutableMapOf<String, Int>()

        listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha").forEach { pName ->
            val defaultAlert = if (pName == "Sunrise") "Mute" else "Adhan"
            val alertType = sharedPrefs.getString("alert_type_$pName", defaultAlert) ?: defaultAlert
            loadedAlertTypes[pName] = alertType

            val defaultTimer = if (pName == "Fajr") -15 else 0
            val timer = sharedPrefs.getInt("notification_timer_$pName", defaultTimer)
            loadedTimers[pName] = timer

            loadedEnabled[pName] = (alertType != "Mute")
        }
        prayerAlertTypes.value = loadedAlertTypes
        prayerNotificationTimers.value = loadedTimers
        prayerNotificationEnabled.value = loadedEnabled

        val loadedAdhanSounds = mutableMapOf<String, String>()
        listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha").forEach { pName ->
            val defaultSound = AdhanAudioRepository.getDefaultSoundIdForPrayer(pName)
            val soundId = sharedPrefs.getString("adhan_sound_$pName", defaultSound) ?: defaultSound
            loadedAdhanSounds[pName] = soundId
        }
        perPrayerAdhanSounds.value = loadedAdhanSounds
        val initialSound = AdhanAudioRepository.getSoundById(application, globalAdhanSoundId.value)
        athanSoundName.value = initialSound.title

        loadSavedHomeWidgetsConfig()
        loadSavedQuickAccessToolsConfig()
        refreshDownloadedSurahs()

        // Rehydrate all user settings & options to ensure zero state regression across app restarts
        try {
            val userPrefs = application.getSharedPreferences("noor_user_prefs", Context.MODE_PRIVATE)
            val savedUserName = userPrefs.getString("user_name", null)
            if (!savedUserName.isNullOrBlank()) {
                userName.value = savedUserName
            }
        } catch (e: Exception) {}

        showArabicSecondaryText.value = sharedPrefs.getBoolean("show_arabic_secondary_text", true)
        showTranslation.value = sharedPrefs.getBoolean("quran_show_translation", true)
        showTransliteration.value = sharedPrefs.getBoolean("quran_show_transliteration", true)
        arabicFontSizeSp.value = sharedPrefs.getInt("quran_arabic_font_size", 24)
        val savedReciterId = sharedPrefs.getString("selected_reciter_id", null)
        if (savedReciterId != null) {
            QuranData.reciters.find { it.id == savedReciterId }?.let { selectedReciter.value = it }
        }
        audioPlaybackSpeed.value = sharedPrefs.getFloat("audio_playback_speed", 1.0f)
        isAudioRepeatOne.value = sharedPrefs.getBoolean("is_audio_repeat_one", false)
        isAutoAdvanceAyah.value = sharedPrefs.getBoolean("is_auto_advance_ayah", true)
        hijriAdjustmentDays.value = sharedPrefs.getInt("hijri_adjustment_days", 0)
        selectedMood.value = sharedPrefs.getString("selected_daily_mood", "Anxious") ?: "Anxious"
        val savedMaskStyle = sharedPrefs.getString("hifz_masking_style", null)
        if (savedMaskStyle != null) {
            try {
                memorizationMaskingStyle.value = HifzMaskingStyle.valueOf(savedMaskStyle)
            } catch (e: Exception) {}
        }
        memorizationRepeatCount.value = sharedPrefs.getInt("memorization_repeat_count", 1)
        memorizationWordRepeatCount.value = sharedPrefs.getInt("hifz_word_repeat_count", 1)
        memorizationWordDelaySeconds.value = sharedPrefs.getInt("hifz_word_delay_seconds", 1)

        viewModelScope.launch {
            val savedCompleted = repository.getCompletedPrayersForFajrDay(selectedPrayerZone.value)
            _completedPrayers.value = savedCompleted
            refreshPrayerTimes()
            schedulePrayerAlarms()
            startRealtimeCountdown()
        }

        viewModelScope.launch {
            repository.preloadQuranIfNeeded(application)
            repository.initDefaultHabitsIfEmpty()
            repository.initDefaultStreaksIfEmpty()

            // Load initial surah with real canonical verses from Room
            val initialSurah = repository.getSurahWithVerses(1)
            if (initialSurah.verses.isNotEmpty()) {
                selectedSurahForReading.value = initialSurah
                memorizationSurah.value = initialSurah
            }
        }

        // Low-priority background coroutine to prewarm Mushaf cache on app startup
        viewModelScope.launch(Dispatchers.Default) {
            delay(500)
            try {
                repository.preloadQuranIfNeeded(application)
                val progress = repository.readingProgress.firstOrNull()
                val targetSurahNum = progress?.surahNumber ?: 1
                val surah = repository.getSurahWithVerses(targetSurahNum)
                if (surah.verses.isNotEmpty()) {
                    val isTajweed = sharedPrefs.getBoolean("is_tajweed_enabled", false)
                    val isSepia = sharedPrefs.getBoolean("is_quran_sepia_mode", false)
                    val savedTheme = sharedPrefs.getString("shared_reading_theme", "Madani Crisp")
                    val themeColors = if (isSepia) {
                        com.example.ui.theme.ReadingThemes.SepiaParchment
                    } else if (savedTheme == "Obsidian Night" || savedTheme == "Dark") {
                        com.example.ui.theme.ReadingThemes.ObsidianNight
                    } else {
                        com.example.ui.theme.ReadingThemes.MadaniCrisp
                    }

                    com.example.ui.quran.MushafTextCache.getOrCreate(
                        surah = surah,
                        isTajweedEnabled = isTajweed,
                        themeColors = themeColors
                    )
                }
            } catch (_: Exception) {
                // Ignore background prewarm failure
            }
        }
    }

    fun showToast(msg: String) {
        toastMessage.value = msg
        viewModelScope.launch {
            delay(3000)
            if (toastMessage.value == msg) {
                toastMessage.value = null
            }
        }
    }

    fun refreshPrayerTimes() {
        if (!isLocationConfigured.value) {
            val dummyPrayers = listOf(
                Pair("Fajr", "الفجر"),
                Pair("Sunrise", "الشروق"),
                Pair("Dhuhr", "الظهر"),
                Pair("Asr", "العصر"),
                Pair("Maghrib", "المغرب"),
                Pair("Isha", "العشاء")
            ).mapIndexed { idx, pair ->
                PrayerTime(
                    name = pair.first,
                    arabicName = pair.second,
                    timeString = "--:--",
                    hour = 0,
                    minute = 0,
                    isNext = idx == 0,
                    isPast = false,
                    isCurrent = false,
                    isCompleted = false
                )
            }
            _prayerTimes.value = dummyPrayers
            _nextPrayerName.value = "Salat"
            _nextPrayerTimeStr.value = "--:--"
            _nextPrayerCountdown.value = "--:--"
            supplementaryPrayerTimes.value = null
            val isAr = appLanguage.value.equals("Arabic", ignoreCase = true) ||
                    appLanguage.value == "العربية" ||
                    appLanguage.value.startsWith("ar", ignoreCase = true)
            locationName.value = if (isAr) "الموقع غير مفعّل" else "Location is Off"
            com.example.widget.PrayerWidgetUpdater.updateAsync(getApplication())
            return
        }

        val list = repository.calculatePrayerTimes(
            zone = selectedPrayerZone.value,
            authority = selectedAuthority.value,
            isHanafiAsr = isHanafiAsr.value,
            minuteOffsets = prayerManualMinuteOffsets.value
        )
        val completed = _completedPrayers.value
        _prayerTimes.value = list.map { pt ->
            pt.copy(isCompleted = completed.contains(pt.name))
        }

        val next = list.firstOrNull { it.isNext } ?: list.firstOrNull()
        if (next != null) {
            _nextPrayerName.value = next.name
            _nextPrayerTimeStr.value = next.timeString
        }

        supplementaryPrayerTimes.value = repository.calculateSupplementaryTimes(
            zone = selectedPrayerZone.value,
            authority = selectedAuthority.value,
            isHanafiAsr = isHanafiAsr.value,
            minuteOffsets = prayerManualMinuteOffsets.value
        )

        // Keep Home Screen Glance Widget updated in real-time
        com.example.widget.PrayerWidgetUpdater.updateAsync(getApplication())
    }

    private fun startRealtimeCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var lastFajrDateKey = repository.getFajrDayDateString(selectedPrayerZone.value)
            var lastMinute = -1

            while (true) {
                if (!isLocationConfigured.value) {
                    _nextPrayerCountdown.value = "--:--"
                    _nextPrayerTimeStr.value = "--:--"
                    delay(1000)
                    continue
                }

                val tz = java.util.TimeZone.getTimeZone(selectedPrayerZone.value.timeZoneId)
                val now = Calendar.getInstance(tz)
                val currentHour = now.get(Calendar.HOUR_OF_DAY)
                val currentMin = now.get(Calendar.MINUTE)
                val currentSec = now.get(Calendar.SECOND)

                // Refresh prayer times schedule when minute changes or rollover occurs
                if (currentMin != lastMinute) {
                    lastMinute = currentMin
                    refreshPrayerTimes()

                    // Check for Fajr day rollover (Fajr-to-Fajr boundary)
                    val currentFajrDateKey = repository.getFajrDayDateString(selectedPrayerZone.value)
                    if (currentFajrDateKey != lastFajrDateKey) {
                        lastFajrDateKey = currentFajrDateKey
                        val freshCompleted = repository.getCompletedPrayersForFajrDay(selectedPrayerZone.value)
                        _completedPrayers.value = freshCompleted
                        refreshPrayerTimes()
                    }
                }

                val currentPrayers = _prayerTimes.value
                val nextPrayer = currentPrayers.firstOrNull { it.isNext } ?: currentPrayers.firstOrNull()

                if (nextPrayer != null) {
                    val targetHour = nextPrayer.hour
                    val targetMin = nextPrayer.minute

                    var diffSec = (targetHour * 3600 + targetMin * 60) - (currentHour * 3600 + currentMin * 60 + currentSec)
                    if (diffSec < 0) {
                        // Next day Fajr
                        diffSec += 24 * 3600
                    }

                    val hours = diffSec / 3600
                    val mins = (diffSec % 3600) / 60
                    val secs = diffSec % 60

                    _nextPrayerCountdown.value = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, mins, secs)
                    _nextPrayerName.value = nextPrayer.name
                    _nextPrayerTimeStr.value = nextPrayer.timeString
                }

                delay(1000)
            }
        }
    }

    fun togglePrayerCompleted(prayer: PrayerTime) {
        // Sunrise is not an obligatory salat and cannot be checked
        if (prayer.name.equals("Sunrise", ignoreCase = true) || prayer.name.equals("الشروق", ignoreCase = true)) {
            return
        }

        // Enforce time-gated rule: prayer can only be checked once its time has actually started (past or current)
        // Prayers do NOT need to be checked in order.
        if (!prayer.isPast && !prayer.isCurrent) {
            showToast("Prayer time hasn't started yet.")
            return
        }

        val current = _completedPrayers.value.toMutableSet()
        val willBeCompleted = !current.contains(prayer.name)
        if (willBeCompleted) {
            current.add(prayer.name)
            triggerHaptic()
            showToast("${prayer.name} marked as completed. Baraka Allahu feek!")
        } else {
            current.remove(prayer.name)
            triggerHaptic()
            showToast("${prayer.name} unmarked.")
        }
        _completedPrayers.value = current
        viewModelScope.launch {
            repository.setPrayerCompleted(prayer.name, willBeCompleted, selectedPrayerZone.value)
            refreshPrayerTimes()
        }
    }

    // Daily Mood & Wisdom Actions
    fun selectMood(mood: String) {
        selectedMood.value = mood
        sharedPrefs.edit().putString("selected_daily_mood", mood).apply()
    }

    fun getCurrentMoodWisdom(): DailyMoodWisdom {
        val pair = DuaData.moodWisdomMap[selectedMood.value] ?: DuaData.moodWisdomMap["Anxious"]!!
        return if (isIslamicWisdomMode.value) pair.first else pair.second
    }

    // Bookmark / Favorite
    fun toggleBookmark(type: String, title: String, arabic: String, translation: String, source: String) {
        viewModelScope.launch {
            repository.toggleFavorite(type, title, arabic, translation, source)
            showToast("Updated favorites!")
        }
    }

    fun toggleQuranBookmark(
        surahNumber: Int,
        verseNumber: Int,
        surahName: String = "",
        surahNameArabic: String = "",
        arabicText: String = "",
        translation: String = "",
        juz: Int = 1,
        page: Int = 1
    ) {
        viewModelScope.launch {
            val isBookmarked = repository.isQuranBookmarkedOnce(surahNumber, verseNumber)
            repository.toggleQuranBookmark(
                surahNumber = surahNumber,
                verseNumber = verseNumber,
                surahName = surahName,
                surahNameArabic = surahNameArabic,
                arabicText = arabicText,
                translation = translation,
                juz = juz,
                page = page
            )
            if (!isBookmarked) {
                addQuranAyahsReadToday(1)
            }
            triggerHaptic()
            showToast(if (isBookmarked) "Bookmark removed" else "Ayah $surahNumber:$verseNumber bookmarked")
        }
    }

    fun removeQuranBookmarkById(id: Long) {
        viewModelScope.launch {
            repository.removeQuranBookmarkById(id)
            triggerHaptic()
            showToast("Bookmark deleted")
        }
    }

    fun toggleSurahFavorite(surah: Surah) {
        viewModelScope.launch {
            val title = "Surah ${surah.nameEnglish}"
            val existing = favorites.value.filter { fav ->
                fav.title.equals(title, ignoreCase = true) ||
                        fav.arabicText == surah.nameArabic ||
                        fav.source == "Surah ${surah.number}"
            }
            if (existing.isNotEmpty()) {
                existing.forEach { repository.removeFavorite(it) }
                triggerHaptic()
                showToast("Removed Surah ${surah.nameEnglish.substringBefore(" (")} from favorites")
            } else {
                repository.toggleFavorite(
                    type = "SURAH",
                    title = title,
                    arabicText = surah.nameArabic,
                    translation = "${surah.englishMeaning} • ${surah.totalVerses} Verses",
                    source = "Surah ${surah.number}"
                )
                triggerHaptic()
                showToast("Added Surah ${surah.nameEnglish.substringBefore(" (")} to favorites")
            }
        }
    }

    fun removeFavorite(item: FavoriteItemEntity) {
        viewModelScope.launch {
            repository.removeFavorite(item)
            showToast("Removed from favorites")
        }
    }



    // Digital Tasbih Methods
    fun incrementTasbih() {
        val current = tasbihCount.value
        val target = tasbihTarget.value
        val newCount = current + 1
        val newTotal = tasbihTotalAllTime.value + 1
        tasbihTotalAllTime.value = newTotal
        sharedPrefs.edit().putInt("tasbih_total_all_time", newTotal).apply()

        if (isTasbihSoundEnabled.value) {
            playTasbihClickSound()
        }
        
        if (isTasbihHapticEnabled.value) {
            triggerHaptic()
        }

        if (newCount >= target) {
            val newLaps = tasbihLapsCompleted.value + 1
            tasbihLapsCompleted.value = newLaps
            sharedPrefs.edit().putInt("tasbih_laps_completed", newLaps).apply()
            triggerCompletionHaptic()
            showToast("SubhanAllah! Completed target of $target.")
            if (isTasbihAutoReset.value) {
                tasbihCount.value = 0
            } else {
                tasbihCount.value = newCount
            }
        } else {
            tasbihCount.value = newCount
        }

        viewModelScope.launch {
            repository.updateTasbih(selectedDhikr.value, tasbihCount.value, tasbihTarget.value)
            repository.recordTasbihActivity()
        }
    }

    private fun playTasbihClickSound() {
        try {
            val audioManager = getApplication<Application>().getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
            audioManager?.playSoundEffect(android.media.AudioManager.FX_KEY_CLICK, 0.4f)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun resetTasbih() {
        tasbihCount.value = 0
        viewModelScope.launch {
            repository.resetTasbih(selectedDhikr.value, tasbihTarget.value)
            showToast("Tasbih counter reset")
        }
    }

    fun resetAllTasbihStats() {
        tasbihCount.value = 0
        tasbihLapsCompleted.value = 0
        tasbihTotalAllTime.value = 0
        sharedPrefs.edit()
            .putInt("tasbih_laps_completed", 0)
            .putInt("tasbih_total_all_time", 0)
            .apply()
        showToast("All Tasbih lifetime statistics reset")
    }

    fun setTasbihTarget(target: Int) {
        tasbihTarget.value = target
        tasbihCount.value = 0
        sharedPrefs.edit().putInt("tasbih_target", target).apply()
    }

    fun setDhikr(
        dhikrTitle: String,
        arabic: String = "",
        meaning: String = "",
        target: Int = 33,
        virtue: String = "",
        audioUrl: String = "",
        isCustom: Boolean = false
    ) {
        selectedDhikr.value = dhikrTitle
        if (arabic.isNotBlank()) selectedDhikrArabic.value = arabic
        if (meaning.isNotBlank()) selectedDhikrMeaning.value = meaning
        if (virtue.isNotBlank()) selectedDhikrVirtue.value = virtue
        selectedDhikrAudioUrl.value = audioUrl
        isSelectedDhikrCustom.value = isCustom
        tasbihTarget.value = target
        tasbihCount.value = 0
        sharedPrefs.edit()
            .putString("tasbih_dhikr_title", dhikrTitle)
            .putString("tasbih_dhikr_arabic", arabic)
            .putString("tasbih_dhikr_meaning", meaning)
            .putString("tasbih_dhikr_virtue", virtue)
            .putString("tasbih_dhikr_audio_url", audioUrl)
            .putBoolean("tasbih_dhikr_is_custom", isCustom)
            .putInt("tasbih_target", target)
            .apply()
    }

    fun setTasbihTransliterationVisible(enabled: Boolean) {
        isTasbihTransliterationVisible.value = enabled
        sharedPrefs.edit().putBoolean("tasbih_transliteration_visible", enabled).apply()
    }

    fun setTasbihTranslationVisible(enabled: Boolean) {
        isTasbihTranslationVisible.value = enabled
        sharedPrefs.edit().putBoolean("tasbih_translation_visible", enabled).apply()
    }

    fun setTasbihTheme(theme: String) {
        tasbihVisualTheme.value = theme
        sharedPrefs.edit().putString("tasbih_visual_theme", theme).apply()
    }

    fun setTasbihMarbleStyle(style: String) {
        tasbihMarbleStyle.value = style
        sharedPrefs.edit().putString("tasbih_marble_style", style).apply()
        triggerHaptic()
    }

    fun setTasbihHaptic(enabled: Boolean) {
        isTasbihHapticEnabled.value = enabled
        sharedPrefs.edit().putBoolean("tasbih_haptic_enabled", enabled).apply()
        if (enabled) triggerHaptic()
    }

    fun setTasbihSound(enabled: Boolean) {
        isTasbihSoundEnabled.value = enabled
        sharedPrefs.edit().putBoolean("tasbih_sound_enabled", enabled).apply()
    }

    fun setTasbihAutoReset(enabled: Boolean) {
        isTasbihAutoReset.value = enabled
        sharedPrefs.edit().putBoolean("tasbih_auto_reset", enabled).apply()
    }

    fun setTasbihBeadsVisible(enabled: Boolean) {
        isTasbihBeadsVisible.value = enabled
        sharedPrefs.edit().putBoolean("tasbih_beads_visible", enabled).apply()
    }

    // Du'as & Azkar Methods
    fun openDuaCategory(category: String) {
        selectedDuaCategory.value = category
        navigateTo(NoorDestination.AZKAR_READER)
    }

    fun setShowArabicInAzkarCards(enabled: Boolean) {
        showArabicInAzkarCards.value = enabled
        sharedPrefs.edit().putBoolean("show_arabic_in_azkar_cards", enabled).apply()
    }

    fun setAzkarTextSize(size: String) {
        azkarTextSize.value = size
        sharedPrefs.edit().putString("azkar_text_size", size).apply()
    }

    fun setAzkarAutoScroll(enabled: Boolean) {
        isAzkarAutoScrollEnabled.value = enabled
        sharedPrefs.edit().putBoolean("azkar_auto_scroll", enabled).apply()
    }

    fun setAzkarHaptic(enabled: Boolean) {
        isAzkarHapticEnabled.value = enabled
        sharedPrefs.edit().putBoolean("azkar_haptic", enabled).apply()
    }

    fun setAzkarTransliteration(enabled: Boolean) {
        showAzkarTransliteration.value = enabled
        sharedPrefs.edit().putBoolean("azkar_transliteration", enabled).apply()
    }

    fun setAzkarBenefits(enabled: Boolean) {
        showAzkarBenefits.value = enabled
        sharedPrefs.edit().putBoolean("azkar_benefits", enabled).apply()
    }

    fun setAzkarSettingsOpen(isOpen: Boolean) {
        isAzkarSettingsOpen.value = isOpen
    }

    fun getRemainingDuaCount(dua: DuaItem): Int {
        val map = azkarRemainingCounts.value
        return map[dua.id] ?: dua.repeatCount
    }

    fun decrementDuaCount(dua: DuaItem, onCompleted: (() -> Unit)? = null) {
        val currentRemaining = getRemainingDuaCount(dua)
        if (currentRemaining > 0) {
            val updated = currentRemaining - 1
            val newMap = azkarRemainingCounts.value.toMutableMap()
            newMap[dua.id] = updated
            azkarRemainingCounts.value = newMap
            if (isAzkarHapticEnabled.value) {
                triggerHaptic()
            }

            if (updated == 0) {
                if (dua.category.contains("Azkar", ignoreCase = true) || dua.category.contains("Adhkar", ignoreCase = true)) {
                    recordAzkarActivity()
                } else {
                    recordDuaActivity()
                }
                if (isAzkarHapticEnabled.value) {
                    triggerCompletionHaptic()
                }
                showToast("Completed: ${dua.title} ✓")
                onCompleted?.invoke()
            }
        }
    }

    fun resetDuaCount(dua: DuaItem) {
        val newMap = azkarRemainingCounts.value.toMutableMap()
        newMap[dua.id] = dua.repeatCount
        azkarRemainingCounts.value = newMap
    }

    fun resetCategoryDuaCounts(category: String) {
        val duasInCategory = DuaData.categorizedDuas.filter { it.category.equals(category, ignoreCase = true) }
        val newMap = azkarRemainingCounts.value.toMutableMap()
        duasInCategory.forEach { dua ->
            newMap[dua.id] = dua.repeatCount
        }
        azkarRemainingCounts.value = newMap
        showToast("All counts in $category reset")
    }

    // ============================================================
    // QURAN AUDIO PLAYER (FULL SURAH & AYAH MP3 STREAMING VIA MEDIAPLAYER)
    // ============================================================

    fun getSurahAudioUrl(surahNumber: Int, reciter: Reciter = selectedReciter.value): String {
        val surahFormatted = String.format(Locale.US, "%03d", surahNumber)
        return when (reciter.id) {
            "abdulbasit" -> "https://server7.mp3quran.net/basit/$surahFormatted.mp3"
            "sudais" -> "https://server11.mp3quran.net/sds/$surahFormatted.mp3"
            "muaiqly" -> "https://server12.mp3quran.net/maher/$surahFormatted.mp3"
            "ghamdi" -> "https://server7.mp3quran.net/ghamdi/$surahFormatted.mp3"
            "shatri" -> "https://server11.mp3quran.net/shatri/$surahFormatted.mp3"
            "minshawi" -> "https://server10.mp3quran.net/minsh/$surahFormatted.mp3"
            "husary" -> "https://server13.mp3quran.net/husr/$surahFormatted.mp3"
            else -> "https://server8.mp3quran.net/afs/$surahFormatted.mp3"
        }
    }

    fun getAyahAudioUrl(surahNumber: Int, ayahNumber: Int, reciter: Reciter = selectedReciter.value): String {
        // If ayahNumber is 0 (Basmala prelude), point to 001001.mp3 (Surah 1 Ayah 1 Bismillah)
        val (sNum, aNum) = if (ayahNumber == 0) Pair(1, 1) else Pair(surahNumber, ayahNumber)
        val surahFormatted = String.format(Locale.US, "%03d", sNum)
        val ayahFormatted = String.format(Locale.US, "%03d", aNum)
        return when (reciter.id) {
            "abdulbasit" -> "https://everyayah.com/data/Abdul_Basit_Murattal_192kbps/$surahFormatted$ayahFormatted.mp3"
            "sudais" -> "https://everyayah.com/data/Abdurrahmaan_As-Sudais_192kbps/$surahFormatted$ayahFormatted.mp3"
            "muaiqly" -> "https://everyayah.com/data/MaherAlMuaiqly128kbps/$surahFormatted$ayahFormatted.mp3"
            "ghamdi" -> "https://everyayah.com/data/Ghamadi_40kbps/$surahFormatted$ayahFormatted.mp3"
            "shatri" -> "https://everyayah.com/data/Abu_Bakr_Ash-Shaatree_128kbps/$surahFormatted$ayahFormatted.mp3"
            "minshawi" -> "https://everyayah.com/data/Minshawy_Murattal_128kbps/$surahFormatted$ayahFormatted.mp3"
            "husary" -> "https://everyayah.com/data/Husary_128kbps/$surahFormatted$ayahFormatted.mp3"
            else -> "https://everyayah.com/data/Alafasy_128kbps/$surahFormatted$ayahFormatted.mp3"
        }
    }

    fun playSurahAudio(surah: Surah, startVerse: Int = 1, openPlayer: Boolean = true) {
        isAyahAudioMode.value = false
        currentPlayingSurah.value = surah
        currentPlayingVerse.value = startVerse

        val audioUrl = getSurahAudioUrl(surah.number, selectedReciter.value)
        val audioSource = QuranAudioCacheManager.getSurahAudioSource(
            getApplication(),
            surah.number,
            selectedReciter.value.id,
            audioUrl
        )

        isAudioBuffering.value = true
        isAudioPlaying.value = true
        audioProgress.value = 0f
        audioCurrentPositionMs.value = 0

        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                }
            } else {
                mediaPlayer?.reset()
            }

            mediaPlayer?.apply {
                setDataSource(audioSource)
                setOnPreparedListener { mp ->
                    isAudioBuffering.value = false
                    isAudioPlaying.value = true
                    val dur = mp.duration.coerceAtLeast(1)
                    audioDurationMs.value = dur
                    mp.start()
                    startAudioProgressTracker()
                }

                setOnCompletionListener {
                    if (isAyahAudioMode.value) {
                        onAyahAudioCompleted()
                    } else {
                        onSurahAudioCompleted()
                    }
                }

                setOnErrorListener { _, _, _ ->
                    isAudioBuffering.value = false
                    isAudioPlaying.value = false
                    val isLocalFile = audioSource.startsWith("/") || java.io.File(audioSource).exists()
                    if (isLocalFile) {
                        showToast("Error playing downloaded audio file.")
                    } else {
                        isAudioNetworkError.value = true
                        showToast("Network connection error streaming audio.")
                    }
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            isAudioBuffering.value = false
            isAudioPlaying.value = false
            showToast("Unable to stream audio: ${e.localizedMessage}")
        }

        if (openPlayer) {
            navigateTo(NoorDestination.QURAN_AUDIO_STREAM)
        }
    }

    fun playAyah(
        surah: Surah,
        ayahNumber: Int,
        openPlayer: Boolean = false,
        playBasmalaFirst: Boolean = true
    ) {
        isAyahAudioMode.value = true
        currentPlayingSurah.value = surah

        // If starting recitation from ayah 1 for any surah except Al-Fatihah (1) and At-Tawbah (9),
        // recite the separate Basmala audio first.
        val shouldPlayBasmala = playBasmalaFirst && ayahNumber == 1 && surah.number != 1 && surah.number != 9
        val effectiveAyahNumber = if (shouldPlayBasmala) 0 else ayahNumber
        currentPlayingVerse.value = effectiveAyahNumber

        // Progressive reveal in memorization mode: reveal on first play
        if (effectiveAyahNumber > 0) {
            revealVerse(effectiveAyahNumber)
        }

        val audioUrl = getAyahAudioUrl(surah.number, effectiveAyahNumber, selectedReciter.value)
        val audioSource = QuranAudioCacheManager.getAudioSource(
            getApplication(),
            if (effectiveAyahNumber == 0) 1 else surah.number,
            if (effectiveAyahNumber == 0) 1 else effectiveAyahNumber,
            selectedReciter.value.id,
            audioUrl
        )

        isAudioBuffering.value = true
        isAudioPlaying.value = true

        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                }
            } else {
                mediaPlayer?.reset()
            }

            mediaPlayer?.apply {
                setDataSource(audioSource)
                setOnPreparedListener { mp ->
                    isAudioBuffering.value = false
                    isAudioPlaying.value = true
                    val dur = mp.duration.coerceAtLeast(1)
                    audioDurationMs.value = dur
                    mp.start()
                    startAudioProgressTracker()
                }

                setOnCompletionListener {
                    if (isAyahAudioMode.value) {
                        onAyahAudioCompleted()
                    } else {
                        onSurahAudioCompleted()
                    }
                }

                setOnErrorListener { _, _, _ ->
                    isAudioBuffering.value = false
                    isAudioPlaying.value = false
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            isAudioBuffering.value = false
            isAudioPlaying.value = false
        }

        if (openPlayer) {
            navigateTo(NoorDestination.QURAN_AUDIO_STREAM)
        }
    }

    fun playAyah(surahNumber: Int, ayahNumber: Int) {
        val surah = QuranData.surahs.firstOrNull { it.number == surahNumber }
            ?: QuranData.completeSurahList.firstOrNull { it.number == surahNumber }
            ?: currentPlayingSurah.value
        playAyah(surah, ayahNumber, openPlayer = false, playBasmalaFirst = true)
    }

    private var memorizationDelayJob: Job? = null

    private fun onAyahAudioCompleted() {
        val surah = currentPlayingSurah.value
        val currentAyah = currentPlayingVerse.value
        val totalAyahs = if (surah.verses.isNotEmpty()) surah.verses.size else surah.totalVerses

        if (isAudioRepeatOne.value) {
            playAyah(surah, currentAyah, openPlayer = false, playBasmalaFirst = false)
            return
        }

        if (currentAyah == 0) {
            // Basmala just finished reciting -> proceed to start ayah
            val targetStart = if (currentDestination.value == NoorDestination.QURAN_MEMORIZATION) {
                memorizationStartAyah.value
            } else {
                1
            }
            playAyah(surah, targetStart, openPlayer = false, playBasmalaFirst = false)
            return
        }

        // Check if playing within the dedicated Quran Memorization Studio
        if (currentDestination.value == NoorDestination.QURAN_MEMORIZATION) {
            if (ayahPatternJob?.isActive == true || warmUpJob?.isActive == true) {
                isAudioPlaying.value = false
                isAudioBuffering.value = false
                currentAyahCompletionCallback?.invoke()
                return
            }

            val startRange = memorizationStartAyah.value
            val endRange = memorizationEndAyah.value.coerceAtMost(totalAyahs)
            val repeats = memorizationRepeatCount.value
            val delaySec = memorizationDelaySeconds.value

            val executeNextPlayback = { nextAyah: Int ->
                if (delaySec > 0) {
                    memorizationDelayJob?.cancel()
                    memorizationDelayJob = viewModelScope.launch {
                        memorizationDelayActive.value = true
                        for (i in delaySec downTo 1) {
                            memorizationDelayCountdown.value = i
                            kotlinx.coroutines.delay(1000)
                        }
                        memorizationDelayActive.value = false
                        memorizationDelayCountdown.value = 0
                        playAyah(surah, nextAyah, openPlayer = false, playBasmalaFirst = false)
                    }
                } else {
                    playAyah(surah, nextAyah, openPlayer = false, playBasmalaFirst = false)
                }
            }

            if (repeats > 1 && memorizationCurrentRepetition.value < repeats) {
                memorizationCurrentRepetition.value += 1
                executeNextPlayback(currentAyah)
            } else if (currentAyah < endRange) {
                memorizationCurrentRepetition.value = 1
                val nextAyah = currentAyah + 1
                executeNextPlayback(nextAyah)
            } else {
                // Completed the range with all repetitions
                memorizationCurrentRepetition.value = 1
                if (memorizationLoopRange.value) {
                    executeNextPlayback(startRange)
                } else {
                    isAudioPlaying.value = false
                    triggerHaptic()
                    showToast("Masha'Allah! Completed drill for Ayahs $startRange–$endRange")
                }
            }
            return
        }

        // Standard reading / reader progression
        if (memorizationRepeatCount.value > 1 && memorizationCurrentRepetition.value < memorizationRepeatCount.value) {
            // Memorization repeat drill in standard reader
            memorizationCurrentRepetition.value += 1
            playAyah(surah, currentAyah, openPlayer = false, playBasmalaFirst = false)
        } else if (isAutoAdvanceAyah.value && currentAyah < totalAyahs) {
            memorizationCurrentRepetition.value = 1
            val nextAyah = currentAyah + 1
            playAyah(surah, nextAyah, openPlayer = false, playBasmalaFirst = false)
        } else if (isAutoAdvanceAyah.value && surah.number < 114) {
            memorizationCurrentRepetition.value = 1
            val nextSurah = QuranData.surahs.firstOrNull { it.number == surah.number + 1 }
                ?: QuranData.completeSurahList.firstOrNull { it.number == surah.number + 1 }
            if (nextSurah != null) {
                currentPlayingSurah.value = nextSurah
                playAyah(nextSurah, 1, openPlayer = false, playBasmalaFirst = true)
                showToast("Now reciting: Surah ${nextSurah.nameEnglish}")
            } else {
                isAudioPlaying.value = false
            }
        } else {
            memorizationCurrentRepetition.value = 1
            isAudioPlaying.value = false
        }
    }

    fun isSurahDownloaded(surahNumber: Int, reciterId: String = selectedReciter.value.id): Boolean {
        return QuranAudioCacheManager.isSurahAudioCached(getApplication(), surahNumber, reciterId) ||
                downloadedSurahs.value.contains("${reciterId}_${surahNumber}")
    }

    fun downloadSurahOffline(surah: Surah, reciter: Reciter = selectedReciter.value) {
        val key = "${reciter.id}_${surah.number}"
        if (isSurahDownloaded(surah.number, reciter.id)) {
            showToast("Surah ${surah.nameEnglish} is already cached for offline listening.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val progressMap = isSurahDownloading.value.toMutableMap()
            progressMap[key] = 0.05f
            isSurahDownloading.value = progressMap
            showToast("Downloading Surah ${surah.nameEnglish} (${reciter.name})...")

            val url = getSurahAudioUrl(surah.number, reciter)
            val success = QuranAudioCacheManager.downloadSurahAudio(
                getApplication(),
                surah.number,
                reciter.id,
                url
            ) { prog ->
                val currentMap = isSurahDownloading.value.toMutableMap()
                currentMap[key] = prog
                isSurahDownloading.value = currentMap
            }

            val finishMap = isSurahDownloading.value.toMutableMap()
            finishMap.remove(key)
            isSurahDownloading.value = finishMap

            if (success) {
                val newSet = downloadedSurahs.value.toMutableSet()
                newSet.add(key)
                downloadedSurahs.value = newSet
                triggerCompletionHaptic()
                showToast("✓ Surah ${surah.nameEnglish} saved for offline listening")
            } else {
                showToast("Could not download audio. Check connection.")
            }
        }
    }

    fun deleteSurahOffline(surah: Surah, reciter: Reciter = selectedReciter.value) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = QuranAudioCacheManager.getSurahFile(getApplication(), surah.number, reciter.id)
            if (file.exists()) {
                file.delete()
            }
            val key = "${reciter.id}_${surah.number}"
            val newSet = downloadedSurahs.value.toMutableSet()
            newSet.remove(key)
            downloadedSurahs.value = newSet
            showToast("Offline audio removed for Surah ${surah.nameEnglish}")
        }
    }

    fun refreshDownloadedSurahs() {
        viewModelScope.launch(Dispatchers.IO) {
            val set = mutableSetOf<String>()
            QuranData.reciters.forEach { reciter ->
                QuranData.completeSurahList.forEach { surah ->
                    if (QuranAudioCacheManager.isSurahAudioCached(getApplication(), surah.number, reciter.id) ||
                        QuranAudioCacheManager.isSurahCached(getApplication(), surah.number, reciter.id, surah.totalVerses)) {
                        set.add("${reciter.id}_${surah.number}")
                    }
                }
            }
            downloadedSurahs.value = set
        }
    }

    private fun onSurahAudioCompleted() {
        if (isAudioRepeatOne.value) {
            playSurahAudio(currentPlayingSurah.value, openPlayer = false)
        } else if (isAutoAdvanceAyah.value) {
            playNextSurah()
        } else {
            isAudioPlaying.value = false
            audioProgress.value = 1.0f
        }
    }

    fun setReciter(reciter: Reciter) {
        selectedReciter.value = reciter
        sharedPrefs.edit().putString("selected_reciter_id", reciter.id).apply()
        triggerHaptic()
    }

    fun toggleAudioPlayback(surah: Surah? = null, reciter: Reciter? = null) {
        if (reciter != null && reciter != selectedReciter.value) {
            selectedReciter.value = reciter
            sharedPrefs.edit().putString("selected_reciter_id", reciter.id).apply()
            val targetSurah = surah ?: currentPlayingSurah.value
            playSurahAudio(targetSurah, openPlayer = false)
            return
        }

        if (surah != null && surah.number != currentPlayingSurah.value.number) {
            playSurahAudio(surah, openPlayer = false)
            return
        }

        val mp = mediaPlayer
        if (mp != null) {
            if (mp.isPlaying) {
                mp.pause()
                isAudioPlaying.value = false
            } else {
                mp.start()
                isAudioPlaying.value = true
                startAudioProgressTracker()
            }
        } else {
            val targetSurah = surah ?: currentPlayingSurah.value
            playSurahAudio(targetSurah, openPlayer = false)
        }
    }

    fun pauseAudio() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            // ignore
        }
        isAudioPlaying.value = false
    }

    fun stopAudioPlayback() {
        stopAndResetAudio()
    }

    fun stopAndResetAudio() {
        try {
            mediaPlayer?.apply {
                try {
                    if (isPlaying) {
                        pause()
                    }
                } catch (e: Exception) {
                    // Ignore state transition errors
                }
                reset()
            }
        } catch (e: Exception) {
            // ignore
        }
        isAudioPlaying.value = false
        isAudioBuffering.value = false
        isAyahAudioMode.value = false
        currentPlayingVerse.value = 0
        audioProgress.value = 0f
        audioCurrentPositionMs.value = 0
        audioDurationMs.value = 0
        audioProgressTrackerJob?.cancel()
    }

    fun stopAudio() {
        stopAndResetAudio()
    }

    fun skipBack10Seconds() {
        val mp = mediaPlayer ?: return
        try {
            val cur = mp.currentPosition
            val target = (cur - 10000).coerceAtLeast(0)
            mp.seekTo(target)
            audioCurrentPositionMs.value = target
            val dur = audioDurationMs.value
            if (dur > 0) {
                audioProgress.value = (target.toFloat() / dur.toFloat()).coerceIn(0f, 1f)
            }
            triggerHaptic()
        } catch (e: Exception) {
            // ignore
        }
    }

    fun seekAudioTo(fraction: Float) {
        val mp = mediaPlayer ?: return
        val duration = audioDurationMs.value
        if (duration > 0) {
            val targetMs = (fraction * duration).toInt().coerceIn(0, duration)
            try {
                mp.seekTo(targetMs)
                audioCurrentPositionMs.value = targetMs
                audioProgress.value = fraction.coerceIn(0f, 1f)
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun playNextSurah() {
        val currentNum = currentPlayingSurah.value.number
        val nextNum = if (currentNum < 114) currentNum + 1 else 1
        val nextSurah = QuranData.surahs.firstOrNull { it.number == nextNum }
            ?: QuranData.completeSurahList.firstOrNull { it.number == nextNum }
            ?: QuranData.surahs.first()
        playSurahAudio(nextSurah, openPlayer = false)
        showToast("Playing Surah ${nextSurah.nameEnglish}")
    }

    fun playPreviousSurah() {
        val currentNum = currentPlayingSurah.value.number
        val prevNum = if (currentNum > 1) currentNum - 1 else 114
        val prevSurah = QuranData.surahs.firstOrNull { it.number == prevNum }
            ?: QuranData.completeSurahList.firstOrNull { it.number == prevNum }
            ?: QuranData.surahs.last()
        playSurahAudio(prevSurah, openPlayer = false)
        showToast("Playing Surah ${prevSurah.nameEnglish}")
    }

    fun playNextAyah() = playNextSurah()

    fun playPreviousAyah() = playPreviousSurah()

    fun toggleRepeatMode() {
        val newState = !isAudioRepeatOne.value
        isAudioRepeatOne.value = newState
        sharedPrefs.edit().putBoolean("is_audio_repeat_one", newState).apply()
        showToast(if (newState) "Repeat Surah: ON" else "Repeat: OFF")
    }

    fun toggleAutoAdvanceAyah(enabled: Boolean? = null) {
        val newState = enabled ?: !isAutoAdvanceAyah.value
        isAutoAdvanceAyah.value = newState
        sharedPrefs.edit().putBoolean("is_auto_advance_ayah", newState).apply()
        triggerHaptic()
        showToast(if (newState) "Auto-advance audio enabled" else "Auto-advance audio disabled")
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        sleepTimerMinutes.value = minutes
        if (minutes == null || minutes <= 0) {
            showToast("Sleep timer turned off")
            return
        }
        showToast("Sleep timer set for $minutes minutes")
        sleepTimerJob = viewModelScope.launch {
            delay(minutes * 60 * 1000L)
            stopAudioPlayback()
            sleepTimerMinutes.value = null
            showToast("Sleep timer: Audio paused")
        }
    }

    fun setAudioSpeed(speed: Float) {
        audioPlaybackSpeed.value = speed
        sharedPrefs.edit().putFloat("audio_playback_speed", speed).apply()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        mp.playbackParams = mp.playbackParams.setSpeed(speed)
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun selectReciter(reciter: Reciter) {
        selectedReciter.value = reciter
        sharedPrefs.edit().putString("selected_reciter_id", reciter.id).apply()
        if (isAudioPlaying.value) {
            playSurahAudio(currentPlayingSurah.value, openPlayer = false)
        }
        showToast("Reciter: ${reciter.name}")
    }

    private fun startAudioProgressTracker() {
        audioProgressTrackerJob?.cancel()
        audioProgressTrackerJob = viewModelScope.launch {
            while (isAudioPlaying.value) {
                try {
                    mediaPlayer?.let { mp ->
                        if (mp.isPlaying) {
                            val cur = mp.currentPosition
                            val dur = mp.duration.coerceAtLeast(1)
                            audioCurrentPositionMs.value = cur
                            audioDurationMs.value = dur
                            audioProgress.value = (cur.toFloat() / dur.toFloat()).coerceIn(0f, 1f)
                        }
                    }
                } catch (e: Exception) {
                    // ignore
                }
                delay(250)
            }
        }
    }

    // ============================================================
    // QURAN READING & EXACT BOOKMARK SYSTEM
    // ============================================================

    fun selectSurahForReading(surah: Surah, startAyah: Int = 0) {
        viewModelScope.launch {
            val surahWithVerses = if (surah.verses.isNotEmpty()) surah else repository.getSurahWithVerses(surah.number)
            selectedSurahForReading.value = surahWithVerses
            revealedVersesInSession.value = emptySet()
            memorizationCurrentRepetition.value = 1
            val currentBookmark = readingProgress.value
            val targetAyah = if (startAyah > 0) {
                startAyah
            } else if (currentBookmark != null && currentBookmark.surahNumber == surah.number) {
                currentBookmark.ayahNumber
            } else {
                0
            }
            targetAyahToScrollTo.value = targetAyah
            quranScrollRequest.value = QuranScrollRequest(surah.number, targetAyah)
            navigateTo(NoorDestination.QURAN_READER)
        }
    }

    fun reloadCurrentSurah() {
        val current = selectedSurahForReading.value
        viewModelScope.launch {
            val loaded = repository.getSurahWithVerses(current.number)
            selectedSurahForReading.value = loaded
            if (loaded.verses.isNotEmpty()) {
                showToast("Surah ${loaded.nameEnglish} loaded (${loaded.verses.size} ayahs).")
            } else {
                showToast("Loading verses for ${loaded.nameEnglish}...")
            }
        }
    }

    suspend fun searchQuranVerses(query: String): List<Verse> {
        return repository.searchVerses(query)
    }

    suspend fun searchDuas(query: String): List<com.example.data.model.DuaItem> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        com.example.data.quran.DuaData.search(query)
    }

    suspend fun searchHadiths(query: String): List<com.example.data.model.HadithItem> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        com.example.data.quran.HadithData.searchRemote(query)
    }

    fun searchReciters(query: String): List<com.example.data.model.Reciter> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return emptyList()
        val isReciterKeyword = q.contains("reciter") || q.contains("recitation") || q.contains("qari") || 
                q.contains("voice") || q.contains("audio") || q.contains("listen") || 
                q.contains("قارئ") || q.contains("تلاوة") || q.contains("صوت")
        return reciters.filter { reciter ->
            isReciterKeyword ||
                    reciter.name.lowercase().contains(q) ||
                    reciter.nameAr.contains(q) ||
                    reciter.style.lowercase().contains(q) ||
                    reciter.country.lowercase().contains(q)
        }
    }

    suspend fun getExactVerse(surahNumber: Int, verseNumber: Int): Verse? {
        return repository.getVerse(surahNumber, verseNumber)
    }

    fun saveExactReadingBookmark(surah: Surah, ayahNumber: Int) {
        viewModelScope.launch {
            val current = readingProgress.value
            if (current != null && current.surahNumber == surah.number && current.ayahNumber == ayahNumber) {
                // Ayah is already bookmarked -> Toggle/Undo bookmark
                repository.clearReadingProgress()
                triggerHaptic()
                showToast("Bookmark removed")
            } else {
                val diff = if (current != null && current.surahNumber == surah.number) {
                    java.lang.Math.abs(ayahNumber - current.ayahNumber).coerceAtLeast(1)
                } else {
                    ayahNumber
                }
                addQuranAyahsReadToday(diff)
                repository.saveReadingProgress(
                    surahNumber = surah.number,
                    surahName = surah.nameEnglish,
                    ayahNumber = ayahNumber,
                    totalAyahs = surah.totalVerses
                )
                targetAyahToScrollTo.value = ayahNumber

                triggerHaptic()
                showToast("🔖 Saved bookmark position: ${surah.nameEnglish} Ayah $ayahNumber")
            }
        }
    }

    fun markKhatmaProgressToVerse(surah: Surah, ayahNumber: Int) {
        viewModelScope.launch {
            val absAyah = KhatmaEngine.getAbsoluteAyahIndex(surah.number, ayahNumber)
            updateKhatmaReadAyahs(absAyah)
            
            // Also keep reading progress pointer updated so reader can resume here seamlessly
            repository.saveReadingProgress(
                surahNumber = surah.number,
                surahName = surah.nameEnglish,
                ayahNumber = ayahNumber,
                totalAyahs = surah.totalVerses
            )
            targetAyahToScrollTo.value = ayahNumber

            triggerHaptic()
            val total = KhatmaEngine.TOTAL_QURAN_AYAHS
            showToast("📖 Khatma Progress Updated: $absAyah / $total Ayahs (${surah.nameEnglish} : $ayahNumber)")
        }
    }

    fun resumeReading(progress: ReadingProgressEntity) {
        viewModelScope.launch {
            val targetSurah = repository.getSurahWithVerses(progress.surahNumber)
            selectedSurahForReading.value = targetSurah
            targetAyahToScrollTo.value = progress.ayahNumber
            quranScrollRequest.value = QuranScrollRequest(progress.surahNumber, progress.ayahNumber)
            navigateTo(NoorDestination.QURAN_READER)
        }
    }

    fun openPreviousSurah() {
        val currentNum = selectedSurahForReading.value.number
        if (currentNum > 1) {
            viewModelScope.launch {
                val prevSurah = repository.getSurahWithVerses(currentNum - 1)
                selectedSurahForReading.value = prevSurah
                val currentBookmark = readingProgress.value
                val targetAyah = if (currentBookmark != null && currentBookmark.surahNumber == prevSurah.number) {
                    currentBookmark.ayahNumber
                } else {
                    0
                }
                targetAyahToScrollTo.value = targetAyah
                quranScrollRequest.value = QuranScrollRequest(prevSurah.number, targetAyah)
            }
        } else {
            showToast("You are at the first Surah (Al-Fatihah)")
        }
    }

    fun openNextSurah() {
        val currentNum = selectedSurahForReading.value.number
        if (currentNum < 114) {
            viewModelScope.launch {
                val nextSurah = repository.getSurahWithVerses(currentNum + 1)
                selectedSurahForReading.value = nextSurah
                val currentBookmark = readingProgress.value
                val targetAyah = if (currentBookmark != null && currentBookmark.surahNumber == nextSurah.number) {
                    currentBookmark.ayahNumber
                } else {
                    0
                }
                targetAyahToScrollTo.value = targetAyah
                quranScrollRequest.value = QuranScrollRequest(nextSurah.number, targetAyah)
            }
        } else {
            showToast("You are at the final Surah (An-Nas)")
        }
    }

    fun setReadingDisplayMode(isArabicOnly: Boolean) {
        if (isMushafFlowMode.value != isArabicOnly) {
            isMushafFlowMode.value = isArabicOnly
            sharedPrefs.edit().putBoolean("is_mushaf_flow_mode", isArabicOnly).apply()
            triggerHaptic()
            showToast(if (isArabicOnly) "Switched to Arabic Only reading mode" else "Switched to Mixed Reading mode")
        }
    }

    fun setSelectedArabicFont(font: QuranArabicFont) {
        selectedArabicFont.value = font
        sharedPrefs.edit().putString("quran_arabic_font", font.id).apply()
        triggerHaptic()
        showToast("Calligraphy: ${font.displayName}")
    }

    fun toggleMushafFlowMode(enabled: Boolean? = null) {
        val newState = enabled ?: !isMushafFlowMode.value
        isMushafFlowMode.value = newState
        sharedPrefs.edit().putBoolean("is_mushaf_flow_mode", newState).apply()
        triggerHaptic()
        showToast(if (newState) "Distraction-Free Mushaf Flow enabled 📖" else "Standard Reading View with Translations")
    }

    fun toggleQuranSepiaMode(enabled: Boolean? = null) {
        val newState = enabled ?: !isQuranSepiaMode.value
        isQuranSepiaMode.value = newState
        sharedPrefs.edit().putBoolean("is_quran_sepia_mode", newState).apply()
        triggerHaptic()
        showToast(if (newState) "Sepia Parchment Quran Canvas active 📜" else "Standard Reading Canvas active")
    }

    fun toggleTajweedMode(enabled: Boolean? = null) {
        val newState = enabled ?: !isTajweedEnabled.value
        isTajweedEnabled.value = newState
        sharedPrefs.edit().putBoolean("is_tajweed_enabled", newState).apply()
        triggerHaptic()
        showToast(if (newState) "Tajweed color-coding active 🎨" else "Tajweed color-coding disabled")
    }

    fun setTajweedButtonPosition(position: TajweedButtonPosition) {
        tajweedButtonPosition.value = position
        sharedPrefs.edit().putString("tajweed_btn_position", position.name).apply()
        triggerHaptic()
    }

    fun setShowTranslation(enabled: Boolean) {
        showTranslation.value = enabled
        sharedPrefs.edit().putBoolean("quran_show_translation", enabled).apply()
        triggerHaptic()
    }

    fun setShowTransliteration(enabled: Boolean) {
        showTransliteration.value = enabled
        sharedPrefs.edit().putBoolean("quran_show_transliteration", enabled).apply()
        triggerHaptic()
    }

    fun setSharedReadingTheme(themeName: String) {
        val normalized = when (themeName) {
            "Obsidian Night", "Dark" -> "Obsidian Night"
            "Warm Parchment", "Warm", "Sepia" -> "Warm Parchment"
            else -> "Madani Crisp"
        }
        sharedReadingTheme.value = normalized
        sharedPrefs.edit().putString("shared_reading_theme", normalized).apply()
        triggerHaptic()
    }

    fun setAppThemeMode(mode: com.example.ui.theme.AppThemeMode) {
        val themeName = when (mode) {
            com.example.ui.theme.AppThemeMode.LIGHT -> "Madani Crisp"
            com.example.ui.theme.AppThemeMode.DARK -> "Obsidian Night"
            com.example.ui.theme.AppThemeMode.WARM -> "Warm Parchment"
        }
        setSharedReadingTheme(themeName)
    }

    fun setHomeTheme(themeName: String) {
        val normalized = if (themeName.equals("Classic Green", ignoreCase = true)) "Classic Green" else "Peach"
        homeTheme.value = normalized
        sharedPrefs.edit().putString("app_home_theme", normalized).apply()
        triggerHaptic()
    }

    fun setQuranReaderFullscreen(enabled: Boolean) {
        if (isQuranReaderFullscreen.value != enabled) {
            isQuranReaderFullscreen.value = enabled
        }
    }

    fun toggleQuranReaderFullscreen() {
        isQuranReaderFullscreen.value = !isQuranReaderFullscreen.value
    }

    fun saveQuranNote(surahNumber: Int, verseNumber: Int, text: String) {
        viewModelScope.launch {
            repository.saveQuranNote(surahNumber, verseNumber, text)
            triggerHaptic()
            if (text.isBlank()) {
                showToast("Note removed")
            } else {
                showToast("Note saved successfully")
            }
        }
    }

    fun deleteQuranNote(surahNumber: Int, verseNumber: Int) {
        viewModelScope.launch {
            repository.deleteQuranNote(surahNumber, verseNumber)
            triggerHaptic()
            showToast("Note removed")
        }
    }

    fun dismissKhatmaMilestoneModal() {
        khatmaMilestoneModal.value = null
    }

    // ============================================================
    // QURAN MEMORIZATION STUDIO / HIFZ HUB CONTROLS & SETTINGS
    // ============================================================

    fun ensureMemorizationVersesLoaded() {
        viewModelScope.launch {
            val current = memorizationSurah.value
            if (current.verses.isEmpty()) {
                val surahWithVerses = repository.getSurahWithVerses(current.number)
                if (surahWithVerses.verses.isNotEmpty()) {
                    memorizationSurah.value = surahWithVerses
                }
            }
        }
    }

    fun openMemorizationSetup(surah: Surah? = null, startAyah: Int = 1, endAyah: Int? = null) {
        stopMemorizationDrill()
        viewModelScope.launch {
            val targetSurah = surah ?: selectedSurahForReading.value.takeIf { it.verses.isNotEmpty() }
                ?: QuranData.surahs.firstOrNull() ?: QuranData.completeSurahList.first()
            val surahWithVerses = if (targetSurah.verses.isNotEmpty()) targetSurah else repository.getSurahWithVerses(targetSurah.number)
            memorizationSurah.value = surahWithVerses
            memorizationStartAyah.value = startAyah.coerceIn(1, surahWithVerses.totalVerses)
            val defaultEnd = endAyah ?: (startAyah + 6).coerceAtMost(surahWithVerses.totalVerses)
            memorizationEndAyah.value = defaultEnd.coerceIn(memorizationStartAyah.value, surahWithVerses.totalVerses)
            memorizationCurrentRepetition.value = 1
            revealedVersesInSession.value = emptySet()
            navigateTo(NoorDestination.QURAN_MEMORIZATION_SETUP)
        }
    }

    fun openMemorizationStudio(surah: Surah? = null, startAyah: Int = 1, endAyah: Int? = null) {
        // Landing first on the dedicated Hifz Setup screen per requirements
        openMemorizationSetup(surah, startAyah, endAyah)
    }

    fun startMemorizationSession() {
        stopMemorizationDrill()
        memorizationCurrentRepetition.value = 1
        revealedVersesInSession.value = emptySet()
        navigateTo(NoorDestination.QURAN_MEMORIZATION)
    }

    fun applyHifzPreset(batchSize: Int, repeatCount: Int, pattern: String, delaySeconds: Int) {
        val total = memorizationSurah.value.totalVerses.coerceAtLeast(1)
        val start = memorizationStartAyah.value.coerceIn(1, total)
        val end = (start + batchSize - 1).coerceIn(start, total)
        setMemorizationRange(start, end)
        setMemorizationRepeatCount(repeatCount)
        setAyahPattern(pattern)
        setMemorizationDelaySeconds(delaySeconds)
    }

    fun setMemorizationSurah(surah: Surah, initialStartAyah: Int = 1, initialEndAyah: Int? = null) {
        stopMemorizationDrill()
        viewModelScope.launch {
            val surahWithVerses = if (surah.verses.isNotEmpty()) surah else repository.getSurahWithVerses(surah.number)
            memorizationSurah.value = surahWithVerses
            val total = surahWithVerses.totalVerses.coerceAtLeast(1)
            val validStart = initialStartAyah.coerceIn(1, total)
            val validEnd = (initialEndAyah ?: (validStart + 6)).coerceIn(validStart, total)
            memorizationStartAyah.value = validStart
            memorizationEndAyah.value = validEnd
            memorizationCurrentRepetition.value = 1
            revealedVersesInSession.value = emptySet()
            if (isAudioPlaying.value) {
                stopAudio()
            }
            checkWarmUpAvailable()
        }
    }

    fun setMemorizationRange(start: Int, end: Int) {
        stopMemorizationDrill()
        val total = memorizationSurah.value.totalVerses.coerceAtLeast(1)
        val validStart = start.coerceIn(1, total)
        val validEnd = end.coerceIn(validStart, total)
        memorizationStartAyah.value = validStart
        memorizationEndAyah.value = validEnd
        memorizationCurrentRepetition.value = 1
        checkWarmUpAvailable()
        triggerHaptic()
    }

    fun setMemorizationMaskingStyle(style: HifzMaskingStyle) {
        memorizationMaskingStyle.value = style
        sharedPrefs.edit().putString("hifz_masking_style", style.name).apply()
        triggerHaptic()
    }

    fun setHifzSilhouetteOpacity(opacity: Float) {
        val safe = opacity.coerceIn(0.04f, 0.60f)
        hifzSilhouetteOpacity.value = safe
        sharedPrefs.edit().putFloat("hifz_silhouette_opacity", safe).apply()
        triggerHaptic()
    }

    fun setRecallGroupSize(size: Int) {
        val safe = size.coerceIn(1, 20)
        recallGroupSize.value = safe
        sharedPrefs.edit().putInt("hifz_recall_group_size", safe).apply()
        triggerHaptic()
    }

    fun setMemorizationDelaySeconds(seconds: Int) {
        val safeSec = seconds.coerceIn(0, 15)
        memorizationDelaySeconds.value = safeSec
        sharedPrefs.edit().putInt("hifz_delay_seconds", safeSec).apply()
        triggerHaptic()
        showToast(if (safeSec == 0) "Verse Delay: Off (Instant)" else "Recitation Pause: ${safeSec}s between verses")
    }

    fun toggleMemorizationLoopRange() {
        val newState = !memorizationLoopRange.value
        memorizationLoopRange.value = newState
        sharedPrefs.edit().putBoolean("hifz_loop_range", newState).apply()
        triggerHaptic()
        showToast(if (newState) "Range Loop: ON (Continuous drill)" else "Range Loop: OFF (Single pass)")
    }

    fun toggleMemorizationAudioSyncReveal() {
        val newState = !memorizationAudioSyncReveal.value
        memorizationAudioSyncReveal.value = newState
        sharedPrefs.edit().putBoolean("hifz_audio_sync_reveal", newState).apply()
        triggerHaptic()
        showToast(if (newState) "Audio Sync Reveal: ON" else "Audio Sync Reveal: OFF (Manual tap only)")
    }

    fun toggleMemorizationShowTranslation() {
        val newState = !memorizationShowTranslation.value
        memorizationShowTranslation.value = newState
        sharedPrefs.edit().putBoolean("hifz_show_translation", newState).apply()
        triggerHaptic()
    }

    fun toggleMemorizationTestRecallMode() {
        val newState = !memorizationTestRecallMode.value
        memorizationTestRecallMode.value = newState
        triggerHaptic()
        if (newState) {
            revealedVersesInSession.value = emptySet()
            showToast("Self-Test Recall Mode: Tap any hidden verse to test memory")
        } else {
            showToast("Test Mode: OFF")
        }
    }

    fun setMemorizationStudioTab(tabIndex: Int) {
        val previous = memorizationStudioTab.value
        memorizationStudioTab.value = tabIndex.coerceIn(0, 3)
        if (tabIndex == 2) {
            pauseMemorizationDrill()
            revealedVersesInSession.value = emptySet()
        }
        triggerHaptic()
    }

    fun toggleVerseMemorizedStatus(surahNumber: Int, verseNumber: Int, isRecallMode: Boolean = false) {
        val key = "${surahNumber}_${verseNumber}"
        val targetFlow = if (isRecallMode) memorizedRecallSet else memorizedPracticeSet
        val prefKey = if (isRecallMode) "hifz_memorized_recall_set" else "hifz_memorized_practice_set"
        val currentSet = targetFlow.value.toMutableSet()
        val isNowMemorized = if (currentSet.contains(key)) {
            currentSet.remove(key)
            false
        } else {
            currentSet.add(key)
            true
        }
        targetFlow.value = currentSet
        sharedPrefs.edit().putStringSet(prefKey, currentSet).apply()

        val modeTag = if (isRecallMode) "RECALL" else "PRACTICE"
        viewModelScope.launch {
            try {
                db.noorDao().insertHifzEvent(
                    com.example.data.local.HifzEventEntity(
                        surahNumber = surahNumber,
                        ayahNumber = verseNumber,
                        type = if (isNowMemorized) "MARKED" else "UNMARKED",
                        mode = modeTag,
                        timestampUtcMs = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                // ignore DB error
            }
        }

        triggerHaptic()
        val modeLabel = if (isRecallMode) "Recall" else "Practice"
        showToast(if (isNowMemorized) "Ayah $verseNumber marked in $modeLabel ✓" else "Ayah $verseNumber unmarked in $modeLabel")
    }

    fun isVerseMemorized(surahNumber: Int, verseNumber: Int, isRecallMode: Boolean = false): Boolean {
        val targetFlow = if (isRecallMode) memorizedRecallSet else memorizedPracticeSet
        return targetFlow.value.contains("${surahNumber}_${verseNumber}")
    }

    fun markCurrentRangeMemorized(markAsMemorized: Boolean, isRecallMode: Boolean = false) {
        val surahNum = memorizationSurah.value.number
        val start = memorizationStartAyah.value
        val end = memorizationEndAyah.value
        val targetFlow = if (isRecallMode) memorizedRecallSet else memorizedPracticeSet
        val prefKey = if (isRecallMode) "hifz_memorized_recall_set" else "hifz_memorized_practice_set"
        val currentSet = targetFlow.value.toMutableSet()
        val newEvents = mutableListOf<com.example.data.local.HifzEventEntity>()
        val now = System.currentTimeMillis()
        val modeTag = if (isRecallMode) "RECALL" else "PRACTICE"

        for (v in start..end) {
            val key = "${surahNum}_${v}"
            if (markAsMemorized) {
                currentSet.add(key)
            } else {
                currentSet.remove(key)
            }
            newEvents.add(
                com.example.data.local.HifzEventEntity(
                    surahNumber = surahNum,
                    ayahNumber = v,
                    type = if (markAsMemorized) "MARKED" else "UNMARKED",
                    mode = modeTag,
                    timestampUtcMs = now
                )
            )
        }
        targetFlow.value = currentSet
        sharedPrefs.edit().putStringSet(prefKey, currentSet).apply()

        viewModelScope.launch {
            try {
                db.noorDao().insertHifzEvents(newEvents)
            } catch (e: Exception) {
                // ignore DB error
            }
        }

        if (markAsMemorized) {
            val total = end - start + 1
            recordHifzSession(
                surahNumber = surahNum,
                surahName = memorizationSurah.value.nameEnglish,
                surahNameArabic = memorizationSurah.value.nameArabic,
                startAyah = start,
                endAyah = end,
                mode = if (isRecallMode) "Self-Recall" else "Practice",
                totalAyahs = total,
                ayahsMemorized = total,
                ayahsMissed = 0,
                notes = "Range marked as memorized"
            )
        }

        triggerHaptic()
        val modeLabel = if (isRecallMode) "Recall" else "Practice"
        showToast(if (markAsMemorized) "Ayahs $start–$end marked in $modeLabel ✓" else "Ayahs $start–$end unmarked in $modeLabel")
    }

    private var wordPatternJob: Job? = null
    private var ayahPatternJob: Job? = null
    private var warmUpJob: Job? = null
    private var currentAyahCompletionCallback: (() -> Unit)? = null

    private suspend fun playAyahAndWait(surah: Surah, ayahNumber: Int, playBasmala: Boolean = false): Boolean {
        val completer = CompletableDeferred<Unit>()
        currentAyahCompletionCallback = {
            if (completer.isActive) completer.complete(Unit)
        }
        isAudioPlaying.value = true
        playAyah(surah, ayahNumber, openPlayer = false, playBasmalaFirst = playBasmala)

        return try {
            completer.await()
            true
        } catch (e: Exception) {
            false
        } finally {
            currentAyahCompletionCallback = null
        }
    }

    private suspend fun playWordRangeAndWait(
        surahNumber: Int,
        ayahNumber: Int,
        fromWordIndex: Int,
        toWordIndex: Int,
        loopCount: Int,
        delayBetweenLoops: Long = 0L
    ): Boolean {
        val completer = CompletableDeferred<Unit>()
        hifzAudioController.playWordRange(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            fromWordIndex = fromWordIndex,
            toWordIndex = toWordIndex,
            reciterId = selectedReciter.value.id,
            loopCount = loopCount,
            delayBetweenLoops = delayBetweenLoops,
            onComplete = {
                if (completer.isActive) completer.complete(Unit)
            }
        )
        return try {
            completer.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun playAyahWithAutomaticSplits(
        surah: Surah,
        ayahNumber: Int,
        segmentRepeatCount: Int = 1,
        delayMs: Long = 0L
    ): Boolean {
        val timing = HifzWordTimingRepository.getAyahTiming(
            getApplication(),
            selectedReciter.value.id,
            surah.number,
            ayahNumber
        )
        val totalWords = timing?.totalWords ?: 15
        val groups = AyahSplitRepository.getGroups(surah.number, ayahNumber, totalWords)

        Log.d("AyahSplitTrace", "playAyahWithAutomaticSplits: Surah ${surah.number}:$ayahNumber (totalWords=$totalWords) -> groups: $groups")

        for (gIdx in groups.indices) {
            val group = groups[gIdx]
            memorizationWordChunkStart.value = group.first
            memorizationWordChunkEnd.value = group.second

            Log.d("AyahSplitTrace", "Playing split segment ${gIdx + 1}/${groups.size} for ${surah.number}:$ayahNumber -> words ${group.first}..${group.second}")

            val success = playWordRangeAndWait(
                surahNumber = surah.number,
                ayahNumber = ayahNumber,
                fromWordIndex = group.first,
                toWordIndex = group.second,
                loopCount = segmentRepeatCount,
                delayBetweenLoops = delayMs
            )
            if (!success) return false

            if (groups.size > 1 && gIdx < groups.size - 1 && viewModelScope.coroutineContext.isActive) {
                val interSegmentPauseMs = maxOf(delayMs, 800L)
                var elapsed = 0L
                while (elapsed < interSegmentPauseMs && viewModelScope.coroutineContext.isActive) {
                    while (isHifzPaused.value && viewModelScope.coroutineContext.isActive) {
                        delay(100L)
                    }
                    val step = minOf(100L, interSegmentPauseMs - elapsed)
                    delay(step)
                    if (!isHifzPaused.value) {
                        elapsed += step
                    }
                }
            }
        }
        return true
    }

    private suspend fun performDrillDelay(seconds: Int) {
        if (seconds <= 0) return
        memorizationDelayActive.value = true
        try {
            var remainingSec = seconds
            while (remainingSec > 0 && viewModelScope.coroutineContext.isActive) {
                while (isHifzPaused.value && viewModelScope.coroutineContext.isActive) {
                    delay(100)
                }
                memorizationDelayCountdown.value = remainingSec
                delay(1000)
                if (!isHifzPaused.value) {
                    remainingSec--
                }
            }
        } finally {
            memorizationDelayActive.value = false
            memorizationDelayCountdown.value = 0
        }
    }

    fun saveLastDrilledChunk(
        surahNumber: Int,
        startAyah: Int,
        endAyah: Int,
        chunkStart: Int,
        chunkEnd: Int,
        mode: String
    ) {
        sharedPrefs.edit()
            .putInt("hifz_last_drilled_${surahNumber}_${mode}_start", startAyah)
            .putInt("hifz_last_drilled_${surahNumber}_${mode}_end", endAyah)
            .putInt("hifz_last_drilled_${surahNumber}_${mode}_chunk_start", chunkStart)
            .putInt("hifz_last_drilled_${surahNumber}_${mode}_chunk_end", chunkEnd)
            .putLong("hifz_last_drilled_${surahNumber}_${mode}_time", System.currentTimeMillis())
            .apply()
        checkWarmUpAvailable()
    }

    fun checkWarmUpAvailable() {
        val surah = memorizationSurah.value
        val mode = memorizationDrillMode.value
        val chunkStart = sharedPrefs.getInt("hifz_last_drilled_${surah.number}_${mode}_chunk_start", 0)
        val chunkEnd = sharedPrefs.getInt("hifz_last_drilled_${surah.number}_${mode}_chunk_end", 0)
        if (chunkStart > 0 && chunkEnd >= chunkStart) {
            warmUpAvailableRange.value = Pair(chunkStart, chunkEnd)
        } else {
            warmUpAvailableRange.value = null
        }
    }

    fun dismissWarmUp() {
        warmUpAvailableRange.value = null
        triggerHaptic()
    }

    fun playWarmUpSession() {
        val range = warmUpAvailableRange.value ?: return
        val surah = memorizationSurah.value
        pauseMemorizationDrill()
        warmUpJob?.cancel()
        warmUpJob = viewModelScope.launch {
            drillProgressState.value = HifzDrillProgressInfo(
                phase = "WARM_UP",
                label = "Warm-Up: Ayahs ${range.first}–${range.second}",
                currentRep = 1,
                totalReps = 1,
                isWarmUp = true
            )
            for (a in range.first..range.second) {
                if (!isActive) break
                currentPlayingSurah.value = surah
                currentPlayingVerse.value = a
                revealVerse(a)
                playAyahAndWait(surah, a, playBasmala = false)
            }
            isAudioPlaying.value = false
            drillProgressState.value = HifzDrillProgressInfo()
            warmUpAvailableRange.value = null
            triggerHaptic()
            showToast("Warm-Up completed! Ready for today's drill.")
        }
    }

    fun triggerConfidencePrompt(
        surahNumber: Int,
        surahName: String,
        startAyah: Int,
        endAyah: Int,
        mode: String
    ) {
        activeConfidencePrompt.value = ConfidencePromptData(
            surahNumber = surahNumber,
            surahName = surahName,
            startAyah = startAyah,
            endAyah = endAyah,
            mode = mode
        )
        triggerHaptic()
    }

    fun recordConfidenceRating(rating: String) {
        val prompt = activeConfidencePrompt.value ?: return
        val key = "hifz_confidence_${prompt.surahNumber}_${prompt.startAyah}_${prompt.endAyah}_${prompt.mode}"
        val timeKey = "hifz_confidence_time_${prompt.surahNumber}_${prompt.startAyah}_${prompt.endAyah}_${prompt.mode}"
        sharedPrefs.edit()
            .putString(key, rating)
            .putLong(timeKey, System.currentTimeMillis())
            .apply()

        val total = (prompt.endAyah - prompt.startAyah + 1).coerceAtLeast(1)
        val isMastered = rating == "GOT_IT"
        val memorized = if (isMastered) total else (total - 1).coerceAtLeast(0)
        val missed = if (isMastered) 0 else 1
        recordHifzSession(
            surahNumber = prompt.surahNumber,
            surahName = prompt.surahName,
            startAyah = prompt.startAyah,
            endAyah = prompt.endAyah,
            mode = if (prompt.mode.contains("RECALL", ignoreCase = true) || prompt.mode.contains("Self", ignoreCase = true)) "Self-Recall" else "Practice",
            totalAyahs = total,
            ayahsMemorized = memorized,
            ayahsMissed = missed,
            notes = if (isMastered) "Chunk mastered" else "Needs revision"
        )

        prompt.onChoice(rating)
        activeConfidencePrompt.value = null
        triggerHaptic()
        if (rating == "GOT_IT") {
            showToast("Ayahs ${prompt.startAyah}–${prompt.endAyah}: Marked as Mastered (Got it) ✓")
        } else {
            showToast("Ayahs ${prompt.startAyah}–${prompt.endAyah}: Marked as Still Shaky — Replaying chunk ⚠️")
        }
    }

    fun dismissConfidencePrompt() {
        val prompt = activeConfidencePrompt.value
        prompt?.onChoice?.invoke("GOT_IT")
        activeConfidencePrompt.value = null
        triggerHaptic()
    }

    fun getConfidenceRating(surahNumber: Int, startAyah: Int, endAyah: Int, mode: String): String? {
        return sharedPrefs.getString("hifz_confidence_${surahNumber}_${startAyah}_${endAyah}_${mode}", null)
    }

    fun setChunkReviewSize(size: Int) {
        val safeSize = size.coerceIn(2, 10)
        memorizationChunkReviewSize.value = safeSize
        sharedPrefs.edit().putInt("hifz_chunk_review_size", safeSize).apply()
        triggerHaptic()
        showToast("Chunk Review Interval: Every $safeSize Ayahs")
    }

    fun parsePatternString(patternStr: String, maxVal: Int = 15): List<Int> {
        val parsed = patternStr.split(",", " ", "-", ";", "x", "X", "*")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in 1..maxVal }
        return if (parsed.isNotEmpty()) parsed else listOf(1)
    }

    fun setWordPattern(pattern: String) {
        val parsed = parsePatternString(pattern, maxVal = 15)
        val formatted = parsed.joinToString(", ")
        memorizationWordPattern.value = formatted
        sharedPrefs.edit().putString("hifz_word_pattern", formatted).apply()
        val firstNum = parsed.firstOrNull() ?: 1
        if (firstNum > 1 && memorizationWordRepeatCount.value == 1) {
            setWordRepeatCount(firstNum)
        }
        triggerHaptic()
    }

    fun setAyahPattern(pattern: String) {
        val parsed = parsePatternString(pattern, maxVal = 15)
        val formatted = parsed.joinToString(", ")
        memorizationAyahPattern.value = formatted
        sharedPrefs.edit().putString("hifz_ayah_pattern", formatted).apply()
        val firstNum = parsed.firstOrNull() ?: 1
        if (firstNum > 1 && memorizationRepeatCount.value == 1) {
            setMemorizationRepeatCount(firstNum)
        }
        triggerHaptic()
    }

    fun playMemorizationDrill() {
        if (hifzAudioState.value.isPaused || isHifzPaused.value) {
            resumeMemorizationDrill()
            return
        }
        isHifzPaused.value = false
        if (memorizationDrillMode.value.uppercase() == "WORD") {
            playWordPatternDrillSession()
        } else {
            playAyahPatternDrillSession()
        }
        triggerHaptic()
    }

    fun playVerseInMemorizationMode(surah: Surah, verseNumber: Int, isRecallMode: Boolean = false) {
        if (verseNumber == 0) {
            stopMemorizationDrill()
            ayahPatternJob = viewModelScope.launch {
                currentPlayingSurah.value = surah
                currentPlayingVerse.value = 0
                playAyahWithAutomaticSplits(
                    surah = surah,
                    ayahNumber = 0,
                    segmentRepeatCount = 1,
                    delayMs = 0L
                )
            }
            return
        }

        if (isRecallMode) {
            revealVerse(verseNumber)
        }

        stopMemorizationDrill()
        isHifzPaused.value = false
        currentPlayingSurah.value = surah
        currentPlayingVerse.value = verseNumber

        if (memorizationDrillMode.value.uppercase() == "WORD") {
            playWordPatternDrillSession(startFromAyah = verseNumber, forceStart = true)
        } else {
            playAyahPatternDrillSession(startFromAyah = verseNumber, forceStart = true)
        }
        triggerHaptic()
    }

    fun pauseMemorizationDrill() {
        isHifzPaused.value = true
        hifzAudioController.pause()
        pauseAudio()
        triggerHaptic()
    }

    fun resumeMemorizationDrill() {
        isHifzPaused.value = false
        if (hifzAudioState.value.isPaused) {
            hifzAudioController.resume()
        } else if (mediaPlayer != null && !isAudioPlaying.value) {
            try {
                mediaPlayer?.start()
                isAudioPlaying.value = true
                startAudioProgressTracker()
            } catch (e: Exception) {
                // ignore
            }
        } else {
            if (memorizationDrillMode.value.uppercase() == "WORD") {
                playWordPatternDrillSession()
            } else {
                playAyahPatternDrillSession()
            }
        }
        triggerHaptic()
    }

    fun stopMemorizationDrill() {
        isHifzPaused.value = false
        wordPatternJob?.cancel()
        wordPatternJob = null
        ayahPatternJob?.cancel()
        ayahPatternJob = null
        warmUpJob?.cancel()
        warmUpJob = null
        currentAyahCompletionCallback?.invoke()
        currentAyahCompletionCallback = null
        drillProgressState.value = HifzDrillProgressInfo()
        memorizationDelayJob?.cancel()
        memorizationDelayActive.value = false
        memorizationDelayCountdown.value = 0
        hifzAudioController.stop()
        stopAndResetAudio()
    }

    fun toggleHideUnreadVerses(enabled: Boolean? = null) {
        val newState = enabled ?: !isHideUnreadVersesEnabled.value
        isHideUnreadVersesEnabled.value = newState
        sharedPrefs.edit().putBoolean("is_hide_unread_verses", newState).apply()
        triggerHaptic()
        if (newState) {
            val currentAyah = currentPlayingVerse.value
            if (isAudioPlaying.value && currentAyah > 0) {
                revealedVersesInSession.value = (1..currentAyah).toSet()
            } else {
                revealedVersesInSession.value = emptySet()
            }
            showToast("Hide Unread Verses: ON (Progressive Reveal)")
        } else {
            showToast("Hide Unread Verses: OFF (Full View)")
        }
    }

    fun setMemorizationDrillMode(mode: String) {
        val safeMode = if (mode.uppercase() == "WORD") "WORD" else "AYAH"
        memorizationDrillMode.value = safeMode
        sharedPrefs.edit().putString("hifz_drill_mode", safeMode).apply()
        pauseMemorizationDrill()
        checkWarmUpAvailable()
        triggerHaptic()
        showToast(if (safeMode == "WORD") "Word Repetition Pattern Mode" else "Ayah Repetition Pattern Mode")
    }

    fun setWordChunkSize(size: Int) {
        val safeSize = size.coerceIn(1, 15)
        memorizationWordChunkSize.value = safeSize
        setWordPattern("$safeSize")
    }

    fun setWordRepeatCount(count: Int) {
        val safeCount = count.coerceIn(1, 20)
        memorizationWordRepeatCount.value = safeCount
        sharedPrefs.edit().putInt("hifz_word_repeat_count", safeCount).apply()
        triggerHaptic()
        showToast("Word Repeats: ${safeCount}x per pattern step")
    }

    fun setWordDelaySeconds(seconds: Int) {
        val safeSec = seconds.coerceIn(0, 15)
        memorizationWordDelaySeconds.value = safeSec
        sharedPrefs.edit().putInt("hifz_word_delay_seconds", safeSec).apply()
        triggerHaptic()
    }

    fun playWordRangeDrill(
        surahNumber: Int,
        ayahNumber: Int,
        fromWordIndex: Int,
        toWordIndex: Int
    ) {
        val repeatCount = memorizationWordRepeatCount.value
        val delayMs = memorizationWordDelaySeconds.value * 1000L
        hifzAudioController.playWordRange(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            fromWordIndex = fromWordIndex,
            toWordIndex = toWordIndex,
            reciterId = selectedReciter.value.id,
            loopCount = repeatCount,
            delayBetweenLoops = delayMs
        )
        triggerHaptic()
    }

    fun stopWordRangeDrill() {
        wordPatternJob?.cancel()
        wordPatternJob = null
        hifzAudioController.stop()
        triggerHaptic()
    }

    fun playWordPatternDrillSession(startFromAyah: Int? = null, forceStart: Boolean = false) {
        if (!forceStart) {
            val hifzState = hifzAudioController.state.value
            if (hifzState.isPlaying || hifzState.isBuffering || isAudioPlaying.value || isAudioBuffering.value) {
                pauseMemorizationDrill()
                return
            }
            if (hifzState.isPaused || isHifzPaused.value) {
                resumeMemorizationDrill()
                return
            }
        }

        stopMemorizationDrill()
        val surah = memorizationSurah.value
        val rangeStart = memorizationStartAyah.value
        val endAyah = memorizationEndAyah.value
        val startAyah = startFromAyah?.coerceIn(rangeStart, endAyah) ?: rangeStart
        val patternList = parsePatternString(memorizationWordPattern.value, maxVal = 15)
        val repeatCount = memorizationWordRepeatCount.value
        val linkingCount = (repeatCount / 2).coerceAtLeast(2)
        val delayMs = memorizationWordDelaySeconds.value * 1000L
        val chunkReviewSize = memorizationChunkReviewSize.value

        wordPatternJob?.cancel()
        wordPatternJob = viewModelScope.launch {
            var lastReviewedAyah = startAyah - 1
            var patternIdx = 0

            for (currAyah in startAyah..endAyah) {
                if (!isActive) break

                memorizationActiveWordAyah.value = currAyah
                currentPlayingSurah.value = surah
                currentPlayingVerse.value = currAyah
                revealVerse(currAyah)

                val timing = HifzWordTimingRepository.getAyahTiming(
                    getApplication(),
                    selectedReciter.value.id,
                    surah.number,
                    currAyah
                )
                val totalWordsInAyah = timing?.totalWords ?: 15

                var currentStartWord = 1
                var chunkIndexInAyah = 0

                while (currentStartWord <= totalWordsInAyah && isActive) {
                    val chunkSize = patternList[patternIdx % patternList.size]
                    val targetEndWord = (currentStartWord + chunkSize - 1).coerceAtMost(totalWordsInAyah)

                    memorizationWordChunkStart.value = currentStartWord
                    memorizationWordChunkEnd.value = targetEndWord
                    memorizationCurrentPatternIndex.value = patternIdx

                    // 1. Play new word chunk alone, repeated per wordRepeatCount
                    drillProgressState.value = HifzDrillProgressInfo(
                        phase = "WORD_CHUNK",
                        label = "Ayah $currAyah • Wd $currentStartWord–$targetEndWord",
                        currentRep = 1,
                        totalReps = repeatCount
                    )

                    playWordRangeAndWait(
                        surahNumber = surah.number,
                        ayahNumber = currAyah,
                        fromWordIndex = currentStartWord,
                        toWordIndex = targetEndWord,
                        loopCount = repeatCount,
                        delayBetweenLoops = delayMs
                    )

                    if (!isActive) break

                    // 2. After each new chunk (from 2nd chunk onward in this ayah),
                    // play everything covered so far WITHIN THE CURRENT AYAH at smaller linking repeat count
                    if (chunkIndexInAyah > 0 && currentStartWord > 1 && isActive) {
                        drillProgressState.value = HifzDrillProgressInfo(
                            phase = "LINKING",
                            label = "Linking Ayah $currAyah • Wd 1–$targetEndWord",
                            currentRep = 1,
                            totalReps = linkingCount,
                            isLinking = true
                        )

                        playWordRangeAndWait(
                            surahNumber = surah.number,
                            ayahNumber = currAyah,
                            fromWordIndex = 1,
                            toWordIndex = targetEndWord,
                            loopCount = linkingCount,
                            delayBetweenLoops = delayMs
                        )
                    }

                    currentStartWord = targetEndWord + 1
                    patternIdx++
                    chunkIndexInAyah++
                }

                if (!isActive) break

                // 3. Once full ayah's words are complete:
                // Check if we hit a 5-ayah chunk review boundary!
                val completedSinceLastReview = currAyah - lastReviewedAyah
                if (completedSinceLastReview >= chunkReviewSize && isActive) {
                    val chunkStart = lastReviewedAyah + 1
                    val chunkEnd = currAyah
                    val chunkRange = chunkStart..chunkEnd

                    drillProgressState.value = HifzDrillProgressInfo(
                        phase = "CHUNK_REVIEW",
                        label = "Chunk Review: Ayahs ${chunkRange.first}–${chunkRange.last}",
                        currentRep = 1,
                        totalReps = 1,
                        isChunkReview = true
                    )

                    for (a in chunkRange) {
                        if (!isActive) break
                        currentPlayingSurah.value = surah
                        currentPlayingVerse.value = a
                        val aTiming = HifzWordTimingRepository.getAyahTiming(
                            getApplication(),
                            selectedReciter.value.id,
                            surah.number,
                            a
                        )
                        val aTotalWords = aTiming?.totalWords ?: 15
                        playWordRangeAndWait(
                            surahNumber = surah.number,
                            ayahNumber = a,
                            fromWordIndex = 1,
                            toWordIndex = aTotalWords,
                            loopCount = 1,
                            delayBetweenLoops = 0L
                        )
                    }

                    lastReviewedAyah = chunkEnd
                    saveLastDrilledChunk(surah.number, startAyah, endAyah, chunkStart, chunkEnd, "WORD")

                    val decisionDeferred = CompletableDeferred<String>()
                    activeConfidencePrompt.value = ConfidencePromptData(
                        surahNumber = surah.number,
                        surahName = surah.nameEnglish,
                        startAyah = chunkStart,
                        endAyah = chunkEnd,
                        mode = "WORD",
                        onChoice = { choice ->
                            if (decisionDeferred.isActive) decisionDeferred.complete(choice)
                        }
                    )
                    triggerHaptic()

                    val choice = try {
                        withTimeoutOrNull(9000L) {
                            decisionDeferred.await()
                        } ?: "GOT_IT"
                    } catch (e: Exception) {
                        "GOT_IT"
                    }

                    if (activeConfidencePrompt.value != null) {
                        activeConfidencePrompt.value = null
                    }

                    if (choice == "STILL_SHAKY" && isActive) {
                        drillProgressState.value = HifzDrillProgressInfo(
                            phase = "CHUNK_REVIEW",
                            label = "Replaying Chunk: Ayahs ${chunkRange.first}–${chunkRange.last}",
                            currentRep = 1,
                            totalReps = 1,
                            isChunkReview = true
                        )

                        for (a in chunkRange) {
                            if (!isActive) break
                            currentPlayingSurah.value = surah
                            currentPlayingVerse.value = a
                            val aTiming = HifzWordTimingRepository.getAyahTiming(
                                getApplication(),
                                selectedReciter.value.id,
                                surah.number,
                                a
                            )
                            val aTotalWords = aTiming?.totalWords ?: 15
                            playWordRangeAndWait(
                                surahNumber = surah.number,
                                ayahNumber = a,
                                fromWordIndex = 1,
                                toWordIndex = aTotalWords,
                                loopCount = 1,
                                delayBetweenLoops = 0L
                            )
                        }
                    }
                }
            }

            // 4. SESSION RECAP: once full selected range is complete, play entire range once through, start to finish
            if (isActive) {
                drillProgressState.value = HifzDrillProgressInfo(
                    phase = "RECAP",
                    label = "Session Recap: Ayahs $startAyah–$endAyah",
                    currentRep = 1,
                    totalReps = 1,
                    isRecap = true
                )

                for (a in startAyah..endAyah) {
                    if (!isActive) break
                    currentPlayingSurah.value = surah
                    currentPlayingVerse.value = a
                    val aTiming = HifzWordTimingRepository.getAyahTiming(
                        getApplication(),
                        selectedReciter.value.id,
                        surah.number,
                        a
                    )
                    val aTotalWords = aTiming?.totalWords ?: 15
                    playWordRangeAndWait(
                        surahNumber = surah.number,
                        ayahNumber = a,
                        fromWordIndex = 1,
                        toWordIndex = aTotalWords,
                        loopCount = 1,
                        delayBetweenLoops = 0L
                    )
                }

                saveLastDrilledChunk(surah.number, startAyah, endAyah, startAyah, endAyah, "WORD")
                val totalAyahs = (endAyah - startAyah + 1).coerceAtLeast(1)
                recordHifzSession(
                    surahNumber = surah.number,
                    surahName = surah.nameEnglish,
                    surahNameArabic = surah.nameArabic,
                    startAyah = startAyah,
                    endAyah = endAyah,
                    mode = "Practice Drill",
                    totalAyahs = totalAyahs,
                    ayahsMemorized = totalAyahs,
                    ayahsMissed = 0,
                    repetitionsCompleted = memorizationWordRepeatCount.value,
                    notes = "Word pattern drill completed"
                )
                drillProgressState.value = HifzDrillProgressInfo()
                triggerHaptic()
                showToast("Masha'Allah! Session recap completed for Ayahs $startAyah–$endAyah")
            }
        }
    }

    fun playAyahPatternDrillSession(startFromAyah: Int? = null, forceStart: Boolean = false) {
        if (!forceStart) {
            if (isAudioPlaying.value || isAudioBuffering.value || hifzAudioState.value.isPlaying || hifzAudioState.value.isBuffering) {
                pauseMemorizationDrill()
                return
            }
            if (hifzAudioState.value.isPaused || isHifzPaused.value) {
                resumeMemorizationDrill()
                return
            }
        }

        stopMemorizationDrill()
        val surah = memorizationSurah.value
        val rangeStart = memorizationStartAyah.value
        val endRange = memorizationEndAyah.value
        val startRange = startFromAyah?.coerceIn(rangeStart, endRange) ?: rangeStart
        val patternList = parsePatternString(memorizationAyahPattern.value, maxVal = 15)
        val groupSize = patternList.size.coerceAtLeast(1)
        val additionalGroupRepeats = memorizationRepeatCount.value
        val totalGroupPasses = 1 + additionalGroupRepeats
        val delaySec = memorizationDelaySeconds.value
        val chunkReviewSize = memorizationChunkReviewSize.value

        ayahPatternJob?.cancel()
        ayahPatternJob = viewModelScope.launch {
            do {
                var currentAyahPointer = startRange
                var patternGroupIdx = 0
                val coveredAyahs = mutableListOf<Int>()
                var lastReviewedAyah = startRange - 1

                while (currentAyahPointer <= endRange && isActive) {
                    val groupEndAyah = (currentAyahPointer + groupSize - 1).coerceAtMost(endRange)
                    val groupRange = currentAyahPointer..groupEndAyah
                    memorizationCurrentPatternIndex.value = patternGroupIdx

                    // Recite the group according to the pattern values and apply repeat each step
                    for (groupPass in 1..totalGroupPasses) {
                        if (!isActive) break

                        for ((elemIdx, a) in groupRange.withIndex()) {
                            if (!isActive) break
                            val ayahRepCount = patternList[elemIdx % patternList.size]

                            for (rep in 1..ayahRepCount) {
                                if (!isActive) break
                                memorizationCurrentRepetition.value = rep
                                drillProgressState.value = HifzDrillProgressInfo(
                                    phase = "AYAH_REPEAT",
                                    label = "Ayah $a • Rep $rep of $ayahRepCount" +
                                            (if (totalGroupPasses > 1) " (Pass $groupPass/$totalGroupPasses)" else ""),
                                    currentRep = rep,
                                    totalReps = ayahRepCount
                                )
                                currentPlayingSurah.value = surah
                                currentPlayingVerse.value = a
                                revealVerse(a)
                                playAyahWithAutomaticSplits(
                                    surah = surah,
                                    ayahNumber = a,
                                    segmentRepeatCount = 1,
                                    delayMs = memorizationWordDelaySeconds.value * 1000L
                                )

                                if (delaySec > 0 && rep < ayahRepCount && isActive) {
                                    performDrillDelay(delaySec)
                                }
                            }

                            if (delaySec > 0 && isActive) {
                                performDrillDelay(delaySec)
                            }
                        }
                    }

                    for (a in groupRange) {
                        if (!coveredAyahs.contains(a)) {
                            coveredAyahs.add(a)
                        }
                    }

                    // 3. Play everything covered SO FAR as one continuous unit
                    val isFirstGroup = (patternGroupIdx == 0)
                    if (!isFirstGroup && coveredAyahs.size > groupRange.count() && isActive) {
                        val soFarRange = coveredAyahs.first()..coveredAyahs.last()
                        val repeatPasses = if (additionalGroupRepeats > 0) additionalGroupRepeats else 1
                        for (rep in 1..repeatPasses) {
                            if (!isActive) break
                            drillProgressState.value = HifzDrillProgressInfo(
                                phase = "LINKING",
                                label = "Linking: Ayahs ${soFarRange.first}–${soFarRange.last}",
                                currentRep = rep,
                                totalReps = repeatPasses,
                                isLinking = true
                            )

                            for (a in soFarRange) {
                                if (!isActive) break
                                currentPlayingSurah.value = surah
                                currentPlayingVerse.value = a
                                revealVerse(a)
                                playAyahWithAutomaticSplits(surah, a, segmentRepeatCount = 1, delayMs = 0L)
                            }

                            if (delaySec > 0 && rep < repeatPasses && isActive) {
                                performDrillDelay(delaySec)
                            }
                        }
                    }

                    // 5. CHUNK REVIEW: every 5 ayahs covered (configurable, default 5),
                    // after the "combined so far" step, do one additional full replay of just that 5-ayah chunk
                    val unreviewedCount = coveredAyahs.last() - lastReviewedAyah
                    if (unreviewedCount >= chunkReviewSize && isActive) {
                        val chunkStart = lastReviewedAyah + 1
                        val chunkEnd = chunkStart + chunkReviewSize - 1
                        val chunkRange = chunkStart..chunkEnd

                        drillProgressState.value = HifzDrillProgressInfo(
                            phase = "CHUNK_REVIEW",
                            label = "Chunk Review: Ayahs ${chunkRange.first}–${chunkRange.last}",
                            currentRep = 1,
                            totalReps = 1,
                            isChunkReview = true
                        )

                        for (a in chunkRange) {
                            if (!isActive) break
                            currentPlayingSurah.value = surah
                            currentPlayingVerse.value = a
                            revealVerse(a)
                            playAyahWithAutomaticSplits(surah, a, segmentRepeatCount = 1, delayMs = 0L)
                        }

                        lastReviewedAyah = chunkEnd
                        saveLastDrilledChunk(surah.number, startRange, endRange, chunkStart, chunkEnd, "AYAH")

                        val decisionDeferred = CompletableDeferred<String>()
                        activeConfidencePrompt.value = ConfidencePromptData(
                            surahNumber = surah.number,
                            surahName = surah.nameEnglish,
                            startAyah = chunkStart,
                            endAyah = chunkEnd,
                            mode = "AYAH",
                            onChoice = { choice ->
                                if (decisionDeferred.isActive) decisionDeferred.complete(choice)
                            }
                        )
                        triggerHaptic()

                        val choice = try {
                            withTimeoutOrNull(9000L) {
                                decisionDeferred.await()
                            } ?: "GOT_IT"
                        } catch (e: Exception) {
                            "GOT_IT"
                        }

                        if (activeConfidencePrompt.value != null) {
                            activeConfidencePrompt.value = null
                        }

                        if (choice == "STILL_SHAKY" && isActive) {
                            drillProgressState.value = HifzDrillProgressInfo(
                                phase = "CHUNK_REVIEW",
                                label = "Replaying Chunk: Ayahs ${chunkRange.first}–${chunkRange.last}",
                                currentRep = 1,
                                totalReps = 1,
                                isChunkReview = true
                            )

                            for (a in chunkRange) {
                                if (!isActive) break
                                currentPlayingSurah.value = surah
                                currentPlayingVerse.value = a
                                revealVerse(a)
                                playAyahWithAutomaticSplits(surah, a, segmentRepeatCount = 1, delayMs = 0L)
                            }
                        }
                    }

                    currentAyahPointer = groupEndAyah + 1
                    patternGroupIdx++
                }

                // 6. SESSION RECAP: once full selected range is complete, play entire range once through, start to finish
                if (isActive) {
                    val fullRange = startRange..endRange
                    drillProgressState.value = HifzDrillProgressInfo(
                        phase = "RECAP",
                        label = "Session Recap: Ayahs ${fullRange.first}–${fullRange.last}",
                        currentRep = 1,
                        totalReps = 1,
                        isRecap = true
                    )

                    for (a in fullRange) {
                        if (!isActive) break
                        currentPlayingSurah.value = surah
                        currentPlayingVerse.value = a
                        revealVerse(a)
                        playAyahWithAutomaticSplits(surah, a, segmentRepeatCount = 1, delayMs = 0L)
                    }

                    saveLastDrilledChunk(surah.number, startRange, endRange, startRange, endRange, "AYAH")
                    val totalAyahs = (endRange - startRange + 1).coerceAtLeast(1)
                    recordHifzSession(
                        surahNumber = surah.number,
                        surahName = surah.nameEnglish,
                        surahNameArabic = surah.nameArabic,
                        startAyah = startRange,
                        endAyah = endRange,
                        mode = "Practice Drill",
                        totalAyahs = totalAyahs,
                        ayahsMemorized = totalAyahs,
                        ayahsMissed = 0,
                        repetitionsCompleted = memorizationRepeatCount.value,
                        notes = "Ayah pattern drill completed"
                    )
                    if (!memorizationLoopRange.value) {
                        val batchSize = (endRange - startRange + 1).coerceAtLeast(1)
                        if (endRange < surah.totalVerses) {
                            val nextStart = endRange + 1
                            val nextEnd = (nextStart + batchSize - 1).coerceAtMost(surah.totalVerses)
                            memorizationStartAyah.value = nextStart
                            memorizationEndAyah.value = nextEnd
                            triggerHaptic()
                            showToast("Moving to next batch: Ayahs $nextStart–$nextEnd")
                            if (isActive) {
                                playAyahPatternDrillSession(startFromAyah = nextStart, forceStart = true)
                            }
                        } else {
                            isAudioPlaying.value = false
                            drillProgressState.value = HifzDrillProgressInfo()
                            triggerHaptic()
                            showToast("Masha'Allah! Completed Surah ${surah.nameEnglish}!")
                        }
                    } else if (isActive && delaySec > 0) {
                        performDrillDelay(delaySec)
                    }
                }
            } while (memorizationLoopRange.value && isActive)
        }
    }

    fun setMemorizationRepeatCount(count: Int) {
        val safeCount = count.coerceIn(1, 10)
        memorizationRepeatCount.value = safeCount
        memorizationCurrentRepetition.value = 1
        sharedPrefs.edit().putInt("memorization_repeat_count", safeCount).apply()
        triggerHaptic()
        showToast("Ayah Repeats: ${safeCount}x per pattern step")
    }

    fun revealAllVersesInSession() {
        val total = memorizationSurah.value.verses.size.takeIf { it > 0 }
            ?: selectedSurahForReading.value.verses.size.takeIf { it > 0 }
            ?: selectedSurahForReading.value.totalVerses
        revealedVersesInSession.value = (1..total).toSet()
        triggerHaptic()
        showToast("All verses revealed for this session")
    }

    fun resetRevealedVersesInSession() {
        revealedVersesInSession.value = emptySet()
        memorizationCurrentRepetition.value = 1
        triggerHaptic()
        showToast("Session reset: Upcoming verses are hidden")
    }

    fun setArabicFontSize(size: Int) {
        val safeSize = size.coerceIn(18, 42)
        arabicFontSizeSp.value = safeSize
        sharedPrefs.edit().putInt("quran_arabic_font_size", safeSize).apply()
    }

    fun revealVerse(verseNumber: Int) {
        if (verseNumber > 0 && !revealedVersesInSession.value.contains(verseNumber)) {
            revealedVersesInSession.value = revealedVersesInSession.value + verseNumber
        }
    }

    fun toggleVerseRevealInMemorization(verseNumber: Int) {
        if (verseNumber <= 0) return
        val current = revealedVersesInSession.value
        revealedVersesInSession.value = if (current.contains(verseNumber)) {
            current - verseNumber
        } else {
            current + verseNumber
        }
        triggerHaptic()
    }

    // Home Screen Widget Customization
    fun openCustomizeHomeSheet() {
        isCustomizeHomeSheetOpen.value = true
        triggerHaptic()
    }

    fun closeCustomizeHomeSheet() {
        isCustomizeHomeSheetOpen.value = false
    }

    fun toggleHomeWidgetVisibility(widget: HomeWidgetType) {
        val current = homeWidgetsVisibility.value.toMutableMap()
        val newVisible = !(current[widget] ?: true)
        current[widget] = newVisible
        homeWidgetsVisibility.value = current
        saveHomeWidgetsConfig()
        triggerHaptic()
    }

    fun moveHomeWidgetUp(widget: HomeWidgetType) {
        val list = homeWidgetsOrder.value.toMutableList()
        val index = list.indexOf(widget)
        if (index > 0) {
            list.removeAt(index)
            list.add(index - 1, widget)
            homeWidgetsOrder.value = list
            saveHomeWidgetsConfig()
            triggerHaptic()
        }
    }

    fun moveHomeWidgetDown(widget: HomeWidgetType) {
        val list = homeWidgetsOrder.value.toMutableList()
        val index = list.indexOf(widget)
        if (index in 0 until list.lastIndex) {
            list.removeAt(index)
            list.add(index + 1, widget)
            homeWidgetsOrder.value = list
            saveHomeWidgetsConfig()
            triggerHaptic()
        }
    }

    fun resetHomeWidgetsOrder() {
        homeWidgetsOrder.value = HomeWidgetType.defaultOrderedList()
        homeWidgetsVisibility.value = HomeWidgetType.values().associateWith { it.defaultVisible }
        saveHomeWidgetsConfig()
        triggerHaptic()
        showToast("Home feed reset to default layout")
    }

    private fun saveHomeWidgetsConfig() {
        val orderString = homeWidgetsOrder.value.joinToString(",") { it.id }
        val visibilityString = homeWidgetsVisibility.value.entries.joinToString(",") { "${it.key.id}:${it.value}" }
        sharedPrefs.edit()
            .putString("home_widgets_order", orderString)
            .putString("home_widgets_visibility", visibilityString)
            .apply()
    }

    private fun loadSavedHomeWidgetsConfig() {
        try {
            val orderString = sharedPrefs.getString("home_widgets_order", null)
            if (!orderString.isNullOrBlank()) {
                val types = orderString.split(",").mapNotNull { id ->
                    HomeWidgetType.values().firstOrNull { it.id == id }
                }.toMutableList()
                if (types.isNotEmpty()) {
                    // Ensure any missing types are appended at the end
                    val allTypes = HomeWidgetType.entries
                    val completeList = (types + allTypes.filter { !types.contains(it) }).distinct()
                    homeWidgetsOrder.value = completeList
                }
            } else {
                homeWidgetsOrder.value = HomeWidgetType.defaultOrderedList()
            }

            val visibilityString = sharedPrefs.getString("home_widgets_visibility", null)
            if (!visibilityString.isNullOrBlank()) {
                val map = mutableMapOf<HomeWidgetType, Boolean>()
                visibilityString.split(",").forEach { pair ->
                    val parts = pair.split(":")
                    if (parts.size == 2) {
                        val widget = HomeWidgetType.values().firstOrNull { it.id == parts[0] }
                        if (widget != null) {
                            map[widget] = parts[1].toBoolean()
                        }
                    }
                }
                if (map.isNotEmpty()) {
                    homeWidgetsVisibility.value = map
                }
            }
        } catch (e: Exception) {
            // fallback to default
        }
    }

    // Quick Access Customization Methods
    fun openCustomizeQuickAccessSheet() {
        isCustomizeQuickAccessSheetOpen.value = true
        triggerHaptic()
    }

    fun closeCustomizeQuickAccessSheet() {
        isCustomizeQuickAccessSheetOpen.value = false
    }

    // Shortcuts Bottom Sheet & Home Tutorial Replay Methods
    fun openShortcutsSheet() {
        isShortcutsSheetOpen.value = true
        triggerHaptic()
    }

    fun closeShortcutsSheet() {
        isShortcutsSheetOpen.value = false
    }

    fun requestHomeTutorial() {
        navigateTo(NoorDestination.HOME)
        isHomeTutorialRequested.value = true
    }

    fun consumeHomeTutorial(): Boolean {
        val requested = isHomeTutorialRequested.value
        isHomeTutorialRequested.value = false
        return requested
    }

    fun markHomeTutorialAsSeen() {
        hasSeenHomeTutorial.value = true
        sharedPrefs.edit().putBoolean("has_seen_home_tutorial", true).apply()
    }

    fun completeOnboarding(focusSelections: Set<String>) {
        sharedPrefs.edit()
            .putBoolean("has_seen_onboarding", true)
            .putBoolean("has_seen_onboarding_v4_complete", true)
            .putStringSet("onboarding_focus_selections", focusSelections)
            .apply()
        hasSeenOnboarding.value = true
    }

    fun skipOnboarding() {
        sharedPrefs.edit()
            .putBoolean("has_seen_onboarding", true)
            .putBoolean("has_seen_onboarding_v4_complete", true)
            .apply()
        hasSeenOnboarding.value = true
    }

    fun resetOnboarding() {
        sharedPrefs.edit()
            .putBoolean("has_seen_onboarding", false)
            .putBoolean("has_seen_onboarding_v4_complete", false)
            .putBoolean("has_seen_home_tutorial", false)
            .apply()
        hasSeenOnboarding.value = false
        hasSeenHomeTutorial.value = false
    }

    fun toggleQuickAccessTool(tool: QuickAccessTool) {
        val current = quickAccessTools.value.toMutableList()
        if (current.contains(tool)) {
            current.remove(tool)
        } else {
            if (current.size < 5) {
                current.add(tool)
            } else {
                val isAr = appLanguage.value == "ar"
                showToast(if (isAr) "تم اختيار ٥ أدوات بالفعل. ألغِ تحديد أداة لاختيار غيرها" else "5 tools already selected. Deselect one first to choose another.")
                return
            }
        }
        quickAccessTools.value = current
        saveQuickAccessToolsConfig()
        triggerHaptic()
    }

    fun moveQuickAccessToolUp(tool: QuickAccessTool) {
        val list = quickAccessTools.value.toMutableList()
        val index = list.indexOf(tool)
        if (index > 0) {
            list.removeAt(index)
            list.add(index - 1, tool)
            quickAccessTools.value = list
            saveQuickAccessToolsConfig()
            triggerHaptic()
        }
    }

    fun moveQuickAccessToolDown(tool: QuickAccessTool) {
        val list = quickAccessTools.value.toMutableList()
        val index = list.indexOf(tool)
        if (index in 0 until list.lastIndex) {
            list.removeAt(index)
            list.add(index + 1, tool)
            quickAccessTools.value = list
            saveQuickAccessToolsConfig()
            triggerHaptic()
        }
    }

    fun resetQuickAccessTools() {
        quickAccessTools.value = QuickAccessTool.defaultTools()
        saveQuickAccessToolsConfig()
        triggerHaptic()
        showToast("Quick access reset to default")
    }

    private fun saveQuickAccessToolsConfig() {
        val toolsString = quickAccessTools.value.joinToString(",") { it.id }
        sharedPrefs.edit().putString("quick_access_tools", toolsString).apply()
    }

    private fun loadSavedQuickAccessToolsConfig() {
        try {
            val toolsString = sharedPrefs.getString("quick_access_tools", null)
            if (!toolsString.isNullOrBlank()) {
                val tools = toolsString.split(",").mapNotNull { id ->
                    QuickAccessTool.entries.firstOrNull { it.id == id }
                }
                if (tools.size == 5) {
                    quickAccessTools.value = tools
                } else if (tools.isNotEmpty()) {
                    val remaining = QuickAccessTool.defaultTools().filter { !tools.contains(it) }
                    quickAccessTools.value = (tools + remaining).take(5)
                }
            }
        } catch (e: Exception) {}
    }

    fun releaseAudioPlayer() {
        try {
            audioProgressTrackerJob?.cancel()
            audioProgressTrackerJob = null
            mediaPlayer?.apply {
                try {
                    if (isPlaying) {
                        pause()
                    }
                } catch (e: Exception) {
                    // Ignore state transition errors
                }
                reset()
                release()
            }
            mediaPlayer = null
            isAudioPlaying.value = false
            isAudioBuffering.value = false
        } catch (e: Exception) {
            // ignore
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
        audioProgressTrackerJob?.cancel()
        releaseAudioPlayer()
    }

    // Daily Habit Tracking
    fun incrementHabit(habit: DailyHabitEntity) {
        viewModelScope.launch {
            repository.updateHabitProgress(habit, habit.currentCount + 1)
            triggerHaptic()
        }
    }

    fun addCustomHabit(title: String, target: Int, category: String) {
        viewModelScope.launch {
            repository.addCustomHabit(title, target, category)
            showToast("Habit added!")
        }
    }

    fun deleteHabit(habit: DailyHabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            showToast("Habit deleted")
        }
    }

    fun toggleFavorite(itemType: String, title: String, subtitle: String, details: String = "", source: String = "") {
        viewModelScope.launch {
            repository.toggleFavorite(
                type = itemType,
                title = title,
                arabicText = subtitle,
                translation = details,
                source = source.ifBlank { "Noor App" }
            )
            showToast("Favorites updated")
        }
    }

    // ============================================================
    // QURAN KHATMA COMPANION & LIFECYCLE MANAGEMENT
    // ============================================================

    fun createOrResetKhatma(
        days: Int,
        startDate: LocalDate = LocalDate.now(),
        sessionsCount: Int = 3,
        reminderEnabled: Boolean = true,
        reminderTime: String = "07:00 AM",
        title: String = "Personal Khatma"
    ) {
        viewModelScope.launch {
            val startDay = startDate.toEpochDay()
            val endDay = startDate.plusDays((days - 1).toLong().coerceAtLeast(0)).toEpochDay()
            val existing = repository.getActiveKhatmaPlanOnce()
            // If existing, keep readAyahsCount if user is simply adjusting goal
            val preservedRead = if (existing != null && !existing.isCompleted) existing.readAyahsCount else 0
            val coord = if (preservedRead > 0) KhatmaEngine.getAyahCoordinate(preservedRead) else KhatmaEngine.getAyahCoordinate(1)

            val newPlan = KhatmaPlanEntity(
                id = 1,
                title = title,
                totalDays = days.coerceIn(1, 365),
                startEpochDay = startDay,
                targetEndEpochDay = endDay,
                dailySessionsCount = sessionsCount.coerceIn(1, 5),
                reminderEnabled = reminderEnabled,
                reminderTime = reminderTime,
                totalAyahs = KhatmaEngine.TOTAL_QURAN_AYAHS,
                readAyahsCount = preservedRead,
                lastReadSurah = coord.surahNumber,
                lastReadAyah = coord.ayahNumber,
                isCompleted = false,
                completedAtEpochDay = null,
                daysTaken = null,
                paceAdjustmentType = "SPREAD",
                completedSessionsTodayBitmask = 0,
                lastSessionDateDay = LocalDate.now().toEpochDay(),
                updatedAt = System.currentTimeMillis()
            )
            repository.saveKhatmaPlan(newPlan)
            triggerHaptic()
            showToast("✨ Khatma plan started ($days Days). May Allah accept!")
            isKhatmaSetupSheetOpen.value = false
        }
    }

    fun updateKhatmaReadAyahs(newTotalRead: Int) {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val clamped = newTotalRead.coerceIn(0, KhatmaEngine.TOTAL_QURAN_AYAHS)
            val coord = if (clamped > 0) KhatmaEngine.getAyahCoordinate(clamped) else KhatmaEngine.getAyahCoordinate(1)
            val isNowCompleted = clamped >= KhatmaEngine.TOTAL_QURAN_AYAHS

            val todayEpoch = LocalDate.now().toEpochDay()
            val daysTaken = (todayEpoch - current.startEpochDay + 1).toInt().coerceAtLeast(1)

            val oldAyahs = current.readAyahsCount
            val diff = clamped - oldAyahs
            if (diff > 0) {
                addQuranAyahsReadToday(diff)
            }
            val oldJuz = if (oldAyahs > 0) KhatmaEngine.getAyahCoordinate(oldAyahs).juzNumber else 0
            val newJuz = if (clamped > 0) KhatmaEngine.getAyahCoordinate(clamped).juzNumber else 0

            val updated = current.copy(
                readAyahsCount = clamped,
                lastReadSurah = coord.surahNumber,
                lastReadAyah = coord.ayahNumber,
                isCompleted = isNowCompleted,
                completedAtEpochDay = if (isNowCompleted) todayEpoch else current.completedAtEpochDay,
                daysTaken = if (isNowCompleted) daysTaken else current.daysTaken,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveKhatmaPlan(updated)

            if (isNowCompleted) {
                triggerCompletionHaptic()
                recordKhatmaCompletionInHistory(updated)
                isKhatmaCompletionCelebrationOpen.value = true
            } else {
                if (newJuz > oldJuz && oldAyahs > 0) {
                    khatmaMilestoneModal.value = KhatmaMilestoneData(
                        title = "Juz $oldJuz Completed!",
                        subtitle = "Alhamdulillah! You entered Juz $newJuz ($clamped / 6,236 Ayahs)",
                        currentJuz = newJuz,
                        ayahsCompletedToday = (clamped - oldAyahs).coerceAtLeast(1),
                        totalAyahsRead = clamped,
                        percentage = clamped.toFloat() / KhatmaEngine.TOTAL_QURAN_AYAHS.toFloat()
                    )
                    triggerCompletionHaptic()
                } else if (clamped % 100 == 0 && clamped > oldAyahs) {
                    khatmaMilestoneModal.value = KhatmaMilestoneData(
                        title = "Milestone: $clamped Ayahs Completed!",
                        subtitle = "Barakallahu Feek! You are maintaining strong spiritual pace.",
                        currentJuz = newJuz,
                        ayahsCompletedToday = (clamped - oldAyahs).coerceAtLeast(1),
                        totalAyahsRead = clamped,
                        percentage = clamped.toFloat() / KhatmaEngine.TOTAL_QURAN_AYAHS.toFloat()
                    )
                    triggerHaptic()
                } else {
                    triggerHaptic()
                }
            }
        }
    }

    fun advanceKhatmaByAyahs(count: Int) {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val newTotal = (current.readAyahsCount + count).coerceIn(0, KhatmaEngine.TOTAL_QURAN_AYAHS)
            updateKhatmaReadAyahs(newTotal)
            showToast("Logged +$count Ayahs in Khatma")
        }
    }

    fun completeKhatmaSession(sessionIndex: Int, targetAyahs: Int) {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val todayEpoch = LocalDate.now().toEpochDay()
            val currentBitmask = if (current.lastSessionDateDay == todayEpoch) current.completedSessionsTodayBitmask else 0
            val newBitmask = currentBitmask or (1 shl sessionIndex)

            // Advance read count to match session target if needed
            val newReadCount = (current.readAyahsCount + targetAyahs).coerceIn(0, KhatmaEngine.TOTAL_QURAN_AYAHS)
            val coord = KhatmaEngine.getAyahCoordinate(newReadCount)
            val isNowCompleted = newReadCount >= KhatmaEngine.TOTAL_QURAN_AYAHS

            val updated = current.copy(
                readAyahsCount = newReadCount,
                lastReadSurah = coord.surahNumber,
                lastReadAyah = coord.ayahNumber,
                completedSessionsTodayBitmask = newBitmask,
                lastSessionDateDay = todayEpoch,
                isCompleted = isNowCompleted,
                completedAtEpochDay = if (isNowCompleted) todayEpoch else null,
                daysTaken = if (isNowCompleted) (todayEpoch - current.startEpochDay + 1).toInt() else null,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveKhatmaPlan(updated)
            triggerCompletionHaptic()
            showToast("Session completed! +$targetAyahs Ayahs ✓")

            if (isNowCompleted) {
                recordKhatmaCompletionInHistory(updated)
                isKhatmaCompletionCelebrationOpen.value = true
            }
        }
    }

    fun adjustKhatmaPace(strategy: String) {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val todayEpoch = LocalDate.now().toEpochDay()
            val currentDay = (todayEpoch - current.startEpochDay + 1).toInt().coerceIn(1, current.totalDays)
            val remainingDays = (current.totalDays - currentDay + 1).coerceAtLeast(1)
            val remainingAyahs = (KhatmaEngine.TOTAL_QURAN_AYAHS - current.readAyahsCount).coerceAtLeast(1)

            val updatedPlan = when (strategy) {
                "EXTEND" -> {
                    // Calculate extended days based on comfortable pace (e.g. 150 ayahs/day)
                    val comfortableDailyPace = 150
                    val neededDays = kotlin.math.ceil(remainingAyahs.toDouble() / comfortableDailyPace).toInt().coerceAtLeast(1)
                    val newTotalDays = (currentDay - 1) + neededDays
                    val newTargetEnd = current.startEpochDay + (newTotalDays - 1)
                    current.copy(
                        totalDays = newTotalDays,
                        targetEndEpochDay = newTargetEnd,
                        paceAdjustmentType = "EXTEND",
                        updatedAt = System.currentTimeMillis()
                    )
                }
                "GRADUAL" -> {
                    current.copy(
                        paceAdjustmentType = "GRADUAL",
                        updatedAt = System.currentTimeMillis()
                    )
                }
                else -> { // "SPREAD"
                    current.copy(
                        paceAdjustmentType = "SPREAD",
                        updatedAt = System.currentTimeMillis()
                    )
                }
            }

            repository.saveKhatmaPlan(updatedPlan)
            triggerHaptic()
            isKhatmaPaceAdjustSheetOpen.value = false
            showToast("Pace recalculated. Continue with barakah 🌿")
        }
    }

    fun changeKhatmaTotalDays(newDays: Int) {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val clampedDays = newDays.coerceIn(1, 365)
            val newEnd = current.startEpochDay + (clampedDays - 1)
            val updated = current.copy(
                totalDays = clampedDays,
                targetEndEpochDay = newEnd,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveKhatmaPlan(updated)
            triggerHaptic()
            showToast("Khatma duration updated to $clampedDays days")
        }
    }

    fun updateKhatmaReminder(enabled: Boolean, time: String) {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val updated = current.copy(
                reminderEnabled = enabled,
                reminderTime = time,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveKhatmaPlan(updated)
            showToast(if (enabled) "Daily Khatma reminder set for $time" else "Daily reminder disabled")
        }
    }

    fun continueKhatmaReading() {
        val state = khatmaDashboardState.value
        if (state != null) {
            val targetCoord = state.nextReadingPosition
            openKhatmaReadingAtAyah(targetCoord.surahNumber, targetCoord.ayahNumber)
        } else {
            navigateTo(NoorDestination.QURAN_SURAH_LIST)
        }
    }

    fun openKhatmaReadingAtAyah(surahNumber: Int, ayahNumber: Int) {
        val surah = QuranData.surahs.firstOrNull { it.number == surahNumber }
            ?: QuranData.completeSurahList.firstOrNull { it.number == surahNumber }
            ?: QuranData.surahs.first()
        selectSurahForReading(surah, ayahNumber)
    }

    fun markKhatmaCompleted() {
        viewModelScope.launch {
            val current = repository.getActiveKhatmaPlanOnce() ?: return@launch
            val todayEpoch = LocalDate.now().toEpochDay()
            val daysTaken = (todayEpoch - current.startEpochDay + 1).toInt().coerceAtLeast(1)
            val completedPlan = current.copy(
                readAyahsCount = KhatmaEngine.TOTAL_QURAN_AYAHS,
                isCompleted = true,
                completedAtEpochDay = todayEpoch,
                daysTaken = daysTaken,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveKhatmaPlan(completedPlan)
            recordKhatmaCompletionInHistory(completedPlan)
            triggerCompletionHaptic()
            isKhatmaCompletionCelebrationOpen.value = true
        }
    }

    private suspend fun recordKhatmaCompletionInHistory(plan: KhatmaPlanEntity) {
        val startDate = LocalDate.ofEpochDay(plan.startEpochDay).format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))
        val endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))
        val daysTaken = plan.daysTaken ?: (LocalDate.now().toEpochDay() - plan.startEpochDay + 1).toInt().coerceAtLeast(1)
        repository.recordKhatmaHistory(
            KhatmaHistoryEntity(
                title = plan.title,
                totalDays = plan.totalDays,
                daysTaken = daysTaken,
                totalAyahsRead = KhatmaEngine.TOTAL_QURAN_AYAHS,
                startDateFormatted = startDate,
                completionDateFormatted = endDate,
                completedAtTimestamp = System.currentTimeMillis()
            )
        )
    }

    fun deleteActiveKhatma() {
        viewModelScope.launch {
            repository.deleteActiveKhatmaPlan()
            showToast("Khatma plan reset")
        }
    }

    fun openKhatmaHub() {
        navigateTo(NoorDestination.QURAN_KHATMA)
    }

    fun triggerHaptic() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getApplication<Application>().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(25)
            }
        } catch (e: Exception) {
            // ignore if vibration not permitted
        }
    }

    // ============================================================
    // BACKUP & RESTORE UTILITIES
    // ============================================================

    suspend fun exportBackupJson(): String {
        return com.example.data.backup.BackupManager.generateBackupJson(
            context = getApplication(),
            db = db,
            userName = userName.value,
            userEmail = userEmail.value,
            userBio = userBio.value,
            appLanguage = appLanguage.value
        )
    }

    suspend fun importBackupJson(jsonString: String): com.example.data.backup.ImportResult {
        val result = com.example.data.backup.BackupManager.restoreFromJson(
            context = getApplication(),
            jsonString = jsonString,
            db = db,
            onProfileRestored = { name, email, bio, lang ->
                if (name.isNotBlank()) userName.value = name
                if (email.isNotBlank()) userEmail.value = email
                if (bio.isNotBlank()) userBio.value = bio
                if (lang.isNotBlank()) setAppLanguage(lang)
                isUserLoggedIn.value = true
                sharedPrefs.edit()
                    .putBoolean("user_logged_in", true)
                    .putString("user_name", name)
                    .putString("user_email", email)
                    .putString("user_bio", bio)
                    .apply()
            }
        )
        if (result.success) {
            showToast("Backup restored successfully!")
        } else {
            showToast(result.message)
        }
        return result
    }

    fun shareBackup(context: Context, backupJson: String) {
        com.example.data.backup.BackupManager.shareBackup(context, backupJson)
    }

    fun copyBackup(context: Context, backupJson: String) {
        com.example.data.backup.BackupManager.copyToClipboard(context, backupJson)
        showToast("Backup copied to clipboard!")
    }

    private fun triggerCompletionHaptic() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getApplication<Application>().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 100), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(150)
            }
        } catch (e: Exception) {
            // ignore
        }
    }
}
