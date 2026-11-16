package com.kotlinjsonui.dynamic

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.FileNotFoundException
import java.io.InputStream
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Ruling U8 (include-child-ids-are-spelled-differently-on-each-platform): an
 * element inside an include that carries an id has ONE id on every face, and
 * it is the one the codegen writes. These arms hold the Dynamic
 * [IncludeExpander] to the codegen's answers.
 *
 * WHERE THE EXPECTED VALUES COME FROM — both are machine output:
 * - the snake→camel table is jsonui-cli shared/core/camel_case_vectors.json,
 *   copied byte-identical into src/test/resources (CI's vendored-attr-guard
 *   compares the copy with the file at the pinned jsonui-cli ref). jsonui-cli
 *   wrote it by running sjui_tools and kjui_tools' include_expander.rb
 *   (`to_camel_case`, `combine_with_prefix`), which agree on every row.
 * - [expectedJson]: the ids, data names and bindings that the codegen's
 *   `process_includes` produces for [specimensJson], run under ruby 3.2.2
 *   against jsonui-cli f45a0cfc — sjui_tools and kjui_tools gave
 *   byte-identical output. Nested partials sit at the layouts root, so the
 *   codegen's directory-relative lookup and this runtime's root lookup read
 *   the same files (the lookup rule has its own arms, at the end).
 * A transcription is not a compile: if the codegen's answer moves, these do
 * not follow by themselves — regenerate them from the Ruby.
 */
class IncludeExpanderIdSpellingTest {

    @After
    fun resetSeam() {
        IncludeExpander.layoutReader = null
    }

    // --- The two functions, row by row (the snake→camel table) ---------------------------

    /** camel = to_camel_case(input); combined = combine_with_prefix(prefix, input). */
    private val camelCaseVectors: JsonObject by lazy {
        val stream = requireNotNull(
            javaClass.classLoader?.getResourceAsStream("camel_case_vectors.json")
        ) { "camel_case_vectors.json missing from test resources" }
        stream.reader(Charsets.UTF_8).use { JsonParser.parseReader(it).asJsonObject }
    }

    private val camelCaseRows: List<JsonObject>
        get() = camelCaseVectors.getAsJsonArray("cases").map { it.asJsonObject }
            .also { assertTrue("camel_case_vectors.json has no cases", it.isNotEmpty()) }

    @Test
    fun toCamelCaseAnswersAsTheCodegenDoes() {
        for (row in camelCaseRows) {
            val input = row.get("input").asString
            assertEquals(input, row.get("camel").asString, IncludeExpander.toCamelCase(input))
        }
    }

    @Test
    fun combineWithPrefixAnswersAsTheCodegenDoes() {
        val prefix = camelCaseVectors.get("prefix").asString
        for (row in camelCaseRows) {
            val input = row.get("input").asString
            assertEquals(input, row.get("combined").asString, IncludeExpander.combineWithPrefix(prefix, input))
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

    // --- Where an included layout is looked up: the layouts root only --------

    /** A bare name and a path are each ONE root-relative lookup. */
    @Test
    fun aNameIsLookedUpAtTheLayoutsRootOnly() {
        assertEquals(listOf("Layouts/u8_inner.json"), IncludeExpander.candidatePaths("u8_inner"))
        assertEquals(listOf("Layouts/section/u8_inner.json"), IncludeExpander.candidatePaths("section/u8_inner"))
    }

    /**
     * Through the real read loop, over a tree where the file exists ONLY in a
     * first-level directory: the bare name finds nothing (this used to fall
     * back to that directory — the one reader of these layouts that did),
     * while the root-relative path to the same file finds it. The second
     * half is the arm's own control: the file is readable, so the first
     * half's null is the lookup rule and not a missing file.
     */
    @Test
    fun aFileOnlyInAFirstLevelDirectoryIsNotFoundByItsBareName() {
        val files = mapOf("Layouts/section/u8_inner.json" to """{"type":"Label","id":"inner_label"}""")
        val opened = mutableListOf<String>()
        val open: (String) -> InputStream = { path ->
            opened += path
            files[path]?.byteInputStream() ?: throw FileNotFoundException(path)
        }
        assertNull(IncludeExpander.readLayout("u8_inner", open))
        assertEquals(listOf("Layouts/u8_inner.json"), opened)
        assertEquals("inner_label", IncludeExpander.readLayout("section/u8_inner", open)?.get("id")?.asString)
    }
}
