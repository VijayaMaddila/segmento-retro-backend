package com.retro.controller;

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

        
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        
        Board newBoard = boardService.createBoard(boardDto.getTitle(), user);

        return ResponseEntity.ok(newBoard);
    }
}