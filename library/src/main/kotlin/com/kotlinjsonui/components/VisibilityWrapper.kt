package com.kotlinjsonui.components

import com.kotlinjsonui.core.DeclaredSpelling
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics

/**
 * Marks a view JsonUI keeps in the layout but does not draw — visibility
 * "invisible" and `hidden: true` — in the semantics tree.
 *
 * Nothing reads it but test drivers: jsonui-test-runner's `hidden` assertion
 * walks the app's unmerged semantics tree (it runs in the app's process) and
 * calls an id hidden when no node carries it, or its node or an ancestor
 * carries this key. The accessibility tree cannot answer that: Compose leaves
 * an off-screen node, and every node under a dialog or a modal sheet, out of
 * it, so "not drawn" and "not on screen" look the same there; and an
 * alpha-0 node is in the semantics tree either way, so without this key
 * "invisible" and `opacity: 0` (which is drawn, transparently) look the same.
 * The driver matches it by name, so the name is the contract.
 *
 * It changes nothing that is drawn, measured or read by an accessibility
 * service (measured 2026-10-15 on API 35: uiautomator dump, the
 * AccessibilityNodeInfo tree and the screen reader's stops are the same with
 * and without it, in the conformance host and a consumer's release build).
 *
 * It does not merge upward: the default merge policy takes the child's value,
 * so a clickable row or a Button holding an invisible child read as
 * JsonUIHidden in the MERGED tree, although the row itself is drawn. Keeping
 * the parent's value leaves the merged tree as it was; the driver reads the
 * unmerged tree, so its answer does not change.
 */
val JsonUIHiddenKey = SemanticsPropertyKey<Boolean>("JsonUIHidden", mergePolicy = { parent, _ -> parent })

private fun SemanticsPropertyReceiver.markHidden() {
    this[JsonUIHiddenKey] = true
}

/**
 * A wrapper component that handles visibility states for KotlinJsonUI
 *
 * @param visibility The visibility state ("visible", "invisible", "gone")
 * @param modifier Optional modifier for the wrapper
 * @param content The content to show/hide
 */
@Composable
fun VisibilityWrapper(
    visibility: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // As declared ("Gone" is no spelling of gone: the codegen compares as
    // written, and so does this — DeclaredSpelling)
    val declared = DeclaredSpelling.lowered(visibility, DeclaredSpelling.VISIBILITY, "common.visibility")
    if (declared == "gone") return

    if (declared == "invisible") {
        Box(modifier = modifier.alpha(0f).semantics { markHidden() }, content = content)
    } else {
        Box(modifier = modifier, content = content)
    }
}

/**
 * A wrapper component that handles boolean hidden state.
 *
 * `hidden: true` is the boolean shorthand for visibility:"invisible":
 * the content KEEPS its measured layout space but is not drawn
 * (alpha 0) and exposes no accessibility/semantics nodes
 * (clearAndSetSemantics), so it is not reachable by accessibility
 * services or UI test drivers. It must NOT collapse.
 *
 * @param hidden Whether the content should be hidden
 * @param modifier Optional modifier for the wrapper
 * @param content The content to show/hide
 */
@Composable
fun VisibilityWrapper(
    hidden: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    if (hidden) {
        Box(modifier = modifier.alpha(0f).clearAndSetSemantics { markHidden() }, content = content)
    } else {
        Box(modifier = modifier, content = content)
    }
}

/**
 * A wrapper component that handles both visibility and hidden states.
 *
 * hidden = true takes precedence and behaves as the boolean shorthand
 * for visibility:"invisible" with cleared semantics: layout space is
 * kept, nothing is drawn, and no accessibility node is exposed.
 *
 * @param visibility Optional visibility state ("visible", "invisible", "gone")
 * @param hidden Optional hidden boolean state
 * @param modifier Optional modifier for the wrapper
 * @param content The content to show/hide
 */
@Composable
fun VisibilityWrapper(
    visibility: String? = null,
    hidden: Boolean? = null,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // Handle hidden first (takes precedence): keep space, draw nothing,
    // no semantics — do NOT collapse.
    if (hidden == true) {
        Box(modifier = modifier.alpha(0f).clearAndSetSemantics { markHidden() }, content = content)
        return
    }

    // Then handle visibility
    val declared = DeclaredSpelling.lowered(visibility, DeclaredSpelling.VISIBILITY, "common.visibility")
    if (declared == "gone") return

    if (declared == "invisible") {
        Box(modifier = modifier.alpha(0f).semantics { markHidden() }, content = content)
    } else {
        Box(modifier = modifier, content = content)
    }
}
