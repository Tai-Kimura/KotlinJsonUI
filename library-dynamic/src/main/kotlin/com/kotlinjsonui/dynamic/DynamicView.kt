package com.kotlinjsonui.dynamic

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.components.VisibilityWrapper
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.DynamicModeManager
import com.kotlinjsonui.dynamic.components.*
import com.kotlinjsonui.dynamic.helpers.ColorParser
import com.kotlinjsonui.dynamic.hotloader.HotLoader
import androidx.compose.runtime.collectAsState

/**
 * Main entry point for rendering dynamic UI from JSON.
 * This component determines the appropriate component type from JSON
 * and renders it with data binding support.
 *
 * Features:
 * - JSON validation and error handling
 * - Component type detection with fallback
 * - Data binding context management
 * - Error recovery with debug information
 * - Responsive layout support via WindowSizeClass
 *
 * @param json The JSON object describing the UI component
 * @param data Map of data for binding. @{key} in JSON will be replaced with data[key].
 *             Functions in the data map can be referenced by name for event handlers.
 * @param onError Optional error handler callback
 */
@Composable
fun DynamicView(
    json: JsonObject,
    data: Map<String, Any> = emptyMap(),
    onError: ((Exception) -> Unit)? = null
) {
    // `$jui` normalization marker (root-level build metadata written by
    // `jui build` when layout normalization is enabled). It is never a
    // renderable attribute: detect it, publish the canonicalized state to
    // the subtree via LocalLayoutCanonicalized so components can skip
    // alias-spelling fallbacks, and otherwise ignore the key silently.
    if (json.has(Normalization.MARKER_KEY)) {
        val canonicalized = Normalization.isCanonicalized(json)
        CompositionLocalProvider(LocalLayoutCanonicalized provides canonicalized) {
            DynamicViewContent(json, data, onError)
        }
    } else {
        DynamicViewContent(json, data, onError)
    }
}

@Composable
private fun DynamicViewContent(
    json: JsonObject,
    data: Map<String, Any> = emptyMap(),
    onError: ((Exception) -> Unit)? = null
) {
    val context = LocalContext.current

    // Initialize ResourceCache, ColorParser, DynamicLayoutLoader, and IncludeExpander with context
    ResourceCache.init(context)
    ColorParser.init(context)
    DynamicLayoutLoader.init(context)
    IncludeExpander.init(context)

    // Apply styles if a style attribute is present
    val styledJson = if (json.has("style")) {
        DynamicStyleLoader.applyStyle(context, json)
    } else {
        json
    }

    // Note: Includes are now expanded by DynamicLayoutLoader/IncludeExpander
    // If we still see an include here, it means it was loaded without expansion
    if (styledJson.has("include")) {
        // Expand inline and render
        val expandedJson = IncludeExpander.processIncludes(styledJson)
        DynamicView(expandedJson, data, onError)
        return
    }

    // Resolve responsive overrides based on current WindowSizeClass and orientation.
    // This merges matching responsive attributes into the node and removes the
    // "responsive" key, so downstream components see a flat attribute set.
    val responsiveJson = resolveResponsiveNode(styledJson)

    // Check if this is a data element (should be skipped)
    if (responsiveJson.has("data") && !responsiveJson.has("type")) {
        // Data-only elements should not be rendered
        return
    }

    // Apply data section defaults from child elements
    // data sections define defaultValues that should be used when the property
    // is not already present in the data map (e.g., visibility defaults to "gone")
    val effectiveData = applyDataSectionDefaults(responsiveJson, data)

    // Validate JSON has required type field
    val type = try {
        responsiveJson.get("type")?.asString
    } catch (e: Exception) {
        onError?.invoke(e)
        null
    }

    if (type.isNullOrEmpty()) {
        val error = IllegalArgumentException("JSON must have a 'type' field")
        onError?.invoke(error)
        if (Configuration.showErrorsInDebug) {
            ErrorComponent("Missing 'type' field in JSON")
        }
        return
    }

    // Check for visibility / hidden attributes (with data binding support)
    val visibility = responsiveJson.get("visibility")?.asString?.let {
        processDataBinding(it, effectiveData, context)
    }
    val hidden = resolveHidden(responsiveJson, effectiveData)

    // Render the appropriate component based on type
    val renderComponent: @Composable () -> Unit = {
        when (type.lowercase()) {
            "text", "label" -> DynamicTextComponent.create(responsiveJson, effectiveData)
            // EditText (Android) and Input (web) are canonical aliases of TextField
            // in attribute_definitions.json
            "textfield", "edittext", "input" -> DynamicTextFieldComponent.create(responsiveJson, effectiveData)
            "button" -> DynamicButtonComponent.create(responsiveJson, effectiveData)
            "image" -> DynamicImageComponent.create(responsiveJson, effectiveData)
            "networkimage" -> DynamicNetworkImageComponent.create(responsiveJson, effectiveData)
            "circleimage" -> DynamicCircleImageComponent.create(responsiveJson, effectiveData)
            "switch" -> DynamicSwitchComponent.create(responsiveJson, effectiveData)
            // CheckBox is primary, Check is alias for backward compatibility
            "checkbox", "check" -> DynamicCheckBoxComponent.create(responsiveJson, effectiveData)
            "radio" -> DynamicRadioComponent.create(responsiveJson, effectiveData)
            "slider" -> DynamicSliderComponent.create(responsiveJson, effectiveData)
            "progress", "progressbar" -> DynamicProgressComponent.create(responsiveJson, effectiveData)
            "indicator" -> DynamicIndicatorComponent.create(responsiveJson, effectiveData)
            "selectbox", "spinner" -> DynamicSelectBoxComponent.create(responsiveJson, effectiveData)
            "segment", "tablayout" -> DynamicSegmentComponent.create(responsiveJson, effectiveData)
            "toggle" -> DynamicToggleComponent.create(responsiveJson, effectiveData)
            "scrollview", "scroll" -> DynamicScrollViewComponent.create(responsiveJson, effectiveData)
            "hstack", "row" -> DynamicHStackComponent.create(responsiveJson, effectiveData)
            "vstack", "column" -> DynamicVStackComponent.create(responsiveJson, effectiveData)
            "zstack", "box" -> DynamicZStackComponent.create(responsiveJson, effectiveData)
            "container", "view" -> DynamicContainerComponent.create(responsiveJson, effectiveData)
            "safeareaview" -> DynamicSafeAreaViewComponent.create(responsiveJson, effectiveData)
            "constraintlayout" -> DynamicConstraintLayoutComponent.create(responsiveJson, effectiveData)
            "collection", "collectionview", "recyclerview", "grid", "lazygrid" -> DynamicCollectionComponent.create(responsiveJson, effectiveData)
            "table", "listview" -> DynamicTableComponent.create(responsiveJson, effectiveData)
            "webview" -> DynamicWebViewComponent.create(responsiveJson, effectiveData)
            "web" -> DynamicWebComponent.create(responsiveJson, effectiveData)
            "tabview" -> DynamicTabViewComponent.create(responsiveJson, effectiveData)
            "embed" -> DynamicEmbedComponent.create(responsiveJson, effectiveData)
            "gradientview" -> DynamicGradientViewComponent.create(responsiveJson, effectiveData)
            "circleview" -> DynamicCircleViewComponent.create(responsiveJson, effectiveData)
            // "Blur" is the canonical section name in attribute_definitions.json
            "blurview", "blur" -> DynamicBlurViewComponent.create(responsiveJson, effectiveData)
            "iconlabel" -> DynamicIconLabelComponent.create(responsiveJson, effectiveData)
            "textview" -> DynamicTextViewComponent.create(responsiveJson, effectiveData)
            "triangle" -> DynamicTriangleComponent.create(responsiveJson, effectiveData)
            else -> {
                // First, try custom component handler
                val handled = Configuration.customComponentHandler?.invoke(type, responsiveJson, effectiveData) ?: false

                if (!handled) {
                    // Unknown component type
                    val error = IllegalArgumentException("Unknown component type: $type")
                    onError?.invoke(error)

                    // Log error in debug mode
                    if (Configuration.showErrorsInDebug) {
                        Log.w("DynamicView", "Unknown component type: $type")
                        ErrorComponent("Unknown component type: $type")
                    } else if (Configuration.fallbackComponent != null) {
                        // Use custom fallback component if configured
                        Configuration.fallbackComponent?.invoke(responsiveJson, effectiveData)
                    }
                }
            }
        }
    }

    // Apply visibility/hidden wrapper if either attribute resolved.
    // hidden = true is the boolean shorthand for visibility:"invisible":
    // the component KEEPS its measured layout space, draws nothing
    // (alpha 0), and exposes no accessibility/semantics nodes
    // (clearAndSetSemantics) — it must NOT collapse. hidden stays
    // reactive: resolveHidden re-reads the data map on every
    // composition, so binding changes re-resolve on recomposition.
    if (hidden == true || !visibility.isNullOrEmpty()) {
        VisibilityWrapper(
            visibility = visibility,
            hidden = hidden
        ) {
            renderComponent()
        }
    } else {
        renderComponent()
    }
}

/**
 * Resolve the 'hidden' attribute with data binding support.
 * Supports: boolean, string "true"/"false", and canonical boolean binding
 * resolution (@{prop} / @{a.b} / @{prop ?? true} / @{!prop}).
 */
@VisibleForTesting
internal fun resolveHidden(json: JsonObject, data: Map<String, Any>): Boolean? {
    val element = json.get("hidden") ?: return null
    if (element.isJsonPrimitive) {
        val p = element.asJsonPrimitive
        if (p.isBoolean) return p.asBoolean
        if (p.isString) {
            val s = p.asString
            if (s.startsWith("@{") && s.endsWith("}")) {
                // Canonical boolean value context: dot paths, `?? default`,
                // negation (@{!prop}), canonical bool coercion.
                return DataBindingContext.resolveBoolean(s, data)
            }
            return s.equals("true", ignoreCase = true)
        }
    }
    return null
}

/**
 * Apply default values from the layout's data section.
 * Two declaration shapes are supported (both produced by jui tooling):
 * - a root-level "data" array on the node itself (canonical layout shape,
 *   what `jui` codegen and the conformance fixture generator emit), and
 * - a data-only child element (has "data" but no "type") inside
 *   child/children (legacy dynamic-mode shape).
 * Extracted defaultValues are fallbacks for properties missing from the
 * data map — e.g. visibility defaults like "gone" apply when the ViewModel
 * doesn't explicitly provide the property.
 */
@VisibleForTesting
internal fun applyDataSectionDefaults(json: JsonObject, data: Map<String, Any>): Map<String, Any> {
    val sections = mutableListOf<com.google.gson.JsonArray>()

    // Root-level data section (canonical shape). Added FIRST so it wins on
    // duplicate names (first-wins below) — same precedence as SwiftJsonUI's
    // DynamicView.mergeDataDefaults ("the root-level array wins").
    json.get("data")?.takeIf { it.isJsonArray }?.let { sections.add(it.asJsonArray) }

    // Data-only child element (legacy shape)
    val children = json.get("child") ?: json.get("children")
    if (children != null && children.isJsonArray) {
        children.asJsonArray.firstOrNull { element ->
            element.isJsonObject && element.asJsonObject.has("data") && !element.asJsonObject.has("type")
        }?.asJsonObject?.get("data")?.takeIf { it.isJsonArray }?.let { sections.add(it.asJsonArray) }
    }

    if (sections.isEmpty()) return data

    val defaults = mutableMapOf<String, Any>()
    sections.flatten().forEach { element ->
        if (element.isJsonObject) {
            val obj = element.asJsonObject
            val name = obj.get("name")?.asString ?: return@forEach
            // Only apply default if not already in data
            if (data.containsKey(name)) return@forEach
            // First section wins (root-level over legacy child) — SJUI parity.
            if (defaults.containsKey(name)) return@forEach
            val defaultValue = obj.get("defaultValue") ?: return@forEach
            when {
                defaultValue.isJsonPrimitive -> {
                    val p = defaultValue.asJsonPrimitive
                    when {
                        p.isBoolean -> defaults[name] = p.asBoolean
                        p.isNumber -> defaults[name] = p.asNumber
                        p.isString -> {
                            val s = p.asString
                            // Skip complex default values (e.g., "CollectionDataSource()")
                            if (!s.contains("(") && !s.contains(")")) {
                                defaults[name] = s
                            }
                        }
                    }
                }
                // Nested defaults (object / array) convert recursively to the
                // plain Map/List data-map shape so the canonical resolver's
                // dot-path / bracket-index traversal reaches into them
                // (e.g. @{profile.name} against a defaulted object).
                defaultValue.isJsonObject || defaultValue.isJsonArray -> {
                    convertDefaultElement(defaultValue)?.let { defaults[name] = it }
                }
            }
        }
    }

    if (defaults.isEmpty()) return data

    // Merge: existing data overrides defaults
    return defaults.apply { putAll(data) }
}

/**
 * Deep-convert a data-section defaultValue element to the plain-Kotlin
 * shape ViewModels provide: JsonObject → Map<String, Any>, JsonArray →
 * List<Any?> (positional nulls preserved so bracket indices stay stable),
 * primitives keep gson number identity (`asNumber` — the same care as
 * [DynamicEmbedComponent.resolveParams] and DataBindingContext's
 * unwrapScalar, so an authored 5 does not become "5.0"). JsonNull yields
 * null; null-valued object entries are dropped (both resolve as
 * unresolved, so `??` fallbacks still apply).
 *
 * Note the top-level constructor-string skip ("CollectionDataSource()")
 * does NOT apply to nested strings — inside an object/array default,
 * parenthesized strings are ordinary data.
 */
private fun convertDefaultElement(element: com.google.gson.JsonElement): Any? = when {
    element.isJsonNull -> null
    element.isJsonPrimitive -> {
        val p = element.asJsonPrimitive
        when {
            p.isBoolean -> p.asBoolean
            p.isNumber -> p.asNumber
            else -> p.asString
        }
    }
    element.isJsonObject -> {
        val out = LinkedHashMap<String, Any>()
        for ((key, value) in element.asJsonObject.entrySet()) {
            convertDefaultElement(value)?.let { out[key] = it }
        }
        out
    }
    element.isJsonArray -> element.asJsonArray.map { convertDefaultElement(it) }
    else -> null
}

/**
 * Error component shown in debug mode
 */
@Composable
private fun ErrorComponent(message: String) {
    Box(
        modifier = Modifier
            .background(Color.Red.copy(alpha = 0.1f))
            .padding(8.dp)
    ) {
        Text(
            text = "⚠️ $message",
            color = Color.Red
        )
    }
}

/**
 * Renders a list of components from a JSON array.
 * Useful for rendering the contents of container components.
 *
 * @param components List of JSON objects describing UI components
 * @param data Map of data for binding
 * @param onError Optional error handler for individual component errors
 */
@Composable
fun DynamicViews(
    components: List<JsonObject>,
    data: Map<String, Any> = emptyMap(),
    onError: ((Exception) -> Unit)? = null
) {
    components.forEach { component ->
        DynamicView(component, data, onError)
    }
}

/**
 * DynamicView with hot reload support using layout name
 * This version loads JSON from assets/cache and supports WebSocket-based hot reload
 *
 * @param layoutName Name of the layout file (without .json extension)
 * @param data Map of data for binding
 * @param onError Optional error handler
 */
@Composable
fun DynamicView(
    layoutName: String,
    data: Map<String, Any> = emptyMap(),
    onError: ((Exception) -> Unit)? = null
) {
    val context = LocalContext.current

    // Initialize ResourceCache and ColorParser with context
    ResourceCache.init(context)
    ColorParser.init(context)

    // Initialize DynamicLayoutLoader with context
    DynamicLayoutLoader.init(context)

    var jsonObject by remember { mutableStateOf<JsonObject?>(null) }
    var styleUpdateCounter by remember { mutableStateOf(0) }
    // Bumped when a hot-reload event may change which variant file wins
    // (a variant appears/updates/disappears for the watched base screen).
    var variantResolutionTick by remember { mutableStateOf(0) }

    // Check if dynamic mode is enabled
    val isDynamicModeEnabled by DynamicModeManager.isDynamicModeEnabled.collectAsState()

    // Responsive variant-file resolution (home@regular.json): pick the
    // layout for the current size-class tier. Recomputed on window size
    // changes (Split View / foldable posture) — the whole tree swaps while
    // the caller-owned data map (VM state) survives.
    val sizeClassTier = currentSizeClassTier()
    val effectiveLayoutName = remember(layoutName, sizeClassTier, isDynamicModeEnabled, variantResolutionTick) {
        if (layoutName.contains('@')) {
            layoutName
        } else {
            val candidate = "$layoutName@$sizeClassTier"
            val hotCached = isDynamicModeEnabled &&
                HotLoader.getInstance(context).getCachedLayout(candidate) != null
            when {
                hotCached -> candidate
                DynamicLayoutLoader.layoutExists(candidate) -> candidate
                else -> layoutName
            }
        }
    }

    DisposableEffect(effectiveLayoutName, isDynamicModeEnabled, variantResolutionTick) {
        if (!isDynamicModeEnabled) {
            // Just load from assets once
            loadLayoutFromAssets(context, effectiveLayoutName, onError)?.let {
                jsonObject = it
            }
            onDispose { }
        } else {
            // Use HotLoader for WebSocket-based hot reload
            val hotLoader = HotLoader.getInstance(context)

            // First try to load from cache
            hotLoader.getCachedLayout(effectiveLayoutName)?.let { cached ->
                try {
                    jsonObject = JsonParser.parseString(cached).asJsonObject
                } catch (e: Exception) {
                    onError?.invoke(e)
                }
            } ?: run {
                // Fall back to loading from assets
                loadLayoutFromAssets(context, effectiveLayoutName, onError)?.let {
                    jsonObject = it
                }
            }

            // Set up listener for updates. The listener params shadow this
            // composable's `layoutName`, so capture it first. Variant files
            // of the watched base re-trigger resolution (the update may
            // introduce or retire the variant that currently wins).
            val watchedLayoutName = effectiveLayoutName
            val watchedBase = LayoutVariantResolver.baseOf(layoutName)
            val listener = object : HotLoader.HotLoaderListener {
                override fun onConnected() {
                    Log.d("DynamicView", "HotLoader connected")
                }

                override fun onDisconnected() {
                    Log.d("DynamicView", "HotLoader disconnected")
                }

                override fun onLayoutUpdated(layoutName: String, content: String) {
                    val incoming = layoutName.removeSuffix(".json")
                    if (incoming == watchedLayoutName) {
                        try {
                            jsonObject = JsonParser.parseString(content).asJsonObject
                            Log.d("DynamicView", "Layout updated: $layoutName")
                        } catch (e: Exception) {
                            onError?.invoke(e)
                        }
                    } else if (LayoutVariantResolver.baseOf(incoming) == watchedBase) {
                        variantResolutionTick++
                    }
                }

                override fun onStyleUpdated(styleName: String, content: String) {
                    // Force recomposition when any style is updated
                    // This will cause all views to re-read their styles
                    styleUpdateCounter++
                    Log.d("DynamicView", "Style updated: $styleName, triggering recomposition")
                }

                override fun onLayoutAdded(layoutName: String) {
                    if (LayoutVariantResolver.baseOf(layoutName.removeSuffix(".json")) == watchedBase) {
                        variantResolutionTick++
                    }
                }

                override fun onLayoutRemoved(layoutName: String) {
                    if (LayoutVariantResolver.baseOf(layoutName.removeSuffix(".json")) == watchedBase) {
                        variantResolutionTick++
                    }
                }

                override fun onError(error: Throwable) {
                    onError?.invoke(Exception(error))
                }
            }

            hotLoader.addListener(listener)
            hotLoader.start()

            onDispose {
                hotLoader.removeListener(listener)
            }
        }
    }

    // Render the view - include styleUpdateCounter as a key to force recomposition
    jsonObject?.let { json ->
        key(styleUpdateCounter) {
            DynamicView(json, data, onError)
        }
    }
}

/**
 * Load layout from assets folder
 */
private fun loadLayoutFromAssets(
    context: Context,
    layoutName: String,
    onError: ((Exception) -> Unit)?
): JsonObject? {
    return try {
        val fileName = if (layoutName.endsWith(".json")) layoutName else "$layoutName.json"
        val jsonString = context.assets.open("Layouts/$fileName").bufferedReader().use { it.readText() }
        JsonParser.parseString(jsonString).asJsonObject
    } catch (e: Exception) {
        // Try without Layouts directory
        try {
            val fileName = if (layoutName.endsWith(".json")) layoutName else "$layoutName.json"
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            JsonParser.parseString(jsonString).asJsonObject
        } catch (e2: Exception) {
            onError?.invoke(e2)
            null
        }
    }
}
