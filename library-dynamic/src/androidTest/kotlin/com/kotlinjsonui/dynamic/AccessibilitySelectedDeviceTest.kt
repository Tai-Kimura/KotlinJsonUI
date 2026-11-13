package com.kotlinjsonui.dynamic

import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * accessibilitySelected on a device, through DynamicView: the node TalkBack reads carries `selected` while the
 * binding is true and loses it when it turns false; a Button, a tappable View and a tappable IconLabel alike, and a
 * View that taps nothing is never selected. Read twice — the Compose semantics, and the AccessibilityNodeInfo the
 * platform hands TalkBack (UiAutomation). Compose hands `selected` on a node that is not a Tab as a checkable node,
 * checked, whose state description is "Selected" — what TalkBack speaks; while it is false nothing is set, so
 * TalkBack says nothing of it (no "Not selected"), as VoiceOver.
 */
@RunWith(AndroidJUnit4::class)
class AccessibilitySelectedDeviceTest {
    @get:Rule val rule = createComposeRule()

    private val chosen = mutableStateOf(true)

    private val layout = JsonParser.parseString(
        """
        {"type":"View","id":"root","orientation":"vertical","child":[
          {"type":"Button","id":"must","text":"Must","onClick":"@{onTap}","accessibilitySelected":"@{isMust}"},
          {"type":"View","id":"maybe","onClick":"@{onTap}","accessibilitySelected":"@{isMust}",
           "child":[{"type":"Label","id":"maybe_label","text":"Maybe"}]},
          {"type":"IconLabel","id":"icon","text":"Icon","onClick":"@{onTap}","accessibilitySelected":"@{isMust}"},
          {"type":"View","id":"still","accessibilitySelected":true,
           "child":[{"type":"Label","id":"still_label","text":"Still"}]}
        ]}
        """.trimIndent()
    ).asJsonObject

    private fun content() = rule.setContent {
        Column { DynamicView(json = layout, data = mapOf("isMust" to chosen.value, "onTap" to {})) }
    }

    private fun selected(value: Boolean) = SemanticsMatcher.expectValue(SemanticsProperties.Selected, value)

    /** The AccessibilityNodeInfo whose view-id (testTag as resource id) is [id], as TalkBack is handed it. */
    /** What TalkBack is handed for [id], polled until it agrees with [checked] (the platform refreshes it after the
     *  semantics change, not with it), as (isChecked, stateDescription). */
    private fun told(id: String, checked: Boolean): Pair<Boolean?, String?> {
        var last: Pair<Boolean?, String?> = null to null
        repeat(30) {
            val info = nodeInfo(id)
            last = info?.isChecked to info?.stateDescription?.toString()
            if (last.first == checked) return last
            Thread.sleep(100)
        }
        return last
    }

    private fun nodeInfo(id: String): AccessibilityNodeInfo? {
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        val seen = mutableListOf<String>()
        fun find(n: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
            if (n == null) return null
            seen += "${n.viewIdResourceName}/${n.className}"
            if (n.viewIdResourceName?.endsWith(id) == true) return n
            for (i in 0 until n.childCount) find(n.getChild(i))?.let { return it }
            return null
        }
        repeat(20) {
            seen.clear()
            find(automation.rootInActiveWindow)?.let { return it }
            Thread.sleep(100)
        }
        throw AssertionError("$id not among the nodes TalkBack is handed: $seen")
    }

    @Test fun selected_follows_the_binding_on_every_one_button_node() {
        content()
        rule.waitForIdle()
        for (id in listOf("must", "maybe", "icon")) {
            rule.onNodeWithTag(id).assert(selected(true))
            assertEquals("$id: TalkBack is handed a checked node and speaks it as Selected", true to "Selected", told(id, true))
        }
        // A View that taps nothing is no element of its own: nothing is told.
        rule.onNodeWithTag("still").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Selected))

        rule.runOnIdle { chosen.value = false }
        rule.waitForIdle()
        for (id in listOf("must", "maybe", "icon")) {
            rule.onNodeWithTag(id).assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Selected))
            assertEquals("$id: TalkBack says nothing of it", false to null, told(id, false))
        }
    }
}
