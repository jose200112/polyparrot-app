package com.polyparrot.chatservice.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.polyparrot.chatservice.entity.Conversation;
import com.polyparrot.chatservice.repository.ConversationRepository;


@Service
public class PresenceService {

    private final Set<Long> onlineUsers = ConcurrentHashMap.newKeySet();
    private final ConversationRepository conversationRepository;

    public PresenceService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public void connect(Long userId) {
        onlineUsers.add(userId);
    }

    public void disconnect(Long userId) {
        onlineUsers.remove(userId);
    }

    public boolean isOnline(Long userId) {
        return onlineUsers.contains(userId);
    }

    // ← añadir
    public List<String> getConversationIds(Long userId) {
        return conversationRepository.findByUserId(userId)
            .stream()
            .map(Conversation::getConversationId)
            .toList();
    }

    // ← añadir
    public List<Long> getConversationPartners(Long userId) {
        return conversationRepository.findByUserId(userId)
            .stream()
            .map(conv -> conv.getUserId1().equals(userId)
                ? conv.getUserId2()
                : conv.getUserId1())
            .toList();
    }
}