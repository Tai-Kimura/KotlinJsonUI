package com.kotlinjsonui.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CellIdGeneratorTest {

    @Test
    fun `same data produces same id`() {
        val data = mapOf<String, Any>("id" to 1, "name" to "Alice", "active" to true)
        val a = CellIdGenerator.autoId(data, "id", 0)
        val b = CellIdGenerator.autoId(data, "id", 0)
        assertEquals(a, b)
        assertTrue(a.startsWith("1_"))
    }

    @Test
    fun `changing other fields changes suffix but keeps primary`() {
        val a = mapOf<String, Any>("id" to 1, "name" to "Alice")
        val b = mapOf<String, Any>("id" to 1, "name" to "Bob")
        val idA = CellIdGenerator.autoId(a, "id", 0)
        val idB = CellIdGenerator.autoId(b, "id", 0)
        assertTrue(idA.startsWith("1_"))
        assertTrue(idB.startsWith("1_"))
        assertNotEquals(idA, idB)
    }

    @Test
    fun `cellId key excluded from hash when primary differs`() {
        val a = mapOf<String, Any>("id" to 1, "name" to "Alice", "cellId" to "stale_a")
        val b = mapOf<String, Any>("id" to 1, "name" to "Alice", "cellId" to "stale_b")
        assertEquals(
            CellIdGenerator.autoId(a, "id", 0),
            CellIdGenerator.autoId(b, "id", 0)
        )
    }

    @Test
    fun `missing primary falls back to index`() {
        val data = mapOf<String, Any>("name" to "Alice")
        val id = CellIdGenerator.autoId(data, "id", 7)
        assertTrue(id.startsWith("7_"))
    }

    @Test
    fun `cellIdProperty equals cellId preserves primary`() {
        val data = mapOf<String, Any>("cellId" to "bar_42", "name" to "Alice", "available" to true)
        val id = CellIdGenerator.autoId(data, "cellId", 0)
        assertTrue(id.startsWith("bar_42_"))
    }

    @Test
    fun `enrichCellIds is idempotent`() {
        val data = listOf<Map<String, Any>>(
            mapOf("id" to 1, "name" to "Alice"),
            mapOf("id" to 2, "name" to "Bob")
        )
        val once = CellIdGenerator.enrichCellIds(data, "id")
        val twice = CellIdGenerator.enrichCellIds(once, "id")
        assertEquals(
            once.map { it["cellId"] },
            twice.map { it["cellId"] }
        )
    }

    @Test
    fun `enrichCellIds dedupes duplicates`() {
        val data = listOf<Map<String, Any>>(
            mapOf("id" to 1, "name" to "Alice"),
            mapOf("id" to 1, "name" to "Alice")
        )
        var duplicatesReported: List<String>? = null
        val result = CellIdGenerator.enrichCellIds(data, "id") {
            duplicatesReported = it
        }
        val ids = result.map { it["cellId"] as String }
        assertNotEquals(ids[0], ids[1])
        assertTrue(ids[1].contains("#2"))
        assertNotEquals(null, duplicatesReported)
    }
}
