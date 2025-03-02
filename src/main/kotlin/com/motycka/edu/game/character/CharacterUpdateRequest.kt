package com.motycka.edu.game.character

data class CharacterUpdateRequest(
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int? = null,
    val defensePower: Int? = null,
    val mana: Int? = null,
    val healingPower: Int? = null
) {
    fun validate() {
        when {
            stamina != null && defensePower != null -> {
                require(mana == null && healingPower == null) { "Warrior cannot have mana or healingPower" }
            }
            mana != null && healingPower != null -> {
                require(stamina == null && defensePower == null) { "Sorcerer cannot have stamina or defensePower" }
            }
            else -> throw IllegalArgumentException("Invalid attribute combination for character update")
        }
        require(health in 50..150) { "Health must be between 50 and 150" }
        require(attackPower in 10..80) { "Attack power must be between 10 and 80" }
        if (stamina != null) require(stamina in 10..50) { "Stamina must be between 10 and 50" }
        if (defensePower != null) require(defensePower in 10..50) { "Defense power must be between 10 and 50" }
        if (mana != null) require(mana in 10..50) { "Mana must be between 10 and 50" }
        if (healingPower != null) require(healingPower in 10..50) { "Healing power must be between 10 and 50" }
        require(name.isNotBlank() && name.length <= 50) { "Name must be non-empty and at most 50 characters" }
    }
}