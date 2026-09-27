package com.kotlinjsonui.core

import java.util.concurrent.ConcurrentHashMap

/**
 * An enum attribute's value by its declared spelling, case and all (4f's
 * ruling, jsonui-cli 1.9.0, as type names are). `declared` is a generated
 * attribute enum's `declaredSpellings` — every spelling
 * attribute_definitions.json declares for the attribute, its values and its
 * valueAliases keys, case-sensitive (`CommonAttributes.Visibility
 * .declaredSpellings`) — or, in this module, which does not see the generated
 * tables, a copy held to them by a test (VISIBILITY, LIST_STYLE).
 *
 * The hand-written comparisons compare lowercased names; [lowered] is what
 * they compare now: the lowercased value when it is one of the declared
 * spellings, null for anything else — a spelling declared in no case
 * ("Gone" for "gone"), a word declared nowhere — which each comparison
 * already sends to its default. They lowercased the value first, so "Gone"
 * was drawn as gone here while the codegen, which compares as written, drew
 * it visible. SwiftJsonUI reads the same way (DeclaredSpelling.lowered).
 *
 * A value the generated parse did not see (a bound value, a string handed
 * to a composable) is named with [context]: the generated parse's sentence,
 * "<context>: unknown enum value 'X'", with " — did you mean 'x'?" when it
 * differs from a declared spelling only in case — once per context and
 * value, to [warningSink]. A value that came through the generated parse
 * (TypedAttrs.enumString) was named there, and is not named again.
 */
object DeclaredSpelling {
    /** The declared spellings of `common.visibility` (CommonAttributes.Visibility.declaredSpellings). */
    val VISIBILITY: List<String> = listOf("visible", "invisible", "gone")

    /** The declared spellings of `Collection.listStyle` (CollectionAttributes.ListStyle.declaredSpellings). */
    val LIST_STYLE: List<String> = listOf("plain", "grouped", "insetGrouped", "sidebar")

    /** Receives every value named; DynamicView installs one in a debuggable build. */
    @Volatile
    var warningSink: ((String) -> Unit)? = null

    private val warned = ConcurrentHashMap.newKeySet<String>()

    /** The lowercased value when it is one of [declared]; null otherwise. */
    fun lowered(value: String?, declared: Collection<String>): String? =
        value?.takeIf { it in declared }?.lowercase()

    /** [lowered], naming a value that is not one of [declared] as [context]'s (once per context and value). */
    fun lowered(value: String?, declared: Collection<String>, context: String): String? {
        val v = value ?: return null
        if (v in declared) return v.lowercase()
        if (warned.add("$context\u0000$v")) warningSink?.invoke(message(context, v, declared))
        return null
    }

    /** The generated parse's sentence for [value], not one of [declared]. */
    fun message(context: String, value: String, declared: Collection<String>): String {
        val near = declared.firstOrNull { it.equals(value, ignoreCase = true) }
        return "$context: unknown enum value '$value'" + (near?.let { " — did you mean '$it'?" } ?: "")
    }
}
