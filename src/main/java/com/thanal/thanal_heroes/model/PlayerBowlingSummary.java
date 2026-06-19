package com.thanal.thanal_heroes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "player_bowling_summary")
public class PlayerBowlingSummary {

    @Id
    @Column(name = "player_id", length = 36)
    private String playerId;

    @Column(name = "player_name", length = 100, nullable = false)
    private String playerName;

    @Column(name = "matches_played")
    private int matchesPlayed;

    @Column(name = "innings_bowled")
    private int inningsBowled;

    @Column(name = "overs_bowled", precision = 5, scale = 1)
    private BigDecimal oversBowled;

    @Column(name = "runs_conceded")
    private int runsConceded;

    @Column(name = "wickets")
    private int wickets;

    @Column(name = "best_bowling", length = 20)
    private String bestBowling;

    @Column(name = "average", precision = 5, scale = 2)
    private BigDecimal average;

    @Column(name = "economy_rate", precision = 5, scale = 2)
    private BigDecimal economyRate;

    @Column(name = "strike_rate", precision = 5, scale = 2)
    private BigDecimal strikeRate;

    public PlayerBowlingSummary() {}

    public PlayerBowlingSummary(String playerId, String playerName, int matchesPlayed, int inningsBowled, BigDecimal oversBowled, int runsConceded, int wickets, String bestBowling, BigDecimal average, BigDecimal economyRate, BigDecimal strikeRate) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.matchesPlayed = matchesPlayed;
        this.inningsBowled = inningsBowled;
        this.oversBowled = oversBowled;
        this.runsConceded = runsConceded;
        this.wickets = wickets;
        this.bestBowling = bestBowling;
        this.average = average;
        this.economyRate = economyRate;
        this.strikeRate = strikeRate;
    }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }
    public int getInningsBowled() { return inningsBowled; }
    public void setInningsBowled(int inningsBowled) { this.inningsBowled = inningsBowled; }
    public BigDecimal getOversBowled() { return oversBowled; }
    public void setOversBowled(BigDecimal oversBowled) { this.oversBowled = oversBowled; }
    public int getRunsConceded() { return runsConceded; }
    public void setRunsConceded(int runsConceded) { this.runsConceded = runsConceded; }
    public int getWickets() { return wickets; }
    public void setWickets(int wickets) { this.wickets = wickets; }
    public String getBestBowling() { return bestBowling; }
    public void setBestBowling(String bestBowling) { this.bestBowling = bestBowling; }
    public BigDecimal getAverage() { return average; }
    public void setAverage(BigDecimal average) { this.average = average; }
    public BigDecimal getEconomyRate() { return economyRate; }
    public void setEconomyRate(BigDecimal economyRate) { this.economyRate = economyRate; }
    public BigDecimal getStrikeRate() { return strikeRate; }
    public void setStrikeRate(BigDecimal strikeRate) { this.strikeRate = strikeRate; }

    public static class Builder {
        private String playerId;
        private String playerName;
        private int matchesPlayed;
        private int inningsBowled;
        private BigDecimal oversBowled;
        private int runsConceded;
        private int wickets;
        private String bestBowling;
        private BigDecimal average;
        private BigDecimal economyRate;
        private BigDecimal strikeRate;

        public Builder playerId(String playerId) { this.playerId = playerId; return this; }
        public Builder playerName(String playerName) { this.playerName = playerName; return this; }
        public Builder matchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; return this; }
        public Builder inningsBowled(int inningsBowled) { this.inningsBowled = inningsBowled; return this; }
        public Builder oversBowled(BigDecimal oversBowled) { this.oversBowled = oversBowled; return this; }
        public Builder runsConceded(int runsConceded) { this.runsConceded = runsConceded; return this; }
        public Builder wickets(int wickets) { this.wickets = wickets; return this; }
        public Builder bestBowling(String bestBowling) { this.bestBowling = bestBowling; return this; }
        public Builder average(BigDecimal average) { this.average = average; return this; }
        public Builder economyRate(BigDecimal economyRate) { this.economyRate = economyRate; return this; }
        public Builder strikeRate(BigDecimal strikeRate) { this.strikeRate = strikeRate; return this; }
        public PlayerBowlingSummary build() {
            return new PlayerBowlingSummary(playerId, playerName, matchesPlayed, inningsBowled, oversBowled, runsConceded, wickets, bestBowling, average, economyRate, strikeRate);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
