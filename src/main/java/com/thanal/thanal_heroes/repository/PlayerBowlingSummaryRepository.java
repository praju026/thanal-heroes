package com.thanal.thanal_heroes.repository;

import com.thanal.thanal_heroes.model.PlayerBowlingSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerBowlingSummaryRepository extends JpaRepository<PlayerBowlingSummary, String> {
    List<PlayerBowlingSummary> findTop10ByOrderByWicketsDesc();
}
