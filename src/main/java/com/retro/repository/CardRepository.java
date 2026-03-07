package com.retro.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import com.retro.model.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

    
    @Query("SELECT c FROM Card c JOIN FETCH c.boardColumn bc JOIN FETCH bc.board WHERE c.id = :cardId")
    Optional<Card> findByIdWithBoard(@Param("cardId") Long cardId);

    
    @Query("SELECT c FROM Card c WHERE c.boardColumn.board.id = :boardId AND c.deleted = false")
    List<Card> findByBoardIdAndDeletedFalse(@Param("boardId") Long boardId);

    
    @Query("SELECT c FROM Card c WHERE c.boardColumn.id = :columnId AND c.deleted = false")
    List<Card> findByColumnIdAndDeletedFalse(@Param("columnId") Long columnId);


    @Modifying
    @Query("UPDATE Card c SET c.deleted = true WHERE c.boardColumn.id = :columnId AND c.deleted = false")
    void softDeleteByColumnId(@Param("columnId") Long columnId);

    
    List<Card> findByDeletedFalse();
    List<Card> findByBoardColumn_Id(Long columnId);
    List<Card> findByBoardColumn_Board_Id(Long boardId);
    List<Card> findByBoardColumn_IdAndDeletedFalse(Long columnId);
    List<Card> findByBoardColumn_Board_IdAndDeletedFalse(Long boardId);
}