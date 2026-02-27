package com.retro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.Board;
import com.retro.model.BoardColumn;

public interface BoardColumnRepository extends JpaRepository<BoardColumn,Long> {

	List<BoardColumn> findByBoard(Board board);

}
