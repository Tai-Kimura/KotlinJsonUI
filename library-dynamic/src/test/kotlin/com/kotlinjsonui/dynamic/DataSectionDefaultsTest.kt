package com.kotlinjsonui.dynamic

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [applyDataSectionDefaults] (DynamicView.kt) — in
 * particular the recursive conversion of nested (object / array)
 * defaultValues into the plain Map/List data-map shape, so canonical
 * dot-path / bracket-index bindings resolve against data-section
 * defaults (conformance parity with SwiftJsonUI's mergeDataDefaults).
 */
class DataSectionDefaultsTest {

    private fun node(json: String): JsonObject =
        JsonParser.parseString(json).asJsonObject

    /** Canonical layout node: root-level data array with the given entries. */
    private fun rootDataNode(entriesJson: String): JsonObject = node(
        """{"type": "View", "data": $entriesJson}"""
    )

    /** Legacy layout node: data-only child element carrying the entries. */
    private fun childDataNode(entriesJson: String): JsonObject = node(
        """{"type": "View", "child": [{"data": $entriesJson}, {"type": "Label", "text": "x"}]}"""
    )

    // ── Nested object defaults + canonical resolver traversal ─────────

    @Test
    fun nestedObjectDefault_resolvesViaDotPath() {
        val json = rootDataNode(
            """[{"name": "profile", "class": "Map", "defaultValue": {"name": "Alice", "age": 30}}]"""
        )
        val effective = applyDataSectionDefaults(json, emptyMap())

        assertTrue("profile default should land in the data map", effective.containsKey("profile"))
        assertEquals("Alice", DataBindingContext.resolveString("@{profile.name}", effective))
    }

    @Test
    fun nestedObjectDefault_deepPathResolves() {
        val json = rootDataNode(
            """[{"name": "profile", "class": "Map",
                 "defaultValue": {"address": {"city": "Osaka", "geo": {"lat": 34.7}}}}]"""
        )
        val effective = applyDataSectionDefaults(json, emptyMap())

        assertEquals("Osaka", DataBindingContext.resolveString("@{profile.address.city}", effective))
        assertEquals(
            34.7,
            DataBindingContext.resolveNumber("@{profile.address.geo.lat}", effective)!!.toDouble(),
            1e-9
        )
    }

    @Test
    fun arrayDefault_bracketIndexResolves() {
        val json = rootDataNode(
            """[{"name": "tags", "class": "List", "defaultValue": ["red", "green", "blue"]},
                {"name": "profile", "class": "Map", "defaultValue": {"scores": [10, 20]}}]"""
        )
        val effective = applyDataSectionDefaults(json, emptyMap())

        assertEquals("green", DataBindingContext.resolveString("@{tags[1]}", effective))
        assertEquals(
            20.0,
            DataBindingContext.resolveNumber("@{profile.scores[1]}", effective)!!.toDouble(),
            1e-9
        )
        // Out-of-range index stays unresolved (no crash, no fallback value)
        assertNull(DataBindingContext.resolveString("@{tags[9]}", effective))
    }

    @Test
    fun nestedDefault_numberIdentityPreserved() {
        // gson asNumber keeps the authored "5" spelling (not "5.0") — same
        // care as embed params (DynamicEmbedComponent.resolveParams).
        val json = rootDataNode(
            """[{"name": "profile", "class": "Map", "defaultValue": {"count": 5, "ratio": 0.5}}]"""
        )
        val effective = applyDataSectionDefaults(json, emptyMap())

        @Suppress("UNCHECKED_CAST")
        val profile = effective["profile"] as Map<String, Any>
        assertEquals("5", profile["count"].toString())
        assertEquals("0.5", profile["ratio"].toString())
    }

    @Test
    fun nestedDefault_parenthesizedStringsInsideObjectsAreKept() {
        // The constructor-string skip ("CollectionDataSource()") is a
        // top-level rule only; nested strings with parens are plain data.
        val json = rootDataNode(
            """[{"name": "profile", "class": "Map", "defaultValue": {"label": "Alice (admin)"}}]"""
        )
        val effective = applyDataSectionDefaults(json, emptyMap())

        assertEquals("Alice (admin)", DataBindingContext.resolveString("@{profile.label}", effective))
    }

    // ── Runtime data precedence ───────────────────────────────────────

    @Test
    fun runtimeData_overridesNestedDefault() {
        val json = rootDataNode(
            """[{"name": "profile", "class": "Map", "defaultValue": {"name": "Alice"}}]"""
        )
        val runtime = mapOf("profile" to mapOf("name" to "Bob"))
        val effective = applyDataSectionDefaults(json, runtime)

        assertEquals("Bob", DataBindingContext.resolveString("@{profile.name}", effective))
    }

    @Test
    fun runtimeData_overridesArrayDefault() {
        val json = rootDataNode(
            """[{"name": "tags", "class": "List", "defaultValue": ["red"]}]"""
        )
        val runtime = mapOf("tags" to listOf("cyan", "magenta"))
        val effective = applyDataSectionDefaults(json, runtime)

        assertEquals("magenta", DataBindingContext.resolveString("@{tags[1]}", effective))
    }

    // ── Primitive defaults (pre-existing behavior unchanged) ──────────

    @Test
    fun primitiveDefaults_unchanged() {
        val json = rootDataNode(
            """[{"name": "title", "class": "String", "defaultValue": "Hello"},
                {"name": "count", "class": "Int", "defaultValue": 5},
                {"name": "isOn", "class": "Bool", "defaultValue": true},
                {"name": "source", "class": "CollectionDataSource",
                 "defaultValue": "CollectionDataSource()"}]"""
        )
        val effective = applyDataSectionDefaults(json, emptyMap())

        assertEquals("Hello", effective["title"])
        assertEquals("5", effective["count"].toString())
        assertEquals(true, effective["isOn"])
        // Constructor-call strings are still skipped at top level
        assertFalse(effective.containsKey("source"))
    }

    @Test
    fun primitiveRuntimeData_stillOverridesDefault() {
        val json = rootDataNode(
            """[{"name": "title", "class": "String", "defaultValue": "Hello"}]"""
        )
        val effective = applyDataSectionDefaults(json, mapOf("title" to "Runtime"))
        assertEquals("Runtime", effective["title"])
    }

    // ── Legacy child-data shape gets identical treatment ──────────────

    @Test
    fun legacyChildDataShape_nestedDefaultResolves() {
        val json = childDataNode(
            """[{"name": "profile", "class": "Map",
                 "defaultValue": {"name": "Alice", "tags": ["a", "b"]}}]"""
        )
        val effective = applyDataSectionDefaults(json, emptyMap())

        assertEquals("Alice", DataBindingContext.resolveString("@{profile.name}", effective))
        assertEquals("b", DataBindingContext.resolveString("@{profile.tags[1]}", effective))
    }

    @Test
    fun duplicateName_rootLevelSectionWins() {
        // SJUI parity: mergeDataDefaults documents "the root-level array
        // wins on duplicate names".
        val json = node(
            """{"type": "View",
                "data": [{"name": "profile", "defaultValue": {"name": "Root"}}],
                "child": [{"data": [{"name": "profile", "defaultValue": {"name": "Child"}}]}]}"""
        )
        val effective = applyDataSectionDefaults(json, emptyMap())

        assertEquals("Root", DataBindingContext.resolveString("@{profile.name}", effective))
    }

    // ── No data section: input map returned untouched ─────────────────

    @Test
    fun noDataSection_returnsInputUnchanged() {
        val json = node("""{"type": "View", "child": [{"type": "Label", "text": "x"}]}""")
        val input = mapOf("key" to "value")
        assertEquals(input, applyDataSectionDefaults(json, input))
    }
}
