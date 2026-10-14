package com.kotlinjsonui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The scrollTo value each Collection of a screen was last seen with, by the
 * Collection's id (jsonui-cli Collection.scrollTo;
 * kjui-collection-scrollto-ignored-after-visibility-gone, 2026-10-14).
 *
 * A scroll is a CHANGE of the value: the value a Collection is drawn with
 * scrolls nowhere. visibility gone takes a Collection out of the composition,
 * so the one that comes back is composed afresh, and a value written in the
 * same update that brought it back was read as the value it is drawn with —
 * the request never ran. The memory lives above the gone boundary, at the
 * screen: a Collection entering with a value other than the one it was last
 * seen with on this screen is entering with a request, and runs it. A screen
 * opened afresh has an empty memory, so the value it opens with still scrolls
 * nowhere. A Collection with no id is not remembered (nothing names it), and
 * keeps the first-draw rule.
 */
class CollectionScrollMemory {
    private val seen = HashMap<String, Any?>()

    /** Whether [value] is a request for the Collection [id] entering now. */
    fun isRequestOnEntry(id: String?, value: Any?): Boolean =
        id != null && seen.containsKey(id) && seen[id] != value

    /** The value the Collection [id] is now drawn with. */
    fun record(id: String?, value: Any?) {
        if (id != null) seen[id] = value
    }
}

/** The screen's memory; null where no screen provided one (the first-draw rule only). */
val LocalCollectionScrollMemory = staticCompositionLocalOf<CollectionScrollMemory?> { null }

/**
 * Provides a [CollectionScrollMemory] to [content] unless one is already
 * provided above: the outermost provider is the screen, and an included
 * layout shares its screen's memory.
 */
@Composable
fun ProvideCollectionScrollMemory(content: @Composable () -> Unit) {
    if (LocalCollectionScrollMemory.current != null) {
        content()
        return
    }
    val memory = remember { CollectionScrollMemory() }
    CompositionLocalProvider(LocalCollectionScrollMemory provides memory, content = content)
}
