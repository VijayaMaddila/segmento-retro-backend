package com.retro.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.retro.model.Board;
import com.retro.model.Team;
import com.retro.model.Users;

public interface BoardRepository extends JpaRepository<Board, Long> {

   
    List<Board> findByDeletedFalse();
    
    Page<Board> findByDeletedFalse(Pageable pageable);

   
    List<Board> findByCreatedByAndDeletedFalse(Users user);

  
    List<Board> findByCreatedBy_Id(Long userId);

   
    List<Board> findByTeamInAndDeletedFalse(List<Team> teams);

   
    List<Board> findByTeam(Team team);

    
    @Query("""
        SELECT DISTINCT b FROM Board b
        WHERE b.deleted = false
        AND (b.createdBy = :user OR b.team IN :teams)
    """)
    List<Board> findAccessibleBoards(@Param("user") Users user,
                                     @Param("teams") List<Team> teams);

	List<Board> findByCreatedBy_IdAndDeletedFalse(Long userId);

	List<Board> findByTeamAndDeletedFalse(Team team);


	// Optimized queries with JOIN FETCH to avoid N+1
	@Query("SELECT DISTINCT b FROM Board b " +
	       "LEFT JOIN FETCH b.team t " +
	       "LEFT JOIN FETCH t.members " +
	       "WHERE b.createdBy.id = :userId AND b.deleted = false")
	List<Board> findByCreatedByIdWithTeam(@Param("userId") Long userId);

	@Query("SELECT DISTINCT b FROM Board b " +
	       "JOIN FETCH b.team t " +
	       "JOIN t.members m " +
	       "WHERE m.id = :userId AND b.deleted = false")
	List<Board> findByTeamMemberIdWithTeam(@Param("userId") Long userId);

}