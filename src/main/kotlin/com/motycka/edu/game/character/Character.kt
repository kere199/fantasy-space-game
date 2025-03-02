package com.motycka.edu.game.character

data class Character(
    val id: Long? = null,
    val accountId: Long,
    val name: String,
    val characterClass: String,
    val health: Int,
    val attack: Int,
    val experience: Int,
    val defense: Int? = null,
    val stamina: Int? = null,
    val healing: Int? = null,
    val mana: Int? = null,
    val level: Int = 1
)