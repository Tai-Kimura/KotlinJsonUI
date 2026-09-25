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
                val (applied, why) = StageMeasurer.judge(r, stage)
                if (!applied) skipped += "$label $stage ($why)"
            }
        }
        assertEquals("components that could not be drawn", emptyList<String>(), unmeasured)
        assertEquals("stages a component skips", emptyList<String>(), skipped)
    }
}
