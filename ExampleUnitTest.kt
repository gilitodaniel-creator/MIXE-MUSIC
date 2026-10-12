package com.example

import com.example.audio.EqualizerState
import com.example.audio.PROFESSIONAL_EQ_PRESETS
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun equalizerPresets_haveValidBandsAndRanges() {
    assertTrue(PROFESSIONAL_EQ_PRESETS.isNotEmpty())
    for (preset in PROFESSIONAL_EQ_PRESETS) {
      assertEquals(5, preset.bandLevels.size)
      assertTrue(preset.bassBoost in 0.0f..1.0f)
      assertTrue(preset.surround3D in 0.0f..1.0f)
      for (band in preset.bandLevels) {
        assertTrue(band in -12.0f..12.0f)
      }
    }
  }

  @Test
  fun defaultEqualizerState_isActiveAndBalanced() {
    val defaultState = EqualizerState()
    assertTrue(defaultState.isEnabled)
    assertEquals(5, defaultState.bandLevels.size)
    assertNotNull(defaultState.activePresetName)
  }
}
