package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject

/**
 * ZStack/Box component – delegates to DynamicContainerComponent with no orientation (Box).
 */
class DynamicZStackComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            // Container with no orientation → Box
            val modified = json.deepCopy().apply {
                remove("orientation")
            }
            DynamicContainerComponent.create(modified, data)
        }
    }
}
