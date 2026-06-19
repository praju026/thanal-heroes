package com.thanal.thanal_heroes.repository;

import com.thanal.thanal_heroes.model.ScoreEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreEventRepository extends JpaRepository<ScoreEvent, String> {

    List<ScoreEvent> findByInningsIdOrderByOverNumberAscBallNumberAsc(String inningsId);

    List<ScoreEvent> findByMatchIdOrderByInningsInningsNumberAscOverNumberAscBallNumberAsc(String matchId);

    List<ScoreEvent> findByBatsmanId(String batsmanId);

    List<ScoreEvent> findByBowlerId(String bowlerId);
}
