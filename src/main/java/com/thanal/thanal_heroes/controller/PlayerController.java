package com.thanal.thanal_heroes.controller;

import com.thanal.thanal_heroes.dto.PlayerRequestDTO;
import com.thanal.thanal_heroes.dto.PlayerResponseDTO;
import com.thanal.thanal_heroes.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final PlayerService playerService;
    private final com.thanal.thanal_heroes.service.StatsService statsService;

    public PlayerController(PlayerService playerService, com.thanal.thanal_heroes.service.StatsService statsService) {
        this.playerService = playerService;
        this.statsService = statsService;
    }

    @GetMapping("/{id}/career-stats")
    public ResponseEntity<com.thanal.thanal_heroes.dto.PlayerCareerStatsResponseDTO> getPlayerCareerStats(@PathVariable String id) {
        return ResponseEntity.ok(statsService.getPlayerCareerStats(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<PlayerResponseDTO> createPlayer(@Valid @RequestBody PlayerRequestDTO request) {
        return ResponseEntity.ok(playerService.createPlayer(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<PlayerResponseDTO> updatePlayer(@PathVariable String id, @Valid @RequestBody PlayerRequestDTO request) {
        return ResponseEntity.ok(playerService.updatePlayer(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponseDTO> getPlayer(@PathVariable String id) {
        return ResponseEntity.ok(playerService.getPlayer(id));
    }

    @GetMapping
    public ResponseEntity<Page<PlayerResponseDTO>> searchPlayers(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(playerService.searchPlayers(name, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlayer(@PathVariable String id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}
