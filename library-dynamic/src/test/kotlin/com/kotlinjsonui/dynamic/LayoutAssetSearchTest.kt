package com.kotlinjsonui.dynamic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * [LayoutAssetSearch] over a listing: the lookup DynamicLayoutLoader runs on
 * assets/Layouts/. AssetManager does not run on the JVM here (no Robolectric
 * in the unit-test classpath), so the loader hands the search a listing
 * function and the device test (NestedLayoutAssetTest, androidTest) loads a
 * real two-level asset through it.
 *
 * Until 2.42.0 the loader looked one directory down: a cell `kjui g collection
 * home/footer/item_cell` writes to Layouts/home/footer/item_cell.json was not
 * found in Dynamic mode (jsonui-cli ticket
 * dynamic-layout-name-drops-the-subdirectory-of-a-nested-cell).
 */
class LayoutAssetSearchTest {

    /** A tree of files (paths under Layouts/) as AssetManager.list answers it. */
    private fun listing(vararg files: String): (String) -> List<String> {
        val children = mutableMapOf<String, MutableSet<String>>()
        for (file in files) {
            val parts = file.split('/')
            for (i in parts.indices) {
                val dir = parts.take(i).joinToString("/")
                children.getOrPut(dir) { mutableSetOf() }.add(parts[i])
            }
        }
        // Unsorted on purpose: the search sorts, AssetManager's order is not a promise.
        return { dir -> children[dir]?.toList()?.reversed() ?: emptyList() }
    }

    @Test
    fun `a cell two directories deep is found by its bare name`() {
        val tree = listing("home/footer/item_cell.json", "home/header.json", "screen.json")
        assertEquals("home/footer/item_cell.json", LayoutAssetSearch.find("item_cell", tree))
    }

    @Test
    fun `one directory deep, as before`() {
        val tree = listing("my_products/product_cell.json", "screen.json")
        assertEquals("my_products/product_cell.json", LayoutAssetSearch.find("product_cell", tree))
    }

    @Test
    fun `the name as a path comes first, at the root or under a directory`() {
        val tree = listing("item_cell.json", "a/item_cell.json", "home/footer/item_cell.json")
        assertEquals("item_cell.json", LayoutAssetSearch.find("item_cell", tree))
        assertEquals("home/footer/item_cell.json", LayoutAssetSearch.find("home/footer/item_cell", tree))
    }

    @Test
    fun `a name with a directory is also matched as a trailing path`() {
        val tree = listing("home/footer/item_cell.json")
        assertEquals("home/footer/item_cell.json", LayoutAssetSearch.find("footer/item_cell", tree))
    }

    @Test
    fun `a match is on whole path components`() {
        val tree = listing("home/item_cell.json", "home/footer/big_item_cell.json")
        assertNull(LayoutAssetSearch.find("cell", tree))
        assertNull(LayoutAssetSearch.find("tem_cell", tree))
        assertNull(LayoutAssetSearch.find("ter/big_item_cell", tree))
        assertNull(LayoutAssetSearch.find("missing", tree))
    }

    // Several matches: the first of Ruby's sorted Dir.glob("<Layouts>/**/item.json")
    // — the file `kjui build` imports a cell from — measured on Ruby 3.2.2
    // (2026-09-26) for these trees:
    //   a/z, a_b, ab, b          -> a/z/item.json, a_b/item.json, ab/item.json, b/item.json
    //   a, a/z, b/c, b, (root)   -> a/item.json, a/z/item.json, b/c/item.json, b/item.json, item.json
    @Test
    fun `several matches - the first in a sorted depth-first walk, as kjui build's glob`() {
        assertEquals(
            "a/z/item.json",
            LayoutAssetSearch.find("item", listing("b/item.json", "ab/item.json", "a_b/item.json", "a/z/item.json"))
        )
        assertEquals(
            "b/c/item.json",
            LayoutAssetSearch.find("item", listing("b/item.json", "b/c/item.json"))
        )
        assertEquals(
            "a/item.json",
            LayoutAssetSearch.find("item", listing("a/z/item.json", "a/item.json", "b/c/item.json"))
        )
    }

    @Test
    fun `an empty or missing tree finds nothing`() {
        assertNull(LayoutAssetSearch.find("item_cell", listing()))
    }
}
