package com.example.data.model

import androidx.annotation.FontRes
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.R

enum class QuranArabicFont(
    val id: String,
    val displayName: String,
    val previewArabic: String,
    @FontRes val fontRes: Int
) {
    AMIRI(
        id = "amiri",
        displayName = "Amiri Classical",
        previewArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
        fontRes = R.font.amiri_quran
    ),
    UTHMANI(
        id = "uthmani",
        displayName = "Uthmani Script",
        previewArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
        fontRes = R.font.scheherazade_new
    ),
    PLAIN_NASKH(
        id = "plain_naskh",
        displayName = "Plain Naskh",
        previewArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
        fontRes = R.font.noto_naskh_arabic
    );

    val fontFamily: FontFamily
        get() = FontFamily(Font(fontRes, FontWeight.Normal))

    companion object {
        fun fromId(id: String?): QuranArabicFont {
            return values().find { it.id.equals(id, ignoreCase = true) } ?: AMIRI
        }
    }
}
