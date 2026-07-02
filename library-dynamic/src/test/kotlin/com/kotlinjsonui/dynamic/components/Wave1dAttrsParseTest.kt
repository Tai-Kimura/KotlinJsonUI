package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.ImageAttributes
import com.kotlinjsonui.dynamic.generated.NetworkImageAttributes
import com.kotlinjsonui.dynamic.generated.SafeAreaViewAttributes
import com.kotlinjsonui.dynamic.generated.ScrollViewAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Parse-level coverage for the wave-1d components (SafeAreaView / ScrollView / CircleImage / NetworkImage). */
class Wave1dAttrsParseTest {

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    // ── SafeAreaView ──

    @Test
    fun `safeareaview safeAreaInsetPositions parses as a string list`() {
        val a = SafeAreaViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"SafeAreaView","safeAreaInsetPositions":["top","bottom"]}"""))
        )
        assertEquals(listOf("top", "bottom"), a.safeAreaInsetPositions?.mapNotNull { it as? String })
    }

    @Test
    fun `safeareaview orientation resolves known values and passes unknown through`() {
        val known = SafeAreaViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"SafeAreaView","orientation":"horizontal"}"""))
        )
        assertEquals("horizontal", TypedAttrs.enumString(known.orientation) { it.json })

        val unknown = SafeAreaViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"SafeAreaView","orientation":"diagonal"}"""))
        )
        assertEquals("diagonal", TypedAttrs.enumString(unknown.orientation) { it.json })
    }

    @Test
    fun `safeareaview does not declare the legacy edges spelling`() {
        assertFalse(SafeAreaViewAttributes.isDeclared("edges"))
        assertFalse(SafeAreaViewAttributes.isDeclared("ignoreKeyboard"))
        assertTrue(SafeAreaViewAttributes.isDeclared("safeAreaInsetPositions"))
    }

    // ── ScrollView ──

    @Test
    fun `scrollview scrollEnabled resolves static and binding values`() {
        val static = ScrollViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"ScrollView","scrollEnabled":false}"""))
        )
        assertEquals(false, TypedAttrs.boolean(static.scrollEnabled, emptyMap()))

        val bound = ScrollViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"ScrollView","scrollEnabled":"@{canScroll}"}"""))
        )
        assertEquals(false, TypedAttrs.boolean(bound.scrollEnabled, mapOf("canScroll" to false)))
        assertNull(TypedAttrs.boolean(bound.scrollEnabled, emptyMap()))
    }

    @Test
    fun `scrollview keyboardAvoidance defaults to enabled when absent`() {
        val absent = ScrollViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"ScrollView"}"""))
        )
        assertNull(absent.keyboardAvoidance)
        assertTrue(absent.keyboardAvoidance != false)

        val disabled = ScrollViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"ScrollView","keyboardAvoidance":false}"""))
        )
        assertEquals(false, disabled.keyboardAvoidance)
    }

    @Test
    fun `scrollview orientation is declared but horizontalScroll is a legacy extra`() {
        val a = ScrollViewAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"ScrollView","orientation":"horizontal"}"""))
        )
        assertEquals("horizontal", TypedAttrs.enumString(a.orientation) { it.json })
        assertFalse(ScrollViewAttributes.isDeclared("horizontalScroll"))
    }

    // ── CircleImage (parses with ImageAttributes) ──

    @Test
    fun `circleimage src keeps static and binding raw representations`() {
        val static = ImageAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"CircleImage","src":"avatar_default"}"""))
        )
        assertEquals("avatar_default", TypedAttrs.rawString(static.src))

        val bound = ImageAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"CircleImage","src":"@{avatarUrl}"}"""))
        )
        assertEquals("@{avatarUrl}", TypedAttrs.rawString(bound.src))
    }

    @Test
    fun `circleimage errorImage is a declared plain string row`() {
        val a = ImageAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"CircleImage","errorImage":"error-icon.png"}"""))
        )
        assertEquals("error-icon.png", a.errorImage)
    }

    @Test
    fun `circleimage url and source stay undeclared on the Image module`() {
        assertFalse(ImageAttributes.isDeclared("url"))
        assertFalse(ImageAttributes.isDeclared("source"))
        assertFalse(ImageAttributes.isDeclared("size"))
        assertTrue(ImageAttributes.isDeclared("src"))
    }

    // ── NetworkImage ──

    @Test
    fun `networkimage url and src parse as independent binding-capable rows`() {
        val a = NetworkImageAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"NetworkImage","url":"https://example.com/a.png","src":"@{imageUrl}"}""")
            )
        )
        assertEquals("https://example.com/a.png", TypedAttrs.rawString(a.url))
        assertEquals("@{imageUrl}", TypedAttrs.rawString(a.src))
        assertFalse(NetworkImageAttributes.isDeclared("source"))
    }

    @Test
    fun `networkimage hint and placeholder are both declared rows`() {
        val a = NetworkImageAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"NetworkImage","placeholder":"loading_img"}"""))
        )
        assertNull(a.hint)
        assertEquals("loading_img", a.placeholder)
        assertEquals("loading_img", a.hint ?: a.placeholder)
    }

    @Test
    fun `networkimage contentMode static spelling resolves and binding stays static-null`() {
        val static = NetworkImageAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"NetworkImage","contentMode":"aspectFill"}"""))
        )
        assertEquals(
            "aspectfill",
            TypedAttrs.staticEnumString(static.contentMode) { it.json }?.lowercase()
        )

        val bound = NetworkImageAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"NetworkImage","contentMode":"@{mode}"}"""))
        )
        assertNull(TypedAttrs.staticEnumString(bound.contentMode) { it.json })
    }
}
