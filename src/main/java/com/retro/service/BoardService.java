package com.retro.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    //CREATE BOARD
    @Transactional
    public Board createBoard(BoardDTO boardDto, Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("UserId must not be null");
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Board board = new Board();
        board.setTitle(boardDto.getTitle());
        board.setCreatedBy(user);
        board.setCreatedAt(LocalDateTime.now());
        board.setDeleted(false);

        if (boardDto.getTeamId() != null) {
            Team team = teamRepository.findById(boardDto.getTeamId())
                    .orElseThrow(() -> new RuntimeException("Team not found"));
            board.setTeam(team);
        }

        Board savedBoard = boardRepository.save(board);

        if (boardDto.getTemplateId() != null) {
            Template template = templateRepository.findById(boardDto.getTemplateId())
                    .orElseThrow(() -> new RuntimeException("Template not found"));

            List<TemplateColumn> templateColumns =
                    templateColumnRepository.findByTemplateId(template.getId());

            for (TemplateColumn tc : templateColumns) {
                BoardColumn boardColumn = new BoardColumn();
                boardColumn.setTitle(tc.getName());
                boardColumn.setPosition(tc.getPosition());
                boardColumn.setBoard(savedBoard);
                boardColumnRepository.save(boardColumn);
            }
        }

        return savedBoard;
    }

    // ---------------- GET ALL BOARDS ----------------
    public List<Board> getAllBoards() {
        return boardRepository.findByDeletedFalse();
    }

    // ---------------- GET BOARD BY ID ----------------
    public Board getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));

        if (board.isDeleted()) {
            throw new RuntimeException("Board has been deleted");
        }

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

    //SOFT DELETE BOARD
    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found: " + id));

        if (board.isDeleted()) {
            throw new RuntimeException("Board is already deleted");
        }

        board.setDeleted(true);
    }

    //RESTORE BOARD
    @Transactional
    public void restoreBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found: " + id));

        board.setDeleted(false);
    }

    //GET BOARDS FOR USER
    public List<Board> getBoardsForUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Team> userTeams = teamRepository.findByMembersContaining(user);

       
        return boardRepository.findAccessibleBoards(user, userTeams);
    }
}