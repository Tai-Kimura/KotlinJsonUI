package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * SelectBox draws its own border/background/corner shape from composable
 * parameters (SelectBox.kt) and takes content padding as a parameter. The
 * outer modifier chain must therefore not re-apply border/background/padding
 * — the shared buildModifier pipeline does, which drew the border twice
 * (outer modifier frame + inner self-drawn frame, offset by the padding).
 * Reference semantics: selectbox_component.rb emits only
 * testTag/margins/size/alpha/clickable modifiers plus a contentPadding
 * parameter for these attributes.
 */
class SelectBoxModifierTest {

    private fun json(s: String) = JsonParser.parseString(s).asJsonObject

    private fun Modifier.elementNames(): List<String> =
        foldIn(mutableListOf<String>()) { acc, element ->
            acc.apply { add(element.javaClass.name) }
        }

    // Style payload equivalent to a bordered text-field style:
    // height + background + cornerRadius + border + horizontal paddings.
    // Colors go through bindings because hex parsing needs the (stubbed)
    // Android color parser.
    private val styledNode = json(
        """
        {
          "type": "SelectBox", "id": "styled_select",
          "height": 56, "background": "@{bg}", "cornerRadius": 4,
          "borderWidth": 1, "borderColor": "@{border}",
          "paddings": [0, 16, 0, 16]
        }
        """
    )

    private val colorData = mapOf(
        "bg" to Color(0xFF222222),
        "border" to Color(0xFF555555)
    )

    @Test
    fun `shared buildModifier would draw the frame on the modifier`() {
        // Guard: documents why SelectBox must NOT use the shared pipeline —
        // it applies border and background from the same attributes the
        // composable already draws.
        val names = ModifierBuilder.buildModifier(styledNode, colorData).elementNames()
        assertTrue("expected a border element in $names", names.any { it.contains("Border") })
        assertTrue("expected a background element in $names", names.any { it.contains("Background") })
    }

    @Test
    fun `selfDrawn modifier applies no border background or padding`() {
        val names = DynamicSelectBoxComponent.buildSelfDrawnModifier(styledNode, colorData)
            .elementNames()
        assertFalse("border must be drawn by the composable only: $names", names.any { it.contains("Border") })
        assertFalse("background must be drawn by the composable only: $names", names.any { it.contains("Background") })
        assertFalse("paddings must map to contentPadding, not a modifier: $names", names.any { it.contains("Padding") })
    }

    @Test
    fun `selfDrawn modifier keeps margins as outer padding`() {
        val node = json("""{"type":"SelectBox","topMargin":8}""")
        val names = DynamicSelectBoxComponent.buildSelfDrawnModifier(node, emptyMap()).elementNames()
        assertTrue("margins stay on the modifier: $names", names.any { it.contains("Padding") })
    }

    // ── contentPadding parsing (static emit: [top, start, bottom, end]) ──

    @Test
    fun `paddings array of four maps to contentPadding`() {
        assertEquals(
            PaddingValues(top = 0.dp, start = 16.dp, bottom = 0.dp, end = 16.dp),
            ModifierBuilder.parseContentPadding(styledNode)
        )
    }

    @Test
    fun `paddings array of two maps to vertical and horizontal`() {
        val node = json("""{"type":"SelectBox","paddings":[4, 12]}""")
        assertEquals(
            PaddingValues(vertical = 4.dp, horizontal = 12.dp),
            ModifierBuilder.parseContentPadding(node)
        )
    }

    @Test
    fun `single padding number maps to all sides`() {
        val node = json("""{"type":"SelectBox","padding":10}""")
        assertEquals(
            PaddingValues(10.dp),
            ModifierBuilder.parseContentPadding(node)
        )
    }

    @Test
    fun `no padding keys means null so the composable default applies`() {
        assertNull(ModifierBuilder.parseContentPadding(json("""{"type":"SelectBox"}""")))
    }
}
