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
@CrossOrigin("http://localhost:5173")
public class VoteController {

    @Autowired
    private VoteService voteService;

    //add vote
    @PostMapping
    public ResponseEntity<?> addVote(@RequestBody VoteRequestDTO request) {
        try {
            VoteResponseDTO response = voteService.addVote(request.getUserId(), request.getCardId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Failed to add vote: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    //delete vote
    @DeleteMapping
    public ResponseEntity<?> removeVote(@RequestBody VoteRequestDTO request) {
        try {
            VoteResponseDTO response = voteService.removeVote(request.getUserId(), request.getCardId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Failed to remove vote: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    //get vote
    @GetMapping("/card/{cardId}/user/{userId}")
    public ResponseEntity<VoteResponseDTO> getVoteInfo(
            @PathVariable Long cardId,
            @PathVariable Long userId) {
        try {
            VoteResponseDTO response = voteService.getVoteInfo(cardId, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Get remaining votes for a user on a board
     * GET /api/votes/board/{boardId}/user/{userId}/remaining
     */
    @GetMapping("/board/{boardId}/user/{userId}/remaining")
    public ResponseEntity<Long> getRemainingVotes(
            @PathVariable Long boardId,
            @PathVariable Long userId) {
        long remaining = voteService.getRemainingVotes(userId, boardId);
        return ResponseEntity.ok(remaining);
    }

    // get all votes of particular board
    @GetMapping("/board/{boardId}")
    public ResponseEntity<Map<Long, Long>> getBoardVotes(@PathVariable Long boardId) {
        Map<Long, Long> votes = voteService.getBoardVotes(boardId);
        return ResponseEntity.ok(votes);
    }

    //votes of specified used
    @GetMapping("/board/{boardId}/user/{userId}")
    public ResponseEntity<List<Vote>> getUserBoardVotes(
            @PathVariable Long boardId,
            @PathVariable Long userId) {
        List<Vote> votes = voteService.getUserBoardVotes(userId, boardId);
        return ResponseEntity.ok(votes);
    }
}
