package com.polyparrot.notificationservice.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.polyparrot.notificationservice.entity.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndReadFalse(Long userId);
}