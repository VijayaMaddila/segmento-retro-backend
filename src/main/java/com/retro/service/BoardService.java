package com.retro.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retro.dto.BoardDTO;
import com.retro.model.*;
import com.retro.repository.BoardColumnRepository;
import com.retro.repository.BoardRepository;
import com.retro.repository.TemplateColumnRepository;
import com.retro.repository.TemplateRepository;
import com.retro.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private BoardColumnRepository boardColumnRepository;
    
    @Autowired 
    private UserRepository userRepository;
    
    @Autowired
    private TemplateColumnRepository templateColumnRepository;

    // ---------------- CREATE BOARD ----------------
    @Transactional
    public Board createBoard(BoardDTO boardDto, Long userId) {

        // 1️⃣ Validate User
        if (userId == null) {
            throw new IllegalArgumentException("UserId must not be null");
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Create Board
        Board board = new Board();
        board.setTitle(boardDto.getTitle());
        board.setCreatedBy(user);
        board.setCreatedAt(LocalDateTime.now());

        Board savedBoard = boardRepository.save(board);

        // 3️⃣ If Template Selected → Copy Columns
        if (boardDto.getTemplateId() != null) {

            Template template = templateRepository.findById(boardDto.getTemplateId())
                    .orElseThrow(() -> new RuntimeException("Template not found"));

            // SAFER WAY → Fetch template columns directly from repository
            List<TemplateColumn> templateColumns =
                    templateColumnRepository.findByTemplateId(template.getId());

            if (templateColumns.isEmpty()) {
                throw new RuntimeException("Template has no columns");
            }

            List<BoardColumn> boardColumns = new ArrayList<>();

            for (TemplateColumn tc : templateColumns) {
                BoardColumn boardColumn = new BoardColumn();
                boardColumn.setTitle(tc.getName());
                boardColumn.setPosition(tc.getPosition());
                boardColumn.setBoard(savedBoard);

                boardColumns.add(boardColumn);
            }

            // Save all columns at once
            boardColumnRepository.saveAll(boardColumns);
        }

        return savedBoard;
    }
    // ---------------- GET ALL BOARDS ----------------
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    // ---------------- GET BOARD BY ID ----------------
    public Board getBoardById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));
    }

    // ---------------- UPDATE BOARD ----------------
    @Transactional
    public Board updateBoard(Long id, BoardDTO boardDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));

        board.setTitle(boardDto.getTitle());
        return boardRepository.save(board);
    }

    // ---------------- DELETE BOARD ----------------
    @Transactional
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }
}