package com.retro.service;

import java.util.List;

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

    @Autowired
    private BoardRepository boardRepository;

    // ---------------- ADD VOTE ----------------
    @Transactional
    public Vote addVote(Long cardId, Long userId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Optional: Check if vote already exists
        voteRepository.findByCardAndUser(card, user).ifPresent(v -> {
            throw new RuntimeException("User has already voted for this card");
        });

        Vote vote = new Vote();
        vote.setCard(card);
        vote.setUser(user);
        vote.setBoard(card.getBoardColumn().getBoard()); // optional convenience

        return voteRepository.save(vote);
    }

    // ---------------- REMOVE VOTE ----------------
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

    // ---------------- GET VOTES FOR CARD ----------------
    public List<Vote> getVotesByCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return voteRepository.findByCard(card);
    }
}