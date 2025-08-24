package com.kotlinjsonui.dynamic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.util.Log
import com.google.gson.JsonObject
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.components.*

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