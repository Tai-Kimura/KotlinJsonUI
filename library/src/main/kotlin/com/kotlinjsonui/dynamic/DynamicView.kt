package com.kotlinjsonui.dynamic

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import com.kotlinjsonui.components.VisibilityWrapper
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.dynamic.components.*
import com.kotlinjsonui.dynamic.helpers.ColorParser
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
    val context = LocalContext.current
    
    // Initialize ResourceCache and ColorParser with context
    ResourceCache.init(context)
    ColorParser.init(context)
    
    // Apply styles if a style attribute is present
    val styledJson = if (json.has("style")) {
        DynamicStyleLoader.applyStyle(context, json)
    } else {
        json
    }
    
    // Check if this is an include element
    if (styledJson.has("include")) {
        DynamicIncludeComponent.create(styledJson, data)
        return
    }
    
    // Check if this is a data element (should be skipped)
    if (styledJson.has("data") && !styledJson.has("type")) {
        // Data-only elements should not be rendered
        return
    }
    
    // Validate JSON has required type field
    val type = try {
        styledJson.get("type")?.asString
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
    
    // Check for visibility attribute
    val visibility = styledJson.get("visibility")?.asString
    
    // Render the appropriate component based on type
    val renderComponent: @Composable () -> Unit = {
        when (type.lowercase()) {
            "text", "label" -> DynamicTextComponent.create(styledJson, data)
            "textfield" -> DynamicTextFieldComponent.create(styledJson, data)
            "button" -> DynamicButtonComponent.create(styledJson, data)
            "image" -> DynamicImageComponent.create(styledJson, data)
            "networkimage" -> DynamicNetworkImageComponent.create(styledJson, data)
            "circleimage" -> DynamicCircleImageComponent.create(styledJson, data)
            "switch" -> DynamicSwitchComponent.create(styledJson, data)
            "checkbox" -> DynamicCheckBoxComponent.create(styledJson, data)
            "radio" -> DynamicRadioComponent.create(styledJson, data)
            "slider" -> DynamicSliderComponent.create(styledJson, data)
            "progress", "progressbar" -> DynamicProgressComponent.create(styledJson, data)
            "indicator" -> DynamicIndicatorComponent.create(styledJson, data)
            "selectbox", "spinner" -> DynamicSelectBoxComponent.create(styledJson, data)
            "segment", "tablayout" -> DynamicSegmentComponent.create(styledJson, data)
            "toggle" -> DynamicToggleComponent.create(styledJson, data)
            "scrollview", "scroll" -> DynamicScrollViewComponent.create(styledJson, data)
            "hstack", "row" -> DynamicHStackComponent.create(styledJson, data)
            "vstack", "column" -> DynamicVStackComponent.create(styledJson, data)
            "zstack", "box" -> DynamicZStackComponent.create(styledJson, data)
            "container", "view" -> DynamicContainerComponent.create(styledJson, data)
            "safeareaview" -> DynamicSafeAreaViewComponent.create(styledJson, data)
            "constraintlayout" -> DynamicConstraintLayoutComponent.create(styledJson, data)
            "collection", "collectionview", "recyclerview", "grid", "lazygrid" -> DynamicCollectionComponent.create(styledJson, data)
            "table", "listview" -> DynamicTableComponent.create(styledJson, data)
            "webview" -> DynamicWebViewComponent.create(styledJson, data)
            "web" -> DynamicWebComponent.create(styledJson, data)
            "tabview" -> DynamicTabViewComponent.create(styledJson, data)
            "gradientview" -> DynamicGradientViewComponent.create(styledJson, data)
            "circleview" -> DynamicCircleViewComponent.create(styledJson, data)
            "blurview" -> DynamicBlurViewComponent.create(styledJson, data)
            "iconlabel" -> DynamicIconLabelComponent.create(styledJson, data)
            "textview" -> DynamicTextViewComponent.create(styledJson, data)
            "triangle" -> DynamicTriangleComponent.create(styledJson, data)
            else -> {
                // First, try custom component handler
                val handled = Configuration.customComponentHandler?.invoke(type, styledJson, data) ?: false
                
                if (!handled) {
                    // Unknown component type
                    val error = IllegalArgumentException("Unknown component type: $type")
                    onError?.invoke(error)
                    
                    // Log error in debug mode
                    if (Configuration.showErrorsInDebug) {
                        Log.w("DynamicView", "Unknown component type: $type")
                        ErrorComponent("Unknown component type: $type")
                    } else if (Configuration.fallbackComponent != null) {
                        // Use custom fallback component if configured
                        Configuration.fallbackComponent?.invoke(styledJson, data)
                    }
                }
            }
        }
    }
    
    // Apply visibility wrapper if visibility attribute is present
    if (!visibility.isNullOrEmpty()) {
        // Process data binding for visibility
        val processedVisibility = processDataBinding(visibility, data, context)
        VisibilityWrapper(
            visibility = processedVisibility,
            content = renderComponent
        )
    } else {
        renderComponent()
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
    
    // Initialize ResourceCache and ColorParser with context
    ResourceCache.init(context)
    ColorParser.init(context)
    
    // Initialize DynamicLayoutLoader with context
    DynamicLayoutLoader.init(context)
    
    var jsonObject by remember { mutableStateOf<JsonObject?>(null) }
    var styleUpdateCounter by remember { mutableStateOf(0) }
    
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
                
                override fun onStyleUpdated(styleName: String, content: String) {
                    // Force recomposition when any style is updated
                    // This will cause all views to re-read their styles
                    styleUpdateCounter++
                    Log.d("DynamicView", "Style updated: $styleName, triggering recomposition")
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
    
    // Render the view - include styleUpdateCounter as a key to force recomposition
    jsonObject?.let { json ->
        key(styleUpdateCounter) {
            DynamicView(json, data, onError)
        }
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