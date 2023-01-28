package me.honkling.prisonbutbans.lib

enum class TimespanLengths(val seconds: Long) {
	SECOND(1),
	MINUTE(60),
	HOUR(3600),
	DAY(86400),
	WEEK(604800),
	MONTH(2419200),
	YEAR(29030400)
}