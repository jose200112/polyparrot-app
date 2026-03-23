package com.polyparrot.teacherservice.dto;

import lombok.Data;

@Data
public class CreateTeacherRequest {
    private String bio;
    private Double pricePerHour;
}