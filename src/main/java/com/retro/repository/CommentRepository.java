package com.retro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.retro.model.Card;
import com.retro.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCard(Card card);
    
    

    Optional<Comment> findByIdAndDeletedFalse(Long id);

    List<Comment> findByCardAndDeletedFalse(Card card);
}