package com.kotlinjsonui.dynamic

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.generated.SafeAreaViewAttributes
import org.junit.Assert.assertEquals
import org.junit.Test

class SpacingProbeTest {
    @Test
    fun safeAreaViewSpacingReads() {
        val json = JsonParser.parseString(
            """{"type":"SafeAreaView","orientation":"horizontal","spacing":8}"""
        ).asJsonObject
        val a = SafeAreaViewAttributes.parse(TypedAttrs.toAttrMap(json), true)
        assertEquals(8f, TypedAttrs.float(a.spacing, emptyMap()))
    }
}
