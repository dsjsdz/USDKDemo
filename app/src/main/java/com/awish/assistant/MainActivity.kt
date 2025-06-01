package com.awish.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    try {
      setContent {
        Initiate()
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}

@Composable
fun Initiate() {
  MaterialTheme {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
      composable("splash") { Splash(navController) }
      composable("main") { Welcome(navController) }
      composable("setting") { Setting(navController) }
    }
  }
}
