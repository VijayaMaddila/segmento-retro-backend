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

    @Autowired
    private SlackService slackService;

    // CREATE COMMENT
    @Transactional
    public Comment addComment(Long cardId, Long userId, String message) {
        // Fetch card with team for Slack notification
        Card card = cardRepository.findByIdWithTeam(cardId)
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

        Comment savedComment = commentRepository.save(comment);

        // Send Slack notification
        try {
            String teamWebhook = (card.getBoardColumn() != null && card.getBoardColumn().getBoard() != null 
                && card.getBoardColumn().getBoard().getTeam() != null) 
                ? card.getBoardColumn().getBoard().getTeam().getSlackWebhookUrl() : null;
            slackService.sendCommentAdded(card.getContent(), message, user.getUsername(), teamWebhook);
        } catch (Exception e) {
            System.err.println("Failed to send Slack notification for comment: " + e.getMessage());
        }

        return savedComment;
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
        
        String oldMessage = comment.getMessage();
        comment.setMessage(message);

        // Send Slack notification
        try {
            Card card = comment.getCard();
            if (card.getBoardColumn() == null) {
                // Fetch card with team
                card = cardRepository.findByIdWithTeam(card.getId()).orElse(card);
            }
            String teamWebhook = (card.getBoardColumn() != null 
                && card.getBoardColumn().getBoard() != null 
                && card.getBoardColumn().getBoard().getTeam() != null) 
                ? card.getBoardColumn().getBoard().getTeam().getSlackWebhookUrl() : null;
            slackService.sendCommentUpdated(card.getContent(), oldMessage, message, 
                comment.getUser().getUsername(), teamWebhook);
        } catch (Exception e) {
            System.err.println("Failed to send Slack notification for comment update: " + e.getMessage());
        }

        return comment;
    }

    // SOFT DELETE COMMENT
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository
                .findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found or already deleted"));
        
        comment.setDeleted(true);

        // Send Slack notification
        try {
            Card card = comment.getCard();
            if (card.getBoardColumn() == null) {
                // Fetch card with team
                card = cardRepository.findByIdWithTeam(card.getId()).orElse(card);
            }
            String teamWebhook = (card.getBoardColumn() != null 
                && card.getBoardColumn().getBoard() != null 
                && card.getBoardColumn().getBoard().getTeam() != null) 
                ? card.getBoardColumn().getBoard().getTeam().getSlackWebhookUrl() : null;
            slackService.sendCommentDeleted(card.getContent(), comment.getMessage(), teamWebhook);
        } catch (Exception e) {
            System.err.println("Failed to send Slack notification for comment deletion: " + e.getMessage());
        }
    }
}
