package com.motycka.edu.game.account

import com.motycka.edu.game.account.rest.AccountRegistrationRequest
import com.motycka.edu.game.account.rest.AccountResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/accounts")
class AccountController(private val accountService: AccountService) {

    @PostMapping
    fun registerAccount(@RequestBody request: AccountRegistrationRequest): ResponseEntity<Any> {
        logger.info { "Registering account: $request" }
        return try {
            val account = accountService.registerAccount(request)
            ResponseEntity.ok(AccountResponse(account.id, account.name, account.username))
        } catch (e: IllegalArgumentException) {
            logger.error(e) { "Validation failure: ${e.message}" }
            ResponseEntity.badRequest().body(mapOf("error" to "Validation failed: ${e.message}"))
        } catch (e: Exception) {
            logger.error(e) { "Internal server error: ${e.message}" }
            ResponseEntity.status(500).body(mapOf("error" to "Internal server error: ${e.message}"))
        }
    }
}