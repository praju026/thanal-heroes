package com.thanal.thanal_heroes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "players")
@SQLRestriction("is_deleted = false")
public class Player extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "profile_picture_url", columnDefinition = "LONGTEXT")
    private String profilePictureUrl;

    @Column(name = "batting_style", length = 50)
    private String battingStyle;

    @Column(name = "bowling_style", length = 50)
    private String bowlingStyle;

    public Player() {}

    public Player(User user, String name, String profilePictureUrl, String battingStyle, String bowlingStyle) {
        this.user = user;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
        this.battingStyle = battingStyle;
        this.bowlingStyle = bowlingStyle;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public String getBattingStyle() { return battingStyle; }
    public void setBattingStyle(String battingStyle) { this.battingStyle = battingStyle; }
    public String getBowlingStyle() { return bowlingStyle; }
    public void setBowlingStyle(String bowlingStyle) { this.bowlingStyle = bowlingStyle; }

    public static class Builder {
        private User user;
        private String name;
        private String profilePictureUrl;
        private String battingStyle;
        private String bowlingStyle;

        public Builder user(User user) { this.user = user; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder profilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; return this; }
        public Builder battingStyle(String battingStyle) { this.battingStyle = battingStyle; return this; }
        public Builder bowlingStyle(String bowlingStyle) { this.bowlingStyle = bowlingStyle; return this; }
        public Player build() {
            return new Player(user, name, profilePictureUrl, battingStyle, bowlingStyle);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
