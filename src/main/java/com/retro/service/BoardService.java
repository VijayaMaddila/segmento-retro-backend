package com.retro.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.retro.dto.BoardDTO;
import com.retro.model.*;
import com.retro.repository.*;

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

    @Autowired
    private TeamRepository teamRepository;

    @Transactional
    public Board createBoard(BoardDTO boardDto, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Board board = new Board();
        board.setTitle(boardDto.getTitle());
        board.setCreatedBy(user);
        board.setCreatedAt(LocalDateTime.now());
        board.setDeleted(false);

        if (boardDto.getTeamId() != null) {
            // ✅ Use findByIdWithMembers to eagerly load members in one query
            // This prevents lazy loading during the email loop in the controller
            Team team = teamRepository.findByIdWithMembers(boardDto.getTeamId())
                    .orElseThrow(() -> new RuntimeException("Team not found"));
            board.setTeam(team);
        }

        Board savedBoard = boardRepository.save(board);

        // ✅ Batch insert all columns in one query instead of saving one by one
        if (boardDto.getTemplateId() != null) {
            Template template = templateRepository.findById(boardDto.getTemplateId())
                    .orElseThrow(() -> new RuntimeException("Template not found"));

            List<TemplateColumn> templateColumns = templateColumnRepository.findByTemplateId(template.getId());

            List<BoardColumn> columns = templateColumns.stream().map(tc -> {
                BoardColumn bc = new BoardColumn();
                bc.setTitle(tc.getName());
                bc.setPosition(tc.getPosition());
                bc.setBoard(savedBoard);
                return bc;
            }).collect(Collectors.toList());

            boardColumnRepository.saveAll(columns); // ✅ single batch insert
        }

        return savedBoard;
    }

    // ---------------- GET ALL BOARDS ----------------
    public Page<Board> getAllBoards(Pageable pageable) {
        return boardRepository.findByDeletedFalse(pageable);
    }

    // ---------------- GET BOARD BY ID ----------------
    public Board getBoardById(Long id) {
        Board board = boardRepository.findByIdWithDetails(id);
        
        if (board == null) {
            throw new RuntimeException("Board not found with id: " + id);
        }
        
        // Fetch columns with cards in a separate optimized query
        List<BoardColumn> columns = boardRepository.findColumnsByBoardIdWithCards(id);
        board.setColumns(columns);

        return board;
    }

    // ---------------- UPDATE BOARD ----------------
    @Transactional
    public Board updateBoard(Long id, BoardDTO boardDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));

        if (board.isDeleted()) {
            throw new RuntimeException("Cannot update a deleted board");
        }

        board.setTitle(boardDto.getTitle());

        if (boardDto.getTeamId() != null) {
            Team team = teamRepository.findById(boardDto.getTeamId())
                    .orElseThrow(() -> new RuntimeException("Team not found"));
            board.setTeam(team);
        }

        return board;
    }

    // ---------------- SOFT DELETE BOARD ----------------
    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found: " + id));

        if (board.isDeleted()) {
            throw new RuntimeException("Board is already deleted");
        }

        board.setDeleted(true);
    }

    // ---------------- RESTORE BOARD ----------------
    @Transactional
    public void restoreBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found: " + id));

        board.setDeleted(false);
    }
}