package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject

/**
 * VStack/Column component – delegates to DynamicContainerComponent with orientation=vertical.
 */
class DynamicVStackComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val modified = json.deepCopy().apply {
                addProperty("orientation", "vertical")
            }
            DynamicContainerComponent.create(modified, data)
        }
    }
}
