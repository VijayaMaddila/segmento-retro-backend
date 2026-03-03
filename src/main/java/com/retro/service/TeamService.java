package com.retro.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.retro.dto.TeamDTO;
import com.retro.model.Team;
import com.retro.model.TeamInvitation;
import com.retro.model.Users;
import com.retro.model.Users.Role;
import com.retro.repository.TeamInvitationRepository;
import com.retro.repository.TeamRepository;
import com.retro.repository.UserRepository;
import com.retro.util.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeamInvitationRepository invitationRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    // Create  team
    public TeamDTO createTeam(TeamDTO teamDTO) {
        Users createdByUser = userRepository.findById(teamDTO.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("Creator user not found"));

        List<Users> memberUsers = teamDTO.getMembers()
                .stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Member user not found with id " + id)))
                .toList();

        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setCreatedBy(createdByUser);
        team.setMembers(memberUsers);

        Team savedTeam = teamRepository.save(team);

        return new TeamDTO(
                savedTeam.getId(),
                savedTeam.getName(),
                savedTeam.getCreatedBy().getId(),
                savedTeam.getMembers().stream().map(Users::getId).toList()
        );
    }

    
    // Get all teams
    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(team -> new TeamDTO(
                        team.getId(),
                        team.getName(),
                        team.getCreatedBy().getId(),
                        team.getMembers().stream().map(Users::getId).toList()
                ))
                .toList();
    }

    // =========================
    // Get team by ID
    // =========================
    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        return new TeamDTO(
                team.getId(),
                team.getName(),
                team.getCreatedBy().getId(),
                team.getMembers().stream().map(Users::getId).toList()
        );
    }

    // =========================
    // Generate invitations and send emails to multiple members
    // =========================
    public void generateInvitations(Long teamId, List<String> emails) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Team not found"));

        for (String email : emails) {
            try {
                // Create a random token
                String token = UUID.randomUUID().toString();

                // Save invitation in DB
                TeamInvitation invitation = new TeamInvitation(email, token, team);
                invitationRepository.save(invitation);

                // Encode token and email for URL
                String inviteLink = "http://localhost:5173/join?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                        + "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);

                // Send email with invitation link
                emailService.sendInviteEmail(email, team.getName(), inviteLink);

            } catch (RuntimeException e) {
                System.out.println("Skipping invalid email: " + email + " (" + e.getMessage() + ")");
            }
        }
    }

    // =========================
    // Accept invitation - Reusable for login
    // First time: Create account and join team
    // Subsequent times: Auto-login and redirect to main page
    // =========================
    @Transactional
    public Map<String, Object> acceptInvitation(String token, String name, String email, String password) {
        TeamInvitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired invitation link"));

        // Check if expired (7 days)
        if (invitation.isExpired()) {
            throw new RuntimeException("This invitation link has expired. Please request a new invitation.");
        }

        String userEmail = email != null ? email : invitation.getEmail();
        Optional<Users> existingUser = userRepository.findByEmail(userEmail);

        Map<String, Object> response = new HashMap<>();
        Users user;

        if (existingUser.isPresent()) {
            // User already exists (second+ click) → Just login and redirect
            user = existingUser.get();
            
            // Check if user is already in the team
            Team team = invitation.getTeam();
            boolean alreadyInTeam = team.getMembers().contains(user);
            
            response.put("userExists", true);
            response.put("isReturningUser", invitation.isAccepted());
            response.put("userId", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            response.put("alreadyInTeam", alreadyInTeam);
            
            if (invitation.isAccepted()) {
                // Second+ click → Just login
                response.put("message", "Welcome back! Logging you in...");
                response.put("action", "redirect");
            } else {
                // First click, existing user → Add to team
                response.put("message", "Welcome! You've been added to the team.");
                response.put("action", "redirect");
            }

            String jwtToken = jwtUtil.generateToken(user);
            response.put("token", jwtToken);

        } else {
            // New user - password is required
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("Password is required for new users");
            }
          
            user = new Users();
            user.setEmail(userEmail);
            user.setName(name != null ? name : "New User");
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(Role.MEMBER);
            user = userRepository.save(user);

            response.put("userExists", false);
            response.put("isReturningUser", false);
            response.put("userId", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("message", "Account created! Welcome to the team.");
            response.put("action", "redirect");
            
            String jwtToken = jwtUtil.generateToken(user);
            response.put("token", jwtToken);
        }

        Team team = invitation.getTeam();
        if (!team.getMembers().contains(user)) {
            team.getMembers().add(user);
            teamRepository.save(team);
        }

        if (!invitation.isAccepted()) {
            invitation.setAccepted(true);
            invitationRepository.save(invitation);
        }

        return response;
    }
    
    // Check invitation status without accepting
    public Map<String, Object> checkInvitationStatus(String token) {
        Optional<TeamInvitation> invitationOpt = invitationRepository.findByToken(token);
        
        Map<String, Object> status = new HashMap<>();
        
        if (invitationOpt.isEmpty()) {
            status.put("valid", false);
            status.put("reason", "Token not found");
            return status;
        }
        
        TeamInvitation invitation = invitationOpt.get();
        
        if (invitation.isExpired()) {
            status.put("valid", false);
            status.put("reason", "Token expired");
            status.put("expiredAt", invitation.getCreatedAt().plusDays(7));
            return status;
        }
        
        status.put("valid", true);
        status.put("accepted", invitation.isAccepted());
        status.put("email", invitation.getEmail());
        status.put("teamName", invitation.getTeam().getName());
        status.put("createdAt", invitation.getCreatedAt());
        status.put("expiresAt", invitation.getCreatedAt().plusDays(7));
        
     
        Optional<Users> existingUser = userRepository.findByEmail(invitation.getEmail());
        status.put("userExists", existingUser.isPresent());
        
        if (existingUser.isPresent()) {
            status.put("userName", existingUser.get().getName());
            status.put("showLoginForm", invitation.isAccepted()); 
        } else {
            status.put("showRegistrationForm", !invitation.isAccepted()); 
        }
        
        return status;
    }
    
    // Check if user exists by email
    public Map<String, Object> checkUserExists(String email) {
        Map<String, Object> response = new HashMap<>();
        Optional<Users> user = userRepository.findByEmail(email);
        
        response.put("exists", user.isPresent());
        if (user.isPresent()) {
            response.put("name", user.get().getName());
        }
        
        return response;
    }
}