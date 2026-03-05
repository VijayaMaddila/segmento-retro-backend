package com.retro.repository;

import com.retro.model.SlackIntegration;
import com.retro.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SlackIntegrationRepository extends JpaRepository<SlackIntegration, Long> {

    // Look up by Team entity
    Optional<SlackIntegration> findByTeamAndActiveTrue(Team team);

    // Look up by team's DB id (convenient for controllers)
    Optional<SlackIntegration> findByTeamIdAndActiveTrue(Long teamId);

    boolean existsByTeamIdAndActiveTrue(Long teamId);
}