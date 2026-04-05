package com.polyparrot.teacherservice.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvailabilitySlotResponse {
    private Long id;
    private Long teacherId;
    private LocalDateTime startTime;
}