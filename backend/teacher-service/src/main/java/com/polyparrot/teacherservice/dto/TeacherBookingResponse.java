package com.polyparrot.teacherservice.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherBookingResponse {
    private Long bookingId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String studentName;
    private String studentFirstSurname;
    private String studentSecondSurname;
    private String status; 
}