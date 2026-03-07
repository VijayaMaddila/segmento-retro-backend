package com.retro.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.retro.service.EmailService;
import com.retro.util.JwtUtil;
import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;
    
    @Autowired 
    private UserRepository userRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private BoardRepository boardRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private JwtUtil jwtUtil;

    //GET all boards 
    @GetMapping
    public ResponseEntity<Page<Board>> getAllBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(boardService.getAllBoards(PageRequest.of(page, size)));
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
        
        // Send email notifications to team members (except the creator)
        if (board.getTeam() != null) {
            Team team = board.getTeam();
            Users creator = board.getCreatedBy();
            
            for (Users member : team.getMembers()) {
                // Don't send email to the creator
                if (!member.getId().equals(creator.getId())) {
                    try {
                        // Generate magic link token for passwordless login
                        String magicToken = jwtUtil.generateMagicLinkToken(member);
                        
                        // Build magic link URL that logs them in and redirects to the specific board
                        String magicLinkUrl = "http://localhost:5173/magic-login?token=" + magicToken;
                        
                        emailService.sendBoardCreationEmail(
                            member.getEmail(),
                            member.getName() != null ? member.getName() : member.getEmail(),
                            board.getTitle(),
                            team.getName(),
                            creator.getName() != null ? creator.getName() : creator.getEmail(),
                            magicLinkUrl,
                            board.getId()  // Pass board ID for direct board link
                        );
                    } catch (MessagingException e) {
                        System.err.println("Failed to send email to " + member.getEmail() + ": " + e.getMessage());
                        // Continue sending to other members even if one fails
                    }
                }
            }
        }
        
        return ResponseEntity.ok(board);
    }

    //UPDATE board title
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id, @RequestBody BoardDTO boardDto) {
        return ResponseEntity.ok(boardService.updateBoard(id, boardDto));
    }

    //DELETE board
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
                // Optimized: Single query with JOIN FETCH instead of N+1
                boards = boardRepository.findByTeamMemberIdWithTeam(userId);
            } else {
                // Optimized: Two queries instead of N+1
                boards = new ArrayList<>(boardRepository.findByCreatedByIdWithTeam(userId));
                boards.addAll(boardRepository.findByTeamMemberIdWithTeam(userId));
            }

            return ResponseEntity.ok(boards);
        }


}
