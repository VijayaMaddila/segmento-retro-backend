package com.retro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.retro.model.BoardColumn;

public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {

    Optional<BoardColumn> findByIdAndDeletedFalse(Long id);

    List<BoardColumn> findByBoard_IdAndDeletedFalse(Long boardId);
}