package com.kotlinjsonui.dynamic

import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.generated.JsonUIBindPrimaryValue

/**
 * `bind`, folded into the attribute it stands for — the jui layout
 * normalizer's fold (canonicalizer `_fold_bind`), for a layout the
 * normalizer did not fold (normalizeLayouts false, a layout from a server,
 * an app's own JsonObject). SSoT common.bind: "an alternative spelling to
 * each component's own value attribute, which takes precedence when both
 * are set"; `primaryValue` names, per section, the attributes `bind` stands
 * for (the generated [JsonUIBindPrimaryValue], vendored from jsonui-cli's
 * attr codegen — the table SwiftJsonUI's fold, the normalizer and the shared
 * validator read, shared/core/bind_fold.rb):
 * - a lone `bind` becomes the first of them;
 * - beside any of them, `bind` is dropped (the shared validator names it at
 *   build);
 * - on a section the table does not name, `bind` stays.
 *
 * So no component reads `bind` for its value: each read it itself, in its
 * own order, and they disagreed — Switch / CheckBox / Slider / Segment /
 * SelectBox took `bind` over a STATIC value of their own (a static
 * `isOn: true` beside `bind` drew the bound value), Progress took it over a
 * bound `progress` too, and Toggle never read it.
 *
 * Applied by DynamicView to every node a built-in component draws, after the
 * node's style, include and responsive overrides are merged and its type
 * synonym is resolved — the node the component reads, as SwiftJsonUI folds
 * just before decode — so a `bind` or a value from a style or a responsive
 * branch folds as the merged node says. An app's own component (the
 * customComponentHandler) gets its node as written.
 */
object BindFold {
    /** [node] with its `bind` folded; the same object when there is nothing to fold. [type] is the drawn section. */
    fun fold(node: JsonObject, type: String): JsonObject {
        val bind = node.get("bind") ?: return node
        // the generated table takes a plain map, as every generated parser
        // does; only a node with `bind` pays for the conversion
        val values = JsonUIBindPrimaryValue.attributesFor(type, TypedAttrs.toAttrMap(node))
        if (values.isEmpty()) return node
        val folded = JsonObject()
        for ((key, value) in node.entrySet()) if (key != "bind") folded.add(key, value)
        if (values.none { node.has(it) }) folded.add(values.first(), bind)
        return folded
    }
}
