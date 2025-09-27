package com.ragnar.ai_tutor.item

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.ImportContacts
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object chat : BottomNavItem("chat", "Chat", Icons.AutoMirrored.Outlined.Chat)
    object lesson : BottomNavItem("lesson", "Lessons", Icons.Outlined.ImportContacts)
    object profile : BottomNavItem("profile", "Profile", Icons.Outlined.Person)
}