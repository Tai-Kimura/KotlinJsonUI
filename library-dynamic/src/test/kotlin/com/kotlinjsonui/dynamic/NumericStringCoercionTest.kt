package com.kotlinjsonui.dynamic

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.generated.AttrCoerce
import com.kotlinjsonui.dynamic.generated.DimensionValue
import com.kotlinjsonui.dynamic.generated.CommonAttributes
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * What the 49-E re-vendor widened, and what it left alone.
 *
 * `AttrCoerce.number` used to be `(raw as? Number)?.toDouble()` — a JSON string
 * holding digits was not a number and fell through to null. It now parses one.
 * That reaches 127 typed rows and `DimensionValue.parse`, so it is a RENDER
 * change, not a table refresh, and the freeze decision needs to know its blast
 * radius rather than assume it.
 *
 * Measured against the fixture corpus: no fixture puts a numeric string in a
 * typed number slot. The one fixture that looks like it does —
 * `weight__binding_as_string` — carries the string in its DATA map behind a
 * `@{...}`, which is the binding path and never touched `AttrCoerce.number`
 * either before or after. Both facts are pinned below so the next re-vendor
 * that moves this coercion has to say so.
 */
class NumericStringCoercionTest {

    private fun common(json: String): CommonAttributes =
        CommonAttributes.parse(TypedAttrs.toAttrMap(Gson().fromJson(json, JsonObject::class.java)))

    // ── the widening itself ──────────────────────────────────────────

    @Test
    fun aNumericStringIsANumber() {
        assertEquals(16.0, AttrCoerce.number("16")!!, 0.0)
        assertEquals(-2.5, AttrCoerce.number(" -2.5 ")!!, 0.0)
    }

    @Test
    fun aNonNumericStringIsStillNotANumber() {
        assertNull(AttrCoerce.number("wrapContent"))
        assertNull(AttrCoerce.number("@{bound}"))
        assertNull(AttrCoerce.number(""))
    }

    @Test
    fun aDimensionKeywordStillWinsOverTheNumericParse() {
        // The keywords are not digits, so the widened branch cannot shadow
        // them — but this is the pair that would break silently if it did.
        assertEquals(DimensionValue.MatchParent, DimensionValue.parse("matchParent", "w"))
        assertEquals(DimensionValue.WrapContent, DimensionValue.parse("wrapContent", "w"))
        assertEquals(
            DimensionValue.Number(100.0),
            DimensionValue.parse("100", "w")
        )
    }

    // ── the fixture that looks affected and is not ───────────────────

    @Test
    fun aBoundWeightCarriedAsAStringResolvesThroughTheDataMap() {
        // `weight__binding_as_string`: the string lives in the data map, so
        // the value never reaches AttrCoerce — it goes through the binding
        // resolver, which read strings before the re-vendor and reads them now.
        val json = Gson().fromJson(
            """{"type":"View","width":200,"height":200,"weight":"@{boundWeightStr}"}""",
            JsonObject::class.java
        )
        assertEquals(1.0f, ModifierBuilder.getWeight(json, mapOf("boundWeightStr" to "1"))!!, 0.0f)
        assertEquals(1.0f, ModifierBuilder.getWeight(json, mapOf("boundWeightStr" to 1))!!, 0.0f)
    }

    @Test
    fun aStaticDimensionUnchangedByTheWidening() {
        // The shape every fixture actually uses: a JSON number. Identical
        // before and after, which is why the re-vendor moves no pixels.
        val a = common("""{"type":"View","width":200,"height":40}""")
        assertEquals(DimensionValue.Number(200.0), TypedAttrs.static(a.width))
        assertEquals(DimensionValue.Number(40.0), TypedAttrs.static(a.height))
    }
}
