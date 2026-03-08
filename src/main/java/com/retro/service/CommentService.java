package com.retro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retro.model.Card;
import com.retro.model.Comment;
import com.retro.model.Users;
import com.retro.repository.CardRepository;
import com.retro.repository.CommentRepository;
import com.retro.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    // CREATE COMMENT
    @Transactional
    public Comment addComment(Long cardId, Long userId, String message) {
        // ✅ Replaced getReferenceById with findById — gives clean errors immediately
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found: " + cardId));
        if (card.isDeleted()) {
            throw new RuntimeException("Cannot comment on a deleted card");
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Comment comment = new Comment();
        comment.setCard(card);
        comment.setUser(user);
        comment.setMessage(message);
        comment.setDeleted(false);

        return commentRepository.save(comment);
    }

    // GET COMMENTS FOR CARD
    public List<Comment> getCommentsByCard(Long cardId) {
        return commentRepository.findByCardIdAndDeletedFalse(cardId);
    }

    // UPDATE COMMENT
    @Transactional
    public Comment updateComment(Long commentId, String message) {
        Comment comment = commentRepository
                .findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found or already deleted"));
        comment.setMessage(message);
        return comment;
    }

    // SOFT DELETE COMMENT
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository
                .findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found or already deleted"));
        comment.setDeleted(true);
    }
}