package com.kotlinjsonui.dynamic.helpers

import android.content.Context
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypeSynonyms
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.ButtonAttributes
import com.kotlinjsonui.dynamic.generated.CheckBoxAttributes
import com.kotlinjsonui.dynamic.generated.LabelAttributes
import com.kotlinjsonui.dynamic.generated.RadioAttributes
import com.kotlinjsonui.dynamic.generated.SegmentAttributes
import com.kotlinjsonui.dynamic.generated.SelectBoxAttributes
import com.kotlinjsonui.dynamic.generated.SliderAttributes
import com.kotlinjsonui.dynamic.generated.SwitchAttributes
import com.kotlinjsonui.dynamic.generated.TextFieldAttributes
import com.kotlinjsonui.dynamic.generated.ViewAttributes

/**
 * What TalkBack is told about a control beyond what it shows — the rule
 * jsonui-cli's codegen applies (shared/core/control_accessibility.rb), on the
 * chain every component takes beside its testTag ([ModifierBuilder.applyTestTag],
 * as kjui's build_test_tag):
 * - `accessibilityLabel` (alias `contentDescription`): the name a control is
 *   read by in place of its text, a strings.json key or text localized like
 *   `text`, or a binding — `contentDescription`. Declared on the types a user
 *   operates; on a View only when the View is one button to a screen reader
 *   ([TapAccessibility.shape] BUTTON / COMBINE).
 * - `accessibilityLiveRegion` (polite / assertive / off) on a Label or a
 *   View: `liveRegion`, read out whenever what it shows changes.
 */
object ControlAccessibility {
    enum class Live { POLITE, ASSERTIVE }

    /** The declared section a node is drawn as (Check as CheckBox, Toggle as Switch). */
    fun section(json: JsonObject): String =
        TypeSynonyms.drawnType(json.get("type")?.takeIf { it.isJsonPrimitive }?.asString ?: "View")

    /** The accessibilityLabel as written (a key, text or `@{…}`), or null. */
    fun writtenLabel(json: JsonObject): String? {
        val m = TypedAttrs.toAttrMap(json)
        val value = when (section(json)) {
            "Button" -> ButtonAttributes.parse(m).accessibilityLabel
            "CheckBox" -> CheckBoxAttributes.parse(m).accessibilityLabel
            "Switch" -> SwitchAttributes.parse(m).accessibilityLabel
            "Radio" -> RadioAttributes.parse(m).accessibilityLabel
            "Segment" -> SegmentAttributes.parse(m).accessibilityLabel
            "Slider" -> SliderAttributes.parse(m).accessibilityLabel
            "SelectBox" -> SelectBoxAttributes.parse(m).accessibilityLabel
            "TextField" -> TextFieldAttributes.parse(m).accessibilityLabel
            "View" -> if (isOneButton(json)) ViewAttributes.parse(m).accessibilityLabel else null
            else -> null
        }
        return TypedAttrs.rawString(value)?.takeIf { it.isNotEmpty() }
    }

    fun isOneButton(json: JsonObject): Boolean =
        TapAccessibility.shape(json).let { it == TapAccessibility.Shape.BUTTON || it == TapAccessibility.Shape.COMBINE }

    /** The name TalkBack reads, resolved and localized, or null. */
    fun label(json: JsonObject, data: Map<String, Any>, context: Context?): String? =
        writtenLabel(json)?.let { ResourceResolver.resolveTextValue(it, data, context) }

    /** A Label's or a View's live region; null for off or none. */
    fun live(json: JsonObject): Live? {
        val m = TypedAttrs.toAttrMap(json)
        val spelling = when (section(json)) {
            "Label" -> TypedAttrs.enumString(LabelAttributes.parse(m).accessibilityLiveRegion) { it.json }
            "View" -> TypedAttrs.enumString(ViewAttributes.parse(m).accessibilityLiveRegion) { it.json }
            else -> null
        }
        return when (spelling) {
            "polite" -> Live.POLITE
            "assertive" -> Live.ASSERTIVE
            else -> null
        }
    }
}
