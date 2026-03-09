package com.retro.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private SlackService slackService;

    

    @Transactional
    public VoteResponseDTO addVote(Long userId, Long cardId) {
        Card card = cardRepository.findByIdWithBoard(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));

        if (card.isDeleted()) throw new RuntimeException("Cannot vote on deleted card");
        if (card.getBoardColumn() == null) throw new RuntimeException("Card is not associated with a board column");
        if (card.getBoardColumn().getBoard() == null) throw new RuntimeException("Card's column is not associated with a board");

        Long boardId = card.getBoardColumn().getBoard().getId();

        
        long currentVotes = voteRepository.countByUserIdAndBoardId(userId, boardId);
        if (voteRepository.existsByUser_IdAndCard_Id(userId, cardId)) {
            throw new RuntimeException("You have already voted for this card");
        }
        if (currentVotes >= MAX_VOTES_PER_BOARD) {
            throw new RuntimeException("You have reached the maximum of " + MAX_VOTES_PER_BOARD + " votes for this board");
        }

        
        Users user = userRepository.getReferenceById(userId);
        voteRepository.save(new Vote(user, card));

        // Send Slack notification
        String teamWebhook = (card.getBoardColumn().getBoard() != null && card.getBoardColumn().getBoard().getTeam() != null) 
            ? card.getBoardColumn().getBoard().getTeam().getSlackWebhookUrl() : null;
        slackService.sendVoteAdded(card.getContent(), user.getUsername(), currentVotes + 1, teamWebhook);

        
        return buildVoteResponseFast(cardId, userId, boardId, currentVotes + 1, true);
    }

    

    @Transactional
    public VoteResponseDTO removeVote(Long userId, Long cardId) {
        
        Card card = cardRepository.findByIdWithBoard(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        Long boardId = card.getBoardColumn().getBoard().getId();

        Vote vote = voteRepository.findByUser_IdAndCard_Id(userId, cardId)
                .orElseThrow(() -> new RuntimeException("Vote not found"));

        Users user = vote.getUser();
        voteRepository.delete(vote);

        long newCount = voteRepository.countByCard_Id(cardId);

        // Send Slack notification
        String teamWebhook = (card.getBoardColumn().getBoard() != null && card.getBoardColumn().getBoard().getTeam() != null) 
            ? card.getBoardColumn().getBoard().getTeam().getSlackWebhookUrl() : null;
        slackService.sendVoteRemoved(card.getContent(), user.getUsername(), newCount, teamWebhook);

        return buildVoteResponseFast(cardId, userId, boardId, newCount, false);
    }



    public VoteResponseDTO getVoteInfo(Long cardId, Long userId) {
        Card card = cardRepository.findByIdWithBoard(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        Long boardId = card.getBoardColumn().getBoard().getId();
        return buildVoteResponse(cardId, userId, boardId);
    }

    public Map<String, Object> getRemainingVotes(Long userId, Long boardId) {
        
        long usedVotes = voteRepository.countByUserIdAndBoardId(userId, boardId);
        long remaining = MAX_VOTES_PER_BOARD - usedVotes;
        Map<String, Object> response = new HashMap<>();
        response.put("remaining", remaining);
        return response;
    }

    

    public Map<Long, Long> getBoardVotes(Long boardId) {
    
        List<Object[]> results = voteRepository.countVotesByBoardId(boardId);
        Map<Long, Long> voteCounts = new HashMap<>();
        for (Object[] result : results) {
            voteCounts.put((Long) result[0], (Long) result[1]);
        }

        
        List<Card> cards = cardRepository.findByBoardIdAndDeletedFalse(boardId);
        for (Card card : cards) {
            voteCounts.putIfAbsent(card.getId(), 0L);
        }
        return voteCounts;
    }

    

    public List<Vote> getUserBoardVotes(Long userId, Long boardId) {
        return voteRepository.findByUserIdAndBoardId(userId, boardId);
    }

    private VoteResponseDTO buildVoteResponseFast(Long cardId, Long userId, Long boardId,
                                                   long voteCount, boolean userHasVoted) {
        long remainingVotes = MAX_VOTES_PER_BOARD - voteRepository.countByUserIdAndBoardId(userId, boardId);
        return new VoteResponseDTO(cardId, voteCount, userHasVoted, remainingVotes);
    }


    private VoteResponseDTO buildVoteResponse(Long cardId, Long userId, Long boardId) {
        long voteCount     = voteRepository.countByCard_Id(cardId);
        boolean hasVoted   = voteRepository.existsByUser_IdAndCard_Id(userId, cardId);
        long remaining     = MAX_VOTES_PER_BOARD - voteRepository.countByUserIdAndBoardId(userId, boardId);
        return new VoteResponseDTO(cardId, voteCount, hasVoted, remaining);
    }
}