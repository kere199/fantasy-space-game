package com.motycka.edu.game.account.character

import com.motycka.edu.game.account.AccountService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class CharacterService(
    private val characterRepository: CharacterRepository,
    private val accountService: AccountService
) {

    fun getAllCharacters(classType: String?, name: String?): List<CharacterDTO> {
        return characterRepository.getAllCharacters(classType, name)
    }

    fun getCharacterById(id: Long): CharacterDTO? {
        return characterRepository.getCharacterById(id)
    }

    fun getChallengers(): List<CharacterDTO> {
        val userId = accountService.getCurrentAccountId()
        return characterRepository.getChallengers(userId)
    }

    fun getOpponents(): List<CharacterDTO> {
        val userId = accountService.getCurrentAccountId()
        return characterRepository.getOpponents(userId)
    }

    @Transactional
    fun levelUpCharacter(id: Long, updatedCharacter: CharacterDTO): CharacterDTO {
        val userId = accountService.getCurrentAccountId()
        val character = characterRepository.getCharacterById(id)
            ?: throw IllegalArgumentException("Character not found")

        if (characterRepository.getChallengers(userId).none { it.id == id }) {
            throw IllegalAccessException("You can only update your own characters")
        }

        val leveledUpCharacter = character.copy(
            name = updatedCharacter.name,
            health = updatedCharacter.health,
            attack = updatedCharacter.attack,
            mana = updatedCharacter.mana,
            healing = updatedCharacter.healing,
            stamina = updatedCharacter.stamina,
            defense = updatedCharacter.defense,
            experience = character.experience + 100  // Level-up effect
        )

        characterRepository.updateCharacter(id, leveledUpCharacter)
        return leveledUpCharacter
    }
}
