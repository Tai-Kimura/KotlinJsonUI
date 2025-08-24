package com.kotlinjsonui.dynamic

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.dynamic.components.*
import com.kotlinjsonui.dynamic.hotloader.HotLoader
import androidx.compose.runtime.collectAsState

/**
 * Main entry point for rendering dynamic UI from JSON.
 * This component determines the appropriate component type from JSON
 * and renders it with data binding support.
 * 
 * Features:
 * - JSON validation and error handling
 * - Component type detection with fallback
 * - Data binding context management
 * - Error recovery with debug information
 * 
 * @param json The JSON object describing the UI component
 * @param data Map of data for binding. @{key} in JSON will be replaced with data[key].
 *             Functions in the data map can be referenced by name for event handlers.
 * @param onError Optional error handler callback
 */
@Composable
fun DynamicView(
    json: JsonObject,
    data: Map<String, Any> = emptyMap(),
    onError: ((Exception) -> Unit)? = null
) {
    // Validate JSON has required type field
    val type = try {
        json.get("type")?.asString
    } catch (e: Exception) {
        onError?.invoke(e)
        null
    }
    
    if (type.isNullOrEmpty()) {
        val error = IllegalArgumentException("JSON must have a 'type' field")
        onError?.invoke(error)
        if (Configuration.showErrorsInDebug) {
            ErrorComponent("Missing 'type' field in JSON")
        }
        return
    }
    
    // Render the appropriate component based on type
    val rendered = when (type.lowercase()) {
        "text", "label" -> {
            DynamicTextComponent.create(json, data)
            true
        }
        "textfield" -> {
            DynamicTextFieldComponent.create(json, data)
            true
        }
        "button" -> {
            DynamicButtonComponent.create(json, data)
            true
        }
        "image" -> {
            DynamicImageComponent.create(json, data)
            true
        }
        "networkimage" -> {
            DynamicNetworkImageComponent.create(json, data)
            true
        }
        "circleimage" -> {
            DynamicCircleImageComponent.create(json, data)
            true
        }
        "switch" -> {
            DynamicSwitchComponent.create(json, data)
            true
        }
        "checkbox" -> {
            DynamicCheckBoxComponent.create(json, data)
            true
        }
        "radio" -> {
            DynamicRadioComponent.create(json, data)
            true
        }
        "slider" -> {
            DynamicSliderComponent.create(json, data)
            true
        }
        "progress", "progressbar" -> {
            DynamicProgressComponent.create(json, data)
            true
        }
        "indicator" -> {
            DynamicIndicatorComponent.create(json, data)
            true
        }
        "selectbox", "spinner" -> {
            DynamicSelectBoxComponent.create(json, data)
            true
        }
        "segment", "tablayout" -> {
            DynamicSegmentComponent.create(json, data)
            true
        }
        "toggle" -> {
            DynamicToggleComponent.create(json, data)
            true
        }
        "scrollview" -> {
            DynamicScrollViewComponent.create(json, data)
            true
        }
        "hstack", "row" -> {
            DynamicHStackComponent.create(json, data)
            true
        }
        "vstack", "column" -> {
            DynamicVStackComponent.create(json, data)
            true
        }
        "zstack", "box" -> {
            DynamicZStackComponent.create(json, data)
            true
        }
        "container", "view" -> {
            DynamicContainerComponent.create(json, data)
            true
        }
        "safeareaview" -> {
            DynamicSafeAreaViewComponent.create(json, data)
            true
        }
        "constraintlayout" -> {
            DynamicConstraintLayoutComponent.create(json, data)
            true
        }
        "collection", "collectionview", "recyclerview", "grid", "lazygrid" -> {
            DynamicCollectionComponent.create(json, data)
            true
        }
        "table", "listview" -> {
            DynamicTableComponent.create(json, data)
            true
        }
        "webview" -> {
            DynamicWebViewComponent.create(json, data)
            true
        }
        "web" -> {
            DynamicWebComponent.create(json, data)
            true
        }
        "tabview" -> {
            DynamicTabViewComponent.create(json, data)
            true
        }
        "gradientview" -> {
            DynamicGradientViewComponent.create(json, data)
            true
        }
        "circleview" -> {
            DynamicCircleViewComponent.create(json, data)
            true
        }
        "blurview" -> {
            DynamicBlurViewComponent.create(json, data)
            true
        }
        "iconlabel" -> {
            DynamicIconLabelComponent.create(json, data)
            true
        }
        "textview" -> {
            DynamicTextViewComponent.create(json, data)
            true
        }
        "triangle" -> {
            DynamicTriangleComponent.create(json, data)
            true
        }
        else -> {
            // Unknown component type
            false
        }
    }
    
    if (!rendered) {
        val error = IllegalArgumentException("Unknown component type: $type")
        onError?.invoke(error)
        
        // Log error in debug mode
        if (Configuration.showErrorsInDebug) {
            Log.w("DynamicView", "Unknown component type: $type")
            ErrorComponent("Unknown component type: $type")
        } else if (Configuration.fallbackComponent != null) {
            // Use custom fallback component if configured
            Configuration.fallbackComponent?.invoke(json, data)
        }
    }
}

/**
 * Error component shown in debug mode
 */
@Composable
private fun ErrorComponent(message: String) {
    Box(
        modifier = Modifier
            .background(Color.Red.copy(alpha = 0.1f))
            .padding(8.dp)
    ) {
        Text(
            text = "⚠️ $message",
            color = Color.Red
        )
    }
}

/**
 * Renders a list of components from a JSON array.
 * Useful for rendering the contents of container components.
 * 
 * @param components List of JSON objects describing UI components
 * @param data Map of data for binding
 * @param onError Optional error handler for individual component errors
 */
@Composable
fun DynamicViews(
    components: List<JsonObject>,
    data: Map<String, Any> = emptyMap(),
    onError: ((Exception) -> Unit)? = null
) {
    components.forEach { component ->
        DynamicView(component, data, onError)
    }
}

/**
 * DynamicView with hot reload support using layout name
 * This version loads JSON from assets/cache and supports WebSocket-based hot reload
 * 
 * @param layoutName Name of the layout file (without .json extension)
 * @param data Map of data for binding
 * @param onError Optional error handler
 */
@Composable
fun DynamicView(
    layoutName: String,
    data: Map<String, Any> = emptyMap(),
    onError: ((Exception) -> Unit)? = null
) {
    val context = LocalContext.current
    var jsonObject by remember { mutableStateOf<JsonObject?>(null) }
    
    // Check if dynamic mode is enabled
    val isDynamicModeEnabled by DynamicModeManager.isDynamicModeEnabled.collectAsState()
    
    DisposableEffect(layoutName, isDynamicModeEnabled) {
        if (!isDynamicModeEnabled) {
            // Just load from assets once
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
                    Log.d("DynamicView", "HotLoader connected")
                }
                
                override fun onDisconnected() {
                    Log.d("DynamicView", "HotLoader disconnected")
                }
                
                override fun onLayoutUpdated(name: String, content: String) {
                    if (name == layoutName || name == "$layoutName.json") {
                        try {
                            jsonObject = JsonParser.parseString(content).asJsonObject
                            Log.d("DynamicView", "Layout updated: $name")
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
        DynamicView(json, data, onError)
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