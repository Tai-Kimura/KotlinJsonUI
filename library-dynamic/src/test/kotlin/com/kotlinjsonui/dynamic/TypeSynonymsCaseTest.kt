package com.kotlinjsonui.dynamic

import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.generated.JsonUIComponentAliases
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Type names are their SSoT spellings, case-sensitive (jsonui-cli 1.9.0). A
 * synonym and a declared alias section are found as written, not in another
 * case; TypeSynonyms.caseOnlyMatch is where what names an unknown type
 * (UnknownComponentType) finds the spelling it offers.
 */
class TypeSynonymsCaseTest {
    private var saved: Set<String> = emptySet()

    @Before
    fun save() { saved = Configuration.customComponentTypes }

    @After
    fun restore() { Configuration.customComponentTypes = saved }

    @Test
    fun `a synonym and an alias section are found as written, not in another case`() {
        assertEquals("View", TypeSynonyms.drawnType("HStack"))
        assertEquals("hstack", TypeSynonyms.drawnType("hstack"))
        val (alias, canonical) = JsonUIComponentAliases.canonical.entries.sortedBy { it.key }.first()
        assertNotEquals("control: the spelling has an upper-case letter", alias.lowercase(), alias)
        assertEquals(canonical, TypeSynonyms.drawnType(alias))
        assertEquals(alias.lowercase(), TypeSynonyms.drawnType(alias.lowercase()))
    }

    @Test
    fun `caseOnlyMatch offers the declared spelling from the caller's types, the synonyms, the aliases and the app's types`() {
        assertEquals("Switch", TypeSynonyms.caseOnlyMatch("switch", listOf("Label", "Switch")))
        assertEquals("SelectBox", TypeSynonyms.caseOnlyMatch("SELECTBOX", listOf("SelectBox")))
        assertEquals("HStack", TypeSynonyms.caseOnlyMatch("hstack"))
        val alias = JsonUIComponentAliases.canonical.keys.sorted().first()
        assertEquals(alias, TypeSynonyms.caseOnlyMatch(alias.uppercase()))
        Configuration.customComponentTypes = setOf("HeaderMenu")
        assertEquals("HeaderMenu", TypeSynonyms.caseOnlyMatch("headerMenu"))
    }

    @Test
    fun `caseOnlyMatch never offers the spelling itself, nor one no case matches`() {
        assertNull(TypeSynonyms.caseOnlyMatch("Switch", listOf("Switch")))
        assertNull(TypeSynonyms.caseOnlyMatch("HStack"))
        assertNull(TypeSynonyms.caseOnlyMatch("Nope", listOf("Label")))
    }
}
