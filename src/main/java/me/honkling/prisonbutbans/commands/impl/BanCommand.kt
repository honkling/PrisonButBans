@file:Command("ban")

package me.honkling.prisonbutbans.commands.impl

import me.honkling.prisonbutbans.commands.lib.Command
import me.honkling.prisonbutbans.punishments.PunishmentType
import me.honkling.prisonbutbans.lib.Timespan
import me.honkling.prisonbutbans.lib.instance
import me.honkling.prisonbutbans.lib.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

fun ban(executor: Player, target: OfflinePlayer, timespan: Timespan, reason: String) {
	 launch {
		 instance.punishments.issuePunishment(
				 PunishmentType.BAN,
				 executor,
				 target,
				 reason,
				 timespan
		 )

		 Bukkit.getOnlinePlayers().forEach {
			 if (it.hasPermission("prisonbutbans.ban") || it.isOp)
				 it.sendMessage(Component
						 .text("PrisonButBans> ")
						 .color(NamedTextColor.RED)
						 .append(Component
								 .text(executor.name)
								 .color(NamedTextColor.WHITE))
						 .append(Component
								 .text(" banned ")
								 .color(NamedTextColor.GRAY))
						 .append(Component
								 .text(target.name!!)
								 .color(NamedTextColor.WHITE))
						 .append(Component
								 .text(" for ")
								 .color(NamedTextColor.GRAY))
						 .append(Component
								 .text(reason)
								 .color(NamedTextColor.WHITE))
						 .append(Component
								 .text(" [$timespan]")
								 .color(NamedTextColor.GRAY)))
		 }
	 }

	if (target.isOnline) {
		val player = target as Player
		player.kick(Component
				.text("You're banned on this server.\n")
				.color(NamedTextColor.RED)
				.append(Component
						.text(timespan.toString() + "\n")
						.color(NamedTextColor.GRAY))
				.append(Component
						.text(reason)
						.color(NamedTextColor.WHITE)))
	}
}
