package com.motycka.edu.game.leaderboard

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/leaderboards")
class LeaderboardController(private val leaderboardService: LeaderboardService) {

    @GetMapping
    fun getLeaderboard(
        @RequestParam("class", required = false) characterClass: String?
    ): ResponseEntity<List<LeaderboardEntry>> {
        logger.info { "Fetching leaderboard with class filter: $characterClass" }
        return try {
            val leaderboard = leaderboardService.getLeaderboard(characterClass)
            ResponseEntity.ok(leaderboard)
        } catch (e: Exception) {
            logger.error(e) { "Internal server error: ${e.message}" }
            ResponseEntity.status(500).body(emptyList())
        }
    }
}