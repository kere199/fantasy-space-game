package com.motycka.edu.game.match

data class Match(
    val id: Long? = null,
    val challengerId: Long,
    val opponentId: Long,
    val matchOutcome: String,
    val challengerXp: Int,
    val opponentXp: Int
)