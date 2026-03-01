package com.retro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.Team;
import com.retro.model.Users;

public interface TeamRepository  extends JpaRepository<Team,Long>{

	List<Team> findByMembersContaining(Users user);
	
	List<Team> findByDeletedFalse();

	List<Team> findByMembersContainingAndDeletedFalse(Users user);

}
