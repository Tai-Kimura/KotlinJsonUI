package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.ScrollViewAttributes
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.rememberTypedAttrs

/**
 * ScrollView component → LazyColumn / LazyRow.
 * Reference: scrollview_component.rb in kjui_tools.
 *
 * Scroll direction priority:
 *   1. horizontalScroll attribute
 *   2. orientation attribute
 *   3. First child View's orientation
 *
 * Attribute access goes through the generated [ScrollViewAttributes]
 * extraction via the [TypedAttrs] bridge; the node itself is only passed
 * wholesale to the shared ModifierBuilder pipeline.
 */
class DynamicScrollViewComponent {
    companion object {
        /** ScrollView-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "keyboardAvoidance", "scrollEnabled", "orientation"
        )

        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                ScrollViewAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "ScrollView", json,
                declared = ScrollViewAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            ModifierBuilder.ApplyLifecycleEffects(json, data)

            // Determine scroll direction
            val isHorizontal = determineScrollDirection(json, a)

            // Keyboard avoidance (default true)
            val keyboardAvoidance = a.keyboardAvoidance != false

            // Build modifier
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)
            if (keyboardAvoidance) {
                modifier = modifier.imePadding()
            }

            // scrollEnabled - controls whether user can scroll
            val scrollEnabled = TypedAttrs.boolean(a.scrollEnabled, data) ?: true

            // Get children
            val children = DynamicContainerComponent.getChildren(json)

            if (isHorizontal) {
                LazyRow(
                    modifier = modifier,
                    userScrollEnabled = scrollEnabled
                ) {
                    item {
                        children.forEach { child ->
                            DynamicView(child, data)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = modifier,
                    userScrollEnabled = scrollEnabled
                ) {
                    item {
                        children.forEach { child ->
                            DynamicView(child, data)
                        }
                    }
                }
            }
        }

        private fun determineScrollDirection(json: JsonObject, a: ScrollViewAttributes): Boolean {
            // 1. horizontalScroll attribute (highest priority;
            //    undeclared legacy runtime extra)
            TypedAttrs.undeclared(json, "horizontalScroll")?.let {
                return it.asBoolean
            }

            // 2. orientation attribute
            if (a.orientation != null) {
                return TypedAttrs.enumString(a.orientation) { it.json } == "horizontal"
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
