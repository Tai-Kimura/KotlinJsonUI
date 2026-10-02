package com.kotlinjsonui.dynamic

import com.kotlinjsonui.dynamic.components.DynamicTabViewComponent
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * A bare handler name on an event attribute the SSoT types `string | binding`
 * (Collection onValueChange / onItemAppear, TabView onValueChange) is a
 * declared form: `"onValueChange": "onTab"` calls what `"@{onTab}"` calls.
 * Through 2.43.0 the Dynamic renderer read the binding form only and dropped
 * a bare name with no warning (bare-event-handler-is-dropped-without-a-warning).
 */
class BareHandlerNameTest {
    @Test
    fun aBareNameReadsAsItsBinding() {
        assertEquals("@{onPage}", ModifierBuilder.handlerExpression("@{onPage}"))
        assertEquals("@{onPage}", ModifierBuilder.handlerExpression("onPage"))
        assertEquals("@{handlers.onPage}", ModifierBuilder.handlerExpression("handlers.onPage"))
        assertEquals("@{_on_page2}", ModifierBuilder.handlerExpression(" _on_page2 "))
    }

    @Test
    fun textThatIsNoNameIsNoHandler() {
        assertNull(ModifierBuilder.handlerExpression(null))
        assertNull(ModifierBuilder.handlerExpression(""))
        assertNull(ModifierBuilder.handlerExpression("on page"))
        assertNull(ModifierBuilder.handlerExpression("2page"))
        assertNull(ModifierBuilder.handlerExpression("onPage()"))
        assertNull(ModifierBuilder.handlerExpression("a..b"))
    }

    @Test
    fun aTabViewCallsABareName() {
        val got = mutableListOf<Int>()
        val onTab: (Int) -> Unit = { got += it }
        val data: Map<String, Any> = mapOf("onTab" to onTab)
        requireNotNull(DynamicTabViewComponent.tabChangeCallback("onTab", data, "tv")) { "bare" }(2)
        requireNotNull(DynamicTabViewComponent.tabChangeCallback("@{onTab}", data, "tv")) { "binding" }(3)
        assertEquals(listOf(2, 3), got)
        assertNull(DynamicTabViewComponent.tabChangeCallback("missing", data, "tv"))
    }
}
