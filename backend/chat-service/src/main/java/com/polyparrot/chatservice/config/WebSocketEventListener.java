package com.polyparrot.chatservice.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.polyparrot.chatservice.security.AuthenticatedUser;
import com.polyparrot.chatservice.service.PresenceService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Set<Long> notifiedConnect = ConcurrentHashMap.newKeySet();

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) return;

        String sessionType = (String) sessionAttributes.get("sessionType");
        if (!"presence".equals(sessionType)) return; // ← ignorar sesiones de chat

        AuthenticatedUser authUser = (AuthenticatedUser) sessionAttributes.get("authUser");
        if (authUser == null) return;

        if (notifiedConnect.add(authUser.getUserId())) {
            presenceService.connect(authUser.getUserId());
            notifyPresence(authUser.getUserId(), true);
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) return;

        String sessionType = (String) sessionAttributes.get("sessionType");
        if (!"presence".equals(sessionType)) return; // ← ignorar sesiones de chat

        AuthenticatedUser authUser = (AuthenticatedUser) sessionAttributes.get("authUser");
        if (authUser == null) return;

        notifiedConnect.remove(authUser.getUserId());
        presenceService.disconnect(authUser.getUserId());
        notifyPresence(authUser.getUserId(), false);
    }

    private void notifyPresence(Long userId, boolean online) {
        presenceService.getConversationIds(userId).forEach(conversationId ->
            messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                Map.of("type", "PRESENCE", "userId", userId, "online", online)
            )
        );

        presenceService.getConversationPartners(userId).forEach(partnerId ->
            messagingTemplate.convertAndSend(
                "/topic/presence/" + partnerId,
                Map.of("type", "PRESENCE", "userId", userId, "online", online)
            )
        );
    }
}