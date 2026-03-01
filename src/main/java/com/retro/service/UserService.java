package com.retro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retro.model.Users;
import com.retro.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // GET all users
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    // GET user by ID
    public Users getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // GET user by email
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // CREATE user
    @Transactional
    public Users createUser(Users user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }

    // UPDATE user
    @Transactional
    public Users updateUser(Long id, Users user) {
        Users existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword()); // ideally hash password

        return userRepository.save(existingUser);
    }

    // DELETE user
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}