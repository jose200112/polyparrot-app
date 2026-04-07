package com.polyparrot.notificationservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public void createBookingCreatedNotification(Long teacherId, Long bookingId, 
            LocalDateTime startTime, String studentName) {
        String formattedDate = startTime.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"));
        Notification notification = Notification.builder()
            .userId(teacherId)
            .type("BOOKING_CREATED")
            .message(studentName + " ha reservado una clase el " + formattedDate)
            .bookingId(bookingId)
            .startTime(startTime)
            .read(false)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
    }

    public void createBookingConfirmedNotification(Long studentId, Long bookingId,
            LocalDateTime startTime, String teacherName) {
        String formattedDate = startTime.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"));
        Notification notification = Notification.builder()
            .userId(studentId)
            .type("BOOKING_CONFIRMED")
            .message(teacherName + " ha confirmado tu clase del " + formattedDate)
            .bookingId(bookingId)
            .startTime(startTime)
            .read(false)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
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
    
    public void createBookingCancelledNotification(Long studentId, Long bookingId,
            LocalDateTime startTime, String teacherName) {
        String formattedDate = startTime.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"));
        Notification notification = Notification.builder()
            .userId(studentId)
            .type("BOOKING_CANCELLED")
            .message(teacherName + " ha cancelado tu clase del " + formattedDate)
            .bookingId(bookingId)
            .startTime(startTime)
            .read(false)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
    }

    public void createBookingCancelledByStudentNotification(Long teacherId, Long bookingId,
            LocalDateTime startTime, String studentName) {
        String formattedDate = startTime.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"));
        Notification notification = Notification.builder()
            .userId(teacherId)
            .type("BOOKING_CANCELLED")
            .message(studentName + " ha cancelado la clase del " + formattedDate)
            .bookingId(bookingId)
            .startTime(startTime)
            .read(false)
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
    }
}