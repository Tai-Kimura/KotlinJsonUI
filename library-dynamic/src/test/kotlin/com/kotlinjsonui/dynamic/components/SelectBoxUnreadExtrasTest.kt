package com.kotlinjsonui.dynamic.components

import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DebugDiagnostics
import com.kotlinjsonui.dynamic.UnreadAttributes
import com.kotlinjsonui.dynamic.generated.SelectBoxAttributes
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * SelectBox declares no `disabled`; the declared way is `enabled`. The
 * Dynamic SelectBox read `disabled` as an undeclared runtime extra (both
 * the dropdown and the date box), so a layout that wrote it drew a
 * disabled box on this path while the declared vocabulary said nothing.
 * It is not read now, and a debuggable build names it when a layout
 * still writes it.
 */
class SelectBoxUnreadExtrasTest {

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
        File("src/main/kotlin/com/kotlinjsonui/dynamic/components/DynamicSelectBoxComponent.kt").readText()
            .replace(Regex("""/\*[\s\S]*?\*/"""), "")
            .lines().joinToString("\n") { it.substringBefore("//") }

    @Test
    fun theKeyIsNotDeclaredOnSelectBox() {
        assertEquals(listOf("disabled"), DynamicSelectBoxComponent.NOT_READ)
        for (key in DynamicSelectBoxComponent.NOT_READ) {
            assertFalse(
                "SelectBox declares '$key' now — read it through SelectBoxAttributes",
                SelectBoxAttributes.isDeclared(key)
            )
        }
        assertTrue(SelectBoxAttributes.isDeclared("enabled"))
    }

    @Test
    fun theSelectBoxReadsNoneOfThem() {
        val box = code()
        for (key in DynamicSelectBoxComponent.NOT_READ) {
            // Any raw read names the node and the key: undeclared(json, "k"),
            // rawKey(json, "k"), json.get("k").
            assertFalse("SelectBox still reads '$key'", Regex("""json\s*[,.].{0,20}"$key"""").containsMatchIn(box))
        }
        assertTrue(
            "SelectBox does not name the keys it does not read",
            box.contains("UnreadAttributes.check(\"SelectBox\", json, NOT_READ, context)")
        )
    }

    @Test
    fun aLayoutWritingItIsNamedOnce() {
        val node = obj("""{"type":"SelectBox","items":["a","b"],"disabled":true}""")
        repeat(3) { UnreadAttributes.check("SelectBox", node, DynamicSelectBoxComponent.NOT_READ) }
        assertEquals(listOf(UnreadAttributes.message("SelectBox", "disabled")), seen)
        assertTrue(seen[0], seen[0].startsWith("SelectBox: 'disabled' is not a declared attribute of SelectBox"))
    }

    @Test
    fun aLayoutWithoutItAndAReleaseBuildNameNothing() {
        UnreadAttributes.check(
            "SelectBox",
            obj("""{"type":"SelectBox","enabled":false}"""),
            DynamicSelectBoxComponent.NOT_READ
        )
        assertEquals(emptyList<String>(), seen)
        DebugDiagnostics.enabledOverride = false
        UnreadAttributes.check(
            "SelectBox",
            obj("""{"type":"SelectBox","disabled":true}"""),
            DynamicSelectBoxComponent.NOT_READ
        )
        assertEquals(emptyList<String>(), seen)
    }
}
