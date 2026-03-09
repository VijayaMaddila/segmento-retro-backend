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

    @Autowired
    private SlackService slackService;

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
            Team team = teamRepository.findByIdWithMembers(boardDto.getTeamId())
                    .orElseThrow(() -> new RuntimeException("Team not found"));
            board.setTeam(team);
        }

        Board savedBoard = boardRepository.save(board);
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

            boardColumnRepository.saveAll(columns); 
        }

        // Send Slack notification with board link (use team's webhook if available)
        String teamWebhook = (savedBoard.getTeam() != null) ? savedBoard.getTeam().getSlackWebhookUrl() : null;
        slackService.sendBoardCreated(savedBoard.getTitle(), user.getUsername(), savedBoard.getId(), teamWebhook);

        return savedBoard;
    }

    // GET ALL BOARDS
    public Page<Board> getAllBoards(Pageable pageable) {
        return boardRepository.findByDeletedFalse(pageable);
    }

    //GET BOARD BY ID
    public Board getBoardById(Long id) {
        long t1 = System.currentTimeMillis();
        Board board = boardRepository.findByIdWithDetails(id);
        long t2 = System.currentTimeMillis();
        System.out.println("  ↳ DB: findByIdWithDetails took " + (t2 - t1) + "ms");
        
        if (board == null) {
            throw new RuntimeException("Board not found with id: " + id);
        }
        
        long t3 = System.currentTimeMillis();
        List<BoardColumn> columns = boardRepository.findColumnsByBoardIdWithCards(id);
        long t4 = System.currentTimeMillis();
        System.out.println("  ↳ DB: findColumnsByBoardIdWithCards took " + (t4 - t3) + "ms");
        
        board.setColumns(columns);

        return board;
    }

    //UPDATE BOARD 
    @Transactional
    public Board updateBoard(Long id, BoardDTO boardDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));

        if (board.isDeleted()) {
            throw new RuntimeException("Cannot update a deleted board");
        }

        String oldTitle = board.getTitle();
        board.setTitle(boardDto.getTitle());

        if (boardDto.getTeamId() != null) {
            Team team = teamRepository.findById(boardDto.getTeamId())
                    .orElseThrow(() -> new RuntimeException("Team not found"));
            board.setTeam(team);
        }

        // Send Slack notification
        String teamWebhook = (board.getTeam() != null) ? board.getTeam().getSlackWebhookUrl() : null;
        slackService.sendBoardUpdated(oldTitle, boardDto.getTitle(), teamWebhook);

        return board;
    }

    //DELETE BOARD
    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found: " + id));

        if (board.isDeleted()) {
            throw new RuntimeException("Board is already deleted");
        }

        board.setDeleted(true);

        // Send Slack notification
        String teamWebhook = (board.getTeam() != null) ? board.getTeam().getSlackWebhookUrl() : null;
        slackService.sendBoardDeleted(board.getTitle(), teamWebhook);
    }

    //RESTORE BOARD
    @Transactional
    public void restoreBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found: " + id));

        board.setDeleted(false);
    }

    // START RETRO SESSION - Send notification to team
    public void startRetroSession(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found: " + boardId));
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Send Slack notification with board link (use team's webhook if available)
        String teamWebhook = (board.getTeam() != null) ? board.getTeam().getSlackWebhookUrl() : null;
        slackService.sendRetroSessionStarted(board.getTitle(), board.getId(), user.getUsername(), teamWebhook);
    }
}