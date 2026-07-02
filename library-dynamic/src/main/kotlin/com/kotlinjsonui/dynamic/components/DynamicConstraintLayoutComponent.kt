package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.ViewAttributes
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.rememberTypedAttrs
import androidx.compose.ui.platform.LocalContext

/**
 * Dynamic ConstraintLayout Component Converter
 * Converts JSON to ConstraintLayout composable at runtime
 *
 * Supported JSON attributes:
 * - child/children: Array of child components with constraint definitions
 * - background: String hex color for background
 * - padding/margins: Spacing properties
 *
 * Child relative positioning attributes:
 * - id: String identifier for the component
 * - alignTopOfView/alignBottomOfView/alignLeftOfView/alignRightOfView: Position relative to another view's edge
 * - alignTopView/alignBottomView/alignLeftView/alignRightView: Align edges with another view
 * - alignCenterVerticalView/alignCenterHorizontalView: Center align with another view
 * - alignTop/alignBottom/alignLeft/alignRight: Align to parent edges
 * - centerHorizontal/centerVertical/centerInParent: Center in parent
 * - topMargin/bottomMargin/leftMargin/rightMargin: Margins for positioning
 * - margins: Array [top, right, bottom, left] for margins
 *
 * ConstraintLayout has no own attribute section: it parses/checks through
 * the shared `View` section ([ViewAttributes]). Every json.get read below
 * targets CHILD nodes (per-child constraint / dimension keys) or the
 * structural child/children arrays — those stay raw by design; the node
 * itself is only passed wholesale to the shared ModifierBuilder pipeline.
 */
class DynamicConstraintLayoutComponent {
    companion object {
        /**
         * ConstraintLayout-specific attributes this component applies (see
         * UnappliedAttributes): none beyond the common set — the constraint
         * keys (alignXxxView, margins, width/height) are read from CHILD
         * nodes, and child/children are structural.
         */
        private val APPLIED: Set<String> = emptySet()

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current
            // Typed parse for parity with the other converted components
            // (surfaces AttrWarnings for malformed own-node values); the
            // result is not consumed because all own-node reads here are
            // structural.
            rememberTypedAttrs(json) { m, canonicalOnly ->
                ViewAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "ConstraintLayout", json,
                declared = ViewAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Apply lifecycle effects first
            ModifierBuilder.ApplyLifecycleEffects(json, data)

            // Get children - support both 'child' and 'children'
            val childrenArray: JsonArray = when {
                json.has("children") && json.get("children").isJsonArray ->
                    json.getAsJsonArray("children")
                json.has("child") && json.get("child").isJsonArray ->
                    json.getAsJsonArray("child")
                json.has("child") && json.get("child").isJsonObject -> {
                    // Single child as object, wrap in array
                    JsonArray().apply { add(json.getAsJsonObject("child")) }
                }
                else -> JsonArray() // Empty array if no children
            }

            // Check if any child has relative positioning
            val hasConstraints = childrenArray.any { element ->
                if (element.isJsonObject) {
                    hasRelativePositioning(element.asJsonObject)
                } else false
            }

            if (!hasConstraints) {
                // No constraints, use regular Container instead
                DynamicContainerComponent.create(json, data)
                return
            }

            // Build modifier using composite buildModifier (handles background internally via applyBackground)
            val modifier = ModifierBuilder.buildModifier(json, data, context = context, defaultFillMaxWidth = true)

            // Create constraint set from children
            val constraintSet = ConstraintSet {
                // Create refs for all children
                val refs = mutableMapOf<String, androidx.constraintlayout.compose.ConstrainedLayoutReference>()
                childrenArray.forEachIndexed { index, childElement ->
                    if (childElement.isJsonObject) {
                        val childJson = childElement.asJsonObject
                        val id = childJson.get("id")?.asString ?: "view_$index"
                        refs[id] = createRefFor(id)
                    }
                }

                // Apply constraints to each child
                childrenArray.forEachIndexed { index, childElement ->
                    if (childElement.isJsonObject) {
                        val childJson = childElement.asJsonObject
                        val id = childJson.get("id")?.asString ?: "view_$index"
                        val ref = refs[id] ?: return@forEachIndexed

                        constrain(ref) {
                            // Apply relative positioning constraints
                            applyRelativePositioning(childJson, this@ConstraintSet, this, refs)

                            // Apply dimension constraints
                            applyDimensionConstraints(childJson, this)
                        }
                    }
                }
            }

            ConstraintLayout(
                constraintSet = constraintSet,
                modifier = modifier
            ) {
                childrenArray.forEachIndexed { index, childElement ->
                    if (childElement.isJsonObject) {
                        val childJson = childElement.asJsonObject
                        val id = childJson.get("id")?.asString ?: "view_$index"

                        Box(modifier = Modifier.layoutId(id)) {
                            DynamicView(
                                json = childJson,
                                data = data
                            )
                        }
                    }
                }
            }
        }

        private fun hasRelativePositioning(childNode: JsonObject): Boolean {
            val relativeAttrs = listOf(
                "alignTopOfView", "alignBottomOfView", "alignLeftOfView", "alignRightOfView",
                "alignTopView", "alignBottomView", "alignLeftView", "alignRightView",
                "alignCenterVerticalView", "alignCenterHorizontalView",
                "alignTop", "alignBottom", "alignLeft", "alignRight",
                "centerHorizontal", "centerVertical", "centerInParent"
            )

            return relativeAttrs.any { childNode.has(it) }
        }

        private fun applyRelativePositioning(
            childNode: JsonObject,
            setScope: ConstraintSetScope,
            scope: ConstrainScope,
            refs: Map<String, androidx.constraintlayout.compose.ConstrainedLayoutReference>
        ) {
            with(scope) {
                // Extract margins
                var topMargin = childNode.get("topMargin")?.asInt ?: 0
                var bottomMargin = childNode.get("bottomMargin")?.asInt ?: 0
                var leftMargin = childNode.get("leftMargin")?.asInt ?: 0
                var rightMargin = childNode.get("rightMargin")?.asInt ?: 0

                // Handle margins array
                childNode.get("margins")?.asJsonArray?.let { margins ->
                    if (margins.size() == 4) {
                        val arrayTopMargin = margins[0].asInt
                        val arrayRightMargin = margins[1].asInt
                        val arrayBottomMargin = margins[2].asInt
                        val arrayLeftMargin = margins[3].asInt

                        // Use array values if individual margins not specified
                        if (!childNode.has("topMargin")) topMargin = arrayTopMargin
                        if (!childNode.has("bottomMargin")) bottomMargin = arrayBottomMargin
                        if (!childNode.has("leftMargin")) leftMargin = arrayLeftMargin
                        if (!childNode.has("rightMargin")) rightMargin = arrayRightMargin
                    }
                }

                // Position relative to other views
                childNode.get("alignTopOfView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        bottom.linkTo(targetRef.top, margin = bottomMargin.dp)
                    }
                }

                childNode.get("alignBottomOfView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        top.linkTo(targetRef.bottom, margin = topMargin.dp)
                    }
                }

                childNode.get("alignLeftOfView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        end.linkTo(targetRef.start, margin = rightMargin.dp)
                    }
                }

                childNode.get("alignRightOfView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        start.linkTo(targetRef.end, margin = leftMargin.dp)
                    }
                }

                // Align edges with other views
                childNode.get("alignTopView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        // For edge alignment, negative margin moves in expected direction
                        top.linkTo(targetRef.top, margin = if (topMargin > 0) (-topMargin).dp else 0.dp)
                    }
                }

                childNode.get("alignBottomView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        bottom.linkTo(targetRef.bottom, margin = if (bottomMargin > 0) (-bottomMargin).dp else 0.dp)
                    }
                }

                childNode.get("alignLeftView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        start.linkTo(targetRef.start, margin = if (leftMargin > 0) (-leftMargin).dp else 0.dp)
                    }
                }

                childNode.get("alignRightView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        end.linkTo(targetRef.end, margin = if (rightMargin > 0) (-rightMargin).dp else 0.dp)
                    }
                }

                // Center with other views
                childNode.get("alignCenterVerticalView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        top.linkTo(targetRef.top)
                        bottom.linkTo(targetRef.bottom)
                    }
                }

                childNode.get("alignCenterHorizontalView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        start.linkTo(targetRef.start)
                        end.linkTo(targetRef.end)
                    }
                }

                // Parent constraints
                if (childNode.get("alignTop")?.asBoolean == true) {
                    top.linkTo(parent.top, margin = topMargin.dp)
                }

                if (childNode.get("alignBottom")?.asBoolean == true) {
                    bottom.linkTo(parent.bottom, margin = bottomMargin.dp)
                }

                if (childNode.get("alignLeft")?.asBoolean == true) {
                    start.linkTo(parent.start, margin = leftMargin.dp)
                }

                if (childNode.get("alignRight")?.asBoolean == true) {
                    end.linkTo(parent.end, margin = rightMargin.dp)
                }

                if (childNode.get("centerHorizontal")?.asBoolean == true) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }

                if (childNode.get("centerVertical")?.asBoolean == true) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }

                if (childNode.get("centerInParent")?.asBoolean == true) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            }
        }

        private fun applyDimensionConstraints(
            childNode: JsonObject,
            scope: ConstrainScope
        ) {
            with(scope) {
                // Width constraint
                childNode.get("width")?.let { widthElement ->
                    when {
                        widthElement.isJsonPrimitive -> {
                            val primitive = widthElement.asJsonPrimitive
                            when {
                                primitive.isString -> {
                                    when (primitive.asString) {
                                        "matchConstraint", "0dp" -> width = Dimension.fillToConstraints
                                        "wrapContent", "wrap_content" -> width = Dimension.wrapContent
                                        "matchParent", "match_parent" -> width = Dimension.matchParent
                                    }
                                }
                                primitive.isNumber -> {
                                    width = Dimension.value(primitive.asFloat.dp)
                                }
                            }
                        }
                    }
                } ?: run {
                    width = Dimension.wrapContent
                }

                // Height constraint
                childNode.get("height")?.let { heightElement ->
                    when {
                        heightElement.isJsonPrimitive -> {
                            val primitive = heightElement.asJsonPrimitive
                            when {
                                primitive.isString -> {
                                    when (primitive.asString) {
                                        "matchConstraint", "0dp" -> height = Dimension.fillToConstraints
                                        "wrapContent", "wrap_content" -> height = Dimension.wrapContent
                                        "matchParent", "match_parent" -> height = Dimension.matchParent
                                    }
                                }
                                primitive.isNumber -> {
                                    height = Dimension.value(primitive.asFloat.dp)
                                }
                            }
                        }
                    }
                } ?: run {
                    height = Dimension.wrapContent
                }
            }
        }
    }
}
