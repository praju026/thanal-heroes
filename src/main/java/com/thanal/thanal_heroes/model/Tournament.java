package com.thanal.thanal_heroes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "tournaments")
@SQLRestriction("is_deleted = false")
public class Tournament extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "banner_url", length = 255)
    private String bannerUrl;

    public Tournament() {}

    public Tournament(String name, String bannerUrl) {
        this.name = name;
        this.bannerUrl = bannerUrl;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    public static class Builder {
        private String name;
        private String bannerUrl;

        public Builder name(String name) { this.name = name; return this; }
        public Builder bannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; return this; }
        public Tournament build() {
            return new Tournament(name, bannerUrl);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
