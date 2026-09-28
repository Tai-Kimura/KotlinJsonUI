package com.kotlinjsonui.dynamic.components

import androidx.compose.ui.text.style.TextAlign
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Where a Label without textAlign places its text (and each of its lines)
 * across its frame: by its gravity's horizontal part
 * (DynamicTextComponent.gravityTextAlign; kjui's codegen reads it the same in
 * TextComponent.gravity_text_align).
 */
class LabelGravityTextAlignTest {

    private fun align(json: String): TextAlign? =
        DynamicTextComponent.gravityTextAlign(JsonParser.parseString(json).asJsonObject)

    @Test
    fun aLabelWithAWidthOfItsOwnFollowsItsGravity() {
        val cases = mapOf(
            """{"width":32,"gravity":"right"}""" to TextAlign.End,
            """{"width":"matchParent","gravity":["top","centerHorizontal"]}""" to TextAlign.Center,
            """{"width":200,"gravity":"center"}""" to TextAlign.Center,
            """{"width":32,"gravity":"left"}""" to null,
            """{"width":32}""" to null,
        )
        assertEquals(cases, cases.mapValues { align(it.key) })
    }

    /**
     * No width guard: a wrapContent Label whose text wraps inside a narrower
     * parent takes the parent's width, so its lines follow the gravity as on
     * iOS and the web. On a single line the Text is as wide as its text and
     * the alignment changes nothing.
     */
    @Test
    fun aWrapContentLabelFollowsItsGravityToo() {
        val cases = mapOf(
            """{"width":"wrapContent","gravity":"center"}""" to TextAlign.Center,
            """{"width":"wrap_content","gravity":"right"}""" to TextAlign.End,
            """{"gravity":"centerHorizontal"}""" to TextAlign.Center,
            """{"width":"wrapContent"}""" to null,
        )
        assertEquals(cases, cases.mapValues { align(it.key) })
    }

    /** Center is tested before right (the SSoT Label.textAlign order, as on iOS and the web). */
    @Test
    fun centerWinsOverRightWhenTheGravityNamesBoth() {
        val cases = mapOf(
            """{"width":32,"gravity":"center|right"}""" to TextAlign.Center,
            """{"width":32,"gravity":"right|center"}""" to TextAlign.Center,
            """{"width":32,"gravity":["right","centerHorizontal"]}""" to TextAlign.Center,
            """{"width":32,"gravity":["right","centerVertical"]}""" to TextAlign.End,
        )
        assertEquals(cases, cases.mapValues { align(it.key) })
    }
}
