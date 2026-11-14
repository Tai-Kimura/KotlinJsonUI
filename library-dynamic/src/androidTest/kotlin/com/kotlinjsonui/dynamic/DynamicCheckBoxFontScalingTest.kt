package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.components.DynamicCheckBoxComponent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A CheckBox's label by the text size rule when the CheckBox declares fontScaling / maxFontScale and no font attribute
 * (the conformance fixtures' shape): its label took the plain `Text(label)` branch, which followed the system's text
 * size whatever the CheckBox declared. At a font scale of 2.0 the one that does not follow is as wide as at 1.0, the
 * following one wider, the one capped at 1.3 between. Read as each label's width.
 */
@RunWith(AndroidJUnit4::class)
class DynamicCheckBoxFontScalingTest {
    @get:Rule val rule = createComposeRule()

    private val label = "Plan the order of the stops"
    private val scale = mutableStateOf(1f)

    private fun checkBox(extra: String) =
        JsonParser.parseString("""{"type":"CheckBox","text":"$label"$extra}""").asJsonObject

    private fun content() = rule.setContent {
        val d = LocalDensity.current
        MaterialTheme {
            CompositionLocalProvider(LocalDensity provides Density(d.density, fontScale = scale.value)) {
                Column {
                    DynamicCheckBoxComponent.create(checkBox(""))
                    DynamicCheckBoxComponent.create(checkBox(""","fontScaling":false"""))
                    DynamicCheckBoxComponent.create(checkBox(""","maxFontScale":1.3"""))
                }
            }
        }
    }

    private fun widths(at: Float): List<Float> {
        rule.runOnIdle { scale.value = at }
        rule.waitForIdle()
        val nodes = rule.onAllNodesWithText(label, useUnmergedTree = true).fetchSemanticsNodes()
        assertEquals("three labels", 3, nodes.size)
        return nodes.map { it.boundsInRoot.width }
    }

    @Test fun at_2_0_the_fixed_label_is_as_wide_as_at_1_0_the_following_wider_the_capped_between() {
        content()
        val fixedAt1 = widths(1f)[1]
        val (follows, fixed, capped) = widths(2f)
        assertEquals("fontScaling false: the same width as at 1.0", fixedAt1, fixed, 1f)
        assertTrue("following is wider: $follows vs $fixed", follows > fixed * 1.3f)
        assertTrue("capped at 1.3 lies between: $fixed < $capped < $follows", capped > fixed * 1.15f && capped < follows)
    }
}
