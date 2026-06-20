package com.thanal.thanal_heroes.dto;

import java.math.BigDecimal;

public class BowlerScorecardDTO {
    private String bowlerId;
    private String bowlerName;
    private BigDecimal overs;
    private int runsConceded;
    private int wickets;
    private int maidens;
    private BigDecimal economy;

    public BowlerScorecardDTO() {}

    public BowlerScorecardDTO(String bowlerId, String bowlerName, BigDecimal overs, int runsConceded, int wickets, int maidens, BigDecimal economy) {
        this.bowlerId = bowlerId;
        this.bowlerName = bowlerName;
        this.overs = overs;
        this.runsConceded = runsConceded;
        this.wickets = wickets;
        this.maidens = maidens;
        this.economy = economy;
    }

    public String getBowlerId() { return bowlerId; }
    public void setBowlerId(String bowlerId) { this.bowlerId = bowlerId; }
    public String getBowlerName() { return bowlerName; }
    public void setBowlerName(String bowlerName) { this.bowlerName = bowlerName; }
    public BigDecimal getOvers() { return overs; }
    public void setOvers(BigDecimal overs) { this.overs = overs; }
    public int getRunsConceded() { return runsConceded; }
    public void setRunsConceded(int runsConceded) { this.runsConceded = runsConceded; }
    public int getWickets() { return wickets; }
    public void setWickets(int wickets) { this.wickets = wickets; }
    public int getMaidens() { return maidens; }
    public void setMaidens(int maidens) { this.maidens = maidens; }
    public BigDecimal getEconomy() { return economy; }
    public void setEconomy(BigDecimal economy) { this.economy = economy; }
}
