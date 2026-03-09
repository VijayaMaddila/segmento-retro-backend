package com.retro.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.TeamDTO;
import com.retro.service.TeamService;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    // Create a new team
    @PostMapping("/create")
    public TeamDTO createTeam(@RequestBody TeamDTO teamDTO) {
        return teamService.createTeam(teamDTO);
    }

    // Get all teams
    @GetMapping
    public ResponseEntity<Page<TeamDTO>> getAllTeams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        long startTime = System.currentTimeMillis();
        Page<TeamDTO> result = teamService.getAllTeams(PageRequest.of(page, size));
        long endTime = System.currentTimeMillis();
        System.out.println("⏱️ GET /api/teams (page=" + page + ", size=" + size + ") took " + (endTime - startTime) + "ms");
        return ResponseEntity.ok(result);
    }

    // Get team by ID
    @GetMapping("/{id}")
    public TeamDTO getTeamById(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        TeamDTO result = teamService.getTeamById(id);
        long endTime = System.currentTimeMillis();
        System.out.println("⏱️ GET /api/teams/" + id + " took " + (endTime - startTime) + "ms");
        return result;
    }

    // Invite multiple members
    @PostMapping("/{teamId}/invite")
    public ResponseEntity<String> inviteMembers(@PathVariable Long teamId, @RequestBody List<String> emails) {
        teamService.generateInvitations(teamId, emails); // sends emails internally
        return ResponseEntity.ok("Invitations sent to: " + String.join(", ", emails));
    }

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

    // Update team's Slack webhook URL
    @PutMapping("/{teamId}/slack-webhook")
    public ResponseEntity<Map<String, String>> updateSlackWebhook(
            @PathVariable Long teamId,
            @RequestBody Map<String, String> request) {
        String webhookUrl = request.get("webhookUrl");
        teamService.updateSlackWebhook(teamId, webhookUrl);
        return ResponseEntity.ok(Map.of("message", "Slack webhook updated successfully"));
    }

    // Get team's Slack webhook URL
    @GetMapping("/{teamId}/slack-webhook")
    public ResponseEntity<Map<String, String>> getSlackWebhook(@PathVariable Long teamId) {
        String webhookUrl = teamService.getSlackWebhook(teamId);
        return ResponseEntity.ok(Map.of("webhookUrl", webhookUrl != null ? webhookUrl : ""));
    }
}