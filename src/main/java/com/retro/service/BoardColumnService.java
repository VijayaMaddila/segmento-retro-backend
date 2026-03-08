package com.retro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retro.model.Board;
import com.retro.model.BoardColumn;
import com.retro.repository.BoardColumnRepository;
import com.retro.repository.BoardRepository;
import com.retro.repository.CardRepository;

import jakarta.transaction.Transactional;

@Service
public class BoardColumnService {

    @Autowired
    private BoardColumnRepository boardColumnRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CardRepository cardRepository;

    // ADD COLUMN
    @Transactional
    public BoardColumn addColumn(Long boardId, BoardColumn column) {
        // Validate board exists before creating column
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found: " + boardId));

        column.setBoard(board);
        column.setDeleted(false);
        return boardColumnRepository.save(column);
    }

    // GET COLUMNS BY BOARD
    public List<BoardColumn> getColumnsByBoard(Long boardId) {
        // Return 404-style error instead of silent empty list for invalid boardId
        if (!boardRepository.existsById(boardId)) {
            throw new RuntimeException("Board not found: " + boardId);
        }
        return boardColumnRepository.findByBoard_IdAndDeletedFalse(boardId);
    }

    // UPDATE COLUMN NAME
    @Transactional
    public BoardColumn updateColumnName(Long columnId, String newTitle) {
        BoardColumn column = boardColumnRepository
                .findByIdAndDeletedFalse(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));
        column.setTitle(newTitle);
        return column;
    }

    // SOFT DELETE COLUMN
    @Transactional
    public void deleteColumn(Long columnId) {
        BoardColumn column = boardColumnRepository
                .findByIdAndDeletedFalse(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));

        column.setDeleted(true);

        // Soft delete all cards in this column in one batch query
        cardRepository.softDeleteByColumnId(columnId);
    }
}