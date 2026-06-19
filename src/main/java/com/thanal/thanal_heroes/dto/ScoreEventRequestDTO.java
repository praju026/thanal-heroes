package com.thanal.thanal_heroes.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ScoreEventRequestDTO {

    @NotNull(message = "Innings id is required")
    private String inningsId;

    @Min(value = 0, message = "Over number cannot be negative")
    private int overNumber;

    @Min(value = 1, message = "Ball number must be at least 1")
    @Max(value = 6, message = "Standard over has at most 6 valid balls")
    private int ballNumber;

    @NotBlank(message = "Batsman id is required")
    private String batsmanId;

    @NotBlank(message = "Non-striker id is required")
    private String nonStrikerId;

    @NotBlank(message = "Bowler id is required")
    private String bowlerId;

    @Min(value = 0, message = "Runs off bat cannot be negative")
    private int runsOffBat;

    @Min(value = 0, message = "Extra runs cannot be negative")
    private int extraRuns;

    private String extraType; // WD, NB, LB, B, PENALTY, NONE
    private boolean wicket;
    private String dismissalType; // BOWLED, CAUGHT, RUN_OUT, LBW, STUMPED, HIT_WICKET, RETIRED, OBSTRUCTING, DOUBLE_HIT, TIMED_OUT, NONE
    private String fielderId;

    public ScoreEventRequestDTO() {}

    public ScoreEventRequestDTO(String inningsId, int overNumber, int ballNumber, String batsmanId, String nonStrikerId, String bowlerId, int runsOffBat, int extraRuns, String extraType, boolean wicket, String dismissalType, String fielderId) {
        this.inningsId = inningsId;
        this.overNumber = overNumber;
        this.ballNumber = ballNumber;
        this.batsmanId = batsmanId;
        this.nonStrikerId = nonStrikerId;
        this.bowlerId = bowlerId;
        this.runsOffBat = runsOffBat;
        this.extraRuns = extraRuns;
        this.extraType = extraType;
        this.wicket = wicket;
        this.dismissalType = dismissalType;
        this.fielderId = fielderId;
    }

    public String getInningsId() { return inningsId; }
    public void setInningsId(String inningsId) { this.inningsId = inningsId; }
    public int getOverNumber() { return overNumber; }
    public void setOverNumber(int overNumber) { this.overNumber = overNumber; }
    public int getBallNumber() { return ballNumber; }
    public void setBallNumber(int ballNumber) { this.ballNumber = ballNumber; }
    public String getBatsmanId() { return batsmanId; }
    public void setBatsmanId(String batsmanId) { this.batsmanId = batsmanId; }
    public String getNonStrikerId() { return nonStrikerId; }
    public void setNonStrikerId(String nonStrikerId) { this.nonStrikerId = nonStrikerId; }
    public String getBowlerId() { return bowlerId; }
    public void setBowlerId(String bowlerId) { this.bowlerId = bowlerId; }
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
    public String getFielderId() { return fielderId; }
    public void setFielderId(String fielderId) { this.fielderId = fielderId; }

    public static class Builder {
        private String inningsId;
        private int overNumber;
        private int ballNumber;
        private String batsmanId;
        private String nonStrikerId;
        private String bowlerId;
        private int runsOffBat;
        private int extraRuns;
        private String extraType;
        private boolean wicket;
        private String dismissalType;
        private String fielderId;

        public Builder inningsId(String inningsId) { this.inningsId = inningsId; return this; }
        public Builder overNumber(int overNumber) { this.overNumber = overNumber; return this; }
        public Builder ballNumber(int ballNumber) { this.ballNumber = ballNumber; return this; }
        public Builder batsmanId(String batsmanId) { this.batsmanId = batsmanId; return this; }
        public Builder nonStrikerId(String nonStrikerId) { this.nonStrikerId = nonStrikerId; return this; }
        public Builder bowlerId(String bowlerId) { this.bowlerId = bowlerId; return this; }
        public Builder runsOffBat(int runsOffBat) { this.runsOffBat = runsOffBat; return this; }
        public Builder extraRuns(int extraRuns) { this.extraRuns = extraRuns; return this; }
        public Builder extraType(String extraType) { this.extraType = extraType; return this; }
        public Builder wicket(boolean wicket) { this.wicket = wicket; return this; }
        public Builder dismissalType(String dismissalType) { this.dismissalType = dismissalType; return this; }
        public Builder fielderId(String fielderId) { this.fielderId = fielderId; return this; }
        public ScoreEventRequestDTO build() {
            return new ScoreEventRequestDTO(inningsId, overNumber, ballNumber, batsmanId, nonStrikerId, bowlerId, runsOffBat, extraRuns, extraType, wicket, dismissalType, fielderId);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
