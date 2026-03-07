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

    //CREATE COMMENT
    @Transactional
    public Comment addComment(Long cardId, Long userId, String message) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setCard(card);
        comment.setUser(user);
        comment.setMessage(message);

        return commentRepository.save(comment);
    }

    //GET COMMENTS FOR CARD
    public List<Comment> getCommentsByCard(Long cardId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        return commentRepository.findByCard_IdAndDeletedFalse(cardId);
    }

    //UPDATE COMMENT
    @Transactional
    public Comment updateComment(Long commentId, String message) {

        Comment comment = commentRepository
                .findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found or already deleted"));

        comment.setMessage(message);
        return commentRepository.save(comment);
    }

    //DELETE COMMENT
    @Transactional
    public void deleteComment(Long commentId) {

        Comment comment = commentRepository
                .findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found or already deleted"));

        comment.setDeleted(true);
        commentRepository.save(comment);
    }
}