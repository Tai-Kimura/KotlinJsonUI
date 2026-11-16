package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DebugDiagnostics
import com.kotlinjsonui.dynamic.UnreadAttributes
import com.kotlinjsonui.dynamic.generated.ButtonAttributes
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Button declares no `isLoading`, `loadingText`, `async`, `imagePosition`,
 * `iconSize` or `disabled`, and no codegen draws them. The Dynamic Button read
 * them as undeclared runtime extras (a loading row, a trailing icon, an icon
 * size, a disabled state), so a layout that wrote them drew differently on
 * this path alone. They are not read now, and a debuggable build names each
 * one a layout still writes.
 */
class ButtonUnreadExtrasTest {

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

    private fun code(): String =
        File("src/main/kotlin/com/kotlinjsonui/dynamic/components/DynamicButtonComponent.kt").readText()
            .replace(Regex("""/\*[\s\S]*?\*/"""), "")
            .lines().joinToString("\n") { it.substringBefore("//") }

    @Test
    fun theKeysAreNotDeclaredOnButton() {
        assertEquals(
            listOf("isLoading", "loadingText", "async", "imagePosition", "iconSize", "disabled"),
            DynamicButtonComponent.NOT_READ
        )
        for (key in DynamicButtonComponent.NOT_READ) {
            assertFalse("Button declares '$key' now — read it through ButtonAttributes", ButtonAttributes.isDeclared(key))
        }
    }

    @Test
    fun theButtonReadsNoneOfThem() {
        val button = code()
        for (key in DynamicButtonComponent.NOT_READ) {
            // Any raw read names the node and the key: undeclared(json, "k"),
            // resolveBoolean(json, "k", ...), json.get("k").
            assertFalse("Button still reads '$key'", Regex("""json\s*[,.].{0,20}"$key"""").containsMatchIn(button))
        }
        for (gone in listOf("CircularProgressIndicator", "Dispatchers", "mutableStateOf")) {
            assertFalse("Button still has `$gone`", button.contains(gone))
        }
        assertTrue("Button does not name the keys it does not read",
            button.contains("UnreadAttributes.check(\"Button\", json, NOT_READ, context)"))
    }

    @Test
    fun aLayoutWritingThemIsNamedOncePerKey() {
        val node = obj("""{"type":"Button","text":"Go","isLoading":true,"imagePosition":"trailing","disabled":true}""")
        repeat(3) { UnreadAttributes.check("Button", node, DynamicButtonComponent.NOT_READ) }
        assertEquals(
            listOf("isLoading", "imagePosition", "disabled").map { UnreadAttributes.message("Button", it) },
            seen
        )
        assertTrue(seen[0], seen[0].startsWith("Button: 'isLoading' is not a declared attribute of Button"))
    }

    @Test
    fun aLayoutWithoutThemAndAReleaseBuildNameNothing() {
        UnreadAttributes.check("Button", obj("""{"type":"Button","text":"Go","enabled":false}"""), DynamicButtonComponent.NOT_READ)
        assertEquals(emptyList<String>(), seen)
        DebugDiagnostics.enabledOverride = false
        UnreadAttributes.check("Button", obj("""{"type":"Button","isLoading":true}"""), DynamicButtonComponent.NOT_READ)
        assertEquals(emptyList<String>(), seen)
    }
}
