package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.BoardDTO;
import com.retro.model.Board;
import com.retro.service.BoardService;

@RestController
@RequestMapping("/api/boards")
@CrossOrigin("http://localhost:5173")
public class BoardController {

    @Autowired
    private BoardService boardService;

    //GET all boards 
    @GetMapping
    public ResponseEntity<List<Board>> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    //GET board by id
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    //GET boards accessible by a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Board>> getBoardsByUser(@PathVariable Long userId) {
        List<Board> accessibleBoards = boardService.getBoardsForUser(userId);
        return ResponseEntity.ok(accessibleBoards);
    }

    //CREATE a new board
    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody BoardDTO boardDto) {
        Long userId = boardDto.getUserId();
        Board board = boardService.createBoard(boardDto, userId);
        return ResponseEntity.ok(board);
    }

    //UPDATE board title
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id, @RequestBody BoardDTO boardDto) {
        return ResponseEntity.ok(boardService.updateBoard(id, boardDto));
    }

    // ── DELETE board ──
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.ok("Board deleted successfully.");
    }
}