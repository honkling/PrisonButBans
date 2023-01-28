package me.honkling.prisonbutbans.commands.lib

@Target(AnnotationTarget.FILE)
annotation class Command(
		val name: String,
		vararg val aliases: String
)
