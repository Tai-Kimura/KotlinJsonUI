package com.kotlinjsonui.dynamic

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.components.DynamicRadioComponent
import com.kotlinjsonui.dynamic.generated.RadioAttributes
import com.kotlinjsonui.dynamic.helpers.LayoutPath
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * A node's position in its layout (LayoutPath), run against jsonui-cli's
 * shared table (`shared/core/layout_path_vectors.json`, copied byte-identical
 * into src/test/resources; CI's vendored-fixture step compares the copy with
 * the file at the pinned ref). The kjui and sjui codegen run the same table
 * against JsonUIShared::LayoutPath, so a pass here is Dynamic naming a node
 * as the generated code names it.
 *
 * The walk below is this test's, not the runtime's: DynamicView draws a
 * node's children through each container, and the containers are
 * composables. What it takes from the runtime is every step it can:
 * [LayoutPath.enter] (what DynamicView calls on each node), the include
 * expansion (IncludeExpander.processIncludes, served by its layoutReader
 * seam) in both of the orders the runtime uses it — up front (the loaders)
 * and when the include node is reached (DynamicView) — and the copies made
 * after the stamp (responsive resolution, type synonyms).
 */
class LayoutPathVectorsTest {

    @After
    fun resetSeam() {
        IncludeExpander.layoutReader = null
    }

    private val vectors: JsonObject by lazy {
        val stream = requireNotNull(
            javaClass.classLoader?.getResourceAsStream("layout_path_vectors.json")
        ) { "layout_path_vectors.json missing from test resources" }
        stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it).asJsonObject }
    }

    private fun json(s: String): JsonObject = JsonParser.parseString(s).asJsonObject

    /** DynamicView's order: an include is expanded and drawn in its place; otherwise the node is entered and its children drawn. */
    private fun draw(node: JsonObject, out: MutableMap<String, String>) {
        if (node.has("include")) {
            draw(IncludeExpander.processIncludes(node), out)
            return
        }
        LayoutPath.enter(node)
        node.get("id")?.takeIf { it.isJsonPrimitive }?.let { out[it.asString] = LayoutPath.of(node) }
        LayoutPath.children(node).filter { it.isJsonObject }.forEach { draw(it.asJsonObject, out) }
    }

    private fun serve(case: JsonObject) {
        val includes = case.getAsJsonObject("includes")
        IncludeExpander.layoutReader = { name -> includes?.getAsJsonObject(name)?.deepCopy() }
    }

    @Test
    fun everyCaseGetsThePathsTheTableGives_includesExpandedUpFront() {
        val cases = vectors.getAsJsonArray("cases").map { it.asJsonObject }
        assertTrue("the table has fewer cases than it did (8)", cases.size >= 8)
        for (case in cases) {
            serve(case)
            // DynamicLayoutLoader / DynamicViewRendererImpl: expand, then draw.
            val layout = IncludeExpander.processIncludes(case.getAsJsonObject("layout").deepCopy())
            val got = mutableMapOf<String, String>()
            draw(layout, got)
            val expected = case.getAsJsonObject("expect").entrySet().associate { it.key to it.value.asString }
            assertEquals(case.get("name").asString, expected, got.filterKeys { it in expected })
        }
    }

    @Test
    fun everyCaseGetsThePathsTheTableGives_includesExpandedWhenReached() {
        val cases = vectors.getAsJsonArray("cases").map { it.asJsonObject }
        var withInclude = 0
        for (case in cases) {
            serve(case)
            if (case.has("includes")) withInclude++
            // DynamicView(layoutName): the file as read; an include node is
            // expanded when DynamicView reaches it, and the expansion keeps
            // the include node's stamp (IncludeExpander merges its keys).
            val got = mutableMapOf<String, String>()
            draw(case.getAsJsonObject("layout").deepCopy(), got)
            val expected = case.getAsJsonObject("expect").entrySet().associate { it.key to it.value.asString }
            assertEquals(case.get("name").asString, expected, got.filterKeys { it in expected })
        }
        assertTrue("no case reaches an include — the lazy expansion is not exercised", withInclude > 0)
    }

    @Test
    fun theStampSurvivesTheCopiesMadeAfterIt() {
        val parent = json("""{"type": "View", "child": [{"type": "Label"}, {"type": "HStack", "responsive": {"regular": {"padding": 4}}}]}""")
        LayoutPath.enter(parent)
        val child = parent.getAsJsonArray("child")[1].asJsonObject
        assertEquals("0_1", LayoutPath.of(child))
        // ResponsiveResolver deep-copies the node it resolves.
        assertEquals("0_1", LayoutPath.of(ResponsiveResolver.mergeOverrides(child, child.getAsJsonObject("responsive"), listOf("regular"))))
        // TypeSynonyms draws HStack as a View from a copy of the node.
        val drawn = TypeSynonyms.canonicalize(child)
        assertTrue("HStack was not copied (the arm needs a synonym)", drawn !== child)
        assertEquals("0_1", LayoutPath.of(drawn))
    }

    // --- the Radio item's id ------------------------------------------------

    private fun radioIds(layout: JsonObject): List<String> {
        val ids = mutableListOf<String>()
        fun walk(node: JsonObject) {
            LayoutPath.enter(node)
            if (node.get("type")?.asString == "Radio") {
                ids += DynamicRadioComponent.itemId(RadioAttributes.parse(TypedAttrs.toAttrMap(node)), node)
            }
            LayoutPath.children(node).filter { it.isJsonObject }.forEach { walk(it.asJsonObject) }
        }
        walk(layout)
        return ids
    }

    private val group = """
        {"type": "View", "orientation": "vertical", "child": [
          {"type": "Radio", "text": "first"},
          {"type": "View", "child": [{"type": "Radio", "text": "nested"}]},
          {"type": "Radio", "text": "first"},
          {"type": "Radio", "id": "named", "text": "named"}
        ]}
    """.trimIndent()

    @Test
    fun anIdLessRadioItemIsNamedByItsPosition_theCodegensName_andAnExplicitIdWins() {
        // kjui_tools spec/compose/radio_default_id_is_its_position_spec.rb
        // expects the same four names from the same layout.
        assertEquals(listOf("radio_0_0", "radio_0_1_0", "radio_0_2", "named"), radioIds(json(group)))
    }

    @Test
    fun theNameIsTheSameOnEveryComposition_soTheSelectedItemReadsSelected() {
        val tree = json(group)
        val first = radioIds(tree)
        // A second composition of the same (already stamped) tree.
        assertEquals(first, radioIds(tree))
        val nested = tree.getAsJsonArray("child")[1].asJsonObject.getAsJsonArray("child")[0].asJsonObject
        val a = RadioAttributes.parse(TypedAttrs.toAttrMap(nested))
        val written = mapOf<String, Any>("selectedRadiogroup" to DynamicRadioComponent.itemId(a, nested))
        assertTrue(DynamicRadioComponent.itemIsSelected(a, DynamicRadioComponent.itemId(a, nested), written))
    }

    // --- the viewId a node's handlers are handed ----------------------------

    /**
     * `view_id_cases`: the node's id, else its drawn type lower-first and its
     * position — the name the kjui codegen hands the same node's handlers
     * (kjui spec/compose/view_id_by_position_spec.rb runs this table against
     * JsonUIShared::LayoutPath.view_id).
     */
    @Test
    fun everyViewIdCaseGetsTheNameTheTableGives() {
        val cases = vectors.getAsJsonArray("view_id_cases").map { it.asJsonObject }
        assertTrue("the table has fewer view_id cases than it did (5)", cases.size >= 5)
        var compared = 0
        for (case in cases) {
            val got = mutableMapOf<String, String>()
            fun walk(node: JsonObject) {
                LayoutPath.enter(node)
                node.get("_label")?.let { got[it.asString] = LayoutPath.viewId(node) }
                LayoutPath.children(node).filter { it.isJsonObject }.forEach { walk(it.asJsonObject) }
            }
            walk(case.getAsJsonObject("layout").deepCopy())
            val expected = case.getAsJsonObject("expect").entrySet().associate { it.key to it.value.asString }
            assertEquals(case.get("name").asString, expected, got.filterKeys { it in expected })
            compared += expected.size
        }
        assertTrue("too few labelled nodes compared ($compared)", compared >= 20)
    }

    @Test
    fun anEmptyIdIsStillTheId_asTheSharedRuleAndSwiftJsonUIRead() {
        assertEquals("", LayoutPath.viewId(json("""{"type": "Switch", "id": ""}""")))
        assertEquals("switch_0", LayoutPath.viewId(json("""{"type": "Switch", "id": null}""")))
    }

    /**
     * DynamicViews(components): the list read as a root's child list, each
     * entry `0_<i>` — they were all `0`, so two lists entries' id-less nodes
     * shared viewIds (and Radio groups). An entry a parent stamped keeps its
     * stamp; the caller's object is not stamped.
     */
    @Test
    fun aListOfRootsNamesEachEntryAsARootsChild() {
        val a = json("""{"type": "Switch"}""")
        val b = json("""{"type": "View", "child": [{"type": "Switch"}]}""")
        val entries = listOf(a, b).mapIndexed { i, node -> LayoutPath.listEntry(node, i) }
        assertEquals(listOf("switch_0_0", "view_0_1"), entries.map { LayoutPath.viewId(it) })
        LayoutPath.enter(entries[1])
        assertEquals("switch_0_1_0", LayoutPath.viewId(entries[1].getAsJsonArray("child")[0].asJsonObject))
        assertTrue("the caller's object was stamped", !a.has(LayoutPath.KEY))
        assertEquals("switch_0", LayoutPath.viewId(a))

        val stamped = json("""{"type": "Switch", "_layoutPath": "0_3_1"}""")
        assertTrue(LayoutPath.listEntry(stamped, 0) === stamped)
        assertEquals("switch_0_3_1", LayoutPath.viewId(LayoutPath.listEntry(stamped, 0)))
    }

    // --- where the runtime takes it -----------------------------------------

    private fun source(path: String): String {
        val file = File("src/main/kotlin/com/kotlinjsonui/dynamic/$path")
        assertTrue("missing: ${file.absolutePath}", file.isFile)
        return file.readText()
    }

    /**
     * DynamicView enters each node after its include is expanded and its
     * style merged, and before the responsive resolution copies it. (A
     * source-order read, not a run: the call sites are in a composable.)
     */
    @Test
    fun dynamicViewTakesThePathOnTheExpandedStyledNodeBeforeItIsCopied() {
        val view = source("DynamicView.kt")
        val style = view.indexOf("DynamicStyleLoader.applyStyle(context, json)")
        val include = view.indexOf("IncludeExpander.processIncludes(styledJson)")
        val enter = view.indexOf("LayoutPath.enter(styledJson)")
        val responsive = view.indexOf("resolveResponsiveNode(styledJson)")
        assertTrue("order style $style < include $include < enter $enter < responsive $responsive",
            style in 0 until include && include < enter && enter < responsive)
    }

    /**
     * The family: nothing in the runtime names a drawn node by the clock, a
     * random source or an object's identity. Code only — a comment that
     * names the old spelling does not count. The one clock left is a hot
     * reload message's timestamp, named here with its reason.
     */
    @Test
    fun noRuntimeSourceNamesANodeByTheClockOrARandomSource() {
        val token = Regex("""System\.currentTimeMillis|System\.nanoTime|SystemClock\.|\bRandom\b|\bUUID\b|kotlin\.random|identityHashCode""")
        fun hits(text: String, name: String): List<String> = text.lines().mapIndexedNotNull { i, line ->
            val t = line.trimStart()
            if (t.startsWith("//") || t.startsWith("*") || t.startsWith("/*")) null
            else if (token.containsMatchIn(line.substringBefore("//"))) "$name:${i + 1}" else null
        }
        // The scan tells a call from a comment (both sides).
        assertEquals(1, hits("""            val id = a.common.id ?: "radio_${'$'}{System.currentTimeMillis()}"""", "call").size)
        assertEquals(0, hits("            // it was System.currentTimeMillis()\n     * Random", "comment").size)

        val root = File("src/main/kotlin/com/kotlinjsonui/dynamic")
        val files = root.walkTopDown().filter { it.isFile && it.extension == "kt" }.sortedBy { it.path }.toList()
        assertTrue("the scan reads too few files (${files.size})", files.size > 60)
        val allowed = setOf("hotloader/HotLoader.kt") // LayoutUpdate's timestamp — a message, not a name
        val found = files.flatMap { hits(it.readText(), it.relativeTo(root).path) }
            .filterNot { hit -> allowed.any { hit.startsWith("$it:") } }
        assertEquals("a drawn name from the clock or a random source", emptyList<String>(), found)
        assertTrue("the allowed timestamp moved or went (re-read it)",
            hits(File(root, "hotloader/HotLoader.kt").readText(), "hotloader/HotLoader.kt").size == 1)
    }
}
