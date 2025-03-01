package com.motycka.edu.game.account.character

import com.motycka.edu.game.account.AccountService
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
        val accountId = accountService.getCurrentAccountId()
        return characterRepository.getChallengers(accountId)
    }

    fun getOpponents(): List<CharacterDTO> {
        val accountId = accountService.getCurrentAccountId()
        return characterRepository.getOpponents(accountId)
    }

    fun levelUpCharacter(id: Long, updatedCharacter: CharacterDTO): CharacterDTO {
        characterRepository.updateCharacter(id, updatedCharacter)
        return updatedCharacter
    }

    fun createCharacter(character: CharacterDTO) {
        val accountId = accountService.getCurrentAccountId()
        characterRepository.saveCharacter(character, accountId)
    }
}
