package com.retro.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.BoardDTO;
import com.retro.model.Board;
import com.retro.model.Team;
import com.retro.model.Users;
import com.retro.repository.BoardRepository;
import com.retro.repository.TeamRepository;
import com.retro.repository.UserRepository;
import com.retro.service.BoardService;

@RestController
@RequestMapping("/api/boards")
@CrossOrigin("http://localhost:5173")
public class BoardController {

    @Autowired
    private BoardService boardService;
    
    @Autowired 
    private UserRepository userRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private BoardRepository boardRepository;

    //GET all boards 
    @GetMapping
    public ResponseEntity<List<Board>> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    //GET board by id
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

   

    //CREATE a new board
    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody BoardDTO boardDto) {
        Long userId = boardDto.getUserId();
        Board board = boardService.createBoard(boardDto, userId);
        return ResponseEntity.ok(board);
    }

    //UPDATE board title
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id, @RequestBody BoardDTO boardDto) {
        return ResponseEntity.ok(boardService.updateBoard(id, boardDto));
    }

    // ── DELETE board ──
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.ok("Board deleted successfully.");
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Board>> getUserBoards(@PathVariable Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Board> boards;
        
        if (Users.Role.MEMBER.equals(user.getRole())) {
            // Members only see boards from teams they belong to
            List<Team> userTeams = teamRepository.findByMembersContainingAndDeletedFalse(user);
            boards = new ArrayList<>();
            for (Team team : userTeams) {
                boards.addAll(boardRepository.findByTeamAndDeletedFalse(team));
            }
        } else {
            // ADMIN sees all their created boards + boards from teams they're in
            boards = new ArrayList<>(boardRepository.findByCreatedBy_IdAndDeletedFalse(userId));
            List<Team> userTeams = teamRepository.findByMembersContainingAndDeletedFalse(user);
            for (Team team : userTeams) {
                boards.addAll(boardRepository.findByTeamAndDeletedFalse(team));
            }
        }
        
        return ResponseEntity.ok(boards);
    }
}