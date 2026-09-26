package com.kotlinjsonui.dynamic

import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Every key the device harness StageMeasurer hands a component to draw with
 * (its TYPES and EFFECT_EXTRA, androidTest) is one the component DynamicView
 * dispatches that type to reads. A key it does not read measures nothing:
 * the harness drew CircleImage with `srcName`, which DynamicCircleImageComponent
 * does not read (it reads src / source / url), so every CircleImage stage was
 * measured on an image that was never there — the fallback Box.
 *
 * Read from the sources, mechanically:
 * - the harness entries: StageMeasurer.kt's TYPES / EFFECT_EXTRA literals;
 * - the component: TypeSynonyms' drawn-as type (the vendored table), then
 *   DynamicView.kt's dispatch, matched as spelled (type names are case-sensitive);
 * - what it reads: its APPLIED set (what UnappliedAttributes checks), its
 *   literal raw reads (TypedAttrs.undeclared / rawKey, json.get / has), and
 *   UnappliedAttributes.COMMON_APPLIED / STRUCTURAL_KEYS.
 * The stage keys (StageMeasurer.STAGES) are not audited here: whether each
 * applies is what CommonStagesOnEveryComponentTest measures.
 */
class StageMeasurerKeysAreReadTest {
    private val src = File("src")

    private fun entries(source: String, name: String): List<Pair<String, Set<String>>> {
        val start = source.indexOf("val $name")
        val end = source.indexOf("\n        )\n", start) + 1
        require(start >= 0 && end > start) { "$name not found in StageMeasurer.kt" }
        val pair = Regex("\"(\\w+)\" to (?:\"\"\"(.*?)\"\"\"(?=,?\\n)|\"(.*?)\"(?=,?\\n))", RegexOption.DOT_MATCHES_ALL)
        return pair.findAll(source.substring(start, end)).map { m ->
            val frag = (m.groups[2]?.value ?: m.groups[3]?.value ?: "").trim().removePrefix(",")
            val keys = if (frag.isBlank()) emptySet() else JsonParser.parseString("{$frag}").asJsonObject.keySet()
            m.groupValues[1] to keys
        }.toList()
    }

    private fun setOf(source: String, name: String): Set<String> =
        Regex("$name[^=]*=\\s*setOf\\((.*?)\\)", RegexOption.DOT_MATCHES_ALL).find(source)
            ?.let { m -> Regex("\"(\\w+)\"").findAll(m.groupValues[1]).map { it.groupValues[1] }.toSet() } ?: emptySet()

    private val dispatch: Map<String, String> by lazy {
        val dv = File(src, "main/kotlin/com/kotlinjsonui/dynamic/DynamicView.kt").readText()
        Regex("^\\s*((?:\"[A-Za-z]+\",?\\s*)+)->\\s*(Dynamic\\w+)\\.create", RegexOption.MULTILINE).findAll(dv)
            .flatMap { m -> Regex("\"([A-Za-z]+)\"").findAll(m.groupValues[1]).map { it.groupValues[1] to m.groupValues[2] } }
            .toMap()
    }

    private val synonyms by lazy {
        JsonParser.parseString(File(src, "main/resources/com/kotlinjsonui/dynamic/type_synonyms.json").readText())
            .asJsonObject.getAsJsonObject("synonyms")
    }

    private fun drawnAs(type: String): String {
        val e = synonyms.get(type)?.asJsonObject ?: return type
        return (e.get("render_as") ?: e.get("canonical"))?.asString ?: type
    }

    private fun reads(component: String): Set<String> {
        val code = File(src, "main/kotlin/com/kotlinjsonui/dynamic/components/$component.kt").readText()
        val literal = Regex("(?:undeclared|rawKey)\\(\\s*json\\s*,\\s*\"(\\w+)\"").findAll(code).map { it.groupValues[1] } +
            Regex("json\\.(?:get|has|getAsJsonArray|getAsJsonObject)\\(\"(\\w+)\"\\)").findAll(code).map { it.groupValues[1] }
        return setOf(code, "APPLIED") + literal + UnappliedAttributes.COMMON_APPLIED + UnappliedAttributes.STRUCTURAL_KEYS
    }

    /** `type key` for every harness key the dispatched component does not read. */
    private fun unread(harness: List<Pair<String, Set<String>>>): List<String> = harness.flatMap { (type, keys) ->
        val component = dispatch[drawnAs(type)]
        keys.filter { component == null || it !in reads(component) }
            .map { "$type $it${if (component == null) " (no component)" else " ($component)"}" }
    }

    @Test
    fun theHarnessDrawsEveryComponentWithKeysItReads() {
        val sm = File(src, "androidTest/kotlin/com/kotlinjsonui/dynamic/StageMeasurer.kt").readText()
        val harness = entries(sm, "TYPES") + entries(sm, "EFFECT_EXTRA")
        assertTrue("too few harness entries read: ${harness.size}", harness.size >= 35)
        assertEquals("keys the harness passes that the component does not read", emptyList<String>(), unread(harness))
    }

    /** The detector's controls: a key a component reads, and one it does not. */
    @Test
    fun theDetectorTellsAReadKeyFromAnUnreadOne() {
        assertEquals(emptyList<String>(), unread(listOf("Image" to setOf("srcName"), "CircleImage" to setOf("src"))))
        assertEquals(
            listOf("CircleImage srcName (DynamicCircleImageComponent)", "HStack nosuchkey (DynamicContainerComponent)"),
            unread(listOf("CircleImage" to setOf("srcName"), "HStack" to setOf("nosuchkey")))
        )
    }
}
