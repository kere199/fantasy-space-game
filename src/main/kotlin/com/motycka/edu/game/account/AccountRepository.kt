package com.motycka.edu.game.account

import com.motycka.edu.game.account.model.Account
import com.motycka.edu.game.account.rest.AccountRegistrationRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.Statement

private val logger = KotlinLogging.logger {}

@Repository
class AccountRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findAccountIdByUsername(username: String): Long? {
        val sql = "SELECT id FROM account WHERE LOWER(username) = LOWER(?)"
        val ids = jdbcTemplate.query(sql, arrayOf(username)) { rs, _ ->
            rs.getLong("id")
        }
        logger.info { "Found account IDs for username '$username': $ids" }
        return ids.firstOrNull()
    }

    fun findAccountByUsername(username: String): Account? {
        val sql = "SELECT * FROM account WHERE LOWER(username) = LOWER(?)"
        val accounts = jdbcTemplate.query(sql, arrayOf(username)) { rs, _ ->
            Account(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                username = rs.getString("username"),
                password = rs.getString("password")
            )
        }
        logger.info { "Found accounts for username '$username': $accounts" }
        return accounts.firstOrNull()
    }

    fun saveAccount(request: AccountRegistrationRequest): Long {
        val sql = """
            INSERT INTO account (name, username, password)
            VALUES (?, ?, ?)
        """.trimIndent()

        val keyHolder = GeneratedKeyHolder()

        try {
            jdbcTemplate.update({ connection ->
                val ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ps.setString(1, request.name)
                ps.setString(2, request.username)
                ps.setString(3, request.password)
                ps
            }, keyHolder)
        } catch (e: Exception) {
            logger.error { "Failed to save account: ${e.message}" }
            throw e
        }

        return keyHolder.key?.toLong() ?: throw RuntimeException("Failed to retrieve generated account ID")
    }
}