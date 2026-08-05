package com.kotlinjsonui.dynamic

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.generated.LabelAttributes
import com.kotlinjsonui.dynamic.generated.SwitchAttributes
import com.kotlinjsonui.dynamic.generated.TextViewAttributes
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The second half of run 4's android parity list, after measuring which side
 * was wrong instead of assuming the dynamic path was.
 *
 * That check paid: of the six families measured, TWO turned out to be the
 * codegen dropping the attribute (`minWidth`/`minHeight`, where `.widthIn(min)`
 * is followed by `.wrapContentWidth()` and the minimum is discarded) and web
 * agreed with the dynamic render. Those went back to C. What is pinned here is
 * only what web and the codegen TOGETHER said the dynamic path had wrong.
 */
class Run4ParityRemainderTest {

    private fun json(s: String): JsonObject = Gson().fromJson(s, JsonObject::class.java)

    // ── weight overriding the child's declared size ───────────────────

    @Test
    fun aDeclaredWeightIsReadPerAxis() {
        val j = json("""{"type":"View","width":200,"height":200,"weight":1}""")
        assertEquals(1f, ModifierBuilder.getWeight(j, emptyMap(), "Row")!!, 0f)
        assertEquals(1f, ModifierBuilder.getWeight(j, emptyMap(), "Column")!!, 0f)
    }

    @Test
    fun noWeightDeclaredMeansNoWeight() {
        // The distinction the override rests on: a container's `distribution`
        // supplies a weight where the child declared none, and 49-E ruled that
        // one must NOT override an explicit child size. Only a weight this
        // returns non-null for may.
        val j = json("""{"type":"View","width":200,"height":200}""")
        assertNull(ModifierBuilder.getWeight(j, emptyMap(), "Row"))
        assertNull(ModifierBuilder.getWeight(j, emptyMap(), "Column"))
    }

    @Test
    fun aZeroOrNegativeWeightIsNotAWeight() {
        assertNull(ModifierBuilder.getWeight(json("""{"type":"View","weight":0}"""), emptyMap(), "Row"))
        assertNull(ModifierBuilder.getWeight(json("""{"type":"View","weight":-1}"""), emptyMap(), "Row"))
    }

    // ── highlighted / highlightBackground / tapBackground ─────────────

    @Test
    fun highlightedIsReadAsALiteralAndAsABinding() {
        val lit = json("""{"type":"View","background":"#DDDDDD","highlightBackground":"#FF0000","highlighted":true}""")
        assertEquals(true, ModifierBuilder.resolveHighlighted(lit, emptyMap()))

        val bound = json("""{"type":"View","background":"#DDDDDD","highlighted":"@{on}"}""")
        assertEquals(true, ModifierBuilder.resolveHighlighted(bound, mapOf("on" to true)))
        assertEquals(false, ModifierBuilder.resolveHighlighted(bound, mapOf("on" to false)))
    }

    @Test
    fun anUndeclaredHighlightedIsNullNotFalse() {
        // null and false differ here: null means the background is untouched,
        // false means the plain background wins over a declared highlight.
        assertNull(ModifierBuilder.resolveHighlighted(json("""{"type":"View"}"""), emptyMap()))
        assertEquals(
            false,
            ModifierBuilder.resolveHighlighted(json("""{"type":"View","highlighted":false}"""), emptyMap())
        )
    }

    // ── Label.linkable, bound ─────────────────────────────────────────

    @Test
    fun aBoundLinkableResolvesInsteadOfBeingDropped() {
        val a = LabelAttributes.parse(
            TypedAttrs.toAttrMap(json("""{"type":"Label","text":"See https://x.test","linkable":"@{boundLinkable}"}"""))
        )
        assertTrue(TypedAttrs.boolean(a.linkable, mapOf("boundLinkable" to true)) == true)
        assertFalse(TypedAttrs.boolean(a.linkable, mapOf("boundLinkable" to false)) == true)
        // The read that dropped it: a STATIC-only read of a bound value is null.
        assertNull(TypedAttrs.static(a.linkable))
    }

    @Test
    fun aLiteralLinkableStillWorks() {
        val a = LabelAttributes.parse(
            TypedAttrs.toAttrMap(json("""{"type":"Label","text":"x","linkable":true}"""))
        )
        assertTrue(TypedAttrs.boolean(a.linkable, emptyMap()) == true)
    }

    // ── Switch.labelPosition ──────────────────────────────────────────

    @Test
    fun switchLabelPositionIsReadAndDefaultsToLeading() {
        fun pos(s: String): String? {
            val a = SwitchAttributes.parse(TypedAttrs.toAttrMap(json(s)))
            return TypedAttrs.enumString(a.labelPosition) { it.json }?.lowercase()
        }
        assertEquals("trailing", pos("""{"type":"Switch","labelPosition":"trailing"}"""))
        assertEquals("leading", pos("""{"type":"Switch","labelPosition":"leading"}"""))
        assertNull("undeclared must fall to the component's own default", pos("""{"type":"Switch"}"""))
    }

    // ── TextView.editable ─────────────────────────────────────────────

    @Test
    fun editableAndsIntoTheEnabledArgument() {
        fun enabled(s: String): Boolean {
            val a = TextViewAttributes.parse(TypedAttrs.toAttrMap(json(s)))
            return (TypedAttrs.boolean(a.common.enabled, emptyMap()) ?: true) && (a.editable ?: true)
        }
        assertFalse(enabled("""{"type":"TextView","editable":false}"""))
        assertFalse(enabled("""{"type":"TextView","enabled":false}"""))
        assertFalse(enabled("""{"type":"TextView","enabled":true,"editable":false}"""))
        assertTrue(enabled("""{"type":"TextView","editable":true}"""))
        assertTrue("neither declared leaves the field usable", enabled("""{"type":"TextView"}"""))
    }
}
