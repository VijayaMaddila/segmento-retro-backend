package com.retro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

    // Fetch card with its column and board pre-loaded 
    @Query("SELECT c FROM Card c JOIN FETCH c.boardColumn bc JOIN FETCH bc.board WHERE c.id = :cardId")
    Optional<Card> findByIdWithBoard(@Param("cardId") Long cardId);

    // Fetch card with column, board, and team for Slack notifications
    @Query("SELECT c FROM Card c " +
           "JOIN FETCH c.boardColumn bc " +
           "JOIN FETCH bc.board b " +
           "LEFT JOIN FETCH b.team " +
           "WHERE c.id = :cardId")
    Optional<Card> findByIdWithTeam(@Param("cardId") Long cardId);

    // Fetch all non-deleted cards for a board
    @Query("SELECT c FROM Card c WHERE c.boardColumn.board.id = :boardId AND c.deleted = false")
    List<Card> findByBoardIdAndDeletedFalse(@Param("boardId") Long boardId);

    // Fetch all non-deleted cards for a column
    @Query("SELECT c FROM Card c WHERE c.boardColumn.id = :columnId AND c.deleted = false")
    List<Card> findByColumnIdAndDeletedFalse(@Param("columnId") Long columnId);

    // Batch soft-delete all cards in a column 
    @Modifying
    @Query("UPDATE Card c SET c.deleted = true WHERE c.boardColumn.id = :columnId AND c.deleted = false")
    void softDeleteByColumnId(@Param("columnId") Long columnId);
}