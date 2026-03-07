package com.retro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.Team;
import com.retro.model.Users;

public interface TeamRepository extends JpaRepository<Team, Long> {

    
    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.members WHERE t.deleted = false")
    List<Team> findAllWithMembers();

    
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.members WHERE t.id = :id")
    Optional<Team> findByIdWithMembers(@Param("id") Long id);

    
    @Query("SELECT DISTINCT t FROM Team t JOIN t.members m WHERE m.id = :userId AND t.deleted = false")
    List<Team> findByMemberIdAndDeletedFalse(@Param("userId") Long userId);

    
    @Query("SELECT u FROM Users u WHERE u.id IN :ids")
    List<Users> findMembersByIds(@Param("ids") List<Long> ids);

    
    List<Team> findByMembersContaining(Users user);
    List<Team> findByDeletedFalse();
    List<Team> findByMembersContainingAndDeletedFalse(Users user);
}