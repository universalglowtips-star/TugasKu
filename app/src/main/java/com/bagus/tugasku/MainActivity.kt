package com.bagus.tugasku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bagus.tugasku.ui.navigation.AppNavHost
import com.bagus.tugasku.ui.theme.TugasKuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TugasKuTheme {
                AppNavHost()
            }
        }
    }
}
