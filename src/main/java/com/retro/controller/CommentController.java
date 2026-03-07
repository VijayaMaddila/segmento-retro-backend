package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.CommentRequestDTO;
import com.retro.model.Comment;
import com.retro.service.CommentService;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = {"http://localhost:5173/", "https://your-production-domain.com/"})
public class CommentController {

    @Autowired
    private CommentService commentService;

    //ADD COMMENT 
    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody CommentRequestDTO request) {
        Comment comment = commentService.addComment(
                request.getCardId(),
                request.getUserId(),
                request.getContent()
        );
        return ResponseEntity.ok(comment);
    }

    //GET COMMENTS BY CARD
    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<Comment>> getCommentsByCard(@PathVariable Long cardId) {
        List<Comment> comments = commentService.getCommentsByCard(cardId);
        return ResponseEntity.ok(comments);
    }

    //UPDATE COMMENT
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId,
                                                 @RequestBody CommentRequestDTO request) {
        Comment updated = commentService.updateComment(commentId, request.getContent());
        return ResponseEntity.ok(updated);
    }

    //DELETE COMMENT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {

        commentService.deleteComment(id);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}