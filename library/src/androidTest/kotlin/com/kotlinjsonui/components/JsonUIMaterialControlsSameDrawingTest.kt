package com.kotlinjsonui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Each JsonUI wrapper in JsonUIMaterialControls.kt draws what the material3 call generated code and the dynamic
 * face made before it draws, pixel for pixel: once with the colours a layout declares, once with none (the wrapper's
 * Color.Unspecified / default-expression parameters must land on material3's own defaults). Both sides are composed
 * on one paused clock, so an animation (the indeterminate indicators) is at the same frame in both.
 */
@RunWith(AndroidJUnit4::class)
class JsonUIMaterialControlsSameDrawingTest {
    @get:Rule val rule = createComposeRule()

    private val glyph: ImageVector = ImageVector.Builder("g", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = SolidColor(Color.Black)) { moveTo(4f, 4f); lineTo(20f, 4f); lineTo(12f, 20f); close() }
    }.build()

    private class Case(val name: String, val wrapped: @Composable () -> Unit, val direct: @Composable () -> Unit)

    private fun shot(): androidx.compose.ui.graphics.PixelMap =
        rule.onNodeWithTag("slot", useUnmergedTree = true).captureToImage().toPixelMap()

    /**
     * One slot at the top left, drawn with the wrapper and then with the direct call, so both sides sit on the same
     * pixel grid (a fractional offset would change the anti-aliased edges, not the drawing).
     */
    private fun sameDrawing(cases: List<Case>) {
        rule.mainClock.autoAdvance = false
        var shown by mutableStateOf<(@Composable () -> Unit)?>(null)
        rule.setContent { Box(Modifier.testTag("slot")) { shown?.let { key(it) { it() } } } }
        val problems = mutableListOf<String>()
        for (c in cases) {
            rule.runOnIdle { shown = c.wrapped }
            rule.mainClock.advanceTimeBy(700)
            rule.waitForIdle()
            val a = shot()
            rule.runOnIdle { shown = c.direct }
            rule.mainClock.advanceTimeBy(700)
            rule.waitForIdle()
            val b = shot()
            if (a.width != b.width || a.height != b.height) {
                problems += "${c.name}: size ${a.width}x${a.height} vs ${b.width}x${b.height}"
                continue
            }
            var n = 0
            var first = ""
            for (y in 0 until a.height) for (x in 0 until a.width) if (a[x, y] != b[x, y]) {
                if (n == 0) first = " first at ($x,$y) ${a[x, y]} vs ${b[x, y]}"
                n++
            }
            if (n > 0) problems += "${c.name}: $n of ${a.width * a.height} pixels differ$first"
        }
        assertEquals(emptyList<String>(), problems)
    }

    @Test
    fun theControls() = sameDrawing(listOf(
        Case("switch-on-declared", { JsonUISwitch(true, {}, checkedTrackColor = Color.Red, checkedThumbColor = Color.Yellow, uncheckedThumbColor = Color.Yellow) },
            { Switch(true, {}, colors = SwitchDefaults.colors(checkedTrackColor = Color.Red, checkedThumbColor = Color.Yellow, uncheckedThumbColor = Color.Yellow)) }),
        Case("switch-off-none", { JsonUISwitch(false, {}) }, { Switch(false, {}) }),
        Case("switch-on-none", { JsonUISwitch(true, {}) }, { Switch(true, {}) }),
        Case("switch-on-track-only", { JsonUISwitch(true, {}, checkedTrackColor = Color.Red) },
            { Switch(true, {}, colors = SwitchDefaults.colors(checkedTrackColor = Color.Red)) }),
        Case("switch-disabled-none", { JsonUISwitch(true, {}, enabled = false) }, { Switch(true, {}, enabled = false) }),
        Case("switch-off-track", { JsonUISwitch(false, null, uncheckedTrackColor = Color.Cyan, enabled = false) },
            { Switch(false, null, enabled = false, colors = SwitchDefaults.colors(uncheckedTrackColor = Color.Cyan)) }),
        Case("checkbox-on-declared", { JsonUICheckbox(true, {}, checkedColor = Color.Blue, checkmarkColor = Color.Yellow) },
            { Checkbox(true, {}, colors = CheckboxDefaults.colors(checkedColor = Color.Blue, checkmarkColor = Color.Yellow)) }),
        Case("checkbox-off-none", { JsonUICheckbox(false, {}) }, { Checkbox(false, {}) }),
        Case("radio-on-declared", { JsonUIRadioButton(true, {}, selectedColor = Color.Magenta, unselectedColor = Color.Gray) },
            { RadioButton(true, {}, colors = RadioButtonDefaults.colors(selectedColor = Color.Magenta, unselectedColor = Color.Gray)) }),
        Case("radio-off-none", { JsonUIRadioButton(false, {}) }, { RadioButton(false, {}) }),
    ))

    @Test
    fun theIndicators() = sameDrawing(listOf(
        Case("linear-determinate", { JsonUILinearProgressIndicator(Modifier.width(120.dp), progress = { 0.4f }, color = Color.Red, trackColor = Color.Gray) },
            { LinearProgressIndicator(progress = { 0.4f }, modifier = Modifier.width(120.dp), color = Color.Red, trackColor = Color.Gray) }),
        Case("linear-determinate-none", { JsonUILinearProgressIndicator(Modifier.width(120.dp), progress = { 0.7f }) },
            { LinearProgressIndicator(progress = { 0.7f }, modifier = Modifier.width(120.dp)) }),
        Case("linear-indeterminate", { JsonUILinearProgressIndicator(Modifier.width(120.dp)) }, { LinearProgressIndicator(Modifier.width(120.dp)) }),
        Case("circular-none", { JsonUICircularProgressIndicator() }, { CircularProgressIndicator() }),
        Case("circular-declared", { JsonUICircularProgressIndicator(Modifier.size(30.dp), color = Color.Green, strokeWidth = 6.dp) },
            { CircularProgressIndicator(Modifier.size(30.dp), color = Color.Green, strokeWidth = 6.dp) }),
    ))

    @Test
    fun theButtonDividerAndIcons() = sameDrawing(listOf(
        Case("button-declared", {
            JsonUIButton({}, shape = RoundedCornerShape(4.dp), containerColor = Color.Blue, contentColor = Color.White,
                disabledContainerColor = Color.Blue.copy(alpha = 0.5f), disabledContentColor = Color.White.copy(alpha = 0.5f),
                border = BorderStroke(2.dp, Color.Red), contentPadding = PaddingValues(6.dp)) { Text("Go") }
        }, {
            Button({}, shape = RoundedCornerShape(4.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White,
                disabledContainerColor = Color.Blue.copy(alpha = 0.5f), disabledContentColor = Color.White.copy(alpha = 0.5f)),
                border = BorderStroke(2.dp, Color.Red), contentPadding = PaddingValues(6.dp)) { Text("Go") }
        }),
        Case("button-disabled-none", { JsonUIButton({}, enabled = false) { Text("No") } }, { Button({}, enabled = false) { Text("No") } }),
        Case("divider", { Box(Modifier.width(80.dp)) { JsonUIDivider(color = Color.LightGray, thickness = 1.dp) } },
            { Box(Modifier.width(80.dp)) { HorizontalDivider(color = Color.LightGray, thickness = 1.dp) } }),
        Case("divider-none", { Box(Modifier.width(80.dp)) { JsonUIDivider() } }, { Box(Modifier.width(80.dp)) { HorizontalDivider() } }),
        Case("icon-vector", { JsonUIIcon(glyph, null) }, { Icon(glyph, null) }),
        Case("icon-painter-tint", { JsonUIIcon(rememberVectorPainter(glyph), null, tint = Color.Red) }, { Icon(rememberVectorPainter(glyph), null, tint = Color.Red) }),
        Case("badge", { JsonUIBadgedBox("3") { JsonUIIcon(glyph, null) } }, { BadgedBox(badge = { Badge { Text("3") } }) { Icon(glyph, null) } }),
    ))

    @Test
    fun theTabBar() = sameDrawing(listOf(
        Case("tabbar", {
            JsonUITabBar(containerColor = Color.White) {
                JsonUITabBarItem(selected = true, onClick = {}, icon = { JsonUIIcon(glyph, "a") }, label = { Text("A") },
                    selectedIconColor = Color.Red, selectedTextColor = Color.Red, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
                JsonUITabBarItem(selected = false, onClick = {}, icon = { JsonUIIcon(glyph, "b") }, label = { Text("B") })
            }
        }, {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(glyph, "a") }, label = { Text("A") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Red, selectedTextColor = Color.Red, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray))
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(glyph, "b") }, label = { Text("B") })
            }
        }),
    ))
}
