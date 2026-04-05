package com.polyparrot.bookingservice.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingConfirmedEvent {
    private Long bookingId;
    private Long teacherId;
    private Long studentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}