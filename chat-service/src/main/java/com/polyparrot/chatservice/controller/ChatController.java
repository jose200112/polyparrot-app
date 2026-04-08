package com.polyparrot.chatservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polyparrot.chatservice.dto.ConversationResponse;
import com.polyparrot.chatservice.dto.InitConversationDto;
import com.polyparrot.chatservice.dto.MessageDto;
import com.polyparrot.chatservice.dto.MessageResponse;
import com.polyparrot.chatservice.security.AuthenticatedUser;
import com.polyparrot.chatservice.security.SecurityUtils;
import com.polyparrot.chatservice.service.ChatService;
import com.polyparrot.chatservice.service.PresenceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    
    @MessageMapping("/send")
    public void sendMessage(@Payload MessageDto dto,
                            SimpMessageHeaderAccessor headerAccessor) {

        AuthenticatedUser caller = (AuthenticatedUser) headerAccessor
            .getSessionAttributes().get("authUser");

        if (caller == null || !caller.getUserId().equals(dto.getSenderId())) {
            return;
        }

        if (!chatService.conversationExists(dto.getSenderId(), dto.getReceiverId())) {
            return;
        }

        MessageResponse saved = chatService.saveMessage(dto);
        messagingTemplate.convertAndSend(
            "/topic/conversation/" + saved.getConversationId(),
            saved
        );
    }

    @PostMapping("/conversation/init")
    public ResponseEntity<Void> initConversation(@RequestBody InitConversationDto dto) {
        AuthenticatedUser caller = SecurityUtils.getCurrentUser();

        if (!caller.getUserId().equals(dto.getUserId1())
                && !caller.getUserId().equals(dto.getUserId2())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        chatService.initConversation(
            dto.getUserId1(), dto.getUserId2(),
            dto.getName1(), dto.getName2()
        );
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/presence/{userId}")
    public ResponseEntity<Boolean> isOnline(@PathVariable Long userId) {
        return ResponseEntity.ok(presenceService.isOnline(userId));
    }

    @GetMapping("/conversation/{userId1}/{userId2}")
    public ResponseEntity<List<MessageResponse>> getConversation(
            @PathVariable Long userId1,
            @PathVariable Long userId2) {

        AuthenticatedUser caller = SecurityUtils.getCurrentUser();

        if (!caller.getUserId().equals(userId1) && !caller.getUserId().equals(userId2)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(chatService.getConversation(userId1, userId2));
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ConversationResponse>> getConversations(
            @PathVariable Long userId) {

        AuthenticatedUser caller = SecurityUtils.getCurrentUser();

        if (!caller.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(chatService.getConversations(userId));
    }

    @PatchMapping("/conversation/{userId}/{receiverId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long userId,
            @PathVariable Long receiverId) {

        AuthenticatedUser caller = SecurityUtils.getCurrentUser();

        if (!caller.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        chatService.markAsRead(userId, receiverId);
        return ResponseEntity.noContent().build();
    }
}