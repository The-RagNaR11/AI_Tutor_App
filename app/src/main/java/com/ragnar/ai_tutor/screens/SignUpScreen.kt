package com.ragnar.ai_tutor.screens

import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ragnar.ai_tutor.MainActivity
import com.ragnar.ai_tutor.R
import com.ragnar.ai_tutor.item.GoogleUserPrefs
import com.ragnar.ai_tutor.signIn.GoogleSignInUtils
import com.ragnar.ai_tutor.ui.theme.TextPrimary
import com.ragnar.ai_tutor.ui.theme.White

@Composable
fun SignUpScreen(onLoginSuccess: () -> Unit) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        GoogleSignInUtils.doGoogleSignIn(
            context = context,
            scope = scope,
            launcher = null,
            onLoginSuccess = { }
        )
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(White)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Welcome to The \nNCERT Learning Hub",
                color = TextPrimary,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(15.dp))
            OutlinedButton(
                onClick = {
                    Log.i("SignUpScreen", "Google Sign In Button Clicked")

                    GoogleSignInUtils.doGoogleSignIn(
                        context = context,
                        scope = scope,
                        launcher = launcher,
                        onLoginSuccess = { userInfo ->

                            GoogleUserPrefs.saveUserInfo(context, GoogleUserPrefs.KEY_ID, userInfo.id)
                            GoogleUserPrefs.saveUserInfo(context, GoogleUserPrefs.KEY_EMAIL, userInfo.email)
                            GoogleUserPrefs.saveUserInfo(context, GoogleUserPrefs.KEY_DISPLAY_NAME, userInfo.displayName)
                            GoogleUserPrefs.saveUserInfo(context, GoogleUserPrefs.KEY_PROFILE_PIC, userInfo.profilePictureUri)

                            onLoginSuccess()


                        }
                    )
                },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google sign-in icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google Sign In",
                        color = TextPrimary

                    )
                }
            }
        }
    }
}