package com.kotlinjsonui.dynamic

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.hotloader.HotLoader
import java.io.File

/**
 * HotReloadView provides hot reload functionality for JSON-based UIs using WebSocket
 * 
 * @param layoutName Name of the layout file (without .json extension)
 * @param data Data map for binding
 * @param enabled Whether hot reload is enabled (default: true)
 * @param onError Optional error handler
 */
@Composable
fun HotReloadView(
    layoutName: String,
    data: Map<String, Any> = emptyMap(),
    enabled: Boolean = true,
    onError: ((Exception) -> Unit)? = null
) {
    val context = LocalContext.current
    var jsonObject by remember { mutableStateOf<JsonObject?>(null) }
    
    DisposableEffect(layoutName, enabled) {
        if (!enabled) {
            // Just load from assets or cache once
            loadLayoutFromAssets(context, layoutName, onError)?.let {
                jsonObject = it
            }
            onDispose { }
        } else {
            // Use HotLoader for WebSocket-based hot reload
            val hotLoader = HotLoader.getInstance(context)
            
            // First try to load from cache
            hotLoader.getCachedLayout(layoutName)?.let { cached ->
                try {
                    jsonObject = JsonParser.parseString(cached).asJsonObject
                } catch (e: Exception) {
                    onError?.invoke(e)
                }
            } ?: run {
                // Fall back to loading from assets
                loadLayoutFromAssets(context, layoutName, onError)?.let {
                    jsonObject = it
                }
            }
            
            // Set up listener for updates
            val listener = object : HotLoader.HotLoaderListener {
                override fun onConnected() {
                    // Connection established
                }
                
                override fun onDisconnected() {
                    // Connection lost
                }
                
                override fun onLayoutUpdated(name: String, content: String) {
                    if (name == layoutName || name == "$layoutName.json") {
                        try {
                            jsonObject = JsonParser.parseString(content).asJsonObject
                        } catch (e: Exception) {
                            onError?.invoke(e)
                        }
                    }
                }
                
                override fun onLayoutAdded(name: String) {
                    // New layout added
                }
                
                override fun onLayoutRemoved(name: String) {
                    // Layout removed
                }
                
                override fun onError(error: Throwable) {
                    onError?.invoke(Exception(error))
                }
            }
            
            hotLoader.addListener(listener)
            hotLoader.start()
            
            onDispose {
                hotLoader.removeListener(listener)
            }
        }
    }
    
    // Render the view
    jsonObject?.let { json ->
        DynamicView(json = json, data = data)
    }
}

/**
 * HotReloadView with file path (for backward compatibility)
 * 
 * @param filePath Path to the JSON file
 * @param data Data map for binding  
 * @param watchInterval Ignored - uses WebSocket instead of polling
 * @param enabled Whether hot reload is enabled
 * @param onError Optional error handler
 */
@Composable
fun HotReloadView(
    filePath: String,
    data: Map<String, Any> = emptyMap(),
    watchInterval: Long = 1000, // Ignored - kept for API compatibility
    enabled: Boolean = true,
    onError: ((Exception) -> Unit)? = null
) {
    // Extract layout name from file path
    val layoutName = File(filePath).nameWithoutExtension
    
    HotReloadView(
        layoutName = layoutName,
        data = data,
        enabled = enabled,
        onError = onError
    )
}

/**
 * HotReloadView with JSON string content  
 * 
 * @param jsonString JSON string content
 * @param data Data map for binding
 * @param onError Optional error handler
 */
@Composable
fun HotReloadView(
    jsonString: String,
    data: Map<String, Any> = emptyMap(),
    onError: ((Exception) -> Unit)? = null
) {
    val jsonObject = remember(jsonString) {
        try {
            JsonParser.parseString(jsonString).asJsonObject
        } catch (e: Exception) {
            onError?.invoke(e)
            null
        }
    }
    
    jsonObject?.let { json ->
        DynamicView(json = json, data = data)
    }
}

/**
 * Load layout from assets folder
 */
private fun loadLayoutFromAssets(
    context: Context,
    layoutName: String,
    onError: ((Exception) -> Unit)?
): JsonObject? {
    return try {
        val fileName = if (layoutName.endsWith(".json")) layoutName else "$layoutName.json"
        val jsonString = context.assets.open("Layouts/$fileName").bufferedReader().use { it.readText() }
        JsonParser.parseString(jsonString).asJsonObject
    } catch (e: Exception) {
        // Try without Layouts directory
        try {
            val fileName = if (layoutName.endsWith(".json")) layoutName else "$layoutName.json"
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            JsonParser.parseString(jsonString).asJsonObject
        } catch (e2: Exception) {
            onError?.invoke(e2)
            null
        }
    }
}