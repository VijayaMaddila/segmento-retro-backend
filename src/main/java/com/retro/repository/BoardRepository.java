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

    // Fetch boards created by user, with team and members pre-loaded (paginated)
    @Query(value = "SELECT b.id FROM Board b " +
           "LEFT JOIN b.team t " +
           "WHERE b.createdBy.id = :userId AND b.deleted = false AND (t IS NULL OR t.deleted = false)",
           countQuery = "SELECT COUNT(b) FROM Board b " +
           "LEFT JOIN b.team t " +
           "WHERE b.createdBy.id = :userId AND b.deleted = false AND (t IS NULL OR t.deleted = false)")
    Page<Long> findBoardIdsByCreatedBy(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT DISTINCT b FROM Board b " +
           "LEFT JOIN FETCH b.createdBy " +
           "LEFT JOIN FETCH b.team t " +
           "LEFT JOIN FETCH t.members " +
           "WHERE b.id IN :ids")
    List<Board> findByIdsWithDetails(@Param("ids") List<Long> ids);

    // Fetch boards where user is a team member (paginated)
    @Query(value = "SELECT DISTINCT b.id FROM Board b " +
           "JOIN b.team t " +
           "JOIN t.members m " +
           "WHERE m.id = :userId AND b.deleted = false AND t.deleted = false",
           countQuery = "SELECT COUNT(DISTINCT b) FROM Board b " +
           "JOIN b.team t " +
           "JOIN t.members m " +
           "WHERE m.id = :userId AND b.deleted = false AND t.deleted = false")
    Page<Long> findBoardIdsByTeamMember(@Param("userId") Long userId, Pageable pageable);

    // Single combined query for ADMIN users (paginated)
    @Query(value = "SELECT DISTINCT b.id FROM Board b " +
           "LEFT JOIN b.team t " +
           "LEFT JOIN t.members m " +
           "WHERE (b.createdBy.id = :userId OR m.id = :userId) " +
           "AND b.deleted = false AND (t IS NULL OR t.deleted = false)",
           countQuery = "SELECT COUNT(DISTINCT b) FROM Board b " +
           "LEFT JOIN b.team t " +
           "LEFT JOIN t.members m " +
           "WHERE (b.createdBy.id = :userId OR m.id = :userId) " +
           "AND b.deleted = false AND (t IS NULL OR t.deleted = false)")
    Page<Long> findBoardIdsByAccessibleUser(@Param("userId") Long userId, Pageable pageable);

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