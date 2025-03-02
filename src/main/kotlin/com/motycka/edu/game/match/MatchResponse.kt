package com.motycka.edu.game.match

data class MatchResponse(
    val id: Long,
    val challenger: CharacterSummary,
    val opponent: CharacterSummary,
    val rounds: List<RoundResponse>,
    val matchOutcome: String
)