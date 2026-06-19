package com.thanal.thanal_heroes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "teams")
@SQLRestriction("is_deleted = false")
public class Team extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    public Team() {}

    public Team(String name, String logoUrl) {
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
        public Team build() {
            return new Team(name, logoUrl);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
