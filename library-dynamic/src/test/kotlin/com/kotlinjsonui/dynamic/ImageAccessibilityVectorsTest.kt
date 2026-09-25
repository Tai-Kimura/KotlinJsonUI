package com.kotlinjsonui.dynamic

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.generated.ImageAttributes
import com.kotlinjsonui.dynamic.generated.NetworkImageAttributes
import com.kotlinjsonui.dynamic.helpers.ImageAccessibility
import com.kotlinjsonui.dynamic.helpers.ImageAccessibility.Role
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The role each image gets from its alt and the tappable around it, run
 * against jsonui-cli's shared table (`shared/core/image_accessibility_vectors
 * .json`, copied byte-identical into src/test/resources; CI's
 * vendored-attr-guard compares the copy with the file at the pinned ref).
 * jsonui-cli's sjui / kjui codegen and SwiftJsonUI's Dynamic runtime run the
 * same table.
 *
 * The walk hands each node the nearest tappable above it, the way DynamicView
 * provides [com.kotlinjsonui.dynamic.helpers.LocalImageTappable] around every
 * node with a tap handler.
 */
class ImageAccessibilityVectorsTest {

    private val vectors: JsonObject by lazy {
        val stream = requireNotNull(
            javaClass.classLoader?.getResourceAsStream("image_accessibility_vectors.json")
        ) { "image_accessibility_vectors.json missing from test resources" }
        stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it).asJsonObject }
    }

    private fun roles(node: JsonObject, nearest: JsonObject?, out: MutableMap<String, String>) {
        if (ImageAccessibility.isImage(node)) {
            out[node.get("id").asString] = ImageAccessibility.role(node, nearest).name.lowercase()
        }
        val inner = if (ImageAccessibility.isTappable(node)) node else nearest
        ImageAccessibility.children(node).forEach { roles(it, inner, out) }
    }

    @Test
    fun everyCaseGetsTheRolesTheTableGives() {
        val cases = vectors.getAsJsonArray("cases").map { it.asJsonObject }
        assertTrue("the table has no cases", cases.isNotEmpty())
        val seen = mutableSetOf<String>()
        for (case in cases) {
            val name = case.get("name").asString
            val got = mutableMapOf<String, String>()
            roles(case.getAsJsonObject("layout"), null, got)
            val expected = case.getAsJsonObject("roles").entrySet().associate { it.key to it.value.asString }
            assertEquals(name, expected, got)
            seen += expected.values
        }
        assertEquals(setOf("label", "decorative", "control"), seen)
    }

    @Test
    fun eachRoleSaysWhatTalkBackReads() {
        assertEquals("Logo", ImageAccessibility.contentDescription(Role.LABEL, { "Logo" }, legacy = "logo_id"))
        // A bound alt that resolves to "" is decorative.
        assertNull(ImageAccessibility.contentDescription(Role.LABEL, { "" }, legacy = "logo_id"))
        assertNull(ImageAccessibility.contentDescription(Role.DECORATIVE, { "unused" }, legacy = "logo_id"))
        assertEquals("logo_id", ImageAccessibility.contentDescription(Role.CONTROL, { "unused" }, legacy = "logo_id"))
    }

    @Test
    fun theVendoredTablesReadAltUnderItsAliases() {
        // Re-vendored with jsonui-cli's alt declaration: the aliases fold onto
        // the one declared attribute (what the Unapplied audit checks against).
        // alt is binding-capable, so it arrives as an AttrValue.
        assertEquals("described", TypedAttrs.rawString(ImageAttributes.parse(mapOf("contentDescription" to "described")).alt))
        assertEquals("labelled", TypedAttrs.rawString(NetworkImageAttributes.parse(mapOf("accessibilityLabel" to "labelled")).alt))
    }
}
