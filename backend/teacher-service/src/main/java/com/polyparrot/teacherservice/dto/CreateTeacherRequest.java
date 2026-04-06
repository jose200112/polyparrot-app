package com.polyparrot.teacherservice.dto;

import java.util.List;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateTeacherRequest {

	@Size(max = 200, message = "La bio no puede superar los 200 caracteres")
	private String bio;

    @NotNull(message = "El precio por hora es obligatorio")
    @DecimalMin(value = "1.0", message = "El precio mínimo es 1€")
    @DecimalMax(value = "500.0", message = "El precio máximo es 500€")
    private Double pricePerHour;

    @NotNull(message = "Debes seleccionar al menos un idioma que enseñas")
    @Size(min = 1, message = "Debes seleccionar al menos un idioma que enseñas")
    private List<Long> teachingLanguageIds;

    @NotNull(message = "Debes seleccionar al menos un idioma que hablas")
    @Size(min = 1, message = "Debes seleccionar al menos un idioma que hablas")
    private List<Long> spokenLanguageIds;
}