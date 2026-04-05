package com.polyparrot.notificationservice.event;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent {
    private Long bookingId;
    private Long teacherId;
    private Long studentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}