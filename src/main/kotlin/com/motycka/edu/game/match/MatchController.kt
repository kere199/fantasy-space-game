package com.motycka.edu.game.match



import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/matches")
class MatchController(private val matchService: MatchService) {

    @GetMapping
    fun getAllMatches(): ResponseEntity<List<MatchDTO>> {
        return ResponseEntity.ok(matchService.getAllMatches())
    }

    @PostMapping
    fun createMatch(@RequestParam challengerId: Long, @RequestParam opponentId: Long): ResponseEntity<MatchDTO> {
        return ResponseEntity.ok(matchService.createMatch(challengerId, opponentId))
    }
}
