package me.honkling.prisonbutbans.listeners

import me.honkling.prisonbutbans.lib.Timespan
import me.honkling.prisonbutbans.lib.instance
import me.honkling.prisonbutbans.lib.launch
import me.honkling.prisonbutbans.punishments.PunishmentType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import java.time.Instant

object PlayerLoginListener : Listener {

	@EventHandler
	fun onPlayerLogin(e: PlayerLoginEvent) {
		val player = e.player
		val punishments = instance.punishments.getActivePunishments(player)
		val activeBan = punishments.find { it.type == PunishmentType.BAN } ?: return
		val difference = activeBan.expires!! - Instant.now().epochSecond

		player.kick(Component
				.text("You're banned on this server.\n")
				.color(NamedTextColor.RED)
				.append(Component
						.text(Timespan(difference).toString() + "\n")
						.color(NamedTextColor.GRAY))
				.append(Component
						.text(activeBan.reason)
						.color(NamedTextColor.WHITE)))
	}
}