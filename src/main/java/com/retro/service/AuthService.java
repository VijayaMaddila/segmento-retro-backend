package com.retro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.retro.dto.AuthResponseDTO;
import com.retro.dto.LoginDTO;
import com.retro.dto.RegisterRequestDTO;
import com.retro.model.Users;
import com.retro.model.Users.Role;
import com.retro.repository.UserRepository;
import com.retro.util.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public void register(RegisterRequestDTO request) {

    	 if (userRepository.existsByEmail(request.getEmail())) {
             throw new IllegalArgumentException("Email already exists");
         }
        

        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        

        userRepository.save(user);
    }

    public AuthResponseDTO login(LoginDTO request) {

        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user);

        return new AuthResponseDTO(token);
    }
    
    public AuthResponseDTO magicLogin(String magicToken) {
        try {
           
            String email = jwtUtil.extractEmail(magicToken);
            
            
            Users user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
           
            String sessionToken = jwtUtil.generateToken(user);
            
            return new AuthResponseDTO(sessionToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired magic link: " + e.getMessage());
        }
    }
}
