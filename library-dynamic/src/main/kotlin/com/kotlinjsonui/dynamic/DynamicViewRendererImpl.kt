package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

/**
 * Implementation of DynamicViewRenderer
 * This class is only compiled in DEBUG builds
 */
class DynamicViewRendererImpl {
    companion object {
        private const val TAG = "DynamicViewRenderer"
    }
    
    @Composable
    fun render(
        layoutName: String,
        data: Map<String, Any>,
        modifier: Modifier,
        onError: ((String) -> Unit)?,
        onLoading: @Composable () -> Unit,
        content: @Composable (String) -> Unit
    ) {
        val context = LocalContext.current
        
        // Initialize ResourceCache with context
        ResourceCache.init(context)
        
        var jsonObject by remember { mutableStateOf<JsonObject?>(null) }
        var error by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        
        // Observe HotLoader updates for this layout
        val hotLoader = com.kotlinjsonui.dynamic.hotloader.HotLoader.getInstance(context)
        val lastUpdate by hotLoader.lastUpdate.collectAsState()
        
        LaunchedEffect(layoutName, lastUpdate) {
            isLoading = true
            error = null
            
            try {
                withContext(Dispatchers.IO) {
                    // First try to load from HotLoader cache
                    val cachedLayout = hotLoader.getCachedLayout(layoutName)
                    if (cachedLayout != null) {
                        Log.d(TAG, "Loading layout from HotLoader cache: $layoutName")
                        val gson = Gson()
                        jsonObject = gson.fromJson(cachedLayout, JsonObject::class.java)
                        Log.d(TAG, "Successfully loaded layout from cache")
                        return@withContext
                    }
                    
                    // If not in cache, try to load from assets/Layouts/ directory (capital L)
                    val fileName = if (layoutName.endsWith(".json")) {
                        "Layouts/$layoutName"
                    } else {
                        "Layouts/$layoutName.json"
                    }
                    
                    Log.d(TAG, "Attempting to load layout from assets: $fileName")
                    
                    try {
                        context.assets.open(fileName).use { inputStream ->
                            InputStreamReader(inputStream).use { reader ->
                                val gson = Gson()
                                jsonObject = gson.fromJson(reader, JsonObject::class.java)
                                Log.d(TAG, "Successfully loaded layout from: $fileName")
                            }
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "Failed to load from $fileName, trying alternate paths: ${e.message}")
                        
                        // Try with lowercase layouts/ directory
                        val lowercaseFileName = if (layoutName.endsWith(".json")) {
                            "layouts/$layoutName"
                        } else {
                            "layouts/$layoutName.json"
                        }
                        
                        try {
                            context.assets.open(lowercaseFileName).use { inputStream ->
                                InputStreamReader(inputStream).use { reader ->
                                    val gson = Gson()
                                    jsonObject = gson.fromJson(reader, JsonObject::class.java)
                                    Log.d(TAG, "Successfully loaded layout from: $lowercaseFileName")
                                }
                            }
                        } catch (e2: Exception) {
                            Log.d(TAG, "Failed to load from $lowercaseFileName, trying without prefix: ${e2.message}")
                            
                            // Try without any prefix
                            val alternateFileName = if (layoutName.endsWith(".json")) {
                                layoutName
                            } else {
                                "$layoutName.json"
                            }
                            
                            context.assets.open(alternateFileName).use { inputStream ->
                                InputStreamReader(inputStream).use { reader ->
                                    val gson = Gson()
                                    jsonObject = gson.fromJson(reader, JsonObject::class.java)
                                    Log.d(TAG, "Successfully loaded layout from: $alternateFileName")
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                error = "Failed to load layout '$layoutName': ${e.message}"
                Log.e(TAG, error, e)
                onError?.invoke(error!!)
            } finally {
                isLoading = false
            }
        }
        
        Box(modifier = modifier) {
            when {
                isLoading -> {
                    onLoading()
                }
                error != null -> {
                    Text(
                        text = error!!,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                jsonObject != null -> {
                    DynamicView(
                        json = jsonObject!!,
                        data = data
                    )
                }
            }
        }
    }
}