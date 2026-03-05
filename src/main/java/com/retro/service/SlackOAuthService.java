package com.retro.service;

import com.retro.config.SlackConfig;
import com.retro.model.SlackIntegration;
import com.retro.model.Team;
import com.retro.repository.SlackIntegrationRepository;
import com.retro.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SlackOAuthService {

    private static final Logger log = LoggerFactory.getLogger(SlackOAuthService.class);

    private static final String SLACK_AUTHORIZE_URL = "https://slack.com/oauth/v2/authorize";
    private static final String SLACK_TOKEN_URL     = "https://slack.com/api/oauth.v2.access";

    private final SlackConfig config;
    private final SlackIntegrationRepository slackRepo;
    private final TeamRepository teamRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // state token → teamId  (use Redis in production)
    private final Map<String, Long> pendingStates = new ConcurrentHashMap<>();

    @Value("${retro.frontend-url:https://segmento-retro-omega.vercel.app}")
    private String frontendUrl;

    public SlackOAuthService(SlackConfig config,
                             SlackIntegrationRepository slackRepo,
                             TeamRepository teamRepository) {
        this.config = config;
        this.slackRepo = slackRepo;
        this.teamRepository = teamRepository;
    }

    // ── Step 1: Build "Add to Slack" URL ─────────────────────────────────────

    public String buildAuthorizationUrl(Long teamId) {
        String state = UUID.randomUUID().toString();
        pendingStates.put(state, teamId);

        return SLACK_AUTHORIZE_URL
                + "?client_id="    + encode(config.getClientId())
                + "&scope="        + encode(config.getScopes())
                + "&redirect_uri=" + encode(config.getRedirectUri())
                + "&state="        + encode(state);
    }

    // ── Step 2: Handle callback from Slack, exchange code → token ────────────

    @SuppressWarnings("unchecked")
    public String handleCallback(String code, String state) {
        Long teamId = pendingStates.remove(state);
        if (teamId == null) {
            log.warn("Unknown OAuth state received: {}", state);
            return frontendUrl + "/integrations?slack=error&reason=invalid_state";
        }

        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) {
            log.warn("Team not found for id: {}", teamId);
            return frontendUrl + "/integrations?slack=error&reason=team_not_found";
        }

        try {
            // Exchange code for access token
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id",     config.getClientId());
            params.add("client_secret", config.getClientSecret());
            params.add("code",          code);
            params.add("redirect_uri",  config.getRedirectUri());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            ResponseEntity<Map> response = restTemplate.exchange(
                    SLACK_TOKEN_URL, HttpMethod.POST,
                    new HttpEntity<>(params, headers), Map.class);

            Map<String, Object> body = response.getBody();
            if (body == null || !Boolean.TRUE.equals(body.get("ok"))) {
                log.error("Slack token exchange failed: {}", body);
                return frontendUrl + "/integrations?slack=error&reason=token_exchange";
            }

            saveIntegration(team, body);
            return frontendUrl + "/integrations?slack=success";

        } catch (Exception e) {
            log.error("Slack OAuth error for team {}", teamId, e);
            return frontendUrl + "/integrations?slack=error&reason=server_error";
        }
    }

    // ── Disconnect ────────────────────────────────────────────────────────────

    public void disconnect(Long teamId) {
        slackRepo.findByTeamIdAndActiveTrue(teamId).ifPresent(i -> {
            i.setActive(false);
            slackRepo.save(i);
            log.info("Slack disconnected for team {}", teamId);
        });
    }

    // ── Status ────────────────────────────────────────────────────────────────

    public SlackStatusDTO getStatus(Long teamId) {
        return slackRepo.findByTeamIdAndActiveTrue(teamId)
                .map(i -> new SlackStatusDTO(true, i.getSlackTeamName(), i.getChannelName()))
                .orElse(new SlackStatusDTO(false, null, null));
    }

    // ── Persist ───────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void saveIntegration(Team team, Map<String, Object> oauthBody) {
        // Upsert — if already exists, update it
        SlackIntegration integration = slackRepo.findByTeamAndActiveTrue(team)
                .orElse(new SlackIntegration());

        integration.setTeam(team);
        integration.setBotToken((String) oauthBody.get("access_token"));
        integration.setActive(true);

        Map<String, Object> slackTeam = (Map<String, Object>) oauthBody.get("team");
        if (slackTeam != null) {
            integration.setSlackTeamId((String) slackTeam.get("id"));
            integration.setSlackTeamName((String) slackTeam.get("name"));
        }

        // incoming_webhook carries channel info
        Map<String, Object> webhook = (Map<String, Object>) oauthBody.get("incoming_webhook");
        if (webhook != null) {
            integration.setChannelId((String) webhook.get("channel_id"));
            integration.setChannelName((String) webhook.get("channel"));
        }

        slackRepo.save(integration);
        log.info("Slack integration saved for team id={}", team.getId());
    }

    private String encode(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }

    // ── DTO ───────────────────────────────────────────────────────────────────

    public record SlackStatusDTO(boolean connected, String workspaceName, String channelName) {}
}