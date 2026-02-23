package com.retro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retro.model.Board;
import com.retro.model.BoardColumn;
import com.retro.repository.BoardColumnRepository;
import com.retro.repository.BoardRepository;

import java.util.List;

@Service
public class BoardColumnService {

    @Autowired
    private BoardColumnRepository columnRepository;

    @Autowired
    private BoardRepository boardRepository;

    
    public BoardColumn createColumn(Long boardId, String title) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        BoardColumn column = new BoardColumn();
        column.setName(title);
        column.setBoard(board);

        return columnRepository.save(column);
    }

    
    public List<BoardColumn> getColumnsByBoard(Long boardId) {
        return columnRepository.findByBoardId(boardId);
    }

    
    public BoardColumn updateColumn(Long columnId, String title) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));

        column.setName(title);
        return columnRepository.save(column);
    }

    
    public void deleteColumn(Long columnId) {
        if (!columnRepository.existsById(columnId)) {
            throw new RuntimeException("Column not found");
        }
        columnRepository.deleteById(columnId);
    }
}