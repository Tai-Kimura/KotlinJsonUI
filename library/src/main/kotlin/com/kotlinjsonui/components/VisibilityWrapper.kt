package com.kotlinjsonui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.clearAndSetSemantics

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
    if (visibility.lowercase() == "gone") return

    if (visibility.lowercase() == "invisible") {
        Box(modifier = modifier.alpha(0f), content = content)
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
        Box(modifier = modifier.alpha(0f).clearAndSetSemantics { }, content = content)
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
        Box(modifier = modifier.alpha(0f).clearAndSetSemantics { }, content = content)
        return
    }

    // Then handle visibility
    if (visibility?.lowercase() == "gone") return

    if (visibility?.lowercase() == "invisible") {
        Box(modifier = modifier.alpha(0f), content = content)
    } else {
        Box(modifier = modifier, content = content)
    }
}
