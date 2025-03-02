package com.motycka.edu.game.match

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.character.Character
import com.motycka.edu.game.character.CharacterRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

@Service
class MatchService(
    private val matchRepository: MatchRepository,
    private val characterRepository: CharacterRepository,
    private val accountService: AccountService
) {

    fun getAllMatches(): List<MatchResponse> {
        logger.info { "Fetching all matches" }
        val matches = matchRepository.findAllMatches()
        return matches.map { match ->
            val rounds = matchRepository.findRoundsByMatchId(match.id!!)
            val challenger = characterRepository.getCharacterById(match.challengerId)!!
            val opponent = characterRepository.getCharacterById(match.opponentId)!!
            MatchResponse(
                id = match.id,
                challenger = challenger.toSummary(match.challengerXp),
                opponent = opponent.toSummary(match.opponentXp),
                rounds = rounds.map { it.toResponse() },
                matchOutcome = match.matchOutcome
            )
        }
    }

    fun createMatch(request: CreateMatchRequest): MatchResponse {
        logger.info { "Creating match: $request" }
        request.validate()

        // Validate characters exist
        val challenger = characterRepository.getCharacterById(request.challengerId)
            ?: throw IllegalArgumentException("Challenger character with ID ${request.challengerId} not found")
        val opponent = characterRepository.getCharacterById(request.opponentId)
            ?: throw IllegalArgumentException("Opponent character with ID ${request.opponentId} not found")

        // Validate user owns the challenger
        val currentAccountId = accountService.getCurrentAccountId()
        if (challenger.accountId != currentAccountId) {
            throw IllegalAccessException("You can only challenge with your own character")
        }

        // Simulate the match
        val (matchOutcome, challengerXp, opponentXp, rounds) = simulateMatch(challenger, opponent, request.rounds)

        // Save the match
        val match = Match(
            id = null,
            challengerId = challenger.id!!,
            opponentId = opponent.id!!,
            matchOutcome = matchOutcome,
            challengerXp = challengerXp,
            opponentXp = opponentXp
        )
        val matchId = matchRepository.saveMatch(match)
        logger.info { "Saved match with ID: $matchId" }

        // Save rounds
        rounds.forEach { round ->
            matchRepository.saveRound(round.copy(matchId = matchId))
        }

        // Update character experience
        val updatedChallenger = challenger.copy(experience = challenger.experience + challengerXp)
        val updatedOpponent = opponent.copy(experience = opponent.experience + opponentXp)
        characterRepository.updateCharacter(challenger.id, updatedChallenger)
        characterRepository.updateCharacter(opponent.id, updatedOpponent)

        // Update leaderboard
        updateLeaderboard(challenger.id, opponent.id, matchOutcome)

        return MatchResponse(
            id = matchId,
            challenger = updatedChallenger.toSummary(challengerXp),
            opponent = updatedOpponent.toSummary(opponentXp),
            rounds = rounds.map { it.toResponse() },
            matchOutcome = matchOutcome
        )
    }

    private fun simulateMatch(challenger: Character, opponent: Character, numRounds: Int): MatchSimulationResult {
        logger.info { "Simulating match between challenger ID ${challenger.id} and opponent ID ${opponent.id} for $numRounds rounds" }
        val rounds = mutableListOf<Round>()
        var challengerHealth = challenger.health
        var opponentHealth = opponent.health
        var challengerStamina = challenger.stamina ?: 0
        var opponentMana = opponent.mana ?: 0

        for (roundNum in 1..numRounds) {
            // Challenger attacks opponent (Warrior)
            val challengerAttack = Random.nextInt(5, 15)
            val opponentDefense = opponent.defense ?: 0
            val opponentHealthDelta = -(challengerAttack - opponentDefense).coerceAtLeast(0)
            opponentHealth += opponentHealthDelta
            val challengerStaminaDelta = -Random.nextInt(2, 5)
            challengerStamina = (challengerStamina + challengerStaminaDelta).coerceAtLeast(0)

            // Opponent attacks challenger (Sorcerer)
            val opponentAttack = Random.nextInt(5, 15)
            val challengerDefense = challenger.defense ?: 0
            val challengerHealthDelta = -(opponentAttack - challengerDefense).coerceAtLeast(0)
            challengerHealth += challengerHealthDelta
            val opponentManaDelta = -Random.nextInt(2, 5)
            opponentMana = (opponentMana + opponentManaDelta).coerceAtLeast(0)

            // Record rounds
            rounds.add(Round(
                id = null,
                matchId = null,
                roundNumber = roundNum,
                characterId = challenger.id!!,
                healthDelta = challengerHealthDelta,
                staminaDelta = challengerStaminaDelta,
                manaDelta = 0
            ))
            rounds.add(Round(
                id = null,
                matchId = null,
                roundNumber = roundNum,
                characterId = opponent.id!!,
                healthDelta = opponentHealthDelta,
                staminaDelta = 0,
                manaDelta = opponentManaDelta
            ))

            // Check if match should end early
            if (challengerHealth <= 0 || opponentHealth <= 0) break
        }

        // Determine match outcome
        val matchOutcome = when {
            challengerHealth <= 0 && opponentHealth <= 0 -> "DRAW"
            challengerHealth <= 0 -> "OPPONENT_WON"
            opponentHealth <= 0 -> "CHALLENGER_WON"
            challengerHealth > opponentHealth -> "CHALLENGER_WON"
            opponentHealth > challengerHealth -> "OPPONENT_WON"
            else -> "DRAW"
        }

        // Calculate experience gained
        val challengerXp = when (matchOutcome) {
            "CHALLENGER_WON" -> 100
            "DRAW" -> 50
            else -> 20
        }
        val opponentXp = when (matchOutcome) {
            "OPPONENT_WON" -> 100
            "DRAW" -> 50
            else -> 20
        }

        return MatchSimulationResult(matchOutcome, challengerXp, opponentXp, rounds)
    }

    private fun updateLeaderboard(challengerId: Long, opponentId: Long, matchOutcome: String) {
        logger.info { "Updating leaderboard for challenger ID $challengerId and opponent ID $opponentId with outcome $matchOutcome" }
        val challengerStats = matchRepository.getLeaderboardStats(challengerId)
            ?: LeaderboardStats(characterId = challengerId, wins = 0, losses = 0, draws = 0)
        val opponentStats = matchRepository.getLeaderboardStats(opponentId)
            ?: LeaderboardStats(characterId = opponentId, wins = 0, losses = 0, draws = 0)

        val updatedChallengerStats = when (matchOutcome) {
            "CHALLENGER_WON" -> challengerStats.copy(wins = challengerStats.wins + 1)
            "OPPONENT_WON" -> challengerStats.copy(losses = challengerStats.losses + 1)
            else -> challengerStats.copy(draws = challengerStats.draws + 1)
        }
        val updatedOpponentStats = when (matchOutcome) {
            "OPPONENT_WON" -> opponentStats.copy(wins = opponentStats.wins + 1)
            "CHALLENGER_WON" -> opponentStats.copy(losses = opponentStats.losses + 1)
            else -> opponentStats.copy(draws = opponentStats.draws + 1)
        }

        matchRepository.updateLeaderboard(updatedChallengerStats)
        matchRepository.updateLeaderboard(updatedOpponentStats)
    }
}

data class MatchSimulationResult(
    val matchOutcome: String,
    val challengerXp: Int,
    val opponentXp: Int,
    val rounds: List<Round>
)