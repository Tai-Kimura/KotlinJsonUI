package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.EmbedAttributes
import com.kotlinjsonui.dynamic.generated.WebAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Parse-level coverage for the Web / WebView / Embed conversion (wave 2c).
 * WebView is an alias type that parses with the Web module; Include is
 * structural (no typed parse) and is intentionally not covered here.
 */
class Wave2cAttrsParseTest {

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    // ── Web (also backs the WebView alias type) ──

    @Test
    fun `web url keeps static value and binding raw representation`() {
        val static = WebAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Web","url":"https://example.com"}"""))
        )
        assertEquals("https://example.com", TypedAttrs.rawString(static.url))

        val bound = WebAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"WebView","url":"@{pageUrl}"}"""))
        )
        assertEquals("@{pageUrl}", TypedAttrs.rawString(bound.url))
        assertEquals("pageUrl", TypedAttrs.binding(bound.url))
    }

    @Test
    fun `web html and iframe rows parse as plain strings`() {
        val a = WebAttributes.parse(
            TypedAttrs.toAttrMap(
                obj(
                    """{"type":"Web","html":"<p>hi</p>","sandbox":"allow-scripts","allow":"fullscreen"}"""
                )
            )
        )
        assertEquals("<p>hi</p>", a.html)
        assertEquals("allow-scripts", a.sandbox)
        assertEquals("fullscreen", a.allow)
    }

    @Test
    fun `web runtime extras stay undeclared while url is declared`() {
        // javaScriptEnabled / userAgent / allowZoom are legacy runtime
        // extras read via TypedAttrs.undeclared-style raw access.
        assertFalse("javaScriptEnabled" in WebAttributes.declaredAttributes)
        assertFalse("userAgent" in WebAttributes.declaredAttributes)
        assertFalse("allowZoom" in WebAttributes.declaredAttributes)
        assertTrue("url" in WebAttributes.declaredAttributes)
    }

    // ── Embed ──

    @Test
    fun `embed screen parses and optional rows default to null`() {
        val a = EmbedAttributes.parse(
            TypedAttrs.toAttrMap(obj("""{"type":"Embed","screen":"user_card"}"""))
        )
        assertEquals("user_card", a.screen)
        assertNull(a.params)
        assertNull(a.events)
        assertNull(a.navigationMode)
    }

    @Test
    fun `embed navigationMode known delegate and unknown isolated pass through`() {
        val delegate = EmbedAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"Embed","screen":"s","navigationMode":"delegate"}""")
            )
        )
        assertEquals("delegate", TypedAttrs.enumString(delegate.navigationMode) { it.json })

        // "isolated" is undeclared in v1 — the open enum passes it through
        // so the component's raw-string comparison keeps working.
        val isolated = EmbedAttributes.parse(
            TypedAttrs.toAttrMap(
                obj("""{"type":"Embed","screen":"s","navigationMode":"isolated"}""")
            )
        )
        assertEquals("isolated", TypedAttrs.enumString(isolated.navigationMode) { it.json })
    }

    @Test
    fun `embed params and events objects parse as maps`() {
        val a = EmbedAttributes.parse(
            TypedAttrs.toAttrMap(
                obj(
                    """{"type":"Embed","screen":"s","params":{"userId":"@{id}","label":"hi"},"events":{"onSaved":"handleSaved"}}"""
                )
            )
        )
        val params = TypedAttrs.static(a.params)
        assertEquals("@{id}", params?.get("userId"))
        assertEquals("hi", params?.get("label"))
        assertEquals("handleSaved", a.events?.get("onSaved"))
    }
}
