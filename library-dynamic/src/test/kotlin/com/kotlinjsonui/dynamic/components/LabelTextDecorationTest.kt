package com.kotlinjsonui.dynamic.components

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.LabelAttributes
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The `textDecoration` contract (SSoT attribute_semantics.json, plan 51-E §3).
 *
 * `underline` / `strikethrough` are declared `boolean|object`, and the ruling
 * is explicit about what the object face owes:
 *
 *   - the object face draws the line it describes and must **never render
 *     less than the boolean face** — a platform that cannot honour lineStyle
 *     or colour still draws the plain Single line in the text colour;
 *   - `lineStyle: "None"` is the sole object value that draws nothing, and it
 *     is exactly equivalent to `false`.
 *
 * The dynamic reader tested the row with `== true`, which sees only the
 * boolean face, so every styled object rendered no line — the violation the
 * ruling names and assigns to this lane. Compose's TextDecoration carries no
 * colour or thickness, so the plain line IS the faithful output; Double and
 * Thick stay undistinguished from Single on purpose.
 */
class LabelTextDecorationTest {

    @Test
    fun theBooleanFaceStillDecides() {
        assertTrue(DynamicTextComponent.drawsLine(true))
        assertFalse(DynamicTextComponent.drawsLine(false))
        assertFalse("an absent row draws nothing", DynamicTextComponent.drawsLine(null))
    }

    @Test
    fun theObjectFaceDrawsAtLeastWhatTheBooleanFaceDraws() {
        assertTrue(DynamicTextComponent.drawsLine(mapOf("lineStyle" to "Single")))
        assertTrue(DynamicTextComponent.drawsLine(mapOf("lineStyle" to "Double")))
        assertTrue(DynamicTextComponent.drawsLine(mapOf("lineStyle" to "Thick")))
        assertTrue(
            "an object that styles only the colour still brings a line into existence",
            DynamicTextComponent.drawsLine(mapOf("color" to "#FF0000"))
        )
        assertTrue(
            "and so does an empty object — presence is the statement",
            DynamicTextComponent.drawsLine(emptyMap<String, Any>())
        )
    }

    /**
     * The three tests above hand `drawsLine` a Kotlin Map directly, which is
     * only the right input if the real path really delivers one. It does —
     * `TypedAttrs.toAttrMap` converts a JSON object to a Map before the
     * generated `parse` ever sees it, and `AttrCoerce.lookup` returns that
     * value unchanged — but a reader that believed otherwise would still pass
     * those tests while rendering nothing on device. So this one walks the
     * production path from the fixture's own JSON text.
     */
    @Test
    fun theObjectFaceSurvivesTheRealGsonPath() {
        fun label(json: String): LabelAttributes =
            LabelAttributes.parse(
                TypedAttrs.toAttrMap(Gson().fromJson(json, JsonObject::class.java))
            )

        val styled = label("""{"type":"Label","text":"Sample","underline":{"lineStyle":"Single"}}""")
        assertTrue(DynamicTextComponent.drawsLine(styled.underline))

        val none = label("""{"type":"Label","text":"Sample","underline":{"lineStyle":"None"}}""")
        assertFalse(DynamicTextComponent.drawsLine(none.underline))

        val plain = label("""{"type":"Label","text":"Sample","strikethrough":true}""")
        assertTrue(DynamicTextComponent.drawsLine(plain.strikethrough))
    }

    @Test
    fun lineStyleNoneIsTheOneObjectValueThatDrawsNothing() {
        assertFalse(DynamicTextComponent.drawsLine(mapOf("lineStyle" to "None")))
        // The enum is declared capitalised; the reader must not be fooled by
        // case, since nothing normalises the value before it gets here.
        assertFalse(DynamicTextComponent.drawsLine(mapOf("lineStyle" to "none")))
        assertFalse(
            "None wins over any other property in the same object",
            DynamicTextComponent.drawsLine(mapOf("lineStyle" to "None", "color" to "#FF0000"))
        )
    }
}
