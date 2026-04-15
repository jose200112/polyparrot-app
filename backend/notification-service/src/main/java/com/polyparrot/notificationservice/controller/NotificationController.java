package com.polyparrot.notificationservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.polyparrot.notificationservice.entity.Notification;
import com.polyparrot.notificationservice.security.AuthenticatedUser;
import com.polyparrot.notificationservice.security.SecurityUtils;
import com.polyparrot.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        AuthenticatedUser caller = SecurityUtils.getCurrentUser();
        if (!caller.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(notificationService.getNotifications(userId));
    }

    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<Long> countUnread(@PathVariable Long userId) {
        AuthenticatedUser caller = SecurityUtils.getCurrentUser();
        if (!caller.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(notificationService.countUnread(userId));
    }

    @PatchMapping("/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        AuthenticatedUser caller = SecurityUtils.getCurrentUser();
        if (!caller.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping(value = "/{userId}/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long userId) {
        AuthenticatedUser caller = SecurityUtils.getCurrentUser();
        if (!caller.getUserId().equals(userId)) {
            throw new AccessDeniedException("Forbidden");
        }
        return notificationService.subscribe(userId);
    }
}