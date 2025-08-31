package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.core.DynamicViewProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

/**
 * Implementation of DynamicViewRenderer
 * This class is only compiled in DEBUG builds
 */
class DynamicViewRendererImpl : DynamicViewProvider.DynamicViewRenderer {
    
    @Composable
    override fun render(
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
        
        LaunchedEffect(layoutName) {
            isLoading = true
            error = null
            
            try {
                withContext(Dispatchers.IO) {
                    // Try to load from assets/layouts/ directory
                    val fileName = if (layoutName.endsWith(".json")) {
                        "layouts/$layoutName"
                    } else {
                        "layouts/$layoutName.json"
                    }
                    
                    try {
                        context.assets.open(fileName).use { inputStream ->
                            InputStreamReader(inputStream).use { reader ->
                                val gson = Gson()
                                jsonObject = gson.fromJson(reader, JsonObject::class.java)
                            }
                        }
                    } catch (e: Exception) {
                        // Try alternate path without layouts/ prefix
                        val alternateFileName = if (layoutName.endsWith(".json")) {
                            layoutName
                        } else {
                            "$layoutName.json"
                        }
                        
                        context.assets.open(alternateFileName).use { inputStream ->
                            InputStreamReader(inputStream).use { reader ->
                                val gson = Gson()
                                jsonObject = gson.fromJson(reader, JsonObject::class.java)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                error = "Failed to load layout '$layoutName': ${e.message}"
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

/**
 * Auto-registration of the renderer
 * This will be called when the class is loaded
 */
private val autoRegister = run {
    DynamicViewProvider.setRenderer(DynamicViewRendererImpl())
}