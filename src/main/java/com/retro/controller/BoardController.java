package com.retro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.BoardDTO;
import com.retro.model.Board;
import com.retro.model.Users;
import com.retro.repository.UserRepository;
import com.retro.service.BoardService;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody BoardDTO boardDto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        
        Board newBoard = boardService.createBoard(boardDto.getTitle(), user);

        return ResponseEntity.ok(newBoard);
    }
    
    @GetMapping
    public ResponseEntity<List<Board>> getAllBoards()
    {
    	List<Board> board=boardService.getAllBoards();
    	return ResponseEntity.ok(board);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long id)
    {
    	
    	Board board=boardService.getBoardById(id);
    	return ResponseEntity.ok(board);
    	
    }
    @PutMapping("/{id}")
    public ResponseEntity<Board> updatedBoard(@PathVariable Long id,@RequestBody BoardDTO boardDto)
    {
    	Board board=boardService.updatedBoard(id,boardDto);
    	return ResponseEntity.ok(board);
    }
    
    @DeleteMapping("/{id}")
    public String deleteBoard(@PathVariable Long id)
    {
    	boardService.deleteBoard(id);
    	return "Baord is deleted successfully.";
    }
}