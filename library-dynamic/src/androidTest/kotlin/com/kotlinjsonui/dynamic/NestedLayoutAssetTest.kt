package com.kotlinjsonui.dynamic

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A layout two directories under assets/Layouts/ loads by its bare name — the
 * name a Dynamic Collection turns a cell class into
 * (DynamicCollectionComponent.renderCellViewInner). The asset is
 * Layouts/nested_probe/level_two/nested_probe_cell.json, which the search
 * before 2.42.0 (one directory down) did not reach. The rules themselves are
 * pinned on the JVM by LayoutAssetSearchTest; this runs them over the real
 * AssetManager.
 */
@RunWith(AndroidJUnit4::class)
class NestedLayoutAssetTest {

    @Before
    fun fresh() {
        DynamicLayoutLoader.init(InstrumentationRegistry.getInstrumentation().targetContext)
        DynamicLayoutLoader.clearCache()
    }

    @Test
    fun aLayoutTwoDirectoriesDeepLoadsByItsBareName() {
        assertTrue(DynamicLayoutLoader.layoutExists("nested_probe_cell"))
        val layout = DynamicLayoutLoader.loadLayout("nested_probe_cell")
        assertNotNull(layout)
        assertEquals("nested_probe_cell_root", layout!!.get("id").asString)
    }

    @Test
    fun aTrailingPathFindsItToo() {
        val layout = DynamicLayoutLoader.loadLayout("level_two/nested_probe_cell")
        assertNotNull(layout)
        assertEquals("nested_probe_cell_root", layout!!.get("id").asString)
    }
}
