package com.example.ui.quran

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.theme.QuranReadingThemeColors
import com.example.ui.theme.ReadingThemes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TajweedAudioExample(
    val id: String,
    val arabicPhrase: String,
    val transliteration: String,
    val translation: String,
    val surahName: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val highlightedRulePart: String,
    val audioNote: String
)

data class TajweedDetailedRule(
    val category: TajweedCategory,
    val titleEn: String,
    val titleAr: String,
    val counts: String,
    val description: String,
    val lettersSummary: String,
    val ruleTypeBreakdown: List<String>,
    val examples: List<TajweedAudioExample>
)

enum class TajweedReciterOption(val displayName: String, val reciterPath: String) {
    HUSARY("Sheikh Al-Husary (Tajweed Master)", "Husary_128kbps"),
    ALAFASY("Mishary Alafasy", "Alafasy_128kbps")
}

@Composable
fun TajweedGuideScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val sharedThemeName by viewModel.sharedReadingTheme.collectAsStateWithLifecycle()
    val isSepiaMode by viewModel.isQuranSepiaMode.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme

    val themeColors: QuranReadingThemeColors = remember(isSepiaMode, colorScheme, sharedThemeName) {
        if (isSepiaMode) {
            ReadingThemes.SepiaParchment
        } else if (sharedThemeName == "Obsidian Night") {
            ReadingThemes.ObsidianNight
        } else {
            ReadingThemes.fromColorScheme(colorScheme, isDark = false)
        }
    }
    val palette = TajweedThemePalette.getPalette(themeColors)

    var selectedCategoryFilter by remember { mutableStateOf<TajweedCategory?>(null) }
    var selectedReciter by remember { mutableStateOf(TajweedReciterOption.HUSARY) }

    // Audio Playback State
    var activeAudioExampleId by remember { mutableStateOf<String?>(null) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    var isAudioBuffering by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    val scope = rememberCoroutineScope()

    // Dispose player when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (e: Exception) {
                Log.e("TajweedGuide", "Error releasing MediaPlayer", e)
            }
        }
    }

    fun playAudioExample(example: TajweedAudioExample) {
        if (activeAudioExampleId == example.id && isAudioPlaying) {
            try {
                mediaPlayer?.pause()
                isAudioPlaying = false
            } catch (e: Exception) {
                Log.e("TajweedGuide", "Error pausing", e)
            }
            return
        }

        if (activeAudioExampleId == example.id && mediaPlayer != null && !isAudioPlaying) {
            try {
                mediaPlayer?.start()
                isAudioPlaying = true
            } catch (e: Exception) {
                Log.e("TajweedGuide", "Error resuming", e)
            }
            return
        }

        // New audio selection
        activeAudioExampleId = example.id
        isAudioBuffering = true
        isAudioPlaying = false

        val surahStr = example.surahNumber.toString().padStart(3, '0')
        val ayahStr = example.ayahNumber.toString().padStart(3, '0')
        val url = "https://everyayah.com/data/${selectedReciter.reciterPath}/$surahStr$ayahStr.mp3"

        scope.launch(Dispatchers.IO) {
            try {
                mediaPlayer?.reset()
                val player = mediaPlayer ?: MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                }
                player.setDataSource(url)
                player.setOnPreparedListener { mp ->
                    mp.start()
                    isAudioBuffering = false
                    isAudioPlaying = true
                }
                player.setOnCompletionListener {
                    isAudioPlaying = false
                    activeAudioExampleId = null
                }
                player.setOnErrorListener { _, _, _ ->
                    isAudioBuffering = false
                    isAudioPlaying = false
                    activeAudioExampleId = null
                    true
                }
                player.prepareAsync()
                withContext(Dispatchers.Main) {
                    mediaPlayer = player
                }
            } catch (e: Exception) {
                Log.e("TajweedGuide", "Error playing example: $url", e)
                withContext(Dispatchers.Main) {
                    isAudioBuffering = false
                    isAudioPlaying = false
                    activeAudioExampleId = null
                }
            }
        }
    }

    val detailedRules = remember {
        listOf(
            TajweedDetailedRule(
                category = TajweedCategory.MADD,
                titleEn = "Madd (Elongation & Prolongation)",
                titleAr = "أَحْكَامُ الْمَدِّ",
                counts = "2, 4, 5, or 6 Counts (Harakat)",
                description = "Madd is the elongation of sound when pronouncing long vowel letters (Alif preceded by Fathah, Waw preceded by Dammah, Yaa preceded by Kasrah). Depending on whether the vowel is followed by a Hamzah, Sukoon, or Shaddah, it is stretched for varying counts.",
                lettersSummary = "Letters: Alif (ا), Waw (و), Yaa (ي)",
                ruleTypeBreakdown = listOf(
                    "Madd Tabee'ee (Natural): 2 counts (e.g., قَالَ, يَقُولُ, قِيلَ)",
                    "Madd Wajib Muttasil (Connected): 4–5 counts when Hamzah is in the same word (e.g., جَآءَ, السَّمَآءِ)",
                    "Madd Ja'iz Munfasil (Separated): 4–5 counts when Hamzah begins the next word (e.g., إِنَّآ أَعْطَيْنَاكَ)",
                    "Madd Lazim (Compulsory): 6 counts before Shaddah or Sukoon (e.g., وَلَا الضَّآلِّينَ)"
                ),
                examples = listOf(
                    TajweedAudioExample(
                        id = "madd_ex1",
                        arabicPhrase = "إِذَا جَاءَ نَصْرُ اللَّهِ",
                        transliteration = "Idha ja'a nasru Allahi",
                        translation = "When comes the help of Allah",
                        surahName = "An-Nasr",
                        surahNumber = 110,
                        ayahNumber = 1,
                        highlightedRulePart = "جَاءَ (Madd Muttasil 4-5 counts)",
                        audioNote = "Listen to the 4-5 count prolongation on جَاءَ"
                    ),
                    TajweedAudioExample(
                        id = "madd_ex2",
                        arabicPhrase = "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ",
                        transliteration = "Inna a'taynaka al-kawthar",
                        translation = "Indeed, We have granted you abundance",
                        surahName = "Al-Kawthar",
                        surahNumber = 108,
                        ayahNumber = 1,
                        highlightedRulePart = "إِنَّا أَعْطَيْنَاكَ (Madd Munfasil)",
                        audioNote = "Listen to the elongation transition between إِنَّا and أَعْطَيْنَاكَ"
                    )
                )
            ),
            TajweedDetailedRule(
                category = TajweedCategory.GHUNNAH,
                titleEn = "Ghunnah (Nasalization)",
                titleAr = "أَحْكَامُ الْغُنَّةِ",
                counts = "2 Full Counts (Harakat)",
                description = "Ghunnah is a resonant sound produced from the nasal cavity (Khayshoom) with no tongue participation. It is obligatory and sustained for 2 full counts whenever Noon (نّ) or Meem (مّ) carries a Shaddah (Tashdeed).",
                lettersSummary = "Letters: Noon Mushaddadah (نّ), Meem Mushaddadah (مّ)",
                ruleTypeBreakdown = listOf(
                    "Noon Mushaddadah (نّ): Hold nasal sound firmly for 2 counts (e.g., إِنَّ, النَّاسِ)",
                    "Meem Mushaddadah (مّ): Hold labial nasal sound firmly for 2 counts (e.g., ثُمَّ, عَمَّ)",
                    "Ghunnah in Idgham & Ikhfa: Also applied during merging and concealment"
                ),
                examples = listOf(
                    TajweedAudioExample(
                        id = "ghunnah_ex1",
                        arabicPhrase = "قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
                        transliteration = "Qul a'udhu bi-rabbin-nas",
                        translation = "Say, I seek refuge in the Lord of mankind",
                        surahName = "An-Nas",
                        surahNumber = 114,
                        ayahNumber = 1,
                        highlightedRulePart = "النَّاسِ (Noon with Shaddah)",
                        audioNote = "Hear the distinct 2-count nasal tone on the doubled Noon in النَّاسِ"
                    ),
                    TajweedAudioExample(
                        id = "ghunnah_ex2",
                        arabicPhrase = "عَمَّ يَتَسَاءَلُونَ",
                        transliteration = "'Amma yatasa'aloon",
                        translation = "About what are they asking one another?",
                        surahName = "An-Naba",
                        surahNumber = 78,
                        ayahNumber = 1,
                        highlightedRulePart = "عَمَّ (Meem with Shaddah)",
                        audioNote = "Listen to the complete 2-count Ghunnah held on the Meem in عَمَّ"
                    )
                )
            ),
            TajweedDetailedRule(
                category = TajweedCategory.QALQALAH,
                titleEn = "Qalqalah (Echoing / Bouncing)",
                titleAr = "أَحْكَامُ الْقَلْقَلَةِ",
                counts = "Echo Sound (No extra counts)",
                description = "Qalqalah creates an echoing or bouncing resonance when pronouncing any of the 5 Qalqalah letters when they carry a Sukoon (resting mark) or when stopping upon them at the end of a verse.",
                lettersSummary = "Letters: Qaf, Ta, Ba, Jeem, Dal (ق، ط، ب، ج، د — قُطْبُ جَدّ)",
                ruleTypeBreakdown = listOf(
                    "Qalqalah Sughra (Minor): In the middle of a word or continuous recitation (e.g., يَقْطَعُونَ, خَلَقْنَا)",
                    "Qalqalah Kubra (Major): When stopping on a Qalqalah letter at the end of a word (e.g., أَحَدْ, الْفَلَقْ)",
                    "Qalqalah Akbar (Strongest): Stopping on a doubled letter with Shaddah (e.g., وَتَبَّ)"
                ),
                examples = listOf(
                    TajweedAudioExample(
                        id = "qalqalah_ex1",
                        arabicPhrase = "قُلْ هُوَ اللَّهُ أَحَدٌ",
                        transliteration = "Qul huwa Allahu ahad",
                        translation = "Say, He is Allah, [who is] One",
                        surahName = "Al-Ikhlas",
                        surahNumber = 112,
                        ayahNumber = 1,
                        highlightedRulePart = "أَحَدٌ (Echo on Dal when stopping)",
                        audioNote = "Listen to the clear resonant bounce on the letter Dal in أَحَد"
                    ),
                    TajweedAudioExample(
                        id = "qalqalah_ex2",
                        arabicPhrase = "تَبَّتْ يَدَا أَبِي لَهَبٍ وَتَبَّ",
                        transliteration = "Tabbat yada abi lahabin watabb",
                        translation = "May the hands of Abu Lahab be ruined, and ruined is he",
                        surahName = "Al-Masad",
                        surahNumber = 111,
                        ayahNumber = 1,
                        highlightedRulePart = "وَتَبَّ (Strongest Qalqalah on Ba with Tashdeed)",
                        audioNote = "Notice the heavy release on the stressed Ba in وَتَبَّ"
                    )
                )
            ),
            TajweedDetailedRule(
                category = TajweedCategory.IKHFA,
                titleEn = "Ikhfa (Concealment)",
                titleAr = "أَحْكَامُ الْإِخْفَاءِ",
                counts = "2 Counts Ghunnah with Concealment",
                description = "Ikhfa means concealing the sound of Noon Sakinah (نْ) or Tanween without complete merging and without harsh clarity, while maintaining a 2-count nasal Ghunnah near the articulation point of the subsequent letter.",
                lettersSummary = "15 Letters: ص، ذ، ث، ك، ج، ش، ق، س، د، ط، ز، ف، ت، ض، ظ",
                ruleTypeBreakdown = listOf(
                    "Heavy Ikhfa (Mufakhkham): Before heavy letters (ص، ض، ط، ظ، ق) – nasal sound is deep",
                    "Light Ikhfa (Muraqqaq): Before light letters (ت، ث، ج، د، ذ، ز، س، ش، ف، ك)",
                    "Positioning: Tongue rests near the next letter's exit point during the Ghunnah"
                ),
                examples = listOf(
                    TajweedAudioExample(
                        id = "ikhfa_ex1",
                        arabicPhrase = "مِن شَرِّ مَا خَلَقَ",
                        transliteration = "Min sharri ma khalaq",
                        translation = "From the evil of that which He created",
                        surahName = "Al-Falaq",
                        surahNumber = 113,
                        ayahNumber = 2,
                        highlightedRulePart = "مِن شَرِّ (Noon Sakinah before Shin)",
                        audioNote = "Listen to the soft concealment of Noon followed by a 2-count Ghunnah into Shin"
                    ),
                    TajweedAudioExample(
                        id = "ikhfa_ex2",
                        arabicPhrase = "سَيَصْلَىٰ نَارًا ذَاتَ لَهَبٍ",
                        transliteration = "Sayasla naran dhata lahab",
                        translation = "He will burn in a Fire of blazing flame",
                        surahName = "Al-Masad",
                        surahNumber = 111,
                        ayahNumber = 3,
                        highlightedRulePart = "نَارًا ذَاتَ (Tanween before Dhal)",
                        audioNote = "Observe how Tanween dissolves gently into Dhal with nasal resonance"
                    )
                )
            ),
            TajweedDetailedRule(
                category = TajweedCategory.IDGHAM,
                titleEn = "Idgham (Merging / Assimilation)",
                titleAr = "أَحْكَامُ الْإِدْغَامِ",
                counts = "2 Counts with Ghunnah / Instant without Ghunnah",
                description = "Idgham is the merging of a Noon Sakinah (نْ) or Tanween directly into the succeeding letter from the 6 letters of 'Yarmaloon' (يرملون), turning the two into a single stressed letter.",
                lettersSummary = "6 Letters: Yarmaloon (ي، ر، م، ل، و، ن — يَرْمَلُونَ)",
                ruleTypeBreakdown = listOf(
                    "Idgham bi Ghunnah (With 2-count Nasal Sound): Letters (ي، ن، م، و — يَنْمُو)",
                    "Idgham bila Ghunnah (Without Nasal Sound): Letters (ل، ر) – instant clean merge",
                    "Condition: Must occur across two separate words in standard recitation"
                ),
                examples = listOf(
                    TajweedAudioExample(
                        id = "idgham_ex1",
                        arabicPhrase = "فَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍ",
                        transliteration = "Faman ya'mal mithqala dharrah",
                        translation = "So whoever does an atom's weight of good will see it",
                        surahName = "Az-Zalzalah",
                        surahNumber = 99,
                        ayahNumber = 7,
                        highlightedRulePart = "فَمَن يَعْمَلْ (Noon merges into Ya with Ghunnah)",
                        audioNote = "Listen to the Noon blending into Ya with a sustained 2-count nasal humming"
                    ),
                    TajweedAudioExample(
                        id = "idgham_ex2",
                        arabicPhrase = "وَيْلٌ لِّكُلِّ هُمَزَةٍ لُّمَزَةٍ",
                        transliteration = "Waylul-likulli humazatil-lumazah",
                        translation = "Woe to every scorner and mocker",
                        surahName = "Al-Humazah",
                        surahNumber = 104,
                        ayahNumber = 1,
                        highlightedRulePart = "وَيْلٌ لِّكُلِّ (Tanween merges directly into Lam)",
                        audioNote = "Notice how the Tanween merges smoothly into Lam with zero nasal sound"
                    )
                )
            ),
            TajweedDetailedRule(
                category = TajweedCategory.IQLAB,
                titleEn = "Iqlab (Conversion to Meem)",
                titleAr = "أَحْكَامُ الْإِقْلَابِ",
                counts = "2 Counts Ghunnah on converted Meem",
                description = "Iqlab means converting the pronunciation of a Noon Sakinah (نْ) or Tanween into a concealed Meem (م) with a 2-count Ghunnah when followed immediately by the letter Ba (ب). Marked in Quranic script by a miniature Meem (ۢ).",
                lettersSummary = "Single Letter: Ba (ب)",
                ruleTypeBreakdown = listOf(
                    "Phase 1: Convert Noon/Tanween sound into a Meem (م)",
                    "Phase 2: Conceal the Meem with relaxed lips without pressing tightly",
                    "Phase 3: Prolong the nasal Ghunnah for 2 full counts before releasing Ba"
                ),
                examples = listOf(
                    TajweedAudioExample(
                        id = "iqlab_ex1",
                        arabicPhrase = "مِنۢ بَعْدِ مَا جَاءَتْهُمُ",
                        transliteration = "Mim-ba'di ma ja'at-humu",
                        translation = "After there came to them clear evidence",
                        surahName = "Al-Bayyinah",
                        surahNumber = 98,
                        ayahNumber = 4,
                        highlightedRulePart = "مِنۢ بَعْدِ (Noon converted to Meem before Ba)",
                        audioNote = "Listen to the Noon sounding like 'Mim-ba'di' with a gentle 2-count Ghunnah"
                    ),
                    TajweedAudioExample(
                        id = "iqlab_ex2",
                        arabicPhrase = "كَلَّا ۖ لَيُنبَذَنَّ فِي الْحُطَمَةِ",
                        transliteration = "Kalla layumbadhanna fil-hutamah",
                        translation = "No! He will surely be thrown into the Crusher",
                        surahName = "Al-Humazah",
                        surahNumber = 104,
                        ayahNumber = 4,
                        highlightedRulePart = "لَيُنبَذَنَّ (Iqlab inside single word)",
                        audioNote = "Hear the internal conversion of Noon to Meem in 'layumbadhanna'"
                    )
                )
            ),
            TajweedDetailedRule(
                category = TajweedCategory.MEEM_SAKINAH,
                titleEn = "Meem Sakinah (Labial Rules)",
                titleAr = "أَحْكَامُ الْمِيمِ السَّاكِنَةِ",
                counts = "2 Counts for Ikhfa/Idgham, 0 for Izhar",
                description = "Rules governing the unvoweled Meem (مْ) depending on what letter follows it. There are three sub-rules: Ikhfa Shafawi (before Ba), Idgham Shafawi / Mithlayn (before another Meem), and Izhar Shafawi (before all other 26 letters).",
                lettersSummary = "Governs unvoweled Meem (مْ)",
                ruleTypeBreakdown = listOf(
                    "1. Ikhfa Shafawi: Meem before Ba (ب) with 2 counts Ghunnah (e.g., تَرْمِيهِم بِحِجَارَةٍ)",
                    "2. Idgham Mithlayn / Shafawi: Meem before Meem (م) with 2 counts Ghunnah (e.g., لَهُم مَّا)",
                    "3. Izhar Shafawi: Meem before all other 26 letters recited with clear articulation"
                ),
                examples = listOf(
                    TajweedAudioExample(
                        id = "meem_ex1",
                        arabicPhrase = "تَرْمِيهِم بِحِجَارَةٍ مِّن سِجِّيلٍ",
                        transliteration = "Tarmeehim bi-hijaratin min sijjeel",
                        translation = "Striking them with stones of hard clay",
                        surahName = "Al-Fil",
                        surahNumber = 105,
                        ayahNumber = 4,
                        highlightedRulePart = "تَرْمِيهِم بِحِجَارَةٍ (Ikhfa Shafawi before Ba)",
                        audioNote = "Listen to the unvoweled Meem blending smoothly into Ba with 2-count Ghunnah"
                    ),
                    TajweedAudioExample(
                        id = "meem_ex2",
                        arabicPhrase = "أَلَمْ يَجْعَلْ كَيْدَهُمْ فِي تَضْلِيلٍ",
                        transliteration = "Alam yaj'al kaydahum fee tadleel",
                        translation = "Did He not make their plan into misguidance?",
                        surahName = "Al-Fil",
                        surahNumber = 105,
                        ayahNumber = 2,
                        highlightedRulePart = "أَلَمْ يَجْعَلْ (Izhar Shafawi before Ya)",
                        audioNote = "Notice the crisp, distinct pronunciation of Meem without extra humming"
                    )
                )
            )
        )
    }

    val filteredRules = if (selectedCategoryFilter != null) {
        detailedRules.filter { it.category == selectedCategoryFilter }
    } else {
        detailedRules
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = themeColors.background,
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                color = themeColors.surface,
                border = null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(themeColors.accent.copy(alpha = 0.1f))
                                .testTag("tajweed_guide_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = themeColors.accent
                            )
                        }

                        Column {
                            Text(
                                text = "Tajweed Guide & Rules",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "دَلِيلُ أَحْكَامِ التَّجْوِيدِ مَعَ الصَّوْتِ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = themeColors.accent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Reciter Badge Indicator
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = themeColors.accent.copy(alpha = if (themeColors.isDark) 0.22f else 0.12f),
                        border = null,
                        modifier = Modifier.clickable {
                            selectedReciter = if (selectedReciter == TajweedReciterOption.HUSARY) {
                                TajweedReciterOption.ALAFASY
                            } else {
                                TajweedReciterOption.HUSARY
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = themeColors.accent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (selectedReciter == TajweedReciterOption.HUSARY) "Al-Husary" else "Alafasy",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.accent,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Filter Chips
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "FILTER BY CATEGORY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.translationText,
                            fontSize = 10.5.sp,
                            letterSpacing = 1.sp
                        )
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            val isAllSelected = selectedCategoryFilter == null
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isAllSelected) themeColors.accent else themeColors.surface,
                                border = null,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { selectedCategoryFilter = null }
                            ) {
                                Text(
                                    text = "All Rules (7)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isAllSelected) (if (themeColors.isDark) Color.Black else Color.White) else themeColors.arabicText,
                                        fontSize = 12.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }

                        items(TajweedCategory.values()) { category ->
                            val isSelected = selectedCategoryFilter == category
                            val catColor = palette[category] ?: themeColors.accent

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) catColor else themeColors.surface,
                                border = null,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        selectedCategoryFilter = if (isSelected) null else category
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else catColor)
                                    )
                                    Text(
                                        text = category.titleEn,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else themeColors.arabicText,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Info Card about Audio Pronunciation
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = themeColors.accent.copy(alpha = 0.08f),
                    border = null
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(themeColors.accent.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = themeColors.accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Authentic Recitation Audio",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText,
                                    fontSize = 13.5.sp
                                )
                            )
                            Text(
                                text = "Tap the play button next to each Quranic phrase to hear its exact Tajweed execution by ${selectedReciter.displayName}.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = themeColors.translationText,
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }
            }

            // List of In-Depth Rules
            items(filteredRules) { rule ->
                val ruleColor = palette[rule.category] ?: themeColors.accent

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tajweed_rule_card_${rule.category.name.lowercase()}"),
                    shape = RoundedCornerShape(16.dp),
                    color = themeColors.surface,
                    border = null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header with Color Pill and Calligraphy
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
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(ruleColor)
                                )
                                Column {
                                    Text(
                                        text = rule.titleEn,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.arabicText,
                                            fontSize = 15.5.sp
                                        )
                                    )
                                    Text(
                                        text = rule.counts,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = ruleColor,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }

                            Text(
                                text = rule.titleAr,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ruleColor,
                                    fontSize = 17.sp
                                )
                            )
                        }

                        // Detailed Mechanics Description
                        Text(
                            text = rule.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = themeColors.arabicText.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        )

                        // Letters summary pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ruleColor.copy(alpha = 0.1f),
                            border = null
                        ) {
                            Text(
                                text = rule.lettersSummary,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ruleColor,
                                    fontSize = 11.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        // Rule Type Breakdown
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(themeColors.background)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "RECITATION RULES & CLASSIFICATIONS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.translationText,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 0.8.sp
                                )
                            )
                            rule.ruleTypeBreakdown.forEach { item ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = ruleColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = themeColors.arabicText,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Real Quranic Examples with Audio Playback
                        Text(
                            text = "QURANIC EXAMPLES WITH RECITER AUDIO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.translationText,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        )

                        rule.examples.forEach { example ->
                            val isThisPlaying = activeAudioExampleId == example.id && isAudioPlaying
                            val isThisBuffering = activeAudioExampleId == example.id && isAudioBuffering

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { playAudioExample(example) }
                                    .testTag("audio_example_${example.id}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isThisPlaying) ruleColor.copy(alpha = 0.12f) else themeColors.background,
                                border = null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Play Button / Audio Status
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isThisPlaying) ruleColor else ruleColor.copy(alpha = 0.16f)
                                            )
                                            .then(
                                                if (isThisPlaying) Modifier.scale(pulseScale) else Modifier
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isThisBuffering) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = ruleColor,
                                                strokeWidth = 2.dp
                                            )
                                        } else if (isThisPlaying) {
                                            Icon(
                                                imageVector = Icons.Default.Pause,
                                                contentDescription = "Pause",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play Example",
                                                tint = ruleColor,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Verse Content & Rule Focus
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = ruleColor.copy(alpha = 0.15f),
                                                border = null
                                            ) {
                                                Text(
                                                    text = "${example.surahName} (${example.surahNumber}:${example.ayahNumber})",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = ruleColor,
                                                        fontSize = 10.sp
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Text(
                                                text = example.arabicPhrase,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = themeColors.arabicText,
                                                    fontSize = 17.sp
                                                ),
                                                textAlign = TextAlign.End
                                            )
                                        }

                                        Text(
                                            text = example.transliteration,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = themeColors.arabicText.copy(alpha = 0.85f),
                                                fontSize = 12.sp
                                            )
                                        )

                                        Text(
                                            text = example.highlightedRulePart,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = ruleColor,
                                                fontSize = 11.sp
                                            )
                                        )

                                        Text(
                                            text = example.audioNote,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = themeColors.translationText,
                                                fontSize = 10.5.sp
                                            )
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
