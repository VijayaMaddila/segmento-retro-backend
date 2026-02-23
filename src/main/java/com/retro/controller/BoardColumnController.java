package com.retro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.model.BoardColumn;
import com.retro.service.BoardColumnService;

import java.util.List;

@RestController
@RequestMapping("/api/columns")
public class BoardColumnController {

    @Autowired
    private BoardColumnService boardColumnService;

    
    @PostMapping
    public ResponseEntity<BoardColumn> createBoardColumn(
            @RequestParam Long boardId,
            @RequestParam String title) {

        BoardColumn newColumn = boardColumnService.createColumn(boardId, title);
        return ResponseEntity.ok(newColumn);
    }

    
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<BoardColumn>> getColumnsByBoard(@PathVariable Long boardId) {
        List<BoardColumn> columns = boardColumnService.getColumnsByBoard(boardId);
        return ResponseEntity.ok(columns);
    }

    
    @PutMapping("/{columnId}")
    public ResponseEntity<BoardColumn> updateColumn(
            @PathVariable Long columnId,
            @RequestParam String title) {

        BoardColumn updatedColumn = boardColumnService.updateColumn(columnId, title);
        return ResponseEntity.ok(updatedColumn);
    }

   
    @DeleteMapping("/{columnId}")
    public ResponseEntity<String> deleteColumn(@PathVariable Long columnId) {
        boardColumnService.deleteColumn(columnId);
        return ResponseEntity.ok("Column deleted successfully");
    }
}