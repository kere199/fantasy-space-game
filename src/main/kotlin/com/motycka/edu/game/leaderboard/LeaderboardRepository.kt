package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.character.Character
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

private val logger = KotlinLogging.logger {}

@Repository
class LeaderboardRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findLeaderboardEntries(characterClass: String?): List<LeaderboardWithCharacter> {
        val sql = buildString {
            append("""
                SELECT l.character_id, l.wins, l.losses, l.draws,
                       c.id as character_id, c.account_id, c.name, c.class, c.health, c.attack,
                       c.experience, c.defense, c.stamina, c.healing, c.mana, c.level
                FROM leaderboard l
                JOIN character c ON l.character_id = c.id
            """.trimIndent())
            if (characterClass != null) {
                append(" WHERE c.class = ?")
            }
            append(" ORDER BY l.wins DESC, l.losses ASC, l.draws DESC")
        }

        val params = if (characterClass != null) arrayOf(characterClass) else emptyArray()

        logger.info { "Executing findLeaderboardEntries with SQL: $sql, params: ${params.joinToString()}" }
        return jdbcTemplate.query(sql, params) { rs, _ ->
            LeaderboardWithCharacter(
                leaderboard = Leaderboard(
                    characterId = rs.getLong("character_id"),
                    wins = rs.getInt("wins"),
                    losses = rs.getInt("losses"),
                    draws = rs.getInt("draws")
                ),
                character = Character(
                    id = rs.getLong("character_id"),
                    accountId = rs.getLong("account_id"),
                    name = rs.getString("name"),
                    characterClass = rs.getString("class"),
                    health = rs.getInt("health"),
                    attack = rs.getInt("attack"),
                    experience = rs.getInt("experience"),
                    defense = rs.getObject("defense") as? Int,
                    stamina = rs.getObject("stamina") as? Int,
                    healing = rs.getObject("healing") as? Int,
                    mana = rs.getObject("mana") as? Int,
                    level = rs.getInt("level")
                )
            )
        }
    }
}

data class LeaderboardWithCharacter(
    val leaderboard: Leaderboard,
    val character: Character
)