package com.kotlinjsonui.dynamic.components

import androidx.compose.ui.Alignment
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The mapping behind DynamicLabelAndButtonPlacementTest (4f rulings
 * 2026-09-27, round 17): a Label's text in a frame of its own sits by its
 * gravity's vertical part, the middle when none is named; a Button's text
 * takes the button's width for textAlign only when the button has a width of
 * its own.
 */
class LabelAndButtonPlacementTest {
    private fun obj(json: String) = JsonParser.parseString(json).asJsonObject

    @Test
    fun aLabelsVerticalPlace() {
        val cases = listOf(
            """{"height":56,"gravity":"center"}""" to Alignment.CenterVertically,
            """{"height":56,"gravity":"centerVertical"}""" to Alignment.CenterVertically,
            """{"height":56,"gravity":"bottom"}""" to Alignment.Bottom,
            """{"height":56,"gravity":"top"}""" to null,
            """{"height":56,"gravity":["top","center"]}""" to Alignment.CenterVertically,
            """{"height":56,"gravity":"left"}""" to Alignment.CenterVertically,
            """{"height":56}""" to Alignment.CenterVertically,
            """{"height":"matchParent","gravity":"center"}""" to Alignment.CenterVertically,
            """{"minHeight":36,"gravity":"center"}""" to Alignment.CenterVertically,
            """{"height":"wrapContent","gravity":"center"}""" to null,
            """{"gravity":"center"}""" to null,
        )
        assertEquals(cases.map { it.second }, cases.map { DynamicTextComponent.labelVerticalAlignment(obj(it.first)) })
    }

    @Test
    fun aButtonOwnsItsWidth() {
        val cases = listOf("""{"width":200}""" to true, """{"width":"matchParent"}""" to true, """{"weight":1}""" to true,
            """{"width":"wrapContent"}""" to false, """{}""" to false)
        assertEquals(cases.map { it.second }, cases.map { DynamicButtonComponent.ownsWidth(obj(it.first)) })
    }
}
