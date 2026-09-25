package com.example.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.localization.tr
import com.example.ui.MainViewModel
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.DangerRedDark
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.DarkPine
import com.example.ui.theme.DeepVibrantTeal
import com.example.ui.theme.GoldBadgeBg
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.SecondaryGoldDark
import com.example.ui.theme.SecondaryGoldLight
import com.example.ui.theme.GoldTintBgLight
import com.example.ui.theme.ReadingThemeColors
import com.example.ui.theme.ReadingThemes
import com.example.ui.theme.SlateTealMuted
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceElevatedLight
import com.example.ui.theme.BorderDividerDark

private val SalatEmeraldPrimary = Color(0xFF107C41)
private val SalatBadgeBg = Color(0xFFEDF2F7)
private val SalatBadgeText = Color(0xFF2A4365)
private val SalatDivider = Color(0xFFECEFF1)
private val SalatCardBg = Color(0xFFF6F8F7)

private val NoorTealDark = SecondaryGoldLight
private val NoorTealVibrant = SecondaryGoldDark
private val NoorDarkPine = DarkPine
private val NoorSageSlate = SlateTealMuted
private val NoorGoldAccent = MetallicGold
private val NoorGoldSoft = GoldBadgeBg
private val NoorGoldBorder = MetallicGold.copy(alpha = 0.3f)
private val NoorCardBorder = SoftTealTint
private val NoorSurfaceSoft = SurfaceElevatedLight
private val NoorSoftGreenBg = CanvasMint
private val NoorSoftGreenBorder = SoftTealTint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val userBio by viewModel.userBio.collectAsStateWithLifecycle()
    val isCloudSync by viewModel.isCloudSyncEnabled.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage == "ar"

    val readingThemeName by viewModel.sharedReadingTheme.collectAsStateWithLifecycle()
    val themeColors = remember(readingThemeName) { ReadingThemes.getThemeByName(readingThemeName) }
    val isDark = themeColors.isDark

    var showEditProfileSheet by remember { mutableStateOf(false) }
    var showChangePasswordSheet by remember { mutableStateOf(false) }
    var showConnectSheet by remember { mutableStateOf(false) }
    var showBackupRestoreSheet by remember { mutableStateOf(false) }
    var showPrivacyPolicySheet by remember { mutableStateOf(false) }
    var showContactSupportDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NoorTopBar(
                title = tr("profile_title", viewModel),
                eyebrow = if (isArabic) "الملف الشخصي" else "SPIRITUAL PROFILE",
                subtitle = if (isUserLoggedIn) userName else (if (isArabic) "الملف الشخصي والخصوصية" else "Profile & Data Privacy"),
                onBackClick = onNavigateBack,
                backContentDescription = stringResource(R.string.action_back),
                isDark = isDark,
                themeColors = themeColors,
                actions = {
                    NoorGlassIconButton(
                        onClick = { viewModel.openSettingsModal() },
                        icon = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            )
        },
        containerColor = if (isDark) themeColors.background else Color.White,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. User Profile Header Card
            item(key = "user_header_section") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    UserProfileHeader(
                        viewModel = viewModel,
                        isUserLoggedIn = isUserLoggedIn,
                        userName = userName,
                        userEmail = userEmail,
                        onEditClick = { if (isUserLoggedIn) showEditProfileSheet = true else showConnectSheet = true },
                        onAvatarClick = { if (isUserLoggedIn) showEditProfileSheet = true else showConnectSheet = true },
                        themeColors = themeColors
                    )
                }
            }

            // 2. Data & Sync Section (Backup and Restore, Cloud Sync)
            item(key = "data_sync_section") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    DataSyncSection(
                        isUserLoggedIn = isUserLoggedIn,
                        isCloudSync = isCloudSync,
                        onBackupRestoreClick = { showBackupRestoreSheet = true },
                        onCloudSyncToggle = { viewModel.toggleCloudSync() },
                        onConnectClick = { showConnectSheet = true },
                        themeColors = themeColors
                    )
                }
            }

            // 3. App Preferences & Security Section
            item(key = "app_preferences_section") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    PreferencesSecuritySection(
                        isUserLoggedIn = isUserLoggedIn,
                        onEditProfileClick = { if (isUserLoggedIn) showEditProfileSheet = true else showConnectSheet = true },
                        onChangePasswordClick = { if (isUserLoggedIn) showChangePasswordSheet = true else showConnectSheet = true },
                        onAppSettingsClick = { viewModel.openSettingsModal() },
                        themeColors = themeColors
                    )
                }
            }

            // 4. Support & Legal Section (Privacy Policy & Contact Us)
            item(key = "support_legal_section") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SupportLegalSection(
                        onPrivacyPolicyClick = { showPrivacyPolicySheet = true },
                        onContactSupportClick = { showContactSupportDialog = true },
                        themeColors = themeColors
                    )
                }
            }

            // 5. Account Actions Section (Log Out, Delete Account)
            item(key = "account_actions_section") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AccountActionsSection(
                        viewModel = viewModel,
                        isUserLoggedIn = isUserLoggedIn,
                        onConnectClick = { showConnectSheet = true },
                        onLogOutClick = { showSignOutDialog = true },
                        onDeleteAccountClick = { showDeleteAccountDialog = true },
                        themeColors = themeColors
                    )
                }
            }
        }

        // Backup & Restore Bottom Sheet
        if (showBackupRestoreSheet) {
            BackupRestoreSheet(
                viewModel = viewModel,
                onDismiss = { showBackupRestoreSheet = false },
                themeColors = themeColors
            )
        }

        // Privacy Policy Bottom Sheet
        if (showPrivacyPolicySheet) {
            PrivacyPolicyBottomSheet(
                onDismiss = { showPrivacyPolicySheet = false },
                themeColors = themeColors
            )
        }

        // Contact Support Dialog
        if (showContactSupportDialog) {
            ContactSupportDialog(
                viewModel = viewModel,
                onDismiss = { showContactSupportDialog = false },
                themeColors = themeColors
            )
        }

        // Connect Account Bottom Sheet
        if (showConnectSheet) {
            ConnectAccountBottomSheet(
                viewModel = viewModel,
                onDismiss = { showConnectSheet = false },
                onConnectSuccess = { name, email, bio ->
                    viewModel.connectUser(name, email, bio)
                    showConnectSheet = false
                },
                themeColors = themeColors
            )
        }

        // Edit Profile Bottom Sheet
        if (showEditProfileSheet) {
            EditProfileBottomSheet(
                viewModel = viewModel,
                initialName = userName,
                initialEmail = userEmail,
                initialBio = userBio,
                onDismiss = { showEditProfileSheet = false },
                onSave = { name, email, bio ->
                    viewModel.updateUserProfile(name, email, bio, "")
                    showEditProfileSheet = false
                },
                themeColors = themeColors
            )
        }

        // Change Password Bottom Sheet / Dialog
        if (showChangePasswordSheet) {
            ChangePasswordBottomSheet(
                viewModel = viewModel,
                onDismiss = { showChangePasswordSheet = false },
                onSave = { newPass ->
                    viewModel.updatePassword(newPass)
                    showChangePasswordSheet = false
                },
                themeColors = themeColors
            )
        }

        // Sign Out Confirmation Dialog
        if (showSignOutDialog) {
            AlertDialog(
                onDismissRequest = { showSignOutDialog = false },
                title = { Text(tr("profile_confirm_signout", viewModel), fontWeight = FontWeight.Bold, color = themeColors.arabicText) },
                text = { Text(tr("profile_confirm_signout_sub", viewModel), color = themeColors.translationText) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.disconnectUser()
                            showSignOutDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) DangerRedDark else DangerRedLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(tr("profile_sign_out", viewModel), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSignOutDialog = false }) {
                        Text(stringResource(R.string.action_cancel), color = themeColors.translationText)
                    }
                },
                containerColor = if (isDark) themeColors.surface else Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // Delete Account Confirmation Dialog
        if (showDeleteAccountDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAccountDialog = false },
                title = { Text("Delete Account?", fontWeight = FontWeight.Bold, color = if (isDark) DangerRedDark else DangerRedLight) },
                text = { Text("This will permanently remove your account data and spiritual profile from this device. This action cannot be undone.", color = themeColors.translationText) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteAccount()
                            showDeleteAccountDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) DangerRedDark else DangerRedLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Delete Permanently", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAccountDialog = false }) {
                        Text(stringResource(R.string.action_cancel), color = themeColors.translationText)
                    }
                },
                containerColor = if (isDark) themeColors.surface else Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

/**
 * 1. User Header: clean seamless surface, NO border around container or avatar image
 */
@Composable
private fun UserProfileHeader(
    viewModel: MainViewModel,
    isUserLoggedIn: Boolean,
    userName: String,
    userEmail: String,
    onEditClick: () -> Unit,
    onAvatarClick: () -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    modifier: Modifier = Modifier
) {
    val isDark = themeColors.isDark
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (isDark) themeColors.surface else SalatCardBg,
        shape = RoundedCornerShape(16.dp),
        border = null,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar without border
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(if (isDark) themeColors.border else Color.White)
                    .clickable { onAvatarClick() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_user_avatar),
                    contentDescription = "Profile Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtle edit badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (isDark) themeColors.accent else SalatEmeraldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Tap to edit",
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isUserLoggedIn) userName else "Guest Mode",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText,
                        fontSize = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = if (isUserLoggedIn && userEmail.isNotBlank()) userEmail else "Tap to personalize offline profile",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = themeColors.translationText,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.clickable { onEditClick() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) themeColors.border else Color.White,
                    border = null
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = if (isUserLoggedIn) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (isUserLoggedIn) (if (isDark) themeColors.accent else SalatEmeraldPrimary) else themeColors.translationText,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isUserLoggedIn) "Offline Profile Active (100% Private)" else "Guest Mode (On-Device Only)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isUserLoggedIn) (if (isDark) themeColors.accent else SalatEmeraldPrimary) else themeColors.translationText,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * 2. Data & Sync Section
 */
@Composable
private fun DataSyncSection(
    isUserLoggedIn: Boolean,
    isCloudSync: Boolean,
    onBackupRestoreClick: () -> Unit,
    onCloudSyncToggle: () -> Unit,
    onConnectClick: () -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    modifier: Modifier = Modifier
) {
    val isDark = themeColors.isDark
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "DATA & LOCAL PRIVACY",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF5F5E5A),
                fontSize = 11.5.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDark) themeColors.surface else SalatCardBg,
            border = null,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Backup & Restore
                SettingsRowItem(
                    icon = Icons.Default.Sync,
                    title = "Backup & Restore",
                    subtitle = "Export to Drive/Email or import Khatma & bookmarks",
                    badge = "New",
                    onClick = onBackupRestoreClick,
                    themeColors = themeColors
                )

                HorizontalDivider(color = (if (isDark) themeColors.border else SalatDivider).copy(alpha = 0.7f), modifier = Modifier.padding(horizontal = 16.dp))

                // Cloud Sync Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!isUserLoggedIn) onConnectClick() else onCloudSyncToggle()
                        }
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDark) SecondaryGoldDark.copy(alpha = 0.18f) else SalatEmeraldPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = if (isDark) SecondaryGoldDark else SalatEmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Local Mirror Syncing",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.arabicText,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = "Local device only. Use Export to back up your data.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = themeColors.translationText,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    Switch(
                        checked = isUserLoggedIn && isCloudSync,
                        onCheckedChange = {
                            if (!isUserLoggedIn) onConnectClick() else onCloudSyncToggle()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = if (isDark) SecondaryGoldDark else SalatEmeraldPrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = if (isDark) BorderDividerDark else SalatDivider
                        )
                    )
                }
            }
        }
    }
}

/**
 * 3. App Preferences & Security Section
 */
@Composable
private fun PreferencesSecuritySection(
    isUserLoggedIn: Boolean,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onAppSettingsClick: () -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    modifier: Modifier = Modifier
) {
    val isDark = themeColors.isDark
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "ACCOUNT & SETTINGS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF5F5E5A),
                fontSize = 11.5.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDark) themeColors.surface else SalatCardBg,
            border = null,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // App Settings
                SettingsRowItem(
                    icon = Icons.Default.Settings,
                    title = "App Settings",
                    subtitle = "Language, theme, notifications & prayer sound",
                    onClick = onAppSettingsClick,
                    themeColors = themeColors
                )

                HorizontalDivider(color = (if (isDark) themeColors.border else SalatDivider).copy(alpha = 0.7f), modifier = Modifier.padding(horizontal = 16.dp))

                // Edit Profile
                SettingsRowItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    subtitle = "Update display name, avatar or bio",
                    onClick = onEditProfileClick,
                    themeColors = themeColors
                )

                HorizontalDivider(color = (if (isDark) themeColors.border else SalatDivider).copy(alpha = 0.7f), modifier = Modifier.padding(horizontal = 16.dp))

                // Change Password
                SettingsRowItem(
                    icon = Icons.Default.Lock,
                    title = "Security & Password",
                    subtitle = "Quick password & security update",
                    onClick = onChangePasswordClick,
                    themeColors = themeColors
                )
            }
        }
    }
}

/**
 * 4. Support & Legal Section
 */
@Composable
private fun SupportLegalSection(
    onPrivacyPolicyClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    modifier: Modifier = Modifier
) {
    val isDark = themeColors.isDark
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "SUPPORT & LEGAL",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF5F5E5A),
                fontSize = 11.5.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDark) themeColors.surface else SalatCardBg,
            border = null,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Privacy Policy
                SettingsRowItem(
                    icon = Icons.Default.Security,
                    title = "Privacy Policy & Data Security",
                    subtitle = "100% offline, zero tracking, your data belongs to you",
                    onClick = onPrivacyPolicyClick,
                    themeColors = themeColors
                )

                HorizontalDivider(color = (if (isDark) themeColors.border else SalatDivider).copy(alpha = 0.7f), modifier = Modifier.padding(horizontal = 16.dp))

                // Contact Us
                SettingsRowItem(
                    icon = Icons.Default.Email,
                    title = "Contact Us & Feedback",
                    subtitle = "Reach our support team or request new features",
                    onClick = onContactSupportClick,
                    themeColors = themeColors
                )
            }
        }
    }
}

/**
 * 5. Account Actions
 */
@Composable
private fun AccountActionsSection(
    viewModel: MainViewModel,
    isUserLoggedIn: Boolean,
    onConnectClick: () -> Unit,
    onLogOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp,
    modifier: Modifier = Modifier
) {
    val isDark = themeColors.isDark
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "ACCOUNT ACTIONS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF5F5E5A),
                fontSize = 11.5.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDark) themeColors.surface else SalatCardBg,
            border = null,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                if (!isUserLoggedIn) {
                    SettingsRowItem(
                        icon = Icons.Default.CloudSync,
                        title = "Connect Account",
                        subtitle = "Sign in with Cloud Account or Email",
                        onClick = onConnectClick,
                        themeColors = themeColors
                    )
                } else {
                    SettingsRowItem(
                        icon = Icons.Default.Logout,
                        title = "Log Out",
                        subtitle = "Sign out of your Noor account",
                        titleColor = if (isDark) DangerRedDark else DangerRedLight,
                        onClick = onLogOutClick,
                        themeColors = themeColors
                    )

                    HorizontalDivider(color = (if (isDark) themeColors.border else SalatDivider).copy(alpha = 0.7f), modifier = Modifier.padding(horizontal = 16.dp))

                    SettingsRowItem(
                        icon = Icons.Default.Warning,
                        title = "Delete Account",
                        subtitle = "Permanently remove account and data",
                        titleColor = if (isDark) DangerRedDark else DangerRedLight,
                        onClick = onDeleteAccountClick,
                        themeColors = themeColors
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    titleColor: Color = NoorDarkPine,
    onClick: () -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp
) {
    val isDark = themeColors.isDark
    val actualTitleColor = if (titleColor == NoorDarkPine) themeColors.arabicText else titleColor
    val isDestructive = titleColor != NoorDarkPine

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (!isDestructive) (if (isDark) SecondaryGoldDark.copy(alpha = 0.18f) else SalatEmeraldPrimary.copy(alpha = 0.12f))
                        else (if (isDark) DangerRedDark.copy(alpha = 0.15f) else DangerRedLight.copy(alpha = 0.1f))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (!isDestructive) (if (isDark) SecondaryGoldDark else SalatEmeraldPrimary) else titleColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = actualTitleColor,
                            fontSize = 14.sp
                        )
                    )
                    if (badge != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isDark) themeColors.border else SalatBadgeBg,
                            border = null
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) themeColors.accent else SalatBadgeText,
                                    fontSize = 9.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = themeColors.translationText,
                        fontSize = 11.5.sp
                    )
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = if (isDark) themeColors.accent else SalatEmeraldPrimary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivacyPolicyBottomSheet(
    onDismiss: () -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp
) {
    val isDark = themeColors.isDark
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) themeColors.surface else Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) SecondaryGoldDark.copy(alpha = 0.18f) else SalatEmeraldPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = if (isDark) SecondaryGoldDark else SalatEmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Privacy Policy & Security",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColors.arabicText,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Your spiritual journey is private and protected",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = themeColors.translationText,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Text(
                text = "Al-Noor is engineered from the ground up to guarantee total data sovereignty and privacy. We do not sell, track, or share your personal spiritual habits.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeColors.arabicText.copy(alpha = 0.85f),
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) themeColors.background else SalatCardBg)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PrivacyHighlightItem(
                    title = "100% Offline by Default",
                    description = "Quran text, translations, prayer times, and tasbih counts are stored directly on your local device.",
                    themeColors = themeColors
                )
                PrivacyHighlightItem(
                    title = "Zero Ad Tracking",
                    description = "No third-party trackers, advertisements, or data brokers are embedded in the app.",
                    themeColors = themeColors
                )
                PrivacyHighlightItem(
                    title = "Transparent Backups",
                    description = "You can export and import your entire database at any time using open, readable JSON format.",
                    themeColors = themeColors
                )
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isDark) themeColors.accent else SalatEmeraldPrimary)
            ) {
                Text("Got It", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PrivacyHighlightItem(
    title: String,
    description: String,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp
) {
    val isDark = themeColors.isDark
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (isDark) themeColors.accent else SalatEmeraldPrimary,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.arabicText,
                    fontSize = 13.sp
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeColors.translationText,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
                )
            )
        }
    }
}

@Composable
private fun ContactSupportDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp
) {
    val context = LocalContext.current
    val isDark = themeColors.isDark
    val emailAddress = "support@alnoorapp.com"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isDark) themeColors.surface else Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) SecondaryGoldDark.copy(alpha = 0.18f) else SalatEmeraldPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = if (isDark) SecondaryGoldDark else SalatEmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "Contact Al-Noor Team",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = themeColors.arabicText,
                        fontSize = 16.sp
                    )
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Have questions, feedback, or need help with your Quran Khatma and app settings? Reach out to our dedicated team anytime.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = themeColors.translationText,
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    )
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDark) themeColors.background else SalatCardBg,
                    border = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Support Email", emailAddress))
                            viewModel.showToast("Email address copied to clipboard!")
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = emailAddress,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) themeColors.accent else SalatEmeraldPrimary,
                                fontSize = 13.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Email",
                            tint = if (isDark) themeColors.accent else SalatEmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$emailAddress")
                        putExtra(Intent.EXTRA_SUBJECT, "Al-Noor App Feedback / Support")
                    }
                    try {
                        context.startActivity(Intent.createChooser(intent, "Send Email via"))
                    } catch (e: Exception) {
                        viewModel.showToast("No email client installed. Email copied to clipboard.")
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isDark) themeColors.accent else SalatEmeraldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Open Email", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = themeColors.translationText)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectAccountBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onConnectSuccess: (String, String, String) -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp
) {
    val isDark = themeColors.isDark
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var inputName by remember { mutableStateOf("Zaid Ibrahim") }
    var inputEmail by remember { mutableStateOf("zaid.ibrahim@example.com") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) themeColors.surface else Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Set Up Offline Profile",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.arabicText,
                    fontSize = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Noor is 100% offline-first. Specify an offline profile to personalize your stats and records locally.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = themeColors.translationText,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = inputName,
                onValueChange = { inputName = it },
                label = { Text("Display Name", color = themeColors.translationText) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    unfocusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    focusedTextColor = themeColors.arabicText,
                    unfocusedTextColor = themeColors.arabicText
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = inputEmail,
                onValueChange = { inputEmail = it },
                label = { Text("Email / Phone", color = themeColors.translationText) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    unfocusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    focusedTextColor = themeColors.arabicText,
                    unfocusedTextColor = themeColors.arabicText
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onConnectSuccess(inputName, inputEmail, "") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isDark) themeColors.accent else SalatEmeraldPrimary)
            ) {
                Text(
                    text = "Initialize Profile",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileBottomSheet(
    viewModel: MainViewModel,
    initialName: String,
    initialEmail: String,
    initialBio: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp
) {
    val isDark = themeColors.isDark
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var bio by remember { mutableStateOf(initialBio) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) themeColors.surface else Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.arabicText,
                    fontSize = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name", color = themeColors.translationText) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    unfocusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    focusedTextColor = themeColors.arabicText,
                    unfocusedTextColor = themeColors.arabicText
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email / Phone", color = themeColors.translationText) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    unfocusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    focusedTextColor = themeColors.arabicText,
                    unfocusedTextColor = themeColors.arabicText
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio / Intention", color = themeColors.translationText) },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    unfocusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    focusedTextColor = themeColors.arabicText,
                    unfocusedTextColor = themeColors.arabicText
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSave(name, email, bio) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isDark) themeColors.accent else SalatEmeraldPrimary)
            ) {
                Text(
                    text = stringResource(R.string.action_save),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    themeColors: ReadingThemeColors = ReadingThemes.MadaniCrisp
) {
    val isDark = themeColors.isDark
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) themeColors.surface else Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = themeColors.arabicText,
                    fontSize = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Secure your account with a strong password",
                style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = oldPass,
                onValueChange = { oldPass = it },
                label = { Text("Current Password", color = themeColors.translationText) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    unfocusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    focusedTextColor = themeColors.arabicText,
                    unfocusedTextColor = themeColors.arabicText
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it },
                label = { Text("New Password", color = themeColors.translationText) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    unfocusedContainerColor = if (themeColors.isDark) Color(0xFF1E293B) else SalatCardBg,
                    focusedTextColor = themeColors.arabicText,
                    unfocusedTextColor = themeColors.arabicText
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSave(newPass) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isDark) themeColors.accent else SalatEmeraldPrimary)
            ) {
                Text(
                    text = "Update Password",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                )
            }
        }
    }
}
