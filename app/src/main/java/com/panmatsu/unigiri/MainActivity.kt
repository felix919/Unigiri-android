package com.panmatsu.unigiri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.panmatsu.unigiri.scenes.AppNavHost
import com.panmatsu.unigiri.scenes.search.SearchViewModel
import com.panmatsu.unigiri.scenes.search.SearchViewModelFactory
import com.panmatsu.unigiri.ui.theme.UnigiriTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val mainColor = ContextCompat.getColor(this, R.color.main_color)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(mainColor)
        )
        super.onCreate(savedInstanceState)

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
