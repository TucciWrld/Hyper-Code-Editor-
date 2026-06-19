package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.IdeMainScreen
import com.example.ui.IdeViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true) { // Force a professional luxury cyber dark theme
        val viewModel = remember { IdeViewModel(application) }
        Surface(modifier = Modifier.fillMaxSize()) {
          IdeMainScreen(viewModel = viewModel)
        }
      }
    }
  }
}
