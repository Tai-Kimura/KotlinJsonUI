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

    // --- Values whose identity, not contents, would reach the hash ---------
    // (ticket kjui-cellid-lambda-detection-by-class-name-breaks-under-r8).
    // Each case builds the same cell twice with a NEW instance of the value,
    // as a ViewModel rebuilding its cell data does, and wants one id.

    private fun idsWithFreshValue(make: () -> Any): Pair<String, String> {
        fun cell() = mapOf("cellId" to "x", "label" to "L", "onTap" to make())
        return CellIdGenerator.autoId(cell(), "cellId", 0) to CellIdGenerator.autoId(cell(), "cellId", 0)
    }

    private fun assertStable(make: () -> Any) {
        val (a, b) = idsWithFreshValue(make)
        assertEquals(a, b)
    }

    @Test
    fun `a Kotlin lambda R8 renamed keeps the id`() {
        // What R8 leaves of a synthetic lambda: a short name, no "Lambda".
        assertStable { Mf0() }
    }

    // The lambdas capture a fresh value, as a cell's callback does
    // ({ onOptionTap(option.id) }): a lambda that captures nothing is one
    // shared instance and would pass whatever the rule.
    @Test
    fun `a fun interface lambda keeps the id`() {
        assertStable { val captured = Any(); OnTap { captured.hashCode() } }
    }

    @Test
    fun `a capturing Kotlin lambda keeps the id`() {
        assertStable { val captured = Any(); { captured.hashCode(); Unit } }
    }

    @Test
    fun `a fun interface implementation R8 renamed keeps the id`() {
        assertStable { Ka() }
    }

    @Test
    fun `a Java SAM lambda keeps the id`() {
        assertStable { val captured = Any(); Runnable { captured.hashCode() } }
    }

    @Test
    fun `a callback never reaches the hash, even with a toString of its own`() {
        // A Function whose toString differs per instance: the callback rule,
        // not the toString rule, is what keeps it out.
        var n = 0
        assertStable { NamedCallback(n++) }
    }

    @Test
    fun `a value whose toString is Object's keeps the id`() {
        assertStable { NoToString(1) }
    }

    @Test
    fun `a value with its own toString is still tracked by contents`() {
        val same = idsWithFreshValue { Plain(1) }
        assertEquals(same.first, same.second)
        val a = CellIdGenerator.autoId(mapOf("cellId" to "x", "v" to Plain(1)), "cellId", 0)
        val b = CellIdGenerator.autoId(mapOf("cellId" to "x", "v" to Plain(2)), "cellId", 0)
        assertNotEquals(a, b)
    }

    @Test
    fun `a callback does not hide a change in the other fields`() {
        val a = CellIdGenerator.autoId(mapOf("cellId" to "x", "label" to "A", "onTap" to Mf0()), "cellId", 0)
        val b = CellIdGenerator.autoId(mapOf("cellId" to "x", "label" to "B", "onTap" to Mf0()), "cellId", 0)
        assertNotEquals(a, b)
    }
}

private class Mf0 : () -> Unit { override fun invoke() {} }

private fun interface OnTap { fun tap() }

private class Ka : OnTap { override fun tap() {} }

private class NoToString(val n: Int)

private data class Plain(val n: Int)

private data class NamedCallback(val n: Int) : () -> Unit { override fun invoke() {} }
