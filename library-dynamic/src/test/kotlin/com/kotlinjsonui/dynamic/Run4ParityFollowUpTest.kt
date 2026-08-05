package com.kotlinjsonui.dynamic

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.components.DynamicNetworkImageComponent
import com.kotlinjsonui.dynamic.components.DynamicRadioComponent
import com.kotlinjsonui.dynamic.generated.RadioAttributes
import com.kotlinjsonui.dynamic.helpers.EffectStyleTable
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The four dynamic-side gaps run 4 measured as android parity deviations.
 *
 * All four have the same shape: C landed the codegen half in this wave and the
 * dynamic renderer did not follow, so one layout draws two ways. They are
 * pinned here against the CODEGEN's tables rather than against my reading of
 * them — the failure mode this whole wave exists to stop is two implementations
 * that each decided for themselves what an attribute means.
 */
class Run4ParityFollowUpTest {

    private fun json(s: String): JsonObject = Gson().fromJson(s, JsonObject::class.java)
    private fun radio(s: String): RadioAttributes =
        RadioAttributes.parse(TypedAttrs.toAttrMap(json(s)))

    // ── effectStyle: 6 deviations ────────────────────────────────────

    @Test
    fun everyMaterialCarriesTheCodegensScrimAndRadius() {
        // effect_style_helper.rb SCRIM/BLUR_DP, value for value.
        val expected = listOf(
            Triple("Light", Color.White.copy(alpha = 0.4f), 8),
            Triple("ExtraLight", Color.White.copy(alpha = 0.6f), 4),
            Triple("Dark", Color.Black.copy(alpha = 0.4f), 12),
            Triple("UltraThin", Color.White.copy(alpha = 0.3f), 4),
            Triple("Thin", Color.White.copy(alpha = 0.5f), 8),
            Triple("Regular", Color.White.copy(alpha = 0.7f), 12),
            Triple("Thick", Color.White.copy(alpha = 0.85f), 16),
            Triple("Chrome", Color.White.copy(alpha = 0.95f), 20),
            Triple("Prominent", Color.White.copy(alpha = 0.9f), 12)
        )
        for ((name, scrim, dp) in expected) {
            assertEquals("scrim for $name", scrim, EffectStyleTable.scrim(name))
            assertEquals("blur dp for $name", dp, EffectStyleTable.blurDp(name))
        }
    }

    @Test
    fun theSwiftUIAliasesNormaliseOntoTheirMaterial() {
        // The five `system*Material` spellings the codegen table carries.
        assertEquals(EffectStyleTable.scrim("UltraThin"), EffectStyleTable.scrim("systemUltraThinMaterial"))
        assertEquals(EffectStyleTable.scrim("Thin"), EffectStyleTable.scrim("systemThinMaterial"))
        assertEquals(EffectStyleTable.scrim("Regular"), EffectStyleTable.scrim("systemMaterial"))
        assertEquals(EffectStyleTable.scrim("Thick"), EffectStyleTable.scrim("systemThickMaterial"))
        assertEquals(EffectStyleTable.scrim("Chrome"), EffectStyleTable.scrim("systemChromeMaterial"))
    }

    @Test
    fun theNineMaterialsAreAllDistinguishable() {
        // If two materials drew the same scrim, a value-vs-value fixture could
        // not tell them apart — the collapse `distribution` was ruled over.
        val scrims = listOf(
            "Light", "ExtraLight", "Dark", "UltraThin", "Thin",
            "Regular", "Thick", "Chrome", "Prominent"
        ).map { EffectStyleTable.scrim(it) }
        assertEquals("two materials share a scrim", scrims.size, scrims.toSet().size)
    }

    @Test
    fun anUnknownMaterialFallsBackToRegularAndAnAbsentOneToNothing() {
        assertEquals(EffectStyleTable.scrim("Regular"), EffectStyleTable.scrim("nonsense"))
        assertNull(EffectStyleTable.scrim(null))
        assertNull(EffectStyleTable.scrim("   "))
        assertNull(EffectStyleTable.blurDp(null))
    }

    // ── heightWeight: 1 deviation (distance 61, the run's largest) ────

    @Test
    fun heightWeightIsTheVerticalSpellingAndWinsInAColumn() {
        val j = json("""{"type":"View","heightWeight":2,"weight":1}""")
        assertEquals(2f, ModifierBuilder.getWeight(j, emptyMap(), "Column")!!, 0f)
    }

    @Test
    fun heightWeightDoesNotDisturbARow() {
        // Only the Column branch reads it, exactly as modifier_builder.rb:113
        // does — so no existing horizontal layout moves.
        val j = json("""{"type":"View","heightWeight":2,"weight":1}""")
        assertEquals(1f, ModifierBuilder.getWeight(j, emptyMap(), "Row")!!, 0f)
        assertEquals(1f, ModifierBuilder.getWeight(j, emptyMap(), null)!!, 0f)
    }

    @Test
    fun heightWeightAloneStillDistributesInAColumn() {
        // The defect as measured: declaring it changed the layout's shape and
        // then distributed nothing.
        val j = json("""{"type":"View","heightWeight":1}""")
        assertEquals(1f, ModifierBuilder.getWeight(j, emptyMap(), "Column")!!, 0f)
        assertNull(ModifierBuilder.getWeight(j, emptyMap(), "Row"))
    }

    // ── Radio.spacing: 2 deviations ──────────────────────────────────

    @Test
    fun radioSpacingReachesTheGapItDeclares() {
        assertEquals(16f, DynamicRadioComponent.spacingDp(radio("""{"type":"Radio","spacing":16}"""), emptyMap()), 0f)
    }

    @Test
    fun radioSpacingDefaultsToTheCodegensEight() {
        // The value that made the drop invisible: both paths were right at 8,
        // so only a fixture declaring something else could see it.
        assertEquals(8f, DynamicRadioComponent.spacingDp(radio("""{"type":"Radio"}"""), emptyMap()), 0f)
    }

    @Test
    fun radioSpacingResolvesABinding() {
        val a = radio("""{"type":"Radio","spacing":"@{gap}"}""")
        assertEquals(24f, DynamicRadioComponent.spacingDp(a, mapOf("gap" to 24)), 0f)
        // Unresolved falls to the declared default, not to the expression.
        assertEquals(8f, DynamicRadioComponent.spacingDp(a, emptyMap()), 0f)
    }

    // ── NetworkImage state slots: 2 deviations (49 #19) ───────────────

    @Test
    fun eachSlotTakesOnlyTheImagesItsOwnStateDeclares() {
        val errorImage = 1; val defaultImage = 2; val placeholder = 3
        assertEquals(errorImage, DynamicNetworkImageComponent.errorSlot(errorImage, defaultImage))
        assertEquals(defaultImage, DynamicNetworkImageComponent.errorSlot(null, defaultImage))
        assertEquals(defaultImage, DynamicNetworkImageComponent.fallbackSlot(defaultImage))
        // The tails that were the defect: placeholder must not be summoned
        // into either state, and errorImage must not stand in for no-src.
        assertNotEquals(placeholder, DynamicNetworkImageComponent.errorSlot(null, null))
        assertNull(DynamicNetworkImageComponent.errorSlot(null, null))
        assertNull(DynamicNetworkImageComponent.fallbackSlot(null))
    }

    @Test
    fun aNoSrcViewWithNoDefaultImageShowsNothing() {
        // `NetworkImage/placeholder__static` declares only `placeholder` and
        // carries no url, so it is a no-src view. Showing the placeholder
        // there was the leak; showing nothing is the canon.
        assertNull(DynamicNetworkImageComponent.fallbackSlot(null))
    }

    @Test
    fun theLoadingSlotKeepsItsThreeSpellingsInOrder() {
        assertEquals(1, DynamicNetworkImageComponent.loadingSlot(1, 2, 3))
        assertEquals(2, DynamicNetworkImageComponent.loadingSlot(null, 2, 3))
        assertEquals(3, DynamicNetworkImageComponent.loadingSlot(null, null, 3))
        assertNull(DynamicNetworkImageComponent.loadingSlot(null, null, null))
    }
}
