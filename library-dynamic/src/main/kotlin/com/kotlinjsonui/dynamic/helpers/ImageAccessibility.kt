package com.kotlinjsonui.dynamic.helpers

import androidx.compose.runtime.compositionLocalOf
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * What TalkBack reads for an image — the rule jsonui-cli's codegen applies
 * (shared/core/image_accessibility.rb), and the table both run
 * (image_accessibility_vectors.json, copied byte for byte into the tests).
 *
 * `alt` (aliases `accessibilityLabel`, `contentDescription`) is the spoken
 * text: a strings.json key or literal, or a binding. An image is
 * - LABEL: alt is a non-empty string — it is read;
 * - DECORATIVE: alt is "", or there is no alt and the image operates
 *   nothing — contentDescription = null;
 * - CONTROL: there is no alt and the image operates a control (a tap
 *   handler of its own, or it sits in the nearest tappable whose content
 *   names nothing) — it keeps what it read before alt existed.
 *
 * The nearest tappable reaches an image through [LocalImageTappable], which
 * DynamicView provides around every node that has a tap handler.
 */
object ImageAccessibility {
    enum class Role { LABEL, DECORATIVE, CONTROL }

    /** Image, its type aliases (component_metadata.json) and NetworkImage. */
    private val IMAGE_TYPES = setOf("image", "circleimage", "circleimageview", "imageview", "img", "networkimage")

    /** The canonical spelling first, then the declared aliases. */
    val ALT_KEYS = listOf("alt", "accessibilityLabel", "contentDescription")

    /** What a screen-reader user activates: a tap and a long press. */
    val TAP_KEYS = listOf("onClick", "onclick", "onLongPress")

    /** Text that names a control it sits in (on an image, hint / placeholder name an image). */
    val TEXT_KEYS = listOf("text", "hint", "placeholder", "label", "prompt")

    fun isImage(node: JsonObject): Boolean =
        node.get("type")?.takeIf { it.isJsonPrimitive }?.asString?.lowercase() in IMAGE_TYPES

    fun isTappable(node: JsonObject): Boolean = TAP_KEYS.any { node.has(it) }

    /** The image's alt as written, or null when it declares none (JSON null counts as none). */
    fun alt(node: JsonObject): String? {
        for (key in ALT_KEYS) {
            val value = node.get(key) ?: continue
            if (value.isJsonNull) return null
            return if (value.isJsonPrimitive) value.asString else value.toString()
        }
        return null
    }

    fun children(node: JsonObject): List<JsonObject> =
        listOf("child", "children").flatMap { key ->
            when (val value = node.get(key)) {
                is JsonArray -> value.filterIsInstance<JsonObject>()
                is JsonObject -> listOf(value)
                else -> emptyList()
            }
        }

    /**
     * True when something inside [node] (itself included) names a control:
     * text on a non-image, or an image whose alt is not empty. An include not
     * expanded yet names nothing here, which errs toward CONTROL — the image
     * stays readable rather than hidden.
     */
    fun namesSomething(node: JsonObject): Boolean {
        if (isImage(node)) {
            val value = ALT_KEYS.firstOrNull { node.has(it) }?.let { node.get(it) }
            return value != null && value.isStringNonEmpty()
        }
        if (TEXT_KEYS.any { key -> node.get(key).let { it != null && it.isStringNonEmpty() } }) return true
        val items = node.get("items")
        if (items is JsonArray && items.any { it.isStringNonEmpty() }) return true
        return children(node).any { namesSomething(it) }
    }

    /** The role of an image, given the nearest tappable around it (null when none). */
    fun role(node: JsonObject, nearestTappable: JsonObject?): Role {
        val value = alt(node)
        if (value != null) return if (value.isEmpty()) Role.DECORATIVE else Role.LABEL
        if (isTappable(node)) return Role.CONTROL
        if (nearestTappable != null && !namesSomething(nearestTappable)) return Role.CONTROL
        return Role.DECORATIVE
    }

    /**
     * The contentDescription for a role: the resolved alt for LABEL (a bound
     * alt that resolves to "" is decorative), null for DECORATIVE, and
     * [legacy] — what this image read before alt existed — for CONTROL.
     */
    fun contentDescription(role: Role, resolvedAlt: () -> String, legacy: String): String? =
        when (role) {
            Role.LABEL -> resolvedAlt().ifEmpty { null }
            Role.DECORATIVE -> null
            Role.CONTROL -> legacy
        }

    private fun JsonElement.isStringNonEmpty(): Boolean =
        isJsonPrimitive && asJsonPrimitive.isString && asString.isNotEmpty()
}

/** The nearest node with a tap handler around the one being composed. */
val LocalImageTappable = compositionLocalOf<JsonObject?> { null }
