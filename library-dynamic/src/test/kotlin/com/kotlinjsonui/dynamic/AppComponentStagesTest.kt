package com.kotlinjsonui.dynamic

import androidx.compose.ui.Modifier
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * An app component's common stages in Dynamic mode.
 *
 * - ModifierBuilder.buildModifier leaves out what the component applies
 *   itself (`handles`): an onClick it passes to its own composable is not
 *   applied a second time. Given nothing, every stage, as before.
 * - A component the app's handler draws without buildModifier, while its node
 *   declares a common stage, is said once per type (AppComponentStages); one
 *   drawn with it is not.
 */
class AppComponentStagesTest {
    private val said = mutableListOf<String>()

    @Before
    fun hook() {
        AppComponentStages.resetWarned()
        AppComponentStages.warningSink = { said += it }
    }

    @After
    fun unhook() {
        AppComponentStages.warningSink = null
        AppComponentStages.resetWarned()
    }

    private fun json(s: String) = JsonParser.parseString(s).asJsonObject

    private fun Modifier.elementNames(): List<String> =
        foldIn(mutableListOf<String>()) { acc, element -> acc.apply { add(element.javaClass.name) } }

    private val tapper: Map<String, Any> = mapOf("tapS" to { _: String -> })
    private val node = json("""{"type": "ProbeWidget", "onClick": "@{tapS}", "alpha": 0.5}""")

    @Test
    fun `buildModifier leaves out a stage the component applies itself, and only that one`() {
        val every = ModifierBuilder.buildModifier(node, tapper).elementNames()
        assertTrue("control: the node's onClick is a clickable: $every", every.any { it.contains("Clickable") })
        val noClick = ModifierBuilder.buildModifier(node, tapper, handles = setOf("onClick")).elementNames()
        assertFalse("the component's own onClick is not applied again: $noClick", noClick.any { it.contains("Clickable") })
        assertEquals("the alpha is still applied: $every -> $noClick", every.size - 1, noClick.size)
        val noAlpha = ModifierBuilder.buildModifier(node, tapper, handles = setOf("alpha")).elementNames()
        assertTrue("the clickable is still applied: $noAlpha", noAlpha.any { it.contains("Clickable") })
        assertEquals("only the alpha is left out: $every -> $noAlpha", every.size - 1, noAlpha.size)
    }

    @Test
    fun `a component drawn without buildModifier is said once per type, and one drawn with it is not`() {
        AppComponentStages.begin()
        AppComponentStages.end("ProbeBare", node, drawnByApp = true)
        AppComponentStages.begin()
        AppComponentStages.end("ProbeBare", node, drawnByApp = true)
        assertEquals("said once: $said", 1, said.size)
        assertTrue(said[0], said[0].startsWith("Custom component 'ProbeBare' declares onClick, alpha, but it was drawn without ModifierBuilder.buildModifier"))

        AppComponentStages.begin()
        ModifierBuilder.buildModifier(node, tapper)
        AppComponentStages.end("ProbeBuilt", node, drawnByApp = true)
        assertEquals("built with it: nothing said: $said", 1, said.size)
    }

    @Test
    fun `nothing is said for a built-in, a node with no stage, or a child's buildModifier`() {
        AppComponentStages.begin()
        AppComponentStages.end("Label", node, drawnByApp = false)
        AppComponentStages.begin()
        AppComponentStages.end("ProbePlain", json("""{"type": "ProbePlain"}"""), drawnByApp = true)
        assertEquals(emptyList<String>(), said)

        // A child's entry is its own: what the child builds marks the child's.
        AppComponentStages.begin() // the app's component
        AppComponentStages.begin() // its child
        ModifierBuilder.buildModifier(node, tapper)
        AppComponentStages.end("Label", node, drawnByApp = false)
        AppComponentStages.end("ProbeParent", node, drawnByApp = true)
        assertEquals("the parent, not marked by its child, is said: $said", 1, said.size)
        assertTrue(said[0], said[0].startsWith("Custom component 'ProbeParent'"))
    }
}
