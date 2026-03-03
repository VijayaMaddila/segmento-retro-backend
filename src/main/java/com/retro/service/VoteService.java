package com.retro.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.retro.dto.VoteResponseDTO;
import com.retro.model.Card;
import com.retro.model.Users;
import com.retro.model.Vote;
import com.retro.repository.CardRepository;
import com.retro.repository.UserRepository;
import com.retro.repository.VoteRepository;

@Service
public class VoteService {

    private static final int MAX_VOTES_PER_BOARD = 20;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    // add vote
    @Transactional
    public VoteResponseDTO addVote(Long userId, Long cardId) {
        System.out.println("Adding vote - userId: " + userId + ", cardId: " + cardId);
        
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));

        System.out.println("Found user: " + user.getEmail() + ", card: " + card.getId());

        
        if (card.isDeleted()) {
            throw new RuntimeException("Cannot vote on deleted card");
        }

       
        if (card.getBoardColumn() == null) {
            throw new RuntimeException("Card is not associated with a board column");
        }

        
        if (card.getBoardColumn().getBoard() == null) {
            throw new RuntimeException("Card's column is not associated with a board");
        }

       
        Long boardId = card.getBoardColumn().getBoard().getId();
        System.out.println("Board ID: " + boardId);

      
        Optional<Vote> existingVote = voteRepository.findByUser_IdAndCard_Id(userId, cardId);
        if (existingVote.isPresent()) {
            System.err.println("⚠️ Vote already exists with ID: " + existingVote.get().getId());
            throw new RuntimeException("You have already voted for this card");
        }

        
        long currentVotes = voteRepository.countByUserIdAndBoardId(userId, boardId);
        System.out.println("Current votes on board: " + currentVotes);
        
        if (currentVotes >= MAX_VOTES_PER_BOARD) {
            throw new RuntimeException("You have reached the maximum of " + MAX_VOTES_PER_BOARD + " votes for this board");
        }

        
        Vote vote = new Vote(user, card);
        voteRepository.save(vote);
        System.out.println("✅ Vote saved successfully");

        // Return response
        return buildVoteResponse(cardId, userId, boardId);
    }

  
    @Transactional
    public VoteResponseDTO removeVote(Long userId, Long cardId) {
        System.out.println("Removing vote - userId: " + userId + ", cardId: " + cardId);
        
        // Get card to find board ID
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        
        Long boardId = card.getBoardColumn().getBoard().getId();

        // Find and delete vote
        Vote vote = voteRepository.findByUser_IdAndCard_Id(userId, cardId)
                .orElseThrow(() -> new RuntimeException("Vote not found"));

        System.out.println("Found vote with ID: " + vote.getId() + ", deleting...");
        voteRepository.delete(vote);
        voteRepository.flush(); // Force immediate deletion
        System.out.println("✅ Vote deleted successfully");

        // Verify deletion
        boolean stillExists = voteRepository.existsByUser_IdAndCard_Id(userId, cardId);
        System.out.println("Vote still exists after delete: " + stillExists);

        // Return response
        return buildVoteResponse(cardId, userId, boardId);
    }

    /**
     * Get vote information for a card
     */
    public VoteResponseDTO getVoteInfo(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        
        Long boardId = card.getBoardColumn().getBoard().getId();
        
        return buildVoteResponse(cardId, userId, boardId);
    }

    public long getRemainingVotes(Long userId, Long boardId) {
        long usedVotes = voteRepository.countByUserIdAndBoardId(userId, boardId);
        return MAX_VOTES_PER_BOARD - usedVotes;
    }

    
    public Map<Long, Long> getBoardVotes(Long boardId) {
        List<Card> cards = cardRepository.findByBoardColumn_Board_IdAndDeletedFalse(boardId);
        Map<Long, Long> voteCounts = new HashMap<>();
        
        for (Card card : cards) {
            long count = voteRepository.countByCard_Id(card.getId());
            voteCounts.put(card.getId(), count);
        }
        
        return voteCounts;
    }

  
    public List<Vote> getUserBoardVotes(Long userId, Long boardId) {
        return voteRepository.findByUserIdAndBoardId(userId, boardId);
    }

    
    private VoteResponseDTO buildVoteResponse(Long cardId, Long userId, Long boardId) {
        long voteCount = voteRepository.countByCard_Id(cardId);
        boolean userHasVoted = voteRepository.existsByUser_IdAndCard_Id(userId, cardId);
        long remainingVotes = getRemainingVotes(userId, boardId);
        
        return new VoteResponseDTO(cardId, voteCount, userHasVoted, remainingVotes);
    }
}
