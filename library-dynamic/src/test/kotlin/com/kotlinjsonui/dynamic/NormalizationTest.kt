package com.kotlinjsonui.dynamic

import com.google.gson.JsonParser
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NormalizationTest {

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    @Test
    fun `detects the L1 marker`() {
        val layout = obj("""{"${'$'}jui":{"normalized":"L1","schemaVersion":1},"type":"View"}""")
        assertTrue(Normalization.isCanonicalized(layout))
    }

    @Test
    fun `detects the L2 marker (includes L1 canonicalization)`() {
        val layout = obj("""{"${'$'}jui":{"normalized":"L2","schemaVersion":1},"type":"View"}""")
        assertTrue(Normalization.isCanonicalized(layout))
    }

    @Test
    fun `false without a marker`() {
        assertFalse(Normalization.isCanonicalized(obj("""{"type":"View"}""")))
    }

    @Test
    fun `false for malformed markers`() {
        assertFalse(Normalization.isCanonicalized(obj("""{"${'$'}jui":"L1"}""")))
        assertFalse(Normalization.isCanonicalized(obj("""{"${'$'}jui":{"normalized":"L9"}}""")))
        assertFalse(Normalization.isCanonicalized(obj("""{"${'$'}jui":{"normalized":7}}""")))
        assertFalse(Normalization.isCanonicalized(obj("""{"${'$'}jui":{}}""")))
    }

    @Test
    fun `marker key constant matches the build output`() {
        // The normalizer writes a literal "$jui" key.
        assertTrue(Normalization.MARKER_KEY == "\$jui")
    }
}
