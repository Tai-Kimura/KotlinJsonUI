package com.kotlinjsonui.dynamic

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import com.kotlinjsonui.components.CustomTextField
import com.kotlinjsonui.components.SelectBox
import com.kotlinjsonui.core.Configuration
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The corner a control draws when its layout writes no cornerRadius (the requester's ruling
 * 2026-11-15, jsonui-cli ticket jui-button-default-corner-radius-differs): Button, TextField and
 * TextView square, as on iOS and the web; SelectBox 8. They were all 8 here. The static rows
 * are the calls kjui generates; the Dynamic rows are DynamicView. A declared cornerRadius is
 * drawn as declared (12, the arm's control).
 *
 * Each case is drawn on magenta; the corner is read from the top-left of what was drawn (the
 * first non-magenta pixel on its middle row and column, then along the diagonal: an arc of
 * radius r leaves r(1 - 1/√2) clear). Read to the nearest dp, judged within 1 dp.
 */
@RunWith(AndroidJUnit4::class)
class DefaultCornerRadiusTest {
    @get:Rule val rule = createComposeRule()

    private var content by mutableStateOf<(@Composable () -> Unit)?>(null)
    private var composed = false

    private fun measure(name: String, draw: @Composable () -> Unit): String {
        if (!composed) {
            rule.setContent {
                content?.let { c ->
                    key(c) {
                        Box(Modifier.testTag("frame").width(160.dp).height(72.dp).background(Color.Magenta).padding(8.dp)) { c() }
                    }
                }
            }
            composed = true
        }
        rule.runOnIdle { content = null }
        rule.waitForIdle()
        rule.runOnIdle { content = draw }
        rule.waitForIdle()
        Thread.sleep(300)
        rule.waitForIdle()
        val map = rule.onNodeWithTag("frame").captureToImage().toPixelMap()
        val density = rule.density.density
        fun magenta(x: Int, y: Int): Boolean { val c = map[x, y]; return c.red > 0.85f && c.green < 0.15f && c.blue > 0.85f }
        var minX = Int.MAX_VALUE; var maxX = -1; var minY = Int.MAX_VALUE; var maxY = -1
        for (y in 0 until map.height) for (x in 0 until map.width) if (!magenta(x, y)) {
            if (x < minX) minX = x; if (x > maxX) maxX = x; if (y < minY) minY = y; if (y > maxY) maxY = y
        }
        if (maxX < 0) return "$name :: nothing drawn"
        val midY = (minY + maxY) / 2
        val midX = (minX + maxX) / 2
        val x0 = (0 until map.width).first { !magenta(it, midY) }
        val y0 = (0 until map.height).first { !magenta(midX, it) }
        val k = (0 until minOf(map.width - x0, map.height - y0)).firstOrNull { !magenta(x0 + it, y0 + it) } ?: 0
        val radiusDp = k / (1f - 1f / kotlin.math.sqrt(2f)) / density
        return "$name :: corner %d dp".format(kotlin.math.round(radiusDp).toInt())
    }

    private fun dynamic(json: String): @Composable () -> Unit = {
        DynamicView(json = JsonParser.parseString(json).asJsonObject, data = mapOf("items" to listOf("a", "b")))
    }

    @Test
    fun noCornerRadiusIsSquare_exceptASelectBox() {
        val rows = mutableListOf<String>()
        val go: @Composable () -> Unit = { Text("Go") }
        val colors: @Composable () -> androidx.compose.material3.ButtonColors = {
            ButtonDefaults.buttonColors(containerColor = Configuration.Button.defaultBackgroundColor, contentColor = Configuration.Button.defaultTextColor)
        }
        // ── static: the calls kjui generates ──
        rows += measure("static Button") { Button(onClick = {}, shape = RoundedCornerShape(0.dp), contentPadding = PaddingValues(0.dp), colors = colors()) { go() } }
        // Code kjui generated before jsonui-cli 2.5.44 reads the Configuration value.
        rows += measure("static Button (older generated code)") {
            Button(onClick = {}, shape = RoundedCornerShape(Configuration.Button.defaultCornerRadius.dp), contentPadding = PaddingValues(0.dp), colors = colors()) { go() }
        }
        rows += measure("static Button cornerRadius 12") { Button(onClick = {}, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(0.dp), colors = colors()) { go() } }
        rows += measure("static TextField") { CustomTextField(state = rememberTextFieldState(), placeholder = { Text("x") }) }
        rows += measure("static TextField border") {
            CustomTextField(state = rememberTextFieldState(), placeholder = { Text("x") }, borderColor = Color.Black, isOutlined = true, borderWidth = 2.dp)
        }
        rows += measure("static TextView") {
            CustomTextField(state = rememberTextFieldState(), modifier = Modifier.fillMaxWidth().height(48.dp), placeholder = { Text("x") },
                isOutlined = true, maxLines = Int.MAX_VALUE, singleLine = false)
        }
        rows += measure("static SelectBox") { SelectBox(value = "", onValueChange = {}, options = listOf("a", "b")) }

        // ── Dynamic ──
        val b = """"type": "Button", "id": "b", "text": "Go""""
        rows += measure("dynamic Button", dynamic("""{$b}"""))
        rows += measure("dynamic Button background", dynamic("""{$b, "background": "#FF0000"}"""))
        rows += measure("dynamic Button border", dynamic("""{$b, "borderWidth": 2, "borderColor": "#000000"}"""))
        rows += measure("dynamic Button cornerRadius 12", dynamic("""{$b, "cornerRadius": 12}"""))
        rows += measure("dynamic TextField", dynamic("""{"type": "TextField", "id": "t", "hint": "x"}"""))
        rows += measure("dynamic TextField border", dynamic("""{"type": "TextField", "id": "t", "hint": "x", "borderWidth": 2, "borderColor": "#000000"}"""))
        rows += measure("dynamic TextView", dynamic("""{"type": "TextView", "id": "t", "hint": "x", "width": "matchParent", "height": 48}"""))
        rows += measure("dynamic SelectBox", dynamic("""{"type": "SelectBox", "id": "s", "items": "@{items}"}"""))
        val table = rows.joinToString("\n")
        Log.i("KjuiCornerTable", "\n$table")

        val expected = listOf(
            "static Button" to 0, "static Button (older generated code)" to 0, "static Button cornerRadius 12" to 12,
            "static TextField" to 0, "static TextField border" to 0, "static TextView" to 0, "static SelectBox" to 8,
            "dynamic Button" to 0, "dynamic Button background" to 0, "dynamic Button border" to 0, "dynamic Button cornerRadius 12" to 12,
            "dynamic TextField" to 0, "dynamic TextField border" to 0, "dynamic TextView" to 0, "dynamic SelectBox" to 8,
        )
        // Within 1 dp: the corner is read in whole pixels off an antialiased arc, so a radius
        // reads a dp either side by density (an 8 dp SelectBox read 7 on CI's emulator,
        // conformance-mobile 2026-11-16); 0, 8 and 12 stay 4 dp apart.
        val got = rows.associate { r -> r.substringBefore(" :: ") to Regex("""corner (\d+) dp""").find(r)?.groupValues?.get(1)?.toInt() }
        val off = expected.filter { (n, want) -> got[n]?.let { kotlin.math.abs(it - want) <= 1 } != true }
        assertEquals("corners off by more than 1 dp: $off\n$table", emptyList<Pair<String, Int>>(), off)
        assertEquals(expected.map { it.first }, rows.map { it.substringBefore(" :: ") })
    }
}
