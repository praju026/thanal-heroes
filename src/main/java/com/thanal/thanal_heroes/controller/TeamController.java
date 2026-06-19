package com.thanal.thanal_heroes.controller;

import com.thanal.thanal_heroes.dto.TeamRequestDTO;
import com.thanal.thanal_heroes.dto.TeamResponseDTO;
import com.thanal.thanal_heroes.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<TeamResponseDTO> createTeam(@Valid @RequestBody TeamRequestDTO request) {
        return ResponseEntity.ok(teamService.createTeam(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<TeamResponseDTO> updateTeam(@PathVariable String id, @Valid @RequestBody TeamRequestDTO request) {
        return ResponseEntity.ok(teamService.updateTeam(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponseDTO> getTeam(@PathVariable String id) {
        return ResponseEntity.ok(teamService.getTeam(id));
    }

    @GetMapping
    public ResponseEntity<List<TeamResponseDTO>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @PostMapping("/{id}/players")
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<?> addPlayerToTeam(@PathVariable String id, @RequestBody Map<String, String> body) {
        String playerId = body.get("playerId");
        if (playerId == null || playerId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "playerId is required"));
        }
        teamService.addPlayerToTeam(id, playerId);
        return ResponseEntity.ok(Map.of("message", "Player added to team successfully"));
    }

    @DeleteMapping("/{id}/players/{playerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<?> removePlayerFromTeam(@PathVariable String id, @PathVariable String playerId) {
        teamService.removePlayerFromTeam(id, playerId);
        return ResponseEntity.ok(Map.of("message", "Player removed from team successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeam(@PathVariable String id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}
