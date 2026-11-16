package com.kotlinjsonui.dynamic.helpers

import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.generated.ImageAttributes
import com.kotlinjsonui.dynamic.generated.NetworkImageAttributes
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

    /** Image's own section's spellings (CircleImage validates as Image). */
    private val image = ImageAttributes.ContentMode.declaredSpellings

    @Test
    fun noContentModeDrawsWhatTheDeclaredDefaultDraws() {
        assertEquals("scale", ImageContentScale.scale(declared, image), ImageContentScale.scale(null, image))
        assertEquals("alignment", ImageContentScale.alignment(declared, image), ImageContentScale.alignment(null, image))
    }

    /** The control: the comparison above can tell two modes apart. */
    @Test
    fun anotherModeDrawsOtherwise() {
        val other = if (declared.equals("AspectFill", ignoreCase = true)) "fit" else "AspectFill"
        assertNotEquals(ImageContentScale.scale(other, image), ImageContentScale.scale(null, image))
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
        for ((spelling, scale) in expected) assertEquals(spelling, scale, ImageContentScale.scale(spelling, image))
        val aligned = mapOf(
            "top" to Alignment.TopCenter, "bottom" to Alignment.BottomCenter,
            "left" to Alignment.CenterStart, "right" to Alignment.CenterEnd, "center" to Alignment.Center,
        )
        for ((spelling, alignment) in aligned) {
            assertEquals(spelling, alignment, ImageContentScale.alignment(spelling, image))
        }
    }

    /**
     * A mode is its node's declared spelling, case and all (DeclaredSpelling,
     * jsonui-cli 1.9.0). Both sides of each boundary: the declared spelling
     * draws its mode, another case draws the default; and the node's own
     * section decides — NetworkImage does not declare `ScaleToFill`, Image does.
     */
    @Test
    fun aModeIsItsNodesDeclaredSpelling() {
        val network = NetworkImageAttributes.ContentMode.declaredSpellings
        assertEquals(ContentScale.Crop, ImageContentScale.scale("AspectFill", image))
        assertEquals(ContentScale.Fit, ImageContentScale.scale("aspectFill", image))
        assertEquals(ContentScale.Fit, ImageContentScale.scale("ASPECTFILL", image))
        assertEquals(Alignment.TopCenter, ImageContentScale.alignment("Top", image))
        assertEquals(Alignment.Center, ImageContentScale.alignment("TOP", image))
        assertEquals(ContentScale.FillBounds, ImageContentScale.scale("ScaleToFill", image))
        assertEquals(ContentScale.Fit, ImageContentScale.scale("ScaleToFill", network))
        assertEquals(ContentScale.FillBounds, ImageContentScale.scale("fill", network))
        assertEquals(Alignment.Center, ImageContentScale.alignment("Top", network))
        assertEquals(Alignment.TopCenter, ImageContentScale.alignment("top", network))
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
            val section = if (name == "DynamicNetworkImageComponent") "NetworkImageAttributes" else "ImageAttributes"
            assertTrue("$name does not draw with ImageContentScale.scale over $section's spellings",
                code.contains("ImageContentScale.scale(mode, $section.ContentMode.declaredSpellings)"))
            assertTrue("$name does not align with ImageContentScale.alignment over $section's spellings",
                code.contains("ImageContentScale.alignment(mode, $section.ContentMode.declaredSpellings)"))
            assertEquals("$name names a ContentScale of its own", emptyList<String>(),
                member.findAll(code).map { it.value }.toList())
        }
    }
}
