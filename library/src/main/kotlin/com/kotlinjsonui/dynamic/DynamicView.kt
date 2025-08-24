package com.kotlinjsonui.dynamic

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.components.*

/**
 * Main entry point for rendering dynamic UI from JSON.
 * This component determines the appropriate component type from JSON
 * and renders it with data binding support.
 * 
 * @param json The JSON object describing the UI component
 * @param data Map of data for binding. @{key} in JSON will be replaced with data[key].
 *             Functions in the data map can be referenced by name for event handlers.
 */
@Composable
fun DynamicView(
    json: JsonObject,
    data: Map<String, Any> = emptyMap()
) {
    // Get the component type from JSON
    val type = json.get("type")?.asString ?: return
    
    // Render the appropriate component based on type
    when (type.lowercase()) {
        "text", "label" -> {
            DynamicTextComponent.create(json, data)
        }
        "textfield" -> {
            DynamicTextFieldComponent.create(json, data)
        }
        "button" -> {
            DynamicButtonComponent.create(json, data)
        }
        "image" -> {
            DynamicImageComponent.create(json, data)
        }
        "networkimage" -> {
            DynamicNetworkImageComponent.create(json, data)
        }
        "circleimage" -> {
            DynamicCircleImageComponent.create(json, data)
        }
        "switch" -> {
            DynamicSwitchComponent.create(json, data)
        }
        "checkbox" -> {
            DynamicCheckBoxComponent.create(json, data)
        }
        "radio" -> {
            DynamicRadioComponent.create(json, data)
        }
        "slider" -> {
            DynamicSliderComponent.create(json, data)
        }
        "progress", "progressbar" -> {
            DynamicProgressComponent.create(json, data)
        }
        "indicator" -> {
            DynamicIndicatorComponent.create(json, data)
        }
        "selectbox", "spinner" -> {
            DynamicSelectBoxComponent.create(json, data)
        }
        "segment", "tablayout" -> {
            DynamicSegmentComponent.create(json, data)
        }
        "toggle" -> {
            DynamicToggleComponent.create(json, data)
        }
        "scrollview" -> {
            DynamicScrollViewComponent.create(json, data)
        }
        "hstack", "row" -> {
            DynamicHStackComponent.create(json, data)
        }
        "vstack", "column" -> {
            DynamicVStackComponent.create(json, data)
        }
        "zstack", "box" -> {
            DynamicZStackComponent.create(json, data)
        }
        "container", "view" -> {
            DynamicContainerComponent.create(json, data)
        }
        "safeareaview" -> {
            DynamicSafeAreaViewComponent.create(json, data)
        }
        "constraintlayout" -> {
            DynamicConstraintLayoutComponent.create(json, data)
        }
        "collection", "collectionview", "recyclerview", "grid", "lazygrid" -> {
            DynamicCollectionComponent.create(json, data)
        }
        "table", "listview" -> {
            DynamicTableComponent.create(json, data)
        }
        "webview" -> {
            DynamicWebViewComponent.create(json, data)
        }
        "web" -> {
            DynamicWebComponent.create(json, data)
        }
        "tabview" -> {
            DynamicTabViewComponent.create(json, data)
        }
        "gradientview" -> {
            DynamicGradientViewComponent.create(json, data)
        }
        "circleview" -> {
            DynamicCircleViewComponent.create(json, data)
        }
        "blurview" -> {
            DynamicBlurViewComponent.create(json, data)
        }
        "iconlabel" -> {
            DynamicIconLabelComponent.create(json, data)
        }
        "textview" -> {
            DynamicTextViewComponent.create(json, data)
        }
        "triangle" -> {
            DynamicTriangleComponent.create(json, data)
        }
        else -> {
            // Unknown component type - could log or render placeholder
        }
    }
}

/**
 * Renders a list of components from a JSON array.
 * Useful for rendering the contents of container components.
 */
@Composable
fun DynamicViews(
    components: List<JsonObject>,
    data: Map<String, Any> = emptyMap()
) {
    components.forEach { component ->
        DynamicView(component, data)
    }
}