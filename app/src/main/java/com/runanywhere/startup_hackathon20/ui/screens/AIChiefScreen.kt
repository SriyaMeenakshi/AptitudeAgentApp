package com.runanywhere.startup_hackathon20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runanywhere.startup_hackathon20.ui.theme.*
import com.runanywhere.startup_hackathon20.viewmodel.AIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChiefScreen(
    aiViewModel: AIViewModel,
    onBack: () -> Unit
) {
    val chatHistory by aiViewModel.chatHistory.collectAsState()
    val isGenerating by aiViewModel.isGenerating.collectAsState()
    val statusMessage by aiViewModel.statusMessage.collectAsState()
    val currentModelId by aiViewModel.currentModelId.collectAsState()
    val availableModels by aiViewModel.availableModels.collectAsState()
    val downloadProgress by aiViewModel.downloadProgress.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showModelSelector by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new messages
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Chief Detective",
                            color = NeonBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (currentModelId != null) "Online" else "Offline",
                            color = if (currentModelId != null) SuccessGreen else ErrorRed,
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
                actions = {
                    TextButton(onClick = { showModelSelector = !showModelSelector }) {
                        Text("Setup", color = NeonPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Model selector (collapsible)
            if (showModelSelector) {
                ModelSetupSection(
                    models = availableModels,
                    currentModelId = currentModelId,
                    statusMessage = statusMessage,
                    downloadProgress = downloadProgress,
                    onDownload = { modelId -> aiViewModel.downloadModel(modelId) },
                    onLoad = { modelId -> aiViewModel.loadModel(modelId) },
                    onRefresh = { aiViewModel.refreshModels() }
                )
            }

            // Chat messages
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (chatHistory.isEmpty() && currentModelId != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = DarkCard
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "💡 Tips",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonBlue
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "• Ask for hints on difficult clues\n• Get explanations for aptitude concepts\n• Request case analysis strategies\n• Learn detective techniques",
                                    fontSize = 14.sp,
                                    color = TextSecondary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }

                items(chatHistory) { message ->
                    ChatBubble(
                        message = message.text,
                        isUser = message.isUser
                    )
                }

                if (isGenerating) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // Input field
            if (currentModelId != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Ask the Chief Detective...",
                                color = TextTertiary
                            )
                        },
                        enabled = !isGenerating,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkCard,
                            unfocusedContainerColor = DarkCard,
                            disabledContainerColor = DarkCard,
                            focusedIndicatorColor = NeonBlue,
                            unfocusedIndicatorColor = DarkAccent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                aiViewModel.chatWithChief(inputText)
                                inputText = ""
                            }
                        },
                        enabled = !isGenerating && inputText.isNotBlank(),
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (inputText.isNotBlank()) NeonBlue else DarkAccent)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) DarkBackground else TextTertiary
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚠️ AI Chief is offline. Please load a model from Setup.",
                        color = WarningYellow,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: String, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) {
                    NeonBlue.copy(alpha = 0.2f)
                } else {
                    DarkCard
                }
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isUser) "You" else "Chief Detective",
                    fontSize = 12.sp,
                    color = if (isUser) NeonBlue else NeonPurple,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = DarkCard
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(NeonBlue)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelSetupSection(
    models: List<com.runanywhere.sdk.models.ModelInfo>,
    currentModelId: String?,
    statusMessage: String,
    downloadProgress: Float?,
    onDownload: (String) -> Unit,
    onLoad: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurface,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Model Setup",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue
                )

                TextButton(onClick = onRefresh) {
                    Text("Refresh", color = NeonPurple)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = statusMessage,
                fontSize = 14.sp,
                color = TextSecondary
            )

            downloadProgress?.let { progress ->
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = NeonBlue,
                    trackColor = DarkAccent,
                )
            }

            if (models.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                models.forEach { model ->
                    ModelItem(
                        model = model,
                        isLoaded = model.id == currentModelId,
                        onDownload = { onDownload(model.id) },
                        onLoad = { onLoad(model.id) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ModelItem(
    model: com.runanywhere.sdk.models.ModelInfo,
    isLoaded: Boolean,
    onDownload: () -> Unit,
    onLoad: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoaded) {
                SuccessGreen.copy(alpha = 0.1f)
            } else {
                DarkCard
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = model.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            if (isLoaded) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✓ Active",
                    fontSize = 12.sp,
                    color = SuccessGreen
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDownload,
                        modifier = Modifier.weight(1f),
                        enabled = !model.isDownloaded,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonPurple
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (model.isDownloaded) "Downloaded" else "Download",
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onLoad,
                        modifier = Modifier.weight(1f),
                        enabled = model.isDownloaded,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonBlue
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Load", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
