package com.motycka.edu.game.account.character

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/characters")
class CharacterController(private val characterService: CharacterService) {

    @GetMapping
    fun getAllCharacters(
        @RequestParam(required = false) classType: String?,
        @RequestParam(required = false) name: String?
    ): List<CharacterDTO> {
        return characterService.getAllCharacters(classType, name)
    }

    @GetMapping("/{id}")
    fun getCharacterById(@PathVariable id: Long): CharacterDTO? {
        return characterService.getCharacterById(id)
    }

    @GetMapping("/challengers")
    fun getChallengers(): ResponseEntity<List<CharacterDTO>> {
        return ResponseEntity.ok(characterService.getChallengers())
    }

    @GetMapping("/opponents")
    fun getOpponents(): ResponseEntity<List<CharacterDTO>> {
        return ResponseEntity.ok(characterService.getOpponents())
    }

    @PutMapping("/{id}")
    fun levelUpCharacter(
        @PathVariable id: Long,
        @RequestBody updatedCharacter: CharacterDTO
    ): ResponseEntity<CharacterDTO> {
        return ResponseEntity.ok(characterService.levelUpCharacter(id, updatedCharacter))
    }
}
