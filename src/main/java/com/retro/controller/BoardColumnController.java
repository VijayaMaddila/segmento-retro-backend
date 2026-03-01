package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.ColumnRequestDTO;
import com.retro.dto.UpdatedColumnRequestDTO;
import com.retro.model.Board;
import com.retro.model.BoardColumn;
import com.retro.repository.BoardColumnRepository;
import com.retro.repository.BoardRepository;
import com.retro.service.BoardColumnService;

@RestController
@RequestMapping("/api/board-columns")
@CrossOrigin("http://localhost:5173")
public class BoardColumnController {

    @Autowired
    private BoardColumnService boardColumnService;

    

    @PostMapping("/{boardId}")
    
    public ResponseEntity<BoardColumn> addColumn(
            @RequestBody ColumnRequestDTO dto) {

        BoardColumn column = new BoardColumn();
        column.setTitle(dto.getTitle());
        column.setPosition(dto.getPosition());

        return ResponseEntity.ok(
            boardColumnService.addColumn(dto.getBoardId(), column)
        );
    }

    // GET all columns for a board
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<BoardColumn>> getColumnsByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardColumnService.getColumnsByBoard(boardId));
    }

    @PutMapping("/{columnId}")
    public ResponseEntity<BoardColumn> updateColumn(
            @PathVariable Long columnId,
            @RequestBody UpdatedColumnRequestDTO request) {

        return ResponseEntity.ok(
            boardColumnService.updateColumnName(columnId, request.getTitle())
        );
    }

    // DELETE column
    @DeleteMapping("/{columnId}")
    public ResponseEntity<String> deleteColumn(@PathVariable Long columnId) {
        boardColumnService.deleteColumn(columnId);
        return ResponseEntity.ok("Column deleted successfully.");
    }
}