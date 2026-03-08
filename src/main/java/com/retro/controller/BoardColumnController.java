package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.ColumnRequestDTO;
import com.retro.dto.UpdatedColumnRequestDTO;
import com.retro.model.BoardColumn;
import com.retro.service.BoardColumnService;

@RestController
@RequestMapping("/api/board-columns")
public class BoardColumnController {

    @Autowired
    private BoardColumnService boardColumnService;

    // CREATE column — boardId comes from request body only (removed unused @PathVariable)
    @PostMapping
    public ResponseEntity<BoardColumn> addColumn(@RequestBody ColumnRequestDTO dto) {
        BoardColumn column = new BoardColumn();
        column.setTitle(dto.getTitle());
        column.setPosition(dto.getPosition());
        return ResponseEntity.ok(boardColumnService.addColumn(dto.getBoardId(), column));
    }

    // GET all columns for a board
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<BoardColumn>> getColumnsByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardColumnService.getColumnsByBoard(boardId));
    }

    // UPDATE column title
    @PutMapping("/{columnId}")
    public ResponseEntity<BoardColumn> updateColumn(
            @PathVariable Long columnId,
            @RequestBody UpdatedColumnRequestDTO request) {
        return ResponseEntity.ok(boardColumnService.updateColumnName(columnId, request.getTitle()));
    }

    // DELETE column (soft delete)
    @DeleteMapping("/{columnId}")
    public ResponseEntity<String> deleteColumn(@PathVariable Long columnId) {
        boardColumnService.deleteColumn(columnId);
        return ResponseEntity.ok("Column deleted successfully.");
    }
}