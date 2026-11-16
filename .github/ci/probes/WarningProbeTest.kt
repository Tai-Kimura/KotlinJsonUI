// Positive control for the CI warning gate — copied into library-dynamic's test
// sources by ci.yml and removed again; never part of a build otherwise.
package com.kotlinjsonui.ciprobe

@Deprecated("ci warning probe")
private fun ciWarningProbeTestOld() = Unit

internal fun ciWarningProbeTest() = ciWarningProbeTestOld()
