package com.kotlinjsonui.dynamic

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.generated.LabelAttributes
import com.kotlinjsonui.dynamic.generated.SliderAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Bridge-level parse coverage shared by every typed component: gson →
 * attr-map adaptation, AttrValue/AttrEnum unwrapping, alias fallback vs
 * the canonical-only (L1) path, and the unapplied-attribute detector.
 */
class TypedAttrsBridgeTest {

    private fun node(json: String) = JsonParser.parseString(json).asJsonObject

    private fun parseLabel(json: String, canonicalOnly: Boolean = false): LabelAttributes =
        LabelAttributes.parse(TypedAttrs.toAttrMap(node(json)), canonicalOnly)

    // ── gson adapter ──

    @Test
    fun `toAttrMap converts primitives, objects and arrays deeply`() {
        val map = TypedAttrs.toAttrMap(
            node("""{"a":1,"b":"x","c":true,"d":{"e":[1,"y",{"f":false}]},"g":null}""")
        )
        assertEquals(1.0, map["a"])
        assertEquals("x", map["b"])
        assertEquals(true, map["c"])
        val d = map["d"] as Map<*, *>
        val e = d["e"] as List<*>
        assertEquals(1.0, e[0])
        assertEquals("y", e[1])
        assertEquals(mapOf("f" to false), e[2])
        assertNull(map["g"])
    }

    // ── AttrValue unwrapping (Label pilot) ──

    @Test
    fun `static text parses to its value`() {
        val a = parseLabel("""{"type":"Label","text":"Hello"}""")
        assertEquals("Hello", TypedAttrs.rawString(a.text))
        assertEquals("Hello", TypedAttrs.string(a.text, emptyMap()))
    }

    @Test
    fun `binding text keeps the raw representation and resolves via data`() {
        val a = parseLabel("""{"type":"Label","text":"@{title}"}""")
        assertEquals("@{title}", TypedAttrs.rawString(a.text))
        assertEquals("Bound", TypedAttrs.string(a.text, mapOf("title" to "Bound")))
        assertNull(TypedAttrs.static(a.text))
    }

    @Test
    fun `numeric attributes resolve statically and via binding`() {
        val static = parseLabel("""{"type":"Label","lines":3}""")
        assertEquals(3, TypedAttrs.int(static.lines, emptyMap()))

        val bound = parseLabel("""{"type":"Label","fontSize":"@{size}"}""")
        assertEquals(21f, TypedAttrs.float(bound.fontSize, mapOf("size" to 21)))
    }

    @Test
    fun `enum attributes expose declared spellings and unknown passthrough`() {
        val known = parseLabel("""{"type":"Label","textAlign":"center"}""")
        assertEquals("Center", TypedAttrs.staticEnumString(known.textAlign) { it.json })

        val lineBreak = parseLabel("""{"type":"Label","lineBreakMode":"Tail"}""")
        assertEquals("Tail", TypedAttrs.enumString(lineBreak.lineBreakMode) { it.json })
    }

    // ── alias fallback vs canonical-only (L1) path ──

    @Test
    fun `alias spelling resolves on the L0 path`() {
        val a = SliderAttributes.parse(
            TypedAttrs.toAttrMap(node("""{"type":"Slider","minimumValue":5}""")),
            canonicalOnly = false
        )
        assertEquals(5.0, TypedAttrs.static(a.minimum))
    }

    @Test
    fun `alias spelling is ignored on the canonical-only path`() {
        val a = SliderAttributes.parse(
            TypedAttrs.toAttrMap(node("""{"type":"Slider","minimumValue":5}""")),
            canonicalOnly = true
        )
        assertNull(a.minimum)
    }

    @Test
    fun `canonical spelling still resolves on the canonical-only path`() {
        val a = SliderAttributes.parse(
            TypedAttrs.toAttrMap(node("""{"type":"Slider","minimum":5}""")),
            canonicalOnly = true
        )
        assertEquals(5.0, TypedAttrs.static(a.minimum))
    }

    // ── unapplied-attribute detection ──

    @Test
    fun `unapplied declared attribute fires a warning once`() {
        val warnings = mutableListOf<String>()
        UnappliedAttributes.enabledOverride = true
        UnappliedAttributes.warningSink = { warnings.add(it) }
        try {
            val json = node("""{"type":"Label","text":"x","autoShrink":true}""")
            val declared = LabelAttributes.declaredAttributes
            val applied = setOf("text") // autoShrink deliberately unapplied
            UnappliedAttributes.check("LabelTestA", json, declared, applied)
            UnappliedAttributes.check("LabelTestA", json, declared, applied)
        } finally {
            UnappliedAttributes.enabledOverride = null
            UnappliedAttributes.warningSink = null
        }
        assertEquals(1, warnings.size)
        assertTrue(warnings[0].contains("autoShrink"))
        assertTrue(warnings[0].contains("LabelTestA"))
    }

    @Test
    fun `applied, structural and undeclared keys never warn`() {
        val warnings = mutableListOf<String>()
        UnappliedAttributes.enabledOverride = true
        UnappliedAttributes.warningSink = { warnings.add(it) }
        try {
            val json = node(
                """{"type":"Label","id":"t","text":"x","child":[],"someCustomKey":1,
                    "${'$'}jui":{"normalized":"L1","schemaVersion":1}}"""
            )
            UnappliedAttributes.check(
                "LabelTestB", json,
                declared = LabelAttributes.declaredAttributes,
                applied = setOf("text")
            )
        } finally {
            UnappliedAttributes.enabledOverride = null
            UnappliedAttributes.warningSink = null
        }
        assertTrue(warnings.isEmpty())
    }

    @Test
    fun `detector is inert when disabled`() {
        val warnings = mutableListOf<String>()
        UnappliedAttributes.enabledOverride = false
        UnappliedAttributes.warningSink = { warnings.add(it) }
        try {
            UnappliedAttributes.check(
                "LabelTestC", node("""{"type":"Label","autoShrink":true}"""),
                declared = LabelAttributes.declaredAttributes,
                applied = emptySet()
            )
        } finally {
            UnappliedAttributes.enabledOverride = null
            UnappliedAttributes.warningSink = null
        }
        assertTrue(warnings.isEmpty())
        assertFalse(warnings.contains("autoShrink"))
    }
}
