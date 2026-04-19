package com.kotlinjsonui.data

import org.junit.Assert.*
import org.junit.Test

class CollectionDataTest {

    @Test
    fun `CollectionDataSection CellData holds correct values`() {
        val cellData = CollectionDataSection.CellData(
            viewName = "ProductView",
            data = listOf(
                mapOf("id" to 1, "name" to "Product 1"),
                mapOf("id" to 2, "name" to "Product 2")
            )
        )

        assertEquals("ProductView", cellData.viewName)
        assertEquals(2, cellData.data.size)
        assertEquals(1, cellData.data[0]["id"])
        assertEquals("Product 2", cellData.data[1]["name"])
    }

    @Test
    fun `CollectionDataSection HeaderFooterData holds correct values`() {
        val headerData = CollectionDataSection.HeaderFooterData(
            viewName = "HeaderView",
            data = mapOf("title" to "Section Header", "count" to 10)
        )

        assertEquals("HeaderView", headerData.viewName)
        assertEquals("Section Header", headerData.data["title"])
        assertEquals(10, headerData.data["count"])
    }

    @Test
    fun `CollectionDataSection with all fields`() {
        val section = CollectionDataSection(
            header = CollectionDataSection.HeaderFooterData(
                viewName = "HeaderView",
                data = mapOf("title" to "Header")
            ),
            footer = CollectionDataSection.HeaderFooterData(
                viewName = "FooterView",
                data = mapOf("text" to "Footer")
            ),
            cells = CollectionDataSection.CellData(
                viewName = "ItemView",
                data = listOf(mapOf("name" to "Item"))
            ),
            columns = 2
        )

        assertNotNull(section.header)
        assertNotNull(section.footer)
        assertNotNull(section.cells)
        assertEquals(2, section.columns)
        assertEquals("HeaderView", section.header?.viewName)
        assertEquals("FooterView", section.footer?.viewName)
        assertEquals("ItemView", section.cells?.viewName)
    }

    @Test
    fun `CollectionDataSection with default values`() {
        val section = CollectionDataSection()

        assertNull(section.header)
        assertNull(section.footer)
        assertNull(section.cells)
        assertNull(section.columns)
    }

    @Test
    fun `CollectionDataSection equals and hashCode`() {
        val section1 = CollectionDataSection(
            cells = CollectionDataSection.CellData(
                viewName = "ItemView",
                data = listOf(mapOf("id" to 1))
            ),
            columns = 2
        )
        val section2 = CollectionDataSection(
            cells = CollectionDataSection.CellData(
                viewName = "ItemView",
                data = listOf(mapOf("id" to 1))
            ),
            columns = 2
        )
        val section3 = CollectionDataSection(
            cells = CollectionDataSection.CellData(
                viewName = "DifferentView",
                data = listOf(mapOf("id" to 1))
            ),
            columns = 2
        )

        assertEquals(section1, section2)
        assertEquals(section1.hashCode(), section2.hashCode())
        assertNotEquals(section1, section3)
    }

    @Test
    fun `CollectionDataSection copy works correctly`() {
        val original = CollectionDataSection(
            cells = CollectionDataSection.CellData(
                viewName = "ItemView",
                data = listOf(mapOf("id" to 1))
            ),
            columns = 2
        )
        val copied = original.copy(columns = 3)

        assertEquals(2, original.columns)
        assertEquals(3, copied.columns)
        assertEquals(original.cells, copied.cells)
    }

    @Test
    fun `CollectionDataSource with empty sections`() {
        val dataSource = CollectionDataSource()

        assertTrue(dataSource.sections.isEmpty())
    }

    @Test
    fun `CollectionDataSource getCellData returns matching data`() {
        val dataSource = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(
                    cells = CollectionDataSection.CellData(
                        viewName = "ProductView",
                        data = listOf(
                            mapOf("id" to 1, "name" to "Product 1"),
                            mapOf("id" to 2, "name" to "Product 2")
                        )
                    )
                ),
                CollectionDataSection(
                    cells = CollectionDataSection.CellData(
                        viewName = "CategoryView",
                        data = listOf(
                            mapOf("id" to 1, "name" to "Category 1")
                        )
                    )
                ),
                CollectionDataSection(
                    cells = CollectionDataSection.CellData(
                        viewName = "ProductView",
                        data = listOf(
                            mapOf("id" to 3, "name" to "Product 3")
                        )
                    )
                )
            )
        )

        val productData = dataSource.getCellData("ProductView")
        assertEquals(3, productData.size)
        assertEquals("Product 1", productData[0]["name"])
        assertEquals("Product 2", productData[1]["name"])
        assertEquals("Product 3", productData[2]["name"])

        val categoryData = dataSource.getCellData("CategoryView")
        assertEquals(1, categoryData.size)
        assertEquals("Category 1", categoryData[0]["name"])
    }

    @Test
    fun `CollectionDataSource getCellData returns empty for non-existent class`() {
        val dataSource = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(
                    cells = CollectionDataSection.CellData(
                        viewName = "ProductView",
                        data = listOf(mapOf("id" to 1))
                    )
                )
            )
        )

        val result = dataSource.getCellData("NonExistentView")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `CollectionDataSource getCellData handles null cells`() {
        val dataSource = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(
                    header = CollectionDataSection.HeaderFooterData(
                        viewName = "HeaderView",
                        data = mapOf("title" to "Header")
                    )
                    // cells is null
                ),
                CollectionDataSection(
                    cells = CollectionDataSection.CellData(
                        viewName = "ItemView",
                        data = listOf(mapOf("id" to 1))
                    )
                )
            )
        )

        val result = dataSource.getCellData("ItemView")
        assertEquals(1, result.size)
    }

    @Test
    fun `CollectionDataSource equals and hashCode`() {
        val dataSource1 = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(
                    cells = CollectionDataSection.CellData(
                        viewName = "ItemView",
                        data = listOf(mapOf("id" to 1))
                    )
                )
            )
        )
        val dataSource2 = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(
                    cells = CollectionDataSection.CellData(
                        viewName = "ItemView",
                        data = listOf(mapOf("id" to 1))
                    )
                )
            )
        )

        assertEquals(dataSource1, dataSource2)
        assertEquals(dataSource1.hashCode(), dataSource2.hashCode())
    }

    @Test
    fun `CollectionDataSource copy works correctly`() {
        val original = CollectionDataSource(
            sections = listOf(
                CollectionDataSection(
                    cells = CollectionDataSection.CellData(
                        viewName = "ItemView",
                        data = listOf(mapOf("id" to 1))
                    )
                )
            )
        )
        val copied = original.copy(sections = emptyList())

        assertEquals(1, original.sections.size)
        assertTrue(copied.sections.isEmpty())
    }

    @Test
    fun `CellData with empty data list`() {
        val cellData = CollectionDataSection.CellData(
            viewName = "EmptyView",
            data = emptyList()
        )

        assertEquals("EmptyView", cellData.viewName)
        assertTrue(cellData.data.isEmpty())
    }

    @Test
    fun `HeaderFooterData with empty data map`() {
        val headerData = CollectionDataSection.HeaderFooterData(
            viewName = "EmptyHeaderView",
            data = emptyMap()
        )

        assertEquals("EmptyHeaderView", headerData.viewName)
        assertTrue(headerData.data.isEmpty())
    }

    @Test
    fun `CollectionDataSection toString contains expected content`() {
        val section = CollectionDataSection(
            cells = CollectionDataSection.CellData(
                viewName = "TestView",
                data = listOf(mapOf("key" to "value"))
            ),
            columns = 3
        )
        val str = section.toString()

        assertTrue(str.contains("TestView"))
        assertTrue(str.contains("columns=3"))
    }
}
