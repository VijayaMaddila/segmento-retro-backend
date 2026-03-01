package com.retro.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // ADD VOTE
    @Transactional
    public void toggleVote(Long cardId, Long userId) {

        Optional<Vote> existingVote =
                voteRepository.findByCardIdAndUserId(cardId, userId);

        
        if (existingVote.isPresent()) {
            voteRepository.delete(existingVote.get());
            return;   
        }
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Vote vote = new Vote();
        vote.setCard(card);
        vote.setUser(user);
        vote.setBoard(card.getBoardColumn().getBoard());

        voteRepository.save(vote);
    }

    // REMOVE VOTE
    @Transactional
    public void removeVote(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Vote vote = voteRepository.findByCardAndUser(card, user)
                .orElseThrow(() -> new RuntimeException("Vote not found"));

        voteRepository.delete(vote);
    }

    //GET VOTES FOR CARD 
    public List<Vote> getVotesByCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return voteRepository.findByCard(card);
    }
}