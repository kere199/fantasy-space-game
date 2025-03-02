package com.motycka.edu.game.match

data class RoundResponse(
    val round: Int,
    val characterId: Long,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int
)

fun Round.toResponse(): RoundResponse {
    return RoundResponse(
        round = this.roundNumber,
        characterId = this.characterId,
        healthDelta = this.healthDelta,
        staminaDelta = this.staminaDelta,
        manaDelta = this.manaDelta
    )
}