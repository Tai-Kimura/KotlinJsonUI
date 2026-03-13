package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder

/**
 * ScrollView component → LazyColumn / LazyRow.
 * Reference: scrollview_component.rb in kjui_tools.
 *
 * Scroll direction priority:
 *   1. horizontalScroll attribute
 *   2. orientation attribute
 *   3. First child View's orientation
 */
class DynamicScrollViewComponent {
    companion object {
        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current

            ModifierBuilder.ApplyLifecycleEffects(json, data)

            // Determine scroll direction
            val isHorizontal = determineScrollDirection(json)

            // Keyboard avoidance (default true)
            val keyboardAvoidance = json.get("keyboardAvoidance")?.asBoolean != false

            // Build modifier
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)
            if (keyboardAvoidance) {
                modifier = modifier.imePadding()
            }

            // Get children
            val children = DynamicContainerComponent.getChildren(json)

            if (isHorizontal) {
                LazyRow(modifier = modifier) {
                    item {
                        children.forEach { child ->
                            DynamicView(child, data)
                        }
                    }
                }
            } else {
                LazyColumn(modifier = modifier) {
                    item {
                        children.forEach { child ->
                            DynamicView(child, data)
                        }
                    }
                }
            }
        }

        private fun determineScrollDirection(json: JsonObject): Boolean {
            // 1. horizontalScroll attribute (highest priority)
            if (json.has("horizontalScroll")) {
                return json.get("horizontalScroll").asBoolean
            }

            // 2. orientation attribute
            if (json.has("orientation")) {
                return json.get("orientation").asString == "horizontal"
            }

            // 3. First child View's orientation
            val children = DynamicContainerComponent.getChildren(json)
            val firstView = children.firstOrNull { it.get("type")?.asString == "View" }
            if (firstView != null) {
                return firstView.get("orientation")?.asString == "horizontal"
            }

            return false
        }
    }
}
