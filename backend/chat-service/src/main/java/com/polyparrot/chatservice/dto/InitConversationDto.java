package com.polyparrot.chatservice.dto;

import lombok.Data;

@Data
public class InitConversationDto {
    private Long userId1;
    private Long userId2;
    private String name1;
    private String name2;
}