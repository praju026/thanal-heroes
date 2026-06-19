package com.thanal.thanal_heroes.dto;

import java.math.BigDecimal;

public class InningsResponseDTO {
    private String id;
    private int inningsNumber;
    private String battingTeamId;
    private String battingTeamName;
    private String bowlingTeamId;
    private String bowlingTeamName;
    private int totalRuns;
    private int totalWickets;
    private BigDecimal totalOvers;
    private boolean completed;

    public InningsResponseDTO() {}

    public InningsResponseDTO(String id, int inningsNumber, String battingTeamId, String battingTeamName, String bowlingTeamId, String bowlingTeamName, int totalRuns, int totalWickets, BigDecimal totalOvers, boolean completed) {
        this.id = id;
        this.inningsNumber = inningsNumber;
        this.battingTeamId = battingTeamId;
        this.battingTeamName = battingTeamName;
        this.bowlingTeamId = bowlingTeamId;
        this.bowlingTeamName = bowlingTeamName;
        this.totalRuns = totalRuns;
        this.totalWickets = totalWickets;
        this.totalOvers = totalOvers;
        this.completed = completed;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getInningsNumber() { return inningsNumber; }
    public void setInningsNumber(int inningsNumber) { this.inningsNumber = inningsNumber; }
    public String getBattingTeamId() { return battingTeamId; }
    public void setBattingTeamId(String battingTeamId) { this.battingTeamId = battingTeamId; }
    public String getBattingTeamName() { return battingTeamName; }
    public void setBattingTeamName(String battingTeamName) { this.battingTeamName = battingTeamName; }
    public String getBowlingTeamId() { return bowlingTeamId; }
    public void setBowlingTeamId(String bowlingTeamId) { this.bowlingTeamId = bowlingTeamId; }
    public String getBowlingTeamName() { return bowlingTeamName; }
    public void setBowlingTeamName(String bowlingTeamName) { this.bowlingTeamName = bowlingTeamName; }
    public int getTotalRuns() { return totalRuns; }
    public void setTotalRuns(int totalRuns) { this.totalRuns = totalRuns; }
    public int getTotalWickets() { return totalWickets; }
    public void setTotalWickets(int totalWickets) { this.totalWickets = totalWickets; }
    public BigDecimal getTotalOvers() { return totalOvers; }
    public void setTotalOvers(BigDecimal totalOvers) { this.totalOvers = totalOvers; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public static class Builder {
        private String id;
        private int inningsNumber;
        private String battingTeamId;
        private String battingTeamName;
        private String bowlingTeamId;
        private String bowlingTeamName;
        private int totalRuns;
        private int totalWickets;
        private BigDecimal totalOvers;
        private boolean completed;

        public Builder id(String id) { this.id = id; return this; }
        public Builder inningsNumber(int inningsNumber) { this.inningsNumber = inningsNumber; return this; }
        public Builder battingTeamId(String battingTeamId) { this.battingTeamId = battingTeamId; return this; }
        public Builder battingTeamName(String battingTeamName) { this.battingTeamName = battingTeamName; return this; }
        public Builder bowlingTeamId(String bowlingTeamId) { this.bowlingTeamId = bowlingTeamId; return this; }
        public Builder bowlingTeamName(String bowlingTeamName) { this.bowlingTeamName = bowlingTeamName; return this; }
        public Builder totalRuns(int totalRuns) { this.totalRuns = totalRuns; return this; }
        public Builder totalWickets(int totalWickets) { this.totalWickets = totalWickets; return this; }
        public Builder totalOvers(BigDecimal totalOvers) { this.totalOvers = totalOvers; return this; }
        public Builder completed(boolean completed) { this.completed = completed; return this; }
        public InningsResponseDTO build() {
            return new InningsResponseDTO(id, inningsNumber, battingTeamId, battingTeamName, bowlingTeamId, bowlingTeamName, totalRuns, totalWickets, totalOvers, completed);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
