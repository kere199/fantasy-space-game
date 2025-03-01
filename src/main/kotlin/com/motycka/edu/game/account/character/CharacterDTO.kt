//package com.motycka.edu.game.account.character
//
//data class CharacterDTO(
//    val id: Long,
//    val name: String,
//    val health: Int,
//    val attack: Int,
//    val mana: Int?,
//    val healing: Int?,
//    val stamina: Int?,
//    val defense: Int?,
//    val experience: Int,
//    val classType: String
//)
package com.motycka.edu.game.account.character

data class CharacterDTO(
    val id: Long? = null, // null for creation, present for retrieval
    val name: String,
    val health: Int,
    val attack: Int, // Reverted to original name
    val stamina: Int? = null,
    val defense: Int? = null, // Reverted to original name
    val mana: Int? = null,
    val healing: Int? = null, // Reverted to original name
    val experience: Int = 0, // default for new characters
    val classType: String, // Reverted to original name
    val level: Int? = null, // Keep this field as per README
    val shouldLevelUp: Boolean? = null, // Keep this field as per README
    val isOwner: Boolean? = null // Keep this field as per README
) {
    fun validate() {
        require(classType in listOf("WARRIOR", "SORCERER")) { "Character class must be WARRIOR or SORCERER" }
        when (classType) {
            "WARRIOR" -> {
                require(stamina != null && defense != null) { "Warrior must have stamina and defense" }
                require(mana == null && healing == null) { "Warrior cannot have mana or healing" }
            }
            "SORCERER" -> {
                require(mana != null && healing != null) { "Sorcerer must have mana and healing" }
                require(stamina == null && defense == null) { "Sorcerer cannot have stamina or defense" }
            }
        }
        require(health in 50..150) { "Health must be between 50 and 150" }
        require(attack in 10..80) { "Attack must be between 10 and 80" }
        if (stamina != null) require(stamina in 10..50) { "Stamina must be between 10 and 50" }
        if (defense != null) require(defense in 10..50) { "Defense must be between 10 and 50" }
        if (mana != null) require(mana in 10..50) { "Mana must be between 10 and 50" }
        if (healing != null) require(healing in 10..50) { "Healing must be between 10 and 50" }
        require(name.isNotBlank() && name.length <= 50) { "Name must be non-empty and at most 50 characters" }
    }
}