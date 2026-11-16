package com.kotlinjsonui.dynamic

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.generated.ButtonAttributes
import com.kotlinjsonui.dynamic.generated.CommonAttributes
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * `paddingHorizontal` / `paddingVertical` are declared nowhere in the SSoT,
 * the normalizer does not fold them, and no other platform draws them. The
 * shared padding pipeline read them for every component (and Button for its
 * content padding), so a layout that wrote them drew padding on this path
 * alone. They are not read now; a debuggable build names them for whichever
 * component carries them, through the gate every component already calls.
 */
class PaddingAxisUnreadTest {

    private val seen = mutableListOf<String>()

    @Before
    fun arm() {
        seen.clear()
        UnreadAttributes.resetForTest()
        UnreadAttributes.warningSink = { seen += it }
        DebugDiagnostics.enabledOverride = true
    }

    @After
    fun disarm() {
        UnreadAttributes.warningSink = null
        UnreadAttributes.resetForTest()
        DebugDiagnostics.enabledOverride = null
    }

    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    private fun code(path: String): String =
        File(path).readText()
            .replace(Regex("""/\*[\s\S]*?\*/"""), "")
            .lines().joinToString("\n") { it.substringBefore("//") }

    @Test
    fun theKeysAreDeclaredNowhereAndAppliedByNothing() {
        assertEquals(listOf("paddingHorizontal", "paddingVertical"), UnreadAttributes.COMMON)
        for (key in UnreadAttributes.COMMON) {
            assertFalse("'$key' is declared now — read it through the typed attributes", CommonAttributes.isDeclared(key))
            assertFalse("'$key' is declared on Button now", ButtonAttributes.isDeclared(key))
            assertFalse("'$key' is still listed as applied", key in UnappliedAttributes.COMMON_APPLIED)
        }
    }

    @Test
    fun noPipelineReadsThem() {
        val dir = "src/main/kotlin/com/kotlinjsonui/dynamic/"
        val files = listOf(
            dir + "helpers/ModifierBuilder.kt",
            dir + "components/DynamicButtonComponent.kt"
        ) + File(dir + "components").listFiles { f -> f.extension == "kt" }!!.map { it.path }
        for (path in files.distinct()) {
            val source = code(path)
            for (key in UnreadAttributes.COMMON) {
                assertFalse("$path still reads '$key'", source.contains("\"$key\""))
            }
        }
    }

    @Test
    fun theComponentGateNamesThemOncePerComponent() {
        val node = obj("""{"type":"View","paddingHorizontal":16,"paddingVertical":8}""")
        repeat(2) {
            UnappliedAttributes.check("View", node, declared = emptySet(), applied = emptySet())
            UnappliedAttributes.check("Button", node, declared = emptySet(), applied = emptySet())
        }
        assertEquals(
            listOf(
                UnreadAttributes.message("View", "paddingHorizontal"),
                UnreadAttributes.message("View", "paddingVertical"),
                UnreadAttributes.message("Button", "paddingHorizontal"),
                UnreadAttributes.message("Button", "paddingVertical")
            ),
            seen
        )
    }

    @Test
    fun aReleaseBuildNamesNothing() {
        DebugDiagnostics.enabledOverride = false
        UnappliedAttributes.check("View", obj("""{"type":"View","paddingVertical":8}"""), emptySet(), emptySet())
        assertEquals(emptyList<String>(), seen)
    }
}
