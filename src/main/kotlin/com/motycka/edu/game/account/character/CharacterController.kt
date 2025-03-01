package com.motycka.edu.game.account.character

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
    ): ResponseEntity<List<CharacterResponseDTO>> {
        val characters = characterService.getAllCharacters(classType, name)
        return ResponseEntity.ok(characters.map { it.toResponseDTO() })
    }

    @GetMapping("/{id}")
    fun getCharacterById(@PathVariable id: Long): ResponseEntity<CharacterResponseDTO> {
        val character = characterService.getCharacterById(id)
        return if (character != null) {
            ResponseEntity.ok(character.toResponseDTO())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/challengers")
    fun getChallengers(): ResponseEntity<List<CharacterResponseDTO>> {
        return ResponseEntity.ok(characterService.getChallengers().map { it.toResponseDTO() })
    }

    @GetMapping("/opponents")
    fun getOpponents(): ResponseEntity<List<CharacterResponseDTO>> {
        return ResponseEntity.ok(characterService.getOpponents().map { it.toResponseDTO() })
    }

    @PutMapping("/{id}")
    fun levelUpCharacter(
        @PathVariable id: Long,
        @RequestBody updatedCharacter: CharacterDTO
    ): ResponseEntity<CharacterResponseDTO> {
        return try {
            val updated = characterService.levelUpCharacter(id, updatedCharacter)
            ResponseEntity.ok(updated.toResponseDTO())
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(null)
        }
    }

    @PostMapping
    fun createCharacter(@RequestBody character: CharacterDTO): ResponseEntity<Any> {
        logger.info { "Received character: $character" }
        return try {
            val createdCharacter = characterService.createCharacter(character)
            ResponseEntity.ok(createdCharacter.toResponseDTO())
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

    private fun CharacterDTO.toResponseDTO(): CharacterResponseDTO {
        return CharacterResponseDTO(
            id = this.id,
            name = this.name,
            health = this.health,
            attackPower = this.attack,
            stamina = this.stamina,
            defensePower = this.defense,
            mana = this.mana,
            healingPower = this.healing,
            experience = this.experience,
            characterClass = this.classType,
            level = this.level ?: 1,
            shouldLevelUp = this.shouldLevelUp ?: false,
            isOwner = this.isOwner ?: false
        )
    }
}