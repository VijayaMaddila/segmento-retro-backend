package com.retro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.Team;
import com.retro.model.Users;

public interface TeamRepository extends JpaRepository<Team, Long> {

    // ✅ Used by getAllTeams — excludes soft-deleted teams (paginated)
    @Query(value = "SELECT t.id FROM Team t WHERE t.deleted = false",
           countQuery = "SELECT COUNT(t) FROM Team t WHERE t.deleted = false")
    Page<Long> findTeamIdsByDeletedFalse(Pageable pageable);
    
    @Query("SELECT DISTINCT t FROM Team t " +
           "LEFT JOIN FETCH t.createdBy " +
           "LEFT JOIN FETCH t.members " +
           "WHERE t.id IN :ids")
    List<Team> findByIdsWithDetails(@Param("ids") List<Long> ids);

    List<Team> findByMembersContainingAndDeletedFalse(Users user);

    // Eagerly fetch members in a single query — avoids lazy load during email loop
    @Query("SELECT t FROM Team t " +
           "LEFT JOIN FETCH t.createdBy " +
           "LEFT JOIN FETCH t.members " +
           "WHERE t.id = :id AND t.deleted = false")
    Optional<Team> findByIdWithMembers(@Param("id") Long id);
}