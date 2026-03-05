package com.retro.model;

import com.retro.model.Team;
import jakarta.persistence.*;

@Entity
@Table(name = "slack_integrations")
public class SlackIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false, unique = true)
    private Team team;

    private String slackTeamId;      
    private String slackTeamName;     

    @Column(nullable = false)
    private String botToken;          

    private String channelId;        
    private String channelName;       

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() { return id; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public String getSlackTeamId() { return slackTeamId; }
    public void setSlackTeamId(String v) { this.slackTeamId = v; }

    public String getSlackTeamName() { return slackTeamName; }
    public void setSlackTeamName(String v) { this.slackTeamName = v; }

    public String getBotToken() { return botToken; }
    public void setBotToken(String v) { this.botToken = v; }

    public String getChannelId() { return channelId; }
    public void setChannelId(String v) { this.channelId = v; }

    public String getChannelName() { return channelName; }
    public void setChannelName(String v) { this.channelName = v; }

    public boolean isActive() { return active; }
    public void setActive(boolean v) { this.active = v; }
}