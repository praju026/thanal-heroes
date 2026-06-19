package com.thanal.thanal_heroes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "match_players")
public class MatchPlayer {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "playing_xi", nullable = false)
    private boolean playingXi = true;

    public MatchPlayer() {}

    public MatchPlayer(Match match, Player player, Team team, boolean playingXi) {
        this.match = match;
        this.player = player;
        this.team = team;
        this.playingXi = playingXi;
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public boolean isPlayingXi() { return playingXi; }
    public void setPlayingXi(boolean playingXi) { this.playingXi = playingXi; }

    public static class Builder {
        private Match match;
        private Player player;
        private Team team;
        private boolean playingXi = true;

        public Builder match(Match match) { this.match = match; return this; }
        public Builder player(Player player) { this.player = player; return this; }
        public Builder team(Team team) { this.team = team; return this; }
        public Builder playingXi(boolean playingXi) { this.playingXi = playingXi; return this; }
        public MatchPlayer build() {
            return new MatchPlayer(match, player, team, playingXi);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
