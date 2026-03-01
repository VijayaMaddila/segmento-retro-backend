package com.retro.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.retro.service.VoteService;


@RestController
@RequestMapping("/api/votes")
@CrossOrigin("http://localhost:5173")
public class VoteController {

    @Autowired
    private VoteService voteService;

    //TOGGLE VOTE
    @PostMapping("/card/{cardId}/user/{userId}")
    public ResponseEntity<String> toggleVote(@PathVariable Long cardId,
                                             @PathVariable Long userId) {

        voteService.toggleVote(cardId, userId);
        return ResponseEntity.ok("Vote toggled successfully");
    }

    //GET VOTE COUNT FOR CARD
    @GetMapping("/card/{cardId}/count")
    public ResponseEntity<Integer> getVoteCount(@PathVariable Long cardId) {
        return ResponseEntity.ok(
                voteService.getVotesByCard(cardId).size()
        );
    }
}