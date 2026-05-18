package com.example.nammamela.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.nammamela.ui.screens.*
import com.example.nammamela.ui.screens.ChatBotScreen
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {
        composable(Routes.Splash.route) {
            SplashScreen(navController)
        }

        composable(Routes.Login.route) {
            LoginScreen(navController)
        }

        composable(Routes.Signup.route) {
            SignupScreen(navController)
        }

        composable(Routes.Home.route) {
            HomeScreen(navController)
        }
        composable(Routes.Seat.route) {
            SeatBookingScreen(navController)
        }
        composable(Routes.Cast.route) {
            CastScreen(navController)
        }
        composable(Routes.FanWall.route) {
            FanWallScreen()
        }
        composable(Routes.Admin.route) {
            AdminScreen(navController)
        }
        composable(Routes.Ticket.route) { backStackEntry ->

            val seat =
                backStackEntry.arguments?.getString("seat") ?: ""

            TicketScreen(navController, seat)
        }
        composable(Routes.Bot.route) {
            ChatBotScreen(navController)
        }
    }
}