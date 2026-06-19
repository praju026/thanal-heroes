package com.thanal.thanal_heroes.dto;

public class PlayerResponseDTO {
    private String id;
    private String userId;
    private String username;
    private String name;
    private String profilePictureUrl;
    private String battingStyle;
    private String bowlingStyle;

    public PlayerResponseDTO() {}

    public PlayerResponseDTO(String id, String userId, String username, String name, String profilePictureUrl, String battingStyle, String bowlingStyle) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
        this.battingStyle = battingStyle;
        this.bowlingStyle = bowlingStyle;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public String getBattingStyle() { return battingStyle; }
    public void setBattingStyle(String battingStyle) { this.battingStyle = battingStyle; }
    public String getBowlingStyle() { return bowlingStyle; }
    public void setBowlingStyle(String bowlingStyle) { this.bowlingStyle = bowlingStyle; }

    public static class Builder {
        private String id;
        private String userId;
        private String username;
        private String name;
        private String profilePictureUrl;
        private String battingStyle;
        private String bowlingStyle;

        public Builder id(String id) { this.id = id; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder profilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; return this; }
        public Builder battingStyle(String battingStyle) { this.battingStyle = battingStyle; return this; }
        public Builder bowlingStyle(String bowlingStyle) { this.bowlingStyle = bowlingStyle; return this; }
        public PlayerResponseDTO build() {
            return new PlayerResponseDTO(id, userId, username, name, profilePictureUrl, battingStyle, bowlingStyle);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
