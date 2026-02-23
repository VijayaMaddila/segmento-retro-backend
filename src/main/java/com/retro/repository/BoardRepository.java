package com.retro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.Board;

public interface BoardRepository extends JpaRepository<Board,Long> {

}
