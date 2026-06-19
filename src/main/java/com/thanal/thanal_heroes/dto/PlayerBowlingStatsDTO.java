package com.thanal.thanal_heroes.dto;

import java.math.BigDecimal;

public class PlayerBowlingStatsDTO {
    private int matchesPlayed;
    private int inningsBowled;
    private BigDecimal oversBowled;
    private int runsConceded;
    private int wickets;
    private String bestBowling;
    private BigDecimal average;
    private BigDecimal economyRate;
    private BigDecimal strikeRate;

    public PlayerBowlingStatsDTO() {}

    public PlayerBowlingStatsDTO(int matchesPlayed, int inningsBowled, BigDecimal oversBowled, int runsConceded, int wickets, String bestBowling, BigDecimal average, BigDecimal economyRate, BigDecimal strikeRate) {
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
        private int matchesPlayed;
        private int inningsBowled;
        private BigDecimal oversBowled;
        private int runsConceded;
        private int wickets;
        private String bestBowling;
        private BigDecimal average;
        private BigDecimal economyRate;
        private BigDecimal strikeRate;

        public Builder matchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; return this; }
        public Builder inningsBowled(int inningsBowled) { this.inningsBowled = inningsBowled; return this; }
        public Builder oversBowled(BigDecimal oversBowled) { this.oversBowled = oversBowled; return this; }
        public Builder runsConceded(int runsConceded) { this.runsConceded = runsConceded; return this; }
        public Builder wickets(int wickets) { this.wickets = wickets; return this; }
        public Builder bestBowling(String bestBowling) { this.bestBowling = bestBowling; return this; }
        public Builder average(BigDecimal average) { this.average = average; return this; }
        public Builder economyRate(BigDecimal economyRate) { this.economyRate = economyRate; return this; }
        public Builder strikeRate(BigDecimal strikeRate) { this.strikeRate = strikeRate; return this; }
        public PlayerBowlingStatsDTO build() {
            return new PlayerBowlingStatsDTO(matchesPlayed, inningsBowled, oversBowled, runsConceded, wickets, bestBowling, average, economyRate, strikeRate);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
