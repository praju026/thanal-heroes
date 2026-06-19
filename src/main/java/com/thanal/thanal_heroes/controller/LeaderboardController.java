package com.thanal.thanal_heroes.controller;

import com.thanal.thanal_heroes.model.PlayerBattingSummary;
import com.thanal.thanal_heroes.model.PlayerBowlingSummary;
import com.thanal.thanal_heroes.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/leaderboards")
public class LeaderboardController {

    private final StatsService statsService;

    public LeaderboardController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/batting")
    public ResponseEntity<List<PlayerBattingSummary>> getBattingLeaderboard() {
        return ResponseEntity.ok(statsService.getBattingLeaderboard());
    }

    @GetMapping("/bowling")
    public ResponseEntity<List<PlayerBowlingSummary>> getBowlingLeaderboard() {
        return ResponseEntity.ok(statsService.getBowlingLeaderboard());
    }

    @PostMapping("/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> refreshLeaderboards() {
        statsService.refreshLeaderboardCaches();
        return ResponseEntity.ok(Map.of("message", "Leaderboards cache refreshed successfully"));
    }
}
