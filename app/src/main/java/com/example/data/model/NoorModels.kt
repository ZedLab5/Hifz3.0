package com.example.data.model

import androidx.annotation.DrawableRes

data class PrayerZone(
    val id: String,
    val name: String,
    val arabicName: String,
    val country: String,
    val zoneLabel: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneId: String
) {
    val nameEn: String get() = name
    val nameAr: String get() = arabicName
    val zoneTag: String get() = zoneLabel
}

data class CalculationAuthority(
    val id: String,
    val name: String,
    val description: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaIntervalMinutes: Int = 0
)

data class PrayerTime(
    val name: String,
    val arabicName: String,
    val timeString: String,
    val hour: Int,
    val minute: Int,
    val isNext: Boolean = false,
    val isPast: Boolean = false,
    val isCurrent: Boolean = false,
    val isCompleted: Boolean = false
)

data class DhikrItem(
    val id: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val defaultTarget: Int = 33,
    val virtue: String = "",
    val audioUrl: String = "",
    val audioStartMs: Int = 0
)

data class Reciter(
    val id: String,
    val name: String,
    val style: String,
    val country: String,
    val avatarUrl: String = "",
    val nameAr: String = "",
    val styleAr: String = "",
    @DrawableRes val drawableRes: Int = 0
)

data class SurahMeta(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val englishMeaning: String,
    val totalVerses: Int,
    val revelationType: String
)

data class Verse(
    val surahNumber: Int,
    val verseNumber: Int,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val tafsirShort: String = "",
    val juz: Int = 1,
    val page: Int = 1,
    val absoluteAyahIndex: Int = 0
)

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val englishMeaning: String,
    val totalVerses: Int,
    val revelationType: String,
    val verses: List<Verse> = emptyList()
)

data class DailyMoodWisdom(
    val mood: String,
    val isIslamic: Boolean,
    val arabicText: String,
    val translation: String,
    val source: String,
    val explanation: String,
    val sourceAr: String = "",
    val explanationAr: String = ""
)

data class DuaCategory(
    val id: String,
    val titleEnglish: String,
    val titleArabic: String,
    val description: String,
    val itemCount: Int,
    val iconType: String
)

data class DuaItem(
    val id: String,
    val category: String,
    val title: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val reference: String,
    val occasion: String = "",
    val repeatCount: Int = 1,
    val benefit: String = "",
    val categoryAr: String = "",
    val referenceAr: String = "",
    val audioUrl: String = ""
)

data class HadithItem(
    val id: String,
    val book: String,
    val hadithNumber: String,
    val chapter: String,
    val chapterAr: String = "",
    val narrator: String,
    val arabicText: String,
    val translation: String,
    val topics: List<String> = emptyList(),
    val grade: String = "Sahih",
    val explanation: String = ""
)
