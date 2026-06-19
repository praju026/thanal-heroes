package com.thanal.thanal_heroes.dto;

import java.math.BigDecimal;

public class PlayerBattingStatsDTO {
    private int matchesPlayed;
    private int inningsPlayed;
    private int totalRuns;
    private int ballsFaced;
    private int outs;
    private int highestScore;
    private BigDecimal average;
    private BigDecimal strikeRate;
    private int fifties;
    private int hundreds;

    public PlayerBattingStatsDTO() {}

    public PlayerBattingStatsDTO(int matchesPlayed, int inningsPlayed, int totalRuns, int ballsFaced, int outs, int highestScore, BigDecimal average, BigDecimal strikeRate, int fifties, int hundreds) {
        this.matchesPlayed = matchesPlayed;
        this.inningsPlayed = inningsPlayed;
        this.totalRuns = totalRuns;
        this.ballsFaced = ballsFaced;
        this.outs = outs;
        this.highestScore = highestScore;
        this.average = average;
        this.strikeRate = strikeRate;
        this.fifties = fifties;
        this.hundreds = hundreds;
    }

    public int getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }
    public int getInningsPlayed() { return inningsPlayed; }
    public void setInningsPlayed(int inningsPlayed) { this.inningsPlayed = inningsPlayed; }
    public int getTotalRuns() { return totalRuns; }
    public void setTotalRuns(int totalRuns) { this.totalRuns = totalRuns; }
    public int getBallsFaced() { return ballsFaced; }
    public void setBallsFaced(int ballsFaced) { this.ballsFaced = ballsFaced; }
    public int getOuts() { return outs; }
    public void setOuts(int outs) { this.outs = outs; }
    public int getHighestScore() { return highestScore; }
    public void setHighestScore(int highestScore) { this.highestScore = highestScore; }
    public BigDecimal getAverage() { return average; }
    public void setAverage(BigDecimal average) { this.average = average; }
    public BigDecimal getStrikeRate() { return strikeRate; }
    public void setStrikeRate(BigDecimal strikeRate) { this.strikeRate = strikeRate; }
    public int getFifties() { return fifties; }
    public void setFifties(int fifties) { this.fifties = fifties; }
    public int getHundreds() { return hundreds; }
    public void setHundreds(int hundreds) { this.hundreds = hundreds; }

    public static class Builder {
        private int matchesPlayed;
        private int inningsPlayed;
        private int totalRuns;
        private int ballsFaced;
        private int outs;
        private int highestScore;
        private BigDecimal average;
        private BigDecimal strikeRate;
        private int fifties;
        private int hundreds;

        public Builder matchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; return this; }
        public Builder inningsPlayed(int inningsPlayed) { this.inningsPlayed = inningsPlayed; return this; }
        public Builder totalRuns(int totalRuns) { this.totalRuns = totalRuns; return this; }
        public Builder ballsFaced(int ballsFaced) { this.ballsFaced = ballsFaced; return this; }
        public Builder outs(int outs) { this.outs = outs; return this; }
        public Builder highestScore(int highestScore) { this.highestScore = highestScore; return this; }
        public Builder average(BigDecimal average) { this.average = average; return this; }
        public Builder strikeRate(BigDecimal strikeRate) { this.strikeRate = strikeRate; return this; }
        public Builder fifties(int fifties) { this.fifties = fifties; return this; }
        public Builder hundreds(int hundreds) { this.hundreds = hundreds; return this; }
        public PlayerBattingStatsDTO build() {
            return new PlayerBattingStatsDTO(matchesPlayed, inningsPlayed, totalRuns, ballsFaced, outs, highestScore, average, strikeRate, fifties, hundreds);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
