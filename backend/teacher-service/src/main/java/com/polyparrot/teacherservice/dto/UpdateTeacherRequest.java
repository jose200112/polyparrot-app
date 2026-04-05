package com.polyparrot.teacherservice.dto;

import java.util.List;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTeacherRequest {

    @Size(max = 200, message = "La bio no puede superar los 200 caracteres")
    private String bio;

    @DecimalMin(value = "1.0", message = "El precio mínimo es 1€")
    @DecimalMax(value = "500.0", message = "El precio máximo es 500€")
    private Double pricePerHour;

    private List<Long> teachingLanguageIds;
    private List<Long> spokenLanguageIds;
}