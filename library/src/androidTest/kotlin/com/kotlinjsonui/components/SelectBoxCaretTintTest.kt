package com.kotlinjsonui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * caretAttributes.tintColor paints the default chevron only; the app's own picture (`src`) keeps its own colours
 * (the requester's ruling 2026-11-14, the three faces alike; ticket kjui-date-selectbox-caret-attributes-not-emitted).
 * The list SelectBox and the DateSelectBox draw one SelectBoxArrow, so each is read for both:
 *  - a red picture with a green tintColor stays red (SelectBoxArrow handed the tint to Icon, which drew it green);
 *  - the default chevron with a green tintColor is green (it was, and stays so: the pin of the rule's other half).
 */
@RunWith(AndroidJUnit4::class)
class SelectBoxCaretTintTest {
    @get:Rule val rule = createComposeRule()

    private val green = Color(0xFF00C800)
    private val picture = SelectBoxCaret(painter = ColorPainter(Color.Red), width = 24, height = 24, tintColor = green)
    private val chevron = SelectBoxCaret(width = 24, height = 24, tintColor = green)

    private fun count(tag: String, red: Boolean): Int {
        val map = rule.onNodeWithTag(tag).captureToImage().toPixelMap()
        var n = 0
        for (y in 0 until map.height) for (x in 0 until map.width) {
            val c = map[x, y]
            val hit = if (red) c.red > 0.9f && c.green < 0.1f && c.blue < 0.1f
            else c.red < 0.1f && c.green > 0.7f && c.blue < 0.1f
            if (hit) n++
        }
        return n
    }

    @Test
    fun tint_paints_the_default_chevron_and_never_the_apps_picture() {
        rule.setContent {
            MaterialTheme {
                Column {
                    Box(Modifier.testTag("list_picture")) {
                        SelectBox(value = "One", onValueChange = {}, options = listOf("One"), modifier = Modifier.width(200.dp), caret = picture)
                    }
                    Box(Modifier.testTag("date_picture")) {
                        DateSelectBox(value = "", onValueChange = {}, modifier = Modifier.width(200.dp), caret = picture)
                    }
                    Box(Modifier.testTag("list_chevron")) {
                        SelectBox(value = "One", onValueChange = {}, options = listOf("One"), modifier = Modifier.width(200.dp), caret = chevron)
                    }
                    Box(Modifier.testTag("date_chevron")) {
                        DateSelectBox(value = "", onValueChange = {}, modifier = Modifier.width(200.dp), caret = chevron)
                    }
                }
            }
        }
        rule.waitForIdle()
        for (kind in listOf("list", "date")) {
            val red = count("${kind}_picture", red = true)
            val tinted = count("${kind}_picture", red = false)
            assertTrue("$kind: the picture keeps its red ($red px)", red > 200)
            assertEquals("$kind: the picture is not painted with tintColor", 0, tinted)
            assertTrue("$kind: tintColor paints the default chevron", count("${kind}_chevron", red = false) > 10)
        }
    }
}
