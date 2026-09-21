package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import com.example.ui.theme.GoldTintBgDark
import com.example.ui.theme.GoldTintBgLight
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealLight
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceElevatedLight
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextSecondaryLight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.NoorTopBarGradient
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.PrimaryTealLight
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceWhite
import com.example.data.model.KhatmaMilestoneData
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.MetallicGold

@Composable
fun KhatmaMilestoneCelebrationDialog(
    milestone: KhatmaMilestoneData,
    onDismiss: () -> Unit,
    onReturnHome: () -> Unit,
    onContinueReading: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
        val goldAccent = if (isDark) SecondaryGoldDark else SecondaryGoldLight
        val primaryTeal = if (isDark) PrimaryTealDark else PrimaryTealLight
        val goldBg = if (isDark) GoldTintBgDark else GoldTintBgLight
        val surfaceBg = if (isDark) SurfaceDark else SurfaceWhite
        val surfaceElevated = if (isDark) SurfaceElevatedDark else SurfaceElevatedLight

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = surfaceBg
            ),
            border = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close button top right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (isDark) TextSecondaryDark else TextSecondaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Trophy Badge (Gold for achievement/milestone)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = goldAccent.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = goldBg,
                        border = null,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Celebration",
                                tint = goldAccent,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Arabic Praise Banner
                Text(
                    text = "مَا شَاءَ ٱللَّٰهُ • تَقَبَّلَ ٱللَّٰهُ",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Default,
                        fontSize = 24.sp,
                        color = goldAccent,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = milestone.subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                        fontSize = 14.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Progress Bar Card (Neutral surface with Teal core tracker)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = surfaceElevated,
                    border = null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Quran Progress",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "${(milestone.percentage * 100).toInt()}%",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = goldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        LinearProgressIndicator(
                            progress = { milestone.percentage.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = primaryTeal,
                            trackColor = primaryTeal.copy(alpha = 0.2f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Completed Today: ${milestone.ayahsCompletedToday} Ayahs",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                )
                            )
                            Text(
                                text = "Juz ${milestone.currentJuz} / 30",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = goldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reflection Ayah Quote (Single Gold Spotlight Card per Section 3)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = goldBg,
                    border = null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = milestone.reflectionAyahArabic,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Default,
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                                fontSize = 18.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = milestone.reflectionAyahEnglish,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                fontSize = 12.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Secondary Button (Gold role per Section 3)
                    Button(
                        onClick = {
                            onDismiss()
                            onReturnHome()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = surfaceElevated,
                            contentColor = goldAccent
                        )
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = goldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Home", fontWeight = FontWeight.Bold, color = goldAccent)
                    }

                    // Primary CTA (Locked Teal role per Section 2)
                    Button(
                        onClick = {
                            onDismiss()
                            onContinueReading()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryTeal,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Continue", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
