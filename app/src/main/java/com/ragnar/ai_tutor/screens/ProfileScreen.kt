package com.ragnar.ai_tutor.screens

import android.R
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.ragnar.ai_tutor.MainActivity
import com.ragnar.ai_tutor.item.GoogleUserPrefs
import com.ragnar.ai_tutor.ui.theme.BackgroundPrimary
import com.ragnar.ai_tutor.ui.theme.BackgroundSecondary
import com.ragnar.ai_tutor.ui.theme.ColorHint
import com.ragnar.ai_tutor.ui.theme.IconPrimary
import com.ragnar.ai_tutor.ui.theme.TextPrimary
import com.ragnar.ai_tutor.ui.theme.TextSecondary
import com.ragnar.ai_tutor.ui.theme.White

@Composable
fun ProfileScreen() {
    val context = LocalContext.current

    val name: String? = GoogleUserPrefs.getUserInfo(context, GoogleUserPrefs.KEY_DISPLAY_NAME)
    val imageUri: String? = GoogleUserPrefs.getUserInfo(context, GoogleUserPrefs.KEY_PROFILE_PIC)
    val email: String? = GoogleUserPrefs.getUserInfo(context, GoogleUserPrefs.KEY_EMAIL)

    Surface (
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(White)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Profile Picture
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "User's profile picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default profile icon",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }
            }

            // Name text
            Text(
                text = name ?: "John Doe",
                color = TextPrimary,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.padding(3.dp),)
            // E-Mail text
            Text(
                text = email ?: "johndoe@gmail.com",
                color = TextSecondary,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.padding(15.dp))
            // Setting Card
            Card(
                border = BorderStroke(1.dp, ColorHint),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),

                onClick = {
                    Log.i("ProfileScreen", "Setting Button Clicked")
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundPrimary)
                        .padding(10.dp, 15.dp)

                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Setting Icon",
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(
                        text = "Setting",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.padding(6.dp))
            // Logout Card
            Card(
                border = BorderStroke(1.dp, ColorHint),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                onClick = {
                    Log.i("ProfileScreen", "Logout Button Clicked")

                    GoogleUserPrefs.clearUserInfo(context)
                    // restart the current activity i.e. MainActivity and clear all back stack
                    val intent = Intent(context, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    (context as? Activity)?.finish()

                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundPrimary)
                        .padding(10.dp, 15.dp)

                ) {
                    Icon(
                        Icons.Default.PowerSettingsNew,
                        contentDescription = "Setting Icon",
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(
                        text = "Logout",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
