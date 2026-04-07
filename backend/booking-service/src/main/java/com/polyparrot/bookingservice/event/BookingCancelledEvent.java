package com.polyparrot.bookingservice.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelledEvent {
    private Long bookingId;
    private Long teacherId;
    private Long studentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String cancelledBy;
    private String cancellerName;
}