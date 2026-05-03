package com.example.wishingsprite

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wishingsprite.feature.debug.ui.DebugScreen
import com.example.wishingsprite.feature.home.ui.HomeScreen
import com.example.wishingsprite.feature.settings.ui.SettingsScreen

private const val DebugRoute = "debug"
private const val HomeRoute = "home"
private const val SettingsRoute = "settings"

@Composable
fun WishingSpriteApp() {
  val navController = rememberNavController()

  NavHost(
    navController = navController,
    startDestination = HomeRoute,
    enterTransition = { EnterTransition.None },
    exitTransition = { ExitTransition.None },
    popEnterTransition = { EnterTransition.None },
    popExitTransition = { ExitTransition.None },
  ) {
    composable(HomeRoute) {
      HomeScreen(
        onDebugClick = { navController.navigate(DebugRoute) },
        onSettingsClick = { navController.navigate(SettingsRoute) },
      )
    }
    composable(DebugRoute) { DebugScreen(onBackClick = { navController.popBackStack() }) }
    composable(SettingsRoute) { SettingsScreen(onBackClick = { navController.popBackStack() }) }
  }
}
