package com.kotlinjsonui.dynamic.helpers

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The testTag (with testTagsAsResourceId) comes after the stages that place
 * the view — margins and offset — in every hand-written modifier chain, as it
 * does in [ModifierBuilder.standardOrder] (StandardModifierOrderTest pins that
 * table; this reads the components that do not go through it). A margin is
 * drawn as padding, so a tag before it made the tagged box — the resource-id
 * bounds, TalkBack's focus frame — the margin-inclusive box (ticket
 * kjui-a11y-bounds-of-a-margined-view-include-its-margin).
 */
class TestTagAfterPlacingStagesTest {

    private val componentsDir: File by lazy {
        val rel = "src/main/kotlin/com/kotlinjsonui/dynamic/components"
        listOf(File(rel), File("library-dynamic/$rel")).firstOrNull { it.isDirectory }
            ?: error("components not found from ${File(".").absolutePath}")
    }

    /**
     * Each applyTestTag call followed, in the same chain, by an applyMargins or
     * applyOffset call. The chain ends at a closing brace or `} else {` at a
     * lower indent, or where its variable is declared again.
     */
    private fun violations(source: String): List<String> {
        val lines = source.lines()
        val found = mutableListOf<String>()
        lines.forEachIndexed { i, line ->
            if (!line.contains("ModifierBuilder.applyTestTag(") || line.trim().startsWith("//")) return@forEachIndexed
            val indent = line.length - line.trimStart().length
            val variable = Regex("""^\s*(?:var\s+)?(\w+)\s*(?::\s*\w+\s*)?=""").find(line)?.groupValues?.get(1)
            for (k in i + 1 until lines.size) {
                val text = lines[k]
                val stripped = text.trim()
                val ik = text.length - text.trimStart().length
                if (stripped.isNotEmpty() && ik < indent && stripped.startsWith("}")) break
                if (variable != null && Regex("""^\s*(var|val)\s+$variable\b""").containsMatchIn(text)) break
                if (stripped.startsWith("//")) continue
                val m = Regex("""ModifierBuilder\.apply(Margins|Offset)\(""").find(text)
                if (m != null) {
                    found += "line ${i + 1}: applyTestTag before apply${m.groupValues[1]} at line ${k + 1}"
                    break
                }
            }
        }
        return found
    }

    @Test
    fun everyChainTagsInsideTheMarginsAndTheOffset() {
        val files = componentsDir.listFiles { f -> f.extension == "kt" }.orEmpty()
            .filter { it.readText().contains("ModifierBuilder.applyTestTag(") }
        assertTrue("the population: ${files.map { it.name }}", files.size >= 9)
        val bad = files.associate { it.name to violations(it.readText()) }.filterValues { it.isNotEmpty() }
        assertEquals("chains that tag before placing the view", emptyMap<String, List<String>>(), bad)
    }

    @Test
    fun theReaderFindsATagBeforeTheMarginsAndPassesOneAfterTheOffset() {
        val before = """
            var modifier: Modifier = Modifier
            modifier = ModifierBuilder.applyTestTag(modifier, json)
            modifier = ModifierBuilder.applyMargins(modifier, json, data)
        """.trimIndent()
        assertEquals(1, violations(before).size)
        val after = """
            var modifier: Modifier = Modifier
            modifier = ModifierBuilder.applyMargins(modifier, json, data)
            modifier = ModifierBuilder.applyOffset(modifier, json, data)
            modifier = ModifierBuilder.applyTestTag(modifier, json)
        """.trimIndent()
        assertEquals(emptyList<String>(), violations(after))
    }

    @Test
    fun aHiddenBranchTagIsNotJudgedByTheOtherBranch() {
        val source = """
            modifier = ModifierBuilder.applyMargins(modifier, json, data)
            if (isHidden) {
                modifier = ModifierBuilder.applyTestTag(modifier, json)
                modifier = modifier.alpha(0f)
            } else {
                modifier = ModifierBuilder.applyOffset(modifier, json, data)
                modifier = ModifierBuilder.applyTestTag(modifier, json)
            }
        """.trimIndent()
        assertEquals(emptyList<String>(), violations(source))
    }
}
