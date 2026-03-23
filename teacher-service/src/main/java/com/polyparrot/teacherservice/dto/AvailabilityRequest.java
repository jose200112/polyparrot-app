package com.polyparrot.teacherservice.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AvailabilityRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
