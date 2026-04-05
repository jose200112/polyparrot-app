package com.polyparrot.bookingservice.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BookingDto {
    private Long id;
    private Long studentId;
    private Long teacherId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}