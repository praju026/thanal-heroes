package com.thanal.thanal_heroes.dto;

import jakarta.validation.constraints.NotBlank;

public class PlayerRequestDTO {

    @NotBlank(message = "Player name is required")
    private String name;

    private String userId;
    private String profilePictureUrl;
    private String battingStyle;
    private String bowlingStyle;

    public PlayerRequestDTO() {}

    public PlayerRequestDTO(String name, String userId, String profilePictureUrl, String battingStyle, String bowlingStyle) {
        this.name = name;
        this.userId = userId;
        this.profilePictureUrl = profilePictureUrl;
        this.battingStyle = battingStyle;
        this.bowlingStyle = bowlingStyle;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public String getBattingStyle() { return battingStyle; }
    public void setBattingStyle(String battingStyle) { this.battingStyle = battingStyle; }
    public String getBowlingStyle() { return bowlingStyle; }
    public void setBowlingStyle(String bowlingStyle) { this.bowlingStyle = bowlingStyle; }

    public static class Builder {
        private String name;
        private String userId;
        private String profilePictureUrl;
        private String battingStyle;
        private String bowlingStyle;

        public Builder name(String name) { this.name = name; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder profilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; return this; }
        public Builder battingStyle(String battingStyle) { this.battingStyle = battingStyle; return this; }
        public Builder bowlingStyle(String bowlingStyle) { this.bowlingStyle = bowlingStyle; return this; }
        public PlayerRequestDTO build() {
            return new PlayerRequestDTO(name, userId, profilePictureUrl, battingStyle, bowlingStyle);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
