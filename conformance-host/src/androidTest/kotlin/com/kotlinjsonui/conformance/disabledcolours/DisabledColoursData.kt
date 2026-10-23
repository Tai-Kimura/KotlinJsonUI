// Pasted from what `kjui build` emits for DisabledColoursProbeTest's layout — kjui_tools of jsonui-cli
// fix/bound-enabled-disabled-colors 27adb275 (ticket 2026-10-23-bound-enabled-disabled-colors-three-faces): a bound
// `enabled` chooses the disabled colours. Changed from the output: the package ({com.example.app.*,
// com.kotlinjsonui.generated} → com.kotlinjsonui.conformance.disabledcolours, so the generated ColorManager is the probe's own and never the codegen
// host's), the imports that named them, and the project's resources written as their values
// (colorResource(R.color.dark_blue) → Color(0xFF0000FF), R.color.light_gray → Color(0xFF999999),
// stringResource(R.string.disabled_colours_mmmm) → "MMMM"). The `if (data.on) … else …` choices are as emitted.
// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    Layouts/disabled_colours.json
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.kotlinjsonui.conformance.disabledcolours


data class DisabledColoursData(
    var on: Boolean = true,
    var bg: String = "#0000FF",
    var fg: String = "#123456"
) {
    companion object {

        // The class each Data field is declared with, as the layout spells it: what
        // the test bridge judges an initialState value by (jsonui-test-runner spec 10.3).
        // Requires KotlinJsonUI >= 2.44.0 (test bridge)
        val testDeclaredClasses: Map<String, String> = mapOf(
            "on" to "Boolean",
            "bg" to "String",
            "fg" to "String"
        )
        // Update properties from map
        fun fromMap(map: Map<String, Any>): DisabledColoursData {
            return DisabledColoursData(
                on = map["on"] as? Boolean ?: true,
                bg = map["bg"] as? String ?: "#0000FF",
                fg = map["fg"] as? String ?: "#123456"
            )
        }
    }

    // UI test initialState (jsonui-test-runner spec section 10.3): each value is judged by
    // JsonUITestValue.check against testDeclaredClasses, the accepted ones go to `update`
    // (the ViewModel's updateData) in one call, the rest come back with the reason.
    // Requires KotlinJsonUI >= 2.44.0 (test bridge)
    fun applyTestInitialState(values: Map<String, Any?>, update: ((Map<String, Any?>) -> Unit)?): List<com.kotlinjsonui.core.RejectedKey> =
        com.kotlinjsonui.core.JsonUITestValue.applyAll(values, testDeclaredClasses, update)

    // The state bridge's data (jsonui-test-runner spec section 10.4): the fields an
    // initialState can set, but not a Color (the Data holds the resolved colour).
    // Requires KotlinJsonUI >= 2.44.0 (test bridge)
    fun testSnapshot(): Map<String, Any?> = com.kotlinjsonui.core.JsonUITestValue.snapshot(
        mapOf(
            "on" to on,
            "bg" to bg,
            "fg" to fg
        ),
        testDeclaredClasses
    )

    // Convert properties to map for runtime use
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        
        // Data properties
        map["on"] = on
        map["bg"] = bg
        map["fg"] = fg
        
        return map
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══
