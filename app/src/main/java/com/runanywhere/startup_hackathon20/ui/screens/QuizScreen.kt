package com.runanywhere.startup_hackathon20.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runanywhere.startup_hackathon20.model.AptitudeClue
import com.runanywhere.startup_hackathon20.ui.theme.*
import com.runanywhere.startup_hackathon20.viewmodel.AIViewModel
import com.runanywhere.startup_hackathon20.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    caseId: Int,
    gameViewModel: GameViewModel,
    aiViewModel: AIViewModel,
    onCaseCompleted: () -> Unit,
    onBack: () -> Unit
) {
    val case = gameViewModel.getCaseById(caseId)
    val currentClue by gameViewModel.currentClue.collectAsState()
    val caseProgress by gameViewModel.currentCaseProgress.collectAsState()

    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }
    var hintText by remember { mutableStateOf("") }
    var showExplanation by remember { mutableStateOf(false) }
    var explanationText by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var hintsUsed by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    // Initialize case if not already started
    LaunchedEffect(caseId) {
        if (caseProgress == null || caseProgress?.caseId != caseId) {
            gameViewModel.startCase(caseId)
        }
    }

    // Check if case is completed
    LaunchedEffect(caseProgress?.currentClueIndex) {
        if (gameViewModel.isCaseCompleted()) {
            delay(500)
            gameViewModel.completeCase(caseId)
            onCaseCompleted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = case?.title ?: "Loading...",
                            color = NeonBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Clue ${(caseProgress?.currentClueIndex ?: 0) + 1}/${case?.cluesRequired ?: 5}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (currentClue == null) {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonBlue)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Progress bar
                    ClueProgressBar(
                        currentClue = (caseProgress?.currentClueIndex ?: 0) + 1,
                        totalClues = case?.cluesRequired ?: 5
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Question card
                    QuestionCard(clue = currentClue!!)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Answer options
                    currentClue!!.options.forEachIndexed { index, option ->
                        AnswerOption(
                            text = option,
                            index = index,
                            isSelected = selectedAnswer == index,
                            isCorrect = if (showResult) index == currentClue!!.correctAnswerIndex else null,
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

                    // Hint section
                    if (!showResult) {
                        TextButton(
                            onClick = {
                                showHint = true
                                hintsUsed++
                                aiViewModel.generateHint(currentClue!!) { hint ->
                                    hintText = hint
                                }
                            },
                            enabled = !showHint
                        ) {
                            Text(
                                text = if (showHint) "💡 Hint shown" else "💡 Need a Hint?",
                                color = if (showHint) TextTertiary else NeonPurple
                            )
                        }

                        if (showHint && hintText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = NeonPurple.copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = hintText,
                                    modifier = Modifier.padding(16.dp),
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Result explanation
                    AnimatedVisibility(
                        visible = showExplanation,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
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
                                    text = if (isCorrect) "✓ Correct!" else "✗ Incorrect",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) SuccessGreen else ErrorRed
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = explanationText,
                                    fontSize = 14.sp,
                                    color = TextSecondary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit / Continue button
                    Button(
                        onClick = {
                            if (!showResult) {
                                // Submit answer
                                selectedAnswer?.let { answer ->
                                    val timeSpent = System.currentTimeMillis() - startTime
                                    isCorrect =
                                        gameViewModel.submitAnswer(answer, timeSpent, hintsUsed)
                                    showResult = true
                                    showExplanation = true

                                    // Generate AI explanation
                                    aiViewModel.generateExplanation(
                                        currentClue!!,
                                        answer
                                    ) { explanation ->
                                        explanationText = explanation
                                    }
                                }
                            } else {
                                // Continue to next clue
                                if (isCorrect) {
                                    gameViewModel.generateNextClue(caseId)
                                    // Reset state
                                    selectedAnswer = null
                                    showResult = false
                                    showHint = false
                                    hintText = ""
                                    showExplanation = false
                                    explanationText = ""
                                    startTime = System.currentTimeMillis()
                                    hintsUsed = 0
                                } else {
                                    // Retry same clue
                                    selectedAnswer = null
                                    showResult = false
                                    showExplanation = false
                                    startTime = System.currentTimeMillis()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = selectedAnswer != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showResult && isCorrect) SuccessGreen else NeonBlue,
                            disabledContainerColor = DarkAccent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (!showResult) {
                                "Submit Answer"
                            } else if (isCorrect) {
                                "Continue"
                            } else {
                                "Try Again"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClueProgressBar(currentClue: Int, totalClues: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(totalClues) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (index < currentClue) NeonBlue else DarkAccent
                    )
            )
        }
    }
}

@Composable
private fun QuestionCard(clue: AptitudeClue) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkCard
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = clue.category.name.replace("_", " "),
                fontSize = 12.sp,
                color = NeonPurple,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = clue.question,
                fontSize = 18.sp,
                color = TextPrimary,
                lineHeight = 26.sp
            )
        }
    }
}

@Composable
private fun AnswerOption(
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
            // Option letter
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

            // Check/Cross icon
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
