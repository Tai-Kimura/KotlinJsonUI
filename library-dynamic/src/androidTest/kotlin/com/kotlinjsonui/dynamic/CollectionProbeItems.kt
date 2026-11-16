package com.kotlinjsonui.dynamic

import com.kotlinjsonui.data.CollectionDataSection
import com.kotlinjsonui.data.CollectionDataSource

/**
 * A one-section data source of [count] cells drawn with the layout [cell]
 * (androidTest assets, Layouts/), each carrying `title` = "row<index>" —
 * the declared way to give a Collection cells: `items` bound to it and
 * `sections: [{"cell": …}]`.
 */
internal fun probeItems(count: Int, cell: String): CollectionDataSource = CollectionDataSource(
    sections = listOf(
        CollectionDataSection(
            cells = CollectionDataSection.CellData(
                viewName = cell,
                data = List(count) { mapOf<String, Any>("title" to "row$it") }
            )
        )
    )
)
