package com.ragnar.ai_tutor.chatBot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ragnar.ai_tutor.item.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.plus

class ChatViewModel(
    apiKey: String
) : ViewModel() {

    private val aiChatUtils = AIChatUtils(apiKey)

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ADD THIS: Store the last user message for retry functionality
    private var lastUserMessage: String = ""

    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        // ADD THIS: Store the message for retry
        lastUserMessage = userInput

        // Add user message immediately
        _messages.value = _messages.value + ChatMessage("user", userInput)

        // ADD THIS: Call the new processAIRequest function
        processAIRequest()
    }

    // ADD THIS: New retry function
    fun retryLastMessage() {
        if (lastUserMessage.isNotBlank()) {
            // Remove the last error message if it exists
            _messages.value = _messages.value.filter { !(it.sender == "ai" && it.isError) }
            processAIRequest()
        }
    }

    // ADD THIS: New function to handle AI requests with better error handling
    private fun processAIRequest() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Send to AI
                val aiReply = aiChatUtils.sendMessage(lastUserMessage)

                // Check if response contains error
                if (aiReply.startsWith("Error:")) {
                    // Add error message with retry option
                    _messages.value = _messages.value + ChatMessage(
                        sender = "ai",
                        content = aiReply,
                        isError = true,
                        canRetry = true
                    )
                } else {
                    // Add successful AI response
                    _messages.value = _messages.value + ChatMessage("ai", aiReply)
                }
            } catch (e: Exception) {
                // Add error message with retry option
                _messages.value = _messages.value + ChatMessage(
                    sender = "ai",
                    content = "Failed to get response. Please check your connection and try again.",
                    isError = true,
                    canRetry = true
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}