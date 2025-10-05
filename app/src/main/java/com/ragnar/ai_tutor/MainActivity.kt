package com.ragnar.ai_tutor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ragnar.ai_tutor.item.GoogleUserPrefs
import com.ragnar.ai_tutor.screens.BottomNavMainScreen
import com.ragnar.ai_tutor.screens.ChatScreen
import com.ragnar.ai_tutor.screens.SignUpScreen
import com.ragnar.ai_tutor.ui.theme.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
//                ChatScreen()
                if (GoogleUserPrefs.isUserLoggedIn(this)) {
                    BottomNavMainScreen()
                }
                else {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var showHome by remember { mutableStateOf(false) }

    if (showHome) {
        BottomNavMainScreen()
    } else {
        SignUpScreen( onLoginSuccess = { showHome = true })
    }
}