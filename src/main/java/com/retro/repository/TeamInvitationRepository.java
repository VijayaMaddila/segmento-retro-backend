package com.retro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.TeamInvitation;


public interface TeamInvitationRepository extends JpaRepository<TeamInvitation,Long> {

	Optional<TeamInvitation> findByToken(String token);

	

}
