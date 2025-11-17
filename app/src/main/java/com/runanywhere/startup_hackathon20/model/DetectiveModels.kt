package com.runanywhere.startup_hackathon20.model

import androidx.compose.runtime.Stable

/**
 * Represents a detective case (level)
 */
@Stable
data class DetectiveCase(
    val id: Int,
    val title: String,
    val description: String,
    val difficulty: CaseDifficulty,
    val isUnlocked: Boolean,
    val isCompleted: Boolean,
    val cluesRequired: Int = 5,
    val cluesSolved: Int = 0,
    val xpReward: Int,
    val badgeReward: String?
)

/**
 * Difficulty levels for cases
 */
enum class CaseDifficulty {
    ROOKIE,
    DETECTIVE,
    INSPECTOR,
    CHIEF
}

/**
 * Represents a single aptitude question (clue)
 */
@Stable
data class AptitudeClue(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val category: AptitudeCategory,
    val difficulty: Int // 1-5
)

/**
 * Aptitude question categories
 */
enum class AptitudeCategory {
    LOGICAL_REASONING,
    NUMERICAL_ABILITY,
    VERBAL_REASONING,
    PATTERN_RECOGNITION,
    DATA_INTERPRETATION,
    ANALYTICAL_THINKING
}

/**
 * User's detective profile
 */
@Stable
data class DetectiveProfile(
    val name: String = "Detective",
    val rank: String = "Trainee",
    val totalXP: Int = 0,
    val level: Int = 1,
    val casesCompleted: Int = 0,
    val badgesEarned: List<String> = emptyList(),
    val streak: Int = 0
)

/**
 * Badge/Achievement
 */
@Stable
data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val earnedDate: Long?
)

/**
 * Represents the user's answer to a clue
 */
@Stable
data class ClueAnswer(
    val clueId: String,
    val selectedAnswerIndex: Int,
    val isCorrect: Boolean,
    val timeSpent: Long, // in milliseconds
    val hintsUsed: Int = 0
)

/**
 * Case progress tracking
 */
@Stable
data class CaseProgress(
    val caseId: Int,
    val cluesAnswered: List<ClueAnswer> = emptyList(),
    val currentClueIndex: Int = 0,
    val startTime: Long = System.currentTimeMillis(),
    val hintsUsed: Int = 0
)

/**
 * Daily mystery data
 */
@Stable
data class DailyMystery(
    val date: String,
    val clue: AptitudeClue,
    val isCompleted: Boolean = false,
    val xpReward: Int = 50
)
