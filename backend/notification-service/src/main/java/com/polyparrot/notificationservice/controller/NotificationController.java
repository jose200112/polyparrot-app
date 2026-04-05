package com.polyparrot.notificationservice.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.polyparrot.notificationservice.entity.Notification;
import com.polyparrot.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotifications(userId));
    }

    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<Long> countUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.countUnread(userId));
    }

    @PatchMapping("/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}