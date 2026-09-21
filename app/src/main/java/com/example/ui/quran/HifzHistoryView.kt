package com.example.ui.quran

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.quran.HifzSessionLog
import com.example.ui.theme.ReadingThemeColors

/**
 * Circular percentage progress component for all stats requiring percentage displays.
 */
@Composable
fun CircularPercentageBadge(
    percentage: Int,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp,
    progressColor: Color,
    trackColor: Color = progressColor.copy(alpha = 0.15f),
    textColor: Color,
    textSize: TextUnit = 12.sp,
    label: String? = null
) {
    val clampedPercentage = percentage.coerceIn(0, 100)
    val progress = clampedPercentage / 100f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size)
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = progressColor,
                strokeWidth = strokeWidth,
                trackColor = trackColor
            )
            Text(
                text = "$clampedPercentage%",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                    fontSize = textSize
                )
            )
        }
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 10.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HifzHistoryContent(
    sessionLogs: List<HifzSessionLog>,
    memorizedCount: Int,
    themeColors: ReadingThemeColors,
    onDrillSession: (HifzSessionLog) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val latestSession = sessionLogs.firstOrNull()
    val context = androidx.compose.ui.platform.LocalContext.current

    val revisionItems = remember(sessionLogs) {
        val prefs = context.getSharedPreferences("noor_app_preferences", android.content.Context.MODE_PRIVATE)
        val allPrefs = prefs.all
        val items = mutableListOf<HifzRevisionItem>()
        
        allPrefs.forEach { (key, value) ->
            if (key.startsWith("hifz_confidence_") && !key.startsWith("hifz_confidence_time_")) {
                val rating = value as? String ?: return@forEach
                val parts = key.split("_")
                if (parts.size >= 6) {
                    val surahNum = parts[2].toIntOrNull() ?: return@forEach
                    val startAyah = parts[3].toIntOrNull() ?: return@forEach
                    val endAyah = parts[4].toIntOrNull() ?: return@forEach
                    val mode = parts.subList(5, parts.size).joinToString("_")
                    
                    val timeKey = "hifz_confidence_time_${surahNum}_${startAyah}_${endAyah}_$mode"
                    var timestamp = prefs.getLong(timeKey, 0L)
                    if (timestamp == 0L) {
                        val matchingLog = sessionLogs.find { it.surahNumber == surahNum && it.startAyah == startAyah && it.endAyah == endAyah }
                        timestamp = matchingLog?.timestamp ?: (System.currentTimeMillis() - 12 * 60 * 60 * 1000L)
                    }
                    
                    val intervalDays = if (rating == "STILL_SHAKY") 1 else 7
                    val nextReviewTime = timestamp + (intervalDays * 24 * 60 * 60 * 1000L)
                    val diffMs = nextReviewTime - System.currentTimeMillis()
                    val dueDaysRemaining = kotlin.math.ceil(diffMs.toDouble() / (24 * 60 * 60 * 1000.0)).toInt()
                    val isDue = diffMs <= 0
                    
                    val surahName = com.example.data.quran.QuranData.surahs.find { it.number == surahNum }?.nameEnglish ?: "Unknown"
                    
                    items.add(
                        HifzRevisionItem(
                            surahNumber = surahNum,
                            surahName = surahName,
                            startAyah = startAyah,
                            endAyah = endAyah,
                            mode = mode,
                            confidenceRating = rating,
                            timestamp = timestamp,
                            dueDaysRemaining = dueDaysRemaining,
                            isDue = isDue
                        )
                    )
                }
            }
        }
        items.sortedWith(compareBy<HifzRevisionItem> { !it.isDue }.thenBy { it.dueDaysRemaining })
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. PREVIOUS SESSION SPOTLIGHT (MOST RECENT)
        if (latestSession != null) {
            PreviousSessionCard(
                session = latestSession,
                themeColors = themeColors,
                onReplaySession = { onDrillSession(latestSession) }
            )
        }

        // 2. RELEVANT & MEANINGFUL HIFZ STATS
        HifzMasteryStatsCard(
            sessionLogs = sessionLogs,
            memorizedCount = memorizedCount,
            themeColors = themeColors
        )

        // 2.5. SPACED REPETITION SCHEDULE
        SpacedRepetitionRevisionCard(
            revisionItems = revisionItems,
            themeColors = themeColors,
            onDrillRevision = { item ->
                val dummyLog = HifzSessionLog(
                    surahNumber = item.surahNumber,
                    surahName = item.surahName,
                    startAyah = item.startAyah,
                    endAyah = item.endAyah,
                    mode = item.mode,
                    totalAyahs = item.endAyah - item.startAyah + 1,
                    ayahsMemorized = item.endAyah - item.startAyah + 1,
                    ayahsMissed = 0
                )
                onDrillSession(dummyLog)
            }
        )

        // 3. RETENTION & REVISION OVERVIEW (If recent errors occurred)
        val recentErrors = remember(sessionLogs) {
            sessionLogs.take(5).filter { it.ayahsMissed > 0 }
        }
        if (recentErrors.isNotEmpty()) {
            RevisionNeededCard(
                errorSessions = recentErrors,
                themeColors = themeColors,
                onDrillSession = onDrillSession
            )
        }

        // 4. SESSION HISTORY LOGS LIST
        SessionHistoryListSection(
            sessionLogs = sessionLogs,
            themeColors = themeColors,
            onDrillSession = onDrillSession,
            onClearHistory = onClearHistory
        )
    }
}

data class HifzRevisionItem(
    val surahNumber: Int,
    val surahName: String,
    val startAyah: Int,
    val endAyah: Int,
    val mode: String,
    val confidenceRating: String,
    val timestamp: Long,
    val dueDaysRemaining: Int,
    val isDue: Boolean
)

@Composable
private fun SpacedRepetitionRevisionCard(
    revisionItems: List<HifzRevisionItem>,
    themeColors: ReadingThemeColors,
    onDrillRevision: (HifzRevisionItem) -> Unit
) {
    val dueToday = revisionItems.filter { it.isDue }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        border = null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = themeColors.accent.copy(alpha = 0.15f),
                        border = null,
                        shadowElevation = 0.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier.padding(6.dp).size(14.dp)
                        )
                    }
                    Text(
                        text = "REVISION DUE SCHEDULE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = themeColors.accent,
                            fontSize = 10.sp
                        )
                    )
                }

                Text(
                    text = "${dueToday.size} due • ${revisionItems.size} total",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = themeColors.translationText,
                        fontSize = 10.5.sp
                    )
                )
            }

            if (revisionItems.isEmpty()) {
                Text(
                    text = "No stored confidence ratings yet. Complete self-recall rounds and rate your confidence to schedule automated review cycles.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = themeColors.translationText,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    revisionItems.take(4).forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = themeColors.background,
                            border = null,
                            shadowElevation = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDrillRevision(item) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = "${item.surahName} (${item.startAyah}–${item.endAyah})",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.arabicText,
                                            fontSize = 12.5.sp
                                        )
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (item.confidenceRating == "STILL_SHAKY") Color(0xFFE57373).copy(alpha = 0.15f) else Color(0xFF81C784).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = if (item.confidenceRating == "STILL_SHAKY") "Shaky" else "Mastered",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (item.confidenceRating == "STILL_SHAKY") Color(0xFFD32F2F) else Color(0xFF388E3C),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            text = if (item.isDue) "⚠️ Review now" else "In ${item.dueDaysRemaining}d",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (item.isDue) Color(0xFFD32F2F) else themeColors.translationText,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = themeColors.accent.copy(alpha = 0.12f),
                                    border = null,
                                    shadowElevation = 0.dp
                                ) {
                                    Text(
                                        text = "Revise",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = themeColors.accent,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
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

/**
 * 1. PREVIOUS SESSION SPOTLIGHT CARD
 * Clean card using app palette with zero shadows/borders and circular score percentage.
 */
@Composable
private fun PreviousSessionCard(
    session: HifzSessionLog,
    themeColors: ReadingThemeColors,
    onReplaySession: () -> Unit
) {
    val scoreColor = when {
        session.scorePercentage >= 90 -> themeColors.accent
        session.scorePercentage >= 70 -> themeColors.accent.copy(alpha = 0.85f)
        else -> Color(0xFFE57373)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("previous_session_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        border = null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: Title & Mode badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = themeColors.accent.copy(alpha = 0.15f),
                        border = null,
                        shadowElevation = 0.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier.padding(6.dp).size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "PREVIOUS SESSION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                color = themeColors.accent,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = session.relativeTime,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = themeColors.translationText,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Mode pill (Clean, no border/shadow)
                Surface(
                    shape = CircleShape,
                    color = themeColors.background,
                    border = null,
                    shadowElevation = 0.dp
                ) {
                    Text(
                        text = session.mode,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accent,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Middle Row: Surah Details & Circular Score Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Surah ${session.surahName}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = themeColors.arabicText,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Text(
                        text = "Ayahs ${session.startAyah} – ${session.endAyah} (${session.totalAyahs} verses)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = themeColors.translationText,
                            fontSize = 13.sp
                        )
                    )
                }

                // Circular percentage meter for session score
                CircularPercentageBadge(
                    percentage = session.scorePercentage,
                    size = 52.dp,
                    strokeWidth = 4.5.dp,
                    progressColor = scoreColor,
                    trackColor = scoreColor.copy(alpha = 0.15f),
                    textColor = scoreColor,
                    textSize = 13.sp,
                    label = if (session.scorePercentage >= 90) "Mastered" else if (session.scorePercentage >= 70) "Good" else "Review"
                )
            }

            // Results breakdown: Memorized vs Missed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Memorized count chip
                Surface(
                    shape = CircleShape,
                    color = themeColors.background,
                    border = null,
                    shadowElevation = 0.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${session.ayahsMemorized} Memorized",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                // Missed count chip
                Surface(
                    shape = CircleShape,
                    color = themeColors.background,
                    border = null,
                    shadowElevation = 0.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (session.ayahsMissed > 0) Icons.Default.Close else Icons.Default.Check,
                            contentDescription = null,
                            tint = if (session.ayahsMissed > 0) Color(0xFFE57373) else themeColors.translationText,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${session.ayahsMissed} Missed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (session.ayahsMissed > 0) Color(0xFFE57373) else themeColors.translationText,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }

            // Quick Replay Button (Rounded pill)
            Button(
                onClick = onReplaySession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColors.accent
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Practice This Range Again",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.5.sp
                    )
                )
            }
        }
    }
}

/**
 * 2. HIFZ MASTERY STATS CARD (Strictly relevant, necessary memorization metrics)
 */
@Composable
private fun HifzMasteryStatsCard(
    sessionLogs: List<HifzSessionLog>,
    memorizedCount: Int,
    themeColors: ReadingThemeColors
) {
    val totalQuranVerses = 6236
    val quranProgressPercentage = if (totalQuranVerses > 0) {
        ((memorizedCount.toFloat() / totalQuranVerses) * 100).toInt()
    } else 0

    val totalSessions = sessionLogs.size
    val totalVersesTested = sessionLogs.sumOf { it.totalAyahs }
    val totalMemorizedInSessions = sessionLogs.sumOf { it.ayahsMemorized }
    val accuracyPercentage = if (totalVersesTested > 0) {
        ((totalMemorizedInSessions.toFloat() / totalVersesTested) * 100).toInt()
    } else 100

    val distinctSurahsCount = remember(sessionLogs) {
        sessionLogs.map { it.surahNumber }.distinct().size
    }

    val totalDrillRepetitions = remember(sessionLogs) {
        sessionLogs.sumOf { (it.totalAyahs * it.repetitionsCompleted).coerceAtLeast(1) }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        border = null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "HIFZ MASTERY & PROGRESS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = themeColors.accent,
                    fontSize = 10.sp
                )
            )

            // Circular Percentage Metrics Row (Percentages are rendered in circular meters)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Percentage: Quran Memorization Coverage
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CircularPercentageBadge(
                        percentage = quranProgressPercentage,
                        size = 64.dp,
                        strokeWidth = 5.dp,
                        progressColor = themeColors.accent,
                        trackColor = themeColors.accent.copy(alpha = 0.15f),
                        textColor = themeColors.arabicText,
                        textSize = 14.sp
                    )
                    Text(
                        text = "Qur'an Coverage",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "$memorizedCount / 6,236 Ayahs",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.translationText,
                            fontSize = 10.sp
                        )
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .width(1.dp)
                        .background(themeColors.border.copy(alpha = 0.25f))
                )

                // Circular Percentage: Recall Accuracy
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CircularPercentageBadge(
                        percentage = accuracyPercentage,
                        size = 64.dp,
                        strokeWidth = 5.dp,
                        progressColor = themeColors.accent,
                        trackColor = themeColors.accent.copy(alpha = 0.15f),
                        textColor = themeColors.arabicText,
                        textSize = 14.sp
                    )
                    Text(
                        text = "Recall Accuracy",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "$totalMemorizedInSessions / $totalVersesTested Verses",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.translationText,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            HorizontalDivider(color = themeColors.border.copy(alpha = 0.2f))

            // Secondary Supporting Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CleanStatMetricItem(
                    label = "Total Sessions",
                    value = "$totalSessions",
                    icon = Icons.Default.Timeline,
                    themeColors = themeColors,
                    modifier = Modifier.weight(1f)
                )
                CleanStatMetricItem(
                    label = "Surahs Practiced",
                    value = "$distinctSurahsCount",
                    icon = Icons.Default.MenuBook,
                    themeColors = themeColors,
                    modifier = Modifier.weight(1f)
                )
                CleanStatMetricItem(
                    label = "Total Drills",
                    value = "$totalDrillRepetitions",
                    icon = Icons.Default.Repeat,
                    themeColors = themeColors,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CleanStatMetricItem(
    label: String,
    value: String,
    icon: ImageVector,
    themeColors: ReadingThemeColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = themeColors.background,
            border = null,
            shadowElevation = 0.dp,
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = themeColors.accent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = themeColors.arabicText,
                fontSize = 16.sp
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = themeColors.translationText,
                fontSize = 10.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 3. REVISION NEEDED CARD
 * Shows past sessions with missed verses to facilitate targeted revision.
 */
@Composable
private fun RevisionNeededCard(
    errorSessions: List<HifzSessionLog>,
    themeColors: ReadingThemeColors,
    onDrillSession: (HifzSessionLog) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        border = null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE57373).copy(alpha = 0.15f),
                        border = null,
                        shadowElevation = 0.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color(0xFFE57373),
                            modifier = Modifier.padding(6.dp).size(14.dp)
                        )
                    }
                    Text(
                        text = "RECENT REVISION TARGETS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = themeColors.accent,
                            fontSize = 10.sp
                        )
                    )
                }

                Text(
                    text = "${errorSessions.size} to review",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = themeColors.translationText,
                        fontSize = 10.5.sp
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                errorSessions.take(3).forEach { session ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = themeColors.background,
                        border = null,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDrillSession(session) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Surah ${session.surahName} (${session.startAyah}–${session.endAyah})",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.arabicText,
                                        fontSize = 12.5.sp
                                    )
                                )
                                Text(
                                    text = "${session.ayahsMissed} missed • ${session.relativeTime}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFE57373),
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = themeColors.accent.copy(alpha = 0.12f),
                                border = null,
                                shadowElevation = 0.dp
                            ) {
                                Text(
                                    text = "Revise",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.accent,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4. PAST SESSIONS LOGS LIST SECTION
 */
@Composable
private fun SessionHistoryListSection(
    sessionLogs: List<HifzSessionLog>,
    themeColors: ReadingThemeColors,
    onDrillSession: (HifzSessionLog) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PAST SESSION LOGS (${sessionLogs.size})",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = themeColors.translationText,
                    fontSize = 11.sp
                )
            )

            if (sessionLogs.isNotEmpty()) {
                TextButton(
                    onClick = onClearHistory,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Clear History",
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Clear",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFE57373),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        if (sessionLogs.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = themeColors.surface,
                border = null,
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = themeColors.translationText,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "No Session History Yet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText
                        )
                    )
                    Text(
                        text = "Complete a Practice or Self-Recall drill to record your memorization statistics.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.translationText,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        } else {
            sessionLogs.forEach { log ->
                SessionHistoryItemRow(
                    log = log,
                    themeColors = themeColors,
                    onDrill = { onDrillSession(log) }
                )
            }
        }
    }
}

@Composable
private fun SessionHistoryItemRow(
    log: HifzSessionLog,
    themeColors: ReadingThemeColors,
    onDrill: () -> Unit
) {
    val scoreColor = when {
        log.scorePercentage >= 90 -> themeColors.accent
        log.scorePercentage >= 70 -> themeColors.accent.copy(alpha = 0.85f)
        else -> Color(0xFFE57373)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onDrill() },
        shape = RoundedCornerShape(16.dp),
        color = themeColors.surface,
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
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Score Percentage displayed as a Circle Meter
                CircularPercentageBadge(
                    percentage = log.scorePercentage,
                    size = 42.dp,
                    strokeWidth = 3.5.dp,
                    progressColor = scoreColor,
                    trackColor = scoreColor.copy(alpha = 0.15f),
                    textColor = scoreColor,
                    textSize = 11.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Surah ${log.surahName}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.arabicText,
                                fontSize = 14.sp
                            )
                        )
                        // Pill badge for mode
                        Surface(
                            shape = CircleShape,
                            color = themeColors.background,
                            border = null,
                            shadowElevation = 0.dp
                        ) {
                            Text(
                                text = log.mode,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.accent,
                                    fontSize = 9.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Ayahs ${log.startAyah}–${log.endAyah} • ${log.ayahsMemorized}/${log.totalAyahs} memorized${if (log.ayahsMissed > 0) " • ${log.ayahsMissed} missed" else ""}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.translationText,
                            fontSize = 11.sp
                        )
                    )

                    Text(
                        text = log.formattedDate,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.translationText.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Arrow to drill
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Practice",
                tint = themeColors.translationText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
