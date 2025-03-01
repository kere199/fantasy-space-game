package com.motycka.edu.game.account

data class AccountRegistrationRequest(
    val name: String,
    val username: String,
    val password: String
) {
    fun validate() {
        require(name.isNotBlank() && name.length <= 50) { "Name must be non-empty and at most 50 characters" }
        require(username.isNotBlank() && username.length <= 50) { "Username must be non-empty and at most 50 characters" }
        require(password.isNotBlank() && password.length >= 6) { "Password must be non-empty and at least 6 characters" }
    }
}