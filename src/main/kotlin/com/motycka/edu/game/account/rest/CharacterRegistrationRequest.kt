package com.motycka.edu.game.account.rest

data class CharacterRegistrationRequest(
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int? = null,
    val defensePower: Int? = null,
    val mana: Int? = null,
    val healingPower: Int? = null,
    val characterClass: String
) {
    fun validate() {
        require(characterClass in listOf("WARRIOR", "SORCERER")) { "Character class must be WARRIOR or SORCERER" }
        when (characterClass) {
            "WARRIOR" -> {
                require(stamina != null && defensePower != null) { "Warrior must have stamina and defensePower" }
                require(mana == null && healingPower == null) { "Warrior cannot have mana or healingPower" }
            }
            "SORCERER" -> {
                require(mana != null && healingPower != null) { "Sorcerer must have mana and healingPower" }
                require(stamina == null && defensePower == null) { "Sorcerer cannot have stamina or defensePower" }
            }
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