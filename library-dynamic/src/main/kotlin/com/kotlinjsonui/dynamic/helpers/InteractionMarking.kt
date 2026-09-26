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
}
