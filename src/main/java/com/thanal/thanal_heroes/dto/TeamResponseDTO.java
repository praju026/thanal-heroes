package com.thanal.thanal_heroes.dto;

import java.util.List;

public class TeamResponseDTO {
    private String id;
    private String name;
    private String logoUrl;
    private List<PlayerResponseDTO> players;

    public TeamResponseDTO() {}

    public TeamResponseDTO(String id, String name, String logoUrl, List<PlayerResponseDTO> players) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.players = players;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public List<PlayerResponseDTO> getPlayers() { return players; }
    public void setPlayers(List<PlayerResponseDTO> players) { this.players = players; }

    public static class Builder {
        private String id;
        private String name;
        private String logoUrl;
        private List<PlayerResponseDTO> players;

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder logoUrl(String logoUrl) { this.logoUrl = logoUrl; return this; }
        public Builder players(List<PlayerResponseDTO> players) { this.players = players; return this; }
        public TeamResponseDTO build() {
            return new TeamResponseDTO(id, name, logoUrl, players);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
