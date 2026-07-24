package com.kotlinjsonui.embed

import java.lang.ref.WeakReference

/**
 * embedId-keyed lookup for the private navigators of isolated embeds
 * (KotlinJsonUI >= 2.12.0). Containers in `navigationMode: Isolated`
 * register their active navigator while composed, so code OUTSIDE the embed
 * subtree (a parent VM resetting a pane on tab switch, the conformance
 * host's injected handlers) can drive push/pop imperatively without
 * CompositionLocal access.
 *
 * Contract:
 * - Registration is library-internal: the container registers on
 *   composition and unregisters on dispose. Only [get] is public API.
 * - Navigators are held weakly — a container torn down without its
 *   dispose callback never leaks its stack.
 * - Two composed embeds sharing one embedId: last registration wins
 *   (mirrors the "distinct embeds should use distinct ids" guidance).
 */
object EmbedNavigatorRegistry {
    private val storage = mutableMapOf<String, WeakReference<EmbedNavigator>>()

    /**
     * The navigator of the currently composed isolated embed with this id,
     * or null when no such embed is composed.
     */
    @Synchronized
    fun get(embedId: String): EmbedNavigator? {
        val ref = storage[embedId] ?: return null
        val navigator = ref.get()
        if (navigator == null) storage.remove(embedId)
        return navigator
    }

    @Synchronized
    internal fun register(embedId: String, navigator: EmbedNavigator) {
        storage[embedId] = WeakReference(navigator)
    }

    /**
     * Remove the registration only if this navigator is still the one
     * registered — a later composition under the same id must not be
     * clobbered by the earlier container's teardown.
     */
    @Synchronized
    internal fun unregister(embedId: String, navigator: EmbedNavigator) {
        if (storage[embedId]?.get() === navigator) storage.remove(embedId)
    }
}
