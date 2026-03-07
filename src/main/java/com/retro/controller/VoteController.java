package com.retro.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.VoteRequestDTO;
import com.retro.dto.VoteResponseDTO;
import com.retro.model.Vote;
import com.retro.service.VoteService;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    // ADD VOTE
    @PostMapping
    public ResponseEntity<?> addVote(@RequestBody @jakarta.validation.Valid VoteRequestDTO request) {
        return ResponseEntity.ok(voteService.addVote(request.getUserId(), request.getCardId()));
    }

    // REMOVE VOTE
    @DeleteMapping
    public ResponseEntity<?> removeVote(@RequestBody @jakarta.validation.Valid VoteRequestDTO request) {
        return ResponseEntity.ok(voteService.removeVote(request.getUserId(), request.getCardId()));
    }

    // GET VOTE INFO FOR CARD
    @GetMapping("/card/{cardId}/user/{userId}")
    public ResponseEntity<VoteResponseDTO> getVoteInfo(@PathVariable Long cardId, @PathVariable Long userId) {
        try {
            return ResponseEntity.ok(voteService.getVoteInfo(cardId, userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // GET REMAINING VOTES 
    @GetMapping("/board/{boardId}/user/{userId}/remaining")
    public ResponseEntity<Map<String, Object>> getRemainingVotes(
            @PathVariable Long boardId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(voteService.getRemainingVotes(userId, boardId));
    }

    // GET ALL VOTE COUNTS FOR A BOARD
    @GetMapping("/board/{boardId}")
    public ResponseEntity<Map<Long, Long>> getBoardVotes(@PathVariable Long boardId) {
        return ResponseEntity.ok(voteService.getBoardVotes(boardId));
    }

    // GET VOTES BY USER ON A BOARD
    @GetMapping("/board/{boardId}/user/{userId}")
    public ResponseEntity<List<Vote>> getUserBoardVotes(
            @PathVariable Long boardId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(voteService.getUserBoardVotes(userId, boardId));
    }

    // FORCE DELETE VOTE
    @DeleteMapping("/force/{userId}/{cardId}")
    public ResponseEntity<?> forceDeleteVote(@PathVariable Long userId, @PathVariable Long cardId) {
        try {
            voteService.removeVote(userId, cardId);
            return ResponseEntity.ok(Map.of("message", "Vote deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of("message", "Vote not found or already deleted"));
        }
    }
}