// Pasted from what `kjui build` (kjui_tools of jsonui-cli rel/v1.8.121 = 32785ce8) emits for
// the static inputs layout of StaticValuedControlsProbeTest — ticket
// static-valued-controls-do-not-change-on-a-users-tap. Changed from the output: the package
// (com.kotlinjsonui.probe.* → com.kotlinjsonui.conformance.staticvalued) and the unused
// `import com.kotlinjsonui.probe.R`; nothing else.
// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/static_inputs.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.kotlinjsonui.conformance.staticvalued


data class StaticInputsData(
    var tfIsFocused: Boolean = false,
    var tvIsFocused: Boolean = false
) {
    companion object {
        // Update properties from map
        fun fromMap(map: Map<String, Any>): StaticInputsData {
            return StaticInputsData(
                tfIsFocused = map["tfIsFocused"] as? Boolean ?: false,
                tvIsFocused = map["tvIsFocused"] as? Boolean ?: false
            )
        }
    }

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["tfIsFocused"] = tfIsFocused
        map["tvIsFocused"] = tvIsFocused
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
