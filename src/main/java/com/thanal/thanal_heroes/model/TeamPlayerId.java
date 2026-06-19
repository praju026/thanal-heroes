package com.thanal.thanal_heroes.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TeamPlayerId implements Serializable {

    private String teamId;
    private String playerId;

    public TeamPlayerId() {}

    public TeamPlayerId(String teamId, String playerId) {
        this.teamId = teamId;
        this.playerId = playerId;
    }

    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamPlayerId that = (TeamPlayerId) o;
        return Objects.equals(teamId, that.teamId) &&
               Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, playerId);
    }
}
