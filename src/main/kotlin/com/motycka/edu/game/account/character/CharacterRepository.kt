package com.motycka.edu.game.account.character

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class CharacterRepository(private val jdbcTemplate: JdbcTemplate) {

    fun getAllCharacters(classType: String?, name: String?): List<CharacterDTO> {
        val sql = buildString {
            append("SELECT * FROM character ")
            if (classType != null || name != null) {
                append("WHERE ")
                if (classType != null) append("class = ? ")
                if (name != null) {
                    if (classType != null) append("AND ")
                    append("name LIKE ? ")
                }
            }
        }

        return jdbcTemplate.query(sql, listOfNotNull(classType, name).toTypedArray()) { rs, _ ->
            CharacterDTO(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                health = rs.getInt("health"),
                attack = rs.getInt("attack"),
                mana = rs.getObject("mana") as? Int,
                healing = rs.getObject("healing") as? Int,
                stamina = rs.getObject("stamina") as? Int,
                defense = rs.getObject("defense") as? Int,
                experience = rs.getInt("experience"),
                classType = rs.getString("class")
            )
        }
    }

    fun getCharacterById(id: Long): CharacterDTO? {
        val sql = "SELECT * FROM character WHERE id = ?"
        return jdbcTemplate.queryForObject(sql, arrayOf(id)) { rs, _ ->
            CharacterDTO(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                health = rs.getInt("health"),
                attack = rs.getInt("attack"),
                mana = rs.getObject("mana") as? Int,
                healing = rs.getObject("healing") as? Int,
                stamina = rs.getObject("stamina") as? Int,
                defense = rs.getObject("defense") as? Int,
                experience = rs.getInt("experience"),
                classType = rs.getString("class")
            )
        }
    }

    fun getChallengers(accountId: Long): List<CharacterDTO> {
        val sql = "SELECT * FROM character WHERE account_id = ?"
        return jdbcTemplate.query(sql, arrayOf(accountId)) { rs, _ ->
            CharacterDTO(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                health = rs.getInt("health"),
                attack = rs.getInt("attack"),
                mana = rs.getObject("mana") as? Int,
                healing = rs.getObject("healing") as? Int,
                stamina = rs.getObject("stamina") as? Int,
                defense = rs.getObject("defense") as? Int,
                experience = rs.getInt("experience"),
                classType = rs.getString("class")
            )
        }
    }

    fun getOpponents(accountId: Long): List<CharacterDTO> {
        val sql = "SELECT * FROM character WHERE account_id != ?"
        return jdbcTemplate.query(sql, arrayOf(accountId)) { rs, _ ->
            CharacterDTO(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                health = rs.getInt("health"),
                attack = rs.getInt("attack"),
                mana = rs.getObject("mana") as? Int,
                healing = rs.getObject("healing") as? Int,
                stamina = rs.getObject("stamina") as? Int,
                defense = rs.getObject("defense") as? Int,
                experience = rs.getInt("experience"),
                classType = rs.getString("class")
            )
        }
    }

    fun updateCharacter(id: Long, character: CharacterDTO): Int {
        val sql = """
            UPDATE character SET 
            name = ?, health = ?, attack = ?, mana = ?, healing = ?, 
            stamina = ?, defense = ?, experience = ? WHERE id = ?
        """.trimIndent()

        return jdbcTemplate.update(
            sql,
            character.name, character.health, character.attack, character.mana, character.healing,
            character.stamina, character.defense, character.experience, id
        )
    }

    fun saveCharacter(character: CharacterDTO, accountId: Long) {
        val sql = """
            INSERT INTO character (account_id, name, health, attack, stamina, defense, mana, healing, class) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            accountId,
            character.name,
            character.health,
            character.attack,
            character.stamina,
            character.defense,
            character.mana,
            character.healing,
            character.classType
        )
    }
}
