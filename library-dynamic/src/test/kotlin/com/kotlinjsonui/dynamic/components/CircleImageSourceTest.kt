package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DebugDiagnostics
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.ImageAttributes
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * A CircleImage's src is a local image name, read as Image reads it; a URL
 * belongs to NetworkImage (the SSoT's Image.src — CircleImage is an Image
 * spelling — 4f's ruling, jsonui-cli 1.9.0). The component loaded a src
 * beginning "http" from the network (AsyncImage), and read the undeclared
 * `url` / `source` the same way.
 */
class CircleImageSourceTest {

    private val seen = mutableListOf<String>()

    @Before
    fun arm() {
        seen.clear()
        CircleImageSource.resetForTest()
        CircleImageSource.warningSink = { seen += it }
        DebugDiagnostics.enabledOverride = true
    }

    @After
    fun disarm() {
        CircleImageSource.warningSink = null
        CircleImageSource.resetForTest()
        DebugDiagnostics.enabledOverride = null
    }

    private fun attrs(json: String) = JsonParser.parseString(json).asJsonObject.let { it to ImageAttributes.parse(TypedAttrs.toAttrMap(it)) }

    /** What Image's read looks up, in order, for [json]; the resolver finds only [found]. */
    private fun lookups(json: String, found: Set<String> = emptySet()): Pair<Int, List<String>> {
        val (obj, a) = attrs(json)
        val asked = mutableListOf<String>()
        val id = DynamicImageComponent.resourceId(obj, a) { name -> asked += name; if (name in found) 7 else 0 }
        return id to asked
    }

    private fun code(name: String): String =
        File("src/main/kotlin/com/kotlinjsonui/dynamic/components/$name.kt").readText()
            .replace(Regex("""/\*[\s\S]*?\*/"""), "")
            .lines().joinToString("\n") { it.substringBefore("//") }

    @Test
    fun circleImageReadsItsSourceWithImagesFunctionsAndNoNetwork() {
        val circle = code("DynamicCircleImageComponent")
        assertTrue("CircleImage does not look its drawable up with Image's resourceId",
            circle.contains("DynamicImageComponent.resourceId(json, a)"))
        assertTrue("CircleImage does not check the name Image's read resolves",
            circle.contains("DynamicImageComponent.rawSource(json, a)"))
        for (gone in listOf("AsyncImage", "startsWith(\"http\")", "undeclared(json, \"url\")",
                "undeclared(json, \"source\")", "getIdentifier(", "processDataBinding(")) {
            assertTrue("CircleImage still has `$gone`", !circle.contains(gone))
        }
        assertTrue("Image does not use the shared read", code("DynamicImageComponent").contains("resourceId(json, a) {"))
    }

    @Test
    fun theSharedReadIsImagesChain() {
        // srcName > src > defaultImage > errorImage > loadingImage > text > "placeholder"
        assertEquals(0 to listOf("a"), lookups("""{"srcName":"a","src":"b"}"""))
        assertEquals(7 to listOf("b"), lookups("""{"src":"b","errorImage":"e"}""", setOf("b")))
        assertEquals(0 to listOf("e"), lookups("""{"errorImage":"e","loadingImage":"l"}"""))
        assertEquals(0 to listOf("placeholder"), lookups("""{}"""))
        // A bound source that resolves to nothing falls back to defaultImage; a literal one does not.
        assertEquals(7 to listOf("@{avatar}", "fallback"), lookups("""{"src":"@{avatar}","defaultImage":"fallback"}""", setOf("fallback")))
        assertEquals(0 to listOf("missing"), lookups("""{"src":"missing","defaultImage":"fallback"}""", setOf("fallback")))
        // The undeclared url / source are not a source.
        assertEquals(0 to listOf("placeholder"), lookups("""{"url":"https://example.com/a.png"}"""))
        assertEquals(0 to listOf("placeholder"), lookups("""{"source":"https://example.com/a.png"}"""))
    }

    @Test
    fun aUrlTheSourceResolvesToIsNamedOnceInADebuggableBuild() {
        val (obj, a) = attrs("""{"type":"CircleImage","src":"@{avatar}"}""")
        val resolved = ResourceResolver.drawableName(DynamicImageComponent.rawSource(obj, a), mapOf("avatar" to "https://example.com/a.png"))
        assertEquals("https://example.com/a.png", resolved)
        repeat(3) { CircleImageSource.nameUrl(resolved, null) }
        assertEquals(1, seen.size)
        assertTrue(seen[0], seen[0].startsWith("CircleImage src is a local image name; a URL belongs to NetworkImage"))
        assertTrue(seen[0], seen[0].contains("https://example.com/a.png"))
    }

    @Test
    fun whatIsAUrl() {
        val urls = listOf("https://example.com/a.png", "http://x/y", "HTTPS://X/Y", "file:///tmp/a.png", "content://media/1", " https://x/y ")
        val names = listOf("http_badge", "httpIcon", "ic_avatar", "avatar.png", "", "@{avatar}")
        assertEquals(urls.map { true }, urls.map { CircleImageSource.looksLikeUrl(it) })
        assertEquals(names.map { false }, names.map { CircleImageSource.looksLikeUrl(it) })
        for (n in names) CircleImageSource.nameUrl(n, null)
        CircleImageSource.nameUrl(null, null)
        assertEquals("a name is not named", 0, seen.size)
    }

    @Test
    fun aReleaseBuildStaysQuiet() {
        DebugDiagnostics.enabledOverride = false
        CircleImageSource.nameUrl("https://example.com/a.png", null)
        assertEquals(0, seen.size)
    }
}
