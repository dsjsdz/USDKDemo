package com.awish.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cc.uling.usdk.USDK
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // init usdk
    USDK.getInstance().init(application)

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

    // App router
    NavHost(navController = navController, startDestination = "splash") {
      // loading of splash for app init
      composable("splash") { Splash(navController) }
      composable("main") { Welcome(navController) }
      composable("about") { About(navController) }
    }
  }
}
