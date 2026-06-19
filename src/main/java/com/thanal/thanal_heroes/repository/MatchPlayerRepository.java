package com.thanal.thanal_heroes.repository;

import com.thanal.thanal_heroes.model.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, String> {
    List<MatchPlayer> findByMatchId(String matchId);
    List<MatchPlayer> findByMatchIdAndTeamId(String matchId, String teamId);
}
