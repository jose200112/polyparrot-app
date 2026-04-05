package com.polyparrot.notificationservice.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;
    private Long userId;
    private String type; // "BOOKING_CREATED", "BOOKING_CONFIRMED", "BOOKING_CANCELLED"
    private String message;
    private Long bookingId;
    private LocalDateTime startTime;
    private boolean read;
    private LocalDateTime createdAt;
}
