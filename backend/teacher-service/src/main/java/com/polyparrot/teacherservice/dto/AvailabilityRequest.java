package com.polyparrot.teacherservice.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AvailabilityRequest {
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalDateTime startTime;
}