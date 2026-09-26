package com.kotlinjsonui.dynamic.helpers

import com.google.gson.JsonObject

/**
 * What DynamicView does with `userInteractionEnabled` for each node it draws,
 * as two decisions it composes around (LocalInteractionStopped):
 *
 * - [nodeAsDrawn]: inside a node that stops interaction (the local is true),
 *   the node its components, its click, its Role.Button and the image rule
 *   read is marked (TapAccessibility.STOPPED_KEY — the key kjui's codegen
 *   writes), so its tap is none.
 * - [stopsWhatItComposes]: a node whose flag is false, or a binding
 *   resolving false, provides the stop to what it composes — a Collection's
 *   cells and an Embed's screen included, which are DynamicViews of their
 *   own under the same composition. Inside a stop already, the local is true
 *   and nothing more is provided.
 */
object InteractionMarking {
    fun nodeAsDrawn(json: JsonObject, stoppedAround: Boolean): JsonObject =
        if (stoppedAround) TapAccessibility.markStopped(json) else json

    fun stopsWhatItComposes(json: JsonObject, data: Map<String, Any>, stoppedAround: Boolean): Boolean =
        !stoppedAround && ModifierBuilder.interactionBlocked(json, data)

    /**
     * A control a stop holds (TapAccessibility.isControl) — the stop around
     * it, or its own flag — writes nothing: `updateData`, which every control
     * writes its value through, is a no-op for it. Its node reads disabled
     * (ModifierBuilder.applyStoppedControl), which stops TalkBack's actions on
     * that node; this stops the rest — a key press on a focused control, and
     * the inner node of a wrapped one (a Radio's item, a Segment's tab).
     */
    fun dataAsDrawn(json: JsonObject, data: Map<String, Any>, stoppedAround: Boolean): Map<String, Any> {
        val type = json.get("type")?.takeIf { it.isJsonPrimitive }?.asString
        if (!TapAccessibility.isControl(type)) return data
        if (!stoppedAround && !ModifierBuilder.interactionBlocked(json, data)) return data
        return data + (UPDATE_DATA to DROP_WRITES)
    }

    private const val UPDATE_DATA = "updateData"
    private val DROP_WRITES: (Map<String, Any>) -> Unit = { }
}
