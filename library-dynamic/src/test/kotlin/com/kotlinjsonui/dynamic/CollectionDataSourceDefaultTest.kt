package com.kotlinjsonui.dynamic

import com.google.gson.JsonParser
import com.kotlinjsonui.data.CollectionDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A list declared `CollectionDataSource` is a data source, as the generated
 * Data class has it (jsonui-cli ticket kjui-dynamic-collection-default-list-
 * draws-no-rows-with-sections). As a plain List, a Collection with
 * `sections` drew no row on Dynamic while kjui codegen drew three (runtime
 * census, conf_ci, 2026-10-03; with the change, all 17 Collection cases draw
 * and call their handlers on both faces).
 */
class CollectionDataSourceDefaultTest {
    private fun defaults(json: String) = applyDataSectionDefaults(JsonParser.parseString(json).asJsonObject, emptyMap())

    @Test
    fun aCollectionDataSourceListIsOneSectionOfItsCells() {
        val d = defaults(
            """{"type":"View","data":[{"name":"rows","class":"CollectionDataSource",
                "defaultValue":[{"title":"Row 0"},{"title":"Row 1"}]}]}"""
        )
        val source = d["rows"]
        assertTrue("rows is $source", source is CollectionDataSource)
        source as CollectionDataSource
        assertEquals(1, source.sections.size)
        assertEquals(listOf(mapOf("title" to "Row 0"), mapOf("title" to "Row 1")), source.sections[0].cells?.data)
    }

    @Test
    fun anyOtherListStaysAList() {
        // Control: an Array-declared default keeps its data-map shape.
        val d = defaults("""{"type":"View","data":[{"name":"opts","class":"Array","defaultValue":["a","b"]}]}""")
        assertEquals(listOf("a", "b"), d["opts"])
    }

    @Test
    fun theViewModelsValueWins() {
        val given = CollectionDataSource()
        val d = applyDataSectionDefaults(
            JsonParser.parseString(
                """{"type":"View","data":[{"name":"rows","class":"CollectionDataSource","defaultValue":[{"title":"x"}]}]}"""
            ).asJsonObject,
            mapOf("rows" to given)
        )
        assertTrue(d["rows"] === given)
    }
}
