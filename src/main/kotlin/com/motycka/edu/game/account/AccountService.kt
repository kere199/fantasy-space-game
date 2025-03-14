package com.motycka.edu.game.account

import com.motycka.edu.game.account.model.Account
import com.motycka.edu.game.account.rest.AccountRegistrationRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class AccountService(private val accountRepository: AccountRepository) {

    fun getCurrentAccountId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication?.name ?: throw IllegalStateException("No authenticated user found")
        logger.info { "Authenticated username: $username" }
        val accountId = accountRepository.findAccountIdByUsername(username)
            ?: throw IllegalStateException("Account not found for username: $username")
        logger.info { "Found account ID: $accountId for username: $username" }
        return accountId
    }

    fun registerAccount(request: AccountRegistrationRequest): Account {
        request.validate()
        logger.info { "Validated account registration request: ${request.username}" }

        if (accountRepository.findAccountByUsername(request.username) != null) {
            throw IllegalArgumentException("Username '${request.username}' is already taken")
        }

        val accountId = accountRepository.saveAccount(request)
        logger.info { "Registered new account with ID: $accountId" }

        return Account(
            id = accountId,
            name = request.name,
            username = request.username,
            password = request.password
        )
    }
}