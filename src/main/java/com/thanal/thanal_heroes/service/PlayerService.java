package com.thanal.thanal_heroes.service;

import com.thanal.thanal_heroes.dto.PlayerRequestDTO;
import com.thanal.thanal_heroes.dto.PlayerResponseDTO;
import com.thanal.thanal_heroes.model.Player;
import com.thanal.thanal_heroes.model.User;
import com.thanal.thanal_heroes.repository.PlayerRepository;
import com.thanal.thanal_heroes.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;

    public PlayerService(PlayerRepository playerRepository, UserRepository userRepository) {
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PlayerResponseDTO createPlayer(PlayerRequestDTO request) {
        User user = null;
        if (request.getUserId() != null && !request.getUserId().trim().isEmpty()) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));
            
            // Check if user is already linked to another player
            if (playerRepository.findByUserId(user.getId()).isPresent()) {
                throw new IllegalArgumentException("User is already linked to a player profile");
            }
        }

        Player player = Player.builder()
                .name(request.getName())
                .user(user)
                .profilePictureUrl(request.getProfilePictureUrl())
                .battingStyle(request.getBattingStyle())
                .bowlingStyle(request.getBowlingStyle())
                .build();

        Player savedPlayer = playerRepository.save(player);
        return mapToResponseDTO(savedPlayer);
    }

    @Transactional
    public PlayerResponseDTO updatePlayer(String id, PlayerRequestDTO request) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + id));

        player.setName(request.getName());
        player.setProfilePictureUrl(request.getProfilePictureUrl());
        player.setBattingStyle(request.getBattingStyle());
        player.setBowlingStyle(request.getBowlingStyle());

        if (request.getUserId() != null && !request.getUserId().trim().isEmpty()) {
            if (player.getUser() == null || !player.getUser().getId().equals(request.getUserId())) {
                User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));
                if (playerRepository.findByUserId(user.getId()).isPresent()) {
                    throw new IllegalArgumentException("User is already linked to a player profile");
                }
                player.setUser(user);
            }
        } else {
            player.setUser(null);
        }

        Player updatedPlayer = playerRepository.save(player);
        return mapToResponseDTO(updatedPlayer);
    }

    @Transactional(readOnly = true)
    public PlayerResponseDTO getPlayer(String id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + id));
        return mapToResponseDTO(player);
    }

    @Transactional(readOnly = true)
    public Page<PlayerResponseDTO> searchPlayers(String name, Pageable pageable) {
        return playerRepository.searchByName(name, pageable).map(this::mapToResponseDTO);
    }

    @Transactional
    public void deletePlayer(String id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + id));
        player.setDeleted(true);
        playerRepository.save(player);
    }

    public PlayerResponseDTO mapToResponseDTO(Player player) {
        return PlayerResponseDTO.builder()
                .id(player.getId())
                .userId(player.getUser() != null ? player.getUser().getId() : null)
                .username(player.getUser() != null ? player.getUser().getUsername() : null)
                .name(player.getName())
                .profilePictureUrl(player.getProfilePictureUrl())
                .battingStyle(player.getBattingStyle())
                .bowlingStyle(player.getBowlingStyle())
                .build();
    }
}
