package com.motycka.edu.game.match
import com.motycka.edu.game.account.character.CharacterDTO

data class MatchDTO(
    val id: Long,
    val challenger: CharacterDTO,
    val opponent: CharacterDTO,
    val rounds: List<RoundDTO>,
    val matchOutcome: String
)
