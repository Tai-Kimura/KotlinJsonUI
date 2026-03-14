package com.kotlinjsonui.dynamic.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.VisibilityWrapper
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.processDataBinding

/**
 * Container/View component → Column / Row / Box.
 * Reference: container_component.rb in kjui_tools.
 *
 * Layout determination:
 *   orientation: "vertical" → Column
 *   orientation: "horizontal" → Row
 *   no orientation → Box
 *
 * If any child has relative positioning attributes, delegates to ConstraintLayout.
 */
class DynamicContainerComponent {
    companion object {
        private val RELATIVE_ATTRS = listOf(
            "alignTopOfView", "alignBottomOfView", "alignLeftOfView", "alignRightOfView",
            "alignTopView", "alignBottomView", "alignLeftView", "alignRightView",
            "alignCenterVerticalView", "alignCenterHorizontalView"
        )

        @Composable
        fun create(json: JsonObject, data: Map<String, Any> = emptyMap()) {
            val context = LocalContext.current

            // Lifecycle effects
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            // Check for relative positioning → delegate to ConstraintLayout
            val children = getChildren(json)
            if (hasRelativePositioning(children)) {
                DynamicConstraintLayoutComponent.create(json, data)
                return
            }

            // Determine layout type
            val orientation = json.get("orientation")?.asString
            val layout = when (orientation) {
                "vertical" -> "Column"
                "horizontal" -> "Row"
                else -> "Box"
            }

            // Build modifier
            val modifier = ModifierBuilder.buildModifier(json, data, parentType = null, context = context)

            // Direction (reverse children order)
            val direction = json.get("direction")?.asString
            val orderedChildren = when {
                direction == "bottomToTop" && layout == "Column" -> children.reversed()
                direction == "rightToLeft" && layout == "Row" -> children.reversed()
                else -> children
            }

            when (layout) {
                "Column" -> createColumn(json, modifier, orderedChildren, data, context)
                "Row" -> createRow(json, modifier, orderedChildren, data, context)
                else -> createBox(json, modifier, orderedChildren, data, context)
            }
        }

        @Composable
        private fun createColumn(
            json: JsonObject,
            modifier: Modifier,
            children: List<JsonObject>,
            data: Map<String, Any>,
            context: Context
        ) {
            val verticalArrangement = parseVerticalArrangement(json)
            val horizontalAlignment = parseColumnHorizontalAlignment(json)

            Column(
                modifier = modifier,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment
            ) {
                children.forEach { child ->
                    renderChildInColumn(child, data, context)
                }
            }
        }

        @Composable
        private fun createRow(
            json: JsonObject,
            modifier: Modifier,
            children: List<JsonObject>,
            data: Map<String, Any>,
            context: Context
        ) {
            val horizontalArrangement = parseHorizontalArrangement(json)
            val verticalAlignment = parseRowVerticalAlignment(json)

            Row(
                modifier = modifier,
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = verticalAlignment
            ) {
                children.forEach { child ->
                    renderChildInRow(child, data, context)
                }
            }
        }

        @Composable
        private fun createBox(
            json: JsonObject,
            modifier: Modifier,
            children: List<JsonObject>,
            data: Map<String, Any>,
            context: Context
        ) {
            val contentAlignment = parseBoxContentAlignment(json)

            Box(
                modifier = modifier,
                contentAlignment = contentAlignment
            ) {
                children.forEach { child ->
                    renderChildInBox(child, data, context)
                }
            }
        }

        // ── Child rendering with scope-aware weight/alignment/visibility ──

        @Composable
        private fun ColumnScope.renderChildInColumn(
            child: JsonObject,
            data: Map<String, Any>,
            context: Context
        ) {
            val weight = ModifierBuilder.getWeight(child)
            val alignment = ModifierBuilder.getChildAlignment(child, "Column")
            val visibility = resolveVisibility(child, data, context)

            var childModifier: Modifier = Modifier
            if (weight != null) childModifier = childModifier.weight(weight)
            if (alignment is Alignment.Horizontal) childModifier = childModifier.align(alignment)

            if (visibility != null) {
                // weight + visibility gone guard: skip composition entirely
                if (weight != null && visibility.lowercase() == "gone") return
                VisibilityWrapper(visibility = visibility, modifier = childModifier) {
                    DynamicView(child, data)
                }
            } else if (childModifier != Modifier) {
                Box(modifier = childModifier) {
                    DynamicView(child, data)
                }
            } else {
                DynamicView(child, data)
            }
        }

        @Composable
        private fun RowScope.renderChildInRow(
            child: JsonObject,
            data: Map<String, Any>,
            context: Context
        ) {
            val weight = ModifierBuilder.getWeight(child)
            val alignment = ModifierBuilder.getChildAlignment(child, "Row")
            val visibility = resolveVisibility(child, data, context)

            var childModifier: Modifier = Modifier
            if (weight != null) childModifier = childModifier.weight(weight)
            if (alignment is Alignment.Vertical) childModifier = childModifier.align(alignment)

            if (visibility != null) {
                // weight + visibility gone guard: skip composition entirely
                if (weight != null && visibility.lowercase() == "gone") return
                VisibilityWrapper(visibility = visibility, modifier = childModifier) {
                    DynamicView(child, data)
                }
            } else if (childModifier != Modifier) {
                Box(modifier = childModifier) {
                    DynamicView(child, data)
                }
            } else {
                DynamicView(child, data)
            }
        }

        @Composable
        private fun BoxScope.renderChildInBox(
            child: JsonObject,
            data: Map<String, Any>,
            context: Context
        ) {
            val alignment = ModifierBuilder.getChildAlignment(child, "Box")
            val visibility = resolveVisibility(child, data, context)

            var childModifier: Modifier = Modifier
            if (alignment is Alignment) childModifier = childModifier.align(alignment)

            if (visibility != null) {
                VisibilityWrapper(visibility = visibility, modifier = childModifier) {
                    DynamicView(child, data)
                }
            } else if (childModifier != Modifier) {
                Box(modifier = childModifier) {
                    DynamicView(child, data)
                }
            } else {
                DynamicView(child, data)
            }
        }

        // ── Visibility resolution ──

        private fun resolveVisibility(
            child: JsonObject,
            data: Map<String, Any>,
            context: Context
        ): String? {
            val vis = child.get("visibility")?.asString ?: return null
            return processDataBinding(vis, data, context)
        }

        // ── Arrangement / Alignment parsing (matches container_component.rb) ──

        private fun parseVerticalArrangement(json: JsonObject): Arrangement.Vertical {
            val spacing = json.get("spacing")?.asFloat
            val distribution = json.get("distribution")?.asString
            val gravity = json.get("gravity")?.asString

            return when {
                spacing != null -> Arrangement.spacedBy(spacing.dp)
                distribution == "fillEqually" -> Arrangement.SpaceEvenly
                distribution == "fill" -> Arrangement.SpaceBetween
                distribution == "equalSpacing" -> Arrangement.SpaceAround
                distribution == "equalCentering" -> Arrangement.SpaceEvenly
                gravity == "top" -> Arrangement.Top
                gravity == "bottom" -> Arrangement.Bottom
                gravity == "centerVertical" || gravity == "center" -> Arrangement.Center
                else -> Arrangement.Top
            }
        }

        private fun parseColumnHorizontalAlignment(json: JsonObject): Alignment.Horizontal {
            return when (json.get("gravity")?.asString) {
                "left" -> Alignment.Start
                "right" -> Alignment.End
                "centerHorizontal", "center" -> Alignment.CenterHorizontally
                else -> Alignment.Start
            }
        }

        private fun parseHorizontalArrangement(json: JsonObject): Arrangement.Horizontal {
            val spacing = json.get("spacing")?.asFloat
            val distribution = json.get("distribution")?.asString
            val gravity = json.get("gravity")?.asString

            return when {
                spacing != null -> Arrangement.spacedBy(spacing.dp)
                distribution == "fillEqually" -> Arrangement.SpaceEvenly
                distribution == "fill" -> Arrangement.SpaceBetween
                distribution == "equalSpacing" -> Arrangement.SpaceAround
                distribution == "equalCentering" -> Arrangement.SpaceEvenly
                gravity == "left" -> Arrangement.Start
                gravity == "right" -> Arrangement.End
                gravity == "centerHorizontal" || gravity == "center" -> Arrangement.Center
                else -> Arrangement.Start
            }
        }

        private fun parseRowVerticalAlignment(json: JsonObject): Alignment.Vertical {
            return when (json.get("gravity")?.asString) {
                "top" -> Alignment.Top
                "bottom" -> Alignment.Bottom
                "centerVertical", "center" -> Alignment.CenterVertically
                else -> Alignment.Top
            }
        }

        private fun parseBoxContentAlignment(json: JsonObject): Alignment {
            return when (json.get("gravity")?.asString) {
                "center" -> Alignment.Center
                "centerHorizontal" -> Alignment.TopCenter
                "centerVertical" -> Alignment.CenterStart
                "top" -> Alignment.TopCenter
                "bottom" -> Alignment.BottomCenter
                "left" -> Alignment.CenterStart
                "right" -> Alignment.CenterEnd
                else -> Alignment.TopStart
            }
        }

        // ── Helpers ──

        fun getChildren(json: JsonObject): List<JsonObject> {
            val childElement = json.get("child") ?: json.get("children") ?: return emptyList()
            return when {
                childElement.isJsonArray -> {
                    childElement.asJsonArray.mapNotNull { e ->
                        if (e.isJsonObject) e.asJsonObject else null
                    }
                }
                childElement.isJsonObject -> listOf(childElement.asJsonObject)
                else -> emptyList()
            }
        }

        private fun hasRelativePositioning(children: List<JsonObject>): Boolean {
            return children.any { child ->
                RELATIVE_ATTRS.any { attr -> child.has(attr) }
            }
        }
    }
}
