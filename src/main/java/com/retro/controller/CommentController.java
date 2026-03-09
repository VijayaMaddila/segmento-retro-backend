package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.CommentRequestDTO;
import com.retro.model.Comment;
import com.retro.service.CommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // ADD COMMENT
    @PostMapping
    public ResponseEntity<Comment> addComment(@Valid @RequestBody CommentRequestDTO request) {
        Comment comment = commentService.addComment(
                request.getCardId(),
                request.getUserId(),
                request.getContent()
        );
        return ResponseEntity.ok(comment);
    }

    // GET COMMENTS BY CARD
    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<Comment>> getCommentsByCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(commentService.getCommentsByCard(cardId));
    }

    // UPDATE COMMENT
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDTO request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request.getContent()));
    }

    // DELETE COMMENT 
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}