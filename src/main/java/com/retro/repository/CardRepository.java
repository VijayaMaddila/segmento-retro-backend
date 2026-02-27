package com.retro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

	 List<Card> findByBoardColumn_Board_Id(Long boardId);

	    // Get all cards for a specific column
	    List<Card> findByBoardColumn_Id(Long columnId);
}