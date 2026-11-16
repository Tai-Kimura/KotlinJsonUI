package com.kotlinjsonui.dynamic

import android.util.Log
import com.google.gson.JsonObject

/**
 * Whether a component the app draws itself (Configuration.customComponentHandler)
 * was drawn with the common stages its node declares. DynamicView applies the
 * lifecycle effects to every node; the other common stages — size, margins,
 * padding, background, the tap and its gestures, alpha — reach an app's
 * component only through ModifierBuilder.buildModifier, which the Dynamic
 * component `kjui g converter` generates calls. One drawn without it applies
 * none of them, while kjui's codegen applies them to the same component in
 * release. That was silent; a debuggable build now says so, once per type.
 *
 * DynamicView opens an entry for each node it draws ([begin]) and closes it
 * after the node's dispatch ([end]); buildModifier marks the open entry
 * ([markApplied]). A child's DynamicView opens an entry of its own, so what a
 * child builds does not mark its parent's.
 */
object AppComponentStages {
    /** The node attributes buildModifier applies. */
    val stageKeys: List<String> = listOf(
        "onClick", "onclick", "onLongPress", "onPan", "onPinch",
        "userInteractionEnabled", "canTap", "enabled",
        "width", "height", "padding", "paddings", "margins", "background", "cornerRadius",
        "borderWidth", "borderColor", "alpha", "opacity", "offsetX", "offsetY", "shadow",
    )

    private const val TAG = "JsonUIAppComponent"

    /** Test hook: receives every message. */
    var warningSink: ((String) -> Unit)? = null

    private val marks = ThreadLocal<ArrayList<Boolean>>()

    /** This thread's open entries, innermost last. */
    private fun open(): ArrayList<Boolean> = marks.get() ?: ArrayList<Boolean>().also { marks.set(it) }
    private val warned = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    /** DynamicView, before it draws a node. */
    fun begin() {
        open().add(false)
    }

    /** ModifierBuilder.buildModifier: the node being drawn got the common stages. */
    fun markApplied() {
        val entries = open()
        if (entries.isNotEmpty()) entries[entries.size - 1] = true
    }

    /**
     * DynamicView, after the node's dispatch. When the app's handler drew it
     * ([drawnByApp]), its node declares a common stage and nothing marked the
     * entry, says so once per type.
     */
    fun end(type: String, json: JsonObject, drawnByApp: Boolean, context: android.content.Context? = null) {
        val entries = open()
        val applied = if (entries.isEmpty()) true else entries.removeAt(entries.size - 1)
        if (!drawnByApp || applied) return
        val declared = stageKeys.filter { json.has(it) }
        if (declared.isEmpty() || !warned.add(type)) return
        val message = "Custom component '$type' declares ${declared.joinToString(", ")}, but it was drawn " +
            "without ModifierBuilder.buildModifier, so Debug applies none of them (kjui's codegen does). " +
            "Build its modifier with ModifierBuilder.buildModifier(json, data), as a generated Dynamic component does."
        warningSink?.invoke(message)
        if (DebugDiagnostics.isAppDebuggable(context)) Log.w(TAG, message)
    }

    /** Test hook: forget which types were said. */
    fun resetWarned() {
        warned.clear()
    }
}
