package com.retro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retro.model.Board;
import com.retro.model.BoardColumn;
import com.retro.repository.BoardColumnRepository;
import com.retro.repository.BoardRepository;

import jakarta.transaction.Transactional;

@Service
public class BoardColumnService {

    @Autowired
    private BoardColumnRepository boardColumnRepository;

    @Autowired
    private BoardRepository boardRepository;

    // ---------------- ADD COLUMN TO BOARD ----------------
    @Transactional
    public BoardColumn addColumn(Long boardId, BoardColumn column) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        column.setBoard(board);
        return boardColumnRepository.save(column);
    }

    // ---------------- GET ALL COLUMNS FOR A BOARD ----------------
    public List<BoardColumn> getColumnsByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        return boardColumnRepository.findByBoard(board);
    }

    // ---------------- UPDATE COLUMN ----------------
    @Transactional
    public BoardColumn updateColumn(Long columnId, BoardColumn updatedColumn) {
        BoardColumn column = boardColumnRepository.findById(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));
        column.setTitle(updatedColumn.getTitle());
        column.setPosition(updatedColumn.getPosition());
        return boardColumnRepository.save(column);
    }

    // ---------------- DELETE COLUMN ----------------
    @Transactional
    public void deleteColumn(Long columnId) {
        boardColumnRepository.deleteById(columnId);
    }
}