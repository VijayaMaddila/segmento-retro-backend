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
        
        BoardColumn column = boardColumnRepository.getReferenceById(boardColumnId);
        Users user         = userRepository.getReferenceById(userId);

        Card card = new Card();
        card.setContent(content);
        card.setBoardColumn(column);
        card.setCreatedBy(user);

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

    // DELETE CARD
    @Transactional
    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found: " + cardId));
        card.setDeleted(true);
        
    }

    // UPDATE CARD
    @Transactional
    public Card updateCard(Long cardId, String content) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        card.setContent(content);
    
        return card;
    }

    // GET CARD BY ID
    public Card getCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
    }

}