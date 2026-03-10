package com.retro.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        TeamDTO dto = new TeamDTO(
                savedTeam.getId(),
                savedTeam.getName(),
                savedTeam.getCreatedBy().getId(),
                savedTeam.getMembers().stream().map(Users::getId).toList()
        );
        dto.setMemberDetails(savedTeam.getMembers().stream()
                .map(u -> new TeamDTO.MemberInfo(u.getId(), u.getName(), u.getEmail()))
                .toList());
        return dto;
    }

    // Get all teams
    public Page<TeamDTO> getAllTeams(Pageable pageable) {
        long t1 = System.currentTimeMillis();
        Page<Long> teamIds = teamRepository.findTeamIdsByDeletedFalse(pageable);
        long t2 = System.currentTimeMillis();
        System.out.println("  ↳ DB: findTeamIdsByDeletedFalse took " + (t2 - t1) + "ms");
        
        List<Team> teams = teamIds.getContent().isEmpty() ?
            List.of() :
            teamRepository.findByIdsWithDetails(teamIds.getContent());
        long t3 = System.currentTimeMillis();
        System.out.println("  ↳ DB: findByIdsWithDetails took " + (t3 - t2) + "ms");
        
        Page<TeamDTO> result = teamIds.map(id -> {
            Team team = teams.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
            if (team == null) return null;
            
            TeamDTO dto = new TeamDTO(
                team.getId(),
                team.getName(),
                team.getCreatedBy() != null ? team.getCreatedBy().getId() : null,
                team.getMembers() != null ? team.getMembers().stream().map(Users::getId).toList() : List.of()
            );
            dto.setMemberDetails(team.getMembers() != null ? 
                team.getMembers().stream()
                    .map(u -> new TeamDTO.MemberInfo(u.getId(), u.getName(), u.getEmail()))
                    .toList() : List.of());
            return dto;
        });
        long t4 = System.currentTimeMillis();
        System.out.println("  ↳ DTO mapping took " + (t4 - t3) + "ms");
        
        return result;
    }

    // Delete team (soft delete)
    @Transactional
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        team.setDeleted(true);
        teamRepository.save(team);
    }

    // Get team by ID
    public TeamDTO getTeamById(Long id) {
        long t1 = System.currentTimeMillis();
        Team team = teamRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        long t2 = System.currentTimeMillis();
        System.out.println("  ↳ DB: findByIdWithMembers took " + (t2 - t1) + "ms");
        
        TeamDTO result = new TeamDTO(
                team.getId(),
                team.getName(),
                team.getCreatedBy() != null ? team.getCreatedBy().getId() : null,
                team.getMembers() != null ? team.getMembers().stream().map(Users::getId).toList() : List.of()
        );
        result.setMemberDetails(team.getMembers() != null ? 
            team.getMembers().stream()
                .map(u -> new TeamDTO.MemberInfo(u.getId(), u.getName(), u.getEmail()))
                .toList() : List.of());
        long t3 = System.currentTimeMillis();
        System.out.println("  ↳ DTO mapping took " + (t3 - t2) + "ms");
        
        return result;
    }
    public void generateInvitations(Long teamId, List<String> emails) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Team not found"));

        for (String email : emails) {
            try {
                String token = UUID.randomUUID().toString();

                
                TeamInvitation invitation = new TeamInvitation(email, token, team);
                invitationRepository.save(invitation);
                String inviteLink = "http://localhost:5173/join?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                        + "&email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);
                emailService.sendInviteEmail(email, team.getName(), inviteLink);

            } catch (RuntimeException e) {
                System.out.println("Skipping invalid email: " + email + " (" + e.getMessage() + ")");
            }
        }
    }
    @Transactional
    public Map<String, Object> acceptInvitation(String token, String name, String email, String password) {
        TeamInvitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired invitation link"));
        if (invitation.isExpired()) {
            throw new RuntimeException("This invitation link has expired. Please request a new invitation.");
        }

        String userEmail = email != null ? email : invitation.getEmail();
        Optional<Users> existingUser = userRepository.findByEmail(userEmail);

        Map<String, Object> response = new HashMap<>();
        Users user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
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
                response.put("message", "Welcome back! Logging you in...");
                response.put("action", "redirect");
            } else {
                response.put("message", "Welcome! You've been added to the team.");
                response.put("action", "redirect");
            }

            String jwtToken = jwtUtil.generateToken(user);
            response.put("token", jwtToken);

        } else {
        
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

    // Update team's Slack webhook URL
    @Transactional
    public void updateSlackWebhook(Long teamId, String webhookUrl) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        
        // Validate and clean webhook URL
        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            // Remove any duplicate protocol prefixes
            webhookUrl = webhookUrl.replaceAll("^https?://h+ttps?://", "https://");
            
            // Validate format
            if (!webhookUrl.startsWith("https://hooks.slack.com/services/")) {
                throw new RuntimeException("Invalid Slack webhook URL format. Must start with https://hooks.slack.com/services/");
            }
        }
        
        team.setSlackWebhookUrl(webhookUrl);
        teamRepository.save(team);
    }

    // Get team's Slack webhook URL
    public String getSlackWebhook(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        return team.getSlackWebhookUrl();
    }

    // Remove member from team
    @Transactional
    public void removeMember(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        team.getMembers().remove(user);
        teamRepository.save(team);
    }

    // Add member to team
    @Transactional
    public void addMember(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!team.getMembers().contains(user)) {
            team.getMembers().add(user);
            teamRepository.save(team);
        }
    }
}
