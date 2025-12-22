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
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ColorParser
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
 */
class DynamicConstraintLayoutComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current

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
            
            // Parse background color
            val backgroundColor = json.get("background")?.asString?.let {
                ColorParser.parseColorString(it, context)
            }
            
            // Build modifier
            var modifier = ModifierBuilder.buildModifier(json, defaultFillMaxWidth = true)
            backgroundColor?.let {
                modifier = modifier.background(it)
            }
            
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
        
        private fun hasRelativePositioning(json: JsonObject): Boolean {
            val relativeAttrs = listOf(
                "alignTopOfView", "alignBottomOfView", "alignLeftOfView", "alignRightOfView",
                "alignTopView", "alignBottomView", "alignLeftView", "alignRightView",
                "alignCenterVerticalView", "alignCenterHorizontalView",
                "alignTop", "alignBottom", "alignLeft", "alignRight",
                "centerHorizontal", "centerVertical", "centerInParent"
            )
            
            return relativeAttrs.any { json.has(it) }
        }
        
        private fun applyRelativePositioning(
            json: JsonObject,
            setScope: ConstraintSetScope,
            scope: ConstrainScope,
            refs: Map<String, androidx.constraintlayout.compose.ConstrainedLayoutReference>
        ) {
            with(scope) {
                // Extract margins
                var topMargin = json.get("topMargin")?.asInt ?: 0
                var bottomMargin = json.get("bottomMargin")?.asInt ?: 0
                var leftMargin = json.get("leftMargin")?.asInt ?: 0
                var rightMargin = json.get("rightMargin")?.asInt ?: 0
                
                // Handle margins array
                json.get("margins")?.asJsonArray?.let { margins ->
                    if (margins.size() == 4) {
                        val arrayTopMargin = margins[0].asInt
                        val arrayRightMargin = margins[1].asInt
                        val arrayBottomMargin = margins[2].asInt
                        val arrayLeftMargin = margins[3].asInt
                        
                        // Use array values if individual margins not specified
                        if (!json.has("topMargin")) topMargin = arrayTopMargin
                        if (!json.has("bottomMargin")) bottomMargin = arrayBottomMargin
                        if (!json.has("leftMargin")) leftMargin = arrayLeftMargin
                        if (!json.has("rightMargin")) rightMargin = arrayRightMargin
                    }
                }
                
                // Position relative to other views
                json.get("alignTopOfView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        bottom.linkTo(targetRef.top, margin = bottomMargin.dp)
                    }
                }
                
                json.get("alignBottomOfView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        top.linkTo(targetRef.bottom, margin = topMargin.dp)
                    }
                }
                
                json.get("alignLeftOfView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        end.linkTo(targetRef.start, margin = rightMargin.dp)
                    }
                }
                
                json.get("alignRightOfView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        start.linkTo(targetRef.end, margin = leftMargin.dp)
                    }
                }
                
                // Align edges with other views
                json.get("alignTopView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        // For edge alignment, negative margin moves in expected direction
                        top.linkTo(targetRef.top, margin = if (topMargin > 0) (-topMargin).dp else 0.dp)
                    }
                }
                
                json.get("alignBottomView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        bottom.linkTo(targetRef.bottom, margin = if (bottomMargin > 0) (-bottomMargin).dp else 0.dp)
                    }
                }
                
                json.get("alignLeftView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        start.linkTo(targetRef.start, margin = if (leftMargin > 0) (-leftMargin).dp else 0.dp)
                    }
                }
                
                json.get("alignRightView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        end.linkTo(targetRef.end, margin = if (rightMargin > 0) (-rightMargin).dp else 0.dp)
                    }
                }
                
                // Center with other views
                json.get("alignCenterVerticalView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        top.linkTo(targetRef.top)
                        bottom.linkTo(targetRef.bottom)
                    }
                }
                
                json.get("alignCenterHorizontalView")?.asString?.let { targetId ->
                    refs[targetId]?.let { targetRef ->
                        start.linkTo(targetRef.start)
                        end.linkTo(targetRef.end)
                    }
                }
                
                // Parent constraints
                if (json.get("alignTop")?.asBoolean == true) {
                    top.linkTo(parent.top, margin = topMargin.dp)
                }
                
                if (json.get("alignBottom")?.asBoolean == true) {
                    bottom.linkTo(parent.bottom, margin = bottomMargin.dp)
                }
                
                if (json.get("alignLeft")?.asBoolean == true) {
                    start.linkTo(parent.start, margin = leftMargin.dp)
                }
                
                if (json.get("alignRight")?.asBoolean == true) {
                    end.linkTo(parent.end, margin = rightMargin.dp)
                }
                
                if (json.get("centerHorizontal")?.asBoolean == true) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                
                if (json.get("centerVertical")?.asBoolean == true) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                
                if (json.get("centerInParent")?.asBoolean == true) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            }
        }
        
        private fun applyDimensionConstraints(
            json: JsonObject,
            scope: ConstrainScope
        ) {
            with(scope) {
                // Width constraint
                json.get("width")?.let { widthElement ->
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
                json.get("height")?.let { heightElement ->
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