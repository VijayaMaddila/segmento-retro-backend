package com.retro.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.Board;
import com.retro.model.Team;


public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findByDeletedFalse(Pageable pageable);

    List<Board> findByCreatedBy_IdAndDeletedFalse(Long userId);

    List<Board> findByTeamAndDeletedFalse(Team team);

    // Fetch boards created by user, with team and members pre-loaded
    @Query("SELECT DISTINCT b FROM Board b " +
           "LEFT JOIN FETCH b.team t " +
           "LEFT JOIN FETCH t.members " +
           "WHERE b.createdBy.id = :userId AND b.deleted = false AND (t IS NULL OR t.deleted = false)")
    List<Board> findByCreatedByIdWithTeam(@Param("userId") Long userId);

    // Fetch boards where user is a team member, with team pre-loaded
    // Fixed: added t.deleted = false check
    @Query("SELECT DISTINCT b FROM Board b " +
           "JOIN FETCH b.team t " +
           "JOIN t.members m " +
           "WHERE m.id = :userId AND b.deleted = false AND t.deleted = false")
    List<Board> findByTeamMemberIdWithTeam(@Param("userId") Long userId);

    // Single combined query for ADMIN users — avoids duplicates and two round-trips
    @Query("SELECT DISTINCT b FROM Board b " +
           "LEFT JOIN FETCH b.team t " +
           "LEFT JOIN FETCH t.members " +
           "LEFT JOIN t.members m " +
           "WHERE (b.createdBy.id = :userId OR m.id = :userId) " +
           "AND b.deleted = false AND (t IS NULL OR t.deleted = false)")
    List<Board> findAllAccessibleByUserId(@Param("userId") Long userId);
}