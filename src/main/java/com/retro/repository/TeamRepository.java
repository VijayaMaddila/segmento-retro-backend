package com.retro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.Team;
import com.retro.model.Users;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByMembersContainingAndDeletedFalse(Users user);

    // Eagerly fetch members in a single query — avoids lazy load during email loop
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.members WHERE t.id = :id AND t.deleted = false")
    Optional<Team> findByIdWithMembers(@Param("id") Long id);
}