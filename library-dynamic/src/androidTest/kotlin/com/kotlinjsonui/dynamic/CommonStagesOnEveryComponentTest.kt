package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The SSoT declares the standard stages on `common`, so every component
 * applies every one of them: testTag, margins, size, offset, alpha, shadow,
 * background, cornerRadius, border, clickable, enabled,
 * userInteractionEnabled, paddings.
 *
 * Measured 2026-09-26 (CommonStageFamilyProbe, API 35 emulator): 49 stages
 * on 9 components were skipped — an Embed applied none, a TabView only
 * userInteractionEnabled, a SafeAreaView no alpha / shadow / radius / border
 * / click, CircleImage and SafeAreaView put their margins inside the size —
 * and an Image whose resource was not found drew nothing, its stages with
 * it. A component builds its own chain where it must (a SafeAreaView's
 * background before the system-bar padding, a TextField's paddings as its
 * content padding), so the chain is not what is asserted: each stage is, by
 * its effect where one shows and by the modifier elements otherwise
 * ([StageMeasurer.judge]).
 *
 * Triangle is not declared (neither in the SSoT nor its type-synonym canon),
 * and is not held to it.
 */
@RunWith(AndroidJUnit4::class)
class CommonStagesOnEveryComponentTest {
    @get:Rule
    val rule = createComposeRule()

    private val undeclared = setOf("Triangle")

    /**
     * The clickable and enabled stages of a control whose tap is its own
     * operation — it calls onClick from that operation, after it, with no
     * outer `.clickable` (kjui codegen 62e15706) — where a tap at the root's
     * centre cannot reach the operation: a SelectBox calls after the pick,
     * which one tap only opens; a Radio item's button is not its label, which
     * the centre lands on; a text field calls no onClick at all (its tap
     * focuses it — the tap rule's shape `none`, and iOS attaches no tap to a
     * field). ControlsCallOnClickFromTheirOperationTest operates each of them
     * and holds these cells, `enabled` and `canTap` with them. The judge's
     * own answer stays in the probe's rows.
     */
    private val operatedElsewhere = mapOf(
        "SelectBox" to setOf("clickable", "enabled"),
        "Radio" to setOf("clickable", "enabled"),
        "TextField" to setOf("clickable", "enabled"),
        "EditText" to setOf("clickable", "enabled"),
        "Input" to setOf("clickable", "enabled"),
        "TextView" to setOf("clickable", "enabled"),
    )

    @Test
    fun everyComponentAppliesEveryStage() {
        val m = StageMeasurer(rule)
        m.start()
        val cases = StageMeasurer.TYPES.filter { it.first !in undeclared }.map { (t, x) -> Triple(t, t, x) } +
            // An image whose resource is not found still takes its place and
            // its stages.
            Triple("Image, srcName not found", "Image", """, "srcName": "no_such_drawable"""")
        val skipped = mutableListOf<String>()
        val unmeasured = mutableListOf<String>()
        for ((label, type, extra) in cases) {
            val r = m.measure(type, extra)
            if (r == null) {
                unmeasured += label
                continue
            }
            for ((stage, _) in StageMeasurer.STAGES) {
                if (stage in (operatedElsewhere[type] ?: emptySet())) continue
                val (applied, why) = StageMeasurer.judge(r, stage)
                if (!applied) skipped += "$label $stage ($why)"
            }
        }
        assertEquals("components that could not be drawn", emptyList<String>(), unmeasured)
        assertEquals("stages a component skips", emptyList<String>(), skipped)
    }
}
