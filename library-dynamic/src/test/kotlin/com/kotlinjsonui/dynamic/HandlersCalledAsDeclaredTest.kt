package com.kotlinjsonui.dynamic

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.helpers.LayoutPath
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * A handler is called as it is declared: `()` with nothing, `(String)` with
 * the node's viewId — its id, else its drawn type and position
 * (LayoutPath.viewId). An id-less node's handlers were handed `null` on the
 * click / gesture / lifecycle paths, so an id-less `(String)` handler was
 * never called (resolveEventHandler tried it only with an id), and a
 * lifecycle handler was only ever tried as `() -> Unit`, by its name with
 * only a `:` stripped (`@{x}` looked up `data["@{x}"]`: 0 calls).
 *
 * The composables are not run here (no device on the JVM): what runs is
 * every function the effect and the click call, on the node DynamicView hands
 * them. The composed arms are androidTest HandlersCalledAsDeclaredDeviceTest.
 */
class HandlersCalledAsDeclaredTest {

    private fun json(s: String): JsonObject = JsonParser.parseString(s).asJsonObject

    /** The node at 0_1 (after a Label at 0_0), stamped as DynamicView stamps it. */
    private fun second(node: String): JsonObject {
        val parent = json("""{"type": "View", "child": [{"type": "Label", "text": "first"}, $node]}""")
        LayoutPath.enter(parent)
        return parent.getAsJsonArray("child")[1].asJsonObject
    }

    private val spellings = listOf("appeared", "@{appeared}", "appeared:")

    /**
     * Three spellings × `()` / `(String)` on a Label, a Button and a control:
     * ApplyLifecycleEffects's name and call (lifecycleHandlerName, then
     * resolveEventHandler with the node's viewId).
     */
    @Test
    fun aLifecycleHandlerIsCalledInEverySpelling_asItIsDeclared() {
        val types = mapOf(
            """{"type": "Label", "text": "x"}""" to "label_0_1",
            """{"type": "Button", "text": "x"}""" to "button_0_1",
            """{"type": "Switch"}""" to "switch_0_1",
        )
        var compared = 0
        for ((node, viewId) in types) {
            for (spelling in spellings) {
                for (key in listOf("onAppear", "onDisappear")) {
                    val n = second(node.dropLast(1) + """, "$key": "$spelling"}""")
                    val name = ModifierBuilder.lifecycleHandlerName(n.get(key))
                    assertEquals("$spelling → the handler's name", "appeared", name)

                    var bare = 0
                    ModifierBuilder.resolveEventHandler(name, mapOf("appeared" to { bare += 1 }), LayoutPath.viewId(n))
                    assertEquals("() $key $spelling on $node", 1, bare)

                    val ids = mutableListOf<String>()
                    ModifierBuilder.resolveEventHandler(name, mapOf("appeared" to { id: String -> ids += id }), LayoutPath.viewId(n))
                    assertEquals("(String) $key $spelling on $node", listOf(viewId), ids)
                    compared += 2
                }
            }
        }
        assertEquals(36, compared)
    }

    @Test
    fun aBlankOrMissingLifecycleNameIsNoHandler() {
        assertEquals(null, ModifierBuilder.lifecycleHandlerName(null))
        assertEquals(null, ModifierBuilder.lifecycleHandlerName(JsonParser.parseString("\"@{}\"")))
        assertEquals(null, ModifierBuilder.lifecycleHandlerName(JsonParser.parseString("\":\"")))
        assertEquals(null, ModifierBuilder.lifecycleHandlerName(JsonParser.parseString("{}")))
    }

    /** An id-less node's `(String)` handler gets its position name; an explicit id wins; `()` gets nothing. */
    @Test
    fun anIdLessNodesStringHandlerIsCalledWithItsPositionName() {
        val cases = mapOf(
            """{"type": "View", "onClick": "@{h}"}""" to "view_0_1",
            """{"type": "Label", "text": "x", "onLongPress": "@{h}"}""" to "label_0_1",
            """{"type": "Toggle"}""" to "switch_0_1",
            """{"type": "EditText"}""" to "textField_0_1",
            """{"type": "Picker"}""" to "selectBox_0_1",
            """{"type": "Web", "url": "https://example.com"}""" to "web_0_1",
            """{"type": "Button", "text": "x", "id": "mine"}""" to "mine",
        )
        for ((node, viewId) in cases) {
            val n = second(node)
            val ids = mutableListOf<String>()
            ModifierBuilder.resolveEventHandler("@{h}", mapOf("h" to { id: String -> ids += id }), LayoutPath.viewId(n))
            assertEquals(node, listOf(viewId), ids)
            var bare = 0
            ModifierBuilder.resolveEventHandler("@{h}", mapOf("h" to { bare += 1 }), LayoutPath.viewId(n))
            assertEquals(node, 1, bare)
        }
    }

    // --- where the runtime calls them (source reads: the call sites are composables) ---

    private val root = File("src/main/kotlin/com/kotlinjsonui/dynamic")

    private fun code(file: File): List<Pair<Int, String>> = file.readLines().mapIndexedNotNull { i, line ->
        val t = line.trimStart()
        if (t.startsWith("//") || t.startsWith("*") || t.startsWith("/*")) null else (i + 1) to line.substringBefore("//")
    }

    /**
     * No component names a viewId by a kind word or reads it from `id`
     * alone: `a.common.id ?: "switch"` handed every id-less Switch `switch`,
     * and `json.get("id")?.asString` handed an id-less node's handlers null.
     */
    @Test
    fun noViewIdIsAKindWordOrTheIdAlone() {
        val kindWord = Regex("""\.id\s*\?:\s*"[a-z]+"""")
        val idAlone = Regex("""val viewId\s*=\s*json\.get\("id"\)""")
        // The scan tells them apart (both sides).
        assertTrue(kindWord.containsMatchIn("""val viewId = a.common.id ?: "switch""""))
        assertTrue(idAlone.containsMatchIn("""val viewId = json.get("id")?.asString"""))
        assertTrue(!kindWord.containsMatchIn("""val viewId = LayoutPath.viewId(json)"""))

        val files = root.walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()
        assertTrue("the scan reads too few files (${files.size})", files.size > 60)
        val found = files.flatMap { f ->
            code(f).filter { (_, line) -> kindWord.containsMatchIn(line) || idAlone.containsMatchIn(line) }
                .map { (n, line) -> "${f.relativeTo(root)}:$n: ${line.trim()}" }
        }
        assertEquals(emptyList<String>(), found)
    }

    /**
     * onAppear / onDisappear are applied once, by DynamicView, inside the
     * visibility wrapper (a `gone` view is not composed: not called; an
     * `invisible` one is: called) — no component applies them itself (13 of
     * the 27 dispatched types did not, and a View drawn as a ConstraintLayout
     * applied them twice).
     */
    @Test
    fun lifecycleIsAppliedOnceByDynamicViewInsideTheVisibilityWrapper() {
        val files = root.walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()
        val calls = files.flatMap { f ->
            code(f).filter { (_, line) -> line.contains("ApplyLifecycleEffects(") && !line.contains("fun ApplyLifecycleEffects") }
                .map { (n, _) -> "${f.relativeTo(root)}:$n" }
        }
        assertEquals(1, calls.size)
        assertTrue(calls.single(), calls.single().startsWith("DynamicView.kt:"))

        val view = File(root, "DynamicView.kt").readText()
        val render = view.indexOf("val renderComponent: @Composable () -> Unit = {")
        val effects = view.indexOf("ModifierBuilder.ApplyLifecycleEffects(responsiveJson, effectiveData)")
        val dispatch = view.indexOf("Configuration.customComponentHandler?.invoke(type, responsiveJson, effectiveData)")
        val wrapper = view.indexOf("VisibilityWrapper(", render)
        assertTrue("render $render < effects $effects < dispatch $dispatch < wrapper $wrapper",
            render in 0 until effects && effects < dispatch && dispatch < wrapper)
        // the wrapper draws renderComponent (through render / stopping)
        assertTrue(view.indexOf("render()", wrapper) > wrapper)
    }
}
