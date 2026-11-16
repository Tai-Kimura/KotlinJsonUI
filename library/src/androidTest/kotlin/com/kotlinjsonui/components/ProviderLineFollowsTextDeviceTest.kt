package com.kotlinjsonui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.core.FontSpec
import com.kotlinjsonui.core.ResolvedFont
import com.kotlinjsonui.core.jsonUIFontSize
import com.kotlinjsonui.core.jsonUILetterSpacing
import com.kotlinjsonui.core.jsonUILineHeight
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * kjui's text of a Button, CheckBox, Radio, Switch and IconLabel that declare no font attribute draws its line by the
 * same rule as its size (ruling 2026-11-14): the size is the provider's (P1) and follows Configuration.Font.scaling /
 * maxScale through jsonUIFontSize, and the line box now does too (jsonUILineHeight). Without it the theme's sp line
 * kept growing with the system while the text stood still — the shape of ticket
 * kjui-theme-line-height-scales-under-font-scaling-false. Under the default configuration the line is the theme's, as
 * it was (jsonUILineHeight hands it back): the control.
 *
 * The composables below are kjui's emit for `{"type":"Button","text":…}`, `{"type":"CheckBox","label":…}` (Radio and
 * Switch emit the same Text arguments in their Row) and `{"type":"IconLabel","text":…}`, as generated (jsonui-cli
 * feat/font-provider-android); `withLine = false` is the emit before (f03e55a2c), which left lineHeight out.
 */
@RunWith(AndroidJUnit4::class)
class ProviderLineFollowsTextDeviceTest {
    @get:Rule val rule = createComposeRule()

    private val scale = mutableStateOf(1f)

    @After
    fun reset() {
        Configuration.Font.fontProvider = null
        Configuration.Font.scaling = true
        Configuration.Font.maxScale = null
    }

    private fun resolved(weight: androidx.compose.ui.text.font.FontWeight?) =
        Configuration.Font.resolve(FontSpec(family = null, weight = weight, size = null, italic = false))

    @Composable
    private fun GenButton(withLine: Boolean) {
        Button(
            onClick = { },
            shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Configuration.Button.defaultBackgroundColor,
                contentColor = Configuration.Button.defaultTextColor,
            )
        ) {
            val resolved_button1 = resolved(LocalTextStyle.current.fontWeight)
            Text(
                text = "Button",
                fontFamily = resolved_button1.family,
                fontWeight = resolved_button1.weight,
                fontSize = jsonUIFontSize(resolved_button1.size ?: TextUnit.Unspecified),
                letterSpacing = jsonUILetterSpacing(),
                lineHeight = if (withLine) jsonUILineHeight(jsonUIFontSize(resolved_button1.size ?: TextUnit.Unspecified)) else TextUnit.Unspecified,
                fontStyle = resolved_button1.style ?: FontStyle.Normal,
                modifier = Modifier.testTag("Button"),
            )
        }
    }

    @Composable
    private fun GenRowText(withLine: Boolean) {
        Row {
            val checkBoxFont = resolved(LocalTextStyle.current.fontWeight)
            Text(
                text = "CheckBox",
                fontFamily = checkBoxFont.family,
                fontWeight = checkBoxFont.weight,
                fontSize = jsonUIFontSize(checkBoxFont.size ?: TextUnit.Unspecified),
                letterSpacing = jsonUILetterSpacing(),
                lineHeight = if (withLine) jsonUILineHeight(jsonUIFontSize(checkBoxFont.size ?: TextUnit.Unspecified)) else TextUnit.Unspecified,
                fontStyle = checkBoxFont.style ?: FontStyle.Normal,
                modifier = Modifier.testTag("CheckBox"),
            )
        }
    }

    @Composable
    private fun GenIconLabel(withLine: Boolean) {
        Row {
            val resolved_iconlabel1 = resolved(LocalTextStyle.current.fontWeight)
            Text(
                text = "IconLabel",
                fontFamily = resolved_iconlabel1.family,
                fontWeight = resolved_iconlabel1.weight,
                fontSize = jsonUIFontSize(resolved_iconlabel1.size ?: TextUnit.Unspecified),
                letterSpacing = jsonUILetterSpacing(),
                fontStyle = resolved_iconlabel1.style ?: FontStyle.Normal,
                style = if (withLine) LocalTextStyle.current.copy(lineHeight = jsonUILineHeight(jsonUIFontSize(resolved_iconlabel1.size ?: TextUnit.Unspecified)))
                        else LocalTextStyle.current,
                modifier = Modifier.testTag("IconLabel"),
            )
        }
    }

    private val tags = listOf("Button", "CheckBox", "IconLabel")

    private val withLine = mutableStateOf(false)

    private fun content(line: Boolean) {
        withLine.value = line
        rule.setContent {
        val d = LocalDensity.current
        MaterialTheme {
            CompositionLocalProvider(LocalDensity provides Density(d.density, fontScale = scale.value)) {
                Column {
                    GenButton(withLine.value)
                    GenRowText(withLine.value)
                    GenIconLabel(withLine.value)
                }
            }
        }
        }
    }

    private fun heights(at: Float): Map<String, Float> {
        rule.runOnIdle { scale.value = at }
        rule.waitForIdle()
        return tags.associateWith { rule.onNodeWithTag(it, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot.height }
    }

    /** The app's provider answers a size and the app does not follow the system: the text stands still, its line too. */
    private fun fixedApp() {
        Configuration.Font.scaling = false
        Configuration.Font.fontProvider = { ResolvedFont(size = 20.sp) }
    }

    @Test
    fun under_a_provider_size_and_scaling_false_the_line_stands_still_with_the_text() {
        fixedApp()
        content(line = true)
        val at1 = heights(1f)
        val at2 = heights(2f)
        for (t in tags) assertEquals("$t: its line box at 2.0 as at 1.0 (${at2[t]} vs ${at1[t]})", at1[t]!!, at2[t]!!, 1f)
    }

    /** The emit before: the theme's sp line grew with the system under a text that stood still (the red this guards). */
    @Test
    fun before_the_line_grew_under_a_text_that_stood_still() {
        fixedApp()
        content(line = false)
        val at1 = heights(1f)
        val at2 = heights(2f)
        for (t in tags) assertTrue("$t: the old line grew (${at2[t]} vs ${at1[t]})", at2[t]!! > at1[t]!! + 4f)
    }

    /** The default configuration (no provider, the text follows the system): the line is the theme's, as before. */
    @Test
    fun under_the_default_configuration_the_drawing_is_unchanged() {
        val before = mutableMapOf<Float, Map<String, Float>>()
        content(line = false)
        for (s in listOf(1f, 2f)) before[s] = heights(s)
        val after = mutableMapOf<Float, Map<String, Float>>()
        rule.runOnIdle { withLine.value = true }
        rule.waitForIdle()
        for (s in listOf(1f, 2f)) after[s] = heights(s)
        for (s in listOf(1f, 2f)) for (t in tags) assertEquals("$t at $s", before[s]!![t]!!, after[s]!![t]!!, 0.01f)
    }

}
