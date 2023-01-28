@file:Command("kick")

package me.honkling.prisonbutbans.commands.impl

import me.honkling.prisonbutbans.commands.lib.Command
import me.honkling.prisonbutbans.lib.instance
import me.honkling.prisonbutbans.lib.launch
import me.honkling.prisonbutbans.punishments.PunishmentType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun kick(executor: Player, target: Player, reason: String) {
	launch {
		instance.punishments.issuePunishment(
				PunishmentType.KICK,
				executor,
				target,
				reason
		)

		Bukkit.getOnlinePlayers().forEach {
			if (it.hasPermission("prisonbutbans.kick") || it.isOp)
				it.sendMessage(Component
						.text("PrisonButBans> ")
						.color(NamedTextColor.RED)
						.append(Component
								.text(executor.name)
								.color(NamedTextColor.WHITE))
						.append(Component
								.text(" kicked ")
								.color(NamedTextColor.GRAY))
						.append(Component
								.text(target.name!!)
								.color(NamedTextColor.WHITE))
						.append(Component
								.text(" for ")
								.color(NamedTextColor.GRAY))
						.append(Component
								.text(reason)
								.color(NamedTextColor.WHITE)))
		}
	}

	target.kick(Component
			.text("You were kicked from this server.\n")
			.color(NamedTextColor.RED)
			.append(Component
					.text(reason)
					.color(NamedTextColor.WHITE)))
}