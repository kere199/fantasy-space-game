package com.motycka.edu.game.account.rest

import com.motycka.edu.game.account.model.Account

object AccountModelMapper {

    fun toResponse(account: Account): AccountResponse {
        return AccountResponse(
            id = account.id,
            name = account.name,
            username = account.username
        )
    }
}