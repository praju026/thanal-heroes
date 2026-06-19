package com.thanal.thanal_heroes.repository;

import com.thanal.thanal_heroes.model.TeamPlayer;
import com.thanal.thanal_heroes.model.TeamPlayerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, TeamPlayerId> {

    @Query("SELECT tp FROM TeamPlayer tp WHERE tp.team.id = :teamId AND tp.leftAt IS NULL")
    List<TeamPlayer> findActivePlayersByTeamId(@Param("teamId") String teamId);

    @Query("SELECT tp FROM TeamPlayer tp WHERE tp.team.id = :teamId AND tp.player.id = :playerId AND tp.leftAt IS NULL")
    Optional<TeamPlayer> findActiveTeamPlayer(@Param("teamId") String teamId, @Param("playerId") String playerId);
}
