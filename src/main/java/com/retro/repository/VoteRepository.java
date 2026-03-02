package com.retro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.Card;
import com.retro.model.Users;
import com.retro.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByCardAndUser(Card card, Users user);
    List<Vote> findByCard(Card card);
    Optional<Vote> findByCardIdAndUserId(Long cardId, Long userId);
    List<Vote> findByCardId(Long cardId);
    List<Vote> findByBoardId(Long boardId);
    List<Vote> findByBoardIdAndUserId(Long boardId, Long userId);
    List<Vote> findByUserId(Long userId);
}