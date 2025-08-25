package com.kotlinjsonui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kotlinjsonui.ui.theme.KotlinJsonUITheme
import com.kotlinjsonui.ui.components.JsonUILoader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinJsonUITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Load main layout from JSON
                    JsonUILoader(
                        layoutName = "main",
                        onAction = { action ->
                            handleAction(action)
                        }
                    )
                }
            }
        }
    }
    
    private fun handleAction(action: String) {
        // Handle actions from JSON UI
        when (action) {
            // Add action handlers here
            else -> println("Unknown action: $action")
        }
    }
}
