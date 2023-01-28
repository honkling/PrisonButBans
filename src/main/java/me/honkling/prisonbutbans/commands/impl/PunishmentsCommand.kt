@file:Command("punishments", "puns")

package me.honkling.prisonbutbans.commands.impl

import me.honkling.prisonbutbans.commands.lib.Command
import me.honkling.prisonbutbans.lib.instance
import me.honkling.prisonbutbans.lib.openPunishments
import me.honkling.prisonbutbans.lib.viewPunishment
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun punishments(executor: Player, target: String?) {
	if (target?.length == 48) {
		// Viewing a specific punishment.

		val punishment = instance.punishments.getPunishment(target)

		if (punishment == null) {
			executor.sendMessage(Component
					.text("PrisonButBans> ")
					.color(NamedTextColor.RED)
					.append(Component
							.text("That punishment doesn't exist.")
							.color(NamedTextColor.GRAY)))
			return
		}

		executor.sendMessage(punishment.toString())

		viewPunishment(executor, punishment)
		return
	}

	val player = Bukkit.getOfflinePlayer(target ?: executor.name)

	if (
			player != executor &&
			!executor.hasPermission("prisonbutbans.punishments.other")
	) {
		executor.sendMessage(Component
				.text("PrisonButBans> ")
				.color(NamedTextColor.RED)
				.append(Component
						.text("You cannot view the punishments of others.")
						.color(NamedTextColor.GRAY)))
		return
	}

	openPunishments(executor, player)
}