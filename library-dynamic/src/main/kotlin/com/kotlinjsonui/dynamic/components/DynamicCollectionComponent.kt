package com.kotlinjsonui.dynamic.components

import com.kotlinjsonui.core.DeclaredSpelling
import com.kotlinjsonui.dynamic.generated.CommonAttributes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.foundation.lazy.LazyListState
import android.util.Log
import com.kotlinjsonui.dynamic.DebugDiagnostics
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.google.gson.JsonArray
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.DynamicRuntimeScope
import com.kotlinjsonui.dynamic.LocalDynamicRuntimeWriter
import com.kotlinjsonui.dynamic.DynamicLayoutLoader
import com.kotlinjsonui.dynamic.DataBindingContext
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.CollectionAttributes
import com.kotlinjsonui.dynamic.generated.DimensionValue
import com.kotlinjsonui.dynamic.rememberTypedAttrs
import com.kotlinjsonui.components.CollectionStackMode
import com.kotlinjsonui.components.ReversedColumn
import com.kotlinjsonui.dynamic.helpers.ContentInsetBehavior
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.data.CollectionDataSource
import com.kotlinjsonui.data.CollectionDataSection
import com.kotlinjsonui.data.IdentifiedCellItem
import kotlinx.coroutines.flow.SharedFlow
import androidx.compose.runtime.snapshotFlow

/**
 * Dynamic Collection Component Converter
 * Converts JSON to LazyGrid composable at runtime for grid layouts
 *
 * Supported JSON attributes (matching SwiftUI implementation):
 * - sections: Array of section definitions with cell types and columns
 * - cellClasses: String array of cell class names (legacy)
 * - headerClasses: String array of header class names
 * - footerClasses: String array of footer class names
 * - items: @{variable} for data source
 * - columns: Number of columns (default: 2)
 * - layout: "vertical" | "horizontal" | "flow"
 * - scrollDirection: "vertical" | "horizontal" (deprecated, use layout)
 * - contentPadding: Number or Array for grid content padding
 * - itemSpacing/spacing: Number for uniform spacing between items
 * - lineSpacing: Number for vertical spacing between rows (minimumLineSpacing in iOS)
 * - columnSpacing: Number for horizontal spacing between columns (minimumInteritemSpacing in iOS)
 * - cellHeight: Number for fixed cell height
 * - cellWidth: Number for fixed cell width (horizontal layout)
 * - cellIdProperty: String property name to extract unique ID from cell data
 * - scrollTo: @{variable} — the cell to scroll to: an Int, a cell counted across
 *   the sections; a String with cellIdProperty, the first cell whose key it is
 *   (a SharedFlow of them is still read, as before jsonui-cli 1.9.0)
 * - scrollAnchor: "top" | "center" | "bottom" anchor point for scrollTo
 * - flowAlignment: "leading" | "center" | "trailing" for FlowLayout alignment
 * - showsVerticalScrollIndicator: Boolean
 * - showsHorizontalScrollIndicator: Boolean
 * - width/height: Number dimensions
 * - padding/paddings: Number or Array for padding
 * - margins: Array or individual margin properties
 * - background: Background color
 * - paging: Boolean for paging mode (horizontal only)
 * - onPageChanged: @{callback} binding for page change callback
 */
class DynamicCollectionComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                CollectionAttributes.parse(m, canonicalOnly)
            }
            // The address prefix every cell in this Collection is reached by.
            // Read through the typed attributes, not `json.get("id")` — the
            // module's ComponentRawReadGateTest forbids a raw own-node read,
            // and it is the same spelling this file already uses two lines
            // down for logAutoTrackingMisconfiguration.
            val collectionId = a.common.id
            UnappliedAttributes.check(
                "Collection", json,
                declared = CollectionAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            // `onItemAppear` is a DECLARED row again (49-E `3a48d11`): it was
            // `type: "callback"` — the only one in the SSoT — and attr-codegen
            // skips that type as "function-valued, not extractable from JSON",
            // so it had no row in any generated table. What a layout actually
            // carries is the `@{handlerName}` STRING, which is a binding; the
            // handler itself comes from the data map, as it does here.
            @Suppress("UNCHECKED_CAST")
            val onItemAppear: ((Int) -> Unit)? = run {
                val raw = TypedAttrs.rawString(a.onItemAppear) ?: return@run null
                val propName = ModifierBuilder.extractBindingProperty(raw) ?: return@run null
                data[propName] as? Function1<Int, Unit>
            }

            // Check if sections are defined
            val sections = json.get("sections")?.asJsonArray
            // Support both 'layout' and 'orientation' attributes for
            // horizontal/vertical; `horizontalScroll: true` is the declared
            // boolean spelling of the same direction fact (ScrollView's
            // vocabulary — real carousels use it). Codegen honors it; not
            // reading it here rendered those Collections vertical
            // (parity d=32 on Collection/horizontalScroll__true).
            val layout = when {
                a.horizontalScroll == true -> "horizontal"
                else -> TypedAttrs.enumString(a.layout) { it.json }
                    ?: TypedAttrs.enumString(a.orientation) { it.json }
                    ?: "vertical"
            }
            val isHorizontal = layout == "horizontal"
            val isFlow = layout == "flow"

            // `lazy` accepts boolean (legacy) or one of "lazy"/"eager"/"none".
            //   LAZY  -> existing LazyVerticalGrid / LazyHorizontalGrid path (default).
            //   EAGER -> renderNonLazy/renderNonLazyRow + verticalScroll/horizontalScroll
            //            so heavy cells don't suffer LazyVStack-style virtualization
            //            re-evaluation hiccups.
            //   NONE  -> renderNonLazy/renderNonLazyRow with no scroll modifier.
            // Bindings resolve at runtime via the data map; the wrapper composable
            // re-applies the same closure regardless of value, preserving identity.
            // 'lazy' additionally accepts a legacy boolean form — wider than
            // the declared enum type, so read raw (see TypedAttrs.rawKey).
            val collectionMode = run {
                val raw = TypedAttrs.rawKey(json, "lazy")
                val resolved: Any? = when {
                    raw == null -> null
                    raw.isJsonNull -> null
                    raw.isJsonPrimitive && raw.asJsonPrimitive.isBoolean -> raw.asBoolean
                    raw.isJsonPrimitive && raw.asJsonPrimitive.isString -> {
                        val s = raw.asString
                        if (s.startsWith("@{") && s.endsWith("}")) {
                            // Canonical value resolution (flat-first, dot
                            // paths, `?? default`); unresolved → null.
                            DataBindingContext.evaluateExpression(s, data)
                                .takeIf { it !== s }
                        } else s
                    }
                    else -> null
                }
                CollectionStackMode.fromJson(resolved)
            }
            val lazy = collectionMode == CollectionStackMode.LAZY

            // A section with no `cell` of its own falls back to the first
            // cellClass (sectioned shape; unchanged). The shape with no
            // `sections` is read by cellPlan below.
            val cellClassName = extractStringList(a.cellClasses).firstOrNull()

            // Parse cellIdProperty for data-based identity
            val cellIdProperty = a.cellIdProperty
            val autoChangeTrackingId = a.autoChangeTrackingId ?: false
            if (autoChangeTrackingId && cellIdProperty.isNullOrEmpty()) {
                logAutoTrackingMisconfiguration(a.common.id)
            }

            // Parse data binding for items — canonical whole-value
            // resolution (flat-first, dot paths) of the bound object.
            val itemsRaw = TypedAttrs.raw(a.items) as? String
            val itemsBoundValue: Any? = itemsRaw
                ?.takeIf { it.startsWith("@{") && it.endsWith("}") }
                ?.let { expr ->
                    DataBindingContext.evaluateExpression(expr, data).takeIf { it !== expr }
                }

            // The data source and, per route, the section configs the cells
            // are drawn from — `sections` as declared, or the legacy shape
            // (no `sections`, cellClasses / headerClasses / footerClasses)
            // read the way sjui's codegen draws it. See cellPlan.
            val plan = cellPlan(
                a, sections,
                boundSource(a, sections, itemsBoundValue)?.reconfigured(
                    cellIdProperty = cellIdProperty,
                    autoChangeTrackingId = autoChangeTrackingId
                )
            )
            nameUndrawnCellClasses(a, sections)
            val collectionDataSource = plan.dataSource

            // Parse grid configuration with default columns
            val defaultColumns = TypedAttrs.int(a.columns, data) ?: 1

            // Calculate actual grid columns based on sections
            val gridColumns = if (sections != null) {
                // Collect all unique column counts from sections
                val sectionColumns = sections.map { sectionJson ->
                    sectionJson.asJsonObject.get("columns")?.asInt ?: defaultColumns
                }.distinct()

                // If sections have different column counts, calculate LCM
                if (sectionColumns.size > 1) {
                    calculateLCM(sectionColumns)
                } else {
                    sectionColumns.firstOrNull() ?: defaultColumns
                }
            } else {
                defaultColumns
            }

            // Parse content padding. Tool writes one of:
            //   - contentPadding: number | [t, r, b, l]
            //   - insets: number | array | "t|r|b|l" pipe-separated string
            //   - insetHorizontal / insetVertical: separate axes
            // Not `contentInsets`: it is declared for swift only (mode
            // swiftui) and kjui's codegen does not draw it, so reading it
            // here drew padding in Debug that the Release build does not.
            // A declared contentPadding / insets, insetHorizontal / insetVertical
            // and the safe area `contentInsetAdjustmentBehavior` asks for are
            // ADDED, side by side, as iOS adds them — measured 2026-09-27 on
            // sjui codegen and SwiftJsonUI Dynamic: insets [8,0,0,0] with
            // insetVertical 8 at the top of a 62pt safe area put the first cell
            // at 78 (4f rulings, rounds 16 and 17). Until jsonui-cli 1.9.0 a
            // declared insets replaced the other two (plan 49 lane C, #4, whose
            // reason — "the author named an exact value" — adding keeps too).
            val paddingSides = collectionPaddingSides(a, json, data)
            val safeArea = ContentInsetBehavior.safeAreaPadding(a.contentInsetAdjustmentBehavior, horizontal = isHorizontal)
            val paddingDirection = LocalLayoutDirection.current
            val contentPadding = when {
                safeArea == null -> paddingSides?.let { paddingOfSides(it) } ?: PaddingValues(0.dp)
                paddingSides == null -> safeArea
                else -> PaddingValues(
                    start = safeArea.calculateStartPadding(paddingDirection) + paddingSides[3].dp,
                    top = safeArea.calculateTopPadding() + paddingSides[0].dp,
                    end = safeArea.calculateEndPadding(paddingDirection) + paddingSides[1].dp,
                    bottom = safeArea.calculateBottomPadding() + paddingSides[2].dp
                )
            }

            // Parse spacing
            // lineSpacing: vertical spacing between rows (minimumLineSpacing in iOS)
            // columnSpacing: horizontal spacing between columns (minimumInteritemSpacing in iOS)
            // itemSpacing/spacing: uniform spacing (fallback; 'spacing' is an
            // undeclared legacy runtime extra)
            val defaultSpacing = a.itemSpacing?.toFloat()
                ?: TypedAttrs.undeclared(json, "spacing")?.asFloat
                ?: 0f
            val lineSpacing = (a.lineSpacing?.toFloat() ?: defaultSpacing).dp
            val columnSpacing = (a.columnSpacing?.toFloat() ?: defaultSpacing).dp
            // A HORIZONTAL collection — every one, a single lane, its lanes
            // and its pages — has one rule (4f ruling, 2026-09-26; SwiftJsonUI
            // reads the same): along the scroll axis lineSpacing, else
            // itemSpacing; between lanes columnSpacing, else itemSpacing. See
            // horizontalSpacing. The scroll axis read lineSpacing, else
            // columnSpacing, else itemSpacing, and the lanes were not spaced.
            val horizontal = horizontalSpacing(a, defaultSpacing)
            val scrollAxisSpacing = if (isHorizontal) {
                horizontal.alongScroll.dp
            } else {
                columnSpacing
            }

            // Build modifier
            var modifier = ModifierBuilder.buildModifier(json, data, context = context)
            // Container-level listStyle chrome: an EMPTY collection must
            // still discriminate the four values (the conformance probes
            // carry no cells), the way an empty ios List still shows its
            // style's background — same recipe the codegen emits.
            run {
                val style = DeclaredSpelling.lowered(TypedAttrs.enumString(a.listStyle) { it.json }, CollectionAttributes.ListStyle.declaredSpellings) ?: "plain"
                if (style in setOf("grouped", "insetgrouped", "sidebar")) {
                    if (style == "insetgrouped" || style == "sidebar") {
                        modifier = modifier
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(if (style == "insetgrouped") 12.dp else 8.dp))
                    }
                    modifier = modifier.background(
                        if (style == "sidebar") MaterialTheme.colorScheme.surfaceContainerLow
                        else MaterialTheme.colorScheme.surfaceContainer
                    )
                }
            }

            // Get cell height/width if specified ('cellHeight'/'cellWidth' are
            // undeclared legacy runtime extras)
            val cellHeight = TypedAttrs.undeclared(json, "cellHeight")?.asFloat?.dp
            val cellWidth = TypedAttrs.undeclared(json, "cellWidth")?.asFloat?.dp

            // No node-level `cell` template: it is not declared for Collection
            // (the validators warn it as unknown) and no codegen draws it, so
            // a Collection that relied on it drew ten template cells in Debug
            // and none in Release. Cells come from `sections[].cell` with an
            // `items` data source, as on every other path.

            // Parse gravity for item alignment
            // Box.contentAlignment uses Alignment (compound), not Alignment.Vertical/Horizontal
            // as declared (a single value; common.gravity also takes a list,
            // which this reading does not): another case is no gravity
            val gravity = DeclaredSpelling.lowered(a.common.gravity as? String, CommonAttributes.Gravity.declaredSpellings)
            val gravityAlignment = if (isHorizontal) {
                // Horizontal scroll: vertical alignment (TopStart, CenterStart, BottomStart)
                when (gravity) {
                    "center", "centervertical" -> Alignment.CenterStart
                    "bottom" -> Alignment.BottomStart
                    else -> Alignment.TopStart // 'top' is default for horizontal scroll
                }
            } else {
                // Vertical scroll: horizontal alignment (TopStart, TopCenter, TopEnd)
                when (gravity) {
                    "center", "centerhorizontal" -> Alignment.TopCenter
                    "right" -> Alignment.TopEnd
                    else -> Alignment.TopStart // 'left' is default for vertical scroll
                }
            }

            // Reverse layout
            val reverseLayout = a.reverseLayout == true

            // scrollEnabled - controls whether user can scroll (supports @{binding})
            val scrollEnabled = TypedAttrs.boolean(a.scrollEnabled, data) ?: true

            // listStyle chrome + hideSeparator (51-E) — see ListChrome.
            val listChrome = ListChrome(
                style = DeclaredSpelling.lowered(TypedAttrs.enumString(a.listStyle) { it.json }, CollectionAttributes.ListStyle.declaredSpellings) ?: "plain",
                hideSeparator = a.hideSeparator == true
            )

            // The scrollTo request (resolveScrollTo)
            val scrollTo = resolveScrollTo(a, data)
            val scrollAnchor = TypedAttrs.enumString(a.scrollAnchor) { it.json } ?: "bottom"
            val defaultAnchor = TypedAttrs.enumString(a.defaultScrollAnchor) { it.json }
            // `scrollAnimated` (declared boolean, default true): false moves
            // the list to the scrollTo target at once instead of animating —
            // what both iOS faces do with it (sjui collection_converter.rb
            // generate_scroll_reader_close; SwiftJsonUI's dynamic
            // CollectionConverter). Every scroll here animated whatever it
            // said.
            val scrollAnimated = a.scrollAnimated != false

            val heightStatic = TypedAttrs.static(a.common.height)
            val heightIsWrapContent = heightStatic == DimensionValue.WrapContent
            val widthIsWrapContent = TypedAttrs.static(a.common.width) == DimensionValue.WrapContent
            // A numeric height or a maxHeight hands a scroll modifier a finite
            // max whatever the parent does: the node is bounded by its own
            // declaration. Anything else (matchParent, a bound height) is only
            // bounded if the parent is.
            val heightIsSelfBounded =
                heightStatic is DimensionValue.Number || TypedAttrs.static(a.common.maxHeight) != null

            // FlowLayout mode. Ruling (2026-09-03): with `lazy` in effect (LAZY or
            // EAGER) a flow Collection scrolls vertically inside its own bounds;
            // NONE only wraps and the parent scrolls. FlowRow is not a Lazy
            // container, so this modifier is the only scroll it gets; without it
            // the same JSON scrolled on iOS (SwiftJsonUI wraps the default-lazy
            // flow in a ScrollView) and clipped or spilled here.
            //
            // "Its own bounds" is literal. A vertically scrollable node measured
            // with an infinite max height throws, and that is what wrapContent
            // gets — or matchParent under a LazyColumn cell or a scrolling
            // sheet, a consumer shape the fixed-box corpus never held. So:
            // wrapContent never scrolls (nothing to scroll inside; the parent
            // does), a self-bounded height always may, and matchParent asks the
            // parent's constraints at runtime. kjui's static emit applies the
            // same rule minus the runtime arm (it cannot see the parent), so a
            // matchParent flow under a finite parent scrolls here and not there
            // — recorded as a parity residue, not hidden.
            if (isFlow) {
                // 'flowAlignment' is an undeclared legacy runtime extra
                val flowAlignment = TypedAttrs.undeclared(json, "flowAlignment")?.asString ?: "leading"
                val flowScrolls = collectionMode != CollectionStackMode.NONE && !heightIsWrapContent
                // scrollTo on a flow that scrolls (4f ruling 2026-09-27, round
                // 11): its own scroll state, the scrolled content's and each
                // cell's coordinates recorded as they are laid out, and the
                // cell scrolled to by scrollAnchor. The flow read no scrollTo
                // until jsonui-cli 1.9.0. A flow that does not scroll has
                // nothing to scroll: the parent does. A String that is no key
                // scrolls nowhere here (a flow has no lazy item).
                val flowScroll = rememberScrollState()
                val flowTargets = remember { FlowScrollTargets() }
                val flowSections = plan.sectionsFor(CellRoute.FLOW)
                // The drawn sections' cells, in section order: a section that
                // names a cell (or the class-list shape's cell).
                val flowCells = if (flowSections != null && collectionDataSource != null) {
                    (0 until minOf(flowSections.size(), collectionDataSource.sections.size))
                        .filter { s -> (sectionViewName(flowSections[s].asJsonObject, "cell") ?: cellClassName) != null }
                        .flatMap { s -> collectionDataSource.sections[s].cells?.data.orEmpty() }
                } else emptyList()
                ScrollToEffect(scrollTo, collectionId, { value, _ -> (scrollCell(value, flowCells, cellIdProperty, legacy = false) as? ScrollCell.Cell)?.index }) { cell ->
                    val content = flowTargets.content?.takeIf { it.isAttached } ?: return@ScrollToEffect
                    val placed = flowTargets.cells[cell]?.takeIf { it.isAttached } ?: return@ScrollToEffect
                    val top = content.localPositionOf(placed, androidx.compose.ui.geometry.Offset.Zero).y.toInt()
                    val size = placed.size.height
                    val y = (top + anchorOffset(scrollAnchor, false, flowScroll.viewportSize, size)).coerceAtLeast(0)
                    if (scrollAnimated) flowScroll.animateScrollTo(y) else flowScroll.scrollTo(y)
                }
                val flow: @Composable (Modifier) -> Unit = { flowModifier ->
                    renderFlowLayout(
                        sections = plan.sectionsFor(CellRoute.FLOW),
                        collectionDataSource = collectionDataSource,
                        cellClassName = cellClassName,
                        cellIdProperty = cellIdProperty,
                        data = data,
                        modifier = flowModifier.then(Modifier.padding(contentPadding)),
                        horizontalSpacing = columnSpacing,
                        verticalSpacing = lineSpacing,
                        flowAlignment = flowAlignment,
                        cellWidth = cellWidth,
                        cellHeight = cellHeight,
                        gravityAlignment = gravityAlignment,
                        onItemAppear = onItemAppear,
                        collectionId = collectionId,
                        scrollTargets = flowTargets
                    )
                }
                // scrollEnabled false stops the user's scrolling only (round 13).
                val scrolled = Modifier.verticalScroll(flowScroll, enabled = scrollEnabled).onGloballyPositioned { flowTargets.content = it }
                when {
                    !flowScrolls -> flow(modifier)
                    heightIsSelfBounded -> flow(modifier.then(scrolled))
                    else -> BoxWithConstraints(modifier = modifier) {
                        // The node's own modifiers (size, background, address) sit
                        // on this box; the FlowRow fills it and scrolls only when
                        // the box was given a finite height to fill.
                        val inner = if (constraints.hasBoundedHeight) {
                            Modifier.fillMaxSize().then(scrolled)
                        } else {
                            Modifier.fillMaxWidth()
                        }
                        flow(inner)
                    }
                }
                return
            }

            // For EAGER / NONE / wrapContent paths, route through the Row/Column
            // renderers. EAGER additionally applies verticalScroll/horizontalScroll
            // so the collection scrolls without virtualization. NONE skips the
            // scroll modifier entirely (parent must provide scroll). wrapContent
            // takes the Column, not a lazy container, to avoid Compose's
            // nested-Lazy crash, and scrolls inside the height its parent bounds
            // it to (scrollWithinBounds; until jsonui-cli 1.9.0 it never scrolled).
            if (!lazy && isHorizontal) {
                // An EAGER Row scrolls inside its own bounds, and its scrollTo and
                // defaultScrollAnchor reach its cells by the rule the lazy routes
                // follow (4f ruling 2026-09-27, round 12): the cell's place in the
                // scrolled content, recorded as it is laid out (NonLazyScrollEffects).
                // Until jsonui-cli 1.9.0 neither moved it.
                val rowSections = plan.sectionsFor(CellRoute.NON_LAZY_ROW)
                val rowScroll = rememberScrollState()
                val rowTargets = remember { FlowScrollTargets() }
                val rowScrolls = collectionMode == CollectionStackMode.EAGER && !widthIsWrapContent
                val rowModifier = if (rowScrolls) {
                    modifier.horizontalScroll(rowScroll, enabled = scrollEnabled).onGloballyPositioned { rowTargets.content = it }
                } else {
                    modifier
                }
                if (rowScrolls) {
                    NonLazyScrollEffects(
                        cells = drawnCells(rowSections, collectionDataSource),
                        scrollTo = scrollTo,
                        collectionId = collectionId,
                        cellIdProperty = cellIdProperty,
                        scrollAnchor = scrollAnchor,
                        scrollAnimated = scrollAnimated,
                        defaultAnchor = defaultAnchor,
                        state = rowScroll,
                        targets = rowTargets,
                        horizontal = true,
                        sectionSizes = drawnSectionSizes(rowSections, collectionDataSource),
                        reverse = false
                    )
                }
                renderNonLazyRow(
                    chrome = listChrome,
                    sections = rowSections,
                    collectionDataSource = collectionDataSource,
                    cellIdProperty = cellIdProperty,
                    data = data,
                    modifier = rowModifier,
                    columnSpacing = scrollAxisSpacing,
                    laneSpacing = horizontal.betweenLanes.dp,
                    defaultColumns = defaultColumns,
                    contentPadding = contentPadding,
                    cellWidth = cellWidth,
                    cellHeight = cellHeight,
                    gravityAlignment = gravityAlignment,
                    onItemAppear = onItemAppear,
                    collectionId = collectionId,
                    scrollTargets = if (rowScrolls) rowTargets else null,
                    // A short EAGER Row sits where defaultScrollAnchor says, as the
                    // lazy row does (round 15); it draws no reverseLayout. A Row
                    // that does not scroll takes none, as iOS's does not.
                    contentAlignment = if (rowScrolls) rowContentAlignment(defaultAnchor, false) else null
                )
                return
            }
            if ((!lazy && !isHorizontal) || (!isHorizontal && heightIsWrapContent)) {
                // Which Column scrolls (4f rulings 2026-09-27, round 12): an EAGER one
                // inside its own bounds; a wrapContent one, LAZY or EAGER, inside the
                // height its parent bounds it to, and not at all under a parent that
                // does not bound it — a scrolling ancestor — where it is its content's
                // height (scrollWithinBounds). That is what iOS (a ScrollView as tall
                // as its parent lets it be) and the web (a fit-content box a flex
                // parent shrinks, overflow auto) draw, measured on both. `lazy: none`
                // never scrolls. scrollTo and defaultScrollAnchor reach the cells of
                // a Column that scrolls by the rule the lazy routes follow
                // (NonLazyScrollEffects). Until jsonui-cli 1.9.0 a wrapContent Column
                // never scrolled — its cells ran past a bounded parent — and neither
                // attribute moved an EAGER one.
                val columnSections = plan.sectionsFor(CellRoute.NON_LAZY_COLUMN)
                val columnScroll = rememberScrollState()
                val columnTargets = remember { FlowScrollTargets() }
                val columnScrolls = collectionMode != CollectionStackMode.NONE
                // The EAGER Column draws reverseLayout as the lazy list does — its
                // first item at the bottom, resting there, a short list at the
                // bottom (ReversedColumn, reverseScrolling) — and scrollEnabled
                // false stops the user's scrolling only; a scrollTo still scrolls
                // (4f rulings 2026-09-27, round 13). Until then EAGER drew no
                // reverseLayout and ignored scrollEnabled. The wrapContent and
                // `lazy: none` Columns draw no reverseLayout, as kjui's.
                val columnReversed = reverseLayout && collectionMode == CollectionStackMode.EAGER && !heightIsWrapContent
                val columnModifier = when {
                    !columnScrolls -> modifier
                    heightIsWrapContent -> modifier.scrollWithinBounds(columnScroll, scrollEnabled).onGloballyPositioned { columnTargets.content = it }
                    else -> modifier.verticalScroll(columnScroll, enabled = scrollEnabled, reverseScrolling = columnReversed)
                        .onGloballyPositioned { columnTargets.content = it }
                }
                if (columnScrolls) {
                    NonLazyScrollEffects(
                        cells = drawnCells(columnSections, collectionDataSource),
                        scrollTo = scrollTo,
                        collectionId = collectionId,
                        cellIdProperty = cellIdProperty,
                        scrollAnchor = scrollAnchor,
                        scrollAnimated = scrollAnimated,
                        defaultAnchor = defaultAnchor,
                        state = columnScroll,
                        targets = columnTargets,
                        horizontal = false,
                        sectionSizes = drawnSectionSizes(columnSections, collectionDataSource),
                        reverse = columnReversed
                    )
                }
                renderNonLazy(
                    chrome = listChrome,
                    sections = columnSections,
                    legacyHeader = plan.headerFor(CellRoute.NON_LAZY_COLUMN),
                    legacyFooter = plan.footerFor(CellRoute.NON_LAZY_COLUMN),
                    oneGridForAllSections = !plan.hasDeclaredSections,
                    defaultColumns = defaultColumns,
                    columnSpacing = columnSpacing,
                    collectionDataSource = collectionDataSource,
                    cellIdProperty = cellIdProperty,
                    data = data,
                    modifier = columnModifier,
                    lineSpacing = lineSpacing,
                    contentPadding = contentPadding,
                    cellHeight = cellHeight,
                    gravityAlignment = gravityAlignment,
                    onItemAppear = onItemAppear,
                    collectionId = collectionId,
                    scrollTargets = if (columnScrolls) columnTargets else null,
                    reverseLayout = columnReversed,
                    // Short content with defaultScrollAnchor bottom sits at the bottom of
                    // the EAGER Column, as iOS draws it (round 14).
                    // and in the middle for a center anchor (round 16), reversed or not;
                    // the wrapContent and `lazy: none` Columns at the top.
                    contentAlignment = if (collectionMode == CollectionStackMode.EAGER && !heightIsWrapContent) {
                        columnContentAlignment(defaultAnchor, columnReversed)
                    } else Alignment.Top
                )
                return
            }

            // Paging mode for horizontal collections
            val isPaging = a.paging == true
            if (isHorizontal && isPaging) {
                renderPagingHorizontal(
                    a = a,
                    sections = plan.sectionsFor(CellRoute.PAGING),
                    collectionDataSource = collectionDataSource,
                    cellClassName = cellClassName,
                    cellIdProperty = cellIdProperty,
                    data = data,
                    modifier = modifier,
                    contentPadding = contentPadding,
                    pageSpacing = scrollAxisSpacing,
                    cellWidth = cellWidth,
                    cellHeight = cellHeight,
                    gravityAlignment = gravityAlignment,
                    onItemAppear = onItemAppear,
                    collectionId = collectionId,
                    userScrollEnabled = scrollEnabled
                )
                return
            }

            // Single-lane horizontal (the CollectionStack shape codegen
            // emits): a LazyRow whose cells keep their own cross-axis size.
            // LazyHorizontalGrid with rows=Fixed(1) STRETCHES every cell to
            // the lane height (grid semantics), so a 36dp chip in an 80dp
            // collection rendered 64dp tall with dead space under its label,
            // dynamic face only (a downstream chip carousel, 2026-08-10). Multi-row
            // horizontal grids keep the grid path below.
            if (isHorizontal && gridColumns == 1) {
                renderLazyRowSingleLane(
                    sections = plan.sectionsFor(CellRoute.LAZY_ROW),
                    collectionDataSource = collectionDataSource,
                    cellIdProperty = cellIdProperty,
                    data = data,
                    modifier = modifier,
                    scrollAxisSpacing = scrollAxisSpacing,
                    contentPadding = contentPadding,
                    cellWidth = cellWidth,
                    cellHeight = cellHeight,
                    gravityAlignment = gravityAlignment,
                    scrollTo = scrollTo,
                    scrollAnimated = scrollAnimated,
                    scrollAnchor = scrollAnchor,
                    onItemAppear = onItemAppear,
                    chrome = listChrome,
                    collectionId = collectionId,
                    defaultAnchor = defaultAnchor,
                    userScrollEnabled = scrollEnabled,
                    reverseLayout = reverseLayout
                )
                return
            }

            // LazyGrid state for programmatic scrolling
            val gridState = rememberLazyGridState()

            // The grid's sections as it emits them (scrollItemIndex's walk),
            // for scrollTo and defaultScrollAnchor alike.
            val gridRoute = if (isHorizontal) CellRoute.LAZY_HORIZONTAL_GRID else CellRoute.LAZY_VERTICAL_GRID
            val gridScrollSections = emittedScrollSections(
                sections = plan.sectionsFor(gridRoute),
                collectionDataSource = collectionDataSource,
                gridColumns = gridColumns,
                defaultColumns = defaultColumns,
                reverseLayout = reverseLayout,
                breakRowsBetweenSections = plan.hasDeclaredSections
            )
            val gridLeadingItems = if (!isHorizontal && plan.headerFor(CellRoute.LAZY_VERTICAL_GRID) != null) 1 else 0

            // defaultScrollAnchor — where the list STARTS, as opposed to
            // `scrollAnchor`, which positions a programmatic scrollTo. Only
            // center/bottom do anything (top is already the resting position),
            // and it applies once: keyed on the item count because the data
            // usually arrives async and an anchor applied to an empty list
            // does nothing, with the remembered flag stopping a later append
            // from yanking the user back. The kjui codegen emits exactly this
            // shape (collection_component.rb#default_scroll_anchor_code); the
            // dynamic path had no equivalent (34: `Collection/
            // defaultScrollAnchor` pixel-identical to its control).
            //
            // The cell is counted by the scrollTo rule — across the drawn
            // sections, headers, footers and row breaks not counted — and
            // scrolled to as the item that holds it (4f ruling 2026-09-27,
            // round 11). Until jsonui-cli 1.9.0 the count was the first data
            // section's cells and the result the lazy item index.
            //
            // Under reverseLayout the list rests at its visual bottom, so top
            // and bottom trade places, as they do for scrollAnchor (4f ruling
            // 2026-09-27, round 12, where iOS lands): bottom is where it rests
            // and moves nothing; top goes to the cell drawn at the visual top
            // (restingAnchorCell). Until jsonui-cli 1.9.0 bottom went to the
            // last cell, which a reversed list draws at its visual top.
            val restingAnchor = restingAnchor(defaultAnchor, reverseLayout)
            if (restingAnchor == "center" || restingAnchor == "bottom") {
                val anchorCount = gridScrollSections.sumOf { it.cells?.size ?: 0 }
                val anchorApplied = remember { mutableStateOf(false) }
                LaunchedEffect(anchorCount) {
                    if (!anchorApplied.value && anchorCount > 0) {
                        restingAnchorCell(restingAnchor, gridScrollSections)?.let { cell ->
                            scrollItemIndex(cell, gridScrollSections, null, gridLeadingItems)?.let { gridState.scrollToItem(it) }
                        }
                        anchorApplied.value = true
                    }
                }
            }

            // Handle scrollTo: the lazy item the value names — a cell counted
            // across the sections, headers, footers and fillers not counted
            // (scrollItemIndex) — over the sections as the grid below emits
            // them, landed where scrollAnchor says (scrollToAnchored). Until
            // jsonui-cli 1.9.0 center and bottom landed the item at the top.
            ScrollToEffect(scrollTo, collectionId, { value, onLegacy -> scrollItemIndex(value, gridScrollSections, cellIdProperty, gridLeadingItems, onLegacy) }) { index ->
                gridState.scrollToAnchored(index, scrollAnchor, reverseLayout, scrollAnimated, isHorizontal)
            }

            // Create the appropriate grid based on layout
            if (isHorizontal) {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(gridColumns),
                    modifier = modifier,
                    state = gridState,
                    reverseLayout = reverseLayout,
                    userScrollEnabled = scrollEnabled,
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(horizontal.betweenLanes.dp) /* between lanes: columnSpacing, else itemSpacing */,
                    // A short reversed grid sits at its end, as a reversed list
                    // does (4f ruling 2026-09-27, round 13); a short grid sits
                    // where defaultScrollAnchor says (rowContentAlignment, round 15).
                    horizontalArrangement = Arrangement.spacedBy(scrollAxisSpacing, rowContentAlignment(defaultAnchor, reverseLayout))
                ) {
                    generateCollectionItems(
                        sections = plan.sectionsFor(CellRoute.LAZY_HORIZONTAL_GRID),
                        // Each declared section starts a new column (4f
                        // ruling, 2026-09-26), as it starts a new row on the
                        // vertical grid: the same arithmetic, in lanes.
                        breakRowsBetweenSections = plan.hasDeclaredSections,
                        collectionDataSource = collectionDataSource,
                        cellClassName = cellClassName,
                        cellIdProperty = cellIdProperty,
                        data = data,
                        chrome = listChrome,
                        cellWidth = cellWidth,
                        cellHeight = cellHeight,
                        gridColumns = gridColumns,
                        defaultColumns = defaultColumns,
                        gravityAlignment = gravityAlignment,
                        reverseLayout = reverseLayout,
                        onItemAppear = onItemAppear,
                        collectionId = collectionId
                    )
                }
            } else {
                // Vertical grid (default)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    modifier = modifier,
                    state = gridState,
                    reverseLayout = reverseLayout,
                    userScrollEnabled = scrollEnabled,
                    contentPadding = contentPadding,
                    // A short reversed list sits at its bottom — where iOS draws it,
                    // bottom-anchored (4f ruling 2026-09-27, round 13). It sat at the
                    // top until then (spacedBy aligns to the top).
                    // Not reversed, defaultScrollAnchor bottom puts short content at the
                    // bottom too, as iOS draws it (round 14).
                    // A center anchor puts it in the middle (columnContentAlignment, round 16).
                    verticalArrangement = Arrangement.spacedBy(lineSpacing, columnContentAlignment(defaultAnchor, reverseLayout)),
                    horizontalArrangement = Arrangement.spacedBy(columnSpacing)
                ) {
                    // The legacy shape's headerClasses / footerClasses: once,
                    // without data, full width before and after the cells —
                    // sjui's List / grid. Every data section's cells share
                    // this one grid (sjui's legacy grid is one LazyVGrid).
                    plan.headerFor(CellRoute.LAZY_VERTICAL_GRID)?.let { name ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                renderCellView(name, emptyMap<String, Any>(), -1, data)
                            }
                        }
                    }
                    generateCollectionItems(
                        sections = plan.sectionsFor(CellRoute.LAZY_VERTICAL_GRID),
                        breakRowsBetweenSections = plan.hasDeclaredSections,
                        collectionDataSource = collectionDataSource,
                        cellClassName = cellClassName,
                        cellIdProperty = cellIdProperty,
                        data = data,
                        chrome = listChrome,
                        cellWidth = cellWidth,
                        cellHeight = cellHeight,
                        gridColumns = gridColumns,
                        defaultColumns = defaultColumns,
                        gravityAlignment = gravityAlignment,
                        reverseLayout = reverseLayout,
                        onItemAppear = onItemAppear,
                        collectionId = collectionId
                    )
                    plan.footerFor(CellRoute.LAZY_VERTICAL_GRID)?.let { name ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                renderCellView(name, emptyMap<String, Any>(), -1, data)
                            }
                        }
                    }
                }
            }
        }

        /**
         * The scrollTo request the binding resolves to: the plain value the
         * SSoT declares (Collection.scrollTo — `Int`, or `String` with
         * cellIdProperty), or a SharedFlow of such values. Until jsonui-cli
         * 1.9.0 only a `SharedFlow<Int>` was read, so the declared plain
         * value never scrolled here.
         */
        private fun resolveScrollTo(a: CollectionAttributes, data: Map<String, Any>): Any? {
            val scrollToBinding = TypedAttrs.raw(a.scrollTo) as? String ?: return null
            if (!scrollToBinding.startsWith("@{") || !scrollToBinding.endsWith("}")) return null
            // Canonical value resolution (flat-first, dot paths).
            return DataBindingContext.evaluateExpression(scrollToBinding, data)
        }

        /**
         * A scrollTo request, keyed on its value — kjui's codegen keys its
         * LaunchedEffect on the bound value the same way — or, for a
         * SharedFlow, each value it emits. [target] names the item (the lazy
         * item, the page, the flow's cell); null is no scroll. It is handed a
         * callback for the legacy reading, which a debuggable app hears as a
         * warning naming the value and the migration.
         *
         * A value scrolls when it CHANGES (4f ruling 2026-09-27, round 11):
         * the value the Collection first composes with names no scroll, as
         * SwiftUI's `.onChange(of:)` reads it — a list with a header and an
         * initial 0 stays at its top. Until jsonui-cli 1.9.0 the first
         * composition scrolled too.
         */
        @Composable
        private fun ScrollToEffect(
            value: Any?,
            collectionId: String?,
            target: (Any?, (String, Int) -> Unit) -> Int?,
            scroll: suspend (Int) -> Unit
        ) {
            val currentTarget by rememberUpdatedState(target)
            val currentScroll by rememberUpdatedState(scroll)
            val debuggable = DebugDiagnostics.isAppDebuggable(LocalContext.current)
            val onLegacy: (String, Int) -> Unit = { raw, index ->
                if (debuggable) {
                    Log.w(
                        "DynamicView",
                        "Collection ${collectionId ?: "(unnamed)"}: scrollTo \"$raw\" is no cell's key — read as the legacy " +
                            "lazy item index $index. Scroll by a cell's key, or by its index among the cells (jsonui-cli 1.9.0, Collection.scrollTo)."
                    )
                }
            }
            if (value is SharedFlow<*>) {
                LaunchedEffect(value) {
                    value.collect { emitted -> currentTarget(emitted, onLegacy)?.let { currentScroll(it) } }
                }
                return
            }
            val armed = remember { mutableStateOf(false) }
            LaunchedEffect(value) {
                if (!armed.value) {
                    armed.value = true
                    return@LaunchedEffect
                }
                currentTarget(value, onLegacy)?.let { currentScroll(it) }
            }
        }

        /**
         * Where a scrolled-to item lands: [anchor] (top / center / bottom)
         * along the main axis — top: its start at the viewport's start;
         * center: its middle at the middle; bottom: its end at the viewport's
         * end — what SwiftUI's ScrollViewReader anchors and kjui's codegen
         * emits. Under [reverse] the list starts at its end, so top and bottom
         * trade places. Returns the offset `scrollToItem` takes (negative
         * pushes the item toward the viewport's end).
         */
        internal fun anchorOffset(anchor: String, reverse: Boolean, viewport: Int, size: Int): Int {
            val effective = if (reverse) when (anchor) { "top" -> "bottom"; "bottom" -> "top"; else -> anchor } else anchor
            return when (effective) {
                "center" -> -(viewport - size) / 2
                "bottom" -> -(viewport - size)
                else -> 0
            }
        }

        /**
         * Scrolls to [index] landed by [anchor] (anchorOffset). The item's
         * size is its own when it is laid out, else the laid-out items'
         * average for the scroll, corrected once it is laid out.
         */
        private suspend fun LazyGridState.scrollToAnchored(index: Int, anchor: String, reverse: Boolean, animated: Boolean, horizontal: Boolean) {
            fun sizeOf(info: LazyGridItemInfo) = if (horizontal) info.size.width else info.size.height
            val viewport = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
            val items = layoutInfo.visibleItemsInfo
            val size = items.firstOrNull { it.index == index }?.let(::sizeOf)
                ?: items.map(::sizeOf).average().let { if (it.isNaN()) 0 else it.toInt() }
            val offset = anchorOffset(anchor, reverse, viewport, size)
            if (animated) animateScrollToItem(index, offset) else scrollToItem(index, offset)
            layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }?.let(::sizeOf)?.let {
                if (it != size) scrollToItem(index, anchorOffset(anchor, reverse, viewport, it))
            }
        }

        private suspend fun LazyListState.scrollToAnchored(index: Int, anchor: String, reverse: Boolean, animated: Boolean) {
            val viewport = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
            val items = layoutInfo.visibleItemsInfo
            val size = items.firstOrNull { it.index == index }?.size
                ?: items.map { it.size }.average().let { if (it.isNaN()) 0 else it.toInt() }
            val offset = anchorOffset(anchor, reverse, viewport, size)
            if (animated) animateScrollToItem(index, offset) else scrollToItem(index, offset)
            layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }?.size?.let {
                if (it != size) scrollToItem(index, anchorOffset(anchor, reverse, viewport, it))
            }
        }

        /**
         * The flow's scroll targets: its scrolled content's coordinates and
         * each cell's, recorded as they are laid out, by the cell's place
         * among the drawn sections' cells.
         */
        internal class FlowScrollTargets {
            var content: LayoutCoordinates? = null
            val cells = mutableMapOf<Int, LayoutCoordinates>()
        }

        /**
         * defaultScrollAnchor as the list rests: under [reverse] a lazy list
         * starts at its visual bottom, so top and bottom trade places (4f
         * ruling 2026-09-27, round 12) — the swap scrollAnchor takes.
         */
        internal fun restingAnchor(anchor: String?, reverse: Boolean): String? =
            if (reverse) when (anchor) { "top" -> "bottom"; "bottom" -> "top"; else -> anchor } else anchor

        /**
         * Where a horizontal list whose content is shorter than the row sits
         * along it: defaultScrollAnchor center — its middle; bottom — its end;
         * otherwise its start, its end under [reverse] (a reversed list, round
         * 13). iOS draws a short row at its leading edge / middle / trailing
         * edge by the anchor (4f ruling 2026-09-27, round 15); every row here
         * sat at its start whatever the anchor said.
         */
        /**
         * The same along a vertical list: defaultScrollAnchor center — its
         * middle, as iOS draws a short list (measured round 15; 4f ruling
         * 2026-09-27, round 16); bottom — its bottom (round 14); otherwise its
         * top, its bottom when reversed (round 13). A short list sat at the
         * top (the bottom when reversed) for center until jsonui-cli 1.9.0.
         */
        internal fun columnContentAlignment(anchor: String?, reverse: Boolean): Alignment.Vertical = when {
            anchor == "center" -> Alignment.CenterVertically
            anchor == "bottom" || reverse -> Alignment.Bottom
            else -> Alignment.Top
        }

        internal fun rowContentAlignment(anchor: String?, reverse: Boolean): Alignment.Horizontal = when {
            anchor == "center" -> Alignment.CenterHorizontally
            anchor == "bottom" || reverse -> Alignment.End
            else -> Alignment.Start
        }

        /**
         * The cell a resting anchor (restingAnchor) names among [emitted]
         * (the sections as the lazy content emits them): center — the middle
         * cell by the scrollTo count; bottom — the cell drawn last, the last
         * cell of the last section emitted with cells (under reverseLayout the
         * sections are emitted last-first, and that is the first section's
         * last cell). Null for none.
         */
        internal fun restingAnchorCell(anchor: String, emitted: List<ScrollSection>): Int? {
            val count = emitted.sumOf { it.cells?.size ?: 0 }
            if (count == 0) return null
            if (anchor == "center") return count / 2
            val last = emitted.lastOrNull { !it.cells.isNullOrEmpty() } ?: return null
            return emitted.filter { it.section < last.section }.sumOf { it.cells?.size ?: 0 } + last.cells!!.size - 1
        }

        /** The drawn cells of the non-lazy routes, in section order: a section's cells when it names a cell. */
        internal fun drawnCells(sections: JsonArray?, collectionDataSource: CollectionDataSource?): List<Map<String, Any>> {
            if (sections == null || collectionDataSource == null) return emptyList()
            return (0 until minOf(sections.size(), collectionDataSource.sections.size))
                .filter { s -> sectionViewName(sections[s].asJsonObject, "cell") != null }
                .flatMap { s -> collectionDataSource.sections[s].cells?.data.orEmpty() }
        }

        /**
         * A vertical scroll inside the height the parent bounds the node to.
         * Under a parent that does not bound it (a scrolling ancestor) the
         * node is its content's height and has nothing of its own to scroll —
         * where a bare verticalScroll throws ("measured with an infinity
         * maximum height constraints"). The wrapContent Collection's Column
         * (4f ruling 2026-09-27, round 12).
         */
        internal fun Modifier.scrollWithinBounds(state: ScrollState, enabled: Boolean = true): Modifier = this
            .layout { measurable, constraints ->
                val bounded = if (constraints.hasBoundedHeight) constraints else Constraints.fitPrioritizingWidth(
                    constraints.minWidth, constraints.maxWidth, constraints.minHeight, Int.MAX_VALUE - 1
                )
                val placeable = measurable.measure(bounded)
                layout(placeable.width, placeable.height) { placeable.place(0, 0) }
            }
            .verticalScroll(state, enabled = enabled)

        /**
         * Scrolls a non-lazy container's [state] to [cell], landed by [anchor]
         * along its axis on the edges it names on screen, from where the cell
         * sits in the scrolled content ([targets], recorded as they are laid
         * out). [reverse]: the reversed EAGER Column, whose scroll runs from its
         * bottom (reverseScrolling) — a position counts from the other end. A
         * cell that arrived with the value is waited for, a few frames.
         */
        internal suspend fun scrollToPlacedCell(
            state: ScrollState,
            targets: FlowScrollTargets,
            cell: Int,
            anchor: String,
            animated: Boolean,
            horizontal: Boolean,
            rtl: Boolean,
            reverse: Boolean = false,
        ) {
            var frames = 0
            while ((targets.content?.isAttached != true || targets.cells[cell]?.isAttached != true) && frames < 10) {
                frames++
                withFrameNanos { }
            }
            val content = targets.content?.takeIf { it.isAttached } ?: return
            val placed = targets.cells[cell]?.takeIf { it.isAttached } ?: return
            val at = content.localPositionOf(placed, Offset.Zero)
            val size = if (horizontal) placed.size.width else placed.size.height
            val lead = when {
                !horizontal -> at.y.toInt()
                rtl -> content.size.width - (at.x.toInt() + size)
                else -> at.x.toInt()
            }
            val start = lead + anchorOffset(anchor, false, state.viewportSize, size)
            val to = (if (reverse) state.maxValue - start else start).coerceIn(0, state.maxValue)
            if (animated) state.animateScrollTo(to) else state.scrollTo(to)
        }

        /**
         * scrollTo and defaultScrollAnchor on a non-lazy container that
         * scrolls (the EAGER Column and Row, the wrapContent Column): by the
         * rule the lazy routes follow — a number the counted cell, a String a
         * key, on a change of the value, landed by scrollAnchor; the resting
         * anchor the middle or last cell, applied once when the cells arrive
         * — over [cells] (drawnCells). A String that is no key names no cell
         * here: these containers have no lazy item, so the legacy reading is
         * not theirs (4f ruling 2026-09-27, round 12). [reverse]: the reversed
         * EAGER Column rests at its bottom, and its resting anchor is the lazy
         * list's (restingAnchor: top and bottom traded; the cell put where a
         * lazy list's scrollToItem puts its item, the start edge — the bottom
         * here), round 13. [sectionSizes]: the drawn sections' cell counts.
         */
        @Composable
        private fun NonLazyScrollEffects(
            cells: List<Map<String, Any>>,
            scrollTo: Any?,
            collectionId: String?,
            cellIdProperty: String?,
            scrollAnchor: String,
            scrollAnimated: Boolean,
            defaultAnchor: String?,
            state: ScrollState,
            targets: FlowScrollTargets,
            horizontal: Boolean,
            sectionSizes: List<Int> = listOf(cells.size),
            reverse: Boolean = false,
        ) {
            val rtl = horizontal && LocalLayoutDirection.current == LayoutDirection.Rtl
            ScrollToEffect(scrollTo, collectionId, { value, _ -> (scrollCell(value, cells, cellIdProperty, legacy = false) as? ScrollCell.Cell)?.index }) { cell ->
                scrollToPlacedCell(state, targets, cell, scrollAnchor, scrollAnimated, horizontal, rtl, reverse)
            }
            val resting = restingAnchor(defaultAnchor, reverse)
            if (resting == "center" || resting == "bottom") {
                val count = cells.size
                val applied = remember { mutableStateOf(false) }
                LaunchedEffect(count) {
                    if (!applied.value && count > 0) {
                        val cell = when {
                            resting == "center" -> count / 2
                            // Emitted last-first: the first section with cells is drawn last, its last cell at the top.
                            reverse -> sectionSizes.firstOrNull { it > 0 }?.let { it - 1 } ?: (count - 1)
                            else -> count - 1
                        }
                        scrollToPlacedCell(state, targets, cell, if (reverse) "bottom" else "top", false, horizontal, rtl, reverse)
                        applied.value = true
                    }
                }
            }
        }

        /** The drawn sections' cell counts, in section order (drawnCells' sections). */
        internal fun drawnSectionSizes(sections: JsonArray?, collectionDataSource: CollectionDataSource?): List<Int> {
            if (sections == null || collectionDataSource == null) return emptyList()
            return (0 until minOf(sections.size(), collectionDataSource.sections.size))
                .filter { s -> sectionViewName(sections[s].asJsonObject, "cell") != null }
                .map { s -> collectionDataSource.sections[s].cells?.data?.size ?: 0 }
        }

        /**
         * Render paging horizontal collection using HorizontalPager.
         * Each page displays one cell from the data source, with snap-to-page behavior.
         *
         * Supports:
         * - onPageChanged: @{callback} binding for page change notification
         * - pageSpacing: spacing between pages (the horizontal rule: lineSpacing, else itemSpacing)
         * - contentPadding: the Collection's content padding (insets,
         *   insetHorizontal / insetVertical and the safe area, added), which
         *   pads each page's cell inside the page
         */
        @Suppress("UNCHECKED_CAST")
        @Composable
        private fun renderPagingHorizontal(
            a: CollectionAttributes,
            sections: JsonArray?,
            collectionDataSource: CollectionDataSource?,
            cellClassName: String?,
            cellIdProperty: String?,
            data: Map<String, Any>,
            modifier: Modifier,
            contentPadding: PaddingValues,
            pageSpacing: androidx.compose.ui.unit.Dp,
            cellWidth: androidx.compose.ui.unit.Dp?,
            cellHeight: androidx.compose.ui.unit.Dp?,
            gravityAlignment: Alignment,
            onItemAppear: ((Int) -> Unit)? = null,
            collectionId: String? = null,
            userScrollEnabled: Boolean = true,
        ) {
            // Build a flat list of (cellViewName, itemData, cellIndex) for all sections
            data class PageItem(val cellViewName: String?, val itemData: Any?, val cellIndex: Int)

            val pageItems: List<PageItem> = when {
                sections != null && collectionDataSource != null -> {
                    val items = mutableListOf<PageItem>()
                    sections.forEachIndexed { sectionIndex, sectionElement ->
                        val sectionObj = sectionElement.asJsonObject
                        val cellViewName = sectionViewName(sectionObj, "cell")

                        collectionDataSource.sections.getOrNull(sectionIndex)?.let { section ->
                            section.cells?.let { cellData ->
                                cellData.data.forEachIndexed { cellIndex, item ->
                                    items.add(PageItem(cellViewName ?: cellClassName, item, cellIndex))
                                }
                            }
                        }
                    }
                    items
                }
                else -> emptyList()
            }

            // One page per drawn cell (4f ruling, 2026-09-26, round 6): the page
            // count is the pages built above. It summed every DATA section's
            // cells while the pages came from the declared sections, so with
            // more data sections than declared ones a page past them read
            // past the end of pageItems (IndexOutOfBoundsException, measured
            // on the device: DynamicPagingSectionsTest).
            val pageCount = pageItems.size

            if (pageCount == 0) return

            // `currentPage: "@{prop}"` is a TWO-WAY binding. The codegen face
            // wires three legs (collection_component.rb): seed the pager from
            // the bound value, follow later data writes with an animated
            // scroll, and write every page change back into view state so
            // sibling `@{prop}` readers (a page indicator) track the swipe.
            // The write-back leg goes through the layout root's runtime scope
            // (DynamicRuntimeScope) — the dynamic face's stand-in for the
            // generated view's own mutable state.
            val currentPageProp = (TypedAttrs.raw(a.currentPage) as? String)
                ?.takeIf { it.startsWith("@{") && it.endsWith("}") }
                ?.let { ModifierBuilder.extractBindingProperty(it) }
            val boundPage = currentPageProp?.let { (data[it] as? Number)?.toInt() }

            val pagerState = rememberPagerState(
                initialPage = (boundPage ?: 0).coerceIn(0, (pageCount - 1).coerceAtLeast(0))
            ) { pageCount }

            // scrollTo: the page is the cell the value names, counted across
            // the sections (4f ruling 2026-09-27, round 11) — as kjui's codegen
            // pager. The pager read no scrollTo until jsonui-cli 1.9.0.
            @Suppress("UNCHECKED_CAST")
            val pageCells = pageItems.map { (it.itemData as? Map<String, Any>).orEmpty() }
            ScrollToEffect(resolveScrollTo(a, data), collectionId, { value, onLegacy ->
                when (val named = scrollCell(value, pageCells, cellIdProperty)) {
                    null -> null
                    is ScrollCell.Legacy -> named.item.also { onLegacy(named.raw, it) }
                    is ScrollCell.Cell -> named.index
                }?.takeIf { it in 0 until pageCount }
            }) { page ->
                if (a.scrollAnimated != false) pagerState.animateScrollToPage(page) else pagerState.scrollToPage(page)
            }

            // Sync data binding -> pager. While this programmatic scroll is
            // in flight the write-back leg stays quiet: writing intermediate
            // pages back would move `boundPage`, which restarts this effect
            // and cancels its own animation short of the target. (The codegen
            // face's view-state store tolerates the echo; an override scope
            // must not feed it back.)
            val programmaticScroll = remember { mutableStateOf(false) }
            if (boundPage != null) {
                LaunchedEffect(boundPage) {
                    val target = boundPage.coerceIn(0, (pageCount - 1).coerceAtLeast(0))
                    if (pagerState.currentPage != target) {
                        programmaticScroll.value = true
                        try {
                            pagerState.animateScrollToPage(target)
                        } finally {
                            programmaticScroll.value = false
                        }
                    }
                }
            }

            // Resolve the page-change callback from binding: canonical
            // 'onValueChange' with the 'onValueChanged' / 'onPageChanged'
            // alias spellings resolved by the generated parse (aliases are
            // skipped for L1-normalized layouts via canonicalOnly).
            val onPageChangedBinding = TypedAttrs.raw(a.onValueChange) as? String
            val onPageChanged: ((Int) -> Unit)? = onPageChangedBinding
                ?.takeIf { it.startsWith("@{") && it.endsWith("}") }
                ?.let { expr ->
                    // Canonical value resolution of the handler reference
                    // (flat-first, dot paths).
                    DataBindingContext.evaluateExpression(expr, data) as? Function1<Int, Unit>
                }

            // Sync pager -> binding + callback
            val runtimeWriter = LocalDynamicRuntimeWriter.current
            if ((currentPageProp != null && runtimeWriter != null) || onPageChanged != null) {
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { page ->
                        if (currentPageProp != null && !programmaticScroll.value) {
                            runtimeWriter?.invoke(currentPageProp, page)
                        }
                        // Callback parity with codegen: it fires for
                        // programmatic changes too.
                        onPageChanged?.invoke(page)
                    }
                }
            }

            // The content padding pads EACH PAGE'S CELL, inside the page (the
            // ruling 2026-09-28), as SwiftJsonUI pads the page's cell and kjui's
            // codegen pager does: a page stays the pager's width, so no
            // neighbouring page shows in the padding. Through jsonui-cli 1.9.0
            // it was the HorizontalPager's own contentPadding, which narrows
            // every page and shows its neighbours there.
            HorizontalPager(
                state = pagerState,
                modifier = modifier,
                pageSpacing = pageSpacing,
                // scrollEnabled false stops the user's paging only (round 13).
                userScrollEnabled = userScrollEnabled
            ) { pageIndex ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .then(if (cellWidth != null) Modifier.width(cellWidth) else Modifier)
                        .then(if (cellHeight != null) Modifier.height(cellHeight) else Modifier),
                    contentAlignment = gravityAlignment
                ) {
                    when {
                        pageItems.isNotEmpty() -> {
                            val pageItem = pageItems[pageIndex]
                            // The page's place among all the pages: its address
                            // (`<id>_item_<n>`) and its onItemAppear index count
                            // across the sections, as the codegen pager's page and
                            // SwiftJsonUI's do (4f ruling 2026-09-26, round 7).
                            // They restarted per section until then.
                            renderCellView(pageItem.cellViewName, pageItem.itemData, pageIndex, data, onItemAppear, collectionId = collectionId)
                        }
                    }
                }
            }
        }

        /**
         * Render FlowLayout using Compose FlowRow.
         * Items wrap to next row when they exceed available width.
         */
        @OptIn(ExperimentalLayoutApi::class)
        @Composable
        private fun renderFlowLayout(
            sections: JsonArray?,
            collectionDataSource: CollectionDataSource?,
            cellClassName: String?,
            cellIdProperty: String?,
            data: Map<String, Any>,
            modifier: Modifier,
            horizontalSpacing: androidx.compose.ui.unit.Dp,
            verticalSpacing: androidx.compose.ui.unit.Dp,
            flowAlignment: String,
            cellWidth: androidx.compose.ui.unit.Dp?,
            cellHeight: androidx.compose.ui.unit.Dp?,
            gravityAlignment: Alignment,
            onItemAppear: ((Int) -> Unit)? = null,
            collectionId: String? = null,
            scrollTargets: FlowScrollTargets? = null,
        ) {
            val horizontalArrangement = Arrangement.spacedBy(horizontalSpacing, when (flowAlignment) {
                "center" -> Alignment.CenterHorizontally
                "trailing", "end" -> Alignment.End
                else -> Alignment.Start
            })

            // One section's cells, each in its cell box, keyed by its cellId
            // (else its index — unique within the section, which is now the
            // scope the key lives in).
            @Composable
            fun FlowRowScope.sectionCells(sectionIndex: Int, sectionObj: JsonObject) {
                val cellViewName = sectionViewName(sectionObj, "cell")
                // The cell's place among the drawn sections' cells (scrollTo).
                val cellBase = (0 until sectionIndex)
                    .filter { s -> sections?.get(s)?.asJsonObject?.let { sectionViewName(it, "cell") ?: cellClassName } != null }
                    .sumOf { collectionDataSource?.sections?.getOrNull(it)?.cells?.data?.size ?: 0 }
                collectionDataSource?.sections?.getOrNull(sectionIndex)?.cells?.let { cellData ->
                    cellData.data.forEachIndexed { cellIndex, item ->
                        val cellId = (item["cellId"] as? String)
                            ?: (cellIdProperty?.let { item[it] as? String })
                            ?: cellIndex.toString()
                        androidx.compose.runtime.key(cellId) {
                            Box(
                                modifier = Modifier
                                    .then(
                                        if (cellWidth != null) Modifier.width(cellWidth) else Modifier
                                    )
                                    .then(
                                        if (cellHeight != null) Modifier.height(cellHeight) else Modifier
                                    )
                                    .then(
                                        if (scrollTargets != null) Modifier.onGloballyPositioned { scrollTargets.cells[cellBase + cellIndex] = it } else Modifier
                                    ),
                                contentAlignment = gravityAlignment
                            ) {
                                renderCellView(cellViewName ?: cellClassName, item, cellIndex, data, onItemAppear, collectionId = collectionId)
                            }
                        }
                    }
                }
            }

            // The declared sections that have a data section (a declared one
            // with none draws nothing, and no block to space).
            val sectionObjs = if (sections != null && collectionDataSource != null) {
                sections.map { it.asJsonObject }.take(collectionDataSource.sections.size)
            } else {
                emptyList()
            }

            // `clipToBounds` defaults to false on every component (SSoT
            // common.clipToBounds; attribute_semantics 51-E, 2026-08-07:
            // absent means no clip, and hit testing follows clipping).
            // FlowRow does not lay out the rows that exceed its max height —
            // a lazy:"none" flow in a fixed-height box drew three rows and
            // nothing below, while iOS and web drew all six past the box.
            // Measuring it without regard for the incoming max height and
            // aligning the result over that space (wrapContentHeight,
            // unbounded) is what "overflow visible" is in this toolkit;
            // FlowRow's own `overflow` parameter says the same but is
            // deprecated in the resolved foundation-layout. The modifier chain
            // applies `.clipToBounds()` only when declared true, so that
            // declaration is now the one that decides.
            val overflowVisible = modifier.wrapContentHeight(Alignment.Top, unbounded = true)

            // A flow per section (4f ruling, 2026-09-26): with two or more
            // sections, each section's cells wrap in a FlowRow of their own,
            // one under the other, the blocks spaced as the lines
            // (jsonui-cli attribute_semantics.json -> collectionSpacing) —
            // sjui's FlowLayout per section in a VStack, kjui codegen's
            // FlowRow per section in a Column. One FlowRow held every section,
            // so section 2 continued section 1's last line (measured on the
            // lazy, lazy:none and wrapContent routes: DynamicCollectionFlowSectionsTest).
            //
            // A section's declared header and footer (4f ruling 2026-09-26,
            // round 7) are rows of their own, full width, above and below the
            // section's wrap — so a Collection that declares one takes the
            // Column too; header, wrap and footer are spaced as the lines. The
            // flow drew neither until then.
            val sectionEdges = sectionObjs.any { sectionViewName(it, "header") != null || sectionViewName(it, "footer") != null }
            if (sectionObjs.size > 1 || sectionEdges) {
                Column(
                    modifier = overflowVisible,
                    verticalArrangement = Arrangement.spacedBy(verticalSpacing)
                ) {
                    sectionObjs.forEachIndexed { sectionIndex, sectionObj ->
                        val sectionData = collectionDataSource?.sections?.getOrNull(sectionIndex)
                        sectionViewName(sectionObj, "header")?.let { name ->
                            sectionData?.header?.let { edge ->
                                Box(modifier = Modifier.fillMaxWidth()) { renderCellView(name, edge.data, -1, data) }
                            }
                        }
                        if (sectionData?.cells != null && (sectionViewName(sectionObj, "cell") ?: cellClassName) != null) {
                            FlowRow(
                                horizontalArrangement = horizontalArrangement,
                                verticalArrangement = Arrangement.spacedBy(verticalSpacing)
                            ) {
                                sectionCells(sectionIndex, sectionObj)
                            }
                        }
                        sectionViewName(sectionObj, "footer")?.let { name ->
                            sectionData?.footer?.let { edge ->
                                Box(modifier = Modifier.fillMaxWidth()) { renderCellView(name, edge.data, -1, data) }
                            }
                        }
                    }
                }
            } else {
                FlowRow(
                    modifier = overflowVisible,
                    horizontalArrangement = horizontalArrangement,
                    verticalArrangement = Arrangement.spacedBy(verticalSpacing)
                ) {
                    sectionObjs.forEachIndexed { sectionIndex, sectionObj -> sectionCells(sectionIndex, sectionObj) }
                }
            }
        }

        /**
         * Non-lazy Column-based collection for wrapContent height.
         * Avoids crash from nesting LazyVerticalGrid inside another Lazy container.
         */
        @Composable
        private fun renderNonLazy(
            sections: JsonArray?,
            legacyHeader: String? = null,
            legacyFooter: String? = null,
            oneGridForAllSections: Boolean = false,
            defaultColumns: Int = 1,
            columnSpacing: androidx.compose.ui.unit.Dp = 0.dp,
            collectionDataSource: CollectionDataSource?,
            cellIdProperty: String?,
            data: Map<String, Any>,
            modifier: Modifier,
            lineSpacing: androidx.compose.ui.unit.Dp,
            contentPadding: PaddingValues,
            cellHeight: androidx.compose.ui.unit.Dp?,
            gravityAlignment: Alignment,
            onItemAppear: ((Int) -> Unit)? = null
        ,
            chrome: ListChrome? = null,
            collectionId: String? = null,
            scrollTargets: FlowScrollTargets? = null,
            reverseLayout: Boolean = false,
            contentAlignment: Alignment.Vertical = Alignment.Top,
        ) {
            // Under [reverseLayout] (the EAGER Column, round 13) the content is
            // emitted as the reversed lazy grid emits it — the sections last-first
            // — and a ReversedColumn draws it from the bottom up: the picture the
            // lazy grid draws.
            val content: @Composable () -> Unit = {
                // The legacy shape's headerClasses: once, without data,
                // before the cells (sjui's non-lazy stack); its footer after.
                legacyHeader?.let { renderCellView(it, emptyMap<String, Any>(), -1, data) }
                when {
                    sections != null && collectionDataSource != null -> {
                        val sectionObjs = sections.map { it.asJsonObject }
                        fun cellNameOf(s: Int): String? = sectionObjs.getOrNull(s)?.let { sectionViewName(it, "cell") }
                        val gridRows = nonLazyGridRows(
                            cellCounts = sectionObjs.indices.map { collectionDataSource.sections.getOrNull(it)?.cells?.data?.size ?: 0 },
                            sectionColumns = sectionObjs.map { it.get("columns")?.asInt ?: defaultColumns },
                            oneGrid = oneGridForAllSections
                        )
                        // A cell's place among the drawn cells (drawnCells), for scrollTo.
                        val cellBase = IntArray(sectionObjs.size).also { base ->
                            var drawn = 0
                            for (s in sectionObjs.indices) {
                                base[s] = drawn
                                if (cellNameOf(s) != null) drawn += collectionDataSource.sections.getOrNull(s)?.cells?.data?.size ?: 0
                            }
                        }
                        fun placeOf(s: Int, cellIndex: Int): Modifier =
                            if (scrollTargets != null && cellNameOf(s) != null) {
                                Modifier.onGloballyPositioned { scrollTargets.cells[cellBase[s] + cellIndex] = it }
                            } else Modifier
                        val sectionOrder = sections.mapIndexed { index, element -> index to element }
                        (if (reverseLayout) sectionOrder.reversed() else sectionOrder).forEach { (sectionIndex, sectionElement) ->
                            val sectionObj = sectionElement.asJsonObject

                            // Header
                            val headerViewName = sectionViewName(sectionObj, "header")
                            if (headerViewName != null) {
                                collectionDataSource.sections.getOrNull(sectionIndex)?.header?.let { headerData ->
                                    renderCellView(headerViewName, headerData.data, 0, data)
                                }
                            }

                            // Cells: a grid per section, rows of the section's
                            // columns (4f ruling, 2026-09-26 — sjui's non-lazy
                            // grid is a LazyVGrid per section; this was a
                            // Column of one cell per row whatever `columns`
                            // said). The legacy shape's stand-in sections are
                            // ONE grid (sjui's legacy grid): every row is in
                            // the first section's slot.
                            val rowsHere = gridRows.getOrNull(sectionIndex).orEmpty()
                            rowsHere.forEach { row ->
                                if (row.columns == 1) {
                                    val (s, cellIndex) = row.cells.single()
                                    val item = collectionDataSource.sections[s].cells!!.data[cellIndex]
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .then(if (cellHeight != null) Modifier.height(cellHeight) else Modifier)
                                            .then(placeOf(s, cellIndex)),
                                        contentAlignment = gravityAlignment
                                    ) {
                                        renderCellView(cellNameOf(s), item, cellIndex, data, onItemAppear, chrome, collectionId = collectionId)
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(columnSpacing)
                                    ) {
                                        row.cells.forEach { (s, cellIndex) ->
                                            val item = collectionDataSource.sections[s].cells!!.data[cellIndex]
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .then(if (cellHeight != null) Modifier.height(cellHeight) else Modifier)
                                                    .then(placeOf(s, cellIndex)),
                                                contentAlignment = gravityAlignment
                                            ) {
                                                renderCellView(cellNameOf(s), item, cellIndex, data, onItemAppear, chrome, collectionId = collectionId)
                                            }
                                        }
                                        // A short last row keeps the grid's column width.
                                        repeat(row.columns - row.cells.size) { Spacer(Modifier.weight(1f)) }
                                    }
                                }
                            }

                            // Footer
                            val footerViewName = sectionViewName(sectionObj, "footer")
                            if (footerViewName != null) {
                                collectionDataSource.sections.getOrNull(sectionIndex)?.footer?.let { footerData ->
                                    renderCellView(footerViewName, footerData.data, 0, data)
                                }
                            }
                        }
                    }
                }
                legacyFooter?.let { renderCellView(it, emptyMap<String, Any>(), -1, data) }
            }
            if (reverseLayout) {
                ReversedColumn(
                    modifier = modifier.then(Modifier.padding(contentPadding)),
                    spacing = lineSpacing,
                    verticalAlignment = contentAlignment
                ) { content() }
            } else {
                Column(
                    modifier = modifier.then(Modifier.padding(contentPadding)),
                    verticalArrangement = Arrangement.spacedBy(lineSpacing, contentAlignment)
                ) { content() }
            }
        }

        /**
         * Single-lane horizontal LAZY renderer: LazyRow, cells wrap their
         * own cross-axis size (the codegen face's CollectionStack LAZY
         * shape). See the call site for why this is not LazyHorizontalGrid.
         */
        @Composable
        private fun renderLazyRowSingleLane(
            sections: JsonArray?,
            collectionDataSource: CollectionDataSource?,
            cellIdProperty: String?,
            data: Map<String, Any>,
            modifier: Modifier,
            scrollAxisSpacing: androidx.compose.ui.unit.Dp,
            contentPadding: PaddingValues,
            cellWidth: androidx.compose.ui.unit.Dp?,
            cellHeight: androidx.compose.ui.unit.Dp?,
            gravityAlignment: Alignment,
            scrollTo: Any?,
            scrollAnimated: Boolean,
            scrollAnchor: String = "bottom",
            onItemAppear: ((Int) -> Unit)? = null,
            chrome: ListChrome? = null,
            collectionId: String? = null,
            defaultAnchor: String? = null,
            userScrollEnabled: Boolean = true,
            reverseLayout: Boolean = false,
        ) {
            val listState = androidx.compose.foundation.lazy.rememberLazyListState()
            // The row's items are its sections' cells, a cell its item — emitted
            // last-first under reverseLayout, as kjui's horizontal CollectionStack
            // emits them, the row drawing its first item at its end (4f ruling
            // 2026-09-27, round 14; the row drew no reverseLayout until then).
            val rowScrollSections = if (sections != null && collectionDataSource != null) {
                List(sections.size()) { i ->
                    ScrollSection(i, breakBefore = false, header = false, cells = collectionDataSource.sections.getOrNull(i)?.cells?.data, footer = false)
                }.let { if (reverseLayout) it.reversed() else it }
            } else emptyList()
            ScrollToEffect(scrollTo, collectionId, { value, onLegacy -> scrollItemIndex(value, rowScrollSections, cellIdProperty, 0, onLegacy) }) { index ->
                listState.scrollToAnchored(index, scrollAnchor, reverseLayout, scrollAnimated)
            }
            // defaultScrollAnchor, as the grid applies it (round 11's count, once
            // the row has cells): the middle or last cell, where scrollToItem puts
            // it (4f ruling 2026-09-27, round 13), top and bottom traded under
            // reverseLayout (restingAnchor). The row drew none until round 13.
            val restingRowAnchor = restingAnchor(defaultAnchor, reverseLayout)
            if (restingRowAnchor == "center" || restingRowAnchor == "bottom") {
                val anchorCount = rowScrollSections.sumOf { it.cells?.size ?: 0 }
                val anchorApplied = remember { mutableStateOf(false) }
                LaunchedEffect(anchorCount) {
                    if (!anchorApplied.value && anchorCount > 0) {
                        restingAnchorCell(restingRowAnchor, rowScrollSections)?.let { cell ->
                            scrollItemIndex(cell, rowScrollSections, null, 0)?.let { listState.scrollToItem(it) }
                        }
                        anchorApplied.value = true
                    }
                }
            }
            androidx.compose.foundation.lazy.LazyRow(
                modifier = modifier,
                state = listState,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                // A short row sits where defaultScrollAnchor says, at its end when
                // reversed (rowContentAlignment, round 15).
                horizontalArrangement = Arrangement.spacedBy(scrollAxisSpacing, rowContentAlignment(defaultAnchor, reverseLayout)),
                // scrollEnabled false stops the user's scrolling only (round 13).
                userScrollEnabled = userScrollEnabled
            ) {
                when {
                    sections != null && collectionDataSource != null -> {
                        val sectionOrder = sections.mapIndexed { index, element -> index to element }
                        (if (reverseLayout) sectionOrder.reversed() else sectionOrder).forEach { (sectionIndex, sectionElement) ->
                            val sectionObj = sectionElement.asJsonObject
                            val cellViewName = sectionViewName(sectionObj, "cell")
                            collectionDataSource.sections.getOrNull(sectionIndex)?.let { section ->
                                section.cells?.let { cellData ->
                                    items(cellData.data.size) { cellIndex ->
                                        Box(
                                            modifier = Modifier
                                                .then(if (cellWidth != null) Modifier.width(cellWidth) else Modifier)
                                                .then(if (cellHeight != null) Modifier.height(cellHeight) else Modifier),
                                            contentAlignment = gravityAlignment
                                        ) {
                                            renderCellView(
                                                cellViewName, cellData.data[cellIndex], cellIndex,
                                                data, onItemAppear, chrome,
                                                collectionId = collectionId
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Horizontal non-lazy renderer: Row + forEachIndexed, no LazyRow,
         * no horizontalScroll. Expects an already-scrollable parent.
         */
        @Composable
        private fun renderNonLazyRow(
            sections: JsonArray?,
            collectionDataSource: CollectionDataSource?,
            cellIdProperty: String?,
            data: Map<String, Any>,
            modifier: Modifier,
            columnSpacing: androidx.compose.ui.unit.Dp,
            laneSpacing: androidx.compose.ui.unit.Dp = 0.dp,
            defaultColumns: Int = 1,
            contentPadding: PaddingValues,
            cellWidth: androidx.compose.ui.unit.Dp?,
            cellHeight: androidx.compose.ui.unit.Dp?,
            gravityAlignment: Alignment,
            onItemAppear: ((Int) -> Unit)? = null
        ,
            chrome: ListChrome? = null,
            collectionId: String? = null,
            scrollTargets: FlowScrollTargets? = null,
            contentAlignment: Alignment.Horizontal? = null,
        ) {
            Row(
                modifier = modifier.then(Modifier.padding(contentPadding)),
                horizontalArrangement = if (contentAlignment != null) Arrangement.spacedBy(columnSpacing, contentAlignment) else Arrangement.spacedBy(columnSpacing)
            ) {
                when {
                    sections != null && collectionDataSource != null -> {
                        // A cell's place among the drawn cells (drawnCells), for scrollTo.
                        var drawnBefore = 0
                        sections.forEachIndexed { sectionIndex, sectionElement ->
                            val sectionObj = sectionElement.asJsonObject
                            val cellViewName = sectionViewName(sectionObj, "cell")
                            val cellBase = drawnBefore
                            if (cellViewName != null) drawnBefore += collectionDataSource.sections.getOrNull(sectionIndex)?.cells?.data?.size ?: 0
                            fun placeOf(cellIndex: Int): Modifier =
                                if (scrollTargets != null && cellViewName != null) {
                                    Modifier.onGloballyPositioned { scrollTargets.cells[cellBase + cellIndex] = it }
                                } else Modifier

                            // `columns` on a horizontal Collection is its lanes, and
                            // a section's own `columns` its block's (4f ruling,
                            // 2026-09-26) — as the lazy route's
                            // LazyHorizontalGrid draws them. This route drew one
                            // Row whatever `columns` said. Each section is a
                            // block of its own, so it starts a new column.
                            val lanes = maxOf(1, sectionObj.get("columns")?.asInt ?: defaultColumns)
                            collectionDataSource.sections.getOrNull(sectionIndex)?.let { section ->
                                section.cells?.let { cellData ->
                                    if (lanes > 1) {
                                        LaneGrid(lanes = lanes, laneSpacing = laneSpacing, columnSpacing = columnSpacing) {
                                            cellData.data.forEachIndexed { cellIndex, item ->
                                                // The lazy route's grid cell: a declared size binds
                                                // (anchored, clipped); otherwise the cell fills its lane.
                                                Box(
                                                    modifier = Modifier
                                                        .then(
                                                            if (cellWidth != null || cellHeight != null) {
                                                                Modifier.wrapContentSize(align = Alignment.TopStart)
                                                            } else Modifier
                                                        )
                                                        .then(if (cellWidth != null) Modifier.width(cellWidth) else Modifier)
                                                        .then(if (cellHeight != null) Modifier.height(cellHeight) else Modifier)
                                                        .then(
                                                            if (cellWidth != null || cellHeight != null) Modifier.clipToBounds()
                                                            else Modifier.fillMaxSize()
                                                        )
                                                        .then(placeOf(cellIndex)),
                                                    contentAlignment = gravityAlignment
                                                ) {
                                                    renderCellView(cellViewName, item, cellIndex, data, onItemAppear, chrome, collectionId = collectionId)
                                                }
                                            }
                                        }
                                    } else {
                                    cellData.data.forEachIndexed { cellIndex, item ->
                                        Box(
                                            modifier = Modifier
                                                .then(if (cellWidth != null) Modifier.width(cellWidth) else Modifier)
                                                .then(if (cellHeight != null) Modifier.height(cellHeight) else Modifier)
                                                .then(placeOf(cellIndex)),
                                            contentAlignment = gravityAlignment
                                        ) {
                                            renderCellView(cellViewName, item, cellIndex, data, onItemAppear, chrome, collectionId = collectionId)
                                        }
                                    }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * A horizontal section block of [lanes] lanes, for the routes that do
         * not scroll lazily (eager, lazy:none): cells fill a column top to
         * bottom, then the next — LazyHorizontalGrid's order. With a bounded
         * height each lane is an equal share of it, as GridCells.Fixed gives
         * the lazy route; unbounded, a lane is as tall as its tallest cell.
         * A column is as wide as its widest cell. See [laneGridPlacement].
         */
        @Composable
        private fun LaneGrid(
            lanes: Int,
            laneSpacing: androidx.compose.ui.unit.Dp,
            columnSpacing: androidx.compose.ui.unit.Dp,
            content: @Composable () -> Unit
        ) {
            Layout(content = content) { measurables, constraints ->
                val laneSpacingPx = laneSpacing.roundToPx()
                val columnSpacingPx = columnSpacing.roundToPx()
                val laneHeight = if (constraints.hasBoundedHeight) {
                    ((constraints.maxHeight - laneSpacingPx * (lanes - 1)) / lanes).coerceAtLeast(0)
                } else null
                val cellConstraints = Constraints(
                    minHeight = laneHeight ?: 0,
                    maxHeight = laneHeight ?: Constraints.Infinity
                )
                val placeables = measurables.map { it.measure(cellConstraints) }
                val placement = laneGridPlacement(
                    widths = placeables.map { it.width },
                    heights = placeables.map { it.height },
                    lanes = lanes,
                    laneSpacing = laneSpacingPx,
                    columnSpacing = columnSpacingPx,
                    laneHeight = laneHeight
                )
                layout(
                    placement.width.coerceIn(constraints.minWidth, constraints.maxWidth),
                    placement.height.coerceIn(constraints.minHeight, constraints.maxHeight)
                ) {
                    placeables.forEachIndexed { i, placeable -> placeable.place(placement.x[i], placement.y[i]) }
                }
            }
        }

        private fun LazyGridScope.generateCollectionItems(
            sections: JsonArray?,
            collectionDataSource: CollectionDataSource?,
            cellClassName: String?,
            cellIdProperty: String?,
            data: Map<String, Any>,
            chrome: ListChrome? = null,
            cellWidth: androidx.compose.ui.unit.Dp?,
            cellHeight: androidx.compose.ui.unit.Dp?,
            gridColumns: Int,
            defaultColumns: Int,
            gravityAlignment: Alignment,
            reverseLayout: Boolean = false,
            onItemAppear: ((Int) -> Unit)? = null,
            collectionId: String? = null,
            breakRowsBetweenSections: Boolean = false,
        ) {

            when {
                // If we have sections and data source
                sections != null && collectionDataSource != null -> {
                    // When reverseLayout is true, reverse section order so that
                    // JSON definition order matches iOS display (iOS cannot reverse).
                    // Pair each section with its original index before any reversal —
                    // JsonArray.indexOf uses structural equality, so sections with
                    // identical JSON (e.g. two sections that share the same cell
                    // template) would collapse to the same index and produce
                    // duplicate LazyGrid keys.
                    val indexedSections = sections.toList().mapIndexed { idx, section -> idx to section }
                    val orderedSections = if (reverseLayout) {
                        indexedSections.reversed()
                    } else {
                        indexedSections
                    }
                    // Declared sections are a grid each (4f ruling,
                    // 2026-09-26; sjui's LazyVGrid per section): a section
                    // after the first starts a new row. The sections share
                    // one LazyVerticalGrid, whose items fill the current row
                    // across a section boundary, so an empty item takes the
                    // rest of a part-filled row first — in that row, so no
                    // row and no line spacing is added. On the horizontal
                    // grid the same holds in lanes: a section starts a new
                    // column. A header (full span)
                    // already starts one. The legacy shape's stand-in
                    // sections stay one grid, as sjui's legacy grid is.
                    val breakSpans = if (breakRowsBetweenSections) {
                        orderedBreakSpans(orderedSections, collectionDataSource, gridColumns, defaultColumns)
                    } else null
                    orderedSections.forEachIndexed { orderIndex, (sectionIndex, sectionJson) ->
                        val sectionObj = sectionJson.asJsonObject
                        val cellViewName = sectionViewName(sectionObj, "cell")
                        val headerViewName = sectionViewName(sectionObj, "header")
                        val footerViewName = sectionViewName(sectionObj, "footer")
                        val sectionColumns = sectionObj.get("columns")?.asInt ?: defaultColumns

                        // Calculate span for items in this section
                        val itemSpan = gridColumns / sectionColumns

                        val breakSpan = breakSpans?.get(orderIndex) ?: 0
                        if (breakSpan > 0) {
                            item(span = { GridItemSpan(breakSpan) }, contentType = "sectionBreak") {
                                Spacer(Modifier)
                            }
                        }

                        collectionDataSource.sections.getOrNull(sectionIndex)?.let { section ->
                            // Render header if present
                            if (headerViewName != null && section.header != null) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        val headerData = section.header?.data ?: emptyMap()
                                        renderCellView(headerViewName, headerData, -1, data)
                                    }
                                }
                            }

                            // Render cells
                            section.cells?.let { cellData ->
                                // Build identified items if cellIdProperty is set
                                // The item key: the cell's key in section 0,
                                // "<section>:<key>" in a later one. One lazy
                                // grid holds every section and Compose throws on
                                // a key two items share ("Key … was already
                                // used") — two sections sharing a cell key took
                                // the grid down once both cells were composed
                                // (measured, 4f round 10). kjui's codegen keys
                                // its items the same way (jsonui-cli 1.9.0).
                                val identifiedItems = if (cellIdProperty != null) {
                                    val ids = uniqueKeys(cellData.data.mapIndexed { index, item ->
                                        (item["cellId"] as? String) ?: (item[cellIdProperty] as? String) ?: index.toString()
                                    })
                                    cellData.data.mapIndexed { index, item ->
                                        val id = ids[index]
                                        IdentifiedCellItem(id = if (sectionIndex == 0) id else "$sectionIndex:$id", index = index, data = item)
                                    }
                                } else null

                                if (identifiedItems != null) {
                                    // Use key-based items for stable identity
                                    items(
                                        count = identifiedItems.size,
                                        key = { identifiedItems[it].id },
                                        span = if (itemSpan > 1) { { GridItemSpan(itemSpan) } } else null
                                    ) { idx ->
                                        val identified = identifiedItems[idx]
                                        // A declared cell size binds on BOTH axes (it was axis-gated
                                        // here, so cellWidth was unread on the vertical path), and the
                                        // overflow clips — the same anchored-and-clipped picture the
                                        // codegen face and web draw (Collection_cellWidth__static
                                        // parity d=30, run 31202080745). Size first: a fill would pin
                                        // the constraints and the size after it could not shrink them.
                                        Box(
                                            // wrapContentSize FIRST: a lazy-grid lane hands the item
                                            // TIGHT constraints (min == max == lane width), and
                                            // Modifier.width coerces itself into the incoming
                                            // constraints — so the declared cell size was a no-op here
                                            // (run 31234163967, Collection_cellWidth__static still
                                            // d=30 after the first fix). Resetting to loose and
                                            // anchoring topStart lets the size bind and keeps the
                                            // web picture (leading-edge sliver).
                                            modifier = Modifier
                                                .then(
                                                    if (cellWidth != null || cellHeight != null) {
                                                        Modifier.wrapContentSize(align = Alignment.TopStart)
                                                    } else Modifier
                                                )
                                                .then(if (cellWidth != null) Modifier.width(cellWidth) else Modifier)
                                                .then(if (cellHeight != null) Modifier.height(cellHeight) else Modifier)
                                                .then(
                                                    if (cellWidth != null || cellHeight != null) Modifier.clipToBounds()
                                                    else Modifier.fillMaxSize()
                                                )
                                                .animateItem(),
                                            contentAlignment = gravityAlignment
                                        ) {
                                            renderCellView(cellViewName ?: cellClassName, identified.data, identified.index, data, onItemAppear, chrome, collectionId = collectionId)
                                        }
                                    }
                                } else {
                                    items(
                                        count = cellData.data.size,
                                        span = if (itemSpan > 1) { { GridItemSpan(itemSpan) } } else null
                                    ) { cellIndex ->
                                        val item = cellData.data[cellIndex]
                                        Box(
                                            modifier = Modifier
                                                .then(
                                                    if (cellWidth != null || cellHeight != null) {
                                                        Modifier.wrapContentSize(align = Alignment.TopStart)
                                                    } else Modifier
                                                )
                                                .then(if (cellWidth != null) Modifier.width(cellWidth) else Modifier)
                                                .then(if (cellHeight != null) Modifier.height(cellHeight) else Modifier)
                                                .then(
                                                    if (cellWidth != null || cellHeight != null) Modifier.clipToBounds()
                                                    else Modifier.fillMaxSize()
                                                ),
                                            contentAlignment = gravityAlignment
                                        ) {
                                            renderCellView(cellViewName ?: cellClassName, item, cellIndex, data, onItemAppear, chrome, collectionId = collectionId)
                                        }
                                    }
                                }
                            }

                            // Render footer if present
                            if (footerViewName != null && section.footer != null) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        val footerData = section.footer?.data ?: emptyMap()
                                        renderCellView(footerViewName, footerData, -2, data)
                                    }
                                }
                            }
                        }
                    }
                }
                // Declaration-faithful (2026-08-02 ruling): no cells/sections
                // declared → nothing rendered. The old 10-item
                // "Item $index" placeholder Cards were undeclared behavior
                // (the container itself still renders with its declared
                // size/background via the surrounding modifier chain).
                else -> {}
            }
        }

        private fun calculateLCM(numbers: List<Int>): Int {
            fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
            fun lcm(a: Int, b: Int): Int = (a * b) / gcd(a, b)

            return numbers.reduce { acc, n -> lcm(acc, n) }
        }

        /** Typed replacement for the legacy JsonArray string extraction. */
        private fun extractStringList(values: List<Any?>?): List<String> =
            values.orEmpty().filterIsInstance<String>()

        /** The routes a Collection is drawn by, as [cellPlan] tells them apart. */
        internal enum class CellRoute {
            FLOW, NON_LAZY_ROW, NON_LAZY_COLUMN, PAGING, LAZY_ROW, LAZY_HORIZONTAL_GRID, LAZY_VERTICAL_GRID
        }

        /**
         * What each route draws its cells from. With `sections`, the declared
         * ones, on every route, as before. Without — the legacy shape, the
         * cells / header / footer named on the Collection itself — sjui's
         * codegen table (4f ruling, 2026-09-26; SwiftJsonUI's Dynamic reads
         * it the same way):
         *
         * - a single cellClass draws every data section on the vertical
         *   routes (sjui's List and grid, one grid for all of them), the
         *   first data section on the horizontal, flow and paging routes
         *   (paging: 4f ruling, 2026-09-26, round 6 — it drew nothing until
         *   then, on every face);
         * - several cellClasses name no cell (the build refuses that layout);
         * - headerClasses / footerClasses are drawn once, without data, on
         *   the vertical routes only — not horizontal, flow or paging;
         * - with no items source the legacy shape still draws its container,
         *   header and footer (the codegen emits them whatever `items` says):
         *   an empty source stands in. A Collection that declares neither
         *   keeps no source, as before.
         *
         * Until this, a Collection without `sections` had no data source at
         * all, so its cellClasses drew nothing, and headerClasses /
         * footerClasses were read into locals nothing used.
         */
        internal class CellPlan(
            private val declaredSections: JsonArray?,
            val dataSource: CollectionDataSource?,
            val legacyCell: String?,
            val legacyHeader: String?,
            val legacyFooter: String?,
        ) {
            private val hasSections = declaredSections != null && declaredSections.size() > 0

            /** True when the sections are the layout's own (not the legacy shape's stand-ins). */
            val hasDeclaredSections: Boolean get() = hasSections

            fun sectionsFor(route: CellRoute): JsonArray? {
                if (hasSections) return declaredSections
                val cell = legacyCell ?: return if (dataSource != null) JsonArray() else declaredSections
                val count = when (route) {
                    CellRoute.FLOW, CellRoute.NON_LAZY_ROW, CellRoute.LAZY_ROW, CellRoute.LAZY_HORIZONTAL_GRID, CellRoute.PAGING ->
                        minOf(1, dataSource?.sections?.size ?: 0)
                    else -> dataSource?.sections?.size ?: 0
                }
                return JsonArray().apply {
                    repeat(count) { add(JsonObject().apply { addProperty("cell", cell) }) }
                }
            }

            private fun drawsEdges(route: CellRoute) =
                route == CellRoute.NON_LAZY_COLUMN || route == CellRoute.LAZY_VERTICAL_GRID

            fun headerFor(route: CellRoute): String? = legacyHeader?.takeIf { drawsEdges(route) }
            fun footerFor(route: CellRoute): String? = legacyFooter?.takeIf { drawsEdges(route) }
        }

        /**
         * Spacing on a horizontal Collection (4f ruling, 2026-09-26): along
         * the scroll axis lineSpacing, else itemSpacing; between lanes
         * columnSpacing, else itemSpacing. The SSoT words them for a vertical
         * grid ("Spacing between rows" / "Spacing between columns"); they
         * were declared from UIKit's flow layout (lineSpacing ->
         * minimumLineSpacing, columnSpacing -> minimumInteritemSpacing), in
         * which a horizontally scrolling grid's lines are its columns.
         * [defaultSpacing] is itemSpacing (then the legacy `spacing` extra).
         */
        internal data class HorizontalSpacing(val betweenLanes: Float, val alongScroll: Float)

        internal fun horizontalSpacing(a: CollectionAttributes, defaultSpacing: Float): HorizontalSpacing =
            HorizontalSpacing(
                betweenLanes = a.columnSpacing?.toFloat() ?: defaultSpacing,
                alongScroll = a.lineSpacing?.toFloat() ?: defaultSpacing
            )

        /** Where each cell of a horizontal lane block goes, and the block's size. */
        internal data class LanePlacement(val x: List<Int>, val y: List<Int>, val width: Int, val height: Int)

        /**
         * Column-major placement of cells in [lanes] lanes: cell i in column
         * i / lanes, lane i % lanes. A column is as wide as its widest cell;
         * a lane is [laneHeight] tall when the height is bounded, else as tall
         * as its tallest cell. [columnSpacing] between columns (the scroll
         * axis), [laneSpacing] between lanes.
         */
        internal fun laneGridPlacement(
            widths: List<Int>,
            heights: List<Int>,
            lanes: Int,
            laneSpacing: Int,
            columnSpacing: Int,
            laneHeight: Int?
        ): LanePlacement {
            val n = widths.size
            if (n == 0) return LanePlacement(emptyList(), emptyList(), 0, 0)
            val l = maxOf(1, lanes)
            val columns = (n + l - 1) / l
            val columnWidths = IntArray(columns)
            for (i in 0 until n) columnWidths[i / l] = maxOf(columnWidths[i / l], widths[i])
            val laneHeights = IntArray(l) { laneHeight ?: 0 }
            if (laneHeight == null) for (i in 0 until n) laneHeights[i % l] = maxOf(laneHeights[i % l], heights[i])
            val columnX = IntArray(columns)
            var x = 0
            for (c in 0 until columns) {
                columnX[c] = x
                x += columnWidths[c] + if (c < columns - 1) columnSpacing else 0
            }
            val laneY = IntArray(l)
            var y = 0
            for (r in 0 until l) {
                laneY[r] = y
                y += laneHeights[r] + if (r < l - 1) laneSpacing else 0
            }
            return LanePlacement(List(n) { columnX[it / l] }, List(n) { laneY[it % l] }, x, y)
        }

        /** What a section puts into the vertical grid, for [sectionBreakSpans]. */
        internal data class SectionShape(val cells: Int, val itemSpan: Int, val header: Boolean, val footer: Boolean)

        /**
         * Before each section, in drawing order, the span of an empty item
         * that takes the rest of a part-filled row, so the section starts a
         * row of its own; 0 where none is needed — the first section, a
         * section after a full row or after a footer, a section that opens
         * with its header (full span), a section that draws nothing. Every
         * section starts at the beginning of a row, so it leaves
         * `cells * itemSpan mod gridColumns` of its last row filled.
         */
        internal fun sectionBreakSpans(shapes: List<SectionShape>, gridColumns: Int): List<Int> {
            var used = 0
            return shapes.map { s ->
                if (!s.header && !s.footer && s.cells == 0) return@map 0
                val filler = if (!s.header && used != 0) gridColumns - used else 0
                used = if (s.footer) 0 else (s.cells * s.itemSpan) % gridColumns
                filler
            }
        }

        /** [sectionBreakSpans] for the sections as [generateCollectionItems] orders them. */
        internal fun orderedBreakSpans(
            orderedSections: List<Pair<Int, com.google.gson.JsonElement>>,
            collectionDataSource: CollectionDataSource,
            gridColumns: Int,
            defaultColumns: Int,
        ): List<Int> = sectionBreakSpans(
            orderedSections.map { (sectionIndex, sectionJson) ->
                val obj = sectionJson.asJsonObject
                val section = collectionDataSource.sections.getOrNull(sectionIndex)
                SectionShape(
                    cells = section?.cells?.data?.size ?: 0,
                    itemSpan = gridColumns / (obj.get("columns")?.asInt ?: defaultColumns),
                    header = obj.get("header") != null && section?.header != null,
                    footer = obj.get("footer") != null && section?.footer != null
                )
            },
            gridColumns
        )

        /**
         * A section as a lazy content emits it, for [scrollItemIndex]: its
         * declared index, whether an empty item precedes it (the grid's row
         * break), whether it draws a header and a footer item, and its cells
         * (null when its data has none).
         */
        internal data class ScrollSection(
            val section: Int,
            val breakBefore: Boolean,
            val header: Boolean,
            val cells: List<Map<String, Any>>?,
            val footer: Boolean,
        )

        /** The grid's sections as [generateCollectionItems] emits them. */
        internal fun emittedScrollSections(
            sections: JsonArray?,
            collectionDataSource: CollectionDataSource?,
            gridColumns: Int,
            defaultColumns: Int,
            reverseLayout: Boolean,
            breakRowsBetweenSections: Boolean,
        ): List<ScrollSection> {
            if (sections == null || collectionDataSource == null) return emptyList()
            val indexed = sections.toList().mapIndexed { idx, section -> idx to section }
            val ordered = if (reverseLayout) indexed.reversed() else indexed
            val breaks = if (breakRowsBetweenSections) orderedBreakSpans(ordered, collectionDataSource, gridColumns, defaultColumns) else null
            return ordered.mapIndexed { orderIndex, (sectionIndex, sectionJson) ->
                val obj = sectionJson.asJsonObject
                val section = collectionDataSource.sections.getOrNull(sectionIndex)
                ScrollSection(
                    section = sectionIndex,
                    breakBefore = (breaks?.get(orderIndex) ?: 0) > 0,
                    header = section != null && sectionViewName(obj, "header") != null && section.header != null,
                    cells = section?.cells?.data,
                    footer = section != null && sectionViewName(obj, "footer") != null && section.footer != null
                )
            }
        }

        /**
         * The lazy item a scrollTo value names (4f ruling 2026-09-27;
         * jsonui-cli 1.9.0, the SSoT's Collection.scrollTo), or null for none:
         * - a number is a CELL counted across the drawn sections in section
         *   order — a header or footer item, and a grid's row break, is not a
         *   cell — with or without cellIdProperty;
         * - a String is the first cell in section order whose key — its
         *   "cellId", else its cellIdProperty value — it is; one that is no
         *   cell's key and reads `<digits>` / `<digits>#…` is the legacy lazy
         *   item index (the SSoT says why, and that this reading is the Kotlin
         *   paths' own), said through [onLegacy] (scrollCell).
         * [emitted] is the sections in the order the lazy content emits them
         * (reversed under reverseLayout); [leadingItems] the items before the
         * first (the legacy shape's header). Until jsonui-cli 1.9.0 the value
         * was the lazy item index itself.
         */
        internal fun scrollItemIndex(
            value: Any?,
            emitted: List<ScrollSection>,
            cellIdProperty: String?,
            leadingItems: Int = 0,
            onLegacy: ((String, Int) -> Unit)? = null,
        ): Int? {
            val inOrder = emitted.sortedBy { it.section }
            val cell = when (val named = scrollCell(value, inOrder.flatMap { it.cells.orEmpty() }, cellIdProperty)) {
                null -> return null
                is ScrollCell.Legacy -> {
                    onLegacy?.invoke(named.raw, named.item)
                    return named.item
                }
                is ScrollCell.Cell -> named.index
            }
            var rest = cell
            val target = inOrder.firstOrNull { s ->
                val size = s.cells?.size ?: 0
                if (rest < size) true else { rest -= size; false }
            } ?: return null
            var item = leadingItems
            for (s in emitted) {
                if (s.breakBefore) item += 1
                if (s.header) item += 1
                if (s === target) return item + rest
                item += s.cells?.size ?: 0
                if (s.footer) item += 1
            }
            return null
        }

        /** What a scrollTo value names: a cell, or (the legacy reading) a lazy item index. */
        internal sealed class ScrollCell {
            data class Cell(val index: Int) : ScrollCell()
            data class Legacy(val raw: String, val item: Int) : ScrollCell()
        }

        /** `<digits>` or `<digits>#<anything>` → the digits, else null. */
        internal fun leadingDigits(raw: String): Int? =
            raw.substringBefore("#").takeIf { it.isNotEmpty() && it.all(Char::isDigit) }?.toIntOrNull()

        /**
         * The cell a scrollTo value names among [cells] (the drawn cells in
         * section order), or null. The value's class decides (4f ruling
         * 2026-09-27, round 14; the SSoT's Collection.scrollTo): a number is
         * the cell's index, with or without cellIdProperty; a String is a key —
         * the first cell whose "cellId", else its [cellIdProperty] value when
         * one is set, it is (a cell with neither has no key). A String that is
         * no cell's key and is `<digits>` / `<digits>#…` is the legacy lazy
         * item index when [legacy] (round 11). Until jsonui-cli 1.9.0 a String
         * without cellIdProperty was read as a cell's index.
         */
        internal fun scrollCell(value: Any?, cells: List<Map<String, Any>>, cellIdProperty: String?, legacy: Boolean = true): ScrollCell? {
            val index = when (value) {
                is Number -> value.toInt()
                is String -> {
                    if (value.isEmpty()) return null
                    val keyed = cells.indexOfFirst {
                        ((it["cellId"] as? String) ?: cellIdProperty?.let { prop -> it[prop] as? String }) == value
                    }
                    if (keyed < 0) {
                        val item = leadingDigits(value) ?: return null
                        return if (legacy) ScrollCell.Legacy(value, item) else null
                    }
                    keyed
                }
                else -> return null
            }
            return if (index in cells.indices) ScrollCell.Cell(index) else null
        }

        /**
         * Two cells of one section may share a key; one lazy list may not
         * (Compose throws "Key … was already used"). A key an earlier cell of
         * the section took gets "#2", "#3"… — CellIdGenerator's dedupe, and
         * kjui's codegen (4f ruling 2026-09-27, round 11).
         */
        internal fun uniqueKeys(keys: List<String>): List<String> {
            val seen = HashSet<String>()
            return keys.map { k -> if (seen.add(k)) k else generateSequence(2) { it + 1 }.map { "$k#$it" }.first { seen.add(it) } }
        }

        /** One row of a non-lazy grid: its (section, cell) pairs, and the columns it is laid out in. */
        internal data class GridRow(val cells: List<Pair<Int, Int>>, val columns: Int)

        /**
         * The non-lazy route's rows, per section: each section a grid of its
         * own columns. With [oneGrid] (the legacy shape) every data
         * section's cells are one grid in the first section's slot, in the
         * first section's columns.
         */
        internal fun nonLazyGridRows(cellCounts: List<Int>, sectionColumns: List<Int>, oneGrid: Boolean): List<List<GridRow>> {
            fun rows(cells: List<Pair<Int, Int>>, columns: Int): List<GridRow> {
                val c = maxOf(1, columns)
                return cells.chunked(c).map { GridRow(it, c) }
            }
            if (oneGrid) {
                val all = cellCounts.flatMapIndexed { s, n -> (0 until n).map { s to it } }
                return cellCounts.indices.map { s -> if (s == 0) rows(all, sectionColumns.firstOrNull() ?: 1) else emptyList() }
            }
            return cellCounts.mapIndexed { s, n -> rows((0 until n).map { s to it }, sectionColumns.getOrElse(s) { 1 }) }
        }

        /** A cellClasses / headerClasses / footerClasses entry: a name, or `{"className": …}`. */
        internal fun declaredClassName(entry: Any?): String? = when (entry) {
            is String -> entry
            is Map<*, *> -> entry["className"] as? String
            else -> null
        }?.takeIf { it.isNotEmpty() }

        /**
         * The data source `items` binds. Collection.items is a
         * CollectionDataSource or an array (attribute_definitions.json; 4f
         * ruling, 2026-09-26): with no `sections` and a single declared cell,
         * a list is ONE section of that cell, drawn on the routes a
         * one-section data source takes. The codegens decide by the layout's
         * data declaration; this renderer by the value's shape. A list was
         * not a CollectionDataSource, so it drew no cell (measured on
         * e33f493).
         */
        internal fun boundSource(a: CollectionAttributes, sections: JsonArray?, value: Any?): CollectionDataSource? {
            if (value is CollectionDataSource) return value
            if (value !is List<*> || (sections != null && sections.size() > 0)) return null
            val cell = a.cellClasses?.singleOrNull()?.let(::declaredClassName) ?: return null
            return CollectionDataSource(
                sections = listOf(CollectionDataSection(cells = CollectionDataSection.CellData(cell, value.mapNotNull(::cellMap))))
            )
        }

        /**
         * One element of an `items` list as a cell's data: a map as it is,
         * a generated Data class by its `toMap()` (what the codegen calls),
         * anything else no cell.
         */
        internal fun cellMap(element: Any?): Map<String, Any>? {
            val map = when (element) {
                null -> return null
                is Map<*, *> -> element
                else -> element.javaClass.methods
                    .firstOrNull { it.name == "toMap" && it.parameterCount == 0 }
                    ?.invoke(element) as? Map<*, *>
            } ?: return null
            @Suppress("UNCHECKED_CAST")
            return map.filter { (k, v) -> k is String && v != null } as Map<String, Any>
        }

        /**
         * A section's `cell` / `header` / `footer`: the name of the layout it
         * draws (declared a string). A node written there instead — an inline
         * cell, which is not declared — is not drawn, and a debuggable build
         * names it once per key. Every read of the three went through
         * `sectionObj.get(key)?.asString`, which Gson throws on for a
         * JsonObject, a JsonArray and a JSON null (measured:
         * UnsupportedOperationException), so the whole Collection failed to
         * compose. A number is read as its text, as asString read it.
         */
        internal fun sectionViewName(section: JsonObject, key: String): String? {
            val value = section.get(key) ?: return null
            if (value.isJsonPrimitive) return value.asString
            if (value.isJsonObject || value.isJsonArray) {
                val message = "Collection section $key is a node, not the name of a layout " +
                    "(inline cells are not declared): not drawn"
                if (com.kotlinjsonui.dynamic.DebugDiagnostics.isAppDebuggable(null) &&
                    inlineSectionWarned.add(key)
                ) {
                    inlineSectionSink?.invoke(message)
                    android.util.Log.w("DynamicCollection", message)
                }
            }
            return null
        }

        /** Test hook: receives every inline-section message. */
        internal var inlineSectionSink: ((String) -> Unit)? = null
        private val inlineSectionWarned = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

        internal fun cellPlan(a: CollectionAttributes, sections: JsonArray?, boundSource: CollectionDataSource?): CellPlan {
            val hasSections = sections != null && sections.size() > 0
            val legacyCell = if (hasSections) null else a.cellClasses?.singleOrNull()?.let(::declaredClassName)
            val legacyHeader = if (hasSections) null else a.headerClasses?.firstOrNull()?.let(::declaredClassName)
            val legacyFooter = if (hasSections) null else a.footerClasses?.firstOrNull()?.let(::declaredClassName)
            val isLegacyShape = legacyCell != null || legacyHeader != null || legacyFooter != null
            val dataSource = when {
                isLegacyShape -> boundSource ?: CollectionDataSource()
                sections != null -> boundSource
                else -> null
            }
            return CellPlan(sections, dataSource, legacyCell, legacyHeader, legacyFooter)
        }

        /**
         * `listStyle` chrome + `hideSeparator` (51-E). The chrome wraps each
         * cell on the LAZY grid path — the one the conformance fixtures
         * measure; flow/paging/non-lazy routes keep plain. grouped = surface
         * background + divider; insetGrouped = + horizontal inset and rounded
         * corners; sidebar = softer surface, tighter corners. The four values
         * are pairwise-distinct on purpose, and hideSeparator only has a
         * separator to hide once a chrome draws one (the declaration's own
         * contract: a NO-OP where the container draws no separators).
         */
        internal data class ListChrome(val style: String, val hideSeparator: Boolean) {
            val plain: Boolean get() = style == "plain" || style.isEmpty()
        }

        @Composable
        private fun ChromedCell(
            chrome: ListChrome?,
            content: @Composable () -> Unit
        ) {
            com.kotlinjsonui.components.CollectionCellChrome(
                style = chrome?.style,
                hideSeparator = chrome?.hideSeparator == true,
                content = content
            )
        }

        @Composable
        private fun renderCellView(
            cellClassName: String?,
            item: Any?,
            index: Int,
            data: Map<String, Any>,
            onItemAppear: ((Int) -> Unit)? = null,
            chrome: ListChrome? = null,
            collectionId: String? = null
        ) {
            ChromedCell(chrome) {
                renderCellViewInner(cellClassName, item, index, data, onItemAppear, collectionId)
            }
        }

        /// `{collectionId}_item_{index}` — the address `tapItem` and
        /// `waitFor` resolve, and the spelling the static codegen emits.
        /// Dynamic emitted nothing here, on any layout, so a fixture (which
        /// always runs through Dynamic) could never check the contract the
        /// codegen is expected to keep.
        ///
        /// Written into the cell root's `id` rather than wrapped in a tagged
        /// Box: dynamic already turns a root `id` into a testTag, so this
        /// adds no layout node — and a Box under FlowRow would take part in
        /// measurement. Threading a modifier down instead would mean putting
        /// one through all 35 component entry points, which is a different
        /// change.
        ///
        /// The cell's own root id is replaced. That is already true on iOS —
        /// the collection's outer identifier wins there too, measured on a
        /// consumer face — so this makes the two faces agree rather than
        /// introducing a new asymmetry. Headers and footers pass no
        /// collectionId and keep their own ids.
        private fun withCellAddress(
            cellJson: JsonObject,
            collectionId: String?,
            index: Int
        ): JsonObject {
            if (collectionId.isNullOrEmpty() || index < 0) return cellJson
            val copy = cellJson.deepCopy()
            copy.addProperty("id", "${collectionId}_item_$index")
            return copy
        }

        // Every cell render is a layout root on the dynamic face: it gets its
        // own runtime write channel (DynamicRuntimeScope) so two-way bindings
        // inside the cell layout (a nested paging collection's currentPage,
        // ...) reach sibling readers in the SAME cell, mirroring the codegen
        // face where the generated cell view owns that state.
        @Composable
        private fun CellRoot(cellJson: JsonObject, cellData: Map<String, Any>) {
            DynamicRuntimeScope(cellData) { effectiveData ->
                DynamicView(json = cellJson, data = effectiveData)
            }
        }

        @Composable
        private fun renderCellViewInner(
            cellClassName: String?,
            item: Any?,
            index: Int,
            data: Map<String, Any>,
            onItemAppear: ((Int) -> Unit)? = null,
            collectionId: String? = null
        ) {
            // Call onItemAppear callback when this cell appears
            if (onItemAppear != null) {
                LaunchedEffect(index) {
                    onItemAppear(index)
                }
            }

            if (cellClassName == null) {
                // Default cell
                Card(
                    modifier = Modifier.padding(4.dp).fillMaxWidth()
                ) {
                    Text(
                        text = item?.toString() ?: "",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                return
            }

            // Convert cell class name to JSON file name
            val cellJsonName = cellClassName
                .replace(Regex("([a-z])([A-Z])")) { "${it.groupValues[1]}_${it.groupValues[2].lowercase()}" }
                .lowercase()

            // Initialize DynamicLayoutLoader with context
            val context = LocalContext.current
            DynamicLayoutLoader.init(context)

            // Load the cell JSON from assets
            val cellJson = DynamicLayoutLoader.loadLayout(cellJsonName)

            if (cellJson != null) {
                // Create item data context for the cell
                val cellData = when (item) {
                    is Map<*, *> -> {
                        // If item is already a map, merge it with data
                        data.toMutableMap().apply {
                            @Suppress("UNCHECKED_CAST")
                            (item as Map<String, Any>).forEach { (key, value) ->
                                put(key, value)
                            }
                            put("index", index)
                        }
                    }
                    else -> {
                        // Otherwise, put item as "item" field
                        data.toMutableMap().apply {
                            put("item", item ?: "")
                            put("index", index)
                        }
                    }
                }

                // Render the cell view with item data, addressed the way the
                // test drivers address it.
                CellRoot(withCellAddress(cellJson, collectionId, index), cellData)
            } else {
                // Fallback - display error
                Card(
                    modifier = Modifier.padding(4.dp).fillMaxWidth()
                ) {
                    Text(
                        text = "Cell not found: $cellJsonName",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        /**
         * Whether the author named an exact content padding of their own — a
         * `contentPadding` / `insets` value the reader takes
         * (declaredCollectionPadding). `contentInsets` is not one on this
         * platform (declared swift only, not drawn by kjui's codegen): a
         * layout carrying it is treated as the Release build treats it, as if
         * it were absent.
         */
        internal fun hasDeclaredContentPadding(a: CollectionAttributes, json: JsonObject): Boolean =
            declaredCollectionSides(json) != null

        /**
         * The Collection's own content padding — a declared `contentPadding` /
         * `insets` plus `insetHorizontal` / `insetVertical` (added, as iOS
         * adds them; round 17) — or no padding. The safe area is added by the
         * caller (it is read in the composition).
         */
        internal fun parseCollectionPadding(a: CollectionAttributes, json: JsonObject, data: Map<String, Any> = emptyMap()): PaddingValues =
            collectionPaddingSides(a, json, data)?.let { paddingOfSides(it) } ?: PaddingValues(0.dp)

        /**
         * [top, end, bottom, start] of a declared contentPadding / insets
         * (declaredCollectionSides) plus insetHorizontal / insetVertical, or
         * null when neither is declared.
         */
        internal fun collectionPaddingSides(a: CollectionAttributes, json: JsonObject, data: Map<String, Any>): FloatArray? {
            val declared = declaredCollectionSides(json, data)
            if (declared == null && a.insetHorizontal == null && a.insetVertical == null) return null
            val sides = declared ?: FloatArray(4)
            val h = a.insetHorizontal?.toFloat() ?: 0f
            val v = a.insetVertical?.toFloat() ?: 0f
            return floatArrayOf(sides[0] + v, sides[1] + h, sides[2] + v, sides[3] + h)
        }

        /**
         * A `contentPadding` / `insets` value as the SSoT's Collection.insets
         * declares it, as [top, end, bottom, start], or null: a number, or 1,
         * 2 or 4 values — an array, or a string separated by `|` (whitespace
         * and commas too) — read as `paddings` reads them: one, every side;
         * two, [vertical, horizontal]; four, [top, right, bottom, left] (right
         * the end, left the start). kjui's codegen reads the same
         * (content_padding_values). A value in an array may be a binding,
         * resolved from [data] as kjui's codegen binds it — a number, else 0
         * (4f ruling 2026-09-27, round 17; the array declared nothing until
         * then). 'contentPadding' is an undeclared legacy runtime extra;
         * 'insets' is a declared shape union, read raw (TypedAttrs.rawKey).
         */
        internal fun declaredCollectionSides(json: JsonObject, data: Map<String, Any> = emptyMap()): FloatArray? {
            listOf(
                TypedAttrs.undeclared(json, "contentPadding"),
                TypedAttrs.rawKey(json, "insets")
            ).forEach { element ->
                if (element == null) return@forEach
                val values: List<Float>? = when {
                    element.isJsonPrimitive && element.asJsonPrimitive.isNumber -> listOf(element.asFloat)
                    element.isJsonPrimitive && element.asJsonPrimitive.isString ->
                        element.asString.split(Regex("[|\\s,]+")).filter { it.isNotEmpty() }.mapNotNull { it.toFloatOrNull() }
                    // A number, a number written as a string, or a binding;
                    // anything else is no value this reader takes.
                    element.isJsonArray -> element.asJsonArray.map { item ->
                        val raw = if (item.isJsonPrimitive) item.asString else null
                        raw?.toFloatOrNull()
                            ?: raw?.takeIf { it.startsWith("@{") && it.endsWith("}") }?.let { DataBindingContext.resolveNumber(it, data)?.toFloat() ?: 0f }
                            ?: return@forEach
                    }
                    else -> null
                }
                when (values?.size) {
                    1 -> return FloatArray(4) { values[0] }
                    2 -> return floatArrayOf(values[0], values[1], values[0], values[1])
                    4 -> return floatArrayOf(values[0], values[1], values[2], values[3])
                }
            }
            return null
        }

        /** PaddingValues of [top, end, bottom, start]. */
        internal fun paddingOfSides(sides: FloatArray): PaddingValues =
            PaddingValues(top = sides[0].dp, end = sides[1].dp, bottom = sides[2].dp, start = sides[3].dp)

        /**
         * Collection-specific attributes this component applies (see
         * UnappliedAttributes). `horizontalScroll`, `cellWidth`,
         * `cellHeight`, `hideSeparator` and `listStyle` are drawn above and
         * were missing here, so a Debug build logged each as "not applied";
         * `contentInsets` is no longer read (declared swift only), so a
         * layout carrying it is now logged as it should be.
         */
        internal val APPLIED: Set<String> = setOf(
            "autoChangeTrackingId", "cellClasses", "cellHeight", "cellIdProperty",
            "cellWidth", "columnSpacing", "columns", "currentPage",
            "footerClasses",
            "headerClasses", "hideSeparator", "horizontalScroll",
            "insetHorizontal", "insetVertical", "insets",
            "itemSpacing", "items", "layout", "lazy", "lineSpacing", "listStyle",
            "onValueChange", "orientation", "paging", "reverseLayout",
            "scrollAnchor", "scrollAnimated", "defaultScrollAnchor", "scrollEnabled", "scrollTo",
            "onItemAppear"
        )

        private val loggedMisconfiguredCollections = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

        /** What this renderer has named, in order (read by the tests). */
        internal val named = java.util.Collections.synchronizedList(mutableListOf<String>())
        internal val loggedSeveralCellClasses = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

        /**
         * Several cellClasses and no `sections`: the build refuses that
         * layout (LayoutValidator check_collection, level error, "N
         * cellClasses declared without sections"), and this renderer draws no
         * cell for it — said once per Collection, since a Dynamic layout does
         * not pass the build. It drew nothing and said nothing.
         */
        internal fun nameUndrawnCellClasses(a: CollectionAttributes, sections: JsonArray?) {
            val count = a.cellClasses?.size ?: 0
            if ((sections == null || sections.size() == 0) && count > 1) logSeveralCellClasses(a.common.id, count)
        }

        internal fun logSeveralCellClasses(componentId: String?, count: Int) {
            val key = componentId ?: "(unnamed)"
            if (!loggedSeveralCellClasses.add(key)) return
            val sentence = "Collection (id=$key): $count cellClasses declared without sections — no cell is drawn. " +
                "Fix: assign cells via sections[].cell, or declare a single cellClass."
            named.add(sentence)
            android.util.Log.w("DynamicCollectionComponent", sentence)
        }

        private fun logAutoTrackingMisconfiguration(componentId: String?) {
            val key = componentId ?: "(unnamed)"
            if (!loggedMisconfiguredCollections.add(key)) return
            android.util.Log.w(
                "DynamicCollectionComponent",
                "Collection $key: autoChangeTrackingId is true but cellIdProperty is missing. " +
                    "Auto cellId generation is disabled; cells fall back to index-based identity."
            )
        }
    }
}
