package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.model.Vote;
import com.retro.service.VoteService;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin("http://localhost:5173")
public class VoteController {

    @Autowired
    private VoteService voteService;

    // ---------------- ADD VOTE ----------------
    @PostMapping("/card/{cardId}/user/{userId}")
    public ResponseEntity<Vote> addVote(@PathVariable Long cardId,
                                        @PathVariable Long userId) {
        Vote vote = voteService.addVote(cardId, userId);
        return ResponseEntity.ok(vote);
    }

    // ---------------- REMOVE VOTE ----------------
    @DeleteMapping("/card/{cardId}/user/{userId}")
    public ResponseEntity<String> removeVote(@PathVariable Long cardId,
                                             @PathVariable Long userId) {
        voteService.removeVote(cardId, userId);
        return ResponseEntity.ok("Vote removed successfully.");
    }

    // ---------------- GET VOTE COUNT FOR CARD ----------------
    @GetMapping("/card/{cardId}/count")
    public ResponseEntity<Integer> getVoteCount(@PathVariable Long cardId) {
        List<Vote> votes = voteService.getVotesByCard(cardId);
        return ResponseEntity.ok(votes.size());
    }
}