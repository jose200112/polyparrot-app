package com.polyparrot.notificationservice.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.polyparrot.notificationservice.security.AuthenticatedUser;
import com.polyparrot.notificationservice.security.JwtService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

 private final JwtService jwtService;

 @Override
 public Message<?> preSend(Message<?> message, MessageChannel channel) {
     StompHeaderAccessor accessor =
         MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

     if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
         String authHeader = accessor.getFirstNativeHeader("Authorization");

         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
             throw new MessagingException("Token requerido");
         }

         String token = authHeader.substring(7);
         AuthenticatedUser authUser = new AuthenticatedUser(
             jwtService.extractUserId(token),
             jwtService.extractEmail(token),
             jwtService.extractRole(token)
         );

         accessor.getSessionAttributes().put("authUser", authUser);
         accessor.getSessionAttributes().put("sessionType", "notifications");
     }

     return message;
 }
}