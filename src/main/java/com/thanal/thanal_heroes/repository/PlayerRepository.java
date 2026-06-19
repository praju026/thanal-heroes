package com.thanal.thanal_heroes.repository;

import com.thanal.thanal_heroes.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {

    @Query("SELECT p FROM Player p WHERE :name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Player> searchByName(@Param("name") String name, Pageable pageable);

    Optional<Player> findByUserId(String userId);
}
