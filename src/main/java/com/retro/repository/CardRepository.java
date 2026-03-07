package com.retro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

    
    List<Card> findByDeletedFalse();


    List<Card> findByBoardColumn_IdAndDeletedFalse(Long columnId);

    
    List<Card> findByBoardColumn_Board_IdAndDeletedFalse(Long boardId);
}