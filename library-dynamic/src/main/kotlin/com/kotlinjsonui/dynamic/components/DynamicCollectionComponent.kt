package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
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
 * - scrollTo: @{variable} binding to SharedFlow<Int> for programmatic scrolling
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
            ModifierBuilder.ApplyLifecycleEffects(json, data)

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
                (itemsBoundValue as? CollectionDataSource)?.reconfigured(
                    cellIdProperty = cellIdProperty,
                    autoChangeTrackingId = autoChangeTrackingId
                )
            )
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
            // A DECLARED numeric contentPadding/insets wins: the author named
            // an exact value, and `contentInsetAdjustmentBehavior` only says
            // "clear the system bars" — it cannot also mean "and discard the
            // number I wrote". Same precedence the codegen uses.
            val declaredPadding = parseCollectionPadding(a, json)
            val contentPadding = if (hasDeclaredContentPadding(a, json)) {
                declaredPadding
            } else {
                ContentInsetBehavior.safeAreaPadding(
                    a.contentInsetAdjustmentBehavior,
                    horizontal = isHorizontal
                ) ?: declaredPadding
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
            // For a HORIZONTAL collection both spacing spellings act along
            // the scroll axis, lineSpacing first: minimumLineSpacing
            // separates the "lines" perpendicular to the scroll, which ARE
            // the columns when the scroll runs horizontally. The codegen
            // face emits exactly this fold (collection_component.rb
            // `h_spacing = line_spacing || column_spacing`) while this path
            // read only columnSpacing — a lineSpacing-declared carousel
            // rendered its cells touching, dynamic face only (a downstream chip carousel, 2026-08-10).
            val scrollAxisSpacing = if (isHorizontal) {
                (a.lineSpacing?.toFloat() ?: a.columnSpacing?.toFloat() ?: defaultSpacing).dp
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
                val style = (TypedAttrs.enumString(a.listStyle) { it.json } ?: "plain").lowercase()
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
            val gravity = (a.common.gravity as? String)?.lowercase()
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
                style = (TypedAttrs.enumString(a.listStyle) { it.json } ?: "plain").lowercase(),
                hideSeparator = a.hideSeparator == true
            )

            // Parse scrollTo binding
            val scrollToFlow = resolveScrollToFlow(a, data)
            val scrollAnchor = TypedAttrs.enumString(a.scrollAnchor) { it.json } ?: "bottom"
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
                        collectionId = collectionId
                    )
                }
                when {
                    !flowScrolls -> flow(modifier)
                    heightIsSelfBounded -> flow(modifier.verticalScroll(rememberScrollState()))
                    else -> BoxWithConstraints(modifier = modifier) {
                        // The node's own modifiers (size, background, address) sit
                        // on this box; the FlowRow fills it and scrolls only when
                        // the box was given a finite height to fill.
                        val inner = if (constraints.hasBoundedHeight) {
                            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
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
            // forces NONE-style rendering to avoid Compose's nested-Lazy crash.
            if (!lazy && isHorizontal) {
                val rowModifier = if (collectionMode == CollectionStackMode.EAGER && !widthIsWrapContent) {
                    modifier.horizontalScroll(rememberScrollState())
                } else {
                    modifier
                }
                renderNonLazyRow(
                    chrome = listChrome,
                    sections = plan.sectionsFor(CellRoute.NON_LAZY_ROW),
                    collectionDataSource = collectionDataSource,
                    cellIdProperty = cellIdProperty,
                    data = data,
                    modifier = rowModifier,
                    columnSpacing = scrollAxisSpacing,
                    contentPadding = contentPadding,
                    cellWidth = cellWidth,
                    cellHeight = cellHeight,
                    gravityAlignment = gravityAlignment,
                    onItemAppear = onItemAppear,
                    collectionId = collectionId
                )
                return
            }
            if ((!lazy && !isHorizontal) || (!isHorizontal && heightIsWrapContent)) {
                val columnModifier = if (collectionMode == CollectionStackMode.EAGER && !heightIsWrapContent) {
                    modifier.verticalScroll(rememberScrollState())
                } else {
                    modifier
                }
                renderNonLazy(
                    chrome = listChrome,
                    sections = plan.sectionsFor(CellRoute.NON_LAZY_COLUMN),
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
                    collectionId = collectionId
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
                    pageSpacing = columnSpacing,
                    cellWidth = cellWidth,
                    cellHeight = cellHeight,
                    gravityAlignment = gravityAlignment,
                    onItemAppear = onItemAppear,
                    collectionId = collectionId
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
                    scrollToFlow = scrollToFlow,
                    scrollAnimated = scrollAnimated,
                    onItemAppear = onItemAppear,
                    chrome = listChrome,
                    collectionId = collectionId
                )
                return
            }

            // LazyGrid state for programmatic scrolling
            val gridState = rememberLazyGridState()

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
            val defaultAnchor = TypedAttrs.enumString(a.defaultScrollAnchor) { it.json }
            if (defaultAnchor == "center" || defaultAnchor == "bottom") {
                val anchorCount = collectionDataSource
                    ?.sections?.firstOrNull()?.cells?.data?.size ?: 0
                val anchorApplied = remember { mutableStateOf(false) }
                LaunchedEffect(anchorCount) {
                    if (!anchorApplied.value && anchorCount > 0) {
                        gridState.scrollToItem(
                            if (defaultAnchor == "center") anchorCount / 2 else anchorCount - 1
                        )
                        anchorApplied.value = true
                    }
                }
            }

            // Handle scrollTo
            if (scrollToFlow != null) {
                LaunchedEffect(scrollToFlow) {
                    scrollToFlow.collect { index ->
                        when (scrollAnchor) {
                            "top" -> if (scrollAnimated) gridState.animateScrollToItem(index, 0) else gridState.scrollToItem(index, 0)
                            "center" -> if (scrollAnimated) gridState.animateScrollToItem(index) else gridState.scrollToItem(index)
                            else -> if (scrollAnimated) gridState.animateScrollToItem(index) else gridState.scrollToItem(index)
                        }
                    }
                }
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
                    verticalArrangement = Arrangement.spacedBy(0.dp) /* canon: codegen emits no cross-axis arrangement for horizontal (collection_component.rb:326) */,
                    horizontalArrangement = Arrangement.spacedBy(scrollAxisSpacing)
                ) {
                    generateCollectionItems(
                        sections = plan.sectionsFor(CellRoute.LAZY_HORIZONTAL_GRID),
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
                    verticalArrangement = Arrangement.spacedBy(lineSpacing),
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
         * Resolve scrollTo binding from JSON data.
         * Expects @{variable} pointing to a SharedFlow<Int> in data map.
         */
        @Suppress("UNCHECKED_CAST")
        private fun resolveScrollToFlow(a: CollectionAttributes, data: Map<String, Any>): SharedFlow<Int>? {
            val scrollToBinding = TypedAttrs.raw(a.scrollTo) as? String ?: return null
            if (!scrollToBinding.startsWith("@{") || !scrollToBinding.endsWith("}")) return null
            // Canonical value resolution (flat-first, dot paths).
            return DataBindingContext.evaluateExpression(scrollToBinding, data) as? SharedFlow<Int>
        }

        /**
         * Render paging horizontal collection using HorizontalPager.
         * Each page displays one cell from the data source, with snap-to-page behavior.
         *
         * Supports:
         * - onPageChanged: @{callback} binding for page change notification
         * - pageSpacing: spacing between pages (from columnSpacing/itemSpacing)
         * - contentPadding: padding around the pager
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
        ) {
            // Calculate page count from data source
            val pageCount = when {
                sections != null && collectionDataSource != null -> {
                    // Sum all cell counts across all sections
                    collectionDataSource.sections.sumOf { section ->
                        section.cells?.data?.size ?: 0
                    }
                }
                else -> 0
            }

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

            // Build a flat list of (cellViewName, itemData, cellIndex) for all sections
            data class PageItem(val cellViewName: String?, val itemData: Any?, val cellIndex: Int)

            val pageItems: List<PageItem> = when {
                sections != null && collectionDataSource != null -> {
                    val items = mutableListOf<PageItem>()
                    sections.forEachIndexed { sectionIndex, sectionElement ->
                        val sectionObj = sectionElement.asJsonObject
                        val cellViewName = sectionObj.get("cell")?.asString

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

            HorizontalPager(
                state = pagerState,
                modifier = modifier,
                contentPadding = contentPadding,
                pageSpacing = pageSpacing
            ) { pageIndex ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (cellWidth != null) Modifier.width(cellWidth) else Modifier)
                        .then(if (cellHeight != null) Modifier.height(cellHeight) else Modifier),
                    contentAlignment = gravityAlignment
                ) {
                    when {
                        pageItems.isNotEmpty() -> {
                            val pageItem = pageItems[pageIndex]
                            renderCellView(pageItem.cellViewName, pageItem.itemData, pageItem.cellIndex, data, onItemAppear, collectionId = collectionId)
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
        ) {

            val arrangement = when (flowAlignment) {
                "center" -> Arrangement.Center
                "trailing", "end" -> Arrangement.End
                else -> Arrangement.Start
            }

            FlowRow(
                // `clipToBounds` defaults to false on every component (SSoT
                // common.clipToBounds; attribute_semantics 51-E, 2026-08-07:
                // absent means no clip, and hit testing follows clipping).
                // FlowRow does not lay out the rows that exceed its max
                // height — a lazy:"none" flow in a fixed-height box drew
                // three rows and nothing below, while iOS and web drew all
                // six past the box. Measuring it without regard for the
                // incoming max height and aligning the result over that
                // space (wrapContentHeight, unbounded) is what "overflow
                // visible" is in this toolkit; FlowRow's own `overflow`
                // parameter says the same but is deprecated in the resolved
                // foundation-layout. The modifier chain applies
                // `.clipToBounds()` only when declared true, so that
                // declaration is now the one that decides.
                modifier = modifier.wrapContentHeight(Alignment.Top, unbounded = true),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing, arrangement.let {
                    when (flowAlignment) {
                        "center" -> Alignment.CenterHorizontally
                        "trailing", "end" -> Alignment.End
                        else -> Alignment.Start
                    }
                }),
                verticalArrangement = Arrangement.spacedBy(verticalSpacing)
            ) {
                when {
                    sections != null && collectionDataSource != null -> {
                        sections.forEachIndexed { sectionIndex, sectionJson ->
                            val sectionObj = sectionJson.asJsonObject
                            val cellViewName = sectionObj.get("cell")?.asString

                            collectionDataSource.sections.getOrNull(sectionIndex)?.let { section ->
                                section.cells?.let { cellData ->
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
                                                    ),
                                                contentAlignment = gravityAlignment
                                            ) {
                                                renderCellView(cellViewName ?: cellClassName, item, cellIndex, data, onItemAppear, collectionId = collectionId)
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
        ) {
            Column(
                modifier = modifier.then(Modifier.padding(contentPadding)),
                verticalArrangement = Arrangement.spacedBy(lineSpacing)
            ) {
                // The legacy shape's headerClasses: once, without data,
                // before the cells (sjui's non-lazy stack); its footer after.
                legacyHeader?.let { renderCellView(it, emptyMap<String, Any>(), -1, data) }
                when {
                    sections != null && collectionDataSource != null -> {
                        val sectionObjs = sections.map { it.asJsonObject }
                        fun cellNameOf(s: Int): String? = sectionObjs.getOrNull(s)?.get("cell")?.asString
                        val gridRows = nonLazyGridRows(
                            cellCounts = sectionObjs.indices.map { collectionDataSource.sections.getOrNull(it)?.cells?.data?.size ?: 0 },
                            sectionColumns = sectionObjs.map { it.get("columns")?.asInt ?: defaultColumns },
                            oneGrid = oneGridForAllSections
                        )
                        sections.forEachIndexed { sectionIndex, sectionElement ->
                            val sectionObj = sectionElement.asJsonObject

                            // Header
                            val headerViewName = sectionObj.get("header")?.asString
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
                                            .then(if (cellHeight != null) Modifier.height(cellHeight) else Modifier),
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
                                                    .then(if (cellHeight != null) Modifier.height(cellHeight) else Modifier),
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
                            val footerViewName = sectionObj.get("footer")?.asString
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
            scrollToFlow: SharedFlow<Int>?,
            scrollAnimated: Boolean,
            onItemAppear: ((Int) -> Unit)? = null,
            chrome: ListChrome? = null,
            collectionId: String? = null,
        ) {
            val listState = androidx.compose.foundation.lazy.rememberLazyListState()
            if (scrollToFlow != null) {
                LaunchedEffect(scrollToFlow) {
                    scrollToFlow.collect { index ->
                        if (scrollAnimated) listState.animateScrollToItem(index) else listState.scrollToItem(index)
                    }
                }
            }
            androidx.compose.foundation.lazy.LazyRow(
                modifier = modifier,
                state = listState,
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(scrollAxisSpacing)
            ) {
                when {
                    sections != null && collectionDataSource != null -> {
                        sections.forEachIndexed { sectionIndex, sectionElement ->
                            val sectionObj = sectionElement.asJsonObject
                            val cellViewName = sectionObj.get("cell")?.asString
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
            contentPadding: PaddingValues,
            cellWidth: androidx.compose.ui.unit.Dp?,
            cellHeight: androidx.compose.ui.unit.Dp?,
            gravityAlignment: Alignment,
            onItemAppear: ((Int) -> Unit)? = null
        ,
            chrome: ListChrome? = null,
            collectionId: String? = null,
        ) {
            Row(
                modifier = modifier.then(Modifier.padding(contentPadding)),
                horizontalArrangement = Arrangement.spacedBy(columnSpacing)
            ) {
                when {
                    sections != null && collectionDataSource != null -> {
                        sections.forEachIndexed { sectionIndex, sectionElement ->
                            val sectionObj = sectionElement.asJsonObject
                            val cellViewName = sectionObj.get("cell")?.asString

                            collectionDataSource.sections.getOrNull(sectionIndex)?.let { section ->
                                section.cells?.let { cellData ->
                                    cellData.data.forEachIndexed { cellIndex, item ->
                                        Box(
                                            modifier = Modifier
                                                .then(if (cellWidth != null) Modifier.width(cellWidth) else Modifier)
                                                .then(if (cellHeight != null) Modifier.height(cellHeight) else Modifier),
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
                    // row and no line spacing is added. A header (full span)
                    // already starts one. The legacy shape's stand-in
                    // sections stay one grid, as sjui's legacy grid is.
                    val breakSpans = if (breakRowsBetweenSections) {
                        sectionBreakSpans(
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
                    } else null
                    orderedSections.forEachIndexed { orderIndex, (sectionIndex, sectionJson) ->
                        val sectionObj = sectionJson.asJsonObject
                        val cellViewName = sectionObj.get("cell")?.asString
                        val headerViewName = sectionObj.get("header")?.asString
                        val footerViewName = sectionObj.get("footer")?.asString
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
                                val identifiedItems = if (cellIdProperty != null) {
                                    cellData.data.mapIndexed { index, item ->
                                        val id = (item["cellId"] as? String)
                                            ?: (item[cellIdProperty] as? String)
                                            ?: index.toString()
                                        IdentifiedCellItem(id = id, index = index, data = item)
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
         *   first data section on the horizontal and flow routes, nothing on
         *   paging (which reads declared sections only, on every face);
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
                if (hasSections || route == CellRoute.PAGING) return declaredSections
                val cell = legacyCell ?: return if (dataSource != null) JsonArray() else declaredSections
                val count = when (route) {
                    CellRoute.FLOW, CellRoute.NON_LAZY_ROW, CellRoute.LAZY_ROW, CellRoute.LAZY_HORIZONTAL_GRID ->
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
         * Resolve Collection content padding from any of the tool-emitted
         * attributes: `contentPadding`, `insets`,
         * `insetHorizontal`/`insetVertical`. Accepted value forms:
         *   - number:              uniform dp
         *   - array of 4 numbers:  [top, end, bottom, start]
         *   - string "t|r|b|l":    pipe-separated; whitespace and commas also work
         */
        /**
         * Whether the author named an exact content padding of their own.
         * `contentInsets` is not one on this platform (declared swift only,
         * not drawn by kjui's codegen): a layout carrying it is treated as
         * the Release build treats it, as if it were absent.
         */
        internal fun hasDeclaredContentPadding(a: CollectionAttributes, json: JsonObject): Boolean =
            TypedAttrs.undeclared(json, "contentPadding") != null ||
                TypedAttrs.rawKey(json, "insets") != null

        internal fun parseCollectionPadding(a: CollectionAttributes, json: JsonObject): PaddingValues {
            // 'contentPadding' is an undeclared legacy runtime extra; 'insets'
            // is a declared shape union (number | array | pipe-separated
            // string) — wider than a single typed value, so read raw (see
            // TypedAttrs.rawKey).
            listOf(
                TypedAttrs.undeclared(json, "contentPadding"),
                TypedAttrs.rawKey(json, "insets")
            ).forEach { element ->
                if (element == null) return@forEach
                when {
                    element.isJsonPrimitive && element.asJsonPrimitive.isNumber ->
                        return PaddingValues(element.asFloat.dp)
                    element.isJsonPrimitive && element.asJsonPrimitive.isString ->
                        parsePipeSeparatedPadding(element.asString)?.let { return it }
                    element.isJsonArray -> {
                        val array = element.asJsonArray
                        if (array.size() == 4) {
                            return PaddingValues(
                                top = array[0].asFloat.dp,
                                end = array[1].asFloat.dp,
                                bottom = array[2].asFloat.dp,
                                start = array[3].asFloat.dp
                            )
                        }
                    }
                }
            }

            if (a.insetHorizontal != null || a.insetVertical != null) {
                val hInset = a.insetHorizontal?.toFloat() ?: 0f
                val vInset = a.insetVertical?.toFloat() ?: 0f
                return PaddingValues(horizontal = hInset.dp, vertical = vInset.dp)
            }

            return PaddingValues(0.dp)
        }

        private fun parsePipeSeparatedPadding(raw: String): PaddingValues? {
            val nums = raw.split(Regex("[|\\s,]+"))
                .filter { it.isNotEmpty() }
                .mapNotNull { it.toFloatOrNull() }
            return when (nums.size) {
                1 -> PaddingValues(nums[0].dp)
                2 -> PaddingValues(vertical = nums[0].dp, horizontal = nums[1].dp)
                4 -> PaddingValues(
                    top = nums[0].dp,
                    end = nums[1].dp,
                    bottom = nums[2].dp,
                    start = nums[3].dp
                )
                else -> null
            }
        }

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
