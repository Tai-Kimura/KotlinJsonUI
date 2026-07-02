package com.kotlinjsonui.dynamic

import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.generated.AttrEnum
import com.kotlinjsonui.dynamic.generated.AttrValue

/**
 * Bridge between the generated typed attribute extraction
 * (`com.kotlinjsonui.dynamic.generated.<Component>Attributes.parse`) and
 * the dynamic components. This is the ONE place that absorbs the shape
 * differences between the generated model and what components consume:
 *
 * - gson [JsonObject] nodes are adapted to the plain `Map<String, Any?>`
 *   the generated parsers take ([toAttrMap]; numbers become [Double],
 *   objects become `Map`, arrays become `List`).
 * - [AttrValue] (static value | `@{binding}` expression) and [AttrEnum]
 *   (declared | unknown-passthrough enum) wrappers are unwrapped HERE.
 *   Components never pattern-match the wrappers themselves; they either
 *   take the [raw] layout representation (to feed the existing
 *   binding/resource-aware helpers such as
 *   `ResourceResolver.resolveTextValue` / `ColorParser
 *   .parseColorStringWithBinding`) or use the data-resolving accessors
 *   below, which mirror the legacy `resolveFloat` / `resolveBoolean` /
 *   `resolveString` semantics.
 * - L1-normalized layouts (`$jui` marker → [LocalLayoutCanonicalized])
 *   parse with `canonicalOnly = true`: alias spellings are ignored
 *   (the `jui build` normalizer already rewrote them).
 *
 * Structural keys (`type`, `child`/`children`, `data`, `include`,
 * `style`, `sections`/`cell`/`header`/`footer`, `tabs`, `items`) are NOT
 * attributes: components keep reading those from the node directly, and
 * whole-node passes to the shared helpers (ModifierBuilder etc.) stay
 * as-is.
 */
object TypedAttrs {

    // ------------------------------------------------------------------
    // gson adapter

    /**
     * Deep-convert a gson node to the plain map shape the generated
     * `parse(json: Map<String, Any?>)` implementations expect.
     */
    fun toAttrMap(json: JsonObject): Map<String, Any?> {
        val out = LinkedHashMap<String, Any?>(json.size())
        for ((key, value) in json.entrySet()) {
            out[key] = fromElement(value)
        }
        return out
    }

    private fun fromElement(element: JsonElement?): Any? = when {
        element == null || element.isJsonNull -> null
        element.isJsonObject -> toAttrMap(element.asJsonObject)
        element.isJsonArray -> fromArray(element.asJsonArray)
        else -> {
            val p = element.asJsonPrimitive
            when {
                p.isBoolean -> p.asBoolean
                p.isNumber -> p.asDouble
                else -> p.asString
            }
        }
    }

    private fun fromArray(array: JsonArray): List<Any?> = array.map { fromElement(it) }

    // ------------------------------------------------------------------
    // AttrValue accessors

    /**
     * The original layout representation: `"@{expr}"` for a binding, the
     * static value otherwise. Feed this to the existing binding-aware
     * helpers (resolveTextValue / parseColorStringWithBinding / ...).
     */
    fun raw(v: AttrValue<*>?): Any? = v?.rawRepresentation()

    /** [raw] narrowed to String (binding representation included). */
    fun rawString(v: AttrValue<String>?): String? = v?.rawRepresentation() as? String

    /** Static value only — a binding yields null. */
    fun <T> static(v: AttrValue<T>?): T? = v?.valueOrNull()

    /** Static value or the bound data-map value, as String. */
    fun string(v: AttrValue<String>?, data: Map<String, Any>): String? = when (v) {
        null -> null
        is AttrValue.Binding -> data[v.expression] as? String
        is AttrValue.Value -> v.value
    }

    /** Static value or the bound data-map value, as Float (legacy resolveFloat semantics). */
    fun float(v: AttrValue<Double>?, data: Map<String, Any>): Float? = when (v) {
        null -> null
        is AttrValue.Binding -> when (val bound = data[v.expression]) {
            is Number -> bound.toFloat()
            is String -> bound.toFloatOrNull()
            else -> null
        }
        is AttrValue.Value -> v.value.toFloat()
    }

    /** Static value or the bound data-map value, as Int. */
    fun int(v: AttrValue<Double>?, data: Map<String, Any>): Int? = when (v) {
        null -> null
        is AttrValue.Binding -> when (val bound = data[v.expression]) {
            is Number -> bound.toInt()
            is String -> bound.toIntOrNull()
            else -> null
        }
        is AttrValue.Value -> v.value.toInt()
    }

    /** Static value or the bound data-map value, as Boolean (legacy resolveBoolean semantics). */
    fun boolean(v: AttrValue<Boolean>?, data: Map<String, Any>): Boolean? = when (v) {
        null -> null
        is AttrValue.Binding -> when (val bound = data[v.expression]) {
            is Boolean -> bound
            is String -> bound.equals("true", ignoreCase = true)
            else -> null
        }
        is AttrValue.Value -> v.value
    }

    /** Binding expression (`@{expr}` → `expr`), null for static values. */
    fun binding(v: AttrValue<*>?): String? = v?.bindingExpressionOrNull()

    // ------------------------------------------------------------------
    // AttrEnum accessors

    /** Declared enum constant, or null for unknown pass-through values. */
    fun <T> enum(v: AttrEnum<T>?): T? = v?.knownOrNull()

    /**
     * Binding-capable enum: static declared value directly; a binding
     * resolves through the data map and is matched with [parse]
     * (case handling is up to the generated `from()` passed in).
     */
    fun <T> enum(
        v: AttrValue<AttrEnum<T>>?,
        data: Map<String, Any>,
        parse: (String) -> T?
    ): T? = when (v) {
        null -> null
        is AttrValue.Binding -> (data[v.expression] as? String)?.let(parse)
        is AttrValue.Value -> v.value.knownOrNull()
    }

    /**
     * The enum's declared JSON spelling for known values, or the unknown
     * pass-through raw value (as String). For components whose legacy
     * `when(lowercase())` switches also matched undeclared spellings.
     */
    fun <T> enumString(v: AttrEnum<T>?, json: (T) -> String): String? = when (v) {
        null -> null
        is AttrEnum.Known -> json(v.value)
        is AttrEnum.Unknown -> v.value as? String
    }

    /**
     * [enumString] over a binding-capable enum attribute, static values
     * only (a binding yields null — matches legacy static-only reads).
     */
    fun <T> staticEnumString(v: AttrValue<AttrEnum<T>>?, json: (T) -> String): String? =
        when (v) {
            null -> null
            is AttrValue.Binding -> null
            is AttrValue.Value -> enumString(v.value, json)
        }

    // ------------------------------------------------------------------
    // Documented raw escape hatches (grep-gate friendly: components use
    // these named entry points instead of json.get("attr")).

    /**
     * Raw read of a DECLARED key whose accepted value space is wider
     * than its declared type (e.g. `padding`/`paddings` edge-inset
     * arrays, `shadow` objects) — the generated coercion would drop the
     * extra shapes. Mirrors the rjui bridge RAW_LOOKUP_KEYS.
     */
    fun rawKey(json: JsonObject, key: String): JsonElement? = json.get(key)

    /**
     * Raw read of a key NOT declared in attribute_definitions.json
     * (legacy runtime extras such as `isLoading`, `async`,
     * `imagePosition`). Every call site is a definitions-backfill
     * candidate — keeping them on this single entry point makes them
     * mechanically greppable.
     */
    fun undeclared(json: JsonObject, key: String): JsonElement? = json.get(key)
}

/**
 * Parse a node's typed attributes once per composition, honoring the
 * layout's normalization state ([LocalLayoutCanonicalized]).
 *
 * ```kotlin
 * val attrs = rememberTypedAttrs(json) { m, canonicalOnly ->
 *     LabelAttributes.parse(m, canonicalOnly)
 * }
 * ```
 */
@Composable
fun <A> rememberTypedAttrs(
    json: JsonObject,
    parse: (Map<String, Any?>, Boolean) -> A
): A {
    val canonicalOnly = LocalLayoutCanonicalized.current
    return remember(json, canonicalOnly) { parse(TypedAttrs.toAttrMap(json), canonicalOnly) }
}

/**
 * Debug-build detection of "parsed but unapplied" attributes: a declared
 * attribute present on a node that the rendering component does not
 * apply. This is the machine check that surfaces application gaps when a
 * new attribute lands in attribute_definitions.json before the component
 * grows support for it.
 *
 * Only active when the host app is debuggable ([ApplicationInfo
 * .FLAG_DEBUGGABLE]); warnings are logged once per (component, attribute)
 * pair per process to keep logcat usable.
 */
object UnappliedAttributes {
    private const val TAG = "JsonUIUnapplied"

    /** Overridable for tests; null = derive from the app's debuggable flag. */
    var enabledOverride: Boolean? = null

    /** Test hook: receives every emitted warning message. */
    var warningSink: ((String) -> Unit)? = null

    private val warned = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    /** Keys that are structure / build metadata, never component-applied. */
    val STRUCTURAL_KEYS: Set<String> = setOf(
        Normalization.MARKER_KEY, "_generated",
        "type", "id", "child", "children", "data", "shared_data", "variables",
        "include", "style", "sections", "cell", "header", "footer",
        "responsive", "platforms", "mode"
    )

    /**
     * Common attributes applied for every component through the shared
     * pipeline: DynamicView (visibility / hidden) and
     * ModifierBuilder.buildModifier (size, margins, paddings, background,
     * border, corner radius, shadow, opacity, alignment / relative
     * constraints, gesture handlers, weight, frame, testTag from id).
     * Components pass `COMMON_APPLIED + <their own applied set>` to
     * [check].
     */
    val COMMON_APPLIED: Set<String> = setOf(
        "alignBottom", "alignBottomOfView", "alignBottomView",
        "alignCenterHorizontalView", "alignCenterVerticalView",
        "alignLeft", "alignLeftOfView", "alignLeftView",
        "alignRight", "alignRightOfView", "alignRightView",
        "alignTop", "alignTopOfView", "alignTopView",
        "alpha", "aspectHeight", "aspectWidth",
        "background", "borderColor", "borderStyle", "borderWidth",
        "bottomMargin", "bottomPadding",
        "centerHorizontal", "centerInParent", "centerVertical",
        "cornerRadius", "endMargin", "frame", "gravity",
        "height", "heightWeight",
        "leftMargin", "leftPadding", "margins",
        "maxHeight", "maxWidth", "minHeight", "minWidth",
        "onAppear", "onClick", "onDisappear", "onclick",
        "opacity", "padding", "paddingBottom", "paddingEnd",
        "paddingHorizontal", "paddingLeft", "paddingRight",
        "paddingStart", "paddingTop", "paddingVertical", "paddings",
        "rightMargin", "rightPadding", "shadow",
        "startMargin", "startPadding",
        "topMargin", "topPadding",
        "weight", "width",
        "visibility", "hidden", "testId"
    )

    fun check(
        componentType: String,
        json: JsonObject,
        declared: Set<String>,
        applied: Set<String>,
        context: android.content.Context? = null
    ) {
        val enabled = enabledOverride ?: isAppDebuggable(context)
        if (!enabled) return

        for (key in json.keySet()) {
            if (key in STRUCTURAL_KEYS) continue
            if (key !in declared) continue // undeclared keys are the validator's business
            if (key in applied) continue
            val dedupeKey = "$componentType:$key"
            if (!warned.add(dedupeKey)) continue
            val message =
                "$componentType: declared attribute '$key' is present but not applied by the dynamic component"
            warningSink?.invoke(message)
            Log.w(TAG, message)
        }
    }

    private var debuggable: Boolean? = null

    private fun isAppDebuggable(context: android.content.Context?): Boolean {
        debuggable?.let { return it }
        if (context == null) return false
        val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        debuggable = isDebug
        return isDebug
    }
}
