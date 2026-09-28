package com.kotlinjsonui.dynamic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Structural grep gate for the typed-attribute rollout: dynamic component
 * bodies must not read attributes from the raw gson node. Attribute access
 * goes through the generated `<Component>Attributes.parse` + the TypedAttrs
 * bridge; the only raw reads left on the own node are:
 *
 * - STRUCTURAL_KEYS (child/children/sections/... — tree structure, not
 *   attributes),
 * - `TypedAttrs.rawKey` (declared keys whose accepted value space is wider
 *   than the declared type) and `TypedAttrs.undeclared` (legacy runtime
 *   extras, each to be declared in the SSoT or no longer read) — both
 *   named, greppable entry points.
 *
 * Reads on CHILD nodes (constraint loops etc.) use receivers other than
 * `json`, so they don't trip this gate.
 */
class ComponentRawReadGateTest {

    // Not "cell": a Collection's node-level `cell` template was the one own-
    // node read of it, and it is gone — the key is not declared and no
    // codegen draws it (audit 2026-09-26). A section's `cell` is read off
    // the section object, which this gate does not see.
    private val allowedKeys = setOf(
        "child", "children", "data", "shared_data", "include", "style",
        "sections", "header", "footer", "type"
    )

    @Test
    fun `no raw own-node attribute reads in component bodies`() {
        val componentsDir = File("src/main/kotlin/com/kotlinjsonui/dynamic/components")
        assertTrue("components dir missing: ${componentsDir.absolutePath}", componentsDir.isDirectory)

        val pattern = Regex("""json\.(?:get|has|getAsJsonObject|getAsJsonArray)\("([A-Za-z_]+)"\)""")
        val violations = mutableListOf<String>()

        // The scan reads every component DynamicView dispatches to: a list
        // that read nothing (the files moved, a filter that matched none)
        // would find no violation and pass.
        val files = componentsDir.listFiles { f -> f.extension == "kt" }!!.sorted()
        val dispatched = Regex("""(Dynamic\w+Component)\.create\(""")
            .findAll(File("src/main/kotlin/com/kotlinjsonui/dynamic/DynamicView.kt").readText())
            .map { it.groupValues[1] }.toSet()
        assertTrue("too few dispatched components read (${dispatched.size})", dispatched.size >= 27)
        val missing = dispatched - files.map { it.nameWithoutExtension }.toSet()
        assertTrue("dispatched components the scan does not read: $missing", missing.isEmpty())

        files.forEach { file ->
            file.readLines().forEachIndexed { i, line ->
                // A doc comment that quotes the anti-pattern is the clearest
                // way to explain why a read moved to the typed row — and this
                // gate used to report those quotes as violations, i.e. its own
                // documentation. Comment-ONLY lines are dropped; a trailing
                // comment still counts, because the code before it is real.
                val trimmed = line.trimStart()
                if (trimmed.startsWith("//") || trimmed.startsWith("*")) return@forEachIndexed
                pattern.findAll(line).forEach { m ->
                    val key = m.groupValues[1]
                    if (key !in allowedKeys) {
                        violations.add("${file.name}:${i + 1}: raw read of '$key'")
                    }
                }
            }
        }

        assertTrue(
            "raw own-node attribute reads found (use the typed attributes / " +
                "TypedAttrs.rawKey / TypedAttrs.undeclared instead):\n" +
                violations.joinToString("\n"),
            violations.isEmpty()
        )
    }

    /**
     * A structural key is read as structure, never as a value.
     *
     * The first test lets these keys through because they hold the tree, not
     * attributes — and that let a component read one as an attribute:
     * Progress drew a spinner for a `style` named `circular` / `large` (style
     * is the style file's name), and Toggle bound its state to a string in
     * `data` (the key that declares a layout's data). A structural key read as
     * a scalar (`asString`, `asInt`, …), or through TypedAttrs' named raw
     * entry points, is that misread. The one read that is the key's own
     * structure is Include's layout name. Chains that span lines are joined
     * before matching; comment-only lines are dropped as above.
     */
    @Test
    fun `no structural key is read as a value`() {
        val keys = allowedKeys.joinToString("|")
        val asValue = Regex(
            """json\s*\.\s*get\(\s*"($keys)"\s*\)(?:\s*\??\.\s*takeIf\s*\{[^}]*\})?\s*\??\.\s*as(?:String|Int|Long|Boolean|Float|Double|Number)\b""" +
                """|TypedAttrs\s*\.\s*(?:undeclared|rawKey)\(\s*json\s*,\s*"($keys)"\s*\)"""
        )
        val itsOwnStructure = setOf("DynamicIncludeComponent.kt" to "include")
        fun code(text: String) = text.lines().joinToString("\n") { line ->
            val t = line.trimStart()
            if (t.startsWith("//") || t.startsWith("*")) "" else line
        }
        fun reads(name: String, text: String): List<String> {
            val c = code(text)
            return asValue.findAll(c).mapNotNull { m ->
                val key = m.groupValues[1].ifEmpty { m.groupValues[2] }
                if ((name to key) in itsOwnStructure) null
                else "$name:${c.substring(0, m.range.first).count { it == '\n' } + 1}: '$key' read as a value"
            }.toList()
        }

        // both sides: the two misreads this arm exists for, and a structural read
        assertEquals(1, reads("X.kt", """val style = json.get("style")?.asString ?: "linear"""").size)
        assertEquals(1, reads("X.kt", "val d = json.get(\"data\")\n    ?.takeIf { it.isJsonPrimitive }?.asString").size)
        assertEquals(1, reads("X.kt", """val t = TypedAttrs.undeclared(json, "type")""").size)
        assertEquals(0, reads("X.kt", """val c = json.get("children")?.asJsonArray""").size)
        assertEquals(0, reads("X.kt", """    // json.get("style")?.asString""").size)
        assertEquals(0, reads("DynamicIncludeComponent.kt", """val n = json.get("include")?.asString ?: return""").size)
        assertEquals(1, reads("DynamicTextComponent.kt", """val n = json.get("include")?.asString ?: return""").size)

        val dir = File("src/main/kotlin/com/kotlinjsonui/dynamic/components")
        val files = dir.listFiles { f -> f.extension == "kt" }!!.sorted()
        assertTrue("too few component files read (${files.size})", files.size >= 27)
        val found = files.flatMap { reads(it.name, it.readText()) }
        assertEquals("structural keys read as values", emptyList<String>(), found)
        // the exemption is still the key's own structure (a read that moved away leaves it stale)
        assertEquals(1, reads("X.kt", File(dir, "DynamicIncludeComponent.kt").readText()).count { "'include'" in it })
    }

    /**
     * The comment skip must not become a hole.
     *
     * Loosening a gate to stop it reporting a false positive is how gates rot,
     * so the loosening itself is pinned: a real read still trips it, and a real
     * read that merely *ends* in a comment still trips it.
     */
    @Test
    fun `the comment skip does not blind the gate to real reads`() {
        val pattern = Regex("""json\.(?:get|has|getAsJsonObject|getAsJsonArray)\("([A-Za-z_]+)"\)""")
        fun scanned(line: String): Boolean {
            val trimmed = line.trimStart()
            if (trimmed.startsWith("//") || trimmed.startsWith("*")) return false
            return pattern.containsMatchIn(line)
        }

        assertTrue("a bare read must be caught", scanned("""    val x = json.get("items")"""))
        assertTrue(
            "code that ends in a comment is still code",
            scanned("""    val x = json.get("items") // legacy""")
        )
        assertTrue("a comment-only line must be skipped", !scanned("""    // json.get("items")"""))
        assertTrue("a doc-comment body must be skipped", !scanned("""     * json.get("items")"""))
    }
}
