// Pasted from what `kjui build` (kjui_tools of jsonui-cli rel/v1.8.121 = 74bdbfeb) emits for four
// Segments with a `valueChange` — alone (segv…) and beside an onValueChange (segw…), unbound and bound —
// for SegmentValueChangeProbeTest (ticket control-onclick-is-called-differently-on-every-path). Changed
// from the output: the package (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.valuechange) and
// the unused `import com.kotlinjsonui.probe.R`.
// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/segment_value_change.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.kotlinjsonui.conformance.valuechange


data class SegmentValueChangeData(
    var onSegvuC: (() -> Unit)? = null,
    var onSegvuV: (() -> Unit)? = null,
    var onSegvbC: (() -> Unit)? = null,
    var onSegvbV: (() -> Unit)? = null,
    var onSegwuV: (() -> Unit)? = null,
    var onSegwuC: (() -> Unit)? = null,
    var onSegwuX: (() -> Unit)? = null,
    var onSegwbV: (() -> Unit)? = null,
    var onSegwbC: (() -> Unit)? = null,
    var onSegwbX: (() -> Unit)? = null,
    var segvbIdx: Int = 0,
    var segwbIdx: Int = 0
) {
    companion object {
        // Update properties from map
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): SegmentValueChangeData {
            return SegmentValueChangeData(
                onSegvuC = map["onSegvuC"] as? (() -> Unit)?,
                onSegvuV = map["onSegvuV"] as? (() -> Unit)?,
                onSegvbC = map["onSegvbC"] as? (() -> Unit)?,
                onSegvbV = map["onSegvbV"] as? (() -> Unit)?,
                onSegwuV = map["onSegwuV"] as? (() -> Unit)?,
                onSegwuC = map["onSegwuC"] as? (() -> Unit)?,
                onSegwuX = map["onSegwuX"] as? (() -> Unit)?,
                onSegwbV = map["onSegwbV"] as? (() -> Unit)?,
                onSegwbC = map["onSegwbC"] as? (() -> Unit)?,
                onSegwbX = map["onSegwbX"] as? (() -> Unit)?,
                segvbIdx = (map["segvbIdx"] as? Number)?.toInt() ?: 0,
                segwbIdx = (map["segwbIdx"] as? Number)?.toInt() ?: 0
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        onSegvuC?.let { map["onSegvuC"] = it }
        onSegvuV?.let { map["onSegvuV"] = it }
        onSegvbC?.let { map["onSegvbC"] = it }
        onSegvbV?.let { map["onSegvbV"] = it }
        onSegwuV?.let { map["onSegwuV"] = it }
        onSegwuC?.let { map["onSegwuC"] = it }
        onSegwuX?.let { map["onSegwuX"] = it }
        onSegwbV?.let { map["onSegwbV"] = it }
        onSegwbC?.let { map["onSegwbC"] = it }
        onSegwbX?.let { map["onSegwbX"] = it }
        map["segvbIdx"] = segvbIdx
        map["segwbIdx"] = segwbIdx
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
