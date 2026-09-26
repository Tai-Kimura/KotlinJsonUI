package com.kotlinjsonui.dynamic

/**
 * Where a layout named `name` is, in a tree of layout files — the lookup
 * [DynamicLayoutLoader] runs over `assets/Layouts/`, kept free of Android so
 * the JVM tests can run it over a listing.
 *
 * `list(dir)` returns the entries of `dir`, a path relative to the layouts
 * root ("" for the root itself), or an empty list when `dir` is a file or
 * does not exist — what `AssetManager.list` answers. The answer is the file's
 * path relative to the root, "home/footer/item_cell.json", or null.
 *
 * The rules, those of SwiftJsonUI's `JSONLayoutLoader.loadJSON(named:)` and of
 * `kjui build`, which picks the file a Collection's cell is imported from
 * (`ComposeBuilder#find_cell_subdirectory`: `Dir.glob("<Layouts>/**/<name>.json").first`):
 *  1. the name as a path: `<name>.json` at that place;
 *  2. otherwise any file at ANY depth whose path is `<name>.json` or ends with
 *     `/<name>.json` — a match on whole path components, so "footer/item_cell"
 *     finds "home/footer/item_cell.json" and "cell" does not find
 *     "item_cell.json";
 *  3. several matches: the first in a depth-first walk that takes each
 *     directory's entries in sorted order, files and directories alike — the
 *     order of Ruby's sorted `Dir.glob` (measured on 3.2.2: a/item, a/z/item,
 *     b/c/item, b/item) and of SwiftJsonUI's directory enumerator on a
 *     name-sorted listing.
 *
 * Until KotlinJsonUI 2.42.0 step 2 looked ONE directory down, and only for a
 * name without "/": a cell `kjui g collection home/footer/item_cell` writes to
 * `Layouts/home/footer/item_cell.json` was found by the build and by
 * SwiftJsonUI and not by Dynamic mode here (read, jsonui-cli ticket
 * dynamic-layout-name-drops-the-subdirectory-of-a-nested-cell).
 */
internal object LayoutAssetSearch {

    fun find(name: String, list: (String) -> List<String>): String? {
        val target = "$name.json"
        if (isFile(target, list)) return target
        return walk("", "/$target", target, list)
    }

    private fun walk(dir: String, suffix: String, target: String, list: (String) -> List<String>): String? {
        for (entry in list(dir).sorted()) {
            val path = if (dir.isEmpty()) entry else "$dir/$entry"
            if (list(path).isNotEmpty()) {
                walk(path, suffix, target, list)?.let { return it }
            } else if (path == target || path.endsWith(suffix)) {
                return path
            }
        }
        return null
    }

    /** `path` is a file of the tree: its directory lists it, and it lists nothing. */
    private fun isFile(path: String, list: (String) -> List<String>): Boolean {
        val dir = path.substringBeforeLast('/', "")
        val base = path.substringAfterLast('/')
        return base in list(dir) && list(path).isEmpty()
    }
}
