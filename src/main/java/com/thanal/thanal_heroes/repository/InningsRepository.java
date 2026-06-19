package com.thanal.thanal_heroes.repository;

import com.thanal.thanal_heroes.model.Innings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InningsRepository extends JpaRepository<Innings, String> {
    List<Innings> findByMatchId(String matchId);
    Optional<Innings> findByMatchIdAndInningsNumber(String matchId, int inningsNumber);
}
