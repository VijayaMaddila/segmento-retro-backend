package com.retro.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retro.dto.TeamDTO;
import com.retro.model.Team;
import com.retro.model.Users;
import com.retro.repository.TeamRepository;
import com.retro.repository.UserRepository;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    //Create a new team
    public TeamDTO createTeam(TeamDTO teamDTO) {
        Users createdByUser = userRepository.findById(teamDTO.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("Creator user not found"));

        List<Users> memberUsers = teamDTO.getMembers()
                .stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Member user not found with id " + id)))
                .collect(Collectors.toList());

        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setCreatedBy(createdByUser);
        team.setMembers(memberUsers);

        Team savedTeam = teamRepository.save(team);

      
        return new TeamDTO(
                savedTeam.getId(),
                savedTeam.getName(),
                savedTeam.getCreatedBy().getId(),
                savedTeam.getMembers().stream().map(Users::getId).collect(Collectors.toList())
        );
    }

    //Get all teams
    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(team -> new TeamDTO(
                        team.getId(),
                        team.getName(),
                        team.getCreatedBy().getId(),
                        team.getMembers().stream().map(Users::getId).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    //Get team by ID
    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        return new TeamDTO(
                team.getId(),
                team.getName(),
                team.getCreatedBy().getId(),
                team.getMembers().stream().map(Users::getId).collect(Collectors.toList())
        );
    }
}