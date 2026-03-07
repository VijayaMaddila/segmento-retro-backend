package com.retro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.AuthResponseDTO;
import com.retro.dto.LoginDTO;
import com.retro.dto.RegisterRequestDTO;
import com.retro.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173/", "https://your-production-domain.com/"})
public class AuthController {

    @Autowired
    private AuthService authService;

    
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO request) {
        authService.register(request);
        return ResponseEntity.ok("User Registered Successfully");
    }

    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    
    @GetMapping("/magic-login")
    public ResponseEntity<AuthResponseDTO> magicLogin(@RequestParam String token) {
        return ResponseEntity.ok(authService.magicLogin(token));
    }
}