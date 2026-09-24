package com.kotlinjsonui.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusEventModifierNode
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.FocusTargetModifierNode
import androidx.compose.ui.focus.Focusability
import androidx.compose.ui.focus.getFocusedRect
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.findNearestAncestor
import androidx.compose.ui.node.requireLayoutCoordinates
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * `ScrollView.keyboardAvoidance` / `keyboardAvoidancePadding` on Android,
 * in one place for both renders of a layout (the codegen emits this, the
 * dynamic ScrollView calls it).
 *
 * Two halves. The VIEWPORT: `imePadding()` ends the scrollable at the IME,
 * and the clearance band ends it `clearanceDp` above that while the IME is
 * up — so a field that stops at the viewport's edge stops that far above
 * the keyboard (SSoT: number, default 20; iOS reads the same attribute).
 * The FOLLOW: once the IME is up, if the focused field's bottom is below
 * the viewport's bottom, the list is scrolled by the shortfall.
 *
 * ⚠️ WHY THE FOLLOW IS HERE AT ALL. Until 2.38.0 both renders relied on
 * Compose to do it: `Focusable` brings a field into view when it gains
 * focus, and the scrollable's `ContentInViewNode` re-tracks a focused child
 * when its viewport shrinks (b/216842427) — the IME shrinking it through
 * `imePadding` is exactly that. Measured 2026-09-22 on an API 35 emulator
 * with the reporting face's window (`adjustNothing` + edge-to-edge, a
 * `weight(1f)` LazyColumn beside a fixed footer, the field fully or partly
 * visible, cold or warm IME): the field ended `clearanceDp` above the
 * viewport's edge in every arm — ⚠️ read through `boundsInRoot`, which the
 * list clips at that edge, so it could not see a field reaching past it
 * (see the FIELD, below). The same day, on the reporting user's
 * device, both apps left the field under the keyboard and only the band
 * appeared. Which variable differs on that device is NOT identified. So
 * the follow no longer depends on the built-in tracking's preconditions
 * (it needs the child to have been fully visible before the shrink, and
 * stands down while its own animation runs); this one asks a single
 * question after the IME has moved — is the focused field below the
 * viewport now? — and scrolls if so.
 *
 * 📌 One direction. It never scrolls a field DOWN into view: the IME only
 * takes space from the bottom, and a field above the viewport is where the
 * user put it. It runs only while the IME is up, and only for a vertical
 * list — a horizontal ScrollView keeps the viewport half and skips the
 * follow, because scrolling a LazyRow by a vertical shortfall is nonsense.
 *
 * 📌 Coordinates: a never-focusable focus target placed INSIDE the padding,
 * so its coordinate space is the padded viewport and "below the bottom" is
 * `bottom > height`.
 *
 * 🚨 THE FIELD, NOT THE CARET (2.40.0). Until then the follow read
 * `FocusTargetModifierNode.getFocusedRect()` alone, and for a text field
 * that is the CARET: `BasicTextField` sets `focusProperties.focusRect` to
 * its selection state's focus rect, the cursor while it is focused. So the
 * caret's bottom stopped `clearanceDp` above the viewport's edge and the
 * field reached `(field height − line height) / 2` past it, cut off by the
 * list's clip — 56dp / 16sp fields left about 18dp of their bottom (border
 * included) undrawn, and a 96dp field ended below the IME's top (a consumer,
 * 2026-09-25, and an independent check on an API 35 emulator: 48dp → 4.6dp
 * above the visible bottom, 96dp → −19.4dp, the rect read 16.4dp tall in
 * both). The SSoT, iOS and this file's own docs mean the field's bottom.
 * The test that shipped with the follow read `boundsInRoot`, clipped at the
 * very edge in question, so it read 20dp whatever the field did.
 *
 * So a field says where it is: [keyboardAvoidanceField] on a layout makes
 * that layout's bounds the thing kept clear while anything inside it has
 * focus. `CustomTextField` applies it, and every JsonUI TextField and
 * TextView — codegen and dynamic alike — is a `CustomTextField`.
 *
 * ⚠️ Not the deprecated `Modifier.onFocusedBoundsChanged`, which does pass
 * the whole focusable: its own deprecation says it will become a no-op, and
 * the follow would then fall back to the caret without a compile error.
 *
 * 📌 A focused thing with no [keyboardAvoidanceField] — a plain
 * `BasicTextField` inside a custom component — is kept clear by the rect
 * `getFocusedRect()` returns, which for a text field is the caret: the
 * behaviour before 2.40.0. Such a component opts in by applying the modifier
 * to its field.
 *
 * 📌 A field taller than the viewport cannot be fully shown. It is scrolled
 * no further than keeps the caret's top inside the viewport — the field's
 * bottom is where a short field stops, and the caret is where a tall one
 * is being typed into.
 */
@Composable
fun Modifier.keyboardAvoidance(
    listState: LazyListState,
    clearanceDp: Int = DEFAULT_KEYBOARD_CLEARANCE_DP,
): Modifier {
    val density = LocalDensity.current
    // Read here, not inside the effect: this is the state whose every change
    // (per IME animation frame) re-arms the follow.
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    val probe = remember { FocusedRectProbe() }
    LaunchedEffect(imeBottomPx) {
        if (imeBottomPx <= 0) return@LaunchedEffect
        if (listState.layoutInfo.orientation != Orientation.Vertical) return@LaunchedEffect
        // This frame's layout (the band below, the shrunk viewport) has to
        // land before the rect is read against the viewport's height.
        withFrameNanos { }
        val shortfall = probe.shortfallBelowViewport() ?: return@LaunchedEffect
        if (shortfall > 0f) listState.scrollBy(shortfall)
    }
    return this
        .imePadding()
        .padding(bottom = if (imeBottomPx > 0) clearanceDp.dp else 0.dp)
        .then(probe.element)
}

/** `keyboardAvoidancePadding`'s SSoT default, spelled once for both renders. */
const val DEFAULT_KEYBOARD_CLEARANCE_DP = 20

/**
 * A never-focusable focus target whose only job is to answer "where is the
 * focused descendant, in my coordinates, and how far below my bottom edge
 * does it reach?" — the query the follow needs, through the API Compose
 * recommends for it.
 */
internal class FocusedRectProbe {
    private var node: FocusedRectNode? = null
    private var size: IntSize by mutableStateOf(IntSize.Zero)

    val element: Modifier = FocusedRectElement(this)

    /**
     * How far to scroll so the focused field's bottom meets the viewport's
     * bottom, or null when nothing under this node has focus. Positive means
     * it is below.
     */
    fun shortfallBelowViewport(): Float? {
        val n = node ?: return null
        val focused: Rect = n.focusedRect() ?: return null
        return shortfall(focused, n.focusedFieldBounds(), size.height.toFloat())
    }

    internal fun attach(n: FocusedRectNode) { node = n }
    internal fun detach(n: FocusedRectNode) { if (node === n) node = null }
    internal fun sized(s: IntSize) { size = s }
}

/**
 * The scroll the follow asks for, in the viewport's coordinates.
 *
 * [focused] is what `getFocusedRect()` returned — the caret, for a text
 * field. [field] is the bounds of the [keyboardAvoidanceField] that has
 * focus, or null when the focused thing declared none; then [focused] is all
 * there is. With a field: its bottom, but never so far that the caret's top
 * leaves the viewport (only a field taller than the viewport reaches that
 * limit), and never less than brings the caret itself in.
 */
internal fun shortfall(focused: Rect, field: Rect?, viewportHeight: Float): Float {
    val caretShortfall = focused.bottom - viewportHeight
    if (field == null) return caretShortfall
    return maxOf(caretShortfall, minOf(field.bottom - viewportHeight, focused.top))
}

internal class FocusedRectNode(private val probe: FocusedRectProbe) :
    DelegatingNode(), LayoutAwareModifierNode, TraversableNode {
    private val target = delegate(FocusTargetModifierNode(focusability = Focusability.Never))

    /** The [keyboardAvoidanceField] below this node that last reported focus. */
    private var field: KeyboardAvoidanceFieldNode? = null

    override val traverseKey: Any get() = TraverseKey

    fun focusedRect(): Rect? = target.getFocusedRect()

    /** The focused field's bounds in this node's coordinates, unclipped; null when none has focus. */
    fun focusedFieldBounds(): Rect? {
        val f = field ?: return null
        if (!f.isAttached || !f.hasFocus || !isAttached) return null
        return requireLayoutCoordinates().localBoundingBoxOf(f.requireLayoutCoordinates(), clipBounds = false)
    }

    internal fun fieldFocused(f: KeyboardAvoidanceFieldNode) { field = f }
    internal fun fieldBlurred(f: KeyboardAvoidanceFieldNode) { if (field === f) field = null }

    override fun onAttach() { probe.attach(this) }
    override fun onDetach() { probe.detach(this); field = null }
    override fun onRemeasured(size: IntSize) { probe.sized(size) }

    internal companion object TraverseKey
}

private data class FocusedRectElement(val probe: FocusedRectProbe) : ModifierNodeElement<FocusedRectNode>() {
    override fun create() = FocusedRectNode(probe)
    override fun update(node: FocusedRectNode) {}
}

/**
 * Makes this layout the FIELD that `Modifier.keyboardAvoidance` keeps above
 * the IME while it, or anything inside it, has focus — its bottom rather
 * than the focused rect, which for a text field is the caret.
 *
 * Apply it where the field's visible box ends: after its size, inside its
 * margins. `CustomTextField` does; a custom component with a plain
 * `BasicTextField` applies it to that field's modifier to get the same
 * behaviour. Outside a `keyboardAvoidance` list it does nothing.
 */
fun Modifier.keyboardAvoidanceField(): Modifier = this then KeyboardAvoidanceFieldElement

private object KeyboardAvoidanceFieldElement : ModifierNodeElement<KeyboardAvoidanceFieldNode>() {
    override fun create() = KeyboardAvoidanceFieldNode()
    override fun update(node: KeyboardAvoidanceFieldNode) {}
    override fun hashCode(): Int = 0x6B626176
    override fun equals(other: Any?): Boolean = other === this
    override fun InspectorInfo.inspectableProperties() { name = "keyboardAvoidanceField" }
}

internal class KeyboardAvoidanceFieldNode : Modifier.Node(), FocusEventModifierNode {
    var hasFocus: Boolean = false
        private set

    private fun list(): FocusedRectNode? = findNearestAncestor(FocusedRectNode.TraverseKey) as? FocusedRectNode

    override fun onFocusEvent(focusState: FocusState) {
        hasFocus = focusState.hasFocus
        val list = list() ?: return
        if (hasFocus) list.fieldFocused(this) else list.fieldBlurred(this)
    }

    override fun onDetach() {
        list()?.fieldBlurred(this)
        hasFocus = false
    }
}
