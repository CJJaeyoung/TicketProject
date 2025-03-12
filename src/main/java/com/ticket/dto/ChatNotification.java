package com.ticket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ChatNotification {
    private Long roomId;
    private String message;
    private boolean isRead;

    public ChatNotification(Long roomId) {
        this.roomId = roomId;
        this.isRead = false;
    }
} 