package com.thanal.thanal_heroes.dto;

public class ScoreEventResponseDTO {
    private String id;
    private int overNumber;
    private int ballNumber;
    private String batsmanId;
    private String batsmanName;
    private String nonStrikerId;
    private String nonStrikerName;
    private String bowlerId;
    private String bowlerName;
    private int runsOffBat;
    private int extraRuns;
    private String extraType;
    private boolean wicket;
    private String dismissalType;
    private String fielderId;
    private String fielderName;

    public ScoreEventResponseDTO() {}

    public ScoreEventResponseDTO(String id, int overNumber, int ballNumber, String batsmanId, String batsmanName, String nonStrikerId, String nonStrikerName, String bowlerId, String bowlerName, int runsOffBat, int extraRuns, String extraType, boolean wicket, String dismissalType, String fielderId, String fielderName) {
        this.id = id;
        this.overNumber = overNumber;
        this.ballNumber = ballNumber;
        this.batsmanId = batsmanId;
        this.batsmanName = batsmanName;
        this.nonStrikerId = nonStrikerId;
        this.nonStrikerName = nonStrikerName;
        this.bowlerId = bowlerId;
        this.bowlerName = bowlerName;
        this.runsOffBat = runsOffBat;
        this.extraRuns = extraRuns;
        this.extraType = extraType;
        this.wicket = wicket;
        this.dismissalType = dismissalType;
        this.fielderId = fielderId;
        this.fielderName = fielderName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getOverNumber() { return overNumber; }
    public void setOverNumber(int overNumber) { this.overNumber = overNumber; }
    public int getBallNumber() { return ballNumber; }
    public void setBallNumber(int ballNumber) { this.ballNumber = ballNumber; }
    public String getBatsmanId() { return batsmanId; }
    public void setBatsmanId(String batsmanId) { this.batsmanId = batsmanId; }
    public String getBatsmanName() { return batsmanName; }
    public void setBatsmanName(String batsmanName) { this.batsmanName = batsmanName; }
    public String getNonStrikerId() { return nonStrikerId; }
    public void setNonStrikerId(String nonStrikerId) { this.nonStrikerId = nonStrikerId; }
    public String getNonStrikerName() { return nonStrikerName; }
    public void setNonStrikerName(String nonStrikerName) { this.nonStrikerName = nonStrikerName; }
    public String getBowlerId() { return bowlerId; }
    public void setBowlerId(String bowlerId) { this.bowlerId = bowlerId; }
    public String getBowlerName() { return bowlerName; }
    public void setBowlerName(String bowlerName) { this.bowlerName = bowlerName; }
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
    public String getFielderName() { return fielderName; }
    public void setFielderName(String fielderName) { this.fielderName = fielderName; }

    public static class Builder {
        private String id;
        private int overNumber;
        private int ballNumber;
        private String batsmanId;
        private String batsmanName;
        private String nonStrikerId;
        private String nonStrikerName;
        private String bowlerId;
        private String bowlerName;
        private int runsOffBat;
        private int extraRuns;
        private String extraType;
        private boolean wicket;
        private String dismissalType;
        private String fielderId;
        private String fielderName;

        public Builder id(String id) { this.id = id; return this; }
        public Builder overNumber(int overNumber) { this.overNumber = overNumber; return this; }
        public Builder ballNumber(int ballNumber) { this.ballNumber = ballNumber; return this; }
        public Builder batsmanId(String batsmanId) { this.batsmanId = batsmanId; return this; }
        public Builder batsmanName(String batsmanName) { this.batsmanName = batsmanName; return this; }
        public Builder nonStrikerId(String nonStrikerId) { this.nonStrikerId = nonStrikerId; return this; }
        public Builder nonStrikerName(String nonStrikerName) { this.nonStrikerName = nonStrikerName; return this; }
        public Builder bowlerId(String bowlerId) { this.bowlerId = bowlerId; return this; }
        public Builder bowlerName(String bowlerName) { this.bowlerName = bowlerName; return this; }
        public Builder runsOffBat(int runsOffBat) { this.runsOffBat = runsOffBat; return this; }
        public Builder extraRuns(int extraRuns) { this.extraRuns = extraRuns; return this; }
        public Builder extraType(String extraType) { this.extraType = extraType; return this; }
        public Builder wicket(boolean wicket) { this.wicket = wicket; return this; }
        public Builder dismissalType(String dismissalType) { this.dismissalType = dismissalType; return this; }
        public Builder fielderId(String fielderId) { this.fielderId = fielderId; return this; }
        public Builder fielderName(String fielderName) { this.fielderName = fielderName; return this; }
        public ScoreEventResponseDTO build() {
            return new ScoreEventResponseDTO(id, overNumber, ballNumber, batsmanId, batsmanName, nonStrikerId, nonStrikerName, bowlerId, bowlerName, runsOffBat, extraRuns, extraType, wicket, dismissalType, fielderId, fielderName);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
