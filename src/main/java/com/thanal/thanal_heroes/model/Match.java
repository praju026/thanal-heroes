package com.thanal.thanal_heroes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@SQLRestriction("is_deleted = false")
public class Match extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team1_id", nullable = false)
    private Team team1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team2_id", nullable = false)
    private Team team2;

    @Column(name = "match_date", nullable = false)
    private LocalDateTime matchDate;

    @Column(name = "status", length = 50, nullable = false)
    private String status; // SCHEDULED, TOSS_PENDING, IN_PROGRESS, INNINGS_BREAK, COMPLETED, ABANDONED, CANCELLED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toss_winner_id")
    private Team tossWinner;

    @Column(name = "toss_decision", length = 50)
    private String tossDecision; // BAT, BOWL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private Team winner;

    @Column(name = "result_margin_detail", length = 255)
    private String resultMarginDetail;

    @Column(name = "overs", nullable = false)
    private Integer overs = 20;

    public Match() {}

    public Match(Tournament tournament, Team team1, Team team2, LocalDateTime matchDate, String status, Team tossWinner, String tossDecision, Team winner, String resultMarginDetail, Integer overs) {
        this.tournament = tournament;
        this.team1 = team1;
        this.team2 = team2;
        this.matchDate = matchDate;
        this.status = status;
        this.tossWinner = tossWinner;
        this.tossDecision = tossDecision;
        this.winner = winner;
        this.resultMarginDetail = resultMarginDetail;
        this.overs = overs != null ? overs : 20;
    }

    public Tournament getTournament() { return tournament; }
    public void setTournament(Tournament tournament) { this.tournament = tournament; }
    public Team getTeam1() { return team1; }
    public void setTeam1(Team team1) { this.team1 = team1; }
    public Team getTeam2() { return team2; }
    public void setTeam2(Team team2) { this.team2 = team2; }
    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Team getTossWinner() { return tossWinner; }
    public void setTossWinner(Team tossWinner) { this.tossWinner = tossWinner; }
    public String getTossDecision() { return tossDecision; }
    public void setTossDecision(String tossDecision) { this.tossDecision = tossDecision; }
    public Team getWinner() { return winner; }
    public void setWinner(Team winner) { this.winner = winner; }
    public String getResultMarginDetail() { return resultMarginDetail; }
    public void setResultMarginDetail(String resultMarginDetail) { this.resultMarginDetail = resultMarginDetail; }
    public Integer getOvers() { return overs; }
    public void setOvers(Integer overs) { this.overs = overs; }

    public static class Builder {
        private Tournament tournament;
        private Team team1;
        private Team team2;
        private LocalDateTime matchDate;
        private String status;
        private Team tossWinner;
        private String tossDecision;
        private Team winner;
        private String resultMarginDetail;
        private Integer overs = 20;

        public Builder tournament(Tournament tournament) { this.tournament = tournament; return this; }
        public Builder team1(Team team1) { this.team1 = team1; return this; }
        public Builder team2(Team team2) { this.team2 = team2; return this; }
        public Builder matchDate(LocalDateTime matchDate) { this.matchDate = matchDate; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder tossWinner(Team tossWinner) { this.tossWinner = tossWinner; return this; }
        public Builder tossDecision(String tossDecision) { this.tossDecision = tossDecision; return this; }
        public Builder winner(Team winner) { this.winner = winner; return this; }
        public Builder resultMarginDetail(String resultMarginDetail) { this.resultMarginDetail = resultMarginDetail; return this; }
        public Builder overs(Integer overs) { this.overs = overs; return this; }
        public Match build() {
            return new Match(tournament, team1, team2, matchDate, status, tossWinner, tossDecision, winner, resultMarginDetail, overs);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
