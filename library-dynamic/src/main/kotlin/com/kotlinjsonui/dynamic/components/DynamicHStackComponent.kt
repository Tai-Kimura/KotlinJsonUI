package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject

/**
 * HStack/Row component – delegates to DynamicContainerComponent with orientation=horizontal.
 */
class DynamicHStackComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val modified = json.deepCopy().apply {
                addProperty("orientation", "horizontal")
            }
            DynamicContainerComponent.create(modified, data)
        }
    }
}
