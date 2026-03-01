package com.retro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

	 List<Card> findByBoardColumn_Board_Id(Long boardId);

	    // Get all cards for a specific column
	    List<Card> findByBoardColumn_Id(Long columnId);
	    
	    List<Card> findByDeletedFalse();

		List<Card> findByBoardColumn_Board_IdAndDeletedFalse(Long boardId);

		List<Card> findByBoardColumn_IdAndDeletedFalse(Long columnId);

		


		
}