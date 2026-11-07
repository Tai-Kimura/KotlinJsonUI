package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.State
import androidx.compose.runtime.withFrameNanos

/**
 * After an edit has gone to the data: the field shows what the data holds.
 *
 * The write-back keyed on the value does not run when a view model takes an
 * edit back to the value it held ("12" -> "12a" -> "12"): the flow conflates
 * the raw edit and the correction, composition never sees a different value,
 * and the field keeps "12a" while the data holds "12". So the value is read
 * again once composition has caught up with the writes. Read at once, the value
 * is still the old one and every key is taken back (measured: the cursor and the
 * composing region lost). One frame was enough in every row measured; the second
 * is margin, not a measured need. A frame is the composition's own step, not a
 * length of time.
 *
 * A later edit restarts the caller's effect, which cancels this. The field is
 * only written when the data differs from the edit, so an edit the view model
 * took leaves the cursor and the IME's composing region alone.
 */
internal suspend fun TextFieldState.followHeldValue(edit: String, held: State<String>) {
    withFrameNanos { }
    withFrameNanos { }
    val value = held.value
    if (text.toString() == edit && value != edit) {
        this.edit { replace(0, length, value) }
    }
}
