package com.polyparrot.notificationservice.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.polyparrot.notificationservice.entity.Notification;
import com.polyparrot.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createBookingCreatedNotification(Long teacherId, Long bookingId, LocalDateTime startTime) {
        Notification notification = Notification.builder()
            .userId(teacherId)
            .type("BOOKING_CREATED")
            .message("Tienes una nueva reserva pendiente de confirmación")
            .bookingId(bookingId)
            .startTime(startTime)
            .read(false)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
        log.info("Notificación BOOKING_CREATED guardada para teacherId={}", teacherId);
    }

    public void createBookingConfirmedNotification(Long studentId, Long bookingId, LocalDateTime startTime) {
        Notification notification = Notification.builder()
            .userId(studentId)
            .type("BOOKING_CONFIRMED")
            .message("Tu reserva ha sido confirmada")
            .bookingId(bookingId)
            .startTime(startTime)
            .read(false)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
        log.info("Notificación BOOKING_CONFIRMED guardada para studentId={}", studentId);
    }

    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long countUnread(Long userId) {
        return notificationRepository.findByUserIdAndReadFalse(userId).size();
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}