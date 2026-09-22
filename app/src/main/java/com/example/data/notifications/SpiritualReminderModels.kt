package com.example.data.notifications

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.NoorDestination

enum class SpiritualReminderCategory(val titleEn: String, val titleAr: String) {
    QURAN_HIFZ("Quran & Memorization", "القرآن الكريم والحفظ"),
    DAILY_AZKAR("Daily Azkar & Sunnah", "الأذكار والسنن اليومية"),
    VOLUNTARY_WORSHIP("Voluntary Worship & Fasting", "النوافل والصيام"),
    CONSISTENCY("Habit Consistency & Streaks", "الالتزام وحماية السلسلة")
}

data class SpiritualReminderItem(
    val id: String,
    val category: SpiritualReminderCategory,
    val titleEn: String,
    val titleAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val defaultHour: Int,
    val defaultMinute: Int,
    val defaultEnabled: Boolean,
    val targetDestination: NoorDestination,
    val supportsCustomTime: Boolean = true,
    val isWeeklyFridayOnly: Boolean = false,
    val isFastingSpecific: Boolean = false
)

data class SpiritualReminderState(
    val isEnabled: Boolean,
    val hour: Int,
    val minute: Int
)

object SpiritualReminderRepository {

    val ID_HIFZ_REVISION = "hifz_revision"
    val ID_KHATMA_TARGET = "khatma_target"
    val ID_FRIDAY_KAHF = "friday_kahf"
    val ID_MORNING_AZKAR = "morning_azkar"
    val ID_EVENING_AZKAR = "evening_azkar"
    val ID_BEDTIME_AZKAR = "bedtime_azkar"
    val ID_DAILY_AYAH = "daily_ayah"
    val ID_TAHAJJUD_QIYAM = "tahajjud_qiyam"
    val ID_SALAT_DUHA = "salat_duha"
    val ID_SUNNAH_FASTING = "sunnah_fasting"
    val ID_QAZA_REMINDER = "qaza_reminder"
    val ID_STREAK_SAFEGUARD = "streak_safeguard"

    val reminders: List<SpiritualReminderItem> = listOf(
        // 1. Quran & Memorization
        SpiritualReminderItem(
            id = ID_HIFZ_REVISION,
            category = SpiritualReminderCategory.QURAN_HIFZ,
            titleEn = "Daily Hifz Revision",
            titleAr = "مراجعة ورد الحفظ",
            descriptionEn = "Time for your daily Quran memorization & retention review session.",
            descriptionAr = "حان وقت جلسة مراجعة وتثبيت ما حفظت من كتاب الله.",
            defaultHour = 17,
            defaultMinute = 0,
            defaultEnabled = true,
            targetDestination = NoorDestination.QURAN_MEMORIZATION
        ),
        SpiritualReminderItem(
            id = ID_KHATMA_TARGET,
            category = SpiritualReminderCategory.QURAN_HIFZ,
            titleEn = "Daily Khatma Milestone",
            titleAr = "ورد الختمة القرآنية",
            descriptionEn = "Gentle reminder to fulfill today's Quran reading pages or ayahs.",
            descriptionAr = "تذكير لإتمام ورد القراءة اليومي ومواصلة ختمتك القرآنية.",
            defaultHour = 19,
            defaultMinute = 30,
            defaultEnabled = true,
            targetDestination = NoorDestination.QURAN_KHATMA
        ),
        SpiritualReminderItem(
            id = ID_FRIDAY_KAHF,
            category = SpiritualReminderCategory.QURAN_HIFZ,
            titleEn = "Friday Surah Al-Kahf",
            titleAr = "سورة الكهف يوم الجمعة",
            descriptionEn = "A light between two Fridays. Recite Surat Al-Kahf on blessed Friday.",
            descriptionAr = "نور ما بين الجمعتين. تذكير بقراءة سورة الكهف في يوم الجمعة المبارك.",
            defaultHour = 9,
            defaultMinute = 0,
            defaultEnabled = true,
            isWeeklyFridayOnly = true,
            targetDestination = NoorDestination.QURAN_SURAH_LIST
        ),

        // 2. Daily Azkar & Sunnah
        SpiritualReminderItem(
            id = ID_MORNING_AZKAR,
            category = SpiritualReminderCategory.DAILY_AZKAR,
            titleEn = "Morning Athkar",
            titleAr = "أذكار الصباح",
            descriptionEn = "Fortify your morning with Sunnah supplications and divine protection.",
            descriptionAr = "حصن يومك بالذكر النبوي: 'ألا بذكر الله تطمئن القلوب'.",
            defaultHour = 7,
            defaultMinute = 0,
            defaultEnabled = true,
            targetDestination = NoorDestination.AZKAR_READER
        ),
        SpiritualReminderItem(
            id = ID_EVENING_AZKAR,
            category = SpiritualReminderCategory.DAILY_AZKAR,
            titleEn = "Evening Athkar",
            titleAr = "أذكار المساء",
            descriptionEn = "Supplications for inner peace and evening spiritual renewal.",
            descriptionAr = "أذكار المساء وحفظ النفس والسكينة قبل غروب الشمس.",
            defaultHour = 17,
            defaultMinute = 30,
            defaultEnabled = true,
            targetDestination = NoorDestination.AZKAR_READER
        ),
        SpiritualReminderItem(
            id = ID_BEDTIME_AZKAR,
            category = SpiritualReminderCategory.DAILY_AZKAR,
            titleEn = "Bedtime Sunnah Athkar",
            titleAr = "أذكار النوم والسنة",
            descriptionEn = "Ayat al-Kursi, Surat Al-Mulk, and peaceful night supplications.",
            descriptionAr = "آية الكرسي، سورة الملك، وأدعية النوم النبوية لنوم هادئ ومحفوظ.",
            defaultHour = 22,
            defaultMinute = 0,
            defaultEnabled = true,
            targetDestination = NoorDestination.AZKAR_READER
        ),
        SpiritualReminderItem(
            id = ID_DAILY_AYAH,
            category = SpiritualReminderCategory.DAILY_AZKAR,
            titleEn = "Daily Ayah & Spiritual Reflection",
            titleAr = "آية اليوم والتأمل الصباحي",
            descriptionEn = "A selected Quranic verse and wisdom to brighten your thoughts.",
            descriptionAr = "آية مباركة مع تدبر هادئ لبدء يومك بهداية وطمأنينة.",
            defaultHour = 8,
            defaultMinute = 30,
            defaultEnabled = true,
            targetDestination = NoorDestination.HOME
        ),

        // 3. Voluntary Worship & Fasting
        SpiritualReminderItem(
            id = ID_TAHAJJUD_QIYAM,
            category = SpiritualReminderCategory.VOLUNTARY_WORSHIP,
            titleEn = "Tahajjud / Qiyam al-Layl",
            titleAr = "قيام الليل والتهجد",
            descriptionEn = "Awaken for the blessed last third of the night when prayers are answered.",
            descriptionAr = "تنبيه للاستيقاظ والوقوف بين يدي الله في الثلث الأخير من الليل.",
            defaultHour = 4,
            defaultMinute = 0,
            defaultEnabled = false,
            targetDestination = NoorDestination.SALAT
        ),
        SpiritualReminderItem(
            id = ID_SALAT_DUHA,
            category = SpiritualReminderCategory.VOLUNTARY_WORSHIP,
            titleEn = "Salat ad-Duha (Forenoon Prayer)",
            titleAr = "صلاة الضحى (صلاة الأوابين)",
            descriptionEn = "The prayer of the returners; an act of daily gratitude for your body.",
            descriptionAr = "صلاة الأوابين وصدقة عن كل مفصل وعظم في جسدك.",
            defaultHour = 9,
            defaultMinute = 30,
            defaultEnabled = false,
            targetDestination = NoorDestination.SALAT
        ),
        SpiritualReminderItem(
            id = ID_SUNNAH_FASTING,
            category = SpiritualReminderCategory.VOLUNTARY_WORSHIP,
            titleEn = "Sunnah Fasting Alerts",
            titleAr = "تذكير صيام السنن والأيام البيض",
            descriptionEn = "Evening alert before Monday & Thursday fasts and the White Days.",
            descriptionAr = "تنبيه مسائي يذكرك بفضل صيام الإثنين والخميس والأيام البيض المباركة.",
            defaultHour = 20,
            defaultMinute = 30,
            defaultEnabled = false,
            isFastingSpecific = true,
            targetDestination = NoorDestination.HOME
        ),
        SpiritualReminderItem(
            id = ID_QAZA_REMINDER,
            category = SpiritualReminderCategory.VOLUNTARY_WORSHIP,
            titleEn = "Missed Prayer (Qaza) Logger",
            titleAr = "مراجعة الصلوات الفائتة والقضاء",
            descriptionEn = "Evening prompt to log, review, and make up any missed daily prayers.",
            descriptionAr = "تذكير مسائي لمراجعة صلوات اليوم وتسجيل ما يلزم قضاؤه.",
            defaultHour = 21,
            defaultMinute = 30,
            defaultEnabled = false,
            targetDestination = NoorDestination.SALAT
        ),

        // 4. Consistency & Streaks
        SpiritualReminderItem(
            id = ID_STREAK_SAFEGUARD,
            category = SpiritualReminderCategory.CONSISTENCY,
            titleEn = "Daily Streak Safeguard",
            titleAr = "حماية السلسلة والالتزام",
            descriptionEn = "Protect your spiritual streak! Gentle nudge if today's activity is still empty.",
            descriptionAr = "حافظ على استمرارية وردك! تنبيه مسائي لطيف إذا لم تسجل وردك اليوم.",
            defaultHour = 20,
            defaultMinute = 45,
            defaultEnabled = true,
            targetDestination = NoorDestination.STREAKS
        )
    )

    fun loadAllStates(prefs: SharedPreferences): Map<String, SpiritualReminderState> {
        return reminders.associate { item ->
            val isEnabled = prefs.getBoolean("spiritual_reminder_enabled_${item.id}", item.defaultEnabled)
            val hour = prefs.getInt("spiritual_reminder_hour_${item.id}", item.defaultHour)
            val minute = prefs.getInt("spiritual_reminder_minute_${item.id}", item.defaultMinute)
            item.id to SpiritualReminderState(isEnabled = isEnabled, hour = hour, minute = minute)
        }
    }

    fun saveState(prefs: SharedPreferences, id: String, state: SpiritualReminderState) {
        prefs.edit()
            .putBoolean("spiritual_reminder_enabled_$id", state.isEnabled)
            .putInt("spiritual_reminder_hour_$id", state.hour)
            .putInt("spiritual_reminder_minute_$id", state.minute)
            .apply()
    }

    fun getItem(id: String): SpiritualReminderItem? = reminders.firstOrNull { it.id == id }
}
