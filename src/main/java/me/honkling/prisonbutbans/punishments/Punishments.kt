package me.honkling.prisonbutbans.punishments

import me.honkling.prisonbutbans.PrisonButBans
import me.honkling.prisonbutbans.lib.Timespan
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.sql.ResultSet
import java.time.Instant
import java.util.*

class Punishments(instance: PrisonButBans) {
	private val sql = instance.sql

	fun issuePunishment(
			type: PunishmentType,
			moderator: Player,
			target: OfflinePlayer,
			reason: String,
			timespan: Timespan? = null
	): Punishment {
		val id = sql
				.query("SELECT lower(hex(randomblob(24))) AS id;")
				.getString("id")

		if (timespan == null) {
			sql.execute(
				"INSERT OR REPLACE INTO punishments(id, uuid, moderator, type, reason) VALUES(?, ?, ?, ?, ?);",
					id,
					target.uniqueId.toString(),
					moderator.uniqueId.toString(),
					type.ordinal,
					reason
			)

			return getPunishment(id)!!
		}

		sql.execute(
			"INSERT OR REPLACE INTO punishments(id, uuid, moderator, type, reason, expiration) VALUES(?, ?, ?, ?, ?, ?);",
				id,
				target.uniqueId.toString(),
				moderator.uniqueId.toString(),
				type.ordinal,
				reason,
				Instant.now().epochSecond + timespan.seconds
		)

		return getPunishment(id)!!
	}

	fun getPunishments(target: OfflinePlayer): List<Punishment> {
		val punishments = mutableListOf<Punishment>()
		val rs = sql.query(
				"SELECT * FROM punishments WHERE uuid = ?",
				target.uniqueId
		)

		while (rs.next())
			punishments.add(parsePunishment(rs))

		return punishments
	}

	fun getActivePunishments(target: OfflinePlayer): List<Punishment> {
		val punishments = mutableListOf<Punishment>()
		val rs = sql.query(
				"SELECT * FROM punishments WHERE uuid = ? AND expiration >= ?",
				target.uniqueId,
				Instant.now().epochSecond
		)

		while (rs.next())
			punishments.add(parsePunishment(rs))

		return punishments
	}

	fun getPunishment(id: String): Punishment? {
		val rs = sql.query("SELECT * FROM punishments WHERE id = ?", id)

		if (!rs.next())
			return null

		return parsePunishment(rs)
	}

	fun revokePunishment(target: OfflinePlayer, type: PunishmentType) {
		val rs = sql.query(
				"SELECT id FROM punishments WHERE uuid = ? AND type = ? AND expiration >= ?",
				target.uniqueId.toString(),
				type.ordinal,
				Instant.now().epochSecond
		)

		val id = rs.getString("id")

		sql.execute("DELETE FROM punishments WHERE id = ?", id)
	}

	private fun parsePunishment(rs: ResultSet): Punishment {
		return Punishment(
				rs.getString("id"),
				UUID.fromString(rs.getString("uuid")),
				UUID.fromString(rs.getString("moderator")),
				PunishmentType.values()[rs.getInt("type")],
				rs.getString("reason"),
				rs.getLong("expiration")
		)
	}
}