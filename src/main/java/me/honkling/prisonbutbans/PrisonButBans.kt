package me.honkling.prisonbutbans

import me.honkling.prisonbutbans.commands.CommandManager
import me.honkling.prisonbutbans.lib.SQL
import me.honkling.prisonbutbans.listeners.AsyncChatListener
import me.honkling.prisonbutbans.listeners.PlayerLoginListener
import me.honkling.prisonbutbans.punishments.Punishments
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class PrisonButBans : JavaPlugin() {
	val commandManager = CommandManager(this)
	val sql = SQL(this, dataFolder.mkdir())
	val punishments = Punishments(this)

	override fun onEnable() {
		commandManager.registerCommands()

		val pluginManager = Bukkit.getPluginManager()
		pluginManager.registerEvents(PlayerLoginListener, this)
		pluginManager.registerEvents(AsyncChatListener, this)
	}

	override fun onDisable() {
		sql.conn.close()
		logger.info("bye bye")
	}
}