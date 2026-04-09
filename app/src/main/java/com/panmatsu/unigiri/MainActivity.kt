package com.panmatsu.unigiri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        CoroutineScope(Dispatchers.IO).launch {
            // Google Mobile Ads SDK の初期化
            MobileAds.initialize(this@MainActivity) {}
        }

        setContent {
            UnigiriTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(viewModel = viewModel)
                }
            }
        }
    }
}
