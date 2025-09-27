package com.ragnar.ai_tutor.menu

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewAlertDialog(
    url: String,
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
            },
            containerColor = Color.Transparent,
            text = {
                Card {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .padding(0.dp),
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = true
                                webViewClient = WebViewClient()
                                loadUrl(url)
                            }
                        }
                    )
                }
            }
        )
    }
}
