package com.panmatsu.unigiri.scenes.about

import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.panmatsu.unigiri.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    title: String,
    url: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { padding ->
        val baseUrl = stringResource(R.string.url_github_unigiri)
        AndroidView(
            modifier = Modifier.padding(padding),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val linkUrl = request?.url?.toString() ?: return true
                            if (linkUrl.startsWith(baseUrl)) {
                                return false
                            }
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl)))
                            return true
                        }
                    }
                    settings.javaScriptEnabled = false
                    loadUrl(url)
                }
            }
        )
    }
}
