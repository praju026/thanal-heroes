package com.thanal.thanal_heroes.repository;

import com.thanal.thanal_heroes.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, String> {
}
