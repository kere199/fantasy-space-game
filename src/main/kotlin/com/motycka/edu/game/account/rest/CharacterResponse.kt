package com.motycka.edu.game.account.rest

data class CharacterResponse(
    val id: Long? = null,
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int? = null,
    val defensePower: Int? = null,
    val mana: Int? = null,
    val healingPower: Int? = null,
    val experience: Int,
    val characterClass: String,
    val level: Int,
    val shouldLevelUp: Boolean,
    val isOwner: Boolean
)