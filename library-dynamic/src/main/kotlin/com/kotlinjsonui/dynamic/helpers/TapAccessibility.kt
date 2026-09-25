package com.kotlinjsonui.dynamic.helpers

import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * Whether TalkBack is told a tappable is a button — the rule jsonui-cli's sjui /
 * kjui codegen and SwiftJsonUI's Dynamic runtime also run
 * (`shared/core/tap_accessibility.rb`, and its table
 * `shared/core/tap_accessibility_vectors.json`, copied byte for byte into the
 * test resources and compared by CI's vendored-attr-guard).
 *
 * A tap is `.clickable`, which reports no role. Each tappable gets one shape:
 * BUTTON (no children) and COMBINE (children, none operable on its own) are
 * given `role = Role.Button`; NONE (its own type is a control, or something
 * inside is operable on its own: an interactive or unknown type, a descendant
 * with its own tap or long press, a Label with links) is left as it was — the
 * decision the iOS side makes, where a container over a control has no shape
 * that works.
 *
 * Which types are operable is declared per type (`interactive`) in jsonui-cli's
 * `shared/core/component_metadata.json`; the two lists below are that
 * declaration with its aliases, held equal to the vendored table by
 * TapAccessibilityVectorsTest. A type the declaration does not know counts as
 * operable.
 */
object TapAccessibility {
    enum class Shape { BUTTON, COMBINE, NONE }

    val INTERACTIVE_TYPES = listOf(
        "Button", "Check", "CheckBox", "Checkbox", "Collection", "EditText",
        "Embed", "Input", "Radio", "RecyclerView", "ScrollView", "Segment",
        "SelectBox", "Slider", "Switch", "TabView", "Table", "TableView",
        "TextField", "TextView", "Toggle", "Web"
    )

    val KNOWN_TYPES = INTERACTIVE_TYPES + listOf(
        "Blur", "CircleImage", "CircleImageView", "CircleView", "GradientView", "IconLabel",
        "Image", "ImageView", "Img", "Indicator", "Label", "NetworkImage",
        "Progress", "SafeAreaView", "Text", "View"
    )

    private val interactive = INTERACTIVE_TYPES.map { it.lowercase() }.toSet()
    private val known = KNOWN_TYPES.map { it.lowercase() }.toSet()
    private val TEXT_TYPES = setOf("label", "text")

    fun isInteractiveType(type: String?): Boolean {
        val t = type?.lowercase() ?: return true
        return t in interactive || t !in known
    }

    private fun disabled(node: JsonObject): Boolean {
        val e = node.get("enabled") ?: return false
        return e.isJsonPrimitive && e.asJsonPrimitive.isBoolean && !e.asBoolean
    }

    private fun type(node: JsonObject): String? =
        node.get("type")?.takeIf { it.isJsonPrimitive }?.asString

    private val BINDING = Regex("""@\{(.*)\}""", RegexOption.DOT_MATCHES_ALL)

    /**
     * A handler names a method that is not blank: a binding's inside
     * (`@{onOpen}`), a bare selector, or each string of an `onclick` array.
     * `""`, `"   "`, `"@{}"`, `[]` and `[""]` name none, so they are no tap
     * (jsonui-cli shared/core/tap_accessibility.rb `handler?`; blank is
     * Unicode white space, a full-width space too). They used to attach a
     * click that called nothing and took the click from the view around it.
     */
    fun namesAMethod(value: String): Boolean =
        (BINDING.matchEntire(value)?.groupValues?.get(1) ?: value).isNotBlank()

    /** The elements of a handler value that name a method, in the order written. */
    fun handlerValues(e: JsonElement?): List<String> = when {
        e == null || e.isJsonNull -> emptyList()
        e.isJsonArray -> e.asJsonArray
            .filter { it.isJsonPrimitive && it.asJsonPrimitive.isString }
            .map { it.asString }
            .filter(::namesAMethod)
        e.isJsonPrimitive && e.asJsonPrimitive.isString -> listOf(e.asString).filter(::namesAMethod)
        else -> emptyList()
    }

    /**
     * What a click calls: onClick's handler when it names one (it wins when
     * both are declared), else each named element of onclick, in order.
     */
    fun clickHandlers(node: JsonObject): List<String> =
        handlerValues(node.get("onClick")).ifEmpty { handlerValues(node.get("onclick")) }

    /** A tap the Dynamic runtime attaches: a handler, not statically disabled, not gated shut. */
    fun isTappable(node: JsonObject): Boolean =
        !disabled(node) && !gatedShut(node) && clickHandlers(node).isNotEmpty()

    /** `canTap: false`, the Compose tap gate. */
    private fun gatedShut(node: JsonObject): Boolean {
        val e = node.get("canTap") ?: return false
        return e.isJsonPrimitive && e.asJsonPrimitive.isBoolean && !e.asBoolean
    }

    fun children(node: JsonObject): List<JsonObject> =
        listOf("child", "children").flatMap { key ->
            val v = node.get(key)
            when {
                v == null -> emptyList()
                v.isJsonArray -> v.asJsonArray.filter { it.isJsonObject }.map { it.asJsonObject }
                v.isJsonObject -> listOf(v.asJsonObject)
                else -> emptyList()
            }
        }

    /** A Label with links of its own: `linkable` (true or bound), or a tappable range. */
    fun isLinkedText(node: JsonObject): Boolean {
        if (type(node)?.lowercase() !in TEXT_TYPES) return false
        val linkable = node.get("linkable")
        if (linkable != null && linkable.isJsonPrimitive) {
            val p = linkable.asJsonPrimitive
            if (p.isBoolean && p.asBoolean) return true
            if (p.isString && p.asString.startsWith("@{")) return true
        }
        val ranges = node.get("partialAttributes")
        if (ranges == null || !ranges.isJsonArray) return false
        return ranges.asJsonArray.any { r ->
            r.isJsonObject && clickHandlers(r.asJsonObject).isNotEmpty()
        }
    }

    /**
     * Operable inside a tappable: an interactive or unknown type, its own tap, a
     * long press (a screen-reader action), or links. A tappable whose only
     * handler is a long press is not a tap; `canTap` without onClick has no
     * handler either.
     */
    fun isOperable(node: JsonObject): Boolean =
        isInteractiveType(type(node)) || isTappable(node) || hasLongPress(node) || isLinkedText(node)

    /**
     * A long press a user can perform: a handler (`handlerValues` — an empty or
     * blank one names no method, as for a tap), on a view not statically
     * disabled. `canTap` gates the tap, not the long press. It read "any
     * value" before, so a blank long press counted.
     */
    fun hasLongPress(node: JsonObject): Boolean =
        !disabled(node) && handlerValues(node.get("onLongPress")).isNotEmpty()

    fun holdsAControl(node: JsonObject): Boolean =
        children(node).any { isOperable(it) || holdsAControl(it) }

    fun shape(node: JsonObject): Shape? {
        if (!isTappable(node)) return null
        if (isInteractiveType(type(node))) return Shape.NONE
        if (children(node).isEmpty()) return Shape.BUTTON
        if (holdsAControl(node)) return Shape.NONE
        return Shape.COMBINE
    }

    /** Whether the tap should report `Role.Button`. */
    fun isButton(node: JsonObject): Boolean =
        shape(node).let { it == Shape.BUTTON || it == Shape.COMBINE }
}
