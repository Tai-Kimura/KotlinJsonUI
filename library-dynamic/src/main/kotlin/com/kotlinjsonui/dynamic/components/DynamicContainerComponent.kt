package com.kotlinjsonui.dynamic.components

import com.kotlinjsonui.dynamic.generated.CommonAttributes
import com.kotlinjsonui.core.DeclaredSpelling
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import com.kotlinjsonui.components.DistributionFillColumn
import com.kotlinjsonui.components.DistributionFillRow
import com.kotlinjsonui.components.VisibilityWrapper
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.LocalSafeAreaConfig
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.ViewAttributes
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.SafeAreaEdges
import com.kotlinjsonui.dynamic.processDataBinding
import com.kotlinjsonui.dynamic.rememberTypedAttrs

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
 *
 * Attribute access goes through the generated [ViewAttributes] extraction
 * (typed, alias-aware, L1-marker-aware) via the [TypedAttrs] bridge.
 * Children iteration, per-child reads (weight / alignment / visibility)
 * and whole-node ModifierBuilder passes are structural and keep reading
 * the gson node directly.
 *
 * The HStack / VStack / ZStack wrappers delegate here with their own
 * [componentType] label so UnappliedAttributes warnings name the node's
 * actual component type.
 */
class DynamicContainerComponent {
    companion object {
        private val RELATIVE_ATTRS = listOf(
            "alignTopOfView", "alignBottomOfView", "alignLeftOfView", "alignRightOfView",
            "alignTopView", "alignBottomView", "alignLeftView", "alignRightView",
            "alignCenterVerticalView", "alignCenterHorizontalView"
        )

        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap(),
            componentType: String = "View"
        ) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                ViewAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                componentType, json,
                declared = ViewAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // Check for relative positioning → delegate to ConstraintLayout
            val children = getChildren(json)
            if (hasRelativePositioning(children)) {
                DynamicConstraintLayoutComponent.create(json, data)
                return
            }

            // Determine layout type (exact-spelling match as in the legacy reader)
            val orientation = TypedAttrs.enumString(a.orientation) { it.json }
            val layout = when (orientation) {
                "vertical" -> "Column"
                "horizontal" -> "Row"
                else -> "Box"
            }

            // Build modifier
            var modifier = ModifierBuilder.buildModifier(json, data, parentType = null, context = context)
            // A fixed-size row whose children all declare their size along it
            // lays them out in sequence past its edge (ruling S); see
            // mainAxisOverflowWrapper.
            mainAxisOverflowWrapper(json, a, layout, children)?.let { modifier = modifier.then(it) }

            // `safeAreaInsetPositions` is declared on View as well as on
            // SafeAreaView — the SSoT says so explicitly, because SafeAreaView
            // is its own definition section and does not inherit View's. Only
            // the SafeAreaView component read it, so a plain View naming the
            // edges reserved nothing. Unlike SafeAreaView there is NO default:
            // a View that says nothing reserves nothing.
            SafeAreaEdges.requested(
                json, a.safeAreaInsetPositions,
                ViewAttributes.SafeAreaInsetPositions.declaredSpellings, "View.safeAreaInsetPositions"
            )?.let { requested ->
                val cfg = LocalSafeAreaConfig.current
                modifier = SafeAreaEdges.apply(
                    modifier,
                    SafeAreaEdges.filtered(
                        requested,
                        ignoreTop = cfg.ignoreTop,
                        ignoreBottom = cfg.ignoreBottom
                    )
                )
            }

            // Direction (reverse children order)
            val direction = TypedAttrs.enumString(a.direction) { it.json }
            val orderedChildren = when {
                direction == "bottomToTop" && layout == "Column" -> children.reversed()
                direction == "rightToLeft" && layout == "Row" -> children.reversed()
                else -> children
            }

            if (direction == "rightToLeft" && layout == "Column") {
                // RTL on a column mirrors the inline axis — children anchor
                // to the trailing edge (ios renders it so; 33 cross-effect:
                // android ignored rightToLeft on columns). Row keeps the
                // children-reversal path above.
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.ui.platform.LocalLayoutDirection provides
                        androidx.compose.ui.unit.LayoutDirection.Rtl
                ) {
                    createColumn(json, a, modifier, orderedChildren, data, context)
                }
            } else when (layout) {
                "Column" -> createColumn(json, a, modifier, orderedChildren, data, context)
                "Row" -> createRow(json, a, modifier, orderedChildren, data, context)
                else -> createBox(json, modifier, orderedChildren, data, context)
            }
        }

        @Composable
        private fun createColumn(
            json: JsonObject,
            a: ViewAttributes,
            modifier: Modifier,
            children: List<JsonObject>,
            data: Map<String, Any>,
            context: Context
        ) {
            // `distribution: fill` is not an arrangement and not a weight —
            // children grow from their content to consume the axis
            // (flex-grow, auto basis). Compose's weight cannot express it
            // (weight(1f) IS fillEqually; fill=false just packs, which went
            // inert against the control), so both renderers call the one
            // measurement policy in the base library. `spacing` still pins
            // the gap; a child that declares its own size on this axis keeps
            // it (explicit > fill).
            if (distributionOf(a) == "fill") {
                DistributionFillColumn(
                    modifier = modifier,
                    gap = (TypedAttrs.float(a.spacing, data) ?: 0f).dp,
                    grows = children.map { !it.has("height") }
                ) {
                    children.forEach { child -> renderChildPlain(child, data, context) }
                }
                return
            }

            val verticalArrangement = parseVerticalArrangement(a, json, data)
            val horizontalAlignment = parseColumnHorizontalAlignment(json)

            Column(
                modifier = modifier,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment
            ) {
                val flags = ModifierBuilder.resolvedAlignFlags(json)
                val mainBias = if (stacksFromTheBottom(json, a)) 1f else axisBias(flags.alignTop, flags.alignBottom, flags.centerV || flags.centerInParent)
                children.forEach { child ->
                    renderChildInColumn(child, data, context, distributionOf(a), OverflowPlacement(horizontalAlignment, mainBias))
                }
            }
        }

        @Composable
        private fun createRow(
            json: JsonObject,
            a: ViewAttributes,
            modifier: Modifier,
            children: List<JsonObject>,
            data: Map<String, Any>,
            context: Context
        ) {
            // Same fill policy as createColumn — see the comment there.
            if (distributionOf(a) == "fill") {
                DistributionFillRow(
                    modifier = modifier,
                    gap = (TypedAttrs.float(a.spacing, data) ?: 0f).dp,
                    grows = children.map { !it.has("width") }
                ) {
                    children.forEach { child -> renderChildPlain(child, data, context) }
                }
                return
            }

            val horizontalArrangement = parseHorizontalArrangement(a, json, data)
            val verticalAlignment = parseRowVerticalAlignment(json)

            Row(
                modifier = modifier,
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = verticalAlignment
            ) {
                val flags = ModifierBuilder.resolvedAlignFlags(json)
                val mainBias = if (stacksFromTheEnd(json, a)) 1f else axisBias(flags.alignLeft, flags.alignRight, flags.centerH || flags.centerInParent)
                children.forEach { child ->
                    renderChildInRow(child, data, context, distributionOf(a), OverflowPlacement(verticalAlignment, mainBias))
                }
            }
        }

        /**
         * Scope-free child render for the fill layouts: visibility still
         * wraps, but weight/align are RowScope/ColumnScope concerns that the
         * measurement policy replaces.
         */
        @Composable
        private fun renderChildPlain(child: JsonObject, data: Map<String, Any>, context: Context) {
            val visibility = resolveVisibility(child, data, context)
            if (visibility != null) {
                VisibilityWrapper(visibility = visibility) { DynamicView(child, data) }
            } else {
                DynamicView(child, data)
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
                    renderChildInBox(child, data, context, contentAlignment)
                }
            }
        }

        // ── Child rendering with scope-aware weight/alignment/visibility ──

        @Composable
        internal fun ColumnScope.renderChildInColumn(
            child: JsonObject,
            data: Map<String, Any>,
            context: Context,
            distribution: String? = null,
            overflow: OverflowPlacement<Alignment.Horizontal>? = null
        ) {
            // `fill` and `fillEqually` distribute SIZE among the children, so
            // they are weights — not an Arrangement (49-E: "mapping `fill` to
            // SpaceBetween is precisely backwards: fill means there is no free
            // space left to distribute"). `fill` lets a child stay smaller
            // than its share, `fillEqually` forces every child to the same
            // size. An explicit `weight` on the child still wins.
            val distributedWeight = when (distribution) {
                "fill" -> 1f
                // A child that declares its own height keeps it and takes no
                // share; the others split what is left equally (user ruling,
                // 2026-10-05: 60 / 120 / 120, not 60 drawn in an equal 100
                // slot with 40 empty).
                "fillEqually" -> if (declaresSizeAlong(child, "height")) null else 1f
                else -> null
            }
            // A weight the CHILD declares owns that axis and overrides the
            // child's own width/height there — web renders the weighted child
            // full-width for `weight: 1, width: 200` (run 4 artifacts: 1049dp
            // of a 1049dp row) and so does the kjui codegen, which puts weight
            // and size on ONE node where Compose's `weight(fill = true)` wins.
            // The dynamic path wraps instead, so the child kept painting its
            // declared 200dp inside a full-width Box — `common_weight__*`,
            // three android parity deviations at distance 18.
            //
            // A weight that came from the CONTAINER's `distribution` does NOT
            // override it: 49-E ruled explicit child size beats distribution's
            // size half. Which is why these two are read apart rather than
            // collapsed into one nullable.
            val declaredWeight = ModifierBuilder.getWeight(child, data, "Column")
            val weight = declaredWeight ?: distributedWeight
            val alignment = ModifierBuilder.getChildAlignment(child, "Column", data)
            val visibility = resolveVisibility(child, data, context)

            var childModifier: Modifier = Modifier
            if (weight != null) {
                childModifier = childModifier.weight(
                    weight,
                    fill = distribution != "fill" || declaredWeight != null
                )
            }
            if (alignment is Alignment.Horizontal) childModifier = childModifier.align(alignment)

            // When weight is present, inject fillMaxHeight into the child JSON
            // so the child component fills the weighted space (matching static tool behavior)
            // The SIZE half of `distribution` splits here, and this decision is
            // what run 4's value-vs-value gate measured as "android collapses
            // fill with fillEqually" (0px apart, and the canon requires them
            // to differ: fill grows children from their content, fillEqually
            // makes them equal regardless of it). Injecting matchParent into a
            // `fill` child defeats `weight(fill = false)` — the child fills
            // its track and draws exactly what fillEqually draws. So the
            // inject serves declared weights and fillEqually only; a `fill`
            // child keeps its content size inside its equal track.
            val effectiveChild = (if (weight != null && childFillsItsTrack(declaredWeight, distribution)) {
                injectFillSize(child, fillHeight = true, fillWidth = false, override = declaredWeight != null)
            } else child).let { c ->
                if (overflow == null) c
                else withOverflowBias(c, columnBias(alignment, overflow.crossAlignment), overflow.mainBias)
            }

            if (visibility != null) {
                // weight + visibility gone guard: skip composition entirely
                if (weight != null && DeclaredSpelling.lowered(visibility, CommonAttributes.Visibility.declaredSpellings, "common.visibility") == "gone") return
                VisibilityWrapper(visibility = visibility, modifier = childModifier) {
                    DynamicView(effectiveChild, data)
                }
            } else if (childModifier != Modifier) {
                Box(modifier = childModifier) {
                    DynamicView(effectiveChild, data)
                }
            } else {
                DynamicView(effectiveChild, data)
            }
        }

        @Composable
        internal fun RowScope.renderChildInRow(
            child: JsonObject,
            data: Map<String, Any>,
            context: Context,
            distribution: String? = null,
            overflow: OverflowPlacement<Alignment.Vertical>? = null
        ) {
            // `fill` and `fillEqually` distribute SIZE among the children, so
            // they are weights — not an Arrangement (49-E: "mapping `fill` to
            // SpaceBetween is precisely backwards: fill means there is no free
            // space left to distribute"). `fill` lets a child stay smaller
            // than its share, `fillEqually` forces every child to the same
            // size. An explicit `weight` on the child still wins.
            val distributedWeight = when (distribution) {
                "fill" -> 1f
                // A child that declares its own width keeps it and takes no
                // share; the others split what is left equally (user ruling,
                // 2026-10-05: 60 / 120 / 120, not 60 drawn in an equal 100
                // slot with 40 empty).
                "fillEqually" -> if (declaresSizeAlong(child, "width")) null else 1f
                else -> null
            }
            // A weight the CHILD declares owns that axis and overrides the
            // child's own width/height there — web renders the weighted child
            // full-width for `weight: 1, width: 200` (run 4 artifacts: 1049dp
            // of a 1049dp row) and so does the kjui codegen, which puts weight
            // and size on ONE node where Compose's `weight(fill = true)` wins.
            // The dynamic path wraps instead, so the child kept painting its
            // declared 200dp inside a full-width Box — `common_weight__*`,
            // three android parity deviations at distance 18.
            //
            // A weight that came from the CONTAINER's `distribution` does NOT
            // override it: 49-E ruled explicit child size beats distribution's
            // size half. Which is why these two are read apart rather than
            // collapsed into one nullable.
            val declaredWeight = ModifierBuilder.getWeight(child, data, "Row")
            val weight = declaredWeight ?: distributedWeight
            val alignment = ModifierBuilder.getChildAlignment(child, "Row", data)
            val visibility = resolveVisibility(child, data, context)

            var childModifier: Modifier = Modifier
            if (weight != null) {
                childModifier = childModifier.weight(
                    weight,
                    fill = distribution != "fill" || declaredWeight != null
                )
            }
            if (alignment is Alignment.Vertical) childModifier = childModifier.align(alignment)

            // When weight is present, inject fillMaxWidth into the child JSON
            // The SIZE half of `distribution` splits here, and this decision is
            // what run 4's value-vs-value gate measured as "android collapses
            // fill with fillEqually" (0px apart, and the canon requires them
            // to differ: fill grows children from their content, fillEqually
            // makes them equal regardless of it). Injecting matchParent into a
            // `fill` child defeats `weight(fill = false)` — the child fills
            // its track and draws exactly what fillEqually draws. So the
            // inject serves declared weights and fillEqually only; a `fill`
            // child keeps its content size inside its equal track.
            val effectiveChild = (if (weight != null && childFillsItsTrack(declaredWeight, distribution)) {
                injectFillSize(child, fillHeight = false, fillWidth = true, override = declaredWeight != null)
            } else child).let { c ->
                if (overflow == null) c
                else withOverflowBias(c, overflow.mainBias, rowBias(alignment, overflow.crossAlignment))
            }

            if (visibility != null) {
                // weight + visibility gone guard: skip composition entirely
                if (weight != null && DeclaredSpelling.lowered(visibility, CommonAttributes.Visibility.declaredSpellings, "common.visibility") == "gone") return
                VisibilityWrapper(visibility = visibility, modifier = childModifier) {
                    DynamicView(effectiveChild, data)
                }
            } else if (childModifier != Modifier) {
                Box(modifier = childModifier) {
                    DynamicView(effectiveChild, data)
                }
            } else {
                DynamicView(effectiveChild, data)
            }
        }

        @Composable
        internal fun BoxScope.renderChildInBox(
            child: JsonObject,
            data: Map<String, Any>,
            context: Context,
            contentAlignment: Alignment? = null
        ) {
            val alignment = ModifierBuilder.getChildAlignment(child, "Box", data)
            val visibility = resolveVisibility(child, data, context)
            val drawn = if (contentAlignment == null) child else boxBias(alignment, contentAlignment).let { (h, v) ->
                withOverflowBias(child, h, v)
            }

            var childModifier: Modifier = Modifier
            if (alignment is Alignment) childModifier = childModifier.align(alignment)

            if (visibility != null) {
                VisibilityWrapper(visibility = visibility, modifier = childModifier) {
                    DynamicView(drawn, data)
                }
            } else if (childModifier != Modifier) {
                Box(modifier = childModifier) {
                    DynamicView(drawn, data)
                }
            } else {
                DynamicView(drawn, data)
            }
        }

        // ── Where the container places an over-constrained child ──
        //
        // A child declaring a numeric size larger than its container was
        // coerced and centred by requiredWidth/requiredHeight, whatever the
        // container said (jsonui-cli ticket kjui-oversized-child-is-centred-
        // and-cut-to-its-parent). The container hands the child, per axis,
        // where it places it; the size stage (ModifierBuilder.declaredSize)
        // anchors the declared box there. The kjui codegen computes the same
        // biases (container_component.rb overflow_bias).

        /**
         * A fixed-size Row / Column whose children all declare a numeric size
         * along its axis measures them unbounded along it, innermost in its
         * chain, anchored by its gravity. Compose measures a Row's children
         * against the space left, so the child that crosses the edge was
         * coerced to what was left and every child after it moved up — six
         * 40-wide boxes in a 200 row with padding 8 put box_f at 172 where
         * web and iOS put it at 208 (user ruling S, 2026-10-05: children of a
         * fixed-size row are placed in sequence and overflow past the edge).
         * Content that fits reads the same as before: the wrapper reports the
         * declared size and places the content where the gravity would.
         * Not with a distribution (it needs the free space) nor with any child
         * sized by weight, fill or content, all of which need the bound. The
         * kjui codegen emits the same (container_component.rb
         * main_axis_overflow_wrapper).
         */
        internal fun mainAxisOverflowWrapper(json: JsonObject, a: ViewAttributes, layout: String, children: List<JsonObject>): Modifier? {
            if (layout != "Row" && layout != "Column") return null
            val axis = if (layout == "Row") "width" else "height"
            if (distributionOf(a) != null || children.isEmpty()) return null
            if (!isNumericSize(TypedAttrs.rawKey(json, axis))) return null
            val weights = listOf("weight", "widthWeight", "heightWeight")
            if (children.any { c -> weights.any { TypedAttrs.rawKey(c, it) != null } || !isNumericSize(TypedAttrs.rawKey(c, axis)) }) return null
            val flags = ModifierBuilder.resolvedAlignFlags(json)
            return if (layout == "Row") {
                Modifier.wrapContentWidth(
                    align = androidx.compose.ui.BiasAlignment.Horizontal(
                        if (stacksFromTheEnd(json, a)) 1f else axisBias(flags.alignLeft, flags.alignRight, flags.centerH || flags.centerInParent)
                    ),
                    unbounded = true
                )
            } else {
                Modifier.wrapContentHeight(
                    align = androidx.compose.ui.BiasAlignment.Vertical(
                        if (stacksFromTheBottom(json, a)) 1f else axisBias(flags.alignTop, flags.alignBottom, flags.centerV || flags.centerInParent)
                    ),
                    unbounded = true
                )
            }
        }

        /** A numeric size declared along [axis] ("width" / "height"). */
        internal fun declaresSizeAlong(child: JsonObject, axis: String): Boolean = isNumericSize(TypedAttrs.rawKey(child, axis))

        private fun isNumericSize(e: com.google.gson.JsonElement?): Boolean =
            e != null && e.isJsonPrimitive && (e.asJsonPrimitive.isNumber ||
                (e.asJsonPrimitive.isString && (e.asString.toFloatOrNull() ?: -1f) >= 0f))

        /** The cross-axis alignment a Column / Row gives its children, and the main-axis bias of its gravity. */
        internal data class OverflowPlacement<A>(val crossAlignment: A, val mainBias: Float)

        /** −1 start, 1 end, 0 centre, start when the gravity names none (gravityDefaults). */
        internal fun axisBias(start: Boolean, end: Boolean, center: Boolean): Float = when {
            start -> -1f
            end -> 1f
            center -> 0f
            else -> -1f
        }

        /** A Box child: its own placement if it declares one, else the Box's contentAlignment. */
        internal fun boxBias(childAlignment: Any?, contentAlignment: Alignment): Pair<Float, Float> {
            val a = (childAlignment as? Alignment) ?: contentAlignment
            return (a as? androidx.compose.ui.BiasAlignment)?.let { it.horizontalBias to it.verticalBias } ?: (-1f to -1f)
        }

        /** A Column child's horizontal bias: its own alignment, else the Column's horizontalAlignment. */
        internal fun columnBias(childAlignment: Any?, horizontalAlignment: Alignment.Horizontal): Float =
            (((childAlignment as? Alignment.Horizontal) ?: horizontalAlignment) as? androidx.compose.ui.BiasAlignment.Horizontal)?.bias ?: -1f

        /** A Row child's vertical bias: its own alignment, else the Row's verticalAlignment. */
        internal fun rowBias(childAlignment: Any?, verticalAlignment: Alignment.Vertical): Float =
            (((childAlignment as? Alignment.Vertical) ?: verticalAlignment) as? androidx.compose.ui.BiasAlignment.Vertical)?.bias ?: -1f

        /**
         * The child with the container's placement injected, when it declares a
         * numeric width or height (or a frame) — the only nodes the size stage
         * wraps. A shallow copy: the child's own entries are shared.
         */
        internal fun withOverflowBias(child: JsonObject, h: Float, v: Float): JsonObject {
            if (!declaresNumericSize(child)) return child
            val copy = JsonObject()
            for ((key, value) in child.entrySet()) copy.add(key, value)
            copy.add(ModifierBuilder.OVERFLOW_BIAS_KEY, com.google.gson.JsonArray().apply { add(h); add(v) })
            return copy
        }

        private fun declaresNumericSize(child: JsonObject): Boolean {
            if (TypedAttrs.rawKey(child, "frame")?.isJsonObject == true) return true
            return listOf("width", "height").any { key -> isNumericSize(TypedAttrs.rawKey(child, key)) }
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

        /**
         * The declared `distribution`, or null.
         *
         * Split by KIND (49-E ruling): `fill` / `fillEqually` distribute SIZE
         * and become child weights; `equalSpacing` / `equalCentering`
         * distribute FREE SPACE and become an Arrangement. Every platform
         * conflated the two kinds, and each collapsed a DIFFERENT pair into one
         * output — which is why no fixture comparing two declared values could
         * tell them apart.
         */
        /**
         * Whether a weighted child is stretched to fill its track.
         *
         * The SIZE half of `distribution` splits exactly here: `fillEqually`
         * makes every child its share regardless of content, while `fill`
         * keeps the child content-sized inside its equal track — injecting
         * matchParent into a `fill` child defeats `weight(fill = false)` and
         * draws precisely what fillEqually draws, which is what run 4's
         * value-vs-value gate measured as "android collapses fill with
         * fillEqually" (0px apart). A weight the child DECLARES always
         * stretches: that axis is its to own (f33e66c).
         */
        internal fun childFillsItsTrack(declaredWeight: Float?, distribution: String?): Boolean =
            declaredWeight != null || distribution != "fill"

        internal fun distributionOf(a: ViewAttributes): String? =
            TypedAttrs.enumString(a.distribution) { it.json }

        internal fun parseVerticalArrangement(
            a: ViewAttributes,
            json: JsonObject,
            data: Map<String, Any>
        ): Arrangement.Vertical {
            val spacing = TypedAttrs.float(a.spacing, data)
            val distribution = TypedAttrs.enumString(a.distribution) { it.json }
            val flags = ModifierBuilder.resolvedAlignFlags(json)
            val bottomUp = stacksFromTheBottom(json, a)

            return when {
                // An explicit `spacing` pins the GAP, so it overrides the gap
                // the free-space values would compute; it says nothing about
                // SIZE, so fill/fillEqually still apply as child weights
                // underneath it (49-E: "the more specific declaration wins the
                // axis it speaks about, and only that axis").
                spacing != null -> if (bottomUp) Arrangement.spacedBy(spacing.dp, Alignment.Bottom) else Arrangement.spacedBy(spacing.dp)
                // `equalSpacing` = equal gaps between adjacent children, with
                // no leading or trailing gap.
                distribution == "equalSpacing" -> Arrangement.SpaceBetween
                // `equalCentering` = equal CENTRE-TO-CENTRE distances, which is
                // each child centred in an equal track — SpaceAround's model.
                // SpaceEvenly is NOT it: equal gaps everywhere leaves the
                // outer children off-centre in their tracks. A named this on
                // the web side (`justify-around`, base_converter.rb) and cited
                // Compose's SpaceAround, so the three platforms agree; the
                // whole point of the ruling is that the four values stop
                // collapsing differently per platform.
                distribution == "equalCentering" -> Arrangement.SpaceAround
                flags.alignTop -> Arrangement.Top
                flags.alignBottom -> Arrangement.Bottom
                flags.centerV || flags.centerInParent -> Arrangement.Center
                bottomUp -> Arrangement.Bottom
                else -> Arrangement.Top
            }
        }

        /**
         * `direction: bottomToTop` stacks from the bottom edge: the first child
         * at the bottom (user ruling, 2026-10-05). Reversing the children
         * alone stacked them from the top. Not when the gravity names a
         * vertical place or a distribution spreads them: those say where they
         * go. The kjui codegen: container_component.rb bottom_up?.
         */
        internal fun stacksFromTheBottom(json: JsonObject, a: ViewAttributes): Boolean {
            if (TypedAttrs.enumString(a.direction) { it.json } != "bottomToTop") return false
            if (distributionOf(a) != null) return false
            val f = ModifierBuilder.resolvedAlignFlags(json)
            return !(f.alignTop || f.alignBottom || f.centerV || f.centerInParent)
        }


        /** A Column's content gravity across: also a vertical ScrollView's (DynamicScrollViewComponent). */
        internal fun parseColumnHorizontalAlignment(json: JsonObject): Alignment.Horizontal {
            val flags = ModifierBuilder.resolvedAlignFlags(json)
            return when {
                flags.alignLeft -> Alignment.Start
                flags.alignRight -> Alignment.End
                flags.centerH || flags.centerInParent -> Alignment.CenterHorizontally
                else -> Alignment.Start
            }
        }

        internal fun parseHorizontalArrangement(
            a: ViewAttributes,
            json: JsonObject,
            data: Map<String, Any>
        ): Arrangement.Horizontal {
            val spacing = TypedAttrs.float(a.spacing, data)
            val distribution = TypedAttrs.enumString(a.distribution) { it.json }
            val flags = ModifierBuilder.resolvedAlignFlags(json)
            val endFirst = stacksFromTheEnd(json, a)

            return when {
                // An explicit `spacing` pins the GAP, so it overrides the gap
                // the free-space values would compute; it says nothing about
                // SIZE, so fill/fillEqually still apply as child weights
                // underneath it (49-E: "the more specific declaration wins the
                // axis it speaks about, and only that axis").
                spacing != null -> if (endFirst) Arrangement.spacedBy(spacing.dp, Alignment.End) else Arrangement.spacedBy(spacing.dp)
                // `equalSpacing` = equal gaps between adjacent children, with
                // no leading or trailing gap.
                distribution == "equalSpacing" -> Arrangement.SpaceBetween
                // `equalCentering` = equal CENTRE-TO-CENTRE distances, which is
                // each child centred in an equal track — SpaceAround's model.
                // SpaceEvenly is NOT it: equal gaps everywhere leaves the
                // outer children off-centre in their tracks. A named this on
                // the web side (`justify-around`, base_converter.rb) and cited
                // Compose's SpaceAround, so the three platforms agree; the
                // whole point of the ruling is that the four values stop
                // collapsing differently per platform.
                distribution == "equalCentering" -> Arrangement.SpaceAround
                flags.alignLeft -> Arrangement.Start
                flags.alignRight -> Arrangement.End
                flags.centerH || flags.centerInParent -> Arrangement.Center
                endFirst -> Arrangement.End
                else -> Arrangement.Start
            }
        }

        /**
         * `direction: rightToLeft` stacks a Row from its right edge: the first
         * child rightmost (user ruling, 2026-10-05, bottomToTop's rule turned
         * sideways). Reversing the children alone stacked them from the left.
         * Not when the gravity names a horizontal place or a distribution
         * spreads them. The kjui codegen: container_component.rb end_first?.
         */
        internal fun stacksFromTheEnd(json: JsonObject, a: ViewAttributes): Boolean {
            if (TypedAttrs.enumString(a.direction) { it.json } != "rightToLeft") return false
            if (distributionOf(a) != null) return false
            val f = ModifierBuilder.resolvedAlignFlags(json)
            return !(f.alignLeft || f.alignRight || f.centerH || f.centerInParent)
        }

        private fun parseRowVerticalAlignment(json: JsonObject): Alignment.Vertical {
            val flags = ModifierBuilder.resolvedAlignFlags(json)
            return when {
                flags.alignTop -> Alignment.Top
                flags.alignBottom -> Alignment.Bottom
                flags.centerV || flags.centerInParent -> Alignment.CenterVertically
                else -> Alignment.Top
            }
        }

        /**
         * The Box's contentAlignment. An axis the gravity does not name takes
         * the container default (jsonui-cli shared/core/attribute_semantics.json
         * -> gravityDefaults). Until 2026-09-24 a single value centred the axis it
         * did not name — `top` gave TopCenter, `left` CenterStart — while the
         * ios runtime and both codegens drew it at the leading/top corner.
         */
        internal fun parseBoxContentAlignment(json: JsonObject): Alignment {
            val flags = ModifierBuilder.resolvedAlignFlags(json)
            val vBoth = flags.alignTop && flags.alignBottom
            val hBoth = flags.alignLeft && flags.alignRight
            return when {
                flags.centerInParent -> Alignment.Center
                vBoth && hBoth -> Alignment.Center
                flags.alignTop && flags.alignLeft -> Alignment.TopStart
                flags.alignTop && flags.alignRight -> Alignment.TopEnd
                flags.alignBottom && flags.alignLeft -> Alignment.BottomStart
                flags.alignBottom && flags.alignRight -> Alignment.BottomEnd
                flags.alignTop && flags.centerH -> Alignment.TopCenter
                flags.alignBottom && flags.centerH -> Alignment.BottomCenter
                flags.alignLeft && flags.centerV -> Alignment.CenterStart
                flags.alignRight && flags.centerV -> Alignment.CenterEnd
                flags.centerH && flags.centerV -> Alignment.Center
                flags.alignTop -> Alignment.TopStart
                flags.alignBottom -> Alignment.BottomStart
                flags.alignLeft -> Alignment.TopStart
                flags.alignRight -> Alignment.TopEnd
                flags.centerH -> Alignment.TopCenter
                flags.centerV -> Alignment.CenterStart
                else -> Alignment.TopStart
            }
        }

        // ── Helpers ──

        /** View-section attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "orientation", "direction", "spacing", "distribution",
            "safeAreaInsetPositions"
        )

        /**
         * When a child has weight, the static tool applies weight directly on the
         * component modifier, which also forces the component to fill the weighted
         * axis. In Dynamic mode weight is on a wrapper Box, so we need to ensure the
         * child fills the available space by injecting matchParent on the weighted axis.
         */
        private fun injectFillSize(
            json: JsonObject,
            fillHeight: Boolean,
            fillWidth: Boolean,
            override: Boolean = false
        ): JsonObject {
            val copy = json.deepCopy()
            if (fillHeight && (override || !copy.has("height"))) {
                copy.addProperty("height", "matchParent")
            }
            if (fillWidth && (override || !copy.has("width"))) {
                copy.addProperty("width", "matchParent")
            }
            return copy
        }

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
