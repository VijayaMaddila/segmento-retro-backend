package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.CardRequestDTO;
import com.retro.model.Card;
import com.retro.service.CardService;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = {"http://localhost:5173/", "https://your-production-domain.com/"})
public class CardController {

    @Autowired
    private CardService cardService;

    //CREATE CARD
    @PostMapping
    public Card createCard(@RequestBody CardRequestDTO request) {
        if (request.getColumnId() == null || request.getUserId() == null) {
            throw new RuntimeException("BoardColumnId and UserId must not be null");
        }
        return cardService.createCard(
                request.getColumnId(),
                request.getUserId(),
                request.getContent()
        );
    }

    //GET CARDS BY BOARD
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<Card>> getCardsByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(cardService.getBoardCards(boardId));
    }

    //GET CARDS BY COLUMN
    @GetMapping("/column/{columnId}")
    public ResponseEntity<List<Card>> getCardsByColumn(@PathVariable Long columnId) {
        return ResponseEntity.ok(cardService.getColumnCards(columnId));
    }

    //UPDATE CARD
    @PutMapping("/{cardId}")
    public ResponseEntity<Card> updateCard(@PathVariable Long cardId, @RequestBody CardRequestDTO request) {
        Card updatedCard = cardService.updateCard(cardId, request.getContent());
        return ResponseEntity.ok(updatedCard);
    }

    // DELETE CARD
    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.ok("Card deleted successfully.");
    }
}
