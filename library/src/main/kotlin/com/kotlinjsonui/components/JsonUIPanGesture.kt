package com.kotlinjsonui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

/**
 * onPan: [onPan] is called on every drag event with the translation since the gesture began (the payload of
 * SwiftUI's DragGesture.Value.translation). The node owns the drag: a surrounding scroll does not also scroll from it.
 *
 * Why a wrapper: when Compose changes how a drag is read, only this body changes — the kjui codegen and the dynamic
 * face both call it with the handler (ticket kjui-slider-deprecated-in-material3-1-5: Compose UI 1.13 dispatches a
 * move whose screen location has not changed, which detectDragGestures does not consume; a finger held still on a
 * pan scrolled the list around it).
 */
fun Modifier.jsonUIPanGesture(key: Any?, onPan: (Offset) -> Unit): Modifier = composed {
    val panning = remember { BooleanArray(1) }
    this.consumePointerWhile(key) { panning[0] }.pointerInput(key) {
        var total = Offset.Zero
        detectDragGestures(
            onDragStart = { total = Offset.Zero; panning[0] = true },
            onDragEnd = { panning[0] = false },
            onDragCancel = { panning[0] = false },
            onDrag = { change, dragAmount ->
                change.consume()
                total += dragAmount
                onPan(total)
            }
        )
    }
}

/**
 * While [active], every change of the pointers on this node is consumed — a move that went nowhere included — after
 * the gestures inner to it have seen it (Main pass, inner first). A drag detector consumes only the changes that moved
 * in its node's coordinates; Compose UI 1.13 also dispatches moves whose screen location has not changed
 * (ComposeUiFlags.isTriggerMoveEventsWhenLocationHasNotChangedEnabled, on by default), and one left unconsumed reaches
 * the scroll under the node, which then scrolls by the distance the finger has come.
 */
internal fun Modifier.consumePointerWhile(key: Any?, active: () -> Boolean): Modifier =
    this.pointerInput(key) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Main)
                if (active()) event.changes.forEach { it.consume() }
            }
        }
    }
