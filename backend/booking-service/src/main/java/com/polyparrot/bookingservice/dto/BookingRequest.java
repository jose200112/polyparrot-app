package com.polyparrot.bookingservice.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {

    @NotNull(message = "El profesor es obligatorio")
    private Long teacherId;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalDateTime startTime;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalDateTime endTime;
}