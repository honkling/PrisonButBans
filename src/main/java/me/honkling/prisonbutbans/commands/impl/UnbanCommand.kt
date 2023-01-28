@file:Command("unban")

package me.honkling.prisonbutbans.commands.impl

import me.honkling.prisonbutbans.commands.lib.Command
import me.honkling.prisonbutbans.lib.instance
import me.honkling.prisonbutbans.lib.launch
import me.honkling.prisonbutbans.punishments.PunishmentType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

fun unban(executor: Player, target: OfflinePlayer) {
	launch {
		instance.punishments.revokePunishment(target, PunishmentType.BAN)

		Bukkit.getOnlinePlayers().forEach {
			if (it.hasPermission("prisonbutbans.unban") || it.isOp)
				it.sendMessage(Component
						.text("PrisonButBans> ")
						.color(NamedTextColor.RED)
						.append(Component
								.text(executor.name)
								.color(NamedTextColor.WHITE))
						.append(Component
								.text(" unbanned ")
								.color(NamedTextColor.GRAY))
						.append(Component
								.text(target.name!!)
								.color(NamedTextColor.WHITE)))
		}

	}
}