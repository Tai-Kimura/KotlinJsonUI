package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DebugDiagnostics
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.CollectionAttributes
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * A bound Collection.scrollAnchor (jsonui-cli 1.9.29,
 * jui-collection-scrollto-per-request-anchor): the Collection resolves it from
 * the composition's data through [CollectionScrollAnchor] — top / center /
 * bottom, anything else bottom, named once per (Collection, value) in a
 * debuggable app. A bound scrollAnimated animates only when the bound value is
 * true ([DynamicCollectionComponent.scrollAnimatedOf]).
 */
class CollectionScrollAnchorTest {
    private val heard = mutableListOf<String>()

    @Before
    fun setUp() {
        CollectionScrollAnchor.resetForTest()
        CollectionScrollAnchor.warningSink = { heard += it }
    }

    @After
    fun tearDown() {
        DebugDiagnostics.enabledOverride = null
        CollectionScrollAnchor.warningSink = null
        CollectionScrollAnchor.resetForTest()
    }

    @Test
    fun theThreeAnchorsPassThrough() {
        assertEquals("top", CollectionScrollAnchor.resolve("top", "list", null))
        assertEquals("center", CollectionScrollAnchor.resolve("center", "list", null))
        assertEquals("bottom", CollectionScrollAnchor.resolve("bottom", "list", null))
        assertEquals(emptyList<String>(), heard)
    }

    @Test
    fun aValueThatIsNoAnchorLandsAsBottom() {
        // A null context is not a debuggable app: nothing is named.
        assertEquals("bottom", CollectionScrollAnchor.resolve("sideways", "list", null))
        assertEquals("bottom", CollectionScrollAnchor.resolve(null, "list", null))
        assertEquals(emptyList<String>(), heard)
    }

    @Test
    fun aDebuggableAppHearsItOncePerCollectionAndValue() {
        DebugDiagnostics.enabledOverride = true
        assertEquals("bottom", CollectionScrollAnchor.resolve("sideways", "message_list", null))
        assertEquals("bottom", CollectionScrollAnchor.resolve("sideways", "message_list", null))
        assertEquals("bottom", CollectionScrollAnchor.resolve("sideways", "other", null))
        assertEquals("top", CollectionScrollAnchor.resolve("top", "message_list", null))
        assertEquals(
            listOf(
                "Collection message_list: scrollAnchor \"sideways\" is not top, center or bottom — lands as bottom",
                "Collection other: scrollAnchor \"sideways\" is not top, center or bottom — lands as bottom"
            ),
            heard
        )
    }

    private fun attrs(json: String): CollectionAttributes =
        CollectionAttributes.parse(TypedAttrs.toAttrMap(JsonParser.parseString(json).asJsonObject), true)

    @Test
    fun aBoundScrollAnimatedAnimatesOnlyWhenTrue() {
        val bound = attrs("""{"type":"Collection","scrollAnimated":"@{animated}"}""")
        assertTrue(DynamicCollectionComponent.scrollAnimatedOf(bound, mapOf("animated" to true)))
        assertFalse(DynamicCollectionComponent.scrollAnimatedOf(bound, mapOf("animated" to false)))
        assertFalse(DynamicCollectionComponent.scrollAnimatedOf(bound, emptyMap()))
        assertTrue(DynamicCollectionComponent.scrollAnimatedOf(attrs("""{"type":"Collection"}"""), emptyMap()))
        assertFalse(DynamicCollectionComponent.scrollAnimatedOf(attrs("""{"type":"Collection","scrollAnimated":false}"""), emptyMap()))
    }

    @Test
    fun aBoundScrollAnchorParsesAsABinding() {
        val bound = attrs("""{"type":"Collection","scrollAnchor":"@{anchor}"}""")
        assertEquals("anchor", TypedAttrs.binding(bound.scrollAnchor))
        assertEquals("top", TypedAttrs.enumStringResolved(bound.scrollAnchor, mapOf("anchor" to "top")) { it.json })
        assertEquals("top", TypedAttrs.staticEnumString(attrs("""{"type":"Collection","scrollAnchor":"top"}""").scrollAnchor) { it.json })
    }
}
