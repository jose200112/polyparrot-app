package com.polyparrot.bookingservice.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String teacherName;
    private String teacherFirstSurname;
    private String teacherSecondSurname;
}