package com.retro.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retro.dto.VoteResponseDTO;
import com.retro.dto.VoteStatsDTO;
import com.retro.model.Board;
import com.retro.model.Card;
import com.retro.model.Users;
import com.retro.model.Vote;
import com.retro.repository.BoardRepository;
import com.retro.repository.CardRepository;
import com.retro.repository.UserRepository;
import com.retro.repository.VoteRepository;

import jakarta.transaction.Transactional;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    // TOGGLE VOTE (Add or Remove) with vote limit
    @Transactional
    public Map<String, Object> toggleVote(Long cardId, Long userId) {
        Optional<Vote> existingVote = voteRepository.findByCardIdAndUserId(cardId, userId);

        Map<String, Object> response = new HashMap<>();
        
        if (existingVote.isPresent()) {
            // Remove vote
            voteRepository.delete(existingVote.get());
            response.put("action", "removed");
            response.put("message", "Vote removed successfully");
            response.put("voted", false);
        } else {
            // Check vote limit before adding
            Card card = cardRepository.findById(cardId)
                    .orElseThrow(() -> new RuntimeException("Card not found"));
            
            Long boardId = card.getBoardColumn().getBoard().getId();
            int userVoteCount = voteRepository.findByBoardIdAndUserId(boardId, userId).size();
            
            final int MAX_VOTES_PER_USER = 6;
            
            if (userVoteCount >= MAX_VOTES_PER_USER) {
                response.put("action", "rejected");
                response.put("message", "Vote limit reached. You can only cast " + MAX_VOTES_PER_USER + " votes per board.");
                response.put("voted", false);
                response.put("currentVotes", userVoteCount);
                response.put("maxVotes", MAX_VOTES_PER_USER);
                response.put("remainingVotes", 0);
                return response;
            }
            
            // Add vote
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Vote vote = new Vote();
            vote.setCard(card);
            vote.setUser(user);
            vote.setBoard(card.getBoardColumn().getBoard());
            vote.setVotedAt(LocalDateTime.now());

            voteRepository.save(vote);
            
            response.put("action", "added");
            response.put("message", "Vote added successfully");
            response.put("voted", true);
            response.put("votedAt", vote.getVotedAt());
            response.put("currentVotes", userVoteCount + 1);
            response.put("maxVotes", MAX_VOTES_PER_USER);
            response.put("remainingVotes", MAX_VOTES_PER_USER - (userVoteCount + 1));
        }
        
        // Get updated vote count for the card
        int voteCount = voteRepository.findByCardId(cardId).size();
        response.put("totalVotes", voteCount);
        
        return response;
    }

    // GET VOTES FOR CARD WITH DETAILS
    public List<VoteResponseDTO> getVotesByCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        
        List<Vote> votes = voteRepository.findByCard(card);
        
        return votes.stream()
                .map(vote -> new VoteResponseDTO(
                        vote.getId(),
                        vote.getUser().getId(),
                        vote.getUser().getName(),
                        vote.getUser().getEmail(),
                        vote.getCard().getId(),
                        vote.getVotedAt()
                ))
                .collect(Collectors.toList());
    }

    // GET VOTE COUNT FOR CARD
    public int getVoteCount(Long cardId) {
        return voteRepository.findByCardId(cardId).size();
    }

    // CHECK IF USER VOTED ON CARD
    public boolean hasUserVoted(Long cardId, Long userId) {
        return voteRepository.findByCardIdAndUserId(cardId, userId).isPresent();
    }

    // GET VOTE STATS FOR CARD
    public VoteStatsDTO getVoteStats(Long cardId, Long currentUserId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        
        List<VoteResponseDTO> voters = getVotesByCard(cardId);
        boolean currentUserVoted = hasUserVoted(cardId, currentUserId);
        
        return new VoteStatsDTO(
                cardId,
                card.getContent(),
                voters.size(),
                voters,
                currentUserVoted
        );
    }

    // GET ALL VOTES FOR BOARD
    public Map<Long, List<VoteResponseDTO>> getVotesByBoard(Long boardId) {
        List<Vote> votes = voteRepository.findByBoardId(boardId);
        
        return votes.stream()
                .collect(Collectors.groupingBy(
                        vote -> vote.getCard().getId(),
                        Collectors.mapping(
                                vote -> new VoteResponseDTO(
                                        vote.getId(),
                                        vote.getUser().getId(),
                                        vote.getUser().getName(),
                                        vote.getUser().getEmail(),
                                        vote.getCard().getId(),
                                        vote.getVotedAt()
                                ),
                                Collectors.toList()
                        )
                ));
    }

    // GET USER'S VOTES ON BOARD
    public List<VoteResponseDTO> getUserVotesOnBoard(Long boardId, Long userId) {
        List<Vote> votes = voteRepository.findByBoardIdAndUserId(boardId, userId);
        
        return votes.stream()
                .map(vote -> new VoteResponseDTO(
                        vote.getId(),
                        vote.getUser().getId(),
                        vote.getUser().getName(),
                        vote.getUser().getEmail(),
                        vote.getCard().getId(),
                        vote.getVotedAt()
                ))
                .collect(Collectors.toList());
    }

    // GET BOARD VOTE STATISTICS
    public Map<String, Object> getBoardVoteStatistics(Long boardId) {
        List<Vote> allVotes = voteRepository.findByBoardId(boardId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVotes", allVotes.size());
        stats.put("uniqueVoters", allVotes.stream()
                .map(vote -> vote.getUser().getId())
                .distinct()
                .count());
        stats.put("cardsWithVotes", allVotes.stream()
                .map(vote -> vote.getCard().getId())
                .distinct()
                .count());
        
        // Most voted card
        Map<Long, Long> cardVoteCounts = allVotes.stream()
                .collect(Collectors.groupingBy(
                        vote -> vote.getCard().getId(),
                        Collectors.counting()
                ));
        
        if (!cardVoteCounts.isEmpty()) {
            Long mostVotedCardId = cardVoteCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
            
            if (mostVotedCardId != null) {
                Card mostVotedCard = cardRepository.findById(mostVotedCardId).orElse(null);
                if (mostVotedCard != null) {
                    stats.put("mostVotedCard", Map.of(
                            "cardId", mostVotedCardId,
                            "content", mostVotedCard.getContent(),
                            "voteCount", cardVoteCounts.get(mostVotedCardId)
                    ));
                }
            }
        }
        
        return stats;
    }
    
    // GET USER'S REMAINING VOTES ON BOARD
    public Map<String, Object> getUserVoteStatus(Long boardId, Long userId) {
        final int MAX_VOTES_PER_USER = 6;
        int currentVotes = voteRepository.findByBoardIdAndUserId(boardId, userId).size();
        int remainingVotes = MAX_VOTES_PER_USER - currentVotes;
        
        Map<String, Object> status = new HashMap<>();
        status.put("userId", userId);
        status.put("boardId", boardId);
        status.put("currentVotes", currentVotes);
        status.put("maxVotes", MAX_VOTES_PER_USER);
        status.put("remainingVotes", Math.max(0, remainingVotes));
        status.put("canVote", remainingVotes > 0);
        status.put("limitReached", currentVotes >= MAX_VOTES_PER_USER);
        
        return status;
    }
}