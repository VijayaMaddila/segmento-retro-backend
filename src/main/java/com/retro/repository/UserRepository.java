package com.retro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retro.model.Users;

public interface UserRepository extends JpaRepository<Users,Long>{

	Optional<Users> findByEmail(String email);

	boolean existsByEmail(String email);

}