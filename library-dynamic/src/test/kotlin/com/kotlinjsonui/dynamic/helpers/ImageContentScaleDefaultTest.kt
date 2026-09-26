package com.kotlinjsonui.dynamic.helpers

import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * An image with no contentMode draws the declared default, and Image,
 * NetworkImage and CircleImage all draw with the one table that says so.
 *
 * The default is declared once, in jsonui-cli's
 * shared/core/attribute_semantics.json (`semantics.image.defaultContentMode`,
 * the 2026-08-03 user ruling), copied byte for byte into src/test/resources
 * (CI's vendored-fixture step compares the copy with the pinned ref). The
 * value is READ here, not written: a changed ruling moves the expectation and
 * turns this red until [ImageContentScale] follows. jsonui-cli's sjui / kjui /
 * rjui codegen and SwiftJsonUI's Dynamic runtime bind to the same value (4f
 * ruling, 2026-09-26, jsonui-cli 1.9.0).
 */
class ImageContentScaleDefaultTest {

    private val declared: String by lazy {
        val stream = requireNotNull(javaClass.classLoader?.getResourceAsStream("attribute_semantics.json")) {
            "attribute_semantics.json missing from test resources"
        }
        stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it).asJsonObject }
            .getAsJsonObject("semantics").getAsJsonObject("image").get("defaultContentMode").asString
    }

    @Test
    fun noContentModeDrawsWhatTheDeclaredDefaultDraws() {
        assertEquals("scale", ImageContentScale.scale(declared), ImageContentScale.scale(null))
        assertEquals("alignment", ImageContentScale.alignment(declared), ImageContentScale.alignment(null))
    }

    /** The control: the comparison above can tell two modes apart. */
    @Test
    fun anotherModeDrawsOtherwise() {
        val other = if (declared.equals("AspectFill", ignoreCase = true)) "fit" else "AspectFill"
        assertNotEquals(ImageContentScale.scale(other), ImageContentScale.scale(null))
    }

    /** The table itself — the same as kjui's codegen (content_scale_helper.rb). */
    @Test
    fun everyDeclaredSpellingDrawsItsScale() {
        val expected = mapOf(
            "fit" to ContentScale.Fit, "AspectFit" to ContentScale.Fit,
            "AspectFill" to ContentScale.Crop,
            "fill" to ContentScale.FillBounds, "ScaleToFill" to ContentScale.FillBounds,
            "center" to ContentScale.None, "Center" to ContentScale.None,
            "top" to ContentScale.None, "bottom" to ContentScale.None,
            "left" to ContentScale.None, "right" to ContentScale.None,
        )
        for ((spelling, scale) in expected) assertEquals(spelling, scale, ImageContentScale.scale(spelling))
        val aligned = mapOf(
            "top" to Alignment.TopCenter, "bottom" to Alignment.BottomCenter,
            "left" to Alignment.CenterStart, "right" to Alignment.CenterEnd, "center" to Alignment.Center,
        )
        for ((spelling, alignment) in aligned) {
            assertEquals(spelling, alignment, ImageContentScale.alignment(spelling))
        }
    }

    /**
     * Each image component draws with [ImageContentScale] and names no
     * ContentScale of its own. CircleImage named one, Crop, on both its
     * local and its network image, whatever the contentMode.
     */
    @Test
    fun imageNetworkImageAndCircleImageDrawWithTheOneTable() {
        val dir = File("src/main/kotlin/com/kotlinjsonui/dynamic/components")
        val member = Regex("""ContentScale\.(Crop|Fit|FillBounds|FillWidth|FillHeight|Inside|None)\b""")
        for (name in listOf("DynamicImageComponent", "DynamicNetworkImageComponent", "DynamicCircleImageComponent")) {
            val file = File(dir, "$name.kt")
            assertTrue("missing: ${file.absolutePath}", file.isFile)
            val code = file.readText()
                .replace(Regex("""/\*[\s\S]*?\*/"""), "")
                .lines().joinToString("\n") { it.substringBefore("//") }
            assertTrue("$name does not draw with ImageContentScale.scale", code.contains("ImageContentScale.scale("))
            assertTrue("$name does not align with ImageContentScale.alignment", code.contains("ImageContentScale.alignment("))
            assertEquals("$name names a ContentScale of its own", emptyList<String>(),
                member.findAll(code).map { it.value }.toList())
        }
    }
}
