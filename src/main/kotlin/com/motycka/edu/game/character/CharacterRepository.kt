package com.motycka.edu.game.character

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

private val logger = KotlinLogging.logger {}

@Repository
class CharacterRepository(private val jdbcTemplate: JdbcTemplate) {

    fun getAllCharacters(classType: String?, name: String?): List<Character> {
        val sql = buildString {
            append("SELECT * FROM character ")
            if (classType != null || name != null) {
                append("WHERE ")
                val conditions = mutableListOf<String>()
                if (classType != null) conditions.add("class = ?")
                if (name != null) conditions.add("name LIKE ?")
                append(conditions.joinToString(" AND "))
            }
        }

        val params = listOfNotNull(
            classType,
            if (name != null) "%$name%" else null
        ).toTypedArray()

        logger.info { "Executing getAllCharacters with SQL: $sql, params: ${params.joinToString()}" }
        return jdbcTemplate.query(sql, params) { rs, _ ->
            Character(
                id = rs.getLong("id"),
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
        }
    }

    fun getCharacterById(id: Long): Character? {
        val sql = "SELECT * FROM character WHERE id = ?"
        logger.info { "Executing getCharacterById with SQL: $sql, id: $id" }
        val characters = jdbcTemplate.query(sql, arrayOf(id)) { rs, _ ->
            Character(
                id = rs.getLong("id"),
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
        }
        return characters.firstOrNull()
    }

    fun getChallengers(accountId: Long): List<Character> {
        val sql = "SELECT * FROM character WHERE account_id = ?"
        logger.info { "Executing getChallengers with SQL: $sql, accountId: $accountId" }
        return jdbcTemplate.query(sql, arrayOf(accountId)) { rs, _ ->
            Character(
                id = rs.getLong("id"),
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
        }
    }

    fun getOpponents(accountId: Long): List<Character> {
        val sql = "SELECT * FROM character WHERE account_id != ?"
        logger.info { "Executing getOpponents with SQL: $sql, accountId: $accountId" }
        return jdbcTemplate.query(sql, arrayOf(accountId)) { rs, _ ->
            Character(
                id = rs.getLong("id"),
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
        }
    }

    fun updateCharacter(id: Long, character: Character): Int {
        val sql = """
            UPDATE character SET 
            name = ?, health = ?, attack = ?, mana = ?, healing = ?, 
            stamina = ?, defense = ?, experience = ?, level = ? 
            WHERE id = ?
        """.trimIndent()

        logger.info { "Executing updateCharacter with SQL: $sql, id: $id" }
        return jdbcTemplate.update(
            sql,
            character.name, character.health, character.attack, character.mana, character.healing,
            character.stamina, character.defense, character.experience, character.level, id
        )
    }

    fun saveCharacter(character: Character): Long {
        val sql = """
            INSERT INTO character (account_id, name, class, health, attack, stamina, defense, mana, healing, experience, level) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        val keyHolder = GeneratedKeyHolder()

        try {
            jdbcTemplate.update({ connection ->
                val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ps.setLong(1, character.accountId)
                ps.setString(2, character.name)
                ps.setString(3, character.characterClass)
                ps.setInt(4, character.health)
                ps.setInt(5, character.attack)
                ps.setObject(6, character.stamina)
                ps.setObject(7, character.defense)
                ps.setObject(8, character.mana)
                ps.setObject(9, character.healing)
                ps.setInt(10, character.experience)
                ps.setInt(11, character.level)
                ps
            }, keyHolder)
        } catch (e: Exception) {
            logger.error { "Failed to save character: ${e.message}" }
            throw e
        }

        return keyHolder.key?.toLong() ?: throw RuntimeException("Failed to retrieve generated ID")
    }
}