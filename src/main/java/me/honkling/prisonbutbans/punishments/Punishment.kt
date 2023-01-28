package me.honkling.prisonbutbans.punishments

import java.util.UUID

data class Punishment(
		val id: String,
		val uuid: UUID,
		val moderator: UUID,
		val type: PunishmentType,
		val reason: String,
		val expires: Long?
)
