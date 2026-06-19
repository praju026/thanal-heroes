package com.thanal.thanal_heroes.service;

import com.thanal.thanal_heroes.dto.PlayerBattingStatsDTO;
import com.thanal.thanal_heroes.dto.PlayerBowlingStatsDTO;
import com.thanal.thanal_heroes.dto.PlayerCareerStatsResponseDTO;
import com.thanal.thanal_heroes.model.*;
import com.thanal.thanal_heroes.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final PlayerRepository playerRepository;
    private final ScoreEventRepository scoreEventRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final PlayerBattingSummaryRepository battingSummaryRepository;
    private final PlayerBowlingSummaryRepository bowlingSummaryRepository;

    public StatsService(PlayerRepository playerRepository,
                        ScoreEventRepository scoreEventRepository,
                        MatchPlayerRepository matchPlayerRepository,
                        PlayerBattingSummaryRepository battingSummaryRepository,
                        PlayerBowlingSummaryRepository bowlingSummaryRepository) {
        this.playerRepository = playerRepository;
        this.scoreEventRepository = scoreEventRepository;
        this.matchPlayerRepository = matchPlayerRepository;
        this.battingSummaryRepository = battingSummaryRepository;
        this.bowlingSummaryRepository = bowlingSummaryRepository;
    }

    @Transactional(readOnly = true)
    public PlayerCareerStatsResponseDTO getPlayerCareerStats(String playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + playerId));

        List<ScoreEvent> battingEvents = scoreEventRepository.findByBatsmanId(playerId);
        List<ScoreEvent> bowlingEvents = scoreEventRepository.findByBowlerId(playerId);

        // Find matches played from match_players
        int matchesPlayed = (int) matchPlayerRepository.findByMatchId(null).stream()
                .filter(mp -> mp.getPlayer().getId().equals(playerId))
                .map(mp -> mp.getMatch().getId())
                .distinct()
                .count();

        // 1. Calculate Batting Stats
        PlayerBattingStatsDTO batting = calculateBatting(playerId, matchesPlayed, battingEvents);

        // 2. Calculate Bowling Stats
        PlayerBowlingStatsDTO bowling = calculateBowling(playerId, matchesPlayed, bowlingEvents);

        return PlayerCareerStatsResponseDTO.builder()
                .playerId(playerId)
                .playerName(player.getName())
                .batting(batting)
                .bowling(bowling)
                .build();
    }

    private PlayerBattingStatsDTO calculateBatting(String playerId, int matchesPlayed, List<ScoreEvent> events) {
        if (events.isEmpty()) {
            return PlayerBattingStatsDTO.builder()
                    .matchesPlayed(matchesPlayed)
                    .inningsPlayed(0)
                    .totalRuns(0)
                    .ballsFaced(0)
                    .outs(0)
                    .highestScore(0)
                    .average(BigDecimal.ZERO)
                    .strikeRate(BigDecimal.ZERO)
                    .fifties(0)
                    .hundreds(0)
                    .build();
        }

        int totalRuns = events.stream().mapToInt(ScoreEvent::getRunsOffBat).sum();
        int ballsFaced = (int) events.stream()
                .filter(e -> !e.getExtraType().equalsIgnoreCase("WD"))
                .count();

        int outs = (int) events.stream()
                .filter(ScoreEvent::isWicket)
                .count();

        // Innings played
        int inningsPlayed = (int) events.stream()
                .map(e -> e.getInnings().getId())
                .distinct()
                .count();

        // Highest score, fifties, hundreds
        Map<String, Integer> runsPerInnings = events.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getInnings().getId(),
                        Collectors.summingInt(ScoreEvent::getRunsOffBat)
                ));

        int highestScore = runsPerInnings.values().stream().max(Integer::compare).orElse(0);
        int fifties = (int) runsPerInnings.values().stream().filter(runs -> runs >= 50 && runs < 100).count();
        int hundreds = (int) runsPerInnings.values().stream().filter(runs -> runs >= 100).count();

        BigDecimal average = outs > 0 
                ? BigDecimal.valueOf(totalRuns).divide(BigDecimal.valueOf(outs), 2, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(totalRuns).setScale(2, RoundingMode.HALF_UP);

        BigDecimal strikeRate = ballsFaced > 0
                ? BigDecimal.valueOf(totalRuns).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(ballsFaced), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        return PlayerBattingStatsDTO.builder()
                .matchesPlayed(matchesPlayed)
                .inningsPlayed(inningsPlayed)
                .totalRuns(totalRuns)
                .ballsFaced(ballsFaced)
                .outs(outs)
                .highestScore(highestScore)
                .average(average)
                .strikeRate(strikeRate)
                .fifties(fifties)
                .hundreds(hundreds)
                .build();
    }

    private PlayerBowlingStatsDTO calculateBowling(String playerId, int matchesPlayed, List<ScoreEvent> events) {
        if (events.isEmpty()) {
            return PlayerBowlingStatsDTO.builder()
                    .matchesPlayed(matchesPlayed)
                    .inningsBowled(0)
                    .oversBowled(BigDecimal.ZERO)
                    .runsConceded(0)
                    .wickets(0)
                    .bestBowling("0/0")
                    .average(BigDecimal.ZERO)
                    .economyRate(BigDecimal.ZERO)
                    .strikeRate(BigDecimal.ZERO)
                    .build();
        }

        int inningsBowled = (int) events.stream()
                .map(e -> e.getInnings().getId())
                .distinct()
                .count();

        // Legal balls bowled (exclude Wides, No-Balls)
        int legalBalls = (int) events.stream()
                .filter(e -> !e.getExtraType().equalsIgnoreCase("WD") && !e.getExtraType().equalsIgnoreCase("NB"))
                .count();

        long overs = legalBalls / 6;
        long balls = legalBalls % 6;
        BigDecimal oversBowled = BigDecimal.valueOf(overs + balls / 10.0);

        // Runs conceded (runs off bat + extras EXCEPT leg-byes/byes which do not count against the bowler)
        int runsConceded = events.stream()
                .filter(e -> !e.getExtraType().equalsIgnoreCase("LB") && !e.getExtraType().equalsIgnoreCase("B"))
                .mapToInt(e -> e.getRunsOffBat() + e.getExtraRuns())
                .sum();

        // Wickets taken (exclude run-outs, retired hurt, timed out, etc.)
        int wickets = (int) events.stream()
                .filter(e -> e.isWicket() && !e.getDismissalType().equalsIgnoreCase("RUN_OUT") 
                             && !e.getDismissalType().equalsIgnoreCase("RETIRED")
                             && !e.getDismissalType().equalsIgnoreCase("OBSTRUCTING")
                             && !e.getDismissalType().equalsIgnoreCase("NONE"))
                .count();

        // Best bowling calculations
        Map<String, List<ScoreEvent>> eventsPerInnings = events.stream()
                .collect(Collectors.groupingBy(e -> e.getInnings().getId()));

        int bestWickets = 0;
        int bestRuns = 9999;
        for (List<ScoreEvent> inningsEvents : eventsPerInnings.values()) {
            int innWickets = (int) inningsEvents.stream()
                    .filter(e -> e.isWicket() && !e.getDismissalType().equalsIgnoreCase("RUN_OUT") 
                                 && !e.getDismissalType().equalsIgnoreCase("RETIRED")
                                 && !e.getDismissalType().equalsIgnoreCase("NONE"))
                    .count();
            int innRuns = inningsEvents.stream()
                    .filter(e -> !e.getExtraType().equalsIgnoreCase("LB") && !e.getExtraType().equalsIgnoreCase("B"))
                    .mapToInt(e -> e.getRunsOffBat() + e.getExtraRuns())
                    .sum();

            if (innWickets > bestWickets || (innWickets == bestWickets && innRuns < bestRuns)) {
                bestWickets = innWickets;
                bestRuns = innRuns;
            }
        }
        String bestBowling = bestWickets + "/" + (bestRuns == 9999 ? 0 : bestRuns);

        BigDecimal average = wickets > 0
                ? BigDecimal.valueOf(runsConceded).divide(BigDecimal.valueOf(wickets), 2, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(runsConceded).setScale(2, RoundingMode.HALF_UP);

        BigDecimal economyRate = legalBalls > 0
                ? BigDecimal.valueOf(runsConceded).multiply(BigDecimal.valueOf(6)).divide(BigDecimal.valueOf(legalBalls), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        BigDecimal strikeRate = wickets > 0
                ? BigDecimal.valueOf(legalBalls).divide(BigDecimal.valueOf(wickets), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        return PlayerBowlingStatsDTO.builder()
                .matchesPlayed(matchesPlayed)
                .inningsBowled(inningsBowled)
                .oversBowled(oversBowled)
                .runsConceded(runsConceded)
                .wickets(wickets)
                .bestBowling(bestBowling)
                .average(average)
                .economyRate(economyRate)
                .strikeRate(strikeRate)
                .build();
    }

    // Refresh Leaderboard cache every 5 minutes
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void refreshLeaderboardCaches() {
        List<Player> allPlayers = playerRepository.findAll();

        for (Player player : allPlayers) {
            try {
                PlayerCareerStatsResponseDTO stats = getPlayerCareerStats(player.getId());

                PlayerBattingSummary batSummary = PlayerBattingSummary.builder()
                        .playerId(player.getId())
                        .playerName(player.getName())
                        .matchesPlayed(stats.getBatting().getMatchesPlayed())
                        .inningsPlayed(stats.getBatting().getInningsPlayed())
                        .totalRuns(stats.getBatting().getTotalRuns())
                        .ballsFaced(stats.getBatting().getBallsFaced())
                        .outs(stats.getBatting().getOuts())
                        .highestScore(stats.getBatting().getHighestScore())
                        .average(stats.getBatting().getAverage())
                        .strikeRate(stats.getBatting().getStrikeRate())
                        .fifties(stats.getBatting().getFifties())
                        .hundreds(stats.getBatting().getHundreds())
                        .build();

                battingSummaryRepository.save(batSummary);

                PlayerBowlingSummary bowlSummary = PlayerBowlingSummary.builder()
                        .playerId(player.getId())
                        .playerName(player.getName())
                        .matchesPlayed(stats.getBowling().getMatchesPlayed())
                        .inningsBowled(stats.getBowling().getInningsBowled())
                        .oversBowled(stats.getBowling().getOversBowled())
                        .runsConceded(stats.getBowling().getRunsConceded())
                        .wickets(stats.getBowling().getWickets())
                        .bestBowling(stats.getBowling().getBestBowling())
                        .average(stats.getBowling().getAverage())
                        .economyRate(stats.getBowling().getEconomyRate())
                        .strikeRate(stats.getBowling().getStrikeRate())
                        .build();

                bowlingSummaryRepository.save(bowlSummary);
            } catch (Exception e) {
                System.err.println("Failed to cache stats for player: " + player.getName() + ", error: " + e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<PlayerBattingSummary> getBattingLeaderboard() {
        return battingSummaryRepository.findTop10ByOrderByTotalRunsDesc();
    }

    @Transactional(readOnly = true)
    public List<PlayerBowlingSummary> getBowlingLeaderboard() {
        return bowlingSummaryRepository.findTop10ByOrderByWicketsDesc();
    }
}
