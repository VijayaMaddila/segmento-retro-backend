package com.retro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.Card;

public interface CardRepository  extends JpaRepository<Card,Long>{

}
