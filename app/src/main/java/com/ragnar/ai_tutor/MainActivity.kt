package com.ragnar.ai_tutor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ragnar.ai_tutor.screens.BottomNavMainScreen
import com.ragnar.ai_tutor.ui.theme.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                BottomNavMainScreen()
            }
        }
    }
}