package com.retro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retro.model.BoardColumn;
import com.retro.model.Card;
import com.retro.model.Users;
import com.retro.repository.BoardColumnRepository;
import com.retro.repository.CardRepository;
import com.retro.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private BoardColumnRepository boardColumnRepository;

    @Autowired
    private UserRepository userRepository;

    // CREATE CARD
    @Transactional
    public Card createCard(Long boardColumnId, Long userId, String content) {
        // ✅ Use findById instead of getReferenceById — gives clean errors if not found
        BoardColumn column = boardColumnRepository.findByIdAndDeletedFalse(boardColumnId)
                .orElseThrow(() -> new RuntimeException("Column not found: " + boardColumnId));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Card card = new Card();
        card.setContent(content);
        card.setBoardColumn(column);
        card.setCreatedBy(user);
        card.setDeleted(false);

        return cardRepository.save(card);
    }

    // GET CARDS BY BOARD
    public List<Card> getBoardCards(Long boardId) {
        return cardRepository.findByBoardIdAndDeletedFalse(boardId);
    }

    // GET CARDS BY COLUMN
    public List<Card> getColumnCards(Long columnId) {
        return cardRepository.findByColumnIdAndDeletedFalse(columnId);
    }

    // GET CARD BY ID
    public Card getCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found: " + cardId));
    }

    // UPDATE CARD
    @Transactional
    public Card updateCard(Long cardId, String content) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found: " + cardId));
        if (card.isDeleted()) {
            throw new RuntimeException("Cannot update a deleted card");
        }
        card.setContent(content);
        return card;
    }

    // SOFT DELETE CARD
    @Transactional
    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found: " + cardId));
        // ✅ Guard against double-delete
        if (card.isDeleted()) {
            throw new RuntimeException("Card is already deleted");
        }
        card.setDeleted(true);
    }
}