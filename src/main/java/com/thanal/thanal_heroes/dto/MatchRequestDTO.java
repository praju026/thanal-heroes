package com.thanal.thanal_heroes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MatchRequestDTO {

    private String tournamentId;

    @NotBlank(message = "Team 1 id is required")
    private String team1Id;

    @NotBlank(message = "Team 2 id is required")
    private String team2Id;

    @NotNull(message = "Match date is required")
    private LocalDateTime matchDate;

    private Integer overs;

    public MatchRequestDTO() {}

    public MatchRequestDTO(String tournamentId, String team1Id, String team2Id, LocalDateTime matchDate, Integer overs) {
        this.tournamentId = tournamentId;
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.matchDate = matchDate;
        this.overs = overs;
    }

    public String getTournamentId() { return tournamentId; }
    public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; }
    public String getTeam1Id() { return team1Id; }
    public void setTeam1Id(String team1Id) { this.team1Id = team1Id; }
    public String getTeam2Id() { return team2Id; }
    public void setTeam2Id(String team2Id) { this.team2Id = team2Id; }
    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }
    public Integer getOvers() { return overs; }
    public void setOvers(Integer overs) { this.overs = overs; }

    public static class Builder {
        private String tournamentId;
        private String team1Id;
        private String team2Id;
        private LocalDateTime matchDate;
        private Integer overs;

        public Builder tournamentId(String tournamentId) { this.tournamentId = tournamentId; return this; }
        public Builder team1Id(String team1Id) { this.team1Id = team1Id; return this; }
        public Builder team2Id(String team2Id) { this.team2Id = team2Id; return this; }
        public Builder matchDate(LocalDateTime matchDate) { this.matchDate = matchDate; return this; }
        public Builder overs(Integer overs) { this.overs = overs; return this; }
        public MatchRequestDTO build() {
            return new MatchRequestDTO(tournamentId, team1Id, team2Id, matchDate, overs);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
