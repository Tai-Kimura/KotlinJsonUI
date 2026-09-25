package com.kotlinjsonui.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A type-synonym spelling draws exactly as the type the table says it is
 * drawn as (TypeSynonyms, jsonui-cli's type_synonyms.json vendored), with
 * the attributes its spelling means.
 *
 * Measured before the change (API 35 tablet emulator): DynamicView's own
 * cases took some spellings and not others — HStack as a horizontal
 * container, Table as a component of its own, Iframe / Img / Picker /
 * DatePicker / … not at all, as unknown types — and SwiftJsonUI's cases
 * took a different set.
 */
@RunWith(AndroidJUnit4::class)
class TypeSynonymsDrawTest {
    @get:Rule
    val rule = createComposeRule()

    /** What each drawn-as type needs to draw, as the stage arm draws it. */
    private val extra: Map<String, String> = StageMeasurer.TYPES.toMap()

    /** The page draws differently each time: compared by elements only. */
    private val unsteady = setOf("Web")

    @Test
    fun everySynonymDrawsAsItsType() {
        val m = StageMeasurer(rule)
        m.start()
        val entries = TypeSynonyms.entries
        assertTrue("no synonyms read", entries.size >= 40)
        val raw = JsonParser.parseReader(
            java.io.InputStreamReader(TypeSynonyms::class.java.getResourceAsStream(TypeSynonyms.RESOURCE)!!)
        ).asJsonObject.getAsJsonObject("synonyms")
        val differ = mutableListOf<String>()
        for ((spelling, _) in raw.entrySet()) {
            val entry = entries.getValue(spelling.lowercase())
            val target = entry.drawnAs
            val x = extra[target] ?: error("no drawing extra for $target")
            val implied = entry.implied.entries.joinToString("") { (k, v) -> ", \"$k\": $v" }
            val asSynonym = "{\"type\": \"$spelling\"$x, \"width\": 200, \"height\": 60}"
            val asTarget = "{\"type\": \"$target\"$x$implied, \"width\": 200, \"height\": 60}"
            val e1 = m.elements(asSynonym)
            val e2 = m.elements(asTarget)
            if (e1 != e2) differ += "$spelling elements $e1 vs $target $e2"
            if (target !in unsteady) {
                val d = m.diff(m.capture(asSynonym), m.capture(asTarget))
                if (d != 0) differ += "$spelling pixels differ from $target by $d"
            }
        }
        assertEquals("synonyms drawn otherwise than their type", emptyList<String>(), differ)
    }

    private var json by mutableStateOf("{\"type\": \"View\"}")
    private var lastError: Throwable? = null

    private fun show(j: String) {
        json = j
        rule.waitForIdle()
    }

    private fun startBare() {
        rule.setContent {
            Box(Modifier.testTag("root")) {
                DynamicView(
                    json = JsonParser.parseString(json).asJsonObject,
                    data = emptyMap(),
                    onError = { lastError = it }
                )
            }
        }
    }

    private fun twoLabels(type: String, extra: String = "") =
        "{\"type\": \"$type\"$extra, \"child\": [" +
            "{\"type\": \"Label\", \"id\": \"a\", \"text\": \"aa\"}, " +
            "{\"type\": \"Label\", \"id\": \"b\", \"text\": \"bb\"}]}"

    private fun horizontal(): Boolean {
        val a = rule.onNodeWithTag("a", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val b = rule.onNodeWithTag("b", useUnmergedTree = true).getUnclippedBoundsInRoot()
        return b.left >= a.right && b.top < a.bottom
    }

    private fun vertical(): Boolean {
        val a = rule.onNodeWithTag("a", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val b = rule.onNodeWithTag("b", useUnmergedTree = true).getUnclippedBoundsInRoot()
        return b.top >= a.bottom && b.left < a.right
    }

    @Test
    fun theSpellingsThatMeanADirectionLayTheirChildrenOutInIt() {
        startBare()
        for (t in listOf("HStack", "Row")) {
            show(twoLabels(t))
            assertTrue("$t: children not side by side", horizontal())
        }
        for (t in listOf("VStack", "Column")) {
            show(twoLabels(t))
            assertTrue("$t: children not one under the other", vertical())
        }
        // The node's own orientation is drawn, not the spelling's.
        show(twoLabels("HStack", ", \"orientation\": \"vertical\""))
        assertTrue("HStack with orientation vertical: not drawn vertical", vertical())
    }

    @Test
    fun aTypeNeitherDeclaredNorASynonymIsUnknown() {
        startBare()
        lastError = null
        show("{\"type\": \"Triangle\"}")
        val e = lastError
        assertTrue("Triangle was drawn: $e", e is IllegalArgumentException && e.message!!.contains("Triangle"))
    }
}
