package com.ragnar.ai_tutor.utils

class AIChatUtils {

    data class AIChatStates(
        val isWaiting: Boolean = true,
        val response : String = "",
        val inputMessage: String = "",
    )

    fun initializeConnection() {

    }

    fun sendMessage(message: String) {

    }
}