package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.ui.DashboardScreen
import com.example.ui.MainAppScreen
import com.example.ui.TrackerViewModel
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
  private val viewModel: TrackerViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    setContent {
      MyApplicationTheme {
        MainAppScreen(viewModel = viewModel)
      }
    }
  }
}
