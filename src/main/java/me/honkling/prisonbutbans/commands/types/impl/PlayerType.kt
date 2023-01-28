package me.honkling.prisonbutbans.commands.types.impl

import me.honkling.prisonbutbans.commands.types.Type
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PlayerType : Type<Player> {
	override fun match(input: String): Player {
		return Bukkit.getPlayer(input)!!
	}

	override fun matches(input: String): Boolean {
		return Bukkit.getPlayer(input) != null
	}

	override fun complete(input: String): List<String> {
		return Bukkit
				.getOnlinePlayers()
				.map { it.name }
				.filter { it.contains(input) }
	}
}