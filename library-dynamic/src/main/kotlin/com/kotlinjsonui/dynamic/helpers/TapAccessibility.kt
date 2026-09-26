package com.kotlinjsonui.dynamic.helpers

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.TypeSynonyms

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
 * `userInteractionEnabled` stops the node and everything in it, and the rule
 * reads it as it reads `canTap: false`: `false` makes the node and every node
 * inside it no tap — no click, no role (a click the pointer blocker had stopped
 * was still announced as a button, and TalkBack's double tap still called it).
 * DynamicView marks a node inside one whose flag is false, or whose binding
 * resolves false, with [STOPPED_KEY] (the key kjui's codegen writes); a
 * binding on the node itself is resolved where the click is attached
 * (ModifierBuilder.tapGateOpen). A control's own type still counts inside.
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
    private val TEXT_TYPES = setOf("label") // drawn types only (a Text is drawn as a Label)

    /**
     * Asked of the type the node is drawn as ([TypeSynonyms.drawnType]): an
     * HStack is a View, a Textarea a TextView. Read as written, a synonym the
     * lists do not hold counted as a custom component (operable), so the
     * tappable around it was not flattened where the same layout spelled
     * canonically was.
     */
    fun isInteractiveType(type: String?): Boolean {
        val t = type?.let { TypeSynonyms.drawnType(it) }?.lowercase() ?: return true
        return t in interactive || t !in known
    }

    /**
     * The interactive types, as drawn, that hold the operated things rather
     * than being one (jsonui-cli shared/core/tap_accessibility.rb
     * STOP_CONTAINER_TYPES, and the vectors' `controls.stop_container_types`).
     * A Table, a TableView or a RecyclerView is drawn as a Collection, so the
     * drawn type is never one of them.
     */
    val STOP_CONTAINER_TYPES = listOf("Collection", "Embed", "ScrollView", "TabView", "Web")
    private val STOP_CONTAINERS = STOP_CONTAINER_TYPES.map { it.lowercase() }.toSet()

    /**
     * A control a stop holds — operated where it is, not a container
     * (jsonui-cli shared/core/tap_accessibility.rb `control?`): the stop takes
     * its operation without a tap on it — TalkBack's click on its node too
     * (ModifierBuilder.applyStoppedControl, InteractionMarking.dataAsDrawn).
     * Asked of the type it is drawn as, as [isInteractiveType] is. An app's
     * own component is none, whatever it spells (4f's ruling, jsonui-cli
     * 1.9.0): it carries its own role — and [INTERACTIVE_TYPES] holds alias
     * spellings as written (Toggle, Table), which a registered type draws as
     * written.
     */
    fun isControl(type: String?): Boolean {
        if (type == null || type in Configuration.customComponentTypes) return false
        val t = TypeSynonyms.drawnType(type).lowercase()
        return t in interactive && t !in STOP_CONTAINERS
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

    /**
     * A tap the Dynamic runtime attaches: a handler, not statically disabled,
     * not gated shut, and not stopped — by the node's own
     * `userInteractionEnabled: false`, or by a node around it ([STOPPED_KEY]).
     */
    fun isTappable(node: JsonObject): Boolean =
        !disabled(node) && !gatedShut(node) && !stops(node) && !stoppedAround(node) &&
            clickHandlers(node).isNotEmpty()

    /** On a node inside one that stops interaction: true (DynamicView writes it). */
    const val STOPPED_KEY = "_tapStopped"

    /** `userInteractionEnabled: false`: the node and everything in it take no interaction. */
    fun stops(node: JsonObject): Boolean {
        val e = node.get("userInteractionEnabled") ?: return false
        return e.isJsonPrimitive && e.asJsonPrimitive.isBoolean && !e.asBoolean
    }

    /** A node around it stops interaction ([STOPPED_KEY]). */
    fun stoppedAround(node: JsonObject): Boolean {
        val e = node.get(STOPPED_KEY) ?: return false
        return e.isJsonPrimitive && e.asJsonPrimitive.isBoolean && e.asBoolean
    }

    /** The node as it is, marked as inside one that stops interaction (a shallow copy). */
    fun markStopped(node: JsonObject): JsonObject {
        val copy = JsonObject()
        node.entrySet().forEach { (key, value) -> copy.add(key, value) }
        copy.addProperty(STOPPED_KEY, true)
        return copy
    }

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
        if (type(node)?.let { TypeSynonyms.drawnType(it) }?.lowercase() !in TEXT_TYPES) return false
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
     * handler either. A Label's links stop with it: inside a node that stops
     * interaction ([stopped], which counts the Label's own flag where
     * holdsAControl passes it) they are no links
     * (DynamicTextComponent.linksEnabled), so they do not count
     * (jsonui-cli 1.9.0; shared/core/tap_accessibility.rb `operable?`). As
     * [hasLongPress] does, the node's own flag and the mark of a stop around
     * it ([STOPPED_KEY] — a cell drawn under one) are read here too, for a
     * caller asking about the node itself.
     */
    fun isOperable(node: JsonObject, stopped: Boolean = false): Boolean =
        isInteractiveType(type(node)) || (!stopped && isTappable(node)) || hasLongPress(node, stopped) ||
            (!stopped && !stops(node) && !stoppedAround(node) && isLinkedText(node))

    /**
     * A long press a user can perform: a handler (`handlerValues` — an empty or
     * blank one names no method, as for a tap), on a view not statically
     * disabled. `canTap` gates the tap, not the long press. It read "any
     * value" before, so a blank long press counted. `userInteractionEnabled`
     * stops it as it stops a tap: false on the node, the mark of a node around
     * it ([STOPPED_KEY]), or [stopped]. A bound flag still presses.
     */
    fun hasLongPress(node: JsonObject, stopped: Boolean = false): Boolean =
        !stopped && !stops(node) && !stoppedAround(node) && !disabled(node) &&
            handlerValues(node.get("onLongPress")).isNotEmpty()

    /**
     * The keys the tools write on a node that the layout did not: the
     * position stamp and the rule's own marks (jsonui-cli
     * shared/core/tap_accessibility.rb WRITTEN_STAMPS).
     */
    private val WRITTEN_STAMPS = setOf("_layoutPath", "_tapShape", STOPPED_KEY, "_tapGates")

    /** A data-only element — `data` the only key the layout wrote — declares the data and draws nothing (jsonui-cli `data_only?`). */
    fun isDataOnly(node: JsonObject): Boolean = node.keySet().minus(WRITTEN_STAMPS) == setOf("data")

    /**
     * The children a node draws: the shapes do not count a data-only one
     * (jsonui-cli `drawn_children`). It read as a child of unknown type, a
     * control, and a Label with onClick whose only child declared its data
     * was no button (4f's ruling, jsonui-cli 1.9.0).
     */
    fun drawnChildren(node: JsonObject): List<JsonObject> = children(node).filterNot { isDataOnly(it) }

    /**
     * Something inside [node] a user can operate on its own. [stopped]: a node
     * around the child has `userInteractionEnabled: false`, so its own tap is
     * none (its type still says whether it is a control).
     */
    fun holdsAControl(node: JsonObject, stopped: Boolean = false): Boolean =
        drawnChildren(node).any {
            val inner = stopped || stops(it)
            isOperable(it, inner) || holdsAControl(it, inner)
        }

    /** The shape of one node, or null when it is not a tappable (a stopped one included). */
    fun shape(node: JsonObject): Shape? {
        if (!isTappable(node)) return null
        if (isInteractiveType(type(node))) return Shape.NONE
        if (drawnChildren(node).isEmpty()) return Shape.BUTTON
        if (holdsAControl(node)) return Shape.NONE
        return Shape.COMBINE
    }

    /** Whether the tap should report `Role.Button`. */
    fun isButton(node: JsonObject): Boolean =
        shape(node).let { it == Shape.BUTTON || it == Shape.COMBINE }
}


