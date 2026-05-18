package com.example.nammamela

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.nammamela.navigation.AppNavigation
import com.example.nammamela.ui.theme.NammaMelaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NammaMelaTheme {
                AppNavigation()
            }
        }
    }
}