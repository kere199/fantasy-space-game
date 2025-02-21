package com.motycka.edu.game.account.character

data class CharacterDTO(
    val id: Long,
    val name: String,
    val health: Int,
    val attack: Int,
    val mana: Int?,
    val healing: Int?,
    val stamina: Int?,
    val defense: Int?,
    val experience: Int,
    val classType: String
)
