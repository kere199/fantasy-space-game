package com.motycka.edu.game.match



import com.motycka.edu.game.account.character.CharacterDTO
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class MatchRepository(private val jdbcTemplate: JdbcTemplate) {

    fun getAllMatches(): List<MatchDTO> {
        val sql = "SELECT * FROM match"
        return jdbcTemplate.query(sql) { rs, _ ->
            MatchDTO(
                id = rs.getLong("id"),
                challenger = getCharacterById(rs.getLong("challenger_id"))!!,
                opponent = getCharacterById(rs.getLong("opponent_id"))!!,
                rounds = getRoundsByMatchId(rs.getLong("id")),
                matchOutcome = rs.getString("match_outcome")
            )
        }
    }

    fun getCharacterById(characterId: Long): CharacterDTO? {
        val sql = "SELECT * FROM character WHERE id = ?"
        return jdbcTemplate.queryForObject(sql, arrayOf(characterId)) { rs, _ ->
            CharacterDTO(
                id = rs.getLong("id"),
//                accountId = rs.getLong("account_id"),
                name = rs.getString("name"),
                health = rs.getInt("health"),
                attack = rs.getInt("attack"),
                mana = rs.getObject("mana") as? Int,
                healing = rs.getObject("healing") as? Int,
                stamina = rs.getObject("stamina") as Int,
                defense = rs.getObject("defense") as Int,
                experience = rs.getInt("experience"),
                classType = rs.getString("class")
            )
        }
    }

    fun getRoundsByMatchId(matchId: Long): List<RoundDTO> {
        val sql = "SELECT * FROM round WHERE match_id = ?"
        return jdbcTemplate.query(sql, arrayOf(matchId)) { rs, _ ->
            RoundDTO(
                round = rs.getInt("round_number"),
                characterId = rs.getLong("character_id"),
                healthDelta = rs.getInt("health_delta"),
                staminaDelta = rs.getInt("stamina_delta"),
                manaDelta = rs.getInt("mana_delta")
            )
        }
    }

    fun saveMatch(match: MatchDTO) {
        val sql = """
            INSERT INTO match (challenger_id, opponent_id, match_outcome, challenger_xp, opponent_xp) 
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
        """.trimIndent()

        val matchId = jdbcTemplate.queryForObject(
            sql, Long::class.java, match.challenger.id, match.opponent.id, match.matchOutcome, 100, 100
        ) ?: throw IllegalStateException("Failed to insert match")

        match.rounds.forEach { round ->
            val roundSql = """
                INSERT INTO round (match_id, round_number, character_id, health_delta, stamina_delta, mana_delta) 
                VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()
            jdbcTemplate.update(
                roundSql, matchId, round.round, round.characterId, round.healthDelta, round.staminaDelta, round.manaDelta
            )
        }
    }
}
