package com.motycka.edu.game.match

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

private val logger = KotlinLogging.logger {}

@Repository
class MatchRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findAllMatches(): List<Match> {
        val sql = "SELECT * FROM match"
        logger.info { "Executing findAllMatches with SQL: $sql" }
        return jdbcTemplate.query(sql) { rs, _ ->
            Match(
                id = rs.getLong("id"),
                challengerId = rs.getLong("challenger_id"),
                opponentId = rs.getLong("opponent_id"),
                matchOutcome = rs.getString("match_outcome"),
                challengerXp = rs.getInt("challenger_xp"),
                opponentXp = rs.getInt("opponent_xp")
            )
        }
    }

    fun findRoundsByMatchId(matchId: Long): List<Round> {
        val sql = "SELECT * FROM round WHERE match_id = ?"
        logger.info { "Executing findRoundsByMatchId with SQL: $sql, matchId: $matchId" }
        return jdbcTemplate.query(sql, arrayOf(matchId)) { rs, _ ->
            Round(
                id = rs.getLong("id"),
                matchId = rs.getLong("match_id"),
                roundNumber = rs.getInt("round_number"),
                characterId = rs.getLong("character_id"),
                healthDelta = rs.getInt("health_delta"),
                staminaDelta = rs.getInt("stamina_delta"),
                manaDelta = rs.getInt("mana_delta")
            )
        }
    }

    fun saveMatch(match: Match): Long {
        val sql = """
            INSERT INTO match (challenger_id, opponent_id, match_outcome, challenger_xp, opponent_xp)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()

        val keyHolder = GeneratedKeyHolder()

        try {
            jdbcTemplate.update({ connection ->
                val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ps.setLong(1, match.challengerId)
                ps.setLong(2, match.opponentId)
                ps.setString(3, match.matchOutcome)
                ps.setInt(4, match.challengerXp)
                ps.setInt(5, match.opponentXp)
                ps
            }, keyHolder)
        } catch (e: Exception) {
            logger.error { "Failed to save match: ${e.message}" }
            throw e
        }

        return keyHolder.key?.toLong() ?: throw RuntimeException("Failed to retrieve generated match ID")
    }

    fun saveRound(round: Round): Long {
        val sql = """
            INSERT INTO round (match_id, round_number, character_id, health_delta, stamina_delta, mana_delta)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()

        val keyHolder = GeneratedKeyHolder()

        try {
            jdbcTemplate.update({ connection ->
                val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ps.setLong(1, round.matchId!!)
                ps.setInt(2, round.roundNumber)
                ps.setLong(3, round.characterId)
                ps.setInt(4, round.healthDelta)
                ps.setInt(5, round.staminaDelta)
                ps.setInt(6, round.manaDelta)
                ps
            }, keyHolder)
        } catch (e: Exception) {
            logger.error { "Failed to save round: ${e.message}" }
            throw e
        }

        return keyHolder.key?.toLong() ?: throw RuntimeException("Failed to retrieve generated round ID")
    }

    fun getLeaderboardStats(characterId: Long): LeaderboardStats? {
        val sql = "SELECT * FROM leaderboard WHERE character_id = ?"
        logger.info { "Executing getLeaderboardStats with SQL: $sql, characterId: $characterId" }
        val stats = jdbcTemplate.query(sql, arrayOf(characterId)) { rs, _ ->
            LeaderboardStats(
                characterId = rs.getLong("character_id"),
                wins = rs.getInt("wins"),
                losses = rs.getInt("losses"),
                draws = rs.getInt("draws")
            )
        }
        return stats.firstOrNull()
    }

    fun updateLeaderboard(stats: LeaderboardStats) {
        val existing = getLeaderboardStats(stats.characterId)
        val sql = if (existing != null) {
            """
                UPDATE leaderboard SET wins = ?, losses = ?, draws = ?
                WHERE character_id = ?
            """.trimIndent()
        } else {
            """
                INSERT INTO leaderboard (character_id, wins, losses, draws)
                VALUES (?, ?, ?, ?)
            """.trimIndent()
        }

        logger.info { "Updating leaderboard for character ID ${stats.characterId}: $stats" }
        jdbcTemplate.update(
            sql,
            stats.wins,
            stats.losses,
            stats.draws,
            stats.characterId
        )
    }
}

data class LeaderboardStats(
    val characterId: Long,
    val wins: Int,
    val losses: Int,
    val draws: Int
)