package com.motycka.edu.game.match

data class Round(
    val id: Long? = null,
    val matchId: Long? = null,
    val roundNumber: Int,
    val characterId: Long,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int
)