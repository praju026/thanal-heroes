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
    private final SimpMessagingTemplate messagingTemplate;

    public MatchService(MatchRepository matchRepository,
                        TeamRepository teamRepository,
                        TournamentRepository tournamentRepository,
                        PlayerRepository playerRepository,
                        MatchPlayerRepository matchPlayerRepository,
                        InningsRepository inningsRepository,
                        ScoreEventRepository scoreEventRepository,
                        SimpMessagingTemplate messagingTemplate) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.matchPlayerRepository = matchPlayerRepository;
        this.inningsRepository = inningsRepository;
        this.scoreEventRepository = scoreEventRepository;
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
                .build();

        Match savedMatch = matchRepository.save(match);
        return mapToResponseDTO(savedMatch);
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
        String dismissalType = request.getDismissalType() != null ? request.getDismissalType().toUpperCase() : "NONE";

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
                .dismissalType(dismissalType)
                .fielder(fielder)
                .build();

        ScoreEvent savedEvent = scoreEventRepository.save(event);

        // Update Innings stats
        int runsScored = request.getRunsOffBat() + request.getExtraRuns();
        innings.setTotalRuns(innings.getTotalRuns() + runsScored);

        if (request.isWicket()) {
            innings.setTotalWickets(innings.getTotalWickets() + 1);
        }

        // Calculate legal balls in this innings
        List<ScoreEvent> allEvents = scoreEventRepository.findByInningsIdOrderByOverNumberAscBallNumberAsc(innings.getId());
        long legalBallsCount = allEvents.stream()
                .filter(e -> !e.getExtraType().equalsIgnoreCase("WD") && !e.getExtraType().equalsIgnoreCase("NB"))
                .count();

        long overs = legalBallsCount / 6;
        long balls = legalBallsCount % 6;
        innings.setTotalOvers(BigDecimal.valueOf(overs + balls / 10.0));

        inningsRepository.save(innings);

        // Broadcast live match update
        broadcastMatchUpdate(match.getId());

        return mapToScoreEventResponseDTO(savedEvent);
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
    public MatchResponseDTO completeMatch(String matchId, String winnerId, String resultMarginDetail) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        Team winner = teamRepository.findById(winnerId)
                .orElseThrow(() -> new IllegalArgumentException("Winner team not found with id: " + winnerId));

        match.setWinner(winner);
        match.setResultMarginDetail(resultMarginDetail);
        match.setStatus("COMPLETED");
        Match savedMatch = matchRepository.save(match);

        broadcastMatchUpdate(match.getId());

        return mapToResponseDTO(savedMatch);
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
                .build();
    }

    private InningsResponseDTO mapToInningsResponseDTO(Innings innings) {
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
