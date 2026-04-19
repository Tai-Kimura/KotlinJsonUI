package com.kotlinjsonui.dynamic

import androidx.window.core.layout.WindowWidthSizeClass
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.*
import org.junit.Test

class ResponsiveResolverTest {

    // ── widthSizeClassKey ──

    @Test
    fun widthSizeClassKey_compact() {
        assertEquals("compact", ResponsiveResolver.widthSizeClassKey(WindowWidthSizeClass.COMPACT))
    }

    @Test
    fun widthSizeClassKey_medium() {
        assertEquals("medium", ResponsiveResolver.widthSizeClassKey(WindowWidthSizeClass.MEDIUM))
    }

    @Test
    fun widthSizeClassKey_expanded() {
        assertEquals("regular", ResponsiveResolver.widthSizeClassKey(WindowWidthSizeClass.EXPANDED))
    }

    // ── resolveMatchingKeys ──

    @Test
    fun resolveMatchingKeys_compact_portrait() {
        val keys = ResponsiveResolver.resolveMatchingKeys("compact", isLandscape = false)
        assertEquals(listOf("compact"), keys)
    }

    @Test
    fun resolveMatchingKeys_medium_portrait() {
        val keys = ResponsiveResolver.resolveMatchingKeys("medium", isLandscape = false)
        assertEquals(listOf("compact", "medium"), keys)
    }

    @Test
    fun resolveMatchingKeys_regular_portrait() {
        val keys = ResponsiveResolver.resolveMatchingKeys("regular", isLandscape = false)
        assertEquals(listOf("compact", "medium", "regular"), keys)
    }

    @Test
    fun resolveMatchingKeys_compact_landscape() {
        val keys = ResponsiveResolver.resolveMatchingKeys("compact", isLandscape = true)
        assertEquals(listOf("compact", "landscape", "compact-landscape"), keys)
    }

    @Test
    fun resolveMatchingKeys_medium_landscape() {
        val keys = ResponsiveResolver.resolveMatchingKeys("medium", isLandscape = true)
        assertEquals(listOf("compact", "medium", "landscape", "medium-landscape"), keys)
    }

    @Test
    fun resolveMatchingKeys_regular_landscape() {
        val keys = ResponsiveResolver.resolveMatchingKeys("regular", isLandscape = true)
        assertEquals(listOf("compact", "medium", "regular", "landscape", "regular-landscape"), keys)
    }

    // ── mergeOverrides ──

    @Test
    fun mergeOverrides_appliesMatchingKeys() {
        val base = JsonParser.parseString("""
            {
                "type": "View",
                "orientation": "vertical",
                "spacing": 8,
                "responsive": {
                    "regular": { "orientation": "horizontal", "spacing": 24 },
                    "medium": { "spacing": 12 }
                }
            }
        """).asJsonObject

        val responsive = base.getAsJsonObject("responsive")
        val result = ResponsiveResolver.mergeOverrides(base, responsive, listOf("medium", "regular"))

        assertEquals("horizontal", result.get("orientation").asString)
        assertEquals(24, result.get("spacing").asInt)
        assertFalse(result.has("responsive"))
    }

    @Test
    fun mergeOverrides_laterKeysOverrideEarlier() {
        val base = JsonParser.parseString("""
            {
                "type": "View",
                "spacing": 8,
                "responsive": {
                    "compact": { "spacing": 4 },
                    "medium": { "spacing": 12 },
                    "regular": { "spacing": 24 }
                }
            }
        """).asJsonObject

        val responsive = base.getAsJsonObject("responsive")

        // medium priority: compact then medium → medium wins
        val result = ResponsiveResolver.mergeOverrides(base, responsive, listOf("compact", "medium"))
        assertEquals(12, result.get("spacing").asInt)
    }

    @Test
    fun mergeOverrides_doesNotOverrideChildren() {
        val base = JsonParser.parseString("""
            {
                "type": "View",
                "child": [{ "type": "Text", "text": "original" }],
                "responsive": {
                    "regular": { "child": [{ "type": "Text", "text": "replaced" }], "spacing": 24 }
                }
            }
        """).asJsonObject

        val responsive = base.getAsJsonObject("responsive")
        val result = ResponsiveResolver.mergeOverrides(base, responsive, listOf("regular"))

        // child should NOT be replaced
        val child = result.getAsJsonArray("child").get(0).asJsonObject
        assertEquals("original", child.get("text").asString)
        // spacing should be applied
        assertEquals(24, result.get("spacing").asInt)
    }

    @Test
    fun mergeOverrides_skipsNonexistentKeys() {
        val base = JsonParser.parseString("""
            {
                "type": "View",
                "spacing": 8,
                "responsive": {
                    "regular": { "spacing": 24 }
                }
            }
        """).asJsonObject

        val responsive = base.getAsJsonObject("responsive")
        val result = ResponsiveResolver.mergeOverrides(base, responsive, listOf("compact", "medium"))

        // Neither compact nor medium exist in responsive, so base value stays
        assertEquals(8, result.get("spacing").asInt)
    }

    // ── resolveNode ──

    @Test
    fun resolveNode_noResponsiveBlock_returnsOriginal() {
        val json = JsonParser.parseString("""
            { "type": "View", "spacing": 8 }
        """).asJsonObject

        val result = ResponsiveResolver.resolveNode(json, "regular", isLandscape = false)
        assertSame(json, result) // Same object returned when no responsive block
    }

    @Test
    fun resolveNode_withResponsive_mergesAndRemoves() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "orientation": "vertical",
                "spacing": 8,
                "responsive": {
                    "regular": { "orientation": "horizontal", "spacing": 24 }
                }
            }
        """).asJsonObject

        val result = ResponsiveResolver.resolveNode(json, "regular", isLandscape = false)

        assertEquals("horizontal", result.get("orientation").asString)
        assertEquals(24, result.get("spacing").asInt)
        assertFalse(result.has("responsive"))
        assertEquals("View", result.get("type").asString)
    }

    @Test
    fun resolveNode_compactDoesNotApplyRegular() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "spacing": 8,
                "responsive": {
                    "regular": { "spacing": 24 }
                }
            }
        """).asJsonObject

        val result = ResponsiveResolver.resolveNode(json, "compact", isLandscape = false)

        assertEquals(8, result.get("spacing").asInt)
        assertFalse(result.has("responsive"))
    }

    @Test
    fun resolveNode_landscapeOverridesTakeEffect() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "spacing": 8,
                "responsive": {
                    "landscape": { "spacing": 16 }
                }
            }
        """).asJsonObject

        val result = ResponsiveResolver.resolveNode(json, "compact", isLandscape = true)
        assertEquals(16, result.get("spacing").asInt)
    }

    @Test
    fun resolveNode_compoundKeyHasHighestPriority() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "spacing": 8,
                "responsive": {
                    "regular": { "spacing": 24 },
                    "landscape": { "spacing": 16 },
                    "regular-landscape": { "spacing": 32 }
                }
            }
        """).asJsonObject

        val result = ResponsiveResolver.resolveNode(json, "regular", isLandscape = true)
        assertEquals(32, result.get("spacing").asInt)
    }

    @Test
    fun resolveNode_mediumLandscapeCompound() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "spacing": 8,
                "responsive": {
                    "medium": { "spacing": 12 },
                    "landscape": { "spacing": 16 },
                    "medium-landscape": { "spacing": 20 }
                }
            }
        """).asJsonObject

        val result = ResponsiveResolver.resolveNode(json, "medium", isLandscape = true)
        assertEquals(20, result.get("spacing").asInt)
    }

    // ── resolveTree ──

    @Test
    fun resolveTree_recursesIntoChildren() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "orientation": "vertical",
                "responsive": {
                    "regular": { "orientation": "horizontal" }
                },
                "child": [
                    {
                        "type": "Text",
                        "text": "Hello",
                        "responsive": {
                            "regular": { "fontSize": 24 }
                        }
                    },
                    {
                        "type": "Text",
                        "text": "World"
                    }
                ]
            }
        """).asJsonObject

        val result = ResponsiveResolver.resolveTree(json, WindowWidthSizeClass.EXPANDED, isLandscape = false)

        // Root resolved
        assertEquals("horizontal", result.get("orientation").asString)
        assertFalse(result.has("responsive"))

        // First child resolved
        val children = result.getAsJsonArray("child")
        val firstChild = children.get(0).asJsonObject
        assertEquals(24, firstChild.get("fontSize").asInt)
        assertFalse(firstChild.has("responsive"))

        // Second child unchanged
        val secondChild = children.get(1).asJsonObject
        assertEquals("World", secondChild.get("text").asString)
    }

    @Test
    fun resolveTree_handlesSingleChildObject() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "child": {
                    "type": "Text",
                    "text": "Hello",
                    "responsive": {
                        "regular": { "fontSize": 24 }
                    }
                }
            }
        """).asJsonObject

        val result = ResponsiveResolver.resolveTree(json, WindowWidthSizeClass.EXPANDED, isLandscape = false)

        val child = result.getAsJsonObject("child")
        assertEquals(24, child.get("fontSize").asInt)
        assertFalse(child.has("responsive"))
    }

    @Test
    fun resolveTree_handlesChildrenKey() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "children": [
                    {
                        "type": "Text",
                        "responsive": {
                            "medium": { "fontSize": 16 }
                        }
                    }
                ]
            }
        """).asJsonObject

        val result = ResponsiveResolver.resolveTree(json, WindowWidthSizeClass.MEDIUM, isLandscape = false)

        val child = result.getAsJsonArray("children").get(0).asJsonObject
        assertEquals(16, child.get("fontSize").asInt)
        assertFalse(child.has("responsive"))
    }

    @Test
    fun resolveTree_deeplyNested() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "child": [
                    {
                        "type": "View",
                        "child": [
                            {
                                "type": "Text",
                                "fontSize": 12,
                                "responsive": {
                                    "regular": { "fontSize": 20 }
                                }
                            }
                        ]
                    }
                ]
            }
        """).asJsonObject

        val result = ResponsiveResolver.resolveTree(json, WindowWidthSizeClass.EXPANDED, isLandscape = false)

        val innerText = result.getAsJsonArray("child")
            .get(0).asJsonObject
            .getAsJsonArray("child")
            .get(0).asJsonObject

        assertEquals(20, innerText.get("fontSize").asInt)
        assertFalse(innerText.has("responsive"))
    }

    // ── Priority integration tests ──

    @Test
    fun priority_compoundOverridesLandscapeOverridesRegular() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "orientation": "vertical",
                "spacing": 8,
                "background": "#FFFFFF",
                "responsive": {
                    "regular": { "orientation": "horizontal", "spacing": 24, "background": "#EEEEEE" },
                    "landscape": { "spacing": 16, "background": "#DDDDDD" },
                    "regular-landscape": { "spacing": 32 }
                }
            }
        """).asJsonObject

        val result = ResponsiveResolver.resolveNode(json, "regular", isLandscape = true)

        // spacing: compound wins (32)
        assertEquals(32, result.get("spacing").asInt)
        // orientation: from regular (horizontal), not overridden by landscape or compound
        assertEquals("horizontal", result.get("orientation").asString)
        // background: landscape wins over regular (#DDDDDD)
        assertEquals("#DDDDDD", result.get("background").asString)
    }

    @Test
    fun priority_mediumIncludesCompactOverrides() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "spacing": 8,
                "responsive": {
                    "compact": { "padding": 4 },
                    "medium": { "spacing": 12 }
                }
            }
        """).asJsonObject

        // Medium device also gets compact overrides (lower priority)
        val result = ResponsiveResolver.resolveNode(json, "medium", isLandscape = false)

        assertEquals(12, result.get("spacing").asInt)
        assertEquals(4, result.get("padding").asInt) // from compact
    }

    @Test
    fun resolveNode_noMatchingKeys_removesResponsiveOnly() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "spacing": 8,
                "responsive": {
                    "regular": { "spacing": 24 }
                }
            }
        """).asJsonObject

        // compact portrait — no matching key in responsive block
        val result = ResponsiveResolver.resolveNode(json, "compact", isLandscape = false)

        assertEquals(8, result.get("spacing").asInt)
        assertFalse(result.has("responsive"))
    }

    @Test
    fun resolveTree_noResponsiveAnywhere_returnsCopy() {
        val json = JsonParser.parseString("""
            {
                "type": "View",
                "child": [{ "type": "Text", "text": "Hello" }]
            }
        """).asJsonObject

        val result = ResponsiveResolver.resolveTree(json, WindowWidthSizeClass.COMPACT, isLandscape = false)

        assertEquals("View", result.get("type").asString)
        val child = result.getAsJsonArray("child").get(0).asJsonObject
        assertEquals("Hello", child.get("text").asString)
    }

    @Test
    fun resolveNode_responsiveNotObject_returnsOriginal() {
        val json = JsonObject().apply {
            addProperty("type", "View")
            addProperty("responsive", "invalid")
        }

        val result = ResponsiveResolver.resolveNode(json, "regular", isLandscape = false)
        assertSame(json, result)
    }

    @Test
    fun mergeOverrides_addsNewProperties() {
        val base = JsonParser.parseString("""
            {
                "type": "View",
                "responsive": {
                    "regular": { "shadow": 4, "cornerRadius": 8 }
                }
            }
        """).asJsonObject

        val responsive = base.getAsJsonObject("responsive")
        val result = ResponsiveResolver.mergeOverrides(base, responsive, listOf("regular"))

        assertEquals(4, result.get("shadow").asInt)
        assertEquals(8, result.get("cornerRadius").asInt)
    }
}
