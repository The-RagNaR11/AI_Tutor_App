package com.ragnar.ai_tutor.screens

import AudioWaveform
import android.Manifest
import android.util.Log
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ragnar.ai_tutor.speechModels.SpeechToText
import com.ragnar.ai_tutor.speechModels.TextToSpeech
import com.ragnar.ai_tutor.item.ChatMessageBubble
import com.ragnar.ai_tutor.chatBot.ChatViewModel
import com.ragnar.ai_tutor.chatBot.ChatViewModelFactory
import com.ragnar.ai_tutor.ui.theme.BackgroundPrimary
import com.ragnar.ai_tutor.ui.theme.BackgroundSecondary
import com.ragnar.ai_tutor.ui.theme.BrandPrimary
import com.ragnar.ai_tutor.ui.theme.ColorError
import com.ragnar.ai_tutor.ui.theme.ColorHint
import com.ragnar.ai_tutor.ui.theme.ColorSuccess
import com.ragnar.ai_tutor.ui.theme.SendButtonColor
import com.ragnar.ai_tutor.ui.theme.TextPrimary
import com.ragnar.ai_tutor.ui.theme.TextSecondary
import com.ragnar.ai_tutor.ui.theme.WaveformActive
import com.ragnar.ai_tutor.ui.theme.WaveformInactive
import com.ragnar.ai_tutor.ui.theme.White

@Composable
fun ChatScreen(
    ttsController: TextToSpeech = viewModel(), // TextToSpeech core Util
    sttController: SpeechToText = viewModel(), // SpeechToText core Util
    chatBotController : ChatViewModel = viewModel (factory = ChatViewModelFactory("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")) //API key
) {

    val context = LocalContext.current

    val ttsState by ttsController.state.collectAsState() // TTS states
    val sttState by sttController.state.collectAsState() // STT states

    // Collects chat state from ChatViewModel
    val chatMessages by chatBotController.messages.collectAsState()
    val isChatLoading by chatBotController.isLoading.collectAsState()

    // Auto-scroll state for chat messages
    val chatListState = rememberLazyListState()

    var messageInput by remember { mutableStateOf("") }
//    messageInput = sttState.resultText

    var audioSliderPosition by remember { mutableFloatStateOf(0.3f) } // slider to record the position of audio playback
    var audioVolume by remember { mutableFloatStateOf(0.5f) } // slider to record the position of volume

    // FIXED: Get the latest AI message from chat history
    val aiMessageOutput = chatMessages.lastOrNull { it.sender == "ai" }?.content
        ?: "Hi! I'm ready to help you learn. What would you like to work on today?"

    // FIXED: Update messageInput from STT when speech recognition completes
    LaunchedEffect(sttState.resultText) {
        if (sttState.resultText.isNotBlank() && !sttState.isSpeaking) {
            messageInput = sttState.resultText
        }
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        sttController.handlePermissionResult(
            SpeechToText.RECORD_AUDIO_PERMISSION_REQUEST,
            if (isGranted) intArrayOf(android.content.pm.PackageManager.PERMISSION_GRANTED)
            else intArrayOf(android.content.pm.PackageManager.PERMISSION_DENIED)
        )
    }

    // Launch Activity
    LaunchedEffect(Unit) {
        sttController.initialize(context)
        ttsController.initialize(context)
        if (!sttState.hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Disposal Activity
    DisposableEffect(Unit) {
        onDispose {
            sttController.destroy()
            ttsController.cleanup()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(White)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AI Tutor Sarah",
                color = TextPrimary,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Your personal mathematics learning companion",
                color = TextSecondary,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = if (isChatLoading) "• Thinking..." else "• Available to help",
                color = if (isChatLoading) ColorHint else ColorSuccess,
                modifier = Modifier.padding(8.dp)
            )

            /*
            Card to display Lip sync model
             */
            Card (
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp) ,
                modifier = Modifier
                    .width(150.dp)
                    .height(220.dp)
            ) {
                AndroidView(
                    factory = {
                        WebView(context).apply {
                            ttsController.setupWebView(this)
                        }
                    }
                )
            }

            /*
            Card to display current result from AI
             */
            Card(
                border = BorderStroke(1.dp, ColorHint),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp) ,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(0.dp, 15.dp)

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundPrimary)
                        .padding(0.dp, 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Icon(
                                Icons.Outlined.SmartToy,
                                contentDescription = "AI Icon",
                                tint = BrandPrimary
                            )
                            Spacer(modifier = Modifier.padding(4.dp)) // distance between logo and text

                            Text(
                                text = "Sarah is saying: ",
                                color = BrandPrimary
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        // Play Pause Button
                        IconButton(
                            onClick = {
                                if (ttsState.isInitialized) {
                                    if (!ttsState.isSpeaking) {
                                        ttsController.speak(aiMessageOutput)
                                    }else {
                                        ttsController.stop()
                                    }
                                } else {
                                    ttsController.initialize(context)
                                }
                            },
                            modifier = Modifier.size(25.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = BrandPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                if (ttsState.isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Play Audio",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                    // Output message from the chat bot
                    Text(
                        text = aiMessageOutput,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(
                            top = 0.dp,
                            bottom = 10.dp,
                            start = 10.dp,
                            end = 10.dp
                        )
                    )
                }

            }

            /*
            Card to display previous messages
             */
            Card(
                border = BorderStroke(1.dp, ColorHint),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier.align(Alignment.Start)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                ) {
                    // Header row
                    Row(
                        modifier = Modifier
                            .background(BackgroundSecondary)
                            .fillMaxWidth()
                            .padding(5.dp, 10.dp)
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "Previous Conversation Icon",
                            tint = TextPrimary
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = "Previous Conversation: ",
                            color = TextPrimary
                        )
                    }

                    // FIXED: Chat list now properly displays ChatMessage objects with auto-scroll
                    LazyColumn(
                        state = chatListState,
                        modifier = Modifier
                            .height(300.dp) // fixed height
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (chatMessages.isEmpty()) {
                            item {
                                Text(
                                    text = "No conversation yet...",
                                    color = TextSecondary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            items(chatMessages) { message ->
                                ChatMessageBubble(
                                    message = message,
                                    onRetry = if (message.canRetry) {
                                        { chatBotController.retryLastMessage() }
                                    } else null,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            /*
                Send Message Card
             */
            Card (
                border = BorderStroke(1.dp, ColorHint),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(0.dp, 15.dp)
            ){
                Column(
                    modifier = Modifier
                        .background(BackgroundPrimary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // standard TextField
                        TextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Ask Sarah a question...") },
                            shape = RoundedCornerShape(20.dp),
                            singleLine = false,
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = BackgroundSecondary,
                                focusedContainerColor = BackgroundSecondary,
                                cursorColor = ColorHint,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                unfocusedPlaceholderColor = TextSecondary,
                                focusedPlaceholderColor = TextSecondary
                            )
                        )

                        // Button to send message to chat
                        IconButton(
                            onClick = {
                                if (messageInput.isNotBlank() && !isChatLoading) {
                                    // Send message to chatbot
                                    chatBotController.sendMessage(messageInput)
                                    // Clear input
                                    messageInput = ""
//                                    sttState.resultText = ""
                                }
                            },
                            enabled = messageInput.isNotBlank() && !isChatLoading,
                            modifier = Modifier.size(48.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = SendButtonColor,
                                contentColor = Color.White,
                                disabledContainerColor = ColorHint,
                                disabledContentColor = Color.White
                            )
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Message"
                            )
                        }

                        // IconButton for the mic button
                        IconButton(
                            onClick = {
                                Log.i("ChatScreen", "Mic Button Clicked")
                                if (!sttState.isSpeaking) {
                                    if (sttState.isInitialized && sttState.hasPermission) {
                                        sttController.startListening()
                                    } else if (!sttState.hasPermission){
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                } else {
                                    sttController.stopListening()
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .border(1.dp, SendButtonColor, CircleShape)
                        ) {
                            Icon(
                                if (sttState.isSpeaking) Icons.Outlined.Stop else Icons.Outlined.Mic,
                                contentDescription = "Record Audio",
                                tint = SendButtonColor
                            )
                        }
                    }

                    // "Tap to send" text below the Row
                    Text(
                        text = if (isChatLoading) "Sending..." else "Tap to send",
                        style = MaterialTheme.typography.labelMedium,
                        color = ColorHint,
                        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                    )
                }
            }

            /*
            Audio Output Card
             */
            Card(
                border = BorderStroke(1.dp, ColorHint),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundPrimary)
                        .padding(24.dp)
                ) {
                    // Header Texts
                    Text(
                        text = "Solving Basic Equations",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "AI Audio Explanation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    AudioWaveform(progress = audioSliderPosition)

                    Spacer(modifier = Modifier.height(32.dp))
                    // Progress Slide bar
                    Column (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Slider(
                            value = audioSliderPosition,
                            onValueChange = {
                                audioSliderPosition = it
                            },
                            colors = SliderDefaults.colors(
                                activeTrackColor = WaveformActive,
                                inactiveTrackColor = WaveformInactive,
                                activeTickColor = Color.Transparent,
                                inactiveTickColor = Color.Transparent
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "0:00", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text(text = "3:00", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    /*
                        Audio Control Buttons
                    */
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Button
                        IconButton(
                            onClick = {
                                Log.i("ChatScreen", "Previous Button Clicked")
                            }
                        ) {
                            Icon(
                                Icons.Outlined.SkipPrevious,
                                contentDescription = "Skip Previous",
                                tint = TextSecondary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        // Play Pause Button
                        IconButton(
                            onClick = {
                                if (ttsState.isInitialized) {
                                    if (!ttsState.isSpeaking) {
                                        ttsController.speak(aiMessageOutput)
                                    }else {
                                        ttsController.stop()
                                    }
                                } else {
                                    ttsController.initialize(context)
                                }
                            },
                            modifier = Modifier.size(64.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = BrandPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                if (ttsState.isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Play Audio",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        // Next Button
                        IconButton(
                            onClick = {
                                Log.i("ChatScreen", "Next Button Clicked")
                            },

                            ) {
                            Icon(
                                Icons.Outlined.SkipNext,
                                contentDescription = "Skip Next",
                                tint = TextSecondary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        /*
                            Volume Controls
                         */
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Volume", tint = TextSecondary)
                            Slider(
                                value = audioVolume,
                                onValueChange = { audioVolume = it },
                                modifier = Modifier.width(80.dp),
                                colors = SliderDefaults.colors(
                                    activeTrackColor = WaveformActive,
                                    inactiveTrackColor = WaveformInactive,
                                    thumbColor = Color.Transparent
                                )
                            )
                        }
                    }

                }
            }
        }
    }
}