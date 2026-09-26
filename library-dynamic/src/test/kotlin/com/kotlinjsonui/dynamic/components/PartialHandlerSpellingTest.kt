package com.kotlinjsonui.dynamic.components

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * A partialAttributes range's handler is read in both declared spellings:
 * `onClick` (canonical, a binding) and `onclick` (its alias, a selector), the
 * canonical one first when both are written (4f ruling, jsonui-cli 1.9.0).
 */
class PartialHandlerSpellingTest {
    @Test
    fun bothSpellingsAreReadAndTheCanonicalOneWins() {
        val cases = linkedMapOf(
            "onClick binding" to (mapOf("onClick" to "@{onTerms}") to "onTerms"),
            "onclick selector" to (mapOf("onclick" to "onTerms") to "onTerms"),
            "both: onClick wins" to (mapOf("onClick" to "@{onTerms}", "onclick" to "onOther") to "onTerms"),
            "onClick not a binding: the alias is read" to (mapOf("onClick" to "onTerms", "onclick" to "onOther") to "onOther"),
            "onclick as a binding is no selector" to (mapOf("onclick" to "@{onTerms}") to null),
            "blank" to (mapOf("onClick" to " ", "onclick" to "") to null),
            "none" to (emptyMap<String, String>() to null),
        )
        assertEquals(
            cases.mapValues { it.value.second },
            cases.mapValues { DynamicTextComponent.partialHandlerName(it.value.first) }
        )
    }
}
