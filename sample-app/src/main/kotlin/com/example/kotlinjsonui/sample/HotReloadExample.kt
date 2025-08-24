package com.example.kotlinjsonui.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.kotlinjsonui.HotReloadView
import com.kotlinjsonui.SafeHotReloadView

/**
 * Example of using HotReloadView for development
 * 
 * The JSON file will be automatically reloaded when it changes on disk,
 * allowing for rapid UI development without recompiling
 */
@Composable
fun HotReloadExample() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hot Reload Example") }
            )
        }
    ) { paddingValues ->
        // Example 1: Using HotReloadView with a file path
        HotReloadView(
            filePath = "/sdcard/Download/ui_layout.json",  // Path to your JSON file
            data = mapOf(
                "userName" to "John Doe",
                "isLoggedIn" to true,
                "itemCount" to 5,
                "onButtonClick" to { println("Button clicked!") }
            ),
            watchInterval = 1000,  // Check for changes every second
            enabled = true  // Enable hot reload
        )
    }
}

/**
 * Example using SafeHotReloadView - safer for production
 */
@Composable
fun SafeHotReloadExample() {
    val jsonString = """
        {
            "type": "VStack",
            "spacing": 16,
            "padding": 20,
            "children": [
                {
                    "type": "Text",
                    "text": "Welcome @{userName}!",
                    "fontSize": 24,
                    "fontWeight": "bold"
                },
                {
                    "type": "Button",
                    "text": "Click Me",
                    "onclick": "onButtonClick"
                }
            ]
        }
    """.trimIndent()
    
    SafeHotReloadView(
        jsonString = jsonString,
        data = mapOf(
            "userName" to "Jane Smith",
            "onButtonClick" to { println("Safe button clicked!") }
        )
    )
}

/**
 * Example showing hot reload with dynamic data updates
 */
@Composable
fun HotReloadWithDynamicData() {
    // Note: You would use your actual ViewModel here
    // val viewModel = remember { YourViewModel() }
    // val data by viewModel.data.collectAsState()
    
    val data = mapOf(
        "userName" to "Dynamic User",
        "counter" to 0
    )
    
    HotReloadView(
        filePath = "/sdcard/Download/dynamic_ui.json",
        data = data + mapOf(
            "updateData" to { updates: Map<String, Any> ->
                println("Data update requested: $updates")
            }
        ),
        watchInterval = 500,  // Faster update for development
        onError = { error ->
            println("Hot reload error: ${error.message}")
        }
    )
}