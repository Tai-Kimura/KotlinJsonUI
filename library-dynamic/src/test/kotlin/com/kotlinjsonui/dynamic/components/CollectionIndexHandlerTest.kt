package com.kotlinjsonui.dynamic.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * A Collection's index handler (onItemAppear, the pager's page change) is
 * called as the data holds it (jsonui-cli ticket kjui-dynamic-collection-
 * index-handler-without-a-parameter-is-never-called): a `() -> Unit` was
 * taken `as? Function1<Int, Unit>`, came back null and was never called —
 * measured on conf_ci, 0 calls in all five census cases while kjui codegen
 * called it.
 */
class CollectionIndexHandlerTest {
    @Test
    fun aHandlerWithNoParameterIsCalledWithNothing() {
        var calls = 0
        val handler = DynamicCollectionComponent.indexHandler({ calls++ ; Unit } as () -> Unit)
        handler!!(3)
        handler(4)
        assertEquals(2, calls)
    }

    @Test
    fun aHandlerWithOneParameterGetsTheIndex() {
        val got = mutableListOf<Any?>()
        DynamicCollectionComponent.indexHandler({ i: Int -> got += i; Unit })!!(3)
        DynamicCollectionComponent.indexHandler({ v: Any -> got += v; Unit })!!(5)
        assertEquals(listOf<Any?>(3, 5), got)
    }

    @Test
    fun aValueThatIsNoFunctionIsNoHandler() {
        assertNull(DynamicCollectionComponent.indexHandler("onAppear"))
        assertNull(DynamicCollectionComponent.indexHandler(null))
    }
}
