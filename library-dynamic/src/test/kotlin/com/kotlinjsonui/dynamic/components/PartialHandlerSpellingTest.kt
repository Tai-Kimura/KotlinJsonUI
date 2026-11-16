package com.kotlinjsonui.dynamic.components

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * A partialAttributes range's handler is read in both declared spellings:
 * `onClick` (canonical) and `onclick` (its alias), each a binding or a
 * method name, the canonical one first when both are written. jsonui-cli
 * 1.9.0 folds `onclick` into `onClick` in the layouts `jui build`
 * distributes, so a name arrives in onClick.
 */
class PartialHandlerSpellingTest {
    @Test
    fun bothSpellingsAreReadAndTheCanonicalOneWins() {
        val cases = linkedMapOf(
            "onClick binding" to (mapOf("onClick" to "@{onTerms}") to "onTerms"),
            "onclick selector" to (mapOf("onclick" to "onTerms") to "onTerms"),
            "onClick holding a name (the alias folded)" to (mapOf("onClick" to "onTerms") to "onTerms"),
            "onclick holding a binding" to (mapOf("onclick" to "@{onTerms}") to "onTerms"),
            "both: onClick wins" to (mapOf("onClick" to "@{onTerms}", "onclick" to "onOther") to "onTerms"),
            "both names: onClick wins" to (mapOf("onClick" to "onTerms", "onclick" to "onOther") to "onTerms"),
            "onClick neither a binding nor a name: the alias is read" to (mapOf("onClick" to "@{onTerms} now", "onclick" to "onOther") to "onOther"),
            "blank binding: the alias is read" to (mapOf("onClick" to "@{ }", "onclick" to "onOther") to "onOther"),
            "blank" to (mapOf("onClick" to " ", "onclick" to "") to null),
            "none" to (emptyMap<String, String>() to null),
        )
        assertEquals(
            cases.mapValues { it.value.second },
            cases.mapValues { DynamicTextComponent.partialHandlerName(it.value.first) }
        )
    }
}
