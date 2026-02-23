package com.retro.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retro.model.*;
import com.retro.repository.BoardRepository;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public Board createBoard(String title, Users user) {

        
        Board board = new Board();
        board.setTitle(title);
        board.setCreatedBy(user);
        board.setCreatedAt(LocalDateTime.now());
        board = boardRepository.save(board);

       
        return board;
    }

   
}