package com.motycka.edu.game.account.character

data class CharacterResponseDTO(
    val id: Long? = null,
    val name: String,
    val health: Int,
    val attackPower: Int, // Matches README
    val stamina: Int? = null,
    val defensePower: Int? = null, // Matches README
    val mana: Int? = null,
    val healingPower: Int? = null, // Matches README
    val experience: Int,
    val characterClass: String, // Matches README
    val level: Int,
    val shouldLevelUp: Boolean,
    val isOwner: Boolean
)