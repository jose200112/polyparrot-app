package com.polyparrot.chatservice.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String conversationId;
    private Long senderId;
    private String senderName;
    private String receiverName;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;
    private boolean read;
    
}