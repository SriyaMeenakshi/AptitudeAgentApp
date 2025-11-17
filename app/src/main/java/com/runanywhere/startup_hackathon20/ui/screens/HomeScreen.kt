package com.runanywhere.startup_hackathon20.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runanywhere.startup_hackathon20.ui.theme.*
import com.runanywhere.startup_hackathon20.viewmodel.GameViewModel

@Composable
fun HomeScreen(
    gameViewModel: GameViewModel,
    onNavigateToCaseFiles: () -> Unit,
    onNavigateToDailyMystery: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAIChief: () -> Unit
) {
    val profile by gameViewModel.profile.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Animated background effects
        AnimatedBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column {
                Text(
                    text = "Detective HQ",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Welcome back, ${profile.rank}",
                    fontSize = 16.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // XP Bar
                XPProgressBar(
                    currentXP = profile.totalXP,
                    level = profile.level
                )
            }

            // Main Menu Options
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MenuCard(
                    title = "Case Files",
                    subtitle = "Choose your next case",
                    icon = "📂",
                    gradient = Brush.horizontalGradient(
                        colors = listOf(NeonBlue, NeonPurple)
                    ),
                    onClick = onNavigateToCaseFiles
                )

                Spacer(modifier = Modifier.height(16.dp))

                MenuCard(
                    title = "Daily Mystery",
                    subtitle = "Solve today's challenge",
                    icon = "🎯",
                    gradient = Brush.horizontalGradient(
                        colors = listOf(NeonPurple, NeonBlue)
                    ),
                    onClick = onNavigateToDailyMystery
                )

                Spacer(modifier = Modifier.height(16.dp))

                MenuCard(
                    title = "AI Chief Detective",
                    subtitle = "Get hints and guidance",
                    icon = "🤖",
                    gradient = Brush.horizontalGradient(
                        colors = listOf(SuccessGreen, NeonBlue)
                    ),
                    onClick = onNavigateToAIChief
                )
            }

            // Footer Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    label = "Level",
                    value = profile.level.toString()
                )

                StatCard(
                    label = "Cases Solved",
                    value = profile.casesCompleted.toString()
                )

                StatCard(
                    label = "Streak",
                    value = "${profile.streak} 🔥"
                )
            }
        }
    }
}

@Composable
private fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "background")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .background(
                Brush.radialGradient(
                    colors = listOf(NeonBlue.copy(alpha = 0.1f), DarkBackground)
                )
            )
    )
}

@Composable
private fun MenuCard(
    title: String,
    subtitle: String,
    icon: String,
    gradient: Brush,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkCard
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 40.sp,
                    modifier = Modifier.padding(end = 16.dp)
                )

                Column {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun XPProgressBar(currentXP: Int, level: Int) {
    val xpForCurrentLevel = (level - 1) * 100
    val xpForNextLevel = level * 100
    val progress = (currentXP - xpForCurrentLevel).toFloat() / 100f

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Level $level",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Text(
                text = "$currentXP XP",
                fontSize = 14.sp,
                color = NeonBlue
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = NeonBlue,
            trackColor = DarkAccent,
        )
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkCard
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NeonBlue
            )

            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

// Extension function to copy Brush with alpha (simplified)
private fun Brush.copy(alpha: Float): Brush = this
