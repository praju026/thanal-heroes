package com.thanal.thanal_heroes.repository;

import com.thanal.thanal_heroes.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {
    Optional<Team> findByName(String name);
    boolean existsByName(String name);
}
