package com.retro.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.Board;
import com.retro.model.Team;
import com.retro.model.Users;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // Get all active (not deleted) boards
    List<Board> findByDeletedFalse();

    // Boards created by a specific user (not deleted)
    List<Board> findByCreatedByAndDeletedFalse(Users user);

    // Boards created by a user using userId
    List<Board> findByCreatedBy_Id(Long userId);

    // Boards belonging to specific teams (not deleted)
    List<Board> findByTeamInAndDeletedFalse(List<Team> teams);

    // Boards belonging to a single team
    List<Board> findByTeam(Team team);

    // Professional query: Boards accessible by user, filtered for non-deleted
    @Query("""
        SELECT DISTINCT b FROM Board b
        WHERE b.deleted = false
        AND (b.createdBy = :user OR b.team IN :teams)
    """)
    List<Board> findAccessibleBoards(@Param("user") Users user,
                                     @Param("teams") List<Team> teams);

	List<Board> findByCreatedBy_IdAndDeletedFalse(Long userId);

	List<Board> findByTeamAndDeletedFalse(Team team);
}