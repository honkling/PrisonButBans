package me.honkling.prisonbutbans.lib

import me.honkling.prisonbutbans.PrisonButBans
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class SQL(instance: PrisonButBans, _created: Boolean) {
	val conn = DriverManager.getConnection("jdbc:sqlite:${instance.dataFolder}/punishments.db")

	init {
		execute("""
			CREATE TABLE IF NOT EXISTS punishments(
				id TEXT NOT NULL PRIMARY KEY DEFAULT (lower(hex(randomblob(24)))),
				uuid TEXT NOT NULL,
				moderator TEXT NOT NULL,
				type INTEGER NOT NULL,
				reason STRING NOT NULL,
				expiration INTEGER
			);
		""".trimIndent())
	}

	fun execute(sql: String, vararg values: Any) {
		prepare(sql, *values).execute()
	}

	fun query(sql: String, vararg values: Any): ResultSet {
		return prepare(sql, *values).executeQuery()
	}

	fun prepare(sql: String, vararg values: Any): PreparedStatement {
		val stmt = conn.prepareStatement(sql)

		values.forEachIndexed { i, v ->
			stmt.setObject(i + 1, v)
		}

		return stmt
	}
}