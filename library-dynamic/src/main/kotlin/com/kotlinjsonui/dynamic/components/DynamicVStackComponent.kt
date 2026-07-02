package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject

/**
 * VStack/Column component – delegates to DynamicContainerComponent with orientation=vertical.
 *
 * Typed attribute parsing (ViewAttributes) and the UnappliedAttributes
 * check both happen inside DynamicContainerComponent; the "VStack"
 * component-type label is passed through so warnings name this node type.
 */
class DynamicVStackComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val modified = json.deepCopy().apply {
                addProperty("orientation", "vertical")
            }
            DynamicContainerComponent.create(modified, data, componentType = "VStack")
        }
    }
}
