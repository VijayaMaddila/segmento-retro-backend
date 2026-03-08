package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.BoardDTO;
import com.retro.dto.BoardDetailDTO;
import com.retro.dto.BoardSummaryDTO;
import com.retro.model.Board;
import com.retro.model.Team;
import com.retro.model.Users;
import com.retro.repository.BoardRepository;
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
    private BoardRepository boardRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    // GET all boards (paginated)
    @GetMapping
    public ResponseEntity<Page<Board>> getAllBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(boardService.getAllBoards(PageRequest.of(page, size)));
    }

    // GET board by id
    @GetMapping("/{id}")
    public ResponseEntity<BoardDetailDTO> getBoardById(@PathVariable Long id) {
        Board board = boardService.getBoardById(id);
        return ResponseEntity.ok(BoardDetailDTO.fromEntity(board));
    }

    // CREATE a new board
    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody BoardDTO boardDto) {
        Board board = boardService.createBoard(boardDto, boardDto.getUserId());

        // Send email notifications to team members (except the creator)
        // ✅ team.getMembers() is safe here — members were eagerly loaded in createBoard()
        if (board.getTeam() != null) {
            Team team = board.getTeam();
            Users creator = board.getCreatedBy();

            for (Users member : team.getMembers()) {
                if (!member.getId().equals(creator.getId())) {
                    try {
                        String magicToken = jwtUtil.generateMagicLinkToken(member);
                        String magicLinkUrl = "http://localhost:5173/magic-login?token=" + magicToken;

                        emailService.sendBoardCreationEmail(
                            member.getEmail(),
                            member.getName() != null ? member.getName() : member.getEmail(),
                            board.getTitle(),
                            team.getName(),
                            creator.getName() != null ? creator.getName() : creator.getEmail(),
                            magicLinkUrl,
                            board.getId()
                        );
                    } catch (MessagingException e) {
                        System.err.println("Failed to send email to " + member.getEmail() + ": " + e.getMessage());
                    }
                }
            }
        }

        return ResponseEntity.ok(board);
    }

    // UPDATE board title
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id, @RequestBody BoardDTO boardDto) {
        return ResponseEntity.ok(boardService.updateBoard(id, boardDto));
    }

    // DELETE board
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.ok("Board deleted successfully.");
    }

    // GET boards for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<BoardSummaryDTO>> getUserBoards(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Long> boardIds;

        if (Users.Role.MEMBER.equals(user.getRole())) {
            boardIds = boardRepository.findBoardIdsByTeamMember(userId, pageable);
        } else {
            boardIds = boardRepository.findBoardIdsByAccessibleUser(userId, pageable);
        }

        List<Board> boards = boardIds.getContent().isEmpty() ? 
            List.of() : 
            boardRepository.findByIdsWithDetails(boardIds.getContent());

        Page<BoardSummaryDTO> boardDTOs = boardIds.map(id -> {
            Board board = boards.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);
            return board != null ? BoardSummaryDTO.fromEntity(board) : null;
        });

        return ResponseEntity.ok(boardDTOs);
    }
}