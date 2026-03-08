package com.retro.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.retro.model.Board;
import com.retro.model.BoardColumn;
import com.retro.model.Card;

public class BoardDetailDTO {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private UserBasicDTO createdBy;
    private TeamBasicDTO team;
    private List<ColumnDTO> columns;

    public static BoardDetailDTO fromEntity(Board board) {
        BoardDetailDTO dto = new BoardDetailDTO();
        dto.id = board.getId();
        dto.title = board.getTitle();
        dto.createdAt = board.getCreatedAt();
        
        if (board.getCreatedBy() != null) {
            dto.createdBy = new UserBasicDTO(
                board.getCreatedBy().getId(),
                board.getCreatedBy().getName(),
                board.getCreatedBy().getEmail()
            );
        }
        
        if (board.getTeam() != null) {
            dto.team = new TeamBasicDTO(
                board.getTeam().getId(),
                board.getTeam().getName()
            );
        }
        
        if (board.getColumns() != null) {
            dto.columns = board.getColumns().stream()
                .filter(col -> !col.getDeleted())
                .map(ColumnDTO::fromEntity)
                .collect(Collectors.toList());
        }
        
        return dto;
    }

    public static class UserBasicDTO {
        private Long id;
        private String name;
        private String email;

        public UserBasicDTO(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }

    public static class TeamBasicDTO {
        private Long id;
        private String name;

        public TeamBasicDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
    }

    public static class ColumnDTO {
        private Long id;
        private String title;
        private int position;
        private List<CardDTO> cards;

        public static ColumnDTO fromEntity(BoardColumn column) {
            ColumnDTO dto = new ColumnDTO();
            dto.id = column.getId();
            dto.title = column.getTitle();
            dto.position = column.getPosition();
            
            if (column.getCards() != null) {
                dto.cards = column.getCards().stream()
                    .filter(card -> !card.isDeleted())
                    .map(CardDTO::fromEntity)
                    .collect(Collectors.toList());
            }
            
            return dto;
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public int getPosition() { return position; }
        public List<CardDTO> getCards() { return cards; }
    }

    public static class CardDTO {
        private Long id;
        private String content;
        private UserBasicDTO createdBy;

        public static CardDTO fromEntity(Card card) {
            CardDTO dto = new CardDTO();
            dto.id = card.getId();
            dto.content = card.getContent();
            
            if (card.getCreatedBy() != null) {
                dto.createdBy = new UserBasicDTO(
                    card.getCreatedBy().getId(),
                    card.getCreatedBy().getName(),
                    card.getCreatedBy().getEmail()
                );
            }
            
            return dto;
        }

        public Long getId() { return id; }
        public String getContent() { return content; }
        public UserBasicDTO getCreatedBy() { return createdBy; }
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public UserBasicDTO getCreatedBy() { return createdBy; }
    public TeamBasicDTO getTeam() { return team; }
    public List<ColumnDTO> getColumns() { return columns; }
}
