package com.retro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retro.model.Board;
import com.retro.model.BoardColumn;
import com.retro.model.Card;
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

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        column.setBoard(board);
        column.setDeleted(false);

        return boardColumnRepository.save(column);
    }

    //GET COLUMNS
    public List<BoardColumn> getColumnsByBoard(Long boardId) {

        return boardColumnRepository
                .findByBoard_IdAndDeletedFalse(boardId);
    }

    //UPDATE COLUMN NAME
    @Transactional
    public BoardColumn updateColumnName(Long columnId, String newTitle) {

        BoardColumn column = boardColumnRepository
                .findByIdAndDeletedFalse(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));

        column.setTitle(newTitle);

        return column;
    }

    //SOFT DELETE COLUMN
    @Transactional
    public void deleteColumn(Long columnId) {

        BoardColumn column = boardColumnRepository
                .findByIdAndDeletedFalse(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));

        column.setDeleted(true);

        List<Card> cards = cardRepository
                .findByBoardColumn_IdAndDeletedFalse(columnId);

        for (Card card : cards) {
            card.setDeleted(true);
        }
    }
}