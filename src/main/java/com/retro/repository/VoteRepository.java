package com.retro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.Users;
import com.retro.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    
    boolean existsByUser_IdAndCard_Id(Long userId, Long cardId);

    
    long countByCard_Id(Long cardId);

   
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.user.id = :userId AND v.card.boardColumn.board.id = :boardId")
    long countByUserIdAndBoardId(@Param("userId") Long userId, @Param("boardId") Long boardId);

    
    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.card.boardColumn.board.id = :boardId")
    List<Vote> findByUserIdAndBoardId(@Param("userId") Long userId, @Param("boardId") Long boardId);

    
    List<Vote> findByCard_Id(Long cardId);
    
    long countByUser_IdAndCard_BoardColumn_Board_Id(Long userId, Long boardId);

	List<Vote> findByUser_IdAndCard_BoardColumn_Board_Id(Long userId, Long boardId);


	Optional<Vote> findByUser_IdAndCard_Id(Long userId, Long cardId);
}
