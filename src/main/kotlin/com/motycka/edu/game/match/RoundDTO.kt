package com.motycka.edu.game.match
data class RoundDTO(
    val round: Int,
    val characterId: Long,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int
)
