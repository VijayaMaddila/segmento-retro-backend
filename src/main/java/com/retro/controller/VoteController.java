package com.retro.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.VoteResponseDTO;
import com.retro.dto.VoteStatsDTO;
import com.retro.service.VoteService;


@RestController
@RequestMapping("/api/votes")
@CrossOrigin("http://localhost:5173")
public class VoteController {

    @Autowired
    private VoteService voteService;

    // TOGGLE VOTE (Add or Remove)
    @PostMapping("/card/{cardId}/user/{userId}")
    public ResponseEntity<Map<String, Object>> toggleVote(@PathVariable Long cardId,
                                                          @PathVariable Long userId) {
        Map<String, Object> response = voteService.toggleVote(cardId, userId);
        return ResponseEntity.ok(response);
    }

    // GET VOTE COUNT FOR CARD
    @GetMapping("/card/{cardId}/count")
    public ResponseEntity<Integer> getVoteCount(@PathVariable Long cardId) {
        return ResponseEntity.ok(voteService.getVoteCount(cardId));
    }

    // GET WHO VOTED ON CARD
    @GetMapping("/card/{cardId}/voters")
    public ResponseEntity<List<VoteResponseDTO>> getVoters(@PathVariable Long cardId) {
        return ResponseEntity.ok(voteService.getVotesByCard(cardId));
    }

    // CHECK IF USER VOTED ON CARD
    @GetMapping("/card/{cardId}/user/{userId}/check")
    public ResponseEntity<Map<String, Boolean>> checkUserVote(@PathVariable Long cardId,
                                                               @PathVariable Long userId) {
        boolean hasVoted = voteService.hasUserVoted(cardId, userId);
        return ResponseEntity.ok(Map.of("voted", hasVoted));
    }

    // GET VOTE STATS FOR CARD (includes voters and current user status)
    @GetMapping("/card/{cardId}/stats")
    public ResponseEntity<VoteStatsDTO> getVoteStats(@PathVariable Long cardId,
                                                      @RequestParam Long userId) {
        return ResponseEntity.ok(voteService.getVoteStats(cardId, userId));
    }

    // GET ALL VOTES FOR BOARD (grouped by card)
    @GetMapping("/board/{boardId}")
    public ResponseEntity<Map<Long, List<VoteResponseDTO>>> getBoardVotes(@PathVariable Long boardId) {
        return ResponseEntity.ok(voteService.getVotesByBoard(boardId));
    }

    // GET USER'S VOTES ON BOARD
    @GetMapping("/board/{boardId}/user/{userId}")
    public ResponseEntity<List<VoteResponseDTO>> getUserVotesOnBoard(@PathVariable Long boardId,
                                                                      @PathVariable Long userId) {
        return ResponseEntity.ok(voteService.getUserVotesOnBoard(boardId, userId));
    }

    // GET BOARD VOTE STATISTICS
    @GetMapping("/board/{boardId}/statistics")
    public ResponseEntity<Map<String, Object>> getBoardStatistics(@PathVariable Long boardId) {
        return ResponseEntity.ok(voteService.getBoardVoteStatistics(boardId));
    }
    
    // GET USER'S VOTE STATUS (remaining votes, limit info)
    @GetMapping("/board/{boardId}/user/{userId}/status")
    public ResponseEntity<Map<String, Object>> getUserVoteStatus(@PathVariable Long boardId,
                                                                  @PathVariable Long userId) {
        return ResponseEntity.ok(voteService.getUserVoteStatus(boardId, userId));
    }
}