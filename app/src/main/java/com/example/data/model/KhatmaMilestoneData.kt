package com.example.data.model

data class KhatmaMilestoneData(
    val title: String,
    val subtitle: String,
    val currentJuz: Int,
    val ayahsCompletedToday: Int,
    val totalAyahsRead: Int,
    val percentage: Float,
    val reflectionAyahArabic: String = "وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا",
    val reflectionAyahEnglish: String = "And recite the Qur'an with measured recitation. (73:4)"
)
