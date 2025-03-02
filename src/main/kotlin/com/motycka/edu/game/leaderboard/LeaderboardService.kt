package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.character.toResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class LeaderboardService(
    private val leaderboardRepository: LeaderboardRepository,
    private val accountService: AccountService
) {

    fun getLeaderboard(characterClass: String?): List<LeaderboardEntry> {
        logger.info { "Fetching leaderboard with class filter: $characterClass" }
        val currentAccountId = accountService.getCurrentAccountId()
        val entries = leaderboardRepository.findLeaderboardEntries(characterClass)

        return entries.mapIndexed { index, entry ->
            val characterResponse = entry.character.toResponse(currentAccountId)
            LeaderboardEntry(
                position = index + 1,
                character = characterResponse,
                wins = entry.leaderboard.wins,
                losses = entry.leaderboard.losses,
                draws = entry.leaderboard.draws
            )
        }
    }
}

//fun Character.toResponse(currentAccountId: Long): com.motycka.edu.game.account.rest.CharacterResponse {
//    return com.motycka.edu.game.account.rest.CharacterResponse(
//        id = this.id,
//        name = this.name,
//        health = this.health,
//        attackPower = this.attack,
//        stamina = this.stamina,
//        defensePower = this.defense,
//        mana = this.mana,
//        healingPower = this.healing,
//        experience = this.experience,
//        characterClass = this.characterClass,
//        level = this.level,
//        shouldLevelUp = shouldLevelUp(this.experience, this.level),
//        isOwner = (this.accountId == currentAccountId)
//    )
//}

fun shouldLevelUp(experience: Int, currentLevel: Int): Boolean {
    val requiredExperience = currentLevel * 1000
    return experience >= requiredExperience
}