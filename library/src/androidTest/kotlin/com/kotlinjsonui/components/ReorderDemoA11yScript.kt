package com.kotlinjsonui.components

import android.content.Intent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * NOT a gate: the accessibility half of the reorder demo's video, run only when asked
 * (`-e reorderDemo 1`; it opens ReorderDemoActivity itself). It performs the rows' "下へ移動" / "上へ移動"
 * through the accessibility tree — the custom actions TalkBack offers — in real time, with pauses to film.
 */
@RunWith(AndroidJUnit4::class)
class ReorderDemoA11yScript {
    private fun dump(n: AccessibilityNodeInfo?, depth: Int = 0): String {
        if (n == null || depth > 12) return ""
        val me = "${n.className}:${n.text}:${n.contentDescription}:${n.actionList.mapNotNull { it.label }}"
        return (listOf(me) + (0 until n.childCount).map { dump(n.getChild(it), depth + 1) }).filter { it.isNotEmpty() }.joinToString(" | ")
    }

    @Test fun moves_through_the_accessibility_actions() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("reorderDemo") == "1")
        val inst = InstrumentationRegistry.getInstrumentation()
        val ui = inst.uiAutomation
        fun act(rowText: String, label: String) {
            var root = ui.rootInActiveWindow
            var tries = 0
            while ((root == null || root.packageName != inst.targetContext.packageName) && tries++ < 20) {
                Thread.sleep(250); root = ui.rootInActiveWindow
            }
            root ?: error("no window")
            // Compose's nodes are not searched by findAccessibilityNodeInfosByText: walk the tree.
            fun find(n: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
                if (n == null) return null
                if (n.text?.contains(rowText) == true) return n
                for (i in 0 until n.childCount) find(n.getChild(i))?.let { return it }
                return null
            }
            val node = find(root) ?: error("no row $rowText in ${root.packageName}: " + dump(root))
            var target: AccessibilityNodeInfo? = node
            while (target != null && target.actionList.none { it.label == label }) target = target.parent
            val action = target?.actionList?.first { it.label == label } ?: error("no '$label' on $rowText")
            target.performAction(action.id)
            Thread.sleep(1800)
        }
        // The demo is in this APK, and instrumentation restarts its process: open it from here. The drags are
        // the host's (adb input draganddrop) while this waits (`-e dragWindowMs`); the accessibility moves follow.
        inst.targetContext.startActivity(
            Intent(inst.targetContext, ReorderDemoActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
        Thread.sleep(InstrumentationRegistry.getArguments().getString("dragWindowMs")?.toLongOrNull() ?: 1500)
        act("白州", CollectionReorderText.moveDown)
        act("白州", CollectionReorderText.moveDown)
        act("秩父", CollectionReorderText.moveUp)
        Thread.sleep(1500)
    }
}
