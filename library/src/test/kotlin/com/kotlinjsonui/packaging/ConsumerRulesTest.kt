package com.kotlinjsonui.packaging

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The consumer ProGuard rules this module ships carry no
 * `-assumenosideeffects` / `-assumevalues`.
 *
 * Up to 2.43.6 `consumer-rules.pro` declared `-assumenosideeffects` on
 * `com.kotlinjsonui.dynamic.**`, and in an app's R8 release build it emptied
 * the dashedBorder / dottedBorder draw lambda this module keeps in that
 * package — the border and the element's own content were not drawn (ticket
 * kjui-consumer-rules-assumenosideeffects-strips-dashed-border-draw). Such a
 * rule tells R8 a call may be dropped; on code that draws or writes state it
 * silently removes behaviour, so none is shipped.
 *
 * The files read are the ones `consumerProguardFiles(...)` in
 * build.gradle.kts names — what goes into the aar — not a fixed path.
 */
class ConsumerRulesTest {

    private val forbidden = listOf("-assumenosideeffects", "-assumevalues")

    private fun consumerRuleFiles(): List<File> {
        val gradle = File("build.gradle.kts").readText()
        val call = Regex("""consumerProguardFiles\(([^)]*)\)""")
        return call.findAll(gradle)
            .flatMap { Regex("\"([^\"]+)\"").findAll(it.groupValues[1]) }
            .map { File(it.groupValues[1]) }
            .toList()
    }

    /** Rule text with `#` comments removed, so the explanation above may name the rules. */
    private fun rules(file: File): String =
        file.readLines().joinToString("\n") { it.substringBefore('#') }

    @Test
    fun theShippedRuleFilesAreFoundAndExist() {
        val files = consumerRuleFiles()
        assertTrue("consumerProguardFiles names no file in build.gradle.kts", files.isNotEmpty())
        files.forEach { assertTrue("$it is named in consumerProguardFiles but missing", it.isFile) }
    }

    @Test
    fun noShippedRuleTellsR8ACallMayBeDropped() {
        val found = consumerRuleFiles().flatMap { file ->
            rules(file).lines().mapIndexedNotNull { i, line ->
                val hit = forbidden.firstOrNull { line.contains(it) }
                hit?.let { "${file.path}:${i + 1}: $it" }
            }
        }
        assertEquals("rules R8 may use to drop calls: $found", emptyList<String>(), found)
    }

    @Test
    fun aCommentNamingTheRuleIsNotARule() {
        val tmp = File.createTempFile("rules", ".pro")
        try {
            tmp.writeText("# -assumenosideeffects is not shipped\n-keep class a.B\n")
            assertTrue(forbidden.none { rules(tmp).contains(it) })
            tmp.writeText("# why\n-assumenosideeffects class a.B { void f(); }\n")
            assertTrue(rules(tmp).contains("-assumenosideeffects"))
        } finally {
            tmp.delete()
        }
    }
}
