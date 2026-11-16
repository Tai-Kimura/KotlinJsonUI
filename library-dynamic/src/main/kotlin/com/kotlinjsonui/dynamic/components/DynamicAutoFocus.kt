package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

/**
 * `autoFocus` on a TextField / TextView: at the first composition the field
 * takes the focus and the soft keyboard is asked for, as kjui's generated
 * code does (`LaunchedEffect(Unit) { requester.requestFocus(); keyboard?.show() }`)
 * — requestFocus alone leaves the keyboard down. The caller attaches
 * [requester] to the field's own node.
 */
@Composable
internal fun DynamicAutoFocus(autoFocus: Boolean, requester: FocusRequester) {
    if (!autoFocus) return
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        requester.requestFocus()
        keyboard?.show()
    }
}
