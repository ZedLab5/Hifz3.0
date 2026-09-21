package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.QuickAccessTool
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.theme.GoldTintBgLight
import com.example.ui.theme.PrimaryTealDark
import com.example.ui.theme.SecondaryGoldLight

fun getQuickAccessToolIcon(tool: QuickAccessTool): ImageVector {
    return when (tool) {
        QuickAccessTool.DUAS -> Icons.Default.Lock
        QuickAccessTool.QIBLA -> Icons.Default.Person
        QuickAccessTool.HIFZ -> Icons.Default.Settings
        QuickAccessTool.TASBIH -> Icons.Default.Security
        QuickAccessTool.KHATMA -> Icons.Default.Email
        QuickAccessTool.QURAN -> Icons.AutoMirrored.Filled.MenuBook
        QuickAccessTool.SALAT -> Icons.Default.AccessTime
        QuickAccessTool.STREAKS -> Icons.Default.AutoAwesome
        QuickAccessTool.HABITS -> Icons.Default.Bookmark
        QuickAccessTool.AUDIO -> Icons.Default.Headphones
    }
}

fun getQuickAccessToolDestination(tool: QuickAccessTool): NoorDestination {
    return when (tool) {
        QuickAccessTool.DUAS -> NoorDestination.DUAS_LIBRARY
        QuickAccessTool.QIBLA -> NoorDestination.QIBLA
        QuickAccessTool.HIFZ -> NoorDestination.QURAN_MEMORIZATION_SETUP
        QuickAccessTool.TASBIH -> NoorDestination.TASBIH
        QuickAccessTool.KHATMA -> NoorDestination.QURAN_KHATMA
        QuickAccessTool.QURAN -> NoorDestination.QURAN_SURAH_LIST
        QuickAccessTool.SALAT -> NoorDestination.SALAT
        QuickAccessTool.STREAKS -> NoorDestination.STREAKS
        QuickAccessTool.HABITS -> NoorDestination.HABIT_TRACKER
        QuickAccessTool.AUDIO -> NoorDestination.QURAN_AUDIO_STREAM
    }
}

fun getQuickAccessToolAccent(tool: QuickAccessTool): Color {
    return SecondaryGoldLight
}

@Composable
fun QuickAccessToolVisualIcon(
    tool: QuickAccessTool,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = getQuickAccessToolIcon(tool),
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}

@Composable
fun CustomizeQuickAccessSheet(
    viewModel: MainViewModel,
    selectedTools: List<QuickAccessTool>,
    onDismiss: () -> Unit
) {
    val widgetsOrder by viewModel.homeWidgetsOrder.collectAsStateWithLifecycle()
    val widgetsVisibility by viewModel.homeWidgetsVisibility.collectAsStateWithLifecycle()

    CustomizeHomeFeedSheet(
        viewModel = viewModel,
        widgetsOrder = widgetsOrder,
        widgetsVisibility = widgetsVisibility,
        initialTab = 1,
        onDismiss = onDismiss
    )
}
