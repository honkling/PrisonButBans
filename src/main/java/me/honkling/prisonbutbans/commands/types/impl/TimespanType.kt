package me.honkling.prisonbutbans.commands.types.impl

import me.honkling.prisonbutbans.commands.types.Type
import me.honkling.prisonbutbans.lib.Timespan

object TimespanType : Type<Timespan> {
	override fun match(input: String): Timespan {
		val matches = Regex("(\\d+)(s|m|h|d|w|mo|y)").findAll(input)
		var seconds = 0L

		matches.forEach { match ->
			val (value, type) = match.destructured

			seconds += value.toInt() * when (type) {
				"s" -> 1
				"m" -> 60
				"h" -> 3600
				"d" -> 86400
				"w" -> 604800
				"mo" -> 2419200
				"y" -> 29030400
				else -> 0
			}
		}

		return Timespan(seconds)
	}

	override fun matches(input: String): Boolean {
		return input.matches(Regex("(\\d+(s|m|h|d|w|mo|y))+"))
	}

	override fun complete(input: String): List<String> {
		if (!input.matches(Regex("(\\d+(s|m|h|d|w|mo|y))*\\d+(s|m|h|d|w|mo|y)?")))
			return emptyList()

		val matches = Regex("(\\d+)(s|mo|h|d|w|m|y)?").findAll(input)
		val match = matches.lastOrNull()?.value ?: input

		val type = match.replace(Regex("\\d"), "")
		val pre = input.substring(0, input.length - type.length)

		println("Completey ${listOf(pre, type)}")

		return listOf("s", "m", "h", "d", "w", "mo", "y")
				.filter { it.contains(type) }
				.map { pre + it }
	}

}

// 3[m] (m, mo) (0, 2 - 1)
// 3[mo] (mo) (0, 3 - 2)
// 3d4[m] (m, mo) (4, 4 - 1)
// 3d4mm (invalid)