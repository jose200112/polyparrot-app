package com.polyparrot.bookingservice.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookingRequest {
    private Long teacherId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}