package me.honkling.prisonbutbans.commands

import io.github.classgraph.ClassGraph
import me.honkling.prisonbutbans.PrisonButBans
import me.honkling.prisonbutbans.commands.lib.Command
import me.honkling.prisonbutbans.commands.lib.CommandCompletion
import me.honkling.prisonbutbans.commands.lib.Parameter
import me.honkling.prisonbutbans.commands.lib.Subcommand
import me.honkling.prisonbutbans.commands.types.Type
import me.honkling.prisonbutbans.commands.types.impl.OfflinePlayerType
import me.honkling.prisonbutbans.commands.types.impl.PlayerType
import me.honkling.prisonbutbans.commands.types.impl.StringType
import me.honkling.prisonbutbans.commands.types.impl.TimespanType
import me.honkling.prisonbutbans.lib.Timespan
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.entity.Player

class CommandManager(private val instance: PrisonButBans) {
	private val completer = CommandCompletion(this)
	private val commandMap = hookCommandMap()
	val types = mutableMapOf<Class<*>, Type<*>>()
	val commands = mutableMapOf<String, Subcommand>()

	init {
		types[String::class.java] = StringType
		types[Player::class.java] = PlayerType
		types[OfflinePlayer::class.java] = OfflinePlayerType
		types[Timespan::class.java] = TimespanType
	}

	fun registerCommands(pkg: String = "${CommandManager::class.java.packageName}.impl") {
		val scanner = ClassGraph()
				.enableAllInfo()
				.acceptPackages(pkg)
				.scan()

		scanner.getClassesWithAnnotation(Command::class.java).forEach { clazz ->
			val anno = clazz.annotationInfo.find { it.name.startsWith("me.honkling.prisonbutbans.commands.lib") }!!

			val name = (anno.parameterValues["name"].value as String).lowercase()
			val aliases = (anno.parameterValues["aliases"].value as Array<String>).map { it.lowercase() }
			val subcommands = mutableMapOf<String, List<Parameter>>()

			clazz.methodInfo.forEach { method ->
				subcommands[method.name] = method
						.parameterInfo
						.toList()
						.subList(1, method.parameterInfo.size)
						.map { param ->
							val type = Class.forName(param
									.typeSignatureOrTypeDescriptor
									.toString()
									.replace("int", "kotlin.Integer")
									.replace("boolean", "kotlin.Boolean")
									.replace("char", "kotlin.Character"))

							val isRequired = !param.annotationInfo.map { it.name }.contains("org.jetbrains.annotations.Nullable")

							Pair(type, isRequired)
						}
			}

			val command = createCommand(name, aliases)

			command.setExecutor { sender, _, _, args ->
				if (args.isEmpty() || !subcommands.containsKey(args[0].lowercase())) {
					if (!validateArguments(subcommands[name]!!, args))
						return@setExecutor false

					val method = clazz
							.getDeclaredMethodInfo(name)[0]
							.loadClassAndGetMethod()

					method.invoke(null, sender, *(parseArguments(subcommands[name]!!, args).toTypedArray()))
					return@setExecutor true
				}

				val subcommand = subcommands[args[0].lowercase()]!!
				val rest = args.toList().subList(1, args.size).toTypedArray()

				if (!validateArguments(subcommand, rest))
					return@setExecutor false

				val method = clazz
						.getDeclaredMethodInfo(args[0])[0]
						.loadClassAndGetMethod()

				method.invoke(null, sender, parseArguments(subcommand, rest))
				return@setExecutor true
			}

			commands[name] = subcommands
			commandMap.register(instance.name, command)
		}
	}

	private fun validateArguments(guide: List<Pair<Class<*>, Boolean>>, args: Array<String>): Boolean {
		guide.forEachIndexed { index, guideArg ->
			if (args.size - 1 < index && guideArg.second)
				return@validateArguments false

			val type = types[guideArg.first]

			if (type != null && !type.matches(args[index]))
				return@validateArguments true
		}

		return true
	}

	private fun parseArguments(guide: List<Parameter>, args: Array<String>): List<Any> {
		val parsed = mutableListOf<Any>()

		guide.forEachIndexed { index, guideArg ->
			val type = types[guideArg.first]!!
			parsed.add(type.match(args[index])!!)
		}

		return parsed
	}

	private fun createCommand(name: String, aliases: List<String>): PluginCommand {
		val constructor = PluginCommand::class.java.declaredConstructors[0]
		constructor.isAccessible = true
		val command = constructor.newInstance(name, instance) as PluginCommand

		command.description = "A PrisonButBans command."
		command.usage = ChatColor.translateAlternateColorCodes(
				'&',
				"&cPrisonButBans> &7Invalid usage. Please check /$name help.")
		command.aliases = aliases
		command.permission = "prisonbutbans.$name"
		command.tabCompleter = completer
		command.permissionMessage(Component
				.text("PrisonButBans> ")
				.color(NamedTextColor.RED)
				.append(Component
						.text("You do not have permission to execute this command.")
						.color(NamedTextColor.GRAY)))

		return command
	}

	private fun hookCommandMap(): SimpleCommandMap {
		val server = Bukkit.getServer()
		val getCommandMap = server.javaClass.getDeclaredMethod("getCommandMap")
		return getCommandMap.invoke(server) as SimpleCommandMap
	}
}