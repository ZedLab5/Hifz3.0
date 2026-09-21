package com.example.ui.tasbih

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.DhikrItem
import com.example.ui.MainViewModel
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.AmiriQuranFontFamily
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalAppThemeMode
import com.example.ui.theme.BorderDividerLight
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DangerRedBgDark
import com.example.ui.theme.DangerRedBgLight
import com.example.ui.theme.DangerRedDark
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.ReadingThemes
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceElevated
import kotlinx.coroutines.launch

val standardDhikrPresets = listOf(
    DhikrItem(
        id = "subhanallah",
        arabicText = "سُبْحَانَ اللَّهِ",
        transliteration = "SubhanAllah",
        translation = "Glory be to Allah in His infinite perfection",
        defaultTarget = 33,
        virtue = "Fills the scale with good deeds",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/106.mp3",
        audioStartMs = 3400
    ),
    DhikrItem(
        id = "alhamdulillah",
        arabicText = "الْحَمْدُ لِلَّهِ",
        transliteration = "Alhamdulillah",
        translation = "All praise and gratitude belong to Allah",
        defaultTarget = 33,
        virtue = "Fills what is between the heavens and the earth",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/1.mp3",
        audioStartMs = 2800
    ),
    DhikrItem(
        id = "allahu_akbar",
        arabicText = "اللَّهُ أَكْبَرُ",
        transliteration = "Allahu Akbar",
        translation = "Allah is Greater than everything",
        defaultTarget = 34,
        virtue = "The most beloved words to Allah",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/106.mp3",
        audioStartMs = 3400
    ),
    DhikrItem(
        id = "astaghfirullah",
        arabicText = "أَسْتَغْفِرُ اللَّهَ",
        transliteration = "Astaghfirullah",
        translation = "I seek forgiveness from Allah",
        defaultTarget = 100,
        virtue = "Opens doors of sustenance and relieves distress",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/66.mp3",
        audioStartMs = 3100
    ),
    DhikrItem(
        id = "la_ilaha_illa_allah",
        arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ",
        transliteration = "La ilaha illa Allah",
        translation = "None has the right to be worshipped except Allah",
        defaultTarget = 100,
        virtue = "The finest statement of faith and key to Paradise",
        audioUrl = "https://everyayah.com/data/Alafasy_128kbps/047019.mp3",
        audioStartMs = 0
    ),
    DhikrItem(
        id = "salawat",
        arabicText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ",
        transliteration = "Allahumma Salli 'ala Muhammad",
        translation = "O Allah, send peace and blessings upon Muhammad",
        defaultTarget = 100,
        virtue = "Allah sends 10 blessings for every salawat",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/139.mp3",
        audioStartMs = 3500
    ),
    DhikrItem(
        id = "la_hawla",
        arabicText = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
        transliteration = "La hawla wa la quwwata illa billah",
        translation = "There is no power nor might except with Allah",
        defaultTarget = 33,
        virtue = "A treasure from the treasures of Paradise",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/254.mp3",
        audioStartMs = 3000
    ),
    DhikrItem(
        id = "subhanallahi_bihamdihi",
        arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
        transliteration = "SubhanAllahi wa bihamdihi",
        translation = "Glory and praise be to Allah",
        defaultTarget = 100,
        virtue = "Sins forgiven even if like the foam of the sea",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/77.mp3",
        audioStartMs = 3200
    ),
    DhikrItem(
        id = "subhanallah_wa_bihamdihi_azeem",
        arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
        transliteration = "SubhanAllahi wa bihamdihi, SubhanAllahil 'Azeem",
        translation = "Glory and praise to Allah, glory to Allah the Almighty",
        defaultTarget = 100,
        virtue = "Two phrases light on the tongue, heavy in the scale, beloved to Ar-Rahman",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/77.mp3",
        audioStartMs = 3200
    ),
    DhikrItem(
        id = "astaghfirullah_wa_atoobu_ilayh",
        arabicText = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
        transliteration = "Astaghfirullah wa atoobu ilayh",
        translation = "I seek forgiveness from Allah and repent to Him",
        defaultTarget = 100,
        virtue = "The Prophet ﷺ recited this repentance more than seventy times daily",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/66.mp3",
        audioStartMs = 3100
    ),
    DhikrItem(
        id = "hasbunallahu",
        arabicText = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
        transliteration = "Hasbunallahu wa ni'mal wakeel",
        translation = "Allah is sufficient for us and He is the best Disposer of affairs",
        defaultTarget = 33,
        virtue = "Uttered by Ibrahim (AS) and the Prophet ﷺ in times of hardship",
        audioUrl = "https://everyayah.com/data/Alafasy_128kbps/003173.mp3",
        audioStartMs = 0
    ),
    DhikrItem(
        id = "la_ilaha_illa_anta_subhanaka",
        arabicText = "لَا إِلَٰهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
        transliteration = "La ilaha illa Anta Subhanaka Inni Kuntu minaz-Zalimeen",
        translation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers",
        defaultTarget = 33,
        virtue = "Dua of Yunus (AS); removes grief, anxiety, and all distress",
        audioUrl = "https://everyayah.com/data/Alafasy_128kbps/021087.mp3",
        audioStartMs = 0
    ),
    DhikrItem(
        id = "radheetu_billahi_rabba",
        arabicText = "رَضِيتُ بِاللَّهِ رَبًّا وَبِالإِسْلاَمِ دِينًا وَبِمُحَمَّدٍ نَبِيًّا",
        transliteration = "Radheetu billahi Rabba, wa bil-Islami deena, wa bi-Muhammadin nabiyya",
        translation = "I am pleased with Allah as my Lord, Islam as my religion, and Muhammad as my Prophet",
        defaultTarget = 3,
        virtue = "Allah has promised to satisfy whoever says this three times in morning and evening",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/87.mp3",
        audioStartMs = 3300
    ),
    DhikrItem(
        id = "ya_hayyu_ya_qayyum",
        arabicText = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ",
        transliteration = "Ya Hayyu Ya Qayyum, bi-rahmatika astagheeth",
        translation = "O Ever-Living, O Sustainer, by Your mercy I seek assistance",
        defaultTarget = 33,
        virtue = "Invokes the Greatest Divine Names for immediate ease and guidance",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/86.mp3",
        audioStartMs = 3100
    ),
    DhikrItem(
        id = "tahleel_complete",
        arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
        transliteration = "La ilaha illallahu wahdahu la shareeka lah, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in Qadeer",
        translation = "None has the right to be worshipped except Allah alone without partner; His is sovereignty and praise, and He has power over all things",
        defaultTarget = 100,
        virtue = "Equivalent to freeing ten slaves, 100 rewards written, 100 sins wiped away",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/68.mp3",
        audioStartMs = 3400
    ),
    DhikrItem(
        id = "rabbana_aatina",
        arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
        transliteration = "Rabbana aatina fid-dunya hasanatan wa fil-aakhirati hasanatan wa qina 'adhaban-nar",
        translation = "Our Lord, give us in this world that which is good and in the Hereafter that which is good, and save us from the punishment of the Fire",
        defaultTarget = 33,
        virtue = "The most comprehensive and beloved supplication of the Prophet ﷺ",
        audioUrl = "https://everyayah.com/data/Alafasy_128kbps/002201.mp3",
        audioStartMs = 0
    ),
    DhikrItem(
        id = "subhanal_malikil_quddoos",
        arabicText = "سُبْحَانَ الْمَلِكِ الْقُدُّوسِ",
        transliteration = "Subhanal-Malikil-Quddoos",
        translation = "Glory be to the Sovereign, the Most Holy",
        defaultTarget = 3,
        virtue = "Recited three times concluding the Witr prayer",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/121.mp3",
        audioStartMs = 2900
    ),
    DhikrItem(
        id = "allahumma_ajirni",
        arabicText = "اللَّهُمَّ أَجِرْنِي مِنَ النَّارِ",
        transliteration = "Allahumma ajirni minan-nar",
        translation = "O Allah, protect and shield me from the Fire",
        defaultTarget = 7,
        virtue = "Whoever recites this 7 times, the Fire asks Allah to grant them protection",
        audioUrl = "https://www.hisnmuslim.com/audio/ar/74.mp3",
        audioStartMs = 3000
    )
)

data class MarbleDesignOption(
    val id: String,
    val nameRes: Int,
    val descRes: Int,
    val previewColors: List<Color>
)

val availableMarbleDesigns = listOf(
    MarbleDesignOption(
        id = "GOLD",
        nameRes = R.string.tasbih_marble_gold_name,
        descRes = R.string.tasbih_marble_gold_desc,
        previewColors = listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F), Color(0xFFE5A823), Color(0xFF6D3F04))
    ),
    MarbleDesignOption(
        id = "JADE",
        nameRes = R.string.tasbih_marble_jade_name,
        descRes = R.string.tasbih_marble_jade_desc,
        previewColors = listOf(Color(0xFFA7F3D0), Color(0xFF34D399), Color(0xFF059669), Color(0xFF022C22))
    ),
    MarbleDesignOption(
        id = "LAPIS",
        nameRes = R.string.tasbih_marble_lapis_name,
        descRes = R.string.tasbih_marble_lapis_desc,
        previewColors = listOf(Color(0xFF93C5FD), Color(0xFF3B82F6), Color(0xFF1D4ED8), Color(0xFF0B132B))
    ),
    MarbleDesignOption(
        id = "PEARL",
        nameRes = R.string.tasbih_marble_pearl_name,
        descRes = R.string.tasbih_marble_pearl_desc,
        previewColors = listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC), Color(0xFFCBD5E1), Color(0xFF64748B))
    ),
    MarbleDesignOption(
        id = "OBSIDIAN",
        nameRes = R.string.tasbih_marble_obsidian_name,
        descRes = R.string.tasbih_marble_obsidian_desc,
        previewColors = listOf(Color(0xFF94A3B8), Color(0xFF475569), Color(0xFF1E293B), Color(0xFF020617))
    ),
    MarbleDesignOption(
        id = "ROSE_AMBER",
        nameRes = R.string.tasbih_marble_rose_name,
        descRes = R.string.tasbih_marble_rose_desc,
        previewColors = listOf(Color(0xFFFFEDD5), Color(0xFFFB923C), Color(0xFFEA580C), Color(0xFF431407))
    ),
    MarbleDesignOption(
        id = "RUBY",
        nameRes = R.string.tasbih_marble_ruby_name,
        descRes = R.string.tasbih_marble_ruby_desc,
        previewColors = listOf(Color(0xFFFECDD3), Color(0xFFF43F5E), Color(0xFFE11D48), Color(0xFF4C0519))
    ),
    MarbleDesignOption(
        id = "TURQUOISE",
        nameRes = R.string.tasbih_marble_turquoise_name,
        descRes = R.string.tasbih_marble_turquoise_desc,
        previewColors = listOf(Color(0xFFCCFBF1), Color(0xFF2DD4BF), Color(0xFF0D9488), Color(0xFF042F2E))
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val count by viewModel.tasbihCount.collectAsStateWithLifecycle()
    com.example.ui.components.DndReadingEffect(viewModel)
    val target by viewModel.tasbihTarget.collectAsStateWithLifecycle()
    val selectedDhikrTitle by viewModel.selectedDhikr.collectAsStateWithLifecycle()
    val selectedDhikrArabic by viewModel.selectedDhikrArabic.collectAsStateWithLifecycle()
    val selectedDhikrMeaning by viewModel.selectedDhikrMeaning.collectAsStateWithLifecycle()
    val totalAllTime by viewModel.tasbihTotalAllTime.collectAsStateWithLifecycle()
    val lapsCompleted by viewModel.tasbihLapsCompleted.collectAsStateWithLifecycle()
    val currentTheme by viewModel.tasbihVisualTheme.collectAsStateWithLifecycle()
    val currentMarbleStyle by viewModel.tasbihMarbleStyle.collectAsStateWithLifecycle()
    val isHapticEnabled by viewModel.isTasbihHapticEnabled.collectAsStateWithLifecycle()
    val isSoundEnabled by viewModel.isTasbihSoundEnabled.collectAsStateWithLifecycle()
    val isAutoResetEnabled by viewModel.isTasbihAutoReset.collectAsStateWithLifecycle()
    val isBeadsVisible by viewModel.isTasbihBeadsVisible.collectAsStateWithLifecycle()
    val isTransliterationVisible by viewModel.isTasbihTransliterationVisible.collectAsStateWithLifecycle()
    val isTranslationVisible by viewModel.isTasbihTranslationVisible.collectAsStateWithLifecycle()
    val isDhikrCustom by viewModel.isSelectedDhikrCustom.collectAsStateWithLifecycle()
    val dhikrAudioUrl by viewModel.selectedDhikrAudioUrl.collectAsStateWithLifecycle()

    val readingThemeName by viewModel.sharedReadingTheme.collectAsStateWithLifecycle()
    val themeColors = remember(readingThemeName) { ReadingThemes.getThemeByName(readingThemeName) }

    val context = LocalContext.current
    var isAudioPlaying by remember { mutableStateOf(false) }
    var isAudioBuffering by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.reset()
                mediaPlayer.release()
            } catch (_: Exception) {}
        }
    }

    // Determine if active dhikr is preset or custom
    val isPresetDhikr = remember(selectedDhikrTitle, selectedDhikrArabic, isDhikrCustom) {
        !isDhikrCustom && standardDhikrPresets.any {
            it.arabicText == selectedDhikrArabic || it.transliteration == selectedDhikrTitle
        }
    }

    val currentPreset = remember(selectedDhikrTitle, selectedDhikrArabic) {
        standardDhikrPresets.firstOrNull {
            it.arabicText == selectedDhikrArabic || it.transliteration == selectedDhikrTitle
        }
    }
    val activeAudioUrl = currentPreset?.audioUrl ?: dhikrAudioUrl

    // Stop audio on dhikr change
    LaunchedEffect(selectedDhikrTitle, selectedDhikrArabic) {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
        } catch (_: Exception) {}
        isAudioPlaying = false
        isAudioBuffering = false
    }

    val togglePlayAudio: () -> Unit = {
        if (!isPresetDhikr || activeAudioUrl.isBlank()) {
            Toast.makeText(context, context.getString(R.string.tasbih_audio_not_available), Toast.LENGTH_SHORT).show()
        } else if (isAudioPlaying) {
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.reset()
            } catch (_: Exception) {}
            isAudioPlaying = false
            isAudioBuffering = false
        } else {
            try {
                mediaPlayer.reset()
                isAudioBuffering = true
                isAudioPlaying = true
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                mediaPlayer.setDataSource(activeAudioUrl)
                val audioStartMs = currentPreset?.audioStartMs ?: (if (activeAudioUrl.contains("hisnmuslim.com")) 3200 else 0)
                mediaPlayer.setOnPreparedListener { mp ->
                    isAudioBuffering = false
                    if (audioStartMs > 0) {
                        try {
                            mp.seekTo(audioStartMs)
                        } catch (_: Exception) {}
                    }
                    mp.start()
                }
                mediaPlayer.setOnCompletionListener {
                    isAudioPlaying = false
                    isAudioBuffering = false
                }
                mediaPlayer.setOnErrorListener { _, _, _ ->
                    isAudioPlaying = false
                    isAudioBuffering = false
                    Toast.makeText(context, "Audio playback error. Check connection.", Toast.LENGTH_SHORT).show()
                    true
                }
                mediaPlayer.prepareAsync()
            } catch (e: Exception) {
                isAudioPlaying = false
                isAudioBuffering = false
                Toast.makeText(context, "Unable to play audio: ${e.localizedMessage ?: "Unknown"}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var showCustomDhikrDialog by remember { mutableStateOf(false) }
    var showCustomTargetDialog by remember { mutableStateOf(false) }
    var showTasbihSettingsSheet by remember { mutableStateOf(false) }
    var showDhikrSelectionSheet by remember { mutableStateOf(false) }
    var showResetStatsConfirmDialog by remember { mutableStateOf(false) }
    var showTargetMenu by remember { mutableStateOf(false) }

    var customDhikrArabicInput by remember { mutableStateOf("") }
    var customDhikrTransInput by remember { mutableStateOf("") }
    var customDhikrMeaningInput by remember { mutableStateOf("") }
    var customTargetInput by remember { mutableStateOf("33") }

    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dhikrSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            NoorTopBar(
                title = stringResource(R.string.tasbih_screen_title),
                eyebrow = "NOOR",
                subtitle = stringResource(R.string.tasbih_screen_subtitle),
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = stringResource(R.string.action_back),
                isDark = themeColors.isDark,
                themeColors = themeColors,
                actions = {
                    NoorGlassIconButton(
                        onClick = { showTasbihSettingsSheet = true },
                        icon = Icons.Default.FormatPaint,
                        contentDescription = stringResource(R.string.tasbih_cd_change_theme)
                    )
                    NoorGlassIconButton(
                        onClick = { showTasbihSettingsSheet = true },
                        icon = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.tasbih_settings_dialog_title)
                    )
                }
            )
        },
        containerColor = Color.White,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Upper Section: Dhikr Card, Spaced Action Buttons with Labels, Round & Total, Large Counter
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 1. Dhikr Display (No white container, sits directly on background, balanced typography)
                ActiveDhikrInfoCard(
                    arabicText = selectedDhikrArabic,
                    transliteration = selectedDhikrTitle,
                    meaning = selectedDhikrMeaning,
                    count = count,
                    target = target,
                    showTransliteration = isTransliterationVisible,
                    showTranslation = isTranslationVisible,
                    themeColors = themeColors
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Action Buttons with Labels: Choose Zikr, Count : 33, Listen (if available), Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top
                ) {
                    val hasAudio = isPresetDhikr && activeAudioUrl.isNotBlank()
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(if (hasAudio) 18.dp else 36.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Option 1: Choose Zikr
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { showDhikrSelectionSheet = true }
                                )
                                .widthIn(min = 60.dp)
                        ) {
                            NoorActionRoundButton(
                                icon = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Choose Zikr",
                                themeColors = themeColors,
                                onClick = { showDhikrSelectionSheet = true }
                            )
                            Text(
                                text = "Choose Zikr",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = if (themeColors.isDark) themeColors.arabicText else Color(0xFF334155),
                                    fontSize = 12.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Option 2: Target / Limit Selector (Count : X) with Dropdown
                        Box(contentAlignment = Alignment.TopCenter) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { showTargetMenu = true }
                                    )
                                    .widthIn(min = 60.dp)
                            ) {
                                NoorActionRoundButton(
                                    icon = Icons.Default.Tune,
                                    contentDescription = "Count : $target",
                                    themeColors = themeColors,
                                    onClick = { showTargetMenu = true }
                                )
                                Text(
                                    text = "Count : $target",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = if (themeColors.isDark) themeColors.arabicText else Color(0xFF334155),
                                        fontSize = 12.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }

                            DropdownMenu(
                                expanded = showTargetMenu,
                                onDismissRequest = { showTargetMenu = false },
                                modifier = Modifier.background(if (themeColors.isDark) themeColors.surface else Color.White)
                            ) {
                                listOf(33, 99, 100, 1000).forEach { t ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "$t",
                                                fontWeight = if (target == t) FontWeight.Bold else FontWeight.Normal,
                                                color = if (target == t) themeColors.accent else themeColors.arabicText
                                            )
                                        },
                                        onClick = {
                                            viewModel.setTasbihTarget(t)
                                            showTargetMenu = false
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.tasbih_custom_target),
                                            color = themeColors.arabicText
                                        )
                                    },
                                    onClick = {
                                        showTargetMenu = false
                                        showCustomTargetDialog = true
                                    }
                                )
                            }
                        }

                        // Option 3: Listen (Recitation audio) - only visible if authentic audio is available
                        if (hasAudio) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = togglePlayAudio
                                    )
                                    .widthIn(min = 60.dp)
                            ) {
                                NoorActionRoundButton(
                                    icon = if (isAudioPlaying) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
                                    contentDescription = stringResource(R.string.tasbih_audio_listen),
                                    themeColors = themeColors,
                                    onClick = togglePlayAudio
                                )
                                Text(
                                    text = if (isAudioBuffering) "..." else (if (isAudioPlaying) stringResource(R.string.tasbih_audio_listening) else stringResource(R.string.tasbih_audio_listen)),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = if (isAudioPlaying) themeColors.accent else (if (themeColors.isDark) themeColors.arabicText else Color(0xFF334155)),
                                        fontSize = 12.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Option 4: Reset Counter
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { viewModel.resetTasbih() }
                                )
                                .widthIn(min = 60.dp)
                        ) {
                            NoorActionRoundButton(
                                icon = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                themeColors = themeColors,
                                onClick = { viewModel.resetTasbih() }
                            )
                            Text(
                                text = "Reset",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = if (themeColors.isDark) themeColors.arabicText else Color(0xFF334155),
                                    fontSize = 12.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Round Info & Total Count side-by-side without icon
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Round: ${lapsCompleted + 1}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = themeColors.translationText,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = themeColors.translationText.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = "Total: $totalAllTime",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = themeColors.translationText,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Large Counter Display (aesthetic large number format)
                Row(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { viewModel.incrementTasbih() }
                        ),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (themeColors.isDark) themeColors.arabicText else Color(0xFF0F172A),
                            fontSize = 56.sp
                        )
                    )
                    Text(
                        text = " / $target",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = themeColors.translationText.copy(alpha = 0.75f),
                            fontSize = 24.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            // Lower Section: Bottom Beads Arc with Beads Chooser placed at right bottom directly close to nav bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Interactive Counter Area: Bottom Beads Arc or Minimal Circle
                if (isBeadsVisible) {
                    when (currentTheme) {
                        "Minimal Circle" -> MinimalCircleCounterTheme(
                            count = count,
                            target = target,
                            laps = lapsCompleted,
                            themeColors = themeColors,
                            onIncrement = { viewModel.incrementTasbih() }
                        )
                        else -> TraditionalMarbleBeadsTheme(
                            count = count,
                            target = target,
                            laps = lapsCompleted,
                            marbleStyle = currentMarbleStyle,
                            themeColors = themeColors,
                            onIncrement = { viewModel.incrementTasbih() },
                            modifier = Modifier.offset(y = (-20).dp)
                        )
                    }
                } else {
                    HiddenBeadsTapSurface(
                        themeColors = themeColors,
                        onIncrement = { viewModel.incrementTasbih() }
                    )
                }

                // Floating Beads Chooser placed at the right bottom, directly adjacent to the nav bar
                if (currentTheme != "Minimal Circle" && isBeadsVisible) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 4.dp, bottom = 0.dp)
                    ) {
                        MarbleQuickSwitcherButton(
                            currentMarbleStyle = currentMarbleStyle,
                            themeColors = themeColors,
                            onSelectMarbleStyle = { viewModel.setTasbihMarbleStyle(it) }
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet: Dedicated Tasbih Settings & Marble Design Selection
    if (showTasbihSettingsSheet) {
        TasbihSettingsBottomSheet(
            sheetState = settingsSheetState,
            currentTheme = currentTheme,
            currentMarbleStyle = currentMarbleStyle,
            isHapticEnabled = isHapticEnabled,
            isSoundEnabled = isSoundEnabled,
            isAutoResetEnabled = isAutoResetEnabled,
            isBeadsVisible = isBeadsVisible,
            isTransliterationVisible = isTransliterationVisible,
            isTranslationVisible = isTranslationVisible,
            themeColors = themeColors,
            onSelectTheme = { viewModel.setTasbihTheme(it) },
            onSelectMarbleStyle = { viewModel.setTasbihMarbleStyle(it) },
            onToggleHaptic = { viewModel.setTasbihHaptic(it) },
            onToggleSound = { viewModel.setTasbihSound(it) },
            onToggleAutoReset = { viewModel.setTasbihAutoReset(it) },
            onToggleBeadsVisible = { viewModel.setTasbihBeadsVisible(it) },
            onToggleTransliteration = { viewModel.setTasbihTransliterationVisible(it) },
            onToggleTranslation = { viewModel.setTasbihTranslationVisible(it) },
            onResetAllStatsClick = { showResetStatsConfirmDialog = true },
            onDismiss = {
                scope.launch { settingsSheetState.hide() }.invokeOnCompletion {
                    showTasbihSettingsSheet = false
                }
            }
        )
    }

    // Modal Bottom Sheet: Dhikr Selection
    if (showDhikrSelectionSheet) {
        DhikrSelectionBottomSheet(
            selectedTitle = selectedDhikrTitle,
            sheetState = dhikrSheetState,
            themeColors = themeColors,
            onSelectDhikr = { preset ->
                viewModel.setDhikr(
                    dhikrTitle = preset.transliteration,
                    arabic = preset.arabicText,
                    meaning = preset.translation,
                    target = preset.defaultTarget,
                    virtue = preset.virtue,
                    audioUrl = preset.audioUrl,
                    isCustom = false
                )
                scope.launch { dhikrSheetState.hide() }.invokeOnCompletion {
                    showDhikrSelectionSheet = false
                }
            },
            onAddCustom = {
                scope.launch { dhikrSheetState.hide() }.invokeOnCompletion {
                    showDhikrSelectionSheet = false
                    showCustomDhikrDialog = true
                }
            },
            onDismiss = {
                scope.launch { dhikrSheetState.hide() }.invokeOnCompletion {
                    showDhikrSelectionSheet = false
                }
            }
        )
    }

    // Dialog: Add Custom Dhikr
    if (showCustomDhikrDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDhikrDialog = false },
            containerColor = themeColors.surface,
            title = {
                Text(
                    text = stringResource(R.string.tasbih_dialog_add_dhikr_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = customDhikrArabicInput,
                        onValueChange = { customDhikrArabicInput = it },
                        label = { Text(stringResource(R.string.tasbih_dialog_arabic_label)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = themeColors.arabicText,
                            unfocusedTextColor = themeColors.arabicText,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedContainerColor = themeColors.background,
                            unfocusedContainerColor = themeColors.background,
                            focusedLabelColor = themeColors.accent,
                            unfocusedLabelColor = themeColors.translationText
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customDhikrTransInput,
                        onValueChange = { customDhikrTransInput = it },
                        label = { Text(stringResource(R.string.tasbih_dialog_trans_label)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = themeColors.arabicText,
                            unfocusedTextColor = themeColors.arabicText,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedContainerColor = themeColors.background,
                            unfocusedContainerColor = themeColors.background,
                            focusedLabelColor = themeColors.accent,
                            unfocusedLabelColor = themeColors.translationText
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customDhikrMeaningInput,
                        onValueChange = { customDhikrMeaningInput = it },
                        label = { Text(stringResource(R.string.tasbih_dialog_meaning_label)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = themeColors.arabicText,
                            unfocusedTextColor = themeColors.arabicText,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedContainerColor = themeColors.background,
                            unfocusedContainerColor = themeColors.background,
                            focusedLabelColor = themeColors.accent,
                            unfocusedLabelColor = themeColors.translationText
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                val defaultMeaning = stringResource(R.string.tasbih_default_custom_meaning)
                val toastMsg = stringResource(R.string.tasbih_toast_custom_dhikr_set)
                Button(
                    onClick = {
                        if (customDhikrTransInput.isNotBlank()) {
                            viewModel.setDhikr(
                                dhikrTitle = customDhikrTransInput,
                                arabic = customDhikrArabicInput.ifBlank { customDhikrTransInput },
                                meaning = customDhikrMeaningInput.ifBlank { defaultMeaning },
                                target = 33,
                                virtue = "",
                                audioUrl = "",
                                isCustom = true
                            )
                            showCustomDhikrDialog = false
                            viewModel.showToast(toastMsg)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent)
                ) {
                    Text(stringResource(R.string.tasbih_dialog_use_dhikr_btn), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDhikrDialog = false }) {
                    Text(stringResource(R.string.action_cancel), color = themeColors.translationText)
                }
            }
        )
    }

    // Dialog: Custom Target
    if (showCustomTargetDialog) {
        AlertDialog(
            onDismissRequest = { showCustomTargetDialog = false },
            containerColor = themeColors.surface,
            title = {
                Text(
                    text = stringResource(R.string.tasbih_dialog_set_target_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText
                    )
                )
            },
            text = {
                OutlinedTextField(
                    value = customTargetInput,
                    onValueChange = { customTargetInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text(stringResource(R.string.tasbih_dialog_target_input_label)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = themeColors.arabicText,
                        unfocusedTextColor = themeColors.arabicText,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedContainerColor = themeColors.background,
                        unfocusedContainerColor = themeColors.background,
                        focusedLabelColor = themeColors.accent,
                        unfocusedLabelColor = themeColors.translationText
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = customTargetInput.toIntOrNull() ?: 33
                        if (num > 0) {
                            viewModel.setTasbihTarget(num)
                            showCustomTargetDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent)
                ) {
                    Text(stringResource(R.string.tasbih_dialog_set_target_btn), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomTargetDialog = false }) {
                    Text(stringResource(R.string.action_cancel), color = themeColors.translationText)
                }
            }
        )
    }

    // Dialog: Confirm Reset All Lifetime Stats
    if (showResetStatsConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetStatsConfirmDialog = false },
            containerColor = themeColors.surface,
            title = {
                Text(
                    text = stringResource(R.string.tasbih_reset_all_stats_confirm_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText
                    )
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.tasbih_reset_all_stats_confirm_msg),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = themeColors.translationText
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllTasbihStats()
                        showResetStatsConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (themeColors.isDark) DangerRedDark else DangerRedLight)
                ) {
                    Text(stringResource(R.string.tasbih_reset_count_action), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetStatsConfirmDialog = false }) {
                    Text(stringResource(R.string.action_cancel), color = themeColors.translationText)
                }
            }
        )
    }
}

// -------------------------------------------------------------
// DEDICATED MARBLE QUICK SWITCHER BUTTON (CONTINUOUS UPWARD PILL)
// -------------------------------------------------------------
@Composable
fun MarbleQuickSwitcherButton(
    currentMarbleStyle: String,
    themeColors: ReadingThemeColors,
    onSelectMarbleStyle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isReducedMotion = remember(context) {
        try {
            val scale = android.provider.Settings.Global.getFloat(
                context.contentResolver,
                android.provider.Settings.Global.TRANSITION_ANIMATION_SCALE,
                1.0f
            )
            scale == 0f
        } catch (e: Exception) {
            false
        }
    }

    val heightAnim = remember { Animatable(40f) }
    val contentAlphaAnim = remember { Animatable(0f) }
    val triggerAlphaAnim = remember { Animatable(1f) }

    // Two-stage staggered animation (Respects reduced motion):
    // On open: Shape grows upward first (~200ms), then swatches fade in (~140ms)
    // On close: Swatches fade out first (~90ms), then shape collapses down (~180ms)
    LaunchedEffect(isExpanded) {
        if (isReducedMotion) {
            if (isExpanded) {
                triggerAlphaAnim.snapTo(0f)
                heightAnim.snapTo(270f)
                contentAlphaAnim.snapTo(1f)
            } else {
                contentAlphaAnim.snapTo(0f)
                heightAnim.snapTo(40f)
                triggerAlphaAnim.snapTo(1f)
            }
        } else {
            if (isExpanded) {
                triggerAlphaAnim.animateTo(0f, tween(50, easing = LinearEasing))
                heightAnim.animateTo(
                    targetValue = 270f,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                )
                contentAlphaAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 140, easing = LinearEasing)
                )
            } else {
                contentAlphaAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 90, easing = LinearEasing)
                )
                heightAnim.animateTo(
                    targetValue = 40f,
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
                )
                triggerAlphaAnim.animateTo(1f, tween(60, easing = LinearEasing))
            }
        }
    }

    val labelAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 0f else 1f,
        animationSpec = if (isReducedMotion) snap() else tween(120),
        label = "beadLabelAlpha"
    )

    Column(
        modifier = modifier
            .size(width = 40.dp, height = 58.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Fixed layout slot (40dp x 40dp) so the menu footprint never changes and never disturbs the rest of the page
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Single continuous shape (Grows upward from fixed bottom anchor with pill-rounded corners at all heights)
            Surface(
                modifier = Modifier
                    .width(40.dp)
                    .layout { measurable, constraints ->
                        val currentHeight = heightAnim.value.dp.roundToPx()
                        val slotSize = 40.dp.roundToPx()
                        val placeable = measurable.measure(
                            constraints.copy(
                                minWidth = slotSize,
                                maxWidth = slotSize,
                                minHeight = currentHeight,
                                maxHeight = currentHeight
                            )
                        )
                        // Report fixed slot height to parent layout so page layout never shifts
                        layout(placeable.width, slotSize) {
                            // Anchor to the bottom and grow upward
                            val yOffset = slotSize - currentHeight
                            placeable.place(0, yOffset)
                        }
                    },
                shape = RoundedCornerShape(20.dp),
                color = if (themeColors.isDark) themeColors.surface else Color.White,
                shadowElevation = if (themeColors.isDark) 0.dp else 2.dp,
                border = null
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Collapsed state: single active bead in center of 40dp circle (22dp bead size)
                    if (heightAnim.value < 70f || !isExpanded) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .graphicsLayer { alpha = triggerAlphaAnim.value }
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { isExpanded = true }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(22.dp)) {
                                drawMarbleBead(
                                    center = Offset(size.width / 2f, size.height / 2f),
                                    style = currentMarbleStyle,
                                    radius = 10.dp.toPx()
                                )
                            }
                        }
                    }

                    // Expanded state: vertical column with close button + all 8 beads
                    if (heightAnim.value > 70f) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 5.dp)
                                .graphicsLayer { alpha = contentAlphaAnim.value },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Close affordance at top
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (themeColors.isDark) themeColors.border.copy(alpha = 0.35f)
                                        else themeColors.border.copy(alpha = 0.2f)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { isExpanded = false }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.action_cancel),
                                    tint = themeColors.translationText,
                                    modifier = Modifier.size(11.dp)
                                )
                            }

                            // 8 marble beads, each with 22dp canvas size
                            availableMarbleDesigns.forEach { design ->
                                val isSelected = design.id == currentMarbleStyle
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                    .background(
                                        if (isSelected) themeColors.accent.copy(alpha = 0.18f)
                                        else Color.Transparent
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            onSelectMarbleStyle(design.id)
                                            isExpanded = false
                                        }
                                    ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.size(22.dp)) {
                                        drawMarbleBead(
                                            center = Offset(size.width / 2f, size.height / 2f),
                                            style = design.id,
                                            radius = 10.dp.toPx()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Text label placed below the bead, outside the container (Never alters layout height)
        Text(
            text = stringResource(R.string.tasbih_marble_switcher_label),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = themeColors.translationText,
                fontSize = 10.5.sp
            ),
            modifier = Modifier.graphicsLayer { alpha = labelAlpha }
        )
    }
}

// -------------------------------------------------------------
// 1. THEME 1: PHYSICAL BEAD STRING UI WITH MULTI-MARBLE STYLES
// -------------------------------------------------------------
@Composable
fun TraditionalMarbleBeadsTheme(
    count: Int,
    target: Int,
    laps: Int,
    marbleStyle: String = "GOLD",
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Smooth animated count for infinite sliding bead transition
    val animatedCount by animateFloatAsState(
        targetValue = count.toFloat(),
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "animatedTasbihBeadCount"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onIncrement
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .align(Alignment.Center)
                .layout { measurable, constraints ->
                    val horizontalExtra = 32.dp.roundToPx() // Expand to fill entire screen edge-to-edge
                    val placeable = measurable.measure(
                        constraints.copy(
                            maxWidth = constraints.maxWidth + horizontalExtra,
                            minWidth = constraints.maxWidth + horizontalExtra
                        )
                    )
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(-horizontalExtra / 2, 0)
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // Bezier Curve Definition for the elevated bead arc
            val p0x = -40f
            val p0y = h * 0.85f
            val p1x = w * 0.44f
            val p1y = h * 0.15f
            val p2x = w + 40f
            val p2y = h * 0.30f

            // Parametric quadratic bezier evaluation function
            fun getPointOnCurve(u: Float): Offset {
                val inv = 1f - u
                val x = inv * inv * p0x + 2f * inv * u * p1x + u * u * p2x
                val y = inv * inv * p0y + 2f * inv * u * p1y + u * u * p2y
                return Offset(x, y)
            }

            // String color that complements marble style and dark mode
            val stringColor = getThreadColorForMarble(marbleStyle, themeColors.isDark)

            // 1. Draw the subtle connecting string thread
            val stringPath = Path().apply {
                moveTo(p0x, p0y)
                quadraticTo(p1x, p1y, p2x, p2y)
            }

            drawPath(
                path = stringPath,
                color = stringColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // 2. Bead geometry & infinite loop parameters
            val beadRadius = 22.dp.toPx()
            val beadSpacing = 0.088f // Spacing in curve parameter u
            val gapCenter = 0.63f
            val gapWidth = 0.24f

            val uLeftBase = gapCenter - gapWidth / 2f // Where counted beads accumulate
            val uRightBase = gapCenter + gapWidth / 2f // Where waiting beads arrive

            // Animated step progression
            val progress = animatedCount
            val s = (progress - kotlin.math.floor(progress.toDouble()).toFloat()).coerceIn(0f, 1f)

            // Render Left Beads (Counted queue moving down/left)
            for (k in 0..6) {
                val u = uLeftBase - (k + s) * beadSpacing
                if (u in -0.2f..1.2f) {
                    drawMarbleBead(getPointOnCurve(u), marbleStyle, beadRadius)
                }
            }

            // Render Crossing Bead (Moving seamlessly across the gap)
            val crossingU = (1f - s) * uRightBase + s * uLeftBase
            if (crossingU in -0.2f..1.2f) {
                drawMarbleBead(getPointOnCurve(crossingU), marbleStyle, beadRadius)
            }

            // Render Right Beads (Incoming queue arriving from right)
            for (m in 1..5) {
                val u = uRightBase + (m - s) * beadSpacing
                if (u in -0.2f..1.2f) {
                    drawMarbleBead(getPointOnCurve(u), marbleStyle, beadRadius)
                }
            }
        }
    }
}

// Helper to draw realistic 3D marble beads based on selected style
fun DrawScope.drawMarbleBead(center: Offset, style: String, radius: Float) {
    val bx = center.x
    val by = center.y

    // 1. Soft ambient drop shadow underneath
    drawCircle(
        color = Color(0x33000000),
        radius = radius * 1.06f,
        center = Offset(bx + 1.5.dp.toPx(), by + 3.2.dp.toPx())
    )
    drawCircle(
        color = Color(0x20000000),
        radius = radius * 0.96f,
        center = Offset(bx + 0.8.dp.toPx(), by + 1.6.dp.toPx())
    )

    // 2. Base Spherical Gradient Definition
    val gradientColors = when (style) {
        "JADE" -> listOf(
            Color(0xFFD1FAE5), // Translucent Highlight
            Color(0xFF6EE7B7), // Mint Luster
            Color(0xFF10B981), // Imperial Jade
            Color(0xFF047857), // Rich Forest
            Color(0xFF064E3B), // Deep Jade
            Color(0xFF022C22)  // Shadow Rim
        )
        "LAPIS" -> listOf(
            Color(0xFFBFDBFE), // Highlight Glint
            Color(0xFF60A5FA), // Cobalt Luster
            Color(0xFF2563EB), // Royal Ultramarine
            Color(0xFF1D4ED8), // Deep Lapis
            Color(0xFF1E3A8A), // Midnight Navy
            Color(0xFF0B132B)  // Dark Shadow
        )
        "PEARL" -> listOf(
            Color(0xFFFFFFFF), // Brilliant Nacre
            Color(0xFFF8FAFC), // Silky Ivory
            Color(0xFFF1F5F9), // Iridescent Silver
            Color(0xFFE2E8F0), // Soft Oyster
            Color(0xFFCBD5E1), // Pearl Rim
            Color(0xFF94A3B8)  // Shadow Rim
        )
        "OBSIDIAN" -> listOf(
            Color(0xFFE2E8F0), // Sharp Surface Reflection
            Color(0xFF94A3B8), // Smoky Glint
            Color(0xFF475569), // Slate Onyx
            Color(0xFF1E293B), // Midnight Obsidian
            Color(0xFF0F172A), // Deep Jet
            Color(0xFF020617)  // Pitch Shadow
        )
        "ROSE_AMBER" -> listOf(
            Color(0xFFFEF3C7), // Warm Sunburst Highlight
            Color(0xFFFDE68A), // Translucent Amber
            Color(0xFFF59E0B), // Glowing Honey
            Color(0xFFD97706), // Rich Carnelian
            Color(0xFF9A3412), // Deep Cinnamon
            Color(0xFF451A03)  // Dark Rim
        )
        "RUBY" -> listOf(
            Color(0xFFFFE4E6), // Brilliant Glint
            Color(0xFFFDA4AF), // Pinkish Fire
            Color(0xFFF43F5E), // Crimson Jewel
            Color(0xFFBE123C), // Deep Pigeon-Blood Ruby
            Color(0xFF881337), // Wine Shadow
            Color(0xFF4C0519)  // Shadow Rim
        )
        "TURQUOISE" -> listOf(
            Color(0xFFCCFBF1), // Aqua Shine
            Color(0xFF5EEAD4), // Persian Sky Blue
            Color(0xFF14B8A6), // Vibrant Turquoise
            Color(0xFF0F766E), // Deep Mineral Green
            Color(0xFF115E59), // Earthy Teal
            Color(0xFF042F2E)  // Shadow Rim
        )
        else -> listOf( // "GOLD" (Default)
            Color(0xFFFFFDE7), // Platinum Glint
            Color(0xFFFFF176), // Radiant 24K Gold
            Color(0xFFFFD54F), // Luminous Gold
            Color(0xFFF59E0B), // Deep Amber Gold
            Color(0xFFB45309), // Bronze Gold
            Color(0xFF78350F)  // Dark Spherical Rim
        )
    }

    // 3. 3D Spherical Radial Gradient
    drawCircle(
        brush = Brush.radialGradient(
            colors = gradientColors,
            center = Offset(bx - radius * 0.36f, by - radius * 0.36f),
            radius = radius * 1.32f
        ),
        radius = radius,
        center = Offset(bx, by)
    )

    // 4. Material-Specific Realistic Textures & Subsurface Scattering
    when (style) {
        "JADE" -> {
            // Translucent organic mineral vein
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x35A7F3D0), Color.Transparent),
                    center = Offset(bx + radius * 0.2f, by + radius * 0.1f),
                    radius = radius * 0.6f
                ),
                radius = radius * 0.55f,
                center = Offset(bx + radius * 0.2f, by + radius * 0.1f)
            )
        }
        "LAPIS" -> {
            // Pyrite gold specks inside the stone
            val goldPyrite = Color(0xCCFCD34D)
            drawCircle(goldPyrite, radius * 0.055f, Offset(bx + radius * 0.22f, by - radius * 0.15f))
            drawCircle(goldPyrite, radius * 0.045f, Offset(bx - radius * 0.12f, by + radius * 0.28f))
            drawCircle(goldPyrite, radius * 0.04f, Offset(bx + radius * 0.35f, by + radius * 0.2f))
        }
        "PEARL" -> {
            // Iridescent nacre sheen ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x40FCE7F3), Color(0x30E0F2FE), Color.Transparent),
                    center = Offset(bx + radius * 0.2f, by + radius * 0.2f),
                    radius = radius * 0.7f
                ),
                radius = radius * 0.65f,
                center = Offset(bx + radius * 0.2f, by + radius * 0.2f)
            )
        }
        "ROSE_AMBER" -> {
            // Fiery translucent inner core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x60FBBF24), Color(0x30EA580C), Color.Transparent),
                    center = Offset(bx + radius * 0.15f, by + radius * 0.15f),
                    radius = radius * 0.65f
                ),
                radius = radius * 0.6f,
                center = Offset(bx + radius * 0.15f, by + radius * 0.15f)
            )
        }
        "TURQUOISE" -> {
            // Organic matrix mineral vein
            drawCircle(
                color = Color(0x28451A03),
                radius = radius * 0.07f,
                center = Offset(bx + radius * 0.25f, by - radius * 0.1f)
            )
            drawCircle(
                color = Color(0x22451A03),
                radius = radius * 0.09f,
                center = Offset(bx - radius * 0.18f, by + radius * 0.22f)
            )
        }
        "GOLD" -> {
            // Anisotropic metallic glow band
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x40FFFFFF), Color.Transparent),
                    center = Offset(bx + radius * 0.28f, by + radius * 0.28f),
                    radius = radius * 0.45f
                ),
                radius = radius * 0.4f,
                center = Offset(bx + radius * 0.28f, by + radius * 0.28f)
            )
        }
    }

    // 5. Ambient Subsurface Fresnel Reflection (Bottom-Right Rim Light)
    val rimLightAlpha = when (style) {
        "PEARL" -> 0.45f
        "OBSIDIAN" -> 0.20f
        "GOLD" -> 0.40f
        else -> 0.32f
    }
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.White.copy(alpha = rimLightAlpha)),
            center = Offset(bx, by),
            radius = radius
        ),
        radius = radius,
        center = Offset(bx, by)
    )

    // 6. Primary Brilliant Specular Highlight
    val specularAlpha = if (style == "OBSIDIAN" || style == "PEARL" || style == "GOLD") 0.96f else 0.90f
    drawCircle(
        color = Color.White.copy(alpha = specularAlpha),
        radius = radius * 0.22f,
        center = Offset(bx - radius * 0.36f, by - radius * 0.36f)
    )

    // 7. Secondary Micro Specular Pinpoint
    drawCircle(
        color = Color.White.copy(alpha = specularAlpha * 0.75f),
        radius = radius * 0.075f,
        center = Offset(bx - radius * 0.20f, by - radius * 0.46f)
    )
}

fun getThreadColorForMarble(style: String, isDark: Boolean): Color {
    return when (style) {
        "JADE" -> if (isDark) Color(0xFF064E3B) else Color(0xFF065F46).copy(alpha = 0.65f)
        "LAPIS" -> if (isDark) Color(0xFF1E3A8A) else Color(0xFF1E40AF).copy(alpha = 0.65f)
        "PEARL", "OBSIDIAN" -> if (isDark) Color(0xFF475569) else Color(0xFF64748B).copy(alpha = 0.6f)
        "ROSE_AMBER" -> if (isDark) Color(0xFF7C2D12) else Color(0xFF9A3412).copy(alpha = 0.65f)
        "RUBY" -> if (isDark) Color(0xFF881337) else Color(0xFF9F1239).copy(alpha = 0.65f)
        "TURQUOISE" -> if (isDark) Color(0xFF134E4A) else Color(0xFF0F766E).copy(alpha = 0.65f)
        else -> if (isDark) Color(0xFF78350F).copy(alpha = 0.9f) else Color(0xFF92400E).copy(alpha = 0.65f)
    }
}

// -------------------------------------------------------------
// 2. THEME 2: MINIMAL CIRCLE (DOUBLE LAYER TAP BUTTON)
// -------------------------------------------------------------
@Composable
fun MinimalCircleCounterTheme(
    count: Int,
    target: Int,
    laps: Int,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "circlePressAnim"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .scale(scale)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onIncrement
                ),
            contentAlignment = Alignment.Center
        ) {
            // Layer 1: Outer Pedestal Ring (Slightly larger than beads mode, soft shadow, no border)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(
                        elevation = if (themeColors.isDark) 0.dp else 4.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.08f),
                        spotColor = Color.Black.copy(alpha = 0.08f)
                    )
                    .clip(CircleShape)
                    .background(if (themeColors.isDark) themeColors.surface else Color.White)
            )

            // Layer 2: Inner Primary Circular Button Stacked Directly On Top
            Surface(
                modifier = Modifier
                    .size(128.dp)
                    .shadow(
                        elevation = if (themeColors.isDark) 0.dp else 8.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.12f),
                        spotColor = Color.Black.copy(alpha = 0.12f)
                    ),
                shape = CircleShape,
                color = if (themeColors.isDark) themeColors.surface else Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (themeColors.isDark) themeColors.surface else Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = stringResource(R.string.tasbih_tap_to_count),
                            tint = if (themeColors.isDark) themeColors.accent else Color(0xFF107C41),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = stringResource(R.string.tasbih_tap_button_cue),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (themeColors.isDark) themeColors.accent else Color(0xFF107C41),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER SUB-COMPONENTS
// -------------------------------------------------------------

@Composable
fun NoorActionRoundButton(
    icon: ImageVector,
    contentDescription: String,
    themeColors: ReadingThemeColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (themeColors.isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
        modifier = modifier.size(46.dp),
        shadowElevation = if (themeColors.isDark) 0.dp else 0.5.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (themeColors.isDark) themeColors.arabicText else Color(0xFF334155),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun ActiveDhikrInfoCard(
    arabicText: String,
    transliteration: String,
    meaning: String,
    count: Int = 0,
    target: Int = 33,
    showTransliteration: Boolean = true,
    showTranslation: Boolean = true,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    modifier: Modifier = Modifier
) {
    // Remove any Arabic characters and parentheses from the phonetic transliteration string
    val cleanTransliteration = remember(transliteration) {
        transliteration
            .replace(Regex("[\\p{InArabic}\\u0600-\\u06FF]"), "")
            .replace("()", "")
            .replace("(", "")
            .replace(")", "")
            .trim()
    }

    val cleanMeaning = remember(meaning) {
        meaning
            .replace("()", "")
            .trim()
    }

    // Text sits directly on the background canvas - no white container box or shadow
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Phonetic Transliteration (Balanced font weight - softened from SemiBold to Medium)
        if (showTransliteration && cleanTransliteration.isNotBlank()) {
            Text(
                text = cleanTransliteration,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (themeColors.isDark) themeColors.arabicText.copy(alpha = 0.9f) else Color(0xFF334155),
                    fontSize = 15.sp,
                    letterSpacing = 0.25.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 2. Arabic Calligraphy Text (Explicit Amiri Quran Font for clean rendering on all devices)
        if (arabicText.isNotBlank()) {
            Text(
                text = arabicText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = AmiriQuranFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = themeColors.arabicText,
                    fontSize = 32.sp,
                    lineHeight = 48.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 3. English Meaning / Translation (Balanced font size - upgraded from 12.5sp to 14sp with clean regular weight)
        if (showTranslation && cleanMeaning.isNotBlank()) {
            Text(
                text = cleanMeaning,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    color = if (themeColors.isDark) themeColors.translationText else Color(0xFF475569),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Minimalist Tap Surface when Tasbih Beads are Hidden
 */
@Composable
fun HiddenBeadsTapSurface(
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onIncrement
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = if (themeColors.isDark) themeColors.border.copy(alpha = 0.35f) else Color(0xFFF1F5F9),
            shadowElevation = if (themeColors.isDark) 0.dp else 2.dp,
            modifier = Modifier
                .size(240.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = stringResource(R.string.tasbih_tap_to_count),
                        tint = themeColors.accent,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.tasbih_tap_button_cue),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText,
                            fontSize = 17.sp,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.tasbih_tap_to_count_prompt),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.translationText,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrSelectionBottomSheet(
    selectedTitle: String,
    sheetState: androidx.compose.material3.SheetState,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    onSelectDhikr: (DhikrItem) -> Unit,
    onAddCustom: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = rememberTasbihSettingsColors(themeColors)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.containerBg,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.tasbih_select_dhikr_sheet_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.textTitle,
                        fontSize = 19.sp
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_cancel),
                        tint = colors.textTitle
                    )
                }
            }

            // Add Custom Dhikr Option (No borders, soft shadow)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAddCustom),
                shape = RoundedCornerShape(14.dp),
                color = colors.cardBg,
                shadowElevation = if (themeColors.isDark) 0.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = colors.accentColor,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.tasbih_custom_chip),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.accentColor,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = stringResource(R.string.tasbih_dialog_add_dhikr_title),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = colors.textSub,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Dhikr Presets List (Vertical stacking: Arabic on top, English & transliteration below, recommended count)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 28.dp)
            ) {
                items(standardDhikrPresets, key = { it.id }) { preset ->
                    val isSelected = selectedTitle.contains(preset.transliteration, ignoreCase = true)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectDhikr(preset) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) colors.accentColor.copy(alpha = 0.15f) else colors.cardBg,
                        border = null,
                        shadowElevation = if (themeColors.isDark) 0.dp else 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Top row: Full width Arabic text & selection indicator icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = preset.arabicText,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = AmiriQuranFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        color = colors.textTitle,
                                        fontSize = 22.sp,
                                        lineHeight = 32.sp,
                                        textAlign = TextAlign.End
                                    ),
                                    modifier = Modifier.weight(1f)
                                )

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = colors.accentColor,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .padding(start = 6.dp)
                                    )
                                }
                            }

                            // Middle: Transliteration & English translation
                            Text(
                                text = preset.transliteration,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textTitle,
                                    fontSize = 13.5.sp
                                )
                            )

                            Text(
                                text = preset.translation,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.textSub,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            )

                            // Bottom: Recommended repetition pill at the bottom of the container
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = colors.badgeBg
                            ) {
                                Text(
                                    text = stringResource(R.string.tasbih_recommended_reps, preset.defaultTarget),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = colors.badgeText,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DEDICATED TASBIH SETTINGS MODAL BOTTOM SHEET (LIGHT MODE STYLED)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihSettingsBottomSheet(
    sheetState: androidx.compose.material3.SheetState,
    currentTheme: String,
    currentMarbleStyle: String,
    isHapticEnabled: Boolean,
    isSoundEnabled: Boolean,
    isAutoResetEnabled: Boolean,
    isBeadsVisible: Boolean,
    isTransliterationVisible: Boolean,
    isTranslationVisible: Boolean,
    themeColors: ReadingThemeColors,
    onSelectTheme: (String) -> Unit,
    onSelectMarbleStyle: (String) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleAutoReset: (Boolean) -> Unit,
    onToggleBeadsVisible: (Boolean) -> Unit,
    onToggleTransliteration: (Boolean) -> Unit,
    onToggleTranslation: (Boolean) -> Unit,
    onResetAllStatsClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = rememberTasbihSettingsColors(themeColors)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.containerBg,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.dividerColor)
            )
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 4.dp)
        ) {
            // Header Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(colors.iconBadgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = colors.accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.tasbih_settings_dialog_title),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textTitle,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = stringResource(R.string.tasbih_settings_subtitle),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.textSub,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.action_cancel),
                            tint = colors.textTitle
                        )
                    }
                }
            }

            // Section 1: Visual Style Selection Tabs (Realistic Beads vs Minimal Circle)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.tasbih_section_theme_display),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.accentColor,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Realistic Beads", "Minimal Circle").forEach { themeName ->
                            val isSelected = currentTheme == themeName
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectTheme(themeName) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) colors.accentColor.copy(alpha = 0.15f) else colors.cardBg,
                                border = null,
                                shadowElevation = 0.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (themeName == "Realistic Beads") stringResource(R.string.tasbih_theme_realistic_beads) else stringResource(R.string.tasbih_theme_minimal_circle),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) colors.accentColor else colors.textTitle,
                                            fontSize = 13.sp
                                        )
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = colors.accentColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Sensory & Feedback Controls
            item {
                HorizontalDivider(color = colors.dividerColor)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.tasbih_section_feedback),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.accentColor,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    // Haptic Vibration toggle
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = colors.cardBg,
                        shadowElevation = 0.dp,
                        border = null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Vibration,
                                    contentDescription = null,
                                    tint = colors.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = stringResource(R.string.tasbih_haptic_feedback_title),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textTitle,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Text(
                                        text = stringResource(R.string.tasbih_haptic_feedback_desc),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textSub,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                            Switch(
                                checked = isHapticEnabled,
                                onCheckedChange = onToggleHaptic,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.accentColor,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = colors.dividerColor
                                )
                            )
                        }
                    }

                    // Sound Feedback toggle
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = colors.cardBg,
                        shadowElevation = 0.dp,
                        border = null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = colors.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = stringResource(R.string.tasbih_sound_feedback_title),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textTitle,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Text(
                                        text = stringResource(R.string.tasbih_sound_feedback_desc),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textSub,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                            Switch(
                                checked = isSoundEnabled,
                                onCheckedChange = onToggleSound,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.accentColor,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = colors.dividerColor
                                )
                            )
                        }
                    }
                }
            }

            // Section 3: Text & Translation Display
            item {
                HorizontalDivider(color = colors.dividerColor)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.tasbih_section_text_display),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.accentColor,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    // Phonetic Transliteration toggle
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = colors.cardBg,
                        shadowElevation = 0.dp,
                        border = null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = colors.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = stringResource(R.string.tasbih_show_transliteration_title),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textTitle,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Text(
                                        text = stringResource(R.string.tasbih_show_transliteration_desc),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textSub,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                            Switch(
                                checked = isTransliterationVisible,
                                onCheckedChange = onToggleTransliteration,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.accentColor,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = colors.dividerColor
                                )
                            )
                        }
                    }

                    // English Translation toggle
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = colors.cardBg,
                        shadowElevation = 0.dp,
                        border = null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = null,
                                    tint = colors.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = stringResource(R.string.tasbih_show_translation_title),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textTitle,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Text(
                                        text = stringResource(R.string.tasbih_show_translation_desc),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textSub,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                            Switch(
                                checked = isTranslationVisible,
                                onCheckedChange = onToggleTranslation,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.accentColor,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = colors.dividerColor
                                )
                            )
                        }
                    }
                }
            }

            // Section 4: Counter Behavior
            item {
                HorizontalDivider(color = colors.dividerColor)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.tasbih_section_behavior),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.accentColor,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    // Auto-Reset toggle
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = colors.cardBg,
                        shadowElevation = 0.dp,
                        border = null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.tasbih_auto_reset_title),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textTitle,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = stringResource(R.string.tasbih_auto_reset_desc),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colors.textSub,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                            Switch(
                                checked = isAutoResetEnabled,
                                onCheckedChange = onToggleAutoReset,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.accentColor,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = colors.dividerColor
                                )
                            )
                        }
                    }
                }
            }

            // Section 4: Lifetime Stats Action
            item {
                HorizontalDivider(color = colors.dividerColor)
                Spacer(modifier = Modifier.height(4.dp))
                val resetBgColor = if (themeColors.isDark) Color(0xFF2D1E1E) else if (LocalAppThemeMode.current == AppThemeMode.WARM) Color(0xFFFFF2EC) else Color(0xFFFEF2F2)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onResetAllStatsClick),
                    shape = RoundedCornerShape(14.dp),
                    color = resetBgColor,
                    shadowElevation = 0.dp,
                    border = null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.tasbih_reset_all_stats_btn),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444),
                                fontSize = 13.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

private data class TasbihSettingsColorPalette(
    val containerBg: Color,
    val cardBg: Color,
    val textTitle: Color,
    val textSub: Color,
    val dividerColor: Color,
    val badgeBg: Color,
    val badgeText: Color,
    val accentColor: Color,
    val iconBadgeBg: Color
)

@Composable
private fun rememberTasbihSettingsColors(themeColors: ReadingThemeColors): TasbihSettingsColorPalette {
    val isDark = themeColors.isDark
    val isWarm = LocalAppThemeMode.current == AppThemeMode.WARM

    return remember(isDark, isWarm, themeColors) {
        if (isWarm) {
            TasbihSettingsColorPalette(
                containerBg = Color.White,
                cardBg = Color(0xFFFAF6EE),
                textTitle = Color(0xFF1F1F1F),
                textSub = Color(0xFF7A6650),
                dividerColor = Color(0xFFEDE0C8),
                badgeBg = Color(0xFFF2EAE1),
                badgeText = Color(0xFF7A5C3E),
                accentColor = Color(0xFFD9A44E),
                iconBadgeBg = Color(0xFFFBF3E4)
            )
        } else if (isDark) {
            TasbihSettingsColorPalette(
                containerBg = themeColors.surface,
                cardBg = themeColors.border,
                textTitle = themeColors.arabicText,
                textSub = themeColors.translationText,
                dividerColor = themeColors.border.copy(alpha = 0.25f),
                badgeBg = Color(0xFF1E282D),
                badgeText = Color(0xFF2FBF96),
                accentColor = Color(0xFF2FBF96),
                iconBadgeBg = themeColors.border
            )
        } else {
            TasbihSettingsColorPalette(
                containerBg = Color.White,
                cardBg = Color(0xFFF6F8F7),
                textTitle = themeColors.arabicText,
                textSub = themeColors.translationText,
                dividerColor = Color(0xFFECEFF1),
                badgeBg = Color(0xFFEDF2F7),
                badgeText = Color(0xFF2A4365),
                accentColor = Color(0xFF107C41),
                iconBadgeBg = Color(0xFF107C41).copy(alpha = 0.12f)
            )
        }
    }
}
