package com.ragnar.ai_tutor.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ragnar.ai_tutor.item.BottomNavItem
import com.ragnar.ai_tutor.screens.ChatScreen
import com.ragnar.ai_tutor.screens.LessonScreen
import com.ragnar.ai_tutor.screens.ProfileScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.profile.route) {

        composable(BottomNavItem.chat.route){
            ChatScreen()
        }
        composable(BottomNavItem.lesson.route){
            LessonScreen()
        }
        composable(BottomNavItem.profile.route){
            ProfileScreen()
        }
    }
}