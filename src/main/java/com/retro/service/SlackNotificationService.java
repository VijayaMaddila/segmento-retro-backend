package com.retro.service;

import com.retro.model.Board;
import com.retro.repository.SlackIntegrationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SlackNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationService.class);
    private static final String POST_MESSAGE_URL = "https://slack.com/api/chat.postMessage";

    private final SlackIntegrationRepository slackRepo;
    private final RestTemplate restTemplate = new RestTemplate();

    public SlackNotificationService(SlackIntegrationRepository slackRepo) {
        this.slackRepo = slackRepo;
    }

    // ── Called from BoardService.createBoard() ────────────────────────────────

    public void onBoardCreated(Board board, String joinUrl) {
        Long teamId = getTeamId(board);
        if (teamId == null) return;

        String facilitator = board.getCreatedBy() != null
                ? board.getCreatedBy().getName()   // adjust if your Users field is different
                : "Unknown";

        send(teamId,
            List.of(
                headerBlock("🔁  New Retro Board Created"),
                dividerBlock(),
                fieldsBlock(Map.of(
                    "Board",       board.getTitle(),
                    "Facilitator", facilitator,
                    "Team",        board.getTeam().getName()
                )),
                dividerBlock(),
                buttonBlock("Open Board →", joinUrl, "primary")
            ),
            "New retro board: " + board.getTitle() + " — " + joinUrl
        );
    }

    // ── Called from wherever you "start" a retro session ─────────────────────

    public void onRetroStarted(Board board, String joinUrl) {
        Long teamId = getTeamId(board);
        if (teamId == null) return;

        String facilitator = board.getCreatedBy() != null
                ? board.getCreatedBy().getName()
                : "Unknown";

        send(teamId,
            List.of(
                headerBlock("▶️  Retrospective Is Starting!"),
                dividerBlock(),
                sectionBlock("*" + board.getTitle() + "* is live — time to reflect! 🎯"),
                fieldsBlock(Map.of("Facilitator", facilitator, "Team", board.getTeam().getName())),
                dividerBlock(),
                buttonBlock("Join Now →", joinUrl, "primary")
            ),
            "Retro starting: " + board.getTitle() + " — " + joinUrl
        );
    }

    // ── Called when action items are finalized ────────────────────────────────

    public void onActionItemsPosted(Board board, List<ActionItemDTO> items) {
        Long teamId = getTeamId(board);
        if (teamId == null) return;

        List<Map<String, Object>> blocks = new ArrayList<>();
        blocks.add(headerBlock("📋  Action Items — " + board.getTitle()));
        blocks.add(dividerBlock());
        blocks.add(contextBlock("Team: *" + board.getTeam().getName()
                + "*  •  " + items.size() + " item(s)"));
        blocks.add(dividerBlock());

        for (int i = 0; i < items.size(); i++) {
            ActionItemDTO item = items.get(i);
            blocks.add(sectionBlock(
                "*" + (i + 1) + ".* " + item.description()
                + "\n*Owner:* " + nvl(item.owner(), "_Unassigned_")
                + "   *Due:* "  + nvl(item.dueDate(), "_TBD_")
            ));
        }
        blocks.add(dividerBlock());
        blocks.add(contextBlock("_Posted by Segmento Retro_"));

        send(teamId, blocks, "Action items posted for " + board.getTitle());
    }

    // ── Core send ─────────────────────────────────────────────────────────────

    private void send(Long teamId, List<Map<String, Object>> blocks, String fallbackText) {
        slackRepo.findByTeamIdAndActiveTrue(teamId).ifPresentOrElse(integration -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(integration.getBotToken());

                Map<String, Object> body = Map.of(
                        "channel", integration.getChannelId(),
                        "blocks",  blocks,
                        "text",    fallbackText
                );

                ResponseEntity<Map> res = restTemplate.exchange(
                        POST_MESSAGE_URL, HttpMethod.POST,
                        new HttpEntity<>(body, headers), Map.class);

                if (!Boolean.TRUE.equals(res.getBody().get("ok"))) {
                    log.error("Slack send failed for team {}: {}", teamId, res.getBody());
                } else {
                    log.info("Slack notification sent for team {}", teamId);
                }
            } catch (Exception e) {
                log.error("Slack send error for team {}", teamId, e);
            }
        }, () -> log.debug("No active Slack integration for team {}, skipping notification", teamId));
    }

    // ── Block Kit helpers ─────────────────────────────────────────────────────

    private Map<String, Object> headerBlock(String text) {
        return Map.of("type", "header",
                "text", Map.of("type", "plain_text", "text", text, "emoji", true));
    }

    private Map<String, Object> dividerBlock() {
        return Map.of("type", "divider");
    }

    private Map<String, Object> sectionBlock(String markdown) {
        return Map.of("type", "section",
                "text", Map.of("type", "mrkdwn", "text", markdown));
    }

    private Map<String, Object> contextBlock(String markdown) {
        return Map.of("type", "context",
                "elements", List.of(Map.of("type", "mrkdwn", "text", markdown)));
    }

    private Map<String, Object> fieldsBlock(Map<String, String> fields) {
        var list = fields.entrySet().stream()
                .map(e -> Map.of("type", "mrkdwn", "text", "*" + e.getKey() + ":*\n" + e.getValue()))
                .toList();
        return Map.of("type", "section", "fields", list);
    }

    private Map<String, Object> buttonBlock(String label, String url, String style) {
        return Map.of("type", "actions", "elements", List.of(
                Map.of("type", "button",
                        "text",  Map.of("type", "plain_text", "text", label, "emoji", true),
                        "url",   url,
                        "style", style)));
    }

    private Long getTeamId(Board board) {
        if (board.getTeam() == null) {
            log.debug("Board {} has no team, skipping Slack notification", board.getId());
            return null;
        }
        return board.getTeam().getId();
    }

    private String nvl(String val, String fallback) {
        return (val != null && !val.isBlank()) ? val : fallback;
    }

    // ── DTO ───────────────────────────────────────────────────────────────────

    public record ActionItemDTO(String description, String owner, String dueDate) {}
}