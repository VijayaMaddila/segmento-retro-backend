package com.retro.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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


    @Autowired
    private EmailService emailService;

    @Transactional
    public Board createBoard(BoardDTO boardDto, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Board board = new Board();
        board.setTitle(boardDto.getTitle());
        board.setCreatedBy(user);
        board.setCreatedAt(LocalDateTime.now());
        board.setDeleted(false);

        Team team = null;
        if (boardDto.getTeamId() != null) {
            team = teamRepository.findById(boardDto.getTeamId())
                    .orElseThrow(() -> new RuntimeException("Team not found"));
            board.setTeam(team);
        }

        Board savedBoard = boardRepository.save(board);

        // Copy columns from template if provided
        if (boardDto.getTemplateId() != null) {
            Template template = templateRepository.findById(boardDto.getTemplateId())
                    .orElseThrow(() -> new RuntimeException("Template not found"));

            List<TemplateColumn> templateColumns = templateColumnRepository.findByTemplateId(template.getId());

            for (TemplateColumn tc : templateColumns) {
                BoardColumn bc = new BoardColumn();
                bc.setTitle(tc.getName());
                bc.setPosition(tc.getPosition());
                bc.setBoard(savedBoard);
                boardColumnRepository.save(bc);
            }
        }

        // Send email notifications
        if (team != null) {
            sendBoardCreationNotifications(savedBoard, team);
        }

        return savedBoard;
    }

    private void sendBoardCreationNotifications(Board board, Team team) {
        String creatorName = board.getCreatedBy() != null ? board.getCreatedBy().getName() : "A team member";
        
        // Note: This method is kept for backward compatibility but won't be used
        // The BoardController now handles email sending with magic links
        System.out.println("Board creation notifications handled by BoardController");
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

        List<Board> boards = new ArrayList<>();
        
        if (Users.Role.ADMIN.equals(user.getRole())) {
            // ADMIN sees all their created boards + boards from teams they're in
            boards.addAll(boardRepository.findByCreatedBy_IdAndDeletedFalse(userId));
            List<Team> userTeams = teamRepository.findByMembersContainingAndDeletedFalse(user);
            for (Team team : userTeams) {
                boards.addAll(boardRepository.findByTeamAndDeletedFalse(team));
            }
        } else {
            // MEMBER sees boards from teams they belong to
            List<Team> userTeams = teamRepository.findByMembersContainingAndDeletedFalse(user);
            for (Team team : userTeams) {
                boards.addAll(boardRepository.findByTeamAndDeletedFalse(team));
            }
        }

        return boards;
    }
}