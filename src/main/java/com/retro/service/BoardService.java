package com.retro.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retro.dto.BoardDTO;
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

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

	public Board getBoardById(Long id) {
		return boardRepository.findById(id)
				.orElseThrow(()->new RuntimeException("Board not found by Id"));
	}

	public Board updatedBoard(Long id, BoardDTO boardDto) {
	    
	    Board board = boardRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));

	    board.setTitle(boardDto.getTitle());  
	    Board updatedBoard = boardRepository.save(board);
	    return updatedBoard;
	}
	public void deleteBoard(Long id)
	{
		boardRepository.deleteById(id);
	}
}