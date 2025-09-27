package com.ragnar.ai_tutor.chatBot

import android.util.Log
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

    // a list to store recent chat for the current session
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // for PopUp simulation screen
    private var messageCount = 0
    private val _showPopup = MutableStateFlow(false)
    val showPopup: StateFlow<Boolean> = _showPopup

    // local variable to store user message in case of any error
    private var lastUserMessage: String = ""

    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        // storing user input to retry in case of error
        lastUserMessage = userInput

        _messages.value = _messages.value + ChatMessage("user", userInput)

        //Increment and check if it's the 2nd chat
        messageCount++
        if (messageCount % 2 == 0) {
            _showPopup.value = true
        }

        processAIRequest()
    }

    // method to retry if there is any error
    fun retryLastMessage() {
        if (lastUserMessage.isNotBlank()) {
            _messages.value = _messages.value.filter { !(it.sender == "ai" && it.isError) }
            processAIRequest()
        }
    }

    private fun processAIRequest() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val aiReply = aiChatUtils.sendMessage(lastUserMessage)

                // Check if response contains error
                if (aiReply.startsWith("Error:")) {
                    // adds message even if there is error but with retry option
                    _messages.value = _messages.value + ChatMessage(
                        sender = "ai",
                        content = aiReply,
                        isError = true,
                        canRetry = true
                    )
                    Log.e("ChatViewModel", "Error: Sender: AI\n $aiReply" )
                } else {
                    // on success reply from AI
                    _messages.value = _messages.value + ChatMessage("ai", aiReply)
                    Log.i("ChatViewModel", "Success: Sender: AI\n $aiReply" )
                }
            } catch (e: Exception) {
                // adds message even if there is error but with retry option
                _messages.value = _messages.value + ChatMessage(
                    sender = "ai",
                    content = "Failed to get response. Please check your connection and try again.",
                    isError = true,
                    canRetry = true
                )
                Log.e("ChatViewModel", "Error: $e" )
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun dismissPopUp() {
        _showPopup.value = false
    }
}