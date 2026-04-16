package com.polyparrot.chatservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.polyparrot.chatservice.dto.ConversationResponse;
import com.polyparrot.chatservice.dto.MessageDto;
import com.polyparrot.chatservice.dto.MessageResponse;
import com.polyparrot.chatservice.entity.Conversation;
import com.polyparrot.chatservice.entity.Message;
import com.polyparrot.chatservice.repository.ConversationRepository;
import com.polyparrot.chatservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageResponse saveMessage(MessageDto dto) {
        String conversationId = buildConversationId(dto.getSenderId(), dto.getReceiverId());

        Message message = Message.builder()
            .conversationId(conversationId)
            .senderId(dto.getSenderId())
            .senderName(dto.getSenderName())
            .receiverId(dto.getReceiverId())
            .receiverName(dto.getReceiverName())
            .content(dto.getContent())
            .sentAt(LocalDateTime.now())
            .read(false)
            .build();

        Message saved = messageRepository.save(message);

        // Actualizar conversación
        conversationRepository.findByConversationId(conversationId).ifPresentOrElse(
            conv -> {
                conv.setLastMessage(dto.getContent());
                conv.setLastMessageAt(saved.getSentAt());
                conv.setUnreadCount(conv.getUnreadCount() + 1);
                conversationRepository.save(conv);
            },
            () -> {
                // Crear conversación si no existe
                Conversation conv = Conversation.builder()
                    .conversationId(conversationId)
                    .userId1(dto.getSenderId())
                    .userId2(dto.getReceiverId())
                    .name1(dto.getSenderName())
                    .name2(dto.getReceiverName())
                    .lastMessage(dto.getContent())
                    .lastMessageAt(saved.getSentAt())
                    .unreadCount(1)
                    .createdAt(LocalDateTime.now())
                    .build();
                conversationRepository.save(conv);
            }
        );

        return mapToResponse(saved);
    }

    public List<MessageResponse> getConversation(Long userId1, Long userId2) {
        String conversationId = buildConversationId(userId1, userId2);
        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    public List<ConversationResponse> getConversations(Long userId) {
        return conversationRepository.findByUserId(userId)
            .stream()
            .map(conv -> {
                boolean isUser1 = conv.getUserId1().equals(userId);
                String otherName = isUser1 ? conv.getName2() : conv.getName1();
                Long otherId     = isUser1 ? conv.getUserId2() : conv.getUserId1();

                // Solo mensajes donde YO soy el receptor y no he leído
                long unread = messageRepository.countByConversationIdAndReceiverIdAndReadFalse(
                    conv.getConversationId(),
                    userId
                );

                return ConversationResponse.builder()
                    .receiverId(otherId)
                    .receiverName(otherName)
                    .lastMessage(conv.getLastMessage() != null ? conv.getLastMessage() : "")
                    .unreadCount((int) unread)
                    .build();
            })
            .toList();
    }

    public void initConversation(Long userId1, Long userId2, String name1, String name2) {
        String conversationId = buildConversationId(userId1, userId2);
        if (conversationRepository.findByConversationId(conversationId).isPresent()) return;

        Conversation conv = Conversation.builder()
            .conversationId(conversationId)
            .userId1(userId1)
            .userId2(userId2)
            .name1(name1)
            .name2(name2)
            .lastMessage(null)
            .unreadCount(0)
            .createdAt(LocalDateTime.now())
            .build();
        conversationRepository.save(conv);
    }

    public void markAsRead(Long userId, Long receiverId) {
        String conversationId = buildConversationId(userId, receiverId);

        List<Message> unread = messageRepository
            .findByConversationIdOrderBySentAtAsc(conversationId)
            .stream()
            .filter(m -> !m.isRead() && !m.getSenderId().equals(userId))
            .toList();
        unread.forEach(m -> m.setRead(true));
        messageRepository.saveAll(unread);

        conversationRepository.findByConversationId(conversationId).ifPresent(conv -> {
            conv.setUnreadCount(0);
            conversationRepository.save(conv);
        });

        messagingTemplate.convertAndSend(
            "/topic/conversation/" + conversationId,
            Map.of(
                "type", "READ",
                "readerId", userId
            )
        );
    }

    private String buildConversationId(Long userId1, Long userId2) {
        long min = Math.min(userId1, userId2);
        long max = Math.max(userId1, userId2);
        return min + "_" + max;
    }

    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder()
            .id(message.getId())
            .conversationId(message.getConversationId())
            .senderId(message.getSenderId())
            .senderName(message.getSenderName())
            .content(message.getContent())
            .sentAt(message.getSentAt())
            .read(message.isRead())
            .type("MESSAGE") 
            .build();
    }
    
    public boolean conversationExists(Long userId1, Long userId2) {
        String conversationId = buildConversationId(userId1, userId2);
        return conversationRepository.findByConversationId(conversationId).isPresent();
    }
    
    
}