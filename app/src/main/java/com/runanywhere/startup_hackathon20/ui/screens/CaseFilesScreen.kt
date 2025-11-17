package com.runanywhere.startup_hackathon20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runanywhere.startup_hackathon20.model.CaseDifficulty
import com.runanywhere.startup_hackathon20.model.DetectiveCase
import com.runanywhere.startup_hackathon20.ui.theme.*
import com.runanywhere.startup_hackathon20.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseFilesScreen(
    gameViewModel: GameViewModel,
    onCaseSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val cases by gameViewModel.cases.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Case Files",
                        color = NeonBlue,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(cases) { case ->
                CaseCard(
                    case = case,
                    onClick = {
                        if (case.isUnlocked) {
                            onCaseSelected(case.id)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CaseCard(
    case: DetectiveCase,
    onClick: () -> Unit
) {
    val cardAlpha = if (case.isUnlocked) 1f else 0.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = case.isUnlocked, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (case.isCompleted) {
                SuccessGreen.copy(alpha = 0.1f)
            } else {
                DarkCard
            }
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Lock overlay for locked cases
            if (!case.isUnlocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(DarkBackground.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔒",
                        fontSize = 48.sp
                    )
                }
            }

            // Completed overlay
            if (case.isCompleted) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "✓ Solved",
                        fontSize = 12.sp,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Case #${case.id}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = case.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (case.isUnlocked) TextPrimary else TextTertiary
                        )
                    }

                    DifficultyBadge(difficulty = case.difficulty)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = case.description,
                    fontSize = 14.sp,
                    color = if (case.isUnlocked) TextSecondary else TextTertiary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress bar
                if (case.isUnlocked && !case.isCompleted) {
                    CaseProgressBar(
                        cluesSolved = case.cluesSolved,
                        totalClues = case.cluesRequired
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${case.cluesRequired} Clues",
                        fontSize = 12.sp,
                        color = NeonBlue
                    )

                    Text(
                        text = "+${case.xpReward} XP",
                        fontSize = 12.sp,
                        color = NeonPurple,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (case.badgeReward != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "🏆 Badge: ${case.badgeReward}",
                        fontSize = 12.sp,
                        color = WarningYellow
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: CaseDifficulty) {
    val (text, color) = when (difficulty) {
        CaseDifficulty.ROOKIE -> "Rookie" to SuccessGreen
        CaseDifficulty.DETECTIVE -> "Detective" to NeonBlue
        CaseDifficulty.INSPECTOR -> "Inspector" to NeonPurple
        CaseDifficulty.CHIEF -> "Chief" to ErrorRed
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CaseProgressBar(cluesSolved: Int, totalClues: Int) {
    val progress = cluesSolved.toFloat() / totalClues.toFloat()

    Column {
        Text(
            text = "Progress: $cluesSolved/$totalClues clues",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = NeonBlue,
            trackColor = DarkAccent,
        )
    }
}
