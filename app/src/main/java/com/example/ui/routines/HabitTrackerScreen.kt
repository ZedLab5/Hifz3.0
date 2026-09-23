package com.example.ui.routines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.BentoCard
import com.example.ui.components.BelowTopBarSpacer
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.components.RadialProgressRing
import com.example.ui.streaks.StreaksStatsContent
import com.example.ui.theme.SoftTealTint
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceElevatedLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTrackerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    initialTab: Int = 0
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isLangArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
            appLanguage == "العربية" ||
            appLanguage.startsWith("ar", ignoreCase = true)

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val themeColors = if (isDark) com.example.ui.theme.ReadingThemes.ObsidianNight else com.example.ui.theme.ReadingThemes.MadaniCrisp
    var showAddDialog by remember { mutableStateOf(false) }
    var habitTitle by remember { mutableStateOf("") }
    var habitTarget by remember { mutableStateOf("10") }
    var habitCategory by remember { mutableStateOf("Dhikr") }

    val completedCount = habits.count { it.isCompleted }
    val totalCount = habits.size.coerceAtLeast(1)
    val progress = completedCount.toFloat() / totalCount.toFloat()

    Scaffold(
        topBar = {
            NoorTopBar(
                title = if (selectedTab == 0) (if (isLangArabic) "المهام والعادات اليومية" else "Daily Routine & Habits") else (if (isLangArabic) "إحصائيات السلسلة والالتزام" else "Streaks & Consistency"),
                eyebrow = "ISTIQAMAH",
                subtitle = if (selectedTab == 0) "$completedCount/$totalCount Completed • Steadfast Consistency" else "Unified Daily Spiritual Stats",
                onBackClick = { viewModel.navigateBack() },
                backContentDescription = "Back",
                isDark = isDark,
                themeColors = themeColors,
                actions = {
                    if (selectedTab == 0) {
                        NoorGlassIconButton(
                            onClick = { showAddDialog = true },
                            icon = Icons.Default.Add,
                            contentDescription = "Add Habit"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = themeColors.accent,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Habit")
                }
            }
        },
        containerColor = themeColors.background,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Hifz-style Segmented Pill Tab Switcher: Tasks (0) | Streaks Stats (1)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .height(42.dp),
                shape = CircleShape,
                color = if (isDark) SurfaceElevatedDark else SurfaceElevatedLight
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Tab 0: Tasks
                    val isTasksSelected = selectedTab == 0
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(if (isTasksSelected) themeColors.accent else Color.Transparent)
                            .clickable { selectedTab = 0 },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (isTasksSelected) Color.White else themeColors.translationText,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (isLangArabic) "المهام والعادات" else "Daily Routine",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (isTasksSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isTasksSelected) Color.White else themeColors.translationText,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }

                    // Tab 1: Streaks Stats
                    val isStatsSelected = selectedTab == 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(if (isStatsSelected) themeColors.accent else Color.Transparent)
                            .clickable { selectedTab = 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isStatsSelected) Color.White else themeColors.translationText,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (isLangArabic) "إحصائيات السلسلة" else "Streaks Stats",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (isStatsSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isStatsSelected) Color.White else themeColors.translationText,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedTab == 0) {
                // Tasks Tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Overall Progress Summary Bento Card
                    BentoCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Today's Spiritual Momentum",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.arabicText
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (progress >= 1f) "Masha'Allah! All daily spiritual goals completed today!" else "$completedCount of $totalCount daily spiritual habits accomplished.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = themeColors.translationText)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            RadialProgressRing(
                                progress = progress,
                                modifier = Modifier.size(76.dp),
                                strokeWidth = 8.dp
                            ) {
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = themeColors.arabicText
                                    )
                                )
                            }
                        }
                    }

                    // Habits List
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(habits, key = { it.id }) { habit ->
                            val habitFraction = if (habit.targetCount > 0) (habit.currentCount.toFloat() / habit.targetCount.toFloat()).coerceIn(0f, 1f) else 0f

                            SwipeableHabitRow(
                                habit = habit,
                                themeColors = themeColors,
                                isDark = isDark,
                                onMarkAsDone = { viewModel.markHabitDone(habit) },
                                onDelete = { viewModel.deleteHabit(habit) }
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { viewModel.incrementHabit(habit) },
                                    color = themeColors.surface,
                                    border = null,
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isDark) themeColors.accent.copy(alpha = 0.18f) else SurfaceElevatedLight),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = if (habit.isCompleted) Icons.Default.Check else Icons.Default.Star,
                                                        contentDescription = "Icon",
                                                        tint = if (habit.isCompleted) themeColors.arabicText else themeColors.accent,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }

                                                Column {
                                                    Text(
                                                        text = habit.title,
                                                        style = MaterialTheme.typography.titleSmall.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = themeColors.arabicText
                                                        )
                                                    )
                                                    Text(
                                                        text = "${habit.category} • Swipe right to complete, left to delete",
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = themeColors.translationText,
                                                            fontSize = 11.sp
                                                        )
                                                    )
                                                }
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = "${habit.currentCount}/${habit.targetCount}",
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = themeColors.accent
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        LinearProgressIndicator(
                                            progress = { habitFraction },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = themeColors.accent,
                                            trackColor = if (isDark) themeColors.border else SurfaceElevatedLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Streaks Stats Tab
                StreaksStatsContent(viewModel = viewModel)
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Spiritual Habit", color = themeColors.arabicText, fontWeight = FontWeight.Bold) },
            containerColor = themeColors.surface,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = habitTitle,
                        onValueChange = { habitTitle = it },
                        label = { Text("Habit Title", color = themeColors.translationText) },
                        placeholder = { Text("e.g. Read Surah Al-Mulk before bed", color = themeColors.translationText.copy(alpha = 0.6f)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedContainerColor = if (isDark) themeColors.background else SoftTealTint.copy(alpha = 0.5f),
                            unfocusedContainerColor = if (isDark) themeColors.background else SoftTealTint.copy(alpha = 0.5f),
                            focusedTextColor = themeColors.arabicText,
                            unfocusedTextColor = themeColors.arabicText
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = habitTarget,
                        onValueChange = { habitTarget = it },
                        label = { Text("Target Daily Count", color = themeColors.translationText) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedContainerColor = if (isDark) themeColors.background else SoftTealTint.copy(alpha = 0.5f),
                            unfocusedContainerColor = if (isDark) themeColors.background else SoftTealTint.copy(alpha = 0.5f),
                            focusedTextColor = themeColors.arabicText,
                            unfocusedTextColor = themeColors.arabicText
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = habitTarget.toIntOrNull() ?: 1
                        if (habitTitle.isNotBlank()) {
                            viewModel.addCustomHabit(habitTitle, target, habitCategory)
                            habitTitle = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent)
                ) {
                    Text("Add", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = themeColors.translationText)
                }
            }
        )
    }
}

@Composable
fun SwipeableHabitRow(
    habit: com.example.data.local.DailyHabitEntity,
    themeColors: com.example.ui.theme.ReadingThemeColors,
    isDark: Boolean,
    onMarkAsDone: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val swipeOffsetX = remember { androidx.compose.animation.core.Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
    ) {
        // Swipe Backgrounds (Green for Right swipe, Red for Left swipe)
        val offset = swipeOffsetX.value
        if (offset != 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = if (offset > 0) Color(0xFF1BA486) else Color(0xFFE53935), // Primary Teal vs Soft Red
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = if (offset > 0) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (offset > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Mark as Done",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Delete",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Foreground Card Content
        Box(
            modifier = Modifier
                .offset { androidx.compose.ui.unit.IntOffset(offset.toInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val targetOffset = swipeOffsetX.value
                                if (targetOffset > 150f) {
                                    onMarkAsDone()
                                    swipeOffsetX.animateTo(0f)
                                } else if (targetOffset < -150f) {
                                    onDelete()
                                    swipeOffsetX.animateTo(0f)
                                } else {
                                    swipeOffsetX.animateTo(0f)
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                val newOffset = (swipeOffsetX.value + dragAmount).coerceIn(-300f, 300f)
                                swipeOffsetX.snapTo(newOffset)
                            }
                        }
                    )
                }
                .fillMaxWidth()
        ) {
            content()
        }
    }
}
