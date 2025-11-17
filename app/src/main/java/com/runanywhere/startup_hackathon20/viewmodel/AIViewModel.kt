package com.runanywhere.startup_hackathon20.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.sdk.models.ModelInfo
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.listAvailableModels
import com.runanywhere.startup_hackathon20.model.AptitudeCategory
import com.runanywhere.startup_hackathon20.model.AptitudeClue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for AI-powered features using RunAnywhere SDK
 */
class AIViewModel : ViewModel() {

    // Model management
    private val _availableModels = MutableStateFlow<List<ModelInfo>>(emptyList())
    val availableModels: StateFlow<List<ModelInfo>> = _availableModels.asStateFlow()

    private val _currentModelId = MutableStateFlow<String?>(null)
    val currentModelId: StateFlow<String?> = _currentModelId.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Float?>(null)
    val downloadProgress: StateFlow<Float?> = _downloadProgress.asStateFlow()

    private val _statusMessage = MutableStateFlow("AI Chief Detective initializing...")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    // AI responses
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String> = _currentResponse.asStateFlow()

    // Chat history with AI Chief
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    init {
        loadAvailableModels()
    }

    data class ChatMessage(
        val text: String,
        val isUser: Boolean
    )

    /**
     * Load available AI models
     */
    private fun loadAvailableModels() {
        viewModelScope.launch {
            try {
                val models = listAvailableModels()
                _availableModels.value = models
                _statusMessage.value = if (models.isEmpty()) {
                    "Waiting for AI models..."
                } else {
                    "Please download and load a model to activate AI Chief"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error loading models: ${e.message}"
            }
        }
    }

    /**
     * Download a model
     */
    fun downloadModel(modelId: String) {
        viewModelScope.launch {
            try {
                _statusMessage.value = "Downloading AI brain..."
                RunAnywhere.downloadModel(modelId).collect { progress ->
                    _downloadProgress.value = progress
                    _statusMessage.value = "Downloading: ${(progress * 100).toInt()}%"
                }
                _downloadProgress.value = null
                _statusMessage.value = "Download complete! Please load the AI Chief."
                loadAvailableModels() // Refresh list
            } catch (e: Exception) {
                _statusMessage.value = "Download failed: ${e.message}"
                _downloadProgress.value = null
            }
        }
    }

    /**
     * Load a model
     */
    fun loadModel(modelId: String) {
        viewModelScope.launch {
            try {
                _statusMessage.value = "Loading AI Chief Detective..."
                val success = RunAnywhere.loadModel(modelId)
                if (success) {
                    _currentModelId.value = modelId
                    _statusMessage.value = "AI Chief Detective is ready!"
                    addSystemMessage("Greetings, Trainee. I'm the Chief Detective AI. I'm here to assist you with clues, hints, and case analysis.")
                } else {
                    _statusMessage.value = "Failed to load AI Chief"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error loading AI: ${e.message}"
            }
        }
    }

    /**
     * Generate a hint for a clue using AI
     */
    fun generateHint(clue: AptitudeClue, onHintGenerated: (String) -> Unit) {
        if (_currentModelId.value == null) {
            onHintGenerated("AI Chief is not available. Please load a model first.")
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _currentResponse.value = ""

            try {
                val prompt = buildHintPrompt(clue)
                var fullResponse = ""

                RunAnywhere.generateStream(prompt).collect { token ->
                    fullResponse += token
                    _currentResponse.value = fullResponse
                }

                onHintGenerated(fullResponse)
            } catch (e: Exception) {
                onHintGenerated("Unable to generate hint: ${e.message}")
            } finally {
                _isGenerating.value = false
            }
        }
    }

    /**
     * Generate an explanation for a clue using AI
     */
    fun generateExplanation(
        clue: AptitudeClue,
        userAnswer: Int,
        onExplanationGenerated: (String) -> Unit
    ) {
        if (_currentModelId.value == null) {
            // Fallback to built-in explanation
            onExplanationGenerated(clue.explanation)
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _currentResponse.value = ""

            try {
                val prompt = buildExplanationPrompt(clue, userAnswer)
                var fullResponse = ""

                RunAnywhere.generateStream(prompt).collect { token ->
                    fullResponse += token
                    _currentResponse.value = fullResponse
                }

                onExplanationGenerated(fullResponse)
            } catch (e: Exception) {
                // Fallback to built-in explanation
                onExplanationGenerated(clue.explanation)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    /**
     * Generate a new aptitude question using AI
     */
    fun generateAptitudeQuestion(
        category: AptitudeCategory,
        difficulty: Int,
        onQuestionGenerated: (AptitudeClue?) -> Unit
    ) {
        if (_currentModelId.value == null) {
            onQuestionGenerated(null)
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _currentResponse.value = ""

            try {
                val prompt = buildQuestionPrompt(category, difficulty)
                var fullResponse = ""

                RunAnywhere.generateStream(prompt).collect { token ->
                    fullResponse += token
                    _currentResponse.value = fullResponse
                }

                // Parse the response into an AptitudeClue
                val clue = parseGeneratedQuestion(fullResponse, category, difficulty)
                onQuestionGenerated(clue)
            } catch (e: Exception) {
                onQuestionGenerated(null)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    /**
     * Chat with AI Chief Detective
     */
    fun chatWithChief(userMessage: String) {
        if (_currentModelId.value == null) {
            _statusMessage.value = "AI Chief is not loaded. Please load a model first."
            return
        }

        // Add user message
        _chatHistory.value += ChatMessage(userMessage, isUser = true)

        viewModelScope.launch {
            _isGenerating.value = true

            try {
                val prompt = buildChatPrompt(userMessage)
                var assistantResponse = ""

                RunAnywhere.generateStream(prompt).collect { token ->
                    assistantResponse += token

                    // Update AI message in real-time
                    val currentMessages = _chatHistory.value.toMutableList()
                    if (currentMessages.lastOrNull()?.isUser == false) {
                        currentMessages[currentMessages.lastIndex] =
                            ChatMessage(assistantResponse, isUser = false)
                    } else {
                        currentMessages.add(ChatMessage(assistantResponse, isUser = false))
                    }
                    _chatHistory.value = currentMessages
                }
            } catch (e: Exception) {
                _chatHistory.value += ChatMessage(
                    "Error communicating: ${e.message}",
                    isUser = false
                )
            } finally {
                _isGenerating.value = false
            }
        }
    }

    /**
     * Refresh models list
     */
    fun refreshModels() {
        loadAvailableModels()
    }

    /**
     * Clear chat history
     */
    fun clearChat() {
        _chatHistory.value = emptyList()
        if (_currentModelId.value != null) {
            addSystemMessage("Chat cleared. How can I assist you with your case, Detective?")
        }
    }

    // Private helper methods

    private fun addSystemMessage(message: String) {
        _chatHistory.value += ChatMessage(message, isUser = false)
    }

    private fun buildHintPrompt(clue: AptitudeClue): String {
        return """You are a Chief Detective giving a subtle hint to a trainee detective. 
For the following clue, provide a helpful but not too obvious hint in detective-style language. Keep it brief (2-3 sentences).

Category: ${clue.category.name.replace("_", " ")}
Question: ${clue.question}

Hint (as Chief Detective):"""
    }

    private fun buildExplanationPrompt(clue: AptitudeClue, userAnswer: Int): String {
        val isCorrect = userAnswer == clue.correctAnswerIndex
        return """You are a Chief Detective explaining a clue to a trainee detective. 
Explain the answer in detective-style language, as if analyzing case evidence. Keep it concise but thorough.

Question: ${clue.question}
Options: ${clue.options.joinToString(", ")}
Correct Answer: ${clue.options[clue.correctAnswerIndex]}
Trainee Selected: ${clue.options.getOrNull(userAnswer) ?: "None"}
Status: ${if (isCorrect) "Correct" else "Incorrect"}

Your analysis:"""
    }

    private fun buildQuestionPrompt(category: AptitudeCategory, difficulty: Int): String {
        return """Generate an aptitude question for detective training.
Category: ${category.name.replace("_", " ")}
Difficulty: $difficulty/5
Format: Present it as a mystery clue. Include the question and 4 options (mark correct with *).

Question:"""
    }

    private fun buildChatPrompt(userMessage: String): String {
        return """You are the Chief Detective, an experienced detective AI helping a trainee solve cases. 
Respond in character - be professional, helpful, and occasionally use detective/mystery terminology.
Keep responses concise and encouraging.

Trainee: $userMessage

Chief Detective:"""
    }

    private fun parseGeneratedQuestion(
        response: String,
        category: AptitudeCategory,
        difficulty: Int
    ): AptitudeClue? {
        // Simple parser - in production, use better parsing
        try {
            val lines = response.lines().filter { it.isNotBlank() }
            if (lines.size < 5) return null

            val question = lines[0]
            val options = lines.drop(1).take(4).map { it.replace(Regex("^[*\\-\\d.]+\\s*"), "") }
            val correctIndex =
                lines.drop(1).take(4).indexOfFirst { it.contains("*") }.coerceAtLeast(0)

            return AptitudeClue(
                id = "ai_generated_${System.currentTimeMillis()}",
                question = question,
                options = options,
                correctAnswerIndex = correctIndex,
                explanation = "This clue was generated by the AI Chief Detective.",
                category = category,
                difficulty = difficulty
            )
        } catch (e: Exception) {
            return null
        }
    }
}
