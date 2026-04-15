package com.panmatsu.unigiri

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.google.android.gms.ads.MobileAds
import com.panmatsu.unigiri.scenes.AppNavHost
import com.panmatsu.unigiri.ui.theme.UnigiriTheme
import com.panmatsu.unigiri.scenes.search.SearchViewModel
import com.panmatsu.unigiri.scenes.search.SearchViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        private const val PREFS_NAME = "unigiri_prefs"
        private const val KEY_CONSENT = "user_consent_agreed"
    }

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory()
    }

    private fun initializeAds() {
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity) {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val initialConsent = prefs.getBoolean(KEY_CONSENT, false)

        if (initialConsent) {
            initializeAds()
        }

        setContent {
            UnigiriTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var hasConsent by remember { mutableStateOf(initialConsent) }

                    AppNavHost(
                        viewModel = viewModel,
                        hasConsent = hasConsent,
                        onConsent = {
                            prefs.edit().putBoolean(KEY_CONSENT, true).apply()
                            hasConsent = true
                            initializeAds()
                        }
                    )
                }
            }
        }
    }
}
