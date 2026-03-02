package com.retro.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retro.dto.TeamDTO;
import com.retro.repository.TeamInvitationRepository;
import com.retro.service.TeamService;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin("http://localhost:5173")
public class TeamController {

    @Autowired
    private TeamService teamService;
    
    @Autowired
    private TeamInvitationRepository invitationRepository;

    // Create a new team
    @PostMapping("/create")
    public TeamDTO createTeam(@RequestBody TeamDTO teamDTO) {
        return teamService.createTeam(teamDTO);
    }

    // Get all teams
    @GetMapping
    public List<TeamDTO> getAllTeams() {
        return teamService.getAllTeams();
    }

    // Get team by ID
    @GetMapping("/{id}")
    public TeamDTO getTeamById(@PathVariable Long id) {
        return teamService.getTeamById(id);
    }

    // Invite multiple members
    @PostMapping("/{teamId}/invite")
    public ResponseEntity<String> inviteMembers(@PathVariable Long teamId, @RequestBody List<String> emails) {
        teamService.generateInvitations(teamId, emails); // sends emails internally
        return ResponseEntity.ok("Invitations sent to: " + String.join(", ", emails));
    }

    // Accept invitation without login
    @PostMapping("/accept-invite")
    public ResponseEntity<Map<String, Object>> acceptInvite(@RequestParam String token,
                                               @RequestParam String name,
                                               @RequestParam(required = false) String email) {
        Map<String, Object> response = teamService.acceptInvitation(token, name, email);
        return ResponseEntity.ok(response);
    }
    
    // Check invitation status (for frontend to decide what to show)
    @GetMapping("/check-invite")
    public ResponseEntity<Map<String, Object>> checkInvite(@RequestParam String token) {
        Map<String, Object> status = teamService.checkInvitationStatus(token);
        return ResponseEntity.ok(status);
    }
}