package com.thanal.thanal_heroes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "innings")
@SQLRestriction("is_deleted = false")
public class Innings extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batting_team_id", nullable = false)
    private Team battingTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bowling_team_id", nullable = false)
    private Team bowlingTeam;

    @Column(name = "innings_number", nullable = false)
    private int inningsNumber;

    @Column(name = "total_runs", nullable = false)
    private int totalRuns = 0;

    @Column(name = "total_wickets", nullable = false)
    private int totalWickets = 0;

    @Column(name = "total_overs", precision = 4, scale = 1, nullable = false)
    private BigDecimal totalOvers = BigDecimal.ZERO;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    public Innings() {}

    public Innings(Match match, Team battingTeam, Team bowlingTeam, int inningsNumber, int totalRuns, int totalWickets, BigDecimal totalOvers, boolean isCompleted) {
        this.match = match;
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;
        this.inningsNumber = inningsNumber;
        this.totalRuns = totalRuns;
        this.totalWickets = totalWickets;
        this.totalOvers = totalOvers;
        this.isCompleted = isCompleted;
    }

    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }
    public Team getBattingTeam() { return battingTeam; }
    public void setBattingTeam(Team battingTeam) { this.battingTeam = battingTeam; }
    public Team getBowlingTeam() { return bowlingTeam; }
    public void setBowlingTeam(Team bowlingTeam) { this.bowlingTeam = bowlingTeam; }
    public int getInningsNumber() { return inningsNumber; }
    public void setInningsNumber(int inningsNumber) { this.inningsNumber = inningsNumber; }
    public int getTotalRuns() { return totalRuns; }
    public void setTotalRuns(int totalRuns) { this.totalRuns = totalRuns; }
    public int getTotalWickets() { return totalWickets; }
    public void setTotalWickets(int totalWickets) { this.totalWickets = totalWickets; }
    public BigDecimal getTotalOvers() { return totalOvers; }
    public void setTotalOvers(BigDecimal totalOvers) { this.totalOvers = totalOvers; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public static class Builder {
        private Match match;
        private Team battingTeam;
        private Team bowlingTeam;
        private int inningsNumber;
        private int totalRuns = 0;
        private int totalWickets = 0;
        private BigDecimal totalOvers = BigDecimal.ZERO;
        private boolean isCompleted = false;

        public Builder match(Match match) { this.match = match; return this; }
        public Builder battingTeam(Team battingTeam) { this.battingTeam = battingTeam; return this; }
        public Builder bowlingTeam(Team bowlingTeam) { this.bowlingTeam = bowlingTeam; return this; }
        public Builder inningsNumber(int inningsNumber) { this.inningsNumber = inningsNumber; return this; }
        public Builder totalRuns(int totalRuns) { this.totalRuns = totalRuns; return this; }
        public Builder totalWickets(int totalWickets) { this.totalWickets = totalWickets; return this; }
        public Builder totalOvers(BigDecimal totalOvers) { this.totalOvers = totalOvers; return this; }
        public Builder isCompleted(boolean isCompleted) { this.isCompleted = isCompleted; return this; }
        public Innings build() {
            return new Innings(match, battingTeam, bowlingTeam, inningsNumber, totalRuns, totalWickets, totalOvers, isCompleted);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
