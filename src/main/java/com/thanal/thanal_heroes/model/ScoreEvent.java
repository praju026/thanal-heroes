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
@Table(name = "score_events")
public class ScoreEvent {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "innings_id", nullable = false)
    private Innings innings;

    @Column(name = "over_number", nullable = false)
    private int overNumber;

    @Column(name = "ball_number", nullable = false)
    private int ballNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batsman_id", nullable = false)
    private Player batsman;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "non_striker_id", nullable = false)
    private Player nonStriker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bowler_id", nullable = false)
    private Player bowler;

    @Column(name = "runs_off_bat", nullable = false)
    private int runsOffBat = 0;

    @Column(name = "extra_runs", nullable = false)
    private int extraRuns = 0;

    @Column(name = "extra_type", length = 50, nullable = false)
    private String extraType = "NONE"; // WD, NB, LB, B, PENALTY, NONE

    @Column(name = "is_wicket", nullable = false)
    private boolean wicket = false;

    @Column(name = "dismissal_type", length = 50, nullable = false)
    private String dismissalType = "NONE"; // BOWLED, CAUGHT, RUN_OUT, LBW, STUMPED, HIT_WICKET, RETIRED, OBSTRUCTING, DOUBLE_HIT, TIMED_OUT, NONE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fielder_id")
    private Player fielder;

    public ScoreEvent() {}

    public ScoreEvent(Match match, Innings innings, int overNumber, int ballNumber, Player batsman, Player nonStriker, Player bowler, int runsOffBat, int extraRuns, String extraType, boolean wicket, String dismissalType, Player fielder) {
        this.match = match;
        this.innings = innings;
        this.overNumber = overNumber;
        this.ballNumber = ballNumber;
        this.batsman = batsman;
        this.nonStriker = nonStriker;
        this.bowler = bowler;
        this.runsOffBat = runsOffBat;
        this.extraRuns = extraRuns;
        this.extraType = extraType;
        this.wicket = wicket;
        this.dismissalType = dismissalType;
        this.fielder = fielder;
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
    public Innings getInnings() { return innings; }
    public void setInnings(Innings innings) { this.innings = innings; }
    public int getOverNumber() { return overNumber; }
    public void setOverNumber(int overNumber) { this.overNumber = overNumber; }
    public int getBallNumber() { return ballNumber; }
    public void setBallNumber(int ballNumber) { this.ballNumber = ballNumber; }
    public Player getBatsman() { return batsman; }
    public void setBatsman(Player batsman) { this.batsman = batsman; }
    public Player getNonStriker() { return nonStriker; }
    public void setNonStriker(Player nonStriker) { this.nonStriker = nonStriker; }
    public Player getBowler() { return bowler; }
    public void setBowler(Player bowler) { this.bowler = bowler; }
    public int getRunsOffBat() { return runsOffBat; }
    public void setRunsOffBat(int runsOffBat) { this.runsOffBat = runsOffBat; }
    public int getExtraRuns() { return extraRuns; }
    public void setExtraRuns(int extraRuns) { this.extraRuns = extraRuns; }
    public String getExtraType() { return extraType; }
    public void setExtraType(String extraType) { this.extraType = extraType; }
    public boolean isWicket() { return wicket; }
    public void setWicket(boolean wicket) { this.wicket = wicket; }
    public String getDismissalType() { return dismissalType; }
    public void setDismissalType(String dismissalType) { this.dismissalType = dismissalType; }
    public Player getFielder() { return fielder; }
    public void setFielder(Player fielder) { this.fielder = fielder; }

    public static class Builder {
        private Match match;
        private Innings innings;
        private int overNumber;
        private int ballNumber;
        private Player batsman;
        private Player nonStriker;
        private Player bowler;
        private int runsOffBat = 0;
        private int extraRuns = 0;
        private String extraType = "NONE";
        private boolean wicket = false;
        private String dismissalType = "NONE";
        private Player fielder;

        public Builder match(Match match) { this.match = match; return this; }
        public Builder innings(Innings innings) { this.innings = innings; return this; }
        public Builder overNumber(int overNumber) { this.overNumber = overNumber; return this; }
        public Builder ballNumber(int ballNumber) { this.ballNumber = ballNumber; return this; }
        public Builder batsman(Player batsman) { this.batsman = batsman; return this; }
        public Builder nonStriker(Player nonStriker) { this.nonStriker = nonStriker; return this; }
        public Builder bowler(Player bowler) { this.bowler = bowler; return this; }
        public Builder runsOffBat(int runsOffBat) { this.runsOffBat = runsOffBat; return this; }
        public Builder extraRuns(int extraRuns) { this.extraRuns = extraRuns; return this; }
        public Builder extraType(String extraType) { this.extraType = extraType; return this; }
        public Builder wicket(boolean wicket) { this.wicket = wicket; return this; }
        public Builder dismissalType(String dismissalType) { this.dismissalType = dismissalType; return this; }
        public Builder fielder(Player fielder) { this.fielder = fielder; return this; }
        public ScoreEvent build() {
            return new ScoreEvent(match, innings, overNumber, ballNumber, batsman, nonStriker, bowler, runsOffBat, extraRuns, extraType, wicket, dismissalType, fielder);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
