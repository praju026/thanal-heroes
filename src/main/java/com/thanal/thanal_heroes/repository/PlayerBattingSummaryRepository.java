package com.thanal.thanal_heroes.repository;

import com.thanal.thanal_heroes.model.PlayerBattingSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerBattingSummaryRepository extends JpaRepository<PlayerBattingSummary, String> {
    List<PlayerBattingSummary> findTop10ByOrderByTotalRunsDesc();
}
