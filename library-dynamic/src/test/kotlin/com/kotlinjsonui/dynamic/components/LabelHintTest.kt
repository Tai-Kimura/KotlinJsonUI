package com.kotlinjsonui.dynamic.components

import com.kotlinjsonui.dynamic.generated.LabelAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * A Label's placeholder needs BOTH `hint` and `hintAttributes`.
 *
 * UIKit's SJUILabel swaps in the hint, styled by hintAttributes, when the text
 * is empty — and it requires both, so a hint with no attributes shows nothing.
 * The kjui codegen states that rule verbatim rather than inventing a
 * divergence (`text_component.rb#hint_overrides`); the dynamic Label read none
 * of the four rows, which is why the hintAttributes control fixture deviated.
 *
 * `placeholder` is the declared alias of `hint`, and `hintColor` is the
 * colour-only fallback for a bag that carries no fontColor.
 */
class LabelHintTest {

    private fun attrs(vararg pairs: Pair<String, Any?>): LabelAttributes =
        LabelAttributes.parse(mapOf(*pairs))

    /** The decision the component makes, expressed the way the codegen does. */
    private fun hintShown(a: LabelAttributes): String? {
        val declaredText = a.text?.let { com.kotlinjsonui.dynamic.TypedAttrs.rawString(it) } ?: ""
        val hint = (a.hint ?: a.placeholder)?.takeIf {
            it.isNotEmpty() && a.hintAttributes != null
        }
        return if (declaredText.isEmpty()) hint else null
    }

    @Test
    fun bothPresentAndNoTextShowsTheHint() {
        // The `control_Label__hint-…_hintAttributes--fontSize-12_no-text` shape.
        val a = attrs("hint" to "Conformance Hint", "hintAttributes" to mapOf("fontSize" to 12.0))
        assertEquals("Conformance Hint", hintShown(a))
    }

    @Test
    fun aHintWithNoAttributesShowsNothing() {
        // `Label/hint__static` — this is NOT a gap, it is the rule.
        assertNull(hintShown(attrs("hint" to "Conformance Hint")))
    }

    @Test
    fun attributesWithNoHintShowNothing() {
        assertNull(hintShown(attrs("hintAttributes" to mapOf("fontSize" to 12.0))))
    }

    @Test
    fun declaredTextWinsOverTheHint() {
        val a = attrs(
            "text" to "Sample",
            "hint" to "Conformance Hint",
            "hintAttributes" to mapOf("fontSize" to 12.0)
        )
        assertNull(hintShown(a))
    }

    @Test
    fun placeholderIsTheDeclaredAliasOfHint() {
        val a = attrs("placeholder" to "Conformance Hint", "hintAttributes" to mapOf("fontSize" to 12.0))
        assertEquals("Conformance Hint", hintShown(a))
    }

    @Test
    fun hintWinsOverPlaceholderWhenBothAreWritten() {
        val a = attrs(
            "hint" to "H", "placeholder" to "P",
            "hintAttributes" to mapOf("fontSize" to 12.0)
        )
        assertEquals("H", hintShown(a))
    }

    @Test
    fun anEmptyHintIsNotAHint() {
        assertNull(hintShown(attrs("hint" to "", "hintAttributes" to mapOf("fontSize" to 12.0))))
    }
}
