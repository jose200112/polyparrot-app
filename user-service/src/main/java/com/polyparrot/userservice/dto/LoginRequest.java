package com.polyparrot.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

	   	@NotBlank(message = "El email es obligatorio")
	    @Email(message = "El email no es válido")
	    @Size(max = 100, message = "Máximo 100 caracteres")
	    private String email;

	    @NotBlank(message = "La contraseña es obligatoria")
	    @Size(max = 50, message = "Máximo 50 caracteres")
	    private String password;

}