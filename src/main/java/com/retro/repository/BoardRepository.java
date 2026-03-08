package com.retro.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.Board;
import com.retro.model.BoardColumn;
import com.retro.model.Team;


public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findByDeletedFalse(Pageable pageable);

    List<Board> findByCreatedBy_IdAndDeletedFalse(Long userId);

    List<Board> findByTeamAndDeletedFalse(Team team);

    // Fetch boards created by user, with team and members pre-loaded
    @Query("SELECT DISTINCT b FROM Board b " +
           "LEFT JOIN FETCH b.createdBy " +
           "LEFT JOIN FETCH b.team t " +
           "LEFT JOIN FETCH t.members " +
           "WHERE b.createdBy.id = :userId AND b.deleted = false AND (t IS NULL OR t.deleted = false)")
    List<Board> findByCreatedByIdWithTeam(@Param("userId") Long userId);

    // Fetch boards where user is a team member, with team pre-loaded
    @Query("SELECT DISTINCT b FROM Board b " +
           "JOIN FETCH b.createdBy " +
           "JOIN FETCH b.team t " +
           "JOIN t.members m " +
           "WHERE m.id = :userId AND b.deleted = false AND t.deleted = false")
    List<Board> findByTeamMemberIdWithTeam(@Param("userId") Long userId);

    // Single combined query for ADMIN users — avoids duplicates and two round-trips
    @Query("SELECT DISTINCT b FROM Board b " +
           "LEFT JOIN FETCH b.createdBy " +
           "LEFT JOIN FETCH b.team t " +
           "LEFT JOIN FETCH t.members " +
           "LEFT JOIN t.members m " +
           "WHERE (b.createdBy.id = :userId OR m.id = :userId) " +
           "AND b.deleted = false AND (t IS NULL OR t.deleted = false)")
    List<Board> findAllAccessibleByUserId(@Param("userId") Long userId);

    // Fetch board with all nested relationships in one query to avoid N+1 problem
    @Query("SELECT b FROM Board b " +
           "LEFT JOIN FETCH b.createdBy " +
           "LEFT JOIN FETCH b.team " +
           "WHERE b.id = :id AND b.deleted = false")
    Board findByIdWithDetails(@Param("id") Long id);
    
    // Fetch columns with cards for a board (second query to avoid Cartesian product)
    @Query("SELECT DISTINCT col FROM BoardColumn col " +
           "LEFT JOIN FETCH col.cards c " +
           "LEFT JOIN FETCH c.createdBy " +
           "WHERE col.board.id = :boardId AND col.deleted = false " +
           "AND (c.deleted IS NULL OR c.deleted = false) " +
           "ORDER BY col.position")
    List<BoardColumn> findColumnsByBoardIdWithCards(@Param("boardId") Long boardId);
}