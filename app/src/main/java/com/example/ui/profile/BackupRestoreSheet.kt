package com.example.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DriveFolderUpload
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.theme.BorderDividerLight
import com.example.ui.theme.BorderDividerDark
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.HeaderGradientLight
import com.example.ui.theme.PrimaryTealLight
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.ReadingThemes
import com.example.ui.theme.SurfaceElevatedLight
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.TextSecondaryDark
import kotlinx.coroutines.launch

private val SalatEmeraldPrimary = Color(0xFF107C41)
private val SalatBadgeBg = Color(0xFFEDF2F7)
private val SalatBadgeText = Color(0xFF2A4365)
private val SalatDivider = Color(0xFFECEFF1)
private val SalatCardBg = Color(0xFFF6F8F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = themeColors.isDark
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val activePrimary = if (isDark) SecondaryGoldDark else SalatEmeraldPrimary
    val activeSurface = if (isDark) themeColors.surface else Color.White
    val activeCardBg = if (isDark) themeColors.surface else SalatCardBg
    val activeSoftGreenBg = if (isDark) PrimaryTealLight else SalatBadgeBg
    val activeTextPrimary = if (isDark) TextPrimaryDark else themeColors.arabicText
    val activeTextSecondary = if (isDark) TextSecondaryDark else themeColors.translationText

    var isExporting by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }
    var restoreStatusMessage by remember { mutableStateOf<String?>(null) }
    var restoreIsSuccess by remember { mutableStateOf(false) }
    var showExplanation by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = activeSurface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(activeTextSecondary.copy(alpha = 0.3f))
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) SecondaryGoldDark.copy(alpha = 0.18f) else SalatEmeraldPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = activePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Backup & Restore",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = activeTextPrimary,
                                fontSize = 19.sp
                            )
                        )
                        Text(
                            text = "Transfer Khatma & data across devices",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = activeTextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_cancel),
                        tint = activeTextSecondary
                    )
                }
            }

            // How It Works Explainer Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = activeSoftGreenBg,
                border = null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = activePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "How Backup & Restore Works",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = activeTextPrimary,
                                    fontSize = 14.sp
                                )
                            )
                        }

                        Text(
                            text = if (showExplanation) "Hide" else "Learn more",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = activePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.clickable { showExplanation = !showExplanation }
                        )
                    }

                    Text(
                        text = "Al-Noor stores your spiritual data locally on your device for complete privacy. Use this tool to easily export your Khatma plans, bookmarks, prayers, and streaks to Cloud Drive, Email, or transfer them to another phone.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = activeTextPrimary.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    )

                    AnimatedVisibility(visible = showExplanation) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            HorizontalDivider(color = (if (isDark) themeColors.border else SalatDivider).copy(alpha = 0.7f))
                            ExplanationStep(
                                number = "1",
                                title = "Export your data",
                                description = "Tap 'Export to Drive / Email' to generate a secure backup file or copy it to your clipboard.",
                                themeColors = themeColors
                            )
                            ExplanationStep(
                                number = "2",
                                title = "Save or Send",
                                description = "Save it to your personal Cloud Drive, send via email to yourself, or share to your new device.",
                                themeColors = themeColors
                            )
                            ExplanationStep(
                                number = "3",
                                title = "Restore on new device",
                                description = "On your other phone or tablet, paste your backup text below and tap 'Restore Backup' to instantly sync your progress.",
                                themeColors = themeColors
                            )
                        }
                    }
                }
            }

            // SECTION 1: EXPORT (Upload to Drive, Email, Files)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = activeCardBg,
                border = null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDark) SecondaryGoldDark.copy(alpha = 0.18f) else SalatEmeraldPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null,
                                tint = activePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "1. Export & Backup Data",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = activeTextPrimary,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "Upload to Cloud Drive, Email, or Files",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = activeTextSecondary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    // Export Highlights
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BackupBadge(label = "Quran Bookmark", themeColors = themeColors)
                        BackupBadge(label = "Khatma Plan", themeColors = themeColors)
                        BackupBadge(label = "Tasbih", themeColors = themeColors)
                        BackupBadge(label = "Streaks", themeColors = themeColors)
                    }

                    // Share button
                    Button(
                        onClick = {
                            isExporting = true
                            scope.launch {
                                val backupJson = viewModel.exportBackupJson()
                                viewModel.shareBackup(context, backupJson)
                                isExporting = false
                            }
                        },
                        enabled = !isExporting,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = activePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Preparing Backup...", color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Export to Drive / Email / Files",
                                color = Color.White,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Copy to clipboard secondary button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) themeColors.border else Color.White,
                        border = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                scope.launch {
                                    val backupJson = viewModel.exportBackupJson()
                                    viewModel.copyBackup(context, backupJson)
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = activePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Copy Backup Code to Clipboard",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = activePrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                    }
                }
            }

            // SECTION 2: IMPORT & RESTORE (From another device)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = activeCardBg,
                border = null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDark) SecondaryGoldDark.copy(alpha = 0.18f) else SalatEmeraldPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    tint = activePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "2. Restore on this Device",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = activeTextPrimary,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = "Paste backup code from Drive or Email",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = activeTextSecondary,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        // Quick Paste Action
                        TextButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = clipboard.primaryClip
                                if (clip != null && clip.itemCount > 0) {
                                    val text = clip.getItemAt(0).text?.toString() ?: ""
                                    if (text.isNotBlank()) {
                                        importText = text
                                        viewModel.showToast("Pasted from clipboard!")
                                    } else {
                                        viewModel.showToast("Clipboard is empty")
                                    }
                                } else {
                                    viewModel.showToast("Clipboard is empty")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = null,
                                tint = activePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Paste", color = activePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = importText,
                        onValueChange = {
                            importText = it
                            restoreStatusMessage = null
                        },
                        placeholder = {
                            Text(
                                text = "Paste the exported JSON text or backup code here...",
                                style = MaterialTheme.typography.bodySmall.copy(color = activeTextSecondary.copy(alpha = 0.6f), fontSize = 12.sp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedContainerColor = if (isDark) Color(0xFF1E293B) else Color.White,
                            unfocusedContainerColor = if (isDark) Color(0xFF1E293B) else Color.White,
                            focusedTextColor = activeTextPrimary,
                            unfocusedTextColor = activeTextPrimary
                        ),
                        maxLines = 5
                    )

                    // Status Message banner
                    if (restoreStatusMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (restoreIsSuccess) activeSoftGreenBg else Color(0xFFFDE8E8),
                            border = null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (restoreIsSuccess) Icons.Default.CheckCircle else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (restoreIsSuccess) activePrimary else Color(0xFFC0392B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = restoreStatusMessage ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (restoreIsSuccess) activePrimary else Color(0xFFC0392B),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    // Restore Action Button
                    Button(
                        onClick = {
                            isImporting = true
                            scope.launch {
                                val result = viewModel.importBackupJson(importText)
                                restoreIsSuccess = result.success
                                restoreStatusMessage = result.message
                                isImporting = false
                            }
                        },
                        enabled = importText.isNotBlank() && !isImporting,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = activePrimary,
                            disabledContainerColor = activePrimary.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        if (isImporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Restoring Data...", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Restore & Apply Backup",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ExplanationStep(
    number: String,
    title: String,
    description: String,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp
) {
    val isDark = themeColors.isDark
    val activePrimary = if (isDark) SecondaryGoldDark else SalatEmeraldPrimary
    val activeTextPrimary = if (isDark) TextPrimaryDark else themeColors.arabicText
    val activeTextSecondary = if (isDark) TextSecondaryDark else themeColors.translationText

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(activePrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = activeTextPrimary,
                    fontSize = 12.5.sp
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = activeTextSecondary,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
                )
            )
        }
    }
}

@Composable
private fun BackupBadge(
    label: String,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp
) {
    val isDark = themeColors.isDark
    val activePrimary = if (isDark) SecondaryGoldDark else SalatEmeraldPrimary
    val activeBadgeBg = if (isDark) PrimaryTealLight else SalatBadgeBg

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = activeBadgeBg,
        border = null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = activePrimary
            )
        )
    }
}
