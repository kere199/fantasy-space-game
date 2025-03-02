package com.motycka.edu.game.character

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.account.rest.CharacterRegistrationRequest
import com.motycka.edu.game.account.rest.CharacterResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CharacterService(
    private val characterRepository: CharacterRepository,
    private val accountService: AccountService
) {

    fun getAllCharacters(classType: String?, name: String?): List<CharacterResponse> {
        logger.info { "Fetching all characters with classType: $classType, name: $name" }
        val characters = characterRepository.getAllCharacters(classType, name)
        val currentAccountId = accountService.getCurrentAccountId()
        return characters.map { it.toResponse(currentAccountId) }
    }

    fun getCharacterById(id: Long): CharacterResponse? {
        logger.info { "Fetching character with ID: $id" }
        val character = characterRepository.getCharacterById(id) ?: return null
        val currentAccountId = accountService.getCurrentAccountId()
        return character.toResponse(currentAccountId)
    }

    fun getChallengers(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId()
        logger.info { "Fetching challengers for account ID: $accountId" }
        return characterRepository.getChallengers(accountId).map { it.toResponse(accountId) }
    }

    fun getOpponents(): List<CharacterResponse> {
        val accountId = accountService.getCurrentAccountId()
        logger.info { "Fetching opponents for account ID: $accountId" }
        return characterRepository.getOpponents(accountId).map { it.toResponse(accountId) }
    }

    fun levelUpCharacter(id: Long, updatedCharacter: CharacterUpdateRequest): CharacterResponse {
        logger.info { "Updating character with ID: $id, data: $updatedCharacter" }
        updatedCharacter.validate()
        val existingCharacter = characterRepository.getCharacterById(id)
            ?: throw IllegalArgumentException("Character with ID $id not found")
        val currentAccountId = accountService.getCurrentAccountId()
        if (existingCharacter.accountId != currentAccountId) {
            throw IllegalAccessException("You can only update your own characters")
        }
        val characterToUpdate = updatedCharacter.toCharacter(existingCharacter.accountId, existingCharacter.experience)
        characterRepository.updateCharacter(id, characterToUpdate)
        return characterToUpdate.toResponse(currentAccountId)
    }

    fun createCharacter(request: CharacterRegistrationRequest): CharacterResponse {
        logger.info { "Creating character: $request" }
        request.validate()
        logger.info { "Validation passed for character: ${request.name}" }
        val accountId = accountService.getCurrentAccountId()
        logger.info { "Got account ID: $accountId" }
        val characterToSave = request.toCharacter(accountId).copy(id = null, level = 1, experience = 0)
        logger.info { "Character to save: $characterToSave" }
        val savedId = characterRepository.saveCharacter(characterToSave)
        logger.info { "Saved character with ID: $savedId" }
        val savedCharacter = characterToSave.copy(id = savedId)
        return savedCharacter.toResponse(accountId)
    }
}

fun Character.toResponse(currentAccountId: Long): CharacterResponse {
    return CharacterResponse(
        id = this.id,
        name = this.name,
        health = this.health,
        attackPower = this.attack,
        stamina = this.stamina,
        defensePower = this.defense,
        mana = this.mana,
        healingPower = this.healing,
        experience = this.experience,
        characterClass = this.characterClass,
        level = this.level,
        shouldLevelUp = shouldLevelUp(this.experience, this.level),
        isOwner = (this.accountId == currentAccountId)
    )
}

fun CharacterRegistrationRequest.toCharacter(accountId: Long): Character {
    return Character(
        id = null,
        accountId = accountId,
        name = this.name,
        characterClass = this.characterClass,
        health = this.health,
        attack = this.attackPower,
        experience = 0,
        defense = this.defensePower,
        stamina = this.stamina,
        healing = this.healingPower,
        mana = this.mana,
        level = 1
    )
}

fun CharacterUpdateRequest.toCharacter(accountId: Long, existingExperience: Int): Character {
    return Character(
        id = null, // ID will be set during update
        accountId = accountId,
        name = this.name,
        characterClass = "", // Not updated in level-up
        health = this.health,
        attack = this.attackPower,
        experience = existingExperience,
        defense = this.defensePower,
        stamina = this.stamina,
        healing = this.healingPower,
        mana = this.mana,
        level = 0 // Will be set based on experience
    )
}

fun shouldLevelUp(experience: Int, currentLevel: Int): Boolean {
    val requiredExperience = currentLevel * 1000
    return experience >= requiredExperience
}