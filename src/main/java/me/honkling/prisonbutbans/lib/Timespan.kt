package me.honkling.prisonbutbans.lib

import kotlin.math.floor

data class Timespan(val seconds: Long) {
	override fun toString(): String {
		val components = mutableMapOf<TimespanLengths, Int>()
		var translate = seconds.toDouble()

		TimespanLengths
				.values()
				.reversedArray()
				.forEach { timespan ->
					if (translate / timespan.seconds >= 1) {
						val factor = floor(translate / timespan.seconds).toInt()
						components[timespan] = factor
						translate -= timespan.seconds * factor
					}
				}

		return components
				.entries
				.joinToString(" ") { (t, i) ->
					"$i ${t.name}${if (i == 1) "" else "S"}"
				}
	}
}
