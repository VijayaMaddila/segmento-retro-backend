package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.CardRequestDTO;
import com.retro.model.Card;
import com.retro.service.CardService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    // CREATE CARD
    @PostMapping
    public ResponseEntity<Card> createCard(@Valid @RequestBody CardRequestDTO request) {
        Card card = cardService.createCard(
                request.getColumnId(),
                request.getUserId(),
                request.getContent()
        );
        return ResponseEntity.ok(card);
    }

    // GET CARDS BY BOARD
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<Card>> getCardsByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(cardService.getBoardCards(boardId));
    }

    // GET CARDS BY COLUMN
    @GetMapping("/column/{columnId}")
    public ResponseEntity<List<Card>> getCardsByColumn(@PathVariable Long columnId) {
        return ResponseEntity.ok(cardService.getColumnCards(columnId));
    }

    // UPDATE CARD
    @PutMapping("/{cardId}")
    public ResponseEntity<Card> updateCard(
            @PathVariable Long cardId,
            @RequestBody CardRequestDTO request) {
        return ResponseEntity.ok(cardService.updateCard(cardId, request.getContent()));
    }

    // DELETE CARD
    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.ok("Card deleted successfully.");
    }
}