package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.model.BoardColumn;
import com.retro.service.BoardColumnService;

@RestController
@RequestMapping("/api/board-columns")
@CrossOrigin("http://localhost:5173")
public class BoardColumnController {

    @Autowired
    private BoardColumnService boardColumnService;

    // ADD column to board
    @PostMapping("/{boardId}")
    public ResponseEntity<BoardColumn> addColumn(@PathVariable Long boardId, @RequestBody BoardColumn column) {
        return ResponseEntity.ok(boardColumnService.addColumn(boardId, column));
    }

    // GET all columns for a board
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<BoardColumn>> getColumnsByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardColumnService.getColumnsByBoard(boardId));
    }

    // UPDATE column
    @PutMapping("/{columnId}")
    public ResponseEntity<BoardColumn> updateColumn(@PathVariable Long columnId, @RequestBody BoardColumn column) {
        return ResponseEntity.ok(boardColumnService.updateColumn(columnId, column));
    }

    // DELETE column
    @DeleteMapping("/{columnId}")
    public ResponseEntity<String> deleteColumn(@PathVariable Long columnId) {
        boardColumnService.deleteColumn(columnId);
        return ResponseEntity.ok("Column deleted successfully.");
    }
}