package com.kotlinjsonui.dynamic

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Ruling U8 (include-child-ids-are-spelled-differently-on-each-platform): an
 * element inside an include that carries an id has ONE id on every face, and
 * it is the one the codegen writes. These arms hold the Dynamic
 * [IncludeExpander] to the codegen's answers.
 *
 * WHERE THE EXPECTED VALUES COME FROM — both are machine output, pasted:
 * - [camelCaseTable]: the snake→camel table, from running sjui_tools
 *   include_expander.rb:14/23 and kjui_tools include_expander.rb:16/25 under
 *   ruby 2.6.10 (the two agreed on every row). Compared by script with
 *   jsonui-cli shared/core/camel_case_vectors.json at 7bc802c4: identical in
 *   order and value. Vendor that file instead once it is released.
 * - [expectedJson]: the ids, data names and bindings that the codegen's
 *   `process_includes` produces for [specimensJson], run under ruby 3.2.2
 *   against jsonui-cli f45a0cfc — sjui_tools and kjui_tools gave
 *   byte-identical output. Nested partials sit at the layouts root, so the
 *   codegen's directory-relative lookup and this runtime's root lookup read
 *   the same files (the path rule is pinned separately, [candidatePaths]).
 * A transcription is not a compile: if the codegen's answer moves, these do
 * not follow by themselves — regenerate them from the Ruby.
 */
class IncludeExpanderIdSpellingTest {

    @After
    fun resetSeam() {
        IncludeExpander.layoutReader = null
    }

    // --- The two functions, row by row (the snake→camel table) ---------------------------

    /** (input, to_camel_case, combine_with_prefix("hero", input)) */
    private val camelCaseTable = listOf(
        Triple("email_verify_view", "emailVerifyView", "heroEmailVerifyView"),
        Triple("header1_title_label", "header1TitleLabel", "heroHeader1TitleLabel"),
        Triple("verify_2FA_form", "verify2faForm", "heroVerify2faForm"),
        Triple("clear_URL_button", "clearUrlButton", "heroClearUrlButton"),
        Triple("info_URL", "infoUrl", "heroInfoUrl"),
        Triple("URL_field", "URLField", "heroURLField"),
        Triple("iOS_version", "iOSVersion", "heroIOSVersion"),
        Triple("item_2", "item2", "heroItem2"),
        Triple("step2_done", "step2Done", "heroStep2Done"),
        Triple("a__b", "aB", "heroAB"),
        Triple("trailing_", "trailing", "heroTrailing"),
        Triple("_leading", "Leading", "heroLeading"),
        Triple("alreadyCamel", "alreadyCamel", "heroAlreadyCamel"),
        Triple("single", "single", "heroSingle"),
    )

    @Test
    fun toCamelCaseAnswersAsTheCodegenDoes() {
        for ((input, camel, _) in camelCaseTable) {
            assertEquals(input, camel, IncludeExpander.toCamelCase(input))
        }
    }

    @Test
    fun combineWithPrefixAnswersAsTheCodegenDoes() {
        for ((input, _, combined) in camelCaseTable) {
            assertEquals(input, combined, IncludeExpander.combineWithPrefix("hero", input))
        }
    }

    /** `return name unless prefix` passes only nil through; "" still raises. */
    @Test
    fun anEmptyPrefixStillRaisesTheName() {
        assertEquals("TypeBadge", IncludeExpander.combineWithPrefix("", "type_badge"))
    }

    // --- Whole layouts through processIncludes (the six specimens) -----------

    private val specimensJson = """
{
  "partials": {
    "u8_badges": {
      "type": "View",
      "id": "badges",
      "child": [
        {
          "type": "Label",
          "id": "type_badge",
          "text": "@{badge_text}"
        },
        {
          "type": "Label",
          "id": "typeBadge",
          "text": "b"
        }
      ],
      "data": [
        {
          "name": "badge_text",
          "class": "String",
          "defaultValue": ""
        }
      ]
    },
    "u8_card": {
      "type": "View",
      "id": "card",
      "child": [
        {
          "type": "Label",
          "id": "card_type_badge",
          "text": "c"
        }
      ]
    },
    "u8_plain": {
      "type": "View",
      "id": "plain",
      "child": [
        {
          "type": "Label",
          "id": "type_badge",
          "text": "p"
        },
        {
          "type": "Label",
          "id": "title",
          "text": "t"
        },
        {
          "type": "Label",
          "id": "verify_2FA_form",
          "text": "v"
        },
        {
          "type": "Label",
          "id": "clear_URL_button",
          "text": "u"
        },
        {
          "type": "Label",
          "id": "URL_field",
          "text": "f"
        },
        {
          "type": "Label",
          "id": "_leading",
          "text": "l"
        }
      ]
    },
    "u8_outer": {
      "type": "View",
      "id": "outer",
      "child": [
        {
          "include": "u8_plain",
          "id": "hero_card"
        }
      ]
    }
  },
  "screens": {
    "1_snake_and_camel_collide": {
      "type": "View",
      "id": "root",
      "child": [
        {
          "include": "u8_badges",
          "id": "hero"
        }
      ]
    },
    "2_prefix_boundary_collides": {
      "type": "View",
      "id": "root",
      "child": [
        {
          "include": "u8_card",
          "id": "hero"
        },
        {
          "include": "u8_plain",
          "id": "hero_card"
        }
      ]
    },
    "3_include_id_with_underscore": {
      "type": "View",
      "id": "root",
      "child": [
        {
          "include": "u8_plain",
          "id": "hero_card"
        }
      ]
    },
    "4_uppercase_segments": {
      "type": "View",
      "id": "root",
      "child": [
        {
          "include": "u8_plain",
          "id": "hero"
        },
        {
          "include": "u8_plain",
          "id": "info_URL"
        }
      ]
    },
    "5_nested_id_only_on_the_inner": {
      "type": "View",
      "id": "root",
      "child": [
        {
          "include": "u8_outer"
        }
      ]
    },
    "6_same_partial_twice": {
      "type": "View",
      "id": "root",
      "child": [
        {
          "include": "u8_plain",
          "id": "hero"
        },
        {
          "include": "u8_plain",
          "id": "hero"
        }
      ]
    }
  }
}
""".trimIndent()

    private val expectedJson = """
{
  "1_snake_and_camel_collide": {
    "ids": [
      "root",
      "heroBadges",
      "heroTypeBadge",
      "heroTypeBadge"
    ],
    "dataNames": [
      "heroBadgeText"
    ],
    "bindings": [
      "@{heroBadgeText}"
    ]
  },
  "2_prefix_boundary_collides": {
    "ids": [
      "root",
      "heroCard",
      "heroCardTypeBadge",
      "heroCardPlain",
      "heroCardTypeBadge",
      "heroCardTitle",
      "heroCardVerify2faForm",
      "heroCardClearUrlButton",
      "heroCardURLField",
      "heroCardLeading"
    ],
    "dataNames": [],
    "bindings": []
  },
  "3_include_id_with_underscore": {
    "ids": [
      "root",
      "heroCardPlain",
      "heroCardTypeBadge",
      "heroCardTitle",
      "heroCardVerify2faForm",
      "heroCardClearUrlButton",
      "heroCardURLField",
      "heroCardLeading"
    ],
    "dataNames": [],
    "bindings": []
  },
  "4_uppercase_segments": {
    "ids": [
      "root",
      "heroPlain",
      "heroTypeBadge",
      "heroTitle",
      "heroVerify2faForm",
      "heroClearUrlButton",
      "heroURLField",
      "heroLeading",
      "infoUrlPlain",
      "infoUrlTypeBadge",
      "infoUrlTitle",
      "infoUrlVerify2faForm",
      "infoUrlClearUrlButton",
      "infoUrlURLField",
      "infoUrlLeading"
    ],
    "dataNames": [],
    "bindings": []
  },
  "5_nested_id_only_on_the_inner": {
    "ids": [
      "root",
      "outer",
      "heroCardPlain",
      "heroCardTypeBadge",
      "heroCardTitle",
      "heroCardVerify2faForm",
      "heroCardClearUrlButton",
      "heroCardURLField",
      "heroCardLeading"
    ],
    "dataNames": [],
    "bindings": []
  },
  "6_same_partial_twice": {
    "ids": [
      "root",
      "heroPlain",
      "heroTypeBadge",
      "heroTitle",
      "heroVerify2faForm",
      "heroClearUrlButton",
      "heroURLField",
      "heroLeading",
      "heroPlain",
      "heroTypeBadge",
      "heroTitle",
      "heroVerify2faForm",
      "heroClearUrlButton",
      "heroURLField",
      "heroLeading"
    ],
    "dataNames": [],
    "bindings": []
  }
}
""".trimIndent()

    private data class Expanded(val ids: List<String>, val data: List<String>, val bindings: List<String>)

    private fun collect(node: JsonObject, out: Expanded) {
        (out.ids as MutableList).apply { if (node.has("id")) add(node.get("id").asString) }
        node.getAsJsonArray("data")?.forEach { item ->
            if (item.isJsonObject && item.asJsonObject.has("name")) {
                (out.data as MutableList).add(item.asJsonObject.get("name").asString)
            }
        }
        val text = node.get("text")
        if (text != null && text.isJsonPrimitive && text.asString.contains("@{")) {
            (out.bindings as MutableList).add(text.asString)
        }
        val child = node.get("child") ?: node.get("children")
        when {
            child == null -> {}
            child.isJsonArray -> child.asJsonArray.forEach { if (it.isJsonObject) collect(it.asJsonObject, out) }
            child.isJsonObject -> collect(child.asJsonObject, out)
        }
    }

    private fun expand(screen: String): Expanded {
        val specimens = JsonParser.parseString(specimensJson).asJsonObject
        val partials = specimens.getAsJsonObject("partials")
        IncludeExpander.layoutReader = { name -> partials.getAsJsonObject(name)?.deepCopy() }
        val screenJson = specimens.getAsJsonObject("screens").getAsJsonObject(screen).deepCopy()
        val expanded = IncludeExpander.processIncludes(screenJson)
        return Expanded(mutableListOf(), mutableListOf(), mutableListOf()).also { collect(expanded, it) }
    }

    private fun strings(array: JsonArray): List<String> = array.map { it.asString }

    private fun assertMatchesTheCodegen(screen: String) {
        val expected = JsonParser.parseString(expectedJson).asJsonObject.getAsJsonObject(screen)
        val actual = expand(screen)
        assertEquals("$screen ids", strings(expected.getAsJsonArray("ids")), actual.ids)
        assertEquals("$screen data names", strings(expected.getAsJsonArray("dataNames")), actual.data)
        assertEquals("$screen bindings", strings(expected.getAsJsonArray("bindings")), actual.bindings)
    }

    /**
     * `type_badge` and `typeBadge` in one partial land on the same id — the
     * codegen's collision, reproduced exactly; the data name and its binding
     * are prefixed by the same rule.
     */
    @Test
    fun snakeAndCamelSiblingsCollideAsInTheCodegen() = assertMatchesTheCodegen("1_snake_and_camel_collide")

    /** `hero` + `card_type_badge` and `hero_card` + `type_badge`. */
    @Test
    fun aPrefixBoundaryCollidesAsInTheCodegen() = assertMatchesTheCodegen("2_prefix_boundary_collides")

    /** The include id itself is camel-cased: `hero_card` gives `heroCard…`. */
    @Test
    fun anIncludeIdWithAnUnderscoreIsCamelCased() = assertMatchesTheCodegen("3_include_id_with_underscore")

    /** `verify_2FA_form`, `clear_URL_button`, `URL_field`, `_leading`, and an include id `info_URL`. */
    @Test
    fun uppercaseSegmentsFollowCapitalize() = assertMatchesTheCodegen("4_uppercase_segments")

    /** An include without an id passes nothing down; the inner one's id is the whole prefix. */
    @Test
    fun aNestedIncludeWithAnIdOnlyOnTheInnerOne() = assertMatchesTheCodegen("5_nested_id_only_on_the_inner")

    @Test
    fun theSamePartialTwiceGivesTheSameIdsTwice() = assertMatchesTheCodegen("6_same_partial_twice")

    // --- Where an included layout is looked up (pinned as it is) -------------

    /**
     * Always from the layouts ROOT, never from the including file's directory
     * — measured across six faces as the form that resolves consumers' nested
     * includes (measured 2026-09-25); the codegen's directory-relative lookup
     * is the one being changed. A path with `/` is one root-relative lookup.
     */
    @Test
    fun aPathIsLookedUpFromTheLayoutsRoot() {
        val listDir: (String) -> Array<String>? = { error("a path is not searched: $it") }
        assertEquals(listOf("Layouts/section/u8_inner.json"), IncludeExpander.candidatePaths("section/u8_inner", listDir))
    }

    /**
     * A bare name: the root, then every first-level directory — which the
     * SwiftUI runtime does NOT do (it looks at the root only), a difference
     * pinned here as it is and left to the owner of the path rule.
     */
    @Test
    fun aBareNameIsTheRootThenEachFirstLevelDirectory() {
        val tree = mapOf(
            "Layouts" to arrayOf("section", "other", "top.json"),
            "Layouts/section" to arrayOf("u8_inner.json"),
            "Layouts/other" to arrayOf("x.json"),
            "Layouts/top.json" to arrayOf(),
        )
        assertEquals(
            listOf("Layouts/u8_inner.json", "Layouts/section/u8_inner.json", "Layouts/other/u8_inner.json"),
            IncludeExpander.candidatePaths("u8_inner") { tree[it] },
        )
    }
}
