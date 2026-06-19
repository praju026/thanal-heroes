package com.thanal.thanal_heroes.dto;

public class PlayerCareerStatsResponseDTO {
    private String playerId;
    private String playerName;
    private PlayerBattingStatsDTO batting;
    private PlayerBowlingStatsDTO bowling;

    public PlayerCareerStatsResponseDTO() {}

    public PlayerCareerStatsResponseDTO(String playerId, String playerName, PlayerBattingStatsDTO batting, PlayerBowlingStatsDTO bowling) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.batting = batting;
        this.bowling = bowling;
    }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public PlayerBattingStatsDTO getBatting() { return batting; }
    public void setBatting(PlayerBattingStatsDTO batting) { this.batting = batting; }
    public PlayerBowlingStatsDTO getBowling() { return bowling; }
    public void setBowling(PlayerBowlingStatsDTO bowling) { this.bowling = bowling; }

    public static class Builder {
        private String playerId;
        private String playerName;
        private PlayerBattingStatsDTO batting;
        private PlayerBowlingStatsDTO bowling;

        public Builder playerId(String playerId) { this.playerId = playerId; return this; }
        public Builder playerName(String playerName) { this.playerName = playerName; return this; }
        public Builder batting(PlayerBattingStatsDTO batting) { this.batting = batting; return this; }
        public Builder bowling(PlayerBowlingStatsDTO bowling) { this.bowling = bowling; return this; }
        public PlayerCareerStatsResponseDTO build() {
            return new PlayerCareerStatsResponseDTO(playerId, playerName, batting, bowling);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
