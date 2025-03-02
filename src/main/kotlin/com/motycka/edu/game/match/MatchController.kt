package com.motycka.edu.game.match

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/matches")
class MatchController(private val matchService: MatchService) {

    @GetMapping
    fun getAllMatches(): ResponseEntity<List<MatchResponse>> {
        logger.info { "Fetching all matches" }
        val matches = matchService.getAllMatches()
        return ResponseEntity.ok(matches)
    }

    @PostMapping
    fun createMatch(@RequestBody request: CreateMatchRequest): ResponseEntity<Any> {
        logger.info { "Received match creation request: $request" }
        return try {
            val match = matchService.createMatch(request)
            ResponseEntity.ok(match)
        } catch (e: IllegalArgumentException) {
            logger.error(e) { "Validation failure: ${e.message}" }
            ResponseEntity.badRequest().body(mapOf("error" to "Validation failed: ${e.message}"))
        } catch (e: IllegalStateException) {
            logger.error(e) { "Account issue: ${e.message}" }
            ResponseEntity.badRequest().body(mapOf("error" to "Account issue: ${e.message}"))
        } catch (e: Exception) {
            logger.error(e) { "Internal server error: ${e.message}" }
            ResponseEntity.status(500).body(mapOf("error" to "Internal server error: ${e.message}"))
        }
    }
}