package com.polyparrot.userservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(max = 50, message = "Máximo 50 caracteres")
    private String name;

    @Size(max = 50, message = "Máximo 50 caracteres")
    private String firstSurname;

    @Size(max = 50, message = "Máximo 50 caracteres")
    private String secondSurname;
}