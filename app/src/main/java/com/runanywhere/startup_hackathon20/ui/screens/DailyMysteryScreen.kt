package com.runanywhere.startup_hackathon20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runanywhere.startup_hackathon20.ui.theme.*
import com.runanywhere.startup_hackathon20.viewmodel.AIViewModel
import com.runanywhere.startup_hackathon20.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMysteryScreen(
    gameViewModel: GameViewModel,
    aiViewModel: AIViewModel,
    onBack: () -> Unit
) {
    val dailyMystery by gameViewModel.dailyMystery.collectAsState()
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daily Mystery",
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
        if (dailyMystery == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonBlue)
            }
        } else if (dailyMystery!!.isCompleted) {
            // Already completed
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "✓",
                        fontSize = 80.sp,
                        color = SuccessGreen
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Daily Mystery Complete!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Come back tomorrow for a new challenge",
                        fontSize = 16.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back to HQ", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Show the daily challenge
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
            ) {
                // Header card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = NeonPurple.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "🎯 Today's Challenge",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPurple
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Solve the daily mystery and earn bonus XP!",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Reward: +${dailyMystery!!.xpReward} XP",
                                fontSize = 14.sp,
                                color = NeonBlue,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = dailyMystery!!.date,
                                fontSize = 12.sp,
                                color = TextTertiary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Question
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkCard
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = dailyMystery!!.clue.category.name.replace("_", " "),
                            fontSize = 12.sp,
                            color = NeonPurple,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = dailyMystery!!.clue.question,
                            fontSize = 18.sp,
                            color = TextPrimary,
                            lineHeight = 26.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Answer options
                dailyMystery!!.clue.options.forEachIndexed { index, option ->
                    DailyMysteryOption(
                        text = option,
                        index = index,
                        isSelected = selectedAnswer == index,
                        isCorrect = if (showResult) index == dailyMystery!!.clue.correctAnswerIndex else null,
                        isWrong = if (showResult && selectedAnswer == index) !isCorrect else false,
                        enabled = !showResult,
                        onClick = {
                            if (!showResult) {
                                selectedAnswer = index
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                // Result explanation
                if (showResult) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) {
                                SuccessGreen.copy(alpha = 0.1f)
                            } else {
                                ErrorRed.copy(alpha = 0.1f)
                            }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isCorrect) "✓ Correct! +${dailyMystery!!.xpReward} XP" else "✗ Incorrect",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) SuccessGreen else ErrorRed
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = dailyMystery!!.clue.explanation,
                                fontSize = 14.sp,
                                color = TextSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Submit button
                Button(
                    onClick = {
                        if (!showResult) {
                            selectedAnswer?.let { answer ->
                                isCorrect = answer == dailyMystery!!.clue.correctAnswerIndex
                                showResult = true
                                gameViewModel.completeDailyMystery(isCorrect)
                            }
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedAnswer != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showResult) SuccessGreen else NeonBlue,
                        disabledContainerColor = DarkAccent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (showResult) "Back to HQ" else "Submit Answer",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyMysteryOption(
    text: String,
    index: Int,
    isSelected: Boolean,
    isCorrect: Boolean?,
    isWrong: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrect == true -> SuccessGreen.copy(alpha = 0.2f)
        isWrong -> ErrorRed.copy(alpha = 0.2f)
        isSelected -> NeonBlue.copy(alpha = 0.2f)
        else -> DarkCard
    }

    val borderColor = when {
        isCorrect == true -> SuccessGreen
        isWrong -> ErrorRed
        isSelected -> NeonBlue
        else -> DarkAccent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(borderColor.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ('A' + index).toString(),
                    color = borderColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                color = TextPrimary,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            if (isCorrect == true || isWrong) {
                Text(
                    text = if (isCorrect == true) "✓" else "✗",
                    fontSize = 20.sp,
                    color = if (isCorrect == true) SuccessGreen else ErrorRed
                )
            }
        }
    }
}
