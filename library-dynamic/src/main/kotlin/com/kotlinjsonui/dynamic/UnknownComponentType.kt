package com.kotlinjsonui.dynamic

/**
 * The sentence a node of a type this runtime cannot draw is named with (4f's
 * ruling, jsonui-cli 1.9.0): the type as written, and — when it matches a
 * type the runtime draws but for its case — that type. Type names are matched
 * as written, as the SSoT spells them. The same sentence as jsonui-cli's
 * shared validator (JsonUIShared::AttributeValidatorCore
 * .unknown_component_type_message) and the kjui / sjui / rjui codegen; held
 * to jsonui-cli's shared/core/unknown_component_type_vectors.json, copied
 * byte-identical into src/test/resources (UnknownComponentTypeTest).
 */
object UnknownComponentType {
    const val TEMPLATE = "Unknown component type '%s'"
    const val HINT = " — did you mean '%s'? Type names are case-sensitive."

    /** The sentence for [written], given the types the runtime draws. */
    fun message(written: String, known: Collection<String>): String {
        // TypeSynonyms.caseOnlyMatch: the one place a candidate is looked
        // for — among [known], the app's types, the synonyms and the alias
        // sections; never the type itself (a type known by a definition that
        // nothing draws is not offered as its own spelling).
        val canonical = TypeSynonyms.caseOnlyMatch(written, known)
        val base = TEMPLATE.format(written)
        return if (canonical != null) base + HINT.format(canonical) else base
    }
}
