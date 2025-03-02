package com.motycka.edu.game.character

import com.motycka.edu.game.account.rest.CharacterRegistrationRequest
import com.motycka.edu.game.account.rest.CharacterResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/characters")
class CharacterController(private val characterService: CharacterService) {

    @GetMapping
    fun getAllCharacters(
        @RequestParam("class", required = false) classType: String?,
        @RequestParam(required = false) name: String?
    ): ResponseEntity<List<CharacterResponse>> {
        logger.info { "Fetching all characters with classType: $classType, name: $name" }
        val characters = characterService.getAllCharacters(classType, name)
        return ResponseEntity.ok(characters)
    }

    @GetMapping("/{id}")
    fun getCharacterById(@PathVariable id: Long): ResponseEntity<CharacterResponse> {
        logger.info { "Fetching character with ID: $id" }
        val character = characterService.getCharacterById(id)
        return if (character != null) {
            ResponseEntity.ok(character)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/challengers")
    fun getChallengers(): ResponseEntity<List<CharacterResponse>> {
        logger.info { "Fetching challengers" }
        return ResponseEntity.ok(characterService.getChallengers())
    }

    @GetMapping("/opponents")
    fun getOpponents(): ResponseEntity<List<CharacterResponse>> {
        logger.info { "Fetching opponents" }
        return ResponseEntity.ok(characterService.getOpponents())
    }

    @PutMapping("/{id}")
    fun levelUpCharacter(
        @PathVariable id: Long,
        @RequestBody updatedCharacter: CharacterUpdateRequest
    ): ResponseEntity<CharacterResponse> {
        logger.info { "Updating character with ID: $id, data: $updatedCharacter" }
        return try {
            val updated = characterService.levelUpCharacter(id, updatedCharacter)
            ResponseEntity.ok(updated)
        } catch (e: IllegalArgumentException) {
            logger.error(e) { "Validation failure: ${e.message}" }
            ResponseEntity.badRequest().body(null)
        }
    }

    @PostMapping
    fun createCharacter(@RequestBody request: CharacterRegistrationRequest): ResponseEntity<Any> {
        logger.info { "Received character creation request: $request" }
        return try {
            val createdCharacter = characterService.createCharacter(request)
            ResponseEntity.ok(createdCharacter)
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