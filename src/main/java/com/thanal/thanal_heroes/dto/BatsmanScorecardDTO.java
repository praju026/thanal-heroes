package com.thanal.thanal_heroes.dto;

public class BatsmanScorecardDTO {
    private String batsmanId;
    private String batsmanName;
    private int runs;
    private int ballsFaced;
    private int fours;
    private int sixes;
    private boolean isOut;
    private String dismissalDetail;

    public BatsmanScorecardDTO() {}

    public BatsmanScorecardDTO(String batsmanId, String batsmanName, int runs, int ballsFaced, int fours, int sixes, boolean isOut, String dismissalDetail) {
        this.batsmanId = batsmanId;
        this.batsmanName = batsmanName;
        this.runs = runs;
        this.ballsFaced = ballsFaced;
        this.fours = fours;
        this.sixes = sixes;
        this.isOut = isOut;
        this.dismissalDetail = dismissalDetail;
    }

    public String getBatsmanId() { return batsmanId; }
    public void setBatsmanId(String batsmanId) { this.batsmanId = batsmanId; }
    public String getBatsmanName() { return batsmanName; }
    public void setBatsmanName(String batsmanName) { this.batsmanName = batsmanName; }
    public int getRuns() { return runs; }
    public void setRuns(int runs) { this.runs = runs; }
    public int getBallsFaced() { return ballsFaced; }
    public void setBallsFaced(int ballsFaced) { this.ballsFaced = ballsFaced; }
    public int getFours() { return fours; }
    public void setFours(int fours) { this.fours = fours; }
    public int getSixes() { return sixes; }
    public void setSixes(int sixes) { this.sixes = sixes; }
    public boolean getIsOut() { return isOut; }
    public void setIsOut(boolean out) { isOut = out; }
    public String getDismissalDetail() { return dismissalDetail; }
    public void setDismissalDetail(String dismissalDetail) { this.dismissalDetail = dismissalDetail; }
}
