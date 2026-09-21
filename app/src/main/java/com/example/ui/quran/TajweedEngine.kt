package com.example.ui.quran

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.theme.QuranReadingThemeColors

/**
 * Screen corner position for the persistent Floating Tajweed Button.
 */
enum class TajweedButtonPosition(
    val title: String,
    val iconSymbol: String
) {
    TOP_LEFT("Top Left", "↖"),
    TOP_RIGHT("Top Right", "↗"),
    BOTTOM_LEFT("Bottom Left", "↙"),
    BOTTOM_RIGHT("Bottom Right", "↘")
}

/**
 * Core Tajweed Categories according to standard Quranic recitation rules.
 */
enum class TajweedCategory(
    val titleEn: String,
    val titleAr: String,
    val description: String,
    val examples: String
) {
    MADD(
        titleEn = "Madd (Elongation)",
        titleAr = "مدّ",
        description = "Prolongation of sound for 2, 4, 5, or 6 counts on long vowels or maddah marks.",
        examples = "جَاءَ • السَّمَاءِ • ۤ"
    ),
    GHUNNAH(
        titleEn = "Ghunnah (Nasalization)",
        titleAr = "غُنَّة",
        description = "Nasal resonance held for 2 counts on Noon or Meem carrying a Shaddah.",
        examples = "إِنَّ • ثُمَّ • مِّنْ"
    ),
    QALQALAH(
        titleEn = "Qalqalah (Echoing)",
        titleAr = "قَلْقَلَة",
        description = "Bouncing or echoing sound produced when (ق, ط, ب, ج, د) are Sakin.",
        examples = "أَحَدٌ • ٱلْفَلَقِ • يَقْتُلُونَ"
    ),
    IKHFA(
        titleEn = "Ikhfa (Concealment)",
        titleAr = "إِخْفَاء",
        description = "Concealing Noon Sakinah or Tanween with ghunnah before 15 Ikhfa letters.",
        examples = "مِندُونِ • أَنفُسِكُمْ • كُنتُمْ"
    ),
    IDGHAM(
        titleEn = "Idgham (Merging)",
        titleAr = "إِدْغَام",
        description = "Merging Noon Sakinah or Tanween into the following letter (يرملون).",
        examples = "مَن يَقُولُ • مِّن رَّبِّهِمْ"
    ),
    IQLAB(
        titleEn = "Iqlab (Conversion)",
        titleAr = "إِقْلَاب",
        description = "Converting Noon Sakinah or Tanween to a Meem sound when followed by Ba.",
        examples = "مِن بَعْدِ • أَنۢبِئْهُم"
    ),
    MEEM_SAKINAH(
        titleEn = "Meem Sakinah Rules",
        titleAr = "أَحْكَامُ الْمِيمِ السَّاكِنَةِ",
        description = "Special concealment (Ikhfa) or merging (Idgham) rules for unvoweled Meem.",
        examples = "لَهُم بَشِّرْ • لَهُم مَّا"
    )
}

/**
 * Adaptive Tajweed Color Palette designed for readability across Light, Sepia, and Obsidian themes.
 */
object TajweedThemePalette {
    fun getPalette(themeColors: QuranReadingThemeColors): Map<TajweedCategory, Color> {
        return if (themeColors.isDark) {
            // Obsidian / Dark Theme Palette (Vibrant, high contrast pastels)
            mapOf(
                TajweedCategory.MADD to Color(0xFFE040FB),        // Neon Violet/Pink
                TajweedCategory.GHUNNAH to Color(0xFFFFB74D),     // Warm Gold/Amber
                TajweedCategory.QALQALAH to Color(0xFFFF5252),    // Bright Coral Red
                TajweedCategory.IKHFA to Color(0xFF64FFDA),       // Mint / Cyan Teal
                TajweedCategory.IDGHAM to Color(0xFF448AFF),      // Electric Sky Blue
                TajweedCategory.IQLAB to Color(0xFFB388FF),       // Lavender Indigo
                TajweedCategory.MEEM_SAKINAH to Color(0xFFFF80AB) // Bright Rose
            )
        } else if (themeColors.name == "Sepia Parchment") {
            // Sepia Theme Palette (Warm, rich tones compatible with parchment)
            mapOf(
                TajweedCategory.MADD to Color(0xFF7B1FA2),        // Deep Violet
                TajweedCategory.GHUNNAH to Color(0xFFE65100),     // Rich Amber
                TajweedCategory.QALQALAH to Color(0xFFC62828),    // Crimson Red
                TajweedCategory.IKHFA to Color(0xFF00695C),       // Dark Forest Teal
                TajweedCategory.IDGHAM to Color(0xFF1565C0),      // Deep Sapphire Blue
                TajweedCategory.IQLAB to Color(0xFF4A148C),       // Deep Plum
                TajweedCategory.MEEM_SAKINAH to Color(0xFFAD1457) // Deep Wine Red
            )
        } else {
            // Standard Light Theme Palette (Vibrant, crisp, distinct colors)
            mapOf(
                TajweedCategory.MADD to Color(0xFF8E24AA),        // Purple
                TajweedCategory.GHUNNAH to Color(0xFFF57C00),     // Dark Amber
                TajweedCategory.QALQALAH to Color(0xFFD32F2F),    // Red
                TajweedCategory.IKHFA to Color(0xFF00897B),       // Emerald Teal
                TajweedCategory.IDGHAM to Color(0xFF1976D2),      // Cobalt Blue
                TajweedCategory.IQLAB to Color(0xFF512DA8),       // Indigo
                TajweedCategory.MEEM_SAKINAH to Color(0xFFC2185B) // Magenta
            )
        }
    }
}

/**
 * High-performance Tajweed Parser & AnnotatedString Builder for Arabic Uthmani text.
 */
object TajweedEngine {

    private val QALQALAH_LETTERS = setOf('ق', 'ط', 'ب', 'ج', 'د')
    private val IKHFA_LETTERS = setOf('ت', 'ث', 'ج', 'د', 'ذ', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ف', 'ق', 'ك')
    private val IDGHAM_LETTERS = setOf('ي', 'ر', 'م', 'ل', 'و', 'ن')

    // Diacritic character constants
    private const val SHADDAH = '\u0651'
    private const val SUKUN = '\u0652'
    private const val UTHMANI_SUKUN = '\u06E1'
    private const val MADDAH_ABOVE = '\u0653'
    private const val FATHATAN = '\u064B'
    private const val DAMMATAN = '\u064C'
    private const val KASRATAN = '\u064D'
    private const val SMALL_MEEM_ISOLATED = '\u06E2'
    private const val SMALL_MEEM_HIGH = '\u06D8'
    private const val DAGGER_ALEF = '\u0670'

    fun formatTajweedText(
        arabicText: String,
        isEnabled: Boolean,
        themeColors: QuranReadingThemeColors,
        baseColor: Color = themeColors.arabicText
    ): AnnotatedString {
        if (!isEnabled || arabicText.isBlank()) {
            return AnnotatedString(
                text = arabicText,
                spanStyle = SpanStyle(color = baseColor)
            )
        }

        val palette = TajweedThemePalette.getPalette(themeColors)
        val textLength = arabicText.length

        val rangesToStyle = mutableListOf<Triple<Int, Int, SpanStyle>>()

        fun applyStyleRange(start: Int, end: Int, style: SpanStyle) {
            val s = start.coerceIn(0, textLength)
            val e = end.coerceIn(0, textLength)
            if (s < e) {
                rangesToStyle.add(Triple(s, e, style))
            }
        }

        var i = 0
        while (i < textLength) {
            val char = arabicText[i]

            // --- 1. Madd (Elongation) Check ---
            if (char == MADDAH_ABOVE || char == 'آ') {
                val start = (i - 1).coerceAtLeast(0)
                val end = (i + 2).coerceAtMost(textLength)
                palette[TajweedCategory.MADD]?.let { color ->
                    applyStyleRange(start, end, SpanStyle(color = color))
                }
            } else if (char == DAGGER_ALEF || char == 'ۥ' || char == 'ۦ') {
                // Check if followed by Hamzah
                val nextHamzah = findNextNonWhitespace(arabicText, i + 1)
                if (nextHamzah != null && isHamzah(nextHamzah.second)) {
                    palette[TajweedCategory.MADD]?.let { color ->
                        applyStyleRange(i, (nextHamzah.first + 1).coerceAtMost(textLength), SpanStyle(color = color))
                    }
                }
            }

            // --- 2. Ghunnah Check (Noon or Meem with Shaddah) ---
            if ((char == 'ن' || char == 'م') && hasDiacritic(arabicText, i, SHADDAH)) {
                val end = getDiacriticEndIndex(arabicText, i)
                palette[TajweedCategory.GHUNNAH]?.let { color ->
                    applyStyleRange(i, end, SpanStyle(color = color))
                }
            }

            // --- 3. Qalqalah Check (ق, ط, ب, ج, د with Sakin) ---
            if (char in QALQALAH_LETTERS) {
                val isSakin = hasDiacritic(arabicText, i, SUKUN) ||
                        hasDiacritic(arabicText, i, UTHMANI_SUKUN) ||
                        isEndOrNextSpace(arabicText, i)
                val isShaddah = hasDiacritic(arabicText, i, SHADDAH)

                if (isSakin && !isShaddah) {
                    val end = getDiacriticEndIndex(arabicText, i)
                    palette[TajweedCategory.QALQALAH]?.let { color ->
                        applyStyleRange(i, end, SpanStyle(color = color))
                    }
                }
            }

            // --- 4. Noon Sakinah & Tanween Rules (Ikhfa, Idgham, Iqlab) ---
            val isNoonSakin = char == 'ن' && (hasDiacritic(arabicText, i, SUKUN) ||
                    hasDiacritic(arabicText, i, UTHMANI_SUKUN) ||
                    !hasAnyVowel(arabicText, i))
            val isTanween = isTanweenChar(char) || hasDiacritic(arabicText, i, FATHATAN) ||
                    hasDiacritic(arabicText, i, DAMMATAN) || hasDiacritic(arabicText, i, KASRATAN)

            if (isNoonSakin || isTanween) {
                val nextPair = findNextLetter(arabicText, getDiacriticEndIndex(arabicText, i))
                if (nextPair != null) {
                    val nextIndex = nextPair.first
                    val nextChar = nextPair.second

                    if (nextChar == 'ب' || hasDiacritic(arabicText, i, SMALL_MEEM_ISOLATED) || hasDiacritic(arabicText, i, SMALL_MEEM_HIGH)) {
                        // Iqlab
                        palette[TajweedCategory.IQLAB]?.let { color ->
                            applyStyleRange(i, (nextIndex + 1).coerceAtMost(textLength), SpanStyle(color = color))
                        }
                    } else if (nextChar in IKHFA_LETTERS) {
                        // Ikhfa
                        palette[TajweedCategory.IKHFA]?.let { color ->
                            applyStyleRange(i, (nextIndex + 1).coerceAtMost(textLength), SpanStyle(color = color))
                        }
                    } else if (nextChar in IDGHAM_LETTERS) {
                        // Idgham
                        palette[TajweedCategory.IDGHAM]?.let { color ->
                            applyStyleRange(i, (nextIndex + 1).coerceAtMost(textLength), SpanStyle(color = color))
                        }
                    }
                }
            }

            // --- 5. Meem Sakinah Rules ---
            if (char == 'م' && (hasDiacritic(arabicText, i, SUKUN) || hasDiacritic(arabicText, i, UTHMANI_SUKUN) || !hasAnyVowel(arabicText, i))) {
                val nextPair = findNextLetter(arabicText, getDiacriticEndIndex(arabicText, i))
                if (nextPair != null) {
                    val nextIndex = nextPair.first
                    val nextChar = nextPair.second
                    if (nextChar == 'ب' || nextChar == 'م') {
                        palette[TajweedCategory.MEEM_SAKINAH]?.let { color ->
                            applyStyleRange(i, (nextIndex + 1).coerceAtMost(textLength), SpanStyle(color = color))
                        }
                    }
                }
            }

            i++
        }

        return buildAnnotatedString {
            append(arabicText)
            addStyle(SpanStyle(color = baseColor), 0, textLength)
            for (range in rangesToStyle) {
                val start = range.first
                val end = getDiacriticEndIndex(arabicText, range.second - 1).coerceAtMost(textLength)
                addStyle(range.third, start, end)
            }
        }
    }

    private fun hasDiacritic(text: String, letterIndex: Int, targetDiacritic: Char): Boolean {
        var idx = letterIndex + 1
        while (idx < text.length) {
            val c = text[idx]
            if (c == targetDiacritic) return true
            if (isBaseLetter(c) || c.isWhitespace()) break
            idx++
        }
        return false
    }

    private fun hasAnyVowel(text: String, letterIndex: Int): Boolean {
        var idx = letterIndex + 1
        while (idx < text.length) {
            val c = text[idx]
            if (c in '\u064B'..'\u0652' || c == UTHMANI_SUKUN) return true
            if (isBaseLetter(c) || c.isWhitespace()) break
            idx++
        }
        return false
    }

    private fun getDiacriticEndIndex(text: String, letterIndex: Int): Int {
        var idx = letterIndex + 1
        while (idx < text.length) {
            val c = text[idx]
            if (isBaseLetter(c) || c.isWhitespace()) break
            idx++
        }
        return idx
    }

    private fun findNextLetter(text: String, startIdx: Int): Pair<Int, Char>? {
        var idx = startIdx
        while (idx < text.length) {
            val c = text[idx]
            if (isBaseLetter(c)) return Pair(idx, c)
            idx++
        }
        return null
    }

    private fun findNextNonWhitespace(text: String, startIdx: Int): Pair<Int, Char>? {
        var idx = startIdx
        while (idx < text.length) {
            val c = text[idx]
            if (!c.isWhitespace()) return Pair(idx, c)
            idx++
        }
        return null
    }

    private fun isBaseLetter(c: Char): Boolean {
        return (c in '\u0621'..'\u064A') || c == 'آ' || c == 'أ' || c == 'إ' || c == 'ؤ' || c == 'ئ' || c == 'ى'
    }

    private fun isTanweenChar(c: Char): Boolean {
        return c == FATHATAN || c == DAMMATAN || c == KASRATAN
    }

    private fun isHamzah(c: Char): Boolean {
        return c == 'ء' || c == 'أ' || c == 'إ' || c == 'ؤ' || c == 'ئ'
    }

    private fun isEndOrNextSpace(text: String, letterIndex: Int): Boolean {
        val end = getDiacriticEndIndex(text, letterIndex)
        return end >= text.length || text[end].isWhitespace()
    }
}
