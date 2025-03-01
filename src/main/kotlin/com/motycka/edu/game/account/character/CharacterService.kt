package com.motycka.edu.game.account.character

import com.motycka.edu.game.account.AccountService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CharacterService(
    private val characterRepository: CharacterRepository,
    private val accountService: AccountService
) {

    fun getAllCharacters(classType: String?, name: String?): List<CharacterDTO> {
        val characters = characterRepository.getAllCharacters(classType, name)
        val currentAccountId = accountService.getCurrentAccountId()
        return characters.map { it.toDTO(currentAccountId) }
    }

    fun getCharacterById(id: Long): CharacterDTO? {
        val character = characterRepository.getCharacterById(id) ?: return null
        val currentAccountId = accountService.getCurrentAccountId()
        return character.toDTO(currentAccountId)
    }

    fun getChallengers(): List<CharacterDTO> {
        val accountId = accountService.getCurrentAccountId()
        return characterRepository.getChallengers(accountId).map { it.toDTO(accountId) }
    }

    fun getOpponents(): List<CharacterDTO> {
        val accountId = accountService.getCurrentAccountId()
        return characterRepository.getOpponents(accountId).map { it.toDTO(accountId) }
    }

    fun levelUpCharacter(id: Long, updatedCharacter: CharacterDTO): CharacterDTO {
        updatedCharacter.validate()
        val existingCharacter = characterRepository.getCharacterById(id)
            ?: throw IllegalArgumentException("Character with ID $id not found")
        val currentAccountId = accountService.getCurrentAccountId()
        if (existingCharacter.accountId != currentAccountId) {
            throw IllegalAccessException("You can only update your own characters")
        }
        val characterToUpdate = updatedCharacter.toCharacter(existingCharacter.accountId)
        characterRepository.updateCharacter(id, characterToUpdate)
        return characterToUpdate.toDTO(currentAccountId)
    }

    fun createCharacter(character: CharacterDTO): CharacterDTO {
        logger.info { "Creating character: $character" }
        character.validate()
        logger.info { "Validation passed for character: ${character.name}" }
        val accountId = accountService.getCurrentAccountId()
        logger.info { "Got account ID: $accountId" }
        val characterToSave = character.toCharacter(accountId).copy(id = null, level = 1)
        logger.info { "Character to save: $characterToSave" }
        val savedId = characterRepository.saveCharacter(characterToSave)
        logger.info { "Saved character with ID: $savedId" }
        val savedCharacter = characterToSave.copy(id = savedId)
        return savedCharacter.toDTO(accountId)
    }

    private fun Character.toDTO(currentAccountId: Long): CharacterDTO {
        return CharacterDTO(
            id = this.id,
            name = this.name,
            health = this.health,
            attack = this.attack,
            stamina = this.stamina,
            defense = this.defense,
            mana = this.mana,
            healing = this.healing,
            experience = this.experience,
            classType = this.characterClass,
            level = this.level,
            shouldLevelUp = shouldLevelUp(this.experience, this.level),
            isOwner = (this.accountId == currentAccountId)
        )
    }

    private fun CharacterDTO.toCharacter(accountId: Long): Character {
        return Character(
            id = this.id,
            accountId = accountId,
            name = this.name,
            characterClass = this.classType,
            health = this.health,
            attack = this.attack,
            experience = this.experience,
            defense = this.defense,
            stamina = this.stamina,
            healing = this.healing,
            mana = this.mana,
            level = this.level ?: 1
        )
    }

    private fun shouldLevelUp(experience: Int, currentLevel: Int): Boolean {
        val requiredExperience = currentLevel * 1000
        return experience >= requiredExperience
    }
}