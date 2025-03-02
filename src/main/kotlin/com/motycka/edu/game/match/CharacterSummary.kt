package com.motycka.edu.game.match

data class CharacterSummary(
    val id: Long,
    val name: String,
    val characterClass: String,
    val level: Int,
    val experienceTotal: Int,
    val experienceGained: Int
)