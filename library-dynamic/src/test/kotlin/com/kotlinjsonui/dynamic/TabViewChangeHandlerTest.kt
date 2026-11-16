package com.kotlinjsonui.dynamic

import com.kotlinjsonui.dynamic.components.DynamicTabViewComponent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * A TabView's tab-change handler (DynamicTabViewComponent.tabChangeCallback)
 * is called with the new index as the data holds it — `(Int)` with the index,
 * `(String, Int)` with the viewId first, `()` with nothing — as kjui build
 * calls it as declared. Only `(Int) -> Unit` was called.
 */
class TabViewChangeHandlerTest {
    @Test
    fun theHandlerIsCalledAsTheDataHoldsIt() {
        val got = mutableListOf<String>()
        val onIndex: (Int) -> Unit = { i -> got += "index $i" }
        val onIdIndex: (String, Int) -> Unit = { id, i -> got += "$id $i" }
        val onNothing: () -> Unit = { got += "nothing" }
        val data: Map<String, Any> = mapOf("onIndex" to onIndex, "onIdIndex" to onIdIndex, "onNothing" to onNothing)
        for (name in listOf("onIndex", "onIdIndex", "onNothing")) {
            val callback = DynamicTabViewComponent.tabChangeCallback("@{$name}", data, "tv")
            requireNotNull(callback) { name }(1)
        }
        assertEquals(listOf("index 1", "tv 1", "nothing"), got)
        assertNull(DynamicTabViewComponent.tabChangeCallback(null, data, "tv"))
        assertNull(DynamicTabViewComponent.tabChangeCallback("@{missing}", data, "tv"))
    }
}
