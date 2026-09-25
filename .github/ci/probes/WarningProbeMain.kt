// Positive control for the CI warning gate — copied into sample-app's main
// sources by ci.yml and removed again; never part of a build otherwise.
package com.kotlinjsonui.ciprobe

@Deprecated("ci warning probe")
private fun ciWarningProbeMainOld() = Unit

internal fun ciWarningProbeMain() = ciWarningProbeMainOld()
