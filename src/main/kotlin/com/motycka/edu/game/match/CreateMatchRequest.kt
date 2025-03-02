package com.motycka.edu.game.match

data class CreateMatchRequest(
    val rounds: Int,
    val challengerId: Long,
    val opponentId: Long
) {
    fun validate() {
        require(rounds in 1..10) { "Number of rounds must be between 1 and 10" }
        require(challengerId != opponentId) { "Challenger and opponent must be different characters" }
    }
}