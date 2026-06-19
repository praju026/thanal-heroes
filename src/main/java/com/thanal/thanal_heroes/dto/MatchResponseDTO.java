package com.thanal.thanal_heroes.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MatchResponseDTO {
    private String id;
    private String tournamentId;
    private String tournamentName;
    private String team1Id;
    private String team1Name;
    private String team2Id;
    private String team2Name;
    private LocalDateTime matchDate;
    private String status;
    private String tossWinnerId;
    private String tossWinnerName;
    private String tossDecision;
    private String winnerId;
    private String winnerName;
    private String resultMarginDetail;
    private List<InningsResponseDTO> innings;
    private Integer overs;

    public MatchResponseDTO() {}

    public MatchResponseDTO(String id, String tournamentId, String tournamentName, String team1Id, String team1Name, String team2Id, String team2Name, LocalDateTime matchDate, String status, String tossWinnerId, String tossWinnerName, String tossDecision, String winnerId, String winnerName, String resultMarginDetail, List<InningsResponseDTO> innings, Integer overs) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.team1Id = team1Id;
        this.team1Name = team1Name;
        this.team2Id = team2Id;
        this.team2Name = team2Name;
        this.matchDate = matchDate;
        this.status = status;
        this.tossWinnerId = tossWinnerId;
        this.tossWinnerName = tossWinnerName;
        this.tossDecision = tossDecision;
        this.winnerId = winnerId;
        this.winnerName = winnerName;
        this.resultMarginDetail = resultMarginDetail;
        this.innings = innings;
        this.overs = overs;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTournamentId() { return tournamentId; }
    public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; }
    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }
    public String getTeam1Id() { return team1Id; }
    public void setTeam1Id(String team1Id) { this.team1Id = team1Id; }
    public String getTeam1Name() { return team1Name; }
    public void setTeam1Name(String team1Name) { this.team1Name = team1Name; }
    public String getTeam2Id() { return team2Id; }
    public void setTeam2Id(String team2Id) { this.team2Id = team2Id; }
    public String getTeam2Name() { return team2Name; }
    public void setTeam2Name(String team2Name) { this.team2Name = team2Name; }
    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTossWinnerId() { return tossWinnerId; }
    public void setTossWinnerId(String tossWinnerId) { this.tossWinnerId = tossWinnerId; }
    public String getTossWinnerName() { return tossWinnerName; }
    public void setTossWinnerName(String tossWinnerName) { this.tossWinnerName = tossWinnerName; }
    public String getTossDecision() { return tossDecision; }
    public void setTossDecision(String tossDecision) { this.tossDecision = tossDecision; }
    public String getWinnerId() { return winnerId; }
    public void setWinnerId(String winnerId) { this.winnerId = winnerId; }
    public String getWinnerName() { return winnerName; }
    public void setWinnerName(String winnerName) { this.winnerName = winnerName; }
    public String getResultMarginDetail() { return resultMarginDetail; }
    public void setResultMarginDetail(String resultMarginDetail) { this.resultMarginDetail = resultMarginDetail; }
    public List<InningsResponseDTO> getInnings() { return innings; }
    public void setInnings(List<InningsResponseDTO> innings) { this.innings = innings; }
    public Integer getOvers() { return overs; }
    public void setOvers(Integer overs) { this.overs = overs; }

    public static class Builder {
        private String id;
        private String tournamentId;
        private String tournamentName;
        private String team1Id;
        private String team1Name;
        private String team2Id;
        private String team2Name;
        private LocalDateTime matchDate;
        private String status;
        private String tossWinnerId;
        private String tossWinnerName;
        private String tossDecision;
        private String winnerId;
        private String winnerName;
        private String resultMarginDetail;
        private List<InningsResponseDTO> innings;
        private Integer overs;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tournamentId(String tournamentId) { this.tournamentId = tournamentId; return this; }
        public Builder tournamentName(String tournamentName) { this.tournamentName = tournamentName; return this; }
        public Builder team1Id(String team1Id) { this.team1Id = team1Id; return this; }
        public Builder team1Name(String team1Name) { this.team1Name = team1Name; return this; }
        public Builder team2Id(String team2Id) { this.team2Id = team2Id; return this; }
        public Builder team2Name(String team2Name) { this.team2Name = team2Name; return this; }
        public Builder matchDate(LocalDateTime matchDate) { this.matchDate = matchDate; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder tossWinnerId(String tossWinnerId) { this.tossWinnerId = tossWinnerId; return this; }
        public Builder tossWinnerName(String tossWinnerName) { this.tossWinnerName = tossWinnerName; return this; }
        public Builder tossDecision(String tossDecision) { this.tossDecision = tossDecision; return this; }
        public Builder winnerId(String winnerId) { this.winnerId = winnerId; return this; }
        public Builder winnerName(String winnerName) { this.winnerName = winnerName; return this; }
        public Builder resultMarginDetail(String resultMarginDetail) { this.resultMarginDetail = resultMarginDetail; return this; }
        public Builder innings(List<InningsResponseDTO> innings) { this.innings = innings; return this; }
        public Builder overs(Integer overs) { this.overs = overs; return this; }
        public MatchResponseDTO build() {
            return new MatchResponseDTO(id, tournamentId, tournamentName, team1Id, team1Name, team2Id, team2Name, matchDate, status, tossWinnerId, tossWinnerName, tossDecision, winnerId, winnerName, resultMarginDetail, innings, overs);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
