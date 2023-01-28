package me.honkling.prisonbutbans.lib

import me.honkling.prisonbutbans.commands.impl.punishments
import me.honkling.prisonbutbans.punishments.Punishment
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.time.Instant
import kotlin.math.min

fun launch(runnable: Runnable) {
	Bukkit.getScheduler().runTaskAsynchronously(instance, runnable)
}

fun openPunishments(executor: Player, target: OfflinePlayer) {
	val punishments = instance.punishments.getPunishments(target).reversed()
	val book = Book
			.builder()
			.author(Component.empty())
			.title(Component.empty())

	val pages = mutableListOf<Component>()
	var i = 0

	while (i < punishments.size) {
		val thisPage = punishments.subList(i, min(punishments.size, i + 14))
		var page = Component.empty()

		thisPage.forEach { punishment ->
			val title = punishment.reason.substring(0, min(punishment.reason.length, 19)) +
					if (punishment.reason.length > 18) "..." else ""

			val active = (punishment.expires ?: 0) >= Instant.now().epochSecond

			page = page.append(Component
					.text("$title\n")
					.color(if (active) NamedTextColor.RED else NamedTextColor.BLACK)
					.clickEvent(ClickEvent.clickEvent(
							ClickEvent.Action.RUN_COMMAND,
							"/puns ${punishment.id}"))
					.hoverEvent(HoverEvent.showText(Component
							.text("Click to view punishment details")
							.color(NamedTextColor.RED))))
		}

		pages.add(page)
		i += 14
	}

	executor.openBook(book.pages(pages))
}

fun viewPunishment(executor: Player, punishment: Punishment) {
	val moderator = Bukkit.getOfflinePlayer(punishment.moderator)

	var firstPage = Component
			.text("Type\n")
			.color(NamedTextColor.RED)
			.append(Component
					.text("${punishment.type.name}\n\n")
					.color(NamedTextColor.BLACK))
			.append(Component.text("Issued by\n"))
			.append(Component
					.text("${moderator.name}\n\n")
					.color(NamedTextColor.BLACK))
			.append(Component.text("Reason\n"))
			.append(Component
					.text("${punishment.reason}\n\n")
					.color(NamedTextColor.BLACK))

	if (punishment.expires != null) {
		firstPage = firstPage
				.append(Component.text("Expiration\n"))
				.append(Component
						.text(Instant.ofEpochSecond(punishment.expires).toString())
						.color(NamedTextColor.BLACK))
	}

	val book = Book
			.builder()
			.author(Component.empty())
			.title(Component.empty())
			.pages(listOf(firstPage))

	executor.openBook(book)
}