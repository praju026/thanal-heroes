package com.thanal.thanal_heroes.service;

import com.thanal.thanal_heroes.dto.InningsResponseDTO;
import com.thanal.thanal_heroes.dto.MatchRequestDTO;
import com.thanal.thanal_heroes.dto.MatchResponseDTO;
import com.thanal.thanal_heroes.dto.ScoreEventRequestDTO;
import com.thanal.thanal_heroes.dto.ScoreEventResponseDTO;
import com.thanal.thanal_heroes.model.*;
import com.thanal.thanal_heroes.repository.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.thanal.thanal_heroes.dto.BatsmanScorecardDTO;
import com.thanal.thanal_heroes.dto.BowlerScorecardDTO;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final InningsRepository inningsRepository;
    private final ScoreEventRepository scoreEventRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MatchService(MatchRepository matchRepository,
                        TeamRepository teamRepository,
                        TournamentRepository tournamentRepository,
                        PlayerRepository playerRepository,
                        MatchPlayerRepository matchPlayerRepository,
                        InningsRepository inningsRepository,
                        ScoreEventRepository scoreEventRepository,
                        TeamPlayerRepository teamPlayerRepository,
                        SimpMessagingTemplate messagingTemplate) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.matchPlayerRepository = matchPlayerRepository;
        this.inningsRepository = inningsRepository;
        this.scoreEventRepository = scoreEventRepository;
        this.teamPlayerRepository = teamPlayerRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public MatchResponseDTO createMatch(MatchRequestDTO request) {
        Team team1 = teamRepository.findById(request.getTeam1Id())
                .orElseThrow(() -> new IllegalArgumentException("Team 1 not found with id: " + request.getTeam1Id()));
        Team team2 = teamRepository.findById(request.getTeam2Id())
                .orElseThrow(() -> new IllegalArgumentException("Team 2 not found with id: " + request.getTeam2Id()));

        Tournament tournament = null;
        if (request.getTournamentId() != null && !request.getTournamentId().trim().isEmpty()) {
            tournament = tournamentRepository.findById(request.getTournamentId())
                    .orElseThrow(() -> new IllegalArgumentException("Tournament not found with id: " + request.getTournamentId()));
        }

        Match match = Match.builder()
                .tournament(tournament)
                .team1(team1)
                .team2(team2)
                .matchDate(request.getMatchDate())
                .status("SCHEDULED")
                .overs(request.getOvers())
                .build();

        Match savedMatch = matchRepository.save(match);
        return mapToResponseDTO(savedMatch);
    }

    @Transactional
    public MatchResponseDTO quickStartMatch(String team1Id, String team2Id, int overs, String tossWinnerId, String tossDecision) {
        Team team1 = teamRepository.findById(team1Id)
                .orElseThrow(() -> new IllegalArgumentException("Team 1 not found with id: " + team1Id));
        Team team2 = teamRepository.findById(team2Id)
                .orElseThrow(() -> new IllegalArgumentException("Team 2 not found with id: " + team2Id));

        Match match = Match.builder()
                .team1(team1)
                .team2(team2)
                .matchDate(LocalDateTime.now())
                .status("IN_PROGRESS")
                .overs(overs)
                .build();

        Team tossWinner = team1Id.equals(tossWinnerId) ? team1 : team2;
        match.setTossWinner(tossWinner);
        match.setTossDecision(tossDecision.toUpperCase());

        Match savedMatch = matchRepository.save(match);

        // Auto-assign default playing XI (all team players)
        setupDefaultSquad(savedMatch, team1);
        setupDefaultSquad(savedMatch, team2);

        // Start Innings 1
        Team battingTeam = tossDecision.equalsIgnoreCase("BAT") ? tossWinner : (tossWinner == team1 ? team2 : team1);
        Team bowlingTeam = battingTeam == team1 ? team2 : team1;

        Innings innings = Innings.builder()
                .match(savedMatch)
                .battingTeam(battingTeam)
                .bowlingTeam(bowlingTeam)
                .inningsNumber(1)
                .totalRuns(0)
                .totalWickets(0)
                .totalOvers(BigDecimal.ZERO)
                .isCompleted(false)
                .build();
        inningsRepository.save(innings);

        broadcastMatchUpdate(savedMatch.getId());

        return mapToResponseDTO(savedMatch);
    }

    private void setupDefaultSquad(Match match, Team team) {
        List<TeamPlayer> activeMembers = teamPlayerRepository.findActivePlayersByTeamId(team.getId());
        if (activeMembers != null) {
            for (TeamPlayer tp : activeMembers) {
                Player player = tp.getPlayer();
                MatchPlayer mp = MatchPlayer.builder()
                        .match(match)
                        .player(player)
                        .team(team)
                        .playingXi(true)
                        .build();
                matchPlayerRepository.save(mp);
            }
        }
    }

    @Transactional
    public MatchResponseDTO recordToss(String matchId, String tossWinnerId, String tossDecision) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        Team tossWinner = teamRepository.findById(tossWinnerId)
                .orElseThrow(() -> new IllegalArgumentException("Toss winner team not found with id: " + tossWinnerId));

        if (!tossDecision.equalsIgnoreCase("BAT") && !tossDecision.equalsIgnoreCase("BOWL")) {
            throw new IllegalArgumentException("Invalid toss decision. Must be BAT or BOWL");
        }

        match.setTossWinner(tossWinner);
        match.setTossDecision(tossDecision.toUpperCase());
        match.setStatus("TOSS_PENDING");
        Match savedMatch = matchRepository.save(match);
        return mapToResponseDTO(savedMatch);
    }

    @Transactional
    public void setupMatchSquads(String matchId, String teamId, List<String> playerIds, List<String> playingXiIds) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + teamId));

        for (String playerId : playerIds) {
            Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + playerId));

            boolean playingXi = playingXiIds.contains(playerId);

            MatchPlayer matchPlayer = MatchPlayer.builder()
                    .match(match)
                    .player(player)
                    .team(team)
                    .playingXi(playingXi)
                    .build();

            matchPlayerRepository.save(matchPlayer);
        }
    }

    @Transactional
    public InningsResponseDTO startInnings(String matchId, String battingTeamId, String bowlingTeamId, int inningsNumber) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        Team battingTeam = teamRepository.findById(battingTeamId)
                .orElseThrow(() -> new IllegalArgumentException("Batting team not found with id: " + battingTeamId));

        Team bowlingTeam = teamRepository.findById(bowlingTeamId)
                .orElseThrow(() -> new IllegalArgumentException("Bowling team not found with id: " + bowlingTeamId));

        Innings innings = Innings.builder()
                .match(match)
                .battingTeam(battingTeam)
                .bowlingTeam(bowlingTeam)
                .inningsNumber(inningsNumber)
                .totalRuns(0)
                .totalWickets(0)
                .totalOvers(BigDecimal.ZERO)
                .isCompleted(false)
                .build();

        Innings savedInnings = inningsRepository.save(innings);

        match.setStatus("IN_PROGRESS");
        matchRepository.save(match);

        return mapToInningsResponseDTO(savedInnings);
    }

    @Transactional
    public ScoreEventResponseDTO recordScoreEvent(ScoreEventRequestDTO request) {
        Innings innings = inningsRepository.findById(request.getInningsId())
                .orElseThrow(() -> new IllegalArgumentException("Innings not found with id: " + request.getInningsId()));

        Match match = innings.getMatch();

        Player batsman = playerRepository.findById(request.getBatsmanId())
                .orElseThrow(() -> new IllegalArgumentException("Batsman not found with id: " + request.getBatsmanId()));

        Player nonStriker = playerRepository.findById(request.getNonStrikerId())
                .orElseThrow(() -> new IllegalArgumentException("Non-striker not found with id: " + request.getNonStrikerId()));

        Player bowler = playerRepository.findById(request.getBowlerId())
                .orElseThrow(() -> new IllegalArgumentException("Bowler not found with id: " + request.getBowlerId()));

        Player fielder = null;
        if (request.getFielderId() != null && !request.getFielderId().trim().isEmpty()) {
            fielder = playerRepository.findById(request.getFielderId())
                    .orElseThrow(() -> new IllegalArgumentException("Fielder not found with id: " + request.getFielderId()));
        }

        String extraType = request.getExtraType() != null ? request.getExtraType().toUpperCase() : "NONE";

        ScoreEvent event = ScoreEvent.builder()
                .match(match)
                .innings(innings)
                .overNumber(request.getOverNumber())
                .ballNumber(request.getBallNumber())
                .batsman(batsman)
                .nonStriker(nonStriker)
                .bowler(bowler)
                .runsOffBat(request.getRunsOffBat())
                .extraRuns(request.getExtraRuns())
                .extraType(extraType)
                .wicket(request.isWicket())
                .dismissalType(request.getDismissalType() != null ? request.getDismissalType().toUpperCase() : "NONE")
                .fielder(fielder)
                .build();

        scoreEventRepository.save(event);

        // Update innings totals
        List<ScoreEvent> events = scoreEventRepository.findByInningsIdOrderByOverNumberAscBallNumberAsc(innings.getId());
        int totalRuns = 0;
        int totalWickets = 0;
        long legalBallsCount = 0;

        for (ScoreEvent ev : events) {
            totalRuns += ev.getRunsOffBat() + ev.getExtraRuns();
            if (ev.isWicket() && !ev.getDismissalType().equalsIgnoreCase("RETIRED")) {
                totalWickets++;
            }
            if (!ev.getExtraType().equalsIgnoreCase("WD") && !ev.getExtraType().equalsIgnoreCase("NB")) {
                legalBallsCount++;
            }
        }

        innings.setTotalRuns(totalRuns);
        innings.setTotalWickets(totalWickets);

        long overs = legalBallsCount / 6;
        long balls = legalBallsCount % 6;
        innings.setTotalOvers(BigDecimal.valueOf(overs + balls / 10.0));

        inningsRepository.save(innings);

        broadcastMatchUpdate(match.getId());

        return mapToScoreEventResponseDTO(event);
    }

    @Transactional
    public void completeInnings(String inningsId) {
        Innings innings = inningsRepository.findById(inningsId)
                .orElseThrow(() -> new IllegalArgumentException("Innings not found with id: " + inningsId));
        innings.setCompleted(true);
        inningsRepository.save(innings);

        Match match = innings.getMatch();
        match.setStatus("INNINGS_BREAK");
        matchRepository.save(match);

        broadcastMatchUpdate(match.getId());
    }

    @Transactional
    public MatchResponseDTO completeMatch(String id, String winnerId, String resultMarginDetail) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + id));

        Team winner = teamRepository.findById(winnerId)
                .orElseThrow(() -> new IllegalArgumentException("Winner team not found with id: " + winnerId));

        match.setWinner(winner);
        match.setResultMarginDetail(resultMarginDetail);
        match.setStatus("COMPLETED");

        Match savedMatch = matchRepository.save(match);
        broadcastMatchUpdate(id);
        return mapToResponseDTO(savedMatch);
    }

    @Transactional
    public void deleteMatch(String id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + id));
        match.setDeleted(true);
        matchRepository.save(match);
    }

    @Transactional(readOnly = true)
    public List<MatchResponseDTO> getAllMatches() {
        return matchRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MatchResponseDTO getMatch(String id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + id));
        return mapToResponseDTO(match);
    }

    private void broadcastMatchUpdate(String matchId) {
        try {
            MatchResponseDTO scorecard = getMatch(matchId);
            messagingTemplate.convertAndSend("/topic/live-match/" + matchId, scorecard);
        } catch (Exception e) {
            // Silently log websocket error to not interrupt transaction
            System.err.println("WebSocket broadcast failed: " + e.getMessage());
        }
    }

    public MatchResponseDTO mapToResponseDTO(Match match) {
        List<Innings> inningsList = inningsRepository.findByMatchId(match.getId());
        List<InningsResponseDTO> inningsDTOs = inningsList.stream()
                .map(this::mapToInningsResponseDTO)
                .collect(Collectors.toList());

        return MatchResponseDTO.builder()
                .id(match.getId())
                .tournamentId(match.getTournament() != null ? match.getTournament().getId() : null)
                .tournamentName(match.getTournament() != null ? match.getTournament().getName() : null)
                .team1Id(match.getTeam1().getId())
                .team1Name(match.getTeam1().getName())
                .team2Id(match.getTeam2().getId())
                .team2Name(match.getTeam2().getName())
                .matchDate(match.getMatchDate())
                .status(match.getStatus())
                .tossWinnerId(match.getTossWinner() != null ? match.getTossWinner().getId() : null)
                .tossWinnerName(match.getTossWinner() != null ? match.getTossWinner().getName() : null)
                .tossDecision(match.getTossDecision())
                .winnerId(match.getWinner() != null ? match.getWinner().getId() : null)
                .winnerName(match.getWinner() != null ? match.getWinner().getName() : null)
                .resultMarginDetail(match.getResultMarginDetail())
                .innings(inningsDTOs)
                .overs(match.getOvers())
                .build();
    }

    private InningsResponseDTO mapToInningsResponseDTO(Innings innings) {
        List<ScoreEvent> events = scoreEventRepository.findByInningsIdOrderByOverNumberAscBallNumberAsc(innings.getId());
        
        Map<String, BatsmanScorecardDTO> batsmanMap = new LinkedHashMap<>();
        Map<String, BowlerScorecardDTO> bowlerMap = new LinkedHashMap<>();
        Map<String, Integer> bowlerBallsMap = new HashMap<>();
        Map<String, Map<Integer, Integer>> bowlerOverRuns = new HashMap<>();
        Map<String, Map<Integer, Integer>> bowlerOverLegalBalls = new HashMap<>();

        for (ScoreEvent ev : events) {
            // Process Striker
            String batsmanId = ev.getBatsman().getId();
            if (!batsmanMap.containsKey(batsmanId)) {
                batsmanMap.put(batsmanId, new BatsmanScorecardDTO(
                    batsmanId,
                    ev.getBatsman().getName(),
                    0, 0, 0, 0, false, ""
                ));
            }
            // Process Non-Striker
            if (ev.getNonStriker() != null) {
                String nonStrikerId = ev.getNonStriker().getId();
                if (!batsmanMap.containsKey(nonStrikerId)) {
                    batsmanMap.put(nonStrikerId, new BatsmanScorecardDTO(
                        nonStrikerId,
                        ev.getNonStriker().getName(),
                        0, 0, 0, 0, false, ""
                    ));
                }
            }

            // Update Striker Stats
            BatsmanScorecardDTO strikerCard = batsmanMap.get(batsmanId);
            strikerCard.setRuns(strikerCard.getRuns() + ev.getRunsOffBat());
            if (!ev.getExtraType().equalsIgnoreCase("WD")) {
                strikerCard.setBallsFaced(strikerCard.getBallsFaced() + 1);
            }
            if (ev.getRunsOffBat() == 4) {
                strikerCard.setFours(strikerCard.getFours() + 1);
            } else if (ev.getRunsOffBat() == 6) {
                strikerCard.setSixes(strikerCard.getSixes() + 1);
            }

            if (ev.isWicket()) {
                strikerCard.setIsOut(true);
                String details = "";
                String dType = ev.getDismissalType().toUpperCase();
                String bowlerName = ev.getBowler().getName();
                String fielderName = ev.getFielder() != null ? ev.getFielder().getName() : "";
                switch (dType) {
                    case "BOWLED":
                        details = "b " + bowlerName;
                        break;
                    case "CAUGHT":
                        if (!fielderName.isEmpty()) {
                            details = "c " + fielderName + " b " + bowlerName;
                        } else {
                            details = "c & b " + bowlerName;
                        }
                        break;
                    case "LBW":
                        details = "lbw b " + bowlerName;
                        break;
                    case "STUMPED":
                        if (!fielderName.isEmpty()) {
                            details = "st " + fielderName + " b " + bowlerName;
                        } else {
                            details = "st b " + bowlerName;
                        }
                        break;
                    case "HIT_WICKET":
                        details = "hit wicket b " + bowlerName;
                        break;
                    case "RUN_OUT":
                        if (!fielderName.isEmpty()) {
                            details = "run out (" + fielderName + ")";
                        } else {
                            details = "run out";
                        }
                        break;
                    case "RETIRED":
                        details = "retired";
                        break;
                    default:
                        details = "out";
                        break;
                }
                strikerCard.setDismissalDetail(details);
            }

            // Process Bowler
            String bowlerId = ev.getBowler().getId();
            if (!bowlerMap.containsKey(bowlerId)) {
                bowlerMap.put(bowlerId, new BowlerScorecardDTO(
                    bowlerId,
                    ev.getBowler().getName(),
                    BigDecimal.ZERO, 0, 0, 0, BigDecimal.ZERO
                ));
            }

            BowlerScorecardDTO bowlerCard = bowlerMap.get(bowlerId);
            int runsConceded = ev.getRunsOffBat();
            if (ev.getExtraType().equalsIgnoreCase("WD") || ev.getExtraType().equalsIgnoreCase("NB")) {
                runsConceded += ev.getExtraRuns();
            }
            bowlerCard.setRunsConceded(bowlerCard.getRunsConceded() + runsConceded);

            if (ev.isWicket() && !ev.getDismissalType().equalsIgnoreCase("RUN_OUT") && !ev.getDismissalType().equalsIgnoreCase("RETIRED")) {
                bowlerCard.setWickets(bowlerCard.getWickets() + 1);
            }

            if (!ev.getExtraType().equalsIgnoreCase("WD") && !ev.getExtraType().equalsIgnoreCase("NB")) {
                bowlerBallsMap.put(bowlerId, bowlerBallsMap.getOrDefault(bowlerId, 0) + 1);
            }

            int overNum = ev.getOverNumber();
            bowlerOverRuns.computeIfAbsent(bowlerId, k -> new HashMap<>());
            bowlerOverRuns.get(bowlerId).put(overNum, bowlerOverRuns.get(bowlerId).getOrDefault(overNum, 0) + runsConceded);

            bowlerOverLegalBalls.computeIfAbsent(bowlerId, k -> new HashMap<>());
            if (!ev.getExtraType().equalsIgnoreCase("WD") && !ev.getExtraType().equalsIgnoreCase("NB")) {
                bowlerOverLegalBalls.get(bowlerId).put(overNum, bowlerOverLegalBalls.get(bowlerId).getOrDefault(overNum, 0) + 1);
            }
        }

        // Finalize bowlers
        for (Map.Entry<String, BowlerScorecardDTO> entry : bowlerMap.entrySet()) {
            String bowlerId = entry.getKey();
            BowlerScorecardDTO bowlerCard = entry.getValue();
            int totalBalls = bowlerBallsMap.getOrDefault(bowlerId, 0);
            int ov = totalBalls / 6;
            int bl = totalBalls % 6;
            bowlerCard.setOvers(BigDecimal.valueOf(ov + bl / 10.0));

            if (totalBalls > 0) {
                double economyVal = (double) bowlerCard.getRunsConceded() / (totalBalls / 6.0);
                bowlerCard.setEconomy(BigDecimal.valueOf(economyVal).setScale(2, java.math.RoundingMode.HALF_UP));
            } else {
                bowlerCard.setEconomy(BigDecimal.ZERO);
            }

            int maidens = 0;
            Map<Integer, Integer> runsPerOver = bowlerOverRuns.get(bowlerId);
            Map<Integer, Integer> legalBallsPerOver = bowlerOverLegalBalls.get(bowlerId);
            if (runsPerOver != null && legalBallsPerOver != null) {
                for (Map.Entry<Integer, Integer> ballEntry : legalBallsPerOver.entrySet()) {
                    int overNo = ballEntry.getKey();
                    int legalBalls = ballEntry.getValue();
                    int runsInOver = runsPerOver.getOrDefault(overNo, 0);
                    if (legalBalls == 6 && runsInOver == 0) {
                        maidens++;
                    }
                }
            }
            bowlerCard.setMaidens(maidens);
        }

        return InningsResponseDTO.builder()
                .id(innings.getId())
                .inningsNumber(innings.getInningsNumber())
                .battingTeamId(innings.getBattingTeam().getId())
                .battingTeamName(innings.getBattingTeam().getName())
                .bowlingTeamId(innings.getBowlingTeam().getId())
                .bowlingTeamName(innings.getBowlingTeam().getName())
                .totalRuns(innings.getTotalRuns())
                .totalWickets(innings.getTotalWickets())
                .totalOvers(innings.getTotalOvers())
                .completed(innings.isCompleted())
                .batsmen(new ArrayList<>(batsmanMap.values()))
                .bowlers(new ArrayList<>(bowlerMap.values()))
                .build();
    }

    private ScoreEventResponseDTO mapToScoreEventResponseDTO(ScoreEvent event) {
        return ScoreEventResponseDTO.builder()
                .id(event.getId())
                .overNumber(event.getOverNumber())
                .ballNumber(event.getBallNumber())
                .batsmanId(event.getBatsman().getId())
                .batsmanName(event.getBatsman().getName())
                .nonStrikerId(event.getNonStriker().getId())
                .nonStrikerName(event.getNonStriker().getName())
                .bowlerId(event.getBowler().getId())
                .bowlerName(event.getBowler().getName())
                .runsOffBat(event.getRunsOffBat())
                .extraRuns(event.getExtraRuns())
                .extraType(event.getExtraType())
                .wicket(event.isWicket())
                .dismissalType(event.getDismissalType())
                .fielderId(event.getFielder() != null ? event.getFielder().getId() : null)
                .fielderName(event.getFielder() != null ? event.getFielder().getName() : null)
                .build();
    }
}
