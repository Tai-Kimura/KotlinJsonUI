package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.core.DeclaredSpelling
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.AttrWarnings
import com.kotlinjsonui.dynamic.generated.GradientViewAttributes
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * GradientView declares `gradientDirection` as Vertical / Horizontal /
 * Oblique. The Dynamic GradientView also read the camelCase `leftToRight`,
 * `topToBottom`, `rightToLeft` and `bottomToTop` (the last two as reversed
 * gradients), which neither the kjui codegen nor the other platforms'
 * GradientView draws, so a layout that wrote them drew differently on this
 * path alone. They are not read now: they fall to the default like any
 * undeclared value, and a debuggable build names them.
 */
class GradientViewUnreadDirectionsTest {

    private val named = mutableListOf<String>()
    private val parsed = mutableListOf<String>()

    @Before
    fun arm() {
        named.clear()
        parsed.clear()
        DeclaredSpelling.warningSink = { named += it }
        AttrWarnings.handler = { parsed += it }
    }

    @After
    fun disarm() {
        DeclaredSpelling.warningSink = null
        AttrWarnings.handler = null
    }

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    private fun attrs(json: String) = GradientViewAttributes.parse(TypedAttrs.toAttrMap(obj(json)))

    private fun code(): String =
        File("src/main/kotlin/com/kotlinjsonui/dynamic/components/DynamicGradientViewComponent.kt").readText()
            .replace(Regex("""/\*[\s\S]*?\*/"""), "")
            .lines().joinToString("\n") { it.substringBefore("//") }

    @Test
    fun theSpellingsAreNotDeclaredOnGradientView() {
        assertEquals(
            listOf("leftToRight", "topToBottom", "rightToLeft", "bottomToTop"),
            DynamicGradientViewComponent.NOT_READ_DIRECTIONS
        )
        for (value in DynamicGradientViewComponent.NOT_READ_DIRECTIONS) {
            assertFalse(
                "GradientView declares '$value' now — draw it as declared",
                value in GradientViewAttributes.GradientDirection.declaredSpellings
            )
        }
    }

    @Test
    fun theComponentMatchesNoneOfThem() {
        val source = code()
        for (value in DynamicGradientViewComponent.NOT_READ_DIRECTIONS) {
            // The one occurrence is the NOT_READ_DIRECTIONS list itself.
            assertEquals("GradientView still matches '$value'", 1, Regex("\"$value\"").findAll(source).count())
        }
    }

    @Test
    fun theDeclaredSpellingsAreRead() {
        assertEquals("vertical", DynamicGradientViewComponent.gradientDirectionOf(attrs("""{"gradientDirection":"Vertical"}"""), null))
        assertEquals("horizontal", DynamicGradientViewComponent.gradientDirectionOf(attrs("""{"gradientDirection":"Horizontal"}"""), null))
        assertEquals("oblique", DynamicGradientViewComponent.gradientDirectionOf(attrs("""{"gradientDirection":"Oblique"}"""), null))
        val wrapper = obj("""{"colors":["#000000","#FFFFFF"],"gradientDirection":"Horizontal"}""")
        assertEquals("horizontal", DynamicGradientViewComponent.gradientDirectionOf(attrs("{}"), wrapper))
        assertEquals(emptyList<String>(), named)
    }

    @Test
    fun onTheNodeTheyAreReadAsNothingAndTheParseNamesThem() {
        for (value in DynamicGradientViewComponent.NOT_READ_DIRECTIONS) {
            assertNull(value, DynamicGradientViewComponent.gradientDirectionOf(attrs("""{"gradientDirection":"$value"}"""), null))
        }
        assertEquals(
            DynamicGradientViewComponent.NOT_READ_DIRECTIONS.map { "GradientView.gradientDirection: unknown enum value '$it'" },
            parsed
        )
        // The parse named them; the component does not name them a second time.
        assertEquals(emptyList<String>(), named)
    }

    @Test
    fun insideTheGradientWrapperTheyAreReadAsNothingAndNamed() {
        val attrs = attrs("{}")
        for (value in DynamicGradientViewComponent.NOT_READ_DIRECTIONS) {
            val wrapper = obj("""{"colors":["#000000","#FFFFFF"],"gradientDirection":"$value"}""")
            assertNull(value, DynamicGradientViewComponent.gradientDirectionOf(attrs, wrapper))
        }
        assertEquals(
            DynamicGradientViewComponent.NOT_READ_DIRECTIONS.map {
                DeclaredSpelling.message("GradientView.gradientDirection", it, GradientViewAttributes.GradientDirection.declaredSpellings)
            },
            named
        )
    }
}
