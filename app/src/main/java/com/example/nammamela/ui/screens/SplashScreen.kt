package com.example.nammamela.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.nammamela.navigation.Routes

@Composable
fun SplashScreen(navController: NavController) {

    LaunchedEffect(Unit) {
        delay(2500)
        navController.navigate(Routes.Login.route) {
            popUpTo(0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF5A0000)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🎭 Namma-Mela",
            color = Color(0xFFFFD700),
            fontSize = 34.sp
        )
    }
}