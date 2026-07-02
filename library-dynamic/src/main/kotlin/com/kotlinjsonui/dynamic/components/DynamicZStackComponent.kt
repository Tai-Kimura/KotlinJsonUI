package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject

/**
 * ZStack/Box component – delegates to DynamicContainerComponent with no orientation (Box).
 *
 * Typed attribute parsing (ViewAttributes) and the UnappliedAttributes
 * check both happen inside DynamicContainerComponent; the "ZStack"
 * component-type label is passed through so warnings name this node type.
 */
class DynamicZStackComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            // Container with no orientation → Box
            val modified = json.deepCopy().apply {
                remove("orientation")
            }
            DynamicContainerComponent.create(modified, data, componentType = "ZStack")
        }
    }
}
