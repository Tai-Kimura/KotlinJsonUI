package com.kotlinjsonui.dynamic

import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.components.DynamicTabViewComponent
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A TabView tab's badge reaches a screen reader once — the dynamic component
 * and kjui's generated code alike (GeneratedTabBadgeProbe.kt is the emitter's
 * output for the same four tabs).
 *
 * Material3's NavigationBarItem clears the icon slot's semantics, the badge
 * inside it, whenever a label is shown. Measured before the fix (2026-09-25,
 * API 35, material3 1.4.0): the AccessibilityNodeInfo held the badge 0 times
 * with labels. The item now carries it as its stateDescription; without a
 * label the slot keeps it, so the item does not repeat it.
 *
 * What is counted is what a screen reader receives: text, contentDescription
 * and stateDescription of every AccessibilityNodeInfo in the window.
 *
 * ⚠️ Instrumented: needs an emulator (ANDROID_SERIAL=emulator-…), and
 * KotlinJsonUI's CI runs no connected tests — the numbers come from a run on
 * an emulator, not from CI.
 */
@RunWith(AndroidJUnit4::class)
class TabBadgeA11yTest {
    @get:Rule
    val rule = createComposeRule()

    @Before
    fun connectAccessibility() {
        // A connected UiAutomation is an accessibility client; without one
        // Compose publishes no AccessibilityNodeInfo to read.
        InstrumentationRegistry.getInstrumentation().uiAutomation
    }

    /** How many fields hold [needle], and every stateDescription present. */
    private fun received(vararg needles: String): Pair<Map<String, Int>, List<String>> {
        rule.waitForIdle()
        val counts = needles.associateWith { 0 }.toMutableMap()
        val states = mutableListOf<String>()
        fun walk(n: AccessibilityNodeInfo?) {
            if (n == null) return
            val fields = listOf(n.text, n.contentDescription, n.stateDescription).mapNotNull { it?.toString() }
            for (needle in needles) counts[needle] = counts.getValue(needle) + fields.count { it.contains(needle) }
            n.stateDescription?.let { states += it.toString() }
            for (i in 0 until n.childCount) walk(n.getChild(i))
        }
        walk(InstrumentationRegistry.getInstrumentation().uiAutomation.rootInActiveWindow)
        return counts to states
    }

    private fun dynamicTabs(showLabels: Boolean, badges: Boolean = true) = JsonParser.parseString(
        """{"type":"TabView","id":"tabs","showLabels":$showLabels,"tabs":[
            {"title":"Home","icon":"home"},
            {"title":"Inbox","icon":"mail"${if (badges) ",\"badge\":3" else ""}},
            {"title":"News","icon":"star"${if (badges) ",\"badge\":\"NEW\"" else ""}},
            {"title":"Chat","icon":"chat"${if (badges) ",\"badge\":\"@{unread}\"" else ""}}]}"""
    ).asJsonObject

    // "Inbox" stands for the title: the dynamic component also draws the
    // selected tab's (Home's) title as its content.
    private val needles = arrayOf("3", "NEW", "5", "Inbox")

    @Test
    fun dynamic_withLabels_eachBadgeOnce() {
        rule.setContent { DynamicTabViewComponent.create(dynamicTabs(true), mapOf("unread" to 5)) }
        val (counts, states) = received(*needles)
        assertEquals(mapOf("3" to 1, "NEW" to 1, "5" to 1, "Inbox" to 1), counts)
        assertEquals(listOf("3", "NEW", "5"), states)
    }

    @Test
    fun dynamic_withoutLabels_eachBadgeOnce_notOnTheItem() {
        rule.setContent { DynamicTabViewComponent.create(dynamicTabs(false), mapOf("unread" to 5)) }
        val (counts, states) = received(*needles)
        assertEquals(mapOf("3" to 1, "NEW" to 1, "5" to 1, "Inbox" to 1), counts)
        assertEquals(emptyList<String>(), states)
    }

    @Test
    fun dynamic_boundZero_noBadgeNoState() {
        rule.setContent { DynamicTabViewComponent.create(dynamicTabs(true), mapOf("unread" to 0)) }
        val (_, states) = received(*needles)
        assertEquals(listOf("3", "NEW"), states)
    }

    @Test
    fun dynamic_control_noBadges_noState() {
        rule.setContent { DynamicTabViewComponent.create(dynamicTabs(true, badges = false), mapOf("unread" to 5)) }
        val (counts, states) = received(*needles)
        assertEquals(mapOf("3" to 0, "NEW" to 0, "5" to 0, "Inbox" to 1), counts)
        assertEquals(emptyList<String>(), states)
    }

    @Test
    fun generated_withLabels_eachBadgeOnce() {
        rule.setContent { GeneratedTabBadgeLabelled(TabBadgeProbeData(unread = 5)) }
        val (counts, states) = received(*needles)
        assertEquals(mapOf("3" to 1, "NEW" to 1, "5" to 1, "Inbox" to 1), counts)
        assertEquals(listOf("3", "NEW", "5"), states)
    }

    @Test
    fun generated_withoutLabels_eachBadgeOnce_notOnTheItem() {
        rule.setContent { GeneratedTabBadgeUnlabelled(TabBadgeProbeData(unread = 5)) }
        val (counts, states) = received(*needles)
        assertEquals(mapOf("3" to 1, "NEW" to 1, "5" to 1, "Inbox" to 1), counts)
        assertEquals(emptyList<String>(), states)
    }

    @Test
    fun generated_boundZero_noBadgeNoState() {
        rule.setContent { GeneratedTabBadgeLabelled(TabBadgeProbeData(unread = 0)) }
        val (_, states) = received(*needles)
        assertEquals(listOf("3", "NEW"), states)
    }
}
