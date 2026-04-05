package com.polyparrot.userservice.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String name;

    private String firstSurname;

    private String secondSurname;

    private String email;

    private String password;
}