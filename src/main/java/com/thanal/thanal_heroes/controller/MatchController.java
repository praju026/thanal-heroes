package com.thanal.thanal_heroes.controller;

import com.thanal.thanal_heroes.dto.InningsResponseDTO;
import com.thanal.thanal_heroes.dto.MatchRequestDTO;
import com.thanal.thanal_heroes.dto.MatchResponseDTO;
import com.thanal.thanal_heroes.dto.ScoreEventRequestDTO;
import com.thanal.thanal_heroes.dto.ScoreEventResponseDTO;
import com.thanal.thanal_heroes.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<MatchResponseDTO> createMatch(@Valid @RequestBody MatchRequestDTO request) {
        return ResponseEntity.ok(matchService.createMatch(request));
    }

    @PutMapping("/{id}/toss")
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<MatchResponseDTO> recordToss(
            @PathVariable String id,
            @RequestParam String tossWinnerId,
            @RequestParam String tossDecision) {
        return ResponseEntity.ok(matchService.recordToss(id, tossWinnerId, tossDecision));
    }

    @PostMapping("/{id}/players")
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<?> setupMatchSquads(
            @PathVariable String id,
            @RequestParam String teamId,
            @RequestBody Map<String, List<String>> body) {
        List<String> playerIds = body.get("playerIds");
        List<String> playingXiIds = body.get("playingXiIds");
        if (playerIds == null || playingXiIds == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "playerIds and playingXiIds are required"));
        }
        matchService.setupMatchSquads(id, teamId, playerIds, playingXiIds);
        return ResponseEntity.ok(Map.of("message", "Squad setup completed for team"));
    }

    @PostMapping("/innings")
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<InningsResponseDTO> startInnings(
            @RequestParam String matchId,
            @RequestParam String battingTeamId,
            @RequestParam String bowlingTeamId,
            @RequestParam int inningsNumber) {
        return ResponseEntity.ok(matchService.startInnings(matchId, battingTeamId, bowlingTeamId, inningsNumber));
    }

    @PostMapping("/score-event")
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<ScoreEventResponseDTO> recordScoreEvent(@Valid @RequestBody ScoreEventRequestDTO request) {
        return ResponseEntity.ok(matchService.recordScoreEvent(request));
    }

    @PutMapping("/innings/{inningsId}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<?> completeInnings(@PathVariable String inningsId) {
        matchService.completeInnings(inningsId);
        return ResponseEntity.ok(Map.of("message", "Innings completed successfully"));
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SCORER')")
    public ResponseEntity<MatchResponseDTO> completeMatch(
            @PathVariable String id,
            @RequestParam String winnerId,
            @RequestParam String resultMarginDetail) {
        return ResponseEntity.ok(matchService.completeMatch(id, winnerId, resultMarginDetail));
    }

    @GetMapping
    public ResponseEntity<List<MatchResponseDTO>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponseDTO> getMatch(@PathVariable String id) {
        return ResponseEntity.ok(matchService.getMatch(id));
    }
}
