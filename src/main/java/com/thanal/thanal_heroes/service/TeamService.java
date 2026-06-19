package com.thanal.thanal_heroes.service;

import com.thanal.thanal_heroes.dto.PlayerResponseDTO;
import com.thanal.thanal_heroes.dto.TeamRequestDTO;
import com.thanal.thanal_heroes.dto.TeamResponseDTO;
import com.thanal.thanal_heroes.model.Player;
import com.thanal.thanal_heroes.model.Team;
import com.thanal.thanal_heroes.model.TeamPlayer;
import com.thanal.thanal_heroes.model.TeamPlayerId;
import com.thanal.thanal_heroes.repository.PlayerRepository;
import com.thanal.thanal_heroes.repository.TeamPlayerRepository;
import com.thanal.thanal_heroes.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final PlayerService playerService;

    public TeamService(TeamRepository teamRepository,
                       PlayerRepository playerRepository,
                       TeamPlayerRepository teamPlayerRepository,
                       PlayerService playerService) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.teamPlayerRepository = teamPlayerRepository;
        this.playerService = playerService;
    }

    @Transactional
    public TeamResponseDTO createTeam(TeamRequestDTO request) {
        if (teamRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Team name already exists: " + request.getName());
        }

        Team team = Team.builder()
                .name(request.getName())
                .logoUrl(request.getLogoUrl())
                .build();

        Team savedTeam = teamRepository.save(team);
        return mapToResponseDTO(savedTeam, List.of());
    }

    @Transactional
    public TeamResponseDTO updateTeam(String id, TeamRequestDTO request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + id));

        if (!team.getName().equalsIgnoreCase(request.getName()) && teamRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Team name already exists: " + request.getName());
        }

        team.setName(request.getName());
        team.setLogoUrl(request.getLogoUrl());
        Team updatedTeam = teamRepository.save(team);

        List<TeamPlayer> activeMembers = teamPlayerRepository.findActivePlayersByTeamId(id);
        List<PlayerResponseDTO> players = activeMembers.stream()
                .map(tp -> playerService.mapToResponseDTO(tp.getPlayer()))
                .collect(Collectors.toList());

        return mapToResponseDTO(updatedTeam, players);
    }

    @Transactional(readOnly = true)
    public TeamResponseDTO getTeam(String id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + id));

        List<TeamPlayer> activeMembers = teamPlayerRepository.findActivePlayersByTeamId(id);
        List<PlayerResponseDTO> players = activeMembers.stream()
                .map(tp -> playerService.mapToResponseDTO(tp.getPlayer()))
                .collect(Collectors.toList());

        return mapToResponseDTO(team, players);
    }

    @Transactional(readOnly = true)
    public List<TeamResponseDTO> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(team -> {
                    List<TeamPlayer> activeMembers = teamPlayerRepository.findActivePlayersByTeamId(team.getId());
                    List<PlayerResponseDTO> players = activeMembers.stream()
                            .map(tp -> playerService.mapToResponseDTO(tp.getPlayer()))
                            .collect(Collectors.toList());
                    return mapToResponseDTO(team, players);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void addPlayerToTeam(String teamId, String playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + teamId));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + playerId));

        // Check if player is already active in team
        teamPlayerRepository.findActiveTeamPlayer(teamId, playerId).ifPresent(tp -> {
            throw new IllegalArgumentException("Player is already an active member of this team");
        });

        TeamPlayer teamPlayer = TeamPlayer.builder()
                .id(new TeamPlayerId(teamId, playerId))
                .team(team)
                .player(player)
                .joinedAt(LocalDateTime.now())
                .build();

        teamPlayerRepository.save(teamPlayer);
    }

    @Transactional
    public void removePlayerFromTeam(String teamId, String playerId) {
        TeamPlayer teamPlayer = teamPlayerRepository.findActiveTeamPlayer(teamId, playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player is not an active member of this team"));

        teamPlayer.setLeftAt(LocalDateTime.now());
        teamPlayerRepository.save(teamPlayer);
    }

    @Transactional
    public void deleteTeam(String id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + id));
        team.setDeleted(true);
        teamRepository.save(team);
    }

    private TeamResponseDTO mapToResponseDTO(Team team, List<PlayerResponseDTO> players) {
        return TeamResponseDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .logoUrl(team.getLogoUrl())
                .players(players)
                .build();
    }
}
