package me.honkling.prisonbutbans.listeners

import io.papermc.paper.event.player.AsyncChatEvent
import me.honkling.prisonbutbans.lib.Timespan
import me.honkling.prisonbutbans.lib.instance
import me.honkling.prisonbutbans.lib.launch
import me.honkling.prisonbutbans.punishments.PunishmentType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Instant

object AsyncChatListener : Listener {
	@EventHandler
	fun onAsyncChat(e: AsyncChatEvent) {
		launch {
			val player = e.player
			val punishments = instance.punishments.getActivePunishments(player)
			val activeMute = punishments.find { it.type == PunishmentType.MUTE } ?: return@launch
			val difference = activeMute.expires!! - Instant.now().epochSecond

			e.isCancelled = true
			player.sendMessage(Component
					.text("PrisonButBans> ")
					.color(NamedTextColor.RED)
					.append(Component
							.text("You are muted for ")
							.color(NamedTextColor.WHITE))
					.append(Component
							.text(activeMute.reason)
							.color(NamedTextColor.WHITE))
					.append(Component
							.text(" [${Timespan(difference)}]")
							.color(NamedTextColor.GRAY)))
		}
	}
}