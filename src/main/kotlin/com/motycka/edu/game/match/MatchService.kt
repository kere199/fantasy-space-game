package com.motycka.edu.game.match



import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
class MatchService(private val matchRepository: MatchRepository) {

    fun getAllMatches(): List<MatchDTO> {
        return matchRepository.getAllMatches()
    }

    @Transactional
    fun createMatch(challengerId: Long, opponentId: Long): MatchDTO {
        val challenger = matchRepository.getCharacterById(challengerId)
            ?: throw IllegalArgumentException("Challenger not found")
        val opponent = matchRepository.getCharacterById(opponentId)
            ?: throw IllegalArgumentException("Opponent not found")

        val rounds = mutableListOf<RoundDTO>()
        var challengerHealth = challenger.health
        var opponentHealth = opponent.health
        var roundNumber = 1

        while (challengerHealth > 0 && opponentHealth > 0) {
            val challengerDamage = Random.nextInt(5, challenger.attack)
            val opponentDamage = Random.nextInt(5, opponent.attack)

            challengerHealth -= opponentDamage
            opponentHealth -= challengerDamage

            rounds.add(RoundDTO(roundNumber, challengerId, -opponentDamage, -5, 0))
            rounds.add(RoundDTO(roundNumber, opponentId, -challengerDamage, 0, -5))

            roundNumber++
        }

        val matchOutcome = when {
            challengerHealth > 0 -> "CHALLENGER_WON"
            opponentHealth > 0 -> "OPPONENT_WON"
            else -> "DRAW"
        }

        val matchDTO = MatchDTO(
            id = 0,
            challenger = challenger,
            opponent = opponent,
            rounds = rounds,
            matchOutcome = matchOutcome
        )

        matchRepository.saveMatch(matchDTO)
        return matchDTO
    }
}
