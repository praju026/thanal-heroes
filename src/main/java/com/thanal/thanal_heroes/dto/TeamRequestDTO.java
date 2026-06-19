package com.thanal.thanal_heroes.dto;

import jakarta.validation.constraints.NotBlank;

public class TeamRequestDTO {

    @NotBlank(message = "Team name is required")
    private String name;

    private String logoUrl;

    public TeamRequestDTO() {}

    public TeamRequestDTO(String name, String logoUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public static class Builder {
        private String name;
        private String logoUrl;

        public Builder name(String name) { this.name = name; return this; }
        public Builder logoUrl(String logoUrl) { this.logoUrl = logoUrl; return this; }
        public TeamRequestDTO build() {
            return new TeamRequestDTO(name, logoUrl);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
