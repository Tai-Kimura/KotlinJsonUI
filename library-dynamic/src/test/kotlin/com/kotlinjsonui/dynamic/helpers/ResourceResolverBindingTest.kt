package com.kotlinjsonui.dynamic.helpers

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Text binding resolution must route through DataBindingContext so dot
 * paths ("profile.name"), array access and `?? default` all behave the
 * same as every other binding site. A flat data[key] lookup here silently
 * blanked nested-path bindings — caught by the Embed nested-params
 * conformance fixtures (Embed/params__nested_leaf*).
 */
class ResourceResolverBindingTest {

    private val data: Map<String, Any> = mapOf(
        "userName" to "Ada",
        "profile" to mapOf(
            "name" to "Grace",
            "meta" to mapOf("age" to "36")
        ),
        "items" to listOf(mapOf("title" to "first"))
    )

    @Test
    fun flatBindingResolves() {
        assertEquals("Ada", ResourceResolver.processDataBinding("@{userName}", data))
    }

    @Test
    fun dotPathBindingResolves() {
        assertEquals("Grace", ResourceResolver.processDataBinding("@{profile.name}", data))
    }

    @Test
    fun deepDotPathBindingResolves() {
        assertEquals("36", ResourceResolver.processDataBinding("@{profile.meta.age}", data))
    }

    @Test
    fun arrayAccessBindingResolves() {
        assertEquals("first", ResourceResolver.processDataBinding("@{items[0].title}", data))
    }

    @Test
    fun unresolvedBindingRendersEmpty() {
        assertEquals("", ResourceResolver.processDataBinding("@{missing.path}", data))
    }

    @Test
    fun defaultValueAppliesWhenUnresolved() {
        assertEquals(
            "Guest",
            ResourceResolver.processDataBinding("@{missing ?? \"Guest\"}", data)
        )
    }

    @Test
    fun mixedTextInterpolates() {
        assertEquals(
            "Hello Grace!",
            ResourceResolver.processDataBinding("Hello @{profile.name}!", data)
        )
    }
}
