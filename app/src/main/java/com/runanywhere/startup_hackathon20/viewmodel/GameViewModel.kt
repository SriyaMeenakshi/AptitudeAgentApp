package com.runanywhere.startup_hackathon20.viewmodel

import androidx.lifecycle.ViewModel
import com.runanywhere.startup_hackathon20.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

/**
 * Main game state management ViewModel
 */
class GameViewModel : ViewModel() {

    // User profile
    private val _profile = MutableStateFlow(DetectiveProfile())
    val profile: StateFlow<DetectiveProfile> = _profile.asStateFlow()

    // Available cases
    private val _cases = MutableStateFlow(generateInitialCases())
    val cases: StateFlow<List<DetectiveCase>> = _cases.asStateFlow()

    // Current case progress
    private val _currentCaseProgress = MutableStateFlow<CaseProgress?>(null)
    val currentCaseProgress: StateFlow<CaseProgress?> = _currentCaseProgress.asStateFlow()

    // Daily mystery
    private val _dailyMystery = MutableStateFlow<DailyMystery?>(null)
    val dailyMystery: StateFlow<DailyMystery?> = _dailyMystery.asStateFlow()

    // Current clue being solved
    private val _currentClue = MutableStateFlow<AptitudeClue?>(null)
    val currentClue: StateFlow<AptitudeClue?> = _currentClue.asStateFlow()

    init {
        generateDailyMystery()
    }

    /**
     * Start a new case
     */
    fun startCase(caseId: Int) {
        val case = _cases.value.find { it.id == caseId } ?: return
        if (!case.isUnlocked) return

        _currentCaseProgress.value = CaseProgress(
            caseId = caseId,
            cluesAnswered = emptyList(),
            currentClueIndex = 0,
            startTime = System.currentTimeMillis()
        )

        // Generate first clue
        generateNextClue(caseId)
    }

    /**
     * Generate the next clue for the current case
     */
    fun generateNextClue(caseId: Int) {
        val progress = _currentCaseProgress.value ?: return
        val case = _cases.value.find { it.id == caseId } ?: return

        if (progress.currentClueIndex >= case.cluesRequired) {
            // Case completed
            return
        }

        // Generate appropriate difficulty clue based on case
        val difficulty = when (case.difficulty) {
            CaseDifficulty.ROOKIE -> 1
            CaseDifficulty.DETECTIVE -> 2
            CaseDifficulty.INSPECTOR -> 3
            CaseDifficulty.CHIEF -> 4
        }

        _currentClue.value = generateClue(
            id = "${caseId}_${progress.currentClueIndex}",
            difficulty = difficulty,
            caseId = caseId
        )
    }

    /**
     * Submit an answer to the current clue
     */
    fun submitAnswer(selectedAnswerIndex: Int, timeSpent: Long, hintsUsed: Int = 0): Boolean {
        val clue = _currentClue.value ?: return false
        val progress = _currentCaseProgress.value ?: return false

        val isCorrect = selectedAnswerIndex == clue.correctAnswerIndex

        val answer = ClueAnswer(
            clueId = clue.id,
            selectedAnswerIndex = selectedAnswerIndex,
            isCorrect = isCorrect,
            timeSpent = timeSpent,
            hintsUsed = hintsUsed
        )

        // Update progress
        val updatedAnswers = progress.cluesAnswered + answer
        val updatedProgress = progress.copy(
            cluesAnswered = updatedAnswers,
            currentClueIndex = if (isCorrect) progress.currentClueIndex + 1 else progress.currentClueIndex,
            hintsUsed = progress.hintsUsed + hintsUsed
        )

        _currentCaseProgress.value = updatedProgress

        return isCorrect
    }

    /**
     * Check if current case is completed
     */
    fun isCaseCompleted(): Boolean {
        val progress = _currentCaseProgress.value ?: return false
        val case = _cases.value.find { it.id == progress.caseId } ?: return false
        return progress.currentClueIndex >= case.cluesRequired
    }

    /**
     * Complete the current case and award XP/badges
     */
    fun completeCase(caseId: Int) {
        val case = _cases.value.find { it.id == caseId } ?: return
        val progress = _currentCaseProgress.value ?: return

        // Calculate XP with bonuses
        var xpEarned = case.xpReward

        // Bonus for not using hints
        if (progress.hintsUsed == 0) {
            xpEarned += 50
        }

        // Update profile
        val newXP = _profile.value.totalXP + xpEarned
        val newLevel = calculateLevel(newXP)
        val newRank = calculateRank(newLevel)

        _profile.value = _profile.value.copy(
            totalXP = newXP,
            level = newLevel,
            rank = newRank,
            casesCompleted = _profile.value.casesCompleted + 1,
            badgesEarned = if (case.badgeReward != null) {
                _profile.value.badgesEarned + case.badgeReward
            } else {
                _profile.value.badgesEarned
            }
        )

        // Mark case as completed and unlock next
        val updatedCases = _cases.value.map { c ->
            when {
                c.id == caseId -> c.copy(isCompleted = true, cluesSolved = case.cluesRequired)
                c.id == caseId + 1 -> c.copy(isUnlocked = true)
                else -> c
            }
        }
        _cases.value = updatedCases

        // Clear current progress
        _currentCaseProgress.value = null
        _currentClue.value = null
    }

    /**
     * Get case by ID
     */
    fun getCaseById(caseId: Int): DetectiveCase? {
        return _cases.value.find { it.id == caseId }
    }

    /**
     * Complete daily mystery
     */
    fun completeDailyMystery(isCorrect: Boolean) {
        if (isCorrect) {
            _dailyMystery.value = _dailyMystery.value?.copy(isCompleted = true)
            _profile.value = _profile.value.copy(
                totalXP = _profile.value.totalXP + 50,
                streak = _profile.value.streak + 1
            )
        }
    }

    // Helper functions

    private fun calculateLevel(xp: Int): Int = (xp / 100) + 1

    private fun calculateRank(level: Int): String = when {
        level < 5 -> "Trainee Detective"
        level < 10 -> "Junior Detective"
        level < 20 -> "Detective"
        level < 30 -> "Senior Detective"
        level < 40 -> "Inspector"
        else -> "Chief Inspector"
    }

    private fun generateDailyMystery() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        _dailyMystery.value = DailyMystery(
            date = today,
            clue = generateClue("daily_$today", 2, 0),
            isCompleted = false,
            xpReward = 50
        )
    }

    private fun generateInitialCases(): List<DetectiveCase> = listOf(
        DetectiveCase(
            id = 1,
            title = "The Missing Logic",
            description = "A mysterious sequence has appeared at the crime scene. Solve the pattern to find the culprit.",
            difficulty = CaseDifficulty.ROOKIE,
            isUnlocked = true,
            isCompleted = false,
            xpReward = 100,
            badgeReward = "First Case"
        ),
        DetectiveCase(
            id = 2,
            title = "Numbers Don't Lie",
            description = "Financial records hold the key. Use your numerical skills to uncover the truth.",
            difficulty = CaseDifficulty.ROOKIE,
            isUnlocked = false,
            isCompleted = false,
            xpReward = 150,
            badgeReward = null
        ),
        DetectiveCase(
            id = 3,
            title = "The Verbal Clue",
            description = "Witness statements contradict. Apply verbal reasoning to find consistency.",
            difficulty = CaseDifficulty.DETECTIVE,
            isUnlocked = false,
            isCompleted = false,
            xpReward = 200,
            badgeReward = null
        ),
        DetectiveCase(
            id = 4,
            title = "Data Encryption",
            description = "Encrypted data needs interpretation. Decode the hidden message.",
            difficulty = CaseDifficulty.DETECTIVE,
            isUnlocked = false,
            isCompleted = false,
            xpReward = 250,
            badgeReward = "Data Master"
        ),
        DetectiveCase(
            id = 5,
            title = "The Complex Web",
            description = "Multiple suspects, multiple motives. Use analytical thinking to connect the dots.",
            difficulty = CaseDifficulty.INSPECTOR,
            isUnlocked = false,
            isCompleted = false,
            xpReward = 300,
            badgeReward = null
        ),
        DetectiveCase(
            id = 6,
            title = "Pattern in Chaos",
            description = "Seemingly random events hide a pattern. Find the connection.",
            difficulty = CaseDifficulty.INSPECTOR,
            isUnlocked = false,
            isCompleted = false,
            xpReward = 350,
            badgeReward = null
        ),
        DetectiveCase(
            id = 7,
            title = "The Grand Conspiracy",
            description = "The most complex case yet. Only the sharpest minds can solve this.",
            difficulty = CaseDifficulty.CHIEF,
            isUnlocked = false,
            isCompleted = false,
            xpReward = 500,
            badgeReward = "Master Detective"
        )
    )

    private fun generateClue(id: String, difficulty: Int, caseId: Int): AptitudeClue {
        // This is a sample clue generator. In production, this would pull from a database
        // or use AI to generate contextual clues

        val categories = AptitudeCategory.values()
        val category = categories[caseId % categories.size]

        return when (category) {
            AptitudeCategory.LOGICAL_REASONING -> generateLogicalClue(id, difficulty)
            AptitudeCategory.NUMERICAL_ABILITY -> generateNumericalClue(id, difficulty)
            AptitudeCategory.VERBAL_REASONING -> generateVerbalClue(id, difficulty)
            AptitudeCategory.PATTERN_RECOGNITION -> generatePatternClue(id, difficulty)
            AptitudeCategory.DATA_INTERPRETATION -> generateDataClue(id, difficulty)
            AptitudeCategory.ANALYTICAL_THINKING -> generateAnalyticalClue(id, difficulty)
        }
    }

    private fun generateLogicalClue(id: String, difficulty: Int) = AptitudeClue(
        id = id,
        question = "If all detectives are observant and some observant people are writers, which statement must be true?",
        options = listOf(
            "All writers are detectives",
            "Some detectives are writers",
            "Some writers are observant",
            "All observant people are detectives"
        ),
        correctAnswerIndex = 2,
        explanation = "Since some observant people are writers, and we know observant people exist (detectives are observant), the statement 'Some writers are observant' must be true.",
        category = AptitudeCategory.LOGICAL_REASONING,
        difficulty = difficulty
    )

    private fun generateNumericalClue(id: String, difficulty: Int) = AptitudeClue(
        id = id,
        question = "A suspect claims they traveled 240 km in 3 hours. If they maintained constant speed, what was their speed in km/h?",
        options = listOf("60 km/h", "70 km/h", "80 km/h", "90 km/h"),
        correctAnswerIndex = 2,
        explanation = "Speed = Distance / Time = 240 km / 3 hours = 80 km/h. This matches with traffic camera data.",
        category = AptitudeCategory.NUMERICAL_ABILITY,
        difficulty = difficulty
    )

    private fun generateVerbalClue(id: String, difficulty: Int) = AptitudeClue(
        id = id,
        question = "INVESTIGATE : CLUES :: DIAGNOSE : ?",
        options = listOf("Disease", "Doctor", "Symptoms", "Hospital"),
        correctAnswerIndex = 2,
        explanation = "Just as investigating requires clues, diagnosing requires symptoms. The relationship is between an action and what it uses.",
        category = AptitudeCategory.VERBAL_REASONING,
        difficulty = difficulty
    )

    private fun generatePatternClue(id: String, difficulty: Int) = AptitudeClue(
        id = id,
        question = "What comes next in the sequence: 2, 6, 12, 20, 30, ?",
        options = listOf("38", "40", "42", "44"),
        correctAnswerIndex = 2,
        explanation = "The pattern is n(n+1): 1×2=2, 2×3=6, 3×4=12, 4×5=20, 5×6=30, 6×7=42",
        category = AptitudeCategory.PATTERN_RECOGNITION,
        difficulty = difficulty
    )

    private fun generateDataClue(id: String, difficulty: Int) = AptitudeClue(
        id = id,
        question = "Evidence log shows: Monday-3 items, Tuesday-7 items, Wednesday-5 items. What's the average items per day?",
        options = listOf("4", "5", "6", "7"),
        correctAnswerIndex = 1,
        explanation = "Average = (3 + 7 + 5) / 3 = 15 / 3 = 5 items per day",
        category = AptitudeCategory.DATA_INTERPRETATION,
        difficulty = difficulty
    )

    private fun generateAnalyticalClue(id: String, difficulty: Int) = AptitudeClue(
        id = id,
        question = "Three suspects: A says B is lying, B says C is lying, C says both A and B are lying. Who tells the truth?",
        options = listOf("A only", "B only", "C only", "None of them"),
        correctAnswerIndex = 1,
        explanation = "If B tells the truth, then C is lying. This means A and B are not both lying (C's claim is false), which is consistent. B is the truth-teller.",
        category = AptitudeCategory.ANALYTICAL_THINKING,
        difficulty = difficulty
    )
}
