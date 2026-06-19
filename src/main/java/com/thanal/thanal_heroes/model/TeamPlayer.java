package com.thanal.thanal_heroes.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_players")
public class TeamPlayer {

    @EmbeddedId
    private TeamPlayerId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playerId")
    private Player player;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    public TeamPlayer() {}

    public TeamPlayer(TeamPlayerId id, Team team, Player player, LocalDateTime joinedAt, LocalDateTime leftAt) {
        this.id = id;
        this.team = team;
        this.player = player;
        this.joinedAt = joinedAt;
        this.leftAt = leftAt;
    }

    public TeamPlayerId getId() { return id; }
    public void setId(TeamPlayerId id) { this.id = id; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    public LocalDateTime getLeftAt() { return leftAt; }
    public void setLeftAt(LocalDateTime leftAt) { this.leftAt = leftAt; }

    public static class Builder {
        private TeamPlayerId id;
        private Team team;
        private Player player;
        private LocalDateTime joinedAt;
        private LocalDateTime leftAt;

        public Builder id(TeamPlayerId id) { this.id = id; return this; }
        public Builder team(Team team) { this.team = team; return this; }
        public Builder player(Player player) { this.player = player; return this; }
        public Builder joinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; return this; }
        public Builder leftAt(LocalDateTime leftAt) { this.leftAt = leftAt; return this; }
        public TeamPlayer build() {
            return new TeamPlayer(id, team, player, joinedAt, leftAt);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
