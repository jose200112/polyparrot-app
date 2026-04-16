package com.polyparrot.chatservice.dto;

import lombok.Data;

@Data
public class MessageDto {
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String content;
}