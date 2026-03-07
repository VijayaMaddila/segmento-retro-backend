package com.retro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.Board;
import com.retro.model.Team;
import com.retro.model.Users;

public interface BoardRepository extends JpaRepository<Board, Long> {

    
    List<Board> findByDeletedFalse();


    List<Board> findByCreatedByAndDeletedFalse(Users user);

    
    List<Board> findByCreatedBy_IdAndDeletedFalse(Long userId);

    
    List<Board> findByTeamAndDeletedFalse(Team team);

    
    List<Board> findByTeamInAndDeletedFalse(List<Team> teams);

    
    @Query("""
        SELECT DISTINCT b FROM Board b
        WHERE b.deleted = false
        AND (b.createdBy = :user OR b.team IN :teams)
    """)
    List<Board> findAccessibleBoards(
            @Param("user") Users user,
            @Param("teams") List<Team> teams
    );
}