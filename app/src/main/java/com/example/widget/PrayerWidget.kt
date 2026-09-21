package com.example.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.data.model.PrayerTime
import com.example.data.prayer.PrayerCalculator
import com.example.data.repository.NoorRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

import com.example.ui.theme.BorderDividerDark
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.TextSecondaryDark

// ---------- Brand tokens (matches Color.kt / Obsidian Night — keep in sync if the app palette changes) ----------
private val ObsidianBgTop = android.graphics.Color.parseColor("#101B22")
private val ObsidianBgMid = android.graphics.Color.parseColor("#0C151B")
private val ObsidianBgBottom = android.graphics.Color.parseColor("#070D11")

private val AccentGreen = PrimaryTealDark     // bright, non-dark green accent
private val TextVisible = TextSecondaryDark     // muted-but-legible tone for prayer names, location, date
private val DividerColor = BorderDividerDark

data class PrayerWidgetData(
    val cityName: String,
    val dateFormatted: String,
    val nextPrayerName: String,
    val nextPrayerArabic: String,
    val nextPrayerTime: String,
    val prayers: List<PrayerTime>
)

/**
 * Generates the background gradient at the widget's ACTUAL rendered size (in px),
 * so it never looks stretched/blurry on differently-sized launcher grid cells.
 * Callers must pass real pixel dimensions derived from the widget's current size.
 */
private fun createBackgroundBitmap(widthPx: Int, heightPx: Int): Bitmap {
    val safeW = widthPx.coerceAtLeast(1)
    val safeH = heightPx.coerceAtLeast(1)
    val bitmap = Bitmap.createBitmap(safeW, safeH, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        shader = LinearGradient(
            0f, 0f, 0f, safeH.toFloat(),
            intArrayOf(ObsidianBgTop, ObsidianBgMid, ObsidianBgBottom),
            floatArrayOf(0f, 0.55f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawRect(0f, 0f, safeW.toFloat(), safeH.toFloat(), paint)
    return bitmap
}

class PrayerWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = loadPrayerData(context)
        provideContent {
            PrayerWidgetContent(data)
        }
    }

    companion object {
        fun loadPrayerData(context: Context): PrayerWidgetData {
            return try {
                val prefs = context.getSharedPreferences("noor_app_preferences", Context.MODE_PRIVATE)
                val savedZoneId = prefs.getString("selected_prayer_zone_id", null)
                val zone = NoorRepository.prayerZones.find { it.id == savedZoneId } ?: NoorRepository.prayerZones.first()
                val savedAuthId = prefs.getString("selected_calc_authority_id", null)
                val auth = NoorRepository.calculationAuthorities.find { it.id == savedAuthId } ?: NoorRepository.calculationAuthorities.first()
                val isHanafi = prefs.getBoolean("is_hanafi_asr", false)
                val customLocation = prefs.getString("custom_location_name", null) ?: zone.name

                val offsets = mutableMapOf<String, Int>()
                listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha").forEach { pName ->
                    offsets[pName] = prefs.getInt("manual_offset_$pName", 0)
                }

                val today = LocalDate.now()
                val allPrayers = PrayerCalculator.calculatePrayerTimesList(
                    zone = zone,
                    authority = auth,
                    isHanafiAsr = isHanafi,
                    date = today,
                    minuteOffsets = offsets
                )

                val salatPrayers = allPrayers.filter { it.name != "Sunrise" }
                val nextPrayer = salatPrayers.find { it.isNext }
                    ?: salatPrayers.firstOrNull()
                    ?: PrayerTime("Fajr", "الفجر", "05:00", 5, 0, isNext = true)

                val dateStr = DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ENGLISH).format(today)

                PrayerWidgetData(
                    cityName = customLocation,
                    dateFormatted = dateStr,
                    nextPrayerName = nextPrayer.name,
                    nextPrayerArabic = nextPrayer.arabicName,
                    nextPrayerTime = nextPrayer.timeString,
                    prayers = allPrayers
                )
            } catch (e: Exception) {
                e.printStackTrace()
                fallbackData()
            }
        }

        private fun fallbackData() = PrayerWidgetData(
            cityName = "Noor",
            dateFormatted = "Today",
            nextPrayerName = "Fajr",
            nextPrayerArabic = "الفجر",
            nextPrayerTime = "05:00",
            prayers = listOf(
                PrayerTime("Fajr", "الفجر", "05:00", 5, 0, isNext = true),
                PrayerTime("Sunrise", "الشروق", "06:30", 6, 30),
                PrayerTime("Dhuhr", "الظهر", "12:30", 12, 30),
                PrayerTime("Asr", "العصر", "15:45", 15, 45),
                PrayerTime("Maghrib", "المغرب", "18:20", 18, 20),
                PrayerTime("Isha", "العشاء", "19:50", 19, 50)
            )
        )
    }
}

fun format12Hour(timeStr: String): String {
    return try {
        val clean = timeStr.trim()
        if (clean.contains("AM", ignoreCase = true) || clean.contains("PM", ignoreCase = true)) {
            return clean
        }
        val parts = clean.split(":")
        if (parts.size >= 2) {
            val h = parts[0].toInt()
            val m = parts[1].toInt()
            val amPm = if (h >= 12) "PM" else "AM"
            val h12 = when {
                h == 0 -> 12
                h > 12 -> h - 12
                else -> h
            }
            String.format(Locale.US, "%02d:%02d %s", h12, m, amPm)
        } else clean
    } catch (e: Exception) {
        timeStr
    }
}

fun formatCleanTime(timeStr: String): String {
    return timeStr.replace(" AM", "").replace(" PM", "").replace(" ص", "").replace(" م", "").trim()
}

@Composable
fun PrayerWidgetContent(data: PrayerWidgetData) {
    // Derive the ACTUAL widget size so the background bitmap is generated at the right
    // resolution — fixes blurring/stretching on different launcher grid sizes.
    val size = LocalSize.current
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density
    val widthPx = (size.width.value * density).roundToInt()
    val heightPx = (size.height.value * density).roundToInt()
    val bgBitmap = createBackgroundBitmap(widthPx, heightPx)

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(20.dp)
            .background(ImageProvider(bgBitmap))
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // ---------- Header label ----------
            Text(
                text = "NEXT PRAYER",
                style = TextStyle(
                    color = ColorProvider(AccentGreen),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )

            Spacer(modifier = GlanceModifier.height(10.dp))

            // ---------- Hero row: name, time on the right ----------
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.nextPrayerName,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    modifier = GlanceModifier.defaultWeight()
                )

                Text(
                    text = format12Hour(data.nextPrayerTime),
                    style = TextStyle(
                        color = ColorProvider(AccentGreen),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = GlanceModifier.height(14.dp))

            // Thin divider
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DividerColor)
            ) {}

            // Balanced spacing: equal gap above and below the prayer row relative to the footer
            Spacer(modifier = GlanceModifier.height(18.dp))

            // ---------- Prayer strip: plain text, no containers, no Sunrise (5 columns) ----------
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val displayList = data.prayers
                    .filter { it.name != "Sunrise" }
                    .ifEmpty {
                        listOf(
                            PrayerTime("Fajr", "الفجر", "05:00", 5, 0, isNext = true),
                            PrayerTime("Dhuhr", "الظهر", "12:30", 12, 30),
                            PrayerTime("Asr", "العصر", "15:45", 15, 45),
                            PrayerTime("Maghrib", "المغرب", "18:20", 18, 20),
                            PrayerTime("Isha", "العشاء", "19:50", 19, 50)
                        )
                    }

                displayList.forEach { prayer ->
                    val isNext = prayer.isNext || prayer.name.equals(data.nextPrayerName, ignoreCase = true)
                    val nameColor = if (isNext) AccentGreen else TextVisible
                    val timeColor = if (isNext) AccentGreen else TextVisible

                    Column(
                        modifier = GlanceModifier.defaultWeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = prayer.name,
                            style = TextStyle(
                                color = ColorProvider(nameColor),
                                fontSize = 13.sp,
                                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium
                            ),
                            maxLines = 1
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = formatCleanTime(prayer.timeString),
                            style = TextStyle(
                                color = ColorProvider(timeColor),
                                fontSize = 15.sp,
                                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium
                            ),
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            // ---------- Footer: location + date ----------
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.cityName.uppercase(Locale.getDefault()),
                    style = TextStyle(
                        color = ColorProvider(TextVisible),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    modifier = GlanceModifier.defaultWeight()
                )
                Text(
                    text = data.dateFormatted,
                    style = TextStyle(
                        color = ColorProvider(TextVisible),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
            }
        }
    }
}