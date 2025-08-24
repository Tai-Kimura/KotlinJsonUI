package com.kotlinjsonui.dynamic

import androidx.compose.runtime.*
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.io.File

/**
 * DynamicViewLoader provides hot reload functionality for JSON-based UIs
 * 
 * NOTE: This class is intended for debug builds only.
 * Use HotReloadView or SafeHotReloadView from the main package for production-safe hot reload.
 * 
 * Features:
 * - File watching for JSON changes
 * - Automatic UI refresh on JSON updates
 * - Error recovery
 * - Development mode optimizations
 */
internal object DynamicViewLoader {
    
    /**
     * Loads and watches a JSON file for changes, automatically updating the UI
     * 
     * @param filePath Path to the JSON file
     * @param data Data map for binding
     * @param watchInterval Interval in milliseconds to check for file changes
     * @param onError Error handler
     * @return State containing the parsed JSON object
     */
    @Composable
    fun loadAndWatch(
        filePath: String,
        data: Map<String, Any> = emptyMap(),
        watchInterval: Long = 1000,
        onError: ((Exception) -> Unit)? = null
    ): State<JsonObject?> {
        var jsonState by remember { mutableStateOf<JsonObject?>(null) }
        var lastModified by remember { mutableStateOf(0L) }
        
        LaunchedEffect(filePath) {
            val file = File(filePath)
            
            while (isActive) {
                try {
                    if (file.exists() && file.lastModified() != lastModified) {
                        lastModified = file.lastModified()
                        val jsonString = file.readText()
                        jsonState = JsonParser.parseString(jsonString).asJsonObject
                    }
                } catch (e: Exception) {
                    onError?.invoke(e)
                }
                
                delay(watchInterval)
            }
        }
        
        return remember { derivedStateOf { jsonState } }
    }
    
    /**
     * Loads JSON from a string
     * 
     * @param jsonString JSON string to parse
     * @param onError Error handler
     * @return Parsed JSON object or null on error
     */
    fun loadFromString(
        jsonString: String,
        onError: ((Exception) -> Unit)? = null
    ): JsonObject? {
        return try {
            JsonParser.parseString(jsonString).asJsonObject
        } catch (e: Exception) {
            onError?.invoke(e)
            null
        }
    }
    
    /**
     * Loads JSON from a file
     * 
     * @param filePath Path to the JSON file
     * @param onError Error handler
     * @return Parsed JSON object or null on error
     */
    fun loadFromFile(
        filePath: String,
        onError: ((Exception) -> Unit)? = null
    ): JsonObject? {
        return try {
            val file = File(filePath)
            if (file.exists()) {
                val jsonString = file.readText()
                JsonParser.parseString(jsonString).asJsonObject
            } else {
                onError?.invoke(IllegalArgumentException("File not found: $filePath"))
                null
            }
        } catch (e: Exception) {
            onError?.invoke(e)
            null
        }
    }
    
    /**
     * Composable that renders a DynamicView with hot reload support
     * 
     * @param filePath Path to the JSON file
     * @param data Data map for binding
     * @param watchInterval Interval to check for changes
     * @param onError Error handler
     */
    @Composable
    fun HotReloadView(
        filePath: String,
        data: Map<String, Any> = emptyMap(),
        watchInterval: Long = 1000,
        onError: ((Exception) -> Unit)? = null
    ) {
        val jsonState = loadAndWatch(filePath, data, watchInterval, onError)
        
        jsonState.value?.let { json ->
            DynamicView(json, data, onError)
        }
    }
}