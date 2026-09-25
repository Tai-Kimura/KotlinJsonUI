package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assume
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Which of the standard modifier stages (ModifierBuilder.standardOrder) each
 * component DynamicView dispatches actually applies — NOT a test of the
 * suite, and skipped unless requested (`-e stageFamilyProbe 1`). It prints
 * the rows the family table is built from (jsonui-cli
 * docs/bugs/fixtures/kjui-stage-family/family_table.py);
 * CommonStagesOnEveryComponentTest asserts the same measurement.
 *
 * Per type, from [StageMeasurer]:
 * - `STAGE <type> <stage> +[…] -[…]`: the elements the stage adds and
 *   removes over the bare node;
 * - `STAGE_OVER`: cornerRadius over background, enabled over clickable;
 * - `STAGE_EFFECT` / `STAGE_EFFECT2`: the effects;
 * - `STAGE_JUDGE <type> <stage> Y|- <evidence>`: StageMeasurer.judge.
 */
@RunWith(AndroidJUnit4::class)
class CommonStageFamilyProbe {
    @get:Rule
    val rule = createComposeRule()

    @Before
    fun skipUnlessRequested() {
        val on = InstrumentationRegistry.getArguments().getString("stageFamilyProbe") == "1"
        Assume.assumeTrue("set -e stageFamilyProbe 1", on)
    }

    @Test
    fun whichStagesEachComponentApplies() {
        val m = StageMeasurer(rule)
        m.onError = { println("STAGE_ERROR $it") }
        m.start()
        for ((type, extra) in StageMeasurer.TYPES) {
            val r = m.measure(type, extra) ?: continue
            val (b0, b1) = r.backgroundPx
            println("STAGE_EFFECT $type background_px=$b0->$b1 radius_px=${r.radiusPx} click_calls=${r.clickCalls} disabled_calls=${r.disabledCalls}")
            val (red0, red1) = r.redPx
            val dp = { p: Pair<Int, Int>? -> p?.let { "${it.first}x${it.second}" } }
            println("STAGE_EFFECT2 $type ctrl_diff=${r.controlDiff} tag=${r.tagged} size=${dp(r.sizeDp)} margins=${dp(r.marginsDp)} alpha_diff=${r.alphaDiff} offset_diff=${r.offsetDiff} padding_diff=${r.paddingDiff} border_red=$red0->$red1 uie_calls=${r.uieCalls}")
            println("STAGE_BASE $type ${r.base.toSortedMap()}")
            for ((stage, _) in StageMeasurer.STAGES) {
                if (stage !in r.with) continue
                println("STAGE $type $stage +${r.added(stage)} -${r.removed(stage)}")
                when (stage) {
                    "cornerRadius" -> println("STAGE_OVER $type $stage over background +${r.added(stage, "background")}")
                    "enabled" -> println("STAGE_OVER $type $stage over clickable +${r.added(stage, "clickable")}")
                }
            }
            for ((stage, _) in StageMeasurer.STAGES) {
                val (applied, why) = StageMeasurer.judge(r, stage)
                println("STAGE_JUDGE $type $stage ${if (applied) "Y" else "-"} $why")
            }
        }
    }
}
