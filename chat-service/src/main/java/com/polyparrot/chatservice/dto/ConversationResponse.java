package com.polyparrot.chatservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationResponse {
    private Long receiverId;
    private String receiverName;
    private String lastMessage;
    private int unreadCount;
}