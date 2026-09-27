package com.kotlinjsonui.dynamic.components

import androidx.compose.ui.graphics.Color
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.LabelAttributes
import org.junit.Assert.assertEquals
import org.junit.Test

/** A linkable Label's links are its tintColor (4f ruling, 2026-09-26: the accent of what can be operated). */
class LabelLinkColorTest {

    private val tint = Color(0xFF123456)

    @Test
    fun aLinkableLabelsLinksAreItsTint() {
        fun attrs(s: String) = LabelAttributes.parse(TypedAttrs.toAttrMap(JsonParser.parseString(s).asJsonObject))
        assertEquals(tint, DynamicTextComponent.linkColor(
            attrs("""{"type":"Label","text":"x","linkable":true,"tintColor":"@{t}"}"""), mapOf("t" to tint), null))
        // no tint: Unspecified — the configured link colour, as before
        assertEquals(Color.Unspecified, DynamicTextComponent.linkColor(
            attrs("""{"type":"Label","text":"x","linkable":true}"""), emptyMap(), null))
    }
}
