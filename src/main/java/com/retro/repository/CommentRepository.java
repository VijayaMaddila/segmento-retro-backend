package com.retro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    @Query("SELECT c FROM Comment c WHERE c.card.id = :cardId AND c.deleted = false")
    List<Comment> findByCardIdAndDeletedFalse(@Param("cardId") Long cardId);

    Optional<Comment> findByIdAndDeletedFalse(Long id);
}