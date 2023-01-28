package me.honkling.prisonbutbans.commands.types.impl

import me.honkling.prisonbutbans.commands.types.Type

object StringType : Type<String> {
	override fun match(input: String): String {
		return input
	}

	override fun matches(input: String): Boolean {
		return true
	}

	override fun complete(input: String): List<String> {
		return emptyList()
	}
}