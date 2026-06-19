package com.thanal.thanal_heroes;

import com.thanal.thanal_heroes.dto.InningsResponseDTO;
import com.thanal.thanal_heroes.dto.MatchRequestDTO;
import com.thanal.thanal_heroes.dto.MatchResponseDTO;
import com.thanal.thanal_heroes.dto.ScoreEventRequestDTO;
import com.thanal.thanal_heroes.model.Player;
import com.thanal.thanal_heroes.model.Team;
import com.thanal.thanal_heroes.repository.InningsRepository;
import com.thanal.thanal_heroes.repository.PlayerRepository;
import com.thanal.thanal_heroes.repository.TeamRepository;
import com.thanal.thanal_heroes.service.MatchService;
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
public class ScoringEngineTests {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private InningsRepository inningsRepository;

    @Test
    public void testScoringRulesAndOversCalculation() {
        // 1. Setup Teams
        Team team1 = teamRepository.save(Team.builder().name("Thanal Titans").build());
        Team team2 = teamRepository.save(Team.builder().name("Gravity Gladiators").build());

        // 2. Setup Players
        Player batsman1 = playerRepository.save(Player.builder().name("Aravind").build());
        Player batsman2 = playerRepository.save(Player.builder().name("Pranav").build());
        Player bowler = playerRepository.save(Player.builder().name("Midhun").build());

        // 3. Create Match
        MatchRequestDTO matchReq = MatchRequestDTO.builder()
                .team1Id(team1.getId())
                .team2Id(team2.getId())
                .matchDate(LocalDateTime.now())
                .build();
        MatchResponseDTO match = matchService.createMatch(matchReq);

        // 4. Start Innings
        InningsResponseDTO innings = matchService.startInnings(match.getId(), team1.getId(), team2.getId(), 1);

        // 5. Play Over 0
        // Ball 1: 4 runs off bat
        matchService.recordScoreEvent(ScoreEventRequestDTO.builder()
                .inningsId(innings.getId())
                .overNumber(0)
                .ballNumber(1)
                .batsmanId(batsman1.getId())
                .nonStrikerId(batsman2.getId())
                .bowlerId(bowler.getId())
                .runsOffBat(4)
                .extraRuns(0)
                .extraType("NONE")
                .build());

        // Ball 2: Wide (1 extra run, over still counts legal ball count: 1)
        matchService.recordScoreEvent(ScoreEventRequestDTO.builder()
                .inningsId(innings.getId())
                .overNumber(0)
                .ballNumber(2)
                .batsmanId(batsman1.getId())
                .nonStrikerId(batsman2.getId())
                .bowlerId(bowler.getId())
                .runsOffBat(0)
                .extraRuns(1)
                .extraType("WD")
                .build());

        // Ball 3: Out (caught, legal ball)
        matchService.recordScoreEvent(ScoreEventRequestDTO.builder()
                .inningsId(innings.getId())
                .overNumber(0)
                .ballNumber(2) // Note: ball count doesn't increment since previous was wide, this is the 2nd legal ball
                .batsmanId(batsman1.getId())
                .nonStrikerId(batsman2.getId())
                .bowlerId(bowler.getId())
                .runsOffBat(0)
                .extraRuns(0)
                .extraType("NONE")
                .wicket(true)
                .dismissalType("CAUGHT")
                .build());

        // Ball 4: Leg Bye (1 extra, legal ball)
        matchService.recordScoreEvent(ScoreEventRequestDTO.builder()
                .inningsId(innings.getId())
                .overNumber(0)
                .ballNumber(3)
                .batsmanId(batsman2.getId())
                .nonStrikerId(batsman1.getId())
                .bowlerId(bowler.getId())
                .runsOffBat(0)
                .extraRuns(1)
                .extraType("LB")
                .build());

        // Ball 5: 6 runs off bat (legal ball)
        matchService.recordScoreEvent(ScoreEventRequestDTO.builder()
                .inningsId(innings.getId())
                .overNumber(0)
                .ballNumber(4)
                .batsmanId(batsman2.getId())
                .nonStrikerId(batsman1.getId())
                .bowlerId(bowler.getId())
                .runsOffBat(6)
                .extraRuns(0)
                .extraType("NONE")
                .build());

        // Ball 6: Dot ball (legal ball)
        matchService.recordScoreEvent(ScoreEventRequestDTO.builder()
                .inningsId(innings.getId())
                .overNumber(0)
                .ballNumber(5)
                .batsmanId(batsman2.getId())
                .nonStrikerId(batsman1.getId())
                .bowlerId(bowler.getId())
                .runsOffBat(0)
                .extraRuns(0)
                .extraType("NONE")
                .build());

        // Ball 7: 1 run off bat (legal ball, completes 1st over: 6 legal balls)
        matchService.recordScoreEvent(ScoreEventRequestDTO.builder()
                .inningsId(innings.getId())
                .overNumber(0)
                .ballNumber(6)
                .batsmanId(batsman2.getId())
                .nonStrikerId(batsman1.getId())
                .bowlerId(bowler.getId())
                .runsOffBat(1)
                .extraRuns(0)
                .extraType("NONE")
                .build());

        // Ball 8: Over 1, Ball 1 (legal ball)
        matchService.recordScoreEvent(ScoreEventRequestDTO.builder()
                .inningsId(innings.getId())
                .overNumber(1)
                .ballNumber(1)
                .batsmanId(batsman1.getId())
                .nonStrikerId(batsman2.getId())
                .bowlerId(bowler.getId())
                .runsOffBat(1)
                .extraRuns(0)
                .extraType("NONE")
                .build());

        // 6. Verify aggregate stats
        com.thanal.thanal_heroes.model.Innings savedInnings = inningsRepository.findById(innings.getId()).orElseThrow();

        // Expected runs: 4 (bat) + 1 (wide) + 0 (out) + 1 (legbye) + 6 (bat) + 0 (dot) + 1 (bat) + 1 (bat) = 14 runs
        Assertions.assertEquals(14, savedInnings.getTotalRuns());

        // Expected wickets: 1
        Assertions.assertEquals(1, savedInnings.getTotalWickets());

        // Expected legal balls: 7 (which translates to 1.1 overs)
        Assertions.assertEquals(new BigDecimal("1.1"), savedInnings.getTotalOvers());
    }
}
