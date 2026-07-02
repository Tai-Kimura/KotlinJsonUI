package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject

/**
 * HStack/Row component – delegates to DynamicContainerComponent with orientation=horizontal.
 *
 * Typed attribute parsing (ViewAttributes) and the UnappliedAttributes
 * check both happen inside DynamicContainerComponent; the "HStack"
 * component-type label is passed through so warnings name this node type.
 */
class DynamicHStackComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val modified = json.deepCopy().apply {
                addProperty("orientation", "horizontal")
            }
            DynamicContainerComponent.create(modified, data, componentType = "HStack")
        }
    }
}
