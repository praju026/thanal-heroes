package com.thanal.thanal_heroes;

import com.thanal.thanal_heroes.dto.InningsResponseDTO;
import com.thanal.thanal_heroes.dto.MatchRequestDTO;
import com.thanal.thanal_heroes.dto.MatchResponseDTO;
import com.thanal.thanal_heroes.dto.PlayerCareerStatsResponseDTO;
import com.thanal.thanal_heroes.dto.ScoreEventRequestDTO;
import com.thanal.thanal_heroes.model.Player;
import com.thanal.thanal_heroes.model.Team;
import com.thanal.thanal_heroes.repository.PlayerRepository;
import com.thanal.thanal_heroes.repository.TeamRepository;
import com.thanal.thanal_heroes.service.MatchService;
import com.thanal.thanal_heroes.service.StatsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StatsCalculationTests {

    @Autowired
    private MatchService matchService;

    @Autowired
    private StatsService statsService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void testDynamicCareerStatsCalculation() {
        // Setup Teams & Players
        Team team1 = teamRepository.save(Team.builder().name("Red Devils").build());
        Team team2 = teamRepository.save(Team.builder().name("Blue Sky").build());

        Player batsman = playerRepository.save(Player.builder().name("Dhoni").build());
        Player partner = playerRepository.save(Player.builder().name("Kohli").build());
        Player bowler = playerRepository.save(Player.builder().name("Bumrah").build());

        MatchRequestDTO matchReq = MatchRequestDTO.builder()
                .team1Id(team1.getId())
                .team2Id(team2.getId())
                .matchDate(LocalDateTime.now())
                .build();
        MatchResponseDTO match = matchService.createMatch(matchReq);

        InningsResponseDTO innings = matchService.startInnings(match.getId(), team1.getId(), team2.getId(), 1);

        // Dhoni plays:
        // Ball 1: 6 runs
        matchService.recordScoreEvent(ScoreEventRequestDTO.builder()
                .inningsId(innings.getId())
                .overNumber(0)
                .ballNumber(1)
                .batsmanId(batsman.getId())
                .nonStrikerId(partner.getId())
                .bowlerId(bowler.getId())
                .runsOffBat(6)
                .extraRuns(0)
                .extraType("NONE")
                .build());

        // Ball 2: Dot ball
        matchService.recordScoreEvent(ScoreEventRequestDTO.builder()
                .inningsId(innings.getId())
                .overNumber(0)
                .ballNumber(2)
                .batsmanId(batsman.getId())
                .nonStrikerId(partner.getId())
                .bowlerId(bowler.getId())
                .runsOffBat(0)
                .extraRuns(0)
                .extraType("NONE")
                .build());

        // Ball 3: Out (Bowled)
        matchService.recordScoreEvent(ScoreEventRequestDTO.builder()
                .inningsId(innings.getId())
                .overNumber(0)
                .ballNumber(3)
                .batsmanId(batsman.getId())
                .nonStrikerId(partner.getId())
                .bowlerId(bowler.getId())
                .runsOffBat(0)
                .extraRuns(0)
                .extraType("NONE")
                .wicket(true)
                .dismissalType("BOWLED")
                .build());

        // Get stats for Dhoni
        PlayerCareerStatsResponseDTO dhoniStats = statsService.getPlayerCareerStats(batsman.getId());

        // Batting Assertions
        Assertions.assertEquals(6, dhoniStats.getBatting().getTotalRuns());
        Assertions.assertEquals(3, dhoniStats.getBatting().getBallsFaced());
        Assertions.assertEquals(1, dhoniStats.getBatting().getOuts());
        Assertions.assertEquals(new BigDecimal("6.00"), dhoniStats.getBatting().getAverage());
        Assertions.assertEquals(new BigDecimal("200.00"), dhoniStats.getBatting().getStrikeRate());

        // Get stats for Bumrah (bowler)
        PlayerCareerStatsResponseDTO bumrahStats = statsService.getPlayerCareerStats(bowler.getId());

        // Bowling Assertions
        Assertions.assertEquals(1, bumrahStats.getBowling().getWickets());
        Assertions.assertEquals(6, bumrahStats.getBowling().getRunsConceded());
        Assertions.assertEquals(new BigDecimal("0.3"), bumrahStats.getBowling().getOversBowled());
        Assertions.assertEquals(new BigDecimal("6.00"), bumrahStats.getBowling().getAverage());
        Assertions.assertEquals(new BigDecimal("12.00"), bumrahStats.getBowling().getEconomyRate());
    }
}
