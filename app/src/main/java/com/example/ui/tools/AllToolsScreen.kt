package com.example.ui.tools

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.localization.tr
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.SalatTab
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.home.IslamicIconAzkar
import com.example.ui.home.IslamicIconDua
import com.example.ui.home.IslamicIconMushaf
import com.example.ui.home.IslamicIconQibla
import com.example.ui.home.IslamicIconQuranAudio
import com.example.ui.home.IslamicIconSalat
import com.example.ui.home.IslamicIconTasbeeh
import com.example.ui.home.IslamicIconTask
import com.example.ui.theme.BorderDividerLight
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.GoldTintBgLight
import com.example.ui.theme.HeaderGradientLight
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.SurfaceElevatedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight

private val NoorTealDark = SecondaryGoldLight
private val NoorTealVibrant = SecondaryGoldDark
private val NoorDarkPine = TextPrimaryLight
private val NoorSageSlate = TextSecondaryLight
private val NoorGoldAccent = SecondaryGoldLight
private val NoorGoldSoft = GoldTintBgLight
private val NoorGoldBorder = BorderDividerLight
private val NoorCardBorder = BorderDividerLight
private val NoorSurfaceSoft = SurfaceElevatedLight
private val NoorSoftGreenBg = CanvasMint
private val NoorSoftGreenBorder = BorderDividerLight

enum class ToolCategory(val enName: String, val arName: String) {
    ALL("All Tools", "جميع الأدوات"),
    QURAN_AUDIO("Quran & Audio", "القرآن والتلاوات"),
    PRAYER_QIBLA("Prayer & Qibla", "الصلاة والقبلة"),
    DHIKR_DUAS("Dhikr & Duas", "الأذكار والأدعية"),
    STREAKS_HABITS("Streaks & Habits", "الالتزام والعادات"),
    CLOUD_SETTINGS("Cloud & Settings", "الإعدادات والنسخ")
}

data class ToolItem(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val subtitleEn: String,
    val subtitleAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val category: ToolCategory,
    val badgeEn: String? = null,
    val badgeAr: String? = null,
    val isFeatured: Boolean = false,
    val iconContent: @Composable (Color) -> Unit,
    val onClick: (MainViewModel) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllToolsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage == "ar"

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ToolCategory.ALL) }

    val allTools = remember {
        listOf(
            // 1. MP3 Quran Player (Featured)
            ToolItem(
                id = "mp3_quran",
                titleEn = "MP3 Quran Player",
                titleAr = "مشغل تلاوات القرآن الكريم",
                subtitleEn = "High Quality Audio Recitations",
                subtitleAr = "تلاوات قرآنية عذبة بصوت نخبة القراء",
                descriptionEn = "Stream full surahs in the background with continuous playback, repeat modes, and surah selection.",
                descriptionAr = "استمع إلى التلاوات العذبة في الخلفية مع خاصية التكرار واختيار السور والتنقل السلس.",
                category = ToolCategory.QURAN_AUDIO,
                badgeEn = "MP3 Audio",
                badgeAr = "صوتيات MP3",
                isFeatured = true,
                iconContent = { tint -> IslamicIconQuranAudio(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.QURAN_AUDIO_STREAM) }
            ),

            // 2. Spiritual Streaks & Milestones (Featured)
            ToolItem(
                id = "streaks",
                titleEn = "Spiritual Streaks & Badges",
                titleAr = "سلسلة الالتزام والأوسمة الروحانية",
                subtitleEn = "Daily Devotion & Level Milestones",
                subtitleAr = "سجل المواظبة اليومية والأوسمة المكتسبة",
                descriptionEn = "Track consecutive days of prayer, Quran reading, dhikr, and unlock spiritual achievement badges.",
                descriptionAr = "حافظ على استمرارية العبادات اليومية، وتتبع سجل إنجازاتك وافتح أوسمة التميز الروحاني.",
                category = ToolCategory.STREAKS_HABITS,
                badgeEn = "Streaks & XP",
                badgeAr = "سلسلة وأوسمة",
                isFeatured = true,
                iconContent = { tint -> Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateTo(NoorDestination.STREAKS) }
            ),

            // 3. Holy Qur'an Reader
            ToolItem(
                id = "quran_reader",
                titleEn = "Holy Qur'an (114 Surahs)",
                titleAr = "المصحف الشريف (١١٤ سورة)",
                subtitleEn = "Complete Quran with Translation & Audio",
                subtitleAr = "المصحف الكامل بالرسم العثماني والترجمة",
                descriptionEn = "Read all 114 Surahs with Arabic calligraphy, English translation, transliteration, real-time Tajweed color-coding, and verse audio.",
                descriptionAr = "تصفح القرآن الكريم كاملاً مع الترجمة الإنجليزية، التفسير، التلوين التلقائي للتجويد والاستماع لكل آية.",
                category = ToolCategory.QURAN_AUDIO,
                badgeEn = "114 Surahs",
                badgeAr = "١١٤ سورة",
                iconContent = { tint -> IslamicIconMushaf(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.QURAN_SURAH_LIST) }
            ),

            // 4. Quran Khatma Plan Tracker (Featured)
            ToolItem(
                id = "quran_khatma",
                titleEn = "Quran Khatma Planner",
                titleAr = "خطة ختمة القرآن الكريم",
                subtitleEn = "30-Day, Ramadan & Custom Khatmas",
                subtitleAr = "خطط ختمة مخصصة مع تتبع الإنجاز اليومي",
                descriptionEn = "Create custom Khatma plans, track your daily Juz & page pace, view completion streaks, and auto-resume reading.",
                descriptionAr = "أنشئ خطتك لختم القرآن (٣٠ يوماً، رمضان، أو مخصص)، وتابع وردك اليومي ونسبة الإنجاز.",
                category = ToolCategory.QURAN_AUDIO,
                badgeEn = "Goal Planner",
                badgeAr = "مخطط الختمة",
                isFeatured = true,
                iconContent = { tint -> Icon(Icons.Default.Bookmark, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateTo(NoorDestination.QURAN_KHATMA) }
            ),

            // 5. Quran Memorization Studio (Hifz Hub)
            ToolItem(
                id = "quran_memorization",
                titleEn = "Quran Memorization (Hifz)",
                titleAr = "تحفيظ القرآن الكريم والمراجعة",
                subtitleEn = "Progressive Reveal, Audio Drills & Self-Recall",
                subtitleAr = "إخفاء تدريجي مع التلاوة، تكرار الآيات واختبار الحفظ",
                descriptionEn = "Master Surahs with audio-synced progressive reveal, skeleton shimmer masking, custom ayah repeat drills (1x–10x), pause intervals for recitation, and self-test recall verification.",
                descriptionAr = "أتقن حفظ القرآن الكريم عبر الإخفاء التدريجي المتزامن مع التلاوة الصوتية، تكرار الآيات المخصص، واختبار استرجاع الآيات من الذاكرة.",
                category = ToolCategory.QURAN_AUDIO,
                badgeEn = "Hifz Studio",
                badgeAr = "تحفيظ متقن",
                isFeatured = true,
                iconContent = { tint -> Icon(Icons.Default.School, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.openMemorizationStudio() }
            ),

            // 6. Top Global Reciters
            ToolItem(
                id = "quran_reciters",
                titleEn = "Famous Quran Reciters",
                titleAr = "مشاهير قراء العالم الإسلامي",
                subtitleEn = "20+ Renowned Global Qaris",
                subtitleAr = "أكثر من ٢٠ قارئاً من كبار قراء العالم",
                descriptionEn = "Explore recitations from Mishary Alafasy, Abdulbasit, Al-Ghamdi, Sudais, Al-Minshawi, Al-Husary, and more.",
                descriptionAr = "اختر قارئك المفضل من بين نخبة من أشهر القراء: مشاري العفاسي، عبدالباسط، الغامدي، السديس، المنشاوي.",
                category = ToolCategory.QURAN_AUDIO,
                badgeEn = "20+ Reciters",
                badgeAr = "٢٠+ قارئ",
                iconContent = { tint -> Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateTo(NoorDestination.QURAN_RECITERS) }
            ),

            // 7. Tajweed Recitation Guide with Audio Examples
            ToolItem(
                id = "quran_tajweed_guide",
                titleEn = "Tajweed Guide & Audio Examples",
                titleAr = "دليل أحكام التجويد مع الصوت",
                subtitleEn = "Color-Coded Rules with Reciter Audio",
                subtitleAr = "شرح أحكام التجويد السبعة مع أمثلة قرآنية صوتية",
                descriptionEn = "Master Madd, Ghunnah, Qalqalah, Ikhfa, Idgham, Iqlab, and Meem Sakinah with interactive reciter audio examples from the Quran.",
                descriptionAr = "تعلم أحكام المد، الغنة، القلقلة، الإخفاء، الإدغام، الإقلاب، والميم الساكنة مع أمثلة صوتية مسجلة بأصوات كبار القراء.",
                category = ToolCategory.QURAN_AUDIO,
                badgeEn = "Audio Guide",
                badgeAr = "دليل صوتي",
                iconContent = { tint -> Icon(Icons.Default.Palette, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateTo(NoorDestination.QURAN_TAJWEED_GUIDE) }
            ),

            // 6. Salat & Prayer Times
            ToolItem(
                id = "salat_times",
                titleEn = "Prayer Times & Adhan",
                titleAr = "مواقيت الصلاة والأذان",
                subtitleEn = "Accurate GPS-Based Daily Schedules",
                subtitleAr = "أوقات الأذان والصلوات بدقة الموقع الجغرافي",
                descriptionEn = "Live prayer times for Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha, and Qiyam with next prayer countdown.",
                descriptionAr = "مواقيت الصلاة الخمس مع الشروق وقيام الليل والعد التنازلي للأذان القادم مع التنبيهات.",
                category = ToolCategory.PRAYER_QIBLA,
                badgeEn = "GPS Times",
                badgeAr = "مواقيت دقيقة",
                iconContent = { tint -> IslamicIconSalat(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateToSalat(SalatTab.TIMES) }
            ),

            // 7. Prayer Performance Tracker
            ToolItem(
                id = "prayer_tracker",
                titleEn = "Salat Fulfillment Tracker",
                titleAr = "سجل أداء ومتابعة الصلوات",
                subtitleEn = "Daily & Weekly Prayer Logs",
                subtitleAr = "تسجيل الصلوات في وقتها وجماعة",
                descriptionEn = "Log your daily prayers (On-time, Jama'ah, Late), view completion rates, and build prayer consistency.",
                descriptionAr = "سجل صلواتك اليومية (في وقتها، جماعة)، وتابع مخطط التزامك الأسبوعي والشهري.",
                category = ToolCategory.PRAYER_QIBLA,
                badgeEn = "Daily Log",
                badgeAr = "سجل يومي",
                iconContent = { tint -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateToSalat(SalatTab.STREAKS) }
            ),

            // 8. Qada Missed Prayer Manager
            ToolItem(
                id = "qada_prayers",
                titleEn = "Qada Prayers Manager",
                titleAr = "حاسبة وسجل قضاء الفوائت",
                subtitleEn = "Track & Repay Missed Prayers",
                subtitleAr = "متابعة وقضاء الصلوات الفائتة بسهولة",
                descriptionEn = "Keep count of missed prayers across Fajr, Dhuhr, Asr, Maghrib, and Isha with easy one-tap decrementing.",
                descriptionAr = "تتبع وقضاء الصلوات الفائتة مع عداد إلكتروني سهل الاستخدام لكل صلاة.",
                category = ToolCategory.PRAYER_QIBLA,
                badgeEn = "Qada Counter",
                badgeAr = "قضاء الفوائت",
                iconContent = { tint -> Icon(Icons.Default.AccessTime, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateToSalat(SalatTab.QADA) }
            ),

            // 9. Qibla Direction Compass
            ToolItem(
                id = "qibla_compass",
                titleEn = "Qibla Direction Compass",
                titleAr = "بوصلة اتجاه القبلة الذكية",
                subtitleEn = "Live Direction to Holy Kaaba",
                subtitleAr = "تحديد اتجاه الكعبة المشرفة بدقة متناهية",
                descriptionEn = "Interactive sensor-based 3D compass with degree heading, distance to Makkah, and haptic feedback on alignment.",
                descriptionAr = "بوصلة تفاعلية بحساسات الجهاز ترشدك مباشرة للكعبة المشرفة بمكة المكرمة مع المسافة والاهتزاز.",
                category = ToolCategory.PRAYER_QIBLA,
                badgeEn = "Live Sensor",
                badgeAr = "حساس مباشر",
                iconContent = { tint -> IslamicIconQibla(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.QIBLA) }
            ),

            // 10. Smart Digital Tasbih
            ToolItem(
                id = "digital_tasbih",
                titleEn = "Smart Digital Tasbih",
                titleAr = "السبحة الإلكترونية الذكية",
                subtitleEn = "Tactile Dhikr Bead Counter",
                subtitleAr = "عداد تسبيح باللمس والاهتزاز وحلقات الذكر",
                descriptionEn = "Electronic beads counter with tactile vibration, loop targets (33, 99, 1000), preset adhkar, and lifetime counts.",
                descriptionAr = "سبحة إلكترونية تفاعلية باهتزازات لمسية وأهداف دورات الذكر (٣٣، ٩٩، ١٠٠٠) وإحصائيات التسبيح.",
                category = ToolCategory.DHIKR_DUAS,
                badgeEn = "Tactile Beads",
                badgeAr = "سبحة ذكية",
                iconContent = { tint -> IslamicIconAzkar(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.TASBIH) }
            ),

            // 11. Fortress of the Muslim (Duas Library)
            ToolItem(
                id = "duas_library",
                titleEn = "Fortress of the Muslim (Du'as)",
                titleAr = "حصن المسلم وموسوعة الأدعية",
                subtitleEn = "Authentic Supplications & Rabbana Duas",
                subtitleAr = "أدعية مأثورة من القرآن الكريم والسنة النبوية",
                descriptionEn = "Categorized supplications for Morning/Evening, Travel, Anxiety, Forgiveness, Quranic Rabbana Duas, and Parents.",
                descriptionAr = "مكتبة أدعية شاملة مبوبة: أدعية الصباح والمساء، السفر، الكرب، المغفرة، والرقية وأدعية القرآن الكريم.",
                category = ToolCategory.DHIKR_DUAS,
                badgeEn = "100+ Duas",
                badgeAr = "١٠٠+ دعاء",
                iconContent = { tint -> IslamicIconDua(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.DUAS_LIBRARY) }
            ),

            // 11b. Hadith Library
            ToolItem(
                id = "hadith_library",
                titleEn = "Hadith Library",
                titleAr = "المكتبة الحديثية",
                subtitleEn = "Authentic Prophetic Traditions",
                subtitleAr = "الأحاديث النبوية الصحيحة",
                descriptionEn = "Explore authentic Hadith collections (Bukhari, Muslim, 40 Nawawi, Tirmidhi, Abu Dawud) with authenticity grade badges.",
                descriptionAr = "تصفح مجموعات الأحاديث النبوية الصحيحة (البخاري، مسلم، الأربعين النووية، الترمذي، أبو داود) مع درجات الصحة والتوثيق.",
                category = ToolCategory.DHIKR_DUAS,
                badgeEn = "Authentic",
                badgeAr = "صحيح",
                iconContent = { tint -> Icon(imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateTo(NoorDestination.HADITH_LIBRARY) }
            ),

            // 12. Daily Azkar Reader
            ToolItem(
                id = "daily_azkar",
                titleEn = "Daily Azkar Reader",
                titleAr = "أذكار الصباح والمساء واليوم",
                subtitleEn = "Step-by-Step Repetition Counters",
                subtitleAr = "قراءة تفاعلية للأذكار مع عداد التكرار والفضائل",
                descriptionEn = "Read Morning & Evening Azkar, Wakeup & Sleep Azkar with tap counters, virtues, and English translations.",
                descriptionAr = "أذكار الصباح والمساء، أذكار النوم والاستيقاظ مع عدادات تفاعلية وفضيلة كل ذكر.",
                category = ToolCategory.DHIKR_DUAS,
                badgeEn = "Azkar Counter",
                badgeAr = "عداد الأذكار",
                iconContent = { tint -> IslamicIconAzkar(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.AZKAR_READER) }
            ),

            // 13. Daily Sunnah Habit Tracker
            ToolItem(
                id = "habit_tracker",
                titleEn = "Sunnah & Daily Habits",
                titleAr = "متتبع السنن والعادات اليومية",
                subtitleEn = "Tahajjud, Duha, Witr & Sadaqah",
                subtitleAr = "التهجد، صلاة الضحى، الوتر، الصدقة والسنن",
                descriptionEn = "Track daily Sunnah practices like Tahajjud, Duha prayer, Witr, reading Surah Al-Kahf on Friday, and daily Sadaqah.",
                descriptionAr = "تابع سنن النبي ﷺ اليومية: صلاة الضحى، قيام الليل، قراءة سورة الكهف، الصدقة وصيام التطوع.",
                category = ToolCategory.STREAKS_HABITS,
                badgeEn = "Sunnah Habits",
                badgeAr = "سنن يومية",
                iconContent = { tint -> IslamicIconTask(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.HABIT_TRACKER) }
            ),

            // 14. Favorites & Saved Bookmarks
            ToolItem(
                id = "favorites",
                titleEn = "Favorites & Saved Verses",
                titleAr = "المفضلة والآيات المحفوظة",
                subtitleEn = "Quick Access to Bookmarked Ayahs & Duas",
                subtitleAr = "وصول سريع للآيات والأدعية التي قمت بحفظها",
                descriptionEn = "Access all your bookmarked Quran verses, favorite duas, and treasured spiritual reflections in one clean hub.",
                descriptionAr = "استعرض جميع الآيات المفضلة والأدعية المحفوظة لديك لسهولة الرجوع إليها في أي وقت.",
                category = ToolCategory.STREAKS_HABITS,
                badgeEn = "Bookmarks",
                badgeAr = "المحفوظات",
                iconContent = { tint -> Icon(Icons.Default.Favorite, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateTo(NoorDestination.FAVORITES) }
            ),

            // 15. Spiritual User Profile & Backup
            ToolItem(
                id = "user_profile",
                titleEn = "Spiritual Profile & Backup",
                titleAr = "الملف الشخصي والنسخ الاحتياطي",
                subtitleEn = "Account, Cloud Sync & Data Privacy",
                subtitleAr = "إدارة الحساب، المزامنة، والنسخ الاحتياطي",
                descriptionEn = "Manage your spiritual identity, export/import Khatma and streaks to Google Drive or Email, and adjust cloud sync.",
                descriptionAr = "إدارة حسابك وبياناتك، وتصدير واستيراد بيانات الختمة والسلسلة عبر Google Drive والبريد.",
                category = ToolCategory.CLOUD_SETTINGS,
                badgeEn = "Drive & Email",
                badgeAr = "نسخ سحابي",
                iconContent = { tint -> Icon(Icons.Default.Person, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateTo(NoorDestination.PROFILE) }
            ),

            // 16. App Settings & Customization
            ToolItem(
                id = "app_settings",
                titleEn = "App Settings & Customization",
                titleAr = "إعدادات وتخصيص التطبيق",
                subtitleEn = "Languages, Adhan, Calculation Methods",
                subtitleAr = "اللغة، أصوات الأذان، وطرق حساب المواقيت",
                descriptionEn = "Switch between English and Arabic, select your preferred Adhan muezzin, change calculation method, and theme preferences.",
                descriptionAr = "تغيير لغة التطبيق (العربية والإنجليزية)، تخصيص صوت الأذان، وتعديل طرق حساب المواقيت الفلكية.",
                category = ToolCategory.CLOUD_SETTINGS,
                badgeEn = "Preferences",
                badgeAr = "التفضيلات",
                iconContent = { tint -> Icon(Icons.Default.Settings, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.openSettingsModal() }
            )
        )
    }

    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val themeColors = remember(isDarkMode) {
        if (isDarkMode) com.example.ui.theme.ReadingThemes.ObsidianNight else com.example.ui.theme.ReadingThemes.MadaniCrisp
    }

    // Filter by category and search query
    val filteredTools = remember(selectedCategory, searchQuery, isArabic) {
        allTools.filter { tool ->
            val matchesCategory = selectedCategory == ToolCategory.ALL || tool.category == selectedCategory
            val matchesQuery = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                tool.titleEn.lowercase().contains(q) ||
                tool.titleAr.lowercase().contains(q) ||
                tool.subtitleEn.lowercase().contains(q) ||
                tool.subtitleAr.lowercase().contains(q) ||
                tool.descriptionEn.lowercase().contains(q) ||
                tool.descriptionAr.lowercase().contains(q) ||
                (tool.badgeEn?.lowercase()?.contains(q) == true) ||
                (tool.badgeAr?.lowercase()?.contains(q) == true)
            }
            matchesCategory && matchesQuery
        }
    }

    val chunkedTools = remember(filteredTools) { filteredTools.chunked(2) }

    Scaffold(
        topBar = {
            NoorTopBar(
                title = if (isArabic) "جميع الأدوات والمميزات" else "All Tools & Features",
                eyebrow = if (isArabic) "دليل الخدمات الشامل" else "ALL ACCESS DIRECTORY",
                subtitle = if (isArabic) "استكشف كافة المزايا والخدمات الروحانية" else "Explore every spiritual tool in Al-Noor",
                onBackClick = onNavigateBack,
                backContentDescription = stringResource(R.string.action_back),
                actions = {
                    NoorGlassIconButton(
                        onClick = { viewModel.openSettingsModal() },
                        icon = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            )
        },
        containerColor = themeColors.background,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Search Bar
            item(key = "search_bar") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = if (isArabic) "ابحث عن أداة (مثل: MP3، ختمة، قبلة)..." else "Search tools (e.g. MP3, Khatma, Qibla)...",
                                style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText.copy(alpha = 0.65f), fontSize = 12.5.sp)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = themeColors.accent,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = themeColors.translationText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedContainerColor = themeColors.surface,
                            unfocusedContainerColor = themeColors.surface,
                            focusedTextColor = themeColors.arabicText,
                            unfocusedTextColor = themeColors.arabicText
                        ),
                        singleLine = true
                    )
                }
            }

            // 2. Category Filter Chips (Horizontal Scroll)
            item(key = "category_chips") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ToolCategory.entries.forEach { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) themeColors.accent else themeColors.surface,
                            border = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = category }
                        ) {
                            Text(
                                text = if (isArabic) category.arName else category.enName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else themeColors.arabicText,
                                    fontSize = 12.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // 3. Tools Count Header
            item(key = "tools_count_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "الأدوات المتاحة (${filteredTools.size})" else "AVAILABLE TOOLS (${filteredTools.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.translationText,
                            fontSize = 11.5.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    if (selectedCategory != ToolCategory.ALL || searchQuery.isNotBlank()) {
                        Text(
                            text = if (isArabic) "إعادة ضبط" else "Reset Filter",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.accent,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.clickable {
                                selectedCategory = ToolCategory.ALL
                                searchQuery = ""
                            }
                        )
                    }
                }
            }

            // 4. Tool Item Cards Grid
            if (filteredTools.isEmpty()) {
                item(key = "empty_tools") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = themeColors.translationText.copy(alpha = 0.5f),
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = if (isArabic) "لم يتم العثور على أدوات مطابقة" else "No matching tools found",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = if (isArabic) "جرب كلمة بحث أخرى أو اختر فئة مختلفة" else "Try a different search term or category",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = themeColors.translationText,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            } else {
                items(chunkedTools, key = { it.firstOrNull()?.id ?: "" }) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val first = pair[0]
                        Box(modifier = Modifier.weight(1f)) {
                            ToolGridItem(
                                tool = first,
                                isArabic = isArabic,
                                themeColors = themeColors,
                                onClick = { first.onClick(viewModel) }
                            )
                        }
                        if (pair.size > 1) {
                            val second = pair[1]
                            Box(modifier = Modifier.weight(1f)) {
                                ToolGridItem(
                                    tool = second,
                                    isArabic = isArabic,
                                    themeColors = themeColors,
                                    onClick = { second.onClick(viewModel) }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolGridItem(
    tool: ToolItem,
    isArabic: Boolean,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = themeColors.surface,
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Container - dynamic soft theme background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(themeColors.accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    tool.iconContent(themeColors.accent)
                }

                // Small arrow
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open",
                    tint = themeColors.translationText.copy(alpha = 0.4f),
                    modifier = Modifier.size(14.dp)
                )
            }

            // Info text block
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isArabic) tool.titleAr else tool.titleEn,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText,
                        fontSize = 13.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = if (isArabic) tool.subtitleAr else tool.subtitleEn,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = themeColors.translationText,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
