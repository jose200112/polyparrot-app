package com.polyparrot.chatservice.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponse {
    private String id;
    private String conversationId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime sentAt;
    private boolean read;
    private String type; 
}