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
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
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

                // Every child draws its margins as padding around its declared
                // size (DynamicView -> applyMargins), so its ref box includes
                // them: a sibling aligned to it adds the margin on that edge to
                // reach the box it DRAWS (applyRelativePositioning).
                val pads = mutableMapOf<String, ModifierBuilder.MarginPaddings>()
                childrenArray.forEachIndexed { index, childElement ->
                    if (childElement.isJsonObject) {
                        val childJson = childElement.asJsonObject
                        val id = childJson.get("id")?.asString ?: "view_$index"
                        ModifierBuilder.marginPaddings(childJson, data)?.let { pads[id] = it }
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
                            applyRelativePositioning(childJson, this@ConstraintSet, this, refs, pads, data)

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

        /**
         * Whether a child asks to be positioned relative to something.
         *
         * Decided BEFORE the data map is consulted, so a BINDING counts as
         * declared whatever its value resolves to: if the container does not
         * take this route, the constraint has nowhere to land, and fixing the
         * resolver downstream changes nothing. A literal `false` still does
         * not route — it is a statement that the node is NOT so positioned.
         * (Same rule F pinned on the ios dynamic path, `f248fbd`.)
         */
        private fun hasRelativePositioning(childNode: JsonObject): Boolean {
            val relativeAttrs = listOf(
                "alignTopOfView", "alignBottomOfView", "alignLeftOfView", "alignRightOfView",
                "alignTopView", "alignBottomView", "alignLeftView", "alignRightView",
                "alignCenterVerticalView", "alignCenterHorizontalView",
                "alignTop", "alignBottom", "alignLeft", "alignRight",
                "centerHorizontal", "centerVertical", "centerInParent"
            )

            return relativeAttrs.any { key ->
                val element = childNode.get(key) ?: return@any false
                val p = element.takeIf { it.isJsonPrimitive }?.asJsonPrimitive
                when {
                    p == null -> true
                    p.isBoolean -> p.asBoolean
                    p.isString && p.asString.equals("false", ignoreCase = true) -> false
                    else -> true
                }
            }
        }

        private fun applyRelativePositioning(
            childNode: JsonObject,
            setScope: ConstraintSetScope,
            scope: ConstrainScope,
            refs: Map<String, androidx.constraintlayout.compose.ConstrainedLayoutReference>,
            pads: Map<String, ModifierBuilder.MarginPaddings>,
            data: Map<String, Any>
        ) {
            // The anchor's padding-drawn margin on one edge (0 when none).
            fun pad(id: String, edge: (ModifierBuilder.MarginPaddings) -> Float): Float = pads[id]?.let(edge) ?: 0f
            with(scope) {
                // This child's own margins are drawn as padding around its
                // declared size (DynamicView -> applyMargins), so its ref box
                // already holds them: a link puts the BOX on the line and the
                // margin is the space between the box's edge and what it
                // draws. A link that added the margin again drew it twice
                // (alignTop/Bottom/Left/Right and align*OfView: the margin
                // twice), and one that took it away drew none (align*View).
                // Ticket kjui-dynamic-relative-container-bottom-margin-applied-twice
                // (family table 2026-11-10, 14 rows, against iOS, web and the
                // margins.offsetModel declaration).
                //
                // A centred axis takes no margin (margins.offsetModel: "an
                // explicit center declaration disables that axis margin"):
                // the box is centred between lines moved out by its own
                // margins, which puts what it draws at the centre. Centring
                // the box itself put it half a margin off (ticket
                // relative-center-with-margin-diverges-from-declaration).
                val own = ModifierBuilder.marginPaddings(childNode, data)
                val ownTop = own?.top ?: 0f
                val ownBottom = own?.bottom ?: 0f
                val ownStart = own?.start ?: 0f
                val ownEnd = own?.end ?: 0f

                // Position relative to other views
                childNode.get("alignTopOfView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        // The anchor's top margin is inside its ref box:
                        // `bottom.linkTo(a.top, m)` is a.top - m, the drawn
                        // top is a.top + its margin (ticket
                        // kjui-relative-align-view-measures-the-anchor-with-its-margin).
                        bottom.linkTo(targetRef.top, margin = (-pad(targetId) { it.top }).dp)
                    }
                }

                childNode.get("alignBottomOfView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        top.linkTo(targetRef.bottom, margin = (-pad(targetId) { it.bottom }).dp)
                    }
                }

                childNode.get("alignLeftOfView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        end.linkTo(targetRef.start, margin = (-pad(targetId) { it.start }).dp)
                    }
                }

                childNode.get("alignRightOfView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        start.linkTo(targetRef.end, margin = (-pad(targetId) { it.end }).dp)
                    }
                }

                // Align edges with other views: the box's edge on the
                // anchor's drawn edge; the own margin then sits inside it.
                childNode.get("alignTopView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        top.linkTo(targetRef.top, margin = pad(targetId) { it.top }.dp)
                    }
                }

                childNode.get("alignBottomView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        bottom.linkTo(targetRef.bottom, margin = pad(targetId) { it.bottom }.dp)
                    }
                }

                childNode.get("alignLeftView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        start.linkTo(targetRef.start, margin = pad(targetId) { it.start }.dp)
                    }
                }

                childNode.get("alignRightView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        end.linkTo(targetRef.end, margin = pad(targetId) { it.end }.dp)
                    }
                }

                // Center with other views: between the anchor's drawn edges,
                // moved out by the own margins.
                childNode.get("alignCenterVerticalView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        top.linkTo(targetRef.top, margin = (pad(targetId) { it.top } - ownTop).dp)
                        bottom.linkTo(targetRef.bottom, margin = (pad(targetId) { it.bottom } - ownBottom).dp)
                    }
                }

                childNode.get("alignCenterHorizontalView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        start.linkTo(targetRef.start, margin = (pad(targetId) { it.start } - ownStart).dp)
                        end.linkTo(targetRef.end, margin = (pad(targetId) { it.end } - ownEnd).dp)
                    }
                }

                // Parent constraints: the box on the parent's edge.
                if (ResourceResolver.resolveBoolean(childNode, "alignTop", data, default = false)) {
                    top.linkTo(parent.top)
                }

                if (ResourceResolver.resolveBoolean(childNode, "alignBottom", data, default = false)) {
                    bottom.linkTo(parent.bottom)
                }

                if (ResourceResolver.resolveBoolean(childNode, "alignLeft", data, default = false)) {
                    start.linkTo(parent.start)
                }

                if (ResourceResolver.resolveBoolean(childNode, "alignRight", data, default = false)) {
                    end.linkTo(parent.end)
                }

                val centerAll = ResourceResolver.resolveBoolean(childNode, "centerInParent", data, default = false)
                if (centerAll || ResourceResolver.resolveBoolean(childNode, "centerHorizontal", data, default = false)) {
                    start.linkTo(parent.start, margin = (-ownStart).dp)
                    end.linkTo(parent.end, margin = (-ownEnd).dp)
                }

                if (centerAll || ResourceResolver.resolveBoolean(childNode, "centerVertical", data, default = false)) {
                    top.linkTo(parent.top, margin = (-ownTop).dp)
                    bottom.linkTo(parent.bottom, margin = (-ownBottom).dp)
                }
            }
        }

        /** Margin keys — a margined child measures wider than its declared size. */
        private val MARGIN_KEYS = listOf(
            "topMargin", "bottomMargin", "leftMargin", "rightMargin",
            "startMargin", "endMargin", "marginTop", "marginBottom",
            "marginLeft", "marginRight", "marginStart", "marginEnd", "margins"
        )

        private fun applyDimensionConstraints(
            childNode: JsonObject,
            scope: ConstrainScope
        ) {
            // A margined child is rendered by DynamicView as padding AROUND the
            // declared size, so it occupies margin+size — exactly what the
            // codegen emits (`.padding(top=60).padding(start=60).width(50)`,
            // a 110dp-wide element whose visible box starts at 60).
            // Pinning the ref to the DECLARED size instead made `anchor.end`
            // land 60dp short, and every sibling linked to it followed
            // (common_alignRightOfView__static, d=95). Letting the ref wrap
            // its content restores both edges: `start` at 0, `end` at 110.
            val margined = MARGIN_KEYS.any { childNode.has(it) }
            with(scope) {
                if (margined) {
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                    return@with
                }
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
