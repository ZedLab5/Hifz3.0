package com.example.data.model

enum class QuickAccessTool(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val subtitleEn: String,
    val subtitleAr: String,
    val defaultSelected: Boolean = false
) {
    DUAS(
        id = "duas",
        titleEn = "Duas",
        titleAr = "الأدعية",
        subtitleEn = "Supplications & Fortress",
        subtitleAr = "أدعية وأذكار مختارة",
        defaultSelected = true
    ),
    QIBLA(
        id = "qibla",
        titleEn = "Qibla",
        titleAr = "القبلة",
        subtitleEn = "Live Compass",
        subtitleAr = "بوصلة الكعبة",
        defaultSelected = true
    ),
    HIFZ(
        id = "hifz",
        titleEn = "Hifz",
        titleAr = "الحفظ",
        subtitleEn = "Quran Memorization",
        subtitleAr = "تحفيظ ومراجعة",
        defaultSelected = true
    ),
    TASBIH(
        id = "tasbih",
        titleEn = "Tasbih",
        titleAr = "التسبيح",
        subtitleEn = "Daily Dhikr",
        subtitleAr = "الأذكار والعداد",
        defaultSelected = true
    ),
    KHATMA(
        id = "khatma",
        titleEn = "Khatma",
        titleAr = "الختمة",
        subtitleEn = "Reading Pace & Goals",
        subtitleAr = "خطة وختم القرآن",
        defaultSelected = true
    ),
    QURAN(
        id = "quran",
        titleEn = "Quran",
        titleAr = "القرآن",
        subtitleEn = "Surahs & Reading",
        subtitleAr = "السور والتلاوة",
        defaultSelected = false
    ),
    SALAT(
        id = "salat",
        titleEn = "Salat",
        titleAr = "الصلاة",
        subtitleEn = "Times & Records",
        subtitleAr = "المواقيت والتدوين",
        defaultSelected = false
    ),
    STREAKS(
        id = "streaks",
        titleEn = "Streaks",
        titleAr = "الإنجاز",
        subtitleEn = "Daily Milestones",
        subtitleAr = "تتبع الأيام والأوسمة",
        defaultSelected = false
    ),
    HABITS(
        id = "habits",
        titleEn = "Habits",
        titleAr = "السنن",
        subtitleEn = "Daily Routines",
        subtitleAr = "الروتين اليومي",
        defaultSelected = false
    ),
    AUDIO(
        id = "audio",
        titleEn = "Audio",
        titleAr = "التلاوات",
        subtitleEn = "Background Stream",
        subtitleAr = "تلاوات قرآنية عذبة",
        defaultSelected = false
    );

    companion object {
        fun defaultTools(): List<QuickAccessTool> = listOf(
            DUAS,
            QIBLA,
            HIFZ,
            TASBIH,
            KHATMA
        )
    }
}
