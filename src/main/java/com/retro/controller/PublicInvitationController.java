package com.retro.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.service.TeamService;

@RestController
@RequestMapping("/teams")
@CrossOrigin("http://localhost:5173")
public class PublicInvitationController {

    @Autowired
    private TeamService teamService;

    // Accept invitation without login
    @PostMapping("/accept-invite")
    public ResponseEntity<Map<String, Object>> acceptInvite(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");
        
        Map<String, Object> response = teamService.acceptInvitation(token, name, email, password);
        return ResponseEntity.ok(response);
    }
    
    // Check invitation status
    @GetMapping("/check-invite")
    public ResponseEntity<Map<String, Object>> checkInvite(@RequestParam String token) {
        Map<String, Object> status = teamService.checkInvitationStatus(token);
        return ResponseEntity.ok(status);
    }
    
    // Check if user exists by email
    @GetMapping("/check-user")
    public ResponseEntity<Map<String, Object>> checkUser(@RequestParam String email) {
        Map<String, Object> response = teamService.checkUserExists(email);
        return ResponseEntity.ok(response);
    }
}
